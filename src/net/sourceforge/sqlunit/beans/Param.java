/*
 * $Id: Param.java,v 1.6 2005/05/29 01:08:46 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/beans/Param.java,v $
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
package net.sourceforge.sqlunit.beans;

import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.utils.TypeUtils;

import org.apache.log4j.Logger;

/**
 * The Param bean models an Object whose member variables are the attributes
 * of the param element.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.6 $
 */
public class Param {

    private static final Logger LOG = Logger.getLogger(Param.class);

    private String id;
    private String name;
    private String type;
    private String isNull = "false";
    private String inOut = "in";
    private String value;
    private int scale = 0;
    private String typeName;

    /**
     * Default constructor.
     */
    public Param() {
        // default constructor
    }

    /**
     * Returns the param id.
     * @return the param id.
     */
    public final String getId() { return id; }

    /**
     * Sets the param id.
     * @param id the param id.
     */
    public final void setId(final String id) { this.id = id; }

    /**
     * Returns the param name.
     * @return the param name.
     */
    public final String getName() { return name; }

    /**
     * Sets the param name.
     * @param name the param name.
     */
    public final void setName(final String name) { this.name = name; }

    /**
     * Returns the param type.
     * @return the param type.
     */
    public final String getType() { return type; }

    /**
     * Convenience method to return the SQL (numeric) type from the
     * supplied (String XML) type.
     * @return the SQL Type for this parameter.
     * @exception SQLUnitException if one is thrown.
     */
    public final int getSQLType() throws SQLUnitException {
        return TypeUtils.getSqlTypeFromXmlType(type);
    }

    /**
     * Sets the param type.
     * @param type the param type.
     */
    public final void setType(final String type) { this.type = type; }

    /**
     * Returns true if the param contains null values.
     * @return "true" if the param contains null values.
     */
    public final String getIsNull() { 
        return (isNull == null ? "false" : isNull);
    }

    /**
     * Convenience method to return if the parameter is set to NULL.
     * @return true if the parameter is NULL.
     */
    public final boolean isNull() {
        return (getIsNull().equals("true"));
    }

    /**
     * Sets to "true" if the param supports null values.
     * @param isNull true if the param supports null values.
     */
    public final void setIsNull(final String isNull) { this.isNull = isNull; }

    /**
     * Returns the param in-out value.
     * @return the param in-out value.
     */
    public final String getInOut() { return inOut; }

    /**
     * Returns true if the parameter is an IN or INOUT parameter.
     * @return true or false.
     */
    public final boolean isInParameter() {
        return (("in").equals(inOut) || ("inout").equals(inOut));
    }

    /**
     * Returns true if the parameter is an OUT or INOUT parameter.
     * @return true or false.
     */
    public final boolean isOutParameter() {
        return (("out").equals(inOut) || ("inout").equals(inOut));
    }

    /**
     * Sets the param in-out value.
     * @param inOut the param in-out value.
     */
    public final void setInOut(final String inOut) { 
        if (inOut == null) { return; }
        this.inOut = inOut; 
    }

    /**
     * Returns the param value.
     * @return the param value.
     */
    public final String getValue() { return value; }

    /**
     * Sets the param value.
     * @param value the param value.
     */
    public final void setValue(final String value) { this.value = value; }

    /**
     * Returns the scale of the parameter value. This value is used by
     * SQLUnit only if the type of the parameter maps to a BigDecimalType
     * eg, NUMERIC and DECIMAL types. If this value is not supplied, 
     * SQLUnit will try to guess it based on the number of digits after
     * the decimal point in the value.
     * @return the scale of this parameter, if applicable.
     */
    public final int getScale() {
        if (this.scale == 0) {
            this.scale = TypeUtils.computeScale(getValue());
        }
        return scale;
    }

    /**
     * Sets the scale of the parameter. The scale is applicable only if
     * the parameter maps to a BigDecimalType.
     * @param scale the scale of the parameter as a String.
     */
    public final void setScale(String scale) {
        if (scale == null) {
            this.scale = 0;
            return;
        }
        try {
            this.scale = Integer.parseInt(scale);
        } catch (NumberFormatException e) {
            LOG.warn("Value of scale should be numeric, not " + scale);
            this.scale = 0;
        }
    }

    /**
     * Overloaded convenience setter to set the scale as an int. The scale
     * is applicable only if the parameter maps to a BigDecimalType.
     * @param scale the scale of the parameter as an int.
     */
    public final void setScale(int scale) {
        this.scale = scale;
    }

    /**
     * Return the type name of a user-defined type. If the data type of the
     * Param object is a user-defined type (REF, STRUCT, DISTINCT, JAVA_OBJECT,
     * or named array type), then this information is used by SQLUnit.
     * @return the type name of the parameter, if applicable.
     */
    public final String getTypeName() {
        return typeName;
    }

    /**
     * Set the type name of the user-defined type.
     * @param typeName the fully qualified SQL type name of the Param.
     */
    public final void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
