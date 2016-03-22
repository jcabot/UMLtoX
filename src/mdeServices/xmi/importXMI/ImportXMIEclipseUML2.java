/**
 * 
 */
package mdeServices.xmi.importXMI;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import mdeServices.metamodel.*;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.Package;
import org.jdom.Element;
import org.jdom.Namespace;


/**
 *  Specific import class for Eclipseuml2 files
 * 
 *  Some model attributes are avoided in the XML file when the user does not change the default value
 *  (that is, if they do not appear in the XMI, the default value must be assumed)
 *  
 * @version 0.1 June 2009
 * @author jcabot
 *
 */
public class ImportXMIEclipseUML2 extends ImportXMI {

	protected StaticModel m;
    Namespace umlNamespace; 
    Namespace xmiNamespace;
	protected HashMap<AssociationEnd, String> hashAssocEndPending; //for association ends stored as attributes in classes
	protected Vector<AssociationEnd> assocEndPending;
    
	/**
	 * @param xmi
	 */
	public ImportXMIEclipseUML2() {	super();
	   hashAssocEndPending = new HashMap<AssociationEnd,String>(0);
	   assocEndPending=new Vector<AssociationEnd>(0,1);
	}
	
	/** Operation for importing the project info */
	public Project importProjectInfo() throws ImportXMIException
	{   String language=null;
		umlNamespace = root.getNamespace("uml");//$NON-NLS-1$
		xmiNamespace = root.getNamespace("xmi");
		Project pr = new Project("","","",language);
		return pr;		
	}
	
	/** Operation for importing the static model info */
	public StaticModel importStaticModel() throws ImportXMIException {
		
		//Sometimes the first line is a xmi:XMI element and sometimes is directly the uml:Model
		if ((root.getName().equals("XMI") && root.getChild("Model",umlNamespace)!=null))
			root=root.getChild("Model",umlNamespace);
		else if ((root.getName().equals("XMI") && root.getChild("Package",umlNamespace)!=null))
			root=root.getChild("Package",umlNamespace);
		//and sometimes a uml:Package element 
		
		String modelName=root.getAttributeValue("name");
		if (modelName==null) modelName="unnamedModel";
		
		//Creating the model instance
		
		m= new StaticModel(modelName);
		//Recursive preorder traversal of the tree
		//default package (all elements must be included in a package 
		Package defaultPackage= new Package("default");
		importElements(root.getChildren(),null,defaultPackage,m);
		if (defaultPackage.getElements().size()>0) m.addPackage(defaultPackage);

		//Iteration on the imported elements to solve the dangling references
		Iterator<Class> classes= m.getAllClassesNoAssoc().iterator();
		while (classes.hasNext()) {
			Class class1 = classes.next();
			setDanglingRefClass(class1);
		}
		
		Iterator<Generalization> itGen= m.getAllGeneralizations().iterator();
		while (itGen.hasNext()) {
			Generalization g= itGen.next();
			setDanglingRefGeneralization(g);
		}
		
		Iterator<AssociationEnd> itAEnd=assocEndPending.iterator();
		while (itAEnd.hasNext())
		{
		  AssociationEnd ae=itAEnd.next();
		  String assId=hashAssocEndPending.get(ae);
          ModelElement me=hash.get(assId);
	      if (me instanceof Association) //we add the association end here
	      {
	    	  Association a = (Association) me;
	    	  a.addAssociationEnd(ae);
	    	  ae.setAss(a);
	      }
	      else if (me instanceof AssociationClass)
	      {
	    	  AssociationClass a = (AssociationClass) me;
	    	  a.addAssociationEnd(ae);
	    	  ae.setAss(a);
	      }
		}
		
		
		
		Iterator<Association> associations= m.getAllAssociationsNoAssCl().iterator();
		while (associations.hasNext()) {
			Association assoc1= associations.next();
			setDanglingRefAssociation(assoc1);
		}
		
		Iterator<AssociationClass> assocClasses= m.getAllAssociationClasses().iterator();
		while (assocClasses.hasNext()) {
			AssociationClass asCl= assocClasses.next();
			setDanglingRefClass(asCl);
			Association aux= new Association("fake");
			aux.setEnds(asCl.getEnds());
			setDanglingRefAssociation(aux);
		}
		m.addDefaultDataTypes();
		return m;
	}
	
	
	protected void importElements(List<Element> elements, ModelElement parent, Package defaultPackage, StaticModel m) throws ImportXMIException
	{
		Package realParent; 
		if (parent==null) realParent=defaultPackage;
		else realParent=(Package) parent;
		Iterator<Element> itEl = elements.iterator();
		while (itEl.hasNext()) {
			Element element = (Element) itEl.next();
			String type=getTypeXML(element);
			if (type!=null)
			{
				if (type.equals("Model")) //we may find nested models
					importElements(element.getChildren(),null,defaultPackage,m);
				if (type.equals("Package"))
					importPackage(element,(Package) parent,m);
				else if (type.equals("Class"))
					importClass(element,realParent,m);
				else if (type.equals("Association"))
					importAssociation(element,realParent,m);
				else if (type.equals("DataType") || type.equals("PrimitiveType"))
					importDataType(element,m);
				else if (type.equals("Enumeration"))
					importEnumeration(element,m);
			//else if (getTypeXML(element).equals("Generalization"))
			//	importGeneralization(element,realParent,m);
				else if (type.equals("AssociationClass"))
					importAssociationClass(element,realParent,m);
			}
		}
	}

