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
    
	$RCSfile: HTMLRenderer.java,v $
	$Date: 2003-07-06 13:58:07 $ 
*/
package net.sf.statcvs.output;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamSource;

import net.sf.statcvs.ConfigurationOptions;
import net.sf.statcvs.Main;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.output.xml.DocumentSuite;
import net.sf.statcvs.output.xml.util.HTMLOutputter;
import net.sf.statcvs.output.xml.util.XMLOutputter;
import net.sf.statcvs.util.FileUtils;


/**
 * HTMLRenderer
 * 
 * @author Tammo van Lessen
 */
public class HTMLRenderer extends XMLRenderer {

	private static Logger logger
		= Logger.getLogger("net.sf.statcvs.output.XMLRenderer");

	public HTMLRenderer(Transformer transformer) 
	{
		super(transformer);

		setExtension(".html");

		XMLOutputter xout = new HTMLOutputter();
		xout.setEncoding("ISO-8859-1");
		xout.setOmitDeclaration(true);
		xout.setOmitEncoding(true);
		setOutputter(xout);

		if (transformer != null) {
			logger.info("Using transformer "+transformer.getClass().getName());
		}
	}

	/**
	 * Copies the required resources.
	 */
	public void postRender()
	{
		copyResource("resources/folder.png");
		copyResource("resources/folder-deleted.png");

		copyResource("resources/statcvs.css");
		String filename = OutputSettings.getCustomCss();
		if (filename != null) {
			copyResource(filename);
		}
	}

	/**
	 * Invoked by Main.
	 */
	public static void generate(CvsContent content) 
		throws IOException 
	{
		StreamSource source = new StreamSource
			(FileUtils.getResource("resources/statcvs2html.xsl").toString());
		Transformer transformer;
 		try {
			transformer 
				= TransformerFactory.newInstance().newTransformer(source);
			
			// set stylesheet parameters
			transformer.setParameter("ext", ".html");
			String filename = OutputSettings.getCustomCss();
			if (filename != null) {
				transformer.setParameter
					("customCss", 
					 FileUtils.getFilenameWithoutPath(filename));
			}
				
			DocumentSuite.generate(content, new HTMLRenderer(transformer));
		} catch (TransformerConfigurationException e) {
			logger.warning(e.getMessageAndLocation());
		} catch (TransformerFactoryConfigurationError e) {
			logger.warning(e.getMessage());
		}
	}

//  	/**
//  	 * @see net.sf.statcvs.output.xml.DocumentRenderer#render(net.sf.statcvs.output.xml.StatCvsDocument)
//  	 */
//  	public void render(StatCvsDocument document) throws IOException 
//  	{
//  		super.render(document);
//  	}

}
