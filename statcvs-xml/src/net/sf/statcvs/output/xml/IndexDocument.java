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
	Created on $Date: 2003-06-17 23:59:52 $ 
*/package net.sf.statcvs.output.xml;

import java.util.Iterator;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.*;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.renderer.*;
import net.sf.statcvs.renderer.PieChart;
import net.sf.statcvs.output.*;
import net.sf.statcvs.util.*;

import org.jdom.Element;

import com.jrefinery.data.BasicTimeSeries;

/**
 * The index document. Contains links to all other documents.
 * 
 * @author Steffen Pingel
 */
public class IndexDocument extends StatCvsDocument {

	private CvsContent content;
	
	/**
	 */
	public IndexDocument(CvsContent content) {
		super("index");

		this.content = content;

		Element root = new Element("document");
		root.setAttribute("title", "Development statistics for " 
						  + content.getModuleName());
		setRootElement(root);

		root.addContent(getModulesReport(content));
		root.addContent(getLOCReport(content));
	}

	/**
	 * @see net.sf.statcvs.output.xml.StatCvsDocument#getCharts()
	 */
	public Chart[] getCharts() {
		return new Chart[] {
			createLOCChart(content, "loc_small.png", 400, 300),
		};
	}

	private static Chart createLOCChart(CvsContent content, String filename,
										int width, int height) {
		String projectName = content.getModuleName();
		String subtitle = Messages.getString("TIME_LOC_SUBTITLE");
		RevisionIterator it
			= new RevisionSortIterator(content.getRevisionIterator());
		BasicTimeSeries series = getTimeSeriesFromIterator(it, subtitle);
		if (series == null) {
			return null;
		}
		return new LOCChart(series, projectName, subtitle, filename, 
							width, height);
	}

	private static Element getModulesReport(CvsContent content) {
		Element reportRoot = new Element("report");
		reportRoot.setAttribute("name", "Modules");

		Element element = new Element("period");
		element.setAttribute("from", 
							 DateUtils.formatDate(content.getFirstDate()));
		element.setAttribute("to", 
							 DateUtils.formatDate(content.getLastDate()));
		reportRoot.addContent(element);

		element = new Element("generated");
		element.setAttribute("date", DateUtils.currentDate());
		reportRoot.addContent(element);

		element = new Element("modules");
		element.addContent
			(getModuleElement(content, "authors", 
							  Messages.getString("CPU_TITLE")));
		element.addContent
			(getModuleElement(content, "commit_log",
							  Messages.getString("COMMIT_LOG_TITLE")));
		element.addContent
			(getModuleElement(content, "loc",
							  Messages.getString("LOC_TITLE")));
		element.addContent
			(getModuleElement(content, "file_sizes",
							  Messages.getString("FILE_SIZES_TITLE")));
		element.addContent
			(getModuleElement(content, "dir_sizes",
							  Messages.getString("DIRECTORY_SIZES_TITLE")));
		reportRoot.addContent(element);

		return reportRoot;
	}

	private static Element getModuleElement(CvsContent content, 
											String module, String text) {
		Element element = new Element("module");
		element.setAttribute("name", module);
		element.setText(text);
		return element;
	}

	public static BasicTimeSeries getTimeSeriesFromIterator
		(RevisionIterator it, String title) {
		LOCSeriesBuilder locCounter = new LOCSeriesBuilder(title, true);
		while (it.hasNext()) {
			locCounter.addRevision(it.next());
		}
		return locCounter.getTimeSeries();
	}

	private static Element getLOCReport(CvsContent content) {
		Element reportRoot = new Element("report");
		reportRoot.setAttribute("name", "Lines Of Code");

		Element element = new Element("img");
		element.setAttribute("src", "loc_small.png");
		reportRoot.addContent(element);

		element = new Element("loc");
		element.setAttribute("total", content.getCurrentLOC() + "");
		element.setAttribute("date", 
							 DateUtils.formatDateAndTime(content.getLastDate()));
		reportRoot.addContent(element);

		return reportRoot;
	}

}
