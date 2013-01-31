/*
 * $Id: StringType.java,v 1.11 2005/04/13 06:26:46 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/types/StringType.java,v $
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
 * Models a String type.
 * @author Ralph Brendler (rbrendler@users.sourceforge.net)
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.11 $
 * @sqlunit.type name="StringType" input="Yes" output="Yes" sortable="Yes"
 *  wraps="java.lang.String"
 * @sqlunit.typename name="CHAR" server="Any"
 * @sqlunit.typename name="LONGVARCHAR" server="Any"
 * @sqlunit.typename name="VARCHAR" server="Any"
 * @sqlunit.typename name="NCHAR" server="Microsoft SQL Server"
 * @sqlunit.typename name="NVARCHAR" server="Microsoft SQL Server"
 * @sqlunit.typename name="FIXED_CHAR" server="Oracle"
 */
public class StringType extends UnsupportedType {
    
    /**
     * Formats a String type.
     * @param obj an Object to be converted to the IType interface.
     * @return the String representation of the object.
     * @exception SQLUnitException if the formatting failed.
     */
    protected String format(final Object obj) throws SQLUnitException {
        if (!(obj instanceof String)) {
            throw new SQLUnitException(IErrorCodes.UNSUPPORTED_DATATYPE_FORMAT,
                new String[] {SymbolTable.getCurrentResultKey(),
                (obj == null ? "NULL" : obj.getClass().getName()),
                getName(), (new Integer(getId())).toString()});
        }
        String str = (String) obj;
        return str;
    }

    /**
     * Parses a String type.
     * @param str the String representation of the DATE.
     * @return a java.sql.Date object.
     * @exception SQLUnitException if the parsing failed.
     */
    protected Object parse(final String str) throws SQLUnitException {
        return str;
    }

    /**
     * Returns a negative, zero or positive number according to whether this
     * object is smaller, equal or larger than the passed in object.
     * @param obj an Object of type DateType.
     * @return a negative, zero or positive number.
     */
    public final int compareTo(final Object obj) {
        if (!(obj instanceof StringType)) {
            return 0;
        }
        StringType that = (StringType) obj;
        boolean isEitherNull = TypeUtils.checkIfNull(this, that);
        if (isEitherNull) {
            return TypeUtils.compareNulls(this, that);
        } else {
            String thisValue = (String) this.getValue();
            String thatValue = (String) that.getValue();
            return (thisValue.compareTo(thatValue));
        }
    }
}
