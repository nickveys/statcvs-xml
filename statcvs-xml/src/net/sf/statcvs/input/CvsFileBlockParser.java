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
    
	$RCSfile: CvsFileBlockParser.java,v $ 
	Created on $Date: 2003-06-17 16:43:03 $ 
*/

package net.sf.statcvs.input;

import java.io.IOException;

import net.sf.statcvs.output.ConfigurationOptions;
import net.sf.statcvs.util.CvsLogUtils;
import net.sf.statcvs.util.LookaheadReader;

/**
 * Parses the information of one file from a CVS logfile
 * {@link net.sf.statcvs.util.LookaheadReader}. A {@link Builder} must be
 * specified which constructs some representation of that file.
 * 
 * @author Anja Jentzsch
 * @author Richard Cyganiak
 * @version $Id: CvsFileBlockParser.java,v 1.1 2003-06-17 16:43:03 vanto Exp $
 */
public class CvsFileBlockParser {

	private LookaheadReader logReader;
	private Builder builder;

	/**
	 * Default Constructor CvsFileBlockParser.
	 * @param logReader reader
	 * @param builder a <tt>Builder</tt> for the creation process
	 */
	public CvsFileBlockParser(LookaheadReader logReader, Builder builder) {
		this.logReader = logReader;
		this.builder = builder;
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
		boolean isBinary = isBinary(keywordSubst);
		requireLine(logReader.getNextLine(), "total revisions:");
		parseDescription();
		builder.buildFileBegin(workingFile, isBinary, isInAttic);
		new CvsRevisionParser(logReader, builder).parse();
		builder.buildFileEnd();
		if (ConfigurationOptions.getProjectName() == null) {
			String moduleName = CvsLogUtils.getModuleName(rcsFile, workingFile);
			ConfigurationOptions.setProjectTitle(moduleName);
		}
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

		while (true) {
			String line = logReader.getNextLine();
			if (line.startsWith("keyword substitution: ")) {
				return;
			}
			//TODO: Do something with tagName and tagRevision
//			int firstColon = line.indexOf(':');
//			String tagName = line.substring(1, firstColon);
//			String tagRevision = line.substring(firstColon + 2);
		}
	}

	private void parseLocksAndAccessList()
			throws IOException, LogSyntaxException {

		String line;
		do {
			line = logReader.getNextLine();
		} while (!line.equals("access list:"));
		do {
			line = logReader.getNextLine();
		} while (!line.equals("symbolic names:"));
	}

	private boolean isBinary(String kws) throws IOException, LogSyntaxException {
		if ("kv".equals(kws)) {
			return false;
		}
		if ("kvl".equals(kws)) {
			return false;
		}
		if ("k".equals(kws)) {
			return false;
		}
		if ("o".equals(kws)) {
			return false;
		}
		if ("b".equals(kws)) {
			return true;
		}
		if ("v".equals(kws)) {
			return false;
		}
		if ("u".equals(kws)) {
			return false;
		}
		throw new LogSyntaxException("unknown keyword substitution '"
				+ kws + "' at line " + logReader.getLineNumber());
	}

	private void parseDescription() throws LogSyntaxException, IOException {
		requireLine(logReader.getNextLine(), "description:");
		while (!isDescriptionDelimiter(logReader.getCurrentLine())) {
			logReader.getNextLine();
		}
	}
	
	private boolean isDescriptionDelimiter(String line) {
		return CvsRevisionParser.REVISION_DELIMITER.equals(line)
				|| CvsRevisionParser.FILE_DELIMITER.equals(line);
	}
}