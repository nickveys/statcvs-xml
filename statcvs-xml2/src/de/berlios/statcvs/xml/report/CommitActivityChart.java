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

package de.berlios.statcvs.xml.report;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.data.XYDataset;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.chart.AbstractCombinedChart;
import de.berlios.statcvs.xml.model.AuthorGrouper;
import de.berlios.statcvs.xml.model.Grouper;
import de.berlios.statcvs.xml.output.ChartReportElement;
import de.berlios.statcvs.xml.output.Report;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * CommitActivityChart
 *
 * @author Anja Jentzsch 
 * @author Tammo van Lessen
 */
public class CommitActivityChart extends AbstractCombinedChart {

    /**
     * @param settings
     * @param defaultFilename
     * @param defaultSubtitle
     */
    public CommitActivityChart(CvsContent content, ReportSettings settings) {
        super(settings, "commitactivity%1.png", I18n.tr("Commit Activity%1"));
		

		Grouper grouper = settings.getGrouper(new AuthorGrouper());
		Map seriesByGroup = new HashMap();

		XYSeries series = createXYSeries(grouper.getName() + " " + I18n.tr("(All)"), 
				content.getRevisions().iterator());
		XYDataset dataset = new XYSeriesCollection(series);
		addPlot(createPlot(dataset, series.getName()));
		
		Iterator it = settings.getRevisionIterator(content);
		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision)it.next();
			if (seriesByGroup.get(grouper.getGroup(rev)) == null) {
				seriesByGroup.put(grouper.getGroup(rev), new XYSeries(grouper.getName(grouper.getGroup(rev))));				
			}
			addToXYSeries((XYSeries)seriesByGroup.get(grouper.getGroup(rev)), rev);
		}

		
		Iterator it2 = seriesByGroup.keySet().iterator();
		while (it2.hasNext()) {
			Object group = it2.next();
			dataset = new XYSeriesCollection((XYSeries)seriesByGroup.get(group));
			addPlot(createPlot(dataset, grouper.getName(group)));
		}
		
		setup(false);
    }

	private XYSeries createXYSeries(String title, Iterator it)
	{
		XYSeries series = new XYSeries(title);
		
		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision)it.next();

			Calendar cal = Calendar.getInstance();
			cal.setTime(rev.getDate());
			double hour = cal.get(Calendar.HOUR_OF_DAY);
			double minutes = cal.get(Calendar.MINUTE);

			// clear time info
			cal.clear(Calendar.HOUR);
			cal.clear(Calendar.HOUR_OF_DAY);
			cal.clear(Calendar.MINUTE);
			cal.clear(Calendar.SECOND);
			cal.clear(Calendar.MILLISECOND);
			
			series.add(cal.getTime().getTime(), hour + (minutes / 60));
		}

		return series;	
	}
	
	private void addToXYSeries(XYSeries series, CvsRevision rev)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(rev.getDate());
		double hour = cal.get(Calendar.HOUR_OF_DAY);
		double minutes = cal.get(Calendar.MINUTE);

		// clear time info
		cal.clear(Calendar.HOUR);
		cal.clear(Calendar.HOUR_OF_DAY);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
			
		series.add(cal.getTime().getTime(), hour + (minutes / 60));
	}
	
	public XYPlot createPlot(XYDataset dataset, String title)
	{
		NumberAxis valueAxis = new NumberAxis(title);
		valueAxis.setTickUnit(new NumberTickUnit(6.0, new DecimalFormat("0")));
		valueAxis.setAutoRangeIncludesZero(false);
		valueAxis.setLowerBound(0);
		valueAxis.setUpperBound(24);
		valueAxis.setLabelFont(new Font("SansSerif", Font.PLAIN, 9));

		return new XYPlot(dataset, null, valueAxis, new PointXYRenderer());
	}

	public static Report generate(CvsContent content, ReportSettings settings)
	{
		return new Report(new ChartReportElement(
			new CommitActivityChart(content, settings)));
	}

	/**
	 * @see de.berlios.statcvs.xml.chart.AbstractChart#getPreferredHeigth()
	 */
	public int getPreferredHeigth() 
	{
		return 70 * (getPlotCount() + 1) + 110;
	}

	/**
	 * PointXYRenderer
	 * 
	 */
	public class PointXYRenderer extends StandardXYItemRenderer {
	
		public PointXYRenderer() {
			super(StandardXYItemRenderer.SHAPES);
			setSeriesPaint(0, Color.RED);
			setSeriesShape(0, new Ellipse2D.Double(0,0,3,3));
		}
	

	}
}
