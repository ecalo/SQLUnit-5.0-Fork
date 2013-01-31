// $Id: FieldHandler.java,v 1.1 2006/06/25 23:02:50 spal Exp $
// $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/FieldHandler.java,v $
package net.sourceforge.sqlunit.handlers;

import net.sourceforge.sqlunit.HandlerFactory;
import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.beans.FieldBean;
import net.sourceforge.sqlunit.beans.StructBean;
import net.sourceforge.sqlunit.utils.XMLUtils;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * Handles a field element, child of a struct element.
 * 
 * @author Douglas Kvidera (dkvidera@users.sourceforge.net) - 05/15/06
 * @version $Revision: 1.1 $
 *
 * @sqlunit.parent name="struct" ref="struct"
 * @sqlunit.element name="field"
 *  description="The field tag is used to describe a field of a user-defined
 *  data type (UDT) object returned as a STRUCT type.  The returned object is
 *  treated as a JavaBean, and the fields are the bean properties.  Nested UDT
 *  objects can be specified using a struct tag as the body of the field.  
 *  Fields with null values have NULL as their body."
 *  syntax="(BODY)|(struct)"
 * @sqlunit.attrib name="name"
 *  description="name of the field/(bean property)"
 *  required="Yes"
 * @sqlunit.child name="struct"
 *  description="A field whose type is another UDT instead of a 'simple' type is
 *  specified by a nested struct tag.  Like top-level UDTs, the type  and class
 *  must be specified in a typedef tag."
 *  required="Zero or one"
 *  ref="struct"
 * @sqlunit.example name="Returning nested UDTs as STRUCTs."
 *  description="
 *  <struct>{\n}
 *  {\t}<field name=\"capacity\">30</field>{\n}
 *  {\t}<field name=\"handler\">
 *  {\t}{\t}<struct>{\n}
 *  {\t}{\t}{\t}<field name=\"name\">AEF Gripper</field>{\n}
 *  {\t}{\t}{\t}<field name=\"workLoad\">12.5</field>{\n}
 *  {\t}{\t}</struct>{\n}
 *  {\t}</field>{\n}
 *  {\t}<field name=\"storage\">NULL</field>{\n}
 *  </struct>{\n}
 *  "
 */
public class FieldHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(ArgHandler.class);

    /**
     * Processes the JDOM Element representing the field tag returns the
     * Field object.
     * @param elField the JDOM Element representing the field tag.
     * @return a FieldBean object. Client needs to cast to a FieldBean.
     * @exception Exception if something went wrong processing the field.
     */
    public Object process(final Element elField) throws Exception {
        LOG.debug(">> process(elField)");
        if (elField == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"field"});
        }
        String name = XMLUtils.getAttributeValue(elField, "name");
        FieldBean fBean;
        Element elStruct = elField.getChild("struct");
        if (elStruct != null) {
            IHandler structHandler =
                HandlerFactory.getInstance(elStruct.getName());
            StructBean strBean = (StructBean) structHandler.process(elStruct);
            fBean = new FieldBean(name, strBean);
        } else {
            String value = XMLUtils.getText(elField);
            fBean = new FieldBean(name, value);
        }
        return fBean;
    }

}
