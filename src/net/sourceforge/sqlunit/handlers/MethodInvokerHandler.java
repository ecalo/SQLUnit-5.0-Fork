/*
 * $Id: MethodInvokerHandler.java,v 1.6 2004/09/28 19:15:45 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/MethodInvokerHandler.java,v $
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
import net.sourceforge.sqlunit.beans.DatabaseResult;
import net.sourceforge.sqlunit.utils.MethodInvocationUtils;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * The MethodInvokerHandler class processes the contents of an methodInvoker
 * tag in the input XML file. It runs the specified method in the specified
 * class and returns an empty DatabaseResult if the invocation was succesful.
 * If the invocation was not succesful, it will raise an exception.
 * @author Mario Laureti (mlaureti@users.sourceforge.net)
 * @version $Revision: 1.6 $
 * @sqlunit.parent name="test" ref="test"
 * @sqlunit.element name="methodinvoker"
 *  description="The methodinvoker tag allows the caller to invoke methods in
 *  Java code and check to see if they complete without exception. These 
 *  methods are generally, but need not be, JDBC specific. Any objects that
 *  are returned from the methods are ignored. The tag only verifies that
 *  the method ran successfully, and if not, it threw an expected exception."
 *  syntax="((constructorArgs)*, (methodArgs)*)"
 * @sqlunit.attrib name="static"
 *  description="If set to true, specifies that the method being invoked is
 *  static. Valid values are true and false."
 *  required="No, default is false."
 * @sqlunit.attrib name="class"
 *  description="Specifies the full class name of the class in which the 
 *  method is defined."
 *  required="Yes"
 * @sqlunit.attrib name="method"
 *  description="Specifies the method name to invoke."
 *  required="Yes"
 * @sqlunit.child name="constructorArgs"
 *  description="Wraps a set of arg elements which specify the arguments,
 *  if any, that the constructor will need."
 *  required="Not required for static methods, or if the class has a default
 *  no-args constructor."
 *  ref="constructorArgs"
 * @sqlunit.child name="methodArgs"
 *  description="Wraps a set of arg elements which specify the arguments,
 *  if any, that the method will need."
 *  required="Not required if the method takes no arguments."
 *  ref="methodArgs"
 * @sqlunit.example name="A simple methodinvoker tag without method arguments"
 *  description="
 *  <methodinvoker class=\"net.sourceforge.sqlunit.utils.TestArgument\"{\n}
 *  {\t}{\t}method=\"toString\" >{\n}
 *  {\t}<constructorArgs>{\n}
 *  {\t}{\t}<arg name=\"c7\" type=\"java.lang.Double.TYPE\" value=\"0.2\" />{\n}
 *  {\t}</constructorArgs>{\n}
 *  </methodinvoker>
 *  "
 */
public class MethodInvokerHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(SetHandler.class);

    /**
     * Process the JDOM Element representing the MethodInvoker and return
     * an empty DatabaseResult object.
     * @param elMethodInvoker the JDOM Element representing the methodInvoker
     * directive.
     * @return an Object, null in this case.
     * @exception Exception if something went wrong with the operation.
     */
    public final Object process(final Element elMethodInvoker) 
            throws Exception {
        LOG.debug(">> process(elMethodInvoker)");
        if (elMethodInvoker == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL, 
                new String[] {"methodinvoker"});
        }
        String className = XMLUtils.getAttributeValue(elMethodInvoker, "class");
        String methodName = XMLUtils.getAttributeValue(
            elMethodInvoker, "method");
        boolean isStaticMethod = Boolean.valueOf(
            XMLUtils.getAttributeValue(
            elMethodInvoker, "static")).booleanValue();
        Element elCtorArgs = elMethodInvoker.getChild("constructorArgs");
        Arg[] ctorArgs = new Arg[0];
        if (elCtorArgs != null) {
            IHandler ctorArgsHandler = HandlerFactory.getInstance(
                elCtorArgs.getName());
            ctorArgs = (Arg[]) ctorArgsHandler.process(elCtorArgs);
        }
        Element elMethArgs = elMethodInvoker.getChild("methodArgs");
        Arg[] methodArgs = new Arg[0];
        if (elMethArgs != null) {
            IHandler methArgsHandler = HandlerFactory.getInstance(
                elMethArgs.getName());
            methodArgs = (Arg[]) methArgsHandler.process(elMethArgs);
        }
        DatabaseResult dbResult = new DatabaseResult();
        try {
            // Handle static method call with and without parameters
            if (isStaticMethod) {
                if (methodArgs.length > 0) {
                    Object result = MethodInvocationUtils.invoke(
                        className, methodName, 
                        MethodInvocationUtils.getArguments(methodArgs),
                        MethodInvocationUtils.getArgumentTypes(methodArgs));
                } else {
                    Object result = MethodInvocationUtils.invoke(
                        className, methodName);
                }
            } else {
                // Handle constructor instantiation with and without parameters
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
                Object result = null;
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
        } catch (Throwable th) {
            Throwable cause = th.getCause();
            dbResult.resetAsException(null, 
                MethodInvocationUtils.getRootCauseMessage(th));
        }
        return dbResult;
    }
}
