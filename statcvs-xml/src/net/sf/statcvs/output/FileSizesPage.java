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
    
	$RCSfile: FileSizesPage.java,v $ 
	Created on $Date: 2003-06-17 16:43:02 $ 
*/

package net.sf.statcvs.output;

import java.io.IOException;
import java.util.Iterator;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.renderer.TableRenderer;
import net.sf.statcvs.reports.FilesWithMostRevisionsTableReport;
import net.sf.statcvs.reports.LargestFilesTableReport;
import net.sf.statcvs.reports.TableReport;

/**
 * This page displays the timeline of file count, a table with the largest
 * files, and a table with the files having most revisions
 * 
 * @author anja
 */
public class FileSizesPage extends HTMLPage {
	private static final int MAX_LARGEST_FILES = 20;
	private static final int MAX_FILES_WITH_MOST_REVISIONS = 20;

	/**
	 * @see net.sf.statcvs.output.HTMLPage#HTMLPage(CvsContent)
	 */
	public FileSizesPage(CvsContent content) throws IOException {
		super(content);
		setFileName("file_sizes.html");
		setPageName(Messages.getString("FILE_SIZES_TITLE"));
		createPage();
	}

	protected void printBody() throws IOException {
		printBackLink();
		print(h2(Messages.getString("FILE_COUNT_TITLE")));
		print(getFileCountImage());
		print(h2(Messages.getString("AVERAGE_FILE_SIZE_TITLE")));
		print(getFileSizeImage());
		print(getLargestFilesSection());
		print(getFilesWithMostRevisionsSection());
	}

	private String getLargestFilesSection() {
		String result = "";
		result += h2(Messages.getString("LARGEST_FILES_TITLE"));
		TableReport report = new LargestFilesTableReport(
				getContent().getFiles(), 
				MAX_LARGEST_FILES);
		report.calculate();
		result += new TableRenderer(report.getTable()).getRenderedTable();
		return result;
	}

	private String getFilesWithMostRevisionsSection() {
		String result = "";
		result += h2(Messages.getString("FILES_WITH_MOST_REVISIONS_TITLE"));
		TableReport report = new FilesWithMostRevisionsTableReport(
				getContent().getFiles(), 
				MAX_FILES_WITH_MOST_REVISIONS);
		report.calculate();
		result += new TableRenderer(report.getTable()).getRenderedTable();
		return result;
	}

	private String getFileCountImage() {
		int fileCount = getCurrentFileCount();
		String result = img("file_count.png", 640, 480) + br()
				+ strong(Messages.getString("TOTAL_FILE_COUNT") + ": ") + fileCount;
		result += " (" + HTMLTagger.getDateAndTime(getContent().getLastDate()) + ")";
		return p(result);
	}
	
	private String getFileSizeImage() {
		return p(img("file_size.png", 640, 480));
	}
	
	private int getCurrentFileCount() {
		int result = 0;
		Iterator fileIt = getContent().getFiles().iterator();
		while (fileIt.hasNext()) {
			CvsFile file = (CvsFile) fileIt.next();
			if (!file.isDead()) {
				result++;
			}
		}
		return result;
	}
}