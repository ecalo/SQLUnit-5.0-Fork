/*
 * $Id: IncludeFileParserTest.java,v 1.10 2005/12/17 18:36:18 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/IncludeFileParserTest.java,v $
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

import net.sourceforge.sqlunit.parsers.IncludeFileParser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.List;

/**
 * Runs some tests of the Include File Parser.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.10 $
 */
public class IncludeFileParserTest extends TestCase {

    // a list of statements which will be used to build a file dynamically
    private static final String[] STATEMENT_LIST = {
        "-- Single line comment",                      // index = 0
        "SELECT * FROM foo WHERE bar = 1",             // index = 1
        "create or replace function PassThru(varchar) returns varchar as '\n"
            + "declare \n"
            + "i_param alias for $1;\n"
            + "begin\n"
            + "return i_param;\n"
            + "end;\n"
            + "' language 'plpgsql'",                  // index = 2
        "insert into foo values(1,'bar',2)",           // index = 3
        "-- Another single line comment (1/2)",        // index = 4
        "-- Another single line comment (2/2)",        // index = 5
        "EXECUTE PROCEDURE PassThru('/etc/passwd')",   // index = 6
        "!/bin/sh -c (rm /tmp/*.test)",                // index = 7
        "select * from foo where bar = 2",             // index = 8
        "CREATE PROCEDURE myProc(myVar VARCHAR) RETURNS VARCHAR\n"
            + "DECLARE \n"
            + "i_param INTEGER,\n"
            + "i_param2 VARCHAR;\n"
            + "BEGIN\n"
            + "RETURN i_param;\n"
            + "END;",                                  // index = 9
        "-- Yet another comment line",                 // index = 10
        "update foo\n"                                 // index = 11
            + "set bar=1\n"
            + "where bar=2",
        "SELECT * FROM foo WHERE bar = 3",             // index = 12
        "/*==============================================================*/\n"
            + "/* DBMS name:      ORACLE Version 9i2\n"
            + "   Created on:     8/26/2004 6:50:16 PM\n"
            + " */\n"
            + "create or replace procedure \n"
            + "Web_schm_proc as\n"
            + "BEGIN insert into dummy values (12); \n"
            + "END;",                                  // index = 13
        "DELETE FROM table WHERE column LIKE 'string%';" // index = 14
    };
    private static final String SC_TEST_FILE = "test/java/parsertest.sql";
    private static final String SC_TEST_FILE_SYBASE =
        "test/java/parsertest_syb.sql";

    private static final int INDEX_0 = 0;
    private static final int INDEX_1 = 1;
    private static final int INDEX_2 = 2;
    private static final int INDEX_3 = 3;
    private static final int INDEX_4 = 4;
    private static final int INDEX_5 = 5;
    private static final int INDEX_6 = 6;
    private static final int INDEX_7 = 7;
    private static final int INDEX_8 = 8;
    private static final int INDEX_9 = 9;
    private static final int INDEX_10 = 10;
    private static final int INDEX_11 = 11;
    private static final int INDEX_12 = 12;
    private static final int INDEX_13 = 13;
    private static final int INDEX_14 = 14;
    private static final int EMPTY_SIZE = 0;
    private static final int ALL_STMT_SIZE = 10;
    private static final int NON_MULTI_SIZE = 7;
    private static final int MULTICOMMENT_PROC_SIZE = 1;
    private static final int EMBEDDED_BLANK_SIZE = 2;
    private static final int SC_TESTCASE_SIZE = 11;
    // list of delimiters
    private static final String SLASH_DELIM = "\n/\n";
    private static final String SEMICOL_DELIM = "\n;\n";
    private static final String TRAILING_SEMICOL_DELIM = ";\n";
    private static final String GO_DELIM = "\ngo\n";

