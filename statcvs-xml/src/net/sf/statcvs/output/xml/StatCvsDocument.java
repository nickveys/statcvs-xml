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
    
	$RCSfile: StatCvsDocument.java,v $
	$Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.output.xml;

import net.sf.statcvs.renderer.*;

import org.jdom.Document;
import org.jdom.Element;

/**
 * Represents a document.
 *  
 * @author Steffen Pingel
 */
public class StatCvsDocument extends Document {

	private String filename;

	public StatCvsDocument(Element element, String filename)
	{
		super(element);

		this.filename = filename;
	}

	public StatCvsDocument(String filename)
	{
		this.filename = filename;
	}
	
	/**
	 * Returns the embedded charts.
	 *
	 * @return null by default
	 */
	public Chart[] getCharts()
	{
		return null;
	}

	public String getFilename()
	{
		return filename;
	}
	
}


