/*
 * $Id: SetHandler.java,v 1.8 2004/12/06 18:44:49 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/SetHandler.java,v $
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
import net.sourceforge.sqlunit.beans.Arg;
import net.sourceforge.sqlunit.beans.DatabaseResult;
import net.sourceforge.sqlunit.utils.MethodInvocationUtils;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.util.Iterator;

/**
 * The SetHandler allows setting variables into the Symbol table.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.8 $
 * @sqlunit.parent name="setup" ref="setup"
 * @sqlunit.parent name="prepare" ref="prepare"
 * @sqlunit.element name="set"
 *  description="The set tag allows declarations of variable names and 
 *  associating them with values within the context of a single SQLUnit run.
 *  SQLUnit stores these variables and their values in an internal symbol
 *  table. Variables need not be pre-declared in order to be used. A 
 *  variable is usually created in the symbol table by being assigned the
 *  appropriate value based on context, the first time it is referenced in
 *  the XML file. Subsequent references to the variable will return the 
 *  value of the variable."
 *  syntax="((sql|call),result)?|(constructoryArgs*,methodArgs*)?"
 * @sqlunit.attrib name="name"
 *  description="Specifies the name of the SQLUnit variable. A variable must
 *  always be specified like ${varname}. Attempts to specify variables like
 *  varname or $varname will result in syntax error."
 *  required="Yes"
 * @sqlunit.attrib name="value"
 *  description="Specifies the initial value of the variable. The value 
 *  must be specified. In cases where you do not know the value, you can
 *  initialize it with a default value, or not invoke the set tag to 
 *  declare the variable. When the variable is first encountered in SQLUnit,
 *  it will implicitly create an entry for that variable in the symbol table."
 *  required="Not required if nested elements are specified."
 * @sqlunit.attrib name="static"
 *  description="Specifies whether the method call is static. Valid values
 *  are true and false."
 *  required="Required if the class and method attributes are also supplied"
 * @sqlunit.attrib name="class"
 *  description="Specifies the class name whose method has to be invoked
 *  to populate the variable"
 *  required="Only required if the method attribute is also supplied"
 * @sqlunit.attrib name="method"
 *  description="Specifies the method name in the specified class to invoke."
 *  required="Only required if the class attribute is supplied"
 * @sqlunit.child name="sql"
 *  description="Specifies a SQL statement to invoke which will populate 
 *  the variables in the result."
 *  required="Only required if there is no value attribute in the set tag.
 *  A call tag can also be specified here instead."
 *  ref="sql"
 * @sqlunit.child name="call"
 *  description="Specifies a stored procedure call to invoke which will
 *  populate the variables in the result."
 *  required="Only required if there is no value attribute in the set tag.
 *  A sql tag can also be specified instead."
 *  ref="call"
 * @sqlunit.child name="result"
 *  description="Specifies a result tag with variables in the col tag body.
 *  The variables will be populated from the values returned by the SQL 
 *  or stored procedure call."
 *  required="Only required if there is either a sql or a call tag."
 *  ref="result"
 * @sqlunit.child name="constructorArgs"
 *  description="Specifies arguments to a constructor of a non-static method
 *  if that is being used to populate the variable to set."
 *  required="Only required if we want to invoke a non-static method"
 *  ref="constructorArgs"
 * @sqlunit.child name="methodArgs"
 *  description="Specifies arguments to the method to be executed to populate
 *  the variable to be set"
 *  required="Required if we want to invoke a method to set the variable"
 *  ref="methodArgs"
 * @sqlunit.example name="Setting a variable ${var} to 14"
 *  description="
 *  <set name=\"${var}\" value=\"14\" />
 *  "
 * @sqlunit.example name="Setting variable ${myquery.col1} from SQL"
 *  description="
 *  <set name=\"${myquery}\">{\n}
 *  {\t}<sql><stmt>select col1 from mytable where col2=45</stmt></sql>{\n}
 *  {\t}<result>{\n}
 *  {\t}{\t}<resultset id=\"1\">{\n}
 *  {\t}{\t}{\t}<row id=\"1\">{\n}
 *  {\t}{\t}{\t}{\t}<col id=\"1\" name=\"c1\" type=\"INTEGER\">${col1}</col>{\n}
 *  {\t}{\t}{\t}</row>{\n}
 *  {\t}{\t}</resultset>{\n}
 *  {\t}</result>{\n}
 *  </set>
 *  "
 * @sqlunit.example name = "Setting variable ${var} from a method call return"
 *  description="
 *  <set name=\"${var}\" class=\"java.lang.System\" static=\"true\"{\n}
 *  {\t}{\t}method=\"getProperty\">{\n}
 *  {\t}<methodArgs>{\n}
 *  {\t}{\t}<arg name=\"key\" type=\"java.lang.String\"{\n}
 *  {\t}{\t}value=\"property\" />{\n}
 *  {\t}</methodArgs>{\n}
 *  </set>
 *  "
 */
