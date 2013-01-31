/*
 * $Id: IReporter.java,v 1.6 2005/02/24 03:56:15 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/IReporter.java,v $
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

import java.util.Map;

/**
 * The reporter interface that all reporters must implement.
 * @author Rob Nielsen (robn@asert.com.au)
 */
public interface IReporter {

    /**
     * Returns a hardcoded name identifying the reporter implementation.
     * This name is a key in the reporter.properties file.
     * @return a unique hardcoded name for the reporter implementation.
     */
    String getName();

    /**
     * Returns true if the reporter has its own container. If it does,
     * then SQLUnit will not attempt to build a container for this.
     * @return true if the reporter has its own container, else false.
     */
    boolean hasContainer();

    /**
     * Called when a new sqlunit test file is run.
     * @param testName the name of the test being run
     * @param testFile the filename of the test file
     */
    void newTestFile(String testName, String testFile);

    /**
     * Called before a database connection is setup.
     * @param connectionId the id of the connection being attempted, 
     * or null for the default connection
     */
    void settingUpConnection(String connectionId);

    /**
     * Called after the connection is made.  The map contains key/value 
     * pairs of configuration parameters for the connection and debug settings.
     * @param configMap the configuration map
     */
    void setConfig(Map configMap);

    /**
     * Called before the set up section of the test.
     */
    void setUp();

    /**
     * Called before a test is run.
     * @param name the name of the test being run
     * @param testIndex the index of the test being run
     * @param desc a description of the test being run
     */
    void runningTest(String name, int testIndex, String desc);

    /**
     * Called when a test is completed
     * @param elapsed the time in milliseconds the test took to run
     * @param success true if the test succeeded, false otherwise
     */
    void finishedTest(long elapsed, boolean success);

    /**
     * Called when a test is skipped because of an earlier failure.
     * @param name the name of the test being run
     * @param testIndex the index of the test being run
     * @param desc a description of the test being run
     */
    void skippedTest(String name, int testIndex, String desc);

    /**
     * Called when an exception occured during processing.
     * @param th the exception that occured
     * @param error true if the exception is an error, false if it is a failure
     */
    void addFailure(Throwable th, boolean error);

    /**
     * Called before the tear down section is run
     */
    void tearDown();

    /**
     * Called when a test has failed and a temporary file is left containing
     * data.
     * @param testId the index of the test
     * @param result the result of the test
     * @param file the temporary file
     */
    void tempFile(int testId, String result, String file);

    /**
     * Called when the test file has been completed.
     * @param success true if everything completed with no errors, 
     * false otherwise
     */
    void testFileComplete(boolean success);

    /**
     * Makes the reporter emit a predefined value.
     * @param message the message to emit.
     */
    void echo(String message);
}
