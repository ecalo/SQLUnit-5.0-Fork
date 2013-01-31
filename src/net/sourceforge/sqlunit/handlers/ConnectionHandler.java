/*
 * $Id: ConnectionHandler.java,v 1.10 2006/06/25 23:02:50 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/ConnectionHandler.java,v $
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
package net.sourceforge.sqlunit.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.sqlunit.ConnectionRegistry;
import net.sourceforge.sqlunit.HandlerFactory;
import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.beans.Arg;
import net.sourceforge.sqlunit.beans.StructBean;
import net.sourceforge.sqlunit.beans.TypeDef;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * The ConnectionHandler class creates a JDBC connection object given its
 * parameters in the form of a JDOM Element.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.10 $
 * @sqlunit.parent name="sqlunit" ref="sqlunit"
 * @sqlunit.element name="connection" 
 *  description="Supplies information for the SQLUnit test harness to connect
 *  to the underlying database containing the stored procedures to be tested.
 *  Connections can be built using JDBC or from a DataSource object looked up
 *  through JNDI. Connections can also be specified using file URLs. Multiple
 *  connection tags can be specified if more than one Connection is needed 
 *  for a single SQLUnit test suite."
 *  syntax="(((driver, url, user, password, (jarfile-url)?) | 
 *  (datasource, jndi))?)"
 * @sqlunit.attrib name="connection-id"
 *  description="When multiple Connections are defined in a SQLUnit test,
 *  this attribute refers to a Connection to use for running this call by
 *  its connection-id. If not specified, SQLUnit will try to look up the
 *  default Connection which has no connection-id attribute defined."
 *  required="No, default Connection is used if not supplied"
 * @sqlunit.attrib name="extern"
 *  description="If specified, the value of this attribute points to a Java
 *  properties file which contains the information necessary to build the
 *  Connection. The file can be specified without path information if it
 *  can be found in the caller's CLASSPATH or specified as a relative path,
 *  as well as an absolute path on the file system. A sample properties file
 *  can be found in /cvs/test/postgresql/sqlunit.properties."
 *  required="No"
 * @sqlunit.attrib name="transaction-support"
 *  description="Indicates whether transaction support is provided by SQLUnit.
 *  Setting this to on (the default) will make SQLUnit treat each SQL or CALL
 *  as a single unit of work. Turning this to off will make SQLUnit not do
 *  any COMMITs or ROLLBACKs, it is left to the client to enforce boundaries
 *  of the unit of work by putting COMMIT and ROLLBACK in the SQL or stored
 *  procedures. Finally, turning this to implicit will put SQLUnit in auto
 *  commit mode, but SQLUnit will not do any explicit COMMIT or ROLLBACK.
 *  Transaction boundaries will either by enforced by the database if it
 *  supports it, or by the JDBC autocommit mechanism if not."
 *  required="No, defaults to on"
 * @sqlunit.attrib name="reconnect-on-failure"
 *  description="Will destroy the current Connection and rebuild it in the
 *  event of a test failure or exception. This is needed to work around 
 *  buggy database drivers which leave the Connection in an inconsistent 
 *  state in such cases."
 *  required="No, defaults to false"
 * @sqlunit.attrib name="server-name"
 *  description="If specified, will override the database server name that is 
 *  derived from the DatabaseMetaData for the connection. This is used to
 *  lookup data types supported by this database."
 *  required="No, defaults to the database server name"
 * @sqlunit.child name="driver"
 *  description="Specifies the full Java class name for the JDBC driver.
 *  This is needed for building the Connection using JDBC."
 *  required="Yes (JDBC)" 
 *  ref="none"
 * @sqlunit.child name="url"
 *  description="Specifies the JDBC URL to the database"
 *  required="Yes (JDBC)" 
 *  ref="none"
 * @sqlunit.child name="user"
 *  description="Specifies the database user who will connect to the database"
 *  required="Yes (JDBC)" 
 *  ref="none"
 * @sqlunit.child name="password"
 *  description="Specifies the password for the database user"
 *  required="Yes (JDBC)" 
 *  ref="none"
 * @sqlunit.child name="jarfile-url"
 *  description="Specifies a URL (eg. file:///tmp/mydriver.jar) for a JAR
 *  file containing a JDBC driver which is not in the CLASSPATH."
 *  required="No (JDBC)" 
 *  ref="none"
 * @sqlunit.child name="datasource"
 *  description="Specifies the name of a DataSource. By convention, this
 *  would look something like jdbc:/myDSN, where the DataSource object
 *  is actually stored under java:/comp/env/jdbc. This is likely to vary
 *  between JNDI servers and sites, so check with your administrator for
 *  the DataSource name to use" 
 *  required="Yes (JNDI)" 
 *  ref="none"
 * @sqlunit.child name="jndi"
 *  description="Empty tag, contains zero or more arg elements containing
 *  the name-value pairs to pass in when creating the Initial Naming Context
 *  that will be used to look up the DataSource object. These name-value 
 *  pairs locate the JNDI service. Each arg element will correspond to a line
 *  in the jndi.properties file for the given JNDI server."
 *  required="Yes (JNDI)" 
 *  ref="jndi"
 * @sqlunit.example name="Specifying a JDBC connection for PostgreSQL"
 *  description="
 *  <connection>{\n}
 *  {\t}<driver>org.postgresql.Driver</driver>{\n}
 *  {\t}<url>jdbc:postgresql://localhost:5432/demodb</url>{\n}
 *  {\t}<user>defaultuser</user>{\n}
 *  {\t}<password>d3fau1tus3r</password>{\n}
 *  </connection>"
 * @sqlunit.example name="Specifying a JNDI connection with JBoss"
 *  description="
 *  <connection>{\n}
 *  {\t}<datasource>jdbc/myDSN</datasource>{\n}
 *  {\t}<jndi>{\n}
 *  {\t}{\t}<arg name=\"java.naming.factory.initial\"{\n}
 *  {\t}{\t}{\t}value=\"org.jnp.interfaces.NamingContextFactory\" />{\n}
 *  {\t}{\t}<arg name=\"java.naming.provider.url\" {\n}
 *  {\t}{\t}{\t}value=\"jnlp://localhost:1099\" />{\n}
 *  {\t}{\t}<arg name=\"java.naming.factory.url.pkgs\" {\n}
 *  {\t}{\t}{\t}value=\"org.jboss.naming\" />{\n}
 *  {\t}</jndi>{\n}
 *  </connection>"
 */
