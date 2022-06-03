<xsl:stylesheet
    version="1.0"
    xmlns:html="http://www.w3.org/1999/xhtml"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="xml"
              omit-xml-declaration="yes" /> <!-- xhtml is a form of xml -->

  <xsl:template match="/">
    <html>
      <head>
      </head>
      <body>
        <xsl:apply-templates select="*" />
      </body>
    </html>
  </xsl:template>

  <xsl:template match="html:*">
    <xsl:copy-of select="." />
  </xsl:template>

  <xsl:template match="*">
    <!--
    <xsl:processing-instruction name="xml-stylesheet">
      <xsl:text>href="</xsl:text>
      <xsl:value-of select="name()" />
      <xsl:text>.css" type="text/css"</xsl:text>
    </xsl:processing-instruction>
    -->
    <xsl:copy-of select="." />
  </xsl:template>

</xsl:stylesheet>
