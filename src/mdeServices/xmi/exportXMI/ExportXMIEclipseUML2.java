/**
 * 
 */
package mdeServices.xmi.exportXMI;


import java.util.Iterator;
import java.util.Vector;

import mdeServices.metamodel.AggregationKind;
import mdeServices.metamodel.Association;
import mdeServices.metamodel.ChangeabilityKind;
import mdeServices.metamodel.Enumeration;
import mdeServices.metamodel.Generalization;
import mdeServices.metamodel.Parameter;
import mdeServices.metamodel.AssociationClass;
import mdeServices.metamodel.AssociationEnd;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.Attribute;
import mdeServices.metamodel.Classifier;
import mdeServices.metamodel.DataType;
import mdeServices.metamodel.ModelElement;
import mdeServices.metamodel.Operation;
import mdeServices.metamodel.PrimitiveDataType;
import mdeServices.metamodel.Package;
import mdeServices.metamodel.Property;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.VisibilityKind;


/**
 *  Export for EclipseUML2 files
 * 
 * @version 0.1 Aug 2009
 * @author jcabot
 *
 */
public class ExportXMIEclipseUML2 extends ExportXMI{

	public ExportXMIEclipseUML2() {super();}
	
	protected void exportProjectInfo()
	{
	};
	
	protected void endDocument()
	{
		//out.println("</XMI>");
	}
	

	protected void exportStaticModel(StaticModel s)
	{
	   out.println("<uml:Model xmi:version='2.1' xmlns:xmi='http://schema.omg.org/spec/XMI/2.1' xmlns:uml='http://www.eclipse.org/uml2/2.1.0/UML' xmi:id='" + 
		newID() + "' name='" + project.getName() +"'>");
	  incNested();
	  exportDataTypes(s.getDataTypes());
	  generateIDsForClasses(s); //To avoid problems with dangling references we first generate id's for all classes in the hash
	  generateIDsForAssociationsAndAssociationEnds(s); //The same with associations
	  generateIDsForAssociationClassesAndAssociationEnds(s); //The same with associations
	  exportPackages(s.getPackages());
	  decNested();
	  out.println("</uml:Model>");
	};
	

	protected void generateIDsForClasses(StaticModel s)
	{
		Iterator<Class> itCl = s.getAllClasses().iterator();
		while (itCl.hasNext()) {
			Class class1 = (Class) itCl.next();
			int id = newID();
			hash.put(class1,new String(Integer.toString(id)));
		}
		
	}
	
	protected void generateIDsForAssociationsAndAssociationEnds(StaticModel s)
	{
		Iterator<Association> itAs = s.getAllAssociationsNoAssCl().iterator();
		while (itAs.hasNext()) {
			Association as = (Association) itAs.next();
			int id = newID();
			hash.put(as,new String(Integer.toString(id)));
			Iterator<AssociationEnd> itAE=as.getEnds().iterator();
			while (itAE.hasNext()) {
				AssociationEnd ae = (AssociationEnd) itAE.next();
				int id2 = newID();
				hash.put(ae,new String(Integer.toString(id2)));
			}
		}
	}
	
	protected void generateIDsForAssociationClassesAndAssociationEnds(StaticModel s)
	{
		Iterator<AssociationClass> itAs = s.getAllAssociationClasses().iterator();
		while (itAs.hasNext()) {
			AssociationClass as = (AssociationClass) itAs.next();
			int id = newID();
			hash.put(as,new String(Integer.toString(id)));
			Iterator<AssociationEnd> itAE=as.getEnds().iterator();
			while (itAE.hasNext()) {
				AssociationEnd ae = (AssociationEnd) itAE.next();
				int id2 = newID();
				hash.put(ae,new String(Integer.toString(id2)));
			}
		}
		
	}
	
	protected void exportPackages(Vector<Package> packs)
	{
		Iterator<Package> it=packs.iterator();
		while (it.hasNext())
		{
		  Package p= it.next();
		  out.println(nestedString()+"<packagedElement xmi:type='uml:Package' xmi:id='" + newID() + "' " + nameToText(p) + ">");
		  incNested();
		  exportClasses(p);
		  exportAssociations(p);
		  exportAssociationClasses(p);
		  exportPackages(p.getFirstLevelPackages());
		  decNested();
		  out.println(nestedString()+"</packagedElement>");
		}
	}
	