    private static final boolean DEBUG = true;

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
        return new TestSuite(IncludeFileParserTest.class);
    }

    /**
     * Boilerplate constructor.
     * @param name the name of this test class.
     */
    public IncludeFileParserTest(final String name) {
        super(name);
    }

    // tests begin here

    /**
     * Testing the parser against an empty file.
     * @exception Exception if there was a problem with the test.
     */
    public final void testEmptyIncludeFile() throws Exception {
        String testFile = buildFile("", "");
        File f = new File(testFile);
        List stmtList = IncludeFileParser.parse(new FileReader(f));
        showList(stmtList, f.getName());
        assertEquals("Parsed list should be empty", EMPTY_SIZE,
            stmtList.size());
        f.delete();
    }

    /**
     * Testing the parser against all statements, with / delimiter.
     * @exception Exception if there was a problem with the test.
     */
    public final void testWithSlashDelimiter() throws Exception {
        String testFile = buildFile("0,1,2,3,4,5,6,7,8,9,10,11,12,13",
            SLASH_DELIM);
        File f = new File(testFile);
        List stmtList = IncludeFileParser.parse(new FileReader(f));
        showList(stmtList, f.getName());
        assertEquals("Size mismatch in parsed list", ALL_STMT_SIZE,
            stmtList.size());
        assertEquals("SELECT should be Other", 'O', 
            ((String) stmtList.get(INDEX_0)).charAt(INDEX_0));
        assertEquals("CREATE FUNCTION should be Multiline", 'M',
            ((String) stmtList.get(INDEX_1)).charAt(INDEX_0));
        assertEquals("INSERT should be Other", 'O', 
            ((String) stmtList.get(INDEX_2)).charAt(INDEX_0));
        assertEquals("EXEC PROC should be Callable", 'C',
            ((String) stmtList.get(INDEX_3)).charAt(INDEX_0));
        assertEquals("! should be Shell", 'S', 
            ((String) stmtList.get(INDEX_4)).charAt(INDEX_0));
        assertEquals("SELECT should be Other", 'O',
            ((String) stmtList.get(INDEX_5)).charAt(INDEX_0));
        assertEquals("CREATE PROC should be Multiline", 'M',
            ((String) stmtList.get(INDEX_6)).charAt(INDEX_0));
        assertEquals("UPDATE should be Other", 'O',
            ((String) stmtList.get(INDEX_7)).charAt(INDEX_0));
        assertEquals("SELECT should be Other", 'O',
            ((String) stmtList.get(INDEX_8)).charAt(INDEX_0));
        // testing substring ops
        String shellCall = (String) stmtList.get(INDEX_4);
        String shellCallWithoutPrefix = shellCall.substring(INDEX_2);
        assertEquals("/bin/sh -c 'rm /tmp/*.test'", shellCallWithoutPrefix);
        int epos = shellCallWithoutPrefix.indexOf("'");
        assertTrue(epos > -1);
        String firstPart = shellCallWithoutPrefix.substring(0, epos);
        String secondPart = shellCallWithoutPrefix.substring(epos);
        assertEquals("/bin/sh -c ", firstPart);
        assertEquals("'rm /tmp/*.test'", secondPart);
        String[] temp = firstPart.split("\\s");
        assertEquals(2, temp.length);
        assertEquals("/bin/sh", temp[0]);
        assertEquals("-c", temp[1]);
        f.delete();
    }

    /**
     * Testing the parser against all statements, with leading ; delimiter.
     * @exception Exception if there was a problem with the test.
     */
    public final void testWithLeadingSemicolonDelimiter() throws Exception {
        String testFile = buildFile("0,1,2,3,4,5,6,7,8,9,10,11,12,13",
            SEMICOL_DELIM);
        File f = new File(testFile);
        List stmtList = IncludeFileParser.parse(new FileReader(f));
        showList(stmtList, f.getName());
        assertEquals("Size mismatch in parsed list", ALL_STMT_SIZE,
            stmtList.size());
        assertEquals("SELECT should be Other", 'O', 
            ((String) stmtList.get(INDEX_0)).charAt(INDEX_0));
        assertEquals("CREATE FUNCTION should be Multiline", 'M',
            ((String) stmtList.get(INDEX_1)).charAt(INDEX_0));
        assertEquals("INSERT should be Other", 'O', 
            ((String) stmtList.get(INDEX_2)).charAt(INDEX_0));
        assertEquals("EXEC PROC should be Callable", 'C',
            ((String) stmtList.get(INDEX_3)).charAt(INDEX_0));
        assertEquals("! should be Shell", 'S', 
            ((String) stmtList.get(INDEX_4)).charAt(INDEX_0));
        assertEquals("SELECT should be Other", 'O',
            ((String) stmtList.get(INDEX_5)).charAt(INDEX_0));
        assertEquals("CREATE PROC should be Multiline", 'M',
            ((String) stmtList.get(INDEX_6)).charAt(INDEX_0));
        assertEquals("UPDATE should be Other", 'O',
            ((String) stmtList.get(INDEX_7)).charAt(INDEX_0));
        assertEquals("SELECT should be Other", 'O',
            ((String) stmtList.get(INDEX_8)).charAt(INDEX_0));
        f.delete();
    }

    /**
     * Testing the parser against all statements, with "go" delimiter.
     * @exception Exception if there was a problem with the test.
     */
    public final void testWithGoDelimiter() throws Exception {
        String testFile = buildFile("0,1,2,3,4,5,6,7,8,9,10,11,12,13",
            GO_DELIM);
        File f = new File(testFile);
        List stmtList = IncludeFileParser.parse(new FileReader(f));
        showList(stmtList, f.getName());
        assertEquals("Size mismatch in parsed list", ALL_STMT_SIZE,
            stmtList.size());
        assertEquals("SELECT should be Other", 'O', 
            ((String) stmtList.get(INDEX_0)).charAt(INDEX_0));
        assertEquals("CREATE FUNCTION should be Multiline", 'M',
            ((String) stmtList.get(INDEX_1)).charAt(INDEX_0));
        assertEquals("INSERT should be Other", 'O', 
            ((String) stmtList.get(INDEX_2)).charAt(INDEX_0));
        assertEquals("EXEC PROC should be Callable", 'C',
            ((String) stmtList.get(INDEX_3)).charAt(INDEX_0));
        assertEquals("! should be Shell", 'S', 
            ((String) stmtList.get(INDEX_4)).charAt(INDEX_0));
        assertEquals("SELECT should be Other", 'O',
            ((String) stmtList.get(INDEX_5)).charAt(INDEX_0));
        assertEquals("CREATE PROC should be Multiline", 'M',
            ((String) stmtList.get(INDEX_6)).charAt(INDEX_0));
        assertEquals("UPDATE should be Other", 'O',
            ((String) stmtList.get(INDEX_7)).charAt(INDEX_0));
        assertEquals("SELECT should be Other", 'O',
            ((String) stmtList.get(INDEX_8)).charAt(INDEX_0));
        f.delete();
    }

    /**
     * Testing the parser against all statements, with trailing ; delimiter.
     * Note that multi-line statements cannot be delimited with the trailing
     * semicolon delimiter as they have embedded traling semi-colons which 
     * cannot be easily distinguished from the trailing semi-colon.
     * @exception Exception if there was a problem with the test.
     */
    public final void testWithTrailingSemicolonDelimiter() throws Exception {
        String testFile = buildFile("0,1,3,4,5,6,7,8,10,11,12",
            TRAILING_SEMICOL_DELIM);
        File f = new File(testFile);
        List stmtList = IncludeFileParser.parse(new FileReader(f));
        showList(stmtList, f.getName());
        assertEquals("Size mismatch in parsed list", NON_MULTI_SIZE,
            stmtList.size());
        assertEquals("SELECT should be Other", 'O', 
            ((String) stmtList.get(INDEX_0)).charAt(INDEX_0));
        assertEquals("INSERT should be Other", 'O', 
            ((String) stmtList.get(INDEX_1)).charAt(INDEX_0));
        assertEquals("EXEC PROC should be Callable", 'C',
            ((String) stmtList.get(INDEX_2)).charAt(INDEX_0));
        assertEquals("! should be Shell", 'S', 
            ((String) stmtList.get(INDEX_3)).charAt(INDEX_0));
        assertEquals("SELECT should be Other", 'O',
            ((String) stmtList.get(INDEX_4)).charAt(INDEX_0));
        assertEquals("UPDATE should be Other", 'O',
            ((String) stmtList.get(INDEX_5)).charAt(INDEX_0));
        assertEquals("SELECT should be Other", 'O',
            ((String) stmtList.get(INDEX_6)).charAt(INDEX_0));
        f.delete();
    }

    /**
     * Runs a test case from Satish Chitnis about multi-line comments 
     * on top of a stored procedure call.
     * @exception Exception if there was a problem with the test.
     */
    public final void testMultilineComment() throws Exception {
        String testFile = buildFile("13", SLASH_DELIM);
        File f = new File(testFile);
        List stmtList = IncludeFileParser.parse(new FileReader(f));
        showList(stmtList, f.getName());
        assertEquals("Size mismatch in parsed list", MULTICOMMENT_PROC_SIZE,
            stmtList.size());
        assertEquals("CREATE PROCEDURE should be Multiline", 'M',
            ((String) stmtList.get(INDEX_0)).charAt(INDEX_0));
        f.delete();
    }

    /**
     * Test with embedded blanks between each command.
     * @exception Exception if there was a problem with the test.
     */
    public final void testWithEmbeddedBlankLines() throws Exception {
        String testFile = buildFile("1,13", SLASH_DELIM + "\n");
        File f = new File(testFile);
        List stmtList = IncludeFileParser.parse(new FileReader(f));
        showList(stmtList, f.getName());
        assertEquals("Size mismatch in parsed list", EMBEDDED_BLANK_SIZE,
            stmtList.size());
        assertEquals("SELECT should be other", 'O',
            ((String) stmtList.get(INDEX_0)).charAt(INDEX_0));
        assertEquals("CREATE PROCEDURE should be multiline", 'M',
            ((String) stmtList.get(INDEX_1)).charAt(INDEX_0));
        f.delete();
    }

    /**
     * Test the EXEC PROCEDURE conversion to JDBC CALL.
     * @exception Exception if there was a problem with the test.
     */
    public final void testExecuteProcConversion() throws Exception {
        String testFile = buildFile("0,1,6", SLASH_DELIM);
        File f = new File(testFile);
        List stmtList = IncludeFileParser.parse(new FileReader(f));
        showList(stmtList, f.getName());
        f.delete();
    }

    /**
     * Parses the SQL files in the test/asa directory to ensure that they
     * all parse correctly.
     * @exception Exception if there was a problem with the test.
     */
    public final void testParseAsaFiles() throws Exception {
        File asaDir = new File("test/asa");
        File[] files = asaDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (!files[i].getName().endsWith(".sql")) { continue; }
            List stmtList = IncludeFileParser.parse(new FileReader(files[i]));
            // automating some of this since this may change if Dave
            // decides to change the stored procedure
            if (files[i].getName().equals("add_dept.sql")) {
                System.out.println("Gotit");
                assertEquals(2, stmtList.size());
            }
            showList(stmtList, files[i].getName());
        }
    }

    /**
     * Test with an externally supplied file containing Oracle PL/SQL.
     * @exception Exception if there was a problem with the test.
     */
    public final void testWithOracleFile() throws Exception {
        String testFile = SC_TEST_FILE;
        File f = new File(testFile);
        List stmtList = IncludeFileParser.parse(new FileReader(f));
        showList(stmtList, f.getName());
        assertEquals("Number of statements should be " + SC_TESTCASE_SIZE,
            SC_TESTCASE_SIZE, stmtList.size());
    }

    /**
     * Test with an externally supplied file containing Sybase T-SQL.
     * @exception Exception if there was a problem with the test.
     */
    public final void testWithSybaseFile() throws Exception {
        String testFile = SC_TEST_FILE_SYBASE;
        File f = new File(testFile);
        List stmtList = IncludeFileParser.parse(new FileReader(f));
        showList(stmtList, f.getName());
        assertEquals("Number of statements should be 2", 2, stmtList.size());
    }

    /**
     * Test with single SQL string with trailing semicolon delimiter.
     * @exception Exception if there is a problem with the test.
     */
    public final void testSingleLineInIncludedFile() throws Exception {
        String testFile = buildFile("14", TRAILING_SEMICOL_DELIM);
        File f = new File(testFile);
        List stmtList = IncludeFileParser.parse(new FileReader(f));
        showList(stmtList, f.getName());
        assertEquals("Number of statements should be 1", 1, stmtList.size());
    }

    /**
     * Convenience debug method to show the values that were parsed out.
     * The value of show is controlled by the value of the private
     * variable DEBUG.
     * @param stmtList a List of parsed statements.
     * @param fileName the name of the file that was parsed.
     */
    private void showList(final List stmtList, final String fileName) {
        if (!DEBUG) { return; }
        System.out.println("-- " + fileName);
        for (int i = 0; i < stmtList.size(); i++) {
            System.out.println((String) stmtList.get(i));
        }
        System.out.println("--");
    }

    /**
     * Builds a temporary file with the statements specified in the index
     * list and delimited by the specified delimiter.
     * @param indexList a comma-separated list of indexes specifying the
     * order in which the statements need to be applied.
     * @param delimiter one of the allowed delimiters.
     * @return the name of the temporary file. The caller must delete the
     * file once its done with the test.
     * @exception Exception if there was a problem building the file.
     */
    private String buildFile(final String indexList, final String delimiter)
            throws Exception {
        String[] indexes = indexList.split("\\s*,\\s*");
        File tempFile = File.createTempFile("parsertest-", ".sql");
        PrintWriter writer = new PrintWriter(
            new FileOutputStream(tempFile), true);
        for (int i = 0; i < indexes.length; i++) {
            try {
                int stmtIndex = Integer.parseInt(indexes[i]);
                writer.print(STATEMENT_LIST[stmtIndex]);
                if (STATEMENT_LIST[stmtIndex].startsWith("--")) {
                    writer.println();
                } else {
                    writer.print(delimiter);
                }
            } catch (NumberFormatException e) {
                // :IGNORE: skip the loop
            }
        }
        writer.flush();
        writer.close();
        return tempFile.getCanonicalPath();
    }
}
