package de.berlios.statcvs.xml.report;

import java.awt.BasicStroke;
import java.util.Iterator;

import net.sf.statcvs.model.CvsContent;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.ChartReportElement;
import de.berlios.statcvs.xml.output.Module;
import de.berlios.statcvs.xml.output.ModuleBuilder;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * LocChart
 * 
 * @author Tammo van Lessen
 * @author Steffen Pingel
 */
public class LocByModuleChart extends LocChart {
    
    private CvsContent content;
	
	public LocByModuleChart(CvsContent content, ReportSettings settings)
	{
		super(content, settings, I18n.tr("Lines Of Code (per Module)"));

		Object highlightModule = settings.get("_foreachObject");
				
		// add a time line for each module
		int i = 0;
		ModuleBuilder builder = new ModuleBuilder(settings.getModules(content), content.getRevisions().iterator());
		Iterator it = builder.getModules().iterator();
		while (it.hasNext()) {
			Module module = (Module)it.next();
			addTimeSeries(module.getName(), module.getRevisions().iterator());
			if (module == highlightModule) {
				// make line thicker
				getChart().getXYPlot().getRenderer().setSeriesStroke(i, new BasicStroke(2,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
			}
			++i;
		}

		addSymbolicNames(settings.getSymbolicNameIterator(content));
		setup(true);
	}

	public static ReportElement generate(CvsContent content, ReportSettings settings)
	{
		return new ChartReportElement(new LocByModuleChart(content, settings));
	}

}
