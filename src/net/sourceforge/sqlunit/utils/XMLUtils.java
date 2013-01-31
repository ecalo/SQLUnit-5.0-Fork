/*
 * $Id: XMLUtils.java,v 1.6 2006/04/30 22:25:56 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/utils/XMLUtils.java,v $
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
package net.sourceforge.sqlunit.utils;

import net.sourceforge.sqlunit.HandlerFactory;
import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.SymbolTable;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Provides commonly used functionality for methods which have to deal
 * with JDOM elements.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.6 $
 */
public final class XMLUtils {

    private static final Logger LOG = Logger.getLogger(XMLUtils.class);

    /**
     * Private Constructor. Cannot be instantiated.
     */
    private XMLUtils() {
        // private constructor, cannot instantiate
    }

    /**
     * Wrapper over Element.getAttributeValue(attributeName) which tests
     * to see if the attributeName is a variable, if so, does another
     * lookup of the symbol table to return the value of the variable.
     * @param element the JDOM Element.
     * @param attribute the name of the attribute.
     * @return the value of the attribute, or the value for the variable
     * stored in the SymbolTable if the value is a variable.
     */
    public static String getAttributeValue(final Element element,
                                           final String attribute) {
        LOG.debug(">> getAttributeValue(Element " + element.getName() + ","
            + attribute + ")");
        String value = element.getAttributeValue(attribute);
        if (SymbolTable.isVariableName(value)) {
            String symValue =  SymbolTable.getValue(value);
            if (symValue == null) {
                return value;
            } else {
                return symValue;
            }
        } else {
            return value;
        }
    }

    /**
     * Wrapper over Element.getText() which tests to see if the body text
     * returned is a variable, if so, does another lookup of the symbol
     * table to return the value of the variable.
     * @param element the JDOM Element.
     * @return the value of the body text, or the value for the variable
     * stored in the SymbolTable if the value is a variable.
     */
    public static String getText(final Element element) {
        LOG.debug(">> getText(Element " + (element == null
            ? "NULL" : element.getName()) + ")");
        if (element == null) { return null; }
        String text = element.getText();
        if (SymbolTable.isVariableName(text)) {
            String symValue = SymbolTable.getValue(text);
            if (symValue != null) {
                return symValue;
            } else {
                return text;
            }
        } else {
            return text;
        }
    }

    /**
     * Converts the Element to a formatted XML String.
     * @param element the JDOM Element.
     * @return a formatted XML String representing the Element.
     */
    public static String toXMLString(final Element element) {
        LOG.debug(">> toXMLString(Element " + element.getName() + ")");
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            outputter.output(element, bos);
        } catch (IOException e) {
            return "** IOException from XMLUtils.toXMLString("
                + element.getName() + ")";
        }
        return bos.toString();
    }

    /**
     * Replaces all occurences of CRLF, CR and LF with a single space
     * and returns the trimmed result string.
     * @param unstripped the unstripped string.
     * @return the stripped string.
     */
    public static String stripCRLF(final String unstripped) {
        if (unstripped == null) { return null; }
        return unstripped.replaceAll("\r\n", " ").
            replaceAll("\r", " ").replaceAll("\n", " ").trim();
    }
}
