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
    
	$RCSfile: ActivityProgressionChart.java,v $
	$Date: 2004-03-01 00:42:31 $ 
*/
package de.berlios.statcvs.xml.report;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.util.IntegerMap;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ColorBar;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.SymbolicAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.ContourPlot;
import org.jfree.data.ContourDataset;
import org.jfree.data.DefaultContourDataset;
import org.jfree.ui.RectangleEdge;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.chart.AbstractChart;
import de.berlios.statcvs.xml.output.ChartReportElement;
import de.berlios.statcvs.xml.output.Report;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * ModuleChangesChart
 * 
 * @author Tammo van Lessen
 */
public class ActivityProgressionChart extends AbstractChart {

	private CvsContent content;
	private ReportSettings settings;

	/** The x-axis. */
	private ValueAxis xAxis = null;
    
	/** The y-axis. */
	private AlignedVerticalSymbolicAxis yAxis = null;
    
	/** The z-axis. */
	private ColorBar zAxis = null;

	/** The ratio. */
	private static double ratio = 0.0;

	public static Report generate(CvsContent content, ReportSettings settings)
	{
		return new Report(new ChartReportElement(
			new ActivityProgressionChart(content, settings)));
	}
	
	public ActivityProgressionChart(CvsContent content, ReportSettings settings) 
	{
		super(settings, "progression%1.png", I18n.tr("Activity Progression%1"));
		this.content = content;
		this.settings = settings;
		setChart(createContourPlot());
		setup(true);
		//placeTitle();
	}
	
	private JFreeChart createContourPlot() {
		String title = settings.getProjectName();
		String xAxisLabel = I18n.tr("Date");
		String yAxisLabel = I18n.tr("Modules");
		String zAxisLabel = I18n.tr("Commit Activity (%)");

		xAxis = new DateAxis(xAxisLabel);
		//xAxis = new HorizontalNumberAxis(xAxisLabel);
		
		
		List dirs = new ArrayList(content.getDirectories());
		String[] ax = new String[dirs.size()];
		for (int i=0; i < dirs.size(); i++) {
			ax[i] = (String)((Directory)dirs.get(i)).getPath();
		}
		yAxis = new AlignedVerticalSymbolicAxis(yAxisLabel, ax);

		zAxis = new ColorBar(zAxisLabel);

		//yAxis.setAutoRangeIncludesZero(false);
		//zAxis.setAutoRangeIncludesZero(false);

		yAxis.setInverted(true);

		yAxis.setLowerMargin(0.0);
		yAxis.setUpperMargin(0.0);

		//zAxis.setInverted(false);
		//zAxis.setTickMarksVisible(true);

		ContourDataset data = createDataset();
		ContourPlot plot = new ContourPlot(data, xAxis, yAxis, zAxis);
		//plot.setRenderAsPoints(true);
		ratio = Math.abs(ratio); // don't use plot units for ratios when x axis is date
		plot.setDataAreaRatio(ratio);

		return new JFreeChart(title, null, plot, false);
	}

