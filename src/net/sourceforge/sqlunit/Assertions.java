/*
 * $Id: Assertions.java,v 1.21 2006/06/10 20:29:14 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/Assertions.java,v $
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import net.sourceforge.sqlunit.beans.BatchDatabaseResult;
import net.sourceforge.sqlunit.beans.Col;
import net.sourceforge.sqlunit.beans.DatabaseResult;
import net.sourceforge.sqlunit.beans.OutParam;
import net.sourceforge.sqlunit.beans.ResultSetBean;
import net.sourceforge.sqlunit.beans.Row;

import org.apache.log4j.Logger;

/**
 * Provides methods to assert various conditions for doing SQLUnit tests.
 * Inspired by the JUnit Assert class.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.21 $
 */
public final class Assertions {

    private static final Logger LOG = Logger.getLogger(Assertions.class);

    // DatabaseResult assert methods
    private static final String[][] DATABASE_RESULT_ASSERT_MAPPING = {
        {"none", "assertNone"},
        {"equal", "assertEqual"},
        {"not-equal", "assertNotEqual"},
        {"exception-matches", "assertExceptionMatches"},
        {"not-exception", "assertNotException"},
        {"number-outparams-equal", "assertNumberOfOutParamsEqual"},
        {"outparams-equal", "assertOutParamsEqual"},
        {"updatecounts-equal", "assertUpdateCountsEqual"},
        {"number-resultsets-equal", "assertNumberOfResultSetsEqual"},
        {"resultsets-equal", "assertResultSetsEqual"},
        {"resultset-types-equal", "assertResultSetTypesEqual"},
        {"resultset-values-equal", "assertResultSetValuesEqual"},
        {"fail-with-failure", "assertFailWithFailure"}
    };
    // BatchDatabaseResult assert methods
    private static final String[][] BATCH_DATABASE_RESULT_ASSERT_MAPPING = {
        {"none", "assertNone"},
        {"equal", "assertEqual"},
        {"not-equal", "assertNotEqual"},
        {"failed-at-equal", "assertFailedAtEqual"},
        {"expected-count-equal", "assertExpectedCountEqual"},
        {"update-count-equal", "assertUpdateCountEqual"},
        {"fail-with-failure", "assertFailWithFailure"}
    };
    // parameters for the DatabaseResult methods
    private static Class[] databaseResultParamClasses = {
        String.class, DatabaseResult.class, DatabaseResult.class, List.class
    };
    // parameters for the BatchDatabaseResult methods.
    private static Class[] batchDatabaseResultParamClasses = {
        String.class, BatchDatabaseResult.class, BatchDatabaseResult.class
    };
    // column equality enums
    private static final int COL_TYPES_EQUAL = 1;
    private static final int COL_VALUES_EQUAL = 2;
    private static final int COL_TYPE_VALUE_EQUAL = 3;

    private static Map databaseResultAssertMap = null;
    private static Map batchDatabaseResultAssertMap = null;

    /**
     * Private Constructor. Assertions cannot be instantiated.
     */
    private Assertions() { 
        // cannot be instantiated
    }

    // -------- these are the methods that get called --------

    /**
     * Invokes a named assertion (or a comma-separated list of assertions)
     * and applies it to the supplied DatabaseResult objects.
     * @param failureMessage a user-supplied failure message.
     * @param expR the specified DatabaseResult object.
     * @param gotR the actual DatabaseResult object.
     * @param matchPatterns a List of MatchPattern objects, may be null.
     * @param condition a single assertion or a comma-separated list of
     * assertions.
     * @exception SQLUnitException if the assertion failed.
     */
    public static void assertIsTrue(final String failureMessage, 
            final DatabaseResult expR, final DatabaseResult gotR, 
            final List matchPatterns, final String condition) 
            throws SQLUnitException {
        if (databaseResultAssertMap == null) {
            databaseResultAssertMap = buildMap(DATABASE_RESULT_ASSERT_MAPPING);
        }
        Assertions.invokeAssertion(condition, databaseResultParamClasses,
            databaseResultAssertMap, new Object[] {
            (failureMessage == null ? "" : failureMessage), 
            expR, gotR, matchPatterns});
    }

    /**
     * Invokes a named assertion (or a comma-separated list of assertions)
     * and applies it to the supplied BatchDatabaseResult objects.
     * @param failureMessage a user-supplied failure message.
     * @param expR the specified BatchDatabaseResult object.
     * @param gotR the actual BatchDatabaseResult object.
     * @param condition a single assertion or a comma-separated list of
     * assertions.
     * @exception SQLUnitException if the assertion failed.
     */
    public static void assertIsTrue(final String failureMessage, 
            final BatchDatabaseResult expR, final BatchDatabaseResult gotR, 
            final String condition) throws SQLUnitException {
        if (batchDatabaseResultAssertMap == null) {
            batchDatabaseResultAssertMap = 
                buildMap(BATCH_DATABASE_RESULT_ASSERT_MAPPING);
        }
        Assertions.invokeAssertion(condition, batchDatabaseResultParamClasses,
            batchDatabaseResultAssertMap, new Object[] {
            (failureMessage == null ? "" : failureMessage), expR, gotR});
    }

    // ------- DatabaseResult asserts -------

    /**
     * Asserts nothing. This is used to assert that there are no
     * equality or non-equality relationships between the generated
     * and the expected result.
     * @param failureMessage the user-supplied failure message.
     * @param expR the specified DatabaseResult object.
     * @param gotR the actual DatabaseResult object.
     * @param matchPatterns a List of MatchPattern objects, may be null.
     * @exception SQLUnitException if the assertion failed.
     * @sqlunit.assert name="none"
     *  description="Asserts nothing about the generated and the actual
     *  results. This is typically used to disable assertions for the 
     *  particular test."
     *  usage="diff,test"
     */
    private static void assertNone(final String failureMessage,
            final DatabaseResult expR, DatabaseResult gotR,
            final List matchPatterns) throws SQLUnitException {
        return;
    }

