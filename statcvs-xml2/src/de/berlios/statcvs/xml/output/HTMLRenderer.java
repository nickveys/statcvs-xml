/*
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
 */
package de.berlios.statcvs.xml.output;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.util.FileUtils;
import de.berlios.statcvs.xml.util.FileHelper;


/**
 * HTMLRenderer
 * 
 * @author Tammo van Lessen
 */
public class HTMLRenderer extends XMLRenderer {

	private static Logger logger
		= Logger.getLogger("net.sf.statcvs.output.XMLRenderer");

	public HTMLRenderer(Transformer transformer, File outputPath) 
	{
		super(transformer, outputPath);

		setExtension(".html");

		XMLOutputter xout = new HTMLOutputter();
		xout.setEncoding("ISO-8859-1");
		xout.setOmitDeclaration(true);
		xout.setOmitEncoding(true);
		setOutputter(xout);
	}

	/**
	 * Copies the required resources.
	 */
	public void postRender()
	{
		super.postRender();
		
		FileHelper.copyResource("resources/statcvs.css", getOutputPath());
	}

	/**
	 * Invoked by Main.
	 */
	public static DocumentRenderer create(CvsContent content, ReportSettings settings) 
	{
		StreamSource source = new StreamSource
			(FileHelper.getResource("resources/statcvs2html.xsl").toString());
		Transformer transformer;

		transformer	= TransformerFactory.newInstance().newTransformer(source);
			
		// set stylesheet parameters
		transformer.setParameter("ext", ".html");
		String filename = settings.getString("customCss");
		if (filename != null) {
			transformer.setParameter
				("customCss", 
				 FileUtils.getFilenameWithoutPath(filename));
		}
		
		return new HTMLRenderer(transformer, settings.getOutputPath());		
	}

}
