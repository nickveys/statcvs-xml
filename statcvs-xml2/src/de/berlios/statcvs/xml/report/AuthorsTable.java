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

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.util.IntegerMap;

import org.jdom.Element;

import de.berlios.statcvs.xml.I18n;
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
public class AuthorsTable {

	public static Report generate(CvsContent content, ReportSettings settings)
	{
		ReportElement root = new ReportElement(settings, I18n.tr("Authors"));
		createReport(root, settings.getRevisionIterator(content));
		return new Report(root);
	}
	
	/**
	 * 
	 */
	private static void createReport(ReportElement root, Iterator revs) 
	{
		Element authors = new Element("authors");
		
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
			changesMap.addInt(rev.getAuthor(), 1);
			linesMap.addInt(rev.getAuthor(), rev.getLinesDelta());
			linesAddedMap.addInt(rev.getAuthor(), rev.getNewLines()); 
		}
		
		Iterator it = linesMap.iteratorSortedByValueReverse();
		while (it.hasNext()) {
			Author author = (Author) it.next();
			Element element = new Element("author");
			element.setAttribute("name", author.getName());
			element.setAttribute("commits", changesMap.get(author) + "");
			element.setAttribute("commitsPercent", 
								 Formatter.formatNumber(changesMap.getPercent(author), 2));
			element.setAttribute("loc", linesMap.get(author) + "");
			element.setAttribute("locPercent", 
								 Formatter.formatNumber(linesMap.getPercent(author), 2));
			element.setAttribute("locAdded", linesAddedMap.get(author) + "");
			element.setAttribute("locAddedPercent", 
								 Formatter.formatNumber(linesAddedMap.getPercent(author), 2));
			element.setAttribute("locPerChange", 
								 Formatter.formatNumber(linesMap.get(author) / changesMap.get(author), 1));
			authors.addContent(element);
		}
		
		root.addContent(authors);
	}

}