	protected void exportClasses(Package p)
	{
		Iterator<Class> it=p.getAllDirectClassesNoAssoc().iterator();
		while (it.hasNext())
		{
		  Class cl= it.next();
		  VisibilityKind visibility= cl.getVisibility();
		  String vis="";
		  int id=Integer.parseInt(hash.get(cl));
		  out.println(nestedString()+"<packagedElement xmi:type='uml:Class' xmi:id = '" + id +"' " + nameToText(cl)+ " visibility = '" + visibility.toString() + 
				   "' isAbstract = '" + cl.isAbstract() +"'>");
		  incNested();
		  if (cl.getAtt().size()>0) exportAttributes(cl);
		  if (cl.getOps().size()>0) exportOperations(cl);
		  if (cl.getSuperCl().size()>0) exportGeneralizations(cl);
		  decNested();
		  out.println(nestedString()+"</packagedElement>");
		}
	}
	
	protected void exportAssociations(Package p)
	{
		Iterator<Association> it=p.getAllDirectAssociationsNoAssCl().iterator();
		while (it.hasNext())
		{
		  Association as= it.next();
		  int id = Integer.parseInt(hash.get(as));
		  String memberEnds="memberEnd='";
		  String navEnds="";
		  Iterator<AssociationEnd> itAe =as.getEnds().iterator();
		   while (itAe.hasNext()) {
			AssociationEnd ae= (AssociationEnd) itAe.next();
			memberEnds= memberEnds+ hash.get(ae)+" ";
			if (ae.isNavigable()) navEnds= navEnds+ hash.get(ae)+" ";
		  }
		  memberEnds=memberEnds+"'";
		  if (!navEnds.equals("")) //if there is at least one navigable end
			  navEnds="navigableOwnedEnd = '" + navEnds + "'";
		  out.println(nestedString()+ "<packagedElement xmi:type='uml:Association' xmi:id='"+ id + "' " +nameToText(as) +" " + memberEnds + " " + navEnds +">");
		  incNested();
		  exportAssociationEnds(as);
		  decNested();
		  out.println(nestedString() + "</packagedElement>");
		}
	}
	
	protected void exportAssociationEnds(Association as)
	{
		
		//Eclipse stores the aggregation property in the opposite association ends to the one that
		//graphically displays the diamond, so we need to interchange their aggreagation value
		//before exporting the model
		if (as.getEnds().size()==2)
		{
			AssociationEnd ae1=as.getEnds().get(0);
			AssociationEnd ae2=as.getEnds().get(1);	
			AggregationKind aux= ae1.getAggregation();
			ae1.setAggregation(ae2.getAggregation());
			ae2.setAggregation(aux);
		}	
	  Iterator<AssociationEnd> it=as.getEnds().iterator();
	  while (it.hasNext())
	  {
		AssociationEnd ae=it.next();
		int idAEnd=Integer.parseInt(hash.get(ae));
		int idType=Integer.parseInt(hash.get(ae.getSource()));
		int idAs=Integer.parseInt(hash.get(ae.getAss()));
		out.println(nestedString()+"<ownedEnd xmi:id = '" + idAEnd+ "' " + nameToText(ae) + " type='" + idType+ "' association='" + idAs + "' visibility = '" + ae.getVisibility().toString()+"' "
				+ " isStatic='" + ae.isStatic() + "' aggregation ='" + getAggregationString(ae.getAggregation()) + "' isReadOnly='"+ getIsReadOnlyString(ae.getChangeability())+"'>");
		incNested();
	    out.println(nestedString()+ "<upperValue xmi:type='uml:LiteralUnlimitedNatural' xmi:id='" + newID() + "' value='" + getMaxMultString(ae.getMax())+ "'/>");
	    //For multiplicities equal to zero, the type has to be a LiteralInteger
	    if(ae.getMin()!=0) out.println(nestedString()+ "<lowerValue xmi:type='uml:LiteralUnlimitedNatural' xmi:id='" + newID() + "' value='" + ae.getMin() + "'/>");
	    else out.println(nestedString()+ "<lowerValue xmi:type='uml:LiteralInteger' xmi:id='" + newID() + "' value='" + ae.getMin() + "'/>");
	    decNested();
	    out.println(nestedString()+ "</ownedEnd>");
	  }
	}
	
