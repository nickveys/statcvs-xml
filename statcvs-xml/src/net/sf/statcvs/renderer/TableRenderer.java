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
    
	$RCSfile: TableRenderer.java,v $
	$Date: 2003-06-17 16:43:02 $
*/
package net.sf.statcvs.renderer;

import java.util.Iterator;

import net.sf.statcvs.reportmodel.Column;
import net.sf.statcvs.reportmodel.Table;
import net.sf.statcvs.util.OutputUtils;

/**
 * Renders a {@link net.sf.statcvs.reportmodel.Table} to HTML
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: TableRenderer.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class TableRenderer {

	private Table table;
	private TableCellRenderer renderer = new TableCellRenderer();

	/**
	 * Creates a new table renderer for the given table model
	 * @param table the table to render
	 */
	public TableRenderer(Table table) {
		this.table = table;
	}

	/**
	 * Renders the table to HTML
	 * @return a String of HTML
	 */
	public String getRenderedTable() {
		String result = "  <table rules=\"groups\" summary=\""
				+ OutputUtils.escapeHtml(table.getSummary()) + "\">\n";
		result += getColumnDescriptions();
		result += getTableHead();
		if (table.showTotals()) {
			result += getTableTotals();
		}
		result += getTableBody();
		result += "  </table>\n\n";
		return result;
	}
	
	private String getColumnDescriptions() {
		String result = "";
		Iterator it = table.getColumnIterator();
		boolean isFirstColumn = true;
		while (it.hasNext()) {
			it.next();
			if (table.hasKeysInFirstColumn() && isFirstColumn) {
				result += "    <colgroup align=\"left\">\n";
				isFirstColumn = false;
			} else {
				result += "    <colgroup align=\"right\">\n";
			}
		}
		return result;
	}

	private String getTableHead() {
		String result = "    <thead>\n      <tr>\n";
		Iterator it = table.getColumnIterator();
		while (it.hasNext()) {
			Column column = (Column) it.next();
			column.renderHead(renderer);
			result += "        " + renderer.getColumnHead() + "\n";
		}
		result += "      </tr>\n    </thead>\n";
		return result;
	}

	private String getTableTotals() {
		String result = "    <tfoot>\n      <tr>\n";
		Iterator it = table.getColumnIterator();
		boolean isFirstColumn = true;
		while (it.hasNext()) {
			Column column = (Column) it.next();
			column.renderTotal(renderer);
			if (isFirstColumn && table.hasKeysInFirstColumn()) {
				result += "        " + renderer.getRowHead() + "\n";
				isFirstColumn = false;
			} else {
				result += "        " + renderer.getTableCell() + "\n";
			}
		}
		result += "      </tr>\n    </tfoot>\n";
		return result;
	}

	private String getTableBody() {
		String result = "    <tbody>\n";
		for (int i = 0; i < table.getRowCount(); i++) {
			result += getTableRow(i);
		}
		result += "    </tbody>\n";
		return result;
	}

	private String getTableRow(int rowIndex) {
		String result;
		if (rowIndex % 2 == 0) {
			result = "      <tr class=\"odd\">\n";
		} else {
			result = "      <tr class=\"even\">\n";
		}
		Iterator it = table.getColumnIterator();
		boolean isFirstColumn = true;
		while (it.hasNext()) {
			Column column = (Column) it.next();
			column.renderCell(rowIndex, renderer);
			if (isFirstColumn && table.hasKeysInFirstColumn()) {
				result += "        " + renderer.getRowHead() + "\n";
				isFirstColumn = false;
			} else {
				result += "        " + renderer.getTableCell() + "\n";
			}
		}
		result += "      </tr>\n";
		return result;
	}
}