package de.berlios.statcvs.xml.report;

import java.util.Iterator;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.Directory;

import org.jdom.Element;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;

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
	public static ReportElement generate(CvsContent content, ReportSettings settings) 
	{
		ReportElement root = new ReportElement(I18n.tr("Repository Tree"));
		createReport(root, settings.getDirectoryIterator(content));
		return root;
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
}
