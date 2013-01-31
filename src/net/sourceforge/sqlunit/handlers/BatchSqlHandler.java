/*
 * $Id: BatchSqlHandler.java,v 1.7 2004/09/28 19:15:45 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/BatchSqlHandler.java,v $
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
package net.sourceforge.sqlunit.handlers;

import net.sourceforge.sqlunit.ConnectionRegistry;
import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.beans.BatchDatabaseResult;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

/**
 * The BatchSqlHandler class processes the contents of a batchsql tag in the 
 * input XML file.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.7 $
 * @sqlunit.parent name="batchtest" ref="batchtest"
 * @sqlunit.element name="batchsql"
 *  description="The batchsql tag allows the client to specify a set of 
 *  non-parameterized SQL statements which need to run in a batch. SQLUnit
 *  uses the JDBC batching mechanism to run the set of SQL statements."
 *  syntax="((stmt)+)"
 * @sqlunit.attrib name="connection-id"
 *  description="When multiple Connections are defined in a SQLUnit test,
 *  this attribute provides a mechanism for choosing one of the defined
 *  Connections to use for running this batch. If a connection-id is not
 *  specified, SQLUnit will try to look up a Connection which has no
 *  connection-id."
 *  required="No"
 * @sqlunit.child name="stmt"
 *  description="Contains the SQL statement or stored procedure call in
 *  the body of the element"
 *  required="One or more stmt elements need to be specified"
 *  ref="none"
 * @sqlunit.example name="A typical batchsql call"
 *  description="
 *  <batchsql>{\n}
 *  {\t}<stmt>delete from customer where custId=1</stmt>{\n}
 *  {\t}<stmt>insert into customer values(1,'Some One','secret',{\n}
 *  {\t}{\t}'en_US',null,0,now())</stmt>{\n}
 *  {\t}<stmt>insert into customer values(1,'Someone Else','secret',{\n}
 *  {\t}{\t}'en_US',null,0,now())</stmt>{\n}
 *  {\t}<stmt>delete from customer where 1=1</stmt>{\n}
 *  </batchsql>
 *  "
 */
public class BatchSqlHandler implements IHandler {

    private static final Logger LOG = 
        Logger.getLogger(BatchSqlHandler.class.getName());

    /**
     * Runs the batch of SQL statements supplied as a batch and returns
     * a BatchDatabaseResult object.
     * @param elBatchSql the JDOM Element representing the batchsql tag.
     * @return the DatabaseResult returned from executing the SQL.
     * @exception Exception if there was a problem running the SQL.
     */
    public final Object process(final Element elBatchSql) throws Exception {
        LOG.debug(">> process()");
        if (elBatchSql == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"batchsql"});
        }
        String connectionId = XMLUtils.getAttributeValue(
            elBatchSql, "connection-id");
        Connection conn = ConnectionRegistry.getConnection(connectionId);
        if (conn == null) {
            throw new SQLUnitException(IErrorCodes.CONNECTION_IS_NULL,
               new String[]{(connectionId == null ? "DEFAULT" : connectionId)});
        }
        int numStmts = -1;
        BatchDatabaseResult result = new BatchDatabaseResult();
        try {
            // Create a batch
            Statement batchStmt = conn.createStatement();
            // Parse the element to get individual stmt elements, and 
            // add them to the batch
            List elStmtList = elBatchSql.getChildren("stmt");
            numStmts = elStmtList.size();
            for (int i = 0; i < numStmts; i++) {
                Element elStmt = (Element) elStmtList.get(i);
                String sql = XMLUtils.getText(elStmt);
                batchStmt.addBatch(sql);
            }
            // execute the batch
            int[] updateCounts = batchStmt.executeBatch();
            // parse the results into a BatchDatabaseResult object
            for (int i = 0; i < updateCounts.length; i++) {
                result.addUpdateCount(updateCounts[i]);
            }
            // and return it
            return result;
        } catch (BatchUpdateException e) { 
            int[] updateCounts = e.getUpdateCounts();
            result.resetUpdateCounts();
            for (int i = 0; i < updateCounts.length; i++) {
                result.addUpdateCount(updateCounts[i]);
            }
            BatchDatabaseResult.setException(e);
            ConnectionRegistry.invalidate(connectionId);
            return result;
        } catch (Exception e) {
            ConnectionRegistry.invalidate(connectionId);
            throw new SQLUnitException(IErrorCodes.GENERIC_ERROR,
                new String[] {"System", e.getClass().getName(),
                e.getMessage()}, e);
        }
    }
}
