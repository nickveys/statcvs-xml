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
    
	$RCSfile: AvgFileSizeChart.java,v $
	$Date: 2003-06-27 17:23:09 $
*/
package net.sf.statcvs.output.xml.chart;

import java.util.List;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.reportmodel.TimeLine;
import net.sf.statcvs.reports.AvgFileSizeTimeLineReport;

/**
 * FileCountChart
 * 
 * @author Tammo van Lessen
 */
public class AvgFileSizeChart extends TimeLineChart {

	public AvgFileSizeChart(CvsContent content) {
		super("file_size.png", I18n.tr("File Size"));
	
		List files = content.getFiles();
		TimeLine avgFileSize = new AvgFileSizeTimeLineReport(files).getTimeLine();
		setRangeLabel(avgFileSize.getRangeLabel());
		setTimeLine(avgFileSize);
		placeTitle();
	}
}
