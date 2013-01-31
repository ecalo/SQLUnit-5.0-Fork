/*
 * $Id: ReconnectOnFailureTest.java,v 1.6 2004/10/05 20:13:04 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/ReconnectOnFailureTest.java,v $
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

import net.sourceforge.sqlunit.ConnectionRegistry;
import net.sourceforge.sqlunit.HandlerFactory;
import net.sourceforge.sqlunit.TypeMapper;
import net.sourceforge.sqlunit.handlers.ConnectionHandler;
import net.sourceforge.sqlunit.handlers.TestHandler;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

/**
 * Simulates an exception situation in one of the tests and checks to see
 * if the connection is re-established.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.6 $
 */
public class ReconnectOnFailureTest extends TestCase {

    private static final String TESTFILE = "test/mock/reconnect-test.jml";

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
        return new TestSuite(ReconnectOnFailureTest.class);
    }

    /**
     * Boilerplate constructor.
     * @param name the name of this test class.
     */
    public ReconnectOnFailureTest(final String name) {
        super(name);
    }

    /**
     * Simulates an exception situation in one of the tests and checks to see
     * if the connection is re-established. The test file for this JUnit test
     * is test/mock/reconnect-test.jml.
     * @exception Exception if there was a problem executing the test.
     */
    public final void testReconnectOnFail() throws Exception {
        SAXBuilder builder = new SAXBuilder(true);
        Document doc = builder.build(new FileInputStream(TESTFILE));
        Element elSqlUnit = doc.getRootElement();
        TypeMapper mapper = TypeMapper.getTypeMapper();
        // get the connection
        Element elConnection = elSqlUnit.getChild("connection");
        ConnectionHandler connectionHandler = 
            (ConnectionHandler) HandlerFactory.getInstance(
            elConnection.getName());
        Connection conn1 = (Connection) connectionHandler.process(elConnection);
        String conn1Oid = conn1.toString();
        // run test to raise exception and invalidate connection
        List elTests = elSqlUnit.getChildren("test");
        Iterator elTestsIter = elTests.iterator();
        while (elTestsIter.hasNext()) {
            Element elTest = (Element) elTestsIter.next();
            TestHandler handler = (TestHandler) HandlerFactory.getInstance(
                elTest.getName());
            handler.process(elTest);
        }
        // Get the current connection
        Connection conn2 = ConnectionRegistry.getConnection(null);
        String conn2Oid = conn2.toString();
        // verify that they are different
        assertTrue("Conn1 and Conn2 cannot be equal", 
            !(conn1Oid.equals(conn2Oid)));
    }
}
