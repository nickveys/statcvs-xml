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
    
	$RCSfile: FilesWithMostRevisionsTableReport.java,v $
	$Date: 2003-06-17 16:43:03 $
*/
package net.sf.statcvs.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.FilesRevisionCountComparator;
import net.sf.statcvs.reportmodel.FileColumn;
import net.sf.statcvs.reportmodel.IntegerColumn;
import net.sf.statcvs.reportmodel.Table;

/**
 * Table report for a table containing the files with most changes
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: FilesWithMostRevisionsTableReport.java,v 1.1 2003-06-17 16:43:03 vanto Exp $
 */
public class FilesWithMostRevisionsTableReport implements TableReport {
	private List files;
	private Table table;
	private int maxRows;

	/**
	 * Creates a table containing the files with most changes from a file list
	 * @param files a <tt>List</tt> of
	 *              {@link net.sf.statcvs.model.CvsFile}s
	 * @param maxRows the maximum number of files displayed in the table 
	 */
	public FilesWithMostRevisionsTableReport(List files, int maxRows) {
		this.files = new ArrayList(files);
		this.maxRows = maxRows;
	}

	/**
	 * @see net.sf.statcvs.reports.TableReport#calculate()
	 */
	public void calculate() {
		Collections.sort(files, new FilesRevisionCountComparator());
		table = new Table(Messages.getString("FILES_WITH_MOST_REVISIONS_TABLE_SUMMARY"));
		table.setKeysInFirstColumn(true);
		FileColumn filesCol = new FileColumn();
		filesCol.setWithIcon(true);
		IntegerColumn locCol = new IntegerColumn(Messages.getString("COLUMN_CHANGES"));
		locCol.setShowPercentages(false);
		table.addColumn(filesCol);
		table.addColumn(locCol);
		int lines = 0;
		Iterator it = files.iterator();
		while (it.hasNext() && lines < maxRows) {
			CvsFile file = (CvsFile) it.next();
			filesCol.addValue(file);
			locCol.addValue(file.getRevisions().size());
			lines++;
		} 
	}

	/**
	 * @see net.sf.statcvs.reports.TableReport#getTable()
	 */
	public Table getTable() {
		return table;
	}
}
