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
    
	$RCSfile: HTMLPage.java,v $ 
	Created on $Date: 2003-06-17 16:43:02 $ 
*/

package net.sf.statcvs.output;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.statcvs.Messages;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.util.FileUtils;

/**
 * @author anja
 */
public abstract class HTMLPage {
	private static Logger logger =
		Logger.getLogger("net.sf.statcvs.output.HTMLPage");

	protected static final int SPACE_COUNT = 4;
	private FileWriter htmlFileWriter;
	private CvsContent content;
	private String fileName;
	private String pageName;

	/**
	 * Method HTMLPage.
	 * @param content of the Page
	 */
	public HTMLPage(CvsContent content) {
		this.content = content;
	}

	protected void createPage() throws IOException {
		logger.info("Creating page '" + getPageName() + "'");
		initFileWriter();
		printHeader();
		printHeadline();
		printBody();
		printFooter();
	}

	protected void print(String printStream) throws IOException {
		htmlFileWriter.write(printStream);
	}

	private void initFileWriter() throws IOException {
		htmlFileWriter =
			new FileWriter(FileUtils.getFilenameWithDirectory(getFileName()));
	}

	/**
	 * Method printHeader.
	 */
	private void printHeader() throws IOException {
		print(
			"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\">\n"
				+ "<html>\n  <head>\n    <title>"
				+ Messages.getString("PROJECT_SHORTNAME") + " - " + getPageName() + "</title>\n"
				+ "    <meta http-equiv=\"Content-Type\" content=\"text/html; "
				+ "charset=ISO-8859-1\">\n"
				+ "    <meta name=\"Generator\" content=\"StatCvs "
				+ Messages.getString("CVS_VERSION_TAG") + "\">\n"
				+ "    <link rel=\"stylesheet\" href=\""
				+ ConfigurationOptions.getCssHandler().getLink()
				+ "\" type=\"text/css\">\n"
				+ "  </head>\n\n"
				+ "<body>\n");
	}

	private void printHeadline() throws IOException {
		print(h1(getPageName()));
	}

	//TODO: Remove this! It feels bad.
	protected void printH2(String h2) throws IOException {
		print(h2(h2));
	}

	private void printFooter() throws IOException {
		if (ConfigurationOptions.getShowCreditInformation()) {
			String img =
				"<img src=\"http://statcvs.sourceforge.net/statcvslogo.gif\" "
						+ "width=\"100\" height=\"30\" "
						+ "alt=\"\">";
			print("<div id=\"generatedby\">");
			printParagraph(
				a("http://statcvs.sf.net", img)
				+ br()
				+ Messages.getString("PAGE_GENERATED_BY")
				+ " "
				+ a(
					"http://statcvs.sf.net",
					Messages.getString("PROJECT_SHORTNAME"))
				+ " "
				+ Messages.getString("CVS_VERSION_TAG")
			);
			print("</div>\n");
		}
		print("</body>\n");
		print("</html>");
		htmlFileWriter.close();
	}

	//TODO: Remove this! It feels bad.
	protected void printParagraph(String paragraphContent) throws IOException {
		print(p(paragraphContent));
	}

	protected void printBackLink() throws IOException {
		print(p(a("index.html", Messages.getString("NAVIGATION_BACK"))));
	}

	protected String br() {
		return "<br>\n";
	}

	protected String p(String p) {
		return tag("p", p);
	}

	protected String h1(String h1) {
		return "\n\n" + tag("h1", h1) + "\n";
	}

	protected String h2(String h2) {
		return "\n" + tag("h2", h2) + "\n";
	}

	protected String strong(String b) {
		return tag("strong", b);
	}

	protected String a(String target, String html) {
		return "<a href=\"" + target + "\">" + html + "</a>";
	}

	protected String ul(String ul) {
		return tag("ul", ul);
	}

	protected String li(String li) {
		return tag("li", li);
	}

	/**
	 * Returns HTML code for a image tag
	 * @param image URL of the Image to be hyperlinked
	 * @param width width of the Image to be hyperlinked
	 * @param height height of the Image to be hyperlinked
	 * @return HTML code for the image tag
	 */
	protected String img(String image, int width, int height) {
		return "<img src=\"" + image + "\" width=\"" + width + "\" height=\"" + height
			+ "\" alt=\"\">";
	}

	protected String tag(String tag, String content) {
		return ("<" + tag + ">" + content + "</" + tag + ">\n");
	}

	protected abstract void printBody() throws IOException;

	/**
	 * Returns HTML code for a userPage link tag
	 * @param userName userName to be tagged
	 * @return HTML code for the userPage link tag
	 */
	public String getUserLink(String userName) {
		return a("user_" + userName + ".html", userName);
	}

	private String getSpaces(int count) {
		String result = "";
		for (int i = 0; i < count * SPACE_COUNT; i++) {
			result += "&nbsp;";
		}
		return result;
	}

	protected String deleteEndingSlash(String path) {
		if (path.endsWith("/")) {
			return path.substring(0, path.length() - 1);
		}
		return path;
	}

	protected String getFolderHtml(Directory dir, int currentDepth) {
		String name = dir.isRoot()
				? Messages.getString("NAVIGATION_ROOT")
				: dir.getName();
		String result = getSpaces(dir.getDepth() - currentDepth);
		if (dir.isEmpty()) {
			result += HTMLTagger.getIcon(HTMLOutput.DELETED_DIRECTORY_ICON);
		} else {
			result += HTMLTagger.getIcon(HTMLOutput.DIRECTORY_ICON);
		}
		String pageFilename = HTMLOutput.getDirectoryPageFilename(dir);
		result += " \n" + a(pageFilename, name);
		result += " \n(" + dir.getCurrentFileCount() + " ";
		result += Messages.getString("DIRECTORY_TREE_FILES") + ", ";
		result += dir.getCurrentLOC() + " ";
		result += Messages.getString("DIRECTORY_TREE_LINES") + ")" + br() + "\n";
		return result;
	}
	/**
	 * Returns the logger.
	 * @return Logger
	 */
	public static Logger getLogger() {
		return logger;
	}

	protected CvsContent getContent() {
		return content;
	}

	protected void setFileName(String fileName) {
		this.fileName = fileName;
	}

	protected String getFileName() {
		return fileName;
	}

	protected void setPageName(String pageName) {
		this.pageName = pageName;
	}

	protected String getPageName() {
		return pageName;
	}
}