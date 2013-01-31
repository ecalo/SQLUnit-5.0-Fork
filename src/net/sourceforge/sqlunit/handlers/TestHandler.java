/*
 * $Id: TestHandler.java,v 1.19 2005/06/10 05:52:42 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/TestHandler.java,v $
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
import net.sourceforge.sqlunit.IReporter;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.SymbolTable;
import net.sourceforge.sqlunit.beans.DatabaseResult;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The TestHandler class is responsible for executing a single test specified
 * by the contents of the test tag in the input XML file. A test consists of
 * either a single sql or a call tag followed by a result tag. Thus a test
 * will involve first running the sql or stored procedure and comparing the
 * results with the supplied result. The test will also have to replace any
 * variables specified in the result element with the corresponding value
 * from the symbol table.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.19 $
 * @sqlunit.parent name="sqlunit" ref="sqlunit"
 * @sqlunit.element name="test"
 *  description="The test tag specifies the SQL statement or stored procedure
 *  that needs to be tested, its input parameters, and the expected output.
 *  The tag will be run by SQLUnit to compare the expected results against
 *  the actual result."
 *  syntax="((skip)?, (classifiers)?, (match)*, (prepare)?, ((sql | call | methodinvoker | dynamicsql), 
 *  result)?)"
 * @sqlunit.attrib name="name"
 *  description="Specifies a human-readable name for the test, which will be
 *  printed to the log as part of SQLUnit's progress messages."
 *  required="Yes"
 * @sqlunit.attrib name="assert"
 *  description="Specifies a single or comma-separated list of assertions
 *  that must be true for the test."
 *  required="No, defaults to equal"
 * @sqlunit.attrib name="failure-message"
 *  description="Allows the caller to supply an error message which should be
 *  displayed if the test failed."
 *  required="No, if not specified, no user message will be displayed."
 * @sqlunit.attrib name="java-object-support"
 *  description="If set to on, indicates that Java Object Support is turned
 *  on. Valid values are on and off."
 *  required="No, default is off"
 * @sqlunit.attrib name="expected-duration-millis"
 *  description="If specified, specifies that SQLUnit should time the test
 *  and fail the test if the test does not complete in the window specified
 *  +/- a percentage tolerance if specified, or 10% if not."
 *  required="No"
 * @sqlunit.attrib name="percentage-tolerance"
 *  description="If specified with expected-duration-millis, this specifies
 *  the percentage tolerance that SQLUnit will put on the expected-duration
 *  in order to calculate if the test should fail."
 *  required="No, will default to 10 if expected-duration-millis is specified
 *  and percentage-tolerance is not specified."
 * @sqlunit.child name="skip"
 *  description="Indicates whether the test should be skipped or not."
 *  required="No"
 *  ref="skip"
 * @sqlunit.child name="classifiers"
 *  description="Allows user to provide classification criteria for the test
 *  which SQLUnit can use to decide whether it should run the test or not
 *  based on criteria provided to match the classifier."
 *  required="No"
 *  ref="classifiers"
 * @sqlunit.child name="match"
 *  description="Specifies zero or match elements that should be applied
 *  to match the result returned with that specified."
 *  required="No"
 *  ref="match"
 * @sqlunit.child name="prepare"
 *  description="Specifies SQL setup code that must be run on a per-test basis."
 *  required="No"
 *  ref="prepare"
 * @sqlunit.child name="sql"
 *  description="Specifies the SQL statement that must be run for this test."
 *  required="Either one of sql, call, methodinvoker, dynamicsql or sub"
 *  ref="sql"
 * @sqlunit.child name="call"
 *  description="Specifies a stored procedure that must be run for the test."
 *  required="Either one of sql, call, methodinvoker, dynamicsql or sub"
 *  ref="call"
 * @sqlunit.child name="methodinvoker"
 *  description="Specifies a method that should be invoked for the test."
 *  required="Either one of sql, call, methodinvoker, dynamicsql or sub"
 *  ref="methodinvoker"
 * @sqlunit.child name="dynamicsql"
 *  description="Specifies a method which returns a String of dynamic SQL
 *  code that should be executed for this test."
 *  required="Either one of sql, call, methodinvoker, dynamicsql or sub"
 *  ref="dynamicsql"
 * @sqlunit.child name="sub"
 *  description="Specifies a predefined and partially specified named SQL
 *  or stored procedure call"
 *  required="Either one of sql, call, methodinvoker, dynamicsql or sub
 *  func is required."
 *  ref="sub"
 * @sqlunit.child name="result"
 *  description="Specifies the expected result from the test."
 *  required="Yes"
 *  ref="result"
 * @sqlunit.example name=""
 *  description="
 *  <test name=\"Adding department HR\">{\n}
 *  {\t}<sql>{\n}
 *  {\t}{\t}<stmt>select AddDept(?)</stmt>{\n}
 *  {\t}{\t}<param id=\"1\" type=\"VARCHAR\">Human Resources</param>{\n}
 *  {\t}</sql>{\n}
 *  {\t}<result>{\n}
 *  {\t}{\t}<resultset id=\"1\">{\n}
 *  {\t}{\t}{\t}<row id=\"1\">{\n}
 *  {\t}{\t}{\t}{\t}<col id=\"1\" name=\"adddept\" type=\"INTEGER\">{\n}
 *  {\t}{\t}{\t}{\t}{\t}${deptId_HR}{\n}
 *  {\t}{\t}{\t}{\t}</col>{\n}
 *  {\t}{\t}{\t}</row>{\n}
 *  {\t}{\t}</resultset>{\n}
 *  {\t}</result>{\n}
 *  </test>
 *  "
 */
