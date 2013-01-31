/*
 * $Id: SQLUnitMockPreparedStatement.java,v 1.8 2004/10/05 20:13:04 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/mock/SQLUnitMockPreparedStatement.java,v $
 * SQLUnit - a test harness for unit testing database stored procedures.
 * Copyright (C) 2003  The SQLUnit Team
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.sqlunit.test.mock;

import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.utils.TypeUtils;

import com.mockrunner.jdbc.AbstractResultSetHandler;
import com.mockrunner.mock.jdbc.MockPreparedStatement;
import com.mockrunner.mock.jdbc.MockResultSet;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Overrides certain methods in the MockPreparedStatement class for mock 
 * testing of SQLUnit.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.8 $
 */
public class SQLUnitMockPreparedStatement extends MockPreparedStatement {
    
    private static final Logger LOG = 
        Logger.getLogger(SQLUnitMockPreparedStatement.class);

    private IntrospectingPreparedStatementResultSetHandler handler = null;
    private String mockSql = null;
    private Map parameterMap = null;
    private List batchParameters = null;
    private int resultSetId = 0;

    /**
     * Instantiate a SQLUnitMockPreparedStatement object using a Connection
     * object and a SQL string.
     * @param conn the Connection object.
     * @param sql the SQL string.
     */
    public SQLUnitMockPreparedStatement(final Connection conn,
            final String sql) {
        super(conn, sql);
        LOG.debug(">> [SQLUnitMockPreparedStatement(conn," + sql + ")]");
        this.mockSql = sql;
        this.parameterMap = new HashMap();
        this.batchParameters = new ArrayList();
        if (conn instanceof SQLUnitMockConnection) {
            setResultSetHandler(((SQLUnitMockConnection) conn).
                getPreparedStatementResultSetHandler());
        }
    }

    /**
     * Sets the IntrospectingPreparedStatementResultSetHandler for the 
     * MockPreparedStatement.
     * @param aHandler an IntrospectingPreparedStatementResultSetHandler object.
     */
    public final void setResultSetHandler(
            final AbstractResultSetHandler aHandler) {
        LOG.debug(">> setResultSetHandler(handler)");
        this.handler = 
            (IntrospectingPreparedStatementResultSetHandler) aHandler;
        super.setResultSetHandler(aHandler);
    }

    /**
     * Returns true if there are more result sets available for this SQL.
     * @return true if there are more result sets available, else false.
     * @exception SQLException if one is thrown.
     */
    public final boolean getMoreResults() throws SQLException {
        LOG.debug(">> getMoreResults()");
        return handler.hasMoreResults(substituteParameters(mockSql));
    }

    /**
     * Returns the current result set from the SQL statement.
     * @return a MockResultSet object.
     * @exception SQLException if one is thrown.
     */
    public final ResultSet getResultSet() throws SQLException {
        LOG.debug(">> getResultSet()");
        MockResultSet rs = handler.getResultSet(substituteParameters(mockSql));
        if (MockResultSetUtils.isSQLException(rs)) {
            SQLException e = 
                MockResultSetUtils.getSQLExceptionFromResultSet(rs);
            throw e;
        } else {
            return rs;
        }
    }

    /**
     * Invokes the named method on the MockDatabase using introspection.
     * The only method used to execute() a PreparedStatement is the execute.
     * If other methods are used then we may want to override them too.
     * @return true if the PreparedStatement contains a resultset.
     * @exception SQLException if there was a problem executing.
     */
    public final boolean execute() throws SQLException {
        LOG.debug(">> execute()");
        return getMoreResults();
    }

    /**
     * Returns the update count.
     * @return the update count.
     * @exception SQLException if an error occurs.
     */
    public final int getUpdateCount() throws SQLException {
        LOG.debug(">> getUpdateCount()");
        return handler.getUpdateCount(substituteParameters(mockSql)).intValue();
    }

    /**
     * Sets an object into the parameter map.
     * @param index the parameter index (1-based).
     * @param obj the parameter Object.
     * @param sqlType the SQL Type of the parameter.
     * @exception SQLException if an error occurs.
     */
    public final void setObject(final int index, final Object obj,
            final int sqlType) throws SQLException {
        try {
            parameterMap.put(new Integer(index), 
                TypeUtils.convertToString(obj, sqlType));
        } catch (SQLUnitException e) {
            LOG.error("Could not convert object of type "
                + obj.getClass().getName() + " to String");
            parameterMap.put(new Integer(index), obj.toString());
        }
    }

    /**
     * Parses and replaces positional parameters in the SQL string and
     * adds it to the batch for executeBatch() to pick up.
     * @exception SQLException if there was a problem.
     */
    public final void addBatch() throws SQLException {
        String rsql = substituteParameters(mockSql);
        batchParameters.add(rsql);
    }

    /**
     * Runs the batch list against the IntrospectingResultSetFactory to
     * return an array of update counts.
     * @return an array of update counts.
     * @exception SQLException if there was a problem.
     */
    public final int[] executeBatch() throws SQLException {
        int[] updateCounts = new int[batchParameters.size()];
        for (int i = 0; i < updateCounts.length; i++) {
            String rsql = (String) batchParameters.get(i);
            updateCounts[i] =  handler.getUpdateCount(rsql).intValue();
        }
        return updateCounts;
    }

    /**
     * Substitutes the values of "?" in the parameterized SQL string with
     * values from the parameter map.
     * @param parameterized the parameterized SQL string (embedded "?").
     * @return the unparameterized string.
     */
    private String substituteParameters(final String parameterized) {
        return MockResultSetUtils.replaceParameters(
            parameterized, parameterMap);
    }
}
