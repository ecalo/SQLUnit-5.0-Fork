/*
 * $Id: SyspropertyElement.java,v 1.1 2005/07/28 01:27:28 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/ant/SyspropertyElement.java,v $
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
package net.sourceforge.sqlunit.ant;

/**
 * Sample placeholder for key-value pairs that will be
 * set as System properties.
 * @author Ivan Ivanov
 * @version $Revision: 1.1 $
 */
public class SyspropertyElement {

    /**
     * The key
     */
    private String key;

    /**
     * The value
     */
    private String value;

    /**
     * Returns the key of the property.
     * @return the key of the property.
     * @see #setKey(String)
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key of the property.
     * @param key the key to set.
     * @see #getKey()
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Returns the value of the property.
     * @return the value of the property
     * @see #setValue(String)
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the property.
     * @param value the value to set.
     * @see #getValue()
     */
    public void setValue(String value) {
        this.value = value;
    }
}
