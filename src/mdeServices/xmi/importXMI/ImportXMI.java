/**
 * 
 */
package mdeServices.xmi.importXMI;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import mdeServices.metamodel.AggregationKind;
import mdeServices.metamodel.Association;
import mdeServices.metamodel.AssociationEnd;
import mdeServices.metamodel.Attribute;
import mdeServices.metamodel.ChangeabilityKind;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.Classifier;
import mdeServices.metamodel.DirectionKind;
import mdeServices.metamodel.Generalization;
import mdeServices.metamodel.ModelElement;
import mdeServices.metamodel.ModelFactory;
import mdeServices.metamodel.Operation;
import mdeServices.metamodel.Parameter;
import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.VisibilityKind;

/**
 *  Abstract class for all types of XMI imports
 * 
 * @version 0.1 Aug 2008
 * @author jcabot
 *
 */
public abstract class ImportXMI {

	protected File xmi;
	protected Project project;
	protected SAXBuilder builder;
	protected Document document;
	protected Element root;
	protected HashMap<String, ModelElement> hash;
	protected HashMap<ModelElement, String> hashPending;
		
	public ImportXMI() {}
	
	/**
	 * @param xmi
	 */
	public Project importFile(File xmi) throws JDOMException, IOException, ImportXMIException
	{
		this.xmi = xmi;
		project=null;
		hash = new HashMap<String,ModelElement>(0);
		hashPending = new HashMap<ModelElement,String>(0);
		builder = new SAXBuilder();
		unsetValidation(); //Some imports (e.g. Visio) may require to avoid validation. By default validationis enabled
		document = builder.build(xmi);
       	root= document.getRootElement();
       	importProject(); //Starting the import of the file
        return project; 
	}
	
	protected void unsetValidation(){};
	
	private void importProject() throws ImportXMIException
	{
	   project = importProjectInfo();
	   project.setStaticModel(importStaticModel());
	   if (project.getName().equals("")) project.setName(project.getStaticModel().getName());
	}
	
	public abstract StaticModel importStaticModel() throws ImportXMIException;
	
	public abstract Project importProjectInfo() throws ImportXMIException;
	
	//Checks if a class is in reality a data type stored as a class 
	protected boolean isDataType(String name)
	{
	  //For the comparison we remove the suffix "(idl)" of some datatypes in Visio
      return (ModelFactory.isIntegerDataType(name) || ModelFactory.isUnsignedIntegerDataType(name) ||
    		  ModelFactory.isShortIntegerDataType(name) || ModelFactory.isUnsignedShortIntegerDataType(name) ||
    		  ModelFactory.isLongIntegerDataType(name) || ModelFactory.isUnsignedLongIntegerDataType(name) ||
    		  ModelFactory.isRealDataType(name) || ModelFactory.isLongRealDataType(name) ||
    		  ModelFactory.isStringDataType(name) || ModelFactory.isCharDataType(name) ||
    		  ModelFactory.isDateDataType(name) || ModelFactory.isDateTimeDataType(name) ||
    		  ModelFactory.isBooleanDataType(name) || ModelFactory.isCurrencyDataType(name) ||
    		  ModelFactory.isVoidDataType(name) || ModelFactory.isUnspecifiedDataType(name) );
	}
	
	protected ChangeabilityKind getChangeability(String cha)
	{
		ChangeabilityKind changeability=null;
		if (cha!=null)
		{
			if (cha.equals("changeable") || cha.equals("ninguno") || cha.equals("keine")) changeability= ChangeabilityKind.C_CHANGEABLE;
			else if (cha.equals("frozen") || cha.equals("inmovilizado")) changeability= ChangeabilityKind.C_READONLY;
				else if (cha.equals("addOnly") || cha.equals("solo agregar")) changeability = ChangeabilityKind.C_ADDONLY;
		}
		if(changeability==null) changeability=ChangeabilityKind.C_CHANGEABLE;
		return changeability;
	}
	
