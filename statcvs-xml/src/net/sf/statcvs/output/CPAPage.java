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
    
	$RCSfile: CPAPage.java,v $ 
	Created on $Date: 2003-06-17 16:43:02 $ 
*/

package net.sf.statcvs.output;

import java.io.IOException;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.renderer.TableRenderer;
import net.sf.statcvs.reportmodel.Table;
import net.sf.statcvs.reports.AuthorsTableReport;
import net.sf.statcvs.reports.AbstractLocTableReport;
import net.sf.statcvs.reports.TableReport;

/**
 * @author anja
 */
public class CPAPage extends HTMLPage {
	private CvsContent content;
	private int sortType;
	private boolean withImage;

	/**
	 * Method CPUPage.
	 * @param content to dispay
	 * @param sortType to use
	 * @param withImage <tt>true</tt> if the LOC per Author image was generated
	 * @throws IOException on error
	 */
	public CPAPage(CvsContent content, int sortType, boolean withImage)
			throws IOException {

		super(content);
		this.content = content;
		this.sortType = sortType;
		if (sortType == AbstractLocTableReport.SORT_BY_LINES) {
			setFileName("authors.html");
		} else {
			setFileName("authors2.html");
		}
		this.withImage = withImage;
		setPageName(Messages.getString("CPU_TITLE"));
		createPage();
	}

	protected void printBody() throws IOException {
		printBackLink();
		TableReport report = new AuthorsTableReport(getContent(), sortType);
		report.calculate();
		Table table = report.getTable();
		print(new TableRenderer(table).getRenderedTable());
		if (sortType == AbstractLocTableReport.SORT_BY_LINES) {
			printParagraph(Messages.getString("NAVIGATION_ORDER_BY") + ": "
				+ strong(Messages.getString("ORDER_BY_LOC"))
				+ " / "
				+ a("authors2.html", Messages.getString("ORDER_BY_NAME")));
		} else {
			printParagraph(Messages.getString("NAVIGATION_ORDER_BY") + ": "
				+ a("authors.html", Messages.getString("ORDER_BY_LOC"))
				+ " / "
				+ strong(Messages.getString("ORDER_BY_NAME")));
		}
		if (withImage) {
			printH2(Messages.getString("LOC_TITLE"));
			printParagraph(img("loc_per_author.png", 640, 480));
		}
		printH2(Messages.getString("ACTIVITY_TITLE"));
		printParagraph(img("activity_time.png", 500, 300));
		printParagraph(img("activity_day.png", 500, 300));
		print(getAuthorActivityChartSection());
	}
	
	private String getAuthorActivityChartSection() {
		String result = "";
		result += h2(Messages.getString("AUTHOR_ACTIVITY_TITLE"));
		result += p(img("activity.png", 640, 480));
		int authors = content.getAuthors().size();
		int chartCount = authors / HTMLOutput.AUTHORS_PER_ACTIVITY_CHART;
		if (chartCount * HTMLOutput.AUTHORS_PER_ACTIVITY_CHART != authors) {
			chartCount++;
		}
		for (int i = 2; i <= chartCount; i++) {
			result += p(img("activity" + i + ".png", 640, 480));
		}
		return result;
	}
}
