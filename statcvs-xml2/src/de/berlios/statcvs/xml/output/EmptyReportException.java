package de.berlios.statcvs.xml.output;

/**
 * @author Steffen Pingel
 */
public class EmptyReportException extends Exception {

	/**
	 * 
	 */
	public EmptyReportException() 
	{
	}

	/**
	 * @param message
	 */
	public EmptyReportException(String message) 
	{
		super(message);
	}

}
