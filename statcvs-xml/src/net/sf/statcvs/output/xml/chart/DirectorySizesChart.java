/*
 * statcvs-xml
 * TODO
 * Created on 27.06.2003
 *
 */
package net.sf.statcvs.output.xml.chart;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.statcvs.I18n;
import net.sf.statcvs.Messages;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.RevisionFilterIterator;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.model.RevisionIteratorSummary;
import net.sf.statcvs.model.UserPredicate;
import net.sf.statcvs.output.xml.XMLSuite;
import net.sf.statcvs.util.IntegerMap;
import net.sf.statcvs.util.OutputUtils;

/**
 * DirectorySizesChart
 * 
 * @author Tammo van Lessen
 * @version $id: $
 */
public class DirectorySizesChart extends AbstractPieChart {
	
	private static final int SLICE_MIN_PERCENT = 5;

	/**
	 * Global Module Sizes Chart
	 * 
	 * @param filename
	 * @param title
	 */
	public DirectorySizesChart(CvsContent content) {
		super("module_sizes.png", I18n.tr("Module Sizes"));
		
		List directories = new ArrayList(content.getDirectories()); 
		Collections.sort(directories);

		IntegerMap dirSizes = new IntegerMap();
		Iterator it = directories.iterator();
		while (it.hasNext()) {
			Directory dir = (Directory) it.next();
			RevisionIterator revsByModule = dir.getRevisionIterator();
			Set files = new RevisionIteratorSummary(revsByModule).getAllFiles();
			Iterator fileIt = files.iterator();
			while (fileIt.hasNext()) {
				CvsFile element = (CvsFile) fileIt.next();
				dirSizes.addInt(dir, element.getCurrentLinesOfCode());
			}
			
		}

		setValues(dirSizes);
	}

	/**
	 * User specific Module Sizes Chart
	 * 
	 * @param filename
	 * @param title
	 */
	public DirectorySizesChart(Author author) {
		super("module_sizes_" 
			+ XMLSuite.escapeAuthorName(author.getName()) + ".png",
			I18n.tr("Module Sizes"));
		
		List directories = new ArrayList(author.getDirectories()); 
		Collections.sort(directories);

		IntegerMap dirSizes = new IntegerMap();
		Iterator it = directories.iterator();
		while (it.hasNext()) {
			Directory dir = (Directory) it.next();
			RevisionIterator revsByModule = dir.getRevisionIterator();
				RevisionIterator filteredByUser =
						new RevisionFilterIterator(revsByModule,
								new UserPredicate(author));
				RevisionIteratorSummary summary = 
						new RevisionIteratorSummary(filteredByUser);
				dirSizes.addInt(dir, summary.getLineValue());
		}

		setValues(dirSizes);
	}

	/**
	 * 
	 */
	private void setValues(IntegerMap dirSizes) {

		int otherSum = 0;
		List colors = new ArrayList();
		List outlines = new ArrayList();
		Iterator it = dirSizes.iteratorSortedByValue();
		while (it.hasNext()) {
			Directory dir = (Directory) it.next();
			if (dirSizes.getPercent(dir) >= SLICE_MIN_PERCENT) {
				String dirName = dir.isRoot() ? "/" : dir.getPath();
				dataset.setValue(dirName, dirSizes.getInteger(dir));
				colors.add(OutputUtils.getStringColor(dirName));
				outlines.add(Color.BLACK);
			} else {
				otherSum += dirSizes.get(dir);
			}
		}
		dataset.setValue(Messages.getString("PIE_MODSIZE_OTHER"), new Integer(otherSum));
		colors.add(Color.GRAY);
		outlines.add(Color.BLACK);

		
	}

}
