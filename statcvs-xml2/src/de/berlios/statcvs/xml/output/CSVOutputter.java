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
package de.berlios.statcvs.xml.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.util.IntegerMap;

/**
 * Creates a CSV file that contains loc information.
 * 
 * @author Steffen Pingel
 */
public class CSVOutputter {

	public static final TimeZone utc = TimeZone.getTimeZone("UTC"); 
	public static final DateFormat df = new SimpleDateFormat("MM/yyyy", Locale.US);
	static {
		df.setTimeZone(utc);
	}
	public static final Calendar cal = new GregorianCalendar(utc, Locale.US);
	
	public static final String SEPARATOR = ",";
	private static final String LINEBREAK = "\r\n";
	
	public static void generate(ReportSettings settings, CvsContent content, File file) 
		throws IOException
	{
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		try {
			int totalLoc = 0;
			IntegerMap locByAuthor = new IntegerMap();
			
			Date lastDate = null;
			int lastMonth = -1;
			int lastYear = -1;
			
			Iterator it = content.getRevisions().iterator();
			while (it.hasNext()) {			
				CvsRevision rev = (CvsRevision)it.next();
				
				if (lastDate == null) {
					lastDate = rev.getDate();
					cal.setTime(lastDate);
					lastMonth = cal.get(Calendar.MONTH);
					lastYear = cal.get(Calendar.YEAR);

					dumpHeader(out, content, settings.getProjectName());
				}
				else {
					cal.setTime(rev.getDate());
					if (lastMonth != cal.get(Calendar.MONTH)
						 || lastYear != cal.get(Calendar.YEAR)) {
						dump(out, content, lastDate, rev.getDate(), totalLoc, locByAuthor);
						
						lastDate = rev.getDate();
						cal.setTime(lastDate);
						lastMonth = cal.get(Calendar.MONTH);
						lastYear = cal.get(Calendar.YEAR);
					}
				}
				
				totalLoc += rev.getLinesDelta();
				locByAuthor.addInt(rev.getAuthor(), rev.getLinesDelta());
			}
			
			if (lastDate != null) {
				dump(out, content, lastDate, totalLoc, locByAuthor);
			}
		}
		finally {
			out.close();
		}
	}

	public static void dumpHeader(Writer out, CvsContent content, String projectName) 
		throws IOException
	{
		out.write("Project: ");
		out.write(projectName);
		out.write(LINEBREAK);
		
		out.write("Date");
		out.write(SEPARATOR);
		out.write("Total LOC");
		for (Iterator it = content.getAuthors().iterator(); it.hasNext();) {
			out.write(SEPARATOR);
			out.write(((Author)it.next()).getName());
		}
		out.write(LINEBREAK);
	}

	public static void dump(Writer out, CvsContent content, Date date, Date nextDate, int totalLoc, IntegerMap locByAuthor) 
		throws IOException
	{
		System.out.println("writing data for " + date + " - " + nextDate);

		cal.setTime(nextDate);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);

		cal.setTime(date);
		while (cal.get(Calendar.YEAR) <  year || cal.get(Calendar.MONTH) < month) {
			dump(out, content, cal.getTime(), totalLoc, locByAuthor);
			cal.add(Calendar.MONTH, 1);
		}
	}
	
	public static void dump(Writer out, CvsContent content, Date date, int totalLoc, IntegerMap locByAuthor) 
		throws IOException
	{
		System.out.println("writing data for " + date);
		out.write(df.format(date));
		out.write(SEPARATOR + totalLoc);
		for (Iterator it = content.getAuthors().iterator(); it.hasNext();) {
			out.write(SEPARATOR + locByAuthor.get(it.next()));
		}
		out.write(LINEBREAK);
	}
}
