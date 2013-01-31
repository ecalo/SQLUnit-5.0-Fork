/*
 * $Id: ThreadHandlerAdapter.java,v 1.7 2004/09/25 23:00:00 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/ThreadHandlerAdapter.java,v $
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
import org.jdom.Element;

/**
 * The ThreadHandlerAdapter class allows the running of a Handler class
 * within a Thread. This is used to support multi-threading in the 
 * Diff Element.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.7 $
 */
public class ThreadHandlerAdapter extends Thread {

    private static final Logger LOG = 
        Logger.getLogger(ThreadHandlerAdapter.class);

    private Element element = null;
    private Object processingResult = null;

    /**
     * Constructor using an SQLUnit Element.
     * @param element the Element for which the ThreadHandlerAdapter 
     * needs to be constructed.
     */
    public ThreadHandlerAdapter(final Element element) {
        LOG.debug("[ThreadHandlerAdapter]");
        this.element = element;
    }

    /**
     * Runs the ElementHandler.process(Element) and places the result
     * in the processingResult object. If there is an error reported
     * from the process() call, a Runtime error object is thrown which
     * wraps the Exception object returned.
     */
    public final void run() {
        LOG.debug(">> run()");
        try {
            IHandler handler = HandlerFactory.getInstance(element.getName());
            this.processingResult = handler.process(element);
        } catch (Exception e) { 
            // throw this exception as a throwable
            throw new Error(e);
        }
    }

    /**
     * Accessor for the results of running the process(Element) method.
     * @return the processing result.
     */
    public final Object getProcessingResult() {
        LOG.debug(">> getProcessingResult()");
        return this.processingResult;
    }
}
