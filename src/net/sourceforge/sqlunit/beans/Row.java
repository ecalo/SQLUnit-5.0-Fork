/*
 * $Id: Row.java,v 1.5 2005/05/03 17:22:38 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/beans/Row.java,v $
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

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * Models a row element, (wrapping multiple Col objects) in a database result.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.5 $
 */
public class Row implements Comparable {
    
    private static final Logger LOG = Logger.getLogger(Row.class);

    private String id;
    private boolean partial = false;
    private Col[] cols;
    private Map colMap;
    private String[] sortColIds;

    /**
     * Returns the Id for this row.
     * @return the id for this row.
     */
    public final String getId() {
        return id;
    }

    /**
     * Set the id for this row.
     * @param rowId the id for this row.
     */
    public final void setId(final String rowId) {
        this.id = rowId;
    }

    /**
     * Returns true if the row is partially specified.
     * @return true if the row is partially specified.
     */
    public final boolean isPartial() {
        return partial;
    }

    /**
     * Sets whether the row is partially specified.
     * @param partial "true" or "false", default is "false".
     */
    public final void setPartial(String partial) {
        this.partial = (partial.equals("true"));
    }

    /**
     * Return an array of Col objects for this row.
     * @return an array of Col objects for this row.
     */
    public final Col[] getCols() {
        return cols;
    }

    /**
     * Set the Col object array for this row. Also sets an internal Map
     * of Col objects.
     * @param columns an array of Col objects.
     */
    public final void setCols(final Col[] columns) {
        this.cols = columns;
        this.colMap = new HashMap();
        for (int i = 0; i < cols.length; i++) {
            this.colMap.put(cols[i].getId(), cols[i]);
        }
    }

    /**
     * Convenience method to return a Col object by its id.
     * @param colId the id of the Col object.
     * @return a Col object for that id.
     */
    public final Col getColById(final String colId) {
        return (Col) colMap.get(colId);
    }

    /**
     * Sets a string array of ids to sort this row by.
     * @param colIdsToSort a String array of column ids.
     */
    public final void setSortCols(final String[] colIdsToSort) {
        this.sortColIds = colIdsToSort;
    }

    /**
     * Returns a negative, zero or positive value depending on whether
     * the contents of this column is less than, equal to or greater 
     * than the contents of the passed in Col object.
     * @param obj an instance of a Col object.
     * @return a negative, zero or positive number.
     */
    public final int compareTo(final Object obj) {
        if (!(obj instanceof Row)) { return 0; }
        Row that = (Row) obj;
        // the sort columns are set up from the resultset level
        // so we can use either one, we are going to use this object.
        int compareResult = 0; // they are equal, for now
        String[] sortCols = sortColIds;
        for (int i = 0; i < sortCols.length; i++) {
            // check if prefixed by a negative sign
            LOG.debug("cols[" + i + "] = " + sortCols[i]);
            boolean sortAscending = (!(sortCols[i].startsWith("-")));
            Col thisCol = this.getColById((sortAscending
                ? sortCols[i] : sortCols[i].substring(1)));
            Col thatCol = that.getColById((sortAscending
                ? sortCols[i] : sortCols[i].substring(1)));
            if (sortAscending) {
                compareResult = thisCol.compareTo(thatCol);
            } else {
                compareResult = thatCol.compareTo(thisCol);
            }
            if (compareResult == 0) { 
                continue;  // look at the next column to decide
            } else { 
                break;     // we know which is larger, quit compare
            }
        }
        return compareResult;
    }

    /**
     * Returns a JDOM element that represents this Col object.
     * @return a JDOM element representing this object.
     */
    public final Element toElement() {
        Element elRow = new Element("row");
        elRow.setAttribute("id", getId());
        Col[] theCols = getCols();
        for (int i = 0; i < theCols.length; i++) {
            Element elCol = theCols[i].toElement();
            elRow.addContent(elCol);
        }
        return elRow;
    }

    /**
     * Returns the string representation of the Row bean.
     * @return the String representation of this bean.
     */
    public final String toString() {
        StringBuffer buffer = new StringBuffer();
        Col[] theCols = getCols();
        for (int i = 0; i < theCols.length; i++) {
            if (i > 0) { buffer.append(","); }
            buffer.append(theCols[i].getValue());
        }
        return buffer.toString();
    }
}
