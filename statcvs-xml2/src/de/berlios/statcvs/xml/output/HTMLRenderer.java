package de.berlios.statcvs.xml.output;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamSource;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.util.FileUtils;
import de.berlios.statcvs.xml.util.FileHelper;


/**
 * HTMLRenderer
 * 
 * @author Tammo van Lessen
 */
public class HTMLRenderer extends XMLRenderer {

	private static Logger logger
		= Logger.getLogger("net.sf.statcvs.output.XMLRenderer");

	public HTMLRenderer(Transformer transformer, File outputPath) 
	{
		super(transformer, outputPath);

		setExtension(".html");

		XMLOutputter xout = new HTMLOutputter();
		xout.setEncoding("ISO-8859-1");
		xout.setOmitDeclaration(true);
		xout.setOmitEncoding(true);
		setOutputter(xout);
	}

	/**
	 * Copies the required resources.
	 */
	public void postRender()
	{
		super.postRender();
		
		FileHelper.copyResource("resources/statcvs.css", getOutputPath());
	}

	/**
	 * Invoked by Main.
	 */
	public static DocumentRenderer create(CvsContent content, ReportSettings settings)
		throws IOException, TransformerConfigurationException, TransformerFactoryConfigurationError 
	{
		StreamSource source = new StreamSource
			(FileHelper.getResource("resources/statcvs2html.xsl").toString());
		Transformer transformer;

		transformer	= TransformerFactory.newInstance().newTransformer(source);
			
		// set stylesheet parameters
		transformer.setParameter("ext", ".html");
		String filename = settings.getString("customCss");
		if (filename != null) {
			transformer.setParameter
				("customCss", 
				 FileUtils.getFilenameWithoutPath(filename));
		}
		
		return new HTMLRenderer(transformer, settings.getOutputPath());		
	}

}
