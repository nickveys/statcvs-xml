package net.sf.statcvs.model;

import java.util.TreeSet;
import net.sf.statcvs.util.CvsLogUtils;

/**
 * This class represents a branch in CVS repository
 */
public class CvsBranch implements Comparable {

    private final String name;
	private final TreeSet revisions = new TreeSet();
	
    public CvsBranch(String name) {
        this.name = name;
    }

    /**
     * Constructs CvsBranch represinting HEAD
     */
    public CvsBranch() {
        this(CvsLogUtils.HEAD_BRANCH_NAME);
    }

	public int compareTo(Object o) {
		// FIXME comparison by date would make more sense 
		return getName().compareTo(((CvsBranch)o).getName());
	}

    public String getName() {
        return name;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CvsBranch)) return false;

        final CvsBranch cvsBranch = (CvsBranch) o;

        if (!name.equals(cvsBranch.name)) return false;

        return true;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public void addRevision(CvsRevision revision) {
        revisions.add(revision);
    }
	
	public TreeSet getRevisions() {
		return revisions;
	}
	
}
