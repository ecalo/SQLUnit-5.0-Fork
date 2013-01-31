// $Id: TypeDefHandler.java,v 1.1 2006/06/25 23:02:50 spal Exp $
// $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/TypeDefHandler.java,v $
package net.sourceforge.sqlunit.handlers;

import org.apache.log4j.Logger;
import org.jdom.Element;

import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.beans.TypeDef;
import net.sourceforge.sqlunit.utils.XMLUtils;

/**
 * Handles a typedef element, child of a typemap element.
 * 
 * @author Douglas Kvidera (dkvidera@users.sourceforge.net) - 05/12/06
 * @version $Revision: 1.1 $
 *
 * @sqlunit.parent name="typemap" ref="typemap"
 * @sqlunit.element name="typedef"
 *  description="The typedef tag defines a UTD type name and corresponding Java
 *  class name."
 *  syntax="(EMPTY)"
 * @sqlunit.attrib name="typename"
 *  description="name of the UDT as defined in the database"
 *  required="Yes"
 * @sqlunit.attrib name="classname"
 *  description="name of the fully-qualified Java class"
 *  required="Yes"
 * @sqlunit.example name="Defining UTD-Java class mappings."
 *  description="
 *  <typemap>{\n}
 *  {\t}{\t}<typedef typename=\"MY_SQL_OBJ\"{\n}
 *  {\t}{\t}{\t}classname=\"org.my.stuff.MyClass\" />{\n}
 *  {\t}{\t}<typedef typename=\"THEIR_SQL_OBJ\"{\n}
 *  {\t}{\t}{\t}classname=\"org.their.things.TheirClass\" />{\n}
 *  </typemap>{\n}
 *  "
 */
public class TypeDefHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(ArgHandler.class);

    /**
     * Processes the JDOM Element representing the typedef tag returns the
     * TypeDef object.
     * @param elTypeDef the JDOM Element representing the typdef tag.
     * @return a TypeDef object. Client needs to cast to a TypeDef.
     * @exception Exception if something went wrong processing the typdef.
     */
    public Object process(final Element elTypeDef) throws Exception {
        LOG.debug(">> process(elTypeDef)");
        if (elTypeDef == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"typedef"});
        }
        TypeDef typeDef = new TypeDef();
        typeDef.setTypeName(XMLUtils.getAttributeValue(elTypeDef, "typename"));
        typeDef.setClassName(XMLUtils.getAttributeValue(elTypeDef, "classname"));
        return typeDef;
    }

}
