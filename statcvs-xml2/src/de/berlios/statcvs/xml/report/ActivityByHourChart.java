/*
 * statcvs-xml
 * TODO
 * Created on 27.06.2003
 *
 */
package de.berlios.statcvs.xml.report;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.chart.AbstractBarChart;
import de.berlios.statcvs.xml.output.ChartReportElement;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * ActivityChart
 * 
 * @author Tammo van Lessen
 * @author Steffen Pingel
 */
public class ActivityByHourChart extends AbstractBarChart {

	private String[] categoryNamesHours = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", 
		"8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", 
		"20", "21",	"22", "23" };
	
	public ActivityByHourChart(CvsContent content, ReportSettings settings)
	{
		super(settings, "activity_time%1.png", I18n.tr("Activity by Hour%1"),
			null, I18n.tr("Commits")); 

		setupValues(settings.getRevisionIterator(content));
		setup(false);
	}
	
	/**
	 * 
	 */
	private void setupValues(Iterator it) 
	{
		double[] values = new double[categoryNamesHours.length];

		Calendar cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getDefault());
		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision)it.next();
			cal.setTime(rev.getDate());
			values[cal.get(Calendar.HOUR_OF_DAY)]++;
		}

		for (int i = 0; i < values.length; i++) {
			dataset.addValue(values[i], I18n.tr("Activity"), categoryNamesHours[i]);
		}
	}

	public static ReportElement generate(CvsContent content, ReportSettings settings)
	{
		return new ChartReportElement(new ActivityByHourChart(content, settings));
	}

}