	public String getTypeXML(Element el)
	{
		String type=null;
		String aux=el.getAttributeValue("type",xmiNamespace);
		if (aux==null) //it might be an imported element
		{
			if(el.getName().equals("elementImport")) 
			{
				Element auxEl=el.getChild("importedElement");
				aux=auxEl.getAttributeValue("type",xmiNamespace);
			}
		}
		if (aux!=null)
		{
			if (aux.equals("uml:PrimitiveType") || aux.equals("uml:DataType")) type="DataType";
			else type=aux.substring(4,aux.length());
		}
		return type;
	}
	
	protected void importPackage(Element pack, Package parent, StaticModel m) throws ImportXMIException
	{
		Package p= new Package(pack.getAttributeValue("name"));
		List<Element> xmiPackageContents=pack.getChildren("packagedElement");
		if(xmiPackageContents!=null && !xmiPackageContents.isEmpty())
		{
			importElements(xmiPackageContents,p,null,m); //the default package is no longer needed
			if(p.getElements().size()>0)
			{
				if (parent==null) { m.addPackage(p); p.setModel(m);}
				else parent.addElement(p);
			}
		}
	}
	
	
	/** Operation for importing the generalizations info */
	private void importGeneralization(Element xmiGen, Classifier child,Package p) throws ImportXMIException
	{
		Class supertype,subtype;
		//No discriminator nor name for generalization sin eclipse
		// String name=xmiGen.getAttributeValue("name");
		//	String discriminator=xmiGen.getAttributeValue("discriminator");
		
		Generalization g=new Generalization("");
//		g.setDiscriminator(discriminator);
		
		child.addSuperType(g);
		
		//Retrieving the superclass
		String idSup= xmiGen.getAttributeValue("general");
		supertype = (Class) hash.get(idSup);
		if (supertype==null) 
		{ //We need to set the reference afterwards
			hashPending.put(g, idSup);
		}
		else supertype.addSubType(g);
		
		g.setSubType(child);
		g.setSuperType(supertype);
		p.addElement(g);
	}
	
