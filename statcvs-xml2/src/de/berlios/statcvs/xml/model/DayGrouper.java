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
	public Iterator getGroups(CvsContent content, ReportSettings settings) 
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
