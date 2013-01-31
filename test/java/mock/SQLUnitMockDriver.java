/*
 * $Id: SQLUnitMockDriver.java,v 1.2 2004/10/05 20:13:04 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/mock/SQLUnitMockDriver.java,v $
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

import com.mockrunner.mock.jdbc.MockDriver;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Implements a Mock JDBC driver for SQLUnit.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.2 $
 */
public class SQLUnitMockDriver extends MockDriver {

    private static final Logger LOG = Logger.getLogger(SQLUnitMockDriver.class);

    private static final String URL_PREFIX = "jdbc:mock:";
    private static final int URL_PREFIX_LENGTH = URL_PREFIX.length();

    // register the driver with the DriverManager
    static {
        try { 
            DriverManager.registerDriver(new SQLUnitMockDriver());
        } catch (Exception e) {
            //:IGNORE:
        }
    }

    /**
     * Instantiate a SQLUnitMockDriver.
     */
    public SQLUnitMockDriver() {
        super();
    }

    /**
     * Specifies a database URL pattern that this Driver will accept.
     * @param url the database URL.
     * @return true if this driver accepts this database URL.
     * @exception SQLException if there is a problem in the method.
     */
    public boolean acceptsURL(final String url) throws SQLException {
        LOG.debug(">> acceptsURL(" + url + ")");
        return (url != null && url.startsWith(URL_PREFIX));
    }

    /**
     * Called from the DriverManager.getConnection() method.
     * @param url the database url to use.
     * @param info a Properties object containing other information.
     * @return a Connection.
     * @exception SQLException if there was a problem connecting.
     */
    public Connection connect(final String url, final Properties info)
            throws SQLException {
        LOG.debug(">> connect(" + url + ",info)");
        String datastoreClassName = url.substring(URL_PREFIX_LENGTH);
        SQLUnitMockConnection conn = new SQLUnitMockConnection();
        conn.setCatalog(datastoreClassName);
        return conn;
    }
}
