package de.berlios.statcvs.xml.report;

import java.util.Iterator;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.output.WebRepositoryIntegration;
import net.sf.statcvs.util.IntegerMap;

import org.jdom.Element;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.Settings;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * 
 * 
 * @author Steffen Pingel
 */
public class SizePerFileTable {

	/**
	 * 
	 */
	public static ReportElement generate(CvsContent content, ReportSettings settings) 
	{
		ReportElement root = new ReportElement(I18n.tr("Size Per File"));
		createReport(root, content, settings.getFileIterator(content), settings.getLimit());
		return root;
	}

	private static void createReport(ReportElement root, CvsContent content, Iterator it, int maxItems)
	{
		IntegerMap filesMap = new IntegerMap();
				
		while (it.hasNext()) {
			CvsFile file = (CvsFile)it.next();
			if (file.isDead()) {
				continue;
			}

			filesMap.addInt(file, file.getCurrentLinesOfCode());
		}

		Element filesEl = new Element("files");	
		Iterator filesIt = filesMap.iteratorSortedByValueReverse();
		WebRepositoryIntegration webRepository = Settings.getWebRepository();
		int count = 0;
		while (filesIt.hasNext() && count < maxItems) {
			CvsFile file = (CvsFile)filesIt.next();
			
			Element fileEl = new Element("file");
			fileEl.setAttribute("name", file.getFilenameWithPath());
			fileEl.setAttribute("loc", ""+file.getCurrentLinesOfCode());
			fileEl.setAttribute("revisions", ""+file.getRevisions().size());
			if (webRepository != null) {
				fileEl.setAttribute("url", webRepository.getFileViewUrl(file));				
			}
			
			filesEl.addContent(fileEl);
			count++;
		}

		root.addContent(new Element("largestFiles").addContent(filesEl));
	}
}

