/*
 * $Id: ResultSetBean.java,v 1.9 2005/05/03 17:22:38 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/beans/ResultSetBean.java,v $
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

import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IType;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.SymbolTable;
import net.sourceforge.sqlunit.TypeFactory;
import net.sourceforge.sqlunit.types.BinaryType;
import net.sourceforge.sqlunit.types.TextType;
import net.sourceforge.sqlunit.utils.TypeUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Models a ResultSet element, wrapping multiple Row objects.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.9 $
 */
public class ResultSetBean {
    
    private static final Logger LOG = Logger.getLogger(ResultSetBean.class);

    private String id;
    private boolean partial = false;         // the default if not specified
    private int rowCount = -1;               // the default if not specified
    private String[] sortBy = null;          // the default if not specified
    private Row[] rows = new Row[0];
    private boolean isRowCountOverriden = false;

    /**
     * Default constructor.
     */
    public ResultSetBean() {
        // default constructor
    }

    /**
     * Constructs and populates a ResultSetBean object from a ResultSet
     * object.
     * @param rs the ResultSet object to build from.
     * @param id the id for the resultset, defaults to 1 if not set.
     * @exception SQLUnitException if there was a problem building the bean.
     */
    public ResultSetBean(final ResultSet rs, final int id) 
            throws SQLUnitException {
        this();
        setId((new Integer(id)).toString());
        SymbolTable.setCurrentResultSet((new Integer(id)).toString());
        try {
            List rowList = new ArrayList();
            ResultSetMetaData rsmd = rs.getMetaData();
            int numCols = rsmd.getColumnCount();
            int rowId = 1;
            while (rs.next()) {
                SymbolTable.setCurrentRow((new Integer(rowId)).toString());
                Col[] cols = new Col[numCols];
                for (int colId = 1; colId <= numCols; colId++) {
                    int colType = rsmd.getColumnType(colId);
                    SymbolTable.setCurrentCol((new Integer(colId)).toString());
                    IType colTypeObj = TypeFactory.getInstance(colType);
                    // some type checks here
                    Object colValue;
                    if (colTypeObj instanceof BinaryType) {
                        colValue = rs.getBinaryStream(colId);
                    } else if (colTypeObj instanceof TextType) {
                        colValue = rs.getAsciiStream(colId);
                    } else {
                        colValue = rs.getObject(colId);
                    }
                    cols[colId - 1] = new Col();
                    cols[colId - 1].setId((new Integer(colId)).toString());
                    cols[colId - 1].setName(rsmd.getColumnName(colId));
                    cols[colId - 1].setType(TypeUtils.getXmlTypeFromSqlType(
                        colType));
                    cols[colId - 1].setValue(colTypeObj.toString(colValue));
                }
                rowList.add(cols);
                rowId++;
            }
            int numRows = rowList.size();
            Row[] theRows = new Row[numRows];
            for (int i = 0; i < numRows; i++) {
                theRows[i] = new Row();
                theRows[i].setId((new Integer(i + 1)).toString());
                theRows[i].setCols((Col[]) rowList.get(i));
            }
            setRows(theRows);
            rs.close();
        } catch (SQLException e) {
            throw new SQLUnitException(IErrorCodes.GENERIC_ERROR,
                new String[] {"SQL", e.getClass().getName(), 
                e.getMessage()}, e);
        }
    }

    /**
     * Returns the Id for this row.
     * @return the id for this row.
     */
    public final String getId() {
        return id;
    }

    /**
     * Set the id for this row.
     * @param id the id for this row.
     */
    public final void setId(final String id) {
        this.id = id;
    }
    
    /**
     * Return true if the resultset specifies rows partially, else false.
     * @return true if rows are partially specified. Default is false.
     */
    public final boolean isPartial() {
        return partial;
    }
    
    /**
     * Sets the partial row specification attribute for the resultset.
     * @param partial true or false.
     */
    public final void setPartial(String partial) {
        this.partial = (partial.equals("true"));
    }

