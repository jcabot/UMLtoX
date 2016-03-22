/**
 * 
 */
package mdeServices.xmi.exportXMI;


import java.util.Iterator;
import java.util.Vector;


import mdeServices.metamodel.Association;
import mdeServices.metamodel.Enumeration;
import mdeServices.metamodel.Generalization;
import mdeServices.metamodel.ModelFactory;
import mdeServices.metamodel.Parameter;
import mdeServices.metamodel.AssociationClass;
import mdeServices.metamodel.AssociationEnd;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.Attribute;
import mdeServices.metamodel.Classifier;
import mdeServices.metamodel.DataType;
import mdeServices.metamodel.ModelElement;
import mdeServices.metamodel.Operation;
import mdeServices.metamodel.Package;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.VisibilityKind;


/**
 *  Export for ArgoUML v 0.24 files
 * 
 * @version 0.1 Aug 2008
 * @author jcabot
 *
 */
public class ExportXMIArgoUML extends ExportXMI{

	public ExportXMIArgoUML() {super();}
	
	protected void exportProjectInfo()
	{
		out.println("<XMI xmi.version = '1.2' xmlns:UML = 'org.omg.xmi.namespace.UML'>");
		out.println("<XMI.header>");
		out.println("<XMI.documentation>");
		out.println("<XMI.exporter> MDEServices - ArgoUML </XMI.exporter>");
		out.println("<XMI.exporterVersion>0.24(5) </XMI.exporterVersion>");
		out.println("</XMI.documentation>");
		out.println("<XMI.metamodel xmi.name=\"UML\" xmi.version=\"1.4\"/>");
		out.println("</XMI.header>");
	};
	
	protected void endDocument()
	{
		out.println("</XMI>");
	}
	

