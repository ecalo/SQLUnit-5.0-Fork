/*
 * $Id: SQLUnitMockCallableStatement.java,v 1.9 2005/03/26 07:36:32 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/mock/SQLUnitMockCallableStatement.java,v $
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
import com.mockrunner.mock.jdbc.MockCallableStatement;
import com.mockrunner.mock.jdbc.MockResultSet;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Overrides certain methods in the MockCallableStatement class for mock testing
 * of SQLUnit.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.9 $
 */
public class SQLUnitMockCallableStatement extends MockCallableStatement {
    
    private static Logger LOG = 
        Logger.getLogger(SQLUnitMockCallableStatement.class);

    private IntrospectingCallableStatementResultSetHandler handler = null;
    private String mockSql = null;
    private int resultSetId = 0;
    private Map parameterMap = null;
    private Map parameterValueMap = null;

    /**
     * Instantiates a SQLUnitMockConnectionStatement using the Connection
     * object and the SQL string.
     * @param conn the Connection object.
     * @param sql the SQL string.
     */
    public SQLUnitMockCallableStatement(final Connection conn,
            final String sql) {
        super(conn, sql);
        LOG.debug(">> [SQLUnitMockCallableStatement(conn," + sql + ")]");
        this.mockSql = sql;
        this.parameterMap = new HashMap();
        this.parameterValueMap = new HashMap();
        if (conn instanceof SQLUnitMockConnection) {
            setResultSetHandler(((SQLUnitMockConnection) conn).
                getCallableStatementResultSetHandler());
        }
    }

    /**
     * Sets the IntrospectingCallableStatementResultSetHandler for the 
     * MockCallableStatement.
     * @param hndlr an IntrospectingCallableStatementResultSetHandler object.
     */
    public final void setResultSetHandler(
            final AbstractResultSetHandler hndlr) {
        LOG.debug(">> setResultSetHandler(handler)");
        this.handler = (IntrospectingCallableStatementResultSetHandler) hndlr;
        super.setResultSetHandler(this.handler);
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
     * The execute() method is the only method used in SQLUnit, if we 
     * were to use more in the future, then these methods will also need
     * to be overriden.
     * @return true if a resultset can be returned from the CallableStatement.
     * @exception SQLException if there was a problem.
     */
    public final boolean execute() throws SQLException {
        LOG.debug(">> execute()");
        return getMoreResults();
    }

    /**
     * Sets the object type of the out parameter being registered.
     * @param index the parameter index (1-based).
     * @param sqlType the SQL Type of the parameter.
     * @exception SQLException if an error occurs.
     */
    public final void registerOutParameter(final int index, final int sqlType) 
            throws SQLException {
        parameterMap.put(new Integer(index), new Integer(sqlType));
    }

    /**
     * Set the object type and scale of the outparam being registered. This
     * mock method delegates back to the 2-argument form of this method, since
     * the mock database does not do any processing of the extra scale 
     * information.
     * @param index the parameter index (1-based).
     * @param sqlType the SQL type of the parameter.
     * @param scale the scale.
     * @exception SQLException if an error occurs.
     */
    public final void registerOutParameter(final int index, final int sqlType,
            final int scale) throws SQLException {
        registerOutParameter(index, sqlType);
    }

    /**
     * Sets a parameter into the parameterMap.
     * @param index the parameter index (1-based).
     * @param obj the parameter as an Object.
     * @param sqlType the SQL Type of the parameter.
     * @exception SQLException if an error occurs.
     */
    public final void setObject(final int index, final Object obj,
            final int sqlType) throws SQLException {
        try {
            parameterValueMap.put(new Integer(index), 
                TypeUtils.convertToString(obj, sqlType));
        } catch (SQLUnitException e) {
            LOG.error("Could not convert object of type "
                + obj.getClass().getName() + " to String");
            parameterValueMap.put(new Integer(index), obj.toString());
        }
    }

    /**
     * Returns the object at the specified position.
     * @param index the parameter index (1-based).
     * @return the value of the parameter at the index.
     * @exception SQLException if an error occurs.
     */
    public final Object getObject(final int index) throws SQLException {
        LOG.debug(">> getObject(" + index + ")");
        return handler.getOutParam(substituteParameters(mockSql), index,
            ((Integer) parameterMap.get(new Integer(index))).intValue());
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
     * Substitutes the values of "?" in the parameterized SQL string with
     * values from the parameter map.
     * @param parameterized the parameterized SQL string (embedded "?").
     * @return the unparameterized string.
     */
    private String substituteParameters(final String parameterized) {
        return MockResultSetUtils.replaceParameters(
            parameterized, parameterValueMap);
    }
}
