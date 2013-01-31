/*
 * $Id: Severity.java,v 1.1 2005/06/07 05:16:44 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/beans/Severity.java,v $
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

/**
 * The Severity bean models severity levels in SQLUnit.
 * @author Ivan Ivanov
 * @version $Revision: 1.1 $
 */
public class Severity {

    protected int level;

    protected String levelName;

    public final static int OFF_INT = Integer.MAX_VALUE;
    public final static int FATAL_INT = 16;
    public final static int ERROR_INT = 8;
    public final static int WARN_INT = 4;
    public final static int INFO_INT = 2;
    public final static int DEBUG_INT = 1;
    public final static int ALL_INT = Integer.MIN_VALUE;
    
    public final static String FATAL_STR = "FATAL";
    public final static String ERROR_STR = "ERROR";
    public final static String WARN_STR = "WARN";
    public final static String INFO_STR = "INFO";
    public final static String DEBUG_STR = "DEBUG";

    public final static Severity DEBUG = new Severity(DEBUG_INT, DEBUG_STR);
    public final static Severity INFO = new Severity(INFO_INT, INFO_STR);
    public final static Severity WARN = new Severity(WARN_INT, WARN_STR);
    public final static Severity ERROR = new Severity(ERROR_INT, ERROR_STR);
    public final static Severity FATAL = new Severity(FATAL_INT, FATAL_STR);
    
    /**
     * Create a new Severity object with the desired level id and name.
     * @param level the level id.
     * @param levelName the level name.
     */
    protected Severity(int level, String levelName) {
        this.level = level;
        this.levelName = levelName;
    }

    /**
     * Returns true if the object passed in is equal to this object.
     * @param obj the Object to compare with.
     * @return true or false.
     */
    public boolean equals(Object o) {
        boolean r = false;
        if (o instanceof Severity) {
            r = level == ((Severity)o).level;
        }
        return r;
    }

    /**
     * Returns true if the specified severity is greater than or equal to
     * this severity value.
     * @param s the Severity to compare with.
     * @return true or false.
     */
    public boolean isGreaterOrEquals(Severity s) {
        return level >= s.level;
    }
    
    /** 
     * Returns the String representation of this severity object.
     * @return the String representation of this object.
     */
    public String toString() {
        return levelName; 
    }
    
    /**
     * Create a Severity object from its level name.
     * @param levelName the level name.
     * @return a Severity object.
     */
    public static Severity toSeverity(String levelName) {
        return toSeverity(levelName, DEBUG);
    }
    
    /**
     * Create a Severity object from its level name. If none of the level
     * name matches, then use the default Severity.
     * @param levelName the level name to use.
     * @param defaults the default Severity object to use.
     * @return a Severity object.
     */
    public static Severity toSeverity(String levelName, Severity defaultS) {
        if (DEBUG_STR.equals(levelName)) {
            return DEBUG;
        } else if (INFO_STR.equals(levelName)) {
            return INFO;
        } else if (WARN_STR.equals(levelName)) {
            return WARN;
        } else if (ERROR_STR.equals(levelName)) {
            return ERROR;
        } else if (FATAL_STR.equals(levelName)) {
            return FATAL;
        } else {
            return defaultS;
        }
    }
}
