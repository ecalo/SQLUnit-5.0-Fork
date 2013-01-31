/*
 * $Id: OutParamHandler.java,v 1.6 2006/06/25 23:02:50 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/OutParamHandler.java,v $
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

import net.sourceforge.sqlunit.HandlerFactory;
import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.beans.OutParam;
import net.sourceforge.sqlunit.beans.ResultSetBean;
import net.sourceforge.sqlunit.beans.StructBean;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * The OutParamHandler takes the JDOM Element representing the outparam tag 
 * and converts it to a OutParam object.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.6 $
 * @sqlunit.parent name="result" ref="result"
 * @sqlunit.element name="outparam"
 *  description="The outparam tag is used to represent OUT parameters 
 *  returned as a result of a Stored procedure call. The value is 
 *  stored in the body text as a String or as a embedded resultset
 *  element in case of CURSOR type OUT parameters."
 *  syntax="(BODY)|(resultset)|(struct)"
 * @sqlunit.attrib name="id"
 *  description="The param-id of the outparam parameter."
 *  required="Yes"
 * @sqlunit.attrib name="name"
 *  description="The name of the outparam parameter. This is mainly for 
 *  readability to help stored procedure authors/testers to spot errors 
 *  quickly."
 *  required="No"
 * @sqlunit.attrib name="type"
 *  description="Specifies the type name of the parameter."
 *  required="Yes"
 * @sqlunit.child name="resultset"
 *  description="This is needed only when the type of the OUT parameter
 *  is a CURSOR, and specifies the value of the CURSOR outparam."
 *  required="Not if the type is not CURSOR"
 *  ref="resultset"
 * @sqlunit.example name="A outparam with a INTEGER value"
 *  description="
 *  <outparam id=\"1\" type=\"INTEGER\">24</outparam>
 *  "
 * @sqlunit.example name="A outparam tag with an embedded CURSOR"
 *  description="
 *  <outparam id=\"1\" type=\"oracle.CURSOR\">{\n}
 *  {\t}<resultset id=\"1\">{\n}
 *  {\t}{\t}<row id=\"1\">{\n}
 *  {\t}{\t}{\t}<col id=\"1\" type=\"INTEGER\">7</col>{\n}
 *  {\t}{\t}{\t}<col id=\"2\" type=\"VARCHAR\">James Bond</col>{\n}
 *  {\t}{\t}{\t}<col id=\"3\" type=\"VARCHAR\">Martini</col>{\n}
 *  {\t}{\t}</row>{\n}
 *  {\t}</resultset>{\n}
 *  </outparam>
 *  "
 */
public class OutParamHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(OutParamHandler.class);

    /**
     * Processes the JDOM Element representing the outparam tag returns the
     * OutParam object.
     * @param elOutParam the JDOM Element representing the outparam tag.
     * @return a OutParam object. Client needs to cast to a OutParam.
     * @exception Exception if something went wrong processing.
     */
    public final Object process(final Element elOutParam) throws Exception {
        LOG.debug(">> process(elOutParam)");
        if (elOutParam == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"param"});
        }
        OutParam op = new OutParam();
        op.setId(XMLUtils.getAttributeValue(elOutParam, "id"));
        op.setName(XMLUtils.getAttributeValue(elOutParam, "name"));
        op.setType(XMLUtils.getAttributeValue(elOutParam, "type"));
        Element elResultSet = elOutParam.getChild("resultset");
        Element elStruct = elOutParam.getChild("struct");
        if (elResultSet != null) {
            // contains a resultset bean
            if (!op.getType().endsWith("CURSOR")) {
                throw new SQLUnitException(IErrorCodes.IS_A_CURSOR,
                    new String[] {op.getId(), op.getType()});
            }
            IHandler resultSetHandler = 
                HandlerFactory.getInstance(elResultSet.getName());
            ResultSetBean rsb = 
                (ResultSetBean) resultSetHandler.process(elResultSet);
            op.setValue(rsb);
        } else if (elStruct != null) {
            if (!op.getType().endsWith("STRUCT")) {
                throw new SQLUnitException(IErrorCodes.IS_A_STRUCT,
                    new String[] {op.getId(), op.getType()});
            }
            IHandler structHandler = 
                HandlerFactory.getInstance(elStruct.getName());
            StructBean sb = 
                (StructBean) structHandler.process(elStruct);
            op.setValue(sb);
        } else {
            // contains a String
            op.setValue(XMLUtils.getText(elOutParam));
        }
        return op;
    }
}
