package de.berlios.statcvs.xml.report;

import java.util.Iterator;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.util.IntegerMap;

import org.jdom.Element;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;
import de.berlios.statcvs.xml.util.Formatter;

/**
 * 
 * 
 * @author Steffen Pingel
 * @author Tammo van Lessen
 */
public class AuthorsReport {

	public static ReportElement generate(CvsContent content, ReportSettings settings)
	{
		ReportElement root = new ReportElement(I18n.tr("Authors"));
		createReport(root, settings.getRevisionIterator(content));
		return root;
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

		while (revs.hasNext()) {
			CvsRevision rev = (CvsRevision)revs.next();
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
		
		root.addContent(authors);
	}

}

