/**
 * 
 */
package mdeServices.xmi.exportXMI;


import java.util.Iterator;

import mdeServices.metamodel.Association;
import mdeServices.metamodel.Generalization;

import mdeServices.metamodel.AssociationEnd;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.Attribute;
import mdeServices.metamodel.Classifier;

import mdeServices.metamodel.ModelElement;
import mdeServices.metamodel.Operation;

import mdeServices.metamodel.Property;
import mdeServices.metamodel.StaticModel;

import mdeServices.metamodel.AggregationKind;

/**
 *  Export for yUML files
 *  ((is a textual file not a XMI one but we can reuse the ExportXMI file
 * 
 * @version 0.1 Aug 2008
 * @author jcabot
 *
 */
public class ExportXMIPlantUML extends ExportXMI{

	public ExportXMIPlantUML() {super();}
	
	public boolean isXMLTypeFile()
	{
	  return false;// not an XML file
	}
	
	protected void exportProjectInfo()
	{
	   //The image file is created in an img subdirectory. The name of the file is the same
		// that of the main file but with .png extension
		out.println("@startuml img/"+ xmi.getName()+".png");
		out.println("title " + project.getName()+ " title ");
	};
	
	protected void endDocument()
	{
		out.println("@enduml");	
	}
	

	protected void exportStaticModel(StaticModel s)
	{
	    exportClasses(s);
		exportAssociations(s);
		exportGeneralizations(s);
	}
	
	protected void exportClasses(StaticModel s)
	{
		Iterator<Class> it=s.getAllClassesNoAssoc().iterator();
		boolean first=true;
		while (it.hasNext())
		{
		  Class cl= it.next();
		  if (first) out.print("["+cl.getName());
		  else out.print(",["+cl.getName());
		  if (cl.getAtt().size()>0) exportAttributes(cl);
		  if (cl.getOps().size()>0) exportOperations(cl);
		  out.print("]");
		  first=false;
		}
	}
	
	
	
	protected void exportAssociations(StaticModel s)
	{
		Iterator<Association> it=s.getAllAssociationsNoAssCl().iterator();
		while (it.hasNext())
		{
		  Association as= it.next();
		  out.print(",");
		  exportAssociationEnds(as);
		}
	}
	
	protected void exportAssociationEnds(Association as)
	{
	  Iterator<AssociationEnd> it=as.getEnds().iterator();
	  boolean first=true;
	  while (it.hasNext())
	  {
		AssociationEnd ae=it.next();
		
		String type="";
		if (ae.getAggregation()==AggregationKind.A_NONE)
		{ if (ae.isNavigable()){
			 if (first) type="<"; else type=">";
		  }
		  else type="";
		}
		else if (ae.getAggregation()==AggregationKind.A_AGGREGATION) type="<>";
		else if (ae.getAggregation()==AggregationKind.A_COMPOSITION) type="++";
        
		String mult="";
		if (ae.getMin()==ae.getMax()) mult=(new Integer(ae.getMin())).toString();
		else {
			String max="";
			if (ae.getMax()==Property.N_Multiplicity) max="*"; else max=(new Integer(ae.getMax())).toString();
			mult=ae.getMin()+ ".." + max;
		}
		
		if (first)
		{
			out.print("["+ae.getSource().getName()+"]");
			out.print(type+mult+" "+nameToText(ae)+"-"); 
		}
		else 
		{
			out.print(nameToText(ae)+" "+mult+type);
			out.print("["+ae.getSource().getName()+"]");
		}

        first=false;
	  }
	 
	}
	

	protected void exportAttributes(Class c)
	{
	  Iterator<Attribute> it=c.getAtt().iterator();
	  if (it.hasNext()) out.print("|");
	  while (it.hasNext())
	  {
		Attribute at=it.next();
		if (it.hasNext()) out.print(at.getName()+";");
		else out.print(at.getName());
	  }
	}
	
	protected void exportOperations(Class c)
	{
	  Iterator<Operation> it=c.getOps().iterator();
	  if (it.hasNext()) out.print("|");
	  while (it.hasNext())
	  {
    	Operation op =it.next();
    	if (it.hasNext()) out.print(op.getName() + "();");
    	else  out.print(op.getName() + "()");
	  }
	}
	 
	protected void exportGeneralizations(StaticModel s)
	{
        Iterator<Generalization> it = s.getAllGeneralizations().iterator();
        while (it.hasNext())
        {
        	Generalization g=it.next();
        	Classifier superType=g.getSuperType();
        	Classifier subType=g.getSubType(); 
        	out.print(","+"["+superType.getName()+"]^-["+subType.getName()+"]");
        }
    }
	

	 private String nameToText(ModelElement m)
	 {
	   String name="";
	   if (m.getName()!=null) name=m.getName();
	   return name;
	 }
	 
	public boolean isNAriesAssocSupported() {return false;}
	public boolean isAssocClassSupported() {return false;}
	public boolean isMultipleInheritanceSupported() {return true;}
	public boolean isTextNormalizationNeeded() {return false;}
	 
	 
		
		  
	 
}
