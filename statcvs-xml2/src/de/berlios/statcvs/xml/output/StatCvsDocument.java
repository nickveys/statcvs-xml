package de.berlios.statcvs.xml.output;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;

/**
 * Represents a document.
 *  
 * @author Steffen Pingel
 */
public class StatCvsDocument extends Document {

	private static int documentNumber = 0;
	private String filename;
	private String title;
	private ReportSettings settings;

	public StatCvsDocument(ReportSettings settings)
	{
		this.settings = settings;
		this.filename = StringHelper.escapeFilename(
			settings.getString("filename", "document_" + ++documentNumber)
			.replaceAll("%1", settings.getFilenameId()));
		this.title = settings.getString("title", "").replaceAll("%1", settings.getSubtitlePostfix());
		
		Element root = new Element("document");
		root.setAttribute("title", this.title);
		root.setAttribute("name", this.filename);
		setRootElement(root);
	}

	/**
	 * 
	 */
	public void saveResources(File outputPath) throws IOException
	{
		for (Iterator it = getRootElement().getChildren().iterator(); it.hasNext();) {
			Object o = it.next();
			if (o instanceof ReportElement) {
				((ReportElement)o).saveResources(outputPath);
			}
		}
	}

	public String getFilename()
	{
		return filename;
	}

	public ReportSettings getSettings()
	{
		return settings;
	}

	public String getTitle()
	{
		return title;
	}

}


