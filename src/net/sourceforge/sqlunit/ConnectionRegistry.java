/*
 * $Id: ConnectionRegistry.java,v 1.22 2005/07/09 01:39:20 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/ConnectionRegistry.java,v $
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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A Singelton object which stores database Connection objects in a HashMap
 * and returns them to the application on demand. The properties are kept
 * in a backup HashMap so they can be regenerated on demand.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.22 $
 */
public final class ConnectionRegistry {
    
    private static final Logger LOG = 
        Logger.getLogger(ConnectionRegistry.class);

    private static final String DEFAULT_KEY = "__default";

    private static HashMap connections = new HashMap();
    private static HashMap connProperties = new HashMap();
    private static HashMap threadConnectionMap = new HashMap();

    /**
     * Private Constructor. Cannot be instantiated.
     */
    private ConnectionRegistry() {
        // cannot instantiate
    }

    /**
     * Returns the Connection object associated with a given connection-id.
     * @param id the value of the connection-id attribute.
     * @return the Connection object stored at this id.
     * @exception SQLUnitException if there was a problem getting the connection.
     */
    public static Connection getConnection(final String id)
            throws SQLUnitException {
        LOG.debug(">> getConnection(" + id + ")");
        // if id is null and there are indeed no connections, return null
        if (id == null && connections.size() == 0) { return null; }
        String connectionId = (id == null 
            ? ConnectionRegistry.DEFAULT_KEY : id);
        Connection conn = (Connection) connections.get(connectionId);
        if (conn == null) {
            try {
                conn = ConnectionFactory.getInstance((Map) connProperties.get(
                    connectionId));
            } catch (Exception e) {
                throw new SQLUnitException(IErrorCodes.GENERIC_ERROR,
                    new String[] {"Database Error", e.getClass().getName(), 
                    e.getMessage()});
            }
        }
        if (conn == null) {
            throw new SQLUnitException(IErrorCodes.CONNECTION_IS_NULL,
                new String[] {connectionId});
        }
        threadConnectionMap.put(ThreadIdentifier.getIdentifier(), connectionId);
        // auto-commit is always set to false, except when it is implicit.
        if (!isTransactionSupportImplicit(id)) {
            try {
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                throw new SQLUnitException(IErrorCodes.GENERIC_ERROR,
                    new String[] {"Error setting transaction property",
                    e.getClass().getName(), e.getMessage()});
            }
        }
        return conn;
    }

    /**
     * Returns the server name (supplied explicitly as an attribute to the
     * Connection element or implicitly derived from the DatabaseMetaData)
     * for the current thread.
     * @return the server name for the current thread.
     */
    public static String getServerName() {
        String connectionId = (String) threadConnectionMap.get(
            ThreadIdentifier.getIdentifier());
        Map propertyMap = (Map) connProperties.get(connectionId);
        if (propertyMap != null) { 
            return (String) propertyMap.get("server-name");
        } else {
            return null;
        }
    }

    /**
     * Sets the default connection object by specifying an actual Connection
     * object. Note that if the connection is specified in this manner, then
     * SQLUnit will not be able to recreate the Connection once it is 
     * invalidated. This method is generally used by external clients who
     * do not use the connection tag to specify the Connection to use for 
     * the test.
     * @param conn the Connection object to use.
     */
    public static void setConnection(final Connection conn) {
        connections.put(ConnectionRegistry.DEFAULT_KEY, conn);
    }

    /**
     * Sets a Connection keyed by a Map of connection properties supplied in
     * the connection element for the given Connection id.
     * @param id the value of the connection-id attribute.
     * @param props the Connection properties for the Connection object.
     * @exception Exception if there was a problem setting the connection.
     */
    public static void setConnection(final String id, final Map props) 
            throws Exception {
        LOG.debug(">> setConnectionById(" + id + ")");
        Connection conn = ConnectionFactory.getInstance(props);
        String connectionId = ConnectionRegistry.DEFAULT_KEY;
        if (id != null) { connectionId = id; }
        String serverName = (String) props.get("server-name");
        if (serverName == null) {
            // if not supplied, derive it
            serverName = conn.getMetaData().getDatabaseProductName();
        }
        serverName = serverName.replaceAll(" ", "_");
        serverName = serverName.toLowerCase();
        props.put("server-name", serverName);
        connProperties.put(connectionId, props);
        connections.put(connectionId, conn);
    }

