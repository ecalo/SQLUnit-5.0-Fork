/*
 * $Id: IType.java,v 1.5 2004/09/25 20:40:17 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/IType.java,v $
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
package net.sourceforge.sqlunit;

/**
 * Interface to be implemented by all SQLUnit datatypes.
 * @author Ralph Brendler (rblendler@users.sourceforge.net)
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.5 $
 */
public interface IType extends Comparable {
    
    /**
     * Returns the XML name for this type. The XML name is used by SQLUnit
     * to specify the type name in the XML files.
     * @return the XML name for this type.
     */
    String getName();

    /**
     * Sets the XML name for this type.
     * @param name the XML name for this type.
     */
    void setName(String name);

    /**
     * Returns the SQL Type Code for this type. This is used to refer to 
     * a type by the JDBC driver.
     * @return the SQL Type code for this type.
     */
    int getId();

    /**
     * Sets the SQL Type Code for this type.
     * @param id the SQL Type Code for this type.
     */
    void setId(int id);

    /**
     * Returns the stored value for this type as an Object.
     * @return the stored value of this type.
     */
    Object getValue();

    /**
     * Sets the stored value for this type.
     * @param obj the value to store in this type.
     */
    void setValue(Object obj);

    /**
     * Parses a String representation of an IType to the corresponding
     * object value. If parsing is succesful, it also stores the parsed
     * value as this type object's value.
     * @param str the String representation of the SQLUnit datatype.
     * @return an Object which needs to be cast appropriately by caller.
     * @exception SQLUnitException if the parsing failed.
     */
    Object toObject(String str) throws SQLUnitException;

    /**
     * Formats an implementation of the IType interface to its corresponding
     * String value. If formatting is successful, then this method will also
     * store the passed in Object in its native format.
     * @param obj an Object to be converted to the IType interface.
     * @return the String representation of the object.
     * @exception SQLUnitException if the formatting failed.
     */
    String toString(Object obj) throws SQLUnitException;
}
