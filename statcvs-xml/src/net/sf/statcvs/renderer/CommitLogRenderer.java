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
    
	$Name: not supported by cvs2svn $ 
	Created on $Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Commit;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.output.ConfigurationOptions;
import net.sf.statcvs.output.HTMLTagger;
import net.sf.statcvs.output.WebRepositoryIntegration;
import net.sf.statcvs.util.OutputUtils;

/**
 * Class for rendering a list of commits as HTML.
 * 
 * @author Anja Jentzsch
 * @author Richard Cyganiak
 * @version $Id: CommitLogRenderer.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class CommitLogRenderer {

	private static final int PAGE_SIZE = 50;

	private List commits;
	private int page;
	private List pageCommits;
	private HashMap commitHashMap = new HashMap();
	private WebRepositoryIntegration webRepository;

	/**
	 * Creates a new instance for the list of commits.
	 * 
	 * @param commits A list of {@link Commit} objects
	 */
	public CommitLogRenderer(List commits) {
		this.commits = new ArrayList(commits);
		Collections.reverse(this.commits);
		webRepository = ConfigurationOptions.getWebRepository();
	}


	/**
	 * Returns HTML code for the commit log without splitting the list
	 * into pages.
	 * 
	 * @param maxCommits maximum number of commits for the log; if there
	 * are more, only the most recent will be used
	 * @return HTML code for the commit log
	 */
	public String renderMostRecentCommits(int maxCommits) {
		if (commits.size() > maxCommits) {
			List recentCommits = commits.subList(0, maxCommits);
			return renderCommitList(recentCommits)
					+ "<p>(" + (commits.size() - maxCommits) + " "
					+ Messages.getString("MORE_COMMITS") + ")</p>\n";
		} else {
			return renderCommitList(commits);
		}
	}
	
	/**
	 * Returns HTML code for a page.
	 * @param page the page number
	 * @return HTML code
	 */
	public String renderPage(int page) {
		this.page = page;
		this.pageCommits =
			commits.subList(getFirstCommitOfPage(page), getLastCommitOfPage(page) + 1);
		String result = "";
		if (getPages() > 1) {
			result += renderNavigation();
		}
		result += renderTimespan();
		result += renderCommitList(pageCommits);
		if (getPages() > 1) {
			result += renderNavigation();
		}
		return result;
	}

	private String renderTimespan() {
		Date time1 = ((Commit) pageCommits.get(0)).getDate();
		Date time2 = ((Commit) pageCommits.get(pageCommits.size() - 1)).getDate();
		String commitsText;
		if (getPages() > 1) {
			commitsText = Messages.getString("COMMITS")
					+ " "
					+ (commits.size() - getLastCommitOfPage(page))
					+ "-"
					+ (commits.size() - getFirstCommitOfPage(page))
					+ " of "
					+ commits.size();
		} else {
			commitsText = commits.size() + " " + Messages.getString("COMMITS");
		}
		return HTMLTagger.getSummaryPeriod(time1, time2, " (" + commitsText + ")");
	}

	private String renderNavigation() {
		String result = Messages.getString("PAGES") + ": ";
		if (page > 1) {
			result
				+= HTMLTagger.getLink(
					getFilename(page - 1),
					Messages.getString("NAVIGATION_PREVIOUS"))
				+ " ";
		}
		for (int i = 1; i <= getPages(); i++) {
			if (i == page) {
				result += (i) + " ";
			} else {
				result += HTMLTagger.getLink(getFilename(i), Integer.toString(i))
						+ " ";
			}
		}
		if (page < getPages()) {
			result += HTMLTagger.getLink(getFilename(page + 1),
					Messages.getString("NAVIGATION_NEXT")) + " ";
		}
		return "<p>" + result + "</p>\n";
	}

	private int getFirstCommitOfPage(int page) {
		return (page - 1) * PAGE_SIZE;
	}

	private int getLastCommitOfPage(int page) {
		return Math.min(commits.size(), (page * PAGE_SIZE)) - 1;
	}

	/**
	 * Returns the number of pages for this renderer.
	 * @return the number of pages for this renderer
	 */
	public int getPages() {
		return (commits.size() + PAGE_SIZE - 1) / PAGE_SIZE;
	}

	/**
	 * Returns the filename for a commit log page.
	 * @param page specified page
	 * @return the filename for a commit log page
	 */
	public static String getFilename(int page) {
		if (page == 1) {
			return "commit_log.html";
		} else {
			return "commit_log_page_" + page + ".html";
		}
	}

	private String renderCommitList(List commits) {
		if (commits.isEmpty()) {
			return "<p>No commits</p>\n";
		}
		Iterator it = commits.iterator();
		String result = "<dl class=\"commitlist\">\n";

		while (it.hasNext()) {
			Commit commit = (Commit) it.next();
			result += renderCommit(commit);
		}
		result += "</dl>\n\n";
		return result;
	}

	private String renderCommit(Commit commit) {
		String result = "  <dt>\n    " + getAuthor(commit) + "\n";
		result += "    " + getDate(commit) + "\n  </dt>\n";
		result += "  <dd>\n    <p class=\"comment\">\n" + getComment(commit) + "\n    </p>\n";
		result += "    <p class=\"commitdetails\"><strong>";
		result += getLinesOfCode(commit) + "</strong> ";
		result += "lines of code changed in:</p>\n";
		result += getAffectedFiles(commit) + "  </dd>\n\n";
		return result;
	}

	private String getDate(Commit commit) {
		return HTMLTagger.getDateAndTime(commit.getDate());
	}

	private String getAuthor(Commit commit) {
		return HTMLTagger.getAuthorLink(commit.getAuthor());
	}

	private String getComment(Commit commit) {
		return OutputUtils.escapeHtml(commit.getComment());
	}

	private String getLinesOfCode(Commit commit) {
		Iterator it = commit.getRevisions().iterator();
		int locSum = 0;
		while (it.hasNext()) {
			CvsRevision each = (CvsRevision) it.next();
			locSum += each.getLineValue();
			saveCvsRevision(each);
		}
		return Integer.toString(locSum);
	}

	private void saveCvsRevision(CvsRevision revision) {
		commitHashMap.put(revision.getFile().getFilenameWithPath(), revision);
	}
	
	private String getAffectedFiles(Commit commit) {

		String result = "    <ul class=\"commitdetails\">\n";
		FileCollectionFormatter formatter =
				new FileCollectionFormatter(commit.getAffectedFiles());
		Iterator it = formatter.getDirectories().iterator();
		while (it.hasNext()) {
			result += "      <li>\n";
			String directory = (String) it.next();
			if (!directory.equals("")) {
				result += "        <strong>"
					+ directory.substring(0, directory.length() - 1)
					+ "</strong>:\n";
			}
			Iterator files = formatter.getFiles(directory).iterator();
			String fileList = "";
			while (files.hasNext()) {
				if (!fileList.equals("")) {
					fileList += ",\n";
				}
				fileList += "        ";
				String file = (String) files.next();
				CvsRevision revision =
						(CvsRevision) commitHashMap.get(directory + file);
				if (webRepository != null) {
					CvsRevision previous = revision.getPreviousRevision();
					String url; 
					if (previous == null) {
						url = webRepository.getFileViewUrl(revision);
					} else {
						url = webRepository.getDiffUrl(previous, revision);
					}
					fileList += "<a href=\""
							+ OutputUtils.escapeHtml(url)
							+ "\" class=\"webrepository\">" + file + "</a>"; 
				} else {
					fileList += file;
				}
				if (revision.isInitialRevision()) {
					int linesAdded = revision.getLinesOfCode();
					fileList += "&nbsp;<span class=\"new\">(new";
					if (!revision.getFile().isBinary()) {
						fileList += "&nbsp;" + linesAdded;
					}
					fileList += ")</span>";
				} else if (revision.isDead()) {
					fileList += "&nbsp;<span class=\"del\">(del)</span>";
				} else {
					int linesAdded = revision.getLinesAdded();
					int linesRemoved = revision.getLinesRemoved();
					fileList += "&nbsp;<span class=\"change\">(";
					if (linesAdded > 0) {
						fileList += "+" + linesAdded;
						if (linesRemoved > 0) {
							fileList += "&nbsp;-" + linesRemoved;
						}
					} else if (linesRemoved > 0) {
						fileList += "-" + linesRemoved;
					} else {	// linesAdded == linesRemoved == 0
						// should be binary file or keyword subst change
						fileList += "changed";
					}
					fileList += ")</span>";
				}
			}
			result += fileList + "\n      </li>\n";
		}
		result += "    </ul>\n";
		return result;
	}
}
