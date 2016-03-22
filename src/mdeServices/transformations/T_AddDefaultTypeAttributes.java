package mdeServices.transformations;

import java.util.Iterator;


import mdeServices.metamodel.Attribute;
import mdeServices.metamodel.DataType;
import mdeServices.metamodel.Package;
import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.options.Options;
import mdeServices.metamodel.ModelFactory;

/**
 * Transformation class that adds a default data type to all attributes with no datatype information
 * or with datatype of type "unspecified"
 * 
 * 
 * @version 0.1 Nov 2009
 * @author jcabot
 *
 */

public class T_AddDefaultTypeAttributes extends Transformation {
	
	//Model where to apply the transformation
	protected StaticModel m;


	public T_AddDefaultTypeAttributes(Project p, Options o) throws TransformationNotApplicable {
		super(p, o);
		this.m=(StaticModel) p.getStaticModel();
	}
	
	public void exec() throws TransformationNotApplicable
	{
	  try
	  {
		Iterator<Package> itP = m.getPackages().iterator();
		Iterator<Attribute> itAt;
		//Adding default data types
		while (itP.hasNext())
		{
			Package p=itP.next();
			itAt=p.getAllAttributes().iterator(); 
			while (itAt.hasNext())
			{
				Attribute at=itAt.next();
				if (!at.hasDataType() || ModelFactory.isUnspecifiedDataType(at.getType().getName())) 
				{
					String typeName= o.getProperty("db.default.datatype");
					DataType d=m.findDataType(typeName);
					at.setType(d);
				
				}
			}
		}
//	  }catch(TransformationNotApplicable t) {throw t;
	  }catch(Exception e)
		{ throw new TransformationNotApplicable("Error when adding default types to attributes");}
	}

}
