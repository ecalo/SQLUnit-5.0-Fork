/*
 * $Id: SeverityHandler.java,v 1.3 2005/07/08 04:16:17 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/SeverityHandler.java,v $
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

import java.util.List;

import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.SymbolTable;
import net.sourceforge.sqlunit.beans.Severity;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.jdom.Element;
import org.jdom.Text;

import org.apache.log4j.Logger;

/**
 * The SeverityHandler processes the contents of the Severity tag in the
 * input XML file. It decides whether the test's severity is equal or greater
 * that a user-defined severity, which we will called the threshold severity.
 * The decision is made following these rules:
 * <ol>
 *   <li/>If the current test's severity tag is not present or is empty, the
 *   the test matches and is executed;
 *   <li/>The threshold severity is retrieved as well as the content of the
 *   current test's severity tag and the two values are compared. If the
 *   current test's severity is lower than the threshold severity, the test is
 *   skipped and is reported as skipped; if not it is executed.
 * </ol>
 * The threshold severity is provided in one of the following ways, sorted by
 * priority:
 * <ul>
 *   <li/>As an ant property, called sqlunit.severity.threshold;
 *   <li/>As the value of the key ${sqlunit.severity.threshold} from the
 *   SymbolTable;
 *   <li/>As a System property named sqlunit.severity.threshold;
 * </ul>
 * @author Ivan Ivanov
 * @version $Revision: 1.3 $
 * @sqlunit.parent name="classifiers" ref="classifiers"
 * @sqlunit.element name="severity"
 *  description="Allows caller to define various levels of Severity for
 *  a test. Valid values are FATAL, ERROR, WARN, INFO and DEBUG."
 *  syntax="(BODY)"
 */
public class SeverityHandler implements IHandler {

    private static Logger LOG = Logger.getLogger(SeverityHandler.class);

    private static String SEVERITY_PROPERTY = "sqlunit.severity.threshold";

    /**
     * Processes the Severity JDOM Element. Returns Boolean.TRUE if the
     * severity matches the specified severity threshhold.
     * @param elSeverity the JDOM Element representing the severity tag.
     * @return a Boolean.TRUE or FALSE. Caller must cast appropriately.
     * @exception Exception if there was a problem processing the element.
     */
    public Object process(final Element elSeverity) throws Exception {
        LOG.debug(">> process(elSeverity)");
        List content = elSeverity.getContent();
        if (content.isEmpty()) {
            return Boolean.TRUE;
        }
        Severity threshold = Severity.toSeverity(
                getSeverityThreshold());
        Text text = (Text)content.get(0);
        Severity severity = Severity.toSeverity(
                text.getTextNormalize());
        return new Boolean(severity.isGreaterOrEquals(threshold));
    }

    /**
     * Returns the specified severity threshhold for SQLUnit by looking it
     * up in the ant build.xml file, the Symbol table or system properties
     * in that order.
     * @return the specified Severity threshhold.
     */
    private String getSeverityThreshold() {
        String threshold = SymbolTable.getValue(
                "${ant." + SEVERITY_PROPERTY + "}");
        if (threshold == null) {
            threshold = SymbolTable.getValue("${" + SEVERITY_PROPERTY + "}");
        }
        if (threshold == null) {
            threshold = System.getProperty(SEVERITY_PROPERTY);
        }
        return threshold;
    }
}