	/** Imports the class information and adds it to the package*/
	private void importClass(Element xmiClass, Package p, StaticModel m) throws ImportXMIException
	{
		String name= xmiClass.getAttributeValue("name");
		boolean isAbstract;
		if (xmiClass.getAttributeValue("isAbstract")!=null)
			isAbstract=xmiClass.getAttributeValue("isAbstract").equals("true");
		else isAbstract=false;  //default value
		String vis = xmiClass.getAttributeValue("visibility");
		if (vis==null) vis="public"; //default value
		VisibilityKind visibility=getVisibility(vis);
		Class cl= new Class(name); cl.setAbstract(isAbstract); cl.setVisibility(visibility);
		hash.put(xmiClass.getAttributeValue("id",xmiNamespace),cl);
		p.addElement(cl);
		//Importing the attributes
		List<Element> xmiAttributes= xmiClass.getChildren("ownedAttribute");
		if (xmiAttributes!=null) 
		{
			Iterator <Element> itAt= xmiAttributes.iterator();
			while (itAt.hasNext())
			{
				Element xmiAttribute=itAt.next();
				importAttribute(xmiAttribute,cl,m);
			}
		}
		List<Element> xmiOperations =xmiClass.getChildren("ownedOperation");
		if (xmiOperations!=null)
		{
			Iterator <Element> itOp= xmiOperations.iterator();
			while (itOp.hasNext())
			{
				Element xmiOperation=itOp.next();
				importOperation(xmiOperation,cl,m);
			}
		}
		List<Element> xmiGeneralizations= xmiClass.getChildren("generalization");
		if (xmiGeneralizations!=null) 
		{
			Iterator <Element> itGen= xmiGeneralizations.iterator();
			while (itGen.hasNext())
			{
				Element xmiGeneralization=itGen.next();
				importGeneralization(xmiGeneralization,cl,p);
			}
		}
	}

	
	/** Imports the association class information and adds it to the package*/
	private void importAssociationClass(Element xmiClass, Package p, StaticModel m) throws ImportXMIException
	{
		String name= xmiClass.getAttributeValue("name");
		boolean isAbstract;
		if (xmiClass.getAttributeValue("isAbstract")!=null)
			isAbstract=xmiClass.getAttributeValue(name).equals("true");
		else isAbstract=false;  //default value
		String vis = xmiClass.getAttributeValue("visibility");
		if (vis==null) vis="public"; //default value
		VisibilityKind visibility=getVisibility(vis);
		AssociationClass asCl= new AssociationClass(name); asCl.setAbstract(isAbstract);asCl.setVisibility(visibility);
		hash.put(xmiClass.getAttributeValue("id",xmiNamespace),asCl);
		p.addElement(asCl);
		//Importing the attributes
		List<Element> xmiAttributes= xmiClass.getChildren("ownedAttribute");
		if (xmiAttributes!=null) 
		{
			Iterator <Element> itAt= xmiAttributes.iterator();
			while (itAt.hasNext())
			{
				Element xmiAttribute=itAt.next();
				importAttribute(xmiAttribute,asCl,m);
			}
		}
		List<Element> xmiOperations =xmiClass.getChildren("ownedOperation");
		if (xmiOperations!=null)
		{
			Iterator <Element> itOp= xmiOperations.iterator();
			while (itOp.hasNext())
			{
				Element xmiOperation=itOp.next();
				importOperation(xmiOperation,asCl,m);
			}
		}
		List<Element> xmiGeneralizations= xmiClass.getChildren("generalization");
		if (xmiGeneralizations!=null) 
		{
			Iterator <Element> itGen= xmiGeneralizations.iterator();
			while (itGen.hasNext())
			{
				Element xmiGeneralization=itGen.next();
				importGeneralization(xmiGeneralization,asCl,p);
			}
		}
		//To reuse the importAssociationEnds operation we create a fake association and 
		//pass the association ends to our association class
		Association aux = new Association("auxiliar");
		List<Element> xmiAssociationEnds= xmiClass.getChildren("ownedEnd");
		Iterator <Element> itAE= xmiAssociationEnds.iterator();
		AssociationEnd ae;
		String navigableEnds=xmiClass.getAttributeValue("navigableOwnedEnd");
		while (itAE.hasNext())
		{
			Element xmiAEnd=itAE.next();
			importAssociationEnd(xmiAEnd,aux,navigableEnds);
		}
		
		//Eclipse stores the aggregation property in the opposite association ends to the one that
		//graphically displays the diamond, so we need to interchange their aggreagation value
		//to follow the semantics of my representation (and that of the other tools)
		if (aux.getEnds().size()==2)
		{
			AssociationEnd ae1=aux.getEnds().get(0);
			AssociationEnd ae2=aux.getEnds().get(1);	
			AggregationKind agAux= ae1.getAggregation();
			ae1.setAggregation(ae2.getAggregation());
			ae2.setAggregation(agAux);
		}
		asCl.setEnds(aux.getEnds());
		
	}	


