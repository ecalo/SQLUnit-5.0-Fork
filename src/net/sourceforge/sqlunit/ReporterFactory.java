/*
 * $Id: ReporterFactory.java,v 1.8 2006/01/07 02:27:23 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/ReporterFactory.java,v $
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

import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * The ReporterFactory returns references to Reporters.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.8 $
 */
public final class ReporterFactory {

    private static final Logger LOG = Logger.getLogger(ReporterFactory.class);

    /** The Reporter registry is represented by reporters.properties */
    private static final String REPORTER_REGISTRY = 
        "net.sourceforge.sqlunit.reporters";
    private static final Class DEFAULT_REPORTER_CLASS = 
        net.sourceforge.sqlunit.reporters.TextReporter.class;
    private static ResourceBundle props = null;

    /**
     * Private constructor. Cannot be instantiated.
     */
    private ReporterFactory() {
        // private constructor, cannot instantiate
    }

    /**
     * Returns a IReporter object given its name.
     * @param name the name of the Reporter object.
     * @param reportFile the output file name where the report will be sent.
     * @return an implementation of a IReporter object.
     * @exception Exception if there was a problem instantiating the Reporter.
     */
    public static IReporter getInstance(final String name, 
            final String reportFile) throws Exception {
        if (props == null) {
            try {
                props = ResourceBundle.getBundle(REPORTER_REGISTRY);
            } catch (MissingResourceException e) {
                throw new SQLUnitException(IErrorCodes.GENERIC_ERROR,
                    new String[] {"I/O", e.getClass().getName(),
                    "Registry " + REPORTER_REGISTRY 
                    + " is missing or incorrect"}, e);
            }
        }
        if (name == null) { return getDefaultInstance(reportFile); }
        Class reporterClass = Class.forName(props.getString(name));
        if (reporterClass == null || reportFile == null) {
            return getDefaultInstance(reportFile);
        } else {
            return getReporter(reporterClass, reportFile);
        }
    }

    /**
     * Returns the default IReporter (TextReporter) object for SQLUnit.
     * @param reportFile the report file to use for output.
     * @return the default IReporter instance.
     * @exception Exception if there was a problem instantiating the Reporter.
     */
    public static IReporter getDefaultInstance(final String reportFile) 
            throws Exception {
        return getReporter(DEFAULT_REPORTER_CLASS, reportFile);
    }

    /**
     * Constructs a Reporter implementation from the classname and the
     * report file name.
     * @param reporterClass the reporter class to instantiate.
     * @param reportFile the output file to place the report.
     * @return a IReporter implementation constructed from the inputs.
     * @exception Exception if there was a problem constructing the Reporter.
     */
    private static IReporter getReporter(final Class reporterClass, 
            final String reportFile) throws Exception {
        Constructor ctor = reporterClass.getConstructor(
            new Class[] {String.class});
        return (IReporter) ctor.newInstance(new Object[] {reportFile});
    }
}
