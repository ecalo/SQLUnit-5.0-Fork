/*
 * $Id: MatchHandler.java,v 1.6 2004/09/28 19:15:45 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/MatchHandler.java,v $
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
package net.sourceforge.sqlunit.handlers;

import net.sourceforge.sqlunit.HandlerFactory;
import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.MatchPattern;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.beans.Arg;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The ResultHandler class parses the XML representing the result object.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.6 $
 * @sqlunit.parent name="diff" ref="diff"
 * @sqlunit.element name="match"
 *  description="The match tag specifies a user-defined matching strategy
 *  that will be applied instead of the default strategy to check for
 *  exact equality between two column values"
 *  syntax="((arg)*)"
 * @sqlunit.attrib name="resultset-id"
 *  description="If specified, defines the resultset(s) on which the user-
 *  defined matching criteria should be applied. Wildcards may be applied
 *  to specify multiple values using a '*', eg. '3*' or '*'. Ranges may 
 *  be specified using an expression such as '1-2', or enumerations using 
 *  an expression of the form '1,2,3'."
 *  required="No, defaults to all resultsets if not specified"
 * @sqlunit.attrib name="row-id"
 *  description="If specified, defines the row(s) on which the user-defined
 *  matching strategy will be applied. Same wildcarding rules as defined
 *  in resultset-id apply."
 *  required="No, defaults to all if not specified"
 * @sqlunit.attrib name="col-id"
 *  description="If specified, defines the column(s) on which the user-defined
 *  matching strategy should be applied. Same wildcarding rules as defined
 *  in resultset-id apply."
 *  required="No, defaults to all if not specified"
 * @sqlunit.attrib name="matcher"
 *  description="The full class name of the Matcher class that will be used
 *  for the user-defined matching. The Matcher class must implement the
 *  IMatcher interface and be available in the caller's CLASSPATH"
 *  required="Yes"
 * @sqlunit.child name="arg"
 *  description="Specifies the name and value of the parameters that are
 *  needed by the Matcher class, in addition to the actual column values,
 *  to compute the match."
 *  required="Zero or more arg elements may need to be specified"
 *  ref="arg"
 * @sqlunit.example name="Specifying a perccentage matcher with 20% tolerance"
 *  description="
 *  <match col-id=\"1-2\"{\n}
 *  {\t}matcher=\"net.sourceforge.sqlunitmatchers.PercentageRangeMatcher\">{\n}
 *  {\t}<arg name=\"pc-tolerance\" value=\"20\" />{\n}
 *  </match>
 *  "
 */
public class MatchHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(MatchHandler.class);

    /**
     * Processes a JDOM Element representing a MatchPattern object and
     * returns a MatchPattern object.
     * @param elMatch the JDOM Element to use.
     * @return a populated MatchPattern object.
     * @exception Exception if there was a problem creating the MatchPattern.
     */
    public final Object process(final Element elMatch) throws Exception {
        LOG.debug(">> process(elMatch)");
        if (elMatch == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL, 
                new String[] {"match"});
        }
        // get the attributes
        String resultSetIdFilter = XMLUtils.getAttributeValue(
            elMatch, "resultset-id");
        String rowIdFilter = XMLUtils.getAttributeValue(elMatch, "row-id");
        String colIdFilter = XMLUtils.getAttributeValue(elMatch, "col-id");
        String matcherClass = XMLUtils.getAttributeValue(elMatch, "matcher");
        // get embedded args
        List elArgList = elMatch.getChildren("arg");
        Map argMap = new HashMap();
        Arg[] args = new Arg[elArgList.size()];
        for (int i = 0; i < args.length; i++) {
            Element elArg = (Element) elArgList.get(i);
            IHandler argHandler = HandlerFactory.getInstance(elArg.getName());
            args[i] = (Arg) argHandler.process(elArg);
            argMap.put(args[i].getName(), args[i].getValue());
        }
        // build a MatchPattern object and return it
        return new MatchPattern(
            resultSetIdFilter, rowIdFilter, colIdFilter, matcherClass, argMap);
    }
}
