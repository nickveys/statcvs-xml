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
    
	$RCSfile: AbstractChart.java,v $
	$Date: 2004-02-17 16:40:00 $ 
*/
package de.berlios.statcvs.xml.chart;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.statcvs.util.FileUtils;

import org.jdom.Element;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.Spacer;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;

import de.berlios.statcvs.xml.Settings;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * AbstractChart
 * 
 * @author Tammo van Lessen
 */
public abstract class AbstractChart {
	
	private final static Logger logger = Logger.getLogger("net.sf.statcvs.output.xml.chart.Chart");
	
	private static int chartNumber = 0;
	
	private String filename;
	private String title;
	private JFreeChart chart;
	private int width;
	private int height;

	public AbstractChart(ReportSettings settings, String defaultFilename, String defaultTitle) 
	{
		this.filename = settings.getString("filename", (defaultFilename == null) ? "chart" + ++chartNumber + ".png" : defaultFilename);
		this.title = settings.getString("title", defaultTitle);
		this.width = settings.getInt("width", 640);
		this.height = settings.getInt("height", 480);
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
	 * get filename
	 * @return filename file name
	 */
	public String getFilename() {
		return filename;
	}

	/** 
	 * set filename
	 * @param filename file name
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	/**
	 * create chart with titles and credit information
	 */
	public void placeTitle() {
		Font font = new Font("SansSerif", Font.PLAIN, 12);
		Font creditInformationFont = new Font("SansSerif", Font.PLAIN, 9);
		
		TextTitle title = new TextTitle(this.title, font);
		title.setSpacer(new Spacer(Spacer.RELATIVE, 0.05, 0.05, 0.05, 0.0));
		chart.addSubtitle(title);

		if (Settings.getShowCreditInformation()) {
			TextTitle copyright = new TextTitle("generated by statcvs-xml", creditInformationFont);
			copyright.setPosition(RectangleEdge.BOTTOM);
			copyright.setHorizontalAlignment(HorizontalAlignment.RIGHT);
			chart.addSubtitle(copyright);
		}

		chart.setBackgroundPaint(Color.white);
	}
	
	public void save(File outputPath) throws IOException 
	{
		save(new File(outputPath, filename), width, height);	
	}
	
	public void save(File file, int width, int height) throws IOException 
	{
		ChartUtilities.saveChartAsPNG(file, chart, width, height);
		logger.fine("saved chart '" + title + "' as '" + filename + "'");
	}

	/**
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param string
	 */
	public void setTitle(String string) {
		title = string;
	}
	
	public void setChartTitle(String string) {
		if (chart != null) {
			chart.setTitle(string);
		}
	}

}
