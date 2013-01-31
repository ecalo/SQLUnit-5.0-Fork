/*
 * $Id: SkipHandler.java,v 1.3 2005/07/08 04:16:17 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/SkipHandler.java,v $
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

import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.IReporter;
import net.sourceforge.sqlunit.SymbolTable;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * The SkipHandler class processes the contents of a Skip tag in the
 * input SQLUnit XML file. It decides whether to skip the current test
 * based on the content of value attribute (true/false) of the skip tag.
 * If the content is true, the test is skipped and reported as skip for
 * the reason given in the body of the skip tag; otherwise the test is
 * executed.
 * @author Ivan Ivanov
 * @version $Revision: 1.3 $
 * @sqlunit.parent name="test" ref="test"
 * @sqlunit.element name="skip"
 *  description="The skip tag is used to indicate to SQLUnit that this
 *  test should be skipped."
 *  syntax="(BODY)"
 * @sqlunit.attrib name="value"
 *  description="If set to true, then the test will be skipped"
 *  required="No, default is false"
 */
public class SkipHandler implements IHandler {

    private static Logger LOG = Logger.getLogger(SkipHandler.class);

    /**
     * Returns a Boolean.TRUE or FALSE based on whether the value of the
     * value attribute is set to "true" or "false". If a reason is provided,
     * then the reason is logged else a default message is provided.
     * @param elSkip the JDOM Element representing the skip tag.
     * @return Boolean.TRUE or FALSE.
     * @exception Exception if there was a problem processing the tag.
     */
    public Object process(Element elSkip) throws Exception {
        LOG.debug(">> process(elSkip)");
        String skipValue = XMLUtils.getAttributeValue(elSkip, "value");
        boolean skip = (("true").equals(skipValue));
        if (skip) {
            IReporter reporter =
                (IReporter) SymbolTable.getObject(SymbolTable.REPORTER_KEY);
            String reason = elSkip.getTextTrim();
            reason = ("".equals(reason) ? null : reason);
            SymbolTable.setValue(SymbolTable.SKIP_REASON, reason);
        }
        return new Boolean(skip);
    }
}
