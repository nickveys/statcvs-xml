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

import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.model.CvsRevision;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.model.*;

/**
 * DirectorySizesChart
 * 
 * @author Tammo van Lessen
 * @version $id: $
 */
public class ModuleBuilder {
	
	private List modules; 
	private Module otherModule;

	/**
	 * Global Module Sizes Chart
	 * 
	 * @param filename
	 * @param title
	 */
	public ModuleBuilder(List modules, Iterator it) 
	{
		this.modules = modules;  
		this.otherModule = new Module(I18n.tr("Other"));
		modules.add(otherModule);

		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision)it.next();
			Module module = matches(rev);
			if (module != null) {			
				module.addRevision(rev);
			}
			else {
				otherModule.addRevision(rev);
			}
		}
	}
	
	public List getModules()
	{
		return modules;
	}

	private Module matches(CvsRevision rev)
	{
		for (Iterator it = modules.iterator(); it.hasNext();) {
			Module module = (Module)it.next();
			if (module.matches(rev)) {
				return module;
			}
		}
		return null;
	}

}
