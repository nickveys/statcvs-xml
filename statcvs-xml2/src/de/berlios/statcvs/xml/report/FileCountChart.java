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

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.chart.AbstractTimeSeriesChart;
import de.berlios.statcvs.xml.chart.RevisionVisitor;
import de.berlios.statcvs.xml.output.ChartReportElement;
import de.berlios.statcvs.xml.output.Report;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * 
 * 
 * @author Steffen Pingel
 * @author Tammo van Lessen
 */
public class FileCountChart extends AbstractTimeSeriesChart {

	public FileCountChart(CvsContent content, ReportSettings settings)
	{
		super(settings, "file_count.png", I18n.tr("File Count"), I18n.tr("LOC/File"));
	
		addTimeSeries(createTimeSeries(I18n.tr("File Count"), settings.getRevisionIterator(content), new Calculator()),
					  content.getFirstDate(), 0);
		setup(false);
	}

	public static Report generate(CvsContent content, ReportSettings settings)
	{
		return new Report(new ChartReportElement(new FileCountChart(content, settings)));
	}

	public static class Calculator implements RevisionVisitor
	{
		int fileCount = 0;
		
		public int visit(CvsRevision rev)
		{
			fileCount += rev.getFileCountDelta();
			return fileCount;
		}
	}

}
