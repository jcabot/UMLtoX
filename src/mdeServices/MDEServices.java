/**
 * 
 */
package mdeServices;

import java.util.Iterator;

import mdeServices.options.LangManager;
import mdeServices.metamodel.Attribute;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.Project;
import mdeServices.options.Options;
import mdeServices.system.commandLine.Arguments;

/**
 *  Generic Class for grouping the MDEServices provided by the applications
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */
public class MDEServices {
	protected Project p;
	protected Options o;
	protected LangManager l;
	
	/**
	 * @param m
	 */
	public void  initialize(Project p, Options o, LangManager l) {
		this.p= p;
		this.o= o;
		this.l=l;
	}
	
	public void setOptions(Options _o)
	{
		o=_o;
	}
	public void  initialize(String userName) {
		o = new Options(userName);
		l = new LangManager(o.getProperties());
	}

	/**
	 * @param m
	 */
	public void  initialize(Options o, LangManager l) {
		this.o= o;
		this.l=l;
	}
	
	/**
	 * @return the m
	 */
	public Project getProject() {
		return p;
	}

	/**
	 * @param m the m to set
	 */
	public void setProject(Project p) {
		this.p = p;
	}
	
	/* Returns the class with that name */
	/*public Class getClassByName(String s)
	{
		Iterator<Class> itCl=p.getStaticModel().getAllClasses().iterator();
		while (itCl.hasNext()) {
			Class class1 = (Class) itCl.next();
			if (class1.getName().equals(s)) return class1;
		}
		return null;
	}*/
	
	/*public void setPrimaryKeyClass(String cl, String at)
	{
	   Class c= getClassByName(cl);
	   if (c!=null)
	   {
		 Attribute a= c.getAttributeByName(at);
		 c.setPrimaryKey(a);   
	   }
	}*/
	
	
	

}
