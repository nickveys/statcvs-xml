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
    
	$RCSfile: FileCountChart.java,v $
	$Date: 2003-06-27 18:34:33 $
*/
package net.sf.statcvs.output.xml.chart;

import java.util.List;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.reportmodel.TimeLine;
import net.sf.statcvs.reports.FileCountTimeLineReport;

/**
 * FileCountChart
 * 
 * @author Tammo van Lessen
 */
public class FileCountChart extends TimeLineChart {

	public FileCountChart(CvsContent content) {
		super("file_count.png", I18n.tr("File Count"));
	
		List files = content.getFiles();
		TimeLine fileCount = new FileCountTimeLineReport(files).getTimeLine();
		setRangeLabel(fileCount.getRangeLabel());
		addTimeLine(fileCount);
		getChart().setLegend(null);
		placeTitle();
	}
}
