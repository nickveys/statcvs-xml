package de.berlios.statcvs.xml.report;

import java.util.Iterator;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.util.IntegerMap;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.chart.AbstractPieChart;
import de.berlios.statcvs.xml.model.DirectoryGrouper;
import de.berlios.statcvs.xml.model.Grouper;
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
public class SizeChart extends AbstractPieChart {
	
	/**
	 * Global Module Sizes Chart
	 * 
	 * @param filename
	 * @param title
	 */
	public SizeChart(CvsContent content, ReportSettings settings) 
	{
		super(settings, "size%1.png", I18n.tr("Size%1"));
		
		Grouper grouper = settings.getGrouper(new DirectoryGrouper());
		
		IntegerMap sizeByGroup = new IntegerMap();
		Iterator it = settings.getRevisionIterator(content);
		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision)it.next();
			sizeByGroup.addInt(grouper.getGroup(rev), rev.getLinesDelta());
		}

		setValues(sizeByGroup, settings.getInt("sliceMinValue", 5), grouper);
		setup(true);
	}

	/**
	 * 
	 */
	private void setValues(IntegerMap sizeByGroup, int sliceMinValue, Grouper grouper) 
	{
		Object otherGroup = grouper.getOtherGroup();
		int otherSize = (otherGroup != null) ? sizeByGroup.get(otherGroup) : 0;
		Iterator it = sizeByGroup.iteratorSortedByValue();
		while (it.hasNext()) {
			Object group = it.next();
			if (group != otherGroup) {
				if (sizeByGroup.getPercent(group) >= sliceMinValue) {
					dataset.setValue(grouper.getName(group), sizeByGroup.getInteger(group));
				} 
				else {
					otherSize += sizeByGroup.get(group);
				}
			}
		}
		
		if (otherSize > 0) {
			dataset.setValue((otherGroup != null) ? grouper.getName(otherGroup) : I18n.tr("Other"), new Integer(otherSize));
		}
	}

	public static ReportElement generate(CvsContent content, ReportSettings settings)
	{
		return new ChartReportElement(new SizeChart(content, settings));
	}

}
