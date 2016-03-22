package mdeServices.transformations;

import java.util.Iterator;

import mdeServices.metamodel.Association;
import mdeServices.metamodel.AssociationClass;
import mdeServices.metamodel.AssociationEnd;
import mdeServices.metamodel.Package;
import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.VisibilityKind;
import mdeServices.options.Options;

/**
 * Transformation class for representing as association classes all associations from the model 
 * that need to be created as separated tables
 * 
 * @version 0.1 Sep 2008
 * @author jcabot
 *
 */

public class T_AssociationsMN_ToAssocClass extends Transformation {
	
	//Model where to apply the transformation
	protected StaticModel m;

	public T_AssociationsMN_ToAssocClass(Project p, Options o) throws TransformationNotApplicable {
		super(p, o);
		this.m=(StaticModel) p.getStaticModel();
		this.o=o;
	}
	
	public void exec() throws TransformationNotApplicable
	{
	  
		//users can decide whether to create explicitly association classes for M:N associations
		// depending on the value of property app.assoc.reify
	  if((new Boolean(o.getProperty("app.assoc.reify")).booleanValue()))
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
				if (as.isMN()) 
				{
					AssociationClass a = new AssociationClass(as.getName());
					a.setEnds(as.getEnds());
					Iterator<AssociationEnd> itAE=a.getEnds().iterator();
					while (itAE.hasNext()) itAE.next().setAss(a);
					//Default values for abstract and visibility properties
					a.setAbstract(false);
					a.setVisibility(VisibilityKind.V_PUBLIC);
					a.setComments(as.getComments());
					a.setName(as.getName());
					a.addRefines(as);
					p.addElement(a);
					p.removeElement(as);
				}
			}
		}
	  }	catch(Exception e){throw new TransformationNotApplicable("Error when converting MN associations to association classes");}
	  }
	}

}
