package mdeServices.transformations;

import java.util.Iterator;

import mdeServices.metamodel.Association;
import mdeServices.metamodel.AssociationEnd;
import mdeServices.metamodel.Package;
import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.options.Options;

/**
 * Transformation class that adds as a name for unnamed associations 
 * the concatenation of the names of the participants
 * 
 * @version 0.1 Sep 2008
 * @author jcabot
 *
 */

public class T_AddDefaultNameAssoc extends Transformation {
	
	//Model where to apply the transformation
	protected StaticModel m;


	public T_AddDefaultNameAssoc(Project p, Options o) throws TransformationNotApplicable {
		super(p, o);
		this.m=(StaticModel) p.getStaticModel();
	}
	
	public void exec() throws TransformationNotApplicable
	{
	  try
	  {
		Iterator<Package> itP = m.getPackages().iterator();
		Iterator<Association> itAs;
		//Adding foreign key information
		while (itP.hasNext())
		{
			Package p=itP.next();
			itAs=p.getAllAssociationsNoAssCl().iterator();
			while (itAs.hasNext())
			{
				Association as=itAs.next();
				if (!as.hasName()) 
				{
					String name="";
					Iterator<AssociationEnd> itE = as.getEnds().iterator();
					while (itE.hasNext())
					{
						AssociationEnd ae=itE.next();
						if (ae.getName()!=null) name=name.concat(ae.getName());
					}
					String first=name.substring(0, 1).toUpperCase();
					name=first.concat(name.substring(1));
					as.setName(name);
				}
			}
		}
	  }catch(Exception e)
		{ throw new TransformationNotApplicable("Error when adding default names to associations");}
	}

}
