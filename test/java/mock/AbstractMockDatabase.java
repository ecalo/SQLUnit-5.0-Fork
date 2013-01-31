/*
 * $Id: AbstractMockDatabase.java,v 1.6 2006/01/07 02:27:23 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/mock/AbstractMockDatabase.java,v $
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

import com.mockrunner.mock.jdbc.MockResultSet;

import org.apache.log4j.Logger;

import java.lang.reflect.Method;

/**
 * Abstract base class from which all IMockDatabase objects must extend.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.6 $
 */
public abstract class AbstractMockDatabase implements IMockDatabase {

    private static final Logger LOG = Logger.getLogger(
        AbstractMockDatabase.class);

    /**
     * Delegates to the named queryMethodName and returns the MockResultSet
     * object generated from it.
     * @param methodName the name of the method to invoke.
     * @param resultSetId the id of a single resultset in case of multiple.
     * @return a MockResultSet object.
     * @exception Exception if one is thrown.
     */
    public final MockResultSet getResultSet(final String methodName, 
            final int resultSetId) throws Exception {
        LOG.debug(">> getResultSet(" + methodName + "," + resultSetId + ")");
        Method method = this.getClass().getMethod(
            methodName, new Class[] {Integer.class});
        MockResultSet rs = (MockResultSet) method.invoke(
            this, new Object[] {new Integer(resultSetId)});
        return rs;
    }

    /**
     * Returns the default (1st) resultset from the list of resultsets.
     * @param methodName the name of the method to invoke.
     * @return the first (or only) MockResultSet object.
     * @exception Exception if one is thrown.
     */
    protected MockResultSet getResultSet(final String methodName) 
            throws Exception {
        return getResultSet(methodName, 1);
    }
}
