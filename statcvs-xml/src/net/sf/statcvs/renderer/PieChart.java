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
    
	$RCSfile: PieChart.java,v $
	$Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.renderer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.RevisionFilterIterator;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.model.RevisionIteratorSummary;
import net.sf.statcvs.model.UserPredicate;
import net.sf.statcvs.output.HTMLOutput;
import net.sf.statcvs.util.IntegerMap;
import net.sf.statcvs.util.OutputUtils;

import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.Plot;
import com.jrefinery.data.DefaultPieDataset;

/**
 * Class for producing module size charts
 * @author jentzsch
 * @version $Id: PieChart.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class PieChart extends Chart {

	private static final int SLICE_MIN_PERCENT = 5;
	/**
	 * Filter method by repository
	 */
	public static final int FILTERED_BY_REPOSITORY = 0;
	/**
	 * Filter method by username
	 */
	public static final int FILTERED_BY_USER = 1;

	/**
	 * creates an 3D Pie Chart
	 * @param content CvsContent
	 * @param title chart title
	 * @param subTitle chart subTitle
	 * @param fileName fileName for chart
	 * @param author author for this pie chart 
	 * @param filter filter options (users / whole repository)
	 */
	public PieChart(
		CvsContent content,
		String title,
		String subTitle,
		String fileName,
		Author author,
		int filter) {
		super(title, subTitle, fileName);

		DefaultPieDataset data = new DefaultPieDataset();

		List directories;
		if (filter == FILTERED_BY_USER) {
			directories = new ArrayList(author.getDirectories());
		} else {
			directories = new ArrayList(content.getDirectories());
		}
		Collections.sort(directories);

		IntegerMap dirSizes = new IntegerMap();
		Iterator it = directories.iterator();
		while (it.hasNext()) {
			Directory dir = (Directory) it.next();
			RevisionIterator revsByModule = dir.getRevisionIterator();
			if (filter == FILTERED_BY_USER) {
				RevisionIterator filteredByUser =
						new RevisionFilterIterator(revsByModule,
								new UserPredicate(author));
				RevisionIteratorSummary summary = 
						new RevisionIteratorSummary(filteredByUser);
				dirSizes.addInt(dir, summary.getLineValue());
			} else {
				Set files = new RevisionIteratorSummary(revsByModule).getAllFiles();
				Iterator fileIt = files.iterator();
				while (fileIt.hasNext()) {
					CvsFile element = (CvsFile) fileIt.next();
					dirSizes.addInt(dir, element.getCurrentLinesOfCode());
				}
			}
		}

		int otherSum = 0;
		List colors = new ArrayList();
		List outlines = new ArrayList();
		it = dirSizes.iteratorSortedByValue();
		while (it.hasNext()) {
			Directory dir = (Directory) it.next();
			if (dirSizes.getPercent(dir) >= SLICE_MIN_PERCENT) {
				String dirName = dir.isRoot() ? "/" : dir.getPath();
				data.setValue(dirName, dirSizes.getInteger(dir));
				colors.add(OutputUtils.getStringColor(dirName));
				outlines.add(Color.BLACK);
			} else {
				otherSum += dirSizes.get(dir);
			}
		}
		data.setValue(Messages.getString("PIE_MODSIZE_OTHER"), new Integer(otherSum));
		colors.add(Color.GRAY);
		outlines.add(Color.BLACK);

		setChart(ChartFactory.createPie3DChart(title, data, true));

		Plot plot = getChart().getPlot();
		plot.setSeriesPaint((Color[]) colors.toArray(new Color[colors.size()]));
		plot.setSeriesOutlinePaint((Color[]) outlines.toArray(new Color[colors.size()]));
		
		createChart();
		saveChart(HTMLOutput.IMAGE_WIDTH, HTMLOutput.IMAGE_HEIGHT); 
	}
}
