/*
 * $Id: ContextTest.java,v 1.2 2004/10/05 20:13:04 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/ContextTest.java,v $
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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * Test to check generation of MockInitialContext by MockInitialContextFactory
 * which gets a MockDataSource hard-bound into it, and which is then looked
 * up using context.lookup(). The code in the test mimics the way a client
 * would initialize a InitialContext from a JNDI name server, and retrieve
 * a DataSource object by name. This is to mock tests for the JNDI way of
 * getting a Connection for SQLUnit.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.2 $
 */
public class ContextTest extends TestCase {

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
        return new TestSuite(ContextTest.class);
    }

    /**
     * Boilerplate constructor.
     * @param name the name of this test class.
     */
    public ContextTest(final String name) {
        super(name);
    }

    /**
     * Test to see if we get a valid Connection using a mock JNDI lookup.
     * @exception Exception if the test failed.
     */
    public final void testMockJndiLookup() throws Exception {
        Hashtable env = new Hashtable();
        env.put("java.naming.factory.initial",
            "net.sourceforge.sqlunit.test.mock.MockInitialContextFactory");
        Context ctx = new InitialContext(env);
        DataSource ds = (DataSource) ctx.lookup("jdbc/mockDSN");
        assertNotNull("DataSource retrieved is NULL", ds);
        assertNotNull("Connection retrieved from DataSource is NULL",
            ds.getConnection());
        assertEquals("Connection is corrupted", "MockDatabase",
            ds.getConnection().getMetaData().getDatabaseProductName());
    }
}