public class SetHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(SetHandler.class);

    /**
     * Makes an entry for the given object in the Symbol table. The variable
     * name is checked for correct formatting, ie ${varname}.
     * @param elSet the JDOM Element representing the set directive.
     * @return an Object, null in this case.
     * @exception Exception if something went wrong with the set operation.
     */
    public final Object process(final Element elSet) throws Exception {
        LOG.debug(">> process(elSet)");
        if (elSet == null) {
             throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                 new String[] {"set"});
        }
        // no variable substitution needed here, these are going to be
        // the keys to the Symbol Table.
        String name = elSet.getAttributeValue("name");
        if (!SymbolTable.isVariableName(name)) {
            throw new SQLUnitException(IErrorCodes.INVALID_VARIABLE_NAME_FORMAT,
                new String[] {name});
        }
        String value = XMLUtils.getAttributeValue(elSet, "value");
        if (value != null) {
            // set can either have a value (scalar set) 
            SymbolTable.setValue(name, value);
        } else {
            String className = XMLUtils.getAttributeValue(elSet, "class");
            if (className != null) {
                // or a result from a method call
                String methodName = XMLUtils.getAttributeValue(
                    elSet, "method");
                if (methodName == null) {
                    throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                        new String[] {"method"});
                }
                boolean isStaticMethod = Boolean.valueOf(
                    XMLUtils.getAttributeValue(
                    elSet, "static")).booleanValue();
                Element elCtorArgs = elSet.getChild("constructorArgs");
                Arg[] ctorArgs = new Arg[0];
                if (elCtorArgs != null) {
                    IHandler ctorArgsHandler = HandlerFactory.getInstance(
                        elCtorArgs.getName());
                    ctorArgs = (Arg[]) ctorArgsHandler.process(elCtorArgs);
                }
                Element elMethArgs = elSet.getChild("methodArgs");
                Arg[] methodArgs = new Arg[0];
                if (elMethArgs != null) {
                    IHandler methArgsHandler = HandlerFactory.getInstance(
                        elMethArgs.getName());
                    methodArgs = (Arg[]) methArgsHandler.process(elMethArgs);
                }
                Object result = null;
                // Handle static method call with and without parameters
                if (isStaticMethod) {
                    if (methodArgs.length > 0) {
                        result = MethodInvocationUtils.invoke(
                            className, methodName, 
                            MethodInvocationUtils.getArguments(methodArgs),
                            MethodInvocationUtils.getArgumentTypes(methodArgs));
                    } else {
                        result = MethodInvocationUtils.invoke(
                            className, methodName);
                    }
                } else {
                    // Handle constructor instantiation with and without
                    // parameters
                    Object objectInstance = null;
                    if (ctorArgs.length > 0) {
                        objectInstance = MethodInvocationUtils.getInstance(
                            className, 
                            MethodInvocationUtils.getArguments(ctorArgs),
                            MethodInvocationUtils.getArgumentTypes(ctorArgs));
                    } else {
                        objectInstance = MethodInvocationUtils.getInstance(
                            className);
                    }
                    // Handle method call with and without parameters
                    if (methodArgs.length > 0) { 
                        result = MethodInvocationUtils.invoke(
                            objectInstance, methodName,
                            MethodInvocationUtils.getArguments(methodArgs),
                            MethodInvocationUtils.getArgumentTypes(methodArgs));
                    } else {
                        result = MethodInvocationUtils.invoke(
                            objectInstance, methodName);
                    }
                }
                if (result != null) {
                    SymbolTable.setValue(name, result.toString());
                }
            } else {
                // or an embedded sql or call and a corresponding result
                Iterator citer = elSet.getChildren().iterator();
                DatabaseResult resultCall = new DatabaseResult();
                DatabaseResult resultResult = new DatabaseResult();
                while (citer.hasNext()) {
                    Element elChild = (Element) citer.next();
                    IHandler handler = HandlerFactory.getInstance(
                        elChild.getName());
                    if (elChild.getName().equals("result")) {
                        resultResult =
                            (DatabaseResult) handler.process(elChild);
                    } else {
                        resultCall = (DatabaseResult) handler.process(elChild);
                    }
                }
                SymbolTable.setSymbols(resultResult, resultCall, name);
            }
        }
        return null;
    }
}
