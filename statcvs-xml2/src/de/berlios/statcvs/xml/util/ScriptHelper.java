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

package de.berlios.statcvs.xml.util;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Steffen Pingel
 */
public class ScriptHelper {

	public static boolean atMost(Date date, long millis)
	{
		return date.getTime() > System.currentTimeMillis() - millis;
	}

	public static boolean atMostDaysOld(Date date, int days)
	{
		return atMost(date, (long)days * 24 * 60 * 60 * 1000);
	}
	
	public static boolean isOn(Date date, int year, int month, int day)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_MONTH) == day && cal.get(Calendar.MONTH) + 1 == month && cal.get(Calendar.YEAR) == year;
	}
	
}
