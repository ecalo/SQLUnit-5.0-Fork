/*
 * $Id: SymbolTable.java,v 1.44 2006/06/25 22:42:32 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/SymbolTable.java,v $
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.sqlunit.beans.Col;
import net.sourceforge.sqlunit.beans.DatabaseResult;
import net.sourceforge.sqlunit.beans.OutParam;
import net.sourceforge.sqlunit.beans.ResultSetBean;
import net.sourceforge.sqlunit.beans.Row;
import net.sourceforge.sqlunit.parsers.ParseException;
import net.sourceforge.sqlunit.parsers.SymbolParser;

import org.apache.log4j.Logger;

/**
 * Models a HashMap as a symbol table to store temporary variables and their
 * values that are needed for a SQLUnit test case.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.44 $
 */
public final class SymbolTable {
    
    private static final Logger LOG = Logger.getLogger(SymbolTable.class);

    private static Map symbols = new HashMap();

    /** Indicates if Java Object Support is enabled */
    public static final String JAVA_OBJECT_SUPPORT = "${__JavaObjectSupport__}";
    /** Contains the failure message if available */
    public static final String FAILURE_MESSAGE_OBJ = "${__FailureMessage__}";
    /** Contains the debugging logger if set */
    public static final String DEBUG_LOGGER = "${__debuglogger__}";
    /** Indicates that the value is a LOB file name */
    public static final String FILE_HEADER = "${__file:";
    /** Contains outparam index mapping header */
    public static final String OUT_PARAM = "${__OutParam:";
    /** Contains elapsed time for a test in milliseconds */
    public static final String TEST_ELAPSED_TIME = "${__ElapsedMillisStr__}";
    /** Keeps track of current resultset */
    public static final String CURRENT_RESULTSET_KEY = "${__TID.resultset__}";
    /** Keeps track of current row in resultset */
    public static final String CURRENT_ROW_KEY = "${__TID.row__}";
    /** Keeps track of current column in row */
    public static final String CURRENT_COL_KEY = "${__TID.col__}";
    /** Holds a reference to the Reporter object */
    public static final String REPORTER_KEY = "${__Reporter__}";
    /** Holds a reference to the most recent skipped reason */
    public static final String SKIP_REASON = "${__SkipReason__}";

    /**
     * Private Constructor. Cannot be instantiated.
     */
    private SymbolTable() {
        // private constructor, cannot be instantiated
    }

    /**
     * Returns an object keyed by the param string from the symbol table.
     * @param param the key into the symbol table.
     * @return an Object at that position, null if none is available.
     */
    public static synchronized Object getObject(final String param) {
        LOG.debug(">> getObject(" + param + ")");
        if (SymbolTable.isVariableName(param)) {
            return symbols.get(param);
        } else {
            return param;
        }
    }

    /**
     * Sets an object keyed by the param string into the symbol table.
     * @param param the key into the symbol table.
     * @param obj the object keyed by the param value.
     */
    public static synchronized void setObject(final String param,
            final Object obj) {
        LOG.debug(">> setObject(" + param + "," + obj.getClass().getName());
        symbols.put(param, obj);
    }

    /**
     * Returns the value of the named variable. The variable should look 
     * like ${varname}.
     * @param param the key into the symbol table.
     * @return the value of the named variable, or null if the variable
     * does not exist in the symbol table.
     */
    public static synchronized String getValue(final String param) {
        LOG.debug(">> getValue(" + param + ")");
        Object value = getObject(param);
        if (isVariableName(param)) {
            if (value != null) {
                if (value instanceof String) {
                    return (String) value;
                } else {
                    return value.toString();
                }
            } else {
                return (String) null;
            }
        } else {
            return param;
        }
    }

    /**
     * Updates the symbol table with the symbol's value if it exists or
     * creates a new entry in tha table with the given value if it does not.
     * @param param the variable name.
     * @param value the value of the variable.
     */
    public static synchronized void setValue(final String param, 
            final String value) {
        LOG.debug(">> setValue(" + param + "," + value + ")");
        symbols.put(param, value);
    }

    /**
     * Returns an Iterator containing all the symbols in the Symbol table.
     * @return an Iterator.
     */
    public static synchronized Iterator getSymbols() {
        LOG.debug(">> getSymbols()");
        return symbols.keySet().iterator();
    }

    /**
     * Removes a symbol from the symbol table.
     * @param param the variable name to remove.
     * @return the value of the variable, may be null if symbol does not exist.
     */
    public static synchronized String removeSymbol(final String param) {
        LOG.debug(">> removeSymbol(" + param + ")");
        return (String) symbols.remove(param);
    }

