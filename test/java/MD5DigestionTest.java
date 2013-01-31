/*
 * $Id: MD5DigestionTest.java,v 1.9 2006/04/30 22:25:56 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/MD5DigestionTest.java,v $
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.FileInputStream;

/**
 * Tests MD5 digesting for various types of data.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.9 $
 */
public class MD5DigestionTest extends TestCase {

    private static final String MD5_PREFIX = "md5:";
    private static final int MD5_PREFIX_LENGTH = MD5_PREFIX.length();

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
        return new TestSuite(MD5DigestionTest.class);
    }

    /**
     * Boilerplate constructor.
     * @param name the name of this test class.
     */
    public MD5DigestionTest(final String name) {
        super(name);
    }

    /**
     * Tests MD5 digestion of a serialized Java object file.
     * @exception Exception if there was a problem.
     */
    public final void testDigestJavaObject() throws Exception {
        String expected = "4e93398c9f620694de6e7a9739dbdb08";
        String fileName = "build/tmpDictionary.ser";
        // we rebuild the Dictionary.ser file just in case
        Dictionary stooges = new Dictionary();
        stooges.setEntry("1", "Larry");
        stooges.setEntry("2", "Curly");
        stooges.setEntry("3", "Moe");
        stooges.writeTo(fileName);
        String got = DigestUtils.getMD5CheckSum(new FileInputStream(fileName));
        assertEquals(expected, got.substring(MD5_PREFIX_LENGTH));
    }

    /**
     * Tests MD5 digestion of a text filw.
     * @exception Exception if there was a problem.
     */
    public final void testDigestTextFile() throws Exception {
        String expected = "a5e492029a7dc2c5b890da1f003be720";
        String fileName = "etc/blob.test";
        String got = DigestUtils.getMD5CheckSum(new FileInputStream(fileName));
        assertEquals(expected, got.substring(MD5_PREFIX_LENGTH));
    }

    /**
     * Tests MD5 digestion of a binary file
     * @exception Exception if there was a problem.
     */
    public final void testDigestBinaryFile() throws Exception {
        String expected = "7131359bdbca7b8e40eaf4f6fd77eee6";
        String fileName = "docs/guitool-0.png";
        String got = DigestUtils.getMD5CheckSum(new FileInputStream(fileName));
        assertEquals(expected, got.substring(MD5_PREFIX_LENGTH));
    }
}
