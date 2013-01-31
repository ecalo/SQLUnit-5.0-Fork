/*
 * $Id: IntrospectingResultSetFactory.java,v 1.7 2004/10/05 20:13:04 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/mock/IntrospectingResultSetFactory.java,v $
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

import com.mockrunner.jdbc.ResultSetFactory;
import com.mockrunner.mock.jdbc.MockResultSet;

import org.apache.log4j.Logger;

import java.sql.SQLException;

/**
 * Introspects a given IMockDataStore object and invokes the named method
 * to return ResultSet objects.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.7 $
 */
public class IntrospectingResultSetFactory implements ResultSetFactory {
    
    private static final Logger LOG = Logger.getLogger(
        IntrospectingResultSetFactory.class);

    private String mockDbClassName = null;
    private int index = Integer.MIN_VALUE;

    /**
     * Instantiate a Introspecting Result Set Factory using the classname
     * of the IMockDataStore object.
     * @param mockDbClassName the name of a class implementing the 
     * IMockDatabase interface and representing a particular database.
     */
    public IntrospectingResultSetFactory(final String mockDbClassName) {
        LOG.debug(">> [IntrospectingResultSetFactory(" + mockDbClassName
            + ")]");
        this.mockDbClassName = mockDbClassName;
    }

    /**
     * Build a mockresultset object using the method id. Delegates to the
     * method and resultsetid in the IMockDatabase that was used for 
     * instantiating this factory.
     * @param sql a string containing the sql to execute.
     * @return a MockResultSet object.
     */
    public final MockResultSet create(final String sql) {
        return create(sql, 1);
    }

    /**
     * Returns the resultset for the sql at the specified index. 
     * @param sql a string containing the sql to execute.
     * @param rsIndex the index of the resultset to return.
     * @return a MockResultSet object.
     */
    public final MockResultSet create(final String sql, final int rsIndex) {
        LOG.debug(">> create(" + sql + "," + rsIndex + ")");
        try {
            IMockDatabase mockdb = 
                (IMockDatabase) Class.forName(mockDbClassName).newInstance();
            return mockdb.getResultSet(sql, rsIndex);
        } catch (Exception e) {
            LOG.error("Caught an Exception creating resultset for " + sql
                + "(" + rsIndex + "):" + e.toString());
            return null;
        }
    }

    /**
     * Moves the resultset index up by 1. If it is set to the default
     * value of Integer.MIN_VALUE, then it is bumped up to 1 the first time.
     * @return the new incremented index value.
     */
    public final int incrementIndex() {
        LOG.debug(">> incrementIndex");
        if (index == Integer.MIN_VALUE) { 
            index = 1;
        } else {
            index++;
        }
        LOG.debug(">> incrementIndex to " + index);
        return index;
    }

    /**
     * Convenience method to return the number of result sets for a 
     * particular SQL query. By convention, resultset 0 has a single
     * row and column containing the number of resultsets to return.
     * @param sql the string containing the SQL to execute.
     * @return the number of resultsets for this query.
     * @exception SQLException if an error occurs.
     */
    public final int getNumberOfResultSets(final String sql)
            throws SQLException {
        LOG.debug(">> getNumberOfResultSets(" + sql + ")");
        int numResultSets = -1;
        return (MockResultSetUtils.getNumberOfResultSets(create(sql, 0)));
    }

    /**
     * Convenience method to return the update count for an SQL which 
     * updates the database, such as INSERT, UPDATE or DELETE. If there
     * is no update count declared in the procedure method, it will
     * return -1.
     * @param sql the string containing the SQL to execute.
     * @return the update count for the SQL, or -1 if there was no updates.
     */
    public final int getUpdateCount(final String sql) {
        LOG.debug(">> getUpdateCount(" + sql + ")");
        return (MockResultSetUtils.getUpdateCount(create(sql, 0)));
    }

    /**
     * Returns the outparam value at the specified position and converts
     * to an object of the specified type.
     * @param sql the SQL string to look up.
     * @param pIndex the parameter index (1-based).
     * @param sqlType the SQL Type to use for conversion.
     * @return an Object representing the outparam.
     * @exception SQLException if an error occurs.
     */
    public final Object getOutParamValueAt(final String sql, final int pIndex, 
            final int sqlType) throws SQLException {
        LOG.debug(">> getOutParamValueAt(" + sql + "," + pIndex + ","
            + sqlType + ")");
        int resultSetIndex = (-1) * pIndex;
        return (MockResultSetUtils.getOutParam(
            create(sql, resultSetIndex), sqlType));
    }
}
