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
import java.util.Map;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;

import org.jfree.data.time.TimeSeries;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.chart.AbstractTimeSeriesChart;
import de.berlios.statcvs.xml.chart.RevisionVisitor;
import de.berlios.statcvs.xml.chart.RevisionVisitorFactory;
import de.berlios.statcvs.xml.model.Grouper;
import de.berlios.statcvs.xml.output.ChartReportElement;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * 
 * 
 * @author Steffen Pingel
 * @author Tammo van Lessen
 */
public class AvgFilesizeChart extends AbstractTimeSeriesChart {

	public AvgFilesizeChart(CvsContent content, ReportSettings settings) 
	{
		super(settings, "avgsize%1.png", I18n.tr("Average Filesize%1"), I18n.tr("LOC / File"));
	
		Grouper grouper = settings.getGrouper();
		if (grouper != null) {
			Map serieses = createTimeSerieses(grouper, settings.getRevisionIterator(content), new RevisionVisitorFactory(Calculator.class.getName()));
			
			Object feo = settings.getForEachObject();
			
			for (Iterator it = serieses.keySet().iterator(); it.hasNext();) {
				Object group = it.next();
				addTimeSeries((TimeSeries)serieses.get(group));
				
				if (group == settings.getForEachObject()) {
					// make line thicker
					getChart().getXYPlot().getRenderer().setSeriesStroke(getSeriesCount() - 1, new BasicStroke(2,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
				}
				
			}
			
			setup(true);
		}
		else {
			addTimeSeries(createTimeSeries(I18n.tr("Average Filesize"), settings.getRevisionIterator(content), new Calculator()));
			setup(false);
		}
	}

	public static ReportElement generate(CvsContent content, ReportSettings settings)
	{
		return new ChartReportElement(new AvgFilesizeChart(content, settings));
	}

	public static class Calculator implements RevisionVisitor
	{
		int loc = 0;
		int fileCount = 0;
		
		public int visit(CvsRevision rev)
		{
			loc += rev.getLinesDelta();
			fileCount += rev.getFileCountDelta();
			return (fileCount == 0) ? 0 : loc / fileCount;
		}
	}
	
}