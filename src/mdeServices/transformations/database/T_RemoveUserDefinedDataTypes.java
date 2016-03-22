package mdeServices.transformations.database;

import java.util.Iterator;

import mdeServices.metamodel.Attribute;
import mdeServices.metamodel.DT_LongInteger;
import mdeServices.metamodel.DT_String;
import mdeServices.metamodel.DataType;
import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.UserDefinedDataType;
import mdeServices.options.Options;

import mdeServices.transformations.Transformation;
import mdeServices.transformations.TransformationNotApplicable;
import mdeServices.metamodel.stereotypes.S_ForeignKey;

/**
 * Transformation class for replacing all user defined types by string data types
 * to make sure the script can be executed
 * 
 * @version 0.1 Sep 2008
 * @author jcabot
 *
 */

public class T_RemoveUserDefinedDataTypes extends Transformation{
	
	//Model where to apply the transformation
	protected StaticModel m;

	public T_RemoveUserDefinedDataTypes(Project p, Options o) throws TransformationNotApplicable {
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
			Iterator<Attribute> itAt=cl.getAtt().iterator();
			while (itAt.hasNext()) {
				Attribute at=itAt.next();
				if ( (at.getType() instanceof UserDefinedDataType) || (!(at.getType() instanceof DataType)))
				{
					DT_String dS= new DT_String(at.getType().getName());
					at.setType(dS);
					m.addDataType(dS);
				}
			}	
		}
	  }catch(Exception e){throw new TransformationNotApplicable("Error when removing UserDefinedDataTypes");}

	}
}
