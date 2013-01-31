/*
 * $Id: JndiHandler.java,v 1.4 2004/09/28 19:15:45 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/JndiHandler.java,v $
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
 * The JndiHandler parses the JDOM Element methodArgs and processes 
 * it to return an array of Arg objects.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.4 $
 * @sqlunit.parent name="connection" ref="connection"
 * @sqlunit.element name="jndi"
 *  description="A wrapper tag to group together JNDI properties that are
 *  needed to instantiate an InitialContext for the JNDI DataSource lookup."
 *  syntax="((arg)*)"
 * @sqlunit.child name="arg"
 *  description="A tag that specifies the name and value of a single
 *  JNDI property."
 *  required="One or more arg elements are required."
 *  ref="arg"
 * @sqlunit.example name="A typical JNDI tag for getting a DataSource from
 *  JBoss"
 *  description="
 *  <connection>{\n}
 *  {\t}<datasource>jdbc/myDSN</datasource>{\n}
 *  {\t}<jndi>{\n}
 *  {\t}{\t}<arg name=\"java.naming.factory.initial\"{\n}
 *  {\t}{\t}{\t}value=\"org.jnp.interfaces.NamingContextFactory\" />{\n}
 *  {\t}{\t}<arg name=\"java.naming.provider.url\" {\n}
 *  {\t}{\t}{\t}value=\"jnlp://localhost:1099\" />{\n}
 *  {\t}{\t}<arg name=\"java.naming.factory.url.pkgs\" {\n}
 *  {\t}{\t}{\t}value=\"org.jboss.naming\" />{\n}
 *  {\t}</jndi>{\n}
 *  </connection>"
 *  "
 */
public class JndiHandler extends ConstructorArgsHandler {

    private static final Logger LOG = Logger.getLogger(JndiHandler.class);

    /**
     * Processes the JDOM Element representing the constructorArgs tag 
     * and returns an array of Arg objects.
     * @param elJndi the JDOM Element representing the jndi tag.
     * @return an array of Arg objects. Client needs to cast appropriately.
     * @exception Exception if something went wrong processing the tag.
     */
    public final Object process(final Element elJndi) throws Exception {
        LOG.debug(">> process(jndi)");
        if (elJndi == null) { 
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"jndi"});
        }
        return getArguments(elJndi.getChildren());
    }
}
