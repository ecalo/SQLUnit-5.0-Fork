/*
 * $Id: IntrospectingCallableStatementResultSetHandler.java,v 1.7 2004/10/05 20:13:04 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/mock/IntrospectingCallableStatementResultSetHandler.java,v $
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
import com.mockrunner.mock.jdbc.MockResultSet;

import org.apache.log4j.Logger;

import java.sql.SQLException;

/**
 * Extends the CallableStatementResultSetHandler to use introspection.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.7 $
 */
public class IntrospectingCallableStatementResultSetHandler 
        extends CallableStatementResultSetHandler {

    private static final Logger LOG = Logger.getLogger(
        IntrospectingCallableStatementResultSetHandler.class);

    private IntrospectingResultSetFactory factory;
    private String sql;
    private int resultSetIndex;
    private String catalog;

    /**
     * Instantiate a CallableStatementResultSetHandler that depends on 
     * Introspection.
     * @param catalog the name of the class to introspect.
     */
    public IntrospectingCallableStatementResultSetHandler(
            final String catalog) {
        LOG.debug(">> [IntrospectingCallableStatementResultSetHandler("
            + catalog + ")]");
        this.catalog = catalog;
        factory = new IntrospectingResultSetFactory(this.catalog);
    }

    /**
     * Returns the current result set for the SQL call.
     * @param sqlString the SQL string to execute.
     * @return a MockResultSetObject.
     */
    public final MockResultSet getResultSet(final String sqlString) {
        LOG.debug(">> getResultSet(" + sqlString + ")");
        this.sql = sqlString;
        return factory.create(sql, resultSetIndex);
    }

    /**
     * Returns true of there are more result sets available for this SQL
     * call. Also increments the result set index as a side effect.
     * @param sqlString the SQL string to look up.
     * @return true if there are more resultsets, false if not.
     * @exception SQLException if an error occurs.
     */
    public final boolean hasMoreResults(final String sqlString)
            throws SQLException {
        LOG.debug(">> hasMoreResults()");
        this.resultSetIndex = factory.incrementIndex();
        int numResultSets = factory.getNumberOfResultSets(sqlString);
        return (resultSetIndex < (numResultSets + 1));
    }

    /**
     * Returns the result set at the specified index. No index increment
     * or decrement occurs during this method call. This is useful for
     * pulling out outparams (at negative resultSetIndex positions) that
     * masquerade as resultsets.
     * @param sqlString the SQL string to look up.
     * @param pIndex the parameter index (1-based).
     * @param sqlType the SQL Type of the parameter.
     * @return the Object at the specified parameter index.
     * @exception SQLException if an error occurs.
     */
    public final Object getOutParam(final String sqlString, final int pIndex, 
            final int sqlType) throws SQLException {
        LOG.debug(">> getOutParam(" + sqlString + "," + pIndex + ","
            + sqlType + ")");
        return factory.getOutParamValueAt(sqlString, pIndex, sqlType);
    }

    /**
     * Returns the update count for the specified SQL string.
     * @param sqlString the SQL string to look up.
     * @return an updatecount as an Integer.
     */
    public final Integer getUpdateCount(final String sqlString) {
        LOG.debug(">> getUpdateCount(" + sqlString + ")");
        return new Integer(factory.getUpdateCount(sqlString));
    }
}    
