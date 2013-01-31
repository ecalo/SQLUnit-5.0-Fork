/*
 * $Id: BlobType.java,v 1.9 2004/09/30 17:27:55 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/types/BlobType.java,v $
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
import java.sql.Blob;
import java.sql.SQLException;

/**
 * Models a BLOB type.
 * @author Ralph Brendler (rbrendler@users.sourceforge.net)
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.9 $
 * @sqlunit.type name="BlobType" input="no" output="yes" sortable="no"
 *  wraps="java.sql.Blob"
 * @sqlunit.typename name="BLOB" server="Any"
 */
public class BlobType extends UnsupportedType {
    
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
     * Formats a BLOB into a String representation of the BLOB.
     * @param obj an Object to be converted to the IType interface.
     * @return the String representation of the object.
     * @exception SQLUnitException if the formatting failed.
     */
    protected String format(final Object obj) throws SQLUnitException {
        if (!(obj instanceof Blob)) {
            throw new SQLUnitException(IErrorCodes.UNSUPPORTED_DATATYPE_FORMAT,
                new String[] {SymbolTable.getCurrentResultKey(),
                (obj == null ? "NULL" : obj.getClass().getName()),
                getName(), (new Integer(getId())).toString()});
        }
        Blob blob = (Blob) obj;
        try {
            byte[] bb = blob.getBytes(1, (int) blob.length());
            return DigestUtils.getMD5CheckSum(new ByteArrayInputStream(bb));
        } catch (SQLException e) {
            throw new SQLUnitException(IErrorCodes.GENERIC_ERROR, 
                new String[] {"SQL", e.getClass().getName(),
                e.getMessage()}, e);
        }
    }
}
