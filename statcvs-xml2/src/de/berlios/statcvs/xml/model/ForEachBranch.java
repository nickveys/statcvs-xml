/*
 *  StatCvs-XML - XML output for StatCvs.
 *
 *  Copyright by Steffen Pingel, Tammo van Lessen.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  version 2 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
 
package de.berlios.statcvs.xml.model;

import java.util.Iterator;
import net.sf.statcvs.model.CvsBranch;
import net.sf.statcvs.model.CvsContent;

/**
 * @author Steffen Pingel
 */
public class ForEachBranch extends ForEachObject {

	private CvsBranch branch;

	/**
	 * @param object
	 * @param id
	 */
	public ForEachBranch(CvsBranch branch) 
	{
		super(branch, branch.getName());

		this.branch = branch;
	}

	/**
	 *  @see de.berlios.statcvs.xml.output.ForEachObject#getRevisionIterator(net.sf.statcvs.model.CvsContent)
	 */
	public Iterator getRevisionIterator(CvsContent content) 
	{
		return branch.getRevisions().iterator();
	}

}
