package de.berlios.statcvs.xml.report;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.chart.AbstractTimeSeriesChart;
import de.berlios.statcvs.xml.chart.RevisionVisitor;
import de.berlios.statcvs.xml.output.ChartReportElement;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * 
 * 
 * @author Steffen Pingel
 * @author Tammo van Lessen
 */
public class FileCountChart extends AbstractTimeSeriesChart {

	public FileCountChart(CvsContent content, ReportSettings settings)
	{
		super(settings, "file_count.png", I18n.tr("File Count"), I18n.tr("LOC/File"));
	
		addTimeSeries(createTimeSeries(I18n.tr("File Count"), settings.getRevisionIterator(content), new Calculator()),
					  content.getFirstDate(), 0);
		setup(false);
	}

	public static ReportElement generate(CvsContent content, ReportSettings settings)
	{
		return new ChartReportElement(new FileCountChart(content, settings));
	}

	public static class Calculator implements RevisionVisitor
	{
		int fileCount = 0;
		
		public int visit(CvsRevision rev)
		{
			fileCount += rev.getFileCountDelta();
			return fileCount;
		}
	}

}
