/*
 * $Id: DatabaseResult.java,v 1.8 2005/05/16 17:22:12 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/beans/DatabaseResult.java,v $
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

import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;

import org.jdom.Element;

/**
 * The DatabaseResult models a Database Result.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.8 $
 */
public class DatabaseResult {

    private static final Logger LOG = Logger.getLogger(DatabaseResult.class);

    private String errorCode;
    private String errorMessage;
    private OutParam[] outParams;
    private ResultSetBean[] resultSetBeans;
    private ExceptionBean ex;
    private int updateCount;
    private boolean echoOnly;

    /**
     * Default constructor. Sets up all underlying map and scalar objects.
     */
    public DatabaseResult() {
        LOG.debug("[DatabaseResult]");
        // initialize outparam part
        outParams = new OutParam[0];
        // initialize resultset part
        resultSetBeans = new ResultSetBean[0];
        // initialize exception part
        ex = null;
        // initialize updatecount part
        updateCount = -1;
        // set echoOnly to false
        echoOnly = false;
    }

    /**
     * Returns the number of result sets in the DatabaseResult.
     * @return the number of set elements in the result element.
     */
    public final int getNumResults() {
        return resultSetBeans.length;
    }

    /**
     * Returns the number of rows in resultset identified by resultset index.
     * @param resultSetIndex the index of the resultset in the DatabaseResult.
     * The resultset index is 1-based.
     * @return the number of rows in the resultset.
     */
    public final int getNumRows(final int resultSetIndex) {
        LOG.debug(">> getNumRows(" + resultSetIndex + ")");
        // if the resultSetIndex is out of bounds, return -1
        if (resultSetIndex > getNumResults()) {
            return -1;
        }
        ResultSetBean rsb = resultSetBeans[resultSetIndex - 1];
        Row[] rows = rsb.getRows();
        return rows.length;
    }

    /**
     * Returns the number of columns in resultset identified by resultset index.
     * @param resultSetIndex the index of the resultset in the DatabaseResult.
     * @return the number of columns in the row.
     */
    public final int getNumCols(final int resultSetIndex) {
        LOG.debug(">> getNumCols(" + resultSetIndex + ")");
        if (resultSetIndex > getNumResults()) {
            return -1;
        }
        ResultSetBean rsb = resultSetBeans[resultSetIndex - 1];
        Row[] rows = rsb.getRows();
        if (rows == null || rows.length == 0) {
            return -1;
        }
        Col[] cols = rows[0].getCols();
        return cols.length;
    }

    /**
     * Returns an array of OutParam objects for this DatabaseResult object.
     * @return an array of OutParam objects. May be null.
     */
    public final OutParam[] getOutParams() {
        return outParams;
    }

    /**
     * Sets the outparams into the DatabaseResult object.
     * @param outParams an array of OutParam objects.
     */
    public final void setOutParams(final OutParam[] outParams) {
        this.outParams = outParams;
    }

    /**
     * Returns an array of ResultSetBean objects for this DatabaseResult object.
     * @return an array of ResultSetBean objects. May be null.
     */
    public final ResultSetBean[] getResultSets() {
        return resultSetBeans;
    }

    /**
     * Sets the resultsets into the DatabaseResult object.
     * @param rsbs an array of ResultSetBean objects.
     */
    public final void setResultSets(final ResultSetBean[] rsbs) {
        this.resultSetBeans = rsbs;
    }

    /**
     * Returns true if the DatabaseResult represents an Exception.
     * @return true if this DatabaseResult is an exception, else false.
     */
    public final boolean isException() {
        LOG.debug(">> isException()");
        return (ex != null);
    }

    /**
     * Returns the exception from the DatabaseResult object.
     * @return the ExceptionBean object.
     */
    public final ExceptionBean getException() {
        return ex;
    }

    /**
     * Creates the exception version of a DatabaseResult (this).
     * @param errCode the code to populate.
     * @param errMessage the message to populate.
     */
    public final void resetAsException(final String errCode, 
            final String errMessage) {
        LOG.debug(">> resetAsException(" + errCode + "," + errMessage + ")");
        ex = new ExceptionBean();
        ex.setErrorCode(errCode);
        ex.setErrorMessage(XMLUtils.stripCRLF(errMessage));
    }

    /**
     * Returns the update count associated with this DatabaseResult. If
     * the update count is not applicable for this DatabaseResult, then 
     * it will return -1. If applicable, this represents the number of
     * rows updated by the SQL query or Stored Procedure that generated
     * this DatabaseResult.
     * @return the update count for the DatabaseResult.
     */
    public final int getUpdateCount() {
        LOG.debug(">> getUpdateCount()");
        return updateCount;
    }

    /**
     * Sets the update count for the DatabaseResult.
     * @param updateCount the updateCount to update.
     */
    public final void setUpdateCount(final int updateCount) {
        LOG.debug(">> setUpdateCount(" + updateCount + ")");
        this.updateCount = updateCount;
    }

    /**
     * Returns the echoOnly property of the DatabaseResult object.
     * @return true or false.
     */
    public final boolean getEchoOnly() {
        return echoOnly;
    }

    /**
     * Sets the echoOnly property of the DatabaseResult object. If this
     * property is set to true in the expected result, SQLUnit will not
     * attempt to match with the generated result. Instead it will print
     * the XML String representation of the generated result to the output.
     * @param echoOnly the echoOnly property. Valid values are true and false.
     */
    public final void setEchoOnly(boolean echoOnly) {
        this.echoOnly = echoOnly;
    }

    /**
     * Returns the DatabaseResult object as a JDOM Element.
     * @return the DatabaseResult object as a JDOM Element.
     */
    public final Element toElement() {
        LOG.debug(">> toElement()");
        Element elDatabaseResult = new Element("result");
        if (isException()) {
            // (outparam)*, (exception)
            OutParam[] outparams = getOutParams();
            for (int i = 0; i < outparams.length; i++) {
                Element elOutParam = outparams[i].toElement();
                elDatabaseResult.addContent(elOutParam);
            }
            ExceptionBean thisEx = getException();
            elDatabaseResult.addContent(thisEx.toElement());
        } else {
            // (outparam)*, (updatecount)?, (resultset)*
            OutParam[] outparams = getOutParams();
            for (int i = 0; i < outparams.length; i++) {
                Element elOutParam = outparams[i].toElement();
                elDatabaseResult.addContent(elOutParam);
            }
            int thisUpdateCount = getUpdateCount();
            if (thisUpdateCount > -1) {
                Element elUpdateCount = new Element("updatecount");
                elUpdateCount.setText(
                    (new Integer(thisUpdateCount)).toString());
                elDatabaseResult.addContent(elUpdateCount);
            }
            ResultSetBean[] resultsetbeans = getResultSets();
            for (int i = 0; i < resultsetbeans.length; i++) {
                Element elResultSet = resultsetbeans[i].toElement();
                elDatabaseResult.addContent(elResultSet);
            }
        }
        return elDatabaseResult;
    }

    /**
     * Returns a String representation of the DatabaseResult object.
     * @return a String representation of the DatabaseResult object.
     */
    public final String toString() {
        LOG.debug(">> toString()");
        return XMLUtils.toXMLString(toElement());
    }
}
