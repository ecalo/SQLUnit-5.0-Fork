/*
 * $Id: ResultHandler.java,v 1.7 2005/05/11 01:25:47 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/ResultHandler.java,v $
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
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.beans.DatabaseResult;
import net.sourceforge.sqlunit.beans.ExceptionBean;
import net.sourceforge.sqlunit.beans.OutParam;
import net.sourceforge.sqlunit.beans.ResultSetBean;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.util.List;

/**
 * The ResultHandler class parses the XML representing the result object.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.7 $
 * @sqlunit.parent name="test" ref="test"
 * @sqlunit.element name="result"
 *  description="The result tag allows the test author to specify an expected
 *  result from a SQL call or stored procedure. SQLUnit executes the SQL
 *  or stored procedure and converts the results into an internal Result 
 *  object. It will also convert the data supplied in the result tag to
 *  an internal Result object, and then compares the two for equality.
 *  Values specified in the col nested element of a result element can 
 *  contain variable names instead of literal values. The variable names will
 *  be updated with the values from the internal symbol table. Result
 *  tags can also contain an exception element if the call is expected to
 *  throw an exception for this particular test. Note that all id attributes
 *  of the result and its nested elements start at 1, so as to be consistent
 *  with how JDBC resultsets are set up, and minimize confusion between the
 *  two representations."
 *  syntax="((((outparam)*, (updatecount)?, (resultset)*)+ | 
 *  ((outparam)*, exception))?)"
 * @sqlunit.attrib name="id"
 *  description="The id of the result"
 *  required="No"
 * @sqlunit.attrib name="echo"
 *  description="If set to true, the result will be echoed to the output.
 *  The test will be short-circuited and SQLUnit will not attempt to do
 *  any matching. This is useful for debugging. Valid values are true and
 *  false."
 *  required="No, defaults to false"
 * @sqlunit.child name="outparam"
 *  description="Zero or more outparam elements may be specified, each 
 *  representing an output parameter from the stored procedure. An outparam
 *  element can contain a resultset element in case of Oracle CURSOR types,
 *  or text information in case of non-CURSOR types."
 *  required="No"
 *  ref="none"
 * @sqlunit.child name="updatecount"
 *  description="Specifies the updatecount that is returned from the SQL
 *  or stored procedure call."
 *  required="No."
 *  ref="none"
 * @sqlunit.child name="resultset"
 *  description="The resultset tag specifies a single resultset returned
 *  from a database call (SQL or stored procedure). A result element can
 *  contain one or more resultsets, which in turn will contain one or more
 *  rows, which would contain one or more columns. If there are no rows,
 *  a resultset can also be empty."
 *  required="No."
 *  ref="resultset"
 * @sqlunit.child name="exception"
 *  description="The exception tag is used to test for expected failures
 *  of a stored procedure in response to specific inputs."
 *  required="No. Exception tags can coexist with outparam tags."
 *  ref="exception"
 * @sqlunit.example name="A multi-resultset result tag"
 *  description="
 *  <result>{\n}
 *  {\t}<resultset id=\"1\">{\n}
 *  {\t}{\t}<row id=\"1\">{\n}
 *  {\t}{\t}{\t}<col id=\"1\" name=\"c1\" type=\"INTEGER\">1</col>{\n}
 *  {\t}{\t}{\t}<col id=\"2\" name=\"c2\" type=\"VARCHAR\">Blah</col>{\n}
 *  {\t}{\t}{\t}<col id=\"3\" name=\"c3\" type=\"INTEGER\">3</col>{\n}
 *  {\t}{\t}</row>{\n}
 *  {\t}</resultset>{\n}
 *  {\t}<resultset id=\"2\" />{\n}
 *  {\t}<resultset id=\"3\" />{\n}
 *  {\t}<resultset id=\"4\">{\n}
 *  {\t}<row id=\"1\">{\n}
 *  {\t}{\t}{\t}<col id=\"1\" name=\"ac1\" type=\"INTEGER\">1</col>{\n}
 *  {\t}{\t}{\t}<col id=\"2\" name=\"ac2\" type=\"INTEGER\">1</col>{\n}
 *  {\t}{\t}{\t}<col id=\"3\" name=\"ac3\" type=\"VARCHAR\">Unused Blahs</col>{\n}
 *  {\t}{\t}{\t}<col id=\"4\" name=\"ac4\" type=\"INTEGER\">1</col>{\n}
 *  {\t}{\t}{\t}<col id=\"5\" name=\"ac5\" type=\"VARCHAR\">Unused</col>{\n}
 *  {\t}</row>{\n}
 *  {\t}<row id=\"2\">{\n}
 *  {\t}{\t}{\t}<col id=\"1\" name=\"ac1\" type=\"INTEGER\">1</col>{\n}
 *  {\t}{\t}{\t}<col id=\"2\" name=\"ac2\" type=\"INTEGER\">1000</col>{\n}
 *  {\t}{\t}{\t}<col id=\"3\" name=\"ac3\" type=\"VARCHAR\">Deprecated Blahs</col>{\n}
 *  {\t}{\t}{\t}<col id=\"4\" name=\"ac4\" type=\"INTEGER\">1</col>{\n}
 *  {\t}{\t}{\t}<col id=\"5\" name=\"ac5\" type=\"VARCHAR\">Deprecated</col>{\n}
 *  {\t}</row>{\n}
 *  {\t}</resultset>{\n}
 *  </result>
 *  "
 * @sqlunit.example name="A result containing an exception"
 *  description="
 *  <result>{\n}
 *  {\t}<exception>{\n}
 *  {\t}{\t}<code>1234</code>{\n}
 *  {\t}{\t}<message>Test Exception</message>{\n}
 *  {\t}</exception>{\n}
 *  </result>
 *  "
 */