    /**
     * Updates the symbol table with variables from a target resultset that
     * are populated by a SQL or stored procedure call. The variables will
     * be prefixed by the value of namespace.
     * @param target the target DatabaseResult containing variables.
     * @param source the source DatabaseResult generated by a SQL or stored
     * procedure call.
     * @param namespace the namespace in which the variables will be stored.
     * @exception SQLUnitException if there was a problem.
     */
    public static synchronized void setSymbols(final DatabaseResult target,
            final DatabaseResult source, final String namespace) 
            throws SQLUnitException {
        LOG.debug(">> setSymbols(target,source," + namespace + ")");
        ResultSetBean[] rsbs = target.getResultSets();
        for (int i = 0; i < rsbs.length; i++) {
            Row[] rows = rsbs[i].getRows();
            for (int j = 0; j < rows.length; j++) {
                Col[] cols = rows[j].getCols();
                for (int k = 0; k < cols.length; k++) {
                    String colValue = cols[k].getValue();
                    if (SymbolTable.isVariableName(colValue)) {
                        // we were called from the SetHandler.process()
                        // add in the namespace to the column value
                        if (namespace != null) {
                            colValue = namespace + "." + colValue;
                            colValue = colValue.replaceAll("\\}\\.\\$\\{", ".");
                        }
                        try {
                            SymbolTable.setValue(colValue,
                                (((source.getResultSets()[i]).
                                getRows()[j]).getCols()[k]).getValue());
                        } catch (ArrayIndexOutOfBoundsException e) {
                            // do not update the variable, it will
                            // show up a NULL value later
                            LOG.warn("No value for " + colValue + " at result["
                                + (i + 1) + "," + (j + 1) + "," + (k + 1) 
                                + "]");
                        }
                    }
                }
            }
        }
    }

