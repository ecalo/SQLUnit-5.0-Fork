/*
 * $Id: MatchPattern.java,v 1.5 2004/09/25 20:40:17 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/MatchPattern.java,v $
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
package net.sourceforge.sqlunit;

import org.apache.log4j.Logger;

import java.util.Map;
import java.util.StringTokenizer;

/**
 * MatchPattern fills a role in SQLUnit similar to Regular Expressions in 
 * text pattern matching. It is instantiated from the data supplied in the
 * Match tag using the MatchHandler class. Unlike an user-defined object
 * which implements the IMatcher interface, this class will not only know
 * the algorithm to apply for matching (using the IMatcher object), but 
 * also where in the result object to apply the algorithm. The MatchHandler
 * class builds a list of MatchPattern objects which is successively matched
 * by DatabaseResult to determine whether to apply the pattern, and if so,
 * return the results of the match. If no MatchPattern object is found to 
 * match against for the particular DatabaseResultKey, then the default
 * matching (exact equality) is used. Multiple values may be specified as
 * regular expression filters similar to that used in the Unix cut.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.5 $
 */
public class MatchPattern {

    private static final Logger LOG = Logger.getLogger(MatchPattern.class);

    private String resultSetIdFilter = null;
    private String rowIdFilter = null;
    private String colIdFilter = null;
    private String matcherClass = null;
    private Map args = null;

    /**
     * Builds a MatchPattern object with the supplied input parameters.
     * @param resultSetIdFilter a regular expression.
     * @param rowIdFilter a regular expression.
     * @param colIdFilter a regular expression.
     * @param matcherClass the full java class name of the IMatcher object.
     * @param args a Map of name value pairs representing additional parameters
     * to the IMatcher object to determine matching.
     */
    public MatchPattern(final String resultSetIdFilter, 
            final String rowIdFilter, final String colIdFilter, 
            final String matcherClass, final Map args) {
        LOG.debug("[MatchPattern(" + resultSetIdFilter + "," + rowIdFilter
            + "," + colIdFilter + "," + matcherClass + ",args)]");
        this.resultSetIdFilter = resultSetIdFilter;
        this.rowIdFilter = rowIdFilter;
        this.colIdFilter = colIdFilter;
        this.matcherClass = matcherClass;
        this.args = args;
    }

    /**
     * Returns the full name of the Matcher class invoked by this MatchPattern.
     * @return the Matcher class name.
     */
    public final String getMatcherClass() {
        LOG.debug(">> getMatcherClass()");
        return this.matcherClass;
    }

    /**
     * Returns true if this MatchPattern object can be applied to the 
     * DatabaseResult element specified by the [resultSetId, rowId, colId]
     * tuple. May return false if the conclusion is indeterminate.
     * @param resultSetId the resultSet id.
     * @param rowId the row id.
     * @param colId the column id.
     * @return true if the MatchPattern can be applied here.
     * @exception SQLUnitException if a regular expression was incorrect.
     */
    public final boolean canApply(final int resultSetId, final int rowId, 
            final int colId) throws SQLUnitException {
        LOG.debug(">> canApply(" + resultSetId + "," + rowId + ","
            + colId + ")");
        return matchPattern(this.resultSetIdFilter, resultSetId)
            && matchPattern(this.rowIdFilter, rowId)
            && matchPattern(this.colIdFilter, colId);
    }

    /**
     * Returns true or false as a result of applying the matching algorithm
     * specified by the IMatcher object.
     * @param source the source value of the DatabaseResult element.
     * @param target the target value of the DatabaseResult element.
     * @return true if matching algorithm matches, else false.
     * @exception SQLUnitException if an underlying match operation failed.
     */
    public final boolean applyMatcher(final String source, final String target) 
            throws SQLUnitException {
        LOG.debug(">> applyMatcher(" + source + "," + target + ")");
        // instantiate the Matcher
        IMatcher matcher = null;
        try {
            matcher = (IMatcher) Class.forName(this.matcherClass).newInstance();
        } catch (Exception e) {
            throw new SQLUnitException(IErrorCodes.MATCHER_NOT_FOUND,
                new String[] {this.matcherClass});
        }
        // delegate to the IMatcher.isEqual() method and return
        if (matcher == null) {
            return false;
        } else {
            return matcher.isEqual(source, target, this.args);
        }
    }

    /**
     * Matches a pattern filter against the supplied String s. The pattern
     * filter can be composed of a combination of ranges and comma-separated
     * enumerations. For example, the pattern "m-n,p,q" would mean any 
     * number x which satisfies the following condition: (m &lt;= x &lt;= n
     * or x == p or x == q).
     * @param p the pattern filter to match against.
     * @param s the integer id to match.
     * @return true if the String s matches the pattern p.
     * @exception SQLUnitException if the pattern could not be parsed.
     */
    public static boolean matchPattern(final String p, final int s)
            throws SQLUnitException {
        LOG.debug(">> matchPattern(" + p + "," + s + ")");
        // if pattern is not specified, then any id will match, return true
        if (p == null) { return true; }
        // get rid of whitespace
        String pattern = p.replaceAll(" ", "");
        // if pattern is "*", then any id will match, return true
        if (pattern.equals("*")) { return true; }
        boolean matches = false;
        // Get tokens separated by commas
        StringTokenizer st = new StringTokenizer(pattern, ",", false);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.indexOf("-") > -1) {
                // is a range
                String lb = token.substring(0, token.indexOf("-"));
                String hb = token.substring(token.indexOf("-") + 1);
                try {
                    int ilb = Integer.parseInt(lb);
                    int ihb = Integer.parseInt(hb);
                    matches = matches || (s >= ilb && s <= ihb);
                } catch (NumberFormatException e) {
                    throw new SQLUnitException(
                        IErrorCodes.MATCH_PATTERN_EXCEPTION, new String[] {p});
                }
            } else {
                // is a number
                try {
                    int itoken = Integer.parseInt(token);
                    matches = matches || (s == itoken);
                } catch (NumberFormatException e) {
                    throw new SQLUnitException(
                        IErrorCodes.MATCH_PATTERN_EXCEPTION, new String[] {p});
                }
            }
        }
        return matches;
    }
}
