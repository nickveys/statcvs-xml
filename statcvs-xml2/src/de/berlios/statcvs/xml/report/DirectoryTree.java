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

package de.berlios.statcvs.xml.report;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.Directory;

import org.jdom.Element;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.Report;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;
import de.berlios.statcvs.xml.util.FileHelper;

/**
 * ModulesTreeReport
 * 
 * @author Tammo van Lessen
 * @author Steffen Pingel
 */
public class DirectoryTree {

	/**
	 * 
	 */
	public static Report generate(CvsContent content, ReportSettings settings) 
	{
		ReportElement root = new DirectoryTreeElement(settings, I18n.tr("Repository Tree"));
		createReport(root, settings.getDirectoryIterator(content));
		return new Report(root);
	}
	
	/**
	 * 
	 */
	private static void createReport(ReportElement root, Iterator it)
	{
		Element modules = new Element("modulesTree");
		int rootDepth = 0;
		boolean firstProcessed = false;
		while (it.hasNext()) {
			Directory dir = (Directory) it.next();
			if (!firstProcessed) {
				rootDepth = dir.getDepth();
				firstProcessed = true;
			}
			Element module = new Element("module");
			if (dir.isRoot()) {
				module.setAttribute("name", I18n.tr("[root]"));
			} else {
				module.setAttribute("name", dir.getName());
			}
			module.setAttribute("depth", ""+(dir.getDepth()-rootDepth));
			module.setAttribute("files", ""+dir.getCurrentFileCount());
			module.setAttribute("loc", ""+dir.getCurrentLOC());
			if (dir.isEmpty()) {
				module.setAttribute("removed", "true");
			} else {
				module.setAttribute("ref", dir.getPath());	
			}

			modules.addContent(module);
		}
		root.addContent(modules);
	}
	
	public static class DirectoryTreeElement extends ReportElement
	{
	
		public DirectoryTreeElement(ReportSettings settings, String name)
		{
			super(settings, name);
		}
		
		/**
		 *  @see de.berlios.statcvs.xml.output.ReportElement#saveResources(java.io.File)
		 */
		public void saveResources(File outputPath) throws IOException 
		{
			FileHelper.copyResource("resources/folder.png", outputPath);
			FileHelper.copyResource("resources/folder-deleted.png", outputPath);
		}

	}
	
}
