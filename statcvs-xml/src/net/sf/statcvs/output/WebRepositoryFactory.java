/*
 * statcvs-xml
 * TODO
 * Created on 04.07.2003
 *
 */
package net.sf.statcvs.output;

import java.util.logging.Logger;

/**
 * WebRepositoryFactory
 * 
 * @author Tammo van Lessen
 */
public class WebRepositoryFactory {
	
	private static Logger logger = Logger.getLogger("net.sf.statcvs.output.WebRepositoryFactory");
	
	public static WebRepositoryIntegration getInstance(String url) {
		if (url.indexOf("cvs.php") != 0) {
			// chora
			logger.info("Assuming web repository is chora");
			return new ChoraIntegration(url);		
		}
		if (url.indexOf("cvsweb.cgi") != 0) {
			// cvsweb
			logger.info("Assuming web repository is cvsweb");
			return new ChoraIntegration(url);		
		}
		if (url.indexOf("viewcvs.cgi") != 0) {
			// view cvs
			logger.info("Assuming web repository is viewcvs");
			return new ChoraIntegration(url);		
		}
		return null;	
	}
}
