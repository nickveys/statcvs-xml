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

import java.awt.BasicStroke;
import java.util.Iterator;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.ChartReportElement;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * LocChart
 * 
 * @author Tammo van Lessen
 * @author Steffen Pingel
 */
public class LocByAuthorChart extends LocChart {
    
    private CvsContent content;
	
	public LocByAuthorChart(CvsContent content, ReportSettings settings)
	{
		super(content, settings, I18n.tr("Lines Of Code (per Author)"));

		Object highlightAuthor = settings.getForEachObject();
				
		// add a time line for each author
		int i = 0;
		Iterator it = content.getAuthors().iterator();
		while (it.hasNext()) {
			Author author = (Author)it.next();
			addTimeSeries(author.getName(), author.getRevisions().iterator());
			if (author == highlightAuthor) {
				// make line thicker
				getChart().getXYPlot().getRenderer().setSeriesStroke(i, new BasicStroke(2,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
			}
			++i;
		}

		addSymbolicNames(settings.getSymbolicNameIterator(content));
		setup(true);
	}

	public static ReportElement generate(CvsContent content, ReportSettings settings)
	{
		return new ChartReportElement(new LocByAuthorChart(content, settings));
	}

}
