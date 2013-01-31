/*
 * $Id: SymbolParserTest.java,v 1.1 2005/01/21 06:16:01 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/test/java/SymbolParserTest.java,v $
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
package net.sourceforge.sqlunit.test;

import net.sourceforge.sqlunit.parsers.ParseException;
import net.sourceforge.sqlunit.parsers.SymbolParser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashMap;
import java.util.Map;

/**
 * Runs some tests of the Symbol Parser.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.1 $
 */
public class SymbolParserTest extends TestCase {

    private static final boolean DEBUG = true;
    /* This is the string all parsers will resolve to */
    private static final String TARGET = 
        "The quick brown fox jumped over the lazy dog";

    /**
     * Boilerplate main method.
     * @param argv the arguments (none).
     */
    public static void main(final String[] argv) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Boilerplate suite() method.
     * @return a Test object.
     */
    public static Test suite() {
        return new TestSuite(SymbolParserTest.class);
    }

    /**
     * Boilerplate constructor.
     * @param name the name of this test class.
     */
    public SymbolParserTest(final String name) {
        super(name);
    }

    // tests begin here

    /**
     * Testing full substitution. Values for all variables referenced in 
     * the source must be supplied.
     * @exception Exception if one is thrown.
     */
    public void testFullSubstitution() throws Exception {
        Map map = new HashMap();
        map.put("${quick}", "quick");
        map.put("${brown}", "brown");
        map.put("${fox}", "fox");
        map.put("${lazy}", "lazy");
        map.put("${dog}", "dog");
        String source = 
            "The ${quick} ${brown} ${fox} jumped over the ${lazy} ${dog}";
        String target = SymbolParser.parse(source, map, false);
        assertEquals("Full variable substitution failed",
            "The quick brown fox jumped over the lazy dog", target);
    }

    /**
     * Testing partial substitution. Not all values for variables referenced
     * in source need to be provided. This can allow a partially parsed
     * string to be parsed once again when there are more variables available
     * in the symbol table.
     * @exception Exception if one is thrown.
     */
    public void testPartialSubstitution() throws Exception {
        Map map = new HashMap();
        map.put("${quick}", "quick");
        map.put("${brown}", "brown");
        map.put("${fox}", "fox");
        map.put("${lazy}", "lazy");
        String source = 
            "The ${quick} ${brown} ${fox} jumped over the ${lazy} ${dog}";
        String target = SymbolParser.parse(source, map, true);
        assertEquals("Partial variable substitution failed",
            "The quick brown fox jumped over the lazy ${dog}", target);
    }

    /**
     * Testing full substitution when some variables are not supplied. This
     * should throw a ParseException with the appropriate error message.
     * @exception Exception if one is thrown.
     */
    public void testPartialSubstitutionException() throws Exception {
        Map map = new HashMap();
        map.put("${quick}", "quick");
        map.put("${brown}", "brown");
        map.put("${fox}", "fox");
        map.put("${lazy}", "lazy");
        String source = 
            "The ${quick} ${brown} ${fox} jumped over the ${lazy} ${dog}";
        try {
            String target = SymbolParser.parse(source, map, false);
            fail("Did not throw ParseException for partial substitution");
        } catch (ParseException e) {
            assertEquals("Bad error message",
                "Variable ${dog} not defined in SymbolTable", e.getMessage());
        }
    }
}
