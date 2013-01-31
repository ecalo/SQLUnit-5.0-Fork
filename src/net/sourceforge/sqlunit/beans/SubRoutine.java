/*
 * $Id: SubRoutine.java,v 1.1 2005/01/27 01:40:22 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/beans/SubRoutine.java,v $
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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The SubRoutine bean represents a partially defined stored procedure or
 * SQL call which can be called later by specifying missing parameters or
 * overriding them with new values.
 * @author Victor Alekseev (krocodl@users.sourceforge.net)
 * @version $Revision: 1.1 $
 */
public class SubRoutine {

    private static final Logger LOG = Logger.getLogger(SubRoutine.class);

    private String name;
    private String query;
    private String description;
    private String type;
    private Map paramMap;

    /**
     * Constructs an empty Subroutine bean.
     */
    public SubRoutine() {
        paramMap = new LinkedHashMap();
    }

    /**
     * Returns the name of the subroutine.
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the subroutine.
     * @param name the name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the query for the subroutine.
     * @return the query.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the query for the subroutine.
     * @param query the query.
     */
    public void setQuery(final String query) {
        this.query = query;
    }

    /**
     * Returns the description for the subroutine.
     * @return the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description for the subroutine.
     * @param description the description.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Returns the type of the subroutine.
     * @return the type.
     */
    public String getType() {
        if (type == null) {
            type = "sql";
        }
        return type;
    }

    /**
     * Sets the type of the subroutine. Valid values are "sql" and "call".
     * If not specified, the default value is "sql".
     * @param type the type.
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Returns the parameter map for the subroutine. The map is keyed
     * by the param.id value.
     * @return the Parameter map.
     */
    public Map getParamMap() {
        return paramMap;
    }

    /**
     * Convenience method to add a single param to the subroutine.
     * @param param the Param object to add to the SubRoutine's map.
     */
    public void addParam(final Param param) {
        // this can never be null, since we check for this situation
        // in the handler
        String paramName = param.getName();
        paramMap.put(paramName, param);
    }

    /**
     * Convenience method to update the value of a param object. If
     * the param name does not exist, then no update takes place.
     * @param paramName the name of the param to update.
     * @param paramValue the value of the param to update.
     */
    public void updateParam(final String paramName, final String paramValue) {
        Param param = (Param) paramMap.get(paramName);
        if (param != null) {
            param.setValue(paramValue);
            paramMap.put(paramName, param);
        }
    }
}
