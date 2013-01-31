/*
 * $Id: ReporterList.java,v 1.2 2006/04/05 02:51:59 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/reporters/ReporterList.java,v $
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.sourceforge.sqlunit.IReporter;

/**
 * A IReporter that contains a list of other IReporters.
 * Used to provide reporting to multiple places.
 * @author Ivan Ivanov
 * @version $Revision: 1.2 $
 */
public class ReporterList implements IReporter {

    /**
     * The name of the reporter.
     */
    public static final String NAME = "ReporterList";

    private List reporters;

    /**
     * Always throws IllegalStateException since it is not real
     * reporter; it is just a holder of other regular reporters.
     * @param outputFile not used
     */
    public ReporterList(String outputFile) {
        throw new IllegalStateException("IReporterList is a container" +
                "for other repporters and cannot be constructed as " +
                "ordinary reporter");
    }

    /**
     * Constructs a new IReporterList.
     * @param initialCapacity the initial capacity of the
     * reporter list.
     */
    public ReporterList(int initialCapacity) {
        reporters = new Vector(initialCapacity);
    }

    /**
     * Returns true if the reporter is run in another context
     * (container). If the method returns <code>false</code>
     * SQLUnit engine will print to the reporter's logfile
     * the output from the Ant process.
     * @return <code>true</code>
     */
    public boolean hasContainer() {
        return true;
    }

    /**
     * Returns the name of the reporter that will be used in
     * Ant task.
     * @return the name of the reporter
     */
    public String getName() {
        return NAME;
    }

    /**
     * Adds a new reporter to the reporter list.
     * @param r the new reporter
     */
    public void add(IReporter r) {
        if (r instanceof ReporterList) {
            throw new IllegalStateException("ReporterList is a container for" +
                    "other reporters and cannot be added");
        }
        reporters.add(r);
    }

    /**
     * Called when a new test file is entered.
     * The delegation is passed to the reporters in the list.
     * @param testName the type of the test
     * @param testFile the location of the rest file
     */
    public void newTestFile(String testName, String testFile) {
        for (Iterator i = reporters.iterator(); i.hasNext(); ) {
            IReporter r = (IReporter)i.next();
            r.newTestFile(testName, testFile);
        }
    }

    /**
     * Called before a new connection to a database is
     * established. The delegation is passed to the reporters
     * in the list.
     * @param elConnection the XML node describing the
     * connection as taken from the test file
     */
    public void settingUpConnection(String connectionId) {
        for (Iterator i = reporters.iterator(); i.hasNext(); ) {
            IReporter r = (IReporter)i.next();
            r.settingUpConnection(connectionId);
        }
    }

    /**
     * Called after the connection to the database is made.
     * The delegation is passed to the reporters in the list.
     * @param configMap contains configuration key-value pairs.
     */
    public void setConfig(Map configMap) {
        for (Iterator i = reporters.iterator(); i.hasNext(); ) {
            IReporter r = (IReporter)i.next();
            r.setConfig(configMap);
        }
    }

    /**
     * Called before the setup section of the test file.
     * The delegation is passed to the reporters in the list.
     */
    public void setUp() {
        for (Iterator i = reporters.iterator(); i.hasNext(); ) {
            IReporter r = (IReporter)i.next();
            r.setUp();
        }
    }

    /**
     * Called after the test is executed.
     * The delegation is passed to the reporters in the list.
     * @param elTest the XML node describing the test
     * as taken from the test file.
     * @param testIndex the index of the test
     */
    public void runningTest(String name, int testIndex, String desc) {
        for (Iterator i = reporters.iterator(); i.hasNext(); ) {
            IReporter r = (IReporter)i.next();
            r.runningTest(name, testIndex, desc);
        }
    }

    /**
     * Called when a test is finished.
     * The delegation is passed to the reporters in the list.
     * @param elTest the XML node describing the test
     * as taken from the test file
     * @param elapsed the time taken for the test to execute
     * @param success <code>true</code> if the test succeed;
     * <code>false</code> otherwise.
     */
    public void finishedTest(long elapsed, boolean success) {
        for (Iterator i = reporters.iterator(); i.hasNext(); ) {
            IReporter r = (IReporter)i.next();
            r.finishedTest(elapsed, success);
        }
    }

    /**
     * Called when the test is skipped.
     * The delegation is passed to the reporters in the list.
     * @param elTest the XML node describing the test
     * as taken from the test file
     * @param testIndex the index of the test
     * @param reason the reason why the test is skipped
     */
    public void skippedTest(String name, int testIndex, String desc) {
        for (Iterator i = reporters.iterator(); i.hasNext(); ) {
            IReporter r = (IReporter)i.next();
            r.skippedTest(name, testIndex, desc);
        }
    }

    /**
     * Called when an exception is raised during test execution.
     * The delegation is passed to the reporters in the list.
     * @param t the exception that is thrown
     * @param isError if <code>true</code> it is a test error
     * (cause not by the very test, but by an external factor);
     * if <code>false</code> it is a test failure (like a failed
     * assertion)
     */
    public void addFailure(Throwable t, boolean isError) {
        for (Iterator i = reporters.iterator(); i.hasNext(); ) {
            IReporter r = (IReporter)i.next();
            r.addFailure(t, isError);
        }
    }

    /**
     * Called before tearDown is executed.
     * The delegation is passed to the reporters in the list.
     */
    public void tearDown() {
        for (Iterator i = reporters.iterator(); i.hasNext(); ) {
            IReporter r = (IReporter)i.next();
            r.tearDown();
        }
    }

    /**
     * Called when a test has failed and a temporary file is left
     * containing data. The delegation is passed to the reporters
     * in the list.
     * @param testId the index of the test
     * @param result the result of the test
     * @param file the temporary file
     */
    public void tempFile(int testId, String result, String file) {
        for (Iterator i = reporters.iterator(); i.hasNext(); ) {
            IReporter r = (IReporter)i.next();
            r.tempFile(testId, result, file);
        }
    }

    /**
     * Called when a test file is completed (when all tests
     * in it are executed(. The delegation is passed to the
     * reporters in the list.
     * @param success if <code>true</code> the test is successful;
     * if <code>false</code> it failed
     */
    public void testFileComplete(boolean success) {
        for (Iterator i = reporters.iterator(); i.hasNext(); ) {
            IReporter r = (IReporter)i.next();
            r.testFileComplete(success);
        }
    }

    /**
     * Prints the given the message to the reporter's
     * output destination. The delegation is passed to the
     * reporters in the list.
     * @param message the message to be printed
     */
    public void echo(String message) {
        for (Iterator i = reporters.iterator(); i.hasNext(); ) {
            IReporter r = (IReporter)i.next();
            r.echo(message);
        }
    }

    /**
     * Returns the number of the reporters.
     * @return the number of the reporters
     */
    public int size() {
        return reporters.size();
    }
    
    /**
     * Returns an iterator over all reporters.
     * @return an iterator.
     */
    public Iterator iterator() {
        return reporters.iterator();
    }
}
