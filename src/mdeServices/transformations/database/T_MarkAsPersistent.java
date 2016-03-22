package mdeServices.transformations.database;

import java.util.Iterator;
import java.util.Vector;

import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.Package;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.stereotypes.S_Persistent;
import mdeServices.options.Options;

import mdeServices.transformations.Transformation;
import mdeServices.transformations.TransformationNotApplicable;

/**
 * Transformation class for marking with the persistent stereotype all classes in the model
 * 
 * @version 0.1 Sep 2008
 * @author jcabot
 *
 */

public class T_MarkAsPersistent extends Transformation{
	
	//Model where to apply the transformation
	protected StaticModel m;
	//Names of the class that should not be marked as persistent
	protected Vector<String> notPersistent;

	
	public T_MarkAsPersistent(Project p, Options o) throws TransformationNotApplicable {
		super(p, o);
		this.m=(StaticModel) p.getStaticModel();
		this.o=o;
			
	}
	
	public void exec() throws TransformationNotApplicable
	{
	  try
	  {
		Iterator<Package> itP = m.getPackages().iterator();
		Iterator<Class> itCl;
		while (itP.hasNext())
		{
			Package p=itP.next();
			itCl=p.getAllClasses().iterator();
			while (itCl.hasNext())
			{
				Class cl=itCl.next();
				if (cl.getPersistent()==null)
				{
					S_Persistent s = new S_Persistent();
					s.setTableName(o.getProperty("db.table.name.prefix") +  cl.getName() + o.getProperty("db.table.name.suffix"));
					cl.setPersistent(s);
					cl.addRefines(cl); //The table is a refinement of itself
				}
			}
		}
	  }catch(Exception e){throw new TransformationNotApplicable("Error when marking classes as persistent");}
	}
	
	private boolean inNotPersistent(String name)
	{
	  boolean found=false;
	  if (notPersistent==null) return false;	  
	  Iterator<String> it=notPersistent.iterator();
	  while (it.hasNext() && !found)
	  {
		 if (it.next().equals(name)) found=true;
	  }
	return found;
		
	}
	
	
}
