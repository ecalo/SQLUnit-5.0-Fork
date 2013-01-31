/*
 * $Id: AllOrNothingMatcher.java,v 1.3 2004/09/28 19:15:45 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/matchers/AllOrNothingMatcher.java,v $
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
 * The AllOrNothingMatcher is an implementation of the IMatcher interface
 * used to define rulesets for matching columns in SQLUnit.  This 
 * implementation is of no particular use, except as a template for
 * other useful matchers and for testing. Based on the argument supplied to it
 * via the argument map, it will either always returns true or false for the 
 * column being matched.
 * @sqlunit.matcher.arg name="match"
 *  description="Always returns true if set to true, else always returns 
 *  false."
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.3 $
 */
public class AllOrNothingMatcher implements IMatcher {

    /**
     * Default constructor as per contract with IMatcher.
     */
    public AllOrNothingMatcher() {
        // empty default constructor.
    }

    /**
     * Returns true if the key "match" in the args Map contains the String
     * "true", else always returns false. The source and target are 
     * effectively no-ops in this matcher.
     * @param source the String representing the source to be matched.
     * @param target the String representing the target to be matched.
     * @param args a Map of name value pairs of arguments passed in.
     * @return true if the matching strategy results in success.
     * @exception SQLUnitException if the matching failed for some reason.
     */
    public final boolean isEqual(final String source, final String target, 
            final Map args) throws SQLUnitException {
        String matchArg = (String) args.get("match");
        if (matchArg == null) {
            throw new SQLUnitException(IErrorCodes.MATCHER_EXCEPTION,
                new String[] {this.getClass().getName(),
                "Value for key 'match' is NULL"});
        }
        if (matchArg.equalsIgnoreCase("true")) {
            return true;
        } else if (matchArg.equalsIgnoreCase("false")) {
            return false;
        } else {
            throw new SQLUnitException(IErrorCodes.MATCHER_EXCEPTION,
                new String[] {this.getClass().getName(),
                "Value for key 'match' is " + matchArg
                + ", which is neither TRUE nor FALSE"});
        }
    }
}