public class ResultHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(ResultHandler.class);

    /**
     * Processes a JDOM Element representing a result of a query and returns
     * a DatabaseResult object.
     * @param elResult the JDOM Element to use.
     * @return a populated DatabaseResult object.
     * @exception Exception if there was a problem creating the DatabaseResult.
     */
    public final Object process(final Element elResult) throws Exception {
        LOG.debug(">> process(elResult)");
        if (elResult == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL, 
                new String[] {"result"});
        }
        String id = XMLUtils.getAttributeValue(elResult, "id");
        boolean echo = (("true").equals(XMLUtils.getAttributeValue(
            elResult, "echo")));
        DatabaseResult dbResult = new DatabaseResult();
        // if echo=true, short circuit building of expected result set
        // and return the partially built result
        if (echo) {
            dbResult.setEchoOnly(true);
            return dbResult;
        }
        List children = elResult.getChildren();
        // check for exception
        Element elException = elResult.getChild("exception");
        if (elException != null) {
            IHandler exceptionHandler = 
                HandlerFactory.getInstance(elException.getName());
            ExceptionBean exceptionBean = 
                (ExceptionBean) exceptionHandler.process(elException);
            dbResult.resetAsException(exceptionBean.getErrorCode(),
                exceptionBean.getErrorMessage());
        }
        // check for updatecount
        Element elUpdateCount = elResult.getChild("updatecount");
        if (elUpdateCount != null) {
            try {
                dbResult.setUpdateCount(Integer.parseInt(
                    XMLUtils.getText(elUpdateCount)));
            } catch (NumberFormatException e) {
                // :IGNORE: updatecount is not set, ignore
            }
        }
        // check for outparam(s)
        List elOutParams = elResult.getChildren("outparam");
        int numOutParams = elOutParams.size();
        OutParam[] outparams = new OutParam[numOutParams];
        for (int i = 0; i < numOutParams; i++) {
            Element elOutParam = (Element) elOutParams.get(i);
            IHandler outParamHandler = 
                HandlerFactory.getInstance(elOutParam.getName());
            outparams[i] = (OutParam) outParamHandler.process(elOutParam);
        }
        dbResult.setOutParams(outparams);
        // check for resultset(s)
        List elResultSets = elResult.getChildren("resultset");
        int numResultSets = elResultSets.size();
        ResultSetBean[] rsbs = new ResultSetBean[numResultSets];
        for (int i = 0; i < numResultSets; i++) {
            Element elResultSet = (Element) elResultSets.get(i);
            IHandler resultSetHandler = 
                HandlerFactory.getInstance(elResultSet.getName());
            rsbs[i] = (ResultSetBean) resultSetHandler.process(elResultSet);
        }
        dbResult.setResultSets(rsbs);
        return dbResult;
    }
}
