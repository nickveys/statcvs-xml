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
    
	$RCSfile: RatioColumn.java,v $
	$Date: 2003-06-17 16:43:03 $
*/
package net.sf.statcvs.reportmodel;

import net.sf.statcvs.renderer.TableCellRenderer;

/**
 * A column showing the ratio between two {@link IntegerColumn}s.
 * The two columns do not have to be shown in the table.
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: RatioColumn.java,v 1.1 2003-06-17 16:43:03 vanto Exp $
 */
public class RatioColumn extends Column {

	private String title;
	private IntegerColumn col1;
	private IntegerColumn col2;

	/**
	 * Creates a new <tt>RatioColumn</tt>, which contains the ratio
	 * between col1 and col2
	 * @param title the title for the column
	 * @param col1 the first column
	 * @param col2 the second column
	 */
	public RatioColumn(String title, IntegerColumn col1, IntegerColumn col2) {
		this.title = title;
		this.col1 = col1;
		this.col2 = col2;
	}

	/**
	 * @see net.sf.statcvs.reportmodel.Column#getRows()
	 */
	public int getRows() {
		return col1.getRows();
	}

	/**
	 * @see net.sf.statcvs.reportmodel.Column#renderHead(net.sf.statcvs.renderer.TableCellRenderer)
	 */
	public void renderHead(TableCellRenderer renderer) {
		renderer.renderCell(title);
	}

	/**
	 * @see net.sf.statcvs.reportmodel.Column#renderCell
	 */
	public void renderCell(int rowIndex, TableCellRenderer renderer) {
		renderer.renderCell(getRatio(col1.getValue(rowIndex), col2.getValue(rowIndex)));		
	}

	/**
	 * @see net.sf.statcvs.reportmodel.Column#renderTotal(net.sf.statcvs.renderer.TableCellRenderer)
	 */
	public void renderTotal(TableCellRenderer renderer) {
		renderer.renderCell(getRatio(col1.getSum(), col2.getSum()));		
	}

	private String getRatio(int val1, int val2) {
		if (val2 == 0) {
			return "-";
		}
		int ratioTimes10 = (val1 * 10) / val2;
		double ratio = (double) ratioTimes10 / 10;
		return Double.toString(ratio);
	}
}
