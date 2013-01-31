/*
 * $Id: CallHandler.java,v 1.15 2006/06/25 23:02:50 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/CallHandler.java,v $
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

import net.sourceforge.sqlunit.HandlerFactory;
import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.IType;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.SymbolTable;
import net.sourceforge.sqlunit.TypeFactory;
import net.sourceforge.sqlunit.TypeMapper;
import net.sourceforge.sqlunit.beans.DatabaseResult;
import net.sourceforge.sqlunit.beans.OutParam;
import net.sourceforge.sqlunit.beans.Param;
import net.sourceforge.sqlunit.beans.ResultSetBean;
import net.sourceforge.sqlunit.beans.StructBean;
import net.sourceforge.sqlunit.types.BigDecimalType;
import net.sourceforge.sqlunit.utils.TypeUtils;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * The CallHandler class processes the contents of a call tag in the input
 * XML file.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.15 $
 * @sqlunit.parent name="test" ref="test"
 * @sqlunit.parent name="diff" ref="diff"
 * @sqlunit.element name="call"
 *  description="The call tag is used to describe a call to a stored procedure
 *  or function. A stored procedure differs from a standard SQL call in that
 *  it can return multiple result sets in a single result. Not all databases
 *  support this functionality in the same way. Stored procedures are called
 *  within Java code using the standard call syntax that looks like 
 *  {call [?=]stored_procedure_name([?,...])}. This is also the way it should
 *  be specified to SQLUnit. SQLUnit uses the CallableStatement to execute
 *  this call"
 *  syntax="(stmt, (param)*)"
 * @sqlunit.attrib name="id"
 *  description="An internal id for the stored procedure call"
 *  required="No"
 * @sqlunit.attrib name="connection-id"
 *  description="Used to specify a particular connection when multiple
 *  Connections are specified in a given test. If not specified, SQLUnit
 *  will try to use the default Connection, if one exists"
 *  required="No"
 * @sqlunit.child name="stmt"
 *  description="Specifies the actual stored procedure call in JDBC format
 *  described above in the body of this element"
 *  required="Yes"
 *  ref="none"
 * @sqlunit.child name="param"
 *  description="A param element specifies a single replaceable parameter
 *  specified for the stored procedure call."
 *  required="Zero or more"
 *  ref="param"
 * @sqlunit.example name="Call to Stored Procedure dept_id=AddDept(dept_name)"
 *  description="
 *  <call>{\n}
 *  {\t}<stmt>{? = call AddDept(?)}</stmt>{\n}
 *  {\t}<param id=\"1\" type=\"INTEGER\" inout=\"out\">${deptId}</param>{\n}
 *  {\t}<param id=\"2\" type=\"VARCHAR\">Information Technology</param>{\n}
 *  </call>
 *  "
 */
public class CallHandler extends SqlHandler {

    private static final Logger LOG = Logger.getLogger(CallHandler.class);

