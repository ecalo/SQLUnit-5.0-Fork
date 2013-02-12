SQLUnit-5.0-Fork
================

This is SQLUnit-5.0 distro from the http://sqlunit.sourceforge.net/ download page. I am putting it here to keep track of my additions/modifications to the source for others to use if they want.

So far, the only change that was made was the addition of an ETL tag. This tag is used to launch ETL jobs/workflows during the setup process of the SQLUnit tests. You can only include the ETL tag in the setup tag AFTER SQL tags are used.

The ETL tag currently takes a few options to launch jobs. The attributes are as follows:

* name - Name of the object that you wish to launch.
* tool -  Name of the tool (definied in the batt-config.xml settings file).
* objectType - The type of object htat you are launching (also defined in the batt-config.xml file).
* guid - The guid of the object you are launching.

Currently all four options are required, but this is subject to change.

Example tag that would go into a SQLUnit test:

    <etl name="test_job" tool="bods" objectType="J" guid="abcdefg" />

The core functionality for the ETL tag is coming from another library that is currently in development. That application library can be found here: https://github.com/csutherl/BIAutomationTool
