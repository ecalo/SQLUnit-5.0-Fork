/*
 * $Id: MockInitialContextFactory.java,v 1.3 2004/10/05 20:13:04 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/mock/MockInitialContextFactory.java,v $
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
package net.sourceforge.sqlunit.test.mock;

import com.mockrunner.mock.jdbc.MockDataSource;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.sql.DataSource;

/**
 * Builds a MockInitialContext using an environment Hashtable.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.3 $
 */
public class MockInitialContextFactory implements InitialContextFactory {

    private static final Logger LOG = Logger.getLogger(
        MockInitialContextFactory.class);

    // hardcoded values to set up the Connection
    private static final String DRIVER = 
        "net.sourceforge.sqlunit.test.mock.SQLUnitMockDriver";
    private static final String URL = 
        "jdbc:mock:net.sourceforge.sqlunit.test.mock.SQLUnitMockDatabase";
    private static final String DSN = "jdbc/mockDSN";

    /**
     * Instantiates a MockInitialContextFactory.
     */
    public MockInitialContextFactory() {
        LOG.debug("Instantiated MockInitialContextFactory");
    }

    /**
     * Builds and returns a MockInitialContext object pre-filled with a
     * named DataSource object.
     * @param env a Hashtable of properies to instantiate the InitialContext.
     * @return a MockInitialContext object.
     * @exception NamingException if an error occurs building an InitialContext
     */
    public final Context getInitialContext(final Hashtable env) 
            throws NamingException {
        try {
            MockInitialContext ctx = new MockInitialContext();
            ctx.bind(DSN, getMockDataSource());
            return ctx;
        } catch (Exception e) {
            throw new NamingException(e.getMessage());
        }
    }

    /**
     * Uses JDBC to build up the MockDataSource containing a Connection
     * to our MockDatabase.
     * @return a MockDataSource object.
     * @exception Exception if an error occurs.
     */
    private DataSource getMockDataSource() throws Exception {
        MockDataSource mds = new MockDataSource();
        Class.forName(DRIVER);
        Connection conn = DriverManager.getConnection(URL);
        mds.setupConnection(conn);
        return mds;
    }
}
