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
		return atMost(date, days * 24 * 60 * 60 * 1000);
	}
	
	public static boolean isOn(Date date, int year, int month, int day)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_MONTH) == day && cal.get(Calendar.MONTH) + 1 == month && cal.get(Calendar.YEAR) == year;
	}
	
}
