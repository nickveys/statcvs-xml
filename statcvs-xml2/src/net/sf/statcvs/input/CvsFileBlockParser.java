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
import java.util.logging.Logger;

import net.sf.statcvs.util.CvsLogUtils;
import net.sf.statcvs.util.LookaheadReader;

/**
 * Parses the information of one file from a CVS logfile
 * {@link net.sf.statcvs.util.LookaheadReader}. A {@link Builder} must be
 * specified which constructs some representation of that file. The lookahead
 * reader must be positioned on the first line of the file's section in the
 * log ("RCS file: ...").
 * 
 * @author Anja Jentzsch
 * @author Richard Cyganiak
 * @version $Id$
 */
public class CvsFileBlockParser {
	private static Logger logger = Logger.getLogger(CvsFileBlockParser.class.getName());
	private LookaheadReader logReader;
	private CvsLogBuilder builder;
	private boolean isLogWithoutSymbolicNames = false;
	private boolean isFirstFile;

	/**
	 * Default Constructor CvsFileBlockParser.
	 * @param logReader reader
	 * @param builder a <tt>Builder</tt> for the creation process
	 * @param isFirstFile Is this the first file of the log?
	 */
	public CvsFileBlockParser(LookaheadReader logReader, CvsLogBuilder builder,
							  boolean isFirstFile) {
		this.logReader = logReader;
		this.builder = builder;
		this.isFirstFile = isFirstFile;
	}

	/**
	 * Parses one file from the input reader.
	 * 
	 * @throws LogSyntaxException on syntax error
	 * @throws IOException on read/write error
	 */
	public void parse() throws LogSyntaxException, IOException {
		String rcsFile = parseSingleLine(logReader.getCurrentLine(), "RCS file: ");
		String workingFile = parseSingleLine(logReader.getNextLine(), "Working file: ");
		boolean isInAttic = CvsLogUtils.isInAttic(rcsFile, workingFile);
		requireLine(logReader.getNextLine(), "head:");
		requireLine(logReader.getNextLine(), "branch:");
		requireLine(logReader.getNextLine(), "locks:");
		parseLocksAndAccessList();
		parseSymbolicNames();
		String keywordSubst = parseSingleLine(logReader.getCurrentLine(),
				"keyword substitution: ");	
		boolean isBinary = false;
		try {
			isBinary = CvsLogUtils.isBinaryKeywordSubst(keywordSubst);
		} catch (IllegalArgumentException unknownKeywordSubst) {
			logger.warning("unknown keyword substitution '" + keywordSubst
					+ "' in line " + logReader.getLineNumber());
		}
		requireLine(logReader.getNextLine(), "total revisions:");
		parseDescription();
		if (isFirstFile) {
			builder.buildModule(CvsLogUtils.getModuleName(rcsFile, workingFile));
		}
		builder.buildFile(workingFile, isBinary, isInAttic);
		if (CvsRevisionParser.FILE_DELIMITER.equals(logReader.getCurrentLine())) {
			logReader.getNextLine();
		} else {
			new CvsRevisionParser(logReader, builder).parse();
		}
	}

	/**
	 * Returns <tt>true</tt> if the log was generated
	 * with the "-N" switch of "cvs log"
	 * 
	 * @return Returns <tt>true</tt> if the log was generated
	 * with the "-N" switch of "cvs log"
	 */
	public boolean isLogWithoutSymbolicNames() {
		return isLogWithoutSymbolicNames;
	}

	private String parseSingleLine(String line, String lineStart)
			throws IOException, LogSyntaxException {

		if (!line.startsWith(lineStart)) {
			throw new LogSyntaxException(
				"line " + logReader.getLineNumber() + ": expected '"
						+ lineStart + "' but found '" + line + "'");
		}

		return line.substring(lineStart.length());
	}

	private void requireLine(String line, String lineStart)
			throws IOException, LogSyntaxException {

		parseSingleLine(line, lineStart); // ignore this line
	}

	private void parseSymbolicNames()
			throws IOException, LogSyntaxException {

		String line;
		if (logReader.getCurrentLine().equals("symbolic names:")) {
			line = logReader.getNextLine();
		} else {
			isLogWithoutSymbolicNames = true;
			line = logReader.getCurrentLine();
		}
		while (line != null && !line.startsWith("keyword substitution: ")) {
			//TODO: Do something with tagName and tagRevision
//			int firstColon = line.indexOf(':');
//			String tagName = line.substring(1, firstColon);
//			String tagRevision = line.substring(firstColon + 2);
			line = logReader.getNextLine();
		}
	}

	private void parseLocksAndAccessList()
			throws IOException, LogSyntaxException {

		String line;
		do {
			line = logReader.getNextLine();
		} while (line != null && !line.equals("access list:"));
		do {
			line = logReader.getNextLine();
		} while (line != null
				&& !line.equals("symbolic names:")
				&& !line.startsWith("keyword substitution: "));
	}

	private void parseDescription() throws LogSyntaxException, IOException {
		String line = logReader.getNextLine();
		if (line.equals(CvsRevisionParser.FILE_DELIMITER)) {
			throw new LogSyntaxException(
				"line " + logReader.getLineNumber() + ": missing description; please don't use the -h switch of 'cvs log'!");
		}
		requireLine(logReader.getCurrentLine(), "description:");
		while (!isDescriptionDelimiter(logReader.getCurrentLine())) {
			logReader.getNextLine();
		}
	}
	
	private boolean isDescriptionDelimiter(String line) {
		return CvsRevisionParser.REVISION_DELIMITER.equals(line)
				|| CvsRevisionParser.FILE_DELIMITER.equals(line);
	}
}