/*
 * $Id: ForgivingNullMatcher.java,v 1.2 2005/07/16 01:15:21 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/matchers/ForgivingNullMatcher.java,v $
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

import java.util.Map;

import net.sourceforge.sqlunit.IMatcher;
import net.sourceforge.sqlunit.SQLUnitException;

/**
 * The ForgivingNullMatcher is an implementation of the IMatcher interface
 * used to compare values you know are either supposed to be NULL (but might
 * be textually represented differently) or are actually equal. Basically
 * searches for the word NULL (case insensitive) or for blank elements or
 * for elements that are equal Strings. Especially useful when you're
 * generating your test script by copying and pasting out of a SQL query tool
 * that displays NULLs differently than SQLUnit pulls back from the database.
 * 
 * For example, all of these values are treated as equal:
 * 
 *     &lt;col id="14" name="benchcode" type="INTEGER"&gt;[NULL]&lt;/col&gt;
 *     &lt;col id="14" name="benchcode" type="INTEGER"&gt;NULL&lt;/col&gt;
 *     &lt;col id="14" name="benchcode" type="INTEGER"&gt;&lt;/col&gt;
 *     &lt;col id="14" name="benchcode" type="INTEGER"&gt;null&lt;/col&gt;
 *
 * Example configuration:
 * &lt;match resultset-id="1" row-id="*" col-id="14"
 *    matcher="net.sourceforge.sqlunit.matchers.ForgivingNullMatcher"&gt;
 * &lt;/match&gt;
 *
 * @sqlunit.matcher.arg name="None" description="-"
 *
 * @author Tim Cull (trcull@yahoo.com)
 * @version $Revision: 1.2 $
 */
public class ForgivingNullMatcher implements IMatcher {

    public static final String NULL_STRING = "NULL";

    /**
     * Default constructor as per contract with IMatcher 
     */
    public ForgivingNullMatcher() {
        super();
    }

    /**
     * Returns true if the value of the target and the source
     * are equivalent to NULL (either by being empty or by containing the 
     * word NULL) or are actually equal to each other 
     * @param source the String representing the source to be matched.
     * @param target the String representing the target to be matched.
     * @param args a Map of name value pairs of arguments passed in.
     * @return true if the matching strategy resulted in success.
     * @exception SQLUnitException if there was a problem with matching.
     */
    public boolean isEqual(String source, String target, Map args)
            throws SQLUnitException {
        
        int iSourceLength = source.length();
        int iTargetLength = target.length();
        boolean retVal = false;
        if (iSourceLength==0 && iTargetLength==0){
            // both are empty
            retVal = true;
        } else if (iSourceLength==0){
            // only source is empty
            // eliminate effect of case
            target = target.toUpperCase();
            if(target.indexOf(NULL_STRING)>-1){
                retVal = true;
            }
        } else if (iTargetLength==0){
            // only target is empty
            // eliminate effect of case
            source = source.toUpperCase();
            if(source.indexOf(NULL_STRING)>-1){
                retVal = true;
            }
        } else {
            // neither is empty
            if (source.equals(target)){
                // either they're both actually equal, or they're both the
                // same textual representation of NULL
                retVal = true;
            } else {
                // they're not exactly equal, but might still be different
                // textual representations of NULL
                source = source.toUpperCase();
                target = target.toUpperCase();
                if(target.indexOf(NULL_STRING)>-1
                        && source.indexOf(NULL_STRING)>-1){
                    retVal = true;
                }
            }
        }
        return retVal;
    }
}
