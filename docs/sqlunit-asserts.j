<!-- $Id: sqlunit-asserts.j,v 1.1 2004/09/02 03:16:23 spal Exp $ -->
<!-- $Source -->
<!-- XDoclet template used to generate Docbook XML for sqlunit-asserts.xml -->
<!-- which is an included entity in the sqlunit-book.xml file              -->
<para>
<table frame="all">
  <title>SQLUnit Assertions</title>
  <tgroup cols="3" align="left" colsep="1" rowsep="1">
    <colspec colname="c1" />
    <colspec colname="c2" />
    <colspec colname="c3" />
    <thead>
      <row>
        <entry>Assert-String</entry>
        <entry>Description</entry>
        <entry>Used In</entry>
      </row>
    </thead>
    <tbody>
<XDtClass:forAllClasses>
  <XDtMethod:forAllMethods>
    <XDtMethod:ifHasMethodTag tagName="sqlunit.assert" paramName="name">
      <!-- <XDtMethod:methodName /> -->
      <row>
        <entry><XDtMethod:methodTagValue tagName="sqlunit.assert" paramName="name" /></entry>
        <entry><XDtMethod:methodTagValue tagName="sqlunit.assert" paramName="description" /></entry>
        <entry><XDtMethod:methodTagValue tagName="sqlunit.assert" paramName="usage" /></entry>
      </row>
    </XDtMethod:ifHasMethodTag>
  </XDtMethod:forAllMethods>
</XDtClass:forAllClasses>
    </tbody>
  </tgroup>
</table>
</para>
