package de.berlios.statcvs.xml.output;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Logger;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.Directory;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.JDOMParseException;
import org.jdom.input.SAXBuilder;

/**
 * Reads the document suite configuration from an XML file and create the reports.
 * 
 * @author Steffen Pingel
 */
public class DocumentSuite {

	private static Logger logger = Logger.getLogger(DocumentSuite.class.getName());
	
	private CvsContent content;
	private Document suite;
	private ReportSettings defaultSettings = new ReportSettings();

	/**
	 * 
	 */
	public DocumentSuite(URL url, CvsContent content) throws IOException
	{
		this.content = content;
		
		try {
			SAXBuilder builder = new SAXBuilder();
			suite = builder.build(url);
		}
		catch (JDOMException e) {
			throw new IOException(e.getMessage());
		}

	}

	public StatCvsDocument createDocument(Element root, ReportSettings settings)
	{
		ReportSettings documentSettings = new ReportSettings(settings);
		
		// generate reports
		StatCvsDocument document = new StatCvsDocument(readAttributes(documentSettings, root));
		for (Iterator it = root.getChildren().iterator(); it.hasNext();) {
			Element element = (Element)it.next();
			if ("settings".equals(element.getName())) {
				readSettings(documentSettings, element);
			}
			else if ("report".equals(element.getName())) {
				ReportElement report = createReport(document, element, documentSettings);
				if (report != null) {
					document.getRootElement().addContent(report);
				}
			}
		}
		
		return document;
	}

	/**
	 * @param element
	 */
	private ReportElement createReport(StatCvsDocument document, Element root, ReportSettings documentSettings) 
	{
		ReportSettings reportSettings = readAttributes(documentSettings, root);
//		for (Iterator it = root.getChildren().iterator(); it.hasNext();) {
//			Element element = (Element)it.next();
//			if ("settings".equals(element.getName())) {
//				readSettings(reportSettings, element);
//			}
//		}
		
		String className = root.getAttributeValue("class");
		if (className != null) {
			if (className.indexOf(".") == -1) {
				className = "de.berlios.statcvs.xml.report." + className;
			}  

			try {
				Class c = Class.forName(className);
				Method m = c.getMethod("generate", new Class[] { CvsContent.class, ReportSettings.class });
				ReportElement report = (ReportElement)m.invoke(null, new Object[] { content, reportSettings });
				return report;
			}
			catch (Exception e) {
				logger.warning("Could not generate report: " + e);
				e.printStackTrace();
			}
		}
		
		return null;
	}

	public void generate(DocumentRenderer renderer) 
		throws IOException 
	{
		// generate documents
		for (Iterator it = suite.getRootElement().getChildren().iterator(); it.hasNext();) {
			Element element = (Element)it.next();
			if ("settings".equals(element.getName())) {
				readSettings(defaultSettings, element);
			}
			else if ("document".equals(element.getName())) {
				renderDocument(renderer, element);
			}
		}

		renderer.postRender();
	}

	/**
	 * @param renderer
	 * @param element
	 */
	private void renderDocument(DocumentRenderer renderer, Element element) throws IOException
	{
		String value = element.getAttributeValue("foreach");
		if (value == null) {
			StatCvsDocument document = createDocument(element, defaultSettings);
			renderer.render(document);
		}
		else if ("author".equals(value)) {
			for (Iterator i = content.getAuthors().iterator(); i.hasNext();) {
				Author author = (Author)i.next();
				ReportSettings settings = new ReportSettings(defaultSettings);
				settings.put("foreach", author);
				renderer.render(createDocument(element, settings));
			}
		}
		else if ("directory".equals(value)) {
			for (Iterator i = content.getDirectories().iterator(); i.hasNext();) {
				Directory dir = (Directory)i.next();
				if (!dir.isEmpty()) {
					ReportSettings settings = new ReportSettings(defaultSettings);
					settings.put("foreach", dir);
					renderer.render(createDocument(element, settings));
				}
			}
		}
		else {
			throw new IOException("Invalid foreach value");	
		}
	}

	/**
	 * Creates a new ReportSettings object that inherits from parentSettings. 
	 * All attributes of root are added as key value pairs to the created ReportSettings 
	 * object and the object is returned.
	 */
	private ReportSettings readAttributes(ReportSettings parentSettings, Element root) 
	{
		ReportSettings settings = new ReportSettings(parentSettings);
		for (Iterator it = root.getAttributes().iterator(); it.hasNext();) {
			Attribute setting = (Attribute)it.next();
			settings.put(setting.getName(), setting.getValue());
		}
		return settings;
	}

	/**
	 * Reads all setting elements located under root.
	 */ 	
	public void readSettings(ReportSettings properties, Element root)
	{
		// add childern as key value pairs
		for (Iterator it = root.getChildren().iterator(); it.hasNext();) {
			Element setting = (Element)it.next();
			properties.put(setting.getName(), setting.getText());
		}
	}
	
}
