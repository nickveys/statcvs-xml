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
    
	$RCSfile: AuthorPage.java,v $ 
	Created on $Date: 2003-06-17 16:43:02 $ 
*/

package net.sf.statcvs.output;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CommitListBuilder;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.model.RevisionIteratorSummary;
import net.sf.statcvs.renderer.CommitLogRenderer;
import net.sf.statcvs.renderer.TableRenderer;
import net.sf.statcvs.reportmodel.Table;
import net.sf.statcvs.reports.DirectoriesForAuthorTableReport;
import net.sf.statcvs.reports.TableReport;

/**
 * @author anja
 */
public class AuthorPage extends HTMLPage {
	private static Logger logger = Logger.getLogger("net.sf.statcvs.output.UserPage");
	private Author author;
	private RevisionIterator userRevs;
	private boolean codeDistributionChartCreated;
	private int userChangeCount = 0;
	private int userLineCount = 0;
	private int totalChangeCount = 0;
	private int totalLineCount = 0;

	/**
	 * Method UserPage.
	 * @param content of the page
	 * @param author selected author
	 * @param codeDistributionChartCreated <tt>true</tt> if the code distribution
	 *                                     pie chart was created
	 * @throws IOException on error
	 */
	public AuthorPage(CvsContent content, Author author,
			boolean codeDistributionChartCreated) throws IOException {
		super(content);
		this.author = author;
		this.codeDistributionChartCreated = codeDistributionChartCreated;
		setFileName(HTMLOutput.getAuthorPageFilename(author));
		setPageName("User statistics for " + author.getName());
		logger.fine("creating author page for '" + author.getName() + "'");

		userRevs = author.getRevisionIterator();

		RevisionIteratorSummary summary;
		summary = new RevisionIteratorSummary(getContent().getRevisionIterator());
		totalChangeCount = summary.size();
		totalLineCount = summary.getLineValue();

		summary = new RevisionIteratorSummary(userRevs);
		userChangeCount = summary.size();
		userLineCount = summary.getLineValue();

		createPage();
	}

	protected void printBody() throws IOException {
		printBackLink();
		print(getAuthorInfo());
		print(getChangesSection());
		print(getLinesOfCodeSection());
		print(getModulesSection());
		print(getActivitySection());
		print(getLastCommits());
	}

	private String getActivitySection() {
		String result = "";
		result += h2(Messages.getString("ACTIVITY_TITLE"));
		result += p(img(HTMLOutput.getActivityTimeChartFilename(author), 500, 300));
		result += p(img(HTMLOutput.getActivityDayChartFilename(author), 500, 300));
		return result;
	}

	private String getAuthorInfo() {
		RevisionIteratorSummary summary = new RevisionIteratorSummary(userRevs);
		return HTMLTagger.getSummaryPeriod(
				summary.getFirstDate(),
				summary.getLastDate());
	}

	private String getChangesSection() {
		String result = h2("Total Changes");
		String percentage = getPercentage(totalChangeCount, userChangeCount); 
		result += p(userChangeCount + " (" + percentage + ")");
		return result;
	}

	private String getLinesOfCodeSection() {
		if (totalLineCount == 0) {
			return "";
		}
		String result = h2(Messages.getString("LOC_TITLE"));
		result += p(userLineCount + " (" + getPercentage(totalLineCount, userLineCount) + ")");
		return result;
	}

	private String getModulesSection() {
		String result = h2("Modules");
		if (codeDistributionChartCreated) {
			result += p(img(HTMLOutput.getCodeDistributionChartFilename(author), 640, 480));
		}
		TableReport report = 
				new DirectoriesForAuthorTableReport(getContent(), author);
		report.calculate();
		Table table = report.getTable();
		result += new TableRenderer(table).getRenderedTable();
		return result;
	}

	private String getLastCommits() {
		String result = h2(Messages.getString("MOST_RECENT_COMMITS"));
		List commits = new CommitListBuilder(userRevs).createCommitList();
		CommitLogRenderer renderer = new CommitLogRenderer(commits);
		result += renderer.renderMostRecentCommits(HTMLOutput.MOST_RECENT_COMMITS_LENGTH);
		return result;
	}

	/**
	 * returns the percentage of a given total count and the count
	 * @param totalCount
	 * @param count 
	 * @return String percentage string
	 */
	private String getPercentage(int totalCount, int count) {
		int percentTimes10 = (count * 1000) / totalCount;
		double percent = ((double) percentTimes10) / 10.0;
		return Double.toString(percent) + "%";
	}
}
