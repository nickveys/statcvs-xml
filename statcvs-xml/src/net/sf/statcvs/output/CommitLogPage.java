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
    
	$RCSfile: CommitLogPage.java,v $ 
	Created on $Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.output;

import java.io.IOException;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.renderer.CommitLogRenderer;

/**
 * @author anja
 */
public class CommitLogPage extends HTMLPage {
	private int pageNr;
	private CommitLogRenderer logRenderer;

	/**
	 * Method CommitLogPage.
	 * @param content of entry
	 * @param logRenderer Renderer
	 * @param pageNr with page
	 * @param totalPages total number of log pages
	 * @throws IOException on IOError
	 */
	public CommitLogPage(CvsContent content, CommitLogRenderer logRenderer,
			int pageNr, int totalPages) throws IOException {
		super(content);
		this.logRenderer = logRenderer;
		setFileName(CommitLogRenderer.getFilename(pageNr));
		setPageName(Messages.getString("COMMIT_LOG_TITLE"));
		if (totalPages > 1) {
			setPageName(getPageName() + (" (Page " + pageNr + " of " + totalPages + ")"));
		}
		this.pageNr = pageNr;
		createPage();
	}

	protected void printBody() throws IOException {
		printBackLink();
		print(logRenderer.renderPage(pageNr));
	}
}
