/*
 * $Id: TypeMap.java,v 1.4 2005/03/19 02:28:19 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/utils/TypeMap.java,v $
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

import net.sourceforge.sqlunit.ConnectionRegistry;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A specialized HashMap to lookup values in a server specific manner. If
 * the server specific value is not found, then it falls back to the default
 * lookup.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.4 $
 */
public class TypeMap extends HashMap {

    private static final Logger LOG = Logger.getLogger(TypeMap.class);

    /**
     * Overrides the superclass method to add server-specific logic. The
     * method will first lookup up the current server name from the 
     * ConnectionRegistry, and attempt to look up the value using the 
     * key ${servername}.key. This corresponds to the override type mapping
     * for that particular server. If no value is found for this key, then
     * it falls back on the default lookup by type, which corresponds to 
     * type mappings which have not been overriden and are valid for this
     * server. If neither key is found, then it returns null.
     * @param key the type name to look up.
     * @return the corresponding value for the ${servername}.key or the
     * value corresponding to key if the former is not found. May return
     * null if neither value is found.
     */
    public final Object get(final String key) {
        LOG.debug(">> get(" + key + ")");
        if (key == null) { return null; }
        Object value = null;
        if (key.indexOf(".") > -1) {
            // server name explicitly supplied, only use this
            LOG.debug("Looking up value by " + key);
            value = super.get(key);
        } else {
            String serverName = ConnectionRegistry.getServerName();
            if (serverName != null) {
                // lookup with implicit server name
                LOG.debug("Looking up value by " + serverName + "." + key);
                value = super.get(serverName + "." + key);
                if (value == null) {
                    // no server-specific mapping, fallback to base mapping
                    LOG.debug("Falling back on basic lookup by " + key);
                    value = super.get(key);
                }
            } else {
                // weird case, fallback to default get()
                LOG.debug("No servername found, lookup up value by " + key);
                value = super.get(key);
            }
        }
        return value;
    }

    /**
     * Reverse lookup of the key by value. The method will first lookup the
     * current server name from the ConnectionRegistry. It then gets all the 
     * values in the map, then iterates over the list to find entries of the
     * form ${servername}.value, and returns the corresponding key if found.
     * If not found, it will fall back to look for value, and return the
     * corresponding key if that is found. If neither value is found, then
     * it returns null.
     * @param value the value to look up.
     * @return the key corresponding to ${servername}.value or value. May
     * return null if neither is found.
     */
    public final Object rget(final String value) {
        LOG.debug(">> rget(" + value + ")");
        if (value == null) { return null; }
        // unroll all the values into a list and list the keys where
        // the value matches
        LOG.debug("Unrolling all values into list");
        List sublist = new ArrayList();
        Iterator kiter = super.keySet().iterator();
        while (kiter.hasNext()) {
            String key = (String) kiter.next();
            String val = (String) super.get(key);
            if (value.equals(val)) {
                LOG.debug("Adding " + key + "=" + val);
                sublist.add(key);
            }
        }
        String serverName = ConnectionRegistry.getServerName();
        String theKey = null;
        // go through the list looking for either the server specific key
        // or the basic key, server specific gets higher precedence
        Iterator siter = sublist.iterator();
        String basicKey = null;
        String serverSpecificKey = null;
        while (siter.hasNext()) {
            String key = (String) siter.next();
            if (key.startsWith(serverName + ".")) { // server specific
                LOG.debug("Found server specific key " + key + " for " + value);
                serverSpecificKey = key;
            } else if (key.indexOf(".") == -1) {    // basic type
                LOG.debug("Found basic key " + key + " for " + value);
                basicKey = key;
            } else {                                // other server specific
                LOG.debug("Found other server specific key + " + key
                    + " for " + value);
                continue;
            }
        }
        // if serverSpecificKey is not null, use it, else use the basicKey
        return (serverSpecificKey != null ? serverSpecificKey : basicKey);
    }
}
