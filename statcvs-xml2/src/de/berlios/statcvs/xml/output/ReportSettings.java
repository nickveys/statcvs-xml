package de.berlios.statcvs.xml.output;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.regex.Pattern;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.SymbolicName;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.util.ScriptHelper;

/**
 * @author Steffen Pingel
 */
public class ReportSettings extends Hashtable {

	public static class ExpressionPredicate implements Predicate {
		
		private JexlContext context;
		private Expression expression; 
		
		public ExpressionPredicate(String expressionSource) throws Exception
		{
			expression = ExpressionFactory.createExpression(expressionSource);
			context = JexlHelper.createContext();
			context.getVars().put("util", new ScriptHelper());
		}

		public boolean matches(Object o)
		{
			if (o instanceof CvsRevision) {
				context.getVars().put("rev", o);
			}

			try {
				Object res = expression.evaluate(context);
				return (res instanceof Boolean) ? ((Boolean)res).booleanValue() : false;
			}
			catch (Exception e) {
				return false;
			}
		}
		
	}

	public static class FilteredIterator implements Iterator
	{
		private Iterator it;
		private Object lookAhead;
		private Predicate predicate;

		public FilteredIterator(Iterator it, Predicate predicate)
		{
			this.it = it;
			this.predicate = predicate;
		}

		/**
		 *  @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() 
		{		
			if (lookAhead != null) {
				return true;
			}
			
			while (it.hasNext()) {
				lookAhead = it.next();
				if (predicate.matches(lookAhead)) {
					return true;
				}
			}
			lookAhead = null;
			return false;
		}

		/**
		 *  @see java.util.Iterator#next()
		 */
		public Object next() 
		{
			if (lookAhead != null) {
				Object current = lookAhead;
				lookAhead = null;
				return current;
			}
			
			while (it.hasNext()) {
				Object o = it.next();
				if (predicate.matches(o)) {
					return o;
				}
			}
			throw new NoSuchElementException();
		}

		/**
		 *  @see java.util.Iterator#remove()
		 */
		public void remove() 
		{
			throw new UnsupportedOperationException();
		}
		
	}

	public static interface Predicate
	{

		boolean matches(Object o);
		 
	}

	protected ReportSettings defaults;

	public ReportSettings()
	{
	}

	/**
	 * @param defaults
	 */
	public ReportSettings(ReportSettings defaults) 
	{
		this.defaults = defaults;
	}

	public Object get(Object key)
	{
		return this.get(key, null);
	}

	public Object get(Object key, Object defaultValue)
	{
		Object o = super.get(key);
		return (o != null) ? o : (defaults != null) ? defaults.get(key, defaultValue) : defaultValue;
	}

	/**
	 * @param content
	 */
	public Iterator getDirectoryIterator(CvsContent content) 
	{
		ForEachObject o = getForEach();
		Iterator it = (o != null) 
		   ? o.getDirectoryIterator(content) 
		   : content.getDirectories().iterator(); 
		return getFilterIterator("directoryFilter", it);
	}

	public Iterator getFileIterator(CvsContent content)
	{
		ForEachObject o = getForEach();
		Iterator it = (o != null) 
		   ? o.getFileIterator(content) 
		   : content.getFiles().iterator(); 
		return getFilterIterator("fileFilter", it);
	}

	/**
	 * 
	 */
	public String getFilenameId() 
	{
		String postfix = getForeachId();
		return (postfix == null) ? "" : "_" + postfix;
	}

	public Iterator getFilterIterator(String key, Iterator it)
	{
		String expression = getString(key, null);
		if (expression != null) {
			try {
				return new FilteredIterator(it, new ExpressionPredicate(expression));
			}	
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return it;  
	}
	
	public ForEachObject getForEach()
	{
		return (ForEachObject)get("_foreach");
	}

	public ForAll getForAll(CvsContent content)
	{
		return (ForAll)ForAll.create(content, this, getString("forall", null));
	}

	public Object getForEachObject()
	{
		ForEachObject o = getForEach();
		return (o != null) ? o.getObject() : null; 
	}

	/**
	 * 
	 */
	public String getForeachId() 
	{
		ForEachObject o = getForEach();
		return (o != null) ? o.getID() : "";
	}

	public Iterator getForEachIterator(CvsContent content)
	{
		return getForAll(content).getObjects();
	}

	/**
	 * @param string
	 * @param i
	 * @return
	 */
	public int getInt(Object key, int defaultValue) 
	{
		try {
			return Integer.parseInt(getString(key, defaultValue + ""));
		}
		catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public int getLimit()
	{
		return getLimit(20);
	}

	public int getLimit(int defaultValue)
	{
		return getInt("limit", defaultValue);
	}

	public List getModules(CvsContent content)
	{
		LinkedList modules = new LinkedList();

		Object o = get("modules", null);
		if (o instanceof Map) {
			Map map = (Map)o;
			for (Iterator it = map.keySet().iterator(); it.hasNext();) {
				Object key = it.next();
				modules.add(new Module(key.toString(), map.get(key).toString()));
			}
		}
		else {
			SortedSet directories = content.getDirectories();
			for (Iterator it = directories.iterator(); it.hasNext();) {
				Directory dir = (Directory)it.next();
				if (!dir.isRoot()) {
					modules.add(new Module(dir));
				}
			}
		}
		return modules;
	}

	public String getProjectName()
	{
		return this.getString("projectName", "");
	}
		
	public Iterator getRevisionIterator(CvsContent content)
	{
		ForEachObject o = getForEach();
		Iterator it = (o != null) 
		   ? o.getRevisionIterator(content) 
		   : content.getRevisions().iterator(); 
		return getFilterIterator("revisionFilter", it);
	}
	
	/**
	 * @param string
	 * @param string2
	 * @return
	 */
	public String getString(Object key, String defaultValue)
	{
		return (String)this.get(key, defaultValue);
	}

	/**
	 * 
	 */
	public String getSubtitlePostfix() 
	{
		String postfix = getForeachId();
		return (postfix == null) ? "" : I18n.tr(" for {0}", postfix);
	}

	public Iterator getSymbolicNameIterator(CvsContent content)
	{
		String regexp = getString("tags", null);

		if (regexp == null) {
			return content.getSymbolicNames().iterator();
		}
		
		final Pattern pattern = Pattern.compile(regexp);
		Predicate predicate = new Predicate()
		{
			public boolean matches(Object o)
			{
				SymbolicName tag = (SymbolicName)o;
				return pattern.matcher(tag.getName()).matches();
			}
		};
		return new FilteredIterator(content.getSymbolicNames().iterator(), predicate);
	}

	public void setForEach(ForEachObject object)
	{
		if (object == null) {
			this.remove("_foreach");
		}
		else {
			this.put("_foreach", object);
		}
	}

}
