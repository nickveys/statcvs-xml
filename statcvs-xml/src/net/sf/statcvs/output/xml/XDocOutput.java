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
    
	$RCSfile: XDocOutput.java,v $
	$Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.output.xml;

import java.io.*;

import net.sf.statcvs.model.CvsContent;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

/**
 * Writes xml files to disk.
 *  
 * @author Steffen Pingel
 */
public class XDocOutput {

	public static void generate(CvsContent content) 
		throws IOException, TransformerException
	{
		StreamSource source = new StreamSource("CommitLogDocument2xdoc.xsl");
		Transformer transformer 
			= TransformerFactory.newInstance().newTransformer(source);
		XMLSuite.generate(content, new XMLOutput(transformer));
	}

}
