/*
 * $Id: VarSetter.java,v 1.1 2004/12/03 21:17:37 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/mock/VarSetter.java,v $
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
package net.sourceforge.sqlunit.test.mock;

/**
 * Simple class whose method is called from the set tag.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.1 $
 */
public final class VarSetter {

    /**
     * Constructor for VarSetter. Private, cannot instantiate.
     */
    private VarSetter() {
        // private constructor, cannot instantiate
    }

    /**
     * Passes through the value passed in.
     * @param value the value to pass through.
     * @return the passed in value.
     */
    public static String setVar(final String value) {
        return value;
    }
}
