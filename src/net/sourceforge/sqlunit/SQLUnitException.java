/*
 * $Id: SQLUnitException.java,v 1.7 2006/01/07 02:27:23 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/SQLUnitException.java,v $
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

import java.text.MessageFormat;

/**
 * SQLUnitException is an application specific Exception class. Exceptions
 * which are deemed to be application specific, ie of interest to the 
 * user of SQLUnit rather than the developer, are classified as a 
 * SQLUnitException. The SQLUnit tool treats them a little differently
 * compared to standard Exceptions. Instead of including this in the
 * stack trace in case of an error, the message is displayed in STDERR.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.7 $
 */
public class SQLUnitException extends Exception {

    /**
     * Creates an SQLUnitException object using the error message.
     * @param message the Exception message.
     */
    public SQLUnitException(final String message) {
        super(message);
    }

    /**
     * Creates an SQLUnitException object using the error message and
     * replaceable positional parameters.
     * @param message the Exception message with positional parameters.
     * @param args an array of positional parameter String values.
     */
    public SQLUnitException(final String message, final String[] args) {
        super(MessageFormat.format(message, (Object[]) args));
    }

    /**
     * Creates an SQLUnitException object using the error message and a 
     * chained exception.
     * @param message the Exception message.
     * @param e the underlying chained throwable.
     */
    public SQLUnitException(final String message, final Throwable e) {
        super(message, e);
    }

    /**
     * Creates an SQLUnitException object using the error message, replaceable
     * positional parameters and a chained exception.
     * @param message the Exception message with positional parameters.
     * @param args an array of positional parameter String values.
     * @param e the underlying chained throwable.
     */
    public SQLUnitException(final String message, final String[] args, 
            final Throwable e) {
        super(MessageFormat.format(message, (Object[]) args), e);
    }
}