    /**
     * Asserts that two DatabaseResult objects are equal, or if a List of
     * MatchPattern objects are supplied, then they match.
     * @param failureMessage the user-supplied failure message.
     * @param expR the specified DatabaseResult object.
     * @param gotR the actual DatabaseResult object.
     * @param matchPatterns a List of MatchPattern objects, may be null.
     * @exception SQLUnitExeeption if the assertion failed.
     * @sqlunit.assert name="equal"
     *  description="Asserts that the two results are equal. This is actually 
     *  a macro assertion, which resolves to a number of subassertions, which 
     *  are asserted serially. This is the default assert if not specified 
     *  in a diff or test tag."
     *  usage="diff,test"
     */
    private static void assertEqual(final String failureMessage, 
            final DatabaseResult expR, final DatabaseResult gotR, 
            final List matchPatterns) throws SQLUnitException {
        String conditions = "exception-matches,"
            + "number-outparams-equal,"
            + "outparams-equal,"
            + "updatecounts-equal,"
            + "number-resultsets-equal,"
            + "resultsets-equal";
        assertIsTrue(failureMessage, expR, gotR, matchPatterns, conditions);
    }

    /**
     * Asserts that teh two DatabaseResult objects are not equal.
     * @param failureMessage the user-supplied failure message.
     * @param expR the specified DatabaseResult object.
     * @param gotR the actual DatabaseResult object.
     * @param matchPatterns a List of MatchPattern objects, may be null.
     * @exception SQLUnitExeeption if the assertion failed.
     * @sqlunit.assert name="not-equal"
     *  description="Asserts that the two results are not equal. This is an 
     *  inversion of the equals assertion."
     *  usage="diff,test"
     */
    private static void assertNotEqual(final String failureMessage,
            final DatabaseResult expR, final DatabaseResult gotR, 
            final List matchPatterns) throws SQLUnitException {
        try {
            assertEqual(failureMessage, expR, gotR, matchPatterns);
        } catch (SQLUnitException e) { return; }
        throw new SQLUnitException(IErrorCodes.ASSERT_FAILED,
            new String[] {"not-equal", "", expR.toString(), expR.toString(),
            failureMessage});
    }

    /**
     * Asserts that both or neither DatabaseResult objects resulted in an
     * exception. If both resulted in an exception, then the exception
     * code and message are matched based on whether they are supplied
     * in the expected result or not.
     * @param failureMessage the user-supplied failure message.
     * @param expR the specified DatabaseResult object.
     * @param gotR the actual DatabaseResult object.
     * @param matchPattern the List of MatchPattern objects, may be null.
     * @exception SQLUnitException if the assertion failed.
     * @sqlunit.assert name="exception-matches"
     *  description="Asserts that the two results have identical exceptions."
     *  usage="diff,test"
     */
    private static void assertExceptionMatches(final String failureMessage, 
            final DatabaseResult expR, final DatabaseResult gotR, 
            final List matchPatterns) throws SQLUnitException {
        if (expR.isException()) {
            // then gotR must be an exception
            try {
                Assert.assertTrue(failureMessage, gotR.isException());
            } catch (AssertionFailedError e) {
                throw new SQLUnitException(IErrorCodes.ASSERT_FAILED,
                    new String[] {"exception-matches", 
                    "(exception != resultset)",
                    expR.toString(), gotR.toString(), failureMessage});
            }
            // gotR is an exception, check against code if specified
            if (expR.getException().getErrorCode() != null) {
                try {
                    Assert.assertEquals(failureMessage, 
                        expR.getException().getErrorCode(), 
                        gotR.getException().getErrorCode());
                } catch (AssertionFailedError e) {
                    throw new SQLUnitException(IErrorCodes.ASSERT_FAILED,
                        new String[] {"exception-matches", 
                        "(" + expR.getException().getErrorCode() + " != "
                        + gotR.getException().getErrorCode() + " at "
                        + "exception.code)", expR.toString(), gotR.toString(),
                        failureMessage});
                }
            }
            // if exception message specified, check against that
            if (expR.getException().getErrorMessage() != null) {
                try {
                    Assert.assertEquals(failureMessage, 
                        expR.getException().getErrorMessage(),
                        gotR.getException().getErrorMessage());
                } catch (AssertionFailedError e) {
                    throw new SQLUnitException(IErrorCodes.ASSERT_FAILED,
                        new String[] {"exception-matches",
                        "(" + expR.getException().getErrorMessage() + " != "
                        + gotR.getException().getErrorMessage() + " at "
                        + "exception.message)", expR.toString(), 
                        gotR.toString(), failureMessage});
                }
            }
        } else {
            // check to see if the result is an unexpected exception
            try {
                Assert.assertTrue(failureMessage, !gotR.isException());
            } catch (AssertionFailedError e) {
                throw new SQLUnitException(IErrorCodes.ASSERT_FAILED,
                    new String[] {"exception-matches",
                    "(result != exception)", expR.toString(), gotR.toString(),
                    failureMessage});
            }
        }
    }

    /**
     * Asserts that the result returned is not an exception. This can be 
     * useful to regression test stored procedures after schema changes to
     * make sure nothing broke as a result of the schema change.
     * @param failureMessage the user-supplied failure message.
     * @param expR the specified DatabaseResult object.
     * @param gotR the actual DatabaseResult object.
     * @param matchPattern the List of MatchPattern objects, may be null.
     * @exception SQLUnitException if the assertion failed.
     * @sqlunit.assert name="not-exception"
     *  description="Asserts that the result returned is NOT an exception"
     *  usage="diff,test"
     */
    private static void assertNotException(final String failureMessage, 
            final DatabaseResult expR, final DatabaseResult gotR, 
            final List matchPatterns) throws SQLUnitException {
        if (gotR.isException()) {
            throw new SQLUnitException(IErrorCodes.ASSERT_FAILED,
                new String[] {"not-exception", "(result == exception)",
                expR.toString(), gotR.toString(), failureMessage});
        }
    }

