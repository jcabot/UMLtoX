package mdeServices.transformations.database;

import java.util.Iterator;
import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.stereotypes.S_PrimaryKey;
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

public class T_AddDefaultNamePKConstraints extends Transformation{
	
	//Model where to apply the transformation
	protected StaticModel m;

	public T_AddDefaultNamePKConstraints(Project p, Options o) throws TransformationNotApplicable {
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
			if (cl.getPrimaryKey()!=null)
			{
				S_PrimaryKey s=cl.getPrimaryKey();
				s.setConstraintName(o.getProperty("db.constraint.pk.prefix") + cl.getName() + o.getProperty("db.constraint.pk.suffix"));
			}	
		}
	  }catch(Exception e){throw new TransformationNotApplicable("Error when adding default names to PKs");}

	}
}
