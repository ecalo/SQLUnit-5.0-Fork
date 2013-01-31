<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
  <xsl:param name="testCaseFileName" /> 
   <!--  Set the media type as "text/html" per W3C recommendations.  See http://www.w3.org/TR/xhtml1/#issues section 5.1   -->
   <!--  Note: Some XSLT processors will display the "html" tag as "HTML", which is incorrect.          -->
  <xsl:output method="xml" omit-xml-declaration="no" media-type="text/html" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />
  <xsl:template match="/">  
    <xsl:choose>
      <!--  If the test case file name is supplied, then we will assume that this style sheet is being applied to  -->
      <!--  multiple SQLUnit .xml files to extract test information.  In this case, this stylesheet will only  -->
      <!--  return an HTML fragment.                                -->
      <!--                                                  -->
      <!--  Otherwise, assume it is being called as an argument to an Xalan along with a specific SQLUnit .xml   -->
      <!--  file.  In this case we will supply the proper HTML tags to create a valid XHTML document.    -->
      <xsl:when test="string-length($testCaseFileName) != 0">
        <xsl:apply-templates />
      </xsl:when>
      <xsl:otherwise>
        <html>
          <head>
            <title>Listing of Available Tests</title>
          </head>
          <body>
            <xsl:apply-templates />
          </body>
        </html>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>  
  <xsl:template match="sqlunit">
    <!--  Determine if we have numbered test cases.  The naming convention I used for SQLUnit tests at the   -->
    <!--  time I developed this stylesheet was the filename with an underscore and a monotonically     -->
    <!--  increasing integer as a suffix.  A description of the test would follow the name but be separated  -->
    <!--  by a colon (':').    As an example, if the filename was "TestFoo.xml" a SQLUnit test name would  -->
    <!--  look like "TestFoo_1: A Simple SQLUnit Test".                  -->
    <!--                                                  -->
    <!--  If this convention is not followed, the SQLUnit test name is used without being parsed.    -->
    <xsl:variable name="numberedTestCasesExist">
      <xsl:choose>
        <xsl:when test="contains(test[1]/@name, '_1:')">
          <xsl:value-of select="true()" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="true" />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="testCaseName">
      <xsl:choose>
        <xsl:when test="$numberedTestCasesExist">
          <xsl:value-of select="substring-before(test[1]/@name, '_1')" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="test[1]/@name" />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <div>
      <xsl:choose>
        <xsl:when test="string-length($testCaseFileName) != 0">
          <a name="{$testCaseName}">
            <div style="font-weight: bold; font-size: larger;">Tests Within Test Case <a href="file://{$testCaseFileName}"><xsl:value-of select="$testCaseName" /></a></div>
          </a>
        </xsl:when>
      <xsl:otherwise>
          <h3>Tests Within Test Case <xsl:value-of select="$testCaseName" /></h3>
      </xsl:otherwise>
      </xsl:choose>
      <table border="0" width="100%">
        <xsl:for-each select="test">
          <xsl:call-template name="listTestsWithinTestCase">
            <xsl:with-param name="testNumber" select="substring-after(substring-after(substring-before(@name,':'), $testCaseName), '_')" />
            <xsl:with-param name="testDescription" select="substring-after(@name, ':')" />
          </xsl:call-template>
        </xsl:for-each>
      </table>
    </div>
  </xsl:template>  
  <xsl:template name="listTestsWithinTestCase">
    <xsl:param name="testNumber" />
    <xsl:param name="testDescription" />
    <tr>
      <xsl:if test="string-length($testNumber) != 0">
        <td style="width: 3%; text-align: right;"><xsl:value-of select="$testNumber" />.</td>
      </xsl:if>
      <td><xsl:value-of select="$testDescription" /></td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
