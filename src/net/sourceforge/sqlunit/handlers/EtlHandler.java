package net.sourceforge.sqlunit.handlers;

import com.csutherl.batt.BATT;
import net.sourceforge.sqlunit.IErrorCodes;
import net.sourceforge.sqlunit.IHandler;
import net.sourceforge.sqlunit.SQLUnitException;
import net.sourceforge.sqlunit.SymbolTable;
import net.sourceforge.sqlunit.utils.XMLUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * The SqlHandler class processes the contents of an sql tag in the input XML
 * file.
 *
 * @author Coty Sutherland (sutherland.coty at gmail.com)
 * @sqlunit.parent name="setup" ref="setup"
 * @sqlunit.parent name="teardown" ref="teardown"
 * @sqlunit.element name="etl" description="The etl tag describes a ETL call
 * with replaceable parameters. It is internally passed to the CLI tool that
 * launches jobs. ETL specified by the etl tag can return a result of pass or
 * failure." syntax=""
 * @sqlunit.attrib name="name" description="The name the ETL to call"
 * required="Yes"
 * @sqlunit.attrib tool="tool" description="The name the tool that the ETL call
 * should go to" required="Yes"
 * @sqlunit.attrib objectType="objectType" description="The type of the ETL
 * being executed" required="Yes"
 * @sqlunit.attrib guid="guid" description="The guid of the ETL
 * object being executed" required="Yes"
 * @sqlunit.example name="Example of a simple ETL tag" description=" <etl
 * name="test_job" tool="test_tool" objectType="test_type" guid="abcdef">{\n} "
 */
public class EtlHandler implements IHandler {

    private static final Logger LOG = Logger.getLogger(EtlHandler.class);

    /**
     * Runs the SQL statement contained in the sql tag in the input XML file.
     *
     * @param elSql the JDOM Element representing the sql tag.
     * @return a Object created as a result of running the ETL.
     * @exception Exception if there was a problem running the SQL.
     */
    public Object process(final Element elEtl) throws Exception {
        LOG.debug(">> process(elEtl)");
        if (elEtl == null) {
            throw new SQLUnitException(IErrorCodes.ELEMENT_IS_NULL,
                    new String[]{"etl"});
        }

        String etlName = XMLUtils.getAttributeValue(elEtl, "name");
        String etlTool = XMLUtils.getAttributeValue(elEtl, "tool");
        String etlObjectType = XMLUtils.getAttributeValue(elEtl, "objectType");
        String etlGuid = XMLUtils.getAttributeValue(elEtl, "guid");

        return executeETL(etlName, etlObjectType, etlTool, etlGuid);
    }

    /**
     * Executes ETL calls to run the specified ETL and returns it.
     *
     * @param object the name of the ETL object.
     * @param tool the tool being used.
     * @param objectType the type of object being executed.
     * @return an object.
     * @exception Exception if there was a problem building the result object or
     * running the ETL.
     */
    protected Object executeETL(final String object, final String objectType,
            final String tool, final String guid) throws Exception {
        LOG.debug("executeETL(" + object + "," + objectType + "," + tool + "," + guid + ")");

        System.out.println("Executing ETL...");

        try {
            String args_list = "--launch "
		    + " --object " + object
                    + " --objectType " + objectType
                    + " --tool " + tool
                    + " --guid " + guid
                    + " --no-out";
	    
            String[] args = args_list.split(" ");
            BATT batt = new BATT();
            batt.start(args);
            
            // apparently this try/catch doesn't work :) we still want to exit.
        } catch (Exception e) {
            if (e instanceof SQLUnitException) {
                SymbolTable.setObject(SymbolTable.FAILURE_MESSAGE_OBJ, e);
                throw e;
            } else {
                throw new SQLUnitException(IErrorCodes.GENERIC_ERROR,
                        new String[]{"System", e.getClass().getName(),
                            e.getMessage()}, e);
            }
        }
        
        System.out.println("ETL executed successfully!");
        
        return null;
    }
}
