package mdeServices.transformations;

import java.util.HashSet;
import java.util.Iterator;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.Generalization;
import mdeServices.metamodel.Package;
import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.options.Options;

/**
 * Transformation class that removes all multiple inheritance relationships
 * (all except one, the first one, are removed)
 * 
 * @version 0.1 May 2009
 * @author jcabot
 *
 */

public class T_MultipleToSingleInheritance extends Transformation {
	
	//Model where to apply the transformation
	protected StaticModel m;


	public T_MultipleToSingleInheritance(Project p, Options o) throws TransformationNotApplicable {
		super(p, o);
		this.m=(StaticModel) p.getStaticModel();
	}
	
	public void exec() throws TransformationNotApplicable
	{
	  try
	  {
		Iterator<Package> itP = m.getPackages().iterator();
		Iterator<Class> itCl;
		//Adding foreign key information
		while (itP.hasNext())
		{
			Package p=itP.next();
			itCl=p.getAllClasses().iterator();
			while (itCl.hasNext())
			{
				Class cl=itCl.next();
				Iterator<Generalization> itGen=cl.getSuperCl().iterator();
				if (itGen.hasNext()) //There is at least a supertype
				{
					Generalization gStays=itGen.next();//The one that stays
					if (itGen.hasNext()) //We have at least two so we remove all except the first one
					{	
						cl.setSuperEl( new HashSet<Generalization>(0,1));
						cl.addSuperType(gStays);
						while (itGen.hasNext())
						{
							Generalization delete=itGen.next();
							delete.getSuperType().removeSubType(delete);
						}
					}
				}
			}
		}
	  }catch(Exception e){throw new TransformationNotApplicable("Error when removing multiple inheritance");}

	}

}
