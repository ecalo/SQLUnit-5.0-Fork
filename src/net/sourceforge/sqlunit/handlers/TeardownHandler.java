/*
 * $Id: TeardownHandler.java,v 1.6 2005/03/26 07:36:32 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/TeardownHandler.java,v $
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

import java.util.Iterator;
import java.util.List;

import net.sourceforge.sqlunit.HandlerFactory;
import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.SQLUnitException;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * The TeardownHandler class handles the instructions in the teardown tag
 * of the XML input file.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.6 $
 * @sqlunit.parent name="sqlunit" ref="sqlunit"
 * @sqlunit.element name="teardown"
 *  description="The teardown tag specifies SQL operations that need to be
 *  carried out after all the tests in the XML file are done. If SQLUnit
 *  detects a test failure, it will skip the remaining tests and invoke the
 *  teardown operations before exiting. Typical tasks include dropping the
 *  test database or deleting data from test tables."
 *  syntax="(((sql)*, (foreach)*) | (include)*)"
 * @sqlunit.child name="sql"
 *  description="Specifies the SQL statements to be executed, with or without
 *  replaceable parameters, that should executed on teardown."
 *  required="No"
 *  ref="sql"
 * @sqlunit.child name="foreach"
 *  description="Contains bulk SQL statements such as SQL DELETE statements,
 *  which will be executed from within a foreach tag."
 *  required="No"
 *  ref="foreach"
 * @sqlunit.child name="include"
 *  description="Specifies an external file which contains the SQL statements
 *  to be executed on teardown."
 *  required="No"
 *  ref="include"
 * @sqlunit.example name="Dropping some test tables on teardown"
 *  description="
 *  <teardown>{\n}
 *  {\t}<sql><stmt>drop table testdata</stmt></sql>{\n}
 *  {\t}<sql><stmt>drop table testdata2</stmt></sql>{\n}
 *  </teardown>
 *  "
 */
public class TeardownHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(TeardownHandler.class);

    /**
     * Processes the Teardown tag and executes the sql statements specified 
     * in the tag.
     * @param elTeardown the JDOM Element representing the teardown element.
     * @return no object is expected.
     * @exception Exception if there was a problem running the teardown.
     */
    public final Object process(final Element elTeardown) throws Exception {
        LOG.debug(">> process(elTeardown)");
        if (elTeardown == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"teardown"});
        }
        try {
            // there can either be zero or more <sql> or <include> not both
            List elIncludes = elTeardown.getChildren("include");
            if (elIncludes.size() > 0) {
                Iterator initer = elIncludes.iterator();
                while (initer.hasNext()) {
                    Element elInclude = (Element) initer.next();
                    IHandler includeHandler = 
                        HandlerFactory.getInstance(elInclude.getName());
                    includeHandler.process(elInclude);
                }
            } else {
                List elChildren = elTeardown.getChildren();
                Iterator chiter = elChildren.iterator();
                while (chiter.hasNext()) {
                    Element elChild = (Element) chiter.next();
                    IHandler handler = 
                        HandlerFactory.getInstance(elChild.getName());
                    handler.process(elChild);
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }
}
