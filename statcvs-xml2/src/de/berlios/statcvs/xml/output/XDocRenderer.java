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

import java.io.IOException;
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import net.sf.statcvs.model.CvsContent;
import de.berlios.statcvs.xml.util.FileHelper;

/**
 * Writes xml files to disk.
 *  
 * @author Steffen Pingel
 */
public class XDocRenderer {

	public static DocumentRenderer create(CvsContent content, ReportSettings settings) 
		throws IOException, TransformerException
	{
		URL url = FileHelper.getResource("resources/statcvs2xdoc.xsl");
		if (url == null) {
			throw new IOException("Stylesheet resources/statcvs2xdoc.xsl not found");
		}
		StreamSource source = new StreamSource(url.toString());
		Transformer transformer 
			= TransformerFactory.newInstance().newTransformer(source);
		transformer.setParameter("ext", ".html");
		
		return new XMLRenderer(transformer, settings.getOutputPath());
	}

}
