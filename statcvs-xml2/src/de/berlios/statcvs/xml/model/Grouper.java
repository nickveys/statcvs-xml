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

package de.berlios.statcvs.xml.model;

import java.util.Iterator;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;

import org.jdom.Element;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * @author Steffen Pingel
 */
public abstract class Grouper {

	private String name;
	private String id;

	public Grouper(String id, String name)
	{
		if (id == null) {
			throw new IllegalArgumentException("id must not be null");
		}

		this.id = id;
		this.name = name;
	}

	public Element createElement(Object group, ReportSettings settings)
	{
		return null;
	}

	public Object getGroup(Directory directory) 
	{
		throw new IllegalStateException(I18n.tr("Grouping directories not possible"));
	}

	public Object getGroup(CvsFile file) 
	{
		throw new IllegalStateException(I18n.tr("Grouping files not possible"));
	}
	
	public Object getGroup(CvsRevision rev) 
	{
		throw new IllegalStateException(I18n.tr("Grouping revisions not possible"));
	}

	public String getID() 
	{
		return id;
	}
	
	public String getName()
	{
		return name; 
	}

	public Object getOtherGroup()
	{
		return null;
	}
	
	public abstract Iterator getGroups(CvsContent content, ReportSettings settings);
	
	public abstract String getName(Object group);

}
