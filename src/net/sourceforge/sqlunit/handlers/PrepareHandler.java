/*
 * $Id: PrepareHandler.java,v 1.3 2004/09/28 19:15:45 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/PrepareHandler.java,v $
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
 * The PrepareHandler class takes care of setting up data within a test
 * block.
 * @author Andrey Grigoriev (agrigoriev@users.sourceforge.net)
 * @version $Revision: 1.3 $
 * @sqlunit.parent name="test" ref="test"
 * @sqlunit.element name="prepare"
 *  description="The prepare tag specifies additional SQL statements that
 *  should be run before the test in whose block it appears in. This is
 *  used for specifying additional operations that need to happen on a 
 *  per-test basis, which is not covered in the setup tag."
 *  syntax="((set)*, (sql)*, (foreach)*)"
 * @sqlunit.child name="set"
 *  description="This tag sets values for SQLUnit test variables into the 
 *  SQLUnit symbol table."
 *  required="No."
 *  ref="set"
 * @sqlunit.child name="sql"
 *  description="Identifies one or more SQL statements that should be 
 *  executed as part of the per-test prepare"
 *  required="No."
 *  ref="sql"
 * @sqlunit.child name="foreach"
 *  description="Specifies a block within a foreach block which is executed
 *  as part of the prepare."
 *  required="No."
 *  ref="foreach"
 * @sqlunit.example name="A foreach tag inside a prepare tag"
 *  description="
 *  <prepare>{\n}
 *  {\t}<sql><stmt>delete from foreachtest where 1=1</stmt></sql>{\n}
 *  {\t}<foreach param=\"id\" start=\"0\" stop=\"10\" step=\"1\">{\n}
 *  {\t}{\t}<sql><stmt>insert into foreachtest (id,name) values {\n}
 *  {\t}{\t}{\t}(${id},'name${id}')</stmt></sql>{\n}
 *  {\t}</foreach>{\n}
 *  </prepare>
 *  "
 */
public class PrepareHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(PrepareHandler.class);

    /**
     * Processes the prepare tag and carries out the sql statements specified
     * inside the tag. Prepare is an optional and used to prepare data inside
     * each test block. It may be useful when a test run changes data created
     * by setup or when each test run requires its own specific data
     * @param elPrepare the JDOM Element representing the prepare element.
     * @return returns a null, but needed to satisfy the interface.
     * @exception Exception if there was a problem running the prepare.
     */
    public final Object process(final Element elPrepare) throws Exception {
        LOG.debug(">> process(elPrepare)");
        if (elPrepare == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"prepare"});
        }
        try {
            List elChildren = elPrepare.getChildren();
            Iterator chiter = elChildren.iterator();
            while (chiter.hasNext()) {
                Element elChild = (Element) chiter.next();
                IHandler handler = 
                    HandlerFactory.getInstance(elChild.getName());
                handler.process(elChild);
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }
}
