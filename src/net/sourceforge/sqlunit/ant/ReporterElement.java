/*
 * $Id: ReporterElement.java,v 1.3 2006/04/05 02:51:59 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/ant/ReporterElement.java,v $
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

import java.lang.reflect.Constructor;

import net.sourceforge.sqlunit.IReporter;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.EnumeratedAttribute;

/**
 * Handles the nested &lt;reporter&gt; tag of SQLUnit Ant task.
 * @author Ivan Ivanov
 */
public class ReporterElement {

    /**
     * The class name of the default reporter.
     */
    public static final String DEFAULT_REPORTER_CLASSNAME =
        "net.sourceforge.sqlunit.reporters.TextReporter";

    /**
     * The class name of the CanooWebTest reporter.
     */
    public static final String CANOO_REPORTER_CLASSNAME =
        "net.sourceforge.sqlunit.reporters.CanooWebTestReporter";

    /**
     * Specifies the location of the output file.
     */
    private String file;

    /**
     * The name of the reporter's class.
     */
    private String className;

    /**
     * Returns the class name of the reporter.
     * @return the class name of the reporter
     * @see #setClassName(String)
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the class name of the reporter.
     * @param className the class name of the reporter.
     * @see #getClassName()
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Returns the name of the file that will store the
     * output.
     * @return the name of the file that will store the
     * output
     * @see #setFile(String)
     */
    public String getFile() {
        return file;
    }

    /**
     * Sets the name of the output file.
     * @param file the name of the output file
     * @see #getFile()
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * Sets the format of the reporter.
     * @param format the type of the reporter
     */
    public void setLogFormat(LogFormatAttribute format) {
        if ("default".equals(format.getValue())) {
            setClassName(DEFAULT_REPORTER_CLASSNAME);
        } else if ("canoo".equals(format.getValue())){
            setClassName(CANOO_REPORTER_CLASSNAME);
        }
    }

    /**
     * Creates a new IReporter that corresponds to the
     * current ReporterElement.
     *
     * @return IReporter that belongs to the class specified
     * in the ReporterElement and with the specified logfile.
     * @throws BuildException if an exception is raised
     */
    public IReporter createReporter() throws BuildException {
        if (className == null || className.length() == 0) {
            className = DEFAULT_REPORTER_CLASSNAME;
        }
        try {
            Class clazz = Class.forName(className);
            Constructor constructor = clazz.getConstructor(
                    new Class[] {String.class});
            Object o = constructor.newInstance(new Object[] {file});
            if (!(o instanceof IReporter)) {
                throw new BuildException(className + " does not implement" +
                        "IReporter. Please specify a valid reporter");
            }
            return (IReporter)o;
        } catch(Exception e) {
            throw new BuildException(e);
        }
    }

    /**
     * Defines the valid values for logformat attribute.
     * @author Ivan Ivanov
     */
    public static class LogFormatAttribute extends EnumeratedAttribute {

        /**
         * Returns the an array of the supported reporters' names.
         *
         * @see org.apache.tools.ant.types.EnumeratedAttribute#getValues()
         */
        public String[] getValues() {
            return new String[] {"default", "canoo"};
        }

    }
}
