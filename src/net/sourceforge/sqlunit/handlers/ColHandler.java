/*
 * $Id: ColHandler.java,v 1.8 2004/09/28 19:15:45 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/ColHandler.java,v $
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

import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.IType;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.TypeFactory;
import net.sourceforge.sqlunit.beans.Col;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * The ColHandler processes a JDOM Col element and returns a Col bean.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.8 $
 * @sqlunit.parent name="row" ref="row"
 * @sqlunit.element name="col"
 *  description="Represents a column of a database table returned from
 *  a SQL or stored procedure call."
 *  syntax="(BODY)"
 * @sqlunit.attrib name="id"
 *  description="The column id"
 *  required="Yes"
 * @sqlunit.attrib name="name"
 *  description="The column name"
 *  required="No"
 * @sqlunit.attrib name="type"
 *  description="The XML name of the data type for the column"
 *  required="Yes"
 * @sqlunit.example name="A simple column tag"
 *  description="<col id=\"1\" name=\"col1\" type=\"INTEGER\">1</col>"
 */
public class ColHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(ColHandler.class);

    /**
     * Processes a JDOM Element representing a database column.
     * @param elCol the JDOM Element to use.
     * @return a populated Col object. Caller must cast.
     * @exception Exception if there was a problem creating the Col object.
     */
    public final Object process(final Element elCol) throws Exception {
        LOG.debug(">> process(elCol)");
        if (elCol == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL, 
                new String[] {"col"});
        }
        Col col = new Col();
        col.setId(XMLUtils.getAttributeValue(elCol, "id"));
        col.setName(XMLUtils.getAttributeValue(elCol, "name"));
        col.setType(XMLUtils.getAttributeValue(elCol, "type"));
        // we dont want to use the variable here, if there is a variable
        // here in the expected result, then we will use SymbolTable.update()
        // to update the values anyway.
        IType colType = TypeFactory.getInstance(col.getType());
        col.setValue(colType.toString(elCol.getText()));
        return col;
    }
}
