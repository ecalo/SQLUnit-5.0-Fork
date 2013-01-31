/*
 * $Id: SQLUnit.java,v 1.56 2006/04/30 22:25:56 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/SQLUnit.java,v $
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

import net.sourceforge.sqlunit.handlers.ConnectionHandler;
import net.sourceforge.sqlunit.utils.DigestUtils;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * The SQLUnit class is actually a JUnit test case and is the main program
 * which is called by the SQLUnit user. Instead of executing predefined
 * tests in sequence, the SQLUnit test will parse an XML input file containing
 * the test information and execute tests dynamically.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.56 $
 * @sqlunit.element name="sqlunit"
 *  description="The root tag for an SQLUnit test specification."
 *  syntax="((connection)+, (setup)*, (test | batchtest | diff | echo | func)+, (teardown)*)"
 * @sqlunit.child name="connection"
 *  description="Specifies one or more database Connections that need to be
 *  instantiated for the test to run."
 *  required="Yes, one or more."
 *  ref="connection"
 * @sqlunit.child name="setup"
 *  description="Specifies any setup tasks that need to happen before all
 *  the tests in the suite are run."
 *  required="No"
 *  ref="setup"
 * @sqlunit.child name="test"
 *  description="Specifies a test that needs to run in this suite."
 *  required="Either one or more of test, batchtest, diff, echo or func"
 *  ref="test"
 * @sqlunit.child name="batchtest"
 *  description="Specifies a test that uses the JDBC batching functionality."
 *  required="Either one or more of test, batchtest, diff, echo or func"
 *  ref="batchtest"
 * @sqlunit.child name="diff"
 *  description="Specifies a test that compares the results generated by
 *  two SQL or stored procedure calls"
 *  required="Either one or more of test, batchtest, diff, echo or func"
 *  ref="diff"
 * @sqlunit.child name="echo"
 *  description="Echoes a string after substitution to the log."
 *  required="Either one or more of test, batchtest, diff, echo or func"
 *  ref="echo"
 * @sqlunit.child name="func"
 *  description="Populates a single value returned from a stored procedure
 *  or SQL call into the SymbolTable, identified by function name"
 *  required="Either one or more of test, batchtest, diff, echo or func"
 *  ref="func"
 * @sqlunit.child name="teardown"
 *  description="Specifies any tasks that need to happen after all the tests
 *  in the suite are run"
 *  required="No"
 *  ref="teardown"
 */
public class SQLUnit extends TestCase {

    private static final Logger LOG = Logger.getLogger(SQLUnit.class);

    // passed in from the sqlunit task via the System environment
    private boolean debug = false;
    private boolean haltOnFailure = false;
    private String testFile = null;
    private String errorMessage = null;
    private IReporter reporter = null;

    /**
     * Instantiates a new instance of SQLUnit.
     * @param name the name of the TestCase object.
     */
    public SQLUnit(final String name) {
        super(name);
    }

    /**
     * Returns the TestSuite object to run.
     * @return a Test object.
     */
    public static Test suite() {
        return new TestSuite(SQLUnit.class);
    }

    /**
     * Sets the haltOnFailure attribute from the SQLUnit task.
     * @param haltOnFailure controls the behavior of the test. If true,
     * the test will stop processing the first time a test fails. If
     * false, the test will continue on till all files declared in
     * the sqlunit task is consumed.
     */
    public final void setHaltOnFailure(final boolean haltOnFailure) {
        this.haltOnFailure = haltOnFailure;
    }

