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
    
	$RCSfile: XMLOutputter.java,v $
	$Date: 2003-07-01 20:49:52 $ 
*/
package net.sf.statcvs.output.xml.util;

import java.io.IOException;
import java.io.Writer;

import javax.xml.transform.Result;

import org.jdom.ProcessingInstruction;

/**
 * XMLOutputter
 *
 * Add support for disable-output-escaping to jdom's XMLOutputter
 *  
 * @author Tammo van Lessen
 */
public class XMLOutputter extends org.jdom.output.XMLOutputter {


	private boolean disableOutputEscaping;

	/**
	 * @see org.jdom.output.XMLOutputter#printProcessingInstruction(org.jdom.ProcessingInstruction, java.io.Writer)
	 */
	protected void printProcessingInstruction(ProcessingInstruction pi, Writer out)
		throws IOException {
		if (pi.getTarget().equals(Result.PI_DISABLE_OUTPUT_ESCAPING)) {
			disableOutputEscaping = true;
			setNewlines(false);
			return;
		}
		if (pi.getTarget().equals(Result.PI_ENABLE_OUTPUT_ESCAPING)) {
			disableOutputEscaping = false;
			setNewlines(true);
			return;
		}

		super.printProcessingInstruction(pi, out);
	}


	/**
	 * @see org.jdom.output.XMLOutputter#escapeElementEntities(java.lang.String)
	 */
	public String escapeElementEntities(String str) {
		return (disableOutputEscaping)?str:super.escapeElementEntities(str);
	}

}
