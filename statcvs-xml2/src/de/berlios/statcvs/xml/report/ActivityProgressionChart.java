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
import java.util.Hashtable;
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
import de.berlios.statcvs.xml.model.Grouper;
import de.berlios.statcvs.xml.model.ModuleGrouper;
import de.berlios.statcvs.xml.output.ChartReportElement;
import de.berlios.statcvs.xml.output.Report;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * ModuleChangesChart
 * 
 * @author Tammo van Lessen
 */
public class ActivityProgressionChart extends AbstractChart {

	private int groupCount;
	private CvsContent content;
	private ReportSettings settings;

	public ActivityProgressionChart(CvsContent content, ReportSettings settings) 
	{
		super(settings, "progression%1.png", I18n.tr("Activity Progression%1"));
		
		this.content = content;
		this.settings = settings;

		setChart(createContourPlot());
		if (getChart() != null) {
			setup(true);
		}
	}

	public static Report generate(CvsContent content, ReportSettings settings)
	{
		ActivityProgressionChart chart = new ActivityProgressionChart(content, settings);
		return (chart.getChart() != null) ? new Report(new ChartReportElement(chart)) : null;
	}
	
	private JFreeChart createContourPlot() 
	{
		Grouper grouper = settings.getGrouper(new ModuleGrouper(settings.getModules(content)));
		List groupNames = new ArrayList(); 
		Iterator it = grouper.getGroups(content, settings);
		while (it.hasNext()) {
			groupNames.add(grouper.getName(it.next()));
		}
		this.groupCount = groupNames.size();
		
		ContourDataset data = createDataset(grouper);
		if (data == null) {
			return null;
		}
		
		ValueAxis xAxis = new DateAxis(I18n.tr("Date"));
		
		SymbolicAxis yAxis = new SymbolicAxis(grouper.getName(), (String[])groupNames.toArray(new String[0])); 
		yAxis.setInverted(true);

		ColorBar zAxis = new ColorBar(I18n.tr("Commit Activity (%)"));
		zAxis.getAxis();
	
		ContourPlot plot = new ContourPlot(data, xAxis, yAxis, zAxis);
		// don't use plot units for ratios when x axis is date
		plot.setDataAreaRatio(0.0);
		plot.setColorBarLocation(RectangleEdge.BOTTOM);
		
		return new JFreeChart(settings.getProjectName(), null, plot, false);
	}

	private ContourDataset createDataset(Grouper grouper)
	{
		Hashtable mapByDate = new Hashtable();
		// define 100% to be correlate at least to 1
		int max = 1;
		
		Date currentDate = null;
		IntegerMap commitsPerGroup = new IntegerMap();
		Iterator it = settings.getRevisionIterator(content);
		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision)it.next();
			Date date = rev.getDate();
			if (currentDate == null) {
				currentDate = date;
			}
			else if (!date.equals(currentDate)) {
				max = Math.max(commitsPerGroup.max(), max);
				mapByDate.put(currentDate, commitsPerGroup);
				commitsPerGroup = new IntegerMap();
				currentDate = date;
			}
			commitsPerGroup.inc(grouper.getGroup(rev));
		}

		if (currentDate != null) {
			max = Math.max(commitsPerGroup.max(), max);
			mapByDate.put(currentDate, commitsPerGroup);
		}
		
		int dateCount = mapByDate.size();
		int numValues = dateCount * groupCount;
		if (numValues == 0) {
			return null;
		}
		
		Date[] oDateX = new Date[numValues];
		Double[] oDoubleY = new Double[numValues];
		Double[] oDoubleZ = new Double[numValues];
		
		it = mapByDate.keySet().iterator();
		for (int x = 0; x < dateCount && it.hasNext(); x++) {
			Date date = (Date)it.next();
			
			Iterator it2 = grouper.getGroups(content, settings);
			for (int y = 0; y < groupCount && it2.hasNext(); y++) {
				Object group = it2.next();

				int index = (x * groupCount) + y;
				oDateX[index] = date;
				oDoubleY[index] = new Double(y);
				double value = (double)((IntegerMap)mapByDate.get(date)).get(group) * 100.0 / max;
				oDoubleZ[index] = (value != 0) ? new Double(value) : null;
			}
		}
				
		return new DefaultContourDataset(null, oDateX, oDoubleY, oDoubleZ);		
	}

	public int getPreferredHeigth() 
	{
		return 150 + groupCount * 24;
	}

}
