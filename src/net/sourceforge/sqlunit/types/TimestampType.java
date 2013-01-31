/*
 * $Id: TimestampType.java,v 1.10 2005/04/13 06:26:46 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/types/TimestampType.java,v $
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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Models a TIMESTAMP type.
 * @author Ralph Brendler (rbrendler@users.sourceforge.net)
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.10 $
 * @sqlunit.type name="TimestampType" input="Yes" output="Yes" sortable="Yes"
 *  wraps="java.sql.Timestamp"
 * @sqlunit.typename name="TIMESTAMP" server="Any"
 */
public class TimestampType extends UnsupportedType {
    
    /** 
     * Specifies the pattern for the DATE object. This may be overriden in
     * subclasses to work with alternate forms of the TIMESTAMP type in
     * different databases.
     */
    protected static String PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    private SimpleDateFormat formatter = new SimpleDateFormat(PATTERN);

    /**
     * Formats a TIMESTAMP into its String representation.
     * @param obj an Object to be converted to the IType interface.
     * @return the String representation of the object.
     * @exception SQLUnitException if the formatting failed.
     */
    protected String format(final Object obj) throws SQLUnitException {
        if (!(obj instanceof Timestamp)) {
            throw new SQLUnitException(IErrorCodes.UNSUPPORTED_DATATYPE_FORMAT,
                new String[] {SymbolTable.getCurrentResultKey(),
                (obj == null ? "NULL" : obj.getClass().getName()),
                getName(), (new Integer(getId())).toString()});
        }
        Timestamp timestamp = (Timestamp) obj;
        long millisSinceEpoch = timestamp.getTime();
        Date d = new Date(millisSinceEpoch);
        return formatter.format(d);
    }

    /**
     * Parses a String representing a TIMESTAMP to a Timestamp.
     * @param str the String representation of the TIMESTAMP.
     * @return a java.sql.Time object.
     * @exception SQLUnitException if the parsing failed.
     */
    protected Object parse(final String str) throws SQLUnitException {
        try {
            Date d = formatter.parse(str);
            Timestamp timestamp = new Timestamp(d.getTime());
            return timestamp;
        } catch (ParseException e) {
            throw new SQLUnitException(IErrorCodes.UNSUPPORTED_DATATYPE_PARSE,
                new String[] {SymbolTable.getCurrentResultKey(),
                str, "java.sql.Timestamp", getName(),
                (new Integer(getId())).toString()});
        }
    }

    /**
     * Returns true if this TimestampType is equal to the one passed in.
     * @param obj the Object to compare against.
     * @return true or false.
     */
    public final boolean equals(final Object obj) {
        if (!(obj instanceof TimestampType)) {
            return false;
        } else {
            return (compareTo(obj) == 0);
        }
    }

    /**
     * Returns the hashcode for the underlying wrapped datatype.
     * @return the hashcode for the underlying datatype.
     */
    public final int hashCode() {
        return super.hashCode();
    }

    /**
     * Returns a negative, zero or positive number according to whether this
     * object is smaller, equal or larger than the passed in object.
     * @param obj an Object of type TimestampType.
     * @return a negative, zero or positive number.
     */
    public final int compareTo(final Object obj) {
        if (!(obj instanceof TimestampType)) {
            return 0;
        }
        TimestampType that = (TimestampType) obj;
        boolean isEitherNull = TypeUtils.checkIfNull(this, that);
        if (isEitherNull) {
            return TypeUtils.compareNulls(this, that);
        } else {
            try {
                // strip off nanoseconds
                Timestamp thisTs = (Timestamp) this.getValue();
                String thisTsFormatted = 
                    formatter.format(new Date(thisTs.getTime()));
                Timestamp thisTsStripped = 
                    (Timestamp) this.toObject(thisTsFormatted);
                Timestamp thatTs = (Timestamp) that.getValue();
                String thatTsFormatted = 
                    formatter.format(new Date(thatTs.getTime()));
                Timestamp thatTsStripped = 
                    (Timestamp) that.toObject(thatTsFormatted);
                return (int) (thisTsStripped.getTime()
                    - thatTsStripped.getTime());
            } catch (SQLUnitException e) {
                return 0; // :NOTE: will never happen
            }
        }
    }
}
