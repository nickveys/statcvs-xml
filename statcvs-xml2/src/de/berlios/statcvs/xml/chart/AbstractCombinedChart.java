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

package de.berlios.statcvs.xml.chart;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * AbstractScatterChart
 * 
 * @author Tammo van Lessen
 * @version $Id: AbstractCombinedChart.java,v 1.2 2004-03-05 21:56:01 squig Exp $
 */
public class AbstractCombinedChart extends AbstractChart {

    private CombinedDomainXYPlot combinedPlot;
	
    /**
     * @param settings
     * @param defaultFilename
     * @param defaultSubtitle
     */
    public AbstractCombinedChart(ReportSettings settings, String defaultFilename, String defaultSubtitle) 
    {
        super(settings, defaultFilename, defaultSubtitle);
        
		ValueAxis domainAxis = new DateAxis(I18n.tr("Date"));
		domainAxis.setVerticalTickLabels(true);

		combinedPlot = new CombinedDomainXYPlot(domainAxis);
		combinedPlot.setGap(10);
		combinedPlot.setOrientation(PlotOrientation.VERTICAL);
		
		JFreeChart chart = new JFreeChart(settings.getProjectName(), JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, false);
		setChart(chart);
    }
    
    public void addPlot(XYPlot plot)
    {
    	combinedPlot.add(plot);
    }
    
    public int getPlotCount()
    {
    	return combinedPlot.getSubplots().size();
    }

}
