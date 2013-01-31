/*
 * $Id: TextReporter.java,v 1.6 2005/02/24 03:56:16 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/reporters/TextReporter.java,v $
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
import net.sourceforge.sqlunit.SQLUnitException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Map;

/**
 * SQLUnit reporter that writes its text to standard output. This is the
 * default reporter for SQLUnit.
 * @author Rob Nielsen (robn@asert.com.au)
 * @version $Revision: 1.6 $
 */
public class TextReporter implements IReporter {

    /** The format to be used in the ant task */
    private static final String NAME = "default";

    private PrintStream logStream = null;

    /**
     * Constructs a new TextReporter.
     * @param outputFile the output file to write to, or null to write 
     * to standard error
     */
    public TextReporter(final String outputFile) {
        if (outputFile != null) {
            try {
                this.logStream = new PrintStream(new FileOutputStream(
                    outputFile), true);
            } catch (FileNotFoundException e) {
                // :IGNORE: output to console
            }
        }
    }

    /**
     * Returns the name of this reporter.
     * @return the name of this reporter.
     */
    public final String getName() {
        return NAME;
    }

    /**
     * Returns false since this reporter runs in SQLUnit's context.
     * @return false.
     */
    public final boolean hasContainer() {
        return false;
    }

    /**
     * Called when a new sqlunit test file is run.
     * @param testName the name of the test being run
     * @param testFile the filename of the test file
     */
    public final void newTestFile(final String testName, 
            final String testFile) {
        log("*** Running SQLUnit file: " + testFile);
    }

    /**
     * Called before a database connection is setup.
     * @param connectionId the id of the connection being attempted, or null 
     * for the default connection
     */
    public final void settingUpConnection(final String connectionId) {
        if (connectionId == null) {
            log("Getting connection(DEFAULT)");
        } else {
            log("Getting connection(" + connectionId + ")");
        }
    }

    /**
     * Called after the connection is made. The map contains key/value pairs 
     * of configuration parameters for the connection and debug settings.
     * @param configMap the configuration map
     */
    public final void setConfig(final Map configMap) {
        // :NOTE: no action here
    }

    /**
     * Called before the set up section of the test.
     */
    public final void setUp() {
        log("Setting up test...");
    }

    /**
     * Called before a test is run.
     * @param name the name of the test being run
     * @param testIndex the index of the test being run
     * @param desc a description of the test being run
     */
    public final void runningTest(final String name, final int testIndex, 
            final String desc) {
        log("Running " + name + "[" + (testIndex) + "]: " + desc, false);
    }

    /**
     * Called when a test is completed
     * @param elapsed the time in milliseconds the test took to run
     * @param success true if the test succeeded, false otherwise
     */
    public final void finishedTest(final long elapsed, final boolean success) {
        log(" (" + (elapsed) + "ms)");
    }

    /**
     * Called when a test is skipped because of an earlier failure.
     * @param name the name of the test being run
     * @param testIndex the index of the test being run
     * @param desc a description of the test being run
     */
    public final void skippedTest(final String name, final int testIndex, 
            final String desc) { 
        log("Skipping " + name + "[" + (testIndex) + "]: " + desc, false);
    }

    /**
     * Called when an exception occured during processing.
     * @param th the exception that occured
     * @param error true if the exception is an error, false if it is a failure
     */
    public final void addFailure(final Throwable th, final boolean error) {
        if (th instanceof SQLUnitException) {
            log(th.getMessage());
        } else {
            log((error ? "Error" : "Failure") + " (" + th.getClass().getName()
                + ") encountered: " + th.getMessage());
        }
    }

    /**
     * Called before the tear down section is run
     */
    public final void tearDown() {
        log("Tearing down test...");
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
        log("For test[" + testId + "], " + result + ", created temp file "
            + file);
    }

    /**
     * Called when the test file has been completed.
     * @param success true if everything completed with no errors, 
     * false otherwise
     */
    public final void testFileComplete(final boolean success) {
    }

    /**
     * Emits a message to the reporter's outputstream.
     * @param message the message to print.
     */
    public final void echo(String message) {
        log(message, false);
    }

    /**
     * Logs the message from a SQLUnit run. If a log PrintStream object
     * is passed in, then it logs to that, else logs to STDERR. The
     * message is always terminated with a newline character.
     * @param message the message to log.
     */
    private void log(final String message) {
        log(message, true);
    }

    /**
     * Logs the message from a SQLUnit run. If a log PrintStream object
     * is passed in, then it logs to that, else it logs to STDERR.
     * @param message the message to log.
     * @param eoln if set to false, no end of line character is supplied.
     */
    private void log(final String message, final boolean eoln) {
        if (logStream == null) {
            if (eoln) { 
                System.err.println(message); 
            } else { 
                System.err.print(message); 
            }
        } else {
            if (eoln) { 
                logStream.println(message); 
            } else { 
                logStream.print(message); 
            }
        }
    }
}
