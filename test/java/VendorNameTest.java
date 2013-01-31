/*
 * $Id: VendorNameTest.java,v 1.3 2004/10/05 20:13:04 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/VendorNameTest.java,v $
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

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Tests for vendor name.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.3 $
 */
public class VendorNameTest extends TestCase {

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
        return new TestSuite(VendorNameTest.class);
    }

    /**
     * Boilerplate constructor.
     * @param name the name of this test class.
     */
    public VendorNameTest(final String name) {
        super(name);
    }

    /**
     * Checking to see what the product name is for MockDatabase.
     * @exception Exception if there was a problem.
     */
    public final void testWhatItLooksLike() throws Exception {
        Class.forName("net.sourceforge.sqlunit.test.mock.SQLUnitMockDriver");
        Connection conn = DriverManager.getConnection(
            "jdbc:mock:net.sourceforge.sqlunit.test.mock.SQLUnitMockDatabase",
            "defaultuser", "defaultuser");
        assertEquals("Bad product name returned", 
            "MockDatabase", conn.getMetaData().getDatabaseProductName());
    }
}
