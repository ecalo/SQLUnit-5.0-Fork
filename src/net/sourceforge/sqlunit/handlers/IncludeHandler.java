/*
 * $Id: IncludeHandler.java,v 1.11 2005/07/09 01:39:20 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/IncludeHandler.java,v $
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

import net.sourceforge.sqlunit.ConnectionRegistry;
import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.parsers.IncludeFileParser;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.io.File;
import java.io.FileReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

/**
 * The IncludeHandler reads and executes SQL statements specified in a 
 * included file. The format of the include file is a sequence of valid
 * SQL statements (usually updates and inserts) separated by lines containing
 * a single semi-colon (;) or slash (/) character. The semi-colon or slash
 * character must be the first character in the line and the rest of the
 * line must be empty. Alternatively, a line terminated by the semi-colon (;)
 * character can also be used to signal the end of the SQL statement.Comments 
 * can appear in the include file as well, and uses the standard -- comment 
 * syntax of SQL.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.11 $
 * @sqlunit.parent name="setup" ref="setup"
 * @sqlunit.parent name="teardown" ref="teardown"
 * @sqlunit.element name="include"
 *  description="The include tag is used to specify an external file that
 *  contains a list of SQL statements that should be executed together.
 *  The statements can be separated from one another by a free standing
 *  slash (/) or semi-colon character (;) on its own line, or by a line 
 *  terminated by a semi-colon (;) character."
 *  syntax="(EMPTY)"
 * @sqlunit.attrib name="file"
 *  description="Specifies a relative or absolute path name to the file
 *  containing the SQL statements."
 *  required="Yes"
 * @sqlunit.attrib name="connection-id"
 *  description="When multiple Connections are defined in a SQLUnit test,
 *  this attribute is used to select the Connection to use for running 
 *  the SQL statements. If not specified, SQLUnit will use the default
 *  Connection if that is specified."
 *  required="No"
 * @sqlunit.example name="Cleanup SQL being included in a teardown tag"
 *  description="
 *  <teardown>{\n}
 *  {\t}<include file=\"test/sybase/teardown.sql\" />{\n}
 *  </teardown>
 *  "
 */
