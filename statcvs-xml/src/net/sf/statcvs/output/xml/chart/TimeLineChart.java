/*
 * statcvs-xml
 * TODO
 * Created on 27.06.2003
 *
 */
package net.sf.statcvs.output.xml.chart;

import java.awt.Color;
import java.awt.Paint;
import java.util.Iterator;

import net.sf.statcvs.I18n;
import net.sf.statcvs.reportmodel.TimeLine;
import net.sf.statcvs.reportmodel.TimePoint;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.HorizontalDateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.XYStepRenderer;
import org.jfree.data.XYDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * TimeLineChart
 * 
 * @author Tammo van Lessen
 * @version $id: $
 */
public class TimeLineChart extends AbstractChart {

	private String rangeLabel;

	private TimeSeriesCollection tsc;
	
	/**
	 * @param filename
	 * @param title
	 */
	public TimeLineChart(String filename, String title) {
		super(filename, title);

		Paint[] colors = new Paint[1];
		colors[0] = Color.blue;

		tsc = new TimeSeriesCollection();
		//collection.addSeries(createTimeSeries(timeline));

		//String range = timeline.getRangeLabel();
		String domain = I18n.tr("Date");

		setChart(ChartFactory.createTimeSeriesChart(
			getTitle(), 
			I18n.tr("Date"), rangeLabel,
			(XYDataset)tsc, 
			true,
			true,
			false));
		

		//getChart().getPlot().setSeriesPaint(colors);
		
		XYPlot plot = getChart().getXYPlot();
		HorizontalDateAxis axis = (HorizontalDateAxis) plot.getDomainAxis();
		axis.setVerticalTickLabels(true);
		plot.setRenderer(new XYStepRenderer());
	}

	void setTimeLine(TimeLine timeLine) {
		TimeSeries result =
				new TimeSeries("!??!SERIES_LABEL!??!", Millisecond.class);
		Iterator it = timeLine.getDataPoints().iterator();
		while (it.hasNext()) {
			TimePoint timePoint = (TimePoint) it.next();
			result.add(new Millisecond(timePoint.getDate()), timePoint.getValue());
		}
		tsc.addSeries(result);
	}
	
	void setRangeLabel(String rl) {
		this.rangeLabel = rl;
	}

}
