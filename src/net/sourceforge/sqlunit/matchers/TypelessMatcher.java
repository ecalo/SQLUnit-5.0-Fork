/*
 * $Id: TypelessMatcher.java,v 1.4 2004/09/28 19:15:45 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/matchers/TypelessMatcher.java,v $
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
package net.sourceforge.sqlunit.matchers;

import net.sourceforge.sqlunit.IMatcher;
import net.sourceforge.sqlunit.SQLUnitException;

import java.util.Map;

/**
 * The TypelessMatcher matches column values only from two different
 * SQL or stored procedure calls. This would be useful when the data
 * returned from two different databases may have the same String value
 * but the actual datatypes they are implemented as may be different.
 * This is the matcher that is used implicitly when the assertion
 * "resultset-values-equal" is called.
 * @sqlunit.matcher.arg name="None" description="-"
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.4 $
 */
public class TypelessMatcher implements IMatcher {

    /**
     * Default constructor as per contract with IMatcher.
     */
    public TypelessMatcher() {
        // empty default constructor
    }

    /**
     * Returns true if the String values of the source and target are
     * equal.
     * @param source the String representing the source to be matched.
     * @param target the String representing the target to be matched.
     * @param args a Map of name value pairs of arguments passed in.
     * @return true if the matching strategy resulted in success.
     * @exception SQLUnitException if there was a problem with matching.
     */
    public final boolean isEqual(final String source, final String target, 
            final Map args) throws SQLUnitException {
        return (target.equals(source));
    }
}
