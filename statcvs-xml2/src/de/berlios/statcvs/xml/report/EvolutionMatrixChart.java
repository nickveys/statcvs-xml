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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.SymbolicName;
import net.sf.statcvs.util.IntegerMap;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.chart.AbstractChart;
import de.berlios.statcvs.xml.output.ChartReportElement;
import de.berlios.statcvs.xml.output.Report;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * EvolutionMatrixChart
 * 
 * @author Tammo van Lessen
 */
public class EvolutionMatrixChart extends AbstractChart {

	private final int SPACER = 25; 
	private final int LINE_WIDTH = 4;
    
	private CvsContent content;
	private ReportSettings settings;

    /**
     * 
     */
    public EvolutionMatrixChart(CvsContent content, ReportSettings settings) 
    {
		super(settings, "evolution.png", I18n.tr("Software Evolution Matrix"));

		this.content = content;
		this.settings = settings;

		setChart(createChart());
		if (getChart() != null) {
			setup(true);
		}
    }
    
	/**
	 * 
	 */
	public static Report generate(CvsContent content, ReportSettings settings)
	{
		EvolutionMatrixChart chart = new EvolutionMatrixChart(content, settings);
		return (chart.getChart() != null) ? new Report(new ChartReportElement(chart)) : null;
	}
	
    
    /**
     *
     */
    private JFreeChart createChart() 
    {
        return new JFreeChart(new EvolutionMatrixPlot(content));
    }

	/**
	 * 
	 * EvolutionMatrixPlot
	 * 
	 * @author Tammo van Lessen
	 */
	private class EvolutionMatrixPlot extends Plot
	{
		private CvsContent content;
		private Map filesByVersion = new TreeMap();
		
		/**
		 * 
		 */
		public EvolutionMatrixPlot(CvsContent content) {
			this.content = content;
			
			// map integermaps with file => rev.getLines() by version
			Iterator it = content.getSymbolicNames().iterator();
			while (it.hasNext()) {
				SymbolicName sn = (SymbolicName)it.next();
				Version version = new Version(sn.getName(), sn.getDate());
				IntegerMap map = (IntegerMap)filesByVersion.get(version);
				if (map == null) {
					map = new IntegerMap();
					filesByVersion.put(version, map);
				}
				
				Iterator revIt = sn.getRevisions().iterator();
				while (revIt.hasNext()) {
					CvsRevision rev = (CvsRevision)revIt.next();
					map.addInt(rev.getFile(), rev.getLines());
				}
			}
			
			// cheat head into map
			it = content.getFiles().iterator();
			IntegerMap map = new IntegerMap();
			while (it.hasNext()) {
				CvsFile file = (CvsFile)it.next();
				if (!file.isDead()) {
					map.put(file, file.getLatestRevision().getLines());
				}
			}
			
			filesByVersion.put(new Version("HEAD", new Date()), map);
		}

        /**
         * @see org.jfree.chart.plot.Plot#getPlotType()
         */
        public String getPlotType() {
            return "EvolutionMatrixPlot";
        }

        /**
         * @see org.jfree.chart.plot.Plot#draw(java.awt.Graphics2D, java.awt.geom.Rectangle2D, org.jfree.chart.plot.PlotState, org.jfree.chart.plot.PlotRenderingInfo)
         */
        public void draw(Graphics2D g2, Rectangle2D plotArea, PlotState state, PlotRenderingInfo info) 
        {
			// record the plot area...
			if (info != null) {
				info.setPlotArea(plotArea);
			}

			// adjust the drawing area for the plot insets (if any)...
			Insets insets = getInsets();
			if (insets != null) {
				plotArea.setRect(plotArea.getX() + insets.left,
								 plotArea.getY() + insets.top,
								 plotArea.getWidth() - insets.left - insets.right,
								 plotArea.getHeight() - insets.top - insets.bottom);
			}
			

			// store file here if file occurs the first time
			List newAdded = new ArrayList();

			// get version iterator
			Iterator symIt = filesByVersion.keySet().iterator();
			
			double vspace = plotArea.getWidth() / (content.getSymbolicNames().size() + 1);
			double x = plotArea.getX();
  			double y = plotArea.getY() + SPACER;

			// set drawing settings
			Stroke oldStroke = g2.getStroke();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g2.setStroke(new BasicStroke(LINE_WIDTH));

			while (symIt.hasNext()) {
				Version ver = (Version)symIt.next();
				// get integermaps with files for version
				IntegerMap map = (IntegerMap)filesByVersion.get(ver);
				
				g2.setColor(Color.black);
				g2.drawString(ver.getName(), (int)x, 
						(int)plotArea.getY() + SPACER - 10);

				// walk through all directories...
				Iterator dirIt = content.getDirectories().iterator();
				while (dirIt.hasNext()) {
					Directory dir = (Directory)dirIt.next();
					Iterator fit = dir.getFiles().iterator();
					
					// and files...
					while (fit.hasNext()) {
						CvsFile file = (CvsFile)fit.next();
						
						// green if file occurs the first time, else red
						if (map.contains(file) && !newAdded.contains(file)) {
							g2.setColor(Color.green);
							newAdded.add(file);				
						} else {
							g2.setColor(Color.red);
						}
						
						// draw line if file belongs to this version
						if (map.contains(file)) {
							int length = (int)((map.getPercentOfMaximum(file) / 100) * (vspace - 5));
							g2.drawLine((int)x, (int)y, (int)x + length, (int)y);	
						}
						
						// next line
						y = y + LINE_WIDTH + 1;
					}
				}

				// next block
				x = x + vspace;
				y = plotArea.getY() + SPACER;
			}
			
			// clean up
			g2.setStroke(oldStroke);            
        }
        
        public int getHeight() 
        {
        	return getInsets().bottom + getInsets().bottom + (2*SPACER) + (content.getFiles().size() * (LINE_WIDTH + 1));
        }
	}
	
    /**
     * @see de.berlios.statcvs.xml.chart.AbstractChart#getPreferredHeigth()
     */
    public int getPreferredHeigth() 
    {
		if (getChart() == null) {
			return super.getPreferredHeigth();	
		} else {
			return ((EvolutionMatrixPlot)getChart().getPlot()).getHeight();
		}
    }

	private class Version implements Comparable
	{
		private String name;
        private Date date;

        public Version(String name, Date date)
		{
			this.date = date;
			this.name = name; 
		}
		
		public String getName()
		{
			return name;
		}
		
		public Date getDate()
		{
			return date;
		}

        /**
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Object o) 
        {
			Version other = (Version)o;
			int dateComp = getDate().compareTo(other.getDate()); 
			return (dateComp != 0) ? dateComp
									: getName().compareTo(other.getName());
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj) 
        {
            return (name + date).equals(obj);
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() 
        {
            return (name + date).hashCode();
        }

	}

}
