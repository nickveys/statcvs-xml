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

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.util.IntegerMap;

import org.jdom.Element;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.model.AuthorGrouper;
import de.berlios.statcvs.xml.model.Grouper;
import de.berlios.statcvs.xml.output.Report;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;
import de.berlios.statcvs.xml.util.Formatter;

/**
 * 
 * 
 * @author Steffen Pingel
 * @author Tammo van Lessen
 */
public class CommitTable {

	public static Report generate(CvsContent content, ReportSettings settings)
	{
		ReportElement root = new ReportElement(settings, I18n.tr("Commits%1"));
		createReport(root, settings.getRevisionIterator(content), new AuthorGrouper());
		return new Report(root);
	}
	
	/**
	 * 
	 */
	private static void createReport(ReportElement root, Iterator revs, Grouper grouper) 
	{
		// exit and ignore if report contains no data
		if (!revs.hasNext()) {
			root.setName("ignore");
			return;
		}
		
		IntegerMap changesMap = new IntegerMap();
		IntegerMap linesMap = new IntegerMap();
		IntegerMap linesAddedMap = new IntegerMap();
		
		while (revs.hasNext()) {
			CvsRevision rev = (CvsRevision)revs.next();
			Object group = grouper.getGroup(rev);
			if (group != null) {
				changesMap.addInt(group, 1);
				linesMap.addInt(group, rev.getLinesDelta());
				linesAddedMap.addInt(group, rev.getNewLines());
			} 
		}

		Element table = new Element("table");
		table.addContent(new Element("tr")
			.addContent(new Element("th").addContent("Name"))	
			.addContent(new Element("th").addContent("Commit"))
			.addContent(new Element("th").addContent("Baz")));
		Iterator it = linesMap.iteratorSortedByValueReverse();
		while (it.hasNext()) {
			Object group = it.next();
			Element row = new Element("row");
			row.addContent(new Element(grouper.getID()).addContent(grouper.getName(group)));
			row.addContent(new Element("commits").addContent(changesMap.get(group) + "")
				.setAttribute("percent", 
							  Formatter.formatNumber(changesMap.getPercent(group), 2)));
//			element.setAttribute("loc", linesMap.get(group) + "");
//			element.setAttribute("locPercent", 
//								 Formatter.formatNumber(linesMap.getPercent(group), 2));
//			element.setAttribute("locAdded", linesAddedMap.get(group) + "");
//			element.setAttribute("locAddedPercent", 
//								 Formatter.formatNumber(linesAddedMap.getPercent(group), 2));
//			element.setAttribute("locPerChange", 
//								 Formatter.formatNumber(linesMap.get(group) / changesMap.get(group), 1));
			table.addContent(row);
		}
		root.addContent(table);
	}

}

