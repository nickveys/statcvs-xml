package net.sf.statcvs.input;

public class NoBranchException extends RuntimeException {
    
    public NoBranchException() {
    }

    public NoBranchException(String message) {
        super(message);
    }
}
