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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.SymbolicName;

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
		private Map evoFiles = new HashMap();
		private SortedSet versions = new TreeSet();
		
		/**
		 * 
		 */
		public EvolutionMatrixPlot(CvsContent content) {
			this.content = content;
			
/*			// map integermaps with file => rev.getLines() by version
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
			
			filesByVersion.put(new Version("HEAD", new Date()), map);*/
			

			
			
			Iterator it = content.getSymbolicNames().iterator();
			while (it.hasNext()) {
				SymbolicName sn = (SymbolicName)it.next();
				Version version = new Version(sn.getName(), sn.getDate());

				int maxLoc = 0;
				Iterator revIt = sn.getRevisions().iterator();
				while (revIt.hasNext()) {
					CvsRevision rev = (CvsRevision)revIt.next();
					maxLoc = Math.max(maxLoc, rev.getLines());
					TaggedFile evo = (TaggedFile)evoFiles.get(rev.getFile());
					if (evo == null) {
						evo = new TaggedFile(rev.getFile());
						evoFiles.put(rev.getFile(), evo);	 
					}
					
					evo.addRevision(version, rev);

				}
				
				version.setMaxLoc(maxLoc);
				versions.add(version);

			}
			
			// cheat head into map
			Version version = new Version("HEAD", new Date()); 
			it = content.getFiles().iterator();
			int maxLoc = 0;
			while (it.hasNext()) {
				CvsFile file = (CvsFile)it.next();
				if (!file.isDead()) {
					TaggedFile evo = (TaggedFile)evoFiles.get(file);
					if (evo == null) {
						evo = new TaggedFile(file);
						evoFiles.put(file, evo);	 
					}
					maxLoc = Math.max(maxLoc, file.getLatestRevision().getLines());
					evo.addRevision(version, file.getLatestRevision());
				}
			}
			version.setMaxLoc(maxLoc);
			versions.add(version);			
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
			

			/*// store file here if file occurs the first time
			List newAdded = new ArrayList();

			// get version iterator
			Iterator symIt = filesByVersion.keySet().iterator();
			
			double vspace = plotArea.getWidth() / (content.getSymbolicNames().size() + 1);
			double x = plotArea.getX();
  			double y = plotArea.getY() + SPACER;

			// set drawing settings
			Stroke oldStroke = g2.getStroke();
			Stroke itemStroke = new BasicStroke(LINE_WIDTH);
			Stroke borderStroke = new BasicStroke(1); 
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

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

					//g2.setColor(Color.black);
					//g2.setStroke(borderStroke);
					//g2.drawRect((int)x, (int)y, (int)plotArea.getMaxX(), (int)y + g2.getFontMetrics().getHeight());
					//RefineryUtilities.drawAlignedString("xxx", g2, (int)x, (int)y, TextAnchor.BASELINE_LEFT);
					//g2.drawString(dir.getPath(), (int)x, (int)y);
					//y = y + g2.getFontMetrics().getHeight();
					
					// and files...
					while (fit.hasNext()) {
						CvsFile file = (CvsFile)fit.next();
						
						// colorize
						// new: green
						// old: red
						// not exist: gray				
						if (map.contains(file)) {
							if (!newAdded.contains(file)) {
								g2.setColor(Color.green);
								newAdded.add(file);				
							} else {
								g2.setColor(Color.red);	
							}
							
						} 
						else {
							g2.setColor(Color.lightGray);	
						}
						
						g2.setStroke(itemStroke);
							
						// draw line if file belongs to this version
						//if (map.contains(file)) {
							int length = (int)((map.getPercentOfMaximum(file) / 100) * (vspace - 5));
							g2.drawLine((int)x, (int)y, (int)x + length, (int)y);	
						//}
						
						// next line
						y = y + LINE_WIDTH + 1;
					}
				}

				// next block
				x = x + vspace;
				y = plotArea.getY() + SPACER;
			}
			
			// clean up
			g2.setStroke(oldStroke);*/
			
			
			// store file here if file occurs the first time
			List newAdded = new ArrayList();

			// get version iterator
			Iterator verIt = versions.iterator();
			
			double vspace = plotArea.getWidth() / (content.getSymbolicNames().size() + 1);
			double x = plotArea.getX();
			double y = plotArea.getY() + SPACER;

			// set drawing settings
			Stroke oldStroke = g2.getStroke();
			Stroke itemStroke = new BasicStroke(LINE_WIDTH);
			Stroke borderStroke = new BasicStroke(1); 
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

			Version lastVersion = null;
			while (verIt.hasNext()) {
				Version ver = (Version)verIt.next();

				// walk through all directories...
				Iterator dirIt = content.getDirectories().iterator();
				while (dirIt.hasNext()) {
					Directory dir = (Directory)dirIt.next();
					Iterator fit = dir.getFiles().iterator();

					// and files...
					while (fit.hasNext()) {
						CvsFile file = (CvsFile)fit.next();
						TaggedFile eFile = (TaggedFile)evoFiles.get(file);
						
						
						g2.setStroke(itemStroke);
							
						// tagged file
						if (eFile != null) {
							
							// COLORIZE
							
							if (lastVersion != null) {
								
								//   new files: green
								//   untouched files: grey
								//   modified files: red
								//   deleted files: black
								
								if (!eFile.isInVersion(lastVersion)) {
									g2.setColor(Color.green);
								} else if (eFile.hasSameRevision(lastVersion, ver)) {
									g2.setColor(Color.gray);
								} else if (!eFile.isInVersion(ver) && eFile.isInVersion(lastVersion)) {
									g2.setColor(Color.black);
								} else {
									g2.setColor(Color.red);
								}
							} else {
								// all files of the first version: red
								g2.setColor(Color.red);
							}
							
							// drawing
							if (eFile.isInVersion(ver)) {
								// draw existing file
								int length = (int)((eFile.getScore(ver)) * (vspace - 5));
								g2.drawLine((int)x, (int)y, (int)x + length, (int)y);
							} else if (eFile.isInVersion(lastVersion)) {
								// draw deleted file with score of the last known version
								int length = (int)((eFile.getScore(lastVersion)) * (vspace - 5));
								g2.drawLine((int)x, (int)y, (int)x + length, (int)y);
							}
							
							// mark changes
							g2.setColor(Color.yellow);
							//g2.setStroke(new BasicStroke(2));
							if (lastVersion != null && !eFile.hasSameRevision(lastVersion, ver)) {
								int length = (int)((eFile.getChangedScore(lastVersion, ver)) * (vspace - 5));
								g2.drawLine((int)x, (int)y, (int)x + length, (int)y);								
							}
						} else {
							// file was never tagged
							
							// draw grey dot
							// g2.setColor(Color.lightGray);
							// g2.drawLine((int)x, (int)y, (int)x, (int)y);
						}
						
						// next line
						y = y + LINE_WIDTH + 1;
					}
				}
				
				// next block
				x = x + vspace;
				y = plotArea.getY() + SPACER;

				// remember last version
				lastVersion = ver;	
			}
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
        private int maxLoc;

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

		public int getMaxLoc()
		{
			return maxLoc;
		}
		
		public void setMaxLoc(int maxLoc)
		{
			this.maxLoc = maxLoc;
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
	
	
	private class TaggedFile 
	{
		private Map revisionByVersion = new TreeMap();
		private CvsFile file;
		
		public TaggedFile(CvsFile file)
		{
			this.file = file;
		}
		
		void addRevision(Version ver, CvsRevision rev)
		{
			revisionByVersion.put(ver, rev);
		}
		
		public CvsRevision getRevision(Version ver) 
		{
			if (ver == null) {
				return null;
			}
			return (CvsRevision)revisionByVersion.get(ver);
		}
		
		public double getScore(Version ver)
		{
			return (double)getRevision(ver).getLines() / ver.getMaxLoc();
		}
		
		public double getChangedScore(Version oldV, Version thisV)
		{
			CvsRevision target = getRevision(oldV);
			CvsRevision curr = getRevision(thisV);
			int change = curr.getReplacedLines();
			while (target != curr) {
				curr = curr.getPreviousRevision();
				if (curr != null) {
					change += curr.getReplacedLines();	
				}
			}
			return (double)change / getRevision(thisV).getLines();
		}
		
		public boolean isInVersion(Version ver)
		{
			return getRevision(ver) != null;	
		}
		
		public boolean hasSameRevision(Version v1, Version v2)
		{
			return getRevision(v1) == getRevision(v2);
		}
	}

}
