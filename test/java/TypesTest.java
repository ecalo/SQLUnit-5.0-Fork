/*
 * $Id: TypesTest.java,v 1.9 2006/04/30 22:25:56 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/TypesTest.java,v $
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
package net.sourceforge.sqlunit.test;

import net.sourceforge.sqlunit.IType;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.SymbolTable;
import net.sourceforge.sqlunit.TypeFactory;
import net.sourceforge.sqlunit.TypeMapper;
import net.sourceforge.sqlunit.test.mock.ColumnMetaData;
import net.sourceforge.sqlunit.test.mock.MockResultSetUtils;
import net.sourceforge.sqlunit.types.BigDecimalType;
import net.sourceforge.sqlunit.types.ClobType;

import com.mockrunner.mock.jdbc.MockArray;
import com.mockrunner.mock.jdbc.MockBlob;
import com.mockrunner.mock.jdbc.MockClob;
import com.mockrunner.mock.jdbc.MockResultSet;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Tests for the new flexible data typing system.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.9 $
 */
public class TypesTest extends TestCase {

    private static final String BD1 = "1234.56";
    private static final String BD2 = "2345.67";
    private static final String INT1 = "1234";
    private static final String INT2 = "2345";
    private static final int KB = 1024;
    private static final int MILLIS_PER_DAY = 86400000;
    private static final int TENTH_SEC_OFFSET = 100;
    private static final int CURSOR_ID = 7;
    private static final String DICT_FILE_NAME = "build/tmpDictionary.ser";
    private static final String PASS_FILE_NAME = "etc/blob.test";

