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
