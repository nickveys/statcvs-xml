/*
 * statcvs-xml
 * TODO
 * Created on 24.06.2003
 *
 */
package net.sf.statcvs.output.xml.report;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.FilesRevisionCountComparator;

import org.jdom.Element;

/**
 * AuthorsPerFileReport
 * 
 * @author Tammo van Lessen
 * @version $id: $
 */
public class AuthorsPerFileReport extends ReportElement {

	private CvsContent content;
	
	/**
	 * 
	 */
	public AuthorsPerFileReport(CvsContent content) {
		super(I18n.tr("Authors per File"));
		this.content = content;
		createReport();		
	}

	private void createReport() {
		Element filesEl = new Element("files");
		
		List files = content.getFiles();
		Collections.sort(files, new FilesRevisionCountComparator());
		Iterator it = files.iterator();
		while (it.hasNext()) {
			CvsFile file = (CvsFile) it.next();
			if (file.isBinary() || file.isDead()) {
				continue;
			}

			Element fileEl = new Element("file");
			fileEl.setAttribute("name", file.getFilenameWithPath());
			int authorsCount = 0;
			Iterator authors = content.getAuthors().iterator();
			while (authors.hasNext()) {
				Author author = (Author) authors.next();
				if (file.hasAuthor(author)) {
					authorsCount++;
				}
			}
			fileEl.setAttribute("authors", ""+authorsCount);
			filesEl.addContent(fileEl); 
		}

		addContent(new Element("authorsPerFile").addContent(filesEl));
	}

}
