package de.berlios.statcvs.xml.report;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.chart.RevisionVisitor;
import de.berlios.statcvs.xml.chart.TimeLineChart;
import de.berlios.statcvs.xml.output.ChartReportElement;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * 
 * 
 * @author Steffen Pingel
 * @author Tammo van Lessen
 */
public class AvgFileSizeChart extends TimeLineChart {

	public AvgFileSizeChart(CvsContent content, ReportSettings settings) 
	{
		super(settings, "file_size.png", I18n.tr("Average File Size"), I18n.tr("LOC/File"));
	
		addTimeSeries(createTimeSeries(I18n.tr("Average Filesize"), settings.getRevisionIterator(content), new Calculator()));
		setup(false);
	}

	public static ReportElement generate(CvsContent content, ReportSettings settings)
	{
		return new ChartReportElement(new AvgFileSizeChart(content, settings));
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