    /**
     * Checks to see if the reconnect-on-failure is enabled for the specified
     * connection id parameter.
     * @param id the connection id.
     * @return true if reconnect-on-failure is enabled for this connection.
     */
    public static boolean reconnectOnFailure(final String id) {
        LOG.debug(">> reconnectOnFailure(" + id + ")");
        String connectionId = ConnectionRegistry.DEFAULT_KEY;
        if (id == null) { connectionId = id; }
        boolean reconnectEnabled = false;
        Map connProp = (Map) connProperties.get(connectionId);
        if (connProp != null) {
             reconnectEnabled = (("on").equals((String) connProp.get(
                 "reconnect-on-failure")));
        }
        return reconnectEnabled;
    }

    /**
     * Checks to see if the transaction-support feature is enabled for the
     * specified connection id parameter.
     * @param id the connection id.
     * @return true if transaction-support is on.
     */
    public static boolean hasTransactionSupport(final String id) {
        LOG.debug(">> hasTransactionSupport(" + id + ")");
        String connectionId = ConnectionRegistry.DEFAULT_KEY;
        if (id != null) { connectionId = id; }
        boolean hasTransactionSupport = true;
        Map connProp = (Map) connProperties.get(connectionId);
        if (connProp != null) {
            hasTransactionSupport = (("on").equals((String) connProp.get(
                "transaction-support")));
        }
        return hasTransactionSupport;
    }

    /**
     * Checks to see if the transaction support feature is set to implicit.
     * @param id the connection id.
     * @return true if the transaction-support is implicit.
     */
    public static boolean isTransactionSupportImplicit(final String id) {
        LOG.debug(">> isTransactionSupportImplicit(" + id + ")");
        String connectionId = ConnectionRegistry.DEFAULT_KEY;
        if (id != null) { connectionId = id; }
        boolean isTransactionSupportImplicit = false;
        Map connProp = (Map) connProperties.get(connectionId);
        if (connProp != null) {
            isTransactionSupportImplicit = (("implicit").equals(
                (String) connProp.get("transaction-support")));
        }
        return isTransactionSupportImplicit;
    }

    /**
     * Invalidates the Connection at connection-id attribute in the
     * Connection registry in case of an exception. The invalidation will
     * only happen if the Connection has reconnect-on-failure enabled.
     * @param id the value of the connection-id attribute.
     */
    public static void invalidate(final String id) {
        LOG.debug(">> invalidate(" + id + ")");
        String connectionId = ConnectionRegistry.DEFAULT_KEY;
        if (id != null) { connectionId = id; }
        boolean reconnectEnabled = 
            ConnectionRegistry.reconnectOnFailure(connectionId);
        Connection conn = (Connection) connections.get(connectionId);
        if (reconnectEnabled) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // :IGNORE: not necessary to trap this
                }
                conn = null;
            }
            connections.put(connectionId, null);
        }
    }

    /**
     * Closes all the connections in the registry and marks the registry
     * as ready for garbage collection.
     */
    public static void releaseConnections() {
        LOG.debug(">> releaseConnections()");
        Iterator connIter = connections.values().iterator();
        while (connIter.hasNext()) {
            Connection conn = (Connection) connIter.next();
            try {
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
            } catch (SQLException e) { 
                // :IGNORE: not necessary to trap this
            }
        }
        connections.clear();
    }

    /**
     * Issues a COMMIT on the specified connection only if transaction-support
     * is turned on for the connection.
     * @param id the Connection id.
     * @param conn the Connection.
     */
    public static void safelyCommit(final String id, final Connection conn) {
        LOG.debug(">> safelyCommit(" + id + ",conn)");
        String connectionId = ConnectionRegistry.DEFAULT_KEY;
        if (id != null) { connectionId = id; }
        if (ConnectionRegistry.hasTransactionSupport(connectionId)) {
            try { 
                conn.commit();
            } catch (SQLException e) {
                // :IGNORE: not necessary to trap this
                LOG.warn("Could not commit connection id: " + connectionId
                    + "(" + e.getMessage() + ")");
            }
        }
    }

    /**
     * Issues a ROLLBACK on the specified connection only if transaction-support
     * is turned on for the connection.
     * @param id the Connection id.
     * @param conn the Connection.
     */
    public static void safelyRollback(final String id, final Connection conn) {
        LOG.debug(">> safelyRollback(" + id + ",conn)");
        String connectionId = ConnectionRegistry.DEFAULT_KEY;
        if (id != null) { connectionId = id; }
        if (ConnectionRegistry.hasTransactionSupport(connectionId)) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                // :IGNORE: not necessary to trap this
                LOG.debug("Could not rollback connection id: " + connectionId
                    + "(" + e.getMessage() + ")");
            }
        }
    }
}
