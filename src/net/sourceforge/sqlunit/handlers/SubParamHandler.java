/*
 * $Id: SubParamHandler.java,v 1.2 2005/04/02 22:02:26 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/SubParamHandler.java,v $
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

import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.beans.SubParam;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * The SubParamHandler takes the JDOM Element representing a subparam tag and 
 * converts it to a SubParam object.
 * @author Victor Alekseev (krocodl@users.sourceforge.net)
 * @version $Revision: 1.2 $
 * @sqlunit.parent name="sub" ref="sub"
 * @sqlunit.element name="subparam"
 *  description="The param tag is used to specify an argument to a SQL
 *  statement or stored procedure, or aggregated into a set of paramset
 *  tags for batch calls. The body of the tag contains the value of 
 *  the parameter."
 *  syntax="(BODY)"
 * @sqlunit.attrib name="id"
 *  description="Specifies the sequence number of the parameter to be 
 *  replaced. The sequence is one-based to keep parity with the JDBC 
 *  specification."
 *  required="Yes"
 * @sqlunit.attrib name="name"
 *  description="Specifies the name of the parameter to be replaced. This
 *  has been added for readability to help stored procedure authors/testers
 *  to spot errors quickly."
 *  required="No"
 * @sqlunit.attrib name="type"
 *  description="Specifies the type of the parameter to be replaced. The
 *  type is a String, and has a one to one mapping to the datatypes defined
 *  in java.sql.Types. It also allows some additional Oracle specific types
 *  specified in net.sourceforge.sqlunit.OracleExtensionTypes. The value
 *  of the type is the same as the field name of the member variables in
 *  Types or OracleExtensionTypes classes. Thus if the type is Integer, the
 *  corresponding SQL type is java.sql.Type.INTEGER, and the string value
 *  that should be used here is INTEGER."
 *  required="Yes"
 * @sqlunit.attrib name="is-null"
 *  description="If set to true, indicates that this parameter is an SQL
 *  NULL value. Valid values are true or false."
 *  required="No, default is false."
 * @sqlunit.attrib name="inout"
 *  description="Specifies whether the parameter is an IN, OUT or INOUT
 *  parameter. Valid values are in, out and inout respectively."
 *  required="No, default is in."
 * @sqlunit.example name="A simple param tag definition"
 *  description="
 *  <param id=\"1\" name=\"v1\" type=\"INTEGER\">1</param>{\n}
 *  "
 */
public class SubParamHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(SubParamHandler.class);

    /**
     * Processes the JDOM Element representing the param tag returns the
     * SubParam object.
     * @param elSubParam the JDOM Element representing the param tag.
     * @return a SubParam object. Client needs to cast to a SubParam.
     * @exception Exception if something went wrong processing the param.
     */
    public final Object process(final Element elSubParam) throws Exception {
        LOG.debug(">> process(elSubParam)");
        if (elSubParam == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"subparam"});
        }
        SubParam subparam = new SubParam();
        subparam.setName(XMLUtils.getAttributeValue(elSubParam, "name"));
        subparam.setValue(XMLUtils.getAttributeValue(elSubParam, "value"));
        return subparam;
    }
}
