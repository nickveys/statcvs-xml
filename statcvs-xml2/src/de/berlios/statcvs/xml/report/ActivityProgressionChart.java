package de.berlios.statcvs.xml.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
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

	private List groupNames = new ArrayList();
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
		plot.setRenderAsPoints(true);
		// don't use plot units for ratios when x axis is date
		plot.setDataAreaRatio(0.0);
		plot.setColorBarLocation(RectangleEdge.BOTTOM);
		
		return new JFreeChart(settings.getProjectName(), null, plot, false);
	}

	private ContourDataset createDataset(Grouper grouper)
	{
		Hashtable mapByDate = new Hashtable();
		// define 100% to correlate at least to 1
		int max = 1;
		long diff = content.getLastDate().getTime() - content.getFirstDate().getTime();
//		long day = 24 * 60 * 60 * 1000; // 86400
//		long windowSize 
//			= (diff > 10 * 30 * day) 
//			? 30 * day
//			: (diff > 10 * 7 * day)
//			? 7 * day
//			: day;
		long windowSize = Math.max(diff / 40, 1);
		
		Date currentDate = null;
		IntegerMap commitsPerGroup = new IntegerMap();
		Iterator it = settings.getRevisionIterator(content);
		long windowStart = 0;
		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision)it.next();
			Date date = rev.getDate();
			if (currentDate == null) {
				currentDate = date;
				windowStart = date.getTime();
			}
			else if (date.getTime() > windowStart + windowSize) {
				// save old map
				max = Math.max(commitsPerGroup.max(), max);
				mapByDate.put(currentDate, commitsPerGroup);
				
				// create new map
				commitsPerGroup = new IntegerMap();
				currentDate = date;
				windowStart += ((date.getTime() - windowStart) / windowSize + 1) * windowSize;
			}
			commitsPerGroup.inc(grouper.getGroup(rev));
		}

		if (currentDate != null) {
			max = Math.max(commitsPerGroup.max(), max);
			mapByDate.put(currentDate, commitsPerGroup);
		}

		Iterator it3 = grouper.getGroups(content, settings);
		while (it3.hasNext()) {
			this.groupNames.add(grouper.getName(it3.next()));
		}
		this.groupCount = groupNames.size();
				
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
		return 140 + groupCount * 24;
	}

}