	protected void exportStaticModel(StaticModel s)
	{
	  out.println("<XMI.content>");
	  out.println("<UML:Model xmi.id =" + "'" +newID() + "'" + " name = '"+ ModelFactory.normalize(s.getName()) + "'" + " isSpecification = 'false' isRoot = 'false' isLeaf = 'false' isAbstract = 'false'>");
	  incNested();
	  out.println(nestedString()+"<UML:Namespace.ownedElement>");
	  exportDataTypes(s.getDataTypes());
	  generateIDsForClasses(s); //To avoid problems with dangling references we first generate id's for all classes in the hash
	//  exportStereotypes(s.getStereotypes());
	  exportPackages(s.getPackages());
	  out.println("</UML:Namespace.ownedElement>");
	  decNested();
	  out.println("</UML:Model>");
	  out.println("</XMI.content>");
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
	
	protected void exportPackages(Vector<Package> packs)
	{
		Iterator<Package> it=packs.iterator();
		while (it.hasNext())
		{
		  Package p= it.next();
		  out.println(nestedString()+"<UML:Package xmi.id = '"+newID()+"' name = '" + p.getName() +"' isSpecification = 'false' isRoot = 'false' isLeaf = 'false' isAbstract = 'false'>");
		  incNested();
		  out.println(nestedString() + "<UML:Namespace.ownedElement>");
		  exportClasses(p);
		  exportAssociations(p);
		  exportAssociationClasses(p);
		  exportGeneralizations(p);
		  exportPackages(p.getFirstLevelPackages());
		  out.println(nestedString() + "</UML:Namespace.ownedElement>");
		  decNested();
		  out.println(nestedString()+"</UML:Package>");
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
		  out.println(nestedString()+"<UML:Class xmi.id = '" + id +"' name = '" + cl.getName() + "' visibility = '" + visibility.toString() + "' isSpecification = 'false' isRoot = 'false' "
				  + " isLeaf = 'false' isAbstract = '" + cl.isAbstract()+"' isActive = 'false'>");
		  incNested();
      //    if (cl.getStereotypes().size()>0) exportRefStereotype(cl);
          out.println(nestedString()+"<UML:Classifier.feature>");
		  incNested();
		  if (cl.getAtt().size()>0) exportAttributes(cl);
		  if (cl.getOps().size()>0) exportOperations(cl);
		  decNested();
		  out.println(nestedString()+"</UML:Classifier.feature>");
		  decNested();
		  out.println(nestedString() +"</UML:Class>");
		}
	}
	
	
	
	protected void exportAssociations(Package p)
	{
		Iterator<Association> it=p.getAllDirectAssociationsNoAssCl().iterator();
		while (it.hasNext())
		{
		  Association as= it.next();
		  int id = newID();
		  out.println(nestedString()+ "<UML:Association xmi.id = '"+ newID() +"' "+ nameToText(as)  + " isSpecification = 'false' isRoot = 'false' isLeaf = 'false' isAbstract = 'false'>");
		  incNested();
		  exportAssociationEnds(as);
		  decNested();
		  out.println(nestedString() + "</UML:Association>");
		}
	}
	
	protected void exportAssociationEnds(Association as)
	{
	  Iterator<AssociationEnd> it=as.getEnds().iterator();
	  out.println(nestedString() + "<UML:Association.connection>");
	  while (it.hasNext())
	  {
		AssociationEnd ae=it.next();
		int idType=Integer.parseInt(hash.get(ae.getSource()));
		String ownerScope=""; if (ae.isStatic()) ownerScope="classifier"; else ownerScope="instance";
		out.println(nestedString()+"<UML:AssociationEnd xmi.id = '" +newID()+"' " + nameToText(ae) + " visibility = '" + ae.getVisibility().toString()+"' isSpecification = 'false'"+
				" isNavigable = '" + ae.isNavigable() +"' ordering = 'unordered' aggregation ='" + ae.getAggregation().toString() + "' targetScope = '" +ownerScope +"' changeability = '"+ ae.getChangeability().toString()+"'>");
		incNested();
	    out.println(nestedString()+"<UML:AssociationEnd.multiplicity>");
	    incNested();
	    out.println(nestedString()+"<UML:Multiplicity xmi.id = '" + newID() +"'>");
	    out.println(nestedString()+"<UML:Multiplicity.range>");
	    out.println(nestedString()+ "<UML:MultiplicityRange xmi.id = '" +newID() +"' lower = '" + ae.getMin() +"' upper = '" + ae.getMax() + "'/>");
	    out.println(nestedString()+ "</UML:Multiplicity.range>");
	    out.println(nestedString()+ "</UML:Multiplicity>");
	    decNested();
	    out.println(nestedString()+ "</UML:AssociationEnd.multiplicity>");
	 //   if (ae.getStereotypes().size()>0) exportRefStereotype(ae);
	    out.println(nestedString()+ "<UML:AssociationEnd.participant>");
	    incNested();
	    out.println(nestedString() + "<UML:Class xmi.idref = '"+ idType +"'/>");
        decNested();
        out.println(nestedString() + "</UML:AssociationEnd.participant>");
        decNested();
        out.println(nestedString() + "</UML:AssociationEnd>");
	  }
	  out.println(nestedString() + "</UML:Association.connection>");
	}
	

	protected void exportAttributes(Class c)
	{
	  Iterator<Attribute> it=c.getAtt().iterator();
	  while (it.hasNext())
	  {
		Attribute at=it.next();
		String ownerScope=""; if (at.isStatic()) ownerScope="classifier"; else ownerScope="instance";
		out.println(nestedString() + "<UML:Attribute xmi.id = '" + newID()+"' name = '" + at.getName() + "' visibility = '" + at.getVisibility().toString()+"' isSpecification = 'false' ownerScope = '" + ownerScope + 
				"' changeability = '" + at.getChangeability().toString() + "' targetScope = 'instance'>");
	    incNested();
	    out.println(nestedString()+"<UML:StructuralFeature.multiplicity>");
	    incNested();
	    out.println(nestedString()+"<UML:Multiplicity xmi.id = '" + newID() +"'>");
	    out.println(nestedString()+"<UML:Multiplicity.range>");
	    out.println(nestedString()+ "<UML:MultiplicityRange xmi.id = '" +newID() +"' lower = '" + at.getMin() +"' upper = '" + at.getMax() + "'/>");
	    out.println(nestedString()+ "</UML:Multiplicity.range>");
	    out.println(nestedString()+ "</UML:Multiplicity>");
	    decNested();
	    out.println(nestedString()+ "</UML:StructuralFeature.multiplicity>");
	//    if (at.getStereotypes().size()>0) exportRefStereotype(at);
	    if (at.getType()!=null)
	    {
	    	int idDType=Integer.parseInt(hash.get(at.getType())); 
	    	out.println(nestedString()+ "<UML:StructuralFeature.type>");
	    	incNested();
	       	if (! (at.getType() instanceof Enumeration)) out.println(nestedString()+ "<UML:DataType xmi.idref ='" +idDType+  "'/>");
	    	else out.println(nestedString()+ "<UML:Enumeration xmi.idref ='" +idDType+  "'/>");
	   	    decNested();
	   	    out.println(nestedString()+ "</UML:StructuralFeature.type>");
	    }
	   	decNested();
	    out.println(nestedString()+ "</UML:Attribute>");
	 }
	}
	
	protected void exportOperations(Class c)
	{
	  Iterator<Operation> it=c.getOps().iterator();
	  while (it.hasNext())
	  {
    	Operation op =it.next();
		String ownerScope=""; if (op.isStatic()) ownerScope="classifier"; else ownerScope="instance";
		out.println(nestedString() + "<UML:Operation xmi.id = '" + newID()+"' name = '" + op.getName() + "' visibility = '" + op.getVisibility().toString()+"' isSpecification = 'false' ownerScope = '" + ownerScope + 
				"' isQuery = '" + op.isQuery() + "' concurrency  =' sequential' isRoot ='false' isLeaf='false' isAbstract ='" + op.isAbstract()+"'>");
		incNested();
		if (op.getParams().size()>0) exportParameters(op);
		decNested();
		out.println(nestedString() + "</UML:Operation>");
	  }
	}
	 
		
	
	protected void exportParameters(Operation op)
	{
		Iterator<Parameter> it=op.getParams().iterator();
		out.println(nestedString()+"<UML:BehavioralFeature.parameter>");
		while (it.hasNext())
		{
		  Parameter p=it.next();
	      out.println(nestedString() + "<UML:Parameter xmi.id = '" + newID()+"' name = '" + p.getName() + "' isSpecification = 'false' kind = '" + p.getDirection().toString()+"'>");
		  if(p.getType()!=null)
		  {
			  int idDType=Integer.parseInt(hash.get(p.getType()));
			  incNested();
	          out.println(nestedString()+ "<UML:Parameter.type>");
	          incNested();
	          if (! (p.getType() instanceof Enumeration)) out.println(nestedString()+ "<UML:DataType xmi.idref ='" +idDType+  "'/>");
	          else out.println(nestedString()+ "<UML:Enumeration xmi.idref ='" +idDType+  "'/>");
	          decNested();
	          out.println(nestedString()+ "</UML:Parameter.type>");
	          decNested();
		  }
	      out.println(nestedString()+ "</UML:Parameter>");
		}
		out.println(nestedString()+"</UML:BehavioralFeature.parameter>");
	}
	
	
	
	protected void exportAssociationClasses(Package p)
	{
		Iterator<AssociationClass> it=p.getAllDirectAssociationClasses().iterator();
		while (it.hasNext())
		{
		  AssociationClass ascl= it.next();
		  VisibilityKind visibility= ascl.getVisibility();
		  int id=Integer.parseInt(hash.get(ascl));
		  out.println(nestedString()+"<UML:AssociationClass xmi.id = '" + id +"' name = '" + ascl.getName() + "' visibility = '" + visibility.toString() + "' isSpecification = 'false' isRoot = 'false' "
				  + " isLeaf = 'false' isAbstract = '" + ascl.isAbstract()+"' isActive = 'false'>");
		  incNested();
//		   if (ascl.getStereotypes().size()>0) exportRefStereotype(ascl);
		  out.println(nestedString()+"<UML:Classifier.feature>");
		  incNested();
		  if (ascl.getAtt().size()>0) exportAttributes(ascl);
		  if (ascl.getOps().size()>0) exportOperations(ascl);
		  decNested();
		  out.println(nestedString()+"</UML:Classifier.feature>");
		   //To reuse the exportAssociationEnds operation we create a fake association and 
		  //pass the association ends to this fake association class
		  Association aux = new Association("auxiliar");
		  aux.setEnds(ascl.getEnds());
		  exportAssociationEnds(aux);
		  decNested();
		  out.println(nestedString() +"</UML:AssociationClass>");
		}

	}
	
	protected void exportGeneralizations(Package p)
	{
        Iterator<Class> it = p.getAllDirectSuperClasses().iterator();
        while (it.hasNext())
        {
        	Class cl=it.next();
        	int idSup=Integer.parseInt(hash.get(cl));
        	Iterator<Generalization> itSub = cl.getSubCl().iterator();
        	while (itSub.hasNext())
        	{
        		Generalization g= itSub.next();
        		Classifier sub=g.getSubType();
        		out.println(nestedString()+ "<UML:Generalization xmi.id = '" + newID() +"' " + nameToText(g) + " " + discriminatorToText(g) +" isSpecification = 'false'>");
            	incNested();
            	out.println(nestedString() + "<UML:Generalization.child>");
                incNested();
                int idSub=Integer.parseInt(hash.get(sub));
                out.println(nestedString() + "<UML:Class xmi.idref = '" +  idSub + "'/>");
                decNested();
                out.println(nestedString() + "</UML:Generalization.child>");
                out.println(nestedString() + "<UML:Generalization.parent>");
                incNested();
                out.println(nestedString()+ "<UML:Class xmi.idref = '" +idSup + "'/>");
                decNested();
                out.println(nestedString() + "</UML:Generalization.parent>");
                decNested();
                out.println(nestedString() +"</UML:Generalization>");
        	}
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
			  out.println(nestedString() + "<UML:Enumeration xmi.id = '" + id + "' name = '" + ModelFactory.normalize(dt.getName())+ "' isSpecification = 'false' isRoot = 'false' isLeaf = 'false' isAbstract = 'false'> ");
              if (e.getValues().size()>0)
              {
            	  incNested();
            	  out.println(nestedString() + "<UML:Enumeration.literal>");
            	  incNested();
            	  Iterator<String> values=e.getValues().iterator();
            	  while (values.hasNext()) {
					String string = (String) values.next();
					 int idLit=newID();
					out.println(nestedString() +  "<UML:EnumerationLiteral xmi.id = '" + idLit + "' name = '" + string + "' isSpecification = 'false' />");
            	  }
            	  decNested();
            	  out.println(nestedString() + "</UML:Enumeration.literal>");
            	  decNested();
              }
              out.println(nestedString() + "</UML:Enumeration>");
   		  }
		  else out.println(nestedString() + "<UML:DataType xmi.id = '" + id + "' name = '" +ModelFactory.normalize(dt.getName())+ "' isSpecification = 'false' isRoot = 'false' isLeaf = 'false' isAbstract = 'false'/> ");
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
	 
	public boolean isNAriesAssocSupported() {return false;}
	public boolean isAssocClassSupported() {return true;}
	public boolean isMultipleInheritanceSupported() {return true;}
	public boolean isTextNormalizationNeeded() {return true;}
	 
	 /*protected void exportRefStereotype(ModelElement e)
		{
		  	out.println(nestedString()+"<UML:ModelElement.stereotype>");
		  	incNested();
		  	Iterator<Stereotype> it = e.getStereotypes().iterator();
		  	while (it.hasNext())
		  	{  
		  	   String s=it.next().getName();	
		  	   String id=hashStereotypes.get(s);
		  	   out.println(nestedString() + "<UML:Stereotype xmi.idref = '" + id +"'/>");
		  	}
	  	  decNested();
	      out.println(nestedString()+"</UML:ModelElement.stereotype>");
		}*/
	 
	 /*protected void exportStereotypes(Vector<Stereotype> sts)
		{
			Iterator<Stereotype> it=sts.iterator();
			while (it.hasNext())
			{
				Stereotype st=it.next();
				int id=newID();
				out.println(nestedString() + "<UML:Stereotype xmi.id = '"+ id +"' name = '" + st.getName() +"' isSpecification = 'false' isRoot = 'false' isLeaf = 'false' isAbstract = 'false'>");
				incNested();
				out.println(nestedString() + "<UML:Stereotype.baseClass>" + st.getBaseClass() + "</UML:Stereotype.baseClass>");
		        decNested();
				out.println(nestedString() + "</UML:Stereotype>");
				hashStereotypes.put(st.getName(), Integer.toString(id));
			}
		}
		*/
		
		  
	 
}
