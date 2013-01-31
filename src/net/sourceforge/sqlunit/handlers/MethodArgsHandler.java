/*
 * $Id: MethodArgsHandler.java,v 1.4 2004/09/28 19:15:45 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/MethodArgsHandler.java,v $
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

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * The MethodArgsHandler parses the JDOM Element methodArgs and processes 
 * it to return an array of Arg objects.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.4 $
 * @sqlunit.parent name="methodinvoker" ref="methodinvoker"
 * @sqlunit.parent name="dynamicsql" ref="dynamicsql"
 * @sqlunit.element name="methodArgs"
 *  description="A wrapper tag to group together arguments to a method call."
 *  syntax="((arg)*)"
 * @sqlunit.child name="arg"
 *  description="A tag that specifies the name, type and value of the method
 *  argument."
 *  required="One or more arg elements are required."
 *  ref="arg"
 * @sqlunit.example name="A methodArgs tag with different argument types"
 *  description="
 *  <methodArgs>{\n}
 *  {\t}<arg name=\"m3\" type=\"java.lang.Integer\" value=\"-2147483648\" />{\n}
 *  {\t}<arg name=\"m7\" type=\"java.lang.Double\" value=\"2.2\" />{\n}
 *  {\t}<arg name=\"m8\" type=\"java.lang.String\" value=\"Some text\" />{\n}
 *  </methodArgs>
 *  "
 */
public class MethodArgsHandler extends ConstructorArgsHandler {

    private static final Logger LOG = Logger.getLogger(MethodArgsHandler.class);

    /**
     * Processes the JDOM Element representing the constructorArgs tag 
     * and returns an array of Arg objects.
     * @param elMethArgs the JDOM Element representing the methodArgs tag.
     * @return an array of Arg objects. Client needs to cast appropriately.
     * @exception Exception if something went wrong processing the tag.
     */
    public final Object process(final Element elMethArgs) throws Exception {
        LOG.debug(">> process(elMethArgs)");
        if (elMethArgs == null) { 
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"methodArgs"});
        }
        return getArguments(elMethArgs.getChildren());
    }
}
