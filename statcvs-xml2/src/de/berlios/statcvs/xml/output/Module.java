package de.berlios.statcvs.xml.output;

import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.util.FilePatternMatcher;

/**
 * @author Steffen Pingel
 */
public class Module implements Comparable {

	private String name;
	private FilePatternMatcher matcher;
	private Directory directory;
	private SortedSet revisions = new TreeSet();

	/**
	 * @param string
	 * @param dir
	 */
	public Module(Directory directory) 
	{
		this(directory.getPath());
		
		this.directory = directory;
	}

	public Module(String name, String pattern)
	{
		this(name);
		
		matcher = new FilePatternMatcher(pattern);
	}

	public Module(String name)
	{
		this.name = name;
	}

	public int compareTo(Object o) 
	{
		return getName().compareTo(((Module)o).getName());
	}
	/**
	 * @return
	 */
	public String getName() 
	{
		return name;
	}

	public SortedSet getRevisions()
	{
		return revisions;
	}

	public boolean matches(CvsRevision rev)
	{
		if (directory != null && directory != rev.getFile().getDirectory()) {
			return false;
		}
		if (matcher != null) {
			return matcher.matches(rev.getFile().getFilenameWithPath());
		}
		return true;
	}

	/**
	 * @param rev
	 */
	public void addRevision(CvsRevision rev) 
	{
		revisions.add(rev);
	}

}
