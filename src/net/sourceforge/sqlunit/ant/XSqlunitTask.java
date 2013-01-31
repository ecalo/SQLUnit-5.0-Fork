/*
 * $Id: XSqlunitTask.java,v 1.1 2006/04/05 02:51:59 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/ant/XSqlunitTask.java,v $
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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import net.sourceforge.sqlunit.IReporter;
import net.sourceforge.sqlunit.reporters.ReporterList;

/**
 * Extended SQLUnit task.
 *
 * @author Ivan Ivanov
 */
public class XSqlunitTask extends SqlunitTask {

    /**
     * Contains the nested reporters elements.
     */
    private List reporters;

    /**
     * Constructs a new XSQLUnit task.
     */
    public XSqlunitTask() {
        super();
        /* Usually we use not more than two reporters - one
         * for logging on the console and another for logging
         * in a file.
         */
        reporters = new Vector(3);
    }

    /**
     * Adds a nested reporter element.
     * @param re the reporter element
     */
    public void addReporter(ReporterElement re) {
        reporters.add(re);
    }

    /**
     * Creates a new IReporter from the given nested
     * reporter elements.
     * @return the IReporter object
     */
    protected IReporter createReporter() {
        if (reporters.isEmpty()) {
            ReporterElement re = new ReporterElement();
            re.setClassName(ReporterElement.DEFAULT_REPORTER_CLASSNAME);
            addReporter(re);
        }

        ReporterList reporterList = new ReporterList(reporters.size());
        for (Iterator i = reporterList.iterator(); i.hasNext(); ) {
            ReporterElement re = (ReporterElement)i.next();
            IReporter ireporter = re.createReporter();
            // Important log
            log("IReporter class is " + ireporter.getClass().getName());
            reporterList.add(ireporter);
        }
        // Important log
        log("The number of ireporters is " + reporters.size());
        return reporterList;
    }

    /**
     * Adds a preconfigured sysproperty nested element.
     * @param se the sysproperty element
     */
    public void addConfiguredSysproperty(SyspropertyElement se) {
        System.setProperty(se.getKey(), se.getValue());
    }
}
