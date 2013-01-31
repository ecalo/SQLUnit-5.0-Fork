<!-- $Id: sqlunit-types.j,v 1.2 2004/09/16 01:21:22 spal Exp $ -->
<!-- $Source: /cvsroot/sqlunit/sqlunit/docs/sqlunit-types.j,v $ -->
<!-- XDoclet template used to generate Docbook XML for the sqlunit-types.xml -->
<!-- file which is an included entity in the sqlunit-book.xml file           -->
<para>
<table frame="all">
  <title>SQLUnit Supported Datatypes</title>
  <tgroup cols="6" align="left" colsep="1" rowsep="1">
    <colspec colname="c1" />
    <colspec colname="c2" />
    <colspec colname="c3" />
    <colspec colname="c4" />
    <colspec colname="c5" />
    <colspec colname="c6" />
    <colspec colname="c7" />
    <thead>
      <row>
        <entry>Type Class</entry>
        <entry>Type Name</entry>
        <entry>Server</entry>
        <entry>Allows Input</entry>
        <entry>Allows Output</entry>
        <entry>Is Sortable</entry>
        <entry>Wrapper for</entry>
      </row>
    </thead>
    <tbody>
<XDtClass:forAllClasses>
      <!-- <XDtClass:className /> -->
  <XDtClass:forAllClassTags tagName="sqlunit.typename" tagKey="name" superclasses="false">
      <row>
        <entry><XDtClass:classTagValue tagName="sqlunit.type" paramName="name" /></entry>
        <entry><XDtClass:classTagValue tagName="sqlunit.typename" paramName="name" /></entry>
        <entry><XDtClass:classTagValue tagName="sqlunit.typename" paramName="server" /></entry>
        <entry><XDtClass:classTagValue tagName="sqlunit.type" paramName="input" /></entry>
        <entry><XDtClass:classTagValue tagName="sqlunit.type" paramName="output" /></entry>
        <entry><XDtClass:classTagValue tagName="sqlunit.type" paramName="sortable" /></entry>
        <entry><XDtClass:classTagValue tagName="sqlunit.type" paramName="wraps" /></entry>
      </row>
  </XDtClass:forAllClassTags>
</XDtClass:forAllClasses>
    </tbody>
  </tgroup>
</table>
</para>

