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
	Created on $Date$ 
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
 * @version $Id$
 */
public class CvsLogfileParser {

	private static Logger logger
			= Logger.getLogger(CvsLogfileParser.class.getName());

	private LookaheadReader logReader;
	private CvsLogBuilder builder;

	/**
	 * Default Constructor
	 * @param logReader a <tt>Reader</tt> containing the CVS logfile
	 * @param builder the builder that will process the log information
	 */
	public CvsLogfileParser(Reader logReader, CvsLogBuilder builder) {
		this.logReader = new LookaheadReader(logReader);
		this.builder = builder;
	}
	
	/**
	 * Parses the logfile. After <tt>parse()</tt> has finished, the result
	 * of the parsing process can be obtained from the builder.
	 * @throws LogSyntaxException if syntax errors in log
	 * @throws IOException if errors while reading from the log Reader
	 */
	public void parse() throws LogSyntaxException, IOException, EmptyRepositoryException {
        long startTime = System.currentTimeMillis();
		logger.fine("starting to parse...");
		eatNonCheckedInFileLines();
		if (logReader.isAfterEnd()) {
			throw new LogSyntaxException("empty logfile!");
		}
		if (!logReader.isAfterEnd() && !"".equals(logReader.getCurrentLine())) {
			throw new LogSyntaxException("expected '?' or empty line at line "
					+ logReader.getLineNumber() + ", but found '"
					+ logReader.getCurrentLine() + "'");
		}
		eatEmptyLines();
//		TODO: uncomment when tag/branch reports are added 
//		boolean isLogWithoutSymbolicNames = false;
		boolean isFirstFile = true;
		do {
			CvsFileBlockParser parser = new CvsFileBlockParser(logReader, builder, isFirstFile);
			parser.parse();
			isFirstFile = false;
//			if (parser.isLogWithoutSymbolicNames()) {
//				isLogWithoutSymbolicNames = true;
//			}
			eatEmptyLines();
		} while (!logReader.isAfterEnd());
//		if (isLogWithoutSymbolicNames) {
//			logger.warning("Log was created with '-N' switch of 'cvs log', some reports will be missing!");
//		}
		logger.fine("parsing finished in "
				+ (System.currentTimeMillis() - startTime) + " ms.");
	}

	private void eatNonCheckedInFileLines() throws LogSyntaxException, IOException {
		while (!logReader.isAfterEnd() && logReader.getCurrentLine().startsWith("? ")) {
			logReader.getNextLine();
		}
	}

	private void eatEmptyLines() throws IOException {
		while (!logReader.isAfterEnd() && logReader.getCurrentLine().equals("")) {
			logReader.getNextLine();
		}
	}
}