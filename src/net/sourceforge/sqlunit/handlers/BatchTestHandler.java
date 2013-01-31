/*
 * $Id: BatchTestHandler.java,v 1.11 2005/06/10 05:52:42 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/BatchTestHandler.java,v $
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

import net.sourceforge.sqlunit.Assertions;
import net.sourceforge.sqlunit.HandlerFactory;
import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.SymbolTable;
import net.sourceforge.sqlunit.beans.BatchDatabaseResult;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.util.Iterator;
import java.util.List;

/**
 * The BatchTestHandler allows the grouping of a number of Stored Procedure
 * calls and/or SQL statements and a number of expected results from these.
 * The whole group is executed within a single transactional context, so a
 * failure in one of the SQL statements will roll back the entire test to
 * the point before it was called. Note that all the results need not be
 * specified. The handler will use the result's id attribute to match with
 * the corresponding sql or call element's id attribute to compare the
 * results. Pairs for which the result is not specified will be considered
 * to have passed.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.11 $
 * @sqlunit.parent name="sqlunit" ref="sqlunit"
 * @sqlunit.element name="batchtest" 
 *  description="The batchtest tag allows specifying a batch of SQL statements
 *  or stored procedure calls that must be executed within a batch. It allows
 *  the client to specify the expected number of results, and a point where 
 *  a failure may be expected. Note that only statements which update data
 *  in some way should be specified hre, since the JDBC specification does not
 *  allow the returning of resultsets from SQL statements run using the 
 *  batching feature"
 *  syntax="((skip)?, (classifiers)?, (batchcall | batchsql), batchresult)"
 * @sqlunit.attrib name="name"
 *  description="The name of the batch test. THis is used by SQLUnit to print
 *  progress messages to its log output"
 *  required="Yes"
 * @sqlunit.attrib name="assert"
 *  description="Specifies a single or comma-separated assertion that must
 *  hold true for the batch test."
 *  required="No, defaults to equal if not specified."
 * @sqlunit.attrib name="failure-message"
 *  description="If specified, SQLUnit will print the user-supplied failure
 *  message if the test failed"
 *  required="No, default is no failure message"
 * @sqlunit.attrib name="java-object-support" 
 *  description="If set to true, Java Object Support is enabled for the test"
 *  required="No, default is false"
 * @sqlunit.child name="skip"
 *  description="Indicates whether the func should be skipped or not."
 *  required="No"
 *  ref="skip"
 * @sqlunit.child name="classifiers"
 *  description="Allows user to provide classification criteria for the func
 *  which SQLUnit can use to decide whether it should run the func or not
 *  based on criteria provided to match the classifier."
 *  required="No"
 *  ref="classifiers"
 * @sqlunit.child name="batchcall"
 *  description="Specifies the set of batch calls to be made. Either 
 *  batchcall or batchsql must be specified"
 *  required="No, but see Description"
 *  ref="batchcall"
 * @sqlunit.child name="batchsql"
 *  description="Specifies the set of batch calls to be made. Either 
 *  batchcall or batchsql must be specified"
 *  required="No, but see Description"
 *  ref="batchsql"
 * @sqlunit.child name="batchresult"
 *  description="Specifies the expected return values from the call"
 *  required="Yes"
 *  ref="batchresult"
 * @sqlunit.example name="A typical batchtest specification"
 *  description="
 *  <batchtest name=\"Testing basic batchtest functionality\">{\n}
 *  {\t}<batchsql>{\n}
 *  {\t}{\t}<stmt>delete from customer where custId=1</stmt>{\n}
 *  {\t}{\t}<stmt>insert into customer values(1,'Some One','secret',{\n}
 *  {\t}{\t}{\t}'en_US',null,0,now())</stmt>{\n}
 *  {\t}{\t}<stmt>insert into customer values(1,'Someone Else','secret',{\n}
 *  {\t}{\t}{\t}'en_US',null,0,now())</stmt>{\n}
 *  {\t}{\t}<stmt>delete from customer where 1=1</stmt>{\n}
 *  {\t}</batchsql>{\n}
 *  {\t}<batchresult>{\n}
 *  {\t}{\t}<updatecount>1</updatecount>{\n}
 *  {\t}{\t}<updatecount>1</updatecount>{\n}
 *  {\t}{\t}<updatecount>1</updatecount>{\n}
 *  {\t}{\t}<updatecount>2</updatecount>{\n}
 *  {\t}</batchresult>{\n}
 *  </batchtest>
 *  "
 */
public class BatchTestHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(BatchTestHandler.class);

    /**
     * Collects the stored procedures and SQL statements specified and runs
     * them in a batch, matching with the specified results with the same
     * id attribute. If a failure occurs, the entire batch is rolled back.
     * This behavior of this handler is largely identical to the behavior
     * of the Test handler.
     * @param elBatchTest the JDOM Element representing the batchtest tag.
     * @return a Boolean.TRUE if the test succeeded else Boolean.FALSE.
     * @exception Exception if there was a problem running the process.
     */
    public final Object process(final Element elBatchTest) throws Exception {
        LOG.debug(">> process()");
        if (elBatchTest == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"batchtest"});
        }
        // pull out the attributes
        String hasJavaObjectSupport = XMLUtils.getAttributeValue(
            elBatchTest, "java-object-support");
        SymbolTable.setValue(SymbolTable.JAVA_OBJECT_SUPPORT,
            (hasJavaObjectSupport != null && hasJavaObjectSupport.equals("on"))
                ? "on" : "off");
        String failureMessage = XMLUtils.getAttributeValue(
            elBatchTest, "failure-message");
        SymbolTable.setObject(SymbolTable.FAILURE_MESSAGE_OBJ, 
            new SQLUnitException(failureMessage));
        String assertion = XMLUtils.getAttributeValue(elBatchTest, "assert");
        if (assertion == null || assertion.trim().length() == 0) {
            assertion = "equal";
        }
        // batchsql or batchcall
        Element elBatchCall = null;
        List swappableTags = 
            HandlerFactory.getSwappableTags(elBatchTest.getName());
        for (Iterator it = swappableTags.iterator(); it.hasNext();) {
            String swappableTag = (String) it.next();
            elBatchCall = elBatchTest.getChild(swappableTag);
            if (elBatchCall != null) { break; }
        }
        IHandler batchCallHandler = 
            HandlerFactory.getInstance(elBatchCall.getName());
        BatchDatabaseResult gotR = (BatchDatabaseResult)
            batchCallHandler.process(elBatchCall);
        // parse the batchresult
        Element elBatchResult = elBatchTest.getChild("batchresult");
        IHandler batchResultHandler = 
            HandlerFactory.getInstance(elBatchResult.getName());
        BatchDatabaseResult expR = (BatchDatabaseResult) 
            batchResultHandler.process(elBatchResult);
        SymbolTable.setValue(SymbolTable.JAVA_OBJECT_SUPPORT, null);
        // check if expected-count matches
        Assertions.assertIsTrue(failureMessage, expR, gotR, assertion);
        return Boolean.TRUE;
    }
}
