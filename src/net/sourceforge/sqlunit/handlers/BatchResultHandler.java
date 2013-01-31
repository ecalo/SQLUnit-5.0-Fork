/*
 * $Id: BatchResultHandler.java,v 1.6 2004/09/28 19:15:45 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/BatchResultHandler.java,v $
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

import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.beans.BatchDatabaseResult;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.util.List;

/**
 * The BatchResultHandler class processes the contents of a batchresult tag 
 * in the input XML file.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.6 $
 * @sqlunit.parent name="batchtest" ref="batchtest"
 * @sqlunit.element name="batchresult"
 *  description="The batchresult element specifies the expected result from
 *  running either the statements specified in a batchsql or batchcall tag"
 *  syntax="((updatecount)*)"
 * @sqlunit.attrib name="expected-count"
 *  description="Specifies the number of rows expected to be updated as a
 *  result of executing the batchsql or batchcall element. If the expected-count
 *  is specified, then the number of updatecount elements will not be counted
 *  for determining the test's success"
 *  required="No"
 * @sqlunit.attrib name="failed-at"
 *  description="Specifies which statement in the batch is expected to fail
 *  when running the test. The index is zero-based"
 *  required="No"
 * @sqlunit.child name="updatecount"
 *  description="Specifies in the body of the element the number of rows
 *  expected to be modified by the SQL at the corresponding position 
 *  (zero-based) in the batch"
 *  required="Yes, unless either or both expected-count and failed-at are
 *  specified"
 *  ref="none"
 * @sqlunit.example name="A typical batchresult element"
 *  description="
 *  <batchresult>{\n}
 *  {\t}<updatecount>1</updatecount>{\n}
 *  {\t}<updatecount>1</updatecount>{\n}
 *  </batchresult>{\n}
 *  "
 */
public class BatchResultHandler implements IHandler {

    private static final Logger LOG = 
        Logger.getLogger(BatchResultHandler.class.getName());

    /**
     * Runs the batch of SQL statements supplied as a batch and returns
     * a BatchDatabaseResult object.
     * @param elBatchResult the JDOM Element representing the batchsql tag.
     * @return a DatabaseResult object.
     * @exception Exception if there was a problem running the SQL.
     */
    public final Object process(final Element elBatchResult) throws Exception {
        LOG.debug(">> process()");
        if (elBatchResult == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"batchresult"});
        }
        BatchDatabaseResult result = new BatchDatabaseResult();
        String expectedCount = XMLUtils.getAttributeValue(
            elBatchResult, "expected-count");
        if (expectedCount != null) {
            try {
                int expectedCountAsInt = Integer.parseInt(expectedCount);
                result.setExpectedCount(expectedCountAsInt);
            } catch (NumberFormatException e) { 
                // :IGNORE: expected-count is not defined, ignore
            }
        }
        String failedAtIndex = XMLUtils.getAttributeValue(
            elBatchResult, "failed-at");
        if (failedAtIndex != null) {
            try {
                int failedAtIndexAsInt = Integer.parseInt(failedAtIndex);
                result.setFailedAtIndex(failedAtIndexAsInt);
            } catch (NumberFormatException e) {
                // :IGNORE: failed-at-index is not defined, ignore
            }
        }
        List elUpdateCountList = elBatchResult.getChildren("updatecount");
        int numUpdateCounts = elUpdateCountList.size();
        for (int i = 0; i < numUpdateCounts; i++) {
            Element elUpdateCount = (Element) elUpdateCountList.get(i);
            String updateCount = XMLUtils.getText(elUpdateCount);
            result.addUpdateCount(updateCount);
        }
        return result;
    }
}
