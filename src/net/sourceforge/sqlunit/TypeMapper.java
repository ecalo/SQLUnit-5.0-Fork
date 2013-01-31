/*
 * $Id: TypeMapper.java,v 1.7 2004/09/25 23:00:00 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/TypeMapper.java,v $
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

import net.sourceforge.sqlunit.utils.TypeMap;

import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Reads in the type resource files and provides methods to look up type
 * class names using various keys. This is a Singleton object and will 
 * be lazily instantiated once per run.
 * @author Ralph Brendler (rbrendler@users.sourceforge.net)
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.7 $
 */
public final class TypeMapper {
    
    private static final Logger LOG = Logger.getLogger(TypeMapper.class);

    private static final String TYPES_REGISTRY = 
        "net.sourceforge.sqlunit.types";
    private static final String USERTYPES_REGISTRY = "usertypes";
    private static final String UNSUPPORTED_TYPE = 
        "net.sourceforge.sqlunit.types.UnsupportedType";
    private static final String CLASS_SUFFIX = ".class";
    private static final String TYPE_SUFFIX = ".type";
    private static final int CLASS_SUFFIX_LENGTH = CLASS_SUFFIX.length();
    private static final int TYPE_SUFFIX_LENGTH = TYPE_SUFFIX.length();

    private static TypeMap nameClassMap = null;
    private static TypeMap nameIdMap = null;

    private static TypeMapper mapper = null;

    /**
     * Private default constructor. Can only be instantiated once from
     * TypeMapper.getTypeMapper().
     */
    private TypeMapper() {
        LOG.debug("instantiating TypeMapper");
        nameClassMap = new TypeMap();
        nameIdMap = new TypeMap();
        loadTypes(TYPES_REGISTRY, true);
        loadTypes(USERTYPES_REGISTRY, false);
        loadNonMappedTypes();
    }

    /**
     * Returns the current instance of the TypeMapper object.
     * @return the instance of the TypeMapper object.
     */
    public static TypeMapper getTypeMapper() {
        if (mapper == null) {
            mapper = new TypeMapper();
        }
        return mapper;
    }

    /**
     * Returns the class name for the specified type name. If the name is
     * already specified with server name, then restrict lookup to the
     * fully qualified type. Else first lookup the name with the implicit
     * server name returned from the connection, and if that fails look
     * up the basic data type.
     * @param name the type name that is used in the XML file.
     * @return the full class name for the type class.
     */
    public static String findClassByName(final String name) {
        LOG.debug("finding class by name: " + name);
        return (String) nameClassMap.get(name);
    }

    /**
     * Returns the class name for the specified type code. Extracts the
     * name with findNameById() and then delegates to findClassById().
     * @param id the SQL type code.
     * @return the full class name for the type class.
     */
    public static String findClassById(final int id) {
        LOG.debug("finding class by id: " + id);
        return (String) findClassByName(findNameById(id));
    }

    /**
     * Returns the type name for the specified type code. The id may not 
     * be unique by itself, but the id would be unique for a given server,
     * or only among basic datatype if server is not supplied.
     * @param id the SQL type code.
     * @return the XML type name.
     */
    public static String findNameById(final int id) {
        LOG.debug("finding name by id: " + id);
        return (String) nameIdMap.rget((new Integer(id)).toString());
    }

    /**
     * Returns the type code for the specified type name. If the name
     * contains an explicit server name, then it does an exact lookup
     * of the id for the server.typename. Else, it first attempts to 
     * lookup an override using the value of the server name from the
     * Connection, and if not found, it falls back on the lookup for
     * basic datatype mappings.
     * @param name the XML type name.
     * @return the SQL type code.
     */
    public static int findIdByName(final String name) {
        LOG.debug("finding id by name: " + name);
        String id = (String) nameIdMap.get(name);
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return Types.OTHER;
        }
    }

    /**
     * Convenience method currently used for JUnit testing. May be used
     * elsewhere later.
     * @param showSupported if true, will show only currently supported
     * types, else will show all the currently configured types.
     * @return a List of XML names of the currently supported types.
     */
    public static List findAllTypeNames(final boolean showSupported) {
        LOG.debug("listing all " + (showSupported ? "supported" : "all")
            + " type names");
        List allTypeNames = new ArrayList();
        allTypeNames.addAll(nameClassMap.keySet());
        return allTypeNames;
    }

    /**
     * Loads the types from the specified registry name. This is used once
     * for the SQLUnit mapped types, and once more for the user defined
     * mapped types specified in usertypes.properties. The user defined
     * mapped types may or may not exist.
     * @param registryName the name of the registry to look up.
     * @param logIfMissing if true, a warning will be logged to the console
     * if the method cannot find the resource to load.
     */
    private void loadTypes(final String registryName, 
            final boolean logIfMissing) {
        LOG.debug("loading types from: " + registryName);
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(registryName);
            for (Enumeration e = bundle.getKeys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                String value = bundle.getString(key);
                String xmlName = null;
                String sqlCode = null;
                if (key.endsWith(CLASS_SUFFIX)) {
                    xmlName = key.substring(0, key.length()
                        - CLASS_SUFFIX_LENGTH);
                    nameClassMap.put(xmlName, value);
                } else if (key.endsWith(TYPE_SUFFIX)) {
                    xmlName = key.substring(0, key.length()
                        - TYPE_SUFFIX_LENGTH);
                    nameIdMap.put(xmlName, value);
                } else {
                    LOG.warn("Invalid format for key " + key + ", ignoring");
                    continue;
                }
            }
        } catch (MissingResourceException e) {
            if (logIfMissing) {
                LOG.warn("Could not load " + registryName
                    + ", you will have problems later!");
            }
        }
    }

    /**
     * Introspects the java.sql.Types class to find types which are not
     * explicitly mapped and sets them to UnsupportedType.
     */
    private void loadNonMappedTypes() {
        LOG.debug("loading non-mapped types");
        try {
            Class javaSqlTypes = Class.forName("java.sql.Types");
            Field[] fields = javaSqlTypes.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                int id = fields[i].getInt(fields[i]);
                if (findClassById(id) == null) {
                    String typeName = fields[i].getName();
                    String typeCode = (new Integer(id)).toString();
                    nameClassMap.put(typeName, UNSUPPORTED_TYPE);
                    nameIdMap.put(typeName, typeCode);
                }
            }
        } catch (Exception e) {
            // :IGNORE: this should always work
        }
    }
}
