/*
 * $Id: Dictionary.java,v 1.4 2004/10/05 20:13:04 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/Dictionary.java,v $
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
package net.sourceforge.sqlunit.test;

import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * This class implements a simple dictionary of name value pairs which 
 * can be populated and serialized to the database as a BLOB value. This
 * can then be retrieved using SQLUnit and converted to its String
 * representation automatically. This is a demo of storing smart Java
 * objects in a relational database.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.4 $
 */
public class Dictionary implements java.io.Serializable {
    
    private static final Logger LOG = Logger.getLogger(Dictionary.class);

    private LinkedHashMap dict = new LinkedHashMap();

    /**
     * Default Constructor.
     */
    public Dictionary() {
        // empty constructor
    }

    /**
     * Returns an Iterator on a sorted list of keys in this Dictionary.
     * @return an iterator for the keys to the Dictionary.
     */
    public final Iterator getKeys() {
        ArrayList l = new ArrayList();
        l.addAll(dict.keySet());
        Collections.sort(l);
        return l.iterator();
    }

    /**
     * Sets the value for a given key.
     * @param key the key into the Dictionary.
     * @param value the value associated with the key.
     */
    public final void setEntry(final String key, final String value) {
        dict.put(key, value);
    }

    /**
     * Returns the value associated with the given key.
     * @param key the key into the Dictionary.
     * @return the value associated with the given key.
     */
    public final String getEntry(final String key) {
        return (String) dict.get(key);
    }

    /**
     * Render this object with its values as a String.
     * @return the String representation of this Object.
     */
    public final String toString() {
        StringBuffer buf = new StringBuffer("Dictionary:{");
        Iterator keys = getKeys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = getEntry(key);
            buf.append(key).append("=>'").append(value).append("',");
        }
        if (buf.charAt(buf.length() - 1) == ',') {
            buf.setCharAt(buf.length() - 1, '}');
        } else {
            buf.append("}");
        }
        return buf.toString();
    }

    /**
     * Convenience method to write to a named file. This is not mandated
     * by the IJavaObject interface but helps when trying to build a test
     * case which matches with a file.
     * @param fileName A file name to write to.
     */
    public final void writeTo(final String fileName) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.flush();
            fos.close();
        } catch (Exception e) {
            LOG.error("Could not write Dictionary object to " + fileName);
        }
    }
}
