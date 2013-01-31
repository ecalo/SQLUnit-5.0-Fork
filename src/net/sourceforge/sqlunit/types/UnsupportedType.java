/*
 * $Id: UnsupportedType.java,v 1.8 2004/12/02 21:22:30 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/types/UnsupportedType.java,v $
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
package net.sourceforge.sqlunit.types;

import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IType;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.SymbolTable;

import org.apache.log4j.Logger;

/**
 * Defines behavior for a type which is not supported by SQLUnit. This also
 * serves as the superclass for all the other types.
 * @author Ralph Brendler (rbrendler@users.sourceforge.net)
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.8 $
 * @sqlunit.type name="UnsupportedType" input="No" output="No" sortable="No"
 *  wraps="n/a"
 * @sqlunit.typename name="DATALINK" server="Any"
 * @sqlunit.typename name="DISTINCT" server="Any"
 * @sqlunit.typename name="NULL" server="Any"
 * @sqlunit.typename name="REF" server="Any"
 * @sqlunit.typename name="STRUCT" server="Any"
 */
public class UnsupportedType implements IType {
    
    private static final Logger LOG = Logger.getLogger(UnsupportedType.class);

    private String name;
    private int id;
    private Object value;

    /**
     * Default constructor.
     */
    public UnsupportedType() {
        // empty default constructor
    }

    /**
     * Returns the XML name for this type.
     * @return the XML name for this type.
     */
    public final String getName() {
        return name;
    }

    /**
     * Sets the XML name for this type.
     * @param name the XML name for this type.
     */
    public final void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the SQL Type Code for this type.
     * @return the SQL type code for this type.
     */
    public final int getId() {
        return id;
    }

    /**
     * Sets the SQL Type code for this type.
     * @param id the SQL Type code for this type.
     */
    public final void setId(final int id) {
        this.id = id;
    }

    /**
     * Returns the stored value for this type as an Object.
     * @return the stored value of this type.
     */
    public final Object getValue() {
        return value;
    }

    /**
     * Sets the stored value for this type.
     * @param obj the value to store in this type.
     */
    public final void setValue(final Object obj) {
        this.value = obj;
    }

    /**
     * Converts the Object to its String representation. This is called
     * from the result tag when trying to build the DatabaseResult object
     * from the specified result, as well as from the ResultSetBean when
     * trying to build the DatabaseResult from the results of an SQL
     * or stored procedure call. This method abstracts out some common
     * operations that need to be done during this time, and provides
     * hooks for subclasses to provide their own behavior. Each of these
     * hooks also have default behavior, which is provided by this
     * class if not overriden by the subclass.
     * @param obj an Object to be converted to String.
     * @return the String representation of the object.
     * @exception SQLUnitException if the conversion to String failed.
     */
    public final String toString(final Object obj) throws SQLUnitException {
        if (obj == null) { return "NULL"; }
        if (obj instanceof String) { return formatString((String) obj); }
        setValue(obj);
        return format(obj);
    }

    /**
     * Converts a String representation of the Object to the Object itself.
     * Where provided, this typically uses the parsing methods of the 
     * underlying Java type. This is called from the param element when 
     * we need to set values of variables for a SQL or stored procedure 
     * to execute. This method abstracts out some common operations that
     * need to be done in all subclasses, and provides hooks for subclasses
     * to provide their own behavior. Each of these hooks have default
     * behavior, which is provided by this class if not overriden by the
     * subclass.
     * @param str the String to be converted into an Object.
     * @return the Object
     * @exception SQLUnitException if the conversion to Object failed.
     */
    public final Object toObject(final String str) throws SQLUnitException {
        if (("NULL").equals(str)) { return null; }
        Object obj = parse(str);
        setValue(obj);
        return obj;
    }

    /**
     * Specifies the comparison order for this datatype. In this case there
     * is no comparison possible, so it always returns zero. This will be
     * overriden by subclasses.
     * @param obj the Object to compare against.
     * @return an int indicating whether this object is less than, equal to
     * or greater than the object passed in.
     */
    public int compareTo(final Object obj) {
        return 0;
    }

    /**
     * Returns the hashcode for the underlying value. This is likely to be
     * overriden by subclasses.
     * @return the hashcode for the underlying value.
     */
    public int hashCode() {
        return (value == null ? -1 : value.hashCode());
    }

    /**
     * Returns true if the value of the two types are equal. This is likely
     * to be overriden by subclasses.
     * @param obj the Object to compare against.
     * @return true if the values of the two objects are equal.
     */
    public boolean equals(final Object obj) {
        LOG.debug("Checking for equality");
        if (!(obj instanceof UnsupportedType)) { return false; }
        UnsupportedType that = (UnsupportedType) obj;
        if (this == null && that == null) { return true; }
        if (this == null ^ that == null) { return false; }
        Object thisValue = this.getValue();
        Object thatValue = that.getValue();
        if (thisValue == null && thatValue == null) {
            return true;
        }
        if (thisValue != null && thatValue != null) {
            if (thisValue.getClass().getName().equals(
                thatValue.getClass().getName())) {
                return (thisValue.toString().equals(thatValue.toString()));
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * The formatting hook with default behavior which is meant to be
     * overriden by the subclass. This method is called from within toString()
     * after verifying that the Object passed in is not a String and is not
     * null. A setValue() is called before passing control off to this method.
     * @param obj the Object to convert to a String.
     * @return the String representation of the object.
     * @exception SQLUnitException if the conversion to String failed.
     */
    protected String format(final Object obj) throws SQLUnitException {
        throw new SQLUnitException(IErrorCodes.UNSUPPORTED_DATATYPE,
            new String[] {SymbolTable.getCurrentResultKey(),
            getName(), new Integer(getId()).toString()});
    }

    /**
     * The formatting hook which gets activated if the passed in Object
     * is a String. Subclasses will typically just do the default behavior,
     * ie, pass it back, but some of them convert the String to MD5 Digest.
     * @param obj the String object.
     * @return another String object, the contents of which depend on the
     * particular implementation in the subclass. Default behavior is to
     * return the passed in String.
     * @exception SQLUnitException if there was some problem with formatting.
     */
    protected String formatString(final String obj) throws SQLUnitException {
        return (String) obj;
    }

    /**
     * The parsing hook with default behavior which is meant to be overriden
     * by the subclass. This method is called from within toObject() after
     * verifying that the String is not a "NULL". A setValue() is called 
     * in the toObject() after parse() is called.
     * @param str the String to parse into an Object.
     * @return the Object.
     * @exception SQLUnitException if the conversion to Object failed.
     */
    protected Object parse(final String str) throws SQLUnitException {
        throw new SQLUnitException(IErrorCodes.UNSUPPORTED_DATATYPE,
            new String[] {SymbolTable.getCurrentResultKey(),
            getName(), new Integer(getId()).toString()});
    }
}
