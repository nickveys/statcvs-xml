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
    
	$RCSfile: TimePoint.java,v $
	$Date: 2004-02-15 18:56:13 $
*/
package de.berlios.statcvs.xml.chart;

import java.util.Date;

/**
 * Encapsulates a data point in a {@link TimeLine} 
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: TimePoint.java,v 1.1 2004-02-15 18:56:13 squig Exp $
 */
public class TimePoint {
	private Date date;
	private int value;
	private int delta;

	/**
	 * Creates a new TimePoint
	 * @param date the time point's date
	 * @param value the time point's value
	 * @param delta the time point's change relative to the previous value
	 */
	public TimePoint(Date date, int value, int delta) {
		this.date = date;
		this.value = value;
		this.delta = delta;
	}
	
	/**
	 * @return the time point's date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @return the time point's change relative to the previous value
	 */
	public int getDelta() {
		return delta;
	}

	/**
	 * @return the time point's value
	 */
	public int getValue() {
		return value;
	}
}
