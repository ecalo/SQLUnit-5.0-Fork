/*
 * $Id: TestingMethodsGenerator.java,v 1.3 2004/10/05 20:13:04 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/TestingMethodsGenerator.java,v $
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
package net.sourceforge.sqlunit.test;

/**
 * This class provides dummy methods of various kinds to test the 
 * MethodInvokerHandler and DynamicSqlHandler functionality.
 * permission bitmap and loads these into the database.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.3 $
 */
public class TestingMethodsGenerator {

    private String cArg;

    /**
     * Default ctor.
     */
    public TestingMethodsGenerator() {
        // empty constructor
    }

    /**
     * Non-default ctor that takes a single String argument.
     * @param cArg the constructor argument.
     */
    public TestingMethodsGenerator(final String cArg) {
        this.cArg = cArg;
    }

    /**
     * An example of a static method without arguments. Always returns
     * the String "aSimpleResult" corresponding to a mock procedure name.
     * @return the String "aSimpleResult"
     */
    public static final String staticMethodWithoutArgs() {
        return "aSimpleResult";
    }

    /**
     * An example of a static method with arguments. Can be used to call
     * a mock procedure which ends with the string "SimpleResult".
     * @param anArg an argument to the method.
     * @return the value of anArg prefixed to the String "SimpleResult".
     */
    public static final String staticMethodWithArgs(final String anArg) {
        return anArg + "SimpleResult";
    }

    /**
     * An example of a non-static method without any arguments and with 
     * a null constructor for the class. Always returns "aSimpleResult".
     * @return the String "aSimpleResult".
     */
    public final String nonStaticWithoutArgsAndNullConstructor() {
        return "aSimpleResult";
    }

    /**
     * An example of a non-static method with a single String argument and
     * with a null constructor for the class. Can be used to call a mock
     * procedure whose name is anArg followed by the String "SimpleResult".
     * @param anArg an argument to the method.
     * @return the value of anArg prefixed to the String "SimpleResult".
     */
    public final String nonStaticWithArgsAndNullConstructor(
            final String anArg) {
        return anArg + "SimpleResult";
    }

    /**
     * An example of a non-static method without arguments and a non-null
     * constructor for the class. Can be used to call a mock procedure 
     * whose name is the constructor argument prefixed to "SimpleResult".
     * @return the value of cArg prefixed to "SimpleResult".
     */
    public final String nonStaticWithoutArgsWithConstructor() {
        return cArg + "SimpleResult";
    }

    /**
     * An example of a non-static method with arguments and a non-null
     * constructor for the class. Can be used to call a mock procedure
     * whose name is anArg prefixed to the String "SimpleResult". Can 
     * also be used to throw an exception if the constructor argument
     * is identical to the method argument.
     * @param anArg an argument to the method.
     * @return the value of anArg prefixed to the String "SimpleResult".
     * @exception Exception if the cArg is the same as anArg.
     */
    public final String nonStaticWithArgsWithConstructor(final String anArg) 
            throws Exception {
        if (cArg.equals(anArg)) {
            throw new Exception("Application Specific Exception!");
        }
        return anArg + "SimpleResult";
    }
}
