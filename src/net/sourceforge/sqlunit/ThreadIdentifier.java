/*
 * $Id: ThreadIdentifier.java,v 1.2 2004/09/25 23:00:00 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/ThreadIdentifier.java,v $
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

/**
 * Assigns and returns an identifier for a thread. This is used to tie back
 * the vendor name for the connection, regardless of whether it is operating
 * in single-threaded or multi-threaded mode. Each thread holds an implicit
 * reference to the copy of this thread-local instance of ThreadIdentifier 
 * as long as it is alive.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.2 $
 */
public final class ThreadIdentifier {
    
    // the next thread id to be assigned
    private static int nextId = 0;

    /**
     * Private Constructor. Cannot be instantiated.
     */
    private ThreadIdentifier() {
        // private constructor, cannot instantiate
    }

    /**
     * Builds up an static inner ThreadLocal object and initializes it
     * with the initial value for this thread.
     */
    private static ThreadLocal threadIdentifier = new ThreadLocal() {
        protected synchronized Object initialValue() {
            return new Integer(nextId++);
        }
    };

    /**
     * Returns the identifier for this thread.
     * @return the identifier for this thread.
     */
    public static String getIdentifier() {
        return ((Integer) (threadIdentifier.get())).toString();
    }
}
