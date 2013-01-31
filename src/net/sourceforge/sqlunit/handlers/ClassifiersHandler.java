/*
 * $Id: ClassifiersHandler.java,v 1.3 2005/07/08 04:16:17 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/ClassifiersHandler.java,v $
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
import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.IReporter;
import net.sourceforge.sqlunit.SymbolTable;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * The ClassifiersHandler processes a JDOM Element representing the classifiers
 * element. The ClassifiersHandler is responsible for classifying a test, i.e.
 * whether the current test meets givens some user-defined criterea. These
 * criterea can be given as a set of external (to the test) properties. The
 * handler works in the following way: it iterates over the nested tags of
 * classifiers tag, calls their corresponding handlers, which compare their
 * content with the criterea. If one of them return false, the test does
 * not meet the criterea and is not executed and is reported as skipped;
 * otherwise is executed.<br/>
 * Note that you can nest arbitrary tags in classifiers handler (and update
 * sqlunit.dtd too), as long as method process(org.jdom.Element) of their
 * corresponding handlers return Boolean.
 * @author Ivan Ivanov
 * @version $Revision: 1.3 $
 * @sqlunit.parent name="test" ref="test"
 * @sqlunit.element name="classifiers"
 *  description="Provides the caller with the ability to classify and/or
 *  group tests by a given criteria. When the test is executed, the caller
 *  can pass to it some properties and the handler decides whether the
 *  test matches them. If the test matches it is executed, and if not,
 *  it is skipped and reported as skipped."
 *  syntax="((severity)?, (category)?)"
 * @sqlunit.example name="An example of a classifiers tag"
 *  description="
 *  <test name=\"Find Agent by Id test\"{\n}
 *  {\t}{\t}failure-message=\"Find Agent by Id test failed\">{\n}
 *  {\t}<classifiers>{\n}
 *  {\t}{\t}<severity>INFO</severity>{\n}
 *  {\t}{\t}<category>findById</category>{\n}
 *  {\t}</classifiers>{\n}
 *  {\t}<sql connection-id=\"mysql\">{\n}
 *  {\t}{\t}...{\n}
 *  {\t}</sql>{\n}
 *  </test>
 *  "
 */
public class ClassifiersHandler implements IHandler {

    private static Logger LOG = Logger.getLogger(ClassifiersHandler.class);

    /**
     * Processes the Classifiers JDOM Element and returns a Boolean TRUE or
     * FALSE depending on whether the test matched the criteria specified
     * for it. Delegates to the respective Severity and Category handlers
     * to determine the result of the match. If one or both properties are
     * not set, the handler will assume that all tests match.
     * @param elClassifiers the JDOM Element representing the classifiers tag.
     * @return a Boolean.TRUE or FALSE. Caller must cast appropriately.
     * @exception Exception if there was a problem processing the element.
     */
    public Object process(Element elClassifiers) throws Exception {
        LOG.debug(">> process(elClassifiers)");
        LOG.debug(elClassifiers);
        boolean matches = true;
        List children = elClassifiers.getChildren();
        for (Iterator i = children.iterator(); i.hasNext(); ) {
            Element e = (Element) i.next();
            IHandler handler = HandlerFactory.getInstance(e.getName());
            LOG.debug("Calling process for handler " + e.getName());
            matches = ((Boolean) handler.process(e)).booleanValue();
            LOG.debug("Handler returned " + matches);
            if (!matches) {
                IReporter reporter =
                    (IReporter) SymbolTable.getObject(SymbolTable.REPORTER_KEY);
                SymbolTable.setValue(SymbolTable.SKIP_REASON,
                    "rejected " + e.getName() + ":" + e.getTextTrim());
                break;
            }
        }
        return new Boolean(matches);
    }
}
