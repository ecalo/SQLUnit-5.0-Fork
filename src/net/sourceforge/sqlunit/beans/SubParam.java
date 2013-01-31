/*
 * $Id: SubParam.java,v 1.1 2005/01/27 01:40:22 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/beans/SubParam.java,v $
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
 * The SubParam bean models a subparam element.
 * @author Victor Alekseev (krocodl@users.sourceforge.net)
 * @version $Revision: 1.1 $
 */
public class SubParam {

    private static final Logger LOG = Logger.getLogger(SubParam.class);

    private String name;
    private String value;

    /**
     * Default constructor.
     */
    public SubParam() {
        // default constructor
    }

    /**
     * Returns the subparam name.
     * @return the subparam name.
     */
    public final String getName() {
        return name;
    }

    /**
     * Sets the subparam name.
     * @param name the subparam name.
     */
    public final void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the subparam value.
     * @return the subparam value.
     */
    public final String getValue() {
        return value;
    }

    /**
     * Sets the subparam value.
     * @param value the subparam value.
     */
    public final void setValue(final String value) {
        this.value = value;
    }
}
