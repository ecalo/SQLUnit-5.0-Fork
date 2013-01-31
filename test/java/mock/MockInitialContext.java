/*
 * $Id: MockInitialContext.java,v 1.2 2004/10/05 20:13:04 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/mock/MockInitialContext.java,v $
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
package net.sourceforge.sqlunit.test.mock;

import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Overrides the InitialContext to provide an in-memory lookup mechanism
 * instead of network lookup. The only methods overriden are bind() and 
 * lookup().
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.2 $
 */
public class MockInitialContext extends InitialContext {
    
    private Map map;

    /**
     * Default constructor used by the MockInitialContextFactory.
     * @exception NamingException if an error occurs in initialization.
     */
    public MockInitialContext() throws NamingException {
        super();
        map = new HashMap();
    }

    /**
     * Binds an Object into the Context by name.
     * @param name the key to look the Object up by.
     * @param obj the Object to bind into the Context.
     * @exception NamingException if there is a problem with binding.
     */
    public final void bind(final String name, final Object obj) 
            throws NamingException {
        map.put(name, obj);
    }

    /**
     * Retrieves an Object from the Context by name.
     * @param name the name of the Object in the context.
     * @return the value of the Object.
     * @exception NamingException if there is a problem with lookup.
     */
    public final Object lookup(final String name) throws NamingException {
        return map.get(name);
    }
}
