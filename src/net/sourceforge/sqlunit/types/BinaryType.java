/*
 * $Id: BinaryType.java,v 1.15 2004/12/02 21:22:30 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/types/BinaryType.java,v $
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
package net.sourceforge.sqlunit.types;

import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.SymbolTable;
import net.sourceforge.sqlunit.utils.DigestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Models long binary text. This is identical to the ByteArrayType except
 * it reads from a BinaryStream InputStream object returned in the ResultSet.
 * It will digest the output of toString() using MD5. Results for this
 * type can be provided as an MD5 digest string (prefixed by md5:) or as
 * a file name to compare with (when the file name is prefixed with file:).
 * @author Ralph Brendler (rbrendler@users.sourceforge.net)
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.15 $
 * @sqlunit.type name="BinaryType" input="No" output="Yes" sortable="No"
 *  wraps="java.io.InputStream"
 * @sqlunit.typename name="BINARY" server="Any"
 * @sqlunit.typename name="LONGVARBINARY" server="Any"
 */
public class BinaryType extends UnsupportedType {
    
    /**
     * Defines behavior that is activated when the passed in Object is
     * a String.
     * @param obj the String to convert.
     * @return a converted String.
     * @exception SQLUnitException if there was a problem.
     */
    protected String formatString(final String obj) 
            throws SQLUnitException {
        return DigestUtils.getMD5CheckSum(obj);
    }

    /**
     * Converts the Binary data to its String representation.
     * @param obj an Object to be converted to the IType interface.
     * @return the String representation of the object.
     * @exception SQLUnitException if the formatting failed.
     */
    protected String format(final Object obj) throws SQLUnitException {
        // it must be an inputstream
        if (!(obj instanceof InputStream)) {
            throw new SQLUnitException(
                IErrorCodes.UNSUPPORTED_DATATYPE_FORMAT,
                new String[] {SymbolTable.getCurrentResultKey(),
                (obj == null ? "NULL" : obj.getClass().getName()),
                getName(), (new Integer(getId())).toString()});
        }
        boolean hasJavaObjectSupport = (("on").equals(
            SymbolTable.getValue(SymbolTable.JAVA_OBJECT_SUPPORT)));
        String digest;
        InputStream istream = (InputStream) obj;
        if (hasJavaObjectSupport) {
            byte[] bytesFromStream = DigestUtils.readBytesFromStream(istream);
            if (DigestUtils.isSerializedJavaObject(bytesFromStream)) {
                digest = DigestUtils.getStringifiedObject(bytesFromStream);
            } else {
                digest = DigestUtils.getMD5CheckSum(new ByteArrayInputStream(
                    bytesFromStream));
            }
        } else {
            digest = DigestUtils.getMD5CheckSum(istream);
        }
        return digest;
    }
}
