/*
 * $Id: SQLUnitMockDatabase.java,v 1.21 2006/01/07 02:27:23 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/mock/SQLUnitMockDatabase.java,v $
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
package net.sourceforge.sqlunit.test.mock;

import net.sourceforge.sqlunit.test.Dictionary;

import com.mockrunner.mock.jdbc.MockBlob;
import com.mockrunner.mock.jdbc.MockClob;
import com.mockrunner.mock.jdbc.MockResultSet;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Statement;
import java.sql.Types;

/**
 * Mock database to supply results.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.21 $
 */
public class SQLUnitMockDatabase extends AbstractMockDatabase {

    private static final Logger LOG = Logger.getLogger(
        SQLUnitMockDatabase.class);

    // constants
    private static final int BYTES_IN_KB = 1024;
    private static final int FOUR_RESULTSETS = 4;
    private static final int TOPIC_TYPE_ID = 3;
    private static final int TOPIC_ID1 = 1;
    private static final int TOPIC_ID2 = 1000;
    private static final int LOCALE_ID = 1;
    private static final int ERROR_CODE = 1234;

    private static final int CASE_ZERO = 0;
    private static final int CASE_ONE = 1;
    private static final int CASE_TWO = 2;
    private static final int CASE_THREE = 3;
    private static final int CASE_FOUR = 4;

    // misc ids
    private static final int BOND_ID = 7;
    private static final int SUPERMAN_ID = 2;
    private static final int SPIDERMAN_ID = 1;
    private static final int BATMAN_ID = 3;
    private static final float NUMERIC_VALUE = 300.12f;
    private static final float DECIMAL_VALUE = 301.13f;
    private static final String DATABASE_NAME = "mockdatabase";

    // used with the foreach tag testing methods.
    private static int foreachUpdateCount = 0;
    private static boolean inInsert1Row = false;
    private static boolean inInsert2Row = false;


