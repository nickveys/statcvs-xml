/*
    StatCvs - CVS statistics generation 
    Copyright (C) 2002  Lukasz Pekacki <lukasz@pekacki.de>
    http://statcvs.sf.net/
    
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package de.berlios.statcvs.xml.report;

import java.util.Iterator;

import net.sf.statcvs.model.Author;
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
 * AuthorsPerFileReport
 * 
 * @author Tammo van Lessen
 * @author Steffen Pingel
 */
public class AuthorCountTable {

	/**
	 * 
	 */
	public static ReportElement generate(CvsContent content, ReportSettings settings) 
	{
		ReportElement root = new ReportElement(I18n.tr("Authors Per File"));
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

			Iterator authors = content.getAuthors().iterator();
			while (authors.hasNext()) {
				Author author = (Author)authors.next();
				if (file.hasAuthor(author)) {
					filesMap.addInt(file, 1);
				}
			}
		}
		
		Element filesEl = new Element("files");		
		WebRepositoryIntegration webRepository = Settings.getWebRepository();
		Iterator fIt = filesMap.iteratorSortedByValueReverse();
		int count = 0;
		while (fIt.hasNext() && count < maxItems) {
			CvsFile file = (CvsFile)fIt.next();
			
			Element fileEl = new Element("file");
			fileEl.setAttribute("name", file.getFilenameWithPath());
			
			if (webRepository != null) {
				fileEl.setAttribute("url", webRepository.getFileViewUrl(file));				
			}
			fileEl.setAttribute("authors", ""+filesMap.get(file));
			filesEl.addContent(fileEl); 
			count++;
		}
		root.addContent(new Element("authorsPerFile").addContent(filesEl));
	}

}
