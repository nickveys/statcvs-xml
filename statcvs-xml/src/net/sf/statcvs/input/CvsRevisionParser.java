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
    
	$RCSfile: CvsRevisionParser.java,v $ 
	Created on $Date: 2003-06-17 16:43:03 $ 
*/

package net.sf.statcvs.input;

import java.io.IOException;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import net.sf.statcvs.util.DateUtils;
import net.sf.statcvs.util.LookaheadReader;

/**
 * Parses all revisions of one file.
 * 
 * @author Anja Jentzsch
 * @author Richard Cyganiak
 * @version $Id: CvsRevisionParser.java,v 1.1 2003-06-17 16:43:03 vanto Exp $
 */
public class CvsRevisionParser {

	private static Logger logger
			= Logger.getLogger(CvsRevisionParser.class.getName());;

	/**
	 * Revision Delimiter in CVS log file
	 */
	public static final String REVISION_DELIMITER =
			"----------------------------";
	/**
	 * File Delimiter in CVS log file
	 */
	public static final String FILE_DELIMITER
			= "======================================"
			+ "=======================================";

	private LookaheadReader logReader;
	private Builder builder;
	private boolean fileDone = false;

	/**
	 * Default Constructor CvsRevisionParser.
	 * @param logReader the reader
	 * @param builder a <tt>Builder</tt> for the creation process
	 */
	public CvsRevisionParser(LookaheadReader logReader, Builder builder) {
		this.logReader = logReader;
		this.builder = builder;
	}

	/**
	 * Parses the list of revisions for one file
	 * @throws LogSyntaxException on syntax error in the log
	 * @throws IOException on read error
	 */
	public void parse() throws LogSyntaxException, IOException {
		if (FILE_DELIMITER.equals(logReader.getCurrentLine())) {
			return;
		}
		logReader.getNextLine();
		do {
			parseRevision();
		} while (!fileDone);
	}

	private void parseRevision() throws IOException, LogSyntaxException {
		if (!isNewRevisionLine(logReader.getCurrentLine())) {
			throw new LogSyntaxException(
				"expected 'revision' but found '" + logReader.getCurrentLine()
						+ "' in line " + logReader.getLineNumber());
		}
		String revNo = logReader.getCurrentLine().substring("revision ".length());
		builder.buildRevisionBegin(revNo);
		parseDateLine(logReader.getNextLine());
		if (logReader.getNextLine().startsWith("branches:")) {
			logReader.getNextLine();
		}
		StringBuffer comment = new StringBuffer();
		while (true) {
			String line = logReader.getCurrentLine();
			if (REVISION_DELIMITER.equals(line)) {
				String next = logReader.getNextLine();
				if (isNewRevisionLine(next)) {
					builder.buildRevisionEnd(comment.toString());
					return;
				}
			} else if (FILE_DELIMITER.equals(line)) {
				String next = logReader.getNextLine();
				if (next == null || "".equals(next)) {
					builder.buildRevisionEnd(comment.toString());
					fileDone = true;
					return;
				}
			} else {
				logReader.getNextLine();
			}
			if (comment.length() != 0) {
				comment.append('\n');
			}
			comment.append(line);
		}
	}

	private void parseDateLine(String line)
			throws IOException, LogSyntaxException {

		// date: 2000/06/19 04:56:21;  author: somebody;  state: Exp;  lines: +114 -45

		// get the creation date
		int endOfDateIndex = line.indexOf(';', 6);
		String dateString = line.substring(6, endOfDateIndex) + " GMT";
		Date date = DateUtils.convertFromLogTime(dateString);
		if (date == null) {
			throw new LogSyntaxException(
					"unexpected date format in line " + logReader.getLineNumber());
		}
		builder.buildRevisionDate(date);

		// get the author name
		int endOfAuthorIndex = line.indexOf(';', endOfDateIndex + 1);
		builder.buildRevisionAuthor(
				line.substring(endOfDateIndex + 11, endOfAuthorIndex));

		// get the file state (because this revision might be "dead")
		String fileState = line.substring(endOfAuthorIndex + 10,
				line.indexOf(';', endOfAuthorIndex + 1));
		if (isDeadState(fileState)) {
			builder.buildRevisionStateDead();
			return;
		}

		// is this an initial revision?
		int beginOfLinesIndex = line.indexOf("lines:", endOfAuthorIndex + 1);
		if (beginOfLinesIndex < 0) {
			builder.buildRevisionStateInitial();
			return;
		}
		
		// get lines added and lines removed
		StringTokenizer st =
				new StringTokenizer(line.substring(beginOfLinesIndex + 8));
		int linesAdded = Integer.parseInt(st.nextToken());
		String removed = st.nextToken();
		if (removed.indexOf(';') >= 0) {
			removed = removed.substring(0, removed.indexOf(';'));
		}
		int linesRemoved = -Integer.parseInt(removed);
		builder.buildRevisionStateChange(linesAdded, linesRemoved); 
	}

	private boolean isNewRevisionLine(String line) {
		return line.startsWith("revision ");
	}

	private boolean isDeadState(String state) throws IOException {
		if ("dead".equals(state)) {
			return true;
		}
		if ("Exp".equals(state)) {
			return false;
		}
		logger.warning("unknown file state '" + state + "' at line "
				+ logReader.getLineNumber());
		return false;
	}
}