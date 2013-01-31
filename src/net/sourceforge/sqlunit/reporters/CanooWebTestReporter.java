/*
 * $Id: CanooWebTestReporter.java,v 1.9 2006/04/30 22:25:56 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/reporters/CanooWebTestReporter.java,v $
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
package net.sourceforge.sqlunit.reporters;

import net.sourceforge.sqlunit.IReporter;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * SQLUnit Reporter that works with the Canoo Web Test framework. Generates
 * XML that can be converted to output for the Canoo Web Test console.
 * @author Rob Nielsen (robn@asert.com.au)
 * @author Paul King (paulk@asert.com.au)
 * @version $Revision: 1.9 $
 */
public class CanooWebTestReporter implements IReporter {

    /** the format name to be used in the ant task */
    public static final String FORMAT_NAME = "canoo";

    // Constants used in the XML file
    private static final String ROOT_TAG = "summary";

    private static final String TEST_TAG = "testresult";
    private static final String TEST_END_ATT = "endtime";
    private static final String TEST_START_ATT = "starttime";
    private static final String TEST_FILE_ATT = "location";
    private static final String TEST_SUCCESS_ATT = "successful";
    private static final String TEST_NAME_ATT = "testspecname";

    private static final String RESULTS_TAG = "results";
    private static final String CONFIG_TAG = "config";

    private static final String ECHO_TAG = "echo";
    private static final String ECHO_TEXT_ATT = "text";

    private static final String PARAM_TAG = "parameter";
    private static final String PARAM_NAME_ATT = "name";
    private static final String PARAM_VALUE_ATT = "value";

    private static final String STEP_TAG = "step";
    private static final String DESCRIPTION_TAG = "description";
    private static final String TASKNAME_TAG = "taskName";
    private static final String FAILURE_TAG = "failure";
    private static final String ERROR_TAG = "error";
    private static final String EXCEPTION_ATT = "exception";
    private static final String MESSAGE_ATT = "message";
    private static final String STEP_RESULT_TAG = "result";
    private static final String STEP_COMPLETED_TAG = "completed";
    private static final String STEP_COMPLETED_DURATION_ATT = "duration";
    private static final String STEP_FAILED_TAG = "failed";
    private static final String STEP_NOTEXECUTED_TAG = "notexecuted";

    private static final int DEFAULT_LINE_LENGTH = 50;
    private Element currentStep;
    private Element currentTestResults;
    private Element currentTest;
    private Document doc;
    private String outputFile;

    /**
     * Constructs a new CanooWebTestReporter
     * @param outputFile the file
     * @throws Exception if a problem occurs
     */
    public CanooWebTestReporter(final String outputFile) throws Exception {
        this.outputFile = outputFile;
        File f = new File(outputFile);
        DocumentBuilder builder = 
            DocumentBuilderFactory.newInstance().newDocumentBuilder();
        if (f.exists()) {
            doc = builder.parse(new File(outputFile));
        } else {
            doc = builder.newDocument();
        }
    }

    /**
     * Returns the format name.
     * @return the format name.
     */
    public final String getName() {
        return FORMAT_NAME;
    }

    /**
     * Returns true since this reporter runs in the context of a Canoo
     * Web Test.
     * @return true.
     */
    public final boolean hasContainer() {
        return true;
    }

    /**
     * Called when a new sqlunit test file is run.
     * @param name the name of the test being run
     * @param location the filename of the test file
     */
    public final void newTestFile(final String name, final String location) {
        Element root = null;
        NodeList nodeList = doc.getElementsByTagName(ROOT_TAG);
        if (nodeList.getLength() > 0) {
            root = (Element) nodeList.item(0);
        } else {
            if (doc.getChildNodes().getLength() > 0) {
                throw new IllegalStateException(
                    "Given XML file has an incorrect root element. Expecting "
                    + ROOT_TAG);
            } else {
                root = addElement(doc, ROOT_TAG);
            }
        }
        currentTest = addElement(root, TEST_TAG);
        currentTest.setAttribute(TEST_START_ATT, new Date().toString());
        currentTest.setAttribute(TEST_FILE_ATT, location);
        currentTest.setAttribute(TEST_NAME_ATT, name);
        currentTestResults = addElement(currentTest, RESULTS_TAG);
    }

    /**
     * Called before a database connection is setup.
     * @param connectionId the id of the connection being attempted, 
     * or null for the default connection
     */
    public final void settingUpConnection(final String connectionId) {
        // :NOTE: no action
    }

    /**
     * Called after the connection is made. The map contains key/value 
     * pairs of configuration parameters for the connection and debug settings.
     * @param configMap the configuration map
     */
    public final void setConfig(final Map configMap) {
        Element config = addElement(currentTest, CONFIG_TAG);
        for (Iterator it = configMap.keySet().iterator(); it.hasNext();) {
            String n = (String) it.next();
            String v = (String) configMap.get(n);
            addParameter(config, n, v);
        }
    }

