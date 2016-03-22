package mdeServices.transformations;

import java.util.Iterator;


import mdeServices.metamodel.DT_Integer;
import mdeServices.metamodel.DT_LongInteger;
import mdeServices.metamodel.DT_LongReal;
import mdeServices.metamodel.DT_Real;
import mdeServices.metamodel.DT_String;
import mdeServices.metamodel.DataType;
import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.options.Options;

/**
 * Adds more info about the datayptes of an attribute 
 * 
 * @version Dec 2008
 * @author jcabot
 *
 */

public class T_PreciseDataTypeInformation extends Transformation {
	
	//Model where to apply the transformation
	protected StaticModel m;

	public T_PreciseDataTypeInformation(Project p, Options o) throws TransformationNotApplicable {
		super(p, o);
		this.m=(StaticModel) p.getStaticModel();
	}
	
	public void exec() throws TransformationNotApplicable
	{
      try
      {
		Iterator<DataType> itDT = m.getDataTypes().iterator();
		while (itDT.hasNext())
		{
			DataType d=itDT.next();
			if (d.getClass()==DT_Integer.class)
			{
			   	DT_Integer dI = (DT_Integer) d;
			   	if (dI.getLength()==null) dI.setLength(new Integer(o.getProperty("db.integer.length")));
			}
			if (d instanceof DT_LongInteger)
			{
			   	DT_Integer dI = (DT_Integer) d;
			   	if (dI.getLength()==null) dI.setLength(new Integer(o.getProperty("db.long.integer.length")));
			}
			if (d.getClass()==DT_Real.class)
			{
			  	DT_Real dR = (DT_Real ) d;
			  	if (dR.getLength()==null) dR.setLength(new Integer(o.getProperty("db.real.length")));
			   	if (dR.getPrecision()==null) dR.setPrecision(new Integer(o.getProperty("db.real.precision")));
			 }
			if (d instanceof DT_LongReal)
			{
			  	DT_Real dR = (DT_Real ) d;
			  	if (dR.getLength()==null) dR.setLength(new Integer(o.getProperty("db.long.real.length")));
			   	if (dR.getPrecision()==null) dR.setPrecision(new Integer(o.getProperty("db.long.real.precision")));
			}
			if (d instanceof DT_String)
			{
			  	DT_String dS = (DT_String) d;
			  	if (dS.getLength()==null) dS.setLength(new Integer(o.getProperty("db.string.length")));
			  	if (dS.getFixLength()==null) dS.setFixLength(new Boolean(o.getProperty("db.string.fixedLength").equals("true")));
			}
		
		}
      }catch(Exception e){throw new TransformationNotApplicable("Error when precising data type information");}

	
	}
	
	

}
