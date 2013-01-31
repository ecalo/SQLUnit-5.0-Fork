/*
 * $Id: MethodInvocationUtils.java,v 1.3 2004/12/03 21:17:37 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/utils/MethodInvocationUtils.java,v $
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
package net.sourceforge.sqlunit.utils;

import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.beans.Arg;

import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * The MethodInvocationUtils class allow you to instantiate classes and execute
 * method using reflection.
 * @author Mario Laureti (mlaureti@users.sourceforge.net)
 * @version $Revision: 1.3 $
 */
public final class MethodInvocationUtils {

    private static final Logger LOG = Logger.getLogger(
        MethodInvocationUtils.class);
    private static final String TYPE_SUFFIX = ".TYPE";
    private static final int TYPE_SUFFIX_LENGTH = TYPE_SUFFIX.length();

    /**
     * Private Constructor. Cannot be instantiated.
     */
    private MethodInvocationUtils() {
        // private constructor, cannot instantiate
    }

    /**
     * Returns the constructor method for the specified class and the 
     * parameters.
     * @param className name of the class for which to build the Constructor.
     * @param ctorArgTypes an array of constructor parameter types.
     * @return a Constructor object for the given class and arguments.
     * @exception SQLUnitException if there was a problem.
     */
    public static Constructor getConstructor(final String className, 
        final Class[] ctorArgTypes) throws SQLUnitException {
        LOG.debug(">> getConstructor(" + className + ",ctorArgs)");
        try {
            return Class.forName(className).getConstructor(ctorArgTypes);
        } catch (Exception e) {
            throw new SQLUnitException(IErrorCodes.METHOD_INVOCATION_ERROR,
                new String[] {className, "getConstructor()"}, e);
        }
    }

    /**
     * Get an instance of the object whose classname and initialization 
     * parameters are supplied.
     * @param className the name of the class to instantiate.
     * @param ctorArgs an array of constructor argument values.
     * @param ctorArgTypes an array of constructor argument types.
     * @return an object of the specified class.
     * @exception SQLUnitException if there was a problem with instantiating.
     */
    public static Object getInstance(final String className, 
            final Object[] ctorArgs, final Class[] ctorArgTypes) 
            throws SQLUnitException {
        LOG.debug(">> getInstance(" + className + "ctorArgs,ctorArgTypes)");
        try {
            if (ctorArgs == null) {
                return Class.forName(className).newInstance();
            } else {
                Constructor ctor = getConstructor(className, ctorArgTypes);
                return ctor.newInstance(ctorArgs);
            }
        } catch (Exception e) {
            throw new SQLUnitException(IErrorCodes.METHOD_INVOCATION_ERROR,
                new String[] {className, "getInstance()"}, e);
        }
    }

    /**
     * Convenience method to instantiate a class without constructor args.
     * @param className the name of the class to instantiate.
     * @return an object of the specified class.
     * @exception SQLUnitException if there was a problem with instantiating.
     */
    public static Object getInstance(final String className) 
            throws SQLUnitException {
        LOG.debug(">> getInstance(" + className + ")");
        return getInstance(className, null, null);
    }

    /**
     * Invokes the specified method on the specified object and returns the
     * result.
     * @param anObject the object to invoke the method on.
     * @param methodName the method name to invoke.
     * @param arguments an array of method argument values.
     * @param argumentsType an array of method argument types.
     * @return the Object returned as a result of the invoke.
     * @exception SQLUnitException if there was a problem with invoking method.
     */
    public static Object invoke(final Object anObject, final String methodName, 
            final Object[] arguments, final Class[] argumentsType) 
            throws SQLUnitException {
        LOG.debug(">> invoke(" + anObject.getClass().getName() + ","
            + methodName + ",methodArgs,methodArgTypes)");
        try {
            Method method = anObject.getClass().getDeclaredMethod(methodName, 
                argumentsType);
            return method.invoke(anObject, arguments);
        } catch (Exception e) {
            throw new SQLUnitException(IErrorCodes.METHOD_INVOCATION_ERROR,
                new String[] {anObject.getClass().getName(), methodName}, e);
        }
    }

    /**
     * Convenience method to invoke a method without arguments.
     * @param anObject the object to invoke the method on.
     * @param methodName the method name to invoke.
     * @return the Object returned as a result of the invoke.
     * @exception SQLUnitException if there was a problem with the invocation.
     */
    public static Object invoke(final Object anObject, final String methodName)
            throws SQLUnitException {
        LOG.debug(">> invoke(" + anObject.getClass().getName() + ","
            + methodName + ")");
        return invoke(anObject, methodName, null, null);
    }

    /**
     * Invokes a named method on a named class with the specified arguments.
     * @param className the name of the class to invoke the method on.
     * @param staticMethodName the name of the static method to invoke.
     * @param arguments an array of Object arguments to the method.
     * @param argumentsType an array of Class argument types.
     * @return the Object that is a result of the method invocation.
     * @exception SQLUnitException if there was a problem.
     */
    public static Object invoke(final String className, 
            final String staticMethodName, final Object[] arguments, 
            final Class[] argumentsType) throws SQLUnitException {
        LOG.debug(">> invoke(" + className + "," + staticMethodName
            + ",methodArgs,methodArgTypes)");
        try {
            Method method = Class.forName(className).
                getDeclaredMethod(staticMethodName, argumentsType);
            return method.invoke(null, arguments);
        } catch (Exception e) {
            throw new SQLUnitException(IErrorCodes.METHOD_INVOCATION_ERROR,
                new String[] {className, staticMethodName}, e);
        }
    }

