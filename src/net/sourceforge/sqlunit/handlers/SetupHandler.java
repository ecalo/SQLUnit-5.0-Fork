/*
 * $Id: SetupHandler.java,v 1.6 2005/07/08 06:46:54 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/SetupHandler.java,v $
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

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.util.Iterator;
import java.util.List;

/**
 * The SetupHandler class handles the setup tag of the XML input file.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.6 $
 * @sqlunit.parent name="sqlunit" ref="sqlunit"
 * @sqlunit.element name="setup"
 *  description="The setup tag specifies operations that need to be carried
 *  out once before all the tests defined in the XML file. Typical tasks 
 *  may include dropping and recreating the test database and tables, or
 *  deleting and inserting test data into the tables. A setup operation can
 *  be specified either by zero or more nested sql tags or by zero or more
 *  nested include tag which specify an external file containing the
 *  setup SQL to execute. It can also contain set tags to setup variables
 *  to be used later for the test, or foreach tags which can be used to
 *  execute a single SQL multiple times, such as insert SQL statements."
 *  syntax="((set)*, (((sql)*, (foreach)*) | (include)*))"
 * @sqlunit.child name="set"
 *  description="Sets initial values for SQLUnit variables."
 *  required="No"
 *  ref="set"
 * @sqlunit.child name="funcdef"
 *  description="Defines a SQL or stored procedure call that can be used
 *  like a function call (single string result)."
 *  required="No"
 *  ref="funcdef"
 * @sqlunit.child name="subdef"
 *  description="Defines the structure of a SQL or stored procedure call.
 *  Values of parameters can be supplied or overriden in the actual sub call."
 *  required="No"
 *  ref="subdef"
 * @sqlunit.child name="sql"
 *  description="Specifies the SQL statement to be executed with or without
 *  replaceable parameters."
 *  required="No"
 *  ref="sql"
 * @sqlunit.child name="foreach"
 *  description="Contains bulk SQL statements, such as inserts which are
 *  executed within the foreach tag."
 *  required="No"
 *  ref="foreach"
 * @sqlunit.child name="include"
 *  description="Specifies an external file which contains the SQL statements
 *  to execute during the setup phase."
 *  required="No"
 *  ref="include"
 * @sqlunit.example name="A simple setup to delete rows from a table"
 *  description="
 *  <setup>{\n}
 *  {\t}<sql><stmt>delete from foo where 1=1</stmt></sql>{\n}
 *  </setup>
 *  "
 */
public class SetupHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(SetupHandler.class);

    /**
     * Processes the Setup tag and carries out the sql statements specified
     * inside the tag.
     * @param elSetup the JDOM Element representing the setup element.
     * @return a null, but needed to satisfy the interface.
     * @exception Exception if there was a problem running the setup.
     */
    public final Object process(final Element elSetup) throws Exception {
        LOG.debug(">> process(elSetup)");
        if (elSetup == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"setup"});
        }
        // the first set of child elements are: (set)*, (funcdef)*, (subdef)*
        String[] setupChildTags = new String[] {"set", "funcdef", "subdef"};
        for (int i = 0; i < setupChildTags.length; i++) {
            List elChildList = elSetup.getChildren(setupChildTags[i]);
            for (Iterator it = elChildList.iterator(); it.hasNext();) {
                Element elChild = (Element) it.next();
                IHandler childHandler =
                    HandlerFactory.getInstance(elChild.getName());
                childHandler.process(elChild);
            }
        }
        // the next set of child elements are ((sql)*, (foreach)*)|(include)*
        // so we check for includes and process them if they are there, else
        // we try to process the sql and foreach tags.
        try {
            List elIncludes = elSetup.getChildren("include");
            if (elIncludes.size() > 0) {
                for (Iterator it = elIncludes.iterator(); it.hasNext();) {
                    Element elInclude = (Element) it.next();
                    IHandler includeHandler = 
                        HandlerFactory.getInstance(elInclude.getName());
                    includeHandler.process(elInclude);
                }
            } else {
                setupChildTags = new String[] {"sql", "foreach"};
                for (int i = 0; i < setupChildTags.length; i++) {
                    List elChildList = elSetup.getChildren(setupChildTags[i]);
                    for (Iterator it = elChildList.iterator(); it.hasNext();) {
                        Element elChild = (Element) it.next();
                        IHandler handler = 
                            HandlerFactory.getInstance(elChild.getName());
                        handler.process(elChild);
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }
}