    /**
     * Called before the set up section of the test.
     */
    public final void setUp() {
        // :NOTE: no action
    }

    /**
     * Called before a test is run.
     * @param name the name of the test being run
     * @param testIndex the index of the test being run
     * @param desc a description of the test being run
     */
    public final void runningTest(final String name, final int testIndex, 
            final String desc) {
        currentStep = addElement(currentTestResults, STEP_TAG);
        addParameter(currentStep, DESCRIPTION_TAG, desc);
        addParameter(currentStep, TASKNAME_TAG, "sqlunit:" + name);
    }

    /**
     * Called when a test is completed
     * @param time the time in milliseconds the test took to run
     * @param success true if the test succeeded, false otherwise
     */
    public final void finishedTest(final long time, final boolean success) {
        // we may return a null for DDL code such as DROP and CREATE depending
        // on how the JDBC driver handles it, so if we see this here, then 
        // we dont show it. This is not an ideal situation, we really should
        // manufacture an empty DOM Node implementation with the text value
        // of NULL.
        if (currentStep == null) { return; }
        Element result = addElement(currentStep, STEP_RESULT_TAG);
        if (success) {
            Element completed = addElement(result, STEP_COMPLETED_TAG);
            completed.setAttribute(STEP_COMPLETED_DURATION_ATT, "" + time);
        } else {
            addElement(result, STEP_FAILED_TAG);
        }
    }

    /**
     * Called when a test is skipped because of an earlier failure.
     * @param name the name of the test being run
     * @param testIndex the index of the test being run
     * @param desc a description of the test being run
     */
    public final void skippedTest(final String name, final int testIndex,
            final String desc) {
        runningTest(name, testIndex, desc);
        Element result = addElement(currentStep, STEP_RESULT_TAG);
        addElement(result, STEP_NOTEXECUTED_TAG);
    }

    /**
     * Called when an exception occured during processing.
     * @param th the exception that occured
     * @param isError true if the exception is an error, else false.
     */
    public final void addFailure(final Throwable th, final boolean isError) {
        if (isError) {
            Element error = addElement(currentTestResults, ERROR_TAG);
            error.setAttribute(EXCEPTION_ATT, th.getClass().getName());
            error.setAttribute(MESSAGE_ATT, th.getMessage());
            StringWriter s = new StringWriter();
            th.printStackTrace(new PrintWriter(s));
            error.appendChild(doc.createCDATASection(s.toString()));
        } else {
            Element failure = addElement(currentTestResults, FAILURE_TAG);
            failure.setAttribute(MESSAGE_ATT, th.toString());
        }
    }

    /**
     * Called when a test has failed and a temporary file is left containing
     * data.
     * @param testId the index of the test
     * @param result the result of the test
     * @param file the temporary file
     */
    public final void tempFile(final int testId, final String result, 
            final String file) { 
        // :NOTE: no action
    }

    /**
     * Called before the tear down section is run
     */
    public final void tearDown() {
        // :NOTE: no action
    }

    /**
     * Called when the test file has been completed.
     * @param success true if everything completed with no errors, 
     * false otherwise
     */
    public final void testFileComplete(final boolean success) {
        currentTest.setAttribute(TEST_END_ATT, new Date().toString());
        currentTest.setAttribute(TEST_SUCCESS_ATT, success ? "yes" : "no");
        writeXML();
    }

    /**
     * Called from within the test from the SQLUnit Echo tag to print
     * out values from within the test.
     * @param message the message to echo.
     */
    public final void echo(String message) {
        Element echo = addElement(currentTestResults, ECHO_TAG);
        echo.setAttribute(ECHO_TEXT_ATT, message);
    }

    /**
     * Writes out the new xml data to the file
     */
    private void writeXML() {
        try {
            OutputFormat format = new OutputFormat(doc);
            format.setIndenting(true);
            format.setEncoding("ISO-8859-1");
            format.setLineWidth(DEFAULT_LINE_LENGTH);
            BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(outputFile)));
            XMLSerializer serializer = new XMLSerializer(writer, format);
            serializer.asDOMSerializer();
            serializer.serialize(doc.getDocumentElement());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an element to the tree
     * @param parent the parent node
     * @param name the node to add
     * @return the Element added to the tree.
     */
    private Element addElement(final Node parent, final String name) {
        Element ret = doc.createElement(name);
        parent.appendChild(ret);
        return ret;
    }

    /**
     * Adds a parameter to the tree
     * @param parent the parent node
     * @param name the parameter name
     * @param value the parameter value
     * @return the parameter element added to the tree.
     */
    private Element addParameter(final Node parent, final String name, 
            final String value) {
        Element param = addElement(parent, PARAM_TAG);
        param.setAttribute(PARAM_NAME_ATT, name);
        param.setAttribute(PARAM_VALUE_ATT, value);
        return param;
    }
}
