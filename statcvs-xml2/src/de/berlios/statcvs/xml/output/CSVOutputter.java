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
import java.util.Date;
import java.util.Iterator;

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


	public static void generate(ReportSettings settings, CvsContent content, File file) 
		throws IOException
	{
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		try {
			int totalLoc = 0;
			IntegerMap locByAuthor = new IntegerMap();
			
			Date lastDate = null;
			
			Iterator it = content.getRevisions().iterator();
			while (it.hasNext()) {			
				CvsRevision rev = (CvsRevision)it.next();
				
				totalLoc += rev.getLinesDelta();
				locByAuthor.addInt(rev.getAuthor(), rev.getLinesDelta());
				
				if (lastDate == null) {
					lastDate = rev.getDate();
					dumpHeader(out, content);
				}
				else if (!lastDate.equals(rev.getDate())) {
					dump(out, content, lastDate, totalLoc, locByAuthor);
					lastDate = rev.getDate();
				}
			}
			
			if (lastDate != null) {
				dump(out, content, lastDate, totalLoc, locByAuthor);
			}
		}
		finally {
			out.close();
		}
	}

	public static void dumpHeader(Writer out, CvsContent content) 
		throws IOException
	{
		out.write("Date");
		out.write(";Total Lines of Code");
		for (Iterator it = content.getAuthors().iterator(); it.hasNext();) {
			out.write(";" + ((Author)it.next()).getName());
		}
		out.write("\r\n");
	}

	public static void dump(Writer out, CvsContent content, Date date, int totalLoc, IntegerMap locByAuthor) 
		throws IOException
	{
		out.write(date.getTime() + "");
		out.write(";" + totalLoc);
		for (Iterator it = content.getAuthors().iterator(); it.hasNext();) {
			out.write(";" + locByAuthor.get(it.next()));
		}
		out.write("\r\n");
	}
}
