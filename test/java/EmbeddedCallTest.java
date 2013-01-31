/*
 * $Id: EmbeddedCallTest.java,v 1.2 2005/01/21 06:16:01 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/EmbeddedCallTest.java,v $
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
package net.sourceforge.sqlunit.test;

import net.sourceforge.sqlunit.SQLUnit;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.reporters.CanooWebTestReporter;
import net.sourceforge.sqlunit.reporters.TextReporter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Simulates calling SQLUnit embedded in Java code.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.2 $
 */
public class EmbeddedCallTest extends TestCase {

    private static final String TESTFILE = "test/mock/coretests.xml";
    private static final String TEXTOUT = "/tmp/sqlunit-output.txt";
    private static final String XMLOUT = "/tmp/sqlunit-output.xml";

    /**
     * Boilerplate main method.
     * @param argv the arguments (none).
     */
    public static void main(final String[] argv) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Boilerplate suite() method.
     * @return a Test object.
     */
    public static Test suite() {
        return new TestSuite(EmbeddedCallTest.class);
    }

    /**
     * Boilerplate constructor.
     * @param name the name of this test class.
     */
    public EmbeddedCallTest(final String name) {
        super(name);
    }

    /**
     * Simulates calling SQLUnit with a text reporter.
     * @exception Exception if there was a problem with the test.
     */
    public final void testSQLUnitWithTextReporter() throws Exception {
        SQLUnit sqlunit = new SQLUnit("sqlunit");
        sqlunit.setTestFile(TESTFILE);
        sqlunit.setHaltOnFailure(false);
        sqlunit.setDebug(false);
        sqlunit.setReporter(new TextReporter(TEXTOUT));
        // set connection
        sqlunit.setConnection(getMockConnection());
        // run the tests
        try {
            sqlunit.runTest();
        } catch (SQLUnitException e) {
            fail("Caught an exception: " + e.getMessage());
            e.printStackTrace();
        }
        File f = new File(TEXTOUT);
        f.delete();
    }

    /**
     * Simulates calling SQLUnit with CanooWebTestReporter.
     * @exception Exception if there was a problem with the test.
     */
    public final void testSQLUnitWithCanooReporter() throws Exception {
        SQLUnit sqlunit = new SQLUnit("sqlunit");
        sqlunit.setTestFile(TESTFILE);
        sqlunit.setHaltOnFailure(false);
        sqlunit.setDebug(false);
        sqlunit.setReporter(new CanooWebTestReporter(XMLOUT));
        // set connection
        sqlunit.setConnection(getMockConnection());
        // run the tests
        try {
            sqlunit.runTest();
        } catch (SQLUnitException e) {
            fail("Caught an exception: " + e.getMessage());
        }
        File f = new File(XMLOUT);
        f.delete();
    }

    /**
     * Acquires a mock connection.
     * @return a mock connection.
     * @exception Exception if there was a problem.
     */
    private Connection getMockConnection() throws Exception {
        Class.forName("net.sourceforge.sqlunit.test.mock.SQLUnitMockDriver");
        Connection conn = DriverManager.getConnection(
            "jdbc:mock:net.sourceforge.sqlunit.test.mock.SQLUnitMockDatabase",
            "defaultuser", "defaultuser");
        return conn;
    }
}