	protected String getMaxMultString(int max)
	{
		String stMax="";
		if (max==Property.N_Multiplicity) stMax="*";
		else stMax=Integer.toString(max);
		return stMax;
	}
	
	//Instead of the three values (changeable, frozen and readOnly) we just have a boolean isReadOnly in Eclipse
	protected String getIsReadOnlyString(ChangeabilityKind cha)
	{
		String isReadOnly="false";
		if (cha==ChangeabilityKind.C_ADDONLY || cha==ChangeabilityKind.C_READONLY) isReadOnly="true";
		return isReadOnly;
	}
	
	protected String getAggregationString(AggregationKind agg)
	{
		String aggregation="";
		if (agg==AggregationKind.A_NONE) aggregation="none";
		if (agg==AggregationKind.A_AGGREGATION) aggregation="shared";
		if (agg==AggregationKind.A_COMPOSITION) aggregation="composite";
		return aggregation;
	}

	protected void exportAttributes(Class c)
	{
	  Iterator<Attribute> it=c.getAtt().iterator();
	  while (it.hasNext())
	  {
		Attribute at=it.next();
		
		int idAt=newID();
		String type="";
		//Reference to the attribute data type
		if (at.getType()!=null)
	    {
	        type = " type='" + hash.get(at.getType()) +"' ";
		}
		out.println(nestedString()+"<ownedAttribute xmi:id = '" + idAt+ "' " + nameToText(at) + type + " visibility = '" + at.getVisibility().toString()+"' "
				+ " isStatic='" + at.isStatic() + "' isReadOnly='"+ getIsReadOnlyString(at.getChangeability())+"'>");
		//Only need to indicate the multiplicity if it is different than 1:1
	    if(at.getMin()!=1 || at.getMax()!=1)
		{
	    	incNested();
			out.println(nestedString()+ "<upperValue xmi:type='uml:LiteralUnlimitedNatural' xmi:id='" + newID() + "' value='" + getMaxMultString(at.getMax())+ "'/>");
	    	if (at.getMin()!=0) out.println(nestedString()+ "<lowerValue xmi:type='uml:LiteralUnlimitedNatural' xmi:id='" + newID() + "' value='" + at.getMin() + "'/>");
	    	else out.println(nestedString()+ "<lowerValue xmi:type='uml:LiteralInteger' xmi:id='" + newID() + "' value='" + at.getMin() + "'/>");
	    	decNested();
		}
	    out.println(nestedString()+ "</ownedAttribute>");
	 }
	}
	
	protected void exportOperations(Class c)
	{
	  Iterator<Operation> it=c.getOps().iterator();
	  while (it.hasNext())
	  {
    	Operation op =it.next();
		out.println(nestedString() + "<ownedOperation xmi:id = '" + newID()+"' " + nameToText(op)+ " visibility = '" + op.getVisibility().toString()+"' isStatic='" + op.isStatic() + "' isQuery = '" + op.isQuery() + "' isAbstract ='" + op.isAbstract()+"'>");
		incNested();
		if (op.getParams().size()>0) exportParameters(op);
		decNested();
		out.println(nestedString() + "</ownedOperation>");
	  }
	}
	 
		
	
	protected void exportParameters(Operation op)
	{
		Iterator<Parameter> it=op.getParams().iterator();
		while (it.hasNext())
		{
		  Parameter p=it.next();
	      String type="";
	  	  //Reference to the attribute data type
	  	  if (p.getType()!=null)
	  	  {
	        type = " type='" + hash.get(p.getType()) +"' ";
	  	  }
	  	  out.println(nestedString() + "<ownedParameter xmi:id = '" + newID()+"' name = '" + p.getName() + "' "+  type +  " direction= '" + p.getDirection().toString()+"'>");
		  if(p.getMin()!=1 || p.getMax()!=1)
		  {
			incNested();
			out.println(nestedString()+ "<upperValue xmi:type='uml:LiteralUnlimitedNatural' xmi:id='" + newID() + "' value='" + getMaxMultString(p.getMax())+ "'/>");
		   if(p.getMin()!=0) out.println(nestedString()+ "<lowerValue xmi:type='uml:LiteralUnlimitedNatural' xmi:id='" + newID() + "' value='" + p.getMin() + "'/>");
		   else out.println(nestedString()+ "<lowerValue xmi:type='uml:LiteralInteger' xmi:id='" + newID() + "' value='" + p.getMin() + "'/>"); 
		   decNested();
		 }
	       out.println(nestedString()+ "</ownedParameter>");
		}
	}
	
	
	
