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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
 * Provides a grouper that groups months and years.
 * 
 * @author Tammo van Lessen
 */
public class MonthYearGrouper extends Grouper {

	private Calendar cal = new GregorianCalendar(TimeZone.getDefault());
	private List monthNames;
	private List months = new ArrayList(); 
	private DateFormat df = new SimpleDateFormat("yyyy");
	
	public MonthYearGrouper()
	{
		super("month", I18n.tr("Month"));
		
		monthNames = new ArrayList(12);
		monthNames.add(I18n.tr("January"));
		monthNames.add(I18n.tr("February"));
		monthNames.add(I18n.tr("March"));
		monthNames.add(I18n.tr("April")); 
		monthNames.add(I18n.tr("May"));
		monthNames.add(I18n.tr("June"));
		monthNames.add(I18n.tr("July"));
		monthNames.add(I18n.tr("August"));
		monthNames.add(I18n.tr("September"));
		monthNames.add(I18n.tr("October"));
		monthNames.add(I18n.tr("November"));
		monthNames.add(I18n.tr("December"));
	}


    /**
     * @see de.berlios.statcvs.xml.model.Grouper#getGroups(net.sf.statcvs.model.CvsContent, de.berlios.statcvs.xml.output.ReportSettings)
     */
    public Iterator getGroups(CvsContent content, ReportSettings settings) {
        return months.iterator();
    }

    /**
     * @see de.berlios.statcvs.xml.model.Grouper#getName(java.lang.Object)
     */
    public String getName(Object group) {
		return group.toString();
    }

    /**
     * @see de.berlios.statcvs.xml.model.Grouper#getGroup(net.sf.statcvs.model.CvsRevision)
     */
    public Object getGroup(CvsRevision rev) {
		cal.setTime(rev.getDate());
		
		StringBuffer month = new StringBuffer();
		month.append((String)monthNames.get(cal.get(Calendar.MONTH)))
			 .append(" ")
			 .append(df.format(cal.getTime()));
			 
		String m = month.toString();
			  
		if (!months.contains(m)) {
			months.add(m);	
		}

		return m;
    }

}
