/*
 * $Id: BatchDatabaseResult.java,v 1.6 2004/09/27 18:04:24 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/beans/BatchDatabaseResult.java,v $
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

import net.sourceforge.sqlunit.SymbolTable;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.sql.BatchUpdateException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * The BatchDatabaseResult represents the output from a batch update
 * operation. 
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.6 $
 */
public class BatchDatabaseResult {

    private static final Logger LOG = 
        Logger.getLogger(BatchDatabaseResult.class);

    private List updateCountList = null;
    private int expectedCount = -1;
    private int failedAtIndex = -1;
    private static BatchUpdateException exception = null;

    /**
     * Instantiates a BatchDatabaseResult object.
     */
    public BatchDatabaseResult() {
        LOG.debug(">> [BatchDatabaseResult]");
        updateCountList = new ArrayList();
    }

    /**
     * Returns the expectedCount attribute from the BatchDatabaseResult
     * object.
     * @return the expected number of results for this object.
     */
    public final int getExpectedCount() {
        LOG.debug(">> getExpectedCount");
        return expectedCount;
    }

    /**
     * Sets the expectedCount attribute from the BatchDatabaseResult object.
     * @param expectedCount the expected number of results for this object.
     */
    public final void setExpectedCount(final int expectedCount) {
        LOG.debug(">> setExpectedCount(" + expectedCount + ")");
        this.expectedCount = expectedCount;
    }

    /**
     * Returns the zero-based index into the BatchDatabaseResult object
     * where the fist failure is expected to occur.
     * @return the index into the object where the first failure is expected.
     */
    public final int getFailedAtIndex() {
        LOG.debug(">> getFailedAtIndex");
        return failedAtIndex;
    }

    /**
     * Sets the zero-based index into the BatchDatabaseResult object
     * where the fist failure is expected to occur.
     * @param failedAtIndex zero-based index where the failure is expected.
     */
    public final void setFailedAtIndex(final int failedAtIndex) {
        LOG.debug(">> setFailedAtIndex(" + failedAtIndex + ")");
        this.failedAtIndex = failedAtIndex;
    }

    /**
     * Returns the size of the underlying List of updateCounts.
     * @return the actual size of this BatchDatabaseResult object.
     */
    public final int getActualCount() {
        LOG.debug(">> getActualCount");
        return updateCountList.size();
    }

    /**
     * Returns the update count at the index into the BatchDatabaseResult 
     * object. The index is zero-based.
     * @param index the index into the BatchDatabaseResult object.
     * @return the update count at the index, or -1 if not found.
     */
    public final String getUpdateCountAt(final int index) {
        LOG.debug(">> getUpdateCountAt(" + index + ")");
        try {
            String count = (String) updateCountList.get(index);
            if (count == null) {
                return "NULL";
            } else {
                return count;
            }
        } catch (IndexOutOfBoundsException e) {
            return "NULL";
        }
    }

    /**
     * Allows setting of the update count from a specified integer value.
     * @param updateCount an integer value to set.
     */
    public final void addUpdateCount(final int updateCount) {
        LOG.debug(">> addUpdateCount(" + updateCount + ")");
        if (updateCount == Statement.SUCCESS_NO_INFO) {
            updateCountList.add("SUCCESS_NO_INFO");
        } else if (updateCount == Statement.EXECUTE_FAILED) {
            updateCountList.add("EXECUTE_FAILED");
        } else {
            updateCountList.add((new Integer(updateCount)).toString());
        }
    }

    /**
     * Allows setting of the update count from a specified numeric value
     * or one of the two acceptable symbolic values. It also does variable
     * substitution if the value supplied is a variable.
     * @param updateCountString the specified value of the update count.
     */
    public final void addUpdateCount(final String updateCountString) {
        LOG.debug(">> addUpdateCount(" + updateCountString + ")");
        if (updateCountString == null) {
            updateCountList.add(null);
        } else if (updateCountString.equalsIgnoreCase("SUCCESS_NO_INFO")) {
            updateCountList.add(new Integer(Statement.SUCCESS_NO_INFO));
        } else if (updateCountString.equalsIgnoreCase("EXECUTE_FAILED")) {
            updateCountList.add(new Integer(Statement.EXECUTE_FAILED));
        } else if (SymbolTable.isVariableName(updateCountString)) {
            String variableValue = SymbolTable.getValue(updateCountString);
            try {
                Integer variableValueAsInteger = new Integer(variableValue);
                updateCountList.add(variableValueAsInteger.toString());
            } catch (NumberFormatException e) { 
                updateCountList.add(null);
            }
        } else {
            try {
                updateCountList.add(updateCountString);
            } catch (NumberFormatException e) {
                updateCountList.add(null);
            }
        }
    }

    /**
     * Removes all the update counts from the underlying List object.
     */
    public final void resetUpdateCounts() {
        LOG.debug(">> resetUpdateCounts()");
        updateCountList.clear();
    }

    /**
     * Sets the batch exception into the result object.
     * @param exception the BatchUpdateException object to set.
     */
    public static final void setException(
            final BatchUpdateException exception) {
        LOG.debug(">> setException()");
        BatchDatabaseResult.exception = exception;
    }

    /**
     * Returns the BatchDatabaseResult object as a JDOM Element.
     * @return the BatchDatabaseResult as a JDOM Element.
     */
    public final Element toElement() {
        LOG.debug(">> toElement()");
        Element elBatchResult = new Element("batchresult");
        if (getExpectedCount() > -1) {
            elBatchResult.setAttribute("expected-count", 
                (new Integer(getExpectedCount())).toString());
        }
        if (getFailedAtIndex() > -1) {
            elBatchResult.setAttribute("failed-at", 
                (new Integer(getFailedAtIndex())).toString());
        }
        int numUpdateCounts = getActualCount();
        for (int i = 0; i < numUpdateCounts; i++) {
            String updateCount = (String) updateCountList.get(i);
            Element elUpdateCount = new Element("updatecount");
            elUpdateCount.setText(updateCount);
            elBatchResult.addContent(elUpdateCount);
        }
        return elBatchResult;
    }

    /**
     * Returns the String representation of the BatchDatabaseResult object
     * by converting it from its internal format to a JDOM Element.
     * @return a String representation of the BatchDatabaseResult object.
     */
    public final String toString() {
        LOG.debug(">> toString()");
        return XMLUtils.toXMLString(toElement());
    }
}
