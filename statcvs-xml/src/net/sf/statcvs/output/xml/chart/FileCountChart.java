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
    
	$RCSfile: FileCountChart.java,v $
	$Date: 2003-07-06 21:26:39 $
*/
package net.sf.statcvs.output.xml.chart;

import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.CvsRevision;

/**
 * FileCountChart
 * 
 * @author Tammo van Lessen
 */
public class FileCountChart extends TimeLineChart {

	public FileCountChart(CvsContent content) {
		super("file_count.png", I18n.tr("File Count"));
	
		List files = content.getFiles();
		TimeLine fileCount = new FileCountTimeLineReport(files).getTimeLine();
		setRangeLabel(I18n.tr("File"));
		addTimeLine(fileCount);
		getChart().setLegend(null);
		placeTitle();
	}

	/**
	 * Time line for the number of non-dead files from a specified file list.
	 * 
	 * @author Richard Cyganiak <rcyg@gmx.de>
	 */
	private class FileCountTimeLineReport {
		private TimeLine timeLine;

		/**
		 * Creates a new file count time line for a specified list of files.
		 * @param files a list of {@link net.sf.statcvs.model.CvsFile}s
		 */
		public FileCountTimeLineReport(List files) {
			timeLine = new TimeLine(I18n.tr("File Count"));
			timeLine.setInitialValue(0);
			Iterator filesIt = files.iterator();
			while (filesIt.hasNext()) {
				CvsFile file = (CvsFile) filesIt.next();
				addRevisions(file);
			}
		}

		/**
		 * Returns the result time line
		 * @return the result time line
		 */
		public TimeLine getTimeLine() {
			return timeLine;
		}

		private void addRevisions(CvsFile file) {
			Iterator it = file.getRevisionIterator();
			while (it.hasNext()) {
				CvsRevision rev = (CvsRevision) it.next();
				int change = rev.getFileCountChange();
				if (change != 0) {
					timeLine.addChange(rev.getDate(), change);
				}
			}
		}
	}
}
