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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Merges two cvs log files.    
 * 
 * @author Steffen Pingel
  * @version $Id: CvsLogMerger.java,v 1.1 2004-03-06 21:41:24 squig Exp $
 */
public class CvsLogMerger {

	public static final String FILE_BLOCK_PREFIX = "RCS file: ";

	/**
	 * Revision Delimiter in CVS log file
	 */
	public static final String REVISION_DELIMITER =
			"----------------------------";
	/**
	 * File Delimiter in CVS log file
	 */
	public static final String FILE_DELIMITER
			= "======================================"
			+ "=======================================";

	private static final String LOG_TIMESTAMP_FORMAT =
		"yyyy/MM/dd HH:mm:ss zzz";
	private static final Locale LOG_TIMESTAMP_LOCALE = Locale.US;
	private static SimpleDateFormat logTimeFormat =
		new SimpleDateFormat(LOG_TIMESTAMP_FORMAT, LOG_TIMESTAMP_LOCALE);

	/**
	 * Main method.
	 * @param args command line options
	 */
	public static void main(String[] args) 
	{
		if (args.length != 2) {
			System.err.println("usage: java de.berlios.statcvs.xml.util.CvsLogMerger oldfile newfile");
			System.exit(1);
		}

		try {
			BufferedReader inOld = new BufferedReader(new FileReader(args[0]));
			try {
				BufferedReader inNew= new BufferedReader(new FileReader(args[1]));
				try {
					merge(inOld, inNew);
				}
				finally {
					inNew.close();
				}
			}
			finally {
				inOld.close();
			}
		}
		catch (IOException e) {
			System.err.println(e.toString());
			System.exit(1);
		}
	}

	private static void merge(BufferedReader inOld, BufferedReader inNew) throws IOException
	{
		String cvsFilename = null;
		Date lastRevisionDate = null;

		String line;
		while ((line = inNew.readLine()) != null) {
			if (line.startsWith(FILE_BLOCK_PREFIX)) {
				cvsFilename = line.substring(FILE_BLOCK_PREFIX.length()); 
			}
			else if (line.startsWith(REVISION_DELIMITER)) {
				println(line);             // ------------------
				println(inNew.readLine()); // revision:
				line = inNew.readLine();   // date:
				if (line != null) {
					lastRevisionDate = convertFromLogTime(line);
				}
			}
			else if (line.equals(FILE_DELIMITER)) {
				copyThrough(inOld, cvsFilename, lastRevisionDate);
				cvsFilename = null;
				lastRevisionDate = null;
			}
			
			println(line);
		}
		
	}

	/**
	 * @param line
	 * @return
	 */
	private static Date convertFromLogTime(String line) throws IOException 
	{
		// get the creation date
		int endOfDateIndex = line.indexOf(';', 6);
		String dateString = line.substring(6, endOfDateIndex) + " GMT";
		try {
			return logTimeFormat.parse(dateString);
		}
		catch (ParseException e) {
			throw new IOException("Invalid date format: " + dateString);
		}
	}

	/**
	 * @param inNew
	 * @return
	 */
	private static void copyThrough(BufferedReader in, String cvsFilename, Date lastDate) throws IOException 
	{
		int state = 1;
		String line;
		while ((line = in.readLine()) != null && state == 1) {
			if (line.startsWith(FILE_BLOCK_PREFIX)) {
				if (cvsFilename.equals(line.substring(FILE_BLOCK_PREFIX.length()))) {
					state = 2; 
				}
			}
		}
		
		if (state == 2 && lastDate != null) {
			StringBuffer sb = new StringBuffer();
			while ((line = in.readLine()) != null && state == 2) {
				if (line.startsWith(REVISION_DELIMITER)) {
					sb.append(line + "\n");             // ------------------
					sb.append(in.readLine() + "\n");    // revision:
					line = in.readLine();        // date:
					sb.append(line);
					Date date = convertFromLogTime(line);
					if (date.getTime() > lastDate.getTime()) {
						println(sb.toString());
						state = 3;
					}
				}
			}
		}

		while ((line = in.readLine()) != null && state == 2) {
			if (line.startsWith(FILE_DELIMITER)) {
				return;
			}
			println(line);
		}
	}
	
	private static void println(String line) throws IOException
	{
		if (line == null) {
			throw new IOException("Unexpected end of file");
		}
		System.out.println(line);
	}
	
}
