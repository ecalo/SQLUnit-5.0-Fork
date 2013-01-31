/*
 * $Id: ResultSetHandler.java,v 1.9 2005/05/04 05:05:58 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/ResultSetHandler.java,v $
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
import net.sourceforge.sqlunit.beans.ResultSetBean;
import net.sourceforge.sqlunit.beans.Row;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.util.List;

/**
 * The ResultSetHandler processes a JDOM ResultSet element and returns a 
 * ResultSet bean.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.9 $
 * @sqlunit.parent name="result" ref="result"
 * @sqlunit.element name="resultset"
 *  description="Represents a resultset returned from a SQL or stored 
 *  procedure call."
 *  syntax="(row)*"
 * @sqlunit.attrib name="id"
 *  description="The resultset id"
 *  required="Yes"
 * @sqlunit.attrib name="partial"
 *  description="If set to true, indicates that rows are partially specified.
 *  Valid values are true and false. Rows will be matched based on the row's
 *  id attribute."
 *  required="No, defaults to false"
 * @sqlunit.attrib name="rowcount"
 *  description="Specifies the number of rows in the resultset. This is 
 *  specified when the test client wants SQLUnit to only match the number
 *  of rows in the resultset, rather than the contents themselves."
 *  required="No"
 * @sqlunit.attrib name="order-by"
 *  description="A comma-separated list of column ids on which the rows
 *  in the resultset should be sorted. By default, both the resultset
 *  returned from the SQL or stored procedure call and the resultset 
 *  that is specified will be sorted before comparing. The default sorting
 *  is by all the specified columns in the order in which they appear. The
 *  order-by attribute should be used only if the default sort needs to be
 *  overriden, for example, when the SQL or stored procedure returns ordered
 *  data. The sort order specified is ASCENDING, unless the column id is
 *  prefixed with a negative sign, which will cause it to be sorted in 
 *  DESCENDING order. Setting order-by to NONE will turn off auto-sorting."
 *  required="No"
 * @sqlunit.child name="row"
 *  description="A resultset can have zero or more row elements"
 *  required="No"
 *  ref="row"
 * @sqlunit.example name="A simple resultset tag containing a single row"
 *  description="
 *  <resultset id=\"1\">{\n}
 *  {\t}<row id=\"1\">{\n}
 *  {\t}{\t}<col id=\"1\" name=\"col1\" type=\"INTEGER\">1</col>{\n}
 *  {\t}</row>{\n}
 *  </resultset>
 *  "
 */
public class ResultSetHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(ResultSetHandler.class);

    /**
     * Processes a JDOM Element representing a database resultset.
     * @param elResultSet the JDOM Element to use.
     * @return a populated ResultSet object. Caller must cast.
     * @exception Exception if there was a problem creating the ResultSet object.
     */
    public final Object process(final Element elResultSet) throws Exception {
        LOG.debug(">> process(elResultSet)");
        if (elResultSet == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL, 
                new String[] {"resultset"});
        }
        ResultSetBean resultset = new ResultSetBean();
        resultset.setId(XMLUtils.getAttributeValue(elResultSet, "id"));
        resultset.setPartial(XMLUtils.getAttributeValue(
            elResultSet, "partial"));
        int rowcount = -1;
        try {
            rowcount = Integer.parseInt(
                XMLUtils.getAttributeValue(elResultSet, "rowcount"));
        } catch (Exception e) {
            //:IGNORE: reset to default
        }
        // if rowcount specified, dont bother building the rows
        if (rowcount > -1) { 
            resultset.setRowCount(rowcount); 
            return resultset;
        }
        List elRows = elResultSet.getChildren("row");
        Row[] rows = new Row[elRows.size()];
        for (int i = 0; i < rows.length; i++) {
            Element elRow = (Element) elRows.get(i);
            IHandler rowHandler = HandlerFactory.getInstance(elRow.getName());
            rows[i] = (Row) rowHandler.process(elRow);
        }
        resultset.setRows(rows);
        // check if there is a specific order-by attribute
        String orderBy = XMLUtils.getAttributeValue(elResultSet, "order-by");
        if (orderBy != null && orderBy.trim().length() > 0) {
            resultset.setSortBy(orderBy);
        }
        return resultset;
    }
}
