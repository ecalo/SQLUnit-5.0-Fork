/*
 * $Id: Arg.java,v 1.2 2004/09/27 18:04:24 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/beans/Arg.java,v $
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

/**
 * The Arg bean models an Object whose member variables are the attributes
 * of the arg element.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.2 $
 */
public class Arg {

    private static final Logger LOG = Logger.getLogger(Arg.class);

    private String name;
    private String value;
    private String type;

    /**
     * Default constructor.
     */
    public Arg() {
        // no-op constructor
    }

    /**
     * Returns the arg name.
     * @return the arg name.
     */
    public final String getName() { return name; }

    /**
     * Sets the arg name.
     * @param name the arg name.
     */
    public final void setName(final String name) { this.name = name; }

    /**
     * Returns the arg value.
     * @return the arg value.
     */
    public final String getValue() { return value; }

    /**
     * Sets the arg value.
     * @param value the arg value.
     */
    public final void setValue(final String value) { this.value = value; }

    /**
     * Returns the arg type.
     * @return the arg type.
     */
    public final String getType() { return type; }

    /**
     * Sets the arg type.
     * @param type the arg type.
     */
    public final void setType(final String type) { this.type = type; }
}
