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
import java.util.Date;
import java.util.Iterator;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.chart.RevisionVisitor;
import de.berlios.statcvs.xml.chart.AbstractTimeSeriesChart;
import de.berlios.statcvs.xml.output.ChartReportElement;
import de.berlios.statcvs.xml.output.ForEachObject;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * LocChart
 * 
 * @author Tammo van Lessen
 * @author Steffen Pingel
 */
public class LocByObjectChart extends LocChart {
    
    private CvsContent content;
	
	public LocByObjectChart(CvsContent content, ReportSettings settings)
	{
		super(content, settings, I18n.tr("Lines Of Code%1"));

		Object highlightAuthor = settings.getForEachObject();
				
		// add a time line for each author
		int i = 0;
		Iterator it = settings.getForEachIterator(content);
		while (it.hasNext()) {
			ForEachObject foreach = (ForEachObject)it.next();
			settings.setForEach(foreach);
			addTimeSeries(settings.getForeachId(), settings.getRevisionIterator(content));
			if (foreach == highlightAuthor) {
				// make line thicker
				getChart().getXYPlot().getRenderer().setSeriesStroke(i, new BasicStroke(2,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
			}
			++i;
		}
		settings.setForEach(null);
		
		addSymbolicNames(settings.getSymbolicNameIterator(content));
		setup(true);
	}

	public static ReportElement generate(CvsContent content, ReportSettings settings)
	{
		return new ChartReportElement(new LocByObjectChart(content, settings));
	}

}
