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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.Report;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;
import de.berlios.statcvs.xml.output.TableElement;
import de.berlios.statcvs.xml.output.TextElement;
import de.berlios.statcvs.xml.util.FileHelper;

/**
 * AuthorInfoReport
 * 
 * @author Tammo van Lessen
 */
public class AuthorDetailsReport {

	private static final Logger logger = 
		Logger.getLogger("de.berlios.statcvs.xml.report.AuthorDetailsReport");
	private static final String DEFAULT_PIC = "resources/dummy.png";
	
	public static Report generate(CvsContent content, ReportSettings settings) 
	{
		return new Report(new AuthorInfoElement(settings, 
												  I18n.tr("Author Details")));
	}
	
	public static class AuthorInfoElement extends ReportElement
	{
	
		private String picFile = null;
		
		public AuthorInfoElement(ReportSettings settings, String name)
		{
			super(settings, name);

			if (settings.getForEachObject() instanceof Author) {
				Author author = (Author)settings.getForEachObject();

				// calc data
				int loc = 0;
				int locAdded = 0;
				for (Iterator it = author.getRevisions().iterator();
					it.hasNext();) {
					CvsRevision rev = (CvsRevision)it.next(); 
					loc += rev.getLinesDelta();
					locAdded += rev.getNewLines(); 		
				}

				// create details
				TextElement text = new TextElement(settings, "authorinfo")
					.addValue("login", author.getName(), I18n.tr("Login"))
					.addValue("fullname", settings.getFullname(author), 
								I18n.tr("Fullname"))
					.addValue("revcount", author.getRevisions().size(), 
								I18n.tr("Revisions"))
					.addValue("loc", loc, I18n.tr("Lines of Code"))
					.addValue("locAdded", locAdded, I18n.tr("Added Lines of Code"))
					.addValue("locPerRevision", (double)loc / author.getRevisions().size(), 
								I18n.tr("Lines of Code per Change"));

				if (settings.getBoolean("showImages", true)) {
					// add a table with image and details
					TableElement table = new TableElement(settings, null);

					
					picFile = settings.getAuthorPic(author, DEFAULT_PIC);
					File pf = new File(picFile);
					
					if (!pf.exists() && !picFile.equals(DEFAULT_PIC)) {
						logger.info("Picture "+picFile+" (Author: "+author.getName()+") not found. Using dummy instead.");
						picFile = DEFAULT_PIC;
						pf = new File(picFile); 
					}
					
					table.addRow().addImage("authorPicture", pf.getName())
					  			  .addContent(text);
					
					addContent(table);
				} 
				else {
					// add details only (showImages == false)
					addContent(text);					
				}
			} 
			else {
				logger.warning("This report can only be used in author-foreach environments.");
			}
			
		}
		
		/**
		 *  @see de.berlios.statcvs.xml.output.ReportElement#saveResources(java.io.File)
		 */
		public void saveResources(File outputPath) throws IOException 
		{
			if (picFile != null) {
				FileHelper.copyResource(picFile, outputPath);	
			}
		}

	}

}
