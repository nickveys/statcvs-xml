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
    
	$RCSfile: IndexDocument.java,v $ 
	Created on $Date: 2003-06-19 23:56:28 $ 
*/
package net.sf.statcvs.output.xml;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.model.RevisionSortIterator;
import net.sf.statcvs.output.LOCSeriesBuilder;
import net.sf.statcvs.renderer.Chart;
import net.sf.statcvs.renderer.LOCChart;
import net.sf.statcvs.util.DateUtils;

import org.jdom.Element;

import com.jrefinery.data.BasicTimeSeries;

/**
 * The index document. Contains links to all other documents.
 * 
 * @author Steffen Pingel
 */
public class IndexDocument extends StatCvsDocument {

	private static final String LOC_IMAGE_FILENAME = "loc_small.png";
	private CvsContent content;
	
	/**
	 */
	public IndexDocument(CvsContent content) {
		super("Development statistics for " 
			  + content.getModuleName(), "index");

		this.content = content;

		getRootElement().addContent(createReportRefs());
		getRootElement().addContent(createLOCReport());
	}

	/**
	 * @see net.sf.statcvs.output.xml.StatCvsDocument#getCharts()
	 */
	public Chart[] getCharts() {
		return new Chart[] {
			createLOCChart(LOC_IMAGE_FILENAME, 400, 300),
		};
	}

	private Chart createLOCChart(String filename,
								 int width, int height) {
		String projectName = content.getModuleName();
		RevisionIterator it
			= new RevisionSortIterator(content.getRevisionIterator());
		LOCSeriesBuilder locCounter 
			= new LOCSeriesBuilder(I18n.tr("Lines Of Code"), true);
		while (it.hasNext()) {
			locCounter.addRevision(it.next());
		}
		BasicTimeSeries series = locCounter.getTimeSeries();
		
		if (series == null) {
			return null;
		}
		return new LOCChart(series, projectName, I18n.tr("Lines Of Code"),
							filename, width, height);
	}

	private Element createReportRefs() {
		Element reportRoot = new Element("report");
		reportRoot.setAttribute("name", "Modules");

		reportRoot.addContent(new PeriodElement(I18n.tr("Summary Period"),
												content.getFirstDate(),
												content.getLastDate()));

		reportRoot.addContent(new PeriodElement(I18n.tr("Generated"),
												DateUtils.currentDate()));

		Element list = new Element("reports");
		reportRoot.addContent(list);

		list.addContent(new LinkElement("authors", I18n.tr("Authors")));
		list.addContent(new LinkElement("commit_log", I18n.tr("Commit Log")));
		list.addContent(new LinkElement("loc", I18n.tr("Lines Of Code")));
		list.addContent(new LinkElement("file_sizes", 
										I18n.tr("File Sizes And Counts")));
		list.addContent(new LinkElement("dir_sizes", 
										I18n.tr("Directory Sizes")));
							 
		return reportRoot;
	}

	private Element createLOCReport() {
		Element reportRoot = new Element("report");
		reportRoot.setAttribute("name", "Lines Of Code");

		reportRoot.addContent(new ImageElement (LOC_IMAGE_FILENAME));
		reportRoot.addContent
			(new ValueElement ("loc", content.getCurrentLOC(),
							   I18n.tr("Lines Of Code")));

//  		element.setAttribute("date", 
//  							 DateUtils.formatDateAndTime(content.getLastDate()));

		return reportRoot;
	}

//  	private Element createAuthorsReport()
//  	{
//  		calculateChangesAndLinesPerAuthor(getContent().getRevisionIterator());
//  		Iterator it = getLinesMap().iteratorSortedByValueReverse();
//  		for (int i = 0; i < 10; i++) {
//  			if (!it.hasNext()) {
//  				break;
//  			}
//  			Author author = (Author) it.next();
//  			authors.addValue(author);
//  			linesOfCode.addValue(getLinesMap().get(author));
//  		}
//  		linesOfCode.setSum(getLinesMap().sum());
//  	}

}