    /**
     * Asserts that the number of outparams specified in the DatabaseResult
     * expR is equal to that retrieved in DatabaseResult gotR.
     * @param failureMessage the user-supplied failure message.
     * @param expR the specified DatabaseResult object.
     * @param gotR the actual DatabaseResult object.
     * @param matchPatterns the List of MatchPattern objects.
     * @exception SQLUnitException if the assertion fails.
     * @sqlunit.assert name="number-outparams-equal"
     *  description="Asserts that the two results have the same number of
     *  outparam elements."
     *  usage="diff,test"
     */
    private static void assertNumberOfOutParamsEqual(
            final String failureMessage, final DatabaseResult expR, 
            final DatabaseResult gotR, final List matchPatterns) 
            throws SQLUnitException {
        try {
            Assert.assertEquals(failureMessage, expR.getOutParams().length,
                gotR.getOutParams().length);
        } catch (AssertionFailedError e) {
            throw new SQLUnitException(IErrorCodes.ASSERT_FAILED,
                new String[] {"number-outparams-equal", 
                "(" + expR.getOutParams().length + " != "
                + gotR.getOutParams().length + ")",
                expR.toString(), gotR.toString(), failureMessage});
        }
    }

    /**
     * Asserts that the outparams in both the DatabaseResult objects are
     * equal.
     * @param failureMessage the user-supplied failure message.
     * @param expR the specified DatabaseResult object.
     * @param gotR the actual DatabaseResult object.
     * @param matchPatterns the List of MatchPattern objects.
     * @exception SQLUnitException if the assertion failed.
     * @sqlunit.assert name="outparams-equal"
     *  description="Asserts that the outparams in both the results are equal."
     *  usage="diff,test"
     */
    private static void assertOutParamsEqual(final String failureMessage,
            final DatabaseResult expR, final DatabaseResult gotR, 
            final List matchPatterns) throws SQLUnitException {
        if (expR.getOutParams() != null) {
            OutParam[] expOutParams = expR.getOutParams();
            OutParam[] gotOutParams = gotR.getOutParams();
            // the map holds id to outparam for actuals so we can match by id
            Map outParamIdMap = new HashMap();
            for (int i = 0; i < gotOutParams.length; i++) {
                outParamIdMap.put(gotOutParams[i].getId(), gotOutParams[i]);
            }
            for (int i = 0; i < expOutParams.length; i++) {
                OutParam expOutParam = expOutParams[i];
                OutParam gotOutParam = 
                    (OutParam) outParamIdMap.get(expOutParam.getId());
                if (gotOutParam == null) {
                    throw new AssertionFailedError("outparam[" + (i + 1)
                        + "].id(" + expOutParam.getId() + ") out of bounds");
                }
                try {
                    Assert.assertEquals(failureMessage, 
                        expOutParam, gotOutParam);
                } catch (AssertionFailedError e) {
                    throw new SQLUnitException(IErrorCodes.ASSERT_FAILED,
                        new String[] {"outparams-equal",
                        "(" + expOutParam.toString() + " != "
                        + gotOutParam.toString() + " at "
                        + "outparams[" + i + "])",
                        expR.toString(), gotR.toString(), failureMessage});
                }
            }
        }
    }

    /**
     * Asserts that the update counts, if available in the expected 
     * DatabaseResult object, is equal to that found in the actual
     * DatabaseResult object.
     * @param failureMessage the user-supplied failure message.
     * @param expR the specified DatabaseResult object.
     * @param gotR the actual DatabaseResult object.
     * @param matchPatterns the List of MatchPattern objects.
     * @exception SQLUnitException if the assertion failed.
     * @sqlunit.assert name="update-counts-equal"
     *  description="Asserts that the update counts in the two results
     *  are equal."
     *  usage="diff,test"
     */
    private static void assertUpdateCountsEqual(final String failureMessage,
            final DatabaseResult expR, final DatabaseResult gotR, 
            final List matchPatterns) throws SQLUnitException {
        try {
            Assert.assertEquals(failureMessage, expR.getUpdateCount(),
                gotR.getUpdateCount());
        } catch (AssertionFailedError e) {
            throw new SQLUnitException(IErrorCodes.ASSERT_FAILED,
                new String[] {"update-counts-equal",
                "(" + expR.getUpdateCount() + " != " + gotR.getUpdateCount()
                + ")", expR.toString(), gotR.toString(), failureMessage});
        }
    }

    /**
     * Asserts that the number of resultsets in both the DatabaseResult
     * objects are equal.
     * @param failureMessage the user-supplied failure message.
     * @param expR the specified DatabaseResult object.
     * @param gotR the actual DatabaseResult object.
     * @param matchPatterns the List of MatchPattern objects.
     * @exception SQLUnitException if the assertion failed.
     * @sqlunit.assert name="number-of-resultsets-equal"
     *  description="Asserts that the number of resultsets in the two
     *  results are equal."
     *  usage="diff,test"
     */
    private static void assertNumberOfResultSetsEqual(
            final String failureMessage, final DatabaseResult expR, 
            final DatabaseResult gotR, final List matchPatterns) 
            throws SQLUnitException {
        try {
            Assert.assertEquals(failureMessage, expR.getResultSets().length,
                gotR.getResultSets().length);
        } catch (AssertionFailedError e) {
            throw new SQLUnitException(IErrorCodes.ASSERT_FAILED,
                new String[] {"number-of-resultsets-equal",
                "(" + expR.getResultSets().length + " != "
                + gotR.getResultSets().length + ")",
                expR.toString(), gotR.toString(), failureMessage});
        }
    }

