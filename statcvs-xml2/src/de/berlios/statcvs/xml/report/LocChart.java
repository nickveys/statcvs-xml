package de.berlios.statcvs.xml.report;

import java.awt.BasicStroke;
import java.util.Date;
import java.util.Iterator;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.chart.RevisionVisitor;
import de.berlios.statcvs.xml.chart.AbstractTimeSeriesChart;
import de.berlios.statcvs.xml.output.ChartReportElement;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * LocChart
 * 
 * @author Tammo van Lessen
 */
public class LocChart extends AbstractTimeSeriesChart {
    
    private CvsContent content;
	
	public LocChart(CvsContent content, ReportSettings settings, String title)
	{
		super(settings, "loc%1.png", title, I18n.tr("Lines"));

		this.content = content;
	}
	
	public LocChart(CvsContent content, ReportSettings settings) 
	{
		this(content, settings, I18n.tr("Lines Of Code%1"));
        	
		addTimeSeries("LOC", settings.getRevisionIterator(content));
		addSymbolicNames(settings.getSymbolicNameIterator(content));
		setup(false);
	}

	public static ReportElement generate(CvsContent content, ReportSettings settings)
	{
		return new ChartReportElement(new LocChart(content, settings));
	}

	protected void addTimeSeries(String title, Iterator it)
	{
		TimeSeries series = createTimeSeries(title, it, new LOCCalculator());
		addTimeSeries(series, content.getFirstDate(), 0);
	}
	
	public static class LOCCalculator implements RevisionVisitor
	{
		int loc = 0;
		public int visit(CvsRevision rev)
		{
			loc += rev.getLinesDelta();
			return loc;
		}
	}

}
