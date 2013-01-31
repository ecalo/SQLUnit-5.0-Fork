/*
 * $Id: ArrayType.java,v 1.7 2004/09/30 17:27:55 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/types/ArrayType.java,v $
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
import net.sourceforge.sqlunit.IType;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.SymbolTable;
import net.sourceforge.sqlunit.TypeFactory;
import net.sourceforge.sqlunit.TypeMapper;

import java.sql.Array;
import java.sql.SQLException;

/**
 * Models an Array type.
 * @author Ralph Brendler (rbrendler@users.sourceforge.net)
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.7 $
 * @sqlunit.type name="ArrayType" input="no" output="yes" sortable="no"
 *  wraps="java.sql.Array"
 * @sqlunit.typename name="ARRAY" server="Any"
 */
public class ArrayType extends UnsupportedType {
    
    /**
     * Formats an Array object into a String representation of the Array.
     * @param obj an Object to be converted to the IType interface.
     * @return the String representation of the object.
     * @exception SQLUnitException if the formatting failed.
     */
    protected String format(final Object obj) throws SQLUnitException {
        if (!(obj instanceof Array)) {
            throw new SQLUnitException(IErrorCodes.UNSUPPORTED_DATATYPE_FORMAT,
                new String[] {SymbolTable.getCurrentResultKey(),
                (obj == null ? "NULL" : obj.getClass().getName()),
                getName(), (new Integer(getId())).toString()});
        }
        Array array = (Array) obj;
        StringBuffer buf = new StringBuffer("[");
        try {
            int baseTypeId = array.getBaseType();
            Object[] elements = (Object[]) array.getArray();
            for (int i = 0; i < elements.length; i++) {
                if (i > 0) { buf.append(","); }
                IType type = TypeFactory.getInstance(baseTypeId);
                type.setId(baseTypeId);
                type.setName(TypeMapper.findNameById(baseTypeId));
                type.setValue(elements[i]);
                buf.append(type.getValue().toString());
            }
            buf.append("]");
        } catch (SQLException e) {
            throw new SQLUnitException(IErrorCodes.GENERIC_ERROR,
                new String[] {"SQL", e.getClass().getName(), 
                e.getMessage()}, e);
        }
        return buf.toString();
    }
}
