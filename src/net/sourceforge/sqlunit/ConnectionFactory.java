/*
 * $Id: ConnectionFactory.java,v 1.5 2005/04/19 18:45:59 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/ConnectionFactory.java,v $
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * A factory to generate Connection objects given the Connection properties.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.5 $
 */
public final class ConnectionFactory {
    
    private static final Logger LOG = Logger.getLogger(ConnectionFactory.class);

    /**
     * Private Constructor. Cannot be instantiated.
     */
    private ConnectionFactory() {
        // cannot be instantiated
    }

    /**
     * Builds a Connection object out of a Map of Connection properties.
     * The properties can either be for a JNDI or JDBC connection or some
     * variant where SQLUnit uses the jarfile-url attribute to locate a
     * driver JAR from the filesystem.
     * @param props a Map of Connection properties.
     * @return a Connection object.
     * @exception Exception if there was a problem building the connection.
     */
    public static Connection getInstance(final Map props) throws Exception {
        LOG.debug(">> getInstance(props)");
        Connection conn;
        if (props == null) { return null; }
        boolean usesJdbc = (props.get("driver") != null);
        if (usesJdbc) {
            // check to see if all properties are set
            if (props.get("driver") == null
                    || props.get("url") == null) {
                throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                    new String[] {"driver/url"});
            }
            String url = (String) props.get("url");
            boolean needsUser = true;
            if (url.indexOf("user=") > -1 && url.indexOf("password=") > -1) {
                needsUser = false;
            }
            if (needsUser && ((props.get("user") == null)
                    || (props.get("password") == null))) {
                throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                    new String[] {"user/password"});
            }
            // check to see if location specified, if so, use the 
            // SQLUnitDriver manager to instantiate connection
            if (props.get("jarfile-url") != null) {
                if (url.indexOf("user=") > -1
                        && url.indexOf("password=") > -1) {
                    conn = SQLUnitDriverManager.getConnection(
                        (String) props.get("url"),
                        (String) props.get("driver"),
                        (String) props.get("jarfile-url"));
                } else {
                    conn = SQLUnitDriverManager.getConnection(
                        (String) props.get("url"),
                        (String) props.get("user"),
                        (String) props.get("password"),
                        (String) props.get("driver"),
                        (String) props.get("jarfile-url"));
                }
            } else {
                // instantiate the driver for DriverManager
                try {
                    Class.forName((String) props.get("driver"));
                } catch (ClassNotFoundException e) {
                    throw new SQLUnitException(IErrorCodes.MISSING_RESOURCE,
                        new String[] {e.getMessage()}, e);
                }
                // some drivers need the user and password in the url
                if (url.indexOf("user=") > -1
                        && url.indexOf("password=") > -1) {
                    conn = DriverManager.getConnection(url);
                } else {
                    conn = DriverManager.getConnection(url,
                        (String) props.get("user"),
                        (String) props.get("password"));
                }
            }
        } else {
            // check to see if all properties are set
            String dsn = (String) props.get("datasource");
            if (dsn == null) {
                throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                    new String[] {"connection.datasource"});
            }
            // build the JNDI environment from the args
            Hashtable env = new Hashtable();
            Iterator propIter = props.keySet().iterator();
            String jndiPrefix = "jndi:";
            while (propIter.hasNext()) {
                String key = (String) propIter.next();
                if (key.startsWith(jndiPrefix)) {
                    String value = (String) props.get(key);
                    key = key.substring(jndiPrefix.length());
                    env.put(key, value);
                }
            }
            // build initial context and lookup the datasource object
            Context ctx = null;
            DataSource ds = null;
            // reset the ClassLoader to the thread context classloader
            Thread.currentThread().setContextClassLoader(
                ConnectionFactory.class.getClassLoader());
            try {
                ctx = new InitialContext(env);
                ds = (DataSource) ctx.lookup(dsn);
            } catch (Exception e) {
                throw new SQLUnitException(IErrorCodes.MISSING_RESOURCE,
                    new String[] {e.getMessage()}, e);
            }
            conn = ds.getConnection();
        }
        return conn;
    }
}
