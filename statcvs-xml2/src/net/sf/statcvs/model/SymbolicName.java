/*
 * statcvs-xml2
 * Created on 17.02.2004
 *
 */
package net.sf.statcvs.model;

import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Represents a symbolic name (tags).
 * It is a container for {@link CvsRevision}s.
 * 
 * @author Tammo van Lessen
 * @version $Id$
 */
public class SymbolicName implements Comparable{
	
	private String name;
	private SortedSet revisions = new TreeSet();
	
	/**
	 * Creates a new symbolic name.
	 * 
	 * @param name the symbolic name's name
	 */
	public SymbolicName(String name)
	{
		this.name = name;
	}

	/**
	 * Returns the symbolic name's name.
	 * 
	 * @return the symbolic name's name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Adds a revision to this symbolic name.
	 * 
	 * @param rev the revision
	 */
	protected void addRevision(CvsRevision rev)
	{
		revisions.add(rev);
	}

	/**
	 * Returns a set of {@link CvsRevision}s contained in this symbolic name.
	 * 
	 * @return the revisions
	 */
	public SortedSet getRevisions()
	{
		return revisions;
	}
	
	/**
	 * Returns the 'date' of this symbolic name.
	 * 
	 * Since symbolic names actually dont have a 'date',
	 * the latest date of the affected revisions will be taken.
	 *  
	 * @return the smbolic name's date
	 */
	public Date getDate()
	{
		return ((CvsRevision)revisions.last()).getDate();
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		SymbolicName other = (SymbolicName)o;
		return getDate().compareTo(other.getDate());
	}
	
}
