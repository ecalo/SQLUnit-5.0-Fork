/*
 * $Id: EmptyReporter.java,v 1.1 2005/07/28 01:27:28 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/reporters/EmptyReporter.java,v $
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
package net.sourceforge.sqlunit.reporters;

import java.util.Map;

import net.sourceforge.sqlunit.IReporter;

/**
 * An empty reporter implementation. Useful for test cases.
 * @author Ivan Ivanov
 * @version $Revision: 1.1 $
 */
public class EmptyReporter implements IReporter {

    public EmptyReporter(String filename) {}

    public String getName() {
        return "EmptyReporter";
    }

    public boolean hasContainer() {
        return false;
    }

    public void newTestFile(String testName, String testFile) {}

    public void settingUpConnection(String connectionId) {}

    public void setConfig(Map config) {}

    public void setUp() {}

    public void runningTest(String name, int testIndex, String desc) {}

    public void finishedTest(long elapsed, boolean success) {}

    public void skippedTest(String name, int testIndex, String desc) {}

    public void addFailure(Throwable th, boolean error) {}

    public void tearDown() {}

    public void tempFile(int testId, String result, String file) {}

    public void testFileComplete(boolean success) {}

    public void echo(String message) {}
}
