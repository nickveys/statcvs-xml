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
    
	$RCSfile: ConfigurationException.java,v $
	Created on $Date: 2003/04/19 19:23:42 $ 
*/
package net.sf.statcvs.output;

/**
 * Thrown by {@link ConfigurationOptions} on missing/illegal arguments.
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: ConfigurationException.java,v 1.1 2003/04/19 19:23:42 cyganiak Exp $
 */
public class ConfigurationException extends Exception {

	/**
	 * Constructor
	 * @param message error message
	 */
	public ConfigurationException(String message) {
		super(message);
	}
}