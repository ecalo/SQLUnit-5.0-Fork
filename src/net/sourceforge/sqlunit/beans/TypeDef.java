// $Id: TypeDef.java,v 1.1 2006/06/25 23:02:50 spal Exp $
// $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/beans/TypeDef.java,v $
package net.sourceforge.sqlunit.beans;

/**
 * Models a typedef element, child of a typemap element.
 * 
 * @author Douglas Kvidera (dkvidera@users.sourceforge.net) - 05/15/06
 * @version $Revision: 1.1 $
 *
 */
public class TypeDef {

    private String className;
    private String typeName;

    /**
     * Returns the Java class name.
     * @return the Java class name.
     */
    public final String getClassName() { return className; }

    /**
     * Sets the Java class name.
     * @param name the Java class name.
     */
    public final void setClassName(final String name) { this.className = name; }

    /**
     * Returns the database UDT type.
     * @return the database UDT type.
     */
    public final String getTypeName() { return typeName; }

    /**
     * Sets the database UDT type.
     * @param name the database UDT type name.
     */
    public final void setTypeName(final String name) { this.typeName = name; }
}
