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
    
	$RCSfile: CommitLogDocument.java,v $
	$Date: 2003-06-19 23:56:28 $ 
*/
package net.sf.statcvs.output.xml;

import java.util.logging.Logger;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.CvsContent;

import org.jdom.Document;
import org.jdom.Element;
 
/**
 * CommitLogDocument
 * 
 * @author Tammo van Lessen
 */
public class CommitLogDocument extends AbstractPageableDocument {

	private static final Logger logger = Logger.getLogger("net.sf.statcvs.output.xml.CommitLogDocument");
	private CvsContent content;
	/**
	 * 
	 */
	public CommitLogDocument(CvsContent content) {
		super(I18n.tr("Commit Log"),"commit_log", 5);
		this.content = content;
		
		Element root = getRootElement();
		Element report = new ReportElement(I18n.tr("Commit Log"));
		
		root.addContent(report);
		Element rootClone = (Element)root.clone();
		
		Element commitLog = getPageableContent();
		report.addContent(commitLog);
		setPageableContent(commitLog);
	}
	
	public Element createPageTemplate() {
		Document doc = new StatCvsDocument("dummy",null);
		Element docEl = new Element("document");
		docEl.setAttribute("title", I18n.tr("Commit Log"));

		Element report = new ReportElement(I18n.tr("Commit Log"));
		docEl.addContent(report);
		doc.setRootElement(docEl);
		return report;
	}
	
	public Element getPageableContent() {
		return new CommitLogElement(CommitLogElement.getCommitList(content));
	}
}
