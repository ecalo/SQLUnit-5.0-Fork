// $Id: FieldBean.java,v 1.1 2006/06/25 23:02:50 spal Exp $
// $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/beans/FieldBean.java,v $
package net.sourceforge.sqlunit.beans;

import org.jdom.Element;

/**
 * Models a field element.
 * 
 * @author Douglas Kvidera (dkvidera@users.sourceforge.net) - 05/15/06
 * @version $Revision: 1.1 $
 *
 */
public class FieldBean {
    
    private String name;
    private Object value;

    /**
     * Constructs bean with given name and value.
     * If the given value is not null and not a String, it will be converted
     * to a StructBean.
     * @param name field name.
     * @param value String or other object; can be null.
     */
    public FieldBean(final String name, final Object value) {
        this.name = name;
        if ((value == null) || (value instanceof String)
                || (value instanceof StructBean)) {
            this.value = value;
        } else {
            this.value = new StructBean(value);
        }
    }

    /**
     * Returns name of field.
     * @return field name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of field.
     * @param name field name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns value of field.
     * @return field value; can be null.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets value of field.
     * @param value field value.
     */
    public void setValue(final Object value) {
        this.value = value;
    }

    /**
     * Returns a JDOM Element representing this Field object.
     * @return a JDOM Element representing this Field object.
     */
    public final Element toElement() {
        Element elField = new Element("field");
        elField.setAttribute("name", getName());
        if (getValue() instanceof StructBean) {
            elField.addContent(((StructBean) getValue()).toElement());
        } else {
            elField.addContent((getValue() == null) ? "NULL"
                : getValue().toString());
        }
        return elField;
    }

    /**
     * Returns the string representation of this object.
     * @return the String representation of this object.
     */
    public final String toString() {
        StringBuffer buffer = new StringBuffer(getName()).append(':');
        if (getValue() instanceof StructBean) {
            buffer.append('{').append(getValue().toString()).append('}');
        } else {
            buffer.append((getValue() == null) ? "NULL"
                : getValue().toString());
        }
        
        return buffer.toString();
    }
}
