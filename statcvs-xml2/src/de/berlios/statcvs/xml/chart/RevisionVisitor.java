package de.berlios.statcvs.xml.chart;

import net.sf.statcvs.model.CvsRevision;

/**
 * Defines the requirements for objects that calculate values visiting each revision. 
 * 
 * @author Steffen Pingel
 */
public interface RevisionVisitor {

	/**
	 * Calculates a value for revision.
	 * 
	 * @param revision the revision
	 * @return the value
	 */
	public int visit(CvsRevision revision);

}
