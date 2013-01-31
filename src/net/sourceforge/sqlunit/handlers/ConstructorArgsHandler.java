/*
 * $Id: ConstructorArgsHandler.java,v 1.4 2004/09/28 19:15:45 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/ConstructorArgsHandler.java,v $
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

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.util.List;

/**
 * The ConstructorArgsHandler parses the JDOM Element constructorArgs and
 * processes it to return an array of Arg objects.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.4 $
 * @sqlunit.parent name="dynamicsql" ref="dynamicsql"
 * @sqlunit.parent name="methodinvoker" ref="methodinvoker"
 * @sqlunit.element name="constructorArgs"
 *  description="Collection of arg elements that need to be passed to the
 *  constructor of a specified class whose method needs to be invoked
 *  by SQLUnit"
 *  syntax="((arg)*)"
 * @sqlunit.child name="arg"
 *  description="Specifies an individual argument"
 *  required="One or more"
 *  ref="arg"
 * @sqlunit.example name="Example of a constructorArgs tag"
 *  description="
 *  <constructorArgs>{\n}
 *  {\t}<arg name=\"c1\" type=\"java.lang.Byte\" value=\"-128\" />{\n}
 *  {\t}<arg name=\"c2\" type=\"java.lang.Short\" value=\"-32768\" />{\n}
 *  {\t}<arg name=\"c3\" type=\"java.lang.Integer\" value=\"-2147483648\" />{\n}
 *  {\t}<arg name=\"c5\" type=\"java.lang.Character\" value=\"a\" />{\n}
 *  {\t}<arg name=\"c6\" type=\"java.lang.Float\" value=\"1.1\" />{\n}
 *  {\t}<arg name=\"c7\" type=\"java.lang.Double\" value=\"2.2\" />{\n}
 *  {\t}<arg name=\"c8\" type=\"java.lang.String\" value=\"Some text\" />{\n}
 *  </constructorArgs>
 *  "
 */
public class ConstructorArgsHandler implements IHandler {

    private static final Logger LOG = 
        Logger.getLogger(ConstructorArgsHandler.class);

    /**
     * Processes the JDOM Element representing the constructorArgs tag 
     * and returns an array of Arg objects.
     * @param elCtorArgs the JDOM Element representing the constructorArgs tag.
     * @return an array of Arg objects. Client needs to cast appropriately.
     * @exception Exception if something went wrong processing the tag.
     */
    public Object process(final Element elCtorArgs) throws Exception {
        LOG.debug(">> process(elCtorArgs)");
        if (elCtorArgs == null) { 
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"constructorArgs"});
        }
        return getArguments(elCtorArgs.getChildren());
    }

    /**
     * Returns an array of Arg objects. This will be reused in a subclass
     * which is why this is factored out here.
     * @param elArgs a List of arg elements.
     * @return an array of Arg objects.
     * @exception Exception if something went wrong processing the list.
     */
    protected final Arg[] getArguments(final List elArgs) throws Exception {
        Arg[] args = new Arg[elArgs.size()];
        for (int i = 0; i < args.length; i++) {
            Element elArg = (Element) elArgs.get(i);
            IHandler argHandler = HandlerFactory.getInstance(elArg.getName());
            args[i] = (Arg) argHandler.process(elArg);
        }
        return args;
    }
}
