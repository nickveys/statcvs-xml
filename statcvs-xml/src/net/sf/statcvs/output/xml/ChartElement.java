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
    
	$RCSfile: ChartElement.java,v $
	$Date: 2003-06-28 11:12:27 $ 
*/
package net.sf.statcvs.output.xml;

import net.sf.statcvs.output.xml.chart.AbstractChart;

import org.jdom.Element;

/**
 * ChartElement
 * 
 * @author Tammo van Lessen
 */
public class ChartElement extends Element {

	public ChartElement(AbstractChart chart) {
		super("img");
		if (chart != null) {
			setAttribute("src", chart.getFilename());
		} else {
			//setAttribute("alt", I18n.tr("Chart could not generated"));
			setName("ignore");
		}
	}
}
