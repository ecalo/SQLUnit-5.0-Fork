/*
 * $Id: SignificantDigitsMatcher.java,v 1.1 2005/07/13 01:52:19 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/matchers/SignificantDigitsMatcher.java,v $
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

import java.math.BigDecimal;
import java.util.Map;

import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IMatcher;
import net.sourceforge.sqlunit.SQLUnitException;

/**
 * The SignificantDigitsMatcher is an implementation of the IMatcher interface
 * used to compare FLOAT values where you only really care about matching to a
 * certain number of significant digits to the right of the decimal. Especially
 * useful when you're generating your test script by copying and pasting out of
 * a SQL query tool that displays a different number of significant digits than
 * SQLUnit pulls back from the database.
 * 
 * Example configuration:
 * &lt;match resultset-id="1" row-id="*" col-id="9-13,15-18"
 *    matcher="net.sourceforge.sqlunit.matchers.SignificantDigitsMatcher"&gt;
 *    &lt;arg name="signif-digits" value="3" /&gt;
 * &lt;/match&gt;
 * @sqlunit.matcher.arg name="signif-digits"
 *  description="Number of significant digits to match to"
 *
 * @author Tim Cull (trcull@yahoo.com)
 * @version $Revision: 1.1 $
 */
public class SignificantDigitsMatcher implements IMatcher {

    /**
     * Default constructor per contract with IMatcher.
     */
    public SignificantDigitsMatcher() {
        super();
    }

    /**
     * Returns true if the value of the target is equal to the source
     * up to a certain number of significant digits.
     * @param source the String representing the source to be matched.
     * @param target the String representing the target to be matched.
     * @param args a Map of name value pairs of arguments passed in.
     * @return true if the matching strategy resulted in success.
     * @exception SQLUnitException if there was a problem with matching.
     */
    public boolean isEqual(String source, String target, Map args)
            throws SQLUnitException {

        String aDigits = (String) args.get("signif-digits");
        if (aDigits == null) {
            throw new SQLUnitException(IErrorCodes.MATCHER_EXCEPTION,
                new String[] {this.getClass().getName(),
                "Value for key 'signif-digits' is NULL"});
        }
        // is digits an integer?
        int iDigits = 0;
        try {
            iDigits = Integer.parseInt(aDigits);
        } catch (NumberFormatException e) {
            throw new SQLUnitException(IErrorCodes.MATCHER_EXCEPTION,
                new String[] {this.getClass().getName(),
                "Value of key 'signif-digits' is not an INT"});
        }
        // cannot have the digits less than 0 
        if (iDigits < 0) {
            throw new SQLUnitException(IErrorCodes.MATCHER_EXCEPTION,
                new String[] {this.getClass().getName(),
                "Value of key 'signif-digits' must be >= 0"});
        }
        // is the source a float?
        BigDecimal dSource;
        try {
            dSource = new BigDecimal(source);
        } catch (NumberFormatException e) {
            throw new SQLUnitException(IErrorCodes.MATCHER_EXCEPTION,
                new String[] {this.getClass().getName(),
                "Value of 'source' is not a FLOAT"});
        }
        // is the target a float?
        BigDecimal dTarget;
        try {
            dTarget = new BigDecimal(target);
        } catch (NumberFormatException e) {
            throw new SQLUnitException(IErrorCodes.MATCHER_EXCEPTION,
                new String[] {this.getClass().getName(),
                "Value of 'target' is not a FLOAT"});
        }
        //convert source to a BigDecimal with right number of decimal places
        dSource = dSource.setScale(iDigits,BigDecimal.ROUND_HALF_UP );
        //convert target to a BigDecimal with right number of decimal places
        dTarget = dTarget.setScale(iDigits,BigDecimal.ROUND_HALF_UP);
        // return the match
        return dSource.equals(dTarget);
    }
}
