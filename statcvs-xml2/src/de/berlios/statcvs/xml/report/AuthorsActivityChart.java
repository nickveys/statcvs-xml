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
	$Date: 2004-02-21 20:32:05 $ 
*/
package de.berlios.statcvs.xml.report;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.statcvs.input.CommitListBuilder;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.Commit;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;

import org.jfree.chart.plot.CategoryPlot;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.chart.AbstractStackedChart;
import de.berlios.statcvs.xml.output.ChartReportElement;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * AuthorsActivityChart
 * 
 * @author Tammo van Lessen
 * @fix review
 */
public class AuthorsActivityChart extends AbstractStackedChart {

	private static final int REMOVING = 0;
	private static final int CHANGING = 1;
	private static final int ADDING = 2;

	private List authors = new ArrayList();
	private String[] topics = new String[] 
			{I18n.tr("removing"),I18n.tr("changing"),I18n.tr("adding")};
	
	public AuthorsActivityChart(CvsContent content, ReportSettings settings)
	{
		super(settings, "activity.png", I18n.tr("Author Activity"), null, "%");

		CategoryPlot plot = getChart().getCategoryPlot();
		plot.getRenderer().setSeriesPaint(REMOVING, Color.red);
		plot.getRenderer().setSeriesPaint(CHANGING, Color.yellow);
		plot.getRenderer().setSeriesPaint(ADDING, Color.green);
		//plot.getDomainAxis().setVerticalCategoryLabels(true);
		
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
										
		Iterator revIt = settings.getRevisionIterator(content);
		CommitListBuilder commitList = new CommitListBuilder( revIt);
		List commits = commitList.createCommitList();
		Iterator commitIt = commits.iterator();
		while (commitIt.hasNext()) {
			Commit commit = (Commit) commitIt.next();
			Set commitRevList = commit.getRevisions();
			Iterator commitRevIt = commitRevList.iterator();
			int author = authors.indexOf(commit.getAuthor().getName());
			int linesAdded = 0;
			int linesRemoved = 0;
			while (commitRevIt.hasNext()) {
				CvsRevision revision = (CvsRevision) commitRevIt.next();
				linesAdded += revision.getNewLines();
				linesRemoved += revision.getReplacedLines();
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
		setup(true);
	}
	
	public static ReportElement generate(CvsContent content, ReportSettings settings)
	{
		return new ChartReportElement(new AuthorsActivityChart(content, settings));
	}

}
