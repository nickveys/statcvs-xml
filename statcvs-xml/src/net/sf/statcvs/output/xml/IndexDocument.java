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
	Created on $Date: 2003-06-18 17:36:00 $ 
*/
package net.sf.statcvs.output.xml;

import net.sf.statcvs.Messages;
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
	private CvsContent cvsContent;
	
	/**
	 */
	public IndexDocument(CvsContent content) {
		super("index");

		this.cvsContent = content;

		Element root = new Element("document");
		root.setAttribute("title", "Development statistics for " 
						  + content.getModuleName());
		setRootElement(root);

		root.addContent(createReportRefs());
		root.addContent(createLOCReport());
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
		String projectName = cvsContent.getModuleName();
		String subtitle = Messages.getString("TIME_LOC_SUBTITLE");
		RevisionIterator it
			= new RevisionSortIterator(cvsContent.getRevisionIterator());
		BasicTimeSeries series = getTimeSeriesFromIterator(it, subtitle);
		if (series == null) {
			return null;
		}
		return new LOCChart(series, projectName, subtitle, filename, 
							width, height);
	}

	private Element createReportRefs() {
		Element reportRoot = new Element("report");
		reportRoot.setAttribute("name", "Modules");

		Element element = new Element("period");
		element.setAttribute("from", 
							 DateUtils.formatDate(cvsContent.getFirstDate()));
		element.setAttribute("to", 
							 DateUtils.formatDate(cvsContent.getLastDate()));
		reportRoot.addContent(element);

		element = new Element("generated");
		element.setAttribute("date", DateUtils.currentDate());
		
		reportRoot.addContent(element);

		reportRoot.addContent
			(createReportRef("authors", 
							  Messages.getString("CPU_TITLE")));
		reportRoot.addContent
			(createReportRef("commit_log",
							  Messages.getString("COMMIT_LOG_TITLE")));
		reportRoot.addContent
			(createReportRef("loc",
							  Messages.getString("LOC_TITLE")));
		reportRoot.addContent
			(createReportRef("file_sizes",
							  Messages.getString("FILE_SIZES_TITLE")));
		reportRoot.addContent
			(createReportRef("dir_sizes",
							  Messages.getString("DIRECTORY_SIZES_TITLE")));
		return reportRoot;
	}

	private Element createReportRef(String module, String text) {
		Element element = new Element("link");
		element.setAttribute("ref", module);
		element.setText(text);
		return element;
	}

	private BasicTimeSeries getTimeSeriesFromIterator
		(RevisionIterator it, String title) {
		LOCSeriesBuilder locCounter = new LOCSeriesBuilder(title, true);
		while (it.hasNext()) {
			locCounter.addRevision(it.next());
		}
		return locCounter.getTimeSeries();
	}

	private Element createLOCReport() {
		Element reportRoot = new Element("report");
		reportRoot.setAttribute("name", "Lines Of Code");

		Element element = new Element("img");
		element.setAttribute("src", LOC_IMAGE_FILENAME);
		reportRoot.addContent(element);

		element = new Element("loc");
		element.setAttribute("total", cvsContent.getCurrentLOC() + "");
		element.setAttribute("date", 
							 DateUtils.formatDateAndTime(cvsContent.getLastDate()));
		reportRoot.addContent(element);

		return reportRoot;
	}

}