    /**
     * Runs the SQL Stored Procedure contained in the call tag in the input
     * XML file. Returns a DatabaseResult object.
     * @param elCall the JDOM Element representing the call tag.
     * @return the DatabaseResult returned from executing the stored procedure.
     * @exception Exception if there was a problem running the SQL.
     */
    public final Object process(final Element elCall) throws Exception {
        LOG.debug(">> process()");
        if (elCall == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL, 
                new String[] {"call"});
        }
        String connectionId = XMLUtils.getAttributeValue(
            elCall, "connection-id");
        // get the sql statement
        Element elStmt = elCall.getChild("stmt");
        String callSql = XMLUtils.getText(elStmt);
        callSql = SymbolTable.replaceVariables(callSql);
        // get arguments
        List elParamList = elCall.getChildren("param");
        Param[] params = new Param[elParamList.size()];
        if (params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                Element elParam = (Element) elParamList.get(i);
                IHandler paramHandler = HandlerFactory.getInstance(
                    elParam.getName());
                params[i] = (Param) paramHandler.process(elParam);
            }
        }
        // use the code in the superclass (which is a sequence of protected
        // method calls) and override only the portions that we need below).
        return executeSQL(connectionId, callSql, params);
    }

    /**
     * Overrides the parent method to build a CallableStatement object from
     * the Connection and SQL string.
     * @param conn the Connection object.
     * @param stmt the SQL string.
     * @return a Statement object.
     * @exception Exception if there was a problem building the Statement.
     */
    protected final Statement acquireStatementObject(final Connection conn, 
            final String stmt) throws Exception {
        return conn.prepareCall(stmt);
    }

    /**
     * Overrides the parent method to set replaceable parameters, if any, 
     * into the CallableStatement object. CallableStatements may have to 
     * register OUT parameters, as well as set IN and INOUT parameters.
     * @param cs the Statement object.
     * @param params the array of Param objects.
     * @return a Statement object which should be cast to CallableStatement.
     * @exception Exception if there was a problem setting the parameters.
     */
    protected final Statement setParameters(final Statement cs, 
            final Param[] params) throws Exception {
        CallableStatement tcs = (CallableStatement) cs;
        for (int i = 0; i < params.length; i++) {
            // :NOTE: order is important here. For INOUT which will enter
            // both blocks, we need to set the value and then register the
            // parameter
            if (params[i].isInParameter()) {
                if (params[i].isNull()) {
                    tcs.setNull(i + 1, params[i].getSQLType());
                } else {
                    Object convertedParamValue = TypeUtils.convertToObject(
                        params[i].getValue(), params[i].getType());
                    tcs.setObject(i + 1, convertedParamValue, 
                        params[i].getSQLType());
                }
            }
            if (params[i].isOutParameter()) {
                if (SymbolTable.isVariableName(params[i].getValue())) {
                    SymbolTable.setValue(SymbolTable.OUT_PARAM + tcs.hashCode()
                        + ":" + i + "}", params[i].getValue());
                }
                String className = TypeMapper.findClassByName(
                    params[i].getType());
                if (BigDecimalType.class.getName().equals(className)) {
                    int scale = params[i].getScale();
                    tcs.registerOutParameter(i + 1, params[i].getSQLType(),
                        scale);
                } else if (params[i].getTypeName() != null) {
                    tcs.registerOutParameter(i + 1, params[i].getSQLType(),
                        params[i].getTypeName());
                } else {
                    tcs.registerOutParameter(i + 1, params[i].getSQLType());
                }
            }
        }
        return tcs;
    }

    /**
     * Overrides the parent method to return true if more results are 
     * available. CallableStatements may return multiple result sets, so 
     * this test actually ORs the values it is passed in.
     * @param hasResults boolean generated from cs.execute().
     * @param rowsUpdated int generated from cs.getUpdateCount().
     * @return true if there are more resultsets, else false.
     */
    protected final boolean isMoreResultsAvailable(final boolean hasResults, 
            final int rowsUpdated) {
        return (hasResults || rowsUpdated != -1);
    }

    /**
     * Overrides the parent method to set the OUT parameters into the
     * DatabaseResult object.
     * @param cs the Statement object.
     * @param params the array of Param objects.
     * @param result the DatabaseResult object.
     * @exception Exception if there was a problem setting the OUT params.
     */
    protected final void setOutputParameters(final Statement cs, 
            final Param[] params, final DatabaseResult result) 
            throws Exception {
        CallableStatement tcs = (CallableStatement) cs;
        // get a count of outparams
        List outParamList = new ArrayList();
        for (int i = 0; i < params.length; i++) {
            if (params[i].isOutParameter()) {
                OutParam outParam = new OutParam();
                outParam.setId(params[i].getId());
                outParam.setName(params[i].getName());
                outParam.setType(params[i].getType());
                String outParamSymbol = SymbolTable.removeSymbol(
                    SymbolTable.OUT_PARAM + tcs.hashCode() + ":" + i + "}");
                // get the value from the CallableStatement
                Object value = tcs.getObject(i + 1);
                if (value instanceof ResultSet) {
                    // value is a resultset
                    ResultSetBean rsb = new ResultSetBean((ResultSet) value, 1);
                    outParam.setValue(rsb);
                    if (outParamSymbol != null) {
                        SymbolTable.setObject(outParamSymbol, rsb);
                    }
                } else if (params[i].getType().endsWith("STRUCT") ) {
                    // value is a user-defined type (UDT)
                    StructBean sb = new StructBean(value); 
                    outParam.setValue(sb);
                    if (outParamSymbol != null) {
                        SymbolTable.setObject(outParamSymbol, sb);
                    }
                } else {
                    // value is a String
                    IType type = TypeFactory.getInstance(params[i].getType());
                    outParam.setValue(type.toString(value));
                    if (outParamSymbol != null) {
                        SymbolTable.setValue(outParamSymbol,
                            type.toString(value));
                    }
                }
                outParamList.add(outParam);
            }
        }
        OutParam[] ops = new OutParam[outParamList.size()];
        for (int i = 0; i < ops.length; i++) {
            ops[i] = (OutParam) outParamList.get(i);
        }
        result.setOutParams(ops);
    }

    /**
     * Overrides the parent method to clear parameters in the DatabaseResult
     * object.
     * @param cs the Statement object.
     * @param params the array of Param objects.
     * @exception Exception if there was a problem clearing the out parameters.
     */
    protected final void clrOutputParameters(final Statement cs,
            final Param[] params) throws Exception {
        CallableStatement tcs = (CallableStatement) cs;
        for (int i = 0; i < params.length; i++) {
            if (params[i].isOutParameter()) {
                SymbolTable.removeSymbol(SymbolTable.OUT_PARAM + tcs
                    + ":" + i + "}");
            }
        }
    }
}
