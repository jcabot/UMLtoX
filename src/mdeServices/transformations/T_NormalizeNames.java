package mdeServices.transformations;

import java.util.Iterator;

import mdeServices.metamodel.Association;
import mdeServices.metamodel.AssociationClass;
import mdeServices.metamodel.AssociationEnd;
import mdeServices.metamodel.Attribute;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.DataType;
import mdeServices.metamodel.ModelFactory;
import mdeServices.metamodel.Operation;
import mdeServices.metamodel.Package;
import mdeServices.metamodel.Parameter;
import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.options.Options;

/**
 * Transformation class that normalizes all names of the elements by removing all strange characters
 * 
 * @version 0.1 Sep 2008
 * @author jcabot
 *
 */

public class T_NormalizeNames extends Transformation {
	
	//Model where to apply the transformation
	protected StaticModel m;


	public T_NormalizeNames(Project p, Options o) throws TransformationNotApplicable {
		super(p, o);
		this.m=(StaticModel) p.getStaticModel();
	}
	
	public void exec() throws TransformationNotApplicable
	{
	  try
	  {
		if(m.hasName()) m.setName(ModelFactory.normalize(m.getName()));
		Iterator<Package> itP = m.getPackages().iterator();
		Iterator<Association> itAs;
		//Adding foreign key information
		while (itP.hasNext())
		{
			Package p=itP.next();
			if (p.hasName()) p.setName(ModelFactory.normalize(p.getName()));
			itAs=p.getAllAssociationsNoAssCl().iterator();
			while (itAs.hasNext())
			{
				Association as=itAs.next();
				if (as.hasName()) as.setName(ModelFactory.normalize(as.getName()));
				Iterator<AssociationEnd> itE = as.getEnds().iterator();
				while (itE.hasNext())
				{
					AssociationEnd ae=itE.next();
					if (ae.getName()!=null) ae.setName(ModelFactory.normalize(ae.getName()));
				}
			}
			Iterator<Class> itCl=p.getAllClasses().iterator();
			while (itCl.hasNext()) {
				Class cl = (Class) itCl.next();
				if (cl.hasName()) cl.setName(ModelFactory.normalize(cl.getName()));
				Iterator<Attribute> itAt = cl.getAtt().iterator();
				while (itAt.hasNext())
				{
					Attribute at=itAt.next();
					if (at.getName()!=null) at.setName(ModelFactory.normalize(at.getName()));
				}
				Iterator<Operation> itOp= cl.getOps().iterator();
				while (itOp.hasNext())
				{
					Operation op=itOp.next();
					if (op.getName()!=null) op.setName(ModelFactory.normalize(op.getName()));
					//Normalizing parameter names
					Iterator<Parameter> itParams= op.getParams().iterator();
					while (itParams.hasNext()) {
						Parameter parameter = (Parameter) itParams.next();
						if (parameter.hasName()) parameter.setName(ModelFactory.normalize(parameter.getName()));
					}
				}
				if (cl instanceof AssociationClass)
				{
					AssociationClass asCl=(AssociationClass) cl;
					Iterator<AssociationEnd> itE = asCl.getEnds().iterator();
					while (itE.hasNext())
					{
						AssociationEnd ae=itE.next();
						if (ae.getName()!=null) ae.setName(ModelFactory.normalize(ae.getName()));
					}
				}
			}
			
		}
		Iterator<DataType> itDT = m.getDataTypes().iterator();
		while (itDT.hasNext())
		{
			DataType d=itDT.next();
			if (d.hasName()) d.setName(ModelFactory.normalize(d.getName()));
		}
	  }catch(Exception e){throw new TransformationNotApplicable("Error when normalizing names of model elements");}

	}

}
