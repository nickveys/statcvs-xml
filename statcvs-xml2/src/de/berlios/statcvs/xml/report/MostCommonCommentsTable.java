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

package de.berlios.statcvs.xml.report;

import java.util.Iterator;

import net.sf.statcvs.model.Commit;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.util.IntegerMap;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.model.CommitCommentGrouper;
import de.berlios.statcvs.xml.model.Grouper;
import de.berlios.statcvs.xml.output.Report;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;
import de.berlios.statcvs.xml.output.TableElement;

/**
 * @author Tammo van Lessen
 */
public class MostCommonCommentsTable {

	public static Report generate(CvsContent content, ReportSettings settings) 
	{
		ReportElement root = new ReportElement(settings, I18n.tr("Most Common Commit Comments"));
		
		Grouper grouper = new CommitCommentGrouper();
		
		IntegerMap commentFrequency = new IntegerMap();
		
		Iterator commIt = content.getCommits().iterator();
		while (commIt.hasNext())  {
			Commit commit = (Commit)commIt.next();
			commentFrequency.inc(grouper.getGroup(commit));
		}
		
		TableElement table = new TableElement(settings, new String[] { 
																   I18n.tr("Comment"), I18n.tr("Frequency") });

		Iterator it = commentFrequency.iteratorSortedByValueReverse();
		int maxItems = settings.getLimit();
		int count = 0;
		while (it.hasNext()  && count < maxItems) {
			String comment = (String)it.next();
			table.addRow().addString("comment", comment)
									.addInteger("frequency", commentFrequency.get(comment), 
											commentFrequency.getPercent(comment));
			count++;
		}
		
		root.addContent(table);
		
		return new Report(root);
	}
	
}
