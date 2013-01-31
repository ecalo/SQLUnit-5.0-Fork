/*
 * $Id: MockResultSetUtils.java,v 1.4 2004/10/05 20:13:04 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/mock/MockResultSetUtils.java,v $
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

import com.mockrunner.mock.jdbc.MockResultSet;
import com.mockrunner.mock.jdbc.MockResultSetMetaData;

import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Collection of utility methods to manipulate MockResultSets.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.4 $
 */
public final class MockResultSetUtils {

    private static final Logger LOG = 
        Logger.getLogger(MockResultSetUtils.class);

    private static final int CURSOR_OUTPARAM_TYPE_ID = -10;

    /**
     * Private Constructor. Cannot be instantiated.
     */
    private MockResultSetUtils() {
        // private constructor, cannot instantiate.
    }

    /**
     * Convenience method to return the zero-th result set. We use this 
     * to keep the number of resultsets that must be returned from
     * the method representing the SQL call. This method is expected to
     * be called from subclasses of the AbstractMockDatabase class.
     * @param numResultSets the number of result sets returned by this SQL.
     * @return a MockResultSet object.
     */
    public static MockResultSet buildZerothResultSet(final int numResultSets) {
        LOG.debug(">> buildZerothResultSet(" + numResultSets + ")");
        MockResultSet mrs = new MockResultSet("COUNT");
        mrs.addRow(new Object[] {new Integer(numResultSets), new Integer(-1)});
        return mrs;
    }

    /**
     * Convenience method to return the number of resultsets available 
     * for this query by parsing the MockResultSet returned at index 0. 
     * If the resultset is not a COUNT resultset, then it returns -1.
     * @param mrs a MockResultSet object.
     * @return the number of ResultSets available for this query.
     */
    public static int getNumberOfResultSets(final MockResultSet mrs) {
        LOG.debug(">> getNumberOfResultSets(MockResultSet)");
        int count = -1;
        if (!mrs.getId().equals("COUNT")) {
            return count;
        }
        try {
            while (mrs.next()) {
                count = mrs.getInt(1);
                break;
            }
            mrs.close();
        } catch (SQLException e) {
            LOG.error("SQLException [" + e.getErrorCode() + "]:"
                + e.getMessage() + " caught!");
            return -1;
        }
        return count;
    }

    /**
     * Sets the update count as the second column in the mock resultset for
     * returning number of results (position 0).
     * @param updateCount the count of the number of rows updated.
     * @return a MockResultSet containing the update count.
     */
    public static MockResultSet buildZerothResultSetWithUpdateCount(
            final int updateCount) {
        LOG.debug(">> buildZerothResultSetWithUpdateCount(" + updateCount
            + ")");
        MockResultSet mrs = new MockResultSet("COUNT");
        mrs.addRow(new Object[] {new Integer(0), new Integer(updateCount)});
        return mrs;
    }

    /**
     * Parses the update count from the MockResultSet passed in.
     * @param mrs a MockResultSet object returned from the zeroth position.
     * @return the update count.
     */
    public static int getUpdateCount(final MockResultSet mrs) {
        LOG.debug(">> getUpdateCount(MockResultSet)");
        int updateCount = -1;
        if (!mrs.getId().equals("COUNT")) {
            return updateCount;
        }
        try {
            while (mrs.next()) {
                updateCount = mrs.getInt(2);
                break;
            }
            mrs.close();
        } catch (SQLException e) {
            LOG.error("SQLException [" + e.getErrorCode() + "]: "
                + e.getMessage() + " caught!");
            return -1;
        }
        return updateCount;
    }

    /**
     * Convenience method to build a scalar out parameter as a resultset.
     * @param value the String value of the parameter.
     * @return a MockResultSet object.
     */
    public static MockResultSet buildScalarOutParam(final String value) {
        LOG.debug(">> buildScalarOutParam(" + value + ")");
        MockResultSet mrs = new MockResultSet("OUTPARAM");
        mrs.addRow(new Object[] {value});
        return mrs;
    }

