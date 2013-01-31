/*
 * $Id: DigestUtils.java,v 1.10 2004/09/30 17:27:56 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/utils/DigestUtils.java,v $
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
package net.sourceforge.sqlunit.utils;

import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.SymbolTable;

import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Provides utility methods needed for MD5 Digestion.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.10 $
 */
public final class DigestUtils {

    private static final Logger LOG = Logger.getLogger(DigestUtils.class);

    private static final char HEX_FF = 0xff;
    private static final String FILE_PREFIX = "file:";
    private static final int FILE_PREFIX_LENGTH = 5;
    private static final int BYTES_IN_KB = 1024;

    /**
     * Private Constructor. Cannot be instantiated.
     */
    private DigestUtils() {
        // private constructor, cannot instantiate
    }

    /**
     * Returns the MD5 Checksum for a file or memory buffer represented by
     * the specified InputStream object. Creates a temporary file with the
     * name pattern sqlunit-lob-*.dat as a side effect if the InputStream
     * supplied to it is anything other than a FileInputStream. This is 
     * useful in case you have an error and you want to compare with diff
     * or cmp.
     * @param istream a File or Memory buffer containing data to be digested.
     * @return the MD5 Checksum for the data.
     * @exception SQLUnitException if there was a problem generating MD5.
     */
    public static String getMD5CheckSum(final InputStream istream)
            throws SQLUnitException {
        LOG.debug("getMD5CheckSum(InputStream)");
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] clearbytes = DigestUtils.readBytesFromStream(istream);
            byte[] digest = md.digest(clearbytes);
            StringBuffer buffer = new StringBuffer("md5:");
            for (int i = 0; i < digest.length; i++) {
                String hex = Integer.toHexString(digest[i] & HEX_FF);
                if (hex.length() < 2) { hex = '0' + hex; }
                buffer.append(hex);
            }
            writeTempFile(clearbytes);
            istream.close();
            return buffer.toString();
        } catch (Exception e) {
            throw new SQLUnitException(IErrorCodes.GENERIC_ERROR,
                new String[] {"System", e.getClass().getName(), 
                e.getMessage()}, e);
        }
    }

    /**
     * Takes a String and does the right thing to convert it to MD5 according
     * to the following rules. If it is prefixed with md5:, it assumes that
     * this String has already been digested and returns it. If it is prefixed
     * with file:, it assumes that it is a file whose contents are to be 
     * digested. If it is prefixed with obj:, it assumes that this is an
     * object representatation and also lets it through. In all other cases,
     * it will try to digest the String and return the MD5 checksum for it.
     * @param str the String to convert to MD5.
     * @return an MD5 digest String.
     * @exception SQLUnitException if there was a problem with the conversion.
     */
    public static String getMD5CheckSum(final String str) 
            throws SQLUnitException {
        LOG.debug("getMD5CheckSum(String)");
        if (str.startsWith("md5:") || str.startsWith("obj:")) {
            // do nothing
            return str;
        }
        if (str.startsWith(FILE_PREFIX)) {
            // this is a file, digest contents and return
            try {
                return DigestUtils.getMD5CheckSum(
                    new FileInputStream(str.substring(FILE_PREFIX_LENGTH)));
            } catch (FileNotFoundException e) {
                throw new SQLUnitException(IErrorCodes.ERROR_DIGESTING_DATA,
                   new String[] {"file " + str.substring(FILE_PREFIX_LENGTH)
                   + " not found"}, e);
            }
        } else {
            // this is a string, digest the string and return
            return DigestUtils.getMD5CheckSum(
                new ByteArrayInputStream(str.getBytes()));
        }
    }

    /**
     * Returns the String representation of an Object from an InputStream.
     * @param istream the InputStream to read.
     * @return the String representation of the object.
     * @exception SQLUnitException if there was a problem with the conversion.
     */
    public static String getStringifiedObject(final InputStream istream) 
            throws SQLUnitException {
        LOG.debug("getStringifiedObject(InputStream)");
        try {
            byte[] bytes = DigestUtils.readBytesFromStream(istream);
            if (isSerializedJavaObject(bytes)) {
                return getStringifiedObject(bytes);
            } else {
                throw new SQLUnitException(IErrorCodes.ERROR_DIGESTING_DATA,
                    new String[] {"InputStream does not contain a serialized "
                    + "Java Object"});
            }
        } catch (Exception e) {
            throw new SQLUnitException(IErrorCodes.ERROR_DIGESTING_DATA,
                new String[] {e.getMessage()}, e);
        }
    }

    /**
     * Returns the String representation of an Object which is supplied to
     * the method as a byte array.
     * @param bytes the bytes representing the Object to stringify.
     * @return the String representation of the object.
     * @exception SQLUnitException if there was a problem with the conversion.
     */
    public static String getStringifiedObject(final byte[] bytes) 
            throws SQLUnitException {
        LOG.debug("getStringifiedObject(bytes)");
        try {
            ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(bytes));
            Object obj = ois.readObject();
            ois.close();
            return "obj:" + obj.toString();
        } catch (Exception e) {
            throw new SQLUnitException(IErrorCodes.ERROR_DIGESTING_DATA,
                new String[] {e.getMessage()}, e);
        }
    }

    /**
     * Reads an InputStream and returns an array of bytes.
     * @param istream the InputStream to read.
     * @return a byte array.
     * @exception SQLUnitException if there was a problem with reading.
     */
    public static byte[] readBytesFromStream(final InputStream istream) 
            throws SQLUnitException {
        LOG.debug("readBytesFromStream(InputStream)");
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[BYTES_IN_KB];
            int len = 0;
            while ((len = istream.read(buf, 0, BYTES_IN_KB)) != -1) {
                bos.write(buf, 0, len);
            }
            istream.close();
            byte[] bytes = bos.toByteArray();
            return bytes;
        } catch (IOException e) {
            throw new SQLUnitException(IErrorCodes.GENERIC_ERROR,
                new String[] {"I/O", e.getClass().getName(),
                e.getMessage()}, e);
        }
    }

    /**
     * Converts an object into an array of bytes.
     * @param obj the Object to get bytes for.
     * @return an array of bytes.
     * @exception SQLUnitException if there was a problem generating bytecode.
     */
    public static byte[] getByteCodeForObject(final Object obj) 
            throws SQLUnitException {
        LOG.debug("getByteCodeForObject(" + (obj == null
            ? "NULL" : obj.getClass().getName()) + ")");
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            byte[] bytes = bos.toByteArray();
            return bytes;
        } catch (Exception e) {
            throw new SQLUnitException(IErrorCodes.GENERIC_ERROR,
                new String[] {"I/O", e.getClass().getName(),
                "Could not convert Object " + (obj == null
                ? "NULL" : obj.getClass().getName()) + " to byte array"}, e);
        }
    }

    /**
     * Checks the byte header for the presence of the magic number "aced"
     * and returns true if it finds it. This is not totally reliable and
     * depends on the convention that this is the magic number for a 
     * serialized Java object.
     * @param bytes the byte code to check.
     * @return true if the magic string is found, else false.
     */
    public static boolean isSerializedJavaObject(final byte[] bytes) {
        LOG.debug("isSerializedJavaObject(bytes)");
        if (bytes == null || bytes.length < 2) { return false; }
        StringBuffer check = new StringBuffer();
        for (int i = 0; i < 2; i++) {
            String hex = Integer.toHexString(bytes[i] & HEX_FF);
            check.append(hex);
        }
        return (("aced").equals(check.toString()));
    }

    /**
     * Writes the bytes into a temporary file and adds the mapping of 
     * the the location where it was written to the actual file name
     * in the SymbolTable.
     * @param bytes the bytes to write to the temporary file.
     * @exception Exception if there was a problem writing the file.
     */
    public static void writeTempFile(final byte[] bytes) throws Exception {
        File tempFile = File.createTempFile("sqlunit-lob-", ".dat");
        String tempFileName = tempFile.getCanonicalPath();
        LOG.debug("writeTempFile(bytes) into file:" + tempFileName);
        FileOutputStream tstream = new FileOutputStream(tempFileName);
        tstream.write(bytes);
        tstream.flush();
        tstream.close();
        String key = SymbolTable.getCurrentResultKey();
        SymbolTable.setValue(key, tempFileName);
    }

    /**
     * Gets the temporary file mappings from the Symbol Table.
     * @return a Map of locations to the temporary file names.
     */
    public static Map getTempFileMappings() {
        Map tempFileMappings = new HashMap();
        Iterator iter = SymbolTable.getSymbols();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            if (key.startsWith("${result[") && key.endsWith("]}")) {
                tempFileMappings.put(key, SymbolTable.getValue(key));
            }
        }
        return tempFileMappings;
    }
}