	protected void exportAssociationClasses(Package p)
	{
		Iterator<AssociationClass> it=p.getAllDirectAssociationClasses().iterator();
		while (it.hasNext())
		{
		  AssociationClass ascl= it.next();
		  VisibilityKind visibility= ascl.getVisibility();
		  int id=Integer.parseInt(hash.get(ascl));
		  out.println(nestedString()+"<packagedElement xmi:type='uml:AssociationClass' xmi:id = '" + id +"' " + nameToText(ascl)+ " visibility = '" + visibility.toString() + 
				   "' isAbstract = '" + ascl.isAbstract() +"'>");
		  incNested();
		  if (ascl.getAtt().size()>0) exportAttributes(ascl);
		  if (ascl.getOps().size()>0) exportOperations(ascl);
		  if (ascl.getSuperCl().size()>0) exportGeneralizations(ascl);
		  decNested();
		  //To reuse the exportAssociationEnds operation we create a fake association and 
		  //pass the association ends to this fake association class
		  Association aux = new Association("auxiliar");
		  aux.setEnds(ascl.getEnds());
		  exportAssociationEnds(aux);
		  decNested();
		  out.println(nestedString() + "</packagedElement>");
		}

	}
	
	protected void exportGeneralizations(Class cl)
	{ //Generalizations are exported as part of the export process for the subclass of the generalization
       	Iterator<Generalization> itSup = cl.getSuperCl().iterator();
       	while (itSup.hasNext())
       	{
       		Generalization g= itSup.next();
       		Classifier sup=g.getSuperType();
       		String idSup=hash.get(sup);
       		out.println(nestedString()+ "<generalization xmi:id = '" + newID() +"' general='" +idSup+"'/>");
       	}
    }
	

	protected void exportDataTypes(Vector<DataType> dts)
	{
		Iterator<DataType> it= dts.iterator();
		while (it.hasNext())
		{
		  DataType dt=it.next();
		  int id=newID();
		  hash.put(dt, new String(Integer.toString(id)));
		  if (dt instanceof Enumeration)
		  {
			  Enumeration e= (Enumeration) dt;
			  out.println(nestedString() + "<packagedElement xmi:type='uml:Enumeration' xmi:id='" + id + "' " + nameToText(dt) +">");
              if (e.getValues().size()>0)
              {
            	  incNested();
            	  Iterator<String> values=e.getValues().iterator();
            	  while (values.hasNext()) {
					String stLit = (String) values.next();
					 int idLit=newID();
					out.println(nestedString() + "<ownedLiteral xmi:id='"+ idLit + "' name='" + stLit + "'/>");
               	  }
            	  decNested();
              }
              out.println(nestedString() + "</packagedElement>");
   		  }
		  else
		  {
			 if (dt instanceof PrimitiveDataType) out.println(nestedString() + "<packagedElement xmi:type='uml:PrimitiveType' xmi:id='" + id + "' "+nameToText(dt) + "/>");
			 else out.println(nestedString() + "<packagedElement xmi:type='uml:DataType' xmi:id='" + id + "' " + nameToText(dt) + "/>");
		  }
		}
	}
		
	 private String nameToText(ModelElement m)
	 {
	   String name="";
	   if (m.getName()!=null) name="name = '" + m.getName() +"'";
	   return name;
	 }
	 
	 private String discriminatorToText(Generalization g)
	 {
	   String discriminator="";
	   if (g.getDiscriminator()!=null) discriminator="discriminator = '" + g.getDiscriminator() + "'";
	   return discriminator;
	 }
	 
	public boolean isNAriesAssocSupported() {return true;}
	public boolean isAssocClassSupported() {return true;}
	public boolean isMultipleInheritanceSupported() {return true;}
	public boolean isTextNormalizationNeeded() {return true;}
			 
}