	private String importDataType(Element xmiDataType, StaticModel m) throws ImportXMIException
	{
		//Sometimes datatypes appear as imported elements, we need to deal with both scenarios
		String name="";
		
		if(xmiDataType.getName().equals("elementImport")) 
		{
			Element auxEl=xmiDataType.getChild("importedElement");
			String aux=auxEl.getAttributeValue("href");
			name=aux.substring(aux.indexOf("#")+1,aux.length());
		}
		else //the name can be defined in a name attribute
		{  //or if not, we need to analyze the href as before
			name=xmiDataType.getAttributeValue("name");
			if (name==null) 
			{
				String aux=xmiDataType.getAttributeValue("href");
				name=aux.substring(aux.indexOf("#")+1,aux.length());
			}
		}
		String id=xmiDataType.getAttributeValue("id",xmiNamespace);
		if (id==null){
			int idRandom= (int) (Math.random() * 1000000);
			id= (new Integer(idRandom)).toString() ; //Sometimes the dataype element does not have an id
		}
		DataType aux=ModelFactory.createDataType(name);
		hash.put(id, aux);
		m.addDataType(aux);
		return id;
	}
	
	
	private void importEnumeration(Element xmiEnumeration, StaticModel m)
	{
		String name = xmiEnumeration.getAttributeValue("name");
		Enumeration aux=new Enumeration(name);
		Iterator<Element> xmiLiterals = xmiEnumeration.getChildren("ownedLiteral").iterator();
	    while (xmiLiterals.hasNext()) {
			Element element = (Element) xmiLiterals.next();
			aux.addValue(element.getAttributeValue("name"));
		}
		hash.put(xmiEnumeration.getAttributeValue("id",xmiNamespace), aux);
		m.addDataType(aux);
	}
	
