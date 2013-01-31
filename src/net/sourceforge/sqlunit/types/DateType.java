/*
 * $Id: DateType.java,v 1.10 2005/04/13 06:26:46 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/types/DateType.java,v $
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

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Models a DATE type.
 * @author Ralph Brendler (rbrendler@users.sourceforge.net)
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.10 $
 * @sqlunit.type name="DateType" input="Yes" output="Yes" sortable="Yes"
 *  wraps="java.sql.Date"
 * @sqlunit.typename name="DATE" server="Any"
 */
public class DateType extends UnsupportedType {
    
    /** 
     * Specifies the pattern for the DATE object. This may be overriden
     * in subclasses to work with alternate formats for dates in different
     * databases.
     */
    protected static String PATTERN = "yyyy-MM-dd";

    private SimpleDateFormat formatter = new SimpleDateFormat(PATTERN);

    /**
     * Formats a DATE into its String representation.
     * @param obj an Object to be converted to the IType interface.
     * @return the String representation of the object.
     * @exception SQLUnitException if the formatting failed.
     */
    protected String format(final Object obj) throws SQLUnitException {
        if (!(obj instanceof java.sql.Date)) {
            throw new SQLUnitException(IErrorCodes.UNSUPPORTED_DATATYPE_FORMAT,
                new String[] {SymbolTable.getCurrentResultKey(),
                (obj == null ? "NULL" : obj.getClass().getName()),
                getName(), (new Integer(getId())).toString()});
        }
        java.sql.Date date = (java.sql.Date) obj;
        long millisSinceEpoch = date.getTime();
        java.util.Date d = new java.util.Date(millisSinceEpoch);
        return formatter.format(d);
    }

    /**
     * Parses a String representing a DATE to a java.sql.Date.
     * @param str the String representation of the DATE.
     * @return a java.sql.Date object.
     * @exception SQLUnitException if the parsing failed.
     */
    protected Object parse(final String str) throws SQLUnitException {
        try {
            java.util.Date d = formatter.parse(str);
            java.sql.Date date = new java.sql.Date(d.getTime());
            return date;
        } catch (ParseException e) {
            throw new SQLUnitException(IErrorCodes.UNSUPPORTED_DATATYPE_PARSE,
                new String[] {SymbolTable.getCurrentResultKey(),
                str, "java.sql.Date", getName(),
                (new Integer(getId())).toString()});
        }
    }

    /**
     * Returns true if Object obj is the same as this object, else false.
     * @param obj the Object to compare with.
     * @return true or false.
     */
    public final boolean equals(final Object obj) {
        if (!(obj instanceof DateType)) {
            return false;
        } else {
            return (compareTo(obj) == 0);
        }
    }

    /**
     * The hashcode for this object, returns the hashcode for the wrapped
     * java.sql.Date object.
     * @return the hashcode for the underlying Date object.
     */
    public final int hashCode() {
        return super.hashCode();
    }

    /**
     * Returns a negative, zero or positive number according to whether this
     * object is smaller, equal or larger than the passed in object.
     * @param obj an Object of type DateType.
     * @return a negative, zero or positive number.
     */
    public final int compareTo(final Object obj) {
        if (!(obj instanceof DateType)) {
            return 0;
        }
        DateType that = (DateType) obj;
        boolean isEitherNull = TypeUtils.checkIfNull(this, that);
        if (isEitherNull) {
            return TypeUtils.compareNulls(this, that);
        } else {
            // get the underlying values
            java.sql.Date thisValue = (java.sql.Date) this.getValue();
            java.sql.Date thatValue = (java.sql.Date) that.getValue();
            // strip off the time portion by doing a format and parse
            try {
                String thisDateStr = formatter.format(
                    new java.util.Date(thisValue.getTime()));
                long thisMillisSinceEpoch = formatter.parse(
                    thisDateStr).getTime();
                String thatDateStr = formatter.format(
                    new java.util.Date(thatValue.getTime()));
                long thatMillisSinceEpoch = formatter.parse(
                    thatDateStr).getTime();
                return (int) (thisMillisSinceEpoch - thatMillisSinceEpoch);
            } catch (ParseException e) { return 0; }
        }
    }
}
