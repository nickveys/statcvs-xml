/*
 * statcvs-xml
 * TODO
 * Created on 27.06.2003
 *
 */
package net.sf.statcvs.output.xml.chart;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.output.xml.document.AuthorDocument;

/**
 * ActivityChart
 * 
 * @author Tammo van Lessen
 * @version $id: $
 */
public class ActivityChart extends AbstractBarChart {

	private RevisionIterator revIt;
	public static final int BY_HOUR = 1;
	public static final int BY_DAY = 2;

	private String[] categoryNamesHours = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", 
		"8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", 
		"20", "21",	"22", "23" };

	private String[] categoryNamesDays = new String[] {
			I18n.tr("Sunday"), I18n.tr("Monday"), I18n.tr("Tuesday"), 
			I18n.tr("Wednesday"), I18n.tr("Thursday"), I18n.tr("Friday"), 
			I18n.tr("Saturday") };

	private double[] values;
	
	public ActivityChart(CvsContent content, int type) {
		this(content.getRevisionIterator(), type);
		switch (type) {
			case BY_HOUR :
				setTitle(I18n.tr("Activity by Hour"));
				setFilename("activity_time.png");
				setValuesByHour();
				break;
			case BY_DAY :
				setTitle(I18n.tr("Activity by Day"));
				setFilename("activity_day.png");
				setValuesByDay();
				break;
		}
		placeTitle();
	}
	
	public ActivityChart(Author author, int type) {
		this(author.getRevisionIterator(), type);
		String authorName = AuthorDocument.escapeAuthorName(author.getName());
		switch (type) {
			case BY_HOUR :
				setTitle(I18n.tr("Activity by Hour for {0}",author.getName()));
				setFilename("activity_time_"+authorName+".png");
				setValuesByHour();
				break;
			case BY_DAY :
				setTitle(I18n.tr("Activity by Day for {0}",author.getName()));
				setFilename("activity_day_"+authorName+".png");
				setValuesByDay();
				break;
		}
		placeTitle();
	}

	public ActivityChart(RevisionIterator revIt, int type) {
		super("null", "null");
		this.revIt = revIt;
		setCategoryAxisLabel(null);
		setValueAxisLabel(I18n.tr("Commits"));
		getChart().setLegend(null);
	}
	/**
	 * 
	 */
	private void setValuesByDay() {
		values = new double[categoryNamesDays.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = 0;
		}
		while (revIt.hasNext()) {
			CvsRevision rev = revIt.next();
			Date date = rev.getDate();
			Calendar cal = new GregorianCalendar();
			cal.setTime(date);
			int day = cal.get(Calendar.DAY_OF_WEEK);
			values[day - 1]++;
		}
		for (int i=0; i<values.length; i++) {
			dataset.addValue(values[i], "Activity", categoryNamesDays[i]);
		}
	}
	/**
	 * 
	 */
	private void setValuesByHour() {
		values = new double[categoryNamesHours.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = 0;
		}

		while (revIt.hasNext()) {
			CvsRevision rev = revIt.next();
			Date date = rev.getDate();
			Calendar cal = new GregorianCalendar();
			cal.setTime(date);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			values[hour]++;
		}
		for (int i=0; i<values.length; i++) {
			dataset.addValue(values[i], "Activity", categoryNamesHours[i]);
		}
	}

}
