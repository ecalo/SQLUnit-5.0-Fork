/*
 * $Id: SqlunitTask.java,v 1.11 2006/04/05 03:38:10 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/ant/SqlunitTask.java,v $
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
package net.sourceforge.sqlunit.ant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IReporter;
import net.sourceforge.sqlunit.ReporterFactory;
import net.sourceforge.sqlunit.SQLUnit;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.SymbolTable;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 * SqlunitTask is a custom ant task that accepts a testFile name and
 * runs the SQLUnit test class with the given XML test script.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.11 $
 */
public class SqlunitTask extends Task {

    private boolean haltOnFailure = false;
    private boolean debug = false;
    private String logfile = "";
    private String logformat = null;
    private String testFile = null;
    private String failureProperty = null;
    private String name = "sqlunit";
    private List fileSets = new ArrayList();

    /**
     * Executes the task.
     * @exception BuildException if thrown by the task
     */
    public final void execute() throws BuildException {
        IReporter reporter = null;
        if (logfile != null && logfile.trim().length() == 0) {
            logfile = null;
        }
        PrintStream logStream = null;
        try {
            reporter = createReporter();
            boolean allTestsPassed = true;
            if (!reporter.hasContainer()) {
                Project project = getProject();
                Vector buildListeners = project.getBuildListeners();
                if (buildListeners == null || buildListeners.size() == 0) {
                    project.addBuildListener(new DefaultLogger());
                    buildListeners = project.getBuildListeners();
                }
                BuildLogger sqlUnitLogger = (BuildLogger) buildListeners.get(0);
                if (logfile != null && logfile.trim().length() > 0) {
                    logStream = new PrintStream(
                        new FileOutputStream(logfile), true);
                    sqlUnitLogger.setOutputPrintStream(logStream);
                    sqlUnitLogger.setMessageOutputLevel(debug
                        ? Project.MSG_DEBUG : Project.MSG_INFO);
                }
            }
            List testFiles = getTestFiles();
            int numTestFiles = testFiles.size();
            for (int i = 0; i < numTestFiles; i++) {
                String tf = (String) testFiles.get(i);
                File file = new File(tf);
                if (!file.exists()) {
                    throw new SQLUnitException(IErrorCodes.TESTFILE_NOT_FOUND,
                        new String[] {tf, IErrorCodes.USAGE});
                }
                if (!file.canRead()) {
                    throw new SQLUnitException(IErrorCodes.TESTFILE_CANT_READ,
                        new String[] {tf});
                }
                // Pass in the sqlunit task attributes
                SQLUnit sqlunit = new SQLUnit("sqlunit");
                sqlunit.setHaltOnFailure(haltOnFailure);
                sqlunit.setDebug(debug);
                sqlunit.setTestFile(tf);
                sqlunit.setName(name);
                sqlunit.setReporter(reporter);
                sqlunit.setAntSymbols(getProject().getProperties());
                try {
                    sqlunit.runTest();
                } catch (Throwable e) {
                    if (!haltOnFailure) {
                        allTestsPassed = false;
                        log("sqlunit-ant: " + e.getMessage());
                        continue;
                    } else {
                        log("sqlunit-ant: " + e.getMessage());
                        throw e;
                    }
                }
            }
            if (!allTestsPassed) {
                // where should we look for output?
                if (logfile == null) {
                    logfile = "the console";
                }
                throw new SQLUnitException(IErrorCodes.SUITE_FAILURE_EXCEPTION,
                    new String[] {logfile});
            }
        } catch (Throwable e) {
            if (failureProperty != null) {
                getProject().setNewProperty(failureProperty, "true");
            }
            if (debug) { e.printStackTrace(System.err); }
            log(e.getMessage(), Project.MSG_ERR);
            if (haltOnFailure) {
                throw new BuildException(e.getMessage());
            }
        } finally {
            restoreAntVariables();
            if (logStream != null) { logStream.close(); }
        }
    }

