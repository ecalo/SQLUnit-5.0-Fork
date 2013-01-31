/*
 * $Id: TypelessPercentageMatcher.java,v 1.3 2004/09/28 19:15:45 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/matchers/TypelessPercentageMatcher.java,v $
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
package net.sourceforge.sqlunit.matchers;

import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IMatcher;
import net.sourceforge.sqlunit.SQLUnitException;

import java.util.Map;

/**
 * The TypelessPercentageMatcher matches columns which may differ by a 
 * specified percentage. This can be useful when comparing numeric values
 * across data types which have different default precisions.
 * @sqlunit.matcher.arg name="pc-tolerance"
 *  description="A percentage tolerance value."
 * @author Chris Watts (c_watts@users.sourceforge.net)
 * @version $Revision: 1.3 $
 */
public class TypelessPercentageMatcher implements IMatcher {

    private static final int HUNDRED = 100;

    /**
     * Default constructor.
     */
    public TypelessPercentageMatcher() {
        // empty default constructor
    }

    /**
     * Returns true if the value of the target is withing (+/-) a specified
     * percentage tolerance value of the source if the source and
     * target can both be converted to doubles. If both values are strings 
     * then the two string contents are compared directly. If either or 
     * source or target can be converted to double but the other cannot then 
     * an exception is thrown.
     * @param source the String representing the source to be matched.
     * @param target the String representing the target to be matched.
     * @param args a Map of name value pairs of arguments passed in.
     * @return true if the matching strategy was successful.
     * @exception SQLUnitException if there was a problem with matching.
     */
    public final boolean isEqual(final String source, final String target, 
            final Map args) throws SQLUnitException {
        boolean sourceIsDouble = true;
        boolean targetIsDouble = true;
        
        String aTolerance = (String) args.get("pc-tolerance");
        if (aTolerance == null) {
            throw new SQLUnitException(IErrorCodes.MATCHER_EXCEPTION,
                new String[] {this.getClass().getName(),
                "Value for key 'pc-tolerance' is NULL"});
        }
        // is tolerance a double?
        double toleranceValue = 0;
        try {
            toleranceValue = Double.parseDouble(aTolerance);
        } catch (NumberFormatException e) {
            throw new SQLUnitException(IErrorCodes.MATCHER_EXCEPTION,
                new String[] {this.getClass().getName(),
                "Value of key 'pc-tolerance' is not a double"});
        }
        
        // can the source be converted to a double?
        double sourceValue = 0;
        try {
            sourceValue = Double.parseDouble(source);
        } catch (NumberFormatException e) {
            sourceIsDouble = false;
        }
        // can the target be converted to a double?
        double targetValue = 0;
        try {
            targetValue = Double.parseDouble(target);
        } catch (NumberFormatException e) {
            targetIsDouble = false;
        }
        
        // If only one of the source or target values can be converted to a 
        // double then something is wrong.
        if (targetIsDouble != sourceIsDouble) {
            throw new SQLUnitException(IErrorCodes.MATCHER_EXCEPTION,
                new String[] {this.getClass().getName(),
                "Cannot compare source and target when one is double "
                + "and the other is not"});
        }

        // return the match
        if (targetIsDouble && sourceIsDouble) {
              return (Math.abs(targetValue - sourceValue)
                  <= Math.abs(sourceValue * toleranceValue / HUNDRED));
        } else {
            return target.equals(source);
        }
    }
}
