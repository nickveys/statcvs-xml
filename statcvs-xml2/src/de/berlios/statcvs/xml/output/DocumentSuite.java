package de.berlios.statcvs.xml.output;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.Directory;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.berlios.statcvs.xml.model.*;
import de.berlios.statcvs.xml.model.AuthorGrouper;
import de.berlios.statcvs.xml.model.DayGrouper;
import de.berlios.statcvs.xml.model.DirectoryGrouper;
import de.berlios.statcvs.xml.model.FileGrouper;
import de.berlios.statcvs.xml.model.HourGrouper;
import de.berlios.statcvs.xml.model.ModuleGrouper;

/**
 * Reads the document suite configuration from an XML file and create the reports.
 * 
 * @author Steffen Pingel
 */
public class DocumentSuite {

	private static Map filenameByDirectoryPath = new Hashtable();

	private static Map filenameByAuthorName = new Hashtable();

	private static Map documentTitleByFilename = new LinkedHashMap();

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
		StatCvsDocument document = new StatCvsDocument(readAttributes(settings, root));

		// generate reports
		ReportSettings documentSettings = new ReportSettings(settings);
		for (Iterator it = root.getChildren().iterator(); it.hasNext();) {
			Element element = (Element)it.next();
			if ("settings".equals(element.getName())) {
				documentSettings.load(element);
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
		
		String value = reportSettings.getString("groupby", null);
		if ("author".equals(value)) {
			reportSettings.setGrouper(new AuthorGrouper());
		}
		else if ("day".equals(value)) {
			reportSettings.setGrouper(new DayGrouper());
		}
		else if ("directory".equals(value)) {
			reportSettings.setGrouper(new DirectoryGrouper());
		}
		else if ("file".equals(value)) {
			reportSettings.setGrouper(new FileGrouper());
		}
		else if ("hour".equals(value)) {
			reportSettings.setGrouper(new HourGrouper());
		}
		else if ("module".equals(value)) {
			reportSettings.setGrouper(new ModuleGrouper(reportSettings.getModules(content)));
		}
		
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

	public void generate(DocumentRenderer renderer, ReportSettings defaultSettings) 
		throws IOException 
	{
		this.defaultSettings = defaultSettings;
			
		// generate documents
		for (Iterator it = suite.getRootElement().getChildren().iterator(); it.hasNext();) {
			Element element = (Element)it.next();
			if ("settings".equals(element.getName())) {
				defaultSettings.load(element);
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
			documentTitleByFilename.put(document.getFilename(), document.getTitle());
			renderer.render(document);
		}
		else if ("author".equals(value)) {
			for (Iterator i = content.getAuthors().iterator(); i.hasNext();) {
				Author author = (Author)i.next();
				ReportSettings settings = new ReportSettings(defaultSettings);
				settings.setForEach(new ForEachAuthor(author));
				StatCvsDocument doc = createDocument(element, settings);
				filenameByAuthorName.put(author.getName(), doc.getFilename());
				renderer.render(doc);
			}
		}
		else if ("directory".equals(value)) {
			for (Iterator i = content.getDirectories().iterator(); i.hasNext();) {
				Directory dir = (Directory)i.next();
				if (!dir.isEmpty()) {
					ReportSettings settings = new ReportSettings(defaultSettings);
					settings.setForEach(new ForEachDirectory(dir));
					StatCvsDocument doc = createDocument(element, settings);
					filenameByDirectoryPath.put(dir.getPath(), doc.getFilename());
					renderer.render(doc);
				}
			}
		}
		else if ("module".equals(value)) {
			ModuleBuilder builder = new ModuleBuilder(defaultSettings.getModules(content), content.getRevisions().iterator());
			for (Iterator i = builder.getModules().iterator(); i.hasNext();) {
				Module module = (Module)i.next();
				ReportSettings settings = new ReportSettings(defaultSettings);
				settings.setForEach(new ForEachModule(module));
				StatCvsDocument doc = createDocument(element, settings);
				renderer.render(doc);
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
			if (!setting.getName().startsWith(ReportSettings.PRIVATE_SETTING_PREFIX)) {
				settings.put(setting.getName(), setting.getValue());
			}
		}
		return settings;
	}
	
	public static String getAuthorFilename(String name)
	{
		return (String)filenameByAuthorName.get(name);
	}

	public static String getDirectoryFilename(String path)
	{
		return (String)filenameByDirectoryPath.get(path);
	}

	public static Map getDocuments()
	{
		return documentTitleByFilename;
	}
	
}
