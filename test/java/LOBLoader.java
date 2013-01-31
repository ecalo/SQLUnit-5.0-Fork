/*
 * $Id: LOBLoader.java,v 1.7 2004/10/05 20:13:04 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/LOBLoader.java,v $
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

import net.sourceforge.sqlunit.utils.DigestUtils;

import org.apache.log4j.Logger;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * This class populates the Dictionary object for each user with a tool
 * permission bitmap and loads these into the database.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.7 $
 */
public class LOBLoader extends TestCase {

    private static final Logger LOG = Logger.getLogger(LOBLoader.class);

    private static final String ENGINE_REGISTRY = 
        "net.sourceforge.sqlunit.test.dbengine";
    private static final String[] USERS = {"nobody", "Larry", "Curly", "Moe"};
    private static final String[] TOOLS = {"ToolX", "ToolY", "ToolZ"};
    private static final int BINARY_COL_INDEX = 3;
    private static final int TEXT_COL_INDEX = 4;

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
        return new TestSuite(LOBLoader.class);
    }

    /**
     * Boilerplate constructor.
     * @param name the name of this test class.
     */
    public LOBLoader(final String name) {
        super(name);
    }

    /**
     * A little misleading, since we are too lazy to build a real ant
     * target for this action.
     * @exception Exception if there was a problem.
     */
    public final void testMain() throws Exception {
        String engine = System.getProperty("db.engine");
        System.out.println("Populating " + engine + " database for LOB test");
        ResourceBundle eprops = ResourceBundle.getBundle(ENGINE_REGISTRY);
        // Set up database connection
        /*
        Class.forName(eprops.getString(engine + ".driver"));
        Connection conn = DriverManager.getConnection(
            eprops.getString(engine + ".url"), 
            eprops.getString(engine + ".user"),
            eprops.getString(engine + ".pass"));
        */
        Class.forName("com.mysql.jdbc.Driver");
        Properties connProps = new Properties();
        connProps.setProperty("user", "defaultuser");
        connProps.setProperty("password", "defaultuser");
        connProps.setProperty("useUnicode", "true");
        Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/sqlunitdb", connProps);
        PreparedStatement psd = conn.prepareStatement(
            "delete from lobtest where 1=1");
        psd.executeUpdate();
        psd.close();
        PreparedStatement ps = conn.prepareStatement("insert into lobtest("
            + "user,permission,securekey,certificate) values (?,?,?,?)");
        PreparedStatement pss = conn.prepareStatement(
            "select user,permission,securekey,certificate from "
            + "lobtest where user=?");
        // For each user
        for (int i = 0; i < USERS.length; i++) {
            ps.clearParameters();
            // Build Dictionary object for native serialization
            Dictionary dict = new Dictionary();
            // Populate them
            for (int j = 0; j < TOOLS.length; j++) {
                if ((i + j) % 2 == 0) {
                    dict.setEntry(TOOLS[j], "true");
                } else {
                    dict.setEntry(TOOLS[j], "false");
                }
            }
            // Persist Dictionary to disk for file test
            String fnser = "/tmp/Dictionary-" + USERS[i] + ".ser";
            FileOutputStream fos = new FileOutputStream(fnser);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(dict);
            oos.flush();
            fos.close();
            // Persist record to database
            ps.setString(1, USERS[i]);
            File fser = new File(fnser);
            ps.setBinaryStream(2, new FileInputStream(fser), 
                (int) fser.length());
            // Just read from /bin/ls
            File fkey = new File("/bin/ls");
            ps.setBinaryStream(BINARY_COL_INDEX, new FileInputStream(fkey),
                (int) fkey.length());
            // Just read from this source file
            File fcert = new File(
                "test/java/LOBLoader.java");
            ps.setAsciiStream(TEXT_COL_INDEX, new FileInputStream(fcert),
                (int) fcert.length());
            ps.executeUpdate();
            // now try to get back what we put in
            pss.clearParameters();
            pss.setString(1, USERS[i]);
            ResultSet rs = pss.executeQuery();
            while (rs.next()) {
                String s1 = rs.getString(1);
                assertEquals(USERS[i], s1);
                InputStream is2 = rs.getBinaryStream(2);
                LOG.debug("Comparing /tmp/Dictionary.ser to database output");
                assertEquals(DigestUtils.getMD5CheckSum(new FileInputStream(
                    "/tmp/Dictionary-" + USERS[i] + ".ser")),
                    DigestUtils.getMD5CheckSum(is2));
                InputStream is3 = rs.getBinaryStream(BINARY_COL_INDEX);
                LOG.debug("Comparing /bin/ls to database output");
                assertEquals(DigestUtils.getMD5CheckSum(new FileInputStream(
                    "/bin/ls")), DigestUtils.getMD5CheckSum(is3));
                InputStream is4 = rs.getAsciiStream(TEXT_COL_INDEX);
                LOG.debug("Comparing LOBLoader.java to database output");
                assertEquals(DigestUtils.getMD5CheckSum(new FileInputStream(
                    "test/java/LOBLoader.java")), 
                    DigestUtils.getMD5CheckSum(is4));
            }
            rs.close();
        }
        ps.close();
        pss.close();
        conn.close();
    }
}
