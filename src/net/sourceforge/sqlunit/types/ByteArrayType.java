/*
 * $Id: ByteArrayType.java,v 1.11 2004/12/02 21:22:30 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/types/ByteArrayType.java,v $
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

/**
 * Models a Binary type. This is similar to BinaryType but will read from
 * a supplied byte array instead of a Binary InputStream. It will convert 
 * to a MD5 digest and can be specified either as a MD5 digest (prefixed by
 * md5:) or a file name (prefixed by file:).
 * @author Ralph Brendler (rbrendler@users.sourceforge.net)
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.11 $
 * @sqlunit.type name="ByteArrayType" input="No" output="Yes" sortable="No"
 *  wraps="byte[]"
 * @sqlunit.typename name="BINARY" server="Any"
 * @sqlunit.typename name="VARBINARY" server="Any"
 */
public class ByteArrayType extends UnsupportedType {
    
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
     * Formats a ByteArray into its string representation.
     * @param obj an Object to be converted to the IType interface.
     * @return the String representation of the object.
     * @exception SQLUnitException if the formatting failed.
     */
    protected String format(final Object obj) throws SQLUnitException {
        boolean hasJavaObjectSupport = (("on").equals(
            SymbolTable.getValue(SymbolTable.JAVA_OBJECT_SUPPORT)));
        if (hasJavaObjectSupport) {
            if (!(obj instanceof byte[])) {
                return "obj:" + (obj == null ? "NULL" : obj.toString());
            } else {
                throw new SQLUnitException(
                    IErrorCodes.UNSUPPORTED_DATATYPE_FORMAT,
                    new String[] {SymbolTable.getCurrentResultKey(),
                    (obj == null ? "NULL" : obj.getClass().getName()),
                    getName(), (new Integer(getId())).toString()});
            }
        }
        byte[] bytes;
        if (obj instanceof byte[]) {
            bytes = (byte[]) obj;
        } else {
            bytes = DigestUtils.getByteCodeForObject(obj);
        }
        String digest = DigestUtils.getMD5CheckSum(
            new ByteArrayInputStream(bytes));
        return digest;
    }
}
