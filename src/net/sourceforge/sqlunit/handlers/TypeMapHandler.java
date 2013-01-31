// $Id: TypeMapHandler.java,v 1.1 2006/06/25 23:02:50 spal Exp $
// $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/TypeMapHandler.java,v $
package net.sourceforge.sqlunit.handlers;

import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.SQLUnitException;

import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * Handles a typemap element, child of a typemap element.
 * 
 * @author Douglas Kvidera (dkvidera@users.sourceforge.net) - 05/12/06
 * @version $Revision: 1.1 $
 *
 * @sqlunit.parent name="connection" ref="connection"
 * @sqlunit.element name="typemap"
 *  description="The typemap tag defines mappings of user-defined data types
 *  (UDTs) to their corresponding Java classes.  These mappings are required for
 *  handling all STRUCT type values in the tests that use this connection.  This
 *  includes nested UDT objects."
 *  syntax="(typedef)*"
 * @sqlunit.child name="typedef"
 *  description="A UTD type name and corresponding Java class name."
 *  required="Zero or more"
 *  ref="typedef"
 * @sqlunit.example name="Defining mappings from UTDs to Java classes."
 *  description="
 *  <connection>{\n}
 *  {\t}<typemap>{\n}
 *  {\t}{\t}<typedef typename=\"MY_SQL_OBJ\"{\n}
 *  {\t}{\t}{\t}classname=\"org.my.stuff.MyClass\" />{\n}
 *  {\t}</typemap>{\n}
 *  </connection>{\n}
 *  "
 */
public class TypeMapHandler extends ConstructorArgsHandler {

    private static final Logger LOG = Logger.getLogger(ArgHandler.class);

    /**
     * Processes the JDOM Element representing the typemap tag returns an
     * array of TypeDef objects.
     * @param elTypeMap the JDOM Element representing the typemap tag.
     * @return array of TypeDef objects.
     * @exception Exception if something went wrong processing the typemap.
     */
    public Object process(final Element elTypeMap) throws Exception {
        LOG.debug(">> process(elTypeMap)");
        if (elTypeMap == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                new String[] {"typemap"});
        }
        return getArguments(elTypeMap.getChildren());
    }

}
