/*
 * $Id: OutParam.java,v 1.9 2006/06/25 23:02:50 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/beans/OutParam.java,v $
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
import net.sourceforge.sqlunit.TypeMapper;
import net.sourceforge.sqlunit.types.BigDecimalType;
import net.sourceforge.sqlunit.utils.TypeUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * The OutParam models a SQLUnit outparam element.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.9 $
 */
public class OutParam {

    private static final Logger LOG = Logger.getLogger(OutParam.class);

    private String id;
    private String name;
    private String type;
    private Object value;

    /**
     * Default constructor.
     */
    public OutParam() {
        // default constructor
    }

    /**
     * Returns the outparam id.
     * @return the param id.
     */
    public final String getId() { return id; }

    /**
     * Sets the outparam id.
     * @param id the param id.
     */
    public final void setId(final String id) { this.id = id; }

    /**
     * Returns the outparam name.
     * @return the outparam name.
     */
    public final String getName() { return name; }

    /**
     * Sets the outparam name.
     * @param name the outparam name.
     */
    public final void setName(final String name) { this.name = name; }

    /**
     * Returns the outparam type.
     * @return the outparam type.
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
     * Sets the outparam type.
     * @param type the outparam type.
     */
    public final void setType(final String type) { this.type = type; }

    /**
     * Returns the outparam value as a String.
     * @return the outparam value.
     */
    public final String getValue() { 
        return value.toString(); 
    }

    /**
     * Sets the param value from a String.
     * @param value the param value.
     */
    public final void setValue(final String value) { 
        this.value = value; 
    }

    /**
     * Sets the outparam value from a ResultSetBean.
     * @param value a ResultSetBean object.
     */
    public final void setValue(final ResultSetBean value) {
        this.value = value;
    }

    /**
     * Sets the outparam value from a StructBean.
     * @param value a ResultSetBean object.
     */
    public final void setValue(final StructBean value) {
        this.value = value;
    }

    /**
     * Returns true if the outparams are equal.
     * @param obj the Object to compare against.
     * @return true if the two Objects are equal, false otherwise.
     */
    public final boolean equals(final Object obj) {
        if (!(obj instanceof OutParam)) { return false; }
        OutParam that = (OutParam) obj;
        try {
            boolean isTypeEqual = this.getType().equals(that.getType());
            if (isTypeEqual) {
                String className = TypeMapper.findClassById(
                    TypeUtils.getSqlTypeFromXmlType(this.getType()));
                // special treatment for BigDecimal, we need to normalize
                // the scales
                if (BigDecimalType.class.getName().equals(className)) {
                    return isTypeEqual && TypeUtils.isValueOfBigDecimalEqual(
                        this.getValue(), that.getValue());
                } else {
                    return isTypeEqual
                        && this.getValue().equals(that.getValue());
                }
            } else {
                return false;
            }
        } catch (SQLUnitException e) {
            return false;
        }
    }

    /**
     * Returns a hashcode for the OutParam object. This is the sum of the
     * hashcode for the type and the hashcode for the value.
     * @return the hash code for the OutParam object.
     */
    public final int hashCode() {
        return this.getValue().hashCode() + this.getType().hashCode();
    }

    /**
     * Returns the string representation of this object.
     * @return the String representation of this object.
     */
    public final String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(getValue()).append("(").append(getType()).append(")");
        return buf.toString();
    }

    /**
     * Returns a JDOM Element representing this bean.
     * @return a JDOM Element representing this bean.
     */
    public final Element toElement() {
        Element elOutParam = new Element("outparam");
        elOutParam.setAttribute("id", getId());
        if (getName() != null) {
            elOutParam.setAttribute("name", getName());
        }
        elOutParam.setAttribute("type", getType());
        if (value instanceof ResultSetBean) {
            ResultSetBean rsb = (ResultSetBean) value;
            Element elResultSet = rsb.toElement();
            elOutParam.addContent(elResultSet);
        } else if (value instanceof StructBean) {
            StructBean sb = (StructBean) value;
            Element elStruct = sb.toElement();
            elOutParam.addContent(elStruct);
        } else {
            elOutParam.setText(value.toString());
        }
        return elOutParam;
    }
}
