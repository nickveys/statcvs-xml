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
    
	$RCSfile: LargestFilesReport.java,v $
	$Date: 2003-06-24 22:48:42 $ 
*/
package net.sf.statcvs.output.xml.report;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.FilesLocComparator;
import net.sf.statcvs.output.ConfigurationOptions;
import net.sf.statcvs.output.WebRepositoryIntegration;

import org.jdom.Element;

/**
 * 
 * 
 * @author Steffen Pingel
 */
public class LargestFilesReport extends ReportElement {

	public static final int MAX_ITEMS = 20;
	private CvsContent content;
	
	/**
	 * 
	 */
	public LargestFilesReport(CvsContent content) 
	{
		super(I18n.tr("Largest Files (TOP {0})", new Integer(MAX_ITEMS)));
		this.content = content;
		createReport();
	}

	private void createReport() {
		Element filesEl = new Element("files");
		
		List files = content.getFiles();
		Collections.sort(files, new FilesLocComparator());
		files = files.subList(0, MAX_ITEMS);
		Iterator it = files.iterator();
		while (it.hasNext()) {
			CvsFile file = (CvsFile) it.next();
			if (file.isBinary() || file.isDead()) {
				continue;
			}

			WebRepositoryIntegration webRepository = ConfigurationOptions.getWebRepository();
			
			Element fileEl = new Element("file");
			fileEl.setAttribute("name", file.getFilenameWithPath());
			fileEl.setAttribute("loc", ""+file.getCurrentLinesOfCode());
			fileEl.setAttribute("revisions", ""+file.getRevisions().size());
			if (webRepository != null) {
				fileEl.setAttribute("url", webRepository.getFileViewUrl(file));				
			}
			
			filesEl.addContent(fileEl);
		}

		addContent(new Element("largestFiles").addContent(filesEl));
	}
}

