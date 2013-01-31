/*
 * $Id: ExceptionHandler.java,v 1.6 2004/09/28 19:15:45 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/ExceptionHandler.java,v $
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
import net.sourceforge.sqlunit.beans.ExceptionBean;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * The Exception Handler parses a JDOM Element representing an exception
 * tag and returns an ExceptionBean object.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.6 $
 * @sqlunit.parent name="result" ref="result"
 * @sqlunit.element name="exception"
 *  description="Specifies an expected exception with expected error code 
 *  and message"
 *  syntax="(code)?, (message)?"
 * @sqlunit.child name="code"
 *  description="The error code for the expected exception"
 *  required="No"
 *  ref="none"
 * @sqlunit.child name="message"
 *  description="The error message for the expected exception"
 *  required="No"
 *  ref="none"
 * @sqlunit.example name="An exception declaration"
 *  description="
 *  <exception>{\n}
 *  {\t}<code>0</code>{\n}
 *  {\t}<message>ERROR:  Cannot insert a duplicate key into unique index ux2_employee</message>{\n}
 *  </exception>
 *  "
 */
public class ExceptionHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(ExceptionHandler.class);

    /**
     * Processes the JDOM Element representing the param tag returns the
     * Exception object.
     * @param elException the JDOM Element representing the param tag.
     * @return a Exception object. Client needs to cast to a Exception.
     * @exception Exception if something went wrong processing the param.
     */
    public final Object process(final Element elException) throws Exception {
        LOG.debug(">> process(elException)");
        if (elException == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"exception"});
        }
        ExceptionBean eb = new ExceptionBean();
        Element elCode = elException.getChild("code");
        String errorCode = XMLUtils.getText(elCode);
        if (errorCode != null && errorCode.trim().length() > 0) {
            eb.setErrorCode(errorCode.trim());
        }
        Element elMessage = elException.getChild("message");
        String errorMessage = XMLUtils.getText(elMessage);
        if (errorMessage != null && errorMessage.trim().length() > 0) {
            eb.setErrorMessage(errorMessage);
        }
        return eb;
    }
}
