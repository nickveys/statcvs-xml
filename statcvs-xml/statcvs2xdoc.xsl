<?xml version="1.0" encoding="UTF-8"?>

<!--
   A simple XSL file from Tammo van Lessen
   Transforms Commitlog to xdoc
-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="/">
    <document>
       <properties>
       		<title>Commitlog Report</title>
       </properties>
       <body>
	   <section name="Commitlog Report">
	      <table>
    	    <tr>
        	  <th>Date</th>
	          <th>Author</th>
    	      <th>File/Message</th>
        	</tr>
       		<xsl:apply-templates select="report/commitlog/commit"/>
       	  </table>
	   </section>
       </body>
    </document>
  </xsl:template>

  <xsl:template match="report/commitlog/commit">
	 <tr>
       <td><xsl:value-of select="@date"/></td>
       <td><xsl:value-of select="@author"/></td>
       <td>
		  <p><xsl:value-of select="comment"/>
		  (<xsl:value-of select="@changedfiles"/> Files changed,
		  <xsl:value-of select="@changedlines"/> Lines changed)
		  </p>
		  <xsl:apply-templates select="files/file"/>
		  
       </td>
     </tr>
  </xsl:template>

  <xsl:template match="files/file">
    <xsl:value-of select="@directory"/>
    <xsl:value-of select="@name"/><xsl:text> </xsl:text><xsl:value-of select="@action"/>
	(+<xsl:value-of select="@added"/>
	 -<xsl:value-of select="@removed"/>)
	<br/>
  </xsl:template>

</xsl:stylesheet>
