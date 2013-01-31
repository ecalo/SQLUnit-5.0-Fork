/*
 * $Id: TextType.java,v 1.11 2004/09/30 17:27:55 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/types/TextType.java,v $
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

import java.io.InputStream;

/**
 * Models long character text (TEXT). The difference between this type 
 * and the standard StringType is that this is not sortable, and cannot
 * be used for input. It can be output from stored procedures, and the
 * results are digested into an MD5, which is compared to another specified
 * MD5 String, or a file, whose contents are digested using MD5 and the
 * resulting MD5 digests compared for equality. Internally, it is created
 * from an ASCII InputStream returned in the ResultSet. Results for this
 * type can be provided as an MD5 Digest (prefixed by md5:) or a file name
 * (prefixed by file:).
 * @author Ralph Brendler (rbrendler@users.sourceforge.net)
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.11 $
 * @sqlunit.type name="TextType" input="No" output="Yes" sortable="No"
 *  wraps="java.io.InputStream"
 * @sqlunit.typename name="NTEXT" server="Microsoft SQL Server"
 */
public class TextType extends UnsupportedType {
    
    /**
     * Defines behavior that is activated when the passed in Object is
     * a String.
     * @param obj the String to convert.
     * @return a converted String.
     * @exception SQLUnitException if there was a problem.
     */
    protected String formatString(final String obj) throws SQLUnitException {
        return DigestUtils.getMD5CheckSum(obj);
    }

    /**
     * Format the Text to an MD5 Digest String.
     * @param obj an Object to be converted to the IType interface.
     * @return the String representation of the object.
     * @exception SQLUnitException if the formatting failed.
     */
    protected String format(final Object obj) throws SQLUnitException {
        if (!(obj instanceof InputStream)) {
            throw new SQLUnitException(IErrorCodes.UNSUPPORTED_DATATYPE_FORMAT,
                new String[] {SymbolTable.getCurrentResultKey(),
                (obj == null ? "NULL" : obj.getClass().getName()),
                getName(), (new Integer(getId())).toString()});
        }
        String digest = DigestUtils.getMD5CheckSum((InputStream) obj);
        return digest;
    }
}
