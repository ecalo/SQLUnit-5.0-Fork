/*
 * $Id: SqlHandler.java,v 1.23 2005/12/17 18:59:16 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/SqlHandler.java,v $
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
import net.sourceforge.sqlunit.SymbolTable;
import net.sourceforge.sqlunit.beans.DatabaseResult;
import net.sourceforge.sqlunit.beans.Param;
import net.sourceforge.sqlunit.beans.ResultSetBean;
import net.sourceforge.sqlunit.utils.TypeUtils;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * The SqlHandler class processes the contents of an sql tag in the input
 * XML file.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.23 $
 * @sqlunit.parent name="foreach" ref="foreach"
 * @sqlunit.parent name="setup" ref="setup"
 * @sqlunit.parent name="test" ref="test"
 * @sqlunit.parent name="diff" ref="diff"
 * @sqlunit.parent name="teardown" ref="teardown"
 * @sqlunit.element name="sql"
 *  description="The sql tag describes a regular SQL statement (not a stored
 *  procedure) with or without replaceable parameters. It can be used to 
 *  describe a stored function call for databases that support it. It is
 *  converted internally to a JDBC PreparedStatement object. SQL specified
 *  by the sql statement can return a result with a single resultset or
 *  an updatecount."
 *  syntax="(stmt, (param)*)"
 * @sqlunit.attrib name="id"
 *  description="The name or number of the sql call"
 *  required="No"
 * @sqlunit.attrib name="connection-id"
 *  description="When multiple Connections are defined in a SQLUnit test,
 *  this attribute refers to the Connection to use for running this call.
 *  If not specified, SQLUnit tries to look up the default Connection."
 *  required="No"
 * @sqlunit.child name="stmt"
 *  description="Specifies the actual SQL statement, with or without
 *  replaceable parameters in the body of the tag."
 *  required="Yes"
 *  ref="none"
 * @sqlunit.child name="param"
 *  description="Occurs once for each replaceable parameter, if specified,
 *  for the SQL string."
 *  required="Yes, if there are replaceable parameters in the SQL string."
 *  ref="param"
 * @sqlunit.example name="Example of a simple sql tag"
 *  description="
 *  <sql>{\n}
 *  {\t}<stmt>select custId from customer where custId=?</stmt>{\n}
 *  {\t}<param id=\"1\" type=\"INTEGER\">1</param>{\n}
 *  </sql>
 *  "
 */
