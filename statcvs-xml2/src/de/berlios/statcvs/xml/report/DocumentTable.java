package de.berlios.statcvs.xml.report;

import java.util.Iterator;
import java.util.Map;

import net.sf.statcvs.model.CvsContent;

import org.jdom.Element;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.DocumentSuite;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * 
 * 
 * @author Steffen Pingel
 */
public class DocumentTable {

	public static ReportElement generate(CvsContent content, ReportSettings settings)
	{
		ReportElement root = new ReportElement(I18n.tr("Reports"));
		Element list = new Element("reports");
		root.addContent(list);
		Map documentTitleByFilename = DocumentSuite.getDocuments();
		for (Iterator it = documentTitleByFilename.keySet().iterator(); it.hasNext();) {
			String filename = (String)it.next();
			Element element = new Element("link");
			element.setAttribute("ref", filename);
			element.setText((String)documentTitleByFilename.get(filename));
			list.addContent(element);
		}
		return root;
	}
	
}

