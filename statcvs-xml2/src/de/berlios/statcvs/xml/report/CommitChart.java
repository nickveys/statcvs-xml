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

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.util.IntegerMap;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.chart.AbstractBarChart;
import de.berlios.statcvs.xml.model.Grouper;
import de.berlios.statcvs.xml.model.HourGrouper;
import de.berlios.statcvs.xml.output.ChartReportElement;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * 
 * 
 * @author Tammo van Lessen
 * @author Steffen Pingel
 */
public class CommitChart extends AbstractBarChart {

	public CommitChart(CvsContent content, ReportSettings settings)
	{
		super(settings, "commit%1.png", I18n.tr("Activity%1"),
			null, I18n.tr("Commits")); 

		Grouper grouper = settings.getGrouper(new HourGrouper());
		
		IntegerMap commitsByGroup = new IntegerMap();
		Iterator it = settings.getRevisionIterator(content);
		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision)it.next();
			commitsByGroup.addInt(grouper.getGroup(rev), 1);
		}
		
		Iterator it2 = (settings.getBoolean("showAllGroups", true)) 
			? grouper.getGroups(content, settings)
			: commitsByGroup.iteratorSortedByValueReverse();
		while (it2.hasNext()) {
			Object group = it2.next();
			dataset.addValue(commitsByGroup.get(group), I18n.tr("Activity"), grouper.getName(group));
		}

		setup(false);
	}
	
	public static ReportElement generate(CvsContent content, ReportSettings settings)
	{
		return new ChartReportElement(new CommitChart(content, settings));
	}

}
