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
    
	$RCSfile: Messages.java,v $ 
	Created on $Date: 2003/03/18 10:33:56 $ 
*/
package net.sf.statcvs;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class manages the externalization of strings that will
 * possiby be presented to the user
 * @author Lukasz Pekacki
 * @version $Id: Messages.java,v 1.7 2003/03/18 10:33:56 lukasz Exp $
 */
public class Messages {
	/**
	 * Whitespace constant
	 */
	public static final String WS = " ";
	/**
	 * Newline constant
	 */
	public static final String NL = "\n";

	private static final String BUNDLE_NAME = "net.sf.statcvs.statcvs";
	private static final ResourceBundle RESOURCE_BUNDLE =
			ResourceBundle.getBundle(BUNDLE_NAME);

	/**
	 * Returns the value for the specified key. key-value pairs are specivied
	 * in the resourcebundle properties file.
	 * @param key key of the requested string
	 * @return String
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		}   catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

}
