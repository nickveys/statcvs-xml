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

import java.awt.BasicStroke;
import java.util.Iterator;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.ChartReportElement;
import de.berlios.statcvs.xml.output.Report;
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
			addTimeSeries(settings.getFullname(author), author.getRevisions().iterator());
			if (author == highlightAuthor) {
				// make line thicker
				getChart().getXYPlot().getRenderer().setSeriesStroke(i, new BasicStroke(2,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
			}
			++i;
		}

		addSymbolicNames(settings.getSymbolicNameIterator(content));
		setup(true);
	}

	public static Report generate(CvsContent content, ReportSettings settings)
	{
		return new Report(new ChartReportElement(new LocByAuthorChart(content, settings)));
	}

}
