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

/**
 * @author Steffen Pingel
 */
public class DayGrouper extends Grouper {
	
	private Calendar cal = new GregorianCalendar(TimeZone.getDefault());
	private List days; 

	public DayGrouper()
	{
		super("day", I18n.tr("Day"));
		
		days = new ArrayList(7);
		days.add(I18n.tr("Sunday"));
		days.add(I18n.tr("Monday"));
		days.add(I18n.tr("Tuesday"));
		days.add(I18n.tr("Wednesday")); 
		days.add(I18n.tr("Thursday"));
		days.add(I18n.tr("Friday"));
		days.add(I18n.tr("Saturday"));
	}

	public Object getGroup(CvsRevision rev) 
	{
		cal.setTime(rev.getDate());
		return days.get(cal.get(Calendar.DAY_OF_WEEK) - 1);
	}

	/**
	 *  @see de.berlios.statcvs.xml.model.Grouper#getGroups(net.sf.statcvs.model.CvsContent)
	 */
	public Iterator getGroups(CvsContent content) 
	{
		return days.iterator();
	}

	/**
	 *  @see de.berlios.statcvs.xml.model.Grouper#getValue(java.lang.Object)
	 */
	public String getName(Object group) 
	{
		return group.toString();
	}

}
