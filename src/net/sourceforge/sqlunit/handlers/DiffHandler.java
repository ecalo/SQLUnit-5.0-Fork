/*
 * $Id: DiffHandler.java,v 1.9 2005/06/10 05:52:42 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/DiffHandler.java,v $
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

import net.sourceforge.sqlunit.Assertions;
import net.sourceforge.sqlunit.HandlerFactory;
import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.SymbolTable;
import net.sourceforge.sqlunit.ThreadHandlerAdapter;
import net.sourceforge.sqlunit.beans.DatabaseResult;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * The DiffHandler compares result sets from two different SQL statements
 * or stored procedure calls. The caller does not need to know or specify
 * the exact results returned from each SQL or stored procedure call.
 * This can be used when comparing the output of two queries which are
 * too large or unknown at test specification time.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.9 $
 * @sqlunit.parent name="test" ref="test"
 * @sqlunit.element name="diff"
 *  description="The diff element is used to compare the results of two
 *  SQL queries or stored procedure calls. This scenario can be used to 
 *  the results of a database load or when the results are either too long
 *  or unknown at the time the test specification is being written. The
 *  two queries/calls can either use the same Connection or use their
 *  own dedicated Connection to the same or different databases. The
 *  result tag is not specified in case of diff. SQLUnit will internally
 *  generate the two Result objects and match them. The default matching
 *  is exact equality, but can be overriden by supplying one or more
 *  match elements in the diff element. More information on Matching can
 *  be found in the User-Defined Matching section"
 *  syntax="((skip)?, (classifiers)?, (match)*, (prepare)?, (sql|call), (sql|call))"
 * @sqlunit.attrib name="name"
 *  description="The name of the test used by SQLUnit to report progress
 *  messages in its log output."
 *  required="Yes"
 * @sqlunit.attrib name="assert"
 *  description="Specifies a single assertion or a comma-separated list of
 *  assertions which must be true for this diff."
 *  required="No, defaults to equal if not specified"
 * @sqlunit.attrib name="failure-message"
 *  description="If specified, SQLUnit will print the user-supplied failure
 *  message if the test failed."
 *  required="No"
 * @sqlunit.attrib name="java-object-support"
 *  description="If specified, Java Object Support is enabled for the test"
 *  required="No, default is false"
 * @sqlunit.attrib name="multi-threaded"
 *  description="If set to true, runs the calls in parallel to save time"
 *  required="No, default is false (serial execution)"
 * @sqlunit.child name="skip"
 *  description="Indicates whether the diff should be skipped or not."
 *  required="No"
 *  ref="skip"
 * @sqlunit.child name="classifiers"
 *  description="Allows user to provide classification criteria for the diff
 *  which SQLUnit can use to decide whether it should run the diff or not
 *  based on criteria provided to match the classifier."
 *  required="No"
 *  ref="classifiers"
 * @sqlunit.child name="match"
 *  description="Provides overrides for the matching strategy on a per
 *  column basis"
 *  required="No"
 *  ref="match"
 * @sqlunit.child name="prepare"
 *  description="Allows per test setup calls"
 *  required="No. Zero or one prepares can be specified."
 *  ref="prepare"
 * @sqlunit.child name="sql"
 *  description="Specifies the SQL statement to execute."
 *  required="Either one of sql or call needs to be specified. Two and only
 *  two needs to be specified"
 *  ref="sql"
 * @sqlunit.child name="call"
 *  description="Specifies the stored procedure to execute."
 *  required="Either one of sql or call needs to be specified. Two and only
 *  two needs to be specified."
 *  ref="call"
 * @sqlunit.example name="Diff call with multiple match elements"
 *  description="
 *  <diff name=\"Diffing different resultset/multiple matchers\"{\n}
 *  {\t}{\t}failure-message=\"Diff test #3 failed\">{\n}
 *  {\t}<match col-id=\"1\" {\n}
 *  {\t}{\t}matcher=\"net.sourceforge.sqlunitmatchers.AllOrNothingMatcher\">{\n}
 *  {\t}{\t}<arg name=\"match\" value=\"true\" />{\n}
 *  {\t}</match>{\n}
 *  {\t}<match col-id=\"2\"{\n}
 *  {\t}{\t}matcher=\"net.sourceforge.sqlunitmatchers.RangeMatcher\">{\n}
 *  {\t}{\t}<arg name=\"tolerance\" value=\"50\" />{\n}
 *  {\t}</match>{\n}
 *  {\t}<match col-id=\"3\" {\n}
 *  {\t}{\t}matcher=\"net.sourceforge.sqlunitmatchers.PercentageRangeMatcher\">{\n}
 *  {\t}{\t}<arg name=\"pc-tolerance\" value=\"10\" />{\n}
 *  {\t}</match>{\n}
 *  {\t}<sql connection-id=\"1\">{\n}
 *  {\t}{\t}<stmt>select widget_name, price_per_unit, number_sold from widgets where widget_id=?</stmt>{\n}
 *  {\t}{\t}<param id=\"1\" type=\"INTEGER\">1</param>{\n}
 *  {\t}</sql>{\n}
 *  {\t}<sql connection-id=\"2\">{\n}
 *  {\t}{\t}<stmt>select widget_name, price_per_unit, number_sold from widgets where widget_id=?</stmt>{\n}
 *  {\t}{\t}<param id=\"1\" type=\"INTEGER\">2</param>{\n}
 *  {\t}</sql>{\n}
 *  </diff>
 *  "
 */
