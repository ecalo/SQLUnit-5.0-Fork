<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="C:\sqlunit-4.6\output\TestResults.xsl"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
  <xsl:template match="/">
    <html>
      <head>
        <title>Results of SQLUnit Test Run</title>
      </head>
      <body>
        <h1>Results of SQLUnit Test Run</h1>
        <div>
          <!-- ******************** -->
          <!-- Calculate statistics -->
          <!-- ******************** -->
          <!-- Total number of test cases -->
          <xsl:variable name="totalTestCases" select="count(//testresult)"/>
          <!-- Total number of failing test cases.  (At least one failing test constitutes a failure. -->
          <xsl:variable name="numTestCasesFail" select="count(//testresult/results/failure[1]) + count(//testresult/results/error[1])"/>
          <!-- Total number of passing test cases -->
          <xsl:variable name="numTestCasesPass" select="$totalTestCases - $numTestCasesFail"/>
          <!-- Total number of tests executed -->
          <xsl:variable name="totalTests" select="count(//step)"/>
          <!-- Total number of failing tests -->
          <xsl:variable name="numTestsFail" select="count(//failure) + count(//error)"/>
          <!-- Total number of passing tests -->
          <xsl:variable name="numTestsPass" select="$totalTests - $numTestsFail"/>
          <xsl:variable name="summaryResult">
            <xsl:choose>
              <xsl:when test="$numTestCasesFail=0">&lt;span style="color:limegreen;"&gt;Success&lt;/span&gt;</xsl:when>
              <xsl:otherwise>&lt;span style="color:red;"&gt;Failure&lt;/span&gt;</xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <h2>Summary of Test Results: <xsl:value-of select="$summaryResult" disable-output-escaping="yes"/>
          </h2>
          <table>
            <tbody>
              <tr>
                <td width="46%">
                  <table border="1">
                    <thead>
                      <tr>
                        <th colspan="3">Test Case Summary</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr>
                        <th width="33%">Total Executed</th>
                        <th width="33%">Passed</th>
                        <th width="33%">Failed</th>
                      </tr>
                      <tr>
                        <td align="center">
                          <xsl:value-of select="$totalTestCases"/>
                        </td>
                        <td align="center">
                          <xsl:value-of select="$numTestCasesPass"/>
                        </td>
                        <td align="center">
                          <xsl:value-of select="$numTestCasesFail"/>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </td>
                <td width="8%"/>
                <td width="46%">
                  <table border="1">
                    <thead>
                      <tr>
                        <th colspan="3">Test Summary</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr>
                        <th width="33%">Total Executed</th>
                        <th width="33%">Passed</th>
                        <th width="33%">Failed</th>
                      </tr>
                      <tr>
                        <td align="center">
                          <xsl:value-of select="$totalTests"/>
                        </td>
                        <td align="center">
                          <xsl:value-of select="$numTestsPass"/>
                        </td>
                        <td align="center">
                          <xsl:value-of select="$numTestsFail"/>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </td>
              </tr>
            </tbody>
          </table>
          <br/>
          <table width="50%" border="1">
            <thead>
              <tr>
                <th colspan="4">Test Case Breakdown</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <th>Test Case</th>
                <th>Total Tests</th>
                <th>Tests Passed</th>
                <th>Tests Failed</th>
              </tr>
              <xsl:for-each select="/summary/testresult">
                <xsl:variable name="totalTestsWithinCase" select="count(results/step)"/>
                <xsl:variable name="numTestsWithinTestCaseFail" select="count(results/step/result/failed) + count(results/error)"/>
                <xsl:variable name="numTestsWithinTestCasePass" select="$totalTestsWithinCase - $numTestsWithinTestCaseFail"/>
                <tr>
                  <td>
                    <xsl:if test="$numTestsWithinTestCaseFail &gt; 0">
                      <xsl:text disable-output-escaping="yes">&lt;span style="color:red;"&gt;</xsl:text>
                    </xsl:if>
                    <xsl:if test="$numTestsWithinTestCaseFail &gt; 0">
                      <xsl:text disable-output-escaping="yes">&lt;/span&gt;</xsl:text>
                    </xsl:if>
                    <xsl:value-of select="concat('Test', substring-after(substring-before(@location,'.xml'),'\Test'))"/>
                  </td>
                  <td>
                    <xsl:value-of select="$totalTestsWithinCase"/>
                  </td>
                  <td>
                    <xsl:value-of select="$numTestsWithinTestCasePass"/>
                  </td>
                  <td>
                    <xsl:if test="$numTestsWithinTestCaseFail &gt; 0">
                      <xsl:text disable-output-escaping="yes">&lt;span style="color:red;"&gt;</xsl:text>
                    </xsl:if>
                    <xsl:value-of select="$numTestsWithinTestCaseFail"/>
                    <xsl:if test="$numTestsWithinTestCaseFail &gt; 0">
                      <xsl:text disable-output-escaping="yes">&lt;/span&gt;</xsl:text>
                    </xsl:if>
                  </td>
                </tr>
              </xsl:for-each>
            </tbody>
          </table>
          <xsl:if test="$numTestCasesFail &gt; 0">
            <h2>Test Failure Details</h2>
            <xsl:for-each select="/summary/testresult/results/step">
              <xsl:if test="(node()/failed) or (following-sibling::node()[position() = 1 and name()='error'])">
                <strong>
                  <xsl:value-of select="parent::node()/parent::node()/@location"/>
                </strong>
                <br/>
                <strong>
                  <xsl:value-of select="parameter/@value"/>
                </strong>
                <br/>
                <xsl:variable name="errorMessage">
                  <xsl:choose>
                    <xsl:when test="node()/failed">
                      <xsl:value-of select="following-sibling::node()/@message"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="parent::node()/error/child::text()"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>
                
                <xsl:call-template name="formatErrorMessage">
                  <xsl:with-param name="errMsg" select="$errorMessage"/>
                </xsl:call-template>
                
                <hr/>
              </xsl:if>
            </xsl:for-each>
          </xsl:if>
        </div>
      </body>
    </html>
  </xsl:template>
  <xsl:template name="formatErrorMessage">
    <xsl:param name="errMsg"/>
    <xsl:variable name="errMsg2">
      <xsl:value-of select="translate($errMsg, ' ', '&#xA0;')" />
    </xsl:variable>
    <xsl:variable name="fromString">
    <xsl:choose>
      <xsl:when test="contains($errMsg2,'&#xD;')">&gt;&#xD;&#xA;</xsl:when>
      <xsl:otherwise>&gt;&#xA0;</xsl:otherwise>
    </xsl:choose>
    </xsl:variable>
    <xsl:call-template name="string-replace">
      <xsl:with-param name="stringToModify" select="$errMsg2"/>
      <xsl:with-param name="from" select="$fromString" />
      <xsl:with-param name="to">&gt;<br/>&#xA0;</xsl:with-param>
    </xsl:call-template>
  </xsl:template>  
  <xsl:template name="string-replace">
    <xsl:param name="stringToModify"/>
    <xsl:param name="from"/>
    <xsl:param name="to"/>
    <xsl:choose>
      <xsl:when test="contains($stringToModify,$from)">
        <xsl:value-of select="substring-before($stringToModify,$from)"/>
        <xsl:copy-of select="$to"/>
        <xsl:call-template name="string-replace">
          <xsl:with-param name="stringToModify" select="substring-after($stringToModify,$from)"/>
          <xsl:with-param name="from" select="$from"/>
          <xsl:with-param name="to" select="$to"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$stringToModify"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