    /**
     * Asserts that the ResultSets in the two DatabaseResult objects are
     * equal. If the matchPatterns is not null, then the two objects are
     * matched against the pattern, rather than checked for exact 
     * equality.
     * @param failureMessage the user-supplied failure message.
     * @param expR the specified DatabaseResult object.
     * @param gotR the actual DatabaseResult object.
     * @param matchPatterns the List of MatchPattern objects.
     * @exception SQLUnitException if the assertion failed.
     * @sqlunit.assert name="resultsets-equal"
     *  description="Asserts that the resultsets are equal, or match if
     *  the test contains embedded match tags."
     *  usage="diff,test"
     */
    private static void assertResultSetsEqual(final String failureMessage,
            final DatabaseResult expR, final DatabaseResult gotR, 
            final List matchPatterns) throws SQLUnitException {
        subAssertResultSetsEqual(failureMessage, expR, gotR, matchPatterns,
            COL_TYPE_VALUE_EQUAL);
    }

    /**
     * Asserts that the columns of the two resultsets are equal in value.
     * All other tests that are done for assertResultSetEquals() will be
     * run, except that the column types will not be considered. The values
     * in the columns of the expected and returned resultsets have to be
     * exactly equal.
     * @param failureMessage the user-supplied failure message.
     * @param expR the specified DatabaseResult object.
     * @param gotR the actual DatabaseResult object.
     * @param matchPatterns a List of MatchPattern objects, this will be
     * overriden and the TypelessMatcher will be passed in instead.
     * @exception SQLUnitException if the assertion fails.
     * @sqlunit.assert name="resultset-values-equal"
     *  description="Asserts that the columns in the specified and actual
     *  resultset have the same values. No assertion is made for types."
     *  usage="diff,test"
     */
    private static void assertResultSetValuesEqual(final String failureMessage,
            final DatabaseResult expR, final DatabaseResult gotR, 
            final List matchPatterns) throws SQLUnitException {
        subAssertResultSetsEqual(failureMessage, expR, gotR, matchPatterns,
            COL_VALUES_EQUAL);
    }

    /**
     * Asserts that the columns of the two resultsets are of the same type.
     * All other tests that are done for assertResultSetEquals() will be
     * run, except that the column values will not be considered.
     * @param failureMessage the user-supplied failure message.
     * @param expR the specified DatabaseResult object.
     * @param gotR the actual DatabaseResult object.
     * @exception SQLUnitException if the assertion fails.
     * @sqlunit.assert name="resultset-types-equal"
     *  description="Asserts that the columns in the specified and actual
     *  resultsets are of the same type."
     *  usage="diff,test"
     */
    private static void assertResultSetTypesEqual(final String failureMessage,
            final DatabaseResult expR, final DatabaseResult gotR,
            final List matchPatterns) throws SQLUnitException {
        subAssertResultSetsEqual(failureMessage, expR, gotR, matchPatterns,
            COL_TYPES_EQUAL);
    }

    /**
     * Asserts that the test will fail and the failure message supplied will 
     * be the same as the first line of the failure message returned by
     * SQLUnit. This is used for checking the SQLUnit code but may have 
     * some practical applications.
     * @param failureMessage the user-supplied failure message.
     * @param expR the specified DatabaseResult object.
     * @param gotR the actual DatabaseResult object.
     * @param matchPatterns a List of match patterns.
     * @exception SQLUnitExcep3tion if the assertion failed.
     * @sqlunit.assert name="fail-with-failure"
     *  description="Asserts that the test will fail with the specified
     *  error message."
     *  usage="diff,test"
     */
    private static void assertFailWithFailure(final String failureMessage,
            final DatabaseResult expR, final DatabaseResult gotR, 
            final List matchPatterns) throws SQLUnitException {
        String passedInFailureMessage = failureMessage;
        if (passedInFailureMessage == null) {
            passedInFailureMessage = "NULL";
        }
        String gotFailureMessage = "NULL";
        try {
            assertEqual(passedInFailureMessage, expR, gotR, matchPatterns);
        } catch (SQLUnitException e) {
            gotFailureMessage = getFirstLine(e.getMessage());
        }
        if (!gotFailureMessage.equals("NULL")
                && !gotFailureMessage.equals(passedInFailureMessage)) {
            throw new SQLUnitException(IErrorCodes.ASSERT_FAILED,
                new String[] {"fail-with-failure", 
                "(\"" + passedInFailureMessage + "\" != \"" 
                + gotFailureMessage + "\")", expR.toString(), expR.toString(),
                failureMessage});
        }
    }

    // --------- BatchDatabaseResult asserts --------

    /**
     * Asserts nothing. This is used to assert that there are no
     * equality or non-equality relationships between the generated
     * and the expected result.
     * @param failureMessage the user-supplied failure message.
     * @param expR the specified BatchDatabaseResult object.
     * @param gotR the actual BatchDatabaseResult object.
     * @exception SQLUnitException if the assertion failed.
     * @sqlunit.assert name="none"
     *  description="Asserts nothing about the generated and the actual
     *  batch results. This is typically used to disable assertions for the 
     *  particular test."
     *  usage="diff,test"
     */
    private static void assertNone(final String failureMessage,
            final BatchDatabaseResult expR, BatchDatabaseResult gotR)
            throws SQLUnitException {
        return;
    }

    /**
     * Asserts that the two BatchDatabaseResults are equal.
     * @param failureMessage the user-supplied failure message.
     * @param expR the specified BatchDatabaseResult object.
     * @param gotR the actual BatchDatabaseResult object.
     * @exception SQLUnitException if the assertion failed.
     * @sqlunit.assert name="equal"
     *  description="Asserts that the two batchresults are equal. This is
     *  a macro assertion, composed of a sequence of assertions, which are
     *  executed serially. This is the default assertion if no assert 
     *  attribute is supplied for the batchtest tag."
     *  usage="batchtest"
     */
    private static void assertEqual(final String failureMessage,
            final BatchDatabaseResult expR, final BatchDatabaseResult gotR) 
            throws SQLUnitException {
        String conditions = "failed-at-equal,"
            + "expected-count-equal,"
            + "update-count-equal";
         assertIsTrue(failureMessage, expR, gotR, conditions);
    }

