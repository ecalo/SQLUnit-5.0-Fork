/*
 * $Id: TypeUtils.java,v 1.7 2005/05/21 19:41:23 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/utils/TypeUtils.java,v $
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
package net.sourceforge.sqlunit.utils;

import java.math.BigDecimal;
import java.util.HashMap;

import net.sourceforge.sqlunit.IType;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.SymbolTable;
import net.sourceforge.sqlunit.TypeFactory;
import net.sourceforge.sqlunit.TypeMapper;

import org.apache.log4j.Logger;

/**
 * Provides commonly used functionality to convert between SQL, Java and
 * XML data type names.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.7 $
 */
public final class TypeUtils {

    private static final Logger LOG = Logger.getLogger(TypeUtils.class);

    private static HashMap xmlToSqlMap = new HashMap();
    private static HashMap sqlToXmlMap = new HashMap();
    private static boolean mapCreated = false;

    /**
     * Private Constructor. Cannot be instantiated.
     */
    private TypeUtils() {
        // private constructor, cannot instantiate
    }

    /**
     * Returns the XML Type given the SQL Type Id. For example, return INTEGER
     * when passed a value of java.sql.Types.INTEGER.
     * @param sqlType a valid java.sql.Types value.
     * @return the XML Type name.
     * @exception SQLUnitException if the sqlType is invalid. Should never
     * occur since we call this only when parsing a Result.
     */
    public static String getXmlTypeFromSqlType(final int sqlType) 
            throws SQLUnitException {
        LOG.debug(">> getXmlTypeFromSqlType(" + sqlType + ")");
        return TypeMapper.findNameById(sqlType);
    }

    /**
     * Returns the SQL Type given the XML Type name. For example, returns
     * java.sql.Types.INTEGER when passed in the String INTEGER.
     * @param xmlType a valid String corresponding to a java.sql.Type constant.
     * @return the corresponding java.sql.Types constant value.
     * @exception SQLUnitException if the xmlType is incorrectly specified.
     */
    public static int getSqlTypeFromXmlType(final String xmlType) 
            throws SQLUnitException {
        LOG.debug(">> getSqlTypeFromXmlType(" + xmlType + ")");
        return TypeMapper.findIdByName(xmlType);
    }

    /**
     * Converts a String representation of a specific xmlType to the
     * corresponding Java object for use by PreparedStatement and its
     * children. This is used to convert the input parameters for a 
     * Stored procedure from the String representation to the appropriate
     * object representation.
     * @param value the String representation of the value to convert.
     * @param xmlType the XML Type of the target object.
     * @return an Object of the required java type.
     * @exception SQLUnitException if there was a problem converting.
     */
    public static Object convertToObject(final String value, 
            final String xmlType) throws SQLUnitException {
        LOG.debug(">> convertToObject(" + value + "," + xmlType + ")");
        if (value == null) { return null; }
        if (SymbolTable.isVariableName(value)) {
            return convertToObject(SymbolTable.getValue(value), xmlType);
        } else {
            IType type = TypeFactory.getInstance(xmlType);
            return type.toObject(value);
        }
    }

    /**
     * Converts an Object with the specified type to the corresponding
     * String representation. Only a few types are given special treatment
     * by SQLUnit.
     * @param obj the Object to convert to a String.
     * @param sqlType the type of the object as defined by java.sql.Types.
     * @return a String representation of the object.
     * @exception SQLUnitException if there was a problem during conversion.
     */
    public static String convertToString(final Object obj, final int sqlType)
            throws SQLUnitException {
        LOG.debug(">> convertToString(obj," + sqlType + ")");
        if (obj == null) { return "NULL"; }
        IType type = TypeFactory.getInstance(sqlType);
        return type.toString(obj);
    }

    /**
     * Convenience method to convert a String to an int, and replace
     * with a default value if the conversion fails.
     * @param s the String to convert to an int.
     * @param def the default int value to use.
     * @return the int after the conversion.
     */
    public static int convertStringToInt(final String s, final int def) {
        LOG.debug(">> convertToInt(" + s + "," + def + ")");
        int i = def;
        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            // :IGNORE: reset to default
        }
        return i;
    }

    /**
     * Returns true if Java Support has been turned on.
     * @return true if Java Support is turned on, else false.
     */
    public static boolean hasJavaObjectSupport() {
        LOG.debug(">> hasJavaObjectSupport()");
        String javaSupport = SymbolTable.getValue(
            SymbolTable.JAVA_OBJECT_SUPPORT);
        return (javaSupport != null && javaSupport.equals("on"));
    }

    /**
     * Computes the scale value of a value that maps to a BigDecimalType
     * class by counting the number of digits after the decimal point.
     * @param value the String value of a parameter that maps to the
     * BigDecimalType object.
     * @return the scale of this value.
     */
    public static int computeScale(String value) {
        if (value == null) { 
            return 0;
        }
        int decpos = value.indexOf(".");
        if (decpos > -1) {
            return value.substring(decpos + 1).length();
        } else {
            return 0;
        }
    }

    /**
     * Returns true if both String values resolve to BigDecimal objects
     * with identical values and scale.
     * @param bd1 the String value of a BigDecimal number.
     * @param bd2 the String value of another BigDecimal number.
     * @return true if the two numbers are equal.
     */
    public static boolean isValueOfBigDecimalEqual(String bd1, String bd2) {
        BigDecimal thisBigDecimal = null;
        BigDecimal thatBigDecimal = null;
        if (!("NULL").equals(bd1)) { 
            thisBigDecimal = new BigDecimal(bd1);
        }
        if (!("NULL").equals(bd2)) {
            int scale = TypeUtils.computeScale(bd1);
            thatBigDecimal = new BigDecimal(bd2).setScale(scale,
                BigDecimal.ROUND_HALF_DOWN);
        }
        if (thisBigDecimal == null) {
            if (thatBigDecimal == null) {
                return true;
            } else {
                return false;
            }
        } else {
            return thisBigDecimal.equals(thatBigDecimal);
        }
    }

    /**
     * Returns true if either of the IType arguments contain null values.
     * @param typeObj1 an IType object to compare.
     * @param typeObj2 another IType object to compare.
     * @return true if either of the IType objects contain null values.
     */
    public static boolean checkIfNull(IType typeObj1, IType typeObj2) {
        Object value1 = null;
        Object value2 = null;
        if (typeObj1 != null) {
            value1 = typeObj1.getValue();
        }
        if (typeObj2 != null) {
            value2 = typeObj2.getValue();
        }
        return (value1 == null || value2 == null);
    }

    /**
     * Compares two IType object values, one or both of which is NULL.
     * If both are null, then returns 0. If value of typeObj1 is NULL, 
     * then returns -1, else returns 1. We assume that the order of 
     * precedence in the comparison is that NULL is lesser of the two.
     * @param typeObj1 an IType object to compare.
     * @param typeObj2 another IType object to compare.
     * @return true -1, 0, or 1, according as value of typeObj1 is less
     * than, equal to or greater than typeObj2.
     */
    public static int compareNulls(IType typeObj1, IType typeObj2) {
        Object value1 = null;
        Object value2 = null;
        if (typeObj1 != null) {
            value1 = typeObj1.getValue();
        }
        if (typeObj2 != null) {
            value2 = typeObj2.getValue();
        }
        if (value1 == null && value2 != null) { return -1; }
        if (value1 != null && value2 == null) { return 1; }
        // both null
        return 0;
    }
}
