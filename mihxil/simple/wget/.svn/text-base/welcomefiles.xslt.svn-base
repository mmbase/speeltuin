<xsl:stylesheet
    xmlns:h="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version = "1.0"
    xmlns:str="http://exslt.org/strings"
    extension-element-prefixes="str"
    exclude-result-prefixes="xml"
    >

  <xsl:output method="xml"
              version="1.0"
              encoding="utf-8"
              indent="no"
              />

  <xsl:template match="h:*">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>

  <xsl:template match="h:a">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:attribute name="href"><xsl:value-of select="str:replace(@href, 'index.html', '.')" /></xsl:attribute>
      <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>


</xsl:stylesheet>
