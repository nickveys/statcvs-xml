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

package de.berlios.statcvs.xml.model;

import java.util.Iterator;

import net.sf.statcvs.model.Commit;
import net.sf.statcvs.model.CvsContent;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * @author Tammo van Lessen
 */
public class CommitCommentGrouper extends Grouper {

	/**
	 * @param id
	 * @param name
	 */
	public CommitCommentGrouper() {
		super("commitcomment", I18n.tr("Commit Comment"));
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see de.berlios.statcvs.xml.model.Grouper#getGroups(net.sf.statcvs.model.CvsContent, de.berlios.statcvs.xml.output.ReportSettings)
	 */
	public Iterator getGroups(CvsContent content, ReportSettings settings) {
		return null;
	}

	/**
	 * @see de.berlios.statcvs.xml.model.Grouper#getName(java.lang.Object)
	 */
	public String getName(Object group) {
		return I18n.tr("Comment");
	}

	/**
	 * @see de.berlios.statcvs.xml.model.Grouper#getGroup(net.sf.statcvs.model.Commit)
	 */
	public Object getGroup(Commit commit) {
		return commit.getComment();
	}

}
