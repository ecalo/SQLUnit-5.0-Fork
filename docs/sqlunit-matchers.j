<!-- $Id: sqlunit-matchers.j,v 1.2 2004/09/22 20:16:27 spal Exp $ -->
<!-- $Source: /cvsroot/sqlunit/sqlunit/docs/sqlunit-matchers.j,v $ -->
<!-- XDoclet template used to generate Docbook XML for sqlunit-matchers.xml -->
<!-- which is an included entity in the sqlunit-book.xml file               -->
<para>
<table frame="all">
  <title>SQLUnit Matchers</title>
  <tgroup cols="3" align="left" colsep="1" rowsep="1">
    <colspec colname="c1" />
    <colspec colname="c2" />
    <colspec colname="c3" />
    <colspec colname="c4" />
    <thead>
      <row>
        <entry>Matcher Name</entry>
        <entry>Author</entry>
        <entry>Description</entry>
        <entry>Arguments</entry>
      </row>
    </thead>
    <tbody>
<XDtClass:forAllClasses>
      <row>
        <entry><XDtClass:className /></entry>
        <entry><XDtClass:classTagValue tagName="author" /></entry>
        <entry><XDtClass:classComment no-comment-signs="true" /></entry>
        <entry>
<XDtClass:forAllClassTags tagName="sqlunit.matcher.arg" tagKey="name">
          <para>
          <XDtClass:classTagValue tagName="sqlunit.matcher.arg" paramName="name" />&nbsp;:&nbsp;<XDtClass:classTagValue tagName="sqlunit.matcher.arg" paramName="description" />
</XDtClass:forAllClassTags>
          </para>
        </entry>
      </row>
</XDtClass:forAllClasses>
    </tbody>
  </tgroup>
</table>
</para>
