package de.berlios.statcvs.xml.output;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import net.sf.statcvs.model.CvsContent;
import de.berlios.statcvs.xml.util.FileHelper;

/**
 * Writes xml files to disk.
 *  
 * @author Steffen Pingel
 */
public class XDocRenderer {

	public static DocumentRenderer create(CvsContent content, ReportSettings settings) 
		throws IOException, TransformerException
	{
		StreamSource source = new StreamSource
			(FileHelper.getResource("resources/statcvs2xdoc.xsl").toString());
		Transformer transformer 
			= TransformerFactory.newInstance().newTransformer(source);
		transformer.setParameter("ext", ".html");
		
		return new XMLRenderer(transformer, settings.getOutputPath());
	}

}
