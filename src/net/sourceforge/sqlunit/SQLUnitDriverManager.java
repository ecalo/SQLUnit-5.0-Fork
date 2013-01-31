/*
 * $Id: SQLUnitDriverManager.java,v 1.8 2004/09/25 23:00:00 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/SQLUnitDriverManager.java,v $
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

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.util.Properties;

/**
 * The SQLUnitDriverManager is an incomplete implementation the 
 * java.sql.DriverManager class. This class is used to get a Connection
 * object where the JAR file for the database driver is not in the 
 * CLASSPATH but in a location specified by a URL. The only methods 
 * implemented are the getConnection() methods, with additional 
 * arguments specifying the driver class name and the location of 
 * the JAR file for the driver.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.8 $
 */
public final class SQLUnitDriverManager {

    private static final Logger LOG = 
        Logger.getLogger(SQLUnitDriverManager.class);

    /**
     * Private Constructor. Cannot be instantiated.
     */
    private SQLUnitDriverManager() {
        // private constructor, cannot instantiate
    }

    /**
     * Gets a Connection given the JDBC URL, the class name of the driver
     * and the URL location where the JAR file for the driver can be found.
     * @param url the JDBC URL of the driver.
     * @param driverClass the full class name of the JDBC driver.
     * @param locs a comma-separated list of URL strings indicating JAR files
     * and class file directories where the driver class file may be found.
     * @return a Connection object.
     * @exception Exception if there was a problem getting Connection.
     */
    public static Connection getConnection(final String url, 
            final String driverClass, final String locs) throws Exception {
        LOG.debug(">> getConnection(" + url + "," + driverClass + "," + locs 
            + ")");
        Properties props = new Properties();
        return getConnection(url, props, driverClass, locs);
    }

    /**
     * Gets a Connection given the JDBC URL, the user/password for the 
     * database user, the class name of the driver, and the URL location 
     * for the JAR file containing the JDBC driver.
     * @param url the JDBC URL of the driver.
     * @param user the database username.
     * @param password the password for the database user.
     * @param driverClass the full class name of the JDBC driver.
     * @param locs a comma-separated list of URL strings indicating JAR files
     * and class file directories where the driver class file may be found.
     * @return a Connection object.
     * @exception Exception if there was a problem getting Connection.
     */
    public static Connection getConnection(final String url, final String user,
            final String password, final String driverClass, final String locs)
            throws Exception {
        LOG.debug(">> getConnection(" + url + "," + user + "," + password 
            + "," + driverClass + "," + locs + ")");
        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);
        return getConnection(url, props, driverClass, locs);
    }

    /**
     * Gets the Connection given the JDBC URL, a Properties object containing
     * connection information such as user/password, the class name of the
     * JDBC driver, and the URL location for the JAR file containing the 
     * JDBC driver.
     * @param url the JDBC URL of the driver.
     * @param props a Properties object containing connection information.
     * @param driverClass the full class name of the JDBC driver.
     * @param locs a comma-separated list of URL strings indicating JAR files
     * and class file directories where the driver class file may be found.
     * @return a Connection object.
     * @exception Exception if there was a problem getting Connection.
     */
    public static Connection getConnection(final String url, 
            final Properties props, final String driverClass, 
            final String locs) throws Exception {
        LOG.debug(">> getConnection(" + url + ",props," + driverClass + "," 
            + locs + ")");
        try {
            URL[] urls;
            if (locs.indexOf(',') > -1) {
                String[] urlStrings = locs.split("\\s*,\\s*");
                urls = new URL[urlStrings.length];
                for (int i = 0; i < urls.length; i++) {
                    urls[i] = new URL(urlStrings[i]);
                }
            } else {
                urls = new URL[] {new URL(locs)};
            }
            URLClassLoader ucl = new URLClassLoader(urls);
            Driver driver = 
                (Driver) Class.forName(driverClass, true, ucl).newInstance();
            Connection conn = driver.connect(url, props);
            return conn;
        } catch (Throwable t) {
            throw new SQLUnitException(IErrorCodes.MISSING_RESOURCE,
                new String[] {t.getMessage()}, t);
        }
    }
}