	protected AggregationKind getAggregationKind(String agg)
	{
		AggregationKind aggregation=null;
		if (agg!=null)
		{
			if (agg.equals("none") || agg.equals("ninguno") || agg.equals("keine")) aggregation= AggregationKind.A_NONE;
			else if (agg.equals("aggregate") || agg.equals("shared") ||agg.equals("compartido")) aggregation= AggregationKind.A_AGGREGATION;
				else if (agg.equals("composite") || agg.equals("compuesto")) aggregation= AggregationKind.A_COMPOSITION;
		}
		if (aggregation==null)  aggregation= AggregationKind.A_NONE;
		return aggregation;
	}
	
	protected boolean isStatic(String ownerScope)
	{
	   return !(ownerScope.toUpperCase().equals("INSTANCE") || ownerScope.toUpperCase().equals("INSTANZ") );
	}
	
	protected VisibilityKind getVisibility(String vis)
	{
		VisibilityKind visibility=null;
		if(vis!=null)
		{
			if (vis.equals("public")) visibility = VisibilityKind.V_PUBLIC;
			else if (vis.equals("protected")) visibility = VisibilityKind.V_PROTECTED;
				else if (vis.equals("private")) visibility = VisibilityKind.V_PRIVATE;
		}
		if (visibility==null)  visibility= VisibilityKind.V_PUBLIC;
		return visibility;
	}
	
    protected DirectionKind getDirection(String dir)
    {
		DirectionKind direction=null;
    	if(dir!=null)
    	{
    		if (dir.equals("in")  ) direction= DirectionKind.D_IN;
    		else if (dir.equals("out") ) direction= DirectionKind.D_OUT;
    			else if (dir.equals("inout")  ) direction= DirectionKind.D_INOUT;
    				else if (dir.equals("return") ) direction= DirectionKind.D_RETURN;
    	}
    	if(direction==null) direction=DirectionKind.D_IN;
    	return direction;
    }
    
    protected void setDanglingRefGeneralization(Generalization g)
	{
		String ref=hashPending.get(g);
		if(ref!=null)
		{
			Classifier c= ((Classifier) hash.get(ref));
			//Only one of the both ends can be null 
			if (g.getSubType()==null)
			{
				g.setSubType(c);
				c.addSuperType(g);
			}
			else if (g.getSuperType()==null)
			{
				g.setSuperType(c);
				c.addSubType(g);
			}
		}
			
	}
	
	protected void setDanglingRefClass(Class class1)
	{
		Iterator<Attribute> itAt=class1.getAtt().iterator();
		while (itAt.hasNext()) {
			Attribute attribute = (Attribute) itAt.next();
			if (attribute.getType()==null)
			{
				String ref=hashPending.get(attribute);
				//If ref==null, a type was not defined for the attribute in the original model
				if (ref!=null) attribute.setType((Classifier)hash.get(ref));
			}
		}
		Iterator<Operation> itOp=class1.getOps().iterator();
		while (itOp.hasNext()) {
			Operation op= (Operation) itOp.next();
			Iterator<Parameter> itP= op.getParams().iterator();
			while (itP.hasNext()) {
				Parameter parameter = (Parameter) itP.next();
				if (parameter.getType()==null)
				{
					String ref=hashPending.get(parameter);
					//If ref!=null, a type was not defined for the parameter in the original model
					if (ref!=null) parameter.setType((Classifier)hash.get(ref));
				}	
			}
		}
	}
		
	protected void setDanglingRefAssociation(Association assoc1)
	{
		Iterator<AssociationEnd> itAE=assoc1.getEnds().iterator();
		while (itAE.hasNext()) {
			AssociationEnd ae = itAE.next();
			if (ae.getSource()==null)
			{
				String ref=hashPending.get(ae);
				Classifier c=(Classifier)hash.get(ref);
				ae.setSource(c);
				c.addAssociationEnd(ae);
			}
		}
	}
	

}
