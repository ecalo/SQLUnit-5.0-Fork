/*
 * $Id: IErrorCodes.java,v 1.43 2006/06/25 23:02:51 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/IErrorCodes.java,v $
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
package net.sourceforge.sqlunit;

/**
 * This interface lists the generic error codes that are raised by SQLUnit.
 * The documentation explains each of these errors in detail, including 
 * what corrective action to take.
 * @author Sujit Pal (spal@users.sourceforge.net)
 * @version $Revision: 1.43 $
 */
public interface IErrorCodes {

    /**
     * Returns the line feed character for the current system.
     */
    String LF = System.getProperty("line.separator");

    // ------------------ Input validation errors ----------------------------

    /**
     * NO_TESTFILE.
     * @sqlunit.error name="Input Test File not specified, specify testfile
     *  or fileset. {usage}"
     *  description="Neither the test file or a fileset (in the sqlunit Ant
     *  task) was specified to SQLUnit. This is a usage issue and the usage
     *  message that follows will indicate how to supply the XML file to 
     *  SQLUnit"
     */
    String NO_TESTFILE =
        "Input Test File not specified, specify testfile or fileset."
        + LF + "{0}";

    /**
     * TESTFILE_NOT_FOUND.
     * @sqlunit.error name="Test File {filename} not found. {usage}"
     *  description="The test file specified could not be found by SQLUnit.
     *  Check to see if the test file exists as specified on your operating
     *  system"
     */
    String TESTFILE_NOT_FOUND =
        "Cannot find Test File {0}" + LF + "{1}";

    /**
     * DUPLICATE_TESTFILE.
     * @sqlunit.error name="Input Test File specified twice, specify either
     *  file or fileset"
     *  description="The test file name was specified both in the testfile
     *  attribute as well as the fileset nested element. Use one or the other,
     *  not both"
     */
    String DUPLICATE_TESTFILE = 
        "Input Test File specified twice, specify either testfile or fileset";

    /**
     * TESTFILE_CANT_READ.
     * @sqlunit.error name="Cannot read Test File {filename}"
     *  description="SQLUnit does not have sufficient permissions to read
     *  the specified test file. Fix the permissions on the file at the
     *  operating system level"
     */
    String TESTFILE_CANT_READ = 
        "Cannot read Test File {0}";

    /**
     * CANT_BUILD_CONNECTION.
     * @sqlunit.error name="Cannot build Connection (id={id}) because {reason}"
     *  description="SQLUnit could not build the Connection object with the
     *  properties supplied. Verify that the properties for building the 
     *  Connection are valid."
     */
    String CANT_BUILD_CONNECTION = 
        "Cannot build Connection (connection-id={0}) because {1}";

    /**
     * MISSING_RESOURCE.
     * @sqlunit.error name="{resource} not found in specified Context"
     *  description="SQLUnit could not find the named class or resource
     *  in the specified Context. The Context could be the system classpath,
     *  the path specified by the jarfile-url attribute if that is specified,
     *  or the Naming Context if Connection is being looked up from a JNDI
     *  server."
     */
    String MISSING_RESOURCE =
        "{0} not found in specified Context";

    /**
     * NO_INCLUDE_FILE.
     * @sqlunit.error name="Included file {filename} not found"
     *  description="The include file specified in the include tag in
     *  either the setup or teardown tags could not be found. Check to 
     *  see if the file exists at the specified location on your operating
     *  system."
     */
    String NO_INCLUDE_FILE = 
        "Included file {0} not found";

    /**
     * ELEMENT_IS_NULL.
     * @sqlunit.error name="System Error: Element {element} is null"
     *  description="This is a system error and means that there is a 
     *  problem with the code. Log a bug against the SQLUnit project 
     *  with details on how to reproduce it."
     */
    String ELEMENT_IS_NULL = 
        "System Error: Element <{0}> is NULL";

    /**
     * CONNECTION_IS_NULL.
     * @sqlunit.error name="System Error: Connection {id} is null or 
     *  incorrectly specified"
     *  description="This is a system error and means that there is a problem
     *  with the code. Log a bug against the SQLUnit project with details on
     *  how to reproduce it."
     */
    String CONNECTION_IS_NULL = 
        "Connection({0}) is NULL or incorrectly specified";

    /**
     * MATCHER_EXCEPTION.
     * @sqlunit.error name="Match Exception in class: {classname}, 
     *  message: {exception_message}"
     *  description="This error message may be encountered when using a 
     *  User-defined matcher class in conjunction with the diff tag. A 
     *  Matcher class always throws this type of exception if it did not
     *  get the inputs it expected or if it encounters an unexpected 
     *  exception at runtime. The exception_message provides more 
     *  information as to what the problem is. Usually, it can be fixed
     *  by passign arguments correctly to the Matcher class."
     */
    String MATCHER_EXCEPTION = 
        "Match Exception in class: {0}, message: {1}";

