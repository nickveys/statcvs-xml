/*
 *  StatCvs-XML - XML output for StatCvs.
 *
 *  Copyright by Steffen Pingel, Tammo van Lessen.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  version 2 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package de.berlios.statcvs.xml.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Pie3DPlot;
import org.jfree.data.DefaultPieDataset;
import org.jfree.util.Rotation;

import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * AbstractPieChart
 * 
 * @author Tammo van Lessen
 * @author Steffen Pingel
 */
public abstract class AbstractPieChart extends AbstractChart {

	protected DefaultPieDataset dataset;
	
	/**
	 * @param filename
	 * @param title
	 */
	public AbstractPieChart(ReportSettings settings, String filename, String title) 
	{
		super(settings, filename, title);
		
		dataset = new DefaultPieDataset();
		JFreeChart chart = ChartFactory.createPieChart3D(
			settings.getProjectName(),  // chart title
			dataset,                // data
			true,                // include legend
			true,
			false);

		Pie3DPlot plot = (Pie3DPlot)chart.getPlot();
		plot.setStartAngle(270);
		plot.setDirection(Rotation.CLOCKWISE);
		plot.setForegroundAlpha(0.5f);

		setChart(chart);
	}

}
