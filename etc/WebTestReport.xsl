<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet [
        <!ENTITY space "&#32;">
        <!ENTITY nbsp "&#160;">
        ]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" encoding="us-ascii" indent="yes"
                doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
                doctype-system="http://www.w3.org/TR/html4/loose.dtd"
            />

    <!-- Parameter passed from ant with the creation time -->
    <xsl:param name="reporttime"/>
    <xsl:param name="title"/>

    <!-- Customization hook for site-specific differences -->
    <xsl:param name="resources.dir" select="'.'"/>
    <xsl:variable name="company.logo.alt" select="'Canoo'"/>

    <!-- global variable -->
    <xsl:variable name="duration.total"
                  select="sum(/summary/testresult/results/step/result/node()[name()='completed' or name()='failed']/@duration)"/>

    <xsl:template match="/">
        <!-- HTML prefix -->
        <html>
            <head>
                <title>WebTest - Test Result Overview</title>
                <meta http-equiv="content-style-type" content="text/css"/>
                <link rel="stylesheet" type="text/css" href="{$resources.dir}/report.css"/>
                <script type="text/javascript" src="{$resources.dir}/showHide.js"></script>
				<script type="text/javascript" src="{$resources.dir}/responseBrowser.js" id="scriptResponseBrowser"></script>
            </head>

            <!-- ###################################################################### -->
            <!-- tj: hiding all substeps on load -->
            <body onload="hideAllSubstepsOnPage('expandall.png','collapseall.png')" style="margin: 10px;">
                <div class="header">
                    <img src="{$resources.dir}/images/logo.gif" alt="{$company.logo.alt}"/>
                    <br/>
                    <xsl:value-of select="$title"/>
                    <xsl:text>&nbsp;&nbsp;&nbsp;&nbsp;Tests started at&space;</xsl:text>
                    <xsl:value-of select="/summary/testresult[1]/@starttime"/>
                </div>

                <!-- Header and summary table -->
                <xsl:call-template name="StepStatistics"/>

                <xsl:call-template name="OverviewTable"/>
                <xsl:if test="descendant::step/descendant::step">
                <p>
						<xsl:text>Expand/Collapse nested steps for all tests:&space;</xsl:text>
						<img onclick="showAllSubsteps(document)" src="{$resources.dir}/images/expandall.png" class="withPointer"
						     alt="show all nested steps in document" title="show all nested steps in document"/>
						<xsl:text>&space;</xsl:text>
						<img onclick="hideAllSubsteps(document)" src="{$resources.dir}/images/collapseall.png" class="withPointer"
						     alt="hide all nested steps in document" title="hide all nested steps in document"/>
                    </p>
                </xsl:if>

                <!-- All individual test results -->
                <xsl:apply-templates/>

                <!-- Footer & fun -->
                <hr/>
                <xsl:text>Created using&space;</xsl:text>
                <a href="http://webtest.canoo.com">
                    <xsl:value-of select="/summary/@Implementation-Title"/>
                </a>
                <xsl:text>&space;(</xsl:text>
                <xsl:value-of select="/summary/@Implementation-Version"/>
                <xsl:text>). Report created at&space;</xsl:text>
                <xsl:value-of select="$reporttime"/>
                <!-- HTML postfix -->
            </body>
        </html>

    </xsl:template>

    <xsl:template name="OverviewTable">
        <h4>
            <a name="overview"/>
                <xsl:text>Test Scenario Overview (</xsl:text>
                <xsl:call-template name="time">
                    <xsl:with-param name="msecs" select="$duration.total"/>
                </xsl:call-template>
                <xsl:text>)</xsl:text>
        </h4>

        <!--
              Create summary table entries choosing the td-class depending on successful yes/no
              and create a link to the appropriate detail section (e.g. #testspec1).
          -->
        <table cellpadding="5" border="0" cellspacing="0" width="100%">
            <thead>
                <tr>
                    <th>No</th>
                    <th>Result</th>
                    <th>Name</th>
                    <th>Duration</th>
                    <th>%</th>
                    <th>Graph</th>
                </tr>
            </thead>
            <tbody>
                <xsl:apply-templates select="/summary/testresult" mode="summary"/>
            </tbody>
        </table>
    </xsl:template>

    <xsl:template name="StepStatistics">
        <table cellpadding="0" border="0" cellspacing="2" width="100%">
            <tr>
                <td valign="top" width="50%">
                    <h4>Result Summary</h4>

                    <xsl:variable name="steps.total" select="count(//results/step)"/>
                    <xsl:variable name="steps.ok" select="count(//results/step/result/completed)"/>
                    <xsl:variable name="steps.failed" select="count(//results/step/result/failed)"/>
                    <xsl:variable name="steps.else" select="count(//results/step/result/notexecuted)"/>

                    <table cellpadding="3" border="0" cellspacing="0" width="100%">
                        <thead>
                            <tr>
                                <th align="right">Result</th>
                                <th align="right">#</th>
                                <th align="right">%</th>
                                <th>Graph</th>
                            </tr>
                        </thead>
                        <tfoot>
                            <tr>
                                <td class="light">
                                    <b>Sum</b>
                                </td>
                                <td class="light" align="right">
                                    <b>
                                        <xsl:text>&nbsp;</xsl:text>
                                        <xsl:value-of select="$steps.total"/>
                                    </b>
                                </td>
                                <td class="light" align="right">
                                    <b>&nbsp;100</b>
                                </td>
                                <td class="light">&nbsp;</td>
                            </tr>
                        </tfoot>
                        <tbody>
                            <tr>
                                <td class="light">
                                    <img src="{$resources.dir}/images/ok.gif" alt=""/>
                                </td>
                                <td class="light" align="right">
                                    <xsl:value-of select="$steps.ok"/>
                                </td>
                                <td class="light" align="right">
                                    <xsl:value-of select="round($steps.ok * 100 div $steps.total)"/>
                                </td>
                                <xsl:call-template name="colorBar">
                                    <xsl:with-param name="percentage" select="$steps.ok * 100 div $steps.total"/>
                                    <xsl:with-param name="color" select="'lightgreen'"/>
                                    <xsl:with-param name="title" select="'Successful steps'"/>
                                </xsl:call-template>
                            </tr>
                            <tr>
                                <td class="light">
                                    <img src="{$resources.dir}/images/todo.gif" alt="x"/>
                                </td>
                                <td class="light" align="right">
                                    <xsl:value-of select="$steps.failed"/>
                                </td>
                                <td class="light" align="right">
                                    <xsl:value-of select="round($steps.failed * 100 div $steps.total)"/>
                                </td>
                                <xsl:call-template name="colorBar">
                                    <xsl:with-param name="percentage" select="$steps.failed * 100 div $steps.total"/>
                                    <xsl:with-param name="color" select="'red'"/>
                                    <xsl:with-param name="title" select="'Failed steps'"/>
                                </xsl:call-template>
                            </tr>
                            <tr>
                                <td class="light">
                                    <img src="{$resources.dir}/images/optional.gif" alt="o"/>
                                </td>
                                <td class="light" align="right">
                                    <xsl:value-of select="$steps.else"/>
                                </td>
                                <td class="light" align="right">
                                    <xsl:value-of select="round($steps.else * 100 div $steps.total)"/>
                                </td>
                                <xsl:call-template name="colorBar">
                                    <xsl:with-param name="percentage" select="$steps.else * 100 div $steps.total"/>
                                    <xsl:with-param name="color" select="'yellow'"/>
                                    <xsl:with-param name="title" select="'Skipped steps'"/>
                                </xsl:call-template>
                            </tr>
                        </tbody>
                    </table>
                </td>
                <td valign="top">

                    <h4>Server Roundtrip Timing Profile</h4>
                    <!--        ================================ server roundtrip statistic table =============================    -->

                    <xsl:variable name="duration.steps"
					              select="//step[parameter[@name='taskName'][starts-with(@value, 'sqlunit') or @value='invoke' or @value='clickLink' or @value='clickButton' or @value='clickElement']]/result/node()[name()='completed' or name()='failed']"/>

                    <xsl:variable name="last.steps"
                                  select="count($duration.steps[@duration > 30000])"/>
                    <xsl:variable name="third.steps"
                                  select="count($duration.steps[@duration > 10000][30000 >= @duration])"/>
                    <xsl:variable name="second.steps"
                                  select="count($duration.steps[@duration > 5000][10000 >= @duration])"/>
                    <xsl:variable name="first.steps"
                                  select="count($duration.steps[@duration > 1000][5000 >= @duration])"/>
                    <xsl:variable name="begin.steps"
                                  select="count($duration.steps[1000 >= @duration])"/>

                    <table cellpadding="3" border="0" cellspacing="0" width="100%">
                        <thead>
                            <tr>
                                <th align="center">Secs</th>
                                <th align="right">#</th>
                                <th align="right">%</th>
                                <th>Histogram</th>
                            </tr>
                        </thead>
                        <tfoot>
                            <tr>
                                <td class="light">
                                    <b>Sum</b>
                                </td>
                                <td class="light" align="right">
                                    <b>
                                        <xsl:text>&nbsp;</xsl:text>
                                        <xsl:value-of select="count($duration.steps)"/>
                                    </b>
                                </td>
                                <td class="light" align="right">
                                    <b>&nbsp;100</b>
                                </td>
                                <td class="light">&nbsp;</td>
                            </tr>
                            <tr>
                                <td class="light">
                                    <b>Avg</b>
                                </td>
                                <td class="light" align="right">
                                    &nbsp;
                                </td>
                                <td class="light" align="right">
                                    &nbsp;
                                </td>
                                <td class="light">
                                    <b>
                                        <xsl:value-of
                                                select="round(sum($duration.steps/@duration) div 1000 div count($duration.steps))"/>
                                    </b>
                                </td>
                            </tr>
                        </tfoot>
                        <tbody>
                            <xsl:call-template name="histogramRow">
                                <xsl:with-param name="label"> &nbsp;0 - &nbsp;1</xsl:with-param>
                                <xsl:with-param name="steps" select="$begin.steps"/>
                                <xsl:with-param name="duration.steps" select="$duration.steps"/>
                            </xsl:call-template>

                            <xsl:call-template name="histogramRow">
                                <xsl:with-param name="label"> &nbsp;1 - &nbsp;5</xsl:with-param>
                                <xsl:with-param name="steps" select="$first.steps"/>
                                <xsl:with-param name="duration.steps" select="$duration.steps"/>
                            </xsl:call-template>

                            <xsl:call-template name="histogramRow">
                                <xsl:with-param name="label"> &nbsp;5 - 10</xsl:with-param>
                                <xsl:with-param name="steps" select="$second.steps"/>
                                <xsl:with-param name="duration.steps" select="$duration.steps"/>
                            </xsl:call-template>

                            <xsl:call-template name="histogramRow">
                                <xsl:with-param name="label">10 - 30</xsl:with-param>
                                <xsl:with-param name="steps" select="$third.steps"/>
                                <xsl:with-param name="duration.steps" select="$duration.steps"/>
                            </xsl:call-template>

                            <xsl:call-template name="histogramRow">
                                <xsl:with-param name="label"> &nbsp;&nbsp; > 30</xsl:with-param>
                                <xsl:with-param name="steps" select="$last.steps"/>
                                <xsl:with-param name="duration.steps" select="$duration.steps"/>
                            </xsl:call-template>
                        </tbody>
                    </table>
                </td>
            </tr>
        </table>
    </xsl:template>

    <xsl:template name="histogramRow">
        <xsl:param name="label"/>
        <xsl:param name="steps"/>
        <xsl:param name="duration.steps"/>
        <tr>
            <td class="light" nowrap="nowrap">
                <xsl:value-of select="$label"/>
            </td>
            <td class="light" align="right">
                <xsl:value-of select="$steps"/>
            </td>
            <td class="light" align="right">
                <xsl:value-of select="round($steps * 100 div count($duration.steps))"/>
            </td>
            <xsl:call-template name="colorBar">
                <xsl:with-param name="percentage" select="$steps * 100 div count($duration.steps)"/>
                <xsl:with-param name="color" select="'lightblue'"/>
            </xsl:call-template>
        </tr>
    </xsl:template>

    <xsl:template name="colorBar">
        <xsl:param name="percentage"/>
        <xsl:param name="color"/>
        <xsl:param name="title"/>

        <td width="80%" class="light">
            <xsl:if test="$percentage > 0">
                <div class="colorBar" style="width: {$percentage}%; background: {$color};">
                    <xsl:if test="$title">
                        <xsl:attribute name="title">
                            <xsl:value-of select="$title"/>
                        </xsl:attribute>
                    </xsl:if>
                </div>
            </xsl:if>
        </td>
    </xsl:template>

    <xsl:template name="time">
        <xsl:param name="msecs"/>

        <xsl:choose>
            <xsl:when test="$msecs > 5000">
                <xsl:variable name="base" select="round($msecs div 1000)"/>
                <xsl:variable name="hours" select="floor($base div 3600)"/>
                <xsl:variable name="mins" select="floor(($base - $hours*3600) div 60)"/>
                <xsl:variable name="secs" select="floor(($base - $hours*3600) - $mins*60)"/>

                <xsl:if test="10 > $hours">0</xsl:if>
                <xsl:value-of select="$hours"/>
                <xsl:text>:</xsl:text>
                <xsl:if test="10 > $mins">0</xsl:if>
                <xsl:value-of select="$mins"/>
                <xsl:text>:</xsl:text>
                <xsl:if test="10 > $secs">0</xsl:if>
                <xsl:value-of select="$secs"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$msecs"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="testresult[@successful='no']" mode="successIndicator">
        <a>
            <xsl:attribute name="href">
                <xsl:text>#testspec</xsl:text>
                <xsl:number/>
                <xsl:text>-error</xsl:text>
            </xsl:attribute>
            <img src="{$resources.dir}/images/todo.gif" alt="x"/>
        </a>
    </xsl:template>

    <xsl:template match="testresult[@successful='yes']" mode="successIndicator">
        <img src="{$resources.dir}/images/ok.gif" alt=""/>
    </xsl:template>

    <xsl:template match="testresult" mode="summary">
        <tr>
            <td class="light" align="right">
                <xsl:number/>
            </td>
            <td class="light" align="center">
                <xsl:apply-templates select="." mode="successIndicator"/>
            </td>
            <td class="light" nowrap="nowrap">
                <a>
                    <xsl:attribute name="href">
                        <xsl:text>#testspec</xsl:text>
                        <xsl:number/>
                    </xsl:attribute>
                    <xsl:value-of select="@testspecname"/>
                </a>
            </td>
            <xsl:variable name="actual.testspec.duration"
                          select="sum(results/step/result/node()[name()='completed' or name()='failed']/@duration)"/>
            <td class="light" align="right" nowrap="nowrap">
                <xsl:call-template name="time">
                    <xsl:with-param name="msecs" select="$actual.testspec.duration"/>
                </xsl:call-template>
            </td>
            <td class="light" align="right">
                <xsl:value-of select="round($actual.testspec.duration * 100 div $duration.total)"/>
            </td>

            <xsl:call-template name="colorBar">
                <xsl:with-param name="percentage" select="$actual.testspec.duration * 100 div $duration.total"/>
                <xsl:with-param name="color" select="'lightblue'"/>
            </xsl:call-template>
        </tr>
    </xsl:template>

    <xsl:template match="testresult">
        <hr/>

        <!-- general info left -->
        <!-- Header and red/green box for success/failure overview-->
        <blockquote>
            <h4>
                <xsl:apply-templates select="." mode="successIndicator"/>
                <xsl:text>&nbsp;</xsl:text>
                <a>
                    <xsl:attribute name="name">
                        <xsl:text>testspec</xsl:text>
                        <xsl:number/>
                    </xsl:attribute>
                </a>
                <xsl:value-of select="@testspecname"/>
            </h4>
            <xsl:apply-templates select="description"/>

            <xsl:text>Test started at&space;</xsl:text>
            <xsl:value-of select="@starttime"/>
            <br/>
            <xsl:text>Source:&space;</xsl:text>
            <span class="location">
                <xsl:value-of select="@location"/>
            </span>
            <br/>
            <xsl:text>Base URL (used by&space;</xsl:text>
            <b>invoke</b>
            <xsl:text>&space;steps with a relative URL):&space;</xsl:text>
            <xsl:apply-templates select="config"/>
        </blockquote>

        <!-- ###################################################################### -->
        <!-- tj: show/hide all -->
        <xsl:if test="descendant::step/descendant::step">
            <p>
                <xsl:text>Expand/Collapse nested steps:</xsl:text>
				<img onclick="showAllSubstepsOfTestspec(this)" src="{$resources.dir}/images/expandall.png" class="withPointer"
				     alt="show all nested steps in testspec" title="show all nested steps in testspec"/>
				<xsl:text>&nbsp; </xsl:text>
				<img onclick="hideAllSubstepsOfTestspec(this)" src="{$resources.dir}/images/collapseall.png" class="withPointer"
				     alt="hide all nested steps in testspec" title="hide all nested steps in testspec"/>
            </p>
        </xsl:if>
        <!-- ###################################################################### -->
        <!-- The test step results in sequence below -->
        <xsl:apply-templates select="results"/>

        <xsl:apply-templates select="results/failure"/>
        <xsl:apply-templates select="results/error"/>

        <!-- Create link back to overview (top) -->
        <p>
            <a href="#overview">Back to Test Report Overview</a>
        </p>
    </xsl:template>

    <!-- Individual templates -->
    <xsl:template match="config">
        <xsl:variable name="port" select="parameter[@name='port']/@value"/>
        <xsl:variable name="basepath" select="parameter[@name='basepath']/@value"/>
        <xsl:variable name="configHref">
            <xsl:value-of select="parameter[@name='protocol']/@value"/>
            <xsl:text>://</xsl:text>
            <xsl:value-of select="parameter[@name='host']/@value"/>
            <xsl:if test="$port != 80">
                <xsl:text>:</xsl:text>
                <xsl:value-of select="$port"/>
            </xsl:if>
            <xsl:text>/</xsl:text>
            <xsl:if test="$basepath != 'null'">
                <xsl:value-of select="$basepath"/>
                <xsl:text>/</xsl:text>
            </xsl:if>
        </xsl:variable>
        <a target="_blank" href="{$configHref}">
            <xsl:value-of select="$configHref"/>
        </a>
    </xsl:template>

    <!-- Renders the description of a webtest. Currently a webtest can have 0 or 1 description -->
    <xsl:template match="description">
    	<div class="description"><xsl:value-of select="text()"/></div>
    </xsl:template>

	<!-- Renders the link to a saved result page -->
	<xsl:template match="parameter[@name='resultFilename']">
		<xsl:param name="linkText" select="'Resulting page'"/>
		<br/>
		<a target="_blank" href="{@value}"><xsl:value-of select="$linkText"/></a>
	</xsl:template>

    <xsl:template match="parameter">
        <tr>
            <td class="parameterName light">
                <b>
                    <xsl:value-of select="@name"/>
                </b>
            </td>
            <td class="light">
                <xsl:choose>
                    <xsl:when test="@name='filename' and not(starts-with(@value, '#{'))">
                        <a target="_blank" href="concat('file://', translate(@value, '\', '/'))">
                            <xsl:value-of select="@value"/>
                        </a>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="@value"/>
                    </xsl:otherwise>
                </xsl:choose>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="step">
        <xsl:variable name="stepNumber">
            <xsl:number level="multiple" count="step" from="results"/>
        </xsl:variable>

        <tr>
            <!-- ###################################################################### -->
            <!-- tj: create image to show/hide substeps if row contains substeps and add onclick and groupStep attribute to image -->
            <td class="light" style="border-bottom:1px inset black;">
                <b>&nbsp;
                    <xsl:number/>
                </b>
                <xsl:if test="descendant::step">
                <br/>
					<img name="collapseButton" onclick="changeLevelOfDetailForGroup(this)" src="{$resources.dir}/images/expandall.png" class="withPointer" alt="Shows all substeps" title="Shows all substeps"/>
                </xsl:if>
            </td>
            <!-- ###################################################################### -->

            <xsl:apply-templates select="result"/>
            <xsl:call-template name="stepNameCell"/>
            <xsl:call-template name="stepParameterCell"/>
            <td style="border-bottom:1px inset black;" valign="top" align="right" class="light">
                <xsl:choose>
                    <xsl:when test="result/completed or result/failed">
                        <xsl:call-template name="time">
                            <xsl:with-param name="msecs"
                                            select="result/node()[name()='completed' or name()='failed']/@duration"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        &nbsp;
                    </xsl:otherwise>
                </xsl:choose>
            </td>
        </tr>
    </xsl:template>


    <xsl:template name="stepNameCell">
        <td style="border-bottom:1px inset black;" valign="top" class="light">
            <b>
                <xsl:value-of select="parameter[@name='taskName']/@value"/>
            </b>
            <br/>
            <!-- Hide title unknown-->
            <xsl:choose>
                <xsl:when test="parameter[@name='description']/@value='&lt;unknown&gt;'">
                    &nbsp;
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="parameter[@name='description']/@value"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:apply-templates select="parameter[@name='resultFilename']"/>

			<!-- if the step is a container, display link to last result page of this container.
			This allows to see the last result of a group without to have to click the nested steps of this group -->
            <xsl:if test="descendant::step[descendant::step]">
            <xsl:apply-templates select="(.//parameter[@name='resultFilename'])[last()]">
					<xsl:with-param name="linkText" select="'last result page of this group'"/>
				</xsl:apply-templates>
			</xsl:if>
        </td>
    </xsl:template>

    <xsl:template name="stepParameterCell">
        <td style="border-bottom:1px inset black;" valign="top" class="light">
            <xsl:variable name="parameter.list"
                          select="parameter[@name!='taskName'][@name!='description'][@name!='resultFilename'][@name!='text' or @value!='null'][@name!='regex' or @value!='false']"/>
            <xsl:choose>
                <xsl:when test="count($parameter.list) > 0">
                    <div class="parameterWrapper"> <!-- to have a scrollbar when parameter values are very long -->
                        <table cellpadding="2" cellspacing="0">
                            <xsl:apply-templates select="$parameter.list[@name!='=> value']">
                                <xsl:sort select="@name"/>
                            </xsl:apply-templates>
                            <xsl:apply-templates select="$parameter.list[@name='=> value']"/>
                        </table>
                    </div>
                </xsl:when>
                <xsl:otherwise>
                    &nbsp;
                </xsl:otherwise>
            </xsl:choose>
            <xsl:call-template name="renderStepTable"/>
        </td>
    </xsl:template>

    <!--
     Template to be applied for the element results, and called by name for children of step (group, not, ...)
     -->
    <xsl:template match="results" name="renderStepTable">
        <xsl:if test="count(step) > 0">
            <table cellpadding="3" border="0" cellspacing="0" width="100%" class="expanded">
                <tr>
                    <th>No</th>
                    <th>Result</th>
                    <th>Name</th>
                    <th>Parameter</th>
                    <th>Duration</th>
                </tr>
                <xsl:apply-templates select="step"/>
            </table>
        </xsl:if>
    </xsl:template>

    <xsl:template match="result[completed]">
        <td style="border-bottom:1px inset black;" class="light" align="center">
            <xsl:choose>
                <!-- tj: set status of a step of type 'group' to 'failed' if one of its nested steps fails  -->
                <!-- tj: set status to 'ok' if step is not of type 'group' or if all substeps of a step of type 'group' have been executed successfully. -->
                <xsl:when
                        test="preceding-sibling::*[@name='taskName'][@value='group']/following-sibling::step/result/failed">
                    <img src="{$resources.dir}/images/todo.gif" alt="x"/>
                </xsl:when>
                <xsl:otherwise>
                    <img src="{$resources.dir}/images/ok.gif" alt=""/>
                </xsl:otherwise>
            </xsl:choose>
        </td>
    </xsl:template>

    <xsl:template match="result[failed]">
        <td style="border-bottom:1px inset black;" class="light" align="center">
            <img src="{$resources.dir}/images/todo.gif" alt="x"/>
            <!--
               Marks only the step that fails.
               This step has an even number of 'not' ancestor and a single failing result in its desendants (itself!)
               -->
            <xsl:if
                    test="(count(../ancestor::step/parameter[@name='taskName' and @value='not']) mod 2 = 0) and (count(../descendant::result[failed]) = 1)">
                <br/>
                <a class="linkToError">
                    <xsl:attribute name="name">
                        <xsl:text>testspec</xsl:text>
                        <xsl:number count="testresult"/>
                        <xsl:text>-error</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="href">
                        <xsl:text>#error</xsl:text>
                        <xsl:number count="testresult"/>
                    </xsl:attribute>
                    <xsl:text>Error</xsl:text>
                </a>
            </xsl:if>
        </td>
    </xsl:template>

    <xsl:template match="result[notexecuted]">
        <td style="border-bottom:1px inset black;" class="light" align="center">
            <img src="{$resources.dir}/images/optional.gif" alt="o"/>
        </td>
    </xsl:template>


    <xsl:template match="failure">
        <h2>
            <a>
                <xsl:attribute name="name">
                    <xsl:text>error</xsl:text>
                    <xsl:number count="testresult"/>
                </xsl:attribute>
                <xsl:text>Failure</xsl:text>
            </a>
        </h2>

        <h3>Message</h3>
        <xsl:value-of select="@message"/>

        <br/>
        <br/>
    </xsl:template>

    <xsl:template match="error">
        <h2>
            <a>
                <xsl:attribute name="name">
                    <xsl:text>error</xsl:text>
                    <xsl:number count="testresult"/>
                </xsl:attribute>
                <xsl:text>Error</xsl:text>
            </a>
        </h2>

        <h3>Exception</h3>
        <xsl:value-of select="@exception"/>

        <h3>Message</h3>
        <xsl:value-of select="@message"/>

        <h3>Stacktrace</h3>
        <pre>
            <xsl:value-of select="text()" disable-output-escaping="no"/>
        </pre>
    </xsl:template>
</xsl:stylesheet>
