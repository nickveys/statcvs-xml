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
    
	$RCSfile: AuthorsActivityChart.java,v $
	$Date: 2003-06-28 01:34:55 $ 
*/
package net.sf.statcvs.output.xml.chart;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.Commit;
import net.sf.statcvs.model.CommitListBuilder;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.RevisionIterator;

import org.jfree.chart.axis.HorizontalCategoryAxis;
import org.jfree.chart.plot.CategoryPlot;

/**
 * AuthorsActivityChart
 * 
 * @author Tammo van Lessen
 */
public class AuthorsActivityChart extends AbstractStackedChart {

	private static final int REMOVING = 0;
	private static final int CHANGING = 1;
	private static final int ADDING = 2;

	private List authors = new ArrayList();
	private String[] topics = new String[] 
			{I18n.tr("removing"),I18n.tr("changing"),I18n.tr("adding")};
	
	public AuthorsActivityChart(CvsContent content) {
		super("activity.png", "Author Activity");

		setCategoryAxisLabel("");
		setValueAxisLabel("%");
		CategoryPlot plot = getChart().getCategoryPlot();
		plot.getRenderer().setSeriesPaint(REMOVING, Color.red);
		plot.getRenderer().setSeriesPaint(CHANGING, Color.yellow);
		plot.getRenderer().setSeriesPaint(ADDING, Color.green);
		HorizontalCategoryAxis domainAxis = (HorizontalCategoryAxis) plot.getDomainAxis();
		domainAxis.setVerticalCategoryLabels(true);
		
		Collection auts = content.getAuthors();
		Iterator it = auts.iterator();
		while (it.hasNext()) {
			Author author = (Author) it.next();
			authors.add(author.getName());
		}
		Collections.sort(authors);
		double[][] categories;
		categories = new double[3][authors.size()];
		for (int j = 0; j < authors.size(); j++) {
			categories[REMOVING][j] = 0;
			categories[CHANGING][j] = 0;
			categories[ADDING][j] = 0;
		}
										
		RevisionIterator revIt = content.getRevisionIterator();
		CommitListBuilder commitList = new CommitListBuilder(revIt);
		List commits = commitList.createCommitList();
		Iterator commitIt = commits.iterator();
		while (commitIt.hasNext()) {
			Commit commit = (Commit) commitIt.next();
			List commitRevList = commit.getRevisions();
			Iterator commitRevIt = commitRevList.iterator();
			int author = authors.indexOf(commit.getAuthor().getName());
			int linesAdded = 0;
			int linesRemoved = 0;
			while (commitRevIt.hasNext()) {
				CvsRevision revision = (CvsRevision) commitRevIt.next();
				linesAdded += revision.getLineValue();
				linesRemoved += revision.getRemovingValue();
			}
			if (linesAdded == linesRemoved) {
				categories[CHANGING][author] += linesAdded;
			} 
			if (linesAdded < linesRemoved) {
				categories[CHANGING][author] += linesAdded;
				categories[REMOVING][author] += linesRemoved - linesAdded;
			} 
			if (linesAdded > linesRemoved) {
				categories[ADDING][author] += linesAdded - linesRemoved;
				categories[CHANGING][author] += linesRemoved;
			}
		}
		
		for (int i = 0; i < authors.size(); i++) {
			double maxLines = categories[REMOVING][i]
					+ categories[CHANGING][i] + categories[ADDING][i];
			for (int k = 0; k < 3; k++) {
				categories[k][i] *= (100 / maxLines);
				dataset.addValue(categories[k][i], topics[k], (String)authors.get(i));
			}
		}
		placeTitle();
	}
}