    /**
     * Asserts that the two BatchDatabaseResults are not equal.
     * @param failureMessage the user-supplied failure message.
     * @param expR the specified BatchDatabaseResult object.
     * @param gotR the actual BatchDatabaseResult object.
     * @exception SQLUnitException if the assertion failed.
     * @sqlunit.assert name="not-equal"
     *  description="Asserts that the two batchresults are not equal. This
     *  is the inverse of the equal assertion."
     *  usage="batchtest"
     */
    private static void assertNotEqual(final String failureMessage,
           final BatchDatabaseResult expR, final BatchDatabaseResult gotR) 
           throws SQLUnitException {
        try {
            assertEqual(failureMessage, expR, gotR);
        } catch (SQLUnitException e) { return; }
        throw new SQLUnitException(IErrorCodes.ASSERT_FAILED,
            new String[] {"not-equal", "", expR.toString(), gotR.toString(),
            failureMessage});
    }

    /**
     * Asserts that the failed-at attribute of the batchresult, if 
     * specified, points to the point of actual failure of the result
     * generated by the batchcall or batchsql.
     * @param failureMessage the user-supplied failure message.
     * @param expR the specified BatchDatabaseResult object.
     * @param gotR the actual BatchDatabaseResult object.
     * @exception SQLUnitException if the assertion failed.
     * @sqlunit.assert name="failed-at-equal"
     *  description="Asserts that the failed-at attribute of the batchresult
     *  tag, if specified, points to the point where the result generated
     *  by the batchcall or batchsql actually failed."
     *  usage="batchtest"
     */
    private static void assertFailedAtEqual(final String failureMessage,
           final BatchDatabaseResult expR, final BatchDatabaseResult gotR) 
           throws SQLUnitException {
        // execute the assert only if failed-at is specified in expR
        if (expR.getFailedAtIndex() > -1) {
            if (expR.getActualCount() >= (expR.getFailedAtIndex() + 1)) {
                // updatecounts are explicitly specified
                if (gotR.getUpdateCountAt(expR.getFailedAtIndex()).equals(
                    expR.getUpdateCountAt(expR.getFailedAtIndex()))) {
                    throw new SQLUnitException(IErrorCodes.ASSERT_FAILED,
                        new String[] {"failed-at-equal",
                        "(" + expR.getUpdateCountAt(expR.getFailedAtIndex())
                        + " == "
                        + gotR.getUpdateCountAt(expR.getFailedAtIndex())
                        + " at updatecount[" + expR.getFailedAtIndex() + "])",
                        expR.toString(), gotR.toString(), failureMessage});
                }
            } else {
                // updatecounts are specified as expected-count
                if (!gotR.getUpdateCountAt(expR.getFailedAtIndex()).
                        equals("EXECUTE_FAILED")) {
                    throw new SQLUnitException(IErrorCodes.ASSERT_FAILED,
                        new String[] {"failed-at-equal", "(EXECUTE_FAILED != "
                        + gotR.getUpdateCountAt(expR.getFailedAtIndex())
                        + " at updatecount[" + expR.getFailedAtIndex() + "])",
                        expR.toString(), gotR.toString(), failureMessage});
                }
            }
        }
    }

    /**
     * Asserts that the number of updatecount elements in the source and
     * target BatchDatabaseResults are equal.
     * @param failureMessage the user-supplied failure message.
     * @param expR the specified BatchDatabaseResult object.
     * @param gotR the actual BatchDatabaseResult object.
     * @exception SQLUnitException if the assertion failed.
     * @sqlunit.assert name="expected-count-equal"
     *  description="Asserts that the number of update counts specified
     *  in the expected batchresult, either as an expected-count attribute,
     *  or listed explicitly, is the same as the one in the generated 
     *  batchresult."
     *  usage="batchtest"
     */
    private static void assertExpectedCountEqual(final String failureMessage,
           final BatchDatabaseResult expR, final BatchDatabaseResult gotR) 
           throws SQLUnitException {
        if (expR.getExpectedCount() > -1) {
            // expected-count supplied in the expR, compare counts
            if (expR.getExpectedCount() != gotR.getActualCount()) {
                throw new SQLUnitException(IErrorCodes.ASSERT_FAILED,
                    new String[] {"expected-count-equal",
                    "(" + expR.getExpectedCount() + " != "
                    + gotR.getActualCount() + " at batchresult.expected-count)",
                    expR.toString(), gotR.toString(), failureMessage});
            }
        } else {
            // compare the counts in both results
            // caller can specify no updatecount elements if this is not
            // to be checked.
            if (expR.getActualCount() > 0) {
                if (expR.getActualCount() != gotR.getActualCount()) {
                    throw new SQLUnitException(IErrorCodes.ASSERT_FAILED,
                        new String[] {"expected-count-equal",
                        "(" + expR.getActualCount() + " != "
                        + gotR.getActualCount() 
                        + " at batchresult.#updatecount)",
                        expR.toString(), gotR.toString(), failureMessage});
                }
            }
        }
    }

