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
    
	$RCSfile: Chart.java,v $
	$Date: 2003-06-26 23:04:55 $ 
*/
package net.sf.statcvs.output.xml.chart;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.statcvs.output.ConfigurationOptions;
import net.sf.statcvs.util.FileUtils;

import com.jrefinery.chart.ChartUtilities;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.Spacer;
import com.jrefinery.chart.TextTitle;

/**
 * Chart
 * 
 * @author Tammo van Lessen
 */
public abstract class Chart {
	private final static Logger logger = Logger.getLogger("net.sf.statcvs.output.xml.chart.Chart");
	
	private String filename;
	private String title;
	private JFreeChart chart;

	public Chart(String filename, String title) {
		this.filename = filename;
		this.title = title;
	}
	
	/** 
	 * get chart
	 * @return chart the chart
	 */
	public JFreeChart getChart() {
		return chart;
	}

	/** 
	 * set chart
	 * @param chart the chart
	 */
	public void setChart(JFreeChart chart) {
		this.chart = chart;
	}
	
	/** 
	 * get fileName
	 * @return fileName file name
	 */
	public String getFileName() {
		return filename;
	}

	/**
	 * create chart with titles and credit information
	 */
	public void placeTitle() {
		Font font = new Font("SansSerif", Font.PLAIN, 12);
		Font creditInformationFont = new Font("SansSerif", Font.PLAIN, 9);
		
		TextTitle title = new TextTitle(this.title, font);
		title.setSpacer(new Spacer(Spacer.RELATIVE, 0.05, 0.05, 0.05, 0.0));
		chart.addTitle(title);

		if (ConfigurationOptions.getShowCreditInformation()) {
			TextTitle copyright = new TextTitle("statcvs-xml", creditInformationFont);
			copyright.setPosition(TextTitle.BOTTOM);
			copyright.setHorizontalAlignment(TextTitle.RIGHT);
			chart.addTitle(copyright);
		}

		chart.setBackgroundPaint(Color.white);
	}
	
	public void save() throws IOException {
		// TODO: Make it configurable!
		save(640, 480);	
	}
	
	public void save(int width, int height) throws IOException {
		save(width, height, this.filename);	
	}
	
	public void save(int width, int height, String filename) throws IOException {
		ChartUtilities.saveChartAsPNG(
			new File(FileUtils.getFilenameWithDirectory(filename)),
			chart,
			width,
			height);
		logger.fine("saved chart '" + title + "' as '" + filename + "'");
	}

}
