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
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;

import de.berlios.statcvs.xml.util.StringHelper;

/**
 * Represents a document.
 *  
 * @author Steffen Pingel
 */
public class StatCvsDocument extends Document {

	private static int documentNumber = 0;
	private String filename;
	private String title;
	private ReportSettings settings;

	public StatCvsDocument(ReportSettings settings)
	{
		this.settings = settings;
		this.filename = StringHelper.escapeFilename(
			settings.getString("filename", "document_" + ++documentNumber)
			.replaceAll("%1", settings.getFilenameId())
			+ ((settings.getPageNr() == 0)?"":"_"+settings.getPageNr()));
		this.title = settings.getString("title", "").replaceAll("%1", settings.getSubtitlePostfix());
		
		Element root = new Element("document");
		root.setAttribute("title", this.title);
		root.setAttribute("name", this.filename);
		setRootElement(root);
	}

	/**
	 * 
	 */
	public void saveResources(File outputPath) throws IOException
	{
		for (Iterator it = getRootElement().getChildren().iterator(); it.hasNext();) {
			Object o = it.next();
			if (o instanceof ReportElement) {
				((ReportElement)o).saveResources(outputPath);
			}
		}
	}

	public String getFilename()
	{
		return filename;
	}

	public ReportSettings getSettings()
	{
		return settings;
	}

	public String getTitle()
	{
		return title;
	}

}


