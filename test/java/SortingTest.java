/*
 * $Id: SortingTest.java,v 1.4 2004/10/05 20:13:04 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/SortingTest.java,v $
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

import net.sourceforge.sqlunit.TypeMapper;
import net.sourceforge.sqlunit.beans.ResultSetBean;
import net.sourceforge.sqlunit.beans.Row;

import net.sourceforge.sqlunit.test.mock.ColumnMetaData;
import net.sourceforge.sqlunit.test.mock.MockResultSetUtils;

import com.mockrunner.mock.jdbc.MockResultSet;

import org.apache.log4j.Logger;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.ResultSet;
import java.sql.Types;

/**
 * Some basic sorting tests to verify that the code works before I start
 * on mock database based unit testing.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.4 $
 */
public class SortingTest extends TestCase {

    private static final Logger LOG = Logger.getLogger(SortingTest.class);

    private static final String AUTHOR_ID_INDEX = "1";
    private static final String AUTHOR_NAME_INDEX = "2";
    private static final String CHARACTER_NAME_INDEX = "3";
    private static final String FAVORITE_VICE_INDEX = "4";

    private static final int NUM_ROWS = 4;

    private static final String FLEMING_ID_VALUE = "7";
    private static final String DOYLE_ID_VALUE = "9";
    private static final String WODEHOUSE_ID_VALUE = "6";

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
        return new TestSuite(SortingTest.class);
    }

    /**
     * Boilerplate constructor.
     * @param name the name of this test class.
     */
    public SortingTest(final String name) {
        super(name);
    }

    /**
     * Setting up the mapper to contain type info before each test.
     * @exception Exception if there was a problem.
     */
    public final void setUp() throws Exception {
        TypeMapper.getTypeMapper();
    }

    /**
     * Sorting with single column.
     * @exception Exception if there was a problem.
     */
    public final void testSortByVice() throws Exception {
        ResultSetBean rsb = new ResultSetBean(buildResultSetToSort(), 1);
        rsb.setSortBy(FAVORITE_VICE_INDEX);
        rsb.sort();
        Row[] rows = rsb.getRows();
        assertEquals(NUM_ROWS, rows.length);
        int rowIndex = 0;
        assertEquals("Sherlock Holmes", rows[rowIndex].getColById(
            CHARACTER_NAME_INDEX).getValue());
        rowIndex++;
        assertEquals("Theodore Moriarty", rows[rowIndex].getColById(
            CHARACTER_NAME_INDEX).getValue());
        rowIndex++;
        assertEquals("James Bond", rows[rowIndex].getColById(
            CHARACTER_NAME_INDEX).getValue());
        rowIndex++;
        assertEquals("Bertie Wooster", rows[rowIndex].getColById(
            CHARACTER_NAME_INDEX).getValue());
    }

    /**
     * Sorting with two columns.
     * @exception Exception if there was a problem.
     */
    public final void testSortByIdAndAuthorName() throws Exception {
        ResultSetBean rsb = new ResultSetBean(buildResultSetToSort(), 1);
        rsb.setSortBy(AUTHOR_ID_INDEX + "," + AUTHOR_NAME_INDEX);
        rsb.sort();
        Row[] rows = rsb.getRows();
        assertEquals(NUM_ROWS, rows.length);
        int rowIndex = 0;
        assertEquals("P G Wodehouse", rows[rowIndex].getColById(
            AUTHOR_NAME_INDEX).getValue());
        rowIndex++;
        assertEquals("Ian Fleming", rows[rowIndex].getColById(
            AUTHOR_NAME_INDEX).getValue());
        rowIndex++;
        assertEquals("Arthur C Doyle", rows[rowIndex].getColById(
            AUTHOR_NAME_INDEX).getValue());
        rowIndex++;
        assertEquals("Arthur C Doyle", rows[rowIndex].getColById(
            AUTHOR_NAME_INDEX).getValue());
    }

    /**
     * Sorting with three columns.
     * @exception Exception if there was a problem.
     */
    public final void testSortByViceAuthorCharacter() throws Exception {
        ResultSetBean rsb = new ResultSetBean(buildResultSetToSort(), 1);
        rsb.setSortBy(FAVORITE_VICE_INDEX + "," + AUTHOR_NAME_INDEX + ","
            + CHARACTER_NAME_INDEX);
        rsb.sort();
        Row[] rows = rsb.getRows();
        assertEquals(NUM_ROWS, rows.length);
        int rowIndex = 0;
        assertEquals(DOYLE_ID_VALUE, rows[rowIndex].getColById(
            AUTHOR_ID_INDEX).getValue());
        rowIndex++;
        assertEquals(DOYLE_ID_VALUE, rows[rowIndex].getColById(
            AUTHOR_ID_INDEX).getValue());
        rowIndex++;
        assertEquals(FLEMING_ID_VALUE, rows[rowIndex].getColById(
            AUTHOR_ID_INDEX).getValue());
        rowIndex++;
        assertEquals(WODEHOUSE_ID_VALUE, rows[rowIndex].getColById(
            AUTHOR_ID_INDEX).getValue());
    }

    /**
     * Testing the descending sort.
     * @exception Exception if there was a problem.
     */
    public final void testSortByAuthorCharacterDesc() throws Exception {
        ResultSetBean rsb = new ResultSetBean(buildResultSetToSort(), 1);
        rsb.setSortBy("2,-3");
        rsb.sort();
        Row[] rows = rsb.getRows();
        assertEquals(NUM_ROWS, rows.length);
        int rowIndex = 0;
        assertEquals("Theodore Moriarty", rows[rowIndex].getColById(
            CHARACTER_NAME_INDEX).getValue());
        rowIndex++;
        assertEquals("Sherlock Holmes", rows[rowIndex].getColById(
            CHARACTER_NAME_INDEX).getValue());
        rowIndex++;
        assertEquals("James Bond", rows[rowIndex].getColById(
            CHARACTER_NAME_INDEX).getValue());
        rowIndex++;
        assertEquals("Bertie Wooster", rows[rowIndex].getColById(
            CHARACTER_NAME_INDEX).getValue());
    }

    /**
     * Method to build a result set for sorting. The testing resultset is
     * shown below:
     * <pre>
     *  author_id  author_name    character_name    favorite_vice
     *  ---------  -------------- ----------------- ------------
     *  7          Ian Fleming    James Bond        Martini
     *  9          Arthur C Doyle Sherlock Holmes   Cocaine
     *  9          Arthur C Doyle Theodore Moriarty Crime
     *  6          P G Wodehouse  Bertie Wooster    None
     * </pre>
     * @return a ResultSet.
     */
    private static ResultSet buildResultSetToSort() {
        MockResultSet mr = new MockResultSet("SortingTestResultSet");
        mr.setResultSetMetaData(MockResultSetUtils.buildMetaData(
            new ColumnMetaData[] {
            new ColumnMetaData("author_id", Types.INTEGER),
            new ColumnMetaData("author_name", Types.VARCHAR),
            new ColumnMetaData("character_name", Types.VARCHAR),
            new ColumnMetaData("favorite_vice", Types.VARCHAR)}));
        mr.addRow(new Object[] {
            new Integer(FLEMING_ID_VALUE), "Ian Fleming", "James Bond", 
            "Martini"});
        mr.addRow(new Object[] {
            new Integer(DOYLE_ID_VALUE), "Arthur C Doyle", "Sherlock Holmes", 
            "Cocaine"});
        mr.addRow(new Object[] {
            new Integer(DOYLE_ID_VALUE), "Arthur C Doyle", "Theodore Moriarty",
            "Crime"});
        mr.addRow(new Object[] {
            new Integer(WODEHOUSE_ID_VALUE), "P G Wodehouse", "Bertie Wooster",
            "None"});
        return mr;
    }
}