    /**
     * Sets the debug attribute from the SQLUnit task.
     * @param debug prints trace information using log4j if set to true.
     */
    public final void setDebug(final boolean debug) {
        Properties log4jProps = new Properties();
        boolean isUsingDefaults = false;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("log4j");
            for (Enumeration e = bundle.getKeys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                String value = bundle.getString(key);
                log4jProps.setProperty(key, value);
            }
        } catch (MissingResourceException e) {
            // resource not found, use defaults
            isUsingDefaults = true;
            log4jProps.setProperty("log4j.rootLogger", "DEBUG, sqlunit");
            log4jProps.setProperty("log4j.appender.sqlunit", 
                "org.apache.log4j.ConsoleAppender");
            log4jProps.setProperty("log4j.appender.sqlunit.layout",
                "org.apache.log4j.PatternLayout");
            log4jProps.setProperty(
                "log4j.appender.sqlunit.layout.ConversionPattern",
                "%5p [%t] (%F:%L) - %m%n");
        }
        PropertyConfigurator.configure(log4jProps);
        Logger logger = Logger.getRootLogger();
        logger.setLevel(debug ? Level.DEBUG : Level.INFO);
        this.debug = debug;
        if (isUsingDefaults) {
            logger.warn("log4j.properties not found (using defaults)");
        }
    }

    /**
     * Sets the IReporter object from the SQLUnit task.
     * @param reporter the IReporter object to log messages to.
     */
    public final void setReporter(final IReporter reporter) {
        this.reporter = reporter;
        SymbolTable.setObject(SymbolTable.REPORTER_KEY, reporter);
    }

    /**
     * Sets the testFile attribute from the SQLUnit task.
     * @param testFile the test file to use for the test.
     */
    public final void setTestFile(final String testFile) {
        this.testFile = testFile;
    }

    /**
     * Allows a client other than the sqlunit build task to set the
     * database connection to use for the test. Useful in case the
     * same file needs to be used to test multiple database instances.
     * @param conn the database connection to use.
     */
    public final void setConnection(final Connection conn) {
        ConnectionRegistry.setConnection(conn);
    }
    
    /**
     * Sets any symbols that were set for SQLUnit within the Ant build.xml
     * file, if SQLUnit is invoked using the SQLUnit Ant task.
     * @param projectProps a Hashtable of Ant properties set in the calling
     * build.xml file.
     */
    public final void setAntSymbols(Hashtable projectProps) {
        for (Enumeration e = projectProps.keys(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            String value = (String) projectProps.get(key);
            SymbolTable.setValue("${ant." + key + "}", value);
        }
    }

    /**
     * Called by Test.run() or Test.runBare() from the SQLUnit task. Runs
     * the SQLUnit test and collects the results in SQLUnitResult.
     * @exception SQLUnitException if one is thrown by the test.
     */
    public final void runTest() throws SQLUnitException {
        try {
            testWrapper();
        } catch (SQLUnitException e) {
            // SQLUnitException stack trace available only in DEBUG mode
            if (debug) { LOG.fatal(e.getMessage(), e); }
            throw e;
        } catch (Exception e) {
            // unhandled exceptions dump full stack trace in either mode
            LOG.fatal(e.getMessage(), e); 
            throw new SQLUnitException(IErrorCodes.GENERIC_ERROR,
                new String[] {"Runtime", e.getClass().getName(), 
                e.getMessage()}, e);
        }
    }

    /**
     * The main testing class which dynamically processes the input XML
     * file. Can throw an exception. Exceptions of class SQLUnitException
     * are treated as application error messages and appear on the STDERR
     * instead of being thrown up in the JVM's stack trace.
     * @exception SQLUnitException if one is thrown by underlying methods.
     */
    public final void testWrapper() throws SQLUnitException {
        LOG.debug(">> testWrapper()");
        SQLUnitResult result = processDoc();
        if (!result.wasSuccessful()) {
            throw new SQLUnitException(IErrorCodes.TEST_FAILURE_EXCEPTION,
                new String[] {result.toString()}, 
                result.getLastException());
        }
    }

    /**
     * Parses the input XML file and processes the directives in sequence
     * and runs the tests requested there. Progress messages appear on the
     * STDERR.
     * @return a SQLUnitResult object.
     * @exception SQLUnitException if there was a problem with the processing.
     */
    private SQLUnitResult processDoc() throws SQLUnitException {
        LOG.debug(">> processDoc()");
        getSystemProperties();
        SQLUnitResult result = new SQLUnitResult();
        TypeMapper mapper = TypeMapper.getTypeMapper();
        result.setTestFile(testFile);
        SAXBuilder builder = new SAXBuilder(true);
        builder.setEntityResolver(new SQLUnitEntityResolver());
        try {
            reporter.newTestFile(basename(testFile), testFile);
            Document doc = builder.build(new FileInputStream(testFile));
            testFile = null;
            Element elRoot = doc.getRootElement();
            // get the connection info (multiple connections allowed)
            if (ConnectionRegistry.getConnection(null) == null) {
                List connectionList = elRoot.getChildren("connection");
                for (int i = 0; i < connectionList.size(); i++) {
                    Element elConnection = (Element) connectionList.get(i);
                    String connectionId = elConnection.getAttributeValue(
                        "connection-id");
                    reporter.settingUpConnection(connectionId);
                    ConnectionHandler connectionHandler = (ConnectionHandler)
                        HandlerFactory.getInstance(elConnection.getName());
                    connectionHandler.process(elConnection);
                    Map config = 
                        connectionHandler.getLastConnectionProperties();
                    config.put("haltOnFailure", haltOnFailure 
                        ? "true" : "false");
                    config.put("debug", debug ? "true" : "false");
                    reporter.setConfig(config);
                }
            }
            // setup the test
            reporter.setUp();
            Element elSetup = elRoot.getChild("setup");
            if (elSetup != null) {
                IHandler setupHandler = 
                    HandlerFactory.getInstance(elSetup.getName());
                setupHandler.process(elSetup);
            }
            // run all the tests, batchtests and diffs in sequence
            List swappableTags = HandlerFactory.getSwappableTags("sqlunit");
            List elTaskList = elRoot.getChildren();
            int testIndex = 0;
            for (int i = 0; i < elTaskList.size(); i++) {
                // if this is not a valid tag for sqlunit, skip it
                Element elTest = (Element) elTaskList.get(i);
                if (!swappableTags.contains(elTest.getName())) {
                    continue;
                }
                testIndex++;
                // if we are told to skip, we should and print a message
                if (shouldSkip(elTest)) {
                    String reason = SymbolTable.getValue(SymbolTable.SKIP_REASON);
                    reporter.skippedTest(elTest.getName(),
                        testIndex, elTest.getAttributeValue("name")
                        + (reason == null ? "" : " (" + reason + ")"));
                    continue;
                }
                if (errorMessage == null || !haltOnFailure) {
                    // accumulate into SQLUnitResult
                    result.addRunCount();
                    // start the test
                    IHandler testHandler = HandlerFactory.getInstance(elTest.getName());
                    Boolean succ = Boolean.FALSE;
                    // print the test report header
                    reporter.runningTest(elTest.getName(), testIndex,
                            elTest.getAttributeValue("name"));
                    try {
                        Object retObj = testHandler.process(elTest);
                        if (retObj instanceof Boolean) {
                            succ = (Boolean) retObj;
                        }
                    } catch (SQLUnitException e) {
                        if (!succ.booleanValue()) {
                            result.addFailureCount();
                        } else {
                            result.addErrorCount();
                        }
                        errorMessage = e.getMessage();
                        result.setLastException(e);
                    }
                    long elapsedTime = 0L;
                    try {
                        elapsedTime = Long.parseLong(SymbolTable.getValue(
                            SymbolTable.TEST_ELAPSED_TIME));
                    } catch (NumberFormatException e) {
                        // :IGNORE: reset to default
                    }
                    reporter.finishedTest(elapsedTime, errorMessage == null);
                    if (errorMessage != null) {
                        reporter.addFailure(result.getLastException(), false);
                    }
                    // clean up temp files, if any
                    Map tempFileMap = DigestUtils.getTempFileMappings();
                    if (tempFileMap.keySet().size() > 0) {
                        Iterator titer = tempFileMap.keySet().iterator();
                        while (titer.hasNext()) {
                            String resultKey = (String) titer.next();
                            String filename = 
                                (String) tempFileMap.get(resultKey);
                            if (filename != null) {
                                if (succ.booleanValue()) {
                                    //test succeeded, delete
                                    File f = new File(filename);
                                    f.delete();
                                } else {
                                    // test failed, log
                                    reporter.tempFile(
                                        testIndex, resultKey, filename);
                                }
                            }
                        }
                    }
                } else {
                    reporter.skippedTest(elTest.getName(), testIndex, 
                        elTest.getAttributeValue("name"));
                }
                errorMessage = null;
            }
            // teardown the test
            reporter.tearDown();
            Element elTeardown = elRoot.getChild("teardown");
            if (elTeardown != null) {
                IHandler teardownHandler = 
                    HandlerFactory.getInstance(elTeardown.getName());
                teardownHandler.process(elTeardown);
            }
            // remove references to user variables in this test
            SymbolTable.removeUserVariables();
            return result;
        } catch (IOException e) {
            reporter.addFailure(e, true);
            errorMessage = e.getMessage();
            throw new SQLUnitException(IErrorCodes.GENERIC_ERROR,
                new String[] {"I/O", e.getClass().getName(), 
                e.getMessage()}, e);
        } catch (JDOMException e) {
            reporter.addFailure(e, true);
            errorMessage = e.getMessage();
            throw new SQLUnitException(IErrorCodes.GENERIC_ERROR,
                new String[] {"XML", e.getClass().getName(),
                e.getMessage()}, e);
        } catch (SQLUnitException e) {
            reporter.addFailure(e, true);
            errorMessage = e.getMessage();
            throw e;
        } catch (Exception e) {
            reporter.addFailure(e, true);
            errorMessage = e.getMessage();
            throw new SQLUnitException(IErrorCodes.GENERIC_ERROR, 
                new String[] {"System", e.getClass().getName(),
                e.getMessage()}, e);
        } finally {
            ConnectionRegistry.releaseConnections();
            reporter.testFileComplete(
                (result.errorCount() + result.failureCount()) > 0
                ? false : true);
        }
    }

    private String basename(String testFile) {
        int fs = testFile.lastIndexOf(System.getProperty("file.separator"));
        int dot = testFile.lastIndexOf(".");
        if (dot == -1) dot = testFile.length();
        return testFile.substring(fs + 1, dot);
    }

    /**
     * Convenience method to check the element for the presence of skip and
     * classifiers tags and to check that the test can be run. The method
     * returns true if the test should be skipped, else it returns false.
     * @param elTest the JDOM Element representing the test entity.
     * @return true or false.
     * @exception Exception if one was thrown.
     */
    private boolean shouldSkip(Element elTest) throws Exception {
        Element elSkip = elTest.getChild("skip");
        // if a skip element exists and is true, then it trumps any
        // classifiers
        boolean isSkippable = false;
        if (elSkip != null) {
            IHandler skipHandler = HandlerFactory.getInstance(elSkip.getName());
            isSkippable =
                ((Boolean) skipHandler.process(elSkip)).booleanValue();
            if (isSkippable) {
                return true;
            }
        }
        // if skip is false or non-existent (false), then check classifiers
        Element elClassifiers = elTest.getChild("classifiers");
        boolean isMatched = false;
        if (elClassifiers != null) {
            IHandler classifiersHandler =
                HandlerFactory.getInstance(elClassifiers.getName());
            isMatched = ((Boolean) classifiersHandler.process(elClassifiers)).
                booleanValue();
            if (!isMatched) {
                return true;
            }
        }
        return false;
    }

    /**
     * Pulls the information out of the sqlunit task tag attributes into local
     * variables.
     * @exception SQLUnitException if an error occured.
     */
    private void getSystemProperties() throws SQLUnitException {
        if (testFile == null) {
            throw new SQLUnitException(IErrorCodes.NO_TESTFILE,
                new String[] {IErrorCodes.USAGE});
        }
        File file = new File(testFile);
        if (!file.exists()) {
            throw new SQLUnitException(IErrorCodes.TESTFILE_NOT_FOUND,
                new String[] {IErrorCodes.USAGE});
        }
        // if debug is on, set haltOnFailure to true
        haltOnFailure = (debug ? true : haltOnFailure);
        return;
    }
}
