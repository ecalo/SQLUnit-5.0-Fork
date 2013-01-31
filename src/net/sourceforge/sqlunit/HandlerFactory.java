/*
 * $Id: HandlerFactory.java,v 1.17 2005/01/18 02:00:24 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/HandlerFactory.java,v $
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
package net.sourceforge.sqlunit;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * The HandlerFactory object returns references to Handlers.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.17 $
 */
public final class HandlerFactory {
    
    private static final Logger LOG = Logger.getLogger(HandlerFactory.class);

    /** The Handler registry is represented by handlers.properties */
    private static final String HANDLER_REGISTRY = 
        "net.sourceforge.sqlunit.handlers";
    private static ResourceBundle props = null;

    /**
     * Private Constructor. Cannot be instantiated.
     */
    private HandlerFactory() {
        // cannot instantiate this class
    }

    /**
     * Returns an IHandler object given its name.
     * @param name the name of the Handler.
     * @return an implementation of a IHandler object.
     * @exception Exception if there was a problem instantiating the handler.
     */
    public static IHandler getInstance(final String name) throws Exception {
        LOG.debug(">> getInstance(" + name + ")");
        props = getResourceBundle();
        return (IHandler) Class.forName(props.getString(name)).newInstance();
    }

    /**
     * Returns a list of swappable tags that should be considered. An empty
     * List is returned if there are no swappable tags for this parent tag.
     * @param name the name of the parent handler.
     * @return a List of tag names that are considered swappable.
     * @exception Exception if there was a problem finding the information.
     */
    public static List getSwappableTags(final String name) throws Exception {
        LOG.debug(">> getSwappableTags(" + name + ")");
        List swappableTags = new ArrayList();
        props = getResourceBundle();
        String[] tagListArray =
            props.getString(name + ".swappable.tags").split("\\s*,\\s*");
        for (int i = 0; i < tagListArray.length; i++) {
            swappableTags.add(tagListArray[i]);
        }
        return swappableTags;
    }

    /**
     * Builds a static ResourceBundle object from the resource. If the 
     * ResourceBundle already exists, returns that.
     * @return a ResourceBundle object.
     * @exception Exception if there was a problem finding the bundle.
     */
    private static ResourceBundle getResourceBundle() throws Exception {
        LOG.debug(">> getResourceBundle()");
        if (props == null) {
            try {
                props = ResourceBundle.getBundle(HANDLER_REGISTRY);
            } catch (MissingResourceException e) {
                throw new SQLUnitException(IErrorCodes.GENERIC_ERROR,
                    new String[] {"I/O", e.getClass().getName(),
                        "Registry" + HANDLER_REGISTRY
                        + " is missing or incorrect"}, e);
            }
        }
        return props;
    }
}
