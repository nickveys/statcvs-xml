package de.berlios.statcvs.xml.output;

import java.util.Iterator;
import java.util.List;

import net.sf.statcvs.model.CvsRevision;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.model.*;

/**
 * DirectorySizesChart
 * 
 * @author Tammo van Lessen
 * @version $id: $
 */
public class ModuleBuilder {
	
	private List modules; 
	private Module otherModule;

	/**
	 * Global Module Sizes Chart
	 * 
	 * @param filename
	 * @param title
	 */
	public ModuleBuilder(List modules, Iterator it) 
	{
		this.modules = modules;  
		this.otherModule = new Module(I18n.tr("Other"));
		modules.add(otherModule);

		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision)it.next();
			Module module = matches(rev);
			if (module != null) {			
				module.addRevision(rev);
			}
			else {
				otherModule.addRevision(rev);
			}
		}
	}
	
	public List getModules()
	{
		return modules;
	}

	private Module matches(CvsRevision rev)
	{
		for (Iterator it = modules.iterator(); it.hasNext();) {
			Module module = (Module)it.next();
			if (module.matches(rev)) {
				return module;
			}
		}
		return null;
	}

}
