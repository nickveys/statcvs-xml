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

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;

/**
 * @author Steffen Pingel
 */
public class ForEachAuthor extends ForEachObject {

	private Author author;

	/**
	 * @param object
	 * @param id
	 */
	public ForEachAuthor(Author author) 
	{
		super(author, author.getName());

		this.author = author;
	}

//	public Iterator getCommitIterator(CvsContent content)
//	{
//		List commits = new LinkedList();
//		Iterator it = content.getCommits().iterator();
//		while (it.hasNext()) {
//			Commit commit = (Commit) it.next();
//			if (!author.equals(commit.getAuthor())) {
//				continue;
//			}
//			commits.add(commit);
//		}
//		return commits.iterator();
//	}

	/**
	 *  @see de.berlios.statcvs.xml.output.ForEachObject#getDirectoryIterator(net.sf.statcvs.model.CvsContent)
	 */
	public Iterator getDirectoryIterator(CvsContent content) 
	{
		return author.getDirectories().iterator();
	}

	/**
	 *  @see de.berlios.statcvs.xml.output.ForEachObject#getRevisionIterator(net.sf.statcvs.model.CvsContent)
	 */
	public Iterator getRevisionIterator(CvsContent content) 
	{
		return author.getRevisions().iterator();
	}

}
