package de.berlios.statcvs.xml.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * @author Steffen Pingel
 */
public class HourGrouper extends Grouper {
	
	private Calendar cal = new GregorianCalendar(TimeZone.getDefault());
	private List hours; 

	public HourGrouper()
	{
		super("hour", I18n.tr("Hour [{0}]", TimeZone.getDefault().getID()));
		
		hours = new ArrayList(24);
		for (int i = 0; i < 24; i++) {
			hours.add(new Integer(i));
		}
	}

	public Object getGroup(CvsRevision rev) 
	{
		cal.setTime(rev.getDate());
		return hours.get(cal.get(Calendar.HOUR_OF_DAY));
	}

	/**
	 *  @see de.berlios.statcvs.xml.model.Grouper#getGroups(net.sf.statcvs.model.CvsContent)
	 */
	public Iterator getGroups(CvsContent content, ReportSettings settings) 
	{
		return hours.iterator();
	}

	/**
	 *  @see de.berlios.statcvs.xml.model.Grouper#getValue(java.lang.Object)
	 */
	public String getName(Object group) 
	{
		return group.toString();
	}

}
