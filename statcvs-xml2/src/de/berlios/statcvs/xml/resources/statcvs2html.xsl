<?xml version="1.0" encoding="UTF-8"?>
<!--
 *
 *  StatCvs-XML - XML output for StatCvs.
 *
 *  Copyright by Steffen Pingel, Tammo van Lessen.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  version 2 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *  @author Tammo van Lessen
 *  @author Steffen Pingel
 *
-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="html" indent="yes" encoding="UTF-8"/>
  <xsl:param name="ext"/>
  <xsl:param name="customCss"/>
  <xsl:include href="statcvs2xdoc.xsl"/>

  <xsl:template match="document">
    <html>
       <head>
         <title><xsl:value-of select="@title"/></title>
		 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		 <meta name="Generator" content="StatCvs-XML"/>
	     <link rel="stylesheet" href="statcvs.css" type="text/css"/>
		 <xsl:if test="$customCss != ''">
		   <link rel="stylesheet" href="{$customCss}" type="text/css"/>
		 </xsl:if>
       </head>
       <body>
	     <h1><xsl:value-of select="@title"/></h1>
		 <xsl:if test="@name != 'index'">
		   <a href="index{$ext}">Back to Index Page</a><br/>
		 </xsl:if>
	     <xsl:apply-templates select="report"/>
       </body>
    </html>
  </xsl:template>

  <xsl:template match="report">
    <xsl:if test="@name != ''">
      <h2><xsl:value-of select="@name"/></h2>
    </xsl:if>
	<xsl:apply-templates select="*"/>
  </xsl:template>

  <xsl:template match="row">
	<xsl:choose>
		<xsl:when test="position() mod 2 = 1">
	  	 	<tr class="even"><xsl:apply-templates /></tr>
		</xsl:when>
		<xsl:otherwise>
	  	 	<tr class="odd"><xsl:apply-templates /></tr>
		</xsl:otherwise>
	</xsl:choose>
  </xsl:template>  

</xsl:stylesheet>