public class IncludeHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(IncludeHandler.class);

    /**
     * Processes the JDOM Element representing the include tag and runs
     * the SQL statement specified in the file.
     * @param elInclude the JDOM Element representing the include tag.
     * @return an Object which is the result of processing. Null in this case.
     * @exception Exception if something went wrong processing the include.
     */
    public final Object process(final Element elInclude) throws Exception {
        LOG.debug(">> process(elInclude)");
        if (elInclude == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"include"});
        }
        String connectionId = XMLUtils.getAttributeValue(
            elInclude, "connection-id");
        String includeFile = XMLUtils.getAttributeValue(elInclude, "file");
        File file = new File(includeFile);
        if (!file.exists()) {
            throw new SQLUnitException(IErrorCodes.NO_INCLUDE_FILE,
                new String[] {includeFile});
        }
        List batch = IncludeFileParser.parse(new FileReader(file));
        Iterator iter = batch.iterator();
        int stmtId = 0;
        while (iter.hasNext()) {
            String stmt = (String) iter.next();
            String sstmt = stripMetaInformation(stmt).trim();
            if (sstmt == "") { continue; }
            stmtId++;
            switch (stmt.charAt(0)) {
                case 'M': // multi-line CREATE/REPLACE PROCEDURE, etc
                    executeMultilineSQL(sstmt, stmtId, includeFile,
                        connectionId);
                    break;
                case 'C': // callable statement EXECUTE PROCEDURE
                    executeCallableSQL(sstmt, stmtId, includeFile, 
                        connectionId);
                    break;
                case 'S': // shell call
                    executeShellCall(sstmt, stmtId, includeFile);
                    break;
                case 'O': // Other SQL statement
                    executeOtherSQL(sstmt, stmtId, includeFile, connectionId);
                    break;
                default:  // should never happen
                    throw new SQLUnitException(IErrorCodes.ERROR_IN_INCLUDE,
                        new String[] {sstmt, "" + stmtId, includeFile,
                        "Parse Failure"});
            }
        }
        return null;
    }

    /**
     * Executes a multi-line SQL statement. These are statements that
     * begin with CREATE and REPLACE and are usually stored procedures,
     * functions or triggers and support embedded semicolons. For this
     * reason, trailing semicolon delimiters are not recognized by this
     * type of statement.
     * @param stmt the String containing the SQL.
     * @param stmtId the 1-based statement id in the file.
     * @param fileName the file name in which the statement occured.
     * @param conn the Connection object.
     */
    private void executeMultilineSQL(final String stmt, final int stmtId,
            final String fileName, final String connectionId)
            throws SQLUnitException {
        LOG.debug("executeMultilineSQL(" + stmt + "," + stmtId + "," + fileName
            + ",conn)");
        Connection conn = ConnectionRegistry.getConnection(connectionId);
        try {
            Statement statement = conn.createStatement();
            statement.execute(stmt);
            ConnectionRegistry.safelyCommit(connectionId, conn);
        } catch (SQLException e) {
            ConnectionRegistry.safelyRollback(connectionId, conn);
            throw new SQLUnitException(IErrorCodes.ERROR_IN_INCLUDE,
                new String[] {stmt.trim(), "" + stmtId, fileName,
                e.getMessage()}, e);
        }
    }

    /**
     * Executes a callable SQL statement. These are statements that begin
     * with EXEC(UTE PROCEDURE). They are handled by the handler using a 
     * java.sql.CallableStatement.
     * @param stmt the String containing the SQL.
     * @param stmtId the 1-based statement id in the file.
     * @param fileName the file name in which the statement occured.
     * @param conn the Connection object.
     */
    private void executeCallableSQL(final String stmt, final int stmtId,
            final String fileName, final String connectionId)
            throws SQLUnitException {
        LOG.debug("executeCallableSQL(" + stmt + "," + stmtId + "," + fileName
            + ",conn)");
        Connection conn = ConnectionRegistry.getConnection(connectionId);
        try {
            CallableStatement cs = conn.prepareCall(stmt);
            cs.execute();
            ConnectionRegistry.safelyCommit(connectionId, conn);
        } catch (SQLException e) {
            ConnectionRegistry.safelyRollback(connectionId, conn);
            throw new SQLUnitException(IErrorCodes.ERROR_IN_INCLUDE,
                new String[] {stmt.trim(), "" + stmtId, fileName,
                e.getMessage()}, e);
        }
    }

    /**
     * Executes forms of SQL statements that are not a stored procedure
     * call or not a DDL. They are handled by the handler using a
     * java.sql.PreparedStatement.
     * @param stmt the String containing the SQL.
     * @param stmtId the 1-based statement id in the file.
     * @param fileName the file name in which the statement occured.
     * @param conn the Connection object.
     */
    private void executeOtherSQL(final String stmt, final int stmtId,
            final String fileName, final String connectionId)
            throws SQLUnitException {
        LOG.debug("executeOtherSQL(" + stmt + "," + stmtId + "," + fileName
            + ",conn)");
        Connection conn = ConnectionRegistry.getConnection(connectionId);
        try {
            PreparedStatement ps = conn.prepareStatement(stmt);
            ps.execute();
            ConnectionRegistry.safelyCommit(connectionId, conn);
        } catch (SQLException e) {
            ConnectionRegistry.safelyRollback(connectionId, conn);
            throw new SQLUnitException(IErrorCodes.ERROR_IN_INCLUDE,
                new String[] {stmt.trim(), "" + stmtId, fileName,
                e.getMessage()}, e);
        }
    }

    /**
     * Executes a shell call within the include file. Either a shell 
     * script or batch file must be supplied with the full path name,
     * which would invoke the shell internally, or if shell metacharacter
     * support is desired, then the command should be prefixed by the
     * full path name of the command processor and enclosed in parenthesis.
     * @param stmt the String containing the SQL.
     * @param stmtId the 1-based statement id in the file.
     * @param fileName the file name in which the statement occured.
     */
    private void executeShellCall(final String stmt, final int stmtId,
            final String fileName) throws SQLUnitException {
        LOG.debug("executeShellCall(" + stmt + "," + stmtId + "," + fileName
            + ")");
        try {
            // check if there is an embedded command
            int epos = stmt.indexOf("'");
            String[] cmdArray;
            if (epos > -1) {
                String[] temp = stmt.substring(0, epos).split("\\s");
                cmdArray = new String[temp.length + 1];
                for (int i = 0; i < temp.length; i++) {
                    cmdArray[i] = temp[i];
                }
                cmdArray[temp.length] = stmt.substring(epos);
            } else {
                cmdArray = stmt.split("\\s");
            }
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec(cmdArray);
            int rc = p.waitFor();
        } catch (Exception e) {
            throw new SQLUnitException(IErrorCodes.ERROR_IN_INCLUDE,
                new String[] {stmt, "" + stmtId, fileName, e.getMessage()}, e);
        }
    }

    /**
     * Utility method to remove the meta-information returned by the 
     * parser to tell the handler what kind of statement it is. The meta
     * information is used by the handler to decide which include 
     * statement processing method to invoke.
     * @param unstripped the full String returned from the parser.
     * @return the stripped portion with the meta information removed.
     */
    private String stripMetaInformation(final String unstripped) {
        if (unstripped == null || unstripped.length() < 2) {
            return "";
        }
        return unstripped.substring(2);
    }
}
