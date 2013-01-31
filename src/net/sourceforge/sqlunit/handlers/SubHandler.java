/*
 * $Id: SubHandler.java,v 1.2 2005/01/27 01:45:12 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/SubHandler.java,v $
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
import net.sourceforge.sqlunit.SymbolTable;
import net.sourceforge.sqlunit.beans.DatabaseResult;
import net.sourceforge.sqlunit.beans.Param;
import net.sourceforge.sqlunit.beans.SubParam;
import net.sourceforge.sqlunit.beans.SubRoutine;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The SubHandler is called from within the sub tag. It looks up the
 * named subdef tag and overrides parameter values that are supplied 
 * in the sub tag. SQLUnit will rebuild the resolved Sub Element into
 * either an Sql or Call Element and then delegate to the SqlHandler
 * or CallHandler to build a DatabaseResult object.
 * @author Victor Alekseev (krocodl@users.sourceforge.net)
 * @version $Revision: 1.2 $
 * @sqlunit.parent name="test" ref="test"
 * @sqlunit.element name="sub"
 *  description="The sub tag calls a partially defined stored procedure
 *  or SQL call in a preceding subdef tag."
 * @sqlunit.attrib name="lookup"
 *  description="Specifies the name of the subdef element to look up."
 *  required="Yes"
 * @sqlunit.attrib name="connection-id"
 *  description="The connection-id to use or use the default connection
 *  if not specified."
 *  required="No"
 * @sqlunit.child name="subparam"
 *  description="Specifies a value for the parameter for the stored 
 *  procedure or SQL call that was declared in the subdef tag. Lookup
 *  is by parameter name."
 *  required="Zero or more"
 *  ref="param"
 * @sqlunit.example name="Calling a sub with parameters"
 *  description="
 *  <sub lookup=\"UpsertStreet\">{\n}
 *  {\t}<subparam name=\"street\" value=\"Market Street\" />{\n}
 *  </sub>
 *  "
 */
public class SubHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(SubHandler.class);

    /**
     * Looks up the SQL string for the corresponding SubDef, replaces
     * with parameters if defined, runs it, and returns a single result.
     * @param elSub the JDOM Element representing the func element.
     * @return the value of the result of the SQL execution as a String.
     * @exception Exception if something went wrong with the func operation.
     */
    public final Object process(final Element elSub) throws Exception {
        LOG.debug(">> process(elSub)");
        if (elSub == null) {
             throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                 new String[] {"sub"});
        }
        String lookup = XMLUtils.getAttributeValue(elSub, "lookup");
        String connectionId =
            XMLUtils.getAttributeValue(elSub, "connection-id");
        // look up the subdef in the symbol table
        SubRoutine subroutine = (SubRoutine) SymbolTable.getObject(
            "${subdef." + lookup + "}");
        if (subroutine == null) {
            throw new SQLUnitException(IErrorCodes.UNDEFINED_SYMBOL,
                new String[] {"${subdef." + lookup + "}"});
        }
        // specify or override parameter values
        Map paramMap = subroutine.getParamMap();
        List subParams = elSub.getChildren("subparam");
        for (Iterator it = subParams.iterator(); it.hasNext();) {
            Element elSubParam = (Element) it.next();
            IHandler subParamHandler =
                HandlerFactory.getInstance(elSubParam.getName());
            SubParam subParam = (SubParam) subParamHandler.process(elSubParam);
            subroutine.updateParam(subParam.getName(), subParam.getValue());
        }
        // check if any params are still unspecified
        for (Iterator it = subroutine.getParamMap().values().iterator();
                it.hasNext();) {
            Param param = (Param) it.next();
            if (("?").equals(param.getValue())) {
                throw new SQLUnitException(IErrorCodes.UNDEFINED_PARAM,
                    new String[] {param.getName()});
            }
        }
        // convert to either a Sql or Call element
        Element elNew = new Element("sql");
        if (subroutine.getType().equals("call")) {
            elNew.setName("call");
        }
        if (connectionId != null) {
            elNew.setAttribute("connection-id", connectionId);
        }
        Element elStmt = new Element("stmt");
        elStmt.setText(subroutine.getQuery());
        elNew.addContent(elStmt);
        for (Iterator it = paramMap.values().iterator(); it.hasNext();) {
            Param param = (Param) it.next();
            Element elParam = new Element("param");
            elParam.setAttribute("id", param.getId());
            elParam.setAttribute("name", param.getName());
            elParam.setAttribute("type", param.getType());
            elParam.setAttribute("is-null", param.getIsNull());
            elParam.setAttribute("inout", param.getInOut());
            elParam.setText(param.getValue());
            elNew.addContent(elParam);
        }
        // call the Sql or Call Handler, process and return the
        // DatabaseResult object
        IHandler newHandler = HandlerFactory.getInstance(elNew.getName());
        return (DatabaseResult) newHandler.process(elNew);
    }
}
