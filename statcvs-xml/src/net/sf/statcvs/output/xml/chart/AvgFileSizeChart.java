/*
	StatCvs - CVS statistics generation 
	Copyright (C) 2002  Lukasz Pekacki <lukasz@pekacki.de>
	http://statcvs.sf.net/
    
	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
    
	$RCSfile: AvgFileSizeChart.java,v $
	$Date: 2003-07-06 12:30:23 $
*/
package net.sf.statcvs.output.xml.chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.RevisionDateComparator;

/**
 * FileCountChart
 * 
 * @author Tammo van Lessen
 */
public class AvgFileSizeChart extends TimeLineChart {

	public AvgFileSizeChart(CvsContent content) {
		super("file_size.png", I18n.tr("Average File Size"));
	
		List files = content.getFiles();
		TimeLine avgFileSize = new AvgFileSizeTimeLineReport(files).getTimeLine();
		setRangeLabel(avgFileSize.getRangeLabel());
		addTimeLine(avgFileSize);
		getChart().setLegend(null);
		placeTitle();
	}

	/**
	 * Time line for the average file size from a specified file list.
	 * 
	 * @author Richard Cyganiak <rcyg@gmx.de>
	 */
	private class AvgFileSizeTimeLineReport {
		private TimeLine timeLine;

		/**
		 * Creates a new file count time line for a specified list of files.
		 * @param files a list of {@link net.sf.statcvs.model.CvsFile}s
		 */
		public AvgFileSizeTimeLineReport(List files) {
			timeLine = new TimeLine(I18n.tr("Average Filesize"),
					I18n.tr("LOC/File"));
			List revisions = new ArrayList();
			Iterator filesIt = files.iterator();
			while (filesIt.hasNext()) {
				CvsFile file = (CvsFile) filesIt.next();
				revisions.addAll(file.getRevisions());
			}
			Collections.sort(revisions, new RevisionDateComparator());
			Iterator it = revisions.iterator();
			int loc = 0;
			int fileCount = 0;
			while (it.hasNext()) {
				CvsRevision rev = (CvsRevision) it.next();
				loc += rev.getLinesOfCodeChange();
				fileCount += rev.getFileCountChange();
				int ratio = (fileCount == 0) ? 0 : loc / fileCount;
				timeLine.addTimePoint(rev.getDate(), ratio);
			}
		}

		/**
		 * Returns the result time line
		 * @return the result time line
		 */
		public TimeLine getTimeLine() {
			return timeLine;
		}
	}
}