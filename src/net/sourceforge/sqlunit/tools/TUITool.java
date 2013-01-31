/*
 * $Id: TUITool.java,v 1.8 2004/09/30 17:27:55 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/tools/TUITool.java,v $
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
package net.sourceforge.sqlunit.tools;

import net.sourceforge.sqlunit.TypeMapper;
import net.sourceforge.sqlunit.beans.Param;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.Properties;

/**
 * The TUITool is a text based program that generates the SQLUnit
 * test element by actually running the stored procedure against the 
 * database. To run it from the command line, make sure that your CLASSPATH
 * contains all the JAR files needed by SQLUnit, then run the following
 * command.
 * java net.sourceforge.tuisqlunittool.TUITool [-Drcfile=rcfile]
 *      [-Dmode=offline]
 * where :
 *   rcfile is a startup file containing some or all of the parameters
 * needed to run a single test. Typically you would use this to specify the
 * connection parameters for your database. However, it can also be used to
 * specify a full single test and run it in offline mode. See the supplied
 * sqlunittuirc.sample for an example.
 *   offline indicates that the tool must run in offline mode, ie it should
 * not wait for a quit command from the user to terminate after processing a
 * single test. Only a single test can be run at a time with this setup.
 * @author Sahib Singh Wadhwa (sahibwadhwa@hotmail.com)
 * @version $Revision: 1.8 $
 */
public class TUITool {

    /** The Tool name. */
    private static final String TOOLNAME = "$RCSfile";

    /** Test independent prompts. */
    private static final String[] PROMPTS_1 = {
        "capturefile",
        "driver",
        "url",
        "user",
        "password"
    };
    /** List of connection elements to use. */
    private static final String[] ELEMENTS_PROMPT_1 = {
        "", "driver", "url", "user", "password"
    };
    /** Test level prompts. */
    private static final String[] PROMPTS_2 = {
        "__test!name",
        "__test!call!stmt",
    };
    /** Test parameter level prompts. */
    private static final String[] PROMPTS_3 = {
        "___test!call!param[%i]!type",
        "___test!call!param[%i]!inout",
        "___test!call!param[%i]!is-null",
        "___test!call!param[%i]!value"
    };
    private static final int TYPE_INDEX = 0;
    private static final int INOUT_INDEX = 1;
    private static final int ISNULL_INDEX = 2;
    private static final int VALUE_INDEX = 3;
    /** Commands available from tool, h for help, q to quit */
    private boolean quit = false;
    private boolean offline = false;
    private Properties dataMap = null;
    private BufferedReader stdin = null;
    private PrintStream stdout = null;
    private Connection conn = null;

    /**
     * Runner for the tool. Gets the arguments off the command line, 
     * instantiates the appropriate instance of the tool and runs it.
     * @param argv an array of command line arguments.
     */
    public static void main(final String[] argv) {
        String rcfile = System.getProperty("rcfile");
        String mode = System.getProperty("mode");
        try {
            TUITool tui;
            if (rcfile != null) {
                tui = new TUITool(rcfile);
            } else {
                tui = new TUITool();
            }
            if (mode != null && mode.equalsIgnoreCase("offline")) {
                tui.setOfflineMode(true);
            }
            TypeMapper mapper = TypeMapper.getTypeMapper();
            tui.run();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace(System.err);
            return;
        }
    }

    /**
     * Builds an instance of the TUITool.
     */
    public TUITool() {
        dataMap = new Properties();
        stdin = new BufferedReader(new InputStreamReader(System.in));
        stdout = System.out;
        offline = false;
    }

    /**
     * Builds an instance of the TUITool and populates it with
     * the contents of the rcfile Java properties file.
     * @param rcfile the property file to load.
     * @exception Exception if there was a problem constructing.
     */
    public TUITool(final String rcfile) throws Exception {
        this();
        dataMap = ToolUtils.getConfiguration(rcfile);
    }

    /**
     * Sets the run mode of the tool to offline. The default mode is 
     * online or interactive.
     * @param isOffline if true, tool will exhibit non-interactive behavior.
     */
    public final void setOfflineMode(final boolean isOffline) {
        this.offline = isOffline;
    }

    /**
     * Runs the TUI interface in an infinite loop, first prompting for
     * initial database connection information, then accepting inputs
     * to run each test in turn.
     * @exception Exception if any was thrown by underlying code.
     */
    public final void run() throws Exception {
        copyright();
        for (int i = 0; i < PROMPTS_1.length; i++) {
            prompt(PROMPTS_1[i]);
            if (quit) { return; }
        }
        for (;;) { // foreach test loop
            eraseTestInfo();
            if (quit) { break; }
            for (int j = 0; j < PROMPTS_2.length; j++) {
                prompt(PROMPTS_2[j]);
                if (quit) { break; }
            }
            int numParams = getNumberOfParameters();
            for (int j = 0; j < numParams; j++) { // param loop
                for (int k = 0; k < PROMPTS_3.length; k++) { // param attribs
                    prompt(substitute(PROMPTS_3[k], (j + 1)));
                    if (quit) { break; }
                } // end of param attribs loop
                if (quit) { break; }
            } // end of param loop
            if (quit) { continue; }
            generate();
            if (offline) { break; }
        } // end of test loop
    }

    /**
     * Handler for the interactive Help (h) command from the tool. Prints
     * a hardcoded help message.
     */
    private void help() {
        stdout.println("Answer the prompt and hit return");
        stdout.println("q - quit");
        stdout.println("h - help, display this message");
    }

    /**
     * Prints the copyright information to STDOUT at the beginning of
     * the tool's run.
     */
    private void copyright() {
        stdout.println("SQLUnit TUI Tool");
        stdout.println("Copyright(c) 2003 The SQLUnit Team");
    }

    /**
     * Generates the Test Element. Grabs the connection information from the 
     * supplied inputs, builds a Call Element to send to the CallHandler for 
     * processing against the database, runs the stored procedure, gets back
     * the result as a Result Element, then builds the test Element and outputs
     * it to the STDOUT.
     * @exception Exception if any was thrown by underlying code.
     */
    private void generate() throws Exception {
        if (conn != null) { ToolUtils.releaseConnection(); }
        conn = ToolUtils.getConnection(dataMap);
        String sql = dataMap.getProperty(PROMPTS_2[1]);
        int numParams = getNumberOfParameters();
        Param[] params = new Param[numParams];
        for (int i = 0; i < numParams; i++) {
            params[i] = new Param();
            params[i].setId((new Integer(i + 1)).toString());
            params[i].setName("c" + (new Integer(i + 1)).toString());
            params[i].setType(
                dataMap.getProperty(substitute(PROMPTS_3[TYPE_INDEX],
                (i + 1))));
            params[i].setInOut(
                dataMap.getProperty(substitute(PROMPTS_3[INOUT_INDEX],
                (i + 1))));
            params[i].setIsNull(
                dataMap.getProperty(substitute(PROMPTS_3[ISNULL_INDEX],
                (i + 1))));
            params[i].setValue(
                dataMap.getProperty(substitute(PROMPTS_3[VALUE_INDEX],
                (i + 1))));
        }
        ToolUtils.makeTest(sql, params, 
            dataMap.getProperty(PROMPTS_2[0], "Test generated by " + TOOLNAME),
            dataMap);
    }

    /**
     * Prints a prompt to the tool's STDOUT and waits for a response from
     * the tool's STDIN. If value is already supplied, then it will print
     * the value to STDOUT, otherwise it will prompt, waiting on user input.
     * If the Quit(q) or Help(h) command is entered, it will invoke the
     * handlers for these commands.
     * @param prompt the prompt to show.
     * @exception IOException if any was thrown by underlying code.
     */
    private void prompt(final String prompt) throws IOException {
        while (true) {
            stdout.print(TOOLNAME + ":" + prompt + "> ");
            stdout.flush();
            String response = dataMap.getProperty(prompt);
            if (response == null) {
                response = stdin.readLine();
                if (response.equals("q")) {
                    quit = true;
                    break;
                }
                if (response.equals("h")) {
                    help();
                    continue;
                }
                dataMap.setProperty(prompt, response);
            } else {
                stdout.println(response);
                break;
            }
        }
        return;
    }

    /**
     * Substitutes the value of %i in the param prompt specification. This
     * allows the specification of a set of param prompt templates which 
     * can be resolved at run time into the appropriate number of sets of
     * param elements.
     * @param source the param prompt String.
     * @param index the value to substitute for %i.
     * @return the resolved param prompt String.
     */
    private static String substitute(final String source, final int index) {
        return source.replaceAll("%i", new Integer(index).toString());
    }

    /**
     * Removes the test related rows from the internal hashmap. This is 
     * so a new test can be run without getting the values from the old
     * test. Keys with a single leading _ character, such as connection
     * information, persist across tests.
     */
    private void eraseTestInfo() {
        if (offline) { return; }
        for (Enumeration e = dataMap.propertyNames(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            if (key.startsWith("__") || key.startsWith("___")) {
                dataMap.remove(key);
            }
        }
    }

    /**
     * Counts the number of ? characters in the specified call statement.
     * This is used to determine how many param element sets to prompt for.
     * @return the number of ? characters in the Call statement.
     */
    private int getNumberOfParameters() {
        int numParams = 0;
        String callStmt = dataMap.getProperty(PROMPTS_2[1]);
        if (callStmt == null) {
            numParams = 0;
        } else {
            char[] callStmtCharArray = callStmt.toCharArray();
            for (int i = 0; i < callStmtCharArray.length; i++) {
                if (callStmtCharArray[i] == '?') { numParams++; }
            }
        }
        return numParams;
    }
}
