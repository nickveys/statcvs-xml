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
    
	$RCSfile: AuthorsReport.java,v $
	$Date: 2003-06-28 11:12:27 $ 
*/
package net.sf.statcvs.output.xml.report;

import java.util.Iterator;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.util.Formatter;
import net.sf.statcvs.util.IntegerMap;

import org.jdom.Element;

/**
 * 
 * 
 * @author Steffen Pingel
 * @author Tammo van Lessen
 */
public class AuthorsReport extends ReportElement {

	private IntegerMap changesMap = new IntegerMap();
	private IntegerMap linesMap = new IntegerMap();
	
	/**
	 * 
	 */
	public AuthorsReport(CvsContent content) 
	{
		super(I18n.tr("Authors"));
		RevisionIterator revs = content.getRevisionIterator();
		createReport(revs);
	}

	/**
	 * 
	 */
	public AuthorsReport(Directory dir) 
	{
		super(I18n.tr("Authors"));
		RevisionIterator revs = dir.getRevisionIterator();
		createReport(revs);
	}

	/**
	 * 
	 */
	private void createReport(RevisionIterator revs) {
		Element authors = new Element("authors");
		
		// exit and ignore if report contains no data
		if (!revs.hasNext()) {
			setName("ignore");
			return;
		}
		
		while (revs.hasNext()) {
			CvsRevision rev = revs.next();
			changesMap.addInt(rev.getAuthor(), 1);
			linesMap.addInt(rev.getAuthor(), rev.getLineValue()); 
		}
		Iterator it = linesMap.iteratorSortedByValueReverse();

		while (it.hasNext()) {
			Author author = (Author) it.next();
			Element element = new Element("author");
			element.setAttribute("name", author.getName());
			element.setAttribute("changes", changesMap.get(author) + "");
			element.setAttribute("loc", linesMap.get(author) + "");
			element.setAttribute("locPercent", 
								 Formatter.formatNumber(linesMap.getPercent(author), 2));
			element.setAttribute("changesPercent", 
								 Formatter.formatNumber(changesMap.getPercent(author), 2));

			element.setAttribute("locPerChange", 
								 Formatter.formatNumber(linesMap.get(author) / changesMap.get(author), 1));
			authors.addContent(element);
		}
		addContent(authors);
	}

}

