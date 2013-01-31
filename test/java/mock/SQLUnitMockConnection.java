/*
 * $Id: SQLUnitMockConnection.java,v 1.2 2004/10/05 20:13:04 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/mock/SQLUnitMockConnection.java,v $
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

import com.mockrunner.jdbc.CallableStatementResultSetHandler;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

import org.apache.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Represents a Connection object that depends on an underlying IDatabase.
 * Much of the functionality of the MockConnection object is reused, we
 * extend behavior only where we need it.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.2 $
 */
public class SQLUnitMockConnection extends MockConnection {

    private static final Logger LOG = 
        Logger.getLogger(SQLUnitMockConnection.class);

    private String sql = null;
    private int resultSetId = 1;

    /**
     * Override the superclass method to return an Introspecting handler.
     * @return an IntrospectingStatementResultSetHandler object.
     */
    public StatementResultSetHandler getStatementResultSetHandler() {
        LOG.debug(">> getStatementResultSetHandler()");
        try {
            IntrospectingStatementResultSetHandler handler = 
                new IntrospectingStatementResultSetHandler(getCatalog());
            return handler;
        } catch (SQLException e) {
            LOG.error("Exception caught: " + e.getMessage());
            return null;
        }
    }

    /**
     * Override the superclass method to return an Introspecting handler.
     * @return an IntrospectingPreparedStatementResultSetHandler object.
     */
    public final PreparedStatementResultSetHandler 
            getPreparedStatementResultSetHandler() {
        LOG.debug(">> getPreparedStatementResultSetHandler()");
        try {
            IntrospectingPreparedStatementResultSetHandler handler = 
               new IntrospectingPreparedStatementResultSetHandler(getCatalog());
            return handler;
        } catch (SQLException e) {
            LOG.error("Exception caught: " + e.getMessage());
            return null;
        }
    }

    /**
     * Override the superclass method to return an Introspecting handler.
     * @return an IntrospectingCallableStatementResultSetHandler object.
     */
    public final CallableStatementResultSetHandler 
            getCallableStatementResultSetHandler() {
        LOG.debug(">> getCallableStatementResultSetHandler()");
        try {
            IntrospectingCallableStatementResultSetHandler handler = 
               new IntrospectingCallableStatementResultSetHandler(getCatalog());
            return handler;
        } catch (SQLException e) {
            LOG.error("Exception caught: " + e.getMessage());
            return null;
        }
    }

    /**
     * Creates a SQLUnitMockStatement object.
     * @return a SQLUnitMockStatement object.
     * @exception SQLException if there was an error.
     */
    public final Statement createStatement() throws SQLException {
        LOG.debug(">> createStatement()");
        SQLUnitMockStatement stmt = new SQLUnitMockStatement(this);
        getStatementResultSetHandler().addStatement(stmt);
        return stmt;
    }

    /**
     * Creates a SQLUnitMockStatement with the specified resultSetType and
     * resultSetConcurrency. Delegates to the parameterless version of this 
     * method.
     * @param resultSetType the resultSetType to set.
     * @param resultSetConcurrency the resultSetConcurrency to set.
     * @return a Statement object.
     * @exception SQLException if there was an error.
     */
    public final Statement createStatement(final int resultSetType, 
            final int resultSetConcurrency) throws SQLException {
        return createStatement();
    }

    /**
     * Creates a SQLUnitMockStatement with the specified resultSetType,
     * resultSetConcurrency and resultSetHoldability. Delegates to the 
     * parameterless version of this method.
     * @param resultSetType the resultSetType to set.
     * @param resultSetConcurrency the resultSetConcurrency to set.
     * @param resultSetHoldability the resultSetHoldability to set.
     * @return a Statement object.
     * @exception SQLException if there was an error.
     */
    public final Statement createStatement(final int resultSetType,
            final int resultSetConcurrency, final int resultSetHoldability) 
            throws SQLException {
        return createStatement();
    }

    /**
     * Creates a SQLUnitMockCallableStatement with the specified SQL string.
     * @param sqlString the SQL string to execute.
     * @return a SQLUnitMockCallableStatement object.
     * @exception SQLException if there was an error.
     */
    public final CallableStatement prepareCall(final String sqlString)
            throws SQLException {
        LOG.debug(">> prepareCall(" + sqlString + ")");
        SQLUnitMockCallableStatement stmt = 
            new SQLUnitMockCallableStatement(this, sqlString);
        getCallableStatementResultSetHandler().addCallableStatement(stmt);
        return stmt;
    }

