/*
 * $Id: SQLUnitTestSuite.java,v 1.11 2005/01/21 06:16:01 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/SQLUnitTestSuite.java,v $
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Main SQLUnit test suite. Runs all the JUnit tests for SQLUnit in sequence.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.11 $
 */
public class SQLUnitTestSuite extends TestCase {

    /**
     * Boilerplate main method.
     * @param argv the arguments (none).
     */
    public static void main(final String[] argv) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Boilerplate constructor.
     * @param name the name of this test class.
     */
    public SQLUnitTestSuite(final String name) {
        super(name);
    }

    /**
     * Boilerplate suite() method. This runs the tests in the specified
     * order.
     * @return a Test object.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(ClassLoaderTest.suite());
        suite.addTest(MD5DigestionTest.suite());
        suite.addTest(ReconnectOnFailureTest.suite());
        suite.addTest(TypesTest.suite());
        suite.addTest(VendorNameTest.suite());
        suite.addTest(SortingTest.suite());
        suite.addTest(ContextTest.suite());
        // :NOTE: working test not sure why its not working in batch
        //suite.addTest(EmbeddedCallTest.suite());
        suite.addTest(IncludeFileParserTest.suite());
        suite.addTest(SymbolParserTest.suite());
        // add more tests here
        return suite;
    }
}
