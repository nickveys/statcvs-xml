/*
    StatCvs - CVS statistics generation 
    Copyright (C) 2002  Lukasz Pekacki <lukasz@pekacki.de>
    http://statcvs.sf.net/
    
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
    
	$RCSfile: XMLRenderer.java,v $
	$Date: 2003-07-06 13:58:07 $ 
*/
package net.sf.statcvs.output;

import java.io.*;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import net.sf.statcvs.*;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.output.xml.DocumentRenderer;
import net.sf.statcvs.output.xml.DocumentSuite;
import net.sf.statcvs.output.xml.chart.AbstractChart;
import net.sf.statcvs.output.xml.document.*;
import net.sf.statcvs.output.xml.util.XMLOutputter;
import net.sf.statcvs.util.FileUtils;

import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;

/**
 * Writes xml files to disk.
 *  
 * @author Steffen Pingel
 */
public class XMLRenderer implements DocumentRenderer {

	private static Logger logger
		= Logger.getLogger("net.sf.statcvs.output.XMLRenderer");

	private XMLOutputter out;
	private Transformer transformer;
	private String extension;
	
	public XMLRenderer(Transformer transformer) {
		this.transformer = transformer;
		setExtension(".xml");
		setOutputter(new XMLOutputter());

		if (transformer != null) {
			logger.info("Using transformer "+transformer.getClass().getName());
		}
	}

	public XMLRenderer() {
		this(null);
	}

	/**
	 * Invoked by Main.
	 */
	public static void generate(CvsContent content) throws IOException
	{
		DocumentSuite.generate(content, new XMLRenderer());
	}

	public boolean copyResource(String filename)
	{
		InputStream in = FileUtils.getResourceAsStream(filename);
		if (in != null) {
			try {
				String target = ConfigurationOptions.getOutputDir()
					+ FileUtils.getFilenameWithoutPath(filename);
				FileUtils.copyFile(in, new File(target));
				return true;
			} 
			catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}
		else {
			logger.warning("Resource not found: " + filename);
		}
		return false;
	}

	public void setOutputter(XMLOutputter outputter) {
		out = outputter;
		out.setTextNormalize(true);
		out.setIndent("  ");
		out.setNewlines(true);
	}
	
	public void setExtension(String ext) {
		this.extension = ext;
	}
	
	public String getExtension() {
		return this.extension;
	}
	
	public void render(StatCvsDocument document) throws IOException
	{
		logger.info("Rendering " + document.getFilename());
		if (document instanceof Pageable) {
			renderPages((Pageable)document);
		} else {
			renderSingle(document);
		}
		
	}
	
	private void renderPages(Pageable document) throws IOException
	{
		document.setItemsPerPage
			(OutputSettings.getInstance().get("itemsPerPage", 10));
		for (int i = 0; i < document.getPageCount(); i++) {
			renderSingle(document.getPage(i));					
		}
	}

	private void renderSingle(StatCvsDocument document) throws IOException 
	{
		FileWriter writer = new FileWriter
			(FileUtils.getFilenameWithDirectory
			 (document.getFilename() + extension));

		try {
			if (transformer != null) {
				JDOMResult result = new JDOMResult();
				try {
					transformer.transform(new JDOMSource(document), result);
				}
				catch (TransformerException e) {
					logger.warning("XSLT transformation failed: " + e);
				}
				out.output(result.getDocument(), writer);
			}
			else {
				out.output(document, writer);
			}
		}
		finally {
			writer.close();
		}	

		// create Charts
		AbstractChart[] charts = document.getCharts();
		if (charts != null) { 
			for (int i = 0; i < charts.length; i++) {
				if (charts[i] != null)
					charts[i].save();				
			}
		}
	}

	public void postRender()
	{
	}

}
