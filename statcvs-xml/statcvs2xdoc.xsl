<?xml version="1.0" encoding="UTF-8"?>

<!--
   A simple XSL file from Tammo van Lessen
   Transforms Commitlog to xdoc
-->
<!-- 
	TODO: make the a href only if url-attrib is set!!! 
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:func="http://statcvs-xml.berlios.de/functions"
                xmlns:i18n="net.sf.statcvs.I18n">

  <xsl:template match="document">
    <document>
       <properties>
         <title><xsl:value-of select="@title"/></title>
       </properties>
       <body>
	     <xsl:apply-templates select="report"/>
       </body>
    </document>
  </xsl:template>

  <xsl:template match="report">
    <section name="{@name}">
	  <xsl:apply-templates select="*"/>
	</section>
  </xsl:template>

  <xsl:template match="authors">
     <table>
        <tr>
       	  <th><xsl:value-of select="i18n:tr('Author')"/></th>
          <th><xsl:value-of select="i18n:tr('Lines of Code')"/></th>
      	</tr>
		<xsl:apply-templates select="*"/>
     </table>
  </xsl:template>

  <xsl:template match="author">
	 <tr>
       <td><a href="user_{@name}.html"><xsl:value-of select="@name"/></a></td>
       <td>
         <xsl:value-of select="@loc"/>
         (<xsl:value-of select="@locPercent"/>)
       </td>
     </tr>
  </xsl:template>

  <xsl:template match="authorsPerFile">
     <table>
        <tr>
       	  <th><xsl:value-of select="i18n:tr('File')"/></th>
          <th><xsl:value-of select="i18n:tr('Authors')"/></th>
      	</tr>
		<xsl:for-each select="files/file">
		  <xsl:sort select="@authors" order="descending"/>
		  <tr>
            <td>
            <xsl:call-template name="func:make-link">
				<xsl:with-param name="text" select="@name"/>
				<xsl:with-param name="url" select="@url"/>
            </xsl:call-template>
			</td>
		    <td><xsl:value-of select="@authors"/></td>
		  </tr>
		</xsl:for-each>
     </table>
  </xsl:template>

  <xsl:template match="commit">
	 <tr>
       <td><xsl:value-of select="@date"/></td>
       <td><xsl:value-of select="@author"/></td>
       <td>
		  <b><xsl:value-of select="comment"/></b>
		  (<xsl:value-of select="@changedfiles"/><xsl:text> </xsl:text><xsl:value-of select="i18n:tr('Files changed')"/>,
		  <xsl:value-of select="@changedlines"/><xsl:text> </xsl:text><xsl:value-of select="i18n:tr('Lines changed')"/>)
		  <br/>
		  <xsl:for-each select="files/file">
              <!--<xsl:element name="a">
                   <xsl:attribute name="href"><xsl:value-of select="@url"/></xsl:attribute>
                   <xsl:value-of select="@directory"/>
                   <xsl:value-of select="@name"/><xsl:text> </xsl:text><xsl:value-of select="@revision"/>
              </xsl:element> -->
              <xsl:call-template name="func:make-link">
				<xsl:with-param name="text">
				   <xsl:value-of select="@directory"/>
                   <xsl:value-of select="@name"/><xsl:value-of select="@revision"/>
				</xsl:with-param>
				<xsl:with-param name="url" select="@url"/>
              </xsl:call-template>
              
              <xsl:if test="@action = 'added'">
                <font color="green"><xsl:text> </xsl:text><xsl:value-of select="i18n:tr('added')"/><xsl:text> </xsl:text><xsl:value-of select="@lines"/></font>
              </xsl:if>
              <xsl:if test="@action = 'deleted'">
                <font color="red"><xsl:text> </xsl:text><xsl:value-of select="i18n:tr('removed')"/></font>
              </xsl:if>
              <xsl:if test="@action = 'changed'">
                 (+<xsl:value-of select="@added"/>
                 -<xsl:value-of select="@removed"/>)
              </xsl:if>

              <br/>
		  </xsl:for-each>
       </td>
     </tr>
  </xsl:template>

  <xsl:template match="commitlog">
     <table>
        <tr>
       	  <th><xsl:value-of select="i18n:tr('Date')"/></th>
          <th><xsl:value-of select="i18n:tr('Author')"/></th>
       	  <th><xsl:value-of select="i18n:tr('File/Message')"/></th>
      	</tr>
		<xsl:apply-templates select="*"/>
     </table>
  </xsl:template>

  <xsl:template match="pager">
     <p>
     <xsl:if test="@current!=1">
         <xsl:element name="a">
             <!-- dont break the line -->
             <xsl:attribute name="href"><xsl:value-of select="page[@nr=(../@current)-1]/@filename"/>.html</xsl:attribute>
             <xsl:text>&lt;&lt;</xsl:text>
         </xsl:element>
     </xsl:if>
	 <xsl:for-each select="page">
		<xsl:if test="@nr != ../@current">
		  <a href="{@filename}.html"><xsl:value-of select="@nr"/></a>
		</xsl:if>
		<xsl:if test="@nr = ../@current">
		  <xsl:value-of select="@nr"/>
		</xsl:if>
	 </xsl:for-each>
     <xsl:if test="@current!=@total">
	     <xsl:element name="a">
             <!-- dont break the line -->
		     <xsl:attribute name="href"><xsl:value-of select="page[@nr=(../@current)+1]/@filename"/>.html</xsl:attribute>
		     <xsl:text>&gt;&gt;</xsl:text>
	     </xsl:element>
     </xsl:if>
     </p>
  </xsl:template>
  
  <xsl:template name="link" match="link">
    <a href="{@ref}.html"><xsl:apply-templates /></a>
  </xsl:template>

  <xsl:template match="img">
    <p align="center"><img src="{@src}"/></p>
  </xsl:template>

  <xsl:template match="largestFiles">
     <table>
        <tr>
       	  <th><xsl:value-of select="i18n:tr('File')"/></th>
          <th><xsl:value-of select="i18n:tr('Lines of Code')"/></th>
      	</tr>
		<xsl:for-each select="files/file">
		  <tr>
            <td>
            <xsl:call-template name="func:make-link">
				<xsl:with-param name="text" select="@name"/>
				<xsl:with-param name="url" select="@url"/>
            </xsl:call-template>
			</td>
		    <td><xsl:value-of select="@loc"/></td>
		  </tr>
		</xsl:for-each>
     </table>
  </xsl:template>
  
  <xsl:template match="modules">
     <table>
        <tr>
       	  <th><xsl:value-of select="i18n:tr('Directory')"/></th>
          <th><xsl:value-of select="i18n:tr('Changes')"/></th>
          <th><xsl:value-of select="i18n:tr('Lines of Code')"/></th>
       	  <th><xsl:value-of select="i18n:tr('Lines per change')"/></th>
      	</tr>
		<xsl:apply-templates select="*"/>
     </table>
  </xsl:template>

  <xsl:template match="module">
	 <tr>
       <td><xsl:value-of select="@name"/></td>
       <td>
         <xsl:value-of select="@changes"/>
         (<xsl:value-of select="@changesPercent"/>)
       </td>
       <td>
         <xsl:value-of select="@lines"/> 
         (<xsl:value-of select="@linesPercent"/>)
       </td>
       <td><xsl:value-of select="@linesPerChange"/></td>
     </tr>
  </xsl:template>

  <xsl:template match="mostRecentFiles">
     <table>
        <tr>
       	  <th><xsl:value-of select="i18n:tr('File')"/></th>
       	  <th><xsl:value-of select="i18n:tr('Revisions')"/></th>
      	</tr>
		<xsl:for-each select="files/file">
		  <tr>
            <td>
            <xsl:call-template name="func:make-link">
				<xsl:with-param name="text" select="@name"/>
				<xsl:with-param name="url" select="@url"/>
            </xsl:call-template>
			</td>
		    <td><xsl:value-of select="@revisions"/></td>
		  </tr>
		</xsl:for-each>
     </table>
  </xsl:template>

  <xsl:template match="period">
    <p><xsl:value-of select="@name"/>: <xsl:value-of select="@from"/>
	<xsl:if test="@to"><xsl:text> </xsl:text>
      <xsl:value-of select="i18n:tr('to')"/><xsl:text> </xsl:text><xsl:value-of select="@to"/>
    </xsl:if>
    </p>
  </xsl:template>

  <xsl:template match="reports">
    <ul><xsl:apply-templates/></ul>
  </xsl:template>

  <xsl:template match="reports/link">
	 <li><xsl:call-template name="link"/></li>
  </xsl:template>

  <xsl:template match="value">
    <p><xsl:apply-templates/>: <xsl:value-of select="@value"/>
    </p>    
  </xsl:template>

  <!-- copy any other elements through -->
  <xsl:template match="*">
    <xsl:copy>
	  <xsl:apply-templates />
    </xsl:copy>
  </xsl:template>

  <!-- FUNCTIONS -->
  <xsl:template name="func:make-link">
    <xsl:param name="url"/>
    <xsl:param name="text"/>
	<xsl:choose>
		<xsl:when test="$url">
              <xsl:element name="a">
                <xsl:attribute name="href"><xsl:value-of select="$url"/></xsl:attribute>
                <xsl:value-of select="$text"/>
              </xsl:element>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$text"/>
		</xsl:otherwise>
	</xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>