public class SqlHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(SqlHandler.class);

    /**
     * Runs the SQL statement contained in the sql tag in the input XML file.
     * @param elSql the JDOM Element representing the sql tag.
     * @return a DatabaseResult created as a result of running the SQL.
     * @exception Exception if there was a problem running the SQL.
     */
    public Object process(final Element elSql) throws Exception {
        LOG.debug(">> process(elSql)");
        if (elSql == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"sql"});
        }
        String connectionId = XMLUtils.getAttributeValue(
            elSql, "connection-id");
        // get the sql statement
        Element elStmt = elSql.getChild("stmt");
        String stmt = XMLUtils.getText(elStmt);
        stmt = SymbolTable.replaceVariables(stmt);
        // get the arguments
        List elParamList = elSql.getChildren("param");
        Param[] params = new Param[elParamList.size()];
        if (params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                Element elParam = (Element) elParamList.get(i);
                IHandler paramHandler = HandlerFactory.getInstance(
                    elParam.getName());
                params[i] = (Param) paramHandler.process(elParam);
            }
        }
        return executeSQL(connectionId, stmt, params);
    }

    /**
     * Executes JDBC calls to build the DatabaseResult object and returns it.
     * @param connectionId the connection id string.
     * @param stmt the SQL statement.
     * @param params an array of Param objects.
     * @return a DatabaseResult object.
     * @exception Exception if there was a problem building the DatabaseResult.
     */
    protected Object executeSQL(final String connectionId, final String stmt, 
            final Param[] params) throws Exception {
        LOG.debug("executeSQL(" + connectionId + "," + stmt + ",params)");
        Connection conn = null;
        DatabaseResult result = null;
        try {
            conn = acquireConnection(connectionId);
            Statement ps = acquireStatementObject(conn, stmt);
            ps = setParameters(ps, params);
            result = initDatabaseResult();
            try { 
                executeQuery(ps, result);
                setOutputParameters(ps, params, result);
                ConnectionRegistry.safelyCommit(connectionId, conn);
            } catch (SQLException e) {
                // get the output parameters if we can
                try {
                    setOutputParameters(ps, params, result);
                } catch (SQLException e1) {
                    // :IGNORE: if the driver does not allow it, dont show
                    LOG.debug("Cannot set output parameters, "
                        + e.getErrorCode() + ":" + e.getMessage());
                }
                ConnectionRegistry.safelyRollback(connectionId, conn);
                ConnectionRegistry.invalidate(connectionId);
                result.resetAsException(
                    (new Integer(e.getErrorCode())).toString(), e.getMessage());
            } catch (Exception e) {
                clrOutputParameters(ps, params);
                throw e;
            } finally {
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException e) {
                        // :IGNORE: not necessary to trap this
                    }
                }
            }
        } catch (Exception e) {
            if (conn != null) {
                ConnectionRegistry.safelyRollback(connectionId, conn);
                ConnectionRegistry.invalidate(connectionId);
            }
            if (e instanceof SQLUnitException) {
                SymbolTable.setObject(SymbolTable.FAILURE_MESSAGE_OBJ, e);
                throw e;
            } else {
                throw new SQLUnitException(IErrorCodes.GENERIC_ERROR,
                    new String[] {"System", e.getClass().getName(),
                    e.getMessage()}, e);
            }
        }
        return result;
    }

    /**
     * Acquires a Connection object with the specified connectionId.
     * @param connectionId the connectionId to look up the ConnectionRegistry.
     * @return a Connection object.
     * @exception Exception if there was a problem getting the Connection.
     */
    protected Connection acquireConnection(final String connectionId) 
            throws Exception {
        Connection conn = ConnectionRegistry.getConnection(connectionId);
        return conn;
    }

    /**
     * Builds the Statement object from the Connection and the SQL string.
     * This is meant to be overriden on a class by class basis, so for
     * example, since we know that we will be dealing with a PreparedStatement
     * in this handler, we will override this method to build and return
     * a PreparedStatement object.
     * @param conn the Connection object.
     * @param stmt the SQL string.
     * @return a Statement object.
     * @exception Exception if there was a problem building the Statement.
     */
    protected Statement acquireStatementObject(final Connection conn, 
            final String stmt) throws Exception {
        return conn.prepareStatement(stmt);
    }

    /**
     * Sets replaceable parameters, if any, into the Statement object.
     * Note that if there are no param elements, then an empty Param[]
     * must be supplied. Updates the Statement object in place.
     * @param ps the Statement object.
     * @param params an array of Param objects.
     * @return a Statement object which should be cast to PreparedStatement.
     * @exception Exception if there was a problem setting parameters.
     */
    protected Statement setParameters(final Statement ps, final Param[] params) 
            throws Exception {
        PreparedStatement tps = (PreparedStatement) ps;
        for (int i = 0; i < params.length; i++) {
            if (params[i].isNull()) {
                tps.setNull(i + 1, params[i].getSQLType());
            } else {
                Object convertedParamValue = TypeUtils.convertToObject(
                    params[i].getValue(), params[i].getType());
                tps.setObject(i + 1, convertedParamValue, 
                    params[i].getSQLType());
            }
        }
        return tps;
    }

    /**
     * Initializes a DatabaseResult object for use by the Statement after
     * it finishes executing.
     * @return a new empty DatabaseResult object.
     */
    protected DatabaseResult initDatabaseResult() {
        DatabaseResult result = new DatabaseResult();
        return result;
    }

    /**
     * Executes the SQL query with a prefilled Statement object, and 
     * populates the DatabaseResult object in place.
     * @param ps the Statement object.
     * @param result the DatabaseResult object.
     * @exception Exception if there was a problem in the execute().
     */
    protected void executeQuery(final Statement ps, 
            final DatabaseResult result) throws Exception {
        int rsId = 1;
        int rowId = 1;
        boolean isCallable = (ps instanceof CallableStatement);
        boolean hasResults = (isCallable 
            ? ((CallableStatement) ps).execute() 
            : ((PreparedStatement) ps).execute());
        ResultSet rs;
        int rowsUpdated = -1;
        List rsblist = new ArrayList();
        int resultSetId = 1;
        do {
            if (hasResults) {
                rs = ps.getResultSet();
                ResultSetBean rsb = new ResultSetBean(rs, resultSetId);
                rsblist.add(rsb);
            } else {
                rowsUpdated = ps.getUpdateCount();
                // According to JDBC 2.0 spec, updatecounts can be returned
                // from DDL, such as CREATE/DROP/INSERT/UPDATE/DELETE, which
                // can be embedded within stored procedures, and so it is
                // not as meaningless as the documentation in the previous
                // version insisted. The previous version only set the update
                // count into the result object if the isCallable was false.
                // :NOTE: reverted back to previous version, to undo the
                // revert simply make the if condition in the next line 
                // unconditional.
                if (!isCallable) { result.setUpdateCount(rowsUpdated); }
            }
            hasResults = ps.getMoreResults();
            ps.clearWarnings();
            resultSetId++;
        } while (isMoreResultsAvailable(hasResults, rowsUpdated));
        // convert it to ResultSetBean[] and set into DatabaseResult
        ResultSetBean[] rsbs = new ResultSetBean[rsblist.size()];
        for (int i = 0; i < rsbs.length; i++) {
            rsbs[i] = (ResultSetBean) rsblist.get(i);
        }
        result.setResultSets(rsbs);
    }

    /**
     * Returns whether there are more resultsets to loop through in the
     * returned result from the SQL call. Since we are dealing with a
     * PreparedStatement, this will always be false.
     * @param hasResults a boolean generated from ps.execute().
     * @param rowsUpdated an int generated from ps.getUpdateCount().
     * @return true if we should look for more results in this call.
     */
    protected boolean isMoreResultsAvailable(final boolean hasResults, 
            final int rowsUpdated) {
        return false;
    }

    /**
     * Sets the output parameters into the DatabaseResult. This is a no-op
     * in the case of this handler, but will contain code for the CallHandler.
     * @param ps the Statement object.
     * @param params the array of Param objects.
     * @param result the DatabaseResult object.
     * @exception Exception if one is thrown by this method.
     */
    protected void setOutputParameters(final Statement ps, 
            final Param[] params, final DatabaseResult result) 
            throws Exception {
        return;
    }

    /**
     * Clears output parameter symbol table variables. This is a no-op
     * in case of this handler, but will contain code for the CallHandler.
     * @param ps the Statement object.
     * @param params the array of Param objects.
     * @exception Exception if one is thrown by this method.
     */
    protected void clrOutputParameters(final Statement ps, final Param[] params)
            throws Exception {
        return;
    }
}
