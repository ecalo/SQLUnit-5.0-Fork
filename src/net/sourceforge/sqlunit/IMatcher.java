/*
 * $Id: IMatcher.java,v 1.4 2004/09/25 20:40:17 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/IMatcher.java,v $
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

import java.util.Map;

/**
 * The IMatcher interface specifies the contract that all Matcher objects
 * must fulfil. Classes implementing the IMatcher interface provide the
 * functionality to determine if one object matches the other in some way.
 * Note that all classes implementing the IMatcher interface should have
 * a default no-args constructor.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.4 $
 */
public interface IMatcher {

    /**
     * The isEqual method returns a true or false depending on whether
     * the Matcher object determines that the source String matches the
     * target String. The Matcher uses a map of named arguments supplied
     * to it in the Map args. Each Matcher class may make use of different
     * name value pairs to do this and must document these name-value
     * pairs in the JavaDocs since there is no other place for users of
     * these classes to determine this information other than by looking
     * at the source code. Note that there is no way to enforce that
     * all name value pairs needed by a Matcher are going to be available
     * at runtime, so the Matcher classes should be able to gracefully
     * handle these situations.
     * @param source the String representing the source to be matched.
     * @param target the String representing the target to be matched.
     * @param args a Map of name value pairs of arguments passed in.
     * @return true if the source and target are equal according to the
     * Matcher.
     * @exception SQLUnitException if there was a problem.
     */
    boolean isEqual(String source, String target, Map args)
            throws SQLUnitException;
}
