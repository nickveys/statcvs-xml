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
    
	$RCSfile: HTMLOutput.java,v $
	$Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.output;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.statcvs.Main;
import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CommitListBuilder;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.model.RevisionSortIterator;
import net.sf.statcvs.renderer.BarChart;
import net.sf.statcvs.renderer.CommitLogRenderer;
import net.sf.statcvs.renderer.LOCChart;
import net.sf.statcvs.renderer.PieChart;
import net.sf.statcvs.renderer.StackedBarChart;
import net.sf.statcvs.renderer.TimeLineChart;
import net.sf.statcvs.reportmodel.TimeLine;
import net.sf.statcvs.reports.AbstractLocTableReport;
import net.sf.statcvs.reports.AvgFileSizeTimeLineReport;
import net.sf.statcvs.reports.FileCountTimeLineReport;
import net.sf.statcvs.util.FileUtils;

import com.jrefinery.data.BasicTimeSeries;

/**
 * This class creates HTML File output
 * 
 * @author Anja Jentzsch
 * @version $Id: HTMLOutput.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class HTMLOutput {

//	private static Logger logger = Logger.getLogger("net.sf.statcvs");

	/**
	 * Path to web distribution files inside the distribution JAR,
	 * relative to the {@link net.sf.statcvs.Main} class
	 */
	public static final String WEB_FILE_PATH = "web-files/";

	/**
	 * Filename for folder icon
	 */
	public static final String DIRECTORY_ICON = "folder.png";

	/**
	 * Filename for deleted folder icon
	 */
	public static final String DELETED_DIRECTORY_ICON = "folder-deleted.png";

	/**
	 * Filename for file icon
	 */
	public static final String FILE_ICON = "file.png";

	/**
	 * Filename for deleted file icon
	 */
	public static final String DELETED_FILE_ICON = "file-deleted.png";

	/**
	 * Width of file icons
	 */
	public static final int ICON_WIDTH = 15;

	/**
	 * Height of file icons
	 */
	public static final int ICON_HEIGHT = 13;

	/**
	 * Length for Most Recent Commits list in Author pages and Directory pages
	 */
	public static final int MOST_RECENT_COMMITS_LENGTH = 20;

	/**
	 * standard image width
	 */
	public static final int IMAGE_WIDTH = 640;

	/**
	 * standard image height
	 */
	public static final int IMAGE_HEIGHT = 480;

	/**
	 * small image width
	 */
	public static final int SMALL_IMAGE_WIDTH = 500;

	/**
	 * small image height
	 */
	public static final int SMALL_IMAGE_HEIGHT = 300;

	/**
	 * loc image width
	 */
	public static final int LOC_IMAGE_WIDTH = 400;

	/**
	 * loc image height
	 */
	public static final int LOC_IMAGE_HEIGHT = 300;

	/**
	 * number of authors per activity chart
	 */
	public static final int AUTHORS_PER_ACTIVITY_CHART = 20;	

	private String[] categoryNamesHours = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", 
		"8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", 
		"20", "21",	"22", "23" };

	private String[] categoryNamesDays = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", 
		"Thursday", "Friday", "Saturday" };
	
	private CvsContent content;

	/**
	 * Creates a new <tt>HTMLOutput</tt> object for the given
	 * repository
	 * @param content the repository
	 */
	public HTMLOutput(CvsContent content) {
		this.content = content;
	}

	/**
	 * Creates the Suite of HTML Files
	 * @throws IOException thrown if problems occur with the creation of files
	 */
	public void createHTMLSuite() throws IOException {
		ConfigurationOptions.getCssHandler().createOutputFiles();
		if (content.isEmpty()) {
			new NoFilesPage(content);
			return;
		}
		createIcon(DIRECTORY_ICON);
		createIcon(DELETED_DIRECTORY_ICON);
		createIcon(FILE_ICON);
		createIcon(DELETED_FILE_ICON);
		boolean authorsPageCreated = (content.getAuthors().size() > 1);
		boolean locImageCreated = createLOCChart();
		createFileCountChart();
		createModuleSizesChart();
		if (authorsPageCreated) {
			RevisionIterator revIt = content.getRevisionIterator();
			createActivityChart(revIt, Messages.getString("ACTIVITY_TIME_TITLE"),
					"activity_time.png", categoryNamesHours);
			revIt.reset();
			createActivityChart(revIt, Messages.getString("ACTIVITY_DAY_TITLE"),
					"activity_day.png", categoryNamesDays);
			createAuthorActivityChart();
			boolean locPerAuthorImageCreated = createLOCPerAuthorChart();
			new CPAPage(
					content,
					AbstractLocTableReport.SORT_BY_LINES,
					locPerAuthorImageCreated);
			new CPAPage(
					content,
					AbstractLocTableReport.SORT_BY_NAME,
					locPerAuthorImageCreated);
		}
		new IndexPage(content, locImageCreated, authorsPageCreated);
		new LOCPage(content, locImageCreated);
		new FileSizesPage(content);
		new DirectorySizesPage(content);
		createModulePagesAndCharts();
		createCommitLogPages();
		createAuthorPages();
	}

	/**
	 * @param revIt
	 * @param string
	 * @param string2
	 * @param categoryNamesDays
	 */
	private void createAuthorActivityChart() {
		new StackedBarChart(content, content.getModuleName(), 
			Messages.getString("AUTHOR_ACTIVITY_TITLE"), "activity.png");
	}

	/**
	 * Returns the filename for a direcotry
	 * @param directory a directory
	 * @return filename for the directory page
	 */
	public static String getDirectoryPageFilename(Directory directory) {
		return "module" + escapeDirectoryName(directory.getPath()) + ".html";
	}

	/**
	 * Returns the filename for a directory's LOC chart
	 * @param directory a directory
	 * @return filename for directory's LOC chart
	 */
	public static String getDirectoryLocChartFilename(Directory directory) {
		return "loc_module" + escapeDirectoryName(directory.getPath()) + ".png";		
	}

	/**
	 * @param author an author
	 * @return filename for author's page
	 */
	public static String getAuthorPageFilename(Author author) {
		return "user_" + escapeAuthorName(author.getName()) + ".html";
	}

	/**
	 * @param author an author
	 * @return filename for author's activity by hour of day chart
	 */
	public static String getActivityTimeChartFilename(Author author) {
		return "activity_time_" + escapeAuthorName(author.getName()) + ".png";
	}

	/**
	 * @param author an author
	 * @return filename for author's activity by day of week chart
	 */
	public static String getActivityDayChartFilename(Author author) {
		return "activity_day_" + escapeAuthorName(author.getName()) + ".png";
	}

	/**
	 * @param author an author
	 * @return filename for author's code distribution chart
	 */
	public static String getCodeDistributionChartFilename(Author author) {
		return "module_sizes_" + escapeAuthorName(author.getName()) + ".png";
	}

	private static String escapeDirectoryName(String directoryName) {
		if (!directoryName.startsWith("/")) {
			directoryName = "/" + directoryName;
		}
		return directoryName.substring(0, directoryName.length() - 1).replaceAll("/", "_");
	}
	
	/**
	 * Escapes evil characters in author's names. E.g. "#" must be escaped
	 * because for an author "my#name" a page "author_my#name.html" will be
	 * created, and you can't link to that in HTML
	 * @param authorName an author's name
	 * @return a version safe for creation of files and URLs
	 */
	private static String escapeAuthorName(String authorName) {
		return authorName.replaceAll("#", "_");
	}

	private void createIcon(String iconFilename) throws IOException {
		FileUtils.copyFile(
				Main.class.getResourceAsStream(WEB_FILE_PATH + iconFilename),
				new File(ConfigurationOptions.getOutputDir() + iconFilename));
	}

	private boolean createLOCChart() {
		String projectName = content.getModuleName();
		String subtitle = Messages.getString("TIME_LOC_SUBTITLE");
		RevisionIterator it = new RevisionSortIterator(content.getRevisionIterator());
		BasicTimeSeries series = getTimeSeriesFromIterator(it, subtitle);
		if (series == null) {
			return false;
		}
		new LOCChart(series, projectName, subtitle, "loc.png", 640, 480);
		new LOCChart(series, projectName, subtitle, "loc_small.png", 400, 300);
		return true;
	}

	private void createModulePagesAndCharts() throws IOException {
		Iterator it = content.getDirectories().iterator();
		while (it.hasNext()) {
			Directory dir = (Directory) it.next();
			boolean moduleImageCreated = createLOCChart(dir);
			new ModulePage(content, dir, moduleImageCreated);
		}
	}

	private void createAuthorPages() throws IOException {
		Collection authors = content.getAuthors();
		Iterator it = authors.iterator();
		while (it.hasNext()) {
			Author author = (Author) it.next();
			RevisionIterator revIt = author.getRevisionIterator();
			createActivityChart(revIt, Messages.getString("ACTIVITY_TIME_FOR_AUTHOR_TITLE") + " " 
					+ author.getName(),	getActivityTimeChartFilename(author), 
					categoryNamesHours);
			revIt.reset();
			createActivityChart(revIt, Messages.getString("ACTIVITY_DAY_FOR_AUTHOR_TITLE") + " " 
				+ author.getName(),	getActivityDayChartFilename(author), 
				categoryNamesDays);
			boolean chartCreated = createCodeDistributionChart(author);
			new AuthorPage(content, author, chartCreated);
		}
	}

	private void createCommitLogPages() throws IOException {
		RevisionIterator revisions =
			new RevisionSortIterator(content.getRevisionIterator());
		List commits = new CommitListBuilder(revisions).createCommitList();
		CommitLogRenderer logRenderer = new CommitLogRenderer(commits);
		int pages = logRenderer.getPages();
		for (int i = 1; i <= pages; i++) {
			new CommitLogPage(content, logRenderer, i, pages);
		}
	}

	private boolean createLOCChart(Directory dir) {
		RevisionIterator revs =
				new RevisionSortIterator(dir.getRevisionIterator());

		String subtitle = Messages.getString("TIME_LOC_SUBTITLE");
		BasicTimeSeries series = getTimeSeriesFromIterator(revs, subtitle);
		if (series == null) {
			return false;
		}
		String fileName = getDirectoryLocChartFilename(dir);
		new LOCChart(series, dir.getPath(), subtitle, fileName, 640, 480);
		return true;
	}

	private BasicTimeSeries getTimeSeriesFromIterator(RevisionIterator it, String title) {
		LOCSeriesBuilder locCounter = new LOCSeriesBuilder(title, true);
		while (it.hasNext()) {
			locCounter.addRevision(it.next());
		}
		return locCounter.getTimeSeries();
	}

	private void createFileCountChart() {
		List files = content.getFiles();
		TimeLine fileCount = new FileCountTimeLineReport(files).getTimeLine();
		new TimeLineChart(fileCount, content.getModuleName(),
				"file_count.png", IMAGE_WIDTH, IMAGE_HEIGHT);
		TimeLine avgFileSize = new AvgFileSizeTimeLineReport(files).getTimeLine();
		new TimeLineChart(avgFileSize, content.getModuleName(),
				"file_size.png", IMAGE_WIDTH, IMAGE_HEIGHT);
	}

	private void createModuleSizesChart() {
		new PieChart(content, content.getModuleName(),
				Messages.getString("PIE_MODSIZE_SUBTITLE"),
				"module_sizes.png", null, PieChart.FILTERED_BY_REPOSITORY);
	}

	private void createActivityChart(RevisionIterator revIt, String title, String fileName, 
		String[] categoryNames) {
		new BarChart(revIt, content.getModuleName(),
				title, fileName, categoryNames.length, categoryNames);
	}

	private boolean createLOCPerAuthorChart() {
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
			return false;
		}	 
		String projectName = content.getModuleName();
		String subtitle = Messages.getString("TIME_LOCPERAUTHOR_SUBTITLE");
		new LOCChart(seriesList, projectName, subtitle, "loc_per_author.png", 640, 480);
		return true;
	}
	
	private boolean createCodeDistributionChart(Author author) {
		RevisionIterator it = author.getRevisionIterator();
		int totalLinesOfCode = 0;
		while (it.hasNext()) {
			CvsRevision rev = it.next();
			totalLinesOfCode += rev.getLineValue();
		}
		if (totalLinesOfCode == 0) {
			return false;
		}
		new PieChart(content, content.getModuleName(),
				Messages.getString("PIE_CODEDISTRIBUTION_SUBTITLE") + " " + author.getName(),
				getCodeDistributionChartFilename(author),
				author, PieChart.FILTERED_BY_USER);
		return true;
	}
}