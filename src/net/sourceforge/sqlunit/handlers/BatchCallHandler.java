/*
 * $Id: BatchCallHandler.java,v 1.8 2004/09/28 19:15:45 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/BatchCallHandler.java,v $
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
import net.sourceforge.sqlunit.HandlerFactory;
import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.beans.BatchDatabaseResult;
import net.sourceforge.sqlunit.beans.Param;
import net.sourceforge.sqlunit.utils.TypeUtils;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.List;

/**
 * The BatchCallHandler class processes the contents of a batchcall tag in the 
 * input XML file.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.8 $
 * @sqlunit.parent name="batchtest" ref="batchtest"
 * @sqlunit.element name="batchcall" 
 *  description="The batchcall tag allows the SQLUnit client to run a 
 *  single stored procedure or parameterized SQL with a set of different
 *  input arguments. It uses JDBC batching to do this."
 *  syntax="((stmt), (paramset)+)"
 * @sqlunit.attrib name="connection-id"
 *  description="When multiple Connections are defined for a SQLUnit test,
 *  this attribute refers to the Connection to use for running this call.
 *  If not specified, SQLUnit will try to look up a Connection which has
 *  no connection-id attribute specified."
 *  required="No"
 * @sqlunit.child name="stmt"
 *  description="Contains the SQL statement or stored procedure to call
 *  in the body."
 *  required="Yes" ref="none"
 * @sqlunit.child name="param"
 *  description="Specifies a set of param elements"
 *  required="At least one needs to be provided"
 *  ref="param"
 * @sqlunit.example name="A typical batchcall tag"
 *  description="
 *  <batchcall>{\n}
 *  {\t}<stmt>insert into customer values(?,?,?,?,?,?,now())</stmt>{\n}
 *  {\t}{\t}<paramset id=\"1\">{\n}
 *  {\t}{\t}{\t}<param id=\"1\" type=\"INTEGER\">1</param>{\n}
 *  {\t}{\t}{\t}<param id=\"2\" type=\"VARCHAR\">Some One</param>{\n}
 *  {\t}{\t}{\t}<param id=\"3\" type=\"VARCHAR\">secret</param>{\n}
 *  {\t}{\t}{\t}<param id=\"4\" type=\"VARCHAR\">en_US</param>{\n}
 *  {\t}{\t}{\t}<param id=\"5\" type=\"VARCHAR\">SQLUnit</param>{\n}
 *  {\t}{\t}{\t}<param id=\"6\" type=\"INTEGER\">0</param>{\n}
 *  {\t}{\t}</paramset>{\n}
 *  {\t}{\t}<paramset id=\"2\">{\n}
 *  {\t}{\t}{\t}<param id=\"1\" type=\"INTEGER\">2</param>{\n}
 *  {\t}{\t}{\t}<param id=\"2\" type=\"VARCHAR\">Someone Else</param>{\n}
 *  {\t}{\t}{\t}<param id=\"3\" type=\"VARCHAR\">secret</param>{\n}
 *  {\t}{\t}{\t}<param id=\"4\" type=\"VARCHAR\">en_US</param>{\n}
 *  {\t}{\t}{\t}<param id=\"5\" type=\"VARCHAR\">SQLUnit</param>{\n}
 *  {\t}{\t}{\t}<param id=\"6\" type=\"INTEGER\">0</param>{\n}
 *  {\t}{\t}</paramset>{\n}
 *  </batchcall>
 *  "
 */
public class BatchCallHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(BatchCallHandler.class);

    /**
     * Runs the batch of paramterized SQL statements or stored procedures
     * supplied as a batch and returns a BatchDatabaseResult object.
     * @param elBatchCall the JDOM Element representing the batchcall tag.
     * @return a DatabaseResult object.
     * @exception Exception if there was a problem running the SQL.
     */
    public final Object process(final Element elBatchCall) throws Exception {
        LOG.debug(">> process()");
        if (elBatchCall == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"batchcall"});
        }
        String connectionId = XMLUtils.getAttributeValue(
            elBatchCall, "connection-id");
        Connection conn = ConnectionRegistry.getConnection(connectionId);
        if (conn == null) {
            throw new SQLUnitException(IErrorCodes.CONNECTION_IS_NULL,
               new String[]{(connectionId == null ? "DEFAULT" : connectionId)});
        }
        BatchDatabaseResult result = new BatchDatabaseResult();
        try {
            // Create a batch
            Element elStmt = elBatchCall.getChild("stmt");
            if (elStmt == null) {
                throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                    new String[] {"stmt"});
            }
            String sql = XMLUtils.getText(elStmt);
            PreparedStatement ps = conn.prepareStatement(sql);
            // Get the list of paramsets
            List elParamSetList = elBatchCall.getChildren("paramset");
            Iterator paramSetIter = elParamSetList.iterator();
            while (paramSetIter.hasNext()) {
                Element elParamSet = (Element) paramSetIter.next();
                IHandler paramSetHandler = HandlerFactory.getInstance(
                    elParamSet.getName());
                Param[] params = (Param[]) paramSetHandler.process(elParamSet);
                for (int j = 0; j < params.length; j++) {
                    if (params[j].isNull()) {
                        ps.setNull(j + 1, params[j].getSQLType());
                    } else {
                        Object convertedParamValue = 
                            TypeUtils.convertToObject(
                            params[j].getValue(), params[j].getType());
                        ps.setObject(j + 1, convertedParamValue, 
                            params[j].getSQLType());
                    }
                }
                ps.addBatch();
            }
            int[] updateCounts = ps.executeBatch();
            for (int i = 0; i < updateCounts.length; i++) {
                result.addUpdateCount(updateCounts[i]);
            }
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
        } catch (SQLUnitException e) {
            ConnectionRegistry.invalidate(connectionId);
            throw e;
        } catch (Exception e) {
            ConnectionRegistry.invalidate(connectionId);
            throw new SQLUnitException(IErrorCodes.GENERIC_ERROR, 
                new String[] {"System", e.getClass().getName(),
                e.getMessage()}, e);
        }
    }
}