    /**
     * Asserts that the update counts is equal in both the expected and
     * actual BatchDatabaseResult objects.
     * @param failureMessage the user-supplied failure message.
     * @param expR the specified BatchDatabaseResult object.
     * @param gotR the actual BatchDatabaseResult object.
     * @exception SQLUnitException if the assertion failed.
     * @sqlunit.assert name="updatecount-equal"
     *  description="Asserts that the updatecount values are the same
     *  for the result specified by batchresult and the result generated
     *  by the call to batchsql or batchcall."
     *  usage="batchtest"
     */
    private static void assertUpdateCountEqual(final String failureMessage,
           final BatchDatabaseResult expR, final BatchDatabaseResult gotR) 
           throws SQLUnitException {
        // only do the assert if the expected-count is not specified
        if (expR.getExpectedCount() == -1) {
            int expectedActual = expR.getActualCount();
            for (int i = 0; i < expectedActual; i++) {
                // skip a failed updatecount since we know they are not equal
                if (i == expR.getFailedAtIndex()) { continue; }
                if (!expR.getUpdateCountAt(i).equals(
                        gotR.getUpdateCountAt(i))) {
                    throw new SQLUnitException(IErrorCodes.ASSERT_FAILED,
                        new String[] {"updatecount-equal",
                        "(" + expR.getUpdateCountAt(i).toString() + " != "
                        + gotR.getUpdateCountAt(i).toString(),
                        "at updatecount[" + i + "])",
                        expR.toString(), gotR.toString(), failureMessage});
                }
            }
        }
    }

    /**
     * Asserts that the test will fail and the failure message supplied will
     * be the same as the first line of the failure message returned from the
     * test. This is primarily used for testing SQLUnit code, but may have 
     * some practical uses as well.
     * @param failureMessage the user-supplied failure message.
     * @param expR the specified BatchDatabaseResult object.
     * @param gotR the actual BatchDatabaseResult object.
     * @exception SQLUnitException if the assertion failed.
     * @sqlunit.assert name="fail-with-failure"
     *  description="Asserts that the test will fail with the user-supplied
     *  failure message."
     *  usage="batchtest"
     */
    private static void assertFailWithFailure(final String failureMessage,
            final BatchDatabaseResult expR, final BatchDatabaseResult gotR)
            throws SQLUnitException {
        String passedInFailureMessage = failureMessage;
        if (passedInFailureMessage == null) { 
            passedInFailureMessage = "NULL"; 
        }
        String gotFailureMessage = "NULL";
        try {
            assertEqual(passedInFailureMessage, expR, gotR);
        } catch (SQLUnitException e) { 
            gotFailureMessage = getFirstLine(e.getMessage()); 
        }
        if (!gotFailureMessage.equals("NULL")
                && !gotFailureMessage.equals(passedInFailureMessage)) {
            throw new SQLUnitException(IErrorCodes.ASSERT_FAILED,
                new String[] {"not-equal", 
                "(\"" + passedInFailureMessage + "\" != \"" 
                + gotFailureMessage + "\")", expR.toString(), gotR.toString(),
                failureMessage});
        }
    }

    /**
     * Builds a Map of assert condition calls to the assert method names
     * from a two dimensional String array.
     * @param data a two dimensional String array.
     * @return a Map of name-value pairs.
     */
    private static Map buildMap(final String[][] data) {
        Map map = new HashMap();
        for (int i = 0; i < data.length; i++) {
            String key = data[i][0];
            String value = data[i][1];
            map.put(key, value);
        }
        return map;
    }

    /**
     * Returns the first line of a multi-line string.
     * @param multiline a line with one or more embedded newlines.
     * @return the first line.
     */
    private static String getFirstLine(final String multiline) {
        int pos = multiline.indexOf(IErrorCodes.LF);
        if (pos <= 0) { 
            return multiline; 
        } else { 
            return multiline.substring(0, pos); 
        }
    }

    /**
     * Invokes the assertion with the method name and an array of Object
     * parameters to the method.
     * @param condition the name of the assertion.
     * @param paramClasses an array of Class objects.
     * @param methodMap a Mapping of conditions to method names.
     * @param params an array of Object parameters to the assertion.
     * @exception SQLUnitException if the assertion failed.
     */
    private static void invokeAssertion(final String condition, 
            final Class[] paramClasses, final Map methodMap, 
            final Object[] params) throws SQLUnitException {
        // assertions can be a comma-separated String, get them
        String[] conditions = condition.split("\\s*,\\s*");
        for (int i = 0; i < conditions.length; i++) {
            String methodName = (String) methodMap.get(conditions[i]);
            if (methodName == null) {
                throw new SQLUnitException(IErrorCodes.NO_ASSERT_METHOD,
                    new String[] {conditions[i]});
            }
            try {
                Method method = Assertions.class.getDeclaredMethod(
                    methodName, paramClasses);
                method.invoke(null, params);
            } catch (InvocationTargetException e) {
                // this will be an SQLUnitException
                throw new SQLUnitException(e.getCause().getMessage(),
                    e.getCause());
            } catch (Exception e) {
                throw new SQLUnitException(IErrorCodes.GENERIC_ERROR,
                    new String[] {"System", e.getClass().getName(),
                    e.getMessage()}, e);
            }
        }
    }

