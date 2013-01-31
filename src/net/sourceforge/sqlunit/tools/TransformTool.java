/*
 * $Id: TransformTool.java,v 1.2 2006/04/30 22:25:56 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/tools/TransformTool.java,v $
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
package net.sourceforge.sqlunit.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;

/**
 * The TransformTool is a XSL Transformer that converts XML files containing
 * some or all of the information required to build a SQLUnit test file using
 * a provided XSL stylesheet. The program is called thus:
 *   java net.sourceforge.sqlunit.tools.TransformTool \
 *       --input=/your/input/xml/file.xml \
 *       --transform=/your/stylesheet/file.xsl \
 *       --output=/your/output/sqlunit/xml/file.xml
 * This program was based in large part on the information provided at these
 * websites:
 *   http://www-106.ibm.com/developerworks/java/library/x-tipjdom.html
 * These websites provide a quick primer and a case study on building and
 * using stylesheets and XSLT.
 *   http://www.brics.dk/~amoeller/XML/xslt-4.1.html
 *   http://www.scit.wlv.ac.uk/~jphb/xml/xmlcsv.html
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.2 $
 */
public class TransformTool {

    private static String LF = System.getProperty("line.separator");
    private String copyright = null;
    private String usage = null;
    private String inputFileName = null;
    private String outputFileName = null;
    private String transformFileName = null;
    
    /**
     * This is how we are called.
     * @param argv a String array of arguments.
     */
    public static void main(String[] argv) throws Exception {
        TransformTool tool = new TransformTool();
        tool.print(tool.copyright());
        if (argv.length != 3) {
            tool.print(tool.usage());
            return;
        } else {
            Map argMap = new HashMap();
            for (int i = 0; i < argv.length; i++) {
                String[] nvp = argv[i].split("=");
                argMap.put(nvp[0], nvp[1]);
            }
            tool.setArguments(argMap);
        }
        tool.transform();
    }
    
    /**
     * Prints the specified string to standard out. The string is printed
     * as-is, ie, not terminated automatically by newline.
     * @param s the String to print.
     */
    private void print(String s) {
        System.out.print(s);
    }
    
    /**
     * Generates the copyright notice for the tool.
     * @return the copyright notice.
     */
    private String copyright() {
        if (copyright == null) {
            StringBuffer buf = new StringBuffer();
            buf.append("SQLUnit Transform Tool").append(LF).
                append("Copyright(c) 2005, The SQLUnit Team").
                append(LF);
            this.copyright = buf.toString();
        }
        return this.copyright;
    }
    
    /**
     * Prints the usage for the tool.
     * @return the usage string.
     */
    private String usage() {
        if (usage == null) {
            StringBuffer buf = new StringBuffer();
            buf.append("Usage: ").
                append("java net.sourceforge.sqlunit.tools.TransformTool").
                append(LF).append("\t").
                append(" --input=").append("your input xml file").
                append(LF).append("\t").
                append(" --transform=").append("your xsl stylesheet file").
                append(LF).append("\t").
                append(" --output=").append("your sqlunit output xml file").
                append(LF);
            this.usage = buf.toString();
        }
        return usage;
    }
    
    /**
     * Get the arguments into private member variables.
     * @param args
     * @throws IllegalArgumentException
     */
    private void setArguments(Map args) throws IllegalArgumentException {
        this.inputFileName = (String) args.get("--input");
        if (this.inputFileName == null
                || this.inputFileName.trim().length() == 0) {
            throw new IllegalArgumentException("Input file must be specified");
        }
        this.transformFileName = (String) args.get("--transform");
        if (this.transformFileName == null
                || this.transformFileName.trim().length() == 0) {
            throw new IllegalArgumentException("Transform must be specified");
        }
        this.outputFileName = (String) args.get("--output");
        if (this.outputFileName == null
                || this.outputFileName.trim().length() == 0) {
            throw new IllegalArgumentException("Output file must be specified");
        }
    }

    /**
     * Does the transformation using the XML in the input file and XSL in
     * the transform file to produce some XML in the output file.
     * @throws Exception
     */
    private void transform() throws Exception {
        // create JDOM source from input XML file
        SAXBuilder builder = new SAXBuilder();
        Document idoc = builder.build(this.inputFileName);
        JDOMSource isource = new JDOMSource(idoc);
        // Build the transform
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Templates tdoc = tfactory.newTemplates(new StreamSource(
            new File(this.transformFileName)));
        Transformer transformer = tdoc.newTransformer();
        // Build the JDOM result from output SQLUnit test XML file
        JDOMResult oresult = new JDOMResult();
        // Transform
        transformer.transform(isource, oresult);
        // get the JDOM Document of the result and output it
        Document odoc = oresult.getDocument();
        odoc.setDocType(new DocType("sqlunit", "file:docs/sqlunit.dtd"));
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        outputter.output(odoc, new FileOutputStream(this.outputFileName));
    }
}
