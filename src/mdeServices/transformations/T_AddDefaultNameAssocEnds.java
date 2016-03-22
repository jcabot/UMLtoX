package mdeServices.transformations;

import java.util.Iterator;

import mdeServices.metamodel.Association;
import mdeServices.metamodel.AssociationClass;
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

public class T_AddDefaultNameAssocEnds extends Transformation {
	
	//Model where to apply the transformation
	protected StaticModel m;
	
	public T_AddDefaultNameAssocEnds(Project p, Options o) throws TransformationNotApplicable {
		super(p, o);
		this.m=(StaticModel) p.getStaticModel();
	}
	
	public void exec() throws TransformationNotApplicable
	{
		
	  try
	  {
		Iterator<Package> itP = m.getPackages().iterator();
		Iterator<Association> itAs;
		Iterator<AssociationClass> itCl;
		while (itP.hasNext())
		{
			Package p=itP.next();
			itAs=p.getAllAssociationsNoAssCl().iterator();
			while (itAs.hasNext())
			{
				Association as=itAs.next();
				Iterator<AssociationEnd> itAE= as.getEnds().iterator();
				while (itAE.hasNext())
				{
					AssociationEnd ae=itAE.next();
					if (!ae.hasName()) 
					{
						ae.setName(ae.getSource().getName().toLowerCase());
					}
				}
			}
			itCl=p.getAllAssociationClasses().iterator();
			while (itCl.hasNext())
			{
				AssociationClass ascl=itCl.next();
				Iterator<AssociationEnd> itAE= ascl.getEnds().iterator();
				while (itAE.hasNext())
				{
					AssociationEnd ae=itAE.next();
					if (!ae.hasName()) 
					{
						ae.setName(ae.getSource().getName().toLowerCase());
					}
				}
			}
		}
	  }catch(Exception e)
	   { throw new TransformationNotApplicable("Error when adding default names to the association ends");}
	}
	
}
