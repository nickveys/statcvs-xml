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
    
	$RCSfile: LOCSeriesBuilder.java,v $
	$Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.output;

import net.sf.statcvs.model.CvsRevision;

import com.jrefinery.data.BasicTimeSeries;
import com.jrefinery.data.Minute;

/**
 * Builds a <tt>BasicTimesSeries</tt> for the LOC history of a set of
 * revisions. All revisions that should be counted must be passed to
 * the {@link #addRevision} method. When all revisions have been passed
 * to this method, a <tt>BasicTimeSeries</tt> can
 * be obtained from {@link #getTimeSeries} and can be added to a chart.
 * 
 * TODO: Replace by a custom LocTimeSeriesReport
 * 
 * @author Richard Cyganiak
 * @version $Id: LOCSeriesBuilder.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 **/
public class LOCSeriesBuilder {

	private BasicTimeSeries series;
	private boolean hasRevisions = false;
	private Minute minute;
	private int loc = 0;
	private boolean finished = false;
	private boolean countEffective;

	/**
	 * Creates a new <tt>LOCSeriesBuilder</tt>
	 * @param seriesTitle the title for the time series
	 * @param countEffective If <tt>true</tt>, the effective LOC number will
	 *                       be counted. If <tt>false</tt>, the contributed
	 *                       value of new lines will be counted. 
	 */
	public LOCSeriesBuilder(String seriesTitle, boolean countEffective) {
		series = new BasicTimeSeries(seriesTitle, Minute.class);
		this.countEffective = countEffective;
	}
	
	/**
	 * Adds a revision to the time series. The revision must
	 * be at a later date than all previously added revisions.
	 * @param revision the revision to add to the series
	 */
	public void addRevision(CvsRevision revision) {
		if (finished) {
			throw new IllegalStateException("can't add more revisions after getTimeSeries()");
		}
		if (!isLOCChange(revision)) {
			return;
		}
		if (!hasRevisions) {
			minute = new Minute(revision.getDate());
			series.add(minute.previous(), 0);
			hasRevisions = true;
		} else {
			Minute currentMinute = new Minute(revision.getDate());
			if (!currentMinute.equals(minute)) {
				series.add(minute, loc);
				minute = currentMinute;
			}
		}
		if (countEffective) {
			loc += revision.getLinesOfCodeChange();
		} else {
			loc += revision.getLineValue();
		}
	}
	
	/**
	 * gets the finished time series. Should not be called before
	 * all revisions have been added.
	 * @return the resulting <tt>BasicTimeSeries</tt> or <tt>null</tt>
	 * if no LOC data is available for the revision set
	 */
	public BasicTimeSeries getTimeSeries() {
		if (!hasRevisions) {
			return null;
		}
		if (!finished) {
			series.add(minute, loc);
			series.add(minute.next(), loc);
			finished = true;
		}
		return series;
	}

	private boolean isLOCChange(CvsRevision revision) {
		return !revision.getFile().isBinary();
	}
}