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
    
	$RCSfile: LOCPage.java,v $ 
	Created on $Date: 2003-06-17 16:43:02 $ 
*/

package net.sf.statcvs.output;

import java.io.IOException;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.CvsContent;

/**
 * @author anja
 */
public class LOCPage extends HTMLPage {

	private boolean locImageCreated;
	/**
	 * @see net.sf.statcvs.output.HTMLPage#HTMLPage(CvsContent)
	 */
	public LOCPage(CvsContent content, boolean locImageCreated) throws IOException {
		super(content);
		this.locImageCreated = locImageCreated;
		setFileName("loc.html");
		setPageName(Messages.getString("LOC_TITLE"));
		createPage();
	}

	protected void printBody() throws IOException {
		printBackLink();
		print(getLOCImage());
	}

	private String getLOCImage() {
		if (!locImageCreated) {
			return p(Messages.getString("NO_LOC_AVAILABLE"));
		}
		String result;
		int loc = getContent().getCurrentLOC();
		result = img("loc.png", 640, 480) + br()
				+ strong(Messages.getString("TOTAL_LOC") + ": ") + loc;
		result += " ("
				+ HTMLTagger.getDateAndTime(getContent().getLastDate())
				+ ")";
		return p(result);
	}
}