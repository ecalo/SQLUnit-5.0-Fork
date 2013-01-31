/*
 * $Id: ExceptionBean.java,v 1.4 2004/09/27 18:04:24 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/beans/ExceptionBean.java,v $
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
package net.sourceforge.sqlunit.beans;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * The ExceptionBean bean models an exception specified in a result.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.4 $
 */
public class ExceptionBean {

    private static final Logger LOG = Logger.getLogger(ExceptionBean.class);

    private String errorCode;
    private String errorMessage;

    /**
     * Default constructor.
     */
    public ExceptionBean() {
        // default constructor.
    }

    /**
     * Returns the error code.
     * @return the error code.
     */
    public final String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the error code.
     * @param errorCode the error code to set.
     */
    public final void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Returns the error message.
     * @return the error message.
     */
    public final String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the error message.
     * @param errorMessage the error message to set.
     */
    public final void setErrorMessage(final String errorMessage) {
        // strip trailing whitespace or traling newline
        if (errorMessage != null) { 
            this.errorMessage = errorMessage.replaceAll("\\s*$", "");
        }
    }

    /**
     * Returns a JDOM Element representing the Exception bean.
     * @return a JDOM Element representing the Exception bean.
     */
    public final Element toElement() {
        Element elException = new Element("exception");
        if (getErrorCode() != null) {
            Element elCode = new Element("code");
            elCode.setText(getErrorCode());
            elException.addContent(elCode);
        }
        if (getErrorMessage() != null) {
            Element elMessage = new Element("message");
            elMessage.setText(getErrorMessage());
            elException.addContent(elMessage);
        }
        return elException;
    }
}
