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
    
	$RCSfile: DirectorySizesPage.java,v $ 
	Created on $Date: 2003-06-17 16:43:02 $ 
*/

package net.sf.statcvs.output;

import java.io.IOException;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.renderer.TableRenderer;
import net.sf.statcvs.reportmodel.Table;
import net.sf.statcvs.reports.DirectoriesTableReport;
import net.sf.statcvs.reports.TableReport;

/**
 * A page containing the directory size pie chart and the
 * directories table
 * 
 * @author anja
 */
public class DirectorySizesPage extends HTMLPage {

	/**
	 * @see net.sf.statcvs.output.HTMLPage#HTMLPage(CvsContent)
	 */
	public DirectorySizesPage(CvsContent content) throws IOException {
		super(content);
		setFileName("dir_sizes.html");
		setPageName(Messages.getString("DIRECTORY_SIZES_TITLE"));
		createPage();
	}

	protected void printBody() throws IOException {
		printBackLink();
		printParagraph(img("module_sizes.png", 640, 480));
		TableReport report = new DirectoriesTableReport(getContent());
		report.calculate();
		Table table = report.getTable();
		print(new TableRenderer(table).getRenderedTable());
	}
}
