package mdeServices.transformations.database;

import java.util.Iterator;

import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.Class;
import mdeServices.options.Options;

import mdeServices.transformations.Transformation;
import mdeServices.transformations.TransformationNotApplicable;
import mdeServices.metamodel.stereotypes.S_ForeignKey;

/**
 * Transformation class for adding primary keys to the classes in the model
 * 
 * @version 0.1 Sep 2008
 * @author jcabot
 *
 */

public class T_AddDefaultNameFKConstraints extends Transformation{
	
	//Model where to apply the transformation
	protected StaticModel m;

	public T_AddDefaultNameFKConstraints(Project p, Options o) throws TransformationNotApplicable {
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
			Iterator<S_ForeignKey> itFK=cl.getForeignKeys().iterator();
			while (itFK.hasNext()) {
				S_ForeignKey s= (S_ForeignKey) itFK.next();
				s.setConstraintName(o.getProperty("db.constraint.fk.prefix") + cl.getName()+ o.getProperty("db.constraint.fk.infix")+ s.getReferencedClass().getName()+ "_"+ s.getOwnAtt().getName() + o.getProperty("db.constraint.fk.suffix") );
			}	
		}
	  }catch(Exception e){throw new TransformationNotApplicable("Error when adding default names to FKs");}

	}
}