	private ContourDataset createDataset() {
		List dirs = new ArrayList(content.getDirectories());
		List dateList = new ArrayList();

		Date firstDate = content.getFirstDate();
		Date lastDate = content.getLastDate();
		Calendar cal = new GregorianCalendar();
		cal.setTime(firstDate);
		while (cal.getTime().before(lastDate)) {
			dateList.add(cal.getTime());
			cal.add(Calendar.DATE, 1);
		}
		dateList.add(lastDate);

		Double[][] values = new Double[dateList.size()][dirs.size()];
		for (int i = 0; i < dateList.size(); i++) {
			Iterator dirsIt = dirs.iterator();
			IntegerMap changesMap = new IntegerMap();
			while (dirsIt.hasNext()) {
				Directory dir = (Directory)dirsIt.next();
				Iterator revIt = dir.getRevisions().iterator();
				while (revIt.hasNext()) {
					CvsRevision rev = (CvsRevision)revIt.next();
					Date revDate = rev.getDate();
					Date currDate = (Date)dateList.get(i);
					Date nextDate = null;
					if (i < dateList.size()-1) {
						nextDate = (Date)dateList.get(i+1);
					}
					
					if (revDate.equals(currDate) || (revDate.after(currDate)
						&& revDate.before(nextDate))) {
							changesMap.inc(dir);	
					}
				}
			}
			Iterator  cIt = changesMap.iteratorSortedByKey();
			while (cIt.hasNext()) {
				Directory dir = (Directory) cIt.next();
				int dirIndex = dirs.indexOf(dir);
				//values[i][dirIndex] = new Double(changesMap.getPercent(dir));
				values[i][dirIndex] = new Double(changesMap.getPercentOfMaximum(dir));
			}
		}

		int numValues = dateList.size() * dirs.size();
		Date[] oDateX = new Date[numValues];
		Double[] oDoubleY = new Double[numValues];
		Double[] oDoubleZ = new Double[numValues];
		
		for (int x = 0; x < dateList.size(); x++) {
			for (int y = 0; y < dirs.size(); y++) {
				int index = (x*dirs.size()) + y;
				oDateX[index] = (Date)dateList.get(x);
				oDoubleY[index] = new Double(y);
				if ((values[x][y] != null) && ((values[x][y].isNaN()) 
					|| (values[x][y].equals(new Double(0))))) {
					values[x][y] = null;
				}
				oDoubleZ[index] = values[x][y];
			}
		}
		oDoubleZ[0] = new Double(0.0);				
		return new DefaultContourDataset("getTitle()", oDateX, oDoubleY, oDoubleZ);		
	}

	public int getPreferedHeight() {
		return 480 * (content.getDirectories().size()/35);
	}

	public int getPreferedWidth() {
		return 640;
	}

	private class AlignedVerticalSymbolicAxis extends SymbolicAxis {

		private String longestStr = "";
		private List symbolicGridLineList = null;
		
		public AlignedVerticalSymbolicAxis(String title, String[] syms) {
			super(title, syms);
			// find longest string
			for (int i = 0; i < syms.length; i++) {
				if (longestStr.length() < syms[i].length()) {
					longestStr = syms[i];
				}
			}
		}

		/**
		 * @see org.jfree.chart.axis.Axis#refreshTicks(java.awt.Graphics2D, java.awt.geom.Rectangle2D, java.awt.geom.Rectangle2D, int)
		 */
		public void refreshTicks(Graphics2D g2,
				Rectangle2D plotArea, Rectangle2D dataArea,
				int location) {
			
			//getTicks().clear();

			Font tickLabelFont = getTickLabelFont();
			g2.setFont(tickLabelFont);

			double size = getTickUnit().getSize();
			int count = calculateVisibleTickCount();
			double lowestTickValue = calculateLowestVisibleTickValue();

			if (count <= ValueAxis.MAXIMUM_TICK_COUNT) {
				for (int i = 0; i < count; i++) {
					double currentTickValue = lowestTickValue + (i * size);
					double yy = translateValueToJava2D(currentTickValue, dataArea, RectangleEdge.BOTTOM);
					String tickLabel;
					NumberFormat formatter = getNumberFormatOverride();
					if (formatter != null) {
						tickLabel = formatter.format(currentTickValue);
					}
					else {
						tickLabel = valueToString(currentTickValue);
					}
					FontRenderContext frc = g2.getFontRenderContext();
					Rectangle2D tickLabelBounds = tickLabelFont.getStringBounds(longestStr, frc);
					LineMetrics lm = tickLabelFont.getLineMetrics(tickLabel, frc);
					float x = (float) (dataArea.getX()
									 - tickLabelBounds.getWidth() - getTickLabelInsets().right);
					float y = (float) (yy + (lm.getAscent() / 2));
					//Tick tick = new Tick(new Double(currentTickValue), tickLabel, x, y);
					//getTicks().add(tick);
				}
			}
		}
	}
}