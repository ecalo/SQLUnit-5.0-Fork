/*
 * $Id: IHandler.java,v 1.11 2004/09/25 20:40:17 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/IHandler.java,v $
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

import org.jdom.Element;

/**
 * The IHandler interface represents a SQLUnit tag handler.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.11 $
 */
public interface IHandler {

    /**
     * The process object processes the JDOM Element representing a database
     * call of some kind, applies it to the JDBC Connection object and
     * returns a resultant Object. The client instantiating the IHandler
     * object using the HandlerFactory is responsible for casting it to 
     * the appropriate Handler class.
     * @param el The JDOM Element representing the database call.
     * @return an Object representing the results of the processing.
     * @exception Exception if something went wrong with the process call.
     */
    Object process(Element el) throws Exception;
}