    /**
     * Asserts that the two resultsets are equal. The equality of columns
     * is governed by the matchMode argument passed in.
     * @param failureMessage the user-supplied failure message.
     * @param expR the expected DatabaseResult object.
     * @param gotR the supplied DatabaseResult object.
     * @param matchPatterns a List of Matcher objects which will govern
     * the kind of value matching of Column objects.
     * @param matchMode whether the matching should be full, type-based
     * only or value-based only.
     * @exception SQLUnitException if thrown.
     * @exception AssertionFailedError if thrown.
     */
    private static void subAssertResultSetsEqual(final String failureMessage,
            final DatabaseResult expR, final DatabaseResult gotR,
            final List matchPatterns, final int matchMode)
            throws SQLUnitException, AssertionFailedError {
        String matchModeStr = "resultsets-equal";
        try {
            ResultSetBean[] expResultSets = expR.getResultSets();
            ResultSetBean[] gotResultSets = gotR.getResultSets();
            for (int i = 0; i < expResultSets.length; i++) {
                if (expResultSets[i].isRowCountOverriden()) {
                    // row count override check
                    subAssertRowCountOverrideEqual(expResultSets[i], 
                        gotResultSets[i], expResultSets[i].getId());
                    continue;
                }
                if (!expResultSets[i].isPartial()) {
                    // actual row count check
                    subAssertRowCountEqual(expResultSets[i], gotResultSets[i],
                        expResultSets[i].getId());
                }
                Row[] expRows = expResultSets[i].getRows();
                Row[] gotRows = gotResultSets[i].getRows();
                // sort both Row[] objects in place
                if (expResultSets[i].getSortBy() != null) {
                    // special value "none" turns off sorting
                    if (!("NONE").equalsIgnoreCase(
                            expResultSets[i].getSortBy()[0])) {
                        // sort only the expResultSet, since the gotResultSet
                        // is pre-sorted by the database
                        expResultSets[i].sort();
                    }
                } else {
                    // sort both gotR and expR by their natural order
                    expResultSets[i].sort();
                    gotResultSets[i].sort();
                }
                for (int j = 0; j < expRows.length; j++) {
                    if (!expRows[j].isPartial()) {
                        // check each row for column count equality
                        subAssertColumnCountEqual(expRows[j], gotRows[j],
                            expResultSets[i].getId(), expRows[j].getId());
                    }
                    Row expRow;
                    Row gotRow;
                    if (expResultSets[i].isPartial()) {
                        // Read the (1-based) id from expRow, then pull up
                        // appropriate gotRow by its 0-based position
                        expRow = expRows[j];
                        try {
                            gotRow = gotRows[
                                Integer.parseInt(expRow.getId()) - 1];
                        } catch (NumberFormatException e) {
                            throw new AssertionFailedError("row[" + 
                                (i + 1) + "," + (j + 1) + "].id(" + 
                                expRow.getId() + ") must be numeric");
                        } catch (ArrayIndexOutOfBoundsException e) {
                            throw new AssertionFailedError("row[" + 
                                (i + 1) + "," + (j + 1) + "].id(" + 
                                expRow.getId() + ") out of bounds");
                        }
                    } else {
                        expRow = expRows[j];
                        gotRow = gotRows[j];
                    }
                    Col[] expCols = expRow.getCols();
                    Col[] gotCols = gotRow.getCols();
                    for (int k = 0; k < expCols.length; k++) {
                        Col expCol;
                        Col gotCol;
                        if (expRow.isPartial()) {
                            // Read the (1-based) id from expCol, then pull
                            // up appropriate gotCol by its 0-based position.
                            expCol = expCols[k];
                            try {
                                gotCol = gotCols[
                                    Integer.parseInt(expCol.getId()) - 1];
                            } catch (NumberFormatException e) {
                                throw new AssertionFailedError("col[" +
                                    (i + 1) + "," + (j + 1) + "," + (k + 1) +
                                    "].id(" + expCol.getId() +
                                    ") must be numeric");
                            } catch (ArrayIndexOutOfBoundsException e) {
                                throw new AssertionFailedError("col[" +
                                    (i + 1) + "," + (j + 1) + "," + (k + 1) +
                                    "].id(" + expCol.getId() +
                                    ") out of bounds");
                            }
                        } else {
                            expCol = expCols[k];
                            gotCol = gotCols[k];
                        }
                        boolean matcherFound = false;
                        if (matchPatterns != null) {
                            // user defined matching
                            Iterator mpiter = matchPatterns.iterator();
                            while (mpiter.hasNext()) {
                                MatchPattern mp = (MatchPattern) mpiter.next();
                                if (mp.canApply((i + 1), (j + 1), (k + 1))) {
                                    matcherFound = true;
                                    subAssertColumnsMatch(expCol, gotCol, mp, 
                                        expResultSets[i].getId(),
                                        expRow.getId(), expCol.getId());
                                }
                            } // mpiter loop
                        } // matchPatterns != null
                        if (matchPatterns == null || !matcherFound) {
                            switch(matchMode) {
                                case Assertions.COL_TYPES_EQUAL:
                                    matchModeStr = "resultset-types-equal";
                                    subAssertColumnTypesEqual(expCol,
                                        gotCol, expResultSets[i].getId(),
                                        expRow.getId(), expCol.getId());
                                    break;
                                case Assertions.COL_VALUES_EQUAL:
                                    matchModeStr = "resultset-values-equal";
                                    subAssertColumnValuesEqual(expCol,
                                        gotCol, expResultSets[i].getId(),
                                        expRow.getId(), expCol.getId());
                                    break;
                                case Assertions.COL_TYPE_VALUE_EQUAL:
                                default:
                                    // default match, exact equality
                                    matchModeStr = "resultsets-equal";
                                    subAssertColumnsEqual(expCol,
                                        gotCol, expResultSets[i].getId(),
                                        expRow.getId(), expCol.getId());
                                    break;
                            }
                        }
                    } // col loop (k)
                } // row loop (j)
            } // resultset loop (i)
        } catch (AssertionFailedError e) {
            throw new SQLUnitException(IErrorCodes.ASSERT_FAILED,
                new String[] {matchModeStr, "(" + e.getMessage() + ")", 
                expR.toString(), gotR.toString(), failureMessage}, e);
        }
    } 

    /**
     * Asserts that the rowcount attribute is the same for both the
     * specified and actual ResultSetBean objects.
     * @param expResultSet the bean representing the specified ResultSet.
     * @param gotResultSet the bean representing the actual ResultSet.
     * @param resultSetIndex the index of the resultset within the result.
     * @exception AssertionFailedError if the assertion failed.
     */
    private static void subAssertRowCountOverrideEqual(
            final ResultSetBean expResultSet, final ResultSetBean gotResultSet,
            final String resultSetIndex) throws AssertionFailedError {
        try {
            Assert.assertEquals(expResultSet.getRowCount(), 
                gotResultSet.getRowCount());
        } catch (AssertionFailedError e) {
            throw new AssertionFailedError(expResultSet.getRowCount()
                + " != " + gotResultSet.getRowCount() + " at result["
                + resultSetIndex + "].rowcount");
        }
    }

