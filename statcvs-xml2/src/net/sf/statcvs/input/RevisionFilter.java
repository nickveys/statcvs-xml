package net.sf.statcvs.input;

/**
 * Instances of this class are queried to determine whether a revision parsed from
 * a CVS log should be kep or discarded. Can be used to dicard revisions that one is not
 * interested in.
 */
public interface RevisionFilter {
    public abstract boolean isValid(RevisionData revisionData);
}
