/*
 *  XNap
 *
 *  A pure java file sharing client.
 *
 *  See AUTHORS for copyright information.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package de.berlios.statcvs.xml.output;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.Directory;

/**
 * @author Steffen Pingel
 */
public class ForAll {
	
	private List forEachObjects;

	public ForAll(List forEachObjects)
	{
		this.forEachObjects = forEachObjects;
	}
	
	public Iterator getObjects()
	{
		return forEachObjects.iterator();
	}
	
	public static ForAll create(CvsContent content, ReportSettings settings, String value)
	{
		List list = new LinkedList();
		if ("author".equals(value)) {
			for (Iterator i = content.getAuthors().iterator(); i.hasNext();) {
				Author author = (Author)i.next();
				list.add(new ForEachAuthor(author));
			}
		}
		else if ("directory".equals(value)) {
			for (Iterator i = content.getDirectories().iterator(); i.hasNext();) {
				Directory dir = (Directory)i.next();
				list.add(new ForEachDirectory(dir));
			}
		}
		else if ("module".equals(value)) {
			ModuleBuilder builder = new ModuleBuilder(settings.getModules(content), content.getRevisions().iterator());
			for (Iterator i = builder.getModules().iterator(); i.hasNext();) {
				Module module = (Module)i.next();
				list.add(new ForEachModule(module));
			}
		}
		else {
			list.add(new ForEachObject(null, ""));
		}
		return new ForAll(list);
	}
}
