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
package de.berlios.statcvs.xml.report;

import java.util.Collections;
import java.util.List;

import net.sf.statcvs.input.CommitListBuilder;
import net.sf.statcvs.model.Commit;
import net.sf.statcvs.model.CvsContent;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.Report;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;
import de.berlios.statcvs.xml.output.TableElement;
import de.berlios.statcvs.xml.output.TableElement.RowElement;

/**
 * CommitlogTable
 * 
 * @author Tammo van Lessen
 */
public class CommitLogTable {

	public static Report generate(CvsContent content, ReportSettings settings)
	{
		ReportElement report = new ReportElement(settings, I18n.tr("Commit Log"));
		TableElement table = new TableElement(settings, new String[] {I18n.tr("Date"), 
												I18n.tr("Author"),
												I18n.tr("File/Message")});
		
		CommitListBuilder builder 
			= new CommitListBuilder(settings.getRevisionIterator(content));
		List commits = builder.createCommitList();
		Collections.reverse(commits);
		
		int maxItems = settings.getLimit();
		for (int i = 0; i < commits.size() && i < maxItems; i++) {
			Commit commit = (Commit)commits.get(i);
			RowElement row = table.addRow();
			row.addDate("date", commit.getDate());
			row.addAuthor(commit.getAuthor());
			row.addCommit(commit);
		}
		report.addContent(table);
		return new Report(report, table);
	}
}