    /**
     * MATCH_PATTERN_EXCEPTION.
     * @sqlunit.error name="Cannot parse match pattern: {pattern}"
     *  description="The values for the resultset-id, row-id and col-id
     *  attributes to the Match tag can either be specified as exact 
     *  numbers, a comma-separated enumeration or a range. It can also
     *  be omitted altogether or be specified as a *, both of which
     *  imply not to match against the particular filter. The error 
     *  message means that the pattern was incorrectly specified or 
     *  could not be parsed for some reason. The pattern will need to
     *  be modified to conform to the rules described above."
     */
    String MATCH_PATTERN_EXCEPTION = 
        "Cannot parse match pattern: {1}";

    /**
     * MATCHER_NOT_FOUND.
     * @sqlunit.error name="Matcher class {className} not available in 
     *  CLASSPATH"
     *  description="Some simple matchers are supplied as part of the 
     *  SQLUnit package. Matchers can also be written by users in their
     *  own package and be referenced from the SQLUnit tests provided the
     *  user-written Matcher exists in the user's CLASSPATH. The message
     *  indicates that it is not. Modify the CLASSPATH to include the
     *  user-defined Matcher class."
     */
    String MATCHER_NOT_FOUND = 
        "Matcher class {0} not available in the CLASSPATH";

    /**
     * NO_USERMATCH_VALUE.
     * @sqlunit.error name="No match on variable at [rset,row,col]=[
     *  {rset},{row},{col}] using class {matcher_class_name} expected:
     *  {expected_result} but got {actual_result}"
     *  description="This is a variation of the standard message reported
     *  in case of test failures. This message is reported only by the 
     *  Diff tag since that is the only tag which allows user-defined 
     *  matching. In addition to the information reported by the test
     *  failure, it also reports the class name of the Matcher class 
     *  currently being used. Corrective action is to fix the test."
     */
    String NO_USERMATCH_VALUE =
        "No match on variable at [rset,row,col]=({0}" + LF
        + "*** using class: {1}" + LF
        + "*** expected:" + LF + "{2}" + LF + "*** but got:" + LF + "{3}"
        + LF + "{4}";

    /**
     * IMPOSSIBLE_PARTIAL_MATCH
     * @sqlunit.error name="Partial Match impossible at {position}: {reason}"
     *  description="Partial match at the specified position could not
     *  be done because of the reason specified."
     */
    String IMPOSSIBLE_PARTIAL_MATCH = 
        "Partial Match impossible at {0}: {1}";

    // ------------------ Processing errors ----------------------------------

    /**
     * UNSUPPORTED_DATATYPE_PARSE.
     * @sqlunit.error name="At {position}, could not convert {actual_value}
     *  to {java_class_name} for {SQL_Type_Name}({SQL_Type})"
     *  description="The String value supplied in the param element could
     *  not be converted to the appropriate class dictated by the datatype
     *  mapping for that variable. You will most likely need to provide
     *  an override mapping specific to your database, or, if there is no
     *  suitable mapping, you/we could write a mapping class to handle 
     *  this type."
     */
    String UNSUPPORTED_DATATYPE_PARSE = 
        "At {0}, could not convert {1} to {2} for {3}({4})";

    /**
     * UNSUPPORTED_DATATYPE_FORMAT.
     * @sqlunit.error name="At {position}, could not convert {java_class_name}
     *  returned from database to {SQL_Type_Name}({SQL_Type})"
     *  description="The Object returned from the database is not of
     *  the same class that the class mapped to the SQL Type SQL_Type
     *  wraps. You will most likely need to provide an override mapping
     *  for your database, or, if there is no suitable mapping, you/we
     *  could write a mapping class to handle this type."
     */
    String UNSUPPORTED_DATATYPE_FORMAT = 
        "At {0}, could not convert {1} returned from database to {2}({3})";

    /**
     * UNSUPPORTED_DATATYPE.
     * @sqlunit.error name="At {position}, datatype {SQL_Type_Name}({SQL_Type})
     *  is not supported"
     *  description="The specified datatype is one of the standard 
     *  java.sql.Types but does not have an explicit mapping to a class.
     *  You will most likely need to provide an override mapping for your
     *  database based on your knowledge of the type, or, if there is no
     *  suitable mapping, you/we could write a mapping class to handle this
     *  type."
     */
    String UNSUPPORTED_DATATYPE = 
        "At {0}, datatype {1}({2}) is not supported";

