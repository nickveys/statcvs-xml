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
    
	$RCSfile: AuthorDocument.java,v $ 
	Created on $Date: 2003-06-20 10:17:07 $ 
*/
package net.sf.statcvs.output.xml;

import java.util.*;

import net.sf.statcvs.*;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.model.*;
import net.sf.statcvs.output.LOCSeriesBuilder;
import net.sf.statcvs.renderer.*;
import net.sf.statcvs.renderer.LOCChart;
import net.sf.statcvs.reports.*;
import net.sf.statcvs.util.*;

import org.jdom.Element;

import com.jrefinery.data.BasicTimeSeries;

/**
 * 
 * 
 * @author Steffen Pingel
 */
public class AuthorDocument extends StatCvsDocument {

	private Author author;
	private CvsContent content;
	private RevisionIterator userRevs;

	private String[] categoryNamesHours = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", 
		"8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", 
		"20", "21",	"22", "23" };

	private String[] categoryNamesDays = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", 
		"Thursday", "Friday", "Saturday" };
	
	/**
	 */
	public AuthorDocument(CvsContent content, Author author, String filename) {
		super("User statistics for " 
			  + content.getModuleName(), filename);

		this.content = content;
		this.author = author;

		this.userRevs = author.getRevisionIterator();

		RevisionIteratorSummary summary;
		summary = new RevisionIteratorSummary(content.getRevisionIterator());
		long totalChangeCount = summary.size();
		long totalLineCount = summary.getLineValue();

		summary = new RevisionIteratorSummary(userRevs);
		long userChangeCount = summary.size();
		long userLineCount = summary.getLineValue();

		double percent = (double)userChangeCount * 100 / totalChangeCount;
		getRootElement().addContent
			(new ValueElement("totalChanges", userChangeCount, percent,
							  I18n.tr("Total changes")));
		percent = (double)userLineCount * 100 / totalLineCount;
		getRootElement().addContent
			(new ValueElement("loc", userLineCount, percent, 
							  I18n.tr("Lines of code")));

		getRootElement().addContent(createModuleReport());
		getRootElement().addContent(createActivityReport());
		getRootElement().addContent(createCommitLog());
	}

	/**
	 * @see net.sf.statcvs.output.xml.StatCvsDocument#getCharts()
	 */
	public Chart[] getCharts() {
		return new Chart[] {
			createActivityByHourChart(), createActivityByDayChart(),
			createCodeDistributionChart(),
		};
	}

	private Chart createActivityByHourChart()
	{
		Chart chart = createActivityChart(userRevs, Messages.getString("ACTIVITY_TIME_FOR_AUTHOR_TITLE") + " " 
							+ author.getName(),	getActivityTimeChartFilename(), 
							categoryNamesHours);
		userRevs.reset();
		return chart;
	}

	private Chart createActivityByDayChart()
	{
		Chart chart = createActivityChart(userRevs, Messages.getString("ACTIVITY_DAY_FOR_AUTHOR_TITLE") + " " 
							+ author.getName(),	getActivityDayChartFilename(), 
							categoryNamesDays);
		userRevs.reset();
		return chart;
	}

	private Chart createActivityChart(RevisionIterator revIt, String title, String fileName, 
		String[] categoryNames) {
		return new BarChart(revIt, content.getModuleName(),
				title, fileName, categoryNames.length, categoryNames);
	}

	private Chart createCodeDistributionChart() {
		int totalLinesOfCode = 0;
		while (userRevs.hasNext()) {
			CvsRevision rev = userRevs.next();
			totalLinesOfCode += rev.getLineValue();
		}
		userRevs.reset();
		if (totalLinesOfCode == 0) {
			return null;
		}
		Chart chart = new PieChart(content, content.getModuleName(),
				Messages.getString("PIE_CODEDISTRIBUTION_SUBTITLE") + " " + author.getName(),
				getCodeDistributionChartFilename(),
				author, PieChart.FILTERED_BY_USER);
		userRevs.reset();
		return chart;
	}

	private Element createModuleReport() {
		Element reportRoot = new ReportElement(I18n.tr("Modules"));
		reportRoot.addContent
			(new ImageElement(getCodeDistributionChartFilename()));
		return reportRoot;
	}

	private Element createActivityReport()
	{
		Element reportRoot = new ReportElement(I18n.tr("Activity By Time"));
		reportRoot.addContent
			(new ImageElement(getActivityTimeChartFilename()));
		reportRoot.addContent
			(new ImageElement(getActivityDayChartFilename()));
		return reportRoot;
	}

	private Element createCommitLog() {
		Element reportRoot = new ReportElement(I18n.tr("Most Recent Commits"));

		return reportRoot;
	}

	/**
	 * @param author an author
	 * @return filename for author's activity by hour of day chart
	 */
	public String getActivityTimeChartFilename() {
		return "activity_time_"
			+ XMLSuite.escapeAuthorName(author.getName()) + ".png";
	}

	/**
	 * @param author an author
	 * @return filename for author's activity by day of week chart
	 */
	public String getActivityDayChartFilename() {
		return "activity_day_" 
			+ XMLSuite.escapeAuthorName(author.getName()) + ".png";
	}

	public String getCodeDistributionChartFilename() {
		return "module_sizes_" 
			+ XMLSuite.escapeAuthorName(author.getName()) + ".png";
	}

}
