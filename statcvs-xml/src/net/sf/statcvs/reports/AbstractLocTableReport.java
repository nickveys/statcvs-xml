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
    
	$RCSfile: AbstractLocTableReport.java,v $ 
	Created on $Date: 2003-06-17 16:43:03 $ 
*/
package net.sf.statcvs.reports;

import java.util.Iterator;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.reportmodel.GenericColumn;
import net.sf.statcvs.reportmodel.IntegerColumn;
import net.sf.statcvs.reportmodel.RatioColumn;
import net.sf.statcvs.reportmodel.Table;
import net.sf.statcvs.util.IntegerMap;

/**
 * Convenience superclass for table reports related to authors and directories.
 * Contains methods to calculate some common stuff for these tables.
 * @author Lukasz Pekacki
 * @version $Id: AbstractLocTableReport.java,v 1.1 2003-06-17 16:43:03 vanto Exp $
 */
public abstract class AbstractLocTableReport {

	/**
	 * Sort the authors table by name
	 * */
	public static final int SORT_BY_NAME = 0;

	/**
	 * Sort the authors table by lines of code
	 * */
	public static final int SORT_BY_LINES = 1;

	private CvsContent content;

	private IntegerMap changesMap = new IntegerMap();
	private IntegerMap linesMap = new IntegerMap();
	
	/**
     * Constructor
	 * @param content render table on specified content
	 */
	public AbstractLocTableReport(CvsContent content) {
		this.content = content;
	}

	protected void calculateChangesAndLinesPerAuthor(RevisionIterator revs) {
		while (revs.hasNext()) {
			CvsRevision rev = revs.next();
			changesMap.addInt(rev.getAuthor(), 1);
			linesMap.addInt(rev.getAuthor(), rev.getLineValue()); 
		}
	}

	protected void calculateChangesAndLinesPerDirectory(RevisionIterator revs) {
		while (revs.hasNext()) {
			CvsRevision rev = revs.next();
			Directory dir = rev.getFile().getDirectory();
			changesMap.addInt(dir, 1);
			linesMap.addInt(dir, rev.getLineValue()); 
		}
	}

	protected Table createChangesAndLinesTable(
			GenericColumn keys, int sortedBy, String summary) {

		Table result = new Table(summary);
		IntegerColumn changes =
				new IntegerColumn(Messages.getString("COLUMN_CHANGES"));
		IntegerColumn linesOfCode =
				new IntegerColumn(Messages.getString("COLUMN_LOC"));
		RatioColumn linesPerChange = new RatioColumn(
				Messages.getString("COLUMN_LOC_PER_CHANGE"),
				linesOfCode,
				changes);
		keys.setTotal(Messages.getString("TOTALS"));
		changes.setShowPercentages(true);
		linesOfCode.setShowPercentages(true);
		result.addColumn(keys);
		result.addColumn(changes);
		result.addColumn(linesOfCode);
		result.addColumn(linesPerChange);
		result.setKeysInFirstColumn(true);
		
		Iterator it;
		if (sortedBy == SORT_BY_NAME) {
			it = linesMap.iteratorSortedByKey();
		} else {
			it = linesMap.iteratorSortedByValueReverse();
		}
		while (it.hasNext()) {
			Object key = it.next();
			keys.addValue(key);
			changes.addValue(changesMap.get(key));
			linesOfCode.addValue(linesMap.get(key));
		}
		if (result.getRowCount() > 1) {
			result.setShowTotals(true);
		}
		return result;
	}

	protected CvsContent getContent() {
		return content;
	}

	protected IntegerMap getChangesMap() {
		return changesMap;
	}

	protected IntegerMap getLinesMap() {
		return linesMap;
	}

}