    /**
     * UNDEFINED_TYPE.
     * @sqlunit.error name="No type mapping found for (server.)type 
     *  {{server_name}.{datatype}}"
     *  description="SQLUnit could not find a mapping type for this 
     *  specified type name. This is a non-standard (not in java.sql.Types)
     *  datatype, for which an override needs to be set in the mapping
     *  file for your database in usertypes.properties. If none of the existing
     *  mapping classes seem suitable, then you/we may have to write 
     *  a new mapping class and map this to the datatype."
     */
    String UNDEFINED_TYPE = 
        "No type mapping found for (server.)type {0}";

    /**
     * REPLACE_ERROR
     * @sqlunit.error name="Parsing of {text} failed because {reason}"
     *  description="SQLUnit could not find the variable defined in
     *  the symbol table. If you do not mind missing variables, then
     *  you should set partialOk to true."
     */
    String REPLACE_ERROR = 
        "Variable replacement of \"{0}\" failed because {1}";

    /**
     * UNDEFINED_SYMBOL.
     * @sqlunit.error name="The symbol could not be found in the symbol table"
     *  description="The named symbol could not be found. Check your logic
     *  and verify that you have set the symbol before trying to use it."
     */
    String UNDEFINED_SYMBOL =
        "Symbol {0} is not defined in the symbol table";
        
    /**
     * UNDEFINED_PARAM.
     * @sqlunit.error name="Parameter {parameter_name} is not defined"
     *  description="The named parameter appears in the subdef as unspecified,
     *  but was not specified in the corresponding sub."
     */
    String UNDEFINED_PARAM = 
        "Parameter {0} is not defined";

    /**
     * ERROR_IN_INCLUDE.
     * @sqlunit.error name="Statement {statement} ({number}) in include
     *  file {filename} returned result {error_code}"
     *  description="A SQL Statement in the include file referenced by the
     *  file name failed with the given error code. The SQL statements are
     *  indexed starting at 1."
     */
    String ERROR_IN_INCLUDE =
        "Statement \"{0}\" ({1}) in include file {2} returned error: {3}";

    /**
     * INVALID_VARIABLE_NAME_FORMAT.
     * @sqlunit.error name="Variable {variable_name} is invalid, should be
     *  in the format \${variable_name}"
     *  description="SQLUnit only accepts variables in the format 
     *  ${variable_name}. Fix the name in the XML input file."
     */
    String INVALID_VARIABLE_NAME_FORMAT =
        "Variable name {0} is invalid, should be in the format ${varname}";

    /**
     * IS_A_CURSOR.
     * @sqlunit.error name="Value at outparam id={id} is a CURSOR, not a 
     *  {supplied_type}"
     *  description="The type specified in the param declaration for a stored
     *  procedure or SQL statement was incorrect. Fix the type in the XML 
     *  input file."
     */
    String IS_A_CURSOR = 
        "Value at outparam id={0} is a CURSOR, not a {1}";

    /**
     * IS_A_STRUCT.
     * @sqlunit.error name="Value at outparam id={id} is a STRUCT, not a 
     *  {supplied_type}"
     *  description="The type specified in the param declaration for a stored
     *  procedure or SQL statement was incorrect. Fix the type in the XML 
     *  input file."
     */
    String IS_A_STRUCT = 
        "Value at outparam id={0} is a STRUCT, not a {1}";

    /**
     * NOT_A_NUMBER.
     * @sqlunit.error name="Value {supplied_value} for {variable} could not 
     *  be converted to a numeric value"
     *  description="SQLUnit expects a numeric value at the location 
     *  indicated, but parsing it from its String value to the appropriate
     *  numeric value resulted in a NumberFormatException."
     */
    String NOT_A_NUMBER = 
        "Value {0} for {1} could not be converted to a numeric value";

    /**
     * ERROR_DIGESTING_DATA.
     * @sqlunit.error name="Could not convert to digest because {reason}"
     *  description="The reason will give more information as to why the
     *  digestion process failed. Large Objects such as BLOBs and CLOBs
     *  are digested and then compared with specified MD5 Checksums of files 
     *  or the MD5 Checksum itself. If Java Object Support is enabled, and
     *  the large object represents a Serializable Java Object, or the
     *  datatype of the LOB is JAVA_OBJECT, then the object's toString() 
     *  method will be called and the results returned."
     */
    String ERROR_DIGESTING_DATA = 
        "Could not convert to digest because {0}";

    /**
     * METHOD_INVOCATION_ERROR.
     * @sqlunit.error name="Cannot invoke method {className}.{methodName}"
     *  description="This will usually be encountered when using the
     *  methodinvoker or dynamicsql tags, which depend on the results of
     *  invoking a specified method in a specified class in the CLASSPATH.
     *  The error will identify where the problem is happening, and will
     *  also include the error stack trace within the exception. To get
     *  the stack trace, rerun the test with debug set to true, and fix
     *  the problem that is causing this error to appear."
     */
    String METHOD_INVOCATION_ERROR = 
        "Cannot invoke method {0}.{1}";

