/*
 * $Id: SubDefHandler.java,v 1.2 2005/04/02 22:02:25 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/SubDefHandler.java,v $
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
import net.sourceforge.sqlunit.beans.Param;
import net.sourceforge.sqlunit.beans.SubRoutine;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.util.Iterator;
import java.util.List;

/**
 * The SubDefHandler processes a JDOM Element representing a subroutine
 * definition. A subroutine definition is a complete definition of a
 * stored procedure or SQL call, except that some parameter values may
 * be left missing. These values should be assigned by the sub tag.
 * @author Victor Alekseev (krocodl@users.sourceforge.net)
 * @version $Revision: 1.2 $
 * @sqlunit.parent name="setup" ref="setup"
 * @sqlunit.element name="subdef"
 *  description="The subdef tag is used to define a stored procedure or
 *  SQL call. All parameters need to be defined, but some parameters may
 *  not have values associated with them. These are plugged in when the
 *  actual call is made using the sub tag. This allows test writers to
 *  save keystrokes and work at a higher level of abstraction than building
 *  the tests manually with stored procedures or SQL calls each time."
 *  syntax="((param)*)"
 * @sqlunit.attrib name="name"
 *  description="The name of the subroutine definition. This will be used
 *  by the sub tag to specify the definition to look up."
 *  required="Yes"
 * @sqlunit.attrib name="query"
 *  description="The SQL or stored procedure call with replaceable parameters
 *  specified as in the sql or call tags."
 *  required="Yes"
 * @sqlunit.attrib name="description"
 *  description="A user-friendly description of the subroutine definition.
 *  Used for documentation"
 *  required="No"
 * @sqlunit.attrib name="type"
 *  description="Specifies whether to treat the SQL as a stored procedure
 *  or SQL call when called. Valid values are sql and call."
 *  required="No, defaults to sql"
 * @sqlunit.child name="param"
 *  description="Specifies zero or more parameters that are supplied with
 *  the subroutine definition. All parameters that the call requires should
 *  be supplied, although some or all the values may remain unspecified.
 *  Unspecified values are denoted with a single '?' character in the
 *  text of the param element."
 *  required="Zero or more parameters can be specified"
 *  ref="param"
 * @sqlunit.example name="A sample subroutine definition"
 *  description="
 *  <subdef name=\"UpsertStreetDef\"{\n}
 *  {\t}query=\"{? = pk_edit_street.upsert_street(?,?)}\"{\n}
 *  {\t}description=\"Definition of Upsert Street\">{\n}
 *  {\t}{\t}<param id=\"1\" type=\"INTEGER\" name=\"rc\" inout=\"out\">{\n}
 *  {t}{\t}{\t}${streetId}{\n}
 *  {\t}{\t}</param>{\n}
 *  {\t}{\t}<param id=\"2\" type=\"VARCHAR\" name=\"street\">?</param>{\n}
 *  {\t}{\t}<param id=\"3\" type=\"VARCHAR\" name=\"city\">SFO</param>{\n}
 *  </subdef>
 *  "
 */
public class SubDefHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(SubDefHandler.class);

    /**
     * Partially defines a stored procedure or SQL call and stores it into
     * the symbol table for later execution.
     * @param elSubDef the JDOM Element representing the subdef directive.
     * @return an Object, null in this case.
     * @exception Exception if something went wrong with the subdef operation.
     */
    public final Object process(final Element elSubDef) throws Exception {
        LOG.debug(">> process(elSubDef)");
        if (elSubDef == null) {
             throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                 new String[] {"subdef"});
        }
        SubRoutine subroutine = new SubRoutine();
        subroutine.setName(XMLUtils.getAttributeValue(elSubDef, "name"));
        subroutine.setQuery(XMLUtils.getAttributeValue(elSubDef, "query"));
        subroutine.setDescription(
            XMLUtils.getAttributeValue(elSubDef, "description"));
        subroutine.setType(XMLUtils.getAttributeValue(elSubDef, "type"));
        List params = elSubDef.getChildren("param");
        for (Iterator it = params.iterator(); it.hasNext();) {
             Element elParam = (Element) it.next();
             IHandler paramHandler =
                 HandlerFactory.getInstance(elParam.getName());
             Param param = (Param) paramHandler.process(elParam);
             // make sure we got a name for each param, since the DTD
             // does not (and should not) force you to supply a name
             if (param.getName() == null) {
                 throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                     new String[] {"subdef.param[" + param.getId() + "].name"});
             }
             subroutine.addParam(param);
        }
        SymbolTable.setObject("${subdef." + subroutine.getName() + "}",
            subroutine);
        return null;
    }
}
