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
*/
package de.berlios.statcvs.xml.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.DefaultCategoryDataset;

import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * AbstractStackedChart
 * 
 * @author Tammo van Lessen
 * @author Steffen Pingel
 */
public abstract class AbstractStackedChart extends AbstractChart {

	protected DefaultCategoryDataset dataset;
	
	/**
	 * @param filename
	 * @param title
	 */
	public AbstractStackedChart(ReportSettings settings, String filename, String title, 
						String domainLabel, String rangeLabel)
	{
		super(settings, filename, title);
		
		dataset = new DefaultCategoryDataset();
		setChart(ChartFactory.createStackedBarChart3D(
			settings.getProjectName(),  // chart title
			domainLabel,
			rangeLabel,
			dataset,       // data
			PlotOrientation.HORIZONTAL,
			true,          // include legend
			true,          // tooltips
			false));          // urls
	}

	public int getPreferredHeigth()
	{
		return 30 * dataset.getRowCount() + 150;
	}
	
}
