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
