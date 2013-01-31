/*
 * $Id: ToolUtils.java,v 1.11 2006/04/30 22:25:56 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/tools/ToolUtils.java,v $
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
package net.sourceforge.sqlunit.tools;

import net.sourceforge.sqlunit.ConnectionRegistry;
import net.sourceforge.sqlunit.HandlerFactory;
import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.beans.DatabaseResult;
import net.sourceforge.sqlunit.beans.Param;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Provides methods for common functionality used by the tools.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.11 $
 */
public final class ToolUtils {

    private static final Logger LOG = Logger.getLogger(ToolUtils.class);
    private static String indent = null;

    /**
     * Private Constructor. Cannot be instantiated.
     */
    private ToolUtils() {
        // private constructor, cannot instantiate
    }

    /**
     * Parses the configuration property map to get configuration name
     * value pairs and returns it as a Properties object. The resource
     * may be specified as a relative or absolute path to a file or
     * as a resource in the classpath.
     * @param resource the name of the resource file to use.
     * @return a Properties object containing configuration properties.
     * @exception Exception if there was a problem.
     */
    public static Properties getConfiguration(final String resource) 
            throws Exception {
        LOG.debug(">> getting configuration for " + resource);
        Properties config = new Properties();
        // check if this is a file path
        File f = new File(resource);
        ResourceBundle bundle = null;
        if (f.exists()) {
            try {
                bundle = new PropertyResourceBundle(new FileInputStream(f));
            } catch (IOException e) {
                throw new SQLUnitException(IErrorCodes.GENERIC_ERROR,
                    new String[] {"I/O", e.getClass().getName(),
                    "Cant find config file: " + resource}, e);
            }
        } else {
            // check if this is a resource in the classpath
            try {
                bundle = ResourceBundle.getBundle(resource);
            } catch (MissingResourceException e) {
                throw new SQLUnitException(IErrorCodes.GENERIC_ERROR, 
                    new String[] {"System", e.getClass().getName(),
                    "Cant find resource " + resource + " in CLASSPATH"}, e);
            }
        }
        for (Enumeration e = bundle.getKeys(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            String value = bundle.getString(key);
            if (value.trim().length() > 0) {
                value = value.trim();
            }
            config.put(key, value);
        }
        return config;
    }

    /**
     * Returns a reference to a database Connection using values in the
     * properties object.
     * @param props a Properties object.
     * @return a database Connection.
     * @exception Exception if there was a problem.
     */
    public static Connection getConnection(final Properties props) 
            throws Exception {
        Map connProps = new HashMap();
        connProps.put("driver", props.getProperty("driver"));
        connProps.put("url", props.getProperty("url"));
        connProps.put("user", props.getProperty("user"));
        connProps.put("password", props.getProperty("password"));
        connProps.put("jarfile-url", props.getProperty("jarfile-url"));
        ConnectionRegistry.setConnection(null, connProps);
        return ConnectionRegistry.getConnection(null);
    }

    /**
     * Releases the connection object after the test is complete.
     */
    public static void releaseConnection() {
        ConnectionRegistry.invalidate(null);
    }

    /**
     * Runs a database query with the specified Connection object and
     * the Properties file passed in.
     * @param sql the JDOM Element representing the SQL to execute.
     * @param params an array of Param objects.
     * @param testName the name of the test to use.
     * @param config the Properties object.
     * @exception Exception if there was a problem.
     */
    public static void makeTest(final String sql, final Param[] params,
            final String testName, final Properties config) throws Exception {
        Element elCall;
        if (sql.startsWith("{") && sql.endsWith("}")) {
            elCall = new Element("call");
        } else {
            elCall = new Element("sql");
        }
        Element elStmt = new Element("stmt");
        elStmt.setText(sql);
        elCall.addContent(elStmt);
        for (int i = 0; i < params.length; i++) {
            Element elParam = new Element("param");
            elParam.setAttribute("id", params[i].getId());
            elParam.setAttribute("name", params[i].getName());
            elParam.setAttribute("type", params[i].getType());
            elParam.setAttribute("is-null", params[i].getIsNull());
            elParam.setAttribute("inout", params[i].getInOut());
            elParam.setText(params[i].getValue());
            elCall.addContent(elParam);
        }
        IHandler handler = HandlerFactory.getInstance(elCall.getName());
        DatabaseResult result = (DatabaseResult) handler.process(elCall);
        if (result == null) { result = new DatabaseResult(); }
        Element elResult = result.toElement();
        Element elTest = new Element("test");
        elTest.setAttribute("name", testName);
        elTest.addContent(elCall);
        elTest.addContent(elResult);
        Format format = Format.getPrettyFormat().setIndent(makeIndent(config.getProperty("indentsize")));
        format.setLineSeparator(config.getProperty("lineseparator",
            System.getProperty("line.separator")));
        XMLOutputter outputter = new XMLOutputter(format);
        PrintWriter captureWriter = new PrintWriter(new FileOutputStream(
            config.getProperty("capturefile")), true);
        outputter.output(elTest, captureWriter);
        captureWriter.println();
        LOG.info("Test generated in " + config.getProperty("capturefile"));
    }

    /**
     * Works around JDOM's inconvenient deprecation of setIndentSize(int)
     * @param numSpaces the number of spaces as a String.
     * @return the indent string to use.
     */
    private static String makeIndent(final String numSpaces) {
        if (indent == null) {
            int defaultIndent = 2;
            try {
                defaultIndent = Integer.parseInt(numSpaces);
            } catch (NumberFormatException e) {
                // :IGNORE: reset to default value
            }
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < defaultIndent; i++) {
                buf.append(" ");
            }
            indent = buf.toString();
        }
        return indent;
    }
}
