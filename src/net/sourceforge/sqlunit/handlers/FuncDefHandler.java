/*
 * $Id: FuncDefHandler.java,v 1.1 2005/01/27 01:40:22 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/FuncDefHandler.java,v $
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
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.SymbolTable;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * The FuncDefHandler processes a Function Definition JDOM Element.
 * @author Victor Alekseev (krocodl@users.sourceforge.net)
 * @version $Revision: 1.1 $
 * @sqlunit.parent name="setup" ref="setup"
 * @sqlunit.element name="funcdef"
 *  description="The funcdef tag allows the declaration of an SQL call
 *  or stored procedure which takes upto 10 variable input parameters and 
 *  return a single result as its output (one row with one column). The
 *  funcdef can later be executed within a func tag and the result of
 *  the call can be retrieved within ${name_of_func_tag}.
 *  syntax="(EMPTY)"
 * @sqlunit.attrib name="name"
 *  description="The name of the function definition. This will be used
 *  to call the definition from the func tag."
 *  required="Yes"
 * @sqlunit.attrib name="query"
 *  description="The SQL query with numbered replaceable parameters (see
 *  example below) which will be called to return the result."
 *  required="Yes"
 * @sqlunit.attrib name="description"
 *  description="User Documentation for the function definition."
 *  required="No"
 * @sqlunit.example name="Defining call to MySQL built-in function"
 *  description="
 *  <funcdef name=\"databasedef\" query=\"select DATABASE()\"{\n}
 *  {\t}{\t}description=\"Returns the current database name\" />
 *  "
 * @sqlunit.example name="Defining call to a custom function"
 *  description="
 *  <funcdef name=\"customdef\"{\n}
 *  {\t}query=\"select myCol from myTable where myCol1 = ${0}\"{\n}
 *  {\t}description=\"Defines a custom function\" />
 *  "
 */
public class FuncDefHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(FuncDefHandler.class);

    /**
     * Sets a function declaration and stores it in the symbol table for
     * later execution. Later execution is triggered off using the func
     * element.
     * @param elFuncDef the JDOM Element representing the funcdef directive.
     * @return an Object, null in this case.
     * @exception Exception if something went wrong with the funcdef operation.
     */
    public final Object process(final Element elFuncDef) throws Exception {
        LOG.debug(">> process(elFuncDef)");
        if (elFuncDef == null) {
             throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                 new String[] {"funcdef"});
        }
        String name = XMLUtils.getAttributeValue(elFuncDef, "name");
        String query = XMLUtils.getAttributeValue(elFuncDef, "query");
        SymbolTable.setValue("${funcdef." + name + "}", query);
        return null;
    }
}
