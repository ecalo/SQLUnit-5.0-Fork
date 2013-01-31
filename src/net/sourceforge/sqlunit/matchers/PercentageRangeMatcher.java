/*
 * $Id: PercentageRangeMatcher.java,v 1.3 2004/09/28 19:15:45 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/matchers/PercentageRangeMatcher.java,v $
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
 * The PercentageRangeMatcher is an implementation of the IMatcher interface
 * used to define rulesets for matching columns in SQLUnit. This matcher
 * will accept a percentage tolerance value and check to see that the target 
 * is within (+/-) tolerance percent of the source.
 * @sqlunit.matcher.arg name="pc-tolerance"
 *  description="A percentage tolerance value."
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.3 $
 */
public class PercentageRangeMatcher implements IMatcher {

    private static final int HUNDRED_INT = 100;
    private static final float HUNDRED_FLOAT = 100.0F;

    /**
     * Default constructor as per contract with IMatcher.
     */
    public PercentageRangeMatcher() {
        // empty default constructor.
    }

    /**
     * Returns true if the value of the target is withing (+/-) a specified
     * tolerance value of the source. Note that in this case, the source,
     * target and tolerance must all be numeric values.
     * @param source the String representing the source to be matched.
     * @param target the String representing the target to be matched.
     * @param args a Map of name value pairs of arguments passed in.
     * @return true if the matching strategy resulted in success.
     * @exception SQLUnitException if there was a problem with matching.
     */
    public final boolean isEqual(final String source, final String target, 
            final Map args) throws SQLUnitException {

        String aTolerance = (String) args.get("pc-tolerance");
        if (aTolerance == null) {
            throw new SQLUnitException(IErrorCodes.MATCHER_EXCEPTION,
                new String[] {this.getClass().getName(),
                "Value for key 'pc-tolerance' is NULL"});
        }
        // is tolerance a float?
        float iTolerance = 0;
        try {
            iTolerance = Float.parseFloat(aTolerance);
        } catch (NumberFormatException e) {
            throw new SQLUnitException(IErrorCodes.MATCHER_EXCEPTION,
                new String[] {this.getClass().getName(),
                "Value of key 'pc-tolerance' is not a FLOAT"});
        }
        // cannot have the tolerance exceed 100
        if (iTolerance > HUNDRED_FLOAT) {
            throw new SQLUnitException(IErrorCodes.MATCHER_EXCEPTION,
                new String[] {this.getClass().getName(),
                "Value of key 'pc-tolerance' must be between 0 and 100"});
        }
        // is the source a float?
        float iSource = 0;
        try {
            iSource = Float.parseFloat(source);
        } catch (NumberFormatException e) {
            throw new SQLUnitException(IErrorCodes.MATCHER_EXCEPTION,
                new String[] {this.getClass().getName(),
                "Value of 'source' is not a FLOAT"});
        }
        // is the target an integer?
        float iTarget = 0;
        try {
            iTarget = Float.parseFloat(target);
        } catch (NumberFormatException e) {
            throw new SQLUnitException(IErrorCodes.MATCHER_EXCEPTION,
                new String[] {this.getClass().getName(),
                "Value of 'target' is not a FLOAT"});
        }
        // return the match
        return ((iTarget >= (iSource - (iTolerance * HUNDRED_INT)))
            && (iTarget <= (iSource + (iTolerance * HUNDRED_INT))));
    }
}
