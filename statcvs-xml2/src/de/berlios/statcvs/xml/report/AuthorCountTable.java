/*
    StatCvs - CVS statistics generation 
    Copyright (C) 2002  Lukasz Pekacki <lukasz@pekacki.de>
    http://statcvs.sf.net/
    
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package de.berlios.statcvs.xml.report;

import java.util.Iterator;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.util.IntegerMap;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.model.FileGrouper;
import de.berlios.statcvs.xml.model.Grouper;
import de.berlios.statcvs.xml.output.Report;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;
import de.berlios.statcvs.xml.output.TableElement;

/**
 * AuthorsPerFileReport
 * 
 * @author Tammo van Lessen
 * @author Steffen Pingel
 */
public class AuthorCountTable {

	/**
	 * 
	 */
	public static Report generate(CvsContent content, ReportSettings settings) 
	{
		ReportElement root = new ReportElement(settings, I18n.tr("Authors%1"));
		Grouper grouper = settings.getGrouper(new FileGrouper());
		
		IntegerMap filesMap = new IntegerMap();
		Iterator it = settings.getFileIterator(content);
		while (it.hasNext()) {
			CvsFile file = (CvsFile)it.next();
			if (file.isDead()) {
				continue;
			}
			Object group = grouper.getGroup(file);
			filesMap.addInt(group, file.getAuthors().size());
		}
		
		TableElement table = new TableElement(settings, new String[] { grouper.getName(), I18n.tr("Authors") });		
		
		Iterator fIt = filesMap.iteratorSortedByValueReverse();
		int maxItems = settings.getLimit();
		int count = 0;
		while (fIt.hasNext() && count < maxItems) {
			Object group = fIt.next();
			
			table.addRow().addGroup(grouper, group).addInteger("count", filesMap.get(group));
			
			count++;
		}
		
		root.addContent(table);
		return new Report(root, table);
	}

}