    /**
     * Boilerplate main method.
     * @param argv the arguments (none).
     */
    public static void main(final String[] argv) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Boilerplate suite() method.
     * @return a Test object.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(TypesTest.class);
        return (TestSetup) new TestSetup(suite) {
            protected void setUp() throws Exception {
                TypeMapper mapper = TypeMapper.getTypeMapper();
            }
            protected void tearDown() throws Exception {
                // nothing to teardown
            }
        };
    }

    /**
     * Boilerplate constructor.
     * @param name the name of this test class.
     */
    public TypesTest(final String name) {
        super(name);
    }

    // tests begin here

    /**
     * Testing basic type lookup.
     * @exception Exception if there was a problem.
     */
    public final void testGetInstanceOfNumeric() throws Exception {
        IType type = TypeFactory.getInstance("NUMERIC");
        assertTrue(type instanceof BigDecimalType);
        assertEquals(java.sql.Types.NUMERIC, type.getId());
        assertEquals("NUMERIC", type.getName());
    }

    /**
     * Testing type override lookup.
     * @exception Exception if there was a problem.
     */
    public final void testGetInstanceOfMySqlLongVarchar() throws Exception {
        IType type = TypeFactory.getInstance("mysql.LONGVARCHAR");
        assertTrue(type instanceof ClobType);
        assertEquals(-1, type.getId());
        assertEquals("mysql.LONGVARCHAR", type.getName());
    }

    /**
     * Bulk testing all type lookups.
     * @exception Exception if there was a problem.
     */
    public final void testCheckAllSupportedTypeNames() throws Exception {
        List allTypes = TypeMapper.findAllTypeNames(false);
        int szAllTypes = allTypes.size();
        for (int i = 0; i < szAllTypes; i++) {
            String typeName = (String) allTypes.get(i);
            IType type = TypeFactory.getInstance(typeName);
            assertEquals(TypeMapper.findIdByName(typeName), type.getId());
            assertEquals(typeName, type.getName());
        }
    }

    /**
     * Bulk testing all type lookups by id.
     * @exception Exception if there was a problem.
     */
    public final void testGetInstanceOfAllSupportedTypeNamesById()
            throws Exception {
        List allTypes = TypeMapper.findAllTypeNames(false);
        int szAllTypes = allTypes.size();
        for (int i = 0; i < szAllTypes; i++) {
            String typeName = (String) allTypes.get(i);
            // do not test for dotted type names (vendor specified) since
            // the findById will check for the vendor and if not supplied
            // will only check the basic types
            if (typeName.indexOf(".") > -1) { continue; }
            int id = TypeMapper.findIdByName(typeName);
            IType type = TypeFactory.getInstance(id);
            assertEquals(id, type.getId());
            assertEquals(typeName, type.getName());
        }
    }

    /**
     * Testing Array operations.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsArrayType() throws Exception {
        String[] elements = {"Larry", "Curly", "Moe"};
        MockArray array = new MockArray(elements);
        IType type = TypeFactory.getInstance(Types.ARRAY);
        String sArray = type.toString(array);
        // :NOTE: linked the type NULL (0) to OtherType as a workaround
        // for a MockRunner bug which always returns a 0 for the basetype.
        assertEquals("[Larry,Curly,Moe]", sArray);
    }

    /**
     * Testing BigDecimal operations.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsBigDecimalType() throws Exception {
        BigDecimal[] bds = new BigDecimal[] {
            new BigDecimal(BD1), new BigDecimal(BD2)
        };
        BigDecimal[] bdss = new BigDecimal[bds.length];
        for (int i = 0; i < bds.length; i++) {
            bdss[i] = bds[i].setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        IType t1 = TypeFactory.getInstance(Types.DECIMAL);
        String s1 = t1.toString(bdss[0]);
        assertEquals(BD1, s1);
        Object o1 = t1.toObject(s1);
        assertTrue(o1 instanceof BigDecimal);
        assertEquals(bdss[0], o1);
        IType t2 = TypeFactory.getInstance(Types.DECIMAL);
        String s2 = t2.toString(bdss[1]);
        assertEquals(BD2, s2);
        Object o2 = t2.toObject(s2);
        assertTrue(o2 instanceof BigDecimal);
        assertEquals(bdss[1], o2);
        assertTrue(t1.compareTo(t2) < 0);
    }

    /**
     * Testing Blob operations.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsBlobType() throws Exception {
        String expected = "md5:4e93398c9f620694de6e7a9739dbdb08";
        createDictFile();
        InputStream istream = new FileInputStream(DICT_FILE_NAME);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[KB];
        int len = 0;
        while ((len = istream.read(buf, 0, KB)) != -1) {
            bos.write(buf, 0, len);
        }
        istream.close();
        MockBlob blob = new MockBlob(bos.toByteArray());
        IType type = TypeFactory.getInstance(Types.BLOB);
        String got = type.toString(blob);
        assertEquals(expected, got);
    }

    private Dictionary createDictFile() {
        // we rebuild the Dictionary.ser file just in case
        Dictionary stooges = new Dictionary();
        stooges.setEntry("1", "Larry");
        stooges.setEntry("2", "Curly");
        stooges.setEntry("3", "Moe");
        stooges.writeTo(DICT_FILE_NAME);
        return stooges;
    }

    /**
     * Testing Boolean operations.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsBooleanType() throws Exception {
        Boolean trueValue = Boolean.TRUE;
        Boolean falseValue = Boolean.FALSE;
        IType t1 = TypeFactory.getInstance(Types.BOOLEAN);
        String s1 = t1.toString(trueValue);
        assertEquals("true", s1);
        Object o1 = t1.toObject(s1);
        assertTrue(o1 instanceof Boolean);
        assertEquals(trueValue, (Boolean) o1);
        IType t2 = TypeFactory.getInstance(Types.BOOLEAN);
        String s2 = t2.toString(falseValue);
        assertEquals("false", s2);
        Object o2 = t2.toObject(s2);
        assertTrue(o2 instanceof Boolean);
        assertEquals(falseValue, (Boolean) o2);
        assertTrue(t1.compareTo(t2) > 0);
    }

    /**
     * Testing ByteType operations.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsByteType() throws Exception {
        Byte b1 = new Byte(Byte.MIN_VALUE);
        Byte b2 = new Byte(Byte.MAX_VALUE);
        IType t1 = TypeFactory.getInstance(Types.TINYINT);
        String s1 = t1.toString(b1);
        assertEquals(b1.toString(), s1);
        Object o1 = t1.toObject(s1);
        assertTrue(o1 instanceof Byte);
        assertEquals(b1, (Byte) o1);
        IType t2 = TypeFactory.getInstance(Types.TINYINT);
        String s2 = t2.toString(b2);
        assertEquals(b2.toString(), s2);
        Object o2 = t2.toObject(s2);
        assertTrue(o2 instanceof Byte);
        assertEquals(b2, (Byte) o2);
        assertTrue(t1.compareTo(t2) < 0);
    }

    /**
     * Testing Clob operations.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsClobType() throws Exception {
        String expected = "md5:183893d16bbb38c61b4af25742092eeb";
        BufferedReader reader = new BufferedReader(new InputStreamReader(
            new FileInputStream(PASS_FILE_NAME)));
        StringBuffer buf = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            buf.append(line).append("\n");
        }
        reader.close();
        MockClob clob = new MockClob(buf.toString());
        IType type = TypeFactory.getInstance(Types.CLOB);
        String got = type.toString(clob);
        assertEquals(expected, got);
    }

    /**
     * Testing Date operations.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsDateType() throws Exception {
        java.sql.Date d1 = new java.sql.Date(System.currentTimeMillis());
        java.sql.Date d2 = new java.sql.Date(d1.getTime() + MILLIS_PER_DAY);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        IType t1 = TypeFactory.getInstance(Types.DATE);
        String s1 = t1.toString(d1);
        assertEquals(sdf.format(new java.util.Date(d1.getTime())), s1);
        Object o1 = t1.toObject(s1);
        assertTrue(o1 instanceof java.sql.Date);
        IType t2 = TypeFactory.getInstance(Types.DATE);
        String s2 = t2.toString(d2);
        assertEquals(sdf.format(new java.util.Date(d2.getTime())), s2);
        Object o2 = t2.toObject(s2);
        assertTrue(o2 instanceof java.sql.Date);
        assertTrue("comparison failed", t1.compareTo(t2) < 0);
        // negative case test - same day unless you are working really late
        java.sql.Date d3 = new java.sql.Date(d1.getTime() + TENTH_SEC_OFFSET); 
        IType t3 = TypeFactory.getInstance(Types.DATE);
        String s3 = t3.toString(d3);
        assertTrue("should be equal but was not", t1.compareTo(t3) == 0);
    }

    /**
     * Testing Double operations.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsDoubleType() throws Exception {
        Double d1 = new Double(BD1);
        Double d2 = new Double(BD2);
        IType t1 = TypeFactory.getInstance(Types.DOUBLE);
        String s1 = t1.toString(d1);
        assertEquals(BD1, s1);
        Object o1 = t1.toObject(s1);
        assertTrue(o1 instanceof Double);
        assertEquals(d1, (Double) o1);
        IType t2 = TypeFactory.getInstance(Types.DOUBLE);
        String s2 = t2.toString(d2);
        assertEquals(BD2, s2);
        Object o2 = t2.toObject(s2);
        assertTrue(o2 instanceof Double);
        assertEquals(d2, (Double) o2);
        assertTrue(t1.compareTo(t2) < 0);
    }

    /**
     * Testing Float operations.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsFloatType() throws Exception {
        Double f1 = new Double(BD1);
        Double f2 = new Double(BD2);
        IType t1 = TypeFactory.getInstance(Types.FLOAT);
        String s1 = t1.toString(f1);
        assertEquals(BD1, s1);
        Object o1 = t1.toObject(s1);
        assertTrue(o1 instanceof Double);
        assertEquals(f1, (Double) o1);
        IType t2 = TypeFactory.getInstance(Types.FLOAT);
        String s2 = t2.toString(f2);
        assertEquals(BD2, s2);
        Object o2 = t2.toObject(s2);
        assertTrue(o2 instanceof Double);
        assertEquals(f2, (Double) o2);
        assertTrue(t1.compareTo(t2) < 0);
    }

    /**
     * Testing Integer operations.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsIntegerType() throws Exception {
        Integer i1 = new Integer(INT1);
        Integer i2 = new Integer(INT2);
        IType t1 = TypeFactory.getInstance(Types.INTEGER);
        String s1 = t1.toString(i1);
        assertEquals(INT1, s1);
        Object o1 = t1.toObject(s1);
        assertTrue(o1 instanceof Integer);
        assertEquals(i1, (Integer) o1);
        IType t2 = TypeFactory.getInstance(Types.INTEGER);
        String s2 = t2.toString(i2);
        assertEquals(INT2, s2);
        Object o2 = t2.toObject(s2);
        assertTrue(o2 instanceof Integer);
        assertEquals(i2, (Integer) o2);
        assertTrue(t1.compareTo(t2) < 0);
    }

    /**
     * Testing JavaObjectType operations.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsJavaObjectType() throws Exception {
        String expected = "obj:Dictionary:{1=>'Larry',2=>'Curly',3=>'Moe'}";
        Dictionary stooges = createDictFile();
        InputStream istream = new FileInputStream(DICT_FILE_NAME);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[KB];
        int len = 0;
        while ((len = istream.read(buf, 0, KB)) != -1) {
            bos.write(buf, 0, len);
        }
        istream.close();
        IType type1 = TypeFactory.getInstance(Types.JAVA_OBJECT);
        assertEquals(expected, type1.toString(stooges));
    }

    /**
     * Testing LongType operations.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsLongType() throws Exception {
        Long l1 = new Long(INT1);
        Long l2 = new Long(INT2);
        IType t1 = TypeFactory.getInstance(Types.BIGINT);
        String s1 = t1.toString(l1);
        assertEquals(INT1, s1);
        Object o1 = t1.toObject(s1);
        assertTrue(o1 instanceof Long);
        assertEquals(l1, (Long) o1);
        IType t2 = TypeFactory.getInstance(Types.BIGINT);
        String s2 = t2.toString(l2);
        assertEquals(INT2, s2);
        Object o2 = t2.toObject(s2);
        assertTrue(o2 instanceof Long);
        assertEquals(l2, (Long) o2);
        assertTrue(t1.compareTo(t2) < 0);
    }

    /**
     * Testing OracleCursorType operations.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsOracleCursorType() throws Exception {
        String LS = System.getProperty("line.separator");
        String expected = 
          "<resultset id=\"1\">" + LS
        + "  <row id=\"1\">" + LS
        + "    <col id=\"1\" name=\"agentId\" type=\"INTEGER\">7</col>" + LS
        + "    <col id=\"2\" name=\"name\" type=\"VARCHAR\">James Bond</col>" + LS
        + "  </row>" + LS
        + "</resultset>";
        MockResultSet mrs = new MockResultSet("OracleCursor");
        mrs.setResultSetMetaData(MockResultSetUtils.buildMetaData(
            new ColumnMetaData[] {
                new ColumnMetaData("agentId", Types.INTEGER),
                new ColumnMetaData("name", Types.VARCHAR)}));
        mrs.addRow(new Object[] {new Integer(CURSOR_ID), 
            new String("James Bond")});
        IType type = TypeFactory.getInstance("oracle.CURSOR");
        assertEquals(expected, type.toString(mrs));
    }

    /**
     * Testing OtherType operations.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsOtherType() throws Exception {
        String expected = "Dictionary:{1=>'Larry',2=>'Curly',3=>'Moe'}";
        Dictionary stooges = createDictFile();
        InputStream istream = new FileInputStream(DICT_FILE_NAME);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[KB];
        int len = 0;
        while ((len = istream.read(buf, 0, KB)) != -1) {
            bos.write(buf, 0, len);
        }
        istream.close();
        IType type = TypeFactory.getInstance(Types.OTHER);
        assertEquals(expected, type.toString(stooges));
    }

    /**
     * Testing ShortType operations.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsShortType() throws Exception {
        Short h1 = new Short(INT1);
        Short h2 = new Short(INT2);
        IType t1 = TypeFactory.getInstance(Types.SMALLINT);
        String s1 = t1.toString(h1);
        assertEquals(INT1, s1);
        Object o1 = t1.toObject(s1);
        assertTrue(o1 instanceof Short);
        assertEquals(h1, (Short) o1);
        IType t2 = TypeFactory.getInstance(Types.SMALLINT);
        String s2 = t2.toString(h2);
        assertEquals(INT2, s2);
        Object o2 = t2.toObject(s2);
        assertTrue(o2 instanceof Short);
        assertEquals(h2, (Short) o2);
        assertTrue(t1.compareTo(t2) < 0);
    }

    /**
     * Testing StringType operations.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsStringType() throws Exception {
        String g1 = new String("Mario");
        String g2 = new String("David");
        IType t1 = TypeFactory.getInstance(Types.VARCHAR);
        String ts1 = t1.toString(g1);
        assertEquals(g1, ts1);
        Object o1 = t1.toObject(ts1);
        assertTrue(o1 instanceof String);
        assertEquals(g1, (String) o1);
        IType t2 = TypeFactory.getInstance(Types.VARCHAR);
        t2.toString(g2);
        assertTrue(g1.compareTo(g2) > 0);
    }

    /**
     * Testing TimestampType operations.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsTimestampType() throws Exception {
        Timestamp m1 = new Timestamp(System.currentTimeMillis());
        Timestamp m2 = new Timestamp(m1.getTime() + MILLIS_PER_DAY);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        IType t1 = TypeFactory.getInstance(Types.TIMESTAMP);
        String s1 = t1.toString(m1);
        assertEquals(sdf.format(new java.util.Date(m1.getTime())), s1);
        Object o1 = t1.toObject(s1);
        assertTrue(o1 instanceof Timestamp);
        IType t2 = TypeFactory.getInstance(Types.TIMESTAMP);
        String s2 = t2.toString(m2);
        assertEquals(sdf.format(new java.util.Date(m2.getTime())), s2);
        Object o2 = t2.toObject(s2);
        assertTrue(o2 instanceof Timestamp);
        assertTrue("comparison failed", t1.compareTo(t2) < 0);
    }

    /**
     * Testing TimeType operations.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsTimeType() throws Exception {
        Time m1 = new Time(System.currentTimeMillis());
        Time m2 = new Time(m1.getTime() + MILLIS_PER_DAY);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        IType t1 = TypeFactory.getInstance(Types.TIME);
        String s1 = t1.toString(m1);
        assertEquals(sdf.format(new java.util.Date(m1.getTime())), s1);
        Object o1 = t1.toObject(s1);
        assertTrue(o1 instanceof Time);
        IType t2 = TypeFactory.getInstance(Types.TIME);
        String s2 = t2.toString(m2);
        assertEquals(sdf.format(new java.util.Date(m2.getTime())), s2);
        Object o2 = t2.toObject(s2);
        assertTrue(o2 instanceof Time);
        assertTrue("comparison failed", t1.compareTo(t2) == 0);
        assertTrue("equality check failed", t1.equals(t2));
    }

    /**
     * Testing that UnsupportedType does throw exceptions.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsUnsupportedType() throws Exception {
        URL url = new URL("http://sqlunit.sourceforge.net");
        IType type = TypeFactory.getInstance(Types.DATALINK);
        try {
            String s = type.toString(url);
            fail("Should have thrown an exception for toString()");
        } catch (SQLUnitException e) {
            // :IGNORE: positive test is to throw exception
        }
        try {
            Object o = type.toObject("http://sqlunit.sourceforge.net");
            fail("Should have thrown an exception for toObject()");
        } catch (SQLUnitException e) {
            // :IGNORE: positive test is to throw exception
        }
    }

    /**
     * Testing ByteArrayType operations.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsByteArrayType() throws Exception {
        // no datatype for this yet
    }

    /**
     * Testing BinaryType operations.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsBinaryType() throws Exception {
        String expected = "md5:4e93398c9f620694de6e7a9739dbdb08";
        String expectedWithJavaSupport = 
            "obj:Dictionary:{1=>'Larry',2=>'Curly',3=>'Moe'}";
        createDictFile();
        InputStream istream = new FileInputStream(DICT_FILE_NAME);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[KB];
        int len = 0;
        while ((len = istream.read(buf, 0, KB)) != -1) {
            bos.write(buf, 0, len);
        }
        istream.close();
        // without Java object support
        IType type1 = TypeFactory.getInstance(Types.LONGVARBINARY);
        assertEquals(expected, type1.toString(new FileInputStream(DICT_FILE_NAME)));
        // with Java object support
        IType type2 = TypeFactory.getInstance(Types.LONGVARBINARY);
        SymbolTable.setValue("${__JavaObjectSupport__}", "on");
        assertEquals(expectedWithJavaSupport, 
            type2.toString(new FileInputStream(DICT_FILE_NAME)));
    }

    /**
     * Testing Text type operations.
     * @exception Exception if there was a problem.
     */
    public final void testBeanOpsTextType() throws Exception {
        String expected = "md5:183893d16bbb38c61b4af25742092eeb";
        BufferedReader reader = new BufferedReader(new InputStreamReader(
            new FileInputStream(PASS_FILE_NAME)));
        StringBuffer buf = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            buf.append(line).append("\n");
        }
        reader.close();
        IType type = TypeFactory.getInstance(Types.LONGVARCHAR);
        String got = type.toString(buf.toString());
        assertEquals(expected, got);
    }
}
