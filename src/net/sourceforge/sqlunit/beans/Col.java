/*
 * $Id: Col.java,v 1.11 2005/05/21 19:41:23 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/beans/Col.java,v $
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

import net.sourceforge.sqlunit.IType;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.TypeFactory;
import net.sourceforge.sqlunit.TypeMapper;
import net.sourceforge.sqlunit.types.BigDecimalType;
import net.sourceforge.sqlunit.utils.TypeUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * Models a col element, representing a column in a database row.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.11 $
 */
public class Col implements Comparable {
    
    private String id;
    private String name;
    private String type;
    private String className;
    private String value;

    private static final Logger LOG = Logger.getLogger(Col.class);

    /**
     * Returns the Id for this column.
     * @return the id for this column.
     */
    public final String getId() {
        return id;
    }

    /**
     * Set the id for this column.
     * @param id the id for this column.
     */
    public final void setId(final String id) {
        this.id = id;
    }

    /**
     * Return the name for this column.
     * @return the name for this column.
     */
    public final String getName() {
        return name;
    }

    /**
     * Set the name for this column,
     * @param name the name for this column.
     */
    public final void setName(final String name) {
        this.name = name;
    }

    /**
     * Return the XML type for this column.
     * @return the XML type for this column,
     */
    public final String getType() {
        return type;
    }

    /**
     * Set the XML type for this column,
     * @param type the XML type for this column.
     */
    public final void setType(final String type) {
        this.type = type;
    }

    /**
     * Returns the class name (for IJavaObject objects) for this column,
     * @return the class name for this column.
     */
    public final String getClassName() {
        return className;
    }

    /**
     * Set the class name (for IJavaObject objects) for this column,
     * @param className the class name for this column.
     */
    public final void setClassName(final String className) {
        this.className = className;
    }

    /**
     * Returns the value of this column as a String.
     * @return the value of this column as a String.
     */
    public final String getValue() {
        return value;
    }

    /**
     * Set the value of this column from a String.
     * @param value the value to set.
     */
    public final void setValue(final String value) {
        this.value = value;
    }

    /**
     * Returns a negative, zero or positive value depending on whether
     * the contents of this column is less than, equal to or greater 
     * than the contents of the passed in Col object.
     * @param obj an instance of a Col object.
     * @return a negative, zero or positive number.
     */
    public final int compareTo(final Object obj) {
        LOG.debug("compareTo(" + obj.toString() + ")");
        if (!(obj instanceof Col)) { return 0; }
        Col that = (Col) obj;
        if (!(this.getType().equals(that.getType()))) { return 0; }
        try {
            IType thisTypeObj = TypeFactory.getInstance(this.getType());
            IType thatTypeObj = TypeFactory.getInstance(that.getType());
            thisTypeObj.toObject(this.getValue());
            thatTypeObj.toObject(that.getValue());
            return (thisTypeObj.compareTo(thatTypeObj));
        } catch (SQLUnitException e) {
            return 0;
        }
    }

    /**
     * Returns a true or false based on whether the objects are equal.
     * @param obj the Object to compare against.
     * @return true or false.
     */
    public final boolean equals(final Object obj) {
        if (!(obj instanceof Col)) { return false; }
        Col that = (Col) obj;
        try {
            boolean isTypeEqual = this.getType().equals(that.getType());
                //(TypeUtils.getSqlTypeFromXmlType(this.getType())
                //== TypeUtils.getSqlTypeFromXmlType(that.getType()));
            if (isTypeEqual) {
                String typeClassName = TypeMapper.findClassById(
                    TypeUtils.getSqlTypeFromXmlType(this.getType()));
                // special treatment for BigDecimal, we need to normalize
                // the scales.
                if (BigDecimalType.class.getName().equals(typeClassName)) {
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
     * Returns a hashcode for the Col object. This is the sum of the
     * hashcodes for the type and the value.
     * @return a hash code for the Col object.
     */
    public final int hashCode() {
        return this.getType().hashCode() + this.getValue().hashCode();
    }

    /**
     * Returns a JDOM element that represents this Col object.
     * @return a JDOM element representing this object.
     */
    public final Element toElement() {
        Element elCol = new Element("col");
        elCol.setAttribute("id", getId());
        elCol.setAttribute("name", 
            (getName() == null ? "c" + getId() : getName()));
        if (getClassName() != null) { 
            elCol.setAttribute("class", getClassName()); 
        }
        elCol.setAttribute("type", getType());
        elCol.setText(getValue());
        return elCol;
    }

    /**
     * Returns a compact String representation of the Col object.
     * @return a String representation of the Col object.
     */
    public final String toString() {
        return getValue() + "(" + getType() + ")";
    }
}
