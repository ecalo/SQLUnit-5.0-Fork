/*
 * $Id: IMockDatabase.java,v 1.2 2004/10/05 20:13:04 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/mock/IMockDatabase.java,v $
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

/**
 * Provides a convenient abstraction of what a mock database should look
 * like for SQLUnit mock testing.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.2 $
 */
public interface IMockDatabase {

    /**
     * Returns a MockResultSet given the method name to query and the
     * resultset id desired. Note that this method is implemented in the
     * AbstractMockDatabase class. Subclasses will need to provide a 
     * set of private methods that will actually return the mock resultset
     * object. This method essentially delegates to the named method at
     * runtime. Named methods must have the following signature:
     * <code>public MockResultSet method(Integer resultsetId);</code>
     * @param methodName the name of the private method.
     * @param resultSetId which resultset to return in case of multiple
     * resultsets. The index is 1-based, the default for this parameter is 1.
     * @return a MockResultSet object.
     * @exception Exception if one is thrown in the delegated method.
     */
    MockResultSet getResultSet(String methodName, int resultSetId)
        throws Exception;
}
