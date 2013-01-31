/*
 * $Id: TypeFactory.java,v 1.6 2004/09/25 23:00:00 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/TypeFactory.java,v $
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

/**
 * Factory for generating concrete implementations of IType.
 * @author Ralph Brendler (rbrendler@users.sourceforge.net)
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.6 $
 */
public final class TypeFactory {
    
    private static final Logger LOG = Logger.getLogger(TypeFactory.class);

    /**
     * Private Constructor. Cannot be instantiated.
     */
    private TypeFactory() {
        // private constructor, cannot instantiate.
    }

    /**
     * Given an XML type name, instantiates and returns a partially 
     * populated IType object of the specified type.
     * @param name the XML name for the type. The database server name is 
     * implicitly provided by looking up the metadata for the current 
     * Connection. If there is no match, then the type name without the 
     * server-name (ie server non-specific type) is looked up.
     * @return a partially populated IType object of the named type.
     * @exception SQLUnitException if there was a problem.
     */
    public static IType getInstance(final String name) throws SQLUnitException {
        LOG.debug(">> getInstance(" + name + ")");
        if (name == null) {
            throw new SQLUnitException(IErrorCodes.UNDEFINED_TYPE, 
                new String[] {"NULL"});
        }
        try {
            // try to find the type mapping specific to this database first
            // to get any types that have been overriden
            String typeName = name;
            String typeClass = null;
            if (name.indexOf(".") > -1) {
                // server-name already supplied
                LOG.debug("Looking up " + typeName);
                typeClass = TypeMapper.findClassByName(typeName);
                if (typeClass == null) {
                    throw new SQLUnitException(IErrorCodes.UNDEFINED_TYPE,
                        new String[] {typeName});
                }
            } else {
                // find the server-name
                String serverName = ConnectionRegistry.getServerName(); 
                typeName = serverName + "." + name;
                LOG.debug("Looking up " + typeName);
                typeClass = TypeMapper.findClassByName(typeName);
                LOG.debug("found a typeClass: " + typeClass);
                if (typeClass == null) {
                    // no server specific type found, fallback to standard type
                    typeName = name;
                    LOG.debug("Falling back on basic type lookup for "
                        + typeName);
                    typeClass = TypeMapper.findClassByName(typeName);
                }
                if (typeClass == null) {
                    throw new SQLUnitException(IErrorCodes.UNDEFINED_TYPE,
                        new String[] {name});
                }
            }
            LOG.debug("Instantiating a " + typeClass);
            IType type = (IType) Class.forName(typeClass).newInstance();
            type.setName(typeName);
            type.setId(TypeMapper.findIdByName(typeName));
            return type;
        } catch (SQLUnitException e) { 
            throw e;
        } catch (Exception e) {
            throw new SQLUnitException(IErrorCodes.GENERIC_ERROR,
                new String[] {"System", e.getClass().getName(),
                e.getMessage()}, e);
        }
    }

    /**
     * Given an SQL Type Id, instantiates and returns a partially populated
     * IType object of the specified type.
     * @param typeId an integer that exists in one of the SQLUnit type
     * mapping properties files.
     * @return an IType object of the specified type.
     * @exception SQLUnitException if there was a problem.
     */
    public static IType getInstance(final int typeId) throws SQLUnitException {
        String name = TypeMapper.findNameById(typeId);
        if (name == null) {
            throw new SQLUnitException(IErrorCodes.UNDEFINED_TYPE, 
                new String[] {(new Integer(typeId)).toString()});
        }
        return getInstance(name);
    }
}
