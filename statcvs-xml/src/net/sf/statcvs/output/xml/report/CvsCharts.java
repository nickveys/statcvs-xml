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
    
	$RCSfile: CvsCharts.java,v $
	$Date: 2003-06-26 23:04:55 $ 
*/package net.sf.statcvs.output.xml.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.model.RevisionSortIterator;
import net.sf.statcvs.output.LOCSeriesBuilder;
import net.sf.statcvs.renderer.BarChart;
import net.sf.statcvs.renderer.Chart;
import net.sf.statcvs.renderer.LOCChart;
import net.sf.statcvs.renderer.StackedBarChart;
import net.sf.statcvs.renderer.TimeLineChart;
import net.sf.statcvs.reportmodel.TimeLine;
import net.sf.statcvs.reports.AvgFileSizeTimeLineReport;
import net.sf.statcvs.reports.FileCountTimeLineReport;

import com.jrefinery.data.BasicTimeSeries;

/**
 * CvsCharts
 * 
 * @author Tammo van Lessen
 */
public class CvsCharts {

	private String[] categoryNamesHours = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", 
		"8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", 
		"20", "21",	"22", "23" };

	private String[] categoryNamesDays = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", 
		"Thursday", "Friday", "Saturday" };

	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public static final String FILE_COUNT_CHART_FILE = "file_count.png";
	public static final String FILE_SIZE_CHART_FILE = "file_size.png";
	
	private CvsContent content;
	private RevisionIterator revIt;
	
	private Chart fileCountChart;
	private Chart avgFileSizeChart;
	private Chart activityByHourChart;
	private Chart activityByDayChart;
	private LOCChart locPerAuthorChart;
	private StackedBarChart authorActivityChart;


	/**
	 * 
	 */
	public CvsCharts(CvsContent content) 
	{
		this.content = content;
		this.revIt = content.getRevisionIterator();
	}

	public Chart getFileCountChart() {
		if (fileCountChart == null) {
			List files = content.getFiles();
			TimeLine fileCount = new FileCountTimeLineReport(files).getTimeLine();
			fileCountChart = new TimeLineChart(fileCount, content.getModuleName(),
				FILE_COUNT_CHART_FILE, WIDTH, HEIGHT);
		}
		return fileCountChart;
	}
	
	public Chart getAvgFileSizeChart() {
		if (avgFileSizeChart == null) {
			List files = content.getFiles();
			TimeLine avgFileSize = new AvgFileSizeTimeLineReport(files).getTimeLine();
			avgFileSizeChart = new TimeLineChart(avgFileSize, content.getModuleName(),
				FILE_SIZE_CHART_FILE, WIDTH, HEIGHT);
		}
		return avgFileSizeChart;
	}
	
	public Chart getActivityByHourChart()
	{
		if (activityByHourChart == null) {
			activityByHourChart = createActivityChart
				(revIt, Messages.getString("ACTIVITY_TIME_TITLE"),	
			 	"activity_time.png", categoryNamesHours);
			revIt.reset();
		}
		return activityByHourChart;
	}

	public Chart getActivityByDayChart()
	{
		if (activityByDayChart == null) {
			activityByDayChart = createActivityChart
				(revIt, Messages.getString("ACTIVITY_DAY_TITLE"),
				 "activity_day.png", categoryNamesDays);
			revIt.reset();
		}
		return activityByDayChart;
	}

	private Chart createActivityChart(RevisionIterator revIt, String title, String fileName, 
		String[] categoryNames) {
		return new BarChart(revIt, content.getModuleName(),
				title, fileName, categoryNames.length, categoryNames);
	}

	public Chart getAuthorActivityChart() {
		if (authorActivityChart == null) {
			authorActivityChart = new StackedBarChart(content, content.getModuleName(), 
			Messages.getString("AUTHOR_ACTIVITY_TITLE"), "activity.png"); 
		}
		return authorActivityChart;
	}

	public Chart getLOCPerAuthorChart() {
		if (locPerAuthorChart == null) {
			Iterator authorsIt = content.getAuthors().iterator();
			Map authorSeriesMap = new HashMap();
			while (authorsIt.hasNext()) {
				Author author = (Author) authorsIt.next();
				authorSeriesMap.put(
						author,
						new LOCSeriesBuilder(author.getName(), false));
			}
			RevisionIterator allRevs = new RevisionSortIterator(content.getRevisionIterator());
			while (allRevs.hasNext()) {
				CvsRevision rev = allRevs.next();
				LOCSeriesBuilder builder =
						(LOCSeriesBuilder) authorSeriesMap.get(rev.getAuthor());
				builder.addRevision(rev);
			}
			List authors = new ArrayList(authorSeriesMap.keySet());
			Collections.sort(authors);
			List seriesList = new ArrayList();
			authorsIt = authors.iterator();
			while (authorsIt.hasNext()) {
				Author author = (Author) authorsIt.next();
				LOCSeriesBuilder builder = (LOCSeriesBuilder) authorSeriesMap.get(author);
				BasicTimeSeries series = builder.getTimeSeries();
				if (series != null) {
					seriesList.add(series);
				} 
			}
			if (seriesList.isEmpty()) {
				return null;
			}	 
			String projectName = content.getModuleName();
			String subtitle = Messages.getString("TIME_LOCPERAUTHOR_SUBTITLE");
			locPerAuthorChart = new LOCChart(seriesList, projectName, subtitle, "loc_per_author.png", 640, 480);
		}
		return locPerAuthorChart;
	}


}
