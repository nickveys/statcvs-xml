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

import org.jdom.Element;

/**
 * @author Steffen Pingel
 */
public class ReportElement extends Element {

	private ReportElement(String name)
	{
		super("report");
		
		setAttribute("name", name);
	}
	
	public ReportElement(ReportSettings settings, String defaultTitle)
	{
		this(settings.getString("title", defaultTitle)
			.replaceAll("%1", settings.getSubtitlePostfix()));
	}
	
	/**
	 * Creates a report without a title.
	 */
	public ReportElement()
	{
		this("");
	}
	
	public void saveResources(File outputPath) throws IOException
	{
	}
	
}
