/*
	StatCvs - CVS statistics generation 
	Copyright (C) 2002  Lukasz Pekacki <lukasz@pekacki.de>
	http://statcvs.sf.net/
    
	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
    
	$RCSfile: WebRepositoryFactory.java,v $
	$Date: 2003-07-06 23:38:27 $ 
*/
package net.sf.statcvs.output.util;


/**
 * WebRepositoryFactory
 * 
 * @author Tammo van Lessen
 */
public class WebRepositoryFactory {
	
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
