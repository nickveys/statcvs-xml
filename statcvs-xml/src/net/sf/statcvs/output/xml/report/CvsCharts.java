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
    
	$RCSfile: CvsCharts.java,v $
	$Date: 2003-06-24 19:18:59 $ 
*/package net.sf.statcvs.output.xml.report;

import java.util.List;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.renderer.Chart;
import net.sf.statcvs.renderer.TimeLineChart;
import net.sf.statcvs.reportmodel.TimeLine;
import net.sf.statcvs.reports.AvgFileSizeTimeLineReport;
import net.sf.statcvs.reports.FileCountTimeLineReport;

/**
 * CvsCharts
 * 
 * @author Tammo van Lessen
 */
public class CvsCharts {

	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public static final String FILE_COUNT_CHART_FILE = "file_count.png";
	public static final String FILE_SIZE_CHART_FILE = "file_size.png";
	
	private CvsContent content;

	/**
	 * 
	 */
	public CvsCharts(CvsContent content) 
	{
		this.content = content;
	}

	public Chart getFileCountChart() {
		List files = content.getFiles();
		TimeLine fileCount = new FileCountTimeLineReport(files).getTimeLine();
		return new TimeLineChart(fileCount, content.getModuleName(),
			FILE_COUNT_CHART_FILE, WIDTH, HEIGHT);
	}
	
	public Chart getAvgFileSizeChart() {
		List files = content.getFiles();
		TimeLine avgFileSize = new AvgFileSizeTimeLineReport(files).getTimeLine();
		return new TimeLineChart(avgFileSize, content.getModuleName(),
			FILE_SIZE_CHART_FILE, WIDTH, HEIGHT);
	}

}