	/** Imports the association information and adds it to the package*/
	private void importAssociation(Element xmiAssociation, Package p, StaticModel m) throws ImportXMIException
	{
		String name= xmiAssociation.getAttributeValue("name");
		Association as= new Association(name);
		hash.put(xmiAssociation.getAttributeValue("id",xmiNamespace),as);
		p.addElement(as);
		List<Element> xmiAssociationEnds= xmiAssociation.getChildren("ownedEnd");
		Iterator <Element> itAE= xmiAssociationEnds.iterator();
		//This property is defined as an additional String in the AssociationElement
		String navigableEnds=xmiAssociation.getAttributeValue("navigableOwnedEnd");
		while (itAE.hasNext())
		{
			Element xmiAEnd=itAE.next();
			importAssociationEnd(xmiAEnd,as,navigableEnds);
		}
		//Eclipse stores the aggregation property in the opposite association ends to the one that
		//graphically displays the diamond, so we need to interchange their aggregation value
		//to follow the semantics of my representation (and that of the other tools)
		if (as.getEnds().size()==2)
		{
			AssociationEnd ae1=as.getEnds().get(0);
			AssociationEnd ae2=as.getEnds().get(1);	
			AggregationKind aux= ae1.getAggregation();
			ae1.setAggregation(ae2.getAggregation());
			ae2.setAggregation(aux);
		}
				
	}
	
	
	/** Imports the attributes information and adds it to the class*/
	private void importAssociationEnd(Element xmiAEnd, Association as, String navigableEnds) throws ImportXMIException
	{
		String name= xmiAEnd.getAttributeValue("name");
		boolean isNavigable=false;
		if (navigableEnds!=null)
			isNavigable=navigableEnds.contains(xmiAEnd.getAttributeValue("id",xmiNamespace));

			
		boolean isStatic;
		if (xmiAEnd.getAttributeValue("isStatic")==null) isStatic=false;
		else isStatic=xmiAEnd.getAttributeValue("isStatic").equals("true");
		  
		String vis = xmiAEnd.getAttributeValue("visibility");
		if (vis==null) vis="public";
		VisibilityKind visibility=getVisibility(vis);
	
		String cha= xmiAEnd.getAttributeValue("isReadOnly");
		if (cha==null) cha="changeable";
		else 
		{ if (cha=="true") cha="changeable";
		  else cha="frozen";
		}
		ChangeabilityKind changeability=getChangeability(cha);
		
		String agg=xmiAEnd.getAttributeValue("aggregation");
		if (agg==null) agg="none";
		AggregationKind aggregation=getAggregationKind(agg);
			
		//Getting the participant type 
		String idType = xmiAEnd.getAttributeValue("type");
		Classifier type = (Classifier) hash.get(idType);
	 
		AssociationEnd ae=  ModelFactory.createAssociationEnd(name, as, type, getMinMultiplicityXML(xmiAEnd),getMaxMultiplicityXML(xmiAEnd), changeability, visibility,aggregation,isStatic, isNavigable);
		if (type==null) 
		{ //We need to set the reference afterwards
			hashPending.put(ae, idType);
		}
		else type.addAssociationEnd(ae);
	}
	
	
	/** Imports the attributes information and adds it to the class*/
	private void importAttribute(Element xmiAttribute, Class cl, StaticModel m) throws ImportXMIException
	{
		String elId= xmiAttribute.getAttributeValue("id");
		String assocId = xmiAttribute.getAttributeValue("association");
		if(assocId==null) //We first check that it´s a real attribute and not an owned AssocEnd
		{
			String name= xmiAttribute.getAttributeValue("name");
			Attribute at= new Attribute(name);
			boolean isStatic;
			if (xmiAttribute.getAttributeValue("isStatic")==null) isStatic=false;
			else isStatic=xmiAttribute.getAttributeValue("isStatic").equals("true");
		  
			String vis = xmiAttribute.getAttributeValue("visibility");
			if (vis==null) vis="public";
			VisibilityKind visibility=getVisibility(vis);
	
			String cha= xmiAttribute.getAttributeValue("isReadOnly");
			if (cha==null) cha="changeable";
			else 
			{ if (cha=="true") cha="changeable";
			else cha="frozen";
			}
			ChangeabilityKind changeability=getChangeability(cha);
		
			Classifier type=null;
			String idType=getIdTypeXML(xmiAttribute,m);
			type = (Classifier) hash.get(idType);
			at.setType(type); at.setSource(cl); at.setMin(getMinMultiplicityXML(xmiAttribute)); at.setMax( getMaxMultiplicityXML(xmiAttribute));
			at.setVisibility(visibility); at.setChangeability(changeability); at.setStatic(isStatic);
			cl.addAttribute(at);
			if (type==null) 
			{ //We need to set the reference afterwards
				hashPending.put(at,idType);
			}
		}
		else
		{
			Association as=(Association) hash.get(assocId); //Check if the association exists
			if (as!=null)
			{
				importAssociationEnd(xmiAttribute,as,elId); //to simplify, we assume that the attribute is navigable
				//otherwise we should locate the association and see in the attribute NavigableEnds of the association this element appears
			}
			else {
				as=new Association("fake");  //we add it to the list of dangling association ends
				importAssociationEnd(xmiAttribute,as,elId); 
				assocEndPending.add(as.getEnds().get(0));
				hashAssocEndPending.put(as.getEnds().get(0), assocId);
			}
		}
	}
	
