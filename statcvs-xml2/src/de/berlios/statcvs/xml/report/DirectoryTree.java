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

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.Report;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;
import de.berlios.statcvs.xml.output.TableElement;
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
		Iterator it = settings.getDirectoryIterator(content);

		TableElement table = new TableElement(settings, new String[] {
			I18n.tr("Directory"), I18n.tr("Files"), I18n.tr("Lines of Code") });
			
		int rootDepth = 0;
		boolean firstProcessed = false;
		while (it.hasNext()) {
			Directory dir = (Directory)it.next();
			if (!firstProcessed) {
				rootDepth = dir.getDepth();
				firstProcessed = true;
			}
			table.addRow()
				.addDirectoryTree(dir, dir.getDepth() - rootDepth)
				.addInteger("files", dir.getCurrentFileCount())
				.addInteger("loc", dir.getCurrentLOC());
		}
		root.addContent(table);
		return new Report(root, table);
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
