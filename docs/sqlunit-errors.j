<!-- $Id: sqlunit-errors.j,v 1.2 2004/09/02 03:31:54 spal Exp $ -->
<!-- $Source: /cvsroot/sqlunit/sqlunit/docs/sqlunit-errors.j,v $ -->
<!-- The XDoclet template to generate Docbook XML for sqlunit-errors.xml  -->
<!-- which is an included entity in the sqlunit-book.xnl Docbook XML file -->
<para>
  <table frame="all">
    <title>SQLUnit Error Messages</title>
    <tgroup cols="2" align="left" colsep="1" rowsep="1">
      <colspec colname="c1" />
      <colspec colname="c2" />
      <thead>
        <row>
          <entry>Error Message</entry>
          <entry>Corrective Action</entry>
        </row>
      </thead>
      <tbody>
<XDtClass:forAllClasses>
<XDtField:forAllFields>
      <!-- <XDtField:fieldName /> -->
        <row>
          <entry><XDtField:fieldTagValue tagName="sqlunit.error" paramName="name" /></entry>
          <entry><XDtField:fieldTagValue tagName="sqlunit.error" paramName="description" /></entry>
        </row>
</XDtField:forAllFields>
</XDtClass:forAllClasses>
      </tbody>
    </tgroup>
  </table>
</para>
