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
	$Date: 2003-06-27 18:34:33 $ 
*/package net.sf.statcvs.output.xml.report;

import java.util.HashMap;
import java.util.Map;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.output.xml.chart.AbstractChart;
import net.sf.statcvs.output.xml.chart.ActivityChart;
import net.sf.statcvs.output.xml.chart.AvgFileSizeChart;
import net.sf.statcvs.output.xml.chart.DirectorySizesChart;
import net.sf.statcvs.output.xml.chart.FileCountChart;

/**
 * CvsCharts
 * 
 * @author Tammo van Lessen
 */
public class CvsCharts {

	private Map userActByHourCharts = new HashMap();
	private Map userActByDayCharts = new HashMap();
	private Map userDirSizesCharts = new HashMap();
	private AbstractChart fileCountChart;
	private AbstractChart fileSizeChart;
	private AbstractChart dirSizeChart;
	private AbstractChart actByDayChart;
	private AbstractChart actByHourChart;
		
	private CvsContent content;

	public CvsCharts(CvsContent content) 
	{
		this.content = content;
	}

	public AbstractChart getFileCountChart() {
		if (fileCountChart == null) {
			fileCountChart = new FileCountChart(content);
		}
		return (fileCountChart.isRendered())?fileCountChart:null;
	}

	public AbstractChart getAvgFileSizeChart() {
		if (fileSizeChart == null) {
			fileSizeChart = new AvgFileSizeChart(content);
		}
		return (fileSizeChart.isRendered())?fileSizeChart:null;
	}

	public AbstractChart getLocChart() {
		// TODO
		return null;
	}

	public AbstractChart getDirectorySizesChart() {
		if (dirSizeChart == null) {
			dirSizeChart = new DirectorySizesChart(content);
		}
		return (dirSizeChart.isRendered())?dirSizeChart:null;
	}

	public AbstractChart getDirectorySizesChart(Author author) {
		AbstractChart dsc = (AbstractChart)userDirSizesCharts.get(author);
		if (dsc == null) {
			dsc = new DirectorySizesChart(author);
			userDirSizesCharts.put(author, dsc);
		}
		return (dsc.isRendered())?dsc:null;
	}

	public AbstractChart getLocPerAuthorChart() {
		// TODO
		return null;
	}

	public AbstractChart getActivityByDayChart() {
		if (actByDayChart == null) {
			actByDayChart = new ActivityChart(content, ActivityChart.BY_DAY);
		}
		return (actByDayChart.isRendered())?actByDayChart:null;
	}

	public AbstractChart getActivityByHourChart() {
		if (actByHourChart == null) {
			actByHourChart = new ActivityChart(content, ActivityChart.BY_HOUR);
		}
		return (actByHourChart.isRendered())?actByHourChart:null;
	}

	public AbstractChart getAuthorsActivityChart() {
		// TODO
		return null;
	}

	public AbstractChart getActivityByHourChart(Author author) {
		AbstractChart abh = (AbstractChart)userActByHourCharts.get(author);
		if (abh == null) {
			abh = new ActivityChart(author, ActivityChart.BY_HOUR);
			userActByHourCharts.put(author, abh);
		}
		return (abh.isRendered())?abh:null;
	}

	public AbstractChart getActivityByDayChart(Author author) {
		AbstractChart abd = (AbstractChart)userActByDayCharts.get(author);
		if (abd == null) {
			abd = new ActivityChart(author, ActivityChart.BY_DAY);
			userActByDayCharts.put(author, abd);
		}
		return (abd.isRendered())?abd:null;
	}

/*	private String[] categoryNamesHours = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", 
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
	private StackedBarChart authorsActivityChart;
	private LOCChart locChart;
	private Chart directorySizesChart;
	private Map userActByHour = new HashMap();
	private Map userActByDay = new HashMap();
	private Map userCodeDist = new HashMap();
	
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


	public Chart getAuthorsActivityChart() {
		if (authorsActivityChart == null) {
			authorsActivityChart = new StackedBarChart(content, content.getModuleName(), 
			Messages.getString("AUTHOR_ACTIVITY_TITLE"), "activity.png"); 
		}
		return authorsActivityChart;
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

	public Chart getLOCChart() {
		if (locChart == null) {
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
			locChart = new LOCChart(series, projectName, I18n.tr("Lines Of Code"),
							"loc_small.png", WIDTH, HEIGHT);
		}
		return locChart;
	}

	public Chart getDirectorySizesChart() {
		if (directorySizesChart == null) {
			directorySizesChart = new PieChart(content, content.getModuleName(),
				Messages.getString("PIE_MODSIZE_SUBTITLE"),
				"module_sizes.png", null, PieChart.FILTERED_BY_REPOSITORY);
		}
 // Testblock!
 
 try {
			new DirectorySizesChart(content).save();
			new ActivityChart(content, ActivityChart.BY_DAY).save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
 // TESTBLOCK
		return directorySizesChart;
	}
	
	public Chart getActivityByHourChart(Author author)
	{
		Chart abh = (Chart)userActByHour.get(author);
		if (abh == null) {
			RevisionIterator userRevs = author.getRevisionIterator();
			abh = createActivityChart(userRevs, Messages.getString("ACTIVITY_TIME_FOR_AUTHOR_TITLE") + " " 
					+ author.getName(),	
					"activity_time_"+ XMLSuite.escapeAuthorName(author.getName()) + ".png",
					categoryNamesHours);
			userActByHour.put(author, abh);
		}
		return abh;
	}

	public Chart getActivityByDayChart(Author author)
	{
		Chart abd = (Chart)userActByDay.get(author);
		if (abd == null) {
			RevisionIterator userRevs = author.getRevisionIterator();
			abd = createActivityChart(userRevs, Messages.getString("ACTIVITY_DAY_FOR_AUTHOR_TITLE") + " " 
					+ author.getName(),	
					"activity_day_"+ XMLSuite.escapeAuthorName(author.getName()) + ".png", 
					categoryNamesDays);
			userActByDay.put(author, abd);
		}
		return abd;
	}

	
	public Chart getCodeDistributionChart(Author author) {
		Chart cdc = (Chart)userCodeDist.get(author);
		if (cdc == null) {
			RevisionIterator userRevs = author.getRevisionIterator();
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
					"module_sizes_" + XMLSuite.escapeAuthorName(author.getName()) + ".png",
					author, PieChart.FILTERED_BY_USER);
			userRevs.reset();
			userCodeDist.put(author, cdc);
		}
		return cdc;
	}
*/
}
