/*
 * statcvs-xml
 * TODO
 * Created on 27.06.2003
 *
 */
package net.sf.statcvs.output.xml.chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.RevisionFilterIterator;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.model.RevisionIteratorSummary;
import net.sf.statcvs.model.UserPredicate;
import net.sf.statcvs.output.xml.AuthorDocument;
import net.sf.statcvs.util.IntegerMap;

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
		placeTitle();
	}

	/**
	 * User specific Module Sizes Chart
	 * 
	 * @param filename
	 * @param title
	 */
	public DirectorySizesChart(Author author) {
		super("module_sizes_" 
			+ AuthorDocument.escapeAuthorName(author.getName()) + ".png",
			I18n.tr("Module Sizes for {0}",author.getName()));
		
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
		placeTitle();
	}

	/**
	 * 
	 */
	private void setValues(IntegerMap dirSizes) {

		int otherSum = 0;
		Iterator it = dirSizes.iteratorSortedByValue();
		while (it.hasNext()) {
			Directory dir = (Directory) it.next();
			if (dirSizes.getPercent(dir) >= SLICE_MIN_PERCENT) {
				String dirName = dir.isRoot() ? "/" : dir.getPath();
				dataset.setValue(dirName, dirSizes.getInteger(dir));
			} else {
				otherSum += dirSizes.get(dir);
			}
		}
		dataset.setValue(I18n.tr("(others)"), new Integer(otherSum));
	}

}