    /**
     * Scans the source DatabaseResult and the symbol table and updates
     * the target DatabaseResult object in place. The client will have
     * access to the updated target DatabaseResult since it is passed
     * by reference. The variables to be substituted will need to be 
     * specified in the form ${variable}.
     * @param source the source DatabaseResult returned from the SQL call.
     * @param target the target DatabaseResult to update.
     * @exception SQLUnitException if a variable does not appear in either
     * the source or the symbol table.
     */
    public static synchronized void update(final DatabaseResult target, 
            final DatabaseResult source) throws SQLUnitException {
        LOG.debug(">> update(target,source)");
        // update the outparams
        OutParam[] targetOutParams = target.getOutParams();
        OutParam[] sourceOutParams = source.getOutParams();
        for (int i = 0; i < targetOutParams.length; i++) {
            OutParam targetOutParam = targetOutParams[i];
            OutParam sourceOutParam = sourceOutParams[i];
            String targetValue = targetOutParam.getValue();
            String sourceValue = sourceOutParam.getValue();
            if (SymbolTable.isVariableName(targetValue)) {
                targetOutParam.setValue(SymbolTable.getValue(sourceValue));
            }
        }
        // then update the result beans
        ResultSetBean[] rsbs = target.getResultSets();
        for (int i = 0; i < rsbs.length; i++) {
            Row[] rows = rsbs[i].getRows();
            for (int j = 0; j < rows.length; j++) {
                Col[] cols = rows[j].getCols();
                for (int k = 0; k < cols.length; k++) {
                    // is this a variable, if not skip this
                    String colValue = cols[k].getValue();
                    LOG.debug("looking at colValue: " + colValue);
                    if (SymbolTable.isVariableName(colValue)) {
                        boolean isUpdated = false;
                        // replace with value from symbol table (rvalue)
                        String valueToSet = SymbolTable.getValue(colValue);
                        if (valueToSet != null) {
                            LOG.debug("replacing " + colValue + " with "
                                + valueToSet);
                            cols[k].setValue(valueToSet);
                            isUpdated = true;
                        }
                        // go to next one if the value is already set
                        if (isUpdated) { continue; }
                        // replace with value from source object (lvalue)
                        // and update the symbol table in case this needs
                        // to be used later
                        try {
                            valueToSet = (((source.getResultSets()[i]).
                                getRows()[j]).getCols()[k]).getValue();
                            cols[k].setValue(valueToSet);
                            SymbolTable.setValue(colValue, valueToSet);
                            isUpdated = true;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            // skip if the variable specified in the target
                            // does not exist in the source
                            LOG.warn("No value found for " + colValue
                                + " at result[" + (i + 1) + "," + (j + 1) 
                                + "," + (k + 1) + "]");
                            continue;
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        SymbolTable.dump();
    }

    /**
     * By definition, a string which matches the pattern ${var} is 
     * considered to be a variable name in SQLUnit.
     * @param str the String to check if it is a symbol.
     * @return true if the str is a symbol, false if not.
     */
    public static boolean isVariableName(final String str) {
        LOG.debug(">> isVariableName(" + str + ")");
        if (str == null) { return false; }
        return (str.startsWith("${") && str.endsWith("}"));
    }

    /**
     * Replaces all variables in a string from the symbol table.
     * @param text the text with replaceable variables.
     * @return the text with the variables replaced with the values.
     * @exception SQLUnitException if one or more variables is not found.
     */
    public static synchronized String replaceVariables(final String text)
            throws SQLUnitException {
        LOG.debug(">> replaceVariables(" + text + ")");
        try {
            return SymbolParser.parse(text, symbols, false);
        } catch (ParseException e) {
            throw new SQLUnitException(IErrorCodes.REPLACE_ERROR,
                new String[] {text, e.getMessage()}, e);
        }
    }

    /**
     * Sets the current resultset for the test.
     * @param resultSetId the id of the resultset tag.
     */
    public static void setCurrentResultSet(final String resultSetId) {
        LOG.debug(">> setCurrentResultSet(" + resultSetId + ")");
        SymbolTable.setValue(CURRENT_RESULTSET_KEY.replaceAll(
            "TID", ThreadIdentifier.getIdentifier()), resultSetId);
    }

    /**
     * Sets the current row for the test.
     * @param rowId the id of the row tag.
     */
    public static void setCurrentRow(final String rowId) {
        LOG.debug(">> setCurrentRow(" + rowId + ")");
        SymbolTable.setValue(CURRENT_ROW_KEY.replaceAll(
            "TID", ThreadIdentifier.getIdentifier()), rowId);
    }

    /**
     * Sets the current col for the test.
     * @param colId the id of the col tag.
     */
    public static void setCurrentCol(final String colId) {
        LOG.debug(">> setCurrentCol(" + colId + ")");
        SymbolTable.setValue(CURRENT_COL_KEY.replaceAll(
            "TID", ThreadIdentifier.getIdentifier()), colId);
    }

    /**
     * Returns the current result key as a String. The format of the String
     * is result[resultsetId,rowId,colId].
     * @return the current result as String.
     */
    public static String getCurrentResultKey() {
        LOG.debug(">> getCurrentResultKey()");
        StringBuffer buf = new StringBuffer("${result[");
        String currentThread = ThreadIdentifier.getIdentifier();
        buf.append(SymbolTable.getValue(CURRENT_RESULTSET_KEY.replaceAll(
            "TID", currentThread))).
            append(",").
            append(SymbolTable.getValue(CURRENT_ROW_KEY.replaceAll(
            "TID", currentThread))).
            append(",").
            append(SymbolTable.getValue(CURRENT_COL_KEY.replaceAll(
            "TID", currentThread))).
            append("]}");
        return buf.toString();
    }

    /**
     * Removes the references to the user variables set in a test after
     * all tests are completed.
     */
    public static void removeUserVariables() {
        LOG.debug(">> removeUserVariables()");
        List userVariables = new ArrayList();
        for (Iterator it = symbols.keySet().iterator(); it.hasNext();) {
            Object key = it.next();
            if (!(key instanceof String)) {
                continue;
            }
            String varName = (String) key;
            // do not delete internal variables.
            if (varName.startsWith("${__") && varName.endsWith("__}")) {
                continue;
            }
            // do not delete ant global variables
            if (varName.startsWith("${ant.")) {
                continue;
            }
            userVariables.add(varName);
        }
        for (Iterator it = userVariables.iterator(); it.hasNext();) {
            symbols.remove((String) it.next());
        }
    }

    /**
     * Dumps the contents of the symbol table for inspection.
     */
    public static void dump() {
        LOG.debug(">> dump()");
        Iterator diter = getSymbols();
        LOG.debug("\nSymbol table dump");
        LOG.debug("-----------------");
        while (diter.hasNext()) {
            String key = (String) diter.next();
            Object value = symbols.get(key);
            String strValue = null;
            if (value != null) {
                strValue = value.toString();
            }
        }
        LOG.debug("-----------------");
        return;
    }
}
