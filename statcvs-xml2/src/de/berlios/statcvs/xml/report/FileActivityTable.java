package de.berlios.statcvs.xml.report;

import java.util.Iterator;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.output.WebRepositoryIntegration;
import net.sf.statcvs.util.IntegerMap;

import org.jdom.Element;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * 
 * 
 * @author Steffen Pingel
 */
public class FileActivityTable {

	/**
	 * 
	 */
	public static ReportElement generate(CvsContent content, ReportSettings settings) 
	{
		ReportElement root = new ReportElement(I18n.tr("File Activity"));

		IntegerMap filesMap = new IntegerMap();
		Iterator it = settings.getRevisionIterator(content);
		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision)it.next();
			if (rev.getFile().isDead()) {
				continue;
			}
			filesMap.addInt(rev.getFile(), 1);
		}

		Element filesEl = new Element("files");	
		Iterator filesIt = filesMap.iteratorSortedByValueReverse();
		WebRepositoryIntegration webRepository = settings.getWebRepository();
		
		int maxItems = settings.getLimit();
		int count = 0;
		while (filesIt.hasNext() && count < maxItems) {
			CvsFile file = (CvsFile)filesIt.next();
			
			Element fileEl = new Element("file");
			fileEl.setAttribute("name", file.getFilenameWithPath());
			fileEl.setAttribute("commits", "" + filesMap.get(file));
			if (webRepository != null) {
				fileEl.setAttribute("url", webRepository.getFileViewUrl(file));				
			}
			
			filesEl.addContent(fileEl);
			count++;
		}

		root.addContent(new Element("fileActivity").addContent(filesEl));
		return root;
	}
}

