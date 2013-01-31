/*
 * $Id: CategoryHandler.java,v 1.5 2005/07/09 01:39:20 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/handlers/CategoryHandler.java,v $
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

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import org.jdom.Element;

import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.SymbolTable;

/**
 * The CategoryHandler processes the contents of a Category tag in the
 * input SQLUnit XML file. It decides whether a test falls into a user-defined
 * category, which we will call the global category. The decision is made
 * following these rules:
 * <ol>
 *   <li/>If the current test's category tags is not present or is empty,
 *   the test matches and is executed;
 *   <li/>If the global category is not set, the test matches and is executed;
 *   <li/>The contents of the current test's category tag is parsed as a comma
 *   separated list and a check is made whether the global category matches
 *   (in the terms of regular expession matching) at least one element of that
 *   list. If it matches, the test then falls in the given global category.
 * </ol>
 * The global category is provided in one of the following ways, sorted by
 * priority:
 * <ul>
 *   <li/>As an ant property, called sqlunit.category;
 *   <li/>As the value of the key ${sqlunit.property} from the SymbolTable;
 *   <li/>As a System property named sqlunit.category;
 * </ul>
 * @author Ivan Ivanov
 * @version $Revision: 1.5 $
 * @sqlunit.parent name="classifiers" ref="classifiers"
 * @sqlunit.element name="category"
 *  description="Allows caller to group a test by category"
 *  syntax="(BODY)"
 */
public class CategoryHandler implements IHandler {

    private static Logger LOG = Logger.getLogger(CategoryHandler.class);

    private static String CATEGORY_PROPERTY = "sqlunit.category";

    /**
     * Processes the JDOM Element representing a Category Declaration.
     * @param elCategory the JDOM Element to use.
     * @return a Boolean.TRUE if Category matches the test category,
     * else Boolean.FALSE. Caller must cast appropriately. If category
     * is not specified globally, then it is ignored.
     * @exception if there was a problem handling the category element.
     */
    public Object process(Element elCategory) throws Exception {
        LOG.debug(">> process(elSeverity)");
        List content = elCategory.getContent();
        if (content.isEmpty()) {
            return Boolean.TRUE;
        }
        String category = getCategory();
        // if no category specified, do not match
        if (category == null || category.trim().length() == 0) {
            return Boolean.TRUE;
        }
        String[] categories;
        String text = elCategory.getTextTrim();
        // if no category text, do not check
        if ("".equals(text)) {
            return Boolean.TRUE;
        }
        if (text.indexOf(",") > -1) {
            // comma-separated list, this test is in multiple categories
            categories = text.split("\\s*,\\s*");
        } else {
            // this test is in a single category
            categories = new String[1];
            categories[0] = text;
        }
        // if at least one category has matched, then we need to return TRUE
        Pattern p = Pattern.compile(category);
        boolean isCategoryMatched = false;
        for (int i = 0; i < categories.length; i++) {
            Matcher m = p.matcher(categories[i]);
            if (m.matches()) {
                isCategoryMatched = true;
                break;
            }
        }
        return new Boolean(isCategoryMatched);
    }

    /**
     * Get the category specified for the test from Ant properties, the
     * Symbol table or from System properties, in that order.
     * @return the global category string.
     */
    private String getCategory() {
        String category = SymbolTable.getValue(
                "${ant." + CATEGORY_PROPERTY + "}");
        if (category == null) {
            category = SymbolTable.getValue(
                    "${" + CATEGORY_PROPERTY + "}");
        }
        if (category == null) {
            category = System.getProperty(CATEGORY_PROPERTY);
        }
        return category;
    }
}
