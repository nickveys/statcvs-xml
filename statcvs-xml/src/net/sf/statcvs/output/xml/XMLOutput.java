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
    
	$RCSfile: XMLOutput.java,v $
	$Date: 2003-06-25 12:19:19 $ 
*/
package net.sf.statcvs.output.xml;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.util.FileUtils;

import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;

/**
 * Writes xml files to disk.
 *  
 * @author Steffen Pingel
 */
public class XMLOutput implements DocumentRenderer {

	private static Logger logger
		= Logger.getLogger("net.sf.statcvs.output.XMLOutput");

	private XMLOutputter out;
	private Transformer transformer;

	public XMLOutput(Transformer transformer) {
		this.transformer = transformer;

		out = new XMLOutputter();
		out.setTextNormalize(true);
		out.setIndent("  ");
		out.setNewlines(true);
	}

	public XMLOutput() {
		this(null);
	}

	public static void generate(CvsContent content) throws IOException
	{
		XMLSuite.generate(content, new XMLOutput());
	}

	public void render(StatCvsDocument document) throws IOException {
		
		if (document instanceof Pageable) {
			renderPages(document);
		} else {
			renderSingle(document);
		}
		
	}

	private void renderSingle(StatCvsDocument document) throws IOException {
		FileWriter writer = new FileWriter
			(FileUtils.getFilenameWithDirectory(document.getFilename()+".xml"));

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
		document.getCharts();				
	}
	
	private void renderPages(StatCvsDocument document) throws IOException {
		AbstractPageableDocument p = (AbstractPageableDocument)document;
		p.setItemsPerPage(OutputSettings.getInstance().get("itemsPerPage", 10));
		for (int i=0; i < p.getPageCount(); i++) {
			renderSingle(new StatCvsDocument(p.getPage(i), p.getFilename(i)));					
		}
	}
}
