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
    
	$RCSfile: AuthorsTableReport.java,v $
	$Date: 2003-06-17 16:43:03 $
*/
package net.sf.statcvs.reports;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.reportmodel.AuthorColumn;
import net.sf.statcvs.reportmodel.Table;

/**
 * Table report which creates a table containing the names of
 * all authors, their LOC contributions and number of changes. The
 * table can be sorted by name or LOC.
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: AuthorsTableReport.java,v 1.1 2003-06-17 16:43:03 vanto Exp $
 */
public class AuthorsTableReport extends AbstractLocTableReport 
		implements TableReport {

	private int sortedBy;
	private Table table = null;

	/**
	 * Creates a table report containing all authors, their
	 * number of changes and LOC contributions.
	 * @param content the version control source data
	 * @param sortedBy a SORT_XXXX constant
	 */
	public AuthorsTableReport(CvsContent content, int sortedBy) {
		super(content);
		this.sortedBy = sortedBy;
	}
	
	/**
	 * @see net.sf.statcvs.reports.TableReport#calculate()
	 */
	public void calculate() {
		calculateChangesAndLinesPerAuthor(getContent().getRevisionIterator());
		table = createChangesAndLinesTable(
				new AuthorColumn(),
				sortedBy,
				Messages.getString("AUTHORS_TABLE_SUMMARY"));
	}

	/**
	 * @see net.sf.statcvs.reports.TableReport#getTable()
	 */
	public Table getTable() {
		return table;
	}
}
