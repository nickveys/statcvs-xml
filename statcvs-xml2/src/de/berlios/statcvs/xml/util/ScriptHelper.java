package de.berlios.statcvs.xml.util;

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
	
}
