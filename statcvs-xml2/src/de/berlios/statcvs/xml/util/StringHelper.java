/*
 *  StatCvs-XML - XML output for StatCvs.
 *
 *  Copyright by Steffen Pingel, Tammo van Lessen.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  version 2 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
 
package de.berlios.statcvs.xml.util;


/**
 * @author Steffen Pingel
 */
public class StringHelper {

	public static String escapeFilename(String filename)
	{
		if (filename.endsWith("/")) {
			filename = filename.substring(0, filename.length() - 1);
		}
		filename = filename.replaceAll("/", "_");
		filename = filename.replaceAll("#", "_");
		return filename;
	}

	public static String replaceNonXMLCharacters(String text)
	{
		if (text == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer(text);
        for (int i = 0, len = text.length(); i < len; i++) {
            if (!isXMLCharacter(text.charAt(i))) {
            	sb.setCharAt(i, '?');
            }
        }
        return sb.toString();
	}
	
    /**
     * This is a utility function for determining whether a specified 
     * character is a character according to production 2 of the 
     * XML 1.0 specification.
     *  
     * <p>Copied from org.jdom.Verifier.
     * 
     * <p>Copyright (C) 2000-2004 Jason Hunter & Brett McLaughlin.
     * All rights reserved.
     *
     * @param c <code>char</code> to check for XML compliance
     * @return <code>boolean</code> true if it's a character, 
     *                                false otherwise
     */
    public static boolean isXMLCharacter(char c) {
    
        if (c == '\n') return true;
        if (c == '\r') return true;
        if (c == '\t') return true;
        
        if (c < 0x20) return false;  if (c <= 0xD7FF) return true;
        if (c < 0xE000) return false;  if (c <= 0xFFFD) return true;
        if (c < 0x10000) return false;  if (c <= 0x10FFFF) return true;
        
        return false;
    }

}
