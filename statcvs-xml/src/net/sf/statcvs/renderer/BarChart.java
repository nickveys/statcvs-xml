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
    
	$RCSfile: BarChart.java,v $
	$Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.renderer;

import java.awt.Color;
import java.awt.Paint;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.output.HTMLOutput;

import com.jrefinery.chart.CategoryPlot;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.data.DefaultCategoryDataset;

/**
 * Class for producing bar charts
 * @author jentzsch
 * @version $Id: BarChart.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class BarChart extends Chart {

	private double[][] categories;
	private String[] categoryNames;
	
	/**
	 * creates an Bar Chart
	 * @param revIt RevisionIterator
	 * @param title chart title
	 * @param subTitle chart subTitle
	 * @param fileName fileName for chart
	 * @param categoryCount number of catgories
	 * @param categoryNames names for categories
	 */
	public BarChart(
		RevisionIterator revIt,
		String title,
		String subTitle,
		String fileName,
		int categoryCount,
		String[] categoryNames) {

		super(title, subTitle, fileName);
		
		categories = new double[1][categoryCount];
		this.categoryNames = categoryNames;
		for (int i = 0; i < categories.length; i++) {
			categories[0][i] = 0;
		}
		
 		while (revIt.hasNext()) {
			CvsRevision rev = revIt.next();
			Date date = rev.getDate();
			Calendar cal = new GregorianCalendar();
			cal.setTime(date);
			if (categoryCount == 7) {
				int day = cal.get(Calendar.DAY_OF_WEEK);
				categories[0][day - 1]++;
			} else if (categoryCount == 24) {
				int hour = cal.get(Calendar.HOUR_OF_DAY);
				categories[0][hour]++;
			} 
		}

		DefaultCategoryDataset data = new DefaultCategoryDataset(categories);
		data.setCategories(categoryNames);
		
		setChart(ChartFactory.createVerticalBarChart(title, "", "commits", data, false));

		CategoryPlot plot = getChart().getCategoryPlot();
		plot.setSeriesPaint(new Paint[] { Color.blue });
		
		createChart();
		saveChart(HTMLOutput.SMALL_IMAGE_WIDTH, HTMLOutput.SMALL_IMAGE_HEIGHT);
	}
}
