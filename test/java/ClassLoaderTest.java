/*
 * $Id: ClassLoaderTest.java,v 1.4 2006/04/30 22:25:56 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/ClassLoaderTest.java,v $
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

import net.sourceforge.sqlunit.SQLUnitDriverManager;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;

/**
 * Testing loading with custom classloader and custom DriverManager.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.4 $
 */
public class ClassLoaderTest extends TestCase {
    private static final String FILE_LIB_MOCKRUNNER_JAR = "file:lib/mockrunner.jar";
    private static final String FILE_LIB_LOG4J_JAR = "file:lib/log4j-1.2.13.jar";
    private static final String FILE_LIB_COMMONS_LANG_JAR = "file:lib/commons-lang-2.1.jar";

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
        return new TestSuite(ClassLoaderTest.class);
    }

    /**
     * Boilerplate constructor.
     * @param name the name of this test class.
     */
    public ClassLoaderTest(final String name) {
        super(name);
    }

    /**
     * Provides a use case for loading a Class from a URLClassLoader.
     * @exception Exception if the test failed.
     */
    public final void testClassLoadingOverFileUrl() throws Exception {
        URL[] urls = new URL[] {
            new URL("file:build/"),
            new URL(FILE_LIB_MOCKRUNNER_JAR),
            new URL(FILE_LIB_LOG4J_JAR),
            new URL(FILE_LIB_COMMONS_LANG_JAR)
        };
        URLClassLoader ucl = new URLClassLoader(urls);
        Class driverClass = Class.forName(
            "net.sourceforge.sqlunit.test.mock.SQLUnitMockDriver", true, ucl);
        assertEquals("Failed class loading", 
            "net.sourceforge.sqlunit.test.mock.SQLUnitMockDriver",
            driverClass.getName());
    }

    /**
     * Provides a use case for loading the driver from a JAR file which 
     * is not in the classpath and whose location is specified.
     * @exception Exception if the test failed.
     */
    public final void testGetConnectionWithSQLUnitDriverManager()
            throws Exception {
        StringBuffer urlbuf = new StringBuffer();
        urlbuf.append("file:build/").append(",").
            append(FILE_LIB_MOCKRUNNER_JAR).append(",").
            append(FILE_LIB_LOG4J_JAR).append(",").
            append(FILE_LIB_COMMONS_LANG_JAR);
        Connection conn = SQLUnitDriverManager.getConnection(
            "jdbc:mock:net.sourceforge.sqlunit.test.mock.SQLUnitMockDatabase",
            "defaultuser", "defaultuser", 
            "net.sourceforge.sqlunit.test.mock.SQLUnitMockDriver", 
            urlbuf.toString());
        assertNotNull("Could not get connection", conn);
        assertEquals("Connection object corrupted", "MockDatabase",
            conn.getMetaData().getDatabaseProductName());
    }
}