    /**
     * Convenience method to invoke a named method on a named class with
     * no arguments.
     * @param className the name of the class to invoke the method on.
     * @param staticMethodName the name of the static method to invoke.
     * @return an Object that is the result of the method invocation.
     * @exception SQLUnitException if there was a problem.
     */
    public static Object invoke(final String className, 
            final String staticMethodName) throws SQLUnitException {
        LOG.debug(">> invoke(" + className + "," + staticMethodName + ")");
        return invoke(className, staticMethodName, null, null);
    }

    /**
     * Convenience method to extract the argument values into an array of
     * Objects to pass to Method.invoke().
     * @param args an Array of Arg objects.
     * @return an array of Objects.
     * @exception SQLUnitException if there was a problem.
     */
    public static Object[] getArguments(final Arg[] args) 
            throws SQLUnitException {
        LOG.debug(">> getArguments(args)");
        Object[] arguments = new Object[args.length];
        for (int i = 0; i < arguments.length; i++) {
            String type = args[i].getType();
            if (type.equals("java.lang.Character")
                    || type.equals("java.lang.Character.TYPE")) {
                // Exception: The constructor of Character must be char
                arguments[i] = MethodInvocationUtils.getInstance(type,
                    new Object[] {new Character(args[i].getValue().charAt(0))},
                    new Class[] {Character.class});
            } else {
                if (type.endsWith(TYPE_SUFFIX)) {
                    arguments[i] = MethodInvocationUtils.getInstance(
                        type.substring(0, (type.length() - TYPE_SUFFIX_LENGTH)),
                        new Object[] {args[i].getValue()},
                        new Class[] {String.class});
                } else {
                    arguments[i] = MethodInvocationUtils.getInstance(type, 
                        new Object[] {args[i].getValue()},
                        new Class[] {String.class});
                }
            }
        }
        return arguments;
    }

    /**
     * Convenience method to extract the argument types into a Class array
     * to pass to Method.invoke().
     * @param args an array of Arg objects.
     * @return an array of Class objects.
     * @exception SQLUnitException if there was a problem.
     */
    public static Class[] getArgumentTypes(final Arg[] args) 
            throws SQLUnitException {
        LOG.debug(">> getArgumentTypes(args)");
        Class[] argumentTypes = new Class[args.length];
        for (int i = 0; i < argumentTypes.length; i++) {
            String type = args[i].getType();
            if (type.endsWith(".TYPE")) {
                // primitives
                if (type.equals("java.lang.Boolean.TYPE")) {
                    argumentTypes[i] = java.lang.Boolean.TYPE;
                } else if (type.equals("java.lang.Byte.TYPE")) {
                    argumentTypes[i] = java.lang.Byte.TYPE;
                } else if (type.equals("java.lang.Character.TYPE")) {
                    argumentTypes[i] = java.lang.Character.TYPE;
                } else if (type.equals("java.lang.Double.TYPE")) {
                    argumentTypes[i] = java.lang.Double.TYPE;
                } else if (type.equals("java.lang.Float.TYPE")) {
                    argumentTypes[i] = java.lang.Float.TYPE;
                } else if (type.equals("java.lang.Integer.TYPE")) {
                    argumentTypes[i] = java.lang.Integer.TYPE;
                } else if (type.equals("java.lang.Long.TYPE")) {
                    argumentTypes[i] = java.lang.Long.TYPE;
                } else if (type.equals("java.lang.Short.TYPE")) {
                    argumentTypes[i] = java.lang.Short.TYPE;
                }
            } else {
                // objects
                if (type.equals("java.lang.Boolean")) {
                    argumentTypes[i] = java.lang.Boolean.class;
                } else if (type.equals("java.lang.Byte")) {
                    argumentTypes[i] = java.lang.Byte.class;
                } else if (type.equals("java.lang.Character")) {
                    argumentTypes[i] = java.lang.Character.class;
                } else if (type.equals("java.lang.Double")) {
                    argumentTypes[i] = java.lang.Double.class;
                } else if (type.equals("java.lang.Float")) {
                    argumentTypes[i] = java.lang.Float.class;
                } else if (type.equals("java.lang.Integer")) {
                    argumentTypes[i] = java.lang.Integer.class;
                } else if (type.equals("java.lang.Long")) {
                    argumentTypes[i] = java.lang.Long.class;
                } else if (type.equals("java.lang.Short")) {
                    argumentTypes[i] = java.lang.Short.class;
                } else {
                    argumentTypes[i] = java.lang.String.class;
                }
            }
        }
        return argumentTypes;
    }

    /**
     * Climbs the exception tree to find the application specific message
     * that was thrown by the method. The method must throw an unchained
     * exception for it to be computed in this way.
     * @param th the Throwable to look at.
     * @return the Exception message associated with the root cause.
     */
    public static String getRootCauseMessage(final Throwable th) {
        Throwable inThrowable = th;
        Throwable cause = null;
        String message = null;
        while ((cause = inThrowable.getCause()) != null) {
            message = cause.getMessage();
            inThrowable = cause;
        }
        return message;
    }
}
