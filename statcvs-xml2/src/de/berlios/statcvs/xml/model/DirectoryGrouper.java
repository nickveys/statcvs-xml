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
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * @author Steffen Pingel
 */
public class DirectoryGrouper extends Grouper {

	public DirectoryGrouper()
	{
		super("directory", I18n.tr("Directory"));
	}

	/**
	 *  @see de.berlios.statcvs.xml.model.Grouper#getGroup(net.sf.statcvs.model.CvsFile)
	 */
	public Object getGroup(CvsFile file) 
	{
		return file.getDirectory(); 
	}

	public Object getGroup(CvsRevision rev) 
	{
		return rev.getFile().getDirectory();
	}

	/**
	 *  @see de.berlios.statcvs.xml.model.Grouper#getGroup(net.sf.statcvs.model.Directory)
	 */
	public Object getGroup(Directory directory) 
	{
		return directory;
	}

	/**
	 *  @see de.berlios.statcvs.xml.model.Grouper#getGroups(net.sf.statcvs.model.CvsContent)
	 */
	public Iterator getGroups(CvsContent content, ReportSettings settings) 
	{
		return settings.getDirectoryIterator(content);
	}

	/**
	 *  @see de.berlios.statcvs.xml.model.Grouper#getValue(java.lang.Object)
	 */
	public String getName(Object group) 
	{
		return ((Directory)group).getPath();
	}

}