public class DiffHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(DiffHandler.class);

    /**
     * Collects the stored procedures and SQL statements specified and runs
     * them in a batch, matching with the specified results with the same
     * id attribute. If a failure occurs, the entire batch is rolled back.
     * This behavior of this handler is largely identical to the behavior
     * of the Test handler.
     * @param elDiff the JDOM Element representing the batchtest tag.
     * @return Boolean.TRUE if the diff succeeded, else Boolean.FALSE.
     * @exception Exception if there was a problem running the process.
     */
    public final Object process(final Element elDiff) throws Exception {
        LOG.debug(">> process(elDiff)");
        if (elDiff == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"batchtest"});
        }
        // pull out the attributes
        SymbolTable.setValue(SymbolTable.JAVA_OBJECT_SUPPORT,
            (("on").equals(XMLUtils.getAttributeValue(
            elDiff, "java-object-support")) ? "on" : "off"));
        String failureMessage = XMLUtils.getAttributeValue(
            elDiff, "failure-message");
        String multiThreaded = XMLUtils.getAttributeValue(
            elDiff, "multi-threaded");
        boolean isMultiThreaded = false;
        if (multiThreaded != null && multiThreaded.equalsIgnoreCase("true")) {
            isMultiThreaded = true;
        }
        String assertion = XMLUtils.getAttributeValue(elDiff, "assert");
        if (assertion == null || assertion.trim().length() == 0) {
            assertion = "equal";
        }
        // pull out the elements in a List. The only way to identify the
        // source SQL is to assume that the first one is the source and the
        // second one is the target. Pull out matchers as well and store 
        // in an argument Map to be passed in later.
        List children = elDiff.getChildren();
        int numChildren = children.size();
        boolean foundSource = false;
        List matchPatternObjects = new ArrayList();
        DatabaseResult srcR = null;
        DatabaseResult trgR = null;
        ThreadHandlerAdapter sta = null;
        ThreadHandlerAdapter tta = null;
        for (int i = 0; i < numChildren; i++) {
            Element elChild = (Element) children.get(i);
            if (elChild.getName().equals("match")) {
                // process the match tag
                IHandler handler = 
                    HandlerFactory.getInstance(elChild.getName());
                matchPatternObjects.add(handler.process(elChild));
            } else if (elChild.getName().equals("prepare")) {
                // process the prepare tag
                IHandler handler =
                    HandlerFactory.getInstance(elChild.getName());
                handler.process(elChild);
            } else if (elChild.getName().equals("skip")) {
                // we already considered this, ignore
                continue;
            } else if (elChild.getName().equals("classifiers")) {
                // we already considered this, ignore
                continue;
            } else {
                if (isMultiThreaded) {
                    if (!foundSource) {
                        sta = new ThreadHandlerAdapter(elChild);
                        sta.start();
                        foundSource = true;
                    } else {
                        tta = new ThreadHandlerAdapter(elChild);
                        tta.start();
                    }
                } else {
                    IHandler handler = 
                        HandlerFactory.getInstance(elChild.getName());
                    if (!foundSource) {
                        srcR = (DatabaseResult) handler.process(elChild);
                        foundSource = true;
                    } else {
                        trgR = (DatabaseResult) handler.process(elChild);
                    }
                }
            }
        }
        // if diff is multi-threaded, wait for the threads to complete
        if (isMultiThreaded) {
            sta.join();
            tta.join();
            srcR = (DatabaseResult) sta.getProcessingResult();
            trgR = (DatabaseResult) tta.getProcessingResult();
        }
        // reset to null if no match elements found
        if (matchPatternObjects.size() == 0) {
            matchPatternObjects = null;
        }
        Assertions.assertIsTrue(failureMessage, srcR, trgR, 
            matchPatternObjects, assertion);
        return Boolean.TRUE;
    }
}