    /**
     * A simple method returning a single resultset with a single int column.
     * @param index the result set id.
     * @return a single MockResult with a single row and single column.
     */
    public final MockResultSet aSimpleResult(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch(rsid) {
            case 0: 
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                MockResultSet mrs = new MockResultSet("simpleResult:1");
                mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("col1", Types.INTEGER)}));
                mrs.addRow(new Object[] {new Integer(1)});
                LOG.debug("simpleResult:1:" + mrs.toString());
                return mrs;
            default:
                return null;
        }
    }

    /**
     * A simple method returning a single resultset with a single int column.
     * @param index the result set id.
     * @return a single MockResult with a single row and single column.
     */
    public final MockResultSet anotherSimpleResult(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch(rsid) {
            case 0: 
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                MockResultSet mrs = new MockResultSet("anotherSimpleResult:1");
                mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("col1", Types.INTEGER)}));
                mrs.addRow(new Object[] {new Integer(2)});
                LOG.debug("anotherSimpleResult:1:" + mrs.toString());
                return mrs;
            default:
                return null;
        }
    }

    /**
     * A method that returns the string "another" as a single column in
     * a single row in a single resultset.
     * @param index the result set id.
     * @return a single MockResultSet with single row and single column.
     */
    public final MockResultSet anotherInResultSet(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch(rsid) {
            case 0:
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                MockResultSet mrs = new MockResultSet("anotherInResultSet:1");
                mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("another", Types.VARCHAR)}));
                mrs.addRow(new Object[] {new String("another")});
                LOG.debug("anotherInResultSet:1:" + mrs.toString());
                return mrs;
            default:
                return null;
        }
    }

    /**
     * A simple method returning a single resultset with a single int column
     * which contains a NULL.
     * @param index the result set id.
     * @return a single MockResult with a single row and single column.
     */
    public final MockResultSet aSimpleNullResult(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch(rsid) {
            case 0: 
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                MockResultSet mrs = new MockResultSet("anotherSimpleResult:1");
                mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("col1", Types.INTEGER)}));
                mrs.addRow(new Object[] {null});
                LOG.debug("anotherSimpleResult:1:" + mrs.toString());
                return mrs;
            default:
                return null;
        }
    }

    /**
     * Returns a MockResult with a single row and a single column named 'foo'
     * containing the String value 'foo'.
     * @param index the result set id.
     * @return a single MockResult with a single row and single column.
     */
    public final MockResultSet aSimpleStringResult(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch(rsid) {
            case 0:
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                MockResultSet mrs = new MockResultSet("foo:1");
                mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("foo", Types.VARCHAR)}));
                mrs.addRow(new Object[] {"foo"});
                LOG.debug("foo:1:" + mrs.toString());
                return mrs;
            default:
                return null;
        }
    }

    /**
     * Returns a single resultset with multiple columns in each row. This
     * is used to test the functionality of the assert-resultset-equals
     * assertion.
     * @param index the result set id.
     * @return a single MockResult with two rows and three columns.
     */
    public final MockResultSet aSimpleMultiColResult(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch(rsid) {
            case 0:
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                MockResultSet mrs = 
                    new MockResultSet("aSimpleMultiColResult:1");
                mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("col1", Types.INTEGER),
                    new ColumnMetaData("col2", Types.VARCHAR),
                    new ColumnMetaData("col3", Types.INTEGER)}));
                mrs.addRow(new Object[] {new Integer(1),
                    new String("Larry"), new Integer(31)});
                mrs.addRow(new Object[] {new Integer(2),
                    new String("Curly"), new Integer(32)});
                LOG.debug("aSimpleMultiColResult:1:" + mrs.toString());
                return mrs;
            default:
                return null;
        }
    }

    /**
     * A method returning multiple result sets. Each call returns a single
     * result set, but different values of index will return different
     * result sets. Mimics a stored procedure call with multiple result
     * sets returned, such as found in Sybase T-SQL.
     * @param index the result set id.
     * @return a single MockResultSet per index call.
     */
    public final MockResultSet multipleResults(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case CASE_ZERO:
                return MockResultSetUtils.buildZerothResultSet(FOUR_RESULTSETS);
            case CASE_ONE:
                MockResultSet mrs1 = new MockResultSet("multipleResults:1");
                mrs1.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("topicId", Types.INTEGER),
                    new ColumnMetaData("topic", Types.VARCHAR),
                    new ColumnMetaData("topicTypeId", Types.INTEGER)}));
                mrs1.addRow(new Object[] {
                    new Integer(1), new String("Junk Topics"), 
                    new Integer(TOPIC_TYPE_ID)});
                LOG.debug("multipleResults:1:" + mrs1.toString());
                return mrs1;
            case CASE_TWO:
                return new MockResultSet("multipleResults:2");
            case CASE_THREE:
                return new MockResultSet("multipleResults:3");
            case CASE_FOUR:
                MockResultSet mrs4 = new MockResultSet("multipleResults:4");
                mrs4.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("topicId", Types.INTEGER),
                    new ColumnMetaData("ancestorTopicId", Types.INTEGER),
                    new ColumnMetaData("topic", Types.VARCHAR),
                    new ColumnMetaData("localeId", Types.INTEGER),
                    new ColumnMetaData("description", Types.VARCHAR)}));
                mrs4.addRow(new Object[] {
                    new Integer(TOPIC_ID1), new Integer(TOPIC_ID1), 
                    new String("Unused Topics"),
                    new Integer(LOCALE_ID), new String("Unused")});
                mrs4.addRow(new Object[] {
                    new Integer(TOPIC_ID1), new Integer(TOPIC_ID2), 
                    new String("Deprecated Topics"),
                    new Integer(LOCALE_ID), new String("Deprecated")});
                LOG.debug("multipleResults:4:" + mrs4.toString());
                return mrs4;
            default:
                return null;
        }
    }

    /**
     * Another method returning multiple result sets. This is similar to 
     * the multipleResults() call, but returns different datatypes
     * than that one.
     * @param index the result set id.
     * @return a single MockResultSet per index call.
     */
    public final MockResultSet anotherMultipleResultsDifferentTypes(
            final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case CASE_ZERO:
                return MockResultSetUtils.buildZerothResultSet(FOUR_RESULTSETS);
            case CASE_ONE:
                MockResultSet mrs1 = new MockResultSet(
                    "anotherMultipleResultsDifferentTypes:1");
                mrs1.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("topicId", Types.SMALLINT),
                    new ColumnMetaData("topic", Types.CHAR),
                    new ColumnMetaData("topicTypeId", Types.SMALLINT)}));
                mrs1.addRow(new Object[] {
                    new Short((new Integer(1)).shortValue()), 
                    new String("Junk Topics"), 
                    new Short((new Integer(TOPIC_TYPE_ID)).shortValue())});
                LOG.debug("anotherMultipleResultsDifferentTypes:1:"
                    + mrs1.toString());
                return mrs1;
            case CASE_TWO:
                return new MockResultSet(
                    "anotherMultipleResultsDifferentTypes:2");
            case CASE_THREE:
                return new MockResultSet(
                    "anotherMultipleResultsDifferentTypes:3");
            case CASE_FOUR:
                MockResultSet mrs4 = new MockResultSet(
                    "anotherMultipleResultsDifferentTypes:4");
                mrs4.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("topicId", Types.SMALLINT),
                    new ColumnMetaData("ancestorTopicId", Types.SMALLINT),
                    new ColumnMetaData("topic", Types.CHAR),
                    new ColumnMetaData("localeId", Types.SMALLINT),
                    new ColumnMetaData("description", Types.CHAR)}));
                mrs4.addRow(new Object[] {
                    new Short((new Integer(TOPIC_ID1)).shortValue()), 
                    new Short((new Integer(TOPIC_ID1)).shortValue()), 
                    new String("Unused Topics"),
                    new Short((new Integer(LOCALE_ID)).shortValue()), 
                    new String("Unused")});
                mrs4.addRow(new Object[] {
                    new Short((new Integer(TOPIC_ID1)).shortValue()), 
                    new Short((new Integer(TOPIC_ID2)).shortValue()), 
                    new String("Deprecated Topics"), 
                    new Short((new Integer(LOCALE_ID)).shortValue()),
                    new String("Deprecated")});
                LOG.debug("anotherMultipleResultsDifferentTypes:4:"
                    + mrs4.toString());
                return mrs4;
            default:
                return null;
        }
    }

    /**
     * A method returning a return code only.
     * @param index the result set id.
     * @return a MockResult.
     */
    public final MockResultSet simpleReturnCode(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case -1:
                return MockResultSetUtils.buildScalarOutParam("100");
            case 0:
                return MockResultSetUtils.buildZerothResultSet(0);
            default:
                return null;
        }
    }

    /**
     * A method that is designed to throw an SQLException. Since the framework
     * does not allow us to throw an SQLException directly, we will wrap this
     * in a ResultSet with type = Integer.MAX_INT.
     * @param index the result set id.
     * @return a MockResultSet at index 1 containing an embedded SQLException.
     */
    public final MockResultSet exceptionResult(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case 0:
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                return MockResultSetUtils.buildSQLExceptionResultSet(
                    ERROR_CODE, "Test Exception");
            default:
                return null;
        }
    }

    /**
     * Returns an Oracle style REF CURSOR in the first outparam element.
     * @param index the result set id.
     * @return a MockResultSet at the specified index.
     */
    public final MockResultSet refCursorReturn(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case -1:
                MockResultSet mrs = new MockResultSet("refCursorReturn:-1");
                mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("agentId", Types.INTEGER),
                    new ColumnMetaData("name", Types.VARCHAR),
                    new ColumnMetaData("drink", Types.VARCHAR)}));
                mrs.addRow(new Object[] {
                    new Integer(BOND_ID), new String("James Bond"),
                    new String("Martini")});
                return mrs;
            case 0:
                return MockResultSetUtils.buildZerothResultSet(0);
            default:
                return null;
        }
    }

    /**
     * Returns a result code and a single resultset.
     * @param index the result set id.
     * @return a MockResultSet at the specified index.
     */
    public final MockResultSet resultAndOneResultSet(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case -1:
                return MockResultSetUtils.buildScalarOutParam("143");
            case 0:
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                MockResultSet mrs = new MockResultSet("refCursorReturn:-1");
                mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("agentId", Types.INTEGER),
                    new ColumnMetaData("name", Types.VARCHAR),
                    new ColumnMetaData("drink", Types.VARCHAR)}));
                mrs.addRow(new Object[] {
                    new Integer(BOND_ID), new String("James Bond"),
                    new String("Martini")});
                return mrs;
            default:
                return null;
        }
    }

    /**
     * Returns no return code and no resultset.
     * @param index the result set id.
     * @return a MockResultSet at the specified index.
     */
    public final MockResultSet voidReturnAndNoResultSet(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case 0: 
                return MockResultSetUtils.buildZerothResultSet(0);
            default: 
                return null;
        }
    }

    /**
     * Returns the result code and the exception. This models the case in
     * Sybase ASA where T-SQL allows you to continue and throw a return code
     * even after an exception is raised.
     * @param index the result set id.
     * @return a MockResultSet at the specified index.
     */
    public final MockResultSet resultAndException(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case -1:
                return MockResultSetUtils.buildScalarOutParam("-2001");
            case 0:
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                return MockResultSetUtils.buildSQLExceptionResultSet(
                    ERROR_CODE, "Test Exception");
            default:
                return null;
        }
    }

    /**
     * Returns an error code and a multi-line exception message. This is
     * used to test cases such as Sybase ASA which may return a trailing
     * newline. 
     * @param index the result set id.
     * @return a MockResultSet at the specified index.
     */
    public final MockResultSet resultAndMultilineException(
            final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case -1:
                return MockResultSetUtils.buildScalarOutParam("-2001");
            case 0:
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                return MockResultSetUtils.buildSQLExceptionResultSet(
                    ERROR_CODE, "Test Exception\nMultiline Exception\n");
            default:
                return null;
        }
    }

    /**
     * Returns a update count of 1.
     * @param index the result set id.
     * @return a MockResultSet at the specified index.
     */
    public final MockResultSet updateCount1(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case 0: 
               return MockResultSetUtils.buildZerothResultSetWithUpdateCount(1);
            default:
               return null;
        }
    }

    /**
     * Returns a update count of 2.
     * @param index the result set id.
     * @return a MockResultSet at the specified index.
     */
    public final MockResultSet updateCount2(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case 0: 
               return MockResultSetUtils.buildZerothResultSetWithUpdateCount(2);
            default:
               return null;
        }
    }

    /**
     * Returns an update count of Statement.EXECUTE_FAILED.
     * @param index the result set id.
     * @return a MockResultSet with an embedded updatecount.
     */
    public final MockResultSet updateCountExecuteFailed(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case 0: 
                return MockResultSetUtils.buildZerothResultSetWithUpdateCount(
                    Statement.EXECUTE_FAILED);
            default:
                return null;
        }
    }

    /**
     * Returns a Java object in a single result set.
     * @param index the result set id.
     * @return a MockResultSet with an embedded Java object.
     */
    public final MockResultSet bytesInResultSet(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case 0:
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                MockResultSet mrs = new MockResultSet("bytesResultSet:1");
                mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("id", Types.INTEGER),
                    new ColumnMetaData("lobcol", Types.BINARY)}));
                Dictionary stooges = new Dictionary();
                stooges.setEntry("1", "Larry");
                stooges.setEntry("2", "Curly");
                stooges.setEntry("3", "Moe");
                stooges.writeTo("/tmp/Dictionary.ser");
                mrs.addRow(new Object[] {new Integer(1), stooges});
                LOG.debug("bytesResultSet:1:" + mrs.toString());
                return mrs;
            default:
                return null;
        }
    }

    /**
     * Returns an inputstream in a single result set.
     * @param index the result set id.
     * @return a MockResultSet with an embedded InputStream.
     */
    public final MockResultSet lobInResultSet(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case 0:
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                MockResultSet mrs = new MockResultSet("lobResultSet:1");
                mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("id", Types.INTEGER),
                    new ColumnMetaData("lobcol", Types.LONGVARBINARY)}));
                Dictionary stooges = new Dictionary();
                stooges.setEntry("1", "Larry");
                stooges.setEntry("2", "Curly");
                stooges.setEntry("3", "Moe");
                stooges.writeTo("/tmp/Dictionary.ser");
                try {
                    mrs.addRow(new Object[] {new Integer(1), 
                        new FileInputStream("/tmp/Dictionary.ser")});
                } catch (Exception e) {
                    // :NOTE: will never happen
                }
                LOG.debug("lobResultSet:1:" + mrs.toString());
                return mrs;
            default:
                return null;
        }
    }

    /**
     * Returns a canned XML string from an included file. The test for this
     * is to compare the XML returned from the method with that in the 
     * included file.
     * @param index the result set id.
     * @return a MockResultSet object.
     */
    public final MockResultSet xmlResultSet(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case 0:
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                StringBuffer buf = new StringBuffer();
                try {
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(
                        "test/mock/xmltest.out")));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buf.append(line).append("\n");
                    }
                    reader.close();
                } catch (Exception e) {
                    // :NOTE: will never happen
                }
                MockResultSet mrs = new MockResultSet("xmlResult:1");
                mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("xmlclob", Types.LONGVARCHAR)
                    }));
                mrs.addRow(new Object[] {buf.toString()});
                LOG.debug("xmlResult:1:" + mrs.toString());
                return mrs;
            default:
                return null;
        }
    }

    /**
     * Returns a CLOB datatype from the same canned XML file as the method
     * above, but specifies this as a CLOB data type.
     * @param index the result set id.
     * @return a MockResultSet object.
     */
    public final MockResultSet clobResult(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case 0:
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                StringBuffer buf = new StringBuffer();
                try {
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(
                        "test/mock/xmltest.out")));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buf.append(line).append("\n");
                    }
                    reader.close();
                } catch (Exception e) {
                    // :NOTE: will never happen
                }
                MockResultSet mrs = new MockResultSet("clobResult:1");
                mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("xmlclob", Types.CLOB)
                    }));
                MockClob clob = new MockClob(buf.toString());
                mrs.addRow(new Object[] {clob});
                LOG.debug("clobResult:1:" + mrs.toString());
                return mrs;
            default:
                return null;
        }
    }

    /**
     * Returns a BLOB datatype which is the contents of SQLUnit.class.
     * @param index the resultset id.
     * @return a MockResultSet.
     */
    public final MockResultSet blobResult(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case 0:
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                // build a Dictionary.ser file in /tmp
                Dictionary stooges = new Dictionary();
                stooges.setEntry("1", "Larry");
                stooges.setEntry("2", "Curly");
                stooges.setEntry("3", "Moe");
                stooges.writeTo("/tmp/Dictionary.ser");
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try {
                    InputStream istream = new FileInputStream(
                        "/tmp/Dictionary.ser");
                    byte[] buf = new byte[BYTES_IN_KB];
                    int len = 0;
                    while ((len = istream.read(buf, 0, BYTES_IN_KB)) != -1) {
                        bos.write(buf, 0, len);
                    }
                    istream.close();
                } catch (Exception e) {
                    // :NOTE: will never happen
                }
                MockResultSet mrs = new MockResultSet("blobResult:1");
                mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("bytecode", Types.BLOB)
                    }));
                MockBlob blob = new MockBlob(bos.toByteArray());
                mrs.addRow(new Object[] {blob});
                LOG.debug("blobResult:1:" + mrs.toString());
                return mrs;
            default:
                return null;
        }
    }

    /**
     * Returns a JAVA_OBJECT which is the same as that serialized in Dictionary.
     * @param index the result set id.
     * @return a ResultSet.
     */
    public final MockResultSet javaObjectResult(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case 0:
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                // build a Dictionary.ser file in /tmp
                Dictionary stooges = new Dictionary();
                stooges.setEntry("1", "Larry");
                stooges.setEntry("2", "Curly");
                stooges.setEntry("3", "Moe");
                stooges.writeTo("/tmp/Dictionary.ser");
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try {
                    InputStream istream = new FileInputStream(
                        "/tmp/Dictionary.ser");
                    byte[] buf = new byte[BYTES_IN_KB];
                    int len = 0;
                    while ((len = istream.read(buf, 0, BYTES_IN_KB)) != -1) {
                        bos.write(buf, 0, len);
                    }
                    istream.close();
                } catch (Exception e) {
                    // :NOTE: will never happen
                }
                MockResultSet mrs = new MockResultSet("blobResult:1");
                mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("myobj", Types.JAVA_OBJECT)
                    }));
                mrs.addRow(new Object[] {stooges});
                LOG.debug("javaObjectResult:1:" + mrs.toString());
                return mrs;
            default:
                return null;
        }
    }

    /**
     * Method to test the foreach tag. This method is meant to be used with 
     * the insert[12]Row() and returnCountOfRows() methods. Side effect of 
     * this method is to zero out the static variable foreachUpdateCount.
     * @param index the result set id.
     * @return an update count.
     */
    public final MockResultSet deleteRows(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case 0: 
               foreachUpdateCount = 0;
               inInsert1Row = false;
               inInsert2Row = false;
               return MockResultSetUtils.buildZerothResultSetWithUpdateCount(0);
            default:
               return null;
        }
    }

    /**
     * Method to test the foreach tag. This method is meant to be used
     * with the deleteRows(), insert2Row() and returnCountOfRowsInserted() 
     * methods.
     * @param index the result set id.
     * @return an updatecount.
     */
    public final MockResultSet insert1Row(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case 0: 
               if (!inInsert1Row) {
                   foreachUpdateCount = foreachUpdateCount + 1;
                   inInsert1Row = true;
               }
               return MockResultSetUtils.buildZerothResultSetWithUpdateCount(1);
            default:
               return null;
        }
    }

    /**
     * Method to test the foreach tag. This method is meant to be used
     * with the deleteRows(), insert1Row() and returnCountOfRowsInserted() 
     * methods.
     * @param index the result set id.
     * @return an updatecount.
     */
    public final MockResultSet insert2Row(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case 0: 
               if (!inInsert2Row) {
                   foreachUpdateCount = foreachUpdateCount + 2;
                   inInsert2Row = true;
               }
               return MockResultSetUtils.buildZerothResultSetWithUpdateCount(2);
            default:
               return null;
        }
    }

    /**
     * Method to test the foreach tag. This method is meant to be used
     * with the deleteRows(), insert1Row() and insert2Row() methods.
     * @param index the result set id.
     * @return a count of the number of rows inserted.
     */
    public final MockResultSet returnCountOfRowsInserted(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch(rsid) {
            case 0: 
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                MockResultSet mrs = new MockResultSet("returnCount:1");
                mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("col1", Types.INTEGER)}));
                // it comes into case 0 twice for the update, first to 
                // calculate the number of rows, and second to calculate
                // the updatecount, so we need to halve it here.
                mrs.addRow(new Object[] {new Integer(foreachUpdateCount)});
                foreachUpdateCount = 0;
                inInsert1Row = false;
                inInsert2Row = false;
                LOG.debug("returnCount:1:" + mrs.toString());
                return mrs;
            default:
                return null;
        }
    }

    /**
     * Returns a resultset which is sorted "naturally", ie by ascending
     * order of columns as they appear in the resultset.
     * @param index the result set id.
     * @return a resultset.
     */
    public final MockResultSet superHeroesSortedNaturally(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case 0:
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                MockResultSet mrs = new MockResultSet("superHeroes:natural");
                mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("id", Types.INTEGER),
                    new ColumnMetaData("lname", Types.VARCHAR),
                    new ColumnMetaData("fname", Types.VARCHAR),
                    new ColumnMetaData("alias", Types.VARCHAR)}));
                mrs.addRow(new Object[] {
                    new Integer(SPIDERMAN_ID), "Parker", "Peter", "Spiderman"});
                mrs.addRow(new Object[] {
                    new Integer(SUPERMAN_ID), "Kent", "Clark", "Superman"});
                mrs.addRow(new Object[] {
                    new Integer(BATMAN_ID), "Wayne", "Bruce", "Batman"});
                LOG.debug("superHeroes:natural:" + mrs.toString());
                return mrs;
            default:
                return null;
        }
    }

    /**
     * Same as superHeroesSortedNaturally() but rows are returned sorted
     * by last name (lname).
     * @param index the result set id.
     * @return a resultset.
     */
    public final MockResultSet superHeroesSortedByAliasAndName(
            final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case 0:
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                MockResultSet mrs = new MockResultSet("superHeroes:byAliasNme");
                mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("id", Types.INTEGER),
                    new ColumnMetaData("lname", Types.VARCHAR),
                    new ColumnMetaData("fname", Types.VARCHAR),
                    new ColumnMetaData("alias", Types.VARCHAR)}));
                mrs.addRow(new Object[] {
                    new Integer(BATMAN_ID), "Wayne", "Bruce", "Batman"});
                mrs.addRow(new Object[] {
                    new Integer(SPIDERMAN_ID), "Parker", "Peter", "Spiderman"});
                mrs.addRow(new Object[] {
                    new Integer(SUPERMAN_ID), "Kent", "Clark", "Superman"});
                LOG.debug("superHeroes:byAliasAndName:" + mrs.toString());
                return mrs;
            default:
                return null;
        }
    }

    /**
     * Same as superHeroesSortedNaturally() but rows are returned sorted
     * by alias.
     * @param index the result set id.
     * @return a result set.
     */
    public final MockResultSet superHeroesSortedByAlias(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case 0:
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                MockResultSet mrs = new MockResultSet("superHeroes:byAlias");
                mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("id", Types.INTEGER),
                    new ColumnMetaData("lname", Types.VARCHAR),
                    new ColumnMetaData("fname", Types.VARCHAR),
                    new ColumnMetaData("alias", Types.VARCHAR)}));
                mrs.addRow(new Object[] {
                    new Integer(BATMAN_ID), "Wayne", "Bruce", "Batman"});
                mrs.addRow(new Object[] {
                    new Integer(SPIDERMAN_ID), "Parker", "Peter", "Spiderman"});
                mrs.addRow(new Object[] {
                    new Integer(SUPERMAN_ID), "Kent", "Clark", "Superman"});
                LOG.debug("superHeroes:byAlias:" + mrs.toString());
                return mrs;
            default:
                return null;
        }
    }

    /**
     * Same as superHeroesSortedNaturally() but rows are returned sorted
     * descending by alias.
     * @param index the result set id.
     * @return a result set.
     */
    public final MockResultSet superHeroesSortedByAliasDesc(
            final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case 0:
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                MockResultSet mrs = new MockResultSet("superHeroes:aliasDesc");
                mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("id", Types.INTEGER),
                    new ColumnMetaData("lname", Types.VARCHAR),
                    new ColumnMetaData("fname", Types.VARCHAR),
                    new ColumnMetaData("alias", Types.VARCHAR)}));
                mrs.addRow(new Object[] {
                    new Integer(SUPERMAN_ID), "Kent", "Clark", "Superman"});
                mrs.addRow(new Object[] {
                    new Integer(SPIDERMAN_ID), "Parker", "Peter", "Spiderman"});
                mrs.addRow(new Object[] {
                    new Integer(BATMAN_ID), "Wayne", "Bruce", "Batman"});
                LOG.debug("superHeroes:byAliasDesc:" + mrs.toString());
                return mrs;
            default:
                return null;
        }
    }

    /**
     * Returns a single row with 2 columns, one NUMERIC and one DECIMAL.
     * @param index the result set id.
     * @return a result set.
     */
    public final MockResultSet returnNumericAndDecimalInResultSet(
            final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch(rsid) {
            case 0:
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                MockResultSet mrs = new MockResultSet("numericAndDecimal:1");
                mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("numeric_f", Types.NUMERIC),
                    new ColumnMetaData("decimal_f", Types.DECIMAL)}));
                mrs.addRow(new Object[] {
                    new BigDecimal(NUMERIC_VALUE),
                    new BigDecimal(DECIMAL_VALUE)});
                LOG.debug(mrs.toString());
                return mrs;
            default:
                return null;
        }
    }

    /**
     * Returns a NUMERIC in an OUT Param.
     * @param index the result set id.
     * @return the result set.
     */
    public final MockResultSet returnNumericOutParam(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch (rsid) {
            case -1:
                return MockResultSetUtils.buildScalarOutParam("5.26");
            case 0:
                return MockResultSetUtils.buildZerothResultSet(0);
            default:
                return null;
        }
    }

    /**
     * Returns a canned NUMERIC value in an INOUT Param. The INOUT param 
     * will always contain 300.01 when it is returned from this method.
     * @param index the result set id.
     * @return the result set.
     */
    public final MockResultSet incrementNumericAndReturn(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch(rsid) {
            case -1:
                return MockResultSetUtils.buildScalarOutParam("300.01");
            case 0:
                return MockResultSetUtils.buildZerothResultSet(0);
            default:
                return null;
        }
    }

    /**
     * Same as returnNumericOutParam since underlying datatype is BigDecimal
     * in both cases, and the scalar is built based on actual datatype.
     * @param index the result set id.
     * @return the result set.
     */
    public final MockResultSet returnDecimalOutParam(final Integer index) {
        return returnNumericOutParam(index);
    }

    /**
     * Same as incrementNumericAndReturn1 since the underlying datatype is
     * BigDecimal in both cases, and the scalar is built based on actual
     * datatype.
     * @param index the result set id.
     * @return the result set.
     */
    public final MockResultSet incrementDecimalAndReturn(final Integer index) {
        return incrementNumericAndReturn(index);
    }

    /**
     * Takes no parameters and returns the name of the database. Used in
     * testing the functionality of the func tag.
     * @param index the result set id.
     * @return the result set.
     */
    public final MockResultSet selectDatabase(final Integer index) {
        if (index == null) { return null; }
        int rsid = index.intValue();
        switch(rsid) {
            case 0:
                return MockResultSetUtils.buildZerothResultSet(1);
            case 1:
                MockResultSet mrs = new MockResultSet("selectDatabase");
                mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
                    new ColumnMetaData[] {
                    new ColumnMetaData("database", Types.VARCHAR)}));
                mrs.addRow(new Object[] {new String(DATABASE_NAME)});
                LOG.debug(mrs.toString());
                return mrs;
            default:
                return null;
        }
    }
}
