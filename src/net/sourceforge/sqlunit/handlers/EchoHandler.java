/*
 * $Id: EchoHandler.java,v 1.4 2005/05/11 01:25:47 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/EchoHandler.java,v $
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
import net.sourceforge.sqlunit.IReporter;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.SymbolTable;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * Writes an INFO trace to the log file.
 * @author Victor Alekseev (krocodl@users.sourceforge.net)
 * @version $Revision: 1.4 $
 * @sqlunit.parent name="sqlunit" ref="sqlunit"
 * @sqlunit.element name="echo"
 *  description="Echoes the specified value to the log. This is mainly
 *  used for debugging a test."
 *  syntax="EMPTY"
 * @sqlunit.attrib name="name"
 *  description="A name for this echo tag"
 *  required="Yes, required by the SQLUnit logger"
 * @sqlunit.attrib name="text"
 *  description="The text to echo"
 *  required="Yes"
 * @sqlunit.attrib name="value"
 *  description="Specifies the expected value of text, if used in a test
 *  context. Saves caller the trouble of having to manually compare the
 *  expected and actual returned values."
 *  required="no"
 */
public class EchoHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(EchoHandler.class);

    /**
     * Processes a JDOM Element representing the Echo element. Writes
     * the specified value (after variable substitution) to the log.
     * @param elEcho the JDOM Element to use.
     * @return a null Object.
     * @exception Exception if there was a problem processing the tag.
     */
    public Object process(final Element elEcho) throws Exception {
        LOG.debug(">> process(elEcho)");
        if (elEcho == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"echo"});
        }
        String text = XMLUtils.getAttributeValue(elEcho, "text");
        // specifying an expected value for comparisons. May be null
        // if we dont want to use echo for comparisons
        String value = XMLUtils.getAttributeValue(elEcho, "value");
        text = SymbolTable.replaceVariables(text);
        if (value != null && value.trim().length() > 0) {
            boolean matched = value.trim().equals(text.trim());
            if (matched) {
                return Boolean.TRUE;
            } else {
                throw new SQLUnitException(IErrorCodes.ASSERT_FAILED,
                    new String[] {"equal", "value != text", value, text, ""});
            }
        } else {
            // we want to display it
            IReporter reporter = 
                (IReporter) SymbolTable.getObject(SymbolTable.REPORTER_KEY);
            reporter.echo(IErrorCodes.LF + "echo: (" + text + ")");
            return null;
        }
    }
}
