/*
 * $Id: ParamHandler.java,v 1.10 2005/06/01 16:39:59 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/ParamHandler.java,v $
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
import net.sourceforge.sqlunit.beans.Param;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * The ParamHandler takes the JDOM Element representing the param tag and 
 * converts it to a Param object.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.10 $
 * @sqlunit.parent name="call" ref="call"
 * @sqlunit.parent name="sql" ref="sql"
 * @sqlunit.parent name="paramset" ref="paramset"
 * @sqlunit.parent name="subdef" ref="subdef"
 * @sqlunit.element name="param"
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
 * @sqlunit.attrib name="scale"
 *  description="Specifies the scale of the parameter value if the value
 *  maps to a BigDecimalType. This is an optional parameter. SQLUnit will
 *  try its best to guess the scale based on the actual value, but this is
 *  useful if the value cannot be specified or is NULL."
 *  required="No"
 * @sqlunit.attrib name="typename"
 *  description="Specifies the name of a user-defined type. This is an 
 *  optional parameter."
 *  required="No"
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
public class ParamHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(ParamHandler.class);

    /**
     * Processes the JDOM Element representing the param tag returns the
     * Param object.
     * @param elParam the JDOM Element representing the param tag.
     * @return a Param object. Client needs to cast to a Param.
     * @exception Exception if something went wrong processing the param.
     */
    public final Object process(final Element elParam) throws Exception {
        LOG.debug(">> process(elParam)");
        if (elParam == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"param"});
        }
        Param param = new Param();
        param.setId(XMLUtils.getAttributeValue(elParam, "id"));
        param.setName(XMLUtils.getAttributeValue(elParam, "name"));
        param.setType(XMLUtils.getAttributeValue(elParam, "type"));
        param.setScale(XMLUtils.getAttributeValue(elParam, "scale"));
        param.setTypeName(XMLUtils.getAttributeValue(elParam, "typename"));
        param.setIsNull(XMLUtils.getAttributeValue(elParam, "is-null"));
        param.setInOut(XMLUtils.getAttributeValue(elParam, "inout"));
        param.setValue(XMLUtils.getText(elParam));
        return param;
    }
}
