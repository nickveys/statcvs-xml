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
import java.util.List;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.Module;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * @author Steffen Pingel
 */
public class ModuleGrouper extends Grouper {

	private List modules;
	private Module otherModule;

	public ModuleGrouper(List modules)
	{
		super("module", I18n.tr("Module"));
		
		this.modules = modules;
	}

	public Object getGroup(CvsRevision rev) 
	{
		for (Iterator it = modules.iterator(); it.hasNext();) {
			Module module = (Module)it.next();
			if (module.matches(rev)) {
				return module;
			}
		}
		
		if (otherModule == null) {
			otherModule = new Module(I18n.tr("Other"));
		}
		
		return otherModule;
	}

	public Object getOtherGroup()
	{
		return otherModule;
	}
	
	/**
	 *  @see de.berlios.statcvs.xml.model.Grouper#getValue(java.lang.Object)
	 */
	public String getName(Object group) 
	{
		return ((Module)group).getName();
	}

	/**
	 *  @see de.berlios.statcvs.xml.model.Grouper#getGroups(net.sf.statcvs.model.CvsContent)
	 */
	public Iterator getGroups(CvsContent content, ReportSettings settings) 
	{
		return modules.iterator();
	}

}