    // ------------------ Test output errors ---------------------------------

    /**
     * NO_ASSERT_METHOD.
     * @sqlunit.error name="Assertion {assert} is not supported"
     *  description="The assertion provided in the assert attribute for
     *  this test element is not supported. Please check the documentation
     *  for a list of supported assertions."
     */
    String NO_ASSERT_METHOD = 
        "Assertion \"{0}\" is not supported";

    /**
     * ASSERT_FAILED.
     * @sqlunit.error name="Assertion {assert} failed ({reason}), expected 
     *  {expected_result}, but got {actual_result}, {failure_message}"
     *  description="The specified assertion failed."
     */
    String ASSERT_FAILED = 
        "Assertion \"{0}\" failed {1}" + LF + "*** expected:" + LF + "{2}"
        + LF + "*** but got:" + LF + "{3}" + LF + "{4}";

    /**
     * ASSERT_UNACCEPTABLE_ELAPSED_TIME.
     * @sqlunit.error name="Expected to complete in {expected-duration}
     *  ms +/- {percent-tolerance}%, but took {actual_duration}ms"
     *  description="The test took either too little or too much time
     *  compared with the specified expected time. If the percent tolerance
     *  is provided for the test, then SQLUnit will check for specified
     *  time +/- the tolerance, else it will use a default tolerance of
     *  10%. If it is taking too much time, it is possible that your
     *  new implementation is not using all the optimizations that your
     *  old implementation was taking advantage of. If it is taking too
     *  little time, it is possible that your stored procedure is using
     *  better optimizations or not doing everything it should. It could
     *  also be that your expected durations are now incorrect because of
     *  server-level tuning, or that the JVM is overloaded. There could
     *  be other causes for this discrepancy which are not covered here,
     *  which may be related to your site."
     */
    String ASSERT_UNACCEPTABLE_ELAPSED_TIME = 
        "Expected to complete in {0}ms +/- {1}%, but took {2}ms";

    /**
     * TEST_FAILURE_EXCEPTION.
     * @sqlunit.error name="SQLUnit Tests failed, file {filename}, run:
     *  {total_tests_run}, failures: {number_of_failed_tests}, errors:
     *  {number_of_errors_encountered}"
     *  description="This message is reported at the end of each SQLUnit
     *  test file if there was an error or failure running the tests in the
     *  file."
     */
    String TEST_FAILURE_EXCEPTION = 
        "SQLUnit Tests Failed: {0}";

    /**
     * SUITE_FAILURE_EXCEPTION.
     * @sqlunit.error name="One or more SQLUnit Tests failed, see the 
     *  {console|logfile} for details"
     *  description="This message is reported at the end of an SQLUnit run,
     *  spanning one or more test files, if there was a failure or an error
     *  in any one of the tests. The output will describe the exact nature of
     *  the failure(s)."
     */
    String SUITE_FAILURE_EXCEPTION =
        "One or more SQLUnit Tests failed, see {0} for details";

    // --------------------- System Errors -------------------------------

    /**
     * GENERIC_ERROR.
     * @sqlunit.error name="{Type} error ({Exception_Class}): {message}"
     *  description="The entire error message is returned, usually from 
     *  the JVM. The exception class specifies the Exception class name.
     *  Log a bug against the SQLUnit project if the meaning is
     *  not clear enough, otherwise take the necessary action to fix the
     *  condition that is causing it. Turning on the debug attribute for
     *  the sqlunit ant task will also show the stack trace for reporting
     *  or for analysis."
     */
    String GENERIC_ERROR = "{0} error ({1}): {2}";

    // -------------------- Usage ------------------------------------------

    /**
     * Usage string.
     */
    String USAGE = "Usage:" + LF
        + "<target name=\"test_target_name\">" + LF
        + "  <sqlunit (testFile=\"test_file_name\")*" + LF
        + "      (haltOnFailure=\"true\"|\"false\")*" + LF
        + "      (debug=\"true\"|\"false\")*>" + LF
        + "    (<fileSet>" + LF
        + "      <include name=\"filename pattern to match\" />" + LF
        + "      <exclude name=\"filename pattern to exclude\" />" + LF
        + "    </fileSet>)*" + LF
        + "  </sqlunit>" + LF
        + "</target>";

    /**
     * A pseudo-method inserted here to keep Bloch happy. We are not about
     * to implement this class anytime soon, so there is no danger with 
     * doing this here. We would rather have this inconsistency than give 
     * up the checkstyle check for the other interfaces, which do satisfy
     * this criterion.
     * @return a String.
     */
    String getUsage();
}
