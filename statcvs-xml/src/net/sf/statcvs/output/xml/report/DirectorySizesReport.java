/*
 * statcvs-xml
 * TODO
 * Created on 27.06.2003
 *
 */
package net.sf.statcvs.output.xml.report;

import java.util.Iterator;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.output.xml.document.ModuleDocument;
import net.sf.statcvs.util.Formatter;
import net.sf.statcvs.util.IntegerMap;

import org.jdom.Element;

/**
 * DirectorySizesReport
 * 
 * @author Tammo van Lessen
 * @version $id: $
 */
public class DirectorySizesReport extends ReportElement {

	private CvsContent content;
	/**
	 *
	 */
	public DirectorySizesReport(CvsContent content) {
		super(I18n.tr("Directory Sizes"));
		this.content = content;
		createReport();
	}
	
	/**
	 * 
	 */
	private void createReport() {
		RevisionIterator revs = content.getRevisionIterator();
		IntegerMap dirChanges = new IntegerMap();
		IntegerMap dirLoC = new IntegerMap();

//		addContent(new Element("img")
//			.setAttribute("src", "module_sizes.png"));

		Element list = new Element("modules");		
		addContent(list);
		
		while (revs.hasNext()) {
			CvsRevision rev = revs.next();
			Directory dir = rev.getFile().getDirectory();
			dirChanges.addInt(dir, 1);
			dirLoC.addInt(dir, rev.getLineValue()); 			
		}
		Iterator it = dirLoC.iteratorSortedByValueReverse();
		while (it.hasNext()) {
			Directory key = (Directory)it.next();
			Element el = new Element("module");
			// TODO: Add link to module page
			el.setAttribute("url", ModuleDocument.getModulePageUrl(key));
			el.setAttribute("name", key.isRoot() ? "/" : key.getPath());
			el.setAttribute("changes", ""+dirChanges.get(key));
			el.setAttribute("lines", ""+dirLoC.get(key));
			el.setAttribute("linesPerChange", ""+
							Formatter.formatNumber((double)dirLoC.get(key) / dirChanges.get(key), 1));
			el.setAttribute("changesPercent", ""+Formatter.formatNumber(dirChanges.getPercent(key),2));
			el.setAttribute("linesPercent", ""+ Formatter.formatNumber(dirLoC.getPercent(key),2));
			list.addContent(el);			
		}
	}

}
