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

package de.berlios.statcvs.xml.report;

import java.util.Iterator;
import java.util.Map;

import net.sf.statcvs.model.CvsContent;

import org.jdom.Element;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.DocumentSuite;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * 
 * 
 * @author Steffen Pingel
 */
public class DocumentTable {

	public static ReportElement generate(CvsContent content, ReportSettings settings)
	{
		ReportElement root = new ReportElement(I18n.tr("Reports"));
		Element list = new Element("reports");
		root.addContent(list);
		Map documentTitleByFilename = DocumentSuite.getDocuments();
		for (Iterator it = documentTitleByFilename.keySet().iterator(); it.hasNext();) {
			String filename = (String)it.next();
			Element element = new Element("link");
			element.setAttribute("ref", filename);
			element.setText((String)documentTitleByFilename.get(filename));
			list.addContent(element);
		}
		return root;
	}
	
}
