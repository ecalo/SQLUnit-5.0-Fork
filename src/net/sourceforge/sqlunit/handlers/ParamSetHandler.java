/*
 * $Id: ParamSetHandler.java,v 1.4 2004/09/28 19:15:45 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/ParamSetHandler.java,v $
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
import net.sourceforge.sqlunit.beans.Param;

import org.apache.log4j.Logger;
import org.jdom.Element;

import java.util.List;

/**
 * The ParamSetHandler takes the JDOM Element representing the paramset 
 * tag and converts it to an array of Param objects.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.4 $
 * @sqlunit.parent name="batchcall" ref="batchcall"
 * @sqlunit.element name="paramset"
 *  description="Wrapper tag for one or more param elements."
 *  syntax="((param)+)"
 * @sqlunit.attrib name="id"
 *  description="Specifies the id of the paramset element."
 *  required="Yes"
 * @sqlunit.child name="param"
 *  description="Specifies argument names and values to be passed into
 *  a batchcall call."
 *  required="One or more"
 *  ref="param"
 * @sqlunit.example name="A paramset passed into an INSERT batchcall"
 *  description="
 *  <paramset id=\"1\">{\n}
 *  {\t}<param id=\"1\" type=\"INTEGER\">1</param>{\n}
 *  {\t}<param id=\"2\" type=\"VARCHAR\">Some One</param>{\n}
 *  </paramset>
 *  "
 */
public class ParamSetHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(ParamSetHandler.class);

    /**
     * Processes the JDOM Element representing the paramset tag and 
     * returns an array of Param objects.
     * @param elParamSet the JDOM Element representing the paramset tag.
     * @return an array of Param objects. Client needs to cast to Param[].
     * @exception Exception if something went wrong processing the paramset.
     */
    public final Object process(final Element elParamSet) throws Exception {
        LOG.debug(">> process(elParamSet)");
        if (elParamSet == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"paramset"});
        }
        List elParamList = elParamSet.getChildren("param");
        int numParams = elParamList.size();
        Param[] params = new Param[numParams];
        for (int i = 0; i < numParams; i++) {
            Element elParam = (Element) elParamList.get(i);
            IHandler paramHandler = 
                HandlerFactory.getInstance(elParam.getName());
            params[i] = (Param) paramHandler.process(elParam);
        }
        return params;
    }
}