public class TestHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(TestHandler.class);

    /** Defines a default value for percent-tolerance if not supplied */
    private static final int DEFAULT_PERCENT_TOLERANCE = 10;
    private static final int HUNDRED = 100;
    

    /**
     * Calls either the SqlHandler or CallHandler and compares the result
     * returned with the supplied result in the result tag.
     * @param elTest the JDOM Element representing the test tag.
     * @return a Boolean.TRUE if the test passed, else a Boolean.FALSE.
     * If test was skipped, then it returns null.
     * @exception Exception if there was a problem running the process.
     */
    public final Object process(final Element elTest) throws Exception {
        LOG.debug(">> process(elTest)");
        if (elTest == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"test"});
        }
        // set the start time
        long startTimeMillis = System.currentTimeMillis();
        // pull the attributes
        String hasJavaObjectSupport = XMLUtils.getAttributeValue(
            elTest, "java-object-support");
        SymbolTable.setValue(SymbolTable.JAVA_OBJECT_SUPPORT,
            (("on").equals(XMLUtils.getAttributeValue(
            elTest, "java-object-support")) ? "on" : "off"));
        String failureMessage = XMLUtils.getAttributeValue(
            elTest, "failure-message");
        String name = XMLUtils.getAttributeValue(elTest, "name");
        String expectedDurationMillis = XMLUtils.getAttributeValue(
            elTest, "expected-duration-millis");
        String percentageTolerance = XMLUtils.getAttributeValue(
            elTest, "percentage-tolerance");
        String assertion = XMLUtils.getAttributeValue(elTest, "assert");
        if (assertion == null || assertion.trim().length() == 0) {
            assertion = "equal";
        }
        // Zero or more matcher elements
        List matchPatterns = new ArrayList();
        Iterator miter = elTest.getChildren("match").iterator();
        while (miter.hasNext()) {
            Element elMatch = (Element) miter.next();
            IHandler matchHandler =
                HandlerFactory.getInstance(elMatch.getName());
            matchPatterns.add(matchHandler.process(elMatch));
        }
        if (matchPatterns.size() == 0) { matchPatterns = null; }
        // Per test prepare
        Element elPrepare = elTest.getChild("prepare");
        if (elPrepare != null) {
            IHandler handler = HandlerFactory.getInstance(
                elPrepare.getName());
            handler.process(elPrepare);
        }
        // sql or call or methodinvoker or dynamicsql
        Element elSql = null;
        List swappableTags =
            HandlerFactory.getSwappableTags(elTest.getName());
        for (Iterator it = swappableTags.iterator(); it.hasNext();) {
            String swappableTag = (String) it.next();
            elSql = elTest.getChild(swappableTag);
            if (elSql != null) { break; }
        }
        IHandler handler = HandlerFactory.getInstance(elSql.getName());
        DatabaseResult gotResult = (DatabaseResult) handler.process(elSql);
        // result
        Element elResult = elTest.getChild("result");
        IHandler resultHandler = HandlerFactory.getInstance(elResult.getName());
        DatabaseResult expResult = 
            (DatabaseResult) resultHandler.process(elResult);
        if (expResult.getEchoOnly()) {
            // we want to display it
            echoToReporter(IErrorCodes.LF + "echo: (" + gotResult.toString()
                + ")");
            return Boolean.TRUE;
        } else {
            SymbolTable.setValue(SymbolTable.JAVA_OBJECT_SUPPORT, null);
            try {
                SymbolTable.update(expResult, gotResult);
                Assertions.assertIsTrue(failureMessage, expResult, gotResult,
                    matchPatterns, assertion);
            } catch (SQLUnitException e) {
                throw e;
            }
        }
        long endTimeMillis = System.currentTimeMillis();
        // Check that test finished within expected time
        // the timing test is only triggered off when we have a 
        // success, since we dont want to mask real errors with this
        // one.
        long elapsedTimeMillis = endTimeMillis - startTimeMillis;
        SymbolTable.setValue(SymbolTable.TEST_ELAPSED_TIME,
            (new Long(elapsedTimeMillis)).toString());
        if (isElapsedTimeAcceptable(elapsedTimeMillis,
            expectedDurationMillis, percentageTolerance)) {
        } else {
            throw new SQLUnitException(
                IErrorCodes.ASSERT_UNACCEPTABLE_ELAPSED_TIME,
                new String[] {expectedDurationMillis, percentageTolerance,
                (new Long(elapsedTimeMillis)).toString()});
        }
        return Boolean.TRUE;
    }

    /**
     * Checks to see if the actual elapsed time for the test is within
     * expected limits.
     * @param elapsedMillis actual elapsed time for test in milliseconds.
     * @param expectedMillis specified elapsed time for test in milliseconds.
     * @param percentTolerance tolerance as percentage of the expected time.
     * @return true if the elapsed time is within acceptable limits.
     * @exception Exception if there was a problem checking the time.
     */
    private boolean isElapsedTimeAcceptable(final long elapsedMillis,
            final String expectedMillis, final String percentTolerance) 
            throws Exception {
        if (expectedMillis != null && expectedMillis.trim().length() > 0) {
            long expectedDuration = 0L;
            try {
                expectedDuration = Long.parseLong(expectedMillis);
            } catch (NumberFormatException e) {
                throw new SQLUnitException(IErrorCodes.NOT_A_NUMBER,
                    new String[] {expectedMillis, "expected-duration-millis"});
            }
            int pcTolerance = DEFAULT_PERCENT_TOLERANCE;
            if (percentTolerance != null
                    && percentTolerance.trim().length() > 0) {
                try {
                    pcTolerance = Integer.parseInt(percentTolerance);
                } catch (NumberFormatException e) {
                    throw new SQLUnitException(IErrorCodes.NOT_A_NUMBER,
                       new String[] {percentTolerance, "percentage-tolerance"});
                }
            }
            int range = (int) (expectedDuration * pcTolerance) / HUNDRED;
            return ((elapsedMillis >= expectedDuration - range)
                && (elapsedMillis <= expectedDuration + range));
        } else {
            // no expected-duration-millis provided, do not check
            return true;
        }
    }
    
    /**
     * Prints the message to the configured Reporter object.
     * @param message the message to print.
     */
    private void echoToReporter(String message) {
        IReporter reporter = (IReporter) SymbolTable.getObject(
            SymbolTable.REPORTER_KEY);
        reporter.echo(message);
    }
}
