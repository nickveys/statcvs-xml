package de.berlios.statcvs.xml.model;

import java.util.Iterator;

import net.sf.statcvs.model.CvsContent;

/**
 * @module Steffen Pingel
 */
public class ForEachModule extends ForEachObject {

	private Module module;

	/**
	 * @param object
	 * @param id
	 */
	public ForEachModule(Module module) 
	{
		super(module, module.getName());

		this.module = module;
	}

	/**
	 *  @see de.berlios.statcvs.xml.output.ForEachObject#getRevisionIterator(net.sf.statcvs.model.CvsContent)
	 */
	public Iterator getRevisionIterator(CvsContent content) 
	{
		return module.getRevisions().iterator();
	}

}
