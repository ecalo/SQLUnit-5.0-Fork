/*
 * $Id: SQLUnitEntityResolver.java,v 1.1 2005/05/26 00:35:15 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/SQLUnitEntityResolver.java,v $
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

import java.io.IOException;

import org.apache.log4j.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The SQLUnit Entity Resolver sets the DTD for the SQLUnit test file from
 * within the SQLUnit JAR file.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.1 $
 */
public class SQLUnitEntityResolver implements EntityResolver {

    private static final Logger LOG = Logger.getLogger(
        SQLUnitEntityResolver.class);

    /**
     * Resolves the system id for an SQLUnit XML file to the sqlunit.dtd
     * available inside the SQLUnit JAR.
     * @param publicId the public id in the DocType declaration.
     * @param systemId the system id in the DocType declaration.
     */
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {
        LOG.debug("Resolving entity: " + publicId + "/" + systemId);
        if (systemId.indexOf("sqlunit.dtd") > -1) {
            return new InputSource(
                this.getClass().getResourceAsStream("/sqlunit.dtd"));
        } else {
            return null;
        }
    }
}
