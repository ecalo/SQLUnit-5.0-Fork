/*
 * $Id: SQLUnitMockStatement.java,v 1.7 2004/10/05 20:13:04 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/mock/SQLUnitMockStatement.java,v $
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

import com.mockrunner.jdbc.AbstractResultSetHandler;
import com.mockrunner.mock.jdbc.MockStatement;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Overrides certain methods in the MockStatement class for mock testing
 * of SQLUnit.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.7 $
 */
public class SQLUnitMockStatement extends MockStatement {
    
    private static final Logger LOG = 
        Logger.getLogger(SQLUnitMockStatement.class);

    private IntrospectingStatementResultSetHandler handler = null;
    private int resultSetId = 0;
    private String mockSql = null;

    /**
     * Instantiates a SQLUnitMockStatement object using a Connection.
     * @param conn a Connection object.
     */
    public SQLUnitMockStatement(final Connection conn) {
        super(conn);
        LOG.debug(">> [SQLUnitMockStatement(conn)]");
        if (conn instanceof SQLUnitMockConnection) {
            setResultSetHandler(((SQLUnitMockConnection) conn).
                getStatementResultSetHandler());
        }
    }

    /**
     * Sets the IntrospectingStatementResultSetHandler for the MockStatement.
     * @param aHandler an IntrospectingStatementResultSetHandler object.
     */
    public final void setResultSetHandler(
            final AbstractResultSetHandler aHandler) {
        LOG.debug(">> setResultSetHandler(handler)");
        super.setResultSetHandler(aHandler);
        this.handler = (IntrospectingStatementResultSetHandler) aHandler;
    }

    /**
     * Returns true if there are more result sets available for this SQL.
     * @param sql the SQL to look up.
     * @return true if there are more result sets available, else false.
     * @exception SQLException if one is thrown.
     */
    public final boolean getMoreResults(final String sql) throws SQLException {
        LOG.debug(">> hasMoreResults(" + sql + ")");
        return handler.hasMoreResults(sql);
    }

    /**
     * Returns the current result set from the SQL statement.
     * @return a MockResultSet object.
     * @exception SQLException if one is thrown.
     */
    public final ResultSet getResultSet() throws SQLException {
        LOG.debug(">> getResultSet()");
        return handler.getResultSet(mockSql);
    }

    /**
     * Overrides the execute(String) method in MockStatement. Introspects
     * the named method to pull the current resultset out.
     * @param sql the SQL string to execute.
     * @return true if there are resultsets available.
     * @exception SQLException if there was a problem.
     */
    public final boolean execute(final String sql) throws SQLException {
        LOG.debug(">> execute(" + sql + ")");
        this.mockSql = sql;
        return getMoreResults(sql);
    }

    /**
     * Returns the update count.
     * @return the update count.
     * @exception SQLException if an error occurs.
     */
    public final int getUpdateCount() throws SQLException {
        LOG.debug(">> getUpdateCount()");
        return handler.getUpdateCount(mockSql).intValue();
    }
}