public class ConnectionHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(ConnectionHandler.class);

    private static final String EXTPREFIX_SQLUNIT = "sqlunit.";
    private static final String EXTPREFIX_SQLUNIT_JNDI = "sqlunit.jndi.";
    private static final int EXTPREFIX_SQLUNIT_LEN = EXTPREFIX_SQLUNIT.length();
    private static final int EXTPREFIX_SQLUNIT_JNDI_LEN = 
        EXTPREFIX_SQLUNIT_JNDI.length();

    private Map connPropertyMap;

    /**
     * Returns the last connection's properties.
     * @return the last connection's properties as a Map.
     */
    public final Map getLastConnectionProperties() {
        return connPropertyMap;
    }

    /**
     * Processes the JDOM Element and returns a JDBC Connection object.
     * @param elConn the JDOM Element representing the Connection parameters.
     * @return the Connection object.
     * @exception Exception if something goes wrong with the creating.
     */
    public final Object process(final Element elConn) throws Exception {
        LOG.debug(">> process()");
        if (elConn == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"connection"});
        }
        String connectionId = null;
        try {
            // get all attributes
            connectionId = XMLUtils.getAttributeValue(elConn, "connection-id");
            String externAttr = XMLUtils.getAttributeValue(
                elConn, "extern");
            String hasTransactions = XMLUtils.getAttributeValue(
                elConn, "transaction-support");
            String reconnectEnabled = XMLUtils.getAttributeValue(
                elConn, "reconnect-on-failure");
            String serverName = XMLUtils.getAttributeValue(
                elConn, "server-name");
            // get all elements
            HashMap typeMap = new HashMap();
            connPropertyMap = new HashMap();
            if (externAttr != null) {
                // build the property map from the specified external source
                connPropertyMap = getChildrenFromExternalSource(externAttr);
            } else {
                List elConnElements = elConn.getContent();
                int numElements = elConnElements.size();
                for (int i = 0; i < numElements; i++) {
                    Object connElement = elConnElements.get(i);
                    if (connElement instanceof Element) {
                        Element elConnElement = (Element) connElement;
                        String key = elConnElement.getName();
                        if (key.equals("jndi")) {
                            List elArgs = elConnElement.getChildren("arg");
                            Arg[] jndiArgs = new Arg[elArgs.size()];
                            for (int j = 0; j < jndiArgs.length; j++) {
                                Element elArg = (Element) elArgs.get(j);
                                IHandler argHandler = 
                                    HandlerFactory.getInstance(elArg.getName());
                                jndiArgs[j] = (Arg) argHandler.process(elArg);
                                connPropertyMap.put(
                                    "jndi:" + jndiArgs[j].getName(),
                                    jndiArgs[j].getValue());
                            }
                         } else if (key.equals("typemap")) {
                            List elTypeDefs =
                                elConnElement.getChildren("typedef");
                            Iterator itr = elTypeDefs.iterator();
                            while (itr.hasNext()) {
                                Element elTypeDef = (Element) itr.next();
                                IHandler typeDefHandler = 
                                    HandlerFactory.getInstance(
                                        elTypeDef.getName());
                                TypeDef typeDef =
                                    (TypeDef) typeDefHandler.process(elTypeDef);
                                typeMap.put(typeDef.getTypeName(),
                                    Class.forName(typeDef.getClassName()));
                            }
                         } else {
                            String value = XMLUtils.getText(elConnElement);
                            connPropertyMap.put(key, value);
                        }
                    }
                }
            }
            connPropertyMap.put("transaction-support", hasTransactions);
            connPropertyMap.put("reconnect-on-failure", reconnectEnabled);
            connPropertyMap.put("server-name", serverName);
            // push it into the registry and set default if applicable.
            ConnectionRegistry.setConnection(connectionId, connPropertyMap);
            Connection conn = ConnectionRegistry.getConnection(connectionId);
            Map defTypeMap = conn.getTypeMap();
            defTypeMap.putAll(typeMap);
            StructBean.setTypeMap(defTypeMap);
            return conn;
        } catch (Exception e) {
            throw new SQLUnitException(IErrorCodes.CANT_BUILD_CONNECTION,
                new String[] {connectionId, e.getMessage()}, e);
        }
    }

    /**
     * Retrieves a map of name-value connection properties from the environment
     * or from a named file. The value of extern can be either a file 
     * (absolute or relative from the caller) or a resource name in the
     * caller's classpath. If such a file is not found, SQLUnit will return
     * an empty map, it will not complain that the file was not found.
     * @param source the name of the external source to lookup.
     * @return a Map of name value pairs representing connection properties.
     * @exception Exception if any are thrown.
     */
    private Map getChildrenFromExternalSource(final String source) 
            throws Exception {
        LOG.debug(">> getChildrenFromExternalSource(" + source + ")");
        Map props = new HashMap();
        if (source.trim().length() == 0) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"connection.@extern"});
        } else {
            // see if the file exists
            File f = new File(source);
            if (f.exists()) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(f)));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#")) { continue; }
                    String[] nameValue = line.split("\\s*=\\s*", 2);
                    if (nameValue[0].startsWith("sqlunit.")) {
                        nameValue[0] = getInternalForm(nameValue[0]);
                        if (nameValue.length == 2) {
                            props.put(nameValue[0], nameValue[1]);
                        }
                    }
                }
                reader.close();
            } else {
                ResourceBundle r = ResourceBundle.getBundle(source);
                for (Enumeration e = r.getKeys(); e.hasMoreElements();) {
                    String name = (String) e.nextElement();
                    String value = r.getString(name);
                    if (name.startsWith("sqlunit.")) {
                        name = getInternalForm(name);
                        props.put(name, value);
                    }
                }
            }
        }
        return props;
    }

    /**
     * Converts the external form suitable for multi-application property
     * specification to the internal form understood by SQLUnit.
     * @param externalKey the key to convert.
     * @return the internal form of the key.
     */
    private static String getInternalForm(final String externalKey) {
        LOG.debug(">> getInternalForm(" + externalKey + ")");
        if (externalKey.startsWith(EXTPREFIX_SQLUNIT_JNDI)) {
            return "jndi:" + externalKey.substring(EXTPREFIX_SQLUNIT_JNDI_LEN);
        } else if (externalKey.startsWith(EXTPREFIX_SQLUNIT)) {
            return externalKey.substring(EXTPREFIX_SQLUNIT_LEN);
        } else {
            return externalKey;
        }
    }
}