    /**
     * Creates a SQLUnitMockCallableStatement with the specified SQL string,
     * resultSetType and resultSetConcurrency. Delegates to the one-parameter
     * version of this method.
     * @param sqlString the SQL string to execute.
     * @param resultSetType the resultSetType to set.
     * @param resultSetConcurrency the resultSetConcurrency to set.
     * @return a SQLUnitMockCallableStatement object.
     * @exception SQLException if there was an error.
     */
    public final CallableStatement prepareCall(final String sqlString,
            final int resultSetType, final int resultSetConcurrency)
            throws SQLException {
        return prepareCall(sqlString);
    }

    /**
     * Creates a SQLUnitMockCallableStatement with the specified SQL string,
     * resultSetType, resultSetConcurrency and resultSetHoldability. Delegates
     * to the one-parameter version of this method.
     * @param sqlString the SQL string to execute.
     * @param resultSetType the resultSetType to set.
     * @param resultSetConcurrency the resultSetConcurrency to set.
     * @param resultSetHoldability the resultSetHoldability to set.
     * @return a SQLUnitMockCallableStatement object.
     * @exception SQLException if there was an error.
     */
    public final CallableStatement prepareCall(final String sqlString,
            final int resultSetType, final int resultSetConcurrency, 
            final int resultSetHoldability) throws SQLException {
        return prepareCall(sqlString);
    }

    /**
     * Creates a SQLUnitMockPreparedStatement with the specified SQL string.
     * @param sqlString the SQL string to execute.
     * @return a SQLUnitMockPreparedStatement object.
     * @exception SQLException if there was an error.
     */
    public final PreparedStatement prepareStatement(final String sqlString)
            throws SQLException {
        LOG.debug(">> prepareStatement(" + sqlString + ")");
        SQLUnitMockPreparedStatement stmt = new SQLUnitMockPreparedStatement(
            this, sqlString);
        getPreparedStatementResultSetHandler().addPreparedStatement(stmt);
        return stmt;
    }

    /**
     * Creates a SQLUnitMockPreparedStatement with the specified SQL string
     * with the specified resultSetType and resultSetConcurrency. Delegates
     * to the one-parameter version of this method.
     * @param sqlString the SQL string to execute.
     * @param resultSetType the resultSetType to set.
     * @param resultSetConcurrency the resultSetConcurrency to set.
     * @return a SQLUnitMockPreparedStatement object.
     * @exception SQLException if there was an error.
     */
    public final PreparedStatement prepareStatement(final String sqlString,
            final int resultSetType, final int resultSetConcurrency) 
            throws SQLException {
        return prepareStatement(sqlString);
    }

    /**
     * Creates a SQLUnitMockPreparedStatement with the specified SQL string
     * with the specified resultSetType, resultSetConcurrency and
     * resultSetHoldability. Delegates to the one-parameter version of this
     * method.
     * @param sqlString the SQL string to execute.
     * @param resultSetType the resultSetType to set.
     * @param resultSetConcurrency the resultSetConcurrency to set.
     * @param resultSetHoldability the resultSetHoldability to set.
     * @return a SQLUnitMockPreparedStatement object.
     * @exception SQLException if there was an error.
     */
    public final PreparedStatement prepareStatement(final String sqlString,
            final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {
        return prepareStatement(sqlString);
    }

    /**
     * Creates a SQLUnitMockPreparedStatement with the specified SQL string
     * and the autoGenerated keys parameter. Delegates to the one-parameter
     * version of this method.
     * @param sqlString the SQL string to execute.
     * @param autoGeneratedKeys the autoGeneratedKeys to set.
     * @return a SQLUnitMockPreparedStatement object.
     * @exception SQLException if there was an error.
     */
    public final PreparedStatement prepareStatement(final String sqlString,
            final int autoGeneratedKeys) throws SQLException {
        return prepareStatement(sqlString);
    }

    /**
     * Creates a SQLUnitMockPreparedStatement with the specified SQL string
     * and an int array of column indexes. Delegates to the one-parameter
     * version of this method.
     * @param sqlString the SQL string to execute.
     * @param columnIndexes an int array of column indexes.
     * @return a SQLUnitMockPreparedStatement object.
     * @exception SQLException if there was an error.
     */
    public final PreparedStatement prepareStatement(final String sqlString,
            final int[] columnIndexes) throws SQLException {
        return prepareStatement(sqlString);
    }

    /**
     * Creates a SQLUnitMockPreparedStatement with the specified SQL string
     * and an String array of column names. Delegates to the one-parameter
     * version of this method.
     * @param sqlString the SQL string to execute.
     * @param columnNames a String array of column names.
     * @return a SQLUnitMockPreparedStatement object.
     * @exception SQLException if there was an error.
     */
    public final PreparedStatement prepareStatement(final String sqlString,
            final String[] columnNames) throws SQLException {
        return prepareStatement(sqlString);
    }
}