    /**
     * Returns the rowCount override (if specified) for the resultset. If
     * the rowcount has been set from the rowcount attribute (overrides
     * the actual value of the rowcount), then it returns that, else it
     * returns the number of Row objects.
     * @return the rowcount override (if specified) for the resultset.
     */
    public final int getRowCount() {
        if (rowCount != -1) { // overriden
            return rowCount;
        } else {
            return getRows().length;
        }
    }

    /**
     * Sets the rowCount override for the resultset.
     * @param rowCount the rowcount override to set.
     */
    public final void setRowCount(final int rowCount) {
        this.rowCount = rowCount;
        this.isRowCountOverriden = true;
    }

    /**
     * Returns true if rowcount has been overriden by the caller.
     * @return true if rowcount has been overriden by caller.
     */
    public final boolean isRowCountOverriden() {
        return this.isRowCountOverriden;
    }

    /**
     * Returns the rows in the resultset.
     * @return an array of Row objects in the ResultSet object.
     */
    public final Row[] getRows() {
        return rows;
    }

    /**
     * Sets the rows in the resultset.
     * @param rows an array of Row objects to set.
     */
    public final void setRows(final Row[] rows) {
        this.rows = rows;
    }

    /**
     * Returns the array of column ids on which the rows of this ResultSetBean
     * was sorted. This value is set as a side-effect of the sortBy() call.
     * @return an array of column ids.
     */
    public final String[] getSortBy() {
        return sortBy;
    }

    /** 
     * Sets the colIds from the colIdList.
     * @param colIdList a comma-separated list of col ids to sort by.
     */
    public final void setSortBy(final String colIdList) {
        this.sortBy = colIdList.split("\\s*,\\s*");
    }

    /**
     * Allows sorting of rows in a resultset by the columns specified in
     * the sortColIds array.
     */
    public final void sort() {
        // no sorting needed for this condition
        if (rows == null || rows.length <= 1) {
            return; 
        }
        // no sort columns specified, repopulate sortColIds with all col ids
        boolean isNaturallySorted = false;
        if (this.sortBy == null || this.sortBy.length == 0) {
            Col[] colsInRow = rows[0].getCols();
            this.sortBy = new String[colsInRow.length];
            for (int i = 0; i < colsInRow.length; i++) {
                this.sortBy[i] = colsInRow[i].getId();
            }
            isNaturallySorted = true;
        }
        // set up the rows for sorting
        for (int i = 0; i < rows.length; i++) {
            rows[i].setSortCols(this.sortBy);
        }
        // sort it
        Object[] rowObjs = (Object[]) rows;
        Arrays.sort(rowObjs);
        rows = (Row[]) rowObjs;
        // if naturally sorted, reset the sortBy field
        if (isNaturallySorted) { this.sortBy = null; }
    }

    /**
     * Returns a JDOM Element representing this ResultSet object.
     * @return a JDOM Element representing this ResultSet object.
     */
    public final Element toElement() {
        Element elResultSet = new Element("resultset");
        elResultSet.setAttribute("id", getId());
        if (isRowCountOverriden()) {
            elResultSet.setAttribute("rowcount", 
                (new Integer(getRowCount())).toString());
        }
        Row[] theRows = getRows();
        for (int i = 0; i < theRows.length; i++) {
            Element elRow = theRows[i].toElement();
            elResultSet.addContent(elRow);
        }
        return elResultSet;
    }

    /**
     * Useful method for debugging. Returns the String representation
     * of this ResultSetBean.
     * @return the String representation of this result set bean.
     */
    public final String toString() {
        StringBuffer buffer = new StringBuffer();
        Row[] theRows = getRows();
        for (int i = 0; i < theRows.length; i++) {
            buffer.append(theRows[i].toString()).append("\n");
        }
        return buffer.toString();
    }
}
