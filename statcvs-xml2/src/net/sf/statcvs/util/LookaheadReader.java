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
    
	$RCSfile$
	$Date$
*/
package net.sf.statcvs.util;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

/**
 * Wraps a {@link java.io.Reader} for line-by-line access. Allows reading
 * the current line multiple times. At construction time, the
 * <tt>LookaheadReader</tt> points to line 1 of the source reader. Calls to
 * {@link #getCurrentLine} will return the first line of the source reader.
 * A call to {@link #getNextLine} causes a readLine from the source reader.
 * Subsequent calls to <tt>getCurrentLine()</tt> will return the second line
 * of the source reader.
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id$
 */
public class LookaheadReader {

	private LineNumberReader reader;
	private String currentLine = null;

	/**
	 * Constructor
	 * @param reader a <tt>BufferedReader</tt>
	 */
	public LookaheadReader(Reader reader) {
		this.reader = new LineNumberReader(reader);
	}
	
	/**
	 * Returns the current line without reading a line from the source
	 * reader. At construction time, the source reader's first line is
	 * the current line.
	 * @return the current line, or <tt>null</tt> if at the end of the
	 *         source reader
	 * @throws IOException on error while reading the source reader 
	 */
	public String getCurrentLine() throws IOException {
		fetchLine();
		return currentLine;
	}
	
	/**
	 * Reads and returns a line from the source reader. The result of
	 * this call will be the new current line.
	 * @return the next line of the source reader, or <tt>null</tt> if at
	 *         the end of the source reader
	 * @throws IOException on error while reading the source reader 
	 */
	public String getNextLine() throws IOException {
		currentLine = null;
		return getCurrentLine();
	}
	
	/**
	 * Returns the current line number
	 * @return the current line number, starting at 1
	 * @throws IOException on error while reading the underlying reader 
	 */
	public int getLineNumber() throws IOException {
		fetchLine();
		return reader.getLineNumber();
	}

	/**
	 * Returns <tt>true</tt> if the last call to {@link #getNextLine}
	 * returned null
	 * @return <tt>true</tt> if pointer is behind the end of the source reader 
	 * @throws IOException on error while reading the source reader
	 */
	public boolean isAfterEnd() throws IOException {
		fetchLine();
		return currentLine == null; 
	}

	private void fetchLine() throws IOException {
		if (currentLine == null) {
			currentLine = reader.readLine();
		}
	}
}