	/** Imports the Operation information and adds it to the class*/
	private void importOperation(Element xmiOperation, Class cl, StaticModel m) throws ImportXMIException
	{
		String name= xmiOperation.getAttributeValue("name");
		boolean isStatic;
		if (xmiOperation.getAttributeValue("isStatic")==null) isStatic=false;
		else isStatic=xmiOperation.getAttributeValue("isStatic").equals("true");
		boolean isAbstract;
		if (xmiOperation.getAttributeValue("isAbstract")==null) isAbstract=false;
		else isAbstract=xmiOperation.getAttributeValue("isAbstract").equals("true");
		boolean isQuery=false;
		if (xmiOperation.getAttributeValue("isQuery")==null) isQuery=false;
		else isQuery=xmiOperation.getAttributeValue("isQuery").equals("true");
		String vis = xmiOperation.getAttributeValue("visibility");
		if (vis==null) vis="public";
		VisibilityKind visibility=getVisibility(vis);
		Operation op= new Operation(name); op.setSource(cl); op.setVisibility(visibility); 
		op.setStatic(isStatic); op.setQuery(isQuery); op.setAbstract(isAbstract);
		cl.addOperation(op); 
		List<Element> xmiParams =xmiOperation.getChildren("ownedParameter");
		Iterator <Element> itP= xmiParams.iterator();
		while (itP.hasNext())
		{
			Element xmiParameter=itP.next();
			importParametersOperation(xmiParameter,op,m);
		}
	}
	
	/** Imports the Operation information and adds it to the class*/
	private void importParametersOperation(Element xmiParameter, Operation op, StaticModel m) throws ImportXMIException
	{
			String name= xmiParameter.getAttributeValue("name");
			String dir= xmiParameter.getAttributeValue("direction");
			if (dir==null) dir="in";
			DirectionKind direction=getDirection(dir);
			//Getting the type of the parameter
			//Two options: the type can appear as reference attribute in the same XML element
			//or as a <type> child element
			Classifier type=null;
			String idType=getIdTypeXML(xmiParameter,m);
			type = (Classifier) hash.get(idType);		
			//Getting the element corresponding to the reference
			Parameter p=new Parameter(name); p.setType(type);  p.setDirection(direction); p.setOwner(op); 
			p.setMax(getMaxMultiplicityXML(xmiParameter)); p.setMin(getMinMultiplicityXML(xmiParameter));
			op.addParam(p);
			if (type==null) 
			{ //We need to set the reference afterwards
				hashPending.put(p, idType);
			}
	}
	
	public int getMinMultiplicityXML(Element el)
	{
		String min="1"; //default value 
		Element xmiMultMin = el.getChild("lowerValue");
		if (xmiMultMin!=null)
			min=xmiMultMin.getAttributeValue("value");
		if (min==null) min="0"; //In some cases, there is a lowerValue XML element but then there is not "value" attribute in it. It seems that, those cases should be inteprreted as a zero multiplicity
		return Integer.parseInt(min);
	}
	
	public int getMaxMultiplicityXML(Element el)
	{
		String max="1"; //default value 
		Element xmiMultMax = el.getChild("upperValue");
		if (xmiMultMax!=null)
			  max=xmiMultMax.getAttributeValue("value");
		if (max.equals("*")) max=(new Integer(Property.N_Multiplicity)).toString();
		return Integer.parseInt(max);
	}
	
	public String getIdTypeXML(Element el, StaticModel m) throws ImportXMIException
	{
		//Getting the type of the attribute 
		//Two options: the type can appear as reference attribute in the same XML element
		//or as a <type> child element (if none of the two options is true, it means that no type exists
		String idType=el.getAttributeValue("type");
		if (idType==null) 
		{
			Element xmiHasType=el.getChild("type");
			if (xmiHasType!=null) idType= importDataType(xmiHasType,m);
		}
		return idType;
	}
}
