package net.sf.statcvs.input;

/**
 * Marks all revisions as valid
 */
public class DefaultRevisionFilter implements RevisionFilter {
    /**
     * Always returns true
     * @param revisionData
     * @return true
     */
    public boolean isValid(RevisionData revisionData) {
        return true;
    }
}
