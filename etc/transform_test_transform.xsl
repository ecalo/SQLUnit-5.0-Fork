<?xml version="1.0"?>
<!-- $Id: transform_test_transform.xsl,v 1.1 2005/05/21 19:38:37 spal Exp $ -->
<!-- $Source: /cvsroot/sqlunit/sqlunit/etc/transform_test_transform.xsl,v $ -->
<!-- Test XSL Transform to convert to SQLUnit XML format -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="SqlCode">

    <sqlunit>

      <connection>
        <driver><xsl:text>${jdbc.driver.class}</xsl:text></driver>
        <url><xsl:text>${jdbc.url}</xsl:text></url>
        <user><xsl:text>${jdbc.user}</xsl:text></user>
        <password><xsl:text>${jdbc.password}</xsl:text></password>
      </connection>

      <setup />

      <xsl:apply-templates select="SQL_STMT" />

      <teardown />

    </sqlunit>
  </xsl:template>

  <xsl:template match="SQL_STMT">
      <test>
        <xsl:attribute name="name">
          <xsl:value-of select="../SQL_CODE" />
        </xsl:attribute>
      <sql>
        <stmt>
          <xsl:value-of select="text()" />
        </stmt>
      </sql>
      <result assert="not-exception" />
    </test>
  </xsl:template>

</xsl:stylesheet>
