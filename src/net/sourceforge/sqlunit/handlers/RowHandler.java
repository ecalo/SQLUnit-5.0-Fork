/*
 * $Id: RowHandler.java,v 1.6 2005/05/03 17:22:39 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/RowHandler.java,v $
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

import net.sourceforge.sqlunit.HandlerFactory;
import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.beans.Col;
import net.sourceforge.sqlunit.beans.Row;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.util.List;

/**
 * The RowHandler processes a JDOM Row element and returns a Row bean.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.6 $
 * @sqlunit.parent name="resultset" ref="resultset"
 * @sqlunit.element name="row"
 *  description="Represents a row in a database table returned from 
 *  a SQL or stored procedure call."
 *  syntax="(col)+"
 * @sqlunit.attrib name="id"
 *  description="The row id"
 *  required="Yes"
 * @sqlunit.attrib name="partial"
 *  description="If true, indicates that child cols are partially specified.
 *  Valid values are true and false. Columns will be matched based on the
 *  col element's id attribute."
 *  required="No, defaults to false"
 * @sqlunit.child name="col"
 *  description="A row can have one or more col elements defined within it."
 *  required="Yes"
 *  ref="col"
 * @sqlunit.example name="A simple row tag"
 *  description="
 *  <row id=\"1\">{\n}
 *  {\t}<col id=\"1\" name=\"col1\" type=\"INTEGER\">1</col>{\n}
 *  </row>
 *  "
 */
public class RowHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(RowHandler.class);

    /**
     * Processes a JDOM Element representing a database row.
     * @param elRow the JDOM Element to use.
     * @return a populated Row object. Caller must cast.
     * @exception Exception if there was a problem creating the Row object.
     */
    public final Object process(final Element elRow) throws Exception {
        LOG.debug(">> process(elRow)");
        if (elRow == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL, 
                new String[] {"row"});
        }
        Row row = new Row();
        row.setId(XMLUtils.getAttributeValue(elRow, "id"));
        row.setPartial(XMLUtils.getAttributeValue(elRow, "partial"));
        List elCols = elRow.getChildren("col");
        Col[] cols = new Col[elCols.size()];
        for (int i = 0; i < cols.length; i++) {
            Element elCol = (Element) elCols.get(i);
            IHandler colHandler = HandlerFactory.getInstance(elCol.getName());
            cols[i] = (Col) colHandler.process(elCol);
        }
        row.setCols(cols);
        return row;
    }
}
