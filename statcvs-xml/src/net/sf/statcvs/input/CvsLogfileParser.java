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
    
	$RCSfile: CvsLogfileParser.java,v $ 
	Created on $Date: 2003-06-17 16:43:03 $ 
*/

package net.sf.statcvs.input;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;

import net.sf.statcvs.util.LookaheadReader;

/**
 * Parses a CVS logfile. A {@link Builder} must be specified which does
 * the construction work.
 * 
 * @author Anja Jentzsch
 * @author Richard Cyganiak
 * @version $Id: CvsLogfileParser.java,v 1.1 2003-06-17 16:43:03 vanto Exp $
 */
public class CvsLogfileParser {

	private static Logger logger
			= Logger.getLogger(CvsLogfileParser.class.getName());

	private LookaheadReader logReader;
	private Builder builder;

	/**
	 * Default Constructor
	 * @param logReader a <tt>Reader</tt> containing the CVS logfile
	 * @param builder the builder to use for parsing
	 */
	public CvsLogfileParser(Reader logReader, Builder builder) {
		this.logReader = new LookaheadReader(logReader);
		this.builder = builder;
	}
	
	/**
	 * Parses the logfile. After <tt>parse()</tt> has finished, the result
	 * of the parsing process can be obtained from the builder.
	 * @throws LogSyntaxException if syntax errors in log
	 * @throws IOException if errors while reading from the log Reader
	 */
	public void parse() throws LogSyntaxException, IOException {
        long startTime = System.currentTimeMillis();
		logger.fine("starting to parse...");
		eatNonCheckedInFileLines();
		if (!logReader.isAfterEnd() && !"".equals(logReader.getCurrentLine())) {
			throw new LogSyntaxException("expected '?' or empty line at line "
					+ logReader.getLineNumber() + ", but found '"
					+ logReader.getCurrentLine() + "'");
		}
		eatEmptyLines();
		do {
			new CvsFileBlockParser(logReader, builder).parse();
			eatEmptyLines();
		} while (!logReader.isAfterEnd());
		logger.fine("parsing finished in "
				+ (System.currentTimeMillis() - startTime) + " ms.");
		builder.finish();
	}

	private void eatNonCheckedInFileLines() throws LogSyntaxException, IOException {
		while (logReader.getCurrentLine().startsWith("? ")) {
			logReader.getNextLine();
		}
	}

	private void eatEmptyLines() throws IOException {
		while (!logReader.isAfterEnd() && logReader.getCurrentLine().equals("")) {
			logReader.getNextLine();
		}
	}
}