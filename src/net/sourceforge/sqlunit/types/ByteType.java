/*
 * $Id: ByteType.java,v 1.10 2005/04/13 06:26:46 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/types/ByteType.java,v $
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
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.SymbolTable;
import net.sourceforge.sqlunit.utils.TypeUtils;

/**
 * Models a Byte type.
 * @author Ralph Brendler (rbrendler@users.sourceforge.net)
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.10 $
 * @sqlunit.type name="ByteType" input="Yes" output="Yes" sortable="Yes"
 *  wraps="java.lang.Byte"
 * @sqlunit.typename name="TINYINT" server="Any"
 */
public class ByteType extends UnsupportedType {
    
    /**
     * Formats a Byte type.
     * @param obj an Object to be converted to the IType interface.
     * @return the String representation of the object.
     * @exception SQLUnitException if the formatting failed.
     */
    protected final String format(final Object obj) throws SQLUnitException {
        if (!(obj instanceof Byte)) {
            throw new SQLUnitException(IErrorCodes.UNSUPPORTED_DATATYPE_FORMAT,
                new String[] {SymbolTable.getCurrentResultKey(),
                (obj == null ? "NULL" : obj.getClass().getName()),
                getName(), (new Integer(getId())).toString()});
        }
        Byte s = (Byte) obj;
        return s.toString();
    }

    /**
     * Parses a Byte type.
     * @param str the String representation of the object.
     * @return an object which needs to be cast to a Byte.
     * @exception SQLUnitException if the parsing failed.
     */
    protected final Object parse(final String str) throws SQLUnitException {
        try {
            Byte s = new Byte(str);
            return s;
        } catch (NumberFormatException e) {
            throw new SQLUnitException(IErrorCodes.UNSUPPORTED_DATATYPE_PARSE,
                new String[] {SymbolTable.getCurrentResultKey(),
                str, "java.lang.Byte", getName(),
                (new Integer(getId())).toString()});
        }
    }

    /**
     * Returns a negative, zero or positive number according to whether this
     * object is smaller, equal or larger than the passed in object.
     * @param obj an Object of type DateType.
     * @return a negative, zero or positive number.
     */
    public final int compareTo(final Object obj) {
        if (!(obj instanceof ByteType)) {
            return 0;
        }
        ByteType that = (ByteType) obj;
        boolean isEitherNull = TypeUtils.checkIfNull(this, that);
        if (isEitherNull) {
            return TypeUtils.compareNulls(this, that);
        } else {
            Byte thisValue = (Byte) this.getValue();
            Byte thatValue = (Byte) that.getValue();
            return (thisValue.compareTo(thatValue));
        }
    }
}
