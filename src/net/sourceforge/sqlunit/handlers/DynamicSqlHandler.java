/*
 * $Id: DynamicSqlHandler.java,v 1.6 2004/09/28 19:15:45 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/DynamicSqlHandler.java,v $
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
import net.sourceforge.sqlunit.beans.Arg;
import net.sourceforge.sqlunit.beans.Param;
import net.sourceforge.sqlunit.utils.MethodInvocationUtils;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * The DynamicSqlHandler class processes the contents of an dynamicSql tag in
 * the input XML file.
 * @author Mario Laureti (mlaureti@users.sourceforge.net)
 * @version $Revision: 1.6 $
 * @sqlunit.parent name="test" ref="test"
 * @sqlunit.element name="dynamicsql"
 *  description="The dynamicsql tag invokes a named method in a named class
 *  in order to generate a SQL string dynamically, which is then fed to 
 *  SQLUnit for testing against a known result. This was included because
 *  it is often necessary to test dynamic SQL that is built in response to
 *  known conditions, and the results are known, but the actual SQL may
 *  not be of interest."
 *  syntax="((constructorArgs)*, (methodArgs)*)"
 * @sqlunit.attrib name="static"
 *  description="If set to true, specifies that the method is a static method"
 *  required="No, default is false (non-static method)"
 * @sqlunit.attrib name="class"
 *  description="Specifies the full class name where the method is defined"
 *  required="Yes"
 * @sqlunit.attrib name="method"
 *  description="Specifies the method name to invoke"
 *  required="Yes"
 * @sqlunit.child name="constructorArgs"
 *  description="Wraps a set of arg elements which specify the arguments
 *  the constructor for the class will take"
 *  required="Not required if this is a static method, or if the class has
 *  a default null constructor"
 *  ref="constructorArgs"
 * @sqlunit.child name="methodArgs"
 *  description="Wraps a set of arg elements which specify the arguments
 *  to pass to the method"
 *  required="Not required if the method does not take arguments"
 *  ref="methodArgs"
 * @sqlunit.example name="A dynamicsql tag that invokes a static method"
 *  description="
 *  <dynamicsql class=\"net.sourceforge.sqlunit.utils.TestArgument\"{\n}
 *  {\t}static=\"true\" method=\"getGeneratedOracleSqlString\">{\n}
 *  </dynamicsql>
 *  "
 */
public class DynamicSqlHandler extends SqlHandler {

    private static final Logger LOG = Logger.getLogger(DynamicSqlHandler.class);

    /**
     * Makes an entry for the given object in the Symbol table. The variable
     * name is checked for correct formatting, ie ${varname}.
     * @param elDynamicSql the JDOM Element representing the set directive.
     * @return an Object, null in this case.
     * @exception Exception if something went wrong with the set operation.
     */
    public final Object process(final Element elDynamicSql) throws Exception {
        LOG.debug(">> process(elDynamicSql)");
        if (elDynamicSql == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL, 
                new String[] {"dynamicsql"});
        }
        String connectionId = XMLUtils.getAttributeValue(
            elDynamicSql, "connection-id");
        String className = XMLUtils.getAttributeValue(
            elDynamicSql, "class");
        String methodName = XMLUtils.getAttributeValue(
            elDynamicSql, "method");
        boolean isStaticMethod = Boolean.valueOf(
            XMLUtils.getAttributeValue(elDynamicSql, "static")).booleanValue();
        Element elCtorArgs = elDynamicSql.getChild("constructorArgs");
        Arg[] ctorArgs = new Arg[0];
        if (elCtorArgs != null) {
            IHandler ctorArgsHandler = HandlerFactory.getInstance(
                elCtorArgs.getName());
            ctorArgs = (Arg[]) ctorArgsHandler.process(elCtorArgs);
        }
        Element elMethArgs = elDynamicSql.getChild("methodArgs");
        Arg[] methodArgs = new Arg[0];
        if (elMethArgs != null) {
            IHandler methArgsHandler = HandlerFactory.getInstance(
                elMethArgs.getName());
            methodArgs = (Arg[]) methArgsHandler.process(elMethArgs);
        }
        Object objectInstance = null;
        String sqlString = null;
        if (isStaticMethod) {
            // Handle static method call with and without parameters
            if (methodArgs.length > 0) {
                sqlString = (String) MethodInvocationUtils.invoke(
                    className, methodName, 
                    MethodInvocationUtils.getArguments(methodArgs),
                    MethodInvocationUtils.getArgumentTypes(methodArgs));
            } else {
                sqlString = (String) MethodInvocationUtils.invoke(
                    className, methodName);
            }
        } else {
            // Handle constructor instantiation with and without parameters
            if (ctorArgs.length > 0) {
                objectInstance = MethodInvocationUtils.getInstance(className, 
                    MethodInvocationUtils.getArguments(ctorArgs),
                    MethodInvocationUtils.getArgumentTypes(ctorArgs));
            } else {
                objectInstance = MethodInvocationUtils.getInstance(className);
            }
            // Handle method call with and without parameters
            Object result = null;
            if (methodArgs.length > 0) {
                sqlString = (String) MethodInvocationUtils.invoke(
                    objectInstance, methodName, 
                    MethodInvocationUtils.getArguments(methodArgs),
                    MethodInvocationUtils.getArgumentTypes(methodArgs));
            } else {
                sqlString = (String) MethodInvocationUtils.invoke(
                    objectInstance, methodName);
            }
        }
        return executeSQL(connectionId, sqlString, new Param[0]);
    }
}
