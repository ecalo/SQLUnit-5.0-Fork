/*
 * $Id: FuncHandler.java,v 1.2 2005/06/10 05:52:42 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/FuncHandler.java,v $
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
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.SymbolTable;
import net.sourceforge.sqlunit.beans.DatabaseResult;
import net.sourceforge.sqlunit.beans.Param;
import net.sourceforge.sqlunit.parsers.SymbolParser;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The FuncHandler looks up the named definition from the symbol table
 * and runs it after resolving the query, then returns a result. It 
 * is used to populate the SQLUnit SymbolTable with values for later
 * test runs. It cannot be used directly as inputs for a test. It is 
 * similar to the set tag with an embedded query but more compact. Unlike
 * the set tag, which can be called within the setup tag or a per-test
 * prepare tag, the func tag is called standalone before a test to populate
 * a variable in the scope.
 * @author Victor Alekseev (krocodl@users.sourceforge.net)
 * @version $Revision: 1.2 $
 * @sqlunit.parent name="sqlunit" ref="sqlunit"
 * @sqlunit.element name="func"
 *  description="The func tag will replace the positional parameters
 *  in the SQL string defined in the corresponding funcdef tag, then
 *  run it and return a result."
 *  syntax="(EMPTY)"
 * @sqlunit.attrib name="name"
 *  description="The name of the func instance. The value returned from
 *  the execution of the func element will be available in the SymbolTable
 *  as ${func.name_of_func} for later tests in the same suite."
 *  required="Yes"
 * @sqlunit.attrib name="lookup"
 *  description="The name of the funcdef element to look up."
 *  required="Yes"
 * @sqlunit.attrib name="connection-id"
 *  description="The id of the connection to use, when multiple connections
 *  are defined. If not specified, it will use the default connection."
 *  required="No"
 * @sqlunit.attrib name="param0-9"
 *  description="The value for ${0}-${9} in the SQL string, if it exists.
 *  Note that if the variable is a string, you must supply the enclosing
 *  single quotes to indicate that. This type of query has no indication
 *  of the type of parameter being passed to it."
 *  required="No"
 * @sqlunit.child name="skip"
 *  description="Indicates whether the func should be skipped or not."
 *  required="No"
 *  ref="skip"
 * @sqlunit.child name="classifiers"
 *  description="Allows user to provide classification criteria for the func
 *  which SQLUnit can use to decide whether it should run the func or not
 *  based on criteria provided to match the classifier."
 *  required="No"
 *  ref="classifiers"
 * @sqlunit.example name="Calling a definition without parameters"
 *  description="<func name=\"databasename\" lookup=\"databasedef\" />"
 * @sqlunit.example name="Calling a custom function"
 *  description="<func name=\"mycol\" lookup=\"customdef\" param=\"234\" />"
 */
public class FuncHandler extends SqlHandler {

    private static final Logger LOG = Logger.getLogger(FuncHandler.class);

    private static final int MAX_NUM_PARAMS = 10;

    /**
     * Looks up the SQL string for the corresponding FuncDef, replaces
     * with parameters if defined, runs it, and returns a single result.
     * @param elFunc the JDOM Element representing the func element.
     * @return the value of the result of the SQL execution as a String.
     * @exception Exception if something went wrong with the func operation.
     */
    public final Object process(final Element elFunc) throws Exception {
        LOG.debug(">> process(elFunc)");
        if (elFunc == null) {
             throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                 new String[] {"func"});
        }
        String name = XMLUtils.getAttributeValue(elFunc, "name");
        String lookup = XMLUtils.getAttributeValue(elFunc, "lookup");
        String originalSql = SymbolTable.getValue("${funcdef." + lookup + "}");
        if (originalSql == null) {
            throw new SQLUnitException(IErrorCodes.UNDEFINED_SYMBOL,
                new String[] {"${funcdef." + lookup + "}"});
        }
        String connectionId = 
            XMLUtils.getAttributeValue(elFunc, "connection-id");
        Map params = new HashMap();
        for (int i = 0; i < MAX_NUM_PARAMS; i++) {
            String key = "${" + i + "}";
            String value = elFunc.getAttributeValue("param" + i);
            params.put(key, value);
        }
        String sql = SymbolParser.parse(originalSql, params, false);
        DatabaseResult result =
            (DatabaseResult) executeSQL(connectionId, sql, new Param[0]);
        try {
            // return the value of the first col of the first row of the
            // first resultset in the first result
            SymbolTable.setValue("${" + name + "}",
                ((((((result.getResultSets())[0]).getRows())[0]).
                getCols())[0]).getValue());
        } catch (Exception e) {
            // could be NullPointer or ArrayIndexOutOfBounds
            throw new SQLUnitException(IErrorCodes.GENERIC_ERROR,
                new String[] {"SQL Func", e.getClass().getName(),
                e.getMessage()}, e);
        }
        return Boolean.TRUE;
    }
}
