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

import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.util.IntegerMap;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.chart.AbstractPieChart;
import de.berlios.statcvs.xml.output.ChartReportElement;
import de.berlios.statcvs.xml.output.Module;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * DirectorySizesChart
 * 
 * @author Tammo van Lessen
 * @version $id: $
 */
public class ModuleSizesChart extends AbstractPieChart {
	
	private Module otherModule;
	private int otherModuleSize;

	/**
	 * Global Module Sizes Chart
	 * 
	 * @param filename
	 * @param title
	 */
	public ModuleSizesChart(CvsContent content, ReportSettings settings) 
	{
		super(settings, "module_sizes%1.png", I18n.tr("Module Sizes%1"));
		
		List modules = settings.getModules(content); 
		otherModule = new Module(I18n.tr("Other"));

		IntegerMap moduleSizes = new IntegerMap();
		Iterator it = settings.getRevisionIterator(content);
		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision)it.next();
			Module module = matches(modules, rev);
			if (module != null) {			
				moduleSizes.addInt(module, rev.getLinesDelta());
			}
			else {
				otherModuleSize += rev.getLinesDelta();
			}
		}

		setValues(moduleSizes, settings.getInt("sliceMinValue", 5));
		setup(true);
	}

	private Module matches(List modules, CvsRevision rev)
	{
		for (Iterator it = modules.iterator(); it.hasNext();) {
			Module module = (Module)it.next();
			if (module.matches(rev)) {
				return module;
			}
		}
		return null;
	}

	/**
	 * 
	 */
	private void setValues(IntegerMap moduleSizes, int sliceMinValue) 
	{
		Iterator it = moduleSizes.iteratorSortedByValue();
		while (it.hasNext()) {
			Module module = (Module)it.next();
			if (moduleSizes.getPercent(module) >= sliceMinValue) {
				dataset.setValue(module.getName(), moduleSizes.getInteger(module));
			} 
			else {
				otherModuleSize += moduleSizes.get(module);
			}
		}
		dataset.setValue(otherModule.getName(), new Integer(otherModuleSize));
	}

	public static ReportElement generate(CvsContent content, ReportSettings settings)
	{
		return new ChartReportElement(new ModuleSizesChart(content, settings));
	}

}
