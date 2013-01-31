/*
 * $Id: TimeType.java,v 1.10 2005/04/13 06:26:46 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/types/TimeType.java,v $
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

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Models a TIME type.
 * @author Ralph Brendler (rbrendler@users.sourceforge.net)
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.10 $
 * @sqlunit.type name="TimeType" input="Yes" output="Yes" sortable="Yes"
 *  wraps="java.sql.Time"
 * @sqlunit.typename name="TIME" server="Any"
 */
public class TimeType extends UnsupportedType {
    
    private static final int NUM_SECS_PER_MIN = 60;
    private static final int NUM_SECS_PER_HR = 3600;

    /** 
     * Specifies the pattern for the DATE object. This can be overriden 
     * to work with other database formats for time in subclasses.
     */
    protected static String PATTERN = "HH:mm:ss";

    private SimpleDateFormat formatter = new SimpleDateFormat(PATTERN);

    /**
     * Formats a TIME into its String representation.
     * @param obj an Object to be converted to the IType interface.
     * @return the String representation of the object.
     * @exception SQLUnitException if the formatting failed.
     */
    protected String format(final Object obj) throws SQLUnitException {
        if (!(obj instanceof Time)) {
            throw new SQLUnitException(IErrorCodes.UNSUPPORTED_DATATYPE_FORMAT,
                new String[] {SymbolTable.getCurrentResultKey(),
                (obj == null ? "NULL" : obj.getClass().getName()),
                getName(), (new Integer(getId())).toString()});
        }
        Time time = (Time) obj;
        long millisSinceEpoch = time.getTime();
        Date d = new Date(millisSinceEpoch);
        return formatter.format(d);
    }

    /**
     * Parses a String representing a TIME to a Time object.
     * @param str the String representation of the DATE.
     * @return a Time object.
     * @exception SQLUnitException if the parsing failed.
     */
    protected Object parse(final String str) throws SQLUnitException {
        try {
            Date d = formatter.parse(str);
            Time time = new Time(d.getTime());
            return time;
        } catch (ParseException e) {
            throw new SQLUnitException(IErrorCodes.UNSUPPORTED_DATATYPE_PARSE,
                new String[] {SymbolTable.getCurrentResultKey(),
                str, "java.sql.Time", getName(),
                (new Integer(getId())).toString()});
        }
    }

    /**
     * Returns true if the two objects are equal, false otherwise.
     * @param obj the Object to compare against.
     * @return true or false.
     */
    public final boolean equals(final Object obj) {
        if (!(obj instanceof TimeType)) {
            return false;
        } else {
            return (compareTo(obj) == 0);
        }
    }

    /**
     * Returns the hashcode for the underlying wrapped datatype.
     * @return the hashcode for the underlying wrapped datatype.
     */
    public final int hashCode() {
        return super.hashCode();
    }

    /**
     * Returns a negative, zero or positive number according to whether this
     * object is smaller, equal or larger than the passed in object.
     * @param obj an Object of type TimeType.
     * @return a negative, zero or positive number.
     */
    public final int compareTo(final Object obj) {
        if (!(obj instanceof TimeType)) {
            return 0;
        }
        TimeType that = (TimeType) obj;
        boolean isEitherNull = TypeUtils.checkIfNull(this, that);
        if (isEitherNull) {
            return TypeUtils.compareNulls(this, that);
        } else {
            // compare only the time portion
            Calendar cal = new GregorianCalendar();
            cal.setTime(new Date(((Time) this.getValue()).getTime()));
            int thisSecondsSinceToday = 
                ((cal.get(Calendar.HOUR_OF_DAY) * NUM_SECS_PER_HR)
                + (cal.get(Calendar.MINUTE) * NUM_SECS_PER_MIN)
                + (cal.get(Calendar.SECOND)));
            cal.setTime(new Date(((Time) that.getValue()).getTime()));
            int thatSecondsSinceToday = 
                ((cal.get(Calendar.HOUR_OF_DAY) * NUM_SECS_PER_HR)
                + (cal.get(Calendar.MINUTE) * NUM_SECS_PER_MIN)
                + (cal.get(Calendar.SECOND)));
            return (thisSecondsSinceToday - thatSecondsSinceToday);
        }
    }
}
