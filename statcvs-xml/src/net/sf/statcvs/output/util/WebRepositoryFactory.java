/*
 * statcvs-xml
 * TODO
 * Created on 04.07.2003
 *
 */
package net.sf.statcvs.output.util;

import java.util.logging.Logger;

/**
 * WebRepositoryFactory
 * 
 * @author Tammo van Lessen
 */
public class WebRepositoryFactory {
	
	private static Logger logger = Logger.getLogger("net.sf.statcvs");
	
	public static WebRepositoryIntegration getInstance(String url) {
		if (url.indexOf("cvs.php") != -1) {
			// chora
			return new ChoraIntegration(url);		
		}
		if (url.indexOf("cvsweb") != -1) {
			// cvsweb
			return new CvswebIntegration(url);		
		}
		if (url.indexOf("viewcvs") != -1) {
			// viewcvs
			return new ViewCvsIntegration(url);		
		}
		return null;	
	}
}