    /**
     * Convenience method to extract a scalar out parameter from a resultset.
     * The object is cast to the appropriate class based on the object's 
     * metadata.
     * @param mrs a MockResult object.
     * @param sqlType the SQL type of the object.
     * @return an Object representing the out parameter at that position.
     */
    public static Object getOutParam(final MockResultSet mrs, 
            final int sqlType) {
        LOG.debug(">> getOutParam(MockResultSet," + sqlType + ")");
        if (sqlType == CURSOR_OUTPARAM_TYPE_ID) { // CURSOR
            return mrs;
        } else {
            String value = null;
            try {
                while (mrs.next()) {
                    value = mrs.getString(1);
                    break;
                }
                mrs.close();
            } catch (SQLException e) {
                LOG.error("SQLException [" + e.getErrorCode() + "]:"
                    + e.getMessage() + " caught!");
                return null;
            }
            try {
                String xmlType = TypeUtils.getXmlTypeFromSqlType(sqlType);
                return TypeUtils.convertToObject(value, xmlType);
            } catch (SQLUnitException e) {
                LOG.error("Could not get outparam value!");
                return null;
            }
        }
    }

    /**
     * Convenience method for creating a ResultSet object with an embedded
     * SQLException.
     * @param sqlCode the SQLException sqlCode value.
     * @param message the SQLException message value.
     * @return a MockResultSet.
     */
    public static MockResultSet buildSQLExceptionResultSet(final int sqlCode, 
            final String message) {
        LOG.debug(">> buildSQLExceptionResultSet(" + sqlCode + "," + message
            + ")");
        SQLException ex = new SQLException(message, null, sqlCode);
        MockResultSet mrs = new MockResultSet("EXCEPTION");
        mrs.addRow(new Object[] {ex});
        return mrs;
    }

    /**
     * Convenience method to determine if the ResultSet is an Exception 
     * resultset.
     * @param mrs the MockResultSet object.
     * @return true if the resultset represents an SQLException, else false.
     */
    public static boolean isSQLException(final MockResultSet mrs) {
        LOG.debug(">> isSQLException(mrs)");
        if (mrs == null) { return false; }
        return (mrs.getId().equals("EXCEPTION"));
    }

    /**
     * Convenience method to parse out and return the embedded SQLException
     * object from the specified ResultSet object.
     * @param mrs the MockResultSet object.
     * @return an SQLException object.
     */
    public static SQLException getSQLExceptionFromResultSet(
            final MockResultSet mrs) {
        LOG.debug(">> getSQLExceptionFromResultSet(mrs)");
        if (!mrs.getId().equals("EXCEPTION")) { return null; }
        SQLException ex = null;
        try {
            while (mrs.next()) {
                ex = (SQLException) mrs.getObject(1);
                break;
            }
            mrs.close();
        } catch (SQLException e) {
            LOG.error("SQLException [" + e.getErrorCode() + "]:"
                + e.getMessage() + " caught!");
            return null;
        }
        return ex;
    }

    /**
     * Convenience method to build a ResultSetMetaData object using data
     * passed in through the array of ColumnMetaData objects.
     * @param cols an array of ColumnMetaData objects.
     * @return a MockResultSetMetaData object.
     */
    public static MockResultSetMetaData buildMetaData(
            final ColumnMetaData[] cols) {
        LOG.debug(">> buildMetaData(cols)");
        MockResultSetMetaData mrsmd = new MockResultSetMetaData();
        mrsmd.setColumnCount(cols.length);
        for (int i = 0; i < cols.length; i++) {
            mrsmd.setColumnName((i + 1), cols[i].getColumnName());
            mrsmd.setColumnType((i + 1), cols[i].getColumnType());
        }
        return mrsmd;
    }

    /**
     * Replaces the positional parameters in a parameterized SQL string
     * with appropriate values from the parameter map.
     * @param oldString the parameterized SQL string.
     * @param parameterMap a Map of parameters.
     * @return the unparameterized String.
     */
    public static String replaceParameters(final String oldString, 
            final Map parameterMap) {
        LOG.debug(">> replaceParameters(" + oldString + ","
            + parameterMap.toString() + ")");
        if (oldString.indexOf("?") > -1) {
            List keySetList = new ArrayList();
            keySetList.addAll(parameterMap.keySet());
            Collections.sort(keySetList);
            String[] parameterArray = new String[keySetList.size()];
            Iterator paramIter = keySetList.iterator();
            int i = 0;
            while (paramIter.hasNext()) {
                Integer key = (Integer) paramIter.next();
                parameterArray[i++] = (String) parameterMap.get(key);
            }
            String newString = oldString;
            for (i = 0; i < parameterArray.length; i++) {
                String temp = newString.replaceFirst("\\?", parameterArray[i]);
                newString = temp;
            }
            LOG.debug(">> converting [" + oldString + "] => [" + newString
                + "]");
            return newString;
        } else {
            return oldString;
        }
    }
}
