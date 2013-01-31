/*
 * $Id: SQLUnitResult.java,v 1.6 2005/06/08 04:53:48 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/SQLUnitResult.java,v $
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

import junit.framework.TestResult;

/**
 * SQLUnitResult is a simple container which contains the cumulative
 * results of a SQLUnit test run.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.6 $
 */
public class SQLUnitResult extends TestResult {

    private static final Logger LOG = Logger.getLogger(SQLUnitResult.class);

    private int runs = 0;
    private int failures = 0;
    private int errors = 0;
    private Exception lastException = null;
    private String testFile = null;

    /**
     * Adds to the number of accumulated test failures.
     */
    public final void addFailureCount() { 
        LOG.debug(">> addFailureCount()");
        this.failures++; 
    }

    /**
     * Adds to the number of accumulated test errors.
     */
    public final void addErrorCount() {
        LOG.debug(">> addErrorCount()");
        this.errors++; 
    }

    /**
     * Adds to the number of accumulated tests performed.
     */
    public final void addRunCount() { 
        LOG.debug(">> addRunCount()");
        this.runs++; 
    }

    /**
     * Subtracts the number of accumulated tests performed. This is done
     * when a test is skipped.
     */
    public final void subtractRunCount() {
        LOG.debug(">> subtractRunCount()");
        this.runs--;
    }
    
    /**
     * Returns the number of tests run so far.
     * @return the number of tests run so far.
     */
    public final int runCount() { 
        LOG.debug(">> runCount()");
        return this.runs; 
    }

    /**
     * Returns the number of tests failed so far.
     * @return the number of tests failed so far.
     */
    public final int failureCount() {
        LOG.debug(">> failureCount()");
        return this.failures; 
    }

    /**
     * Returns the number of tests errored so far.
     * @return the number of tests errored so far.
     */
    public final int errorCount() { 
        LOG.debug(">> errorCount()");
        return this.errors; 
    }

    /**
     * Returns the last exception encountered in the test.
     * @return an Exception object representing the last exception encountered.
     */
    public final Exception getLastException() { 
        LOG.debug(">> getLastException()");
        return this.lastException; 
    }

    /**
     * Sets the last exception encountered in the test.
     * @param e an Exception object.
     */
    public final void setLastException(final Exception e) { 
        LOG.debug(">> setLastException(e)");
        this.lastException = e; 
    }

    /**
     * Returns the test file name for the error report.
     * @return the test file name for the error report.
     */
    public final String getTestFile() {
        LOG.debug(">> getTestFile()");
        return this.testFile;
    }

    /**
     * Sets the test file for the report.
     * @param testFile the test file to report.
     */
    public final void setTestFile(final String testFile) {
        LOG.debug(">> setTestFile(" + testFile + ")");
        this.testFile = testFile;
    }

    /**
     * Returns true if the test was successful, false otherwise.
     * @return true if the test was successful, false otherwise.
     */
    public final boolean wasSuccessful() {
        LOG.debug(">> wasSuccessful()");
        return (errorCount() == 0 && failureCount() == 0);
    }

    /**
     * Returns a String representation of this object.
     * @return a String representation.
     */
    public final String toString() {
        LOG.debug(">> toString()");
        return "In file: " + testFile + ", tests: " + runs + ", failures: " 
            + failures + ", errors = " + errors;
    }
}