    /**
     * Asserts that the number of rows in the specified Row[] object
     * is the same as the one returned from the SQL or stored procedure
     * call.
     * @param expResultSet the bean representing the specified ResultSet.
     * @param gotResultSet the bean representing the actual ResultSet.
     * @param resultSetIndex the index of the resultset within the result.
     * @exception AssertionFailedError if the assertion failed.
     */
    private static void subAssertRowCountEqual(final ResultSetBean expResultSet,
            final ResultSetBean gotResultSet, final String resultSetIndex) 
            throws AssertionFailedError {
        try {
            Assert.assertEquals(expResultSet.getRows().length, 
                gotResultSet.getRows().length);
        } catch (AssertionFailedError e) {
            throw new AssertionFailedError(expResultSet.getRows().length
                + " != " + gotResultSet.getRows().length + " at result["
                + resultSetIndex + "].#rows");
        }
    }

    /**
     * Asserts that the number of columns in the two Row objects are equal.
     * @param expRow the bean representing the specified Row.
     * @param gotRow the bean representing the actual Row.
     * @param resultSetIndex the index of the ResultSet within the Result.
     * @param rowIndex the index of the row within the ResultSet.
     * @exception AssertionFailedError if the assertion failed.
     */
    private static void subAssertColumnCountEqual(final Row expRow, 
            final Row gotRow, final String resultSetIndex, 
            final String rowIndex) throws AssertionFailedError {
        try {
            Assert.assertEquals(expRow.getCols().length, 
                gotRow.getCols().length);
        } catch (AssertionFailedError e) {
            throw new AssertionFailedError(expRow.getCols().length
                + " != " + gotRow.getCols().length + " at result["
                + resultSetIndex + "," + rowIndex + "].#cols");
        }
    }

    /**
     * Asserts that the two Column objects match per the MatchPattern's
     * rules.
     * @param expCol the bean representing the specified Column.
     * @param gotCol the bean representing the actual Column.
     * @param mp the MatchPattern object.
     * @param resultSetIndex the index of the resultset within the result.
     * @param rowIndex the index of the row within the resultset.
     * @param colIndex the index of the column within the row.
     * @exception SQLUnitException if the Matcher could not be instantiated.
     * @exception AssertionFailedError if the assertion failed.
     */
    private static void subAssertColumnsMatch(final Col expCol, 
            final Col gotCol, final MatchPattern mp, 
            final String resultSetIndex, final String rowIndex, 
            final String colIndex) 
            throws SQLUnitException, AssertionFailedError {
        boolean matched = mp.applyMatcher(
            expCol.getValue(), gotCol.getValue());
        if (!matched) {
            throw new AssertionFailedError(expCol.getValue() + " !~ "
                + gotCol.getValue() + " (" + mp.getMatcherClass()
                + ") at result[" + resultSetIndex + ","
                + rowIndex + "," + colIndex + "]");
        }
    }

    /**
     * Asserts that the two Column objects are same in value and type.
     * @param expCol the bean representing the specified Column.
     * @param gotCol the bean representing the actual Column.
     * @param resultSetIndex the index of the resultset within the result.
     * @param rowIndex the index of the row within the resultset.
     * @param colIndex the index of the column within the row.
     * @exception AssertionFailedError if the assertion failed.
     */
    private static void subAssertColumnsEqual(final Col expCol, 
            final Col gotCol, final String resultSetIndex, 
            final String rowIndex, final String colIndex) 
            throws AssertionFailedError {
        try {
            Assert.assertEquals(expCol, gotCol);
        } catch (AssertionFailedError e) {
            throw new AssertionFailedError(expCol.toString() + " != "
                + gotCol.toString() + " at result[" + resultSetIndex + ","
                + rowIndex + "," + colIndex + "]");
        }
    }

    /**
     * Asserts that the two Column objects resolve to the same toString()
     * value for their contents.
     * @param expCol the bean representing the specified Column.
     * @param gotCol the bean representing the actual Column.
     * @param resultSetIndex the index of the resultset within the result.
     * @param rowIndex the index of the row within the resultset.
     * @param colIndex the index of the column within the row.
     * @exception AssertionFailedError if the assertion failed.
     */
    private static void subAssertColumnValuesEqual(final Col expCol,
            final Col gotCol, final String resultSetIndex, 
            final String rowIndex, final String colIndex)
            throws AssertionFailedError {
        try {
            Assert.assertEquals(expCol.getValue(), gotCol.getValue());
        } catch (AssertionFailedError e) {
            throw new AssertionFailedError(expCol.getValue() + " != "
                + gotCol.getValue() + " at result[" + resultSetIndex + ","
                + rowIndex + "," + colIndex + "]");
        }
    }

    /**
     * Asserts that the two Column objects are of the same type.
     * @param expCol the bean representing the specified Column.
     * @param gotCol the bean representing the actual Column.
     * @param resultSetIndex the index of the resultset within the result.
     * @param rowIndex the index of the row within the resultset.
     * @param colIndex the index of the column within the row.
     * @exception AssertionFailedError if the assertion failed.
     */
    private static void subAssertColumnTypesEqual(final Col expCol,
            final Col gotCol, final String resultSetIndex,
            final String rowIndex, final String colIndex)
            throws AssertionFailedError {
        try {
            Assert.assertEquals(expCol.getType(), gotCol.getType());
        } catch (AssertionFailedError e) {
            throw new AssertionFailedError(expCol.getType() + " != "
                + gotCol.getType() + " at result[" + resultSetIndex + ","
                + rowIndex + "," + colIndex + "]");
        }
    }
}
