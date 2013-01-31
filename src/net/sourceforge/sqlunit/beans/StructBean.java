// $Id: StructBean.java,v 1.1 2006/06/25 23:02:50 spal Exp $
// $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/beans/StructBean.java,v $
package net.sourceforge.sqlunit.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.sqlunit.handlers.ArgHandler;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * Models a struct element.
 * 
 * @author Douglas Kvidera (dkvidera@users.sourceforge.net) - 05/15/06
 * @version $Revision: 1.1 $
 *
 */
public class StructBean {
    
    private static final Logger LOG = Logger.getLogger(ArgHandler.class);
    
    private static Map typeMap;

    private ArrayList fields = new ArrayList();

    /**
     * Used to sort PropertyDescriptor array by property name, case-insensitive.
     */
    private class PropertyComparator implements Comparator {
        
        public int compare(final Object obj1, final Object obj2) {
            return ((PropertyDescriptor) obj1).getName().
                compareToIgnoreCase(((PropertyDescriptor) obj2).getName());
        }
    }

    /**
     * No-param constructor.
     */
    public StructBean() {
        // No-parm constructor for constructing from XML file parsing
    }
    
    /**
     * Creates a struct and contained field objects by reading parameter as
     * a JavaBean.  Properties for static Class getClass() and mandatory
     * String SQLData.getSQLTypeName() are ignored.
     * @param obj Any object; fields will be JavaBean properties.
     */
    public StructBean(final Object obj) {
        try {
            PropertyDescriptor[] props =
                PropertyUtils.getPropertyDescriptors(obj);
            Arrays.sort(props, new PropertyComparator());
            for (int i = 0; i < props.length; i++) {
                if (!"SQLTypeName".equals(props[i].getName())
                        && !"class".equals(props[i].getName())) {
                    String fieldName = props[i].getName();
                    Method readMethod = props[i].getReadMethod();
                    if (readMethod != null) {
                        Object fieldValue = readMethod.invoke(obj, new Object[0]);
                        FieldBean fBean;
                        if (typeMap.containsValue(props[i].getPropertyType())) {
                            // Field is a nested struct
                            fBean = new FieldBean(fieldName, fieldValue);
                        } else {
                            // Field is a simple type
                            fBean = new FieldBean(fieldName,
                                (fieldValue == null) ? "NULL"
                                    : fieldValue.toString());
                        }
                        fields.add(fBean);
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error("Could not read bean: " + obj.toString(), ex);
        }
    }

    /**
     * Sets a mapping to determine if field is a nested struct.
     * Map is obtained from java.sql.Connection.getTypeMap(), with keys being
     * UTD type names (String) and values being Class objects.
     * @param map Map containing Class objects as values
     */
    public static void setTypeMap(final Map map) {
        typeMap = map;
    }

    /**
     * Adds FieldBean to end of contained list
     * @param fBean FieldBean to add
     */
    public void addField(final FieldBean fBean) {
        fields.add(fBean);
    }
    
    /**
     * Adds FieldBean to end of contained list at given index
     * @param index index where FieldBean should go
     * @param fBean FieldBean to add
     */
    public void addField(final int index, final FieldBean fBean) {
        fields.add(index, fBean);
    }
    
    /**
     * Returns a JDOM Element representing this Struct object.
     * @return a JDOM Element representing this Struct object.
     */
    public final Element toElement() {
        Element elStruct = new Element("struct");
        Iterator itr = fields.iterator();
        while (itr.hasNext()) {
            Element elField = ((FieldBean) itr.next()).toElement();
            elStruct.addContent(elField);
        }
        return elStruct;
    }

    /**
     * Returns the string representation of this object.
     * @return the String representation of this object.
     */
    public final String toString() {
        StringBuffer buffer = new StringBuffer();
        Iterator itr = fields.iterator();
        while (itr.hasNext()) {
            FieldBean fBean = (FieldBean) itr.next();
            buffer.append(fBean.toString()).append('\n');
        }
        return buffer.toString();
    }
}
