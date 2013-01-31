/*
 * $Id: ArgHandler.java,v 1.6 2004/09/28 19:15:44 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/ArgHandler.java,v $
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
import net.sourceforge.sqlunit.beans.Arg;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * The ArgHandler takes the JDOM Element representing the arg tag and 
 * converts it to a Arg object.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.6 $
 * @sqlunit.parent name="jndi" ref="jndi"
 * @sqlunit.parent name="match" ref="match"
 * @sqlunit.parent name="constructorArgs" ref="constructorArgs"
 * @sqlunit.parent name="methodArgs" ref="methodArgs"
 * @sqlunit.element name="arg"
 *  description="Carries argument values and types for the containing element"
 *  syntax="(EMPTY)"
 * @sqlunit.attrib name="name"
 *  description="The name of the argument to be used"
 *  required="Yes"
 * @sqlunit.attrib name="value"
 *  description="The value of the argument"
 *  required="Yes"
 * @sqlunit.attrib name="type"
 *  description="The type of the argument"
 *  required="No, but required in methodArgs and constructorArgs"
 * @sqlunit.example name="Specifying args for jndi"
 *  description="
 *  <arg name=\"java.naming.factory.initial\"{\n}
 *  {\t}value=\"org.jnp.interfaces.NamingFactory\" />{\n}
 *  "
 * @sqlunit.example name="Specifying args for methodArgs"
 *  description="
 *  <methodArgs>{\n}
 *  {\t}<arg name=\"col1\" type=\"java.lang.String\" value=\"ABC\" />{\n}
 *  </methodArgs>{\n}
 *  "
 */
public class ArgHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(ArgHandler.class);

    /**
     * Processes the JDOM Element representing the arg tag returns the
     * Arg object.
     * @param elArg the JDOM Element representing the arg tag.
     * @return a Arg object. Client needs to cast to a Arg.
     * @exception Exception if something went wrong processing the arg.
     */
    public final Object process(final Element elArg) throws Exception {
        LOG.debug(">> process(elArg)");
        if (elArg == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"arg"});
        }
        Arg arg = new Arg();
        arg.setName(XMLUtils.getAttributeValue(elArg, "name"));
        arg.setValue(XMLUtils.getAttributeValue(elArg, "value"));
        arg.setType(XMLUtils.getAttributeValue(elArg, "type"));
        return arg;
    }
}
