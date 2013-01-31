<!-- $Id: sqlunit-tags.j,v 1.2 2004/07/22 00:32:16 spal Exp $ -->
<!-- $Source: /cvsroot/sqlunit/sqlunit/docs/sqlunit-tags.j,v $ -->
<!-- This is the XDoclet template used to generate Docbook XML for the -->
<!-- the sqlunit-tags.xml file, which is an included entity for the    -->
<!-- sqlunit-book.xml Docbook file                                     -->
<XDtClass:forAllClasses>
  <!-- <XDtClass:className /> -->
  <section>
    <title>The <XDtClass:classTagValue tagName="sqlunit.element" paramName="name" /> Tag</title>
    <anchor id="<XDtClass:classTagValue tagName="sqlunit.element" paramName="name" />" />
    <section>
      <title>Description</title>
      <para><XDtClass:classTagValue tagName="sqlunit.element" paramName="description" /></para>
    </section>
    <section>
      <title>Syntax</title>
      <para><programlisting>
<XDtClass:classTagValue tagName="sqlunit.element" paramName="name" />&nbsp;::=&nbsp;<XDtClass:classTagValue tagName="sqlunit.element" paramName="syntax" />
      </programlisting></para>
    </section>
    <section>
      <title>Parent Elements</title>
        <para>
<XDtClass:ifDoesntHaveClassTag tagName="sqlunit.parent">
None. This is the top level element.
</XDtClass:ifDoesntHaveClassTag>
<XDtClass:ifHasClassTag tagName="sqlunit.parent" superclasses="false">
<XDtClass:forAllClassTags tagName="sqlunit.parent" tagKey="name">
          <link linkend="<XDtClass:classTagValue tagName="sqlunit.parent" paramName="name" />"><XDtClass:classTagValue tagName="sqlunit.parent" paramName="name" /></link>&nbsp;
</XDtClass:forAllClassTags>
</XDtClass:ifHasClassTag>
       </para>
    </section>
    <section>
      <title>Attributes</title>
      <para>
<XDtClass:ifDoesntHaveClassTag tagName="sqlunit.attrib">
None
</XDtClass:ifDoesntHaveClassTag>
<XDtClass:ifHasClassTag tagName="sqlunit.attrib" superclasses="false">
        <table frame="all">
          <title>Attributes for <XDtClass:classTagValue tagName="sqlunit.element" paramName="name" /></title>
          <tgroup cols="3" align="left" colsep="1" rowsep="1">
            <colspec colname="c1" />
            <colspec colname="c2" />
            <colspec colname="c3" />
            <thead>
              <row>
                <entry>Name</entry>
                <entry>Description</entry>
                <entry>Required</entry>
              </row>
            </thead>
            <tbody>
<XDtClass:forAllClassTags tagName="sqlunit.attrib" tagKey="name">
              <row>
                <entry>
                  <XDtClass:classTagValue tagName="sqlunit.attrib" paramName="name" />
                </entry>
                <entry>
                  <XDtClass:classTagValue tagName="sqlunit.attrib" paramName="description" />
                </entry>
                <entry>
                  <XDtClass:classTagValue tagName="sqlunit.attrib" paramName="required" />
                </entry>
              </row>
</XDtClass:forAllClassTags>
            </tbody>
          </tgroup>
        </table>
</XDtClass:ifHasClassTag>
      </para>
    </section>
    <section>
      <title>Nested Elements</title>
      <para>
<XDtClass:ifDoesntHaveClassTag tagName="sqlunit.child">
None
</XDtClass:ifDoesntHaveClassTag>
<XDtClass:ifHasClassTag tagName="sqlunit.child" superclasses="false">
        <table frame="all">
          <title>Nested Elements for <XDtClass:classTagValue tagName="sqlunit.element" paramName="name" /></title>
          <tgroup cols="3" align="left" colsep="1" rowsep="1">
            <colspec colname="c1" />
            <colspec colname="c2" />
            <colspec colname="c3" />
            <thead>
              <row>
                <entry>Name</entry>
                <entry>Description</entry>
                <entry>Required</entry>
              </row>
            </thead>
            <tbody>
<XDtClass:forAllClassTags tagName="sqlunit.child" superclasses="false">
              <row>
                <entry>
<XDtClass:ifClassTagValueEquals tagName="sqlunit.child" paramName="ref" value="none">
                  <XDtClass:classTagValue tagName="sqlunit.child" paramName="name" />
</XDtClass:ifClassTagValueEquals>
<XDtClass:ifClassTagValueNotEquals tagName="sqlunit.child" paramName="ref" value="none">
                  <link linkend="<XDtClass:classTagValue tagName="sqlunit.child" paramName="ref" />"><XDtClass:classTagValue tagName="sqlunit.child" paramName="name" /></link>
</XDtClass:ifClassTagValueNotEquals>
                </entry>
                <entry>
                  <XDtClass:classTagValue tagName="sqlunit.child" paramName="description" />
                </entry>
                <entry>
                  <XDtClass:classTagValue tagName="sqlunit.child" paramName="required" />
                </entry>
              </row>
</XDtClass:forAllClassTags>
            </tbody>
          </tgroup>
        </table>
</XDtClass:ifHasClassTag>
      </para>
    </section>
<XDtClass:ifHasClassTag tagName="sqlunit.changelog" superclasses="false">
    <section>
      <title>Change Log</title>
<XDtClass:forAllClassTags tagName="sqlunit.changelog">
        <para>Version <XDtClass:classTagValue tagName="sqlunit.changelog" paramName="version" />: <XDtClass:classTagValue tagName="sqlunit.changelog" paramName="description" /></para>
</XDtClass:forAllClassTags>
    </section>
</XDtClass:ifHasClassTag>
<XDtClass:ifHasClassTag tagName="sqlunit.example" superclasses="false">
    <section>
      <title>Examples</title>
<XDtClass:forAllClassTags tagName="sqlunit.example" superclasses="false">
      <para><XDtClass:classTagValue tagName="sqlunit.example" paramName="name" /></para>
      <para><programlisting><![CDATA[
<XDtClass:classTagValue tagName="sqlunit.example" paramName="description" />]]>
      </programlisting></para>
</XDtClass:forAllClassTags>
    </section>
</XDtClass:ifHasClassTag>
  </section>
</XDtClass:forAllClasses>
