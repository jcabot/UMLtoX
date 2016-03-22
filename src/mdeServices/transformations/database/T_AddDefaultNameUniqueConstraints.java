package mdeServices.transformations.database;

import java.util.Iterator;

import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.stereotypes.S_Unique;
import mdeServices.options.Options;

import mdeServices.transformations.Transformation;
import mdeServices.transformations.TransformationNotApplicable;


/**
 * Transformation class for adding primary keys to the classes in the model
 * 
 * @version 0.1 Sep 2008
 * @author jcabot
 *
 */

public class T_AddDefaultNameUniqueConstraints extends Transformation{
	
	//Model where to apply the transformation
	protected StaticModel m;

	public T_AddDefaultNameUniqueConstraints(Project p, Options o) throws TransformationNotApplicable {
		super(p, o);
		this.m=(StaticModel) p.getStaticModel();
		this.o=o; 
	}
	
	public void exec() throws TransformationNotApplicable
	{
	  try
	  {
		Iterator<Class> itCl= m.getAllClasses().iterator();
		while (itCl.hasNext()) {
			Class cl= (Class) itCl.next();
			Iterator<S_Unique> itU=cl.getUniqueKeys().iterator();
			int i=0;
			while (itU.hasNext()) {
				i=i+1;
				S_Unique s= (S_Unique) itU.next();
			s.setConstraintName(o.getProperty("db.constraint.unique.prefix") + cl.getName() + o.getProperty("db.constraint.unique.suffix") + i );
			}	
		}	
	  }catch(Exception e){throw new TransformationNotApplicable("Error when adding default names to unique constraints");}
	}
}
