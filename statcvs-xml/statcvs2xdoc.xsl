<?xml version="1.0" encoding="UTF-8"?>

<!--
   A simple XSL file from Tammo van Lessen
   Transforms Commitlog to xdoc
-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

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

  <xsl:template match="commit">
	 <tr>
       <td><xsl:value-of select="@date"/></td>
       <td><xsl:value-of select="@author"/></td>
       <td>
		  <b><xsl:value-of select="comment"/></b>
		  (<xsl:value-of select="@changedfiles"/> Files changed,
		  <xsl:value-of select="@changedlines"/> Lines changed)
		  <br/>
		  <xsl:for-each select="files/file">
              <xsl:element name="a">
                   <xsl:attribute name="href"><xsl:value-of select="@url"/></xsl:attribute>
                   <xsl:value-of select="@directory"/>
                   <xsl:value-of select="@name"/><xsl:text> </xsl:text><xsl:value-of select="@revision"/>
              </xsl:element>
              <xsl:if test="@action = 'added'">
                <font color="green">added <xsl:value-of select="@lines"/></font>
              </xsl:if>
              <xsl:if test="@action = 'deleted'">
                <font color="red">removed</font>
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
       	  <th>Date</th>
	      <th>Author</th>
          <th>File/Message</th>
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

  <xsl:template match="modules">
     <table>
        <tr>
       	  <th>Directory</th>
	      <th>Changes</th>
          <th>Lines of code</th>
          <th>Lines per change</th>
      	</tr>
		<xsl:apply-templates select="*"/>
     </table>
  </xsl:template>

  <xsl:template match="module">
	 <tr>
       <td><xsl:value-of select="@name"/></td>
       <td>
         <xsl:value-of select="@changes"/>
         (<xsl:value-of select="@changesPercent"/>%)
       </td>
       <td>
         <xsl:value-of select="@lines"/> 
         (<xsl:value-of select="@linesPercent"/>%)
       </td>
       <td><xsl:value-of select="@linesPerChange"/></td>
     </tr>
  </xsl:template>

  <xsl:template match="period">
    <p><xsl:value-of select="@name"/>: <xsl:value-of select="@from"/>
	<xsl:if test="@to">
	  to <xsl:value-of select="@to"/>
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

</xsl:stylesheet>