    /**
     * Sets the value of the attribute testFile.
     * @param testfile the full path name of the test XML script.
     */
    public final void setTestfile(final String testfile) {
        this.testFile = testfile;
    }
    
    /**
     * Sets the value of the attribute name. If name is null, then the
     * attribute is not changed.
     * @param name the name of the test case (for reporting purposes).
     */
    public final void setName(final String name) {
        if (name != null) {
            this.name = name;
        }
    }

    /**
     * Sets the value of the attribute haltOnFailure.
     * @param haltOnFailure can be set to true or false. Defaults to false.
     */
    public final void setHaltOnFailure(final boolean haltOnFailure) {
        this.haltOnFailure = haltOnFailure;
    }

    /**
     * Sets the value of the failureProperty attribute.
     * @param failureProperty name of ant property.
     */
    public final void setFailureProperty(final String failureProperty) {
        this.failureProperty = failureProperty;
    }
    
    /**
     * Sets the value of the attribute debug.
     * @param debug can be set to true or false. If true, returns the
     * full stack trace of where the problem occured, else returns a short
     * error message.
     */
    public final void setDebug(final boolean debug) {
        this.debug = debug;
    }

    /**
     * Sets the value of the attribute logformat.
     * @param logformat the log format to set.
     */
    public final void setLogformat(final String logformat) {
        this.logformat = logformat;
    }

    /**
     * Sets the value of the logfile attribute.
     * @param logfile the file name to which to log SQLUnit output. If
     * not set, the output will be sent to STDERR. Note that each run
     * of SQLUnit will overwrite the log file.
     */
    public final void setLogfile(final String logfile) {
        this.logfile = logfile;
    }

    /**
     * Sets the value of a list of SQLUnit XML test files specified by
     * a nested fileSet element.
     * @param fileSet the fileSet to add.
     */
    public final void addFileSet(final FileSet fileSet) {
        this.fileSets.add(fileSet);
    }

    /**
     * Converts the testFile attribute or fileSet nested specification into
     * a list of file names to work with.
     * @return a List of file names of SQLUnit test files.
     * @exception SQLUnitException if testFiles specified ambiguously or
     * not specified.
     */
    private List getTestFiles() throws SQLUnitException {
        if (this.testFile != null && this.fileSets.size() > 0) {
            throw new SQLUnitException(IErrorCodes.DUPLICATE_TESTFILE,
                new String[0]);
        }
        List testFiles = new ArrayList();
        try {
            if (this.testFile != null) {
                testFiles.add(this.testFile);
            } else {
                int numFileSets = this.fileSets.size();
                for (int i = 0; i < numFileSets; i++) {
                    FileSet fs = (FileSet) fileSets.get(i);
                    DirectoryScanner ds = fs.getDirectoryScanner(getProject());
                    ds.scan();
                    String baseDir = ds.getBasedir().getCanonicalPath();
                    String[] files = ds.getIncludedFiles();
                    for (int j = 0; j < files.length; j++) {
                        testFiles.add(baseDir + File.separatorChar + files[j]);
                    }
                }
            }
            if (testFiles.size() == 0) {
                throw new SQLUnitException(IErrorCodes.NO_TESTFILE,
                    new String[] {IErrorCodes.USAGE});
            }
        } catch (IOException e) {
            throw new SQLUnitException(IErrorCodes.GENERIC_ERROR,
                new String[] {"I/O", e.getClass().getName(),
                e.getMessage()}, e);
        }
        return testFiles;
    }

    protected IReporter createReporter() throws Exception {
        return ReporterFactory.getInstance(logformat, logfile);
    }
    
    /**
     * Makes changed Ant properties available to Ant. 
     */
    private void restoreAntVariables() {
        Iterator it = SymbolTable.getSymbols();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (key.startsWith("${ant.")) {
                String antKey = key.substring(6, key.length() - 1);
                String value = SymbolTable.getValue(key);
                if (!value.equals(getProject().getProperty(antKey))) {
                    getProject().setProperty(antKey, value);
                }
            }
        }
        // TODO Auto-generated method stub
        
    }

}
