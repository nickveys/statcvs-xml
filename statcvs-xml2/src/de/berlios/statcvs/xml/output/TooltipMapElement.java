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
 
package de.berlios.statcvs.xml.output;

import org.jdom.Element;

/**
 * TooltipMapElement
 * 
 * @author Tammo van Lessen
 */
public class TooltipMapElement extends Element {

	private String name;
	
	public TooltipMapElement(String name)
	{
		super("map");
		this.name = name;
		setAttribute("name", name);
	}
	
	public String getMapName()
	{
		return name;
	}
	public void addRectArea(int x1, int y1, 
							 int x2, int y2, 
							 String tip, String link)
	{
		StringBuffer coords = new StringBuffer();
		coords.append((int)x1).append(", ");
		coords.append((int)y1).append(", ");
		coords.append((int)x2).append(", ");
		coords.append((int)y2);

		addArea("rect", coords.toString(), tip, link);
	}
	
	public void addCircleArea(int x, int y, 
							   int r, String tip, String link)
	{
		StringBuffer coords = new StringBuffer();
		coords.append((int)x).append(", ");
		coords.append((int)y).append(", ");
		coords.append((int)r);

		addArea("rect", coords.toString(), tip, link);
	}

	private void addArea(String shape, String coords, String tip, String link)
	{
		Element area = new Element("area");

		area.setAttribute("shape", shape);
		area.setAttribute("coords", coords);
		area.setAttribute("alt", tip);
		area.setAttribute("title", tip);
		area.setAttribute("href", link);
		addContent(area);
	}

}
