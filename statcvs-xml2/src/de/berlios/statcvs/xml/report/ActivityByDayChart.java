package de.berlios.statcvs.xml.report;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.chart.AbstractBarChart;
import de.berlios.statcvs.xml.output.ChartReportElement;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * 
 * 
 * @author Tammo van Lessen
 * @author Steffen Pingel
 */
public class ActivityByDayChart extends AbstractBarChart {

	private String[] categoryNamesDays = new String[] {
			I18n.tr("Sunday"), I18n.tr("Monday"), I18n.tr("Tuesday"), 
			I18n.tr("Wednesday"), I18n.tr("Thursday"), I18n.tr("Friday"), 
			I18n.tr("Saturday") };

	public ActivityByDayChart(CvsContent content, ReportSettings settings)
	{
		super(settings, "activity_day%1.png", I18n.tr("Activity by Day%1"),
			null, I18n.tr("Commits")); 

		setupValues(settings.getRevisionIterator(content));
		setup(false);
	}
	
	/**
	 * 
	 */
	private void setupValues(Iterator it) 
	{
		double[] values = new double[categoryNamesDays.length];

		Calendar cal = new GregorianCalendar();
		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision)it.next();
			cal.setTime(rev.getDate());
			values[cal.get(Calendar.DAY_OF_WEEK) - 1]++;
		}
		
		for (int i = 0; i < values.length; i++) {
			dataset.addValue(values[i], I18n.tr("Activity"), categoryNamesDays[i]);
		}
	}

	public static ReportElement generate(CvsContent content, ReportSettings settings)
	{
		return new ChartReportElement(new ActivityByDayChart(content, settings));
	}

}
