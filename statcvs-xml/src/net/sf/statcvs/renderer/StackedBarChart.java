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
    
	$RCSfile: StackedBarChart.java,v $
	$Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.renderer;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.Commit;
import net.sf.statcvs.model.CommitListBuilder;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.output.HTMLOutput;

import com.jrefinery.chart.CategoryPlot;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.HorizontalCategoryAxis;
import com.jrefinery.data.DefaultCategoryDataset;

/**
 * Class for producing stacked bar charts
 * @author jentzsch
 * @version $Id: StackedBarChart.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class StackedBarChart extends Chart {
	private static final int REMOVING = 0;
	private static final int CHANGING = 1;
	private static final int ADDING = 2;
	
	private CvsContent content;
	private String title;
	
	private int chartCount = 1;
	private int remaining;
	private int currentChartNr = 0;
	
	private double[][] categories;
	private ArrayList categoryNames = new ArrayList();
	private ArrayList subCategoryNames;
	private double[][] subCategories;
	
	/**
	 * creates an Stacked Bar Chart
	 * @param content CvsContent
	 * @param title chart title
	 * @param subTitle chart subTitle
	 * @param fileName fileName for chart
	 */
	public StackedBarChart(
		CvsContent content,
		String title,
		String subTitle,
		String fileName) {

		super(title, subTitle, fileName);
		this.content = content;
		this.title = title;
		Collection authors = content.getAuthors();
		Iterator it = authors.iterator();
		while (it.hasNext()) {
			Author author = (Author) it.next();
			categoryNames.add(author.getName());
		}
		Collections.sort(categoryNames);
		
		chartCount = categoryNames.size() / HTMLOutput.AUTHORS_PER_ACTIVITY_CHART;
		if (chartCount * HTMLOutput.AUTHORS_PER_ACTIVITY_CHART != categoryNames.size()) {
			chartCount++;
		}
		remaining = HTMLOutput.AUTHORS_PER_ACTIVITY_CHART - (categoryNames.size() 
						% HTMLOutput.AUTHORS_PER_ACTIVITY_CHART);
		
		while (categoryNames.size() < (HTMLOutput.AUTHORS_PER_ACTIVITY_CHART * chartCount)) {
			categoryNames.add("");
		}
		categories = new double[3][categoryNames.size()];
		for (int j = 0; j < categoryNames.size(); j++) {
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
			int author = categoryNames.indexOf(commit.getAuthor().getName());
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
			}
		}
		currentChartNr++;
		createStackedBarChart();
	}

	private void createStackedBarChart() {
		subCategories = new double[3][HTMLOutput.AUTHORS_PER_ACTIVITY_CHART];
		subCategoryNames = new ArrayList();
		for (int i = 0; i < HTMLOutput.AUTHORS_PER_ACTIVITY_CHART; i++) {
			subCategories[REMOVING][i] = categories[REMOVING][((currentChartNr - 1) 
											* HTMLOutput.AUTHORS_PER_ACTIVITY_CHART) + i];
			subCategories[CHANGING][i] = categories[CHANGING][((currentChartNr - 1) 
											* HTMLOutput.AUTHORS_PER_ACTIVITY_CHART) + i];
			subCategories[ADDING][i] = categories[ADDING][((currentChartNr - 1) 
											* HTMLOutput.AUTHORS_PER_ACTIVITY_CHART) + i];
			subCategoryNames.add(categoryNames.get(((currentChartNr - 1) 
				* HTMLOutput.AUTHORS_PER_ACTIVITY_CHART) + i));
		}
		
		DefaultCategoryDataset data = new DefaultCategoryDataset(subCategories);
		data.setSeriesName(REMOVING, "removing");
		data.setSeriesName(CHANGING, "changing");
		data.setSeriesName(ADDING, "adding");
				
		data.setCategories(subCategoryNames.toArray());
		
		setChart(ChartFactory.createStackedVerticalBarChart(title, "", "%", data, true));
		
		CategoryPlot plot = getChart().getCategoryPlot();
		plot.setSeriesPaint(new Paint[] { Color.red, Color.yellow, Color.green });

		HorizontalCategoryAxis domainAxis = (HorizontalCategoryAxis) plot.getDomainAxis();
		domainAxis.setVerticalCategoryLabels(true);
	
		createChart();
		if (currentChartNr > 1) {
			saveChart(HTMLOutput.IMAGE_WIDTH, HTMLOutput.IMAGE_HEIGHT, "activity" + currentChartNr
				+ ".png");
		} else {
			saveChart(HTMLOutput.IMAGE_WIDTH, HTMLOutput.IMAGE_HEIGHT);
		}
		
		if (currentChartNr < chartCount) {
			currentChartNr++;
			createStackedBarChart();
		}
	}
}
