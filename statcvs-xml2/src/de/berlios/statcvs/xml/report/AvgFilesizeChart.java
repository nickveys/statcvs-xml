package de.berlios.statcvs.xml.report;

import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;

import org.jfree.data.time.TimeSeries;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.chart.AbstractTimeSeriesChart;
import de.berlios.statcvs.xml.chart.RevisionVisitor;
import de.berlios.statcvs.xml.chart.RevisionVisitorFactory;
import de.berlios.statcvs.xml.model.Grouper;
import de.berlios.statcvs.xml.output.ChartReportElement;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * 
 * 
 * @author Steffen Pingel
 * @author Tammo van Lessen
 */
public class AvgFilesizeChart extends AbstractTimeSeriesChart {

	public AvgFilesizeChart(CvsContent content, ReportSettings settings) 
	{
		super(settings, "avgsize%1.png", I18n.tr("Average Filesize%1"), I18n.tr("LOC / File"));
	
		Grouper grouper = settings.getGrouper();
		if (grouper != null) {
			List serieses = createTimeSerieses(grouper, settings.getRevisionIterator(content), new RevisionVisitorFactory(Calculator.class.getName()));
			for (Iterator it = serieses.iterator(); it.hasNext();) {
				addTimeSeries((TimeSeries)it.next());
			}
			setup(true);
		}
		else {
			addTimeSeries(createTimeSeries(I18n.tr("Average Filesize"), settings.getRevisionIterator(content), new Calculator()));
			setup(false);
		}
	}

	public static ReportElement generate(CvsContent content, ReportSettings settings)
	{
		return new ChartReportElement(new AvgFilesizeChart(content, settings));
	}

	public static class Calculator implements RevisionVisitor
	{
		int loc = 0;
		int fileCount = 0;
		
		public int visit(CvsRevision rev)
		{
			loc += rev.getLinesDelta();
			fileCount += rev.getFileCountDelta();
			return (fileCount == 0) ? 0 : loc / fileCount;
		}
	}
	
}