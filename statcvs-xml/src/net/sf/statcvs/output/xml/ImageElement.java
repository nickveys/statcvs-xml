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
    
	$RCSfile: ImageElement.java,v $
	$Date: 2003-06-18 21:22:43 $ 
*/
package net.sf.statcvs.output.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.Commit;
import net.sf.statcvs.model.CommitListBuilder;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.model.RevisionSortIterator;
import net.sf.statcvs.output.ConfigurationOptions;
import net.sf.statcvs.output.WebRepositoryIntegration;
import net.sf.statcvs.renderer.FileCollectionFormatter;
import net.sf.statcvs.util.DateUtils;

import org.jdom.Element;

/**
 * @author Steffen Pingel
 */
public class ImageElement extends Element {

	/**
	 * Sets the attributes.
	 *
	 * @param filename the filename of the image
	 */
	public ImageElement(String filename) {
		super("img");
		
		setAttribute("src", filename);
	}

}
