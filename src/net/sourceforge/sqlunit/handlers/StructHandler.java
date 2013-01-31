// $Id: StructHandler.java,v 1.1 2006/06/25 23:02:50 spal Exp $
// $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/StructHandler.java,v $
package net.sourceforge.sqlunit.handlers;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.sqlunit.HandlerFactory;
import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.beans.FieldBean;
import net.sourceforge.sqlunit.beans.StructBean;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * Handles a struct element.
 * 
 * @author Douglas Kvidera (dkvidera@users.sourceforge.net) - 05/15/06
 * @version $Revision: 1.1 $
 *
 * @sqlunit.parent name="outparam" ref="outparam"
 * @sqlunit.element name="struct"
 *  description="The struct tag is used to describe a user-defined data type
 *  (UDT) object returned as a STRUCT type.  The UDT and Java Class must be 
 *  specified in a typedef tag.  The returned object is treated as a JavaBean,
 *  and the fields are the bean properties.  Contained fields MUST be given in
 *  alphabetical order by name, case-insensitive."
 *  syntax="(field)*"
 * @sqlunit.attrib name="name"
 *  description="name of the field (bean property)"
 *  required="Yes"
 * @sqlunit.child name="field"
 *  description="A field (bean property) of the returned object."
 *  required="Zero or more"
 *  ref="field"
 * @sqlunit.example name="Returning UDTs as STRUCTs."
 *  description="
 *  <outparam id=\"1\" type=\"STRUCT\">{\n}
 *  {\t}<struct>{\n}
 *  {\t}{\t}<field name=\"description\">Red rubber ball</field>{\n}
 *  {\t}{\t}<field name=\"size\">30</field>{\n}
 *  {\t}</struct>{\n}
 *  </outparam>
 *  "
 */
public class StructHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(StructHandler.class);

    /**
     * Processes the JDOM Element representing the struct tag returns the
     * Struct object.
     * @param elStruct the JDOM Element representing the struct tag.
     * @return a StructBean object. Client needs to cast to a StructBean.
     * @exception Exception if something went wrong processing the struct.
     */
    public Object process(final Element elStruct) throws Exception {
        LOG.debug(">> process(elStruct)");
        if (elStruct == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"struct"});
        }
        StructBean strBean = new StructBean();
        List elFields = elStruct.getChildren("field");
        Iterator itr = elFields.iterator();
        while (itr.hasNext()) {
            Element elField = (Element) itr.next();
            IHandler fieldHandler =
                HandlerFactory.getInstance(elField.getName());
            strBean.addField((FieldBean) fieldHandler.process(elField));
        }
        return strBean;
    }

}
