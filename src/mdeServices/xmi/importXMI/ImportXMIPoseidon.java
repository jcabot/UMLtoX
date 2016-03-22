/**
 * 
 */
package mdeServices.xmi.importXMI;


import java.util.Iterator;
import java.util.List;
import mdeServices.metamodel.*;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.Package;
import org.jdom.Element;
import org.jdom.Namespace;


/**
 *  Specific import class for ArgoUML v. 024 files
 * 
 * @version 0.1 April 2009
 * @author jcabot
 *
 */
public class ImportXMIPoseidon extends ImportXMI {

	protected StaticModel m;

    Namespace umlNamespace;
    Namespace umlNamespace2; //Poseidon v.5 uses two namespaces one for UML1 and the other for UML2
    Element enumStereotype;
	/**
	 * @param xmi
	 */
	public ImportXMIPoseidon() {	super();	}
	
	/** Operation for importing the project info */
	public Project importProjectInfo() throws ImportXMIException
	{   String language=null;
		Element xmiHeading = root.getChild("XMI.header").getChild("XMI.metamodel");
		if (xmiHeading!=null) language= xmiHeading.getAttributeValue("xmi.name");
	    umlNamespace = root.getNamespace("UML");//$NON-NLS-1$
	    umlNamespace2 = root.getNamespace("UML2");//$NON-NLS-1$
		Project pr = new Project("","","",language);
		return pr;		
	}
	
	/** Operation for importing the static model info */
	public StaticModel importStaticModel() throws ImportXMIException {
		
		//Creating the model instance
		Element xmiModel=root.getChild("XMI.content").getChild("Model",umlNamespace);
		m= new StaticModel(xmiModel.getAttributeValue("name"));
		Element xmiModelContentsRoot= xmiModel.getChild("Namespace.ownedElement", umlNamespace);
     //Recursive preorder traversal of the tree
		Package defaultPackage= new Package("default");
		//Identifying the Enumeration Stereotype to detect classes that are in fact Enumerations and no classes
		//(in Poseidon Enumerations are defined as classes with the stereotype enumeration)
		enumStereotype=getStereotypeEnumerationElement(xmiModelContentsRoot.getChildren("Stereotype",umlNamespace));
		importElements(xmiModelContentsRoot.getChildren(),null,defaultPackage,m);
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
	
	
	/** Returns the Element representing the Enumeration Stereotype */
	protected Element getStereotypeEnumerationElement(List<Element> stereotypes)
	{
		Element enumStereotype=null;
		Iterator<Element> itStereotypes= stereotypes.iterator();
		boolean found=false;		
		while (itStereotypes.hasNext() && !found) {
			Element element = (Element) itStereotypes.next();
			if (element.getAttributeValue("name").equals("enumeration"))
			{
				enumStereotype=element;found=true;
			}
		}
		return enumStereotype;
	}
	
	protected void importElements(List<Element> elements, ModelElement parent, Package defaultPackage, StaticModel m) throws ImportXMIException
	{
		Package realParent; 
		if (parent==null) realParent=defaultPackage;
		else realParent=(Package) parent;
		Iterator<Element> itEl = elements.iterator();
		while (itEl.hasNext()) {
			Element element = (Element) itEl.next();
			if (element.getName().equals("Package"))
				importPackage(element,(Package) parent,m);
			else if (element.getName().equals("Class"))
				 {  if (!isEnumeration(element)) importClass(element,realParent,m);
				    else importEnumeration(element,m);
				 }
			else if (element.getName().equals("Association"))
				importAssociation(element,realParent,m);
			else if (element.getName().equals("DataType"))
				importDataType(element,m);
			else if (element.getName().equals("Generalization"))
				importGeneralization(element,realParent,m);
			else if (element.getName().equals("AssociationClass"))
				importAssociationClass(element,realParent,m);
		}
	}

	//Returns true if the class is in fact an enumeration
	protected boolean isEnumeration(Element el)
	{
		boolean found=false;
		if(enumStereotype!=null)
		{
			Element hasStereotypes=el.getChild("ModelElement.stereotype", umlNamespace);
			if(hasStereotypes!=null)
			{
				List<Element> stereotypes= hasStereotypes.getChildren("Stereotype", umlNamespace);
				Iterator<Element> itStereotypes=stereotypes.iterator();
				while (itStereotypes.hasNext() && !found) {
					Element stereotype= (Element) itStereotypes.next();
					String id= stereotype.getAttributeValue("xmi.idref");
					if (enumStereotype.getAttributeValue("xmi.id").equals(id)) {found=true;}
				}
			}
		}
		return found;
	}
	protected void importPackage(Element pack, Package parent, StaticModel m) throws ImportXMIException
	{
		Package p= new Package(pack.getAttributeValue("name"));
		Element xmiPackageContents=pack.getChild("Namespace.ownedElement", umlNamespace);
		if(xmiPackageContents!=null)
		{
			importElements(xmiPackageContents.getChildren(),p,null,m); //the default package is no longer needed
			if(p.getElements().size()>0)
			{
				if (parent==null) { m.addPackage(p); p.setModel(m);}
				else parent.addElement(p);
			}
		}
	}
	
	
	/** Operation for importing the generalizations info */
	private void importGeneralization(Element xmiGen, Package p, StaticModel m) throws ImportXMIException
	{
		Class supertype,subtype;
		String name=xmiGen.getAttributeValue("name");
		String discriminator=xmiGen.getAttributeValue("discriminator");
		
		Generalization g=new Generalization(name);
		g.setDiscriminator(discriminator);
		
		//Retrieving the subclass
		Element xmiGenSub= xmiGen.getChild("Generalization.child",umlNamespace);
		String idSub= xmiGenSub.getChild("Class",umlNamespace).getAttributeValue("xmi.idref");
		subtype = (Class) hash.get(idSub);
		if (subtype==null) 
		{ //We need to set the reference afterwards
			hashPending.put(g, xmiGenSub.getChild("Class",umlNamespace).getAttributeValue("xmi.idref"));
		}
		else subtype.addSuperType(g);
		
		//Retrieving the superclass
		Element xmiGenSup= xmiGen.getChild("Generalization.parent",umlNamespace);
		String idSup= xmiGenSup.getChild("Class",umlNamespace).getAttributeValue("xmi.idref");
		supertype = (Class) hash.get(idSup);
		if (supertype==null) 
		{ //We need to set the reference afterwards
			hashPending.put(g, xmiGenSup.getChild("Class",umlNamespace).getAttributeValue("xmi.idref"));
		}
		else supertype.addSubType(g);
		g.setSubType(subtype);
		g.setSuperType(supertype);
		p.addElement(g);
	}
	
	/** Imports the class information and adds it to the package*/
	private void importClass(Element xmiClass, Package p, StaticModel m) throws ImportXMIException
	{
		String name= xmiClass.getAttributeValue("name");
		if (!isDataType(name))
		{
			boolean isAbstract = xmiClass.getAttributeValue("isAbstract").equals("true");
			String vis = xmiClass.getAttributeValue("visibility");
			VisibilityKind visibility=null;
			if(vis!=null)
			{	if (vis.equals("public")) visibility = VisibilityKind.V_PUBLIC;
				else if (vis.equals("protected")) visibility = VisibilityKind.V_PROTECTED;
					else if (vis.equals("private")) visibility = VisibilityKind.V_PRIVATE;
			}
			Class cl= new Class(name); cl.setAbstract(isAbstract); cl.setVisibility(visibility);
			hash.put(xmiClass.getAttributeValue("xmi.id"),cl);
			p.addElement(cl);
			//Importing the attributes
			Element classFeature= xmiClass.getChild("Classifier.feature",umlNamespace);
			if (classFeature!=null) 
			{
				List<Element> xmiAttributes =classFeature.getChildren("Attribute",umlNamespace);
				if (xmiAttributes!=null)
				{
					Iterator <Element> itAt= xmiAttributes.iterator();
					Attribute at;
					while (itAt.hasNext())
					{
						Element xmiAttribute=itAt.next();
						importAttribute(xmiAttribute,cl);
					}
				}
				List<Element> xmiOperations =classFeature.getChildren("Operation",umlNamespace);
				if (xmiOperations!=null)
				{
					Iterator <Element> itOp= xmiOperations.iterator();
					Operation op;
					while (itOp.hasNext())
					{
						Element xmiOperation=itOp.next();
						importOperation(xmiOperation,cl);
					}
				}
			}
		}
		//Some datatypes are represented as classes
		else importDataType(xmiClass, m);
	}

	
	/** Imports the association class information and adds it to the package*/
	private void importAssociationClass(Element xmiAsClass, Package p, StaticModel m) throws ImportXMIException
	{
		String name= xmiAsClass.getAttributeValue("name");
		boolean isAbstract = xmiAsClass.getAttributeValue("isAbstract").equals("true");
		String vis = xmiAsClass.getAttributeValue("visibility");
		VisibilityKind visibility=getVisibility(vis);
		AssociationClass asCl= new AssociationClass(name); asCl.setAbstract(isAbstract);asCl.setVisibility(visibility);
		hash.put(xmiAsClass.getAttributeValue("xmi.id"),asCl);
		p.addElement(asCl);
		Element classFeature= xmiAsClass.getChild("Classifier.feature",umlNamespace);
		if (classFeature!=null) 
		{
			List<Element> xmiAttributes =classFeature.getChildren("Attribute",umlNamespace);
			if (xmiAttributes!=null)
			{
				Iterator <Element> itAt= xmiAttributes.iterator();
				Attribute at;
				while (itAt.hasNext())
				{
					Element xmiAttribute=itAt.next();
					importAttribute(xmiAttribute,asCl);
				}
			}
			List<Element> xmiOperations =classFeature.getChildren("Operation",umlNamespace);
			if (xmiOperations!=null)
			{
				Iterator <Element> itOp= xmiOperations.iterator();
				Operation op;
				while (itOp.hasNext())
				{
					Element xmiOperation=itOp.next();
					importOperation(xmiOperation,asCl);
				}
			}
		}
		//To reuse the importAssociationEnds operation we create a fake association and 
		//pass the association ends to our association class
		Association aux = new Association("auxiliar");
		List<Element> xmiAssociationEnds= xmiAsClass.getChild("Association.connection",umlNamespace).getChildren("AssociationEnd",umlNamespace);
		Iterator <Element> itAE= xmiAssociationEnds.iterator();
		AssociationEnd ae;
		while (itAE.hasNext())
		{
			Element xmiAEnd=itAE.next();
			importAssociationEnd(xmiAEnd,aux);
		}
		asCl.setEnds(aux.getEnds());
		
	}	


	private void importDataType(Element xmiDataType, StaticModel m) throws ImportXMIException
	{
		String name = xmiDataType.getAttributeValue("name");
		DataType aux=ModelFactory.createDataType(name);
		hash.put(xmiDataType.getAttributeValue("xmi.id"), aux);
		m.addDataType(aux);
	}


	/* Importing the enumeration. Since it is represented as a class we just access
	 * the class attributes and get their name. Each attribute represents a literal in the enumeration
	 */
	private void importEnumeration(Element xmiEnumeration, StaticModel m)
	{
		String name = xmiEnumeration.getAttributeValue("name");
		Enumeration aux=new Enumeration(name);
		Element classFeature= xmiEnumeration.getChild("Classifier.feature",umlNamespace);
		if (classFeature!=null) 
		{
			List<Element> xmiAttributes =classFeature.getChildren("Attribute",umlNamespace);
			if (xmiAttributes!=null)
			{
				Iterator <Element> itAt= xmiAttributes.iterator();
				while (itAt.hasNext())
				{
					Element xmiAttribute=itAt.next();
					aux.addValue(xmiAttribute.getAttributeValue("name"));
				}
			}
		}
		hash.put(xmiEnumeration.getAttributeValue("xmi.id"), aux);
		m.addDataType(aux);
	}
	
	/** Imports the association information and adds it to the package*/
	private void importAssociation(Element xmiAssociation, Package p, StaticModel m) throws ImportXMIException
	{
		String name= xmiAssociation.getAttributeValue("name");
		Association as= new Association(name);
		hash.put(xmiAssociation.getAttributeValue("xmi.id"),as);
		p.addElement(as);
		List<Element> xmiAssociationEnds= xmiAssociation.getChild("Association.connection",umlNamespace).getChildren("AssociationEnd",umlNamespace);
		Iterator <Element> itAE= xmiAssociationEnds.iterator();
		AssociationEnd ae;
		while (itAE.hasNext())
		{
			Element xmiAEnd=itAE.next();
			importAssociationEnd(xmiAEnd,as);
		}
	}
	
	
	/** Imports the attributes information and adds it to the class*/
	private void importAssociationEnd(Element xmiAEnd, Association as) throws ImportXMIException
	{
		String name= xmiAEnd.getAttributeValue("name");
		boolean isStatic= xmiAEnd.getAttributeValue("targetScope").equals("instance");
		boolean isNavigable= xmiAEnd.getAttributeValue("isNavigable").equals("true");
		
		String cha= xmiAEnd.getAttributeValue("changeability");
		ChangeabilityKind changeability=getChangeability(cha);
	
		String agg= xmiAEnd.getAttributeValue("aggregation");
		AggregationKind aggregation=getAggregationKind(agg);
		
		String vis = xmiAEnd.getAttributeValue("visibility");
		VisibilityKind visibility=getVisibility(vis);
			
		//Getting the participant type 
		Element xmiAEType = xmiAEnd.getChild("AssociationEnd.participant",umlNamespace).getChild("Class",umlNamespace);
		if (xmiAEType==null) //It may be an association involving an association class
		{
			xmiAEType = xmiAEnd.getChild("AssociationEnd.participant",umlNamespace).getChild("AssociationClass",umlNamespace);
			}
		Classifier type = (Classifier) hash.get(xmiAEType.getAttributeValue("xmi.idref"));
	 	//Getting the multiplicity
	    Element xmiMult = xmiAEnd.getChild("AssociationEnd.multiplicity",umlNamespace).getChild("Multiplicity",umlNamespace).getChild("Multiplicity.range",umlNamespace).getChild("MultiplicityRange",umlNamespace);
		int min= Integer.parseInt(xmiMult.getAttributeValue("lower"));
		int max= Integer.parseInt(xmiMult.getAttributeValue("upper"));
		AssociationEnd ae= ModelFactory.createAssociationEnd(name, as, type, min, max, changeability, visibility
				,aggregation,isStatic, isNavigable);
		if (type==null) 
		{ //We need to set the reference afterwards
			hashPending.put(ae, xmiAEType.getAttributeValue("xmi.idref"));
		}
		else type.addAssociationEnd(ae);
	}
	
	
	/** Imports the attributes information and adds it to the class*/
	private void importAttribute(Element xmiAttribute, Class cl) throws ImportXMIException
	{
		String name= xmiAttribute.getAttributeValue("name");
		boolean isStatic= !(xmiAttribute.getAttributeValue("ownerScope").equals("instance"));
		String vis = xmiAttribute.getAttributeValue("visibility");
		VisibilityKind visibility=getVisibility(vis);
		String cha= xmiAttribute.getAttributeValue("changeability");
		ChangeabilityKind changeability=getChangeability(cha);
		
		//Getting the type of the attribute
		Classifier type=null;Element xmiAtType=null;
		Element xmiHasType=xmiAttribute.getChild("TypedElement.type",umlNamespace2);
		if (xmiHasType!=null)
		{
			xmiAtType = xmiHasType.getChild("DataType",umlNamespace);
			if (xmiAtType==null) //The type of the attribute may be a class (including enumeration classes) in the schema
				xmiAtType = xmiAttribute.getChild("TypedElement.type",umlNamespace2).getChild("Class",umlNamespace);
			//Getting the element corresponding to the reference
			type = (Classifier) hash.get(xmiAtType.getAttributeValue("xmi.idref"));
		}
		//Getting the multiplicity
		int min; 
		int max;
		try{
			Element xmiMult = xmiAttribute.getChild("StructuralFeature.multiplicity",umlNamespace).getChild("Multiplicity",umlNamespace).getChild("Multiplicity.range",umlNamespace).getChild("MultiplicityRange",umlNamespace);
			min= Integer.parseInt(xmiMult.getAttributeValue("lower"));
			max= Integer.parseInt(xmiMult.getAttributeValue("upper"));
		}catch(Exception e){min=1; max=1;}
		Attribute at= ModelFactory.createAttribute(name, type, cl, min, max, visibility, changeability, isStatic);
		if (type==null) 
		{ //We need to set the reference afterwards
			hashPending.put(at, xmiAtType.getAttributeValue("xmi.idref"));
		}
	}
	
	/** Imports the Operation information and adds it to the class*/
	private void importOperation(Element xmiOperation, Class cl) throws ImportXMIException
	{
		String name= xmiOperation.getAttributeValue("name");
		boolean isStatic= !(xmiOperation.getAttributeValue("ownerScope").equals("instance"));
		String vis = xmiOperation.getAttributeValue("visibility");
		VisibilityKind visibility=getVisibility(vis);
		boolean isAbstract = xmiOperation.getAttributeValue("isAbstract").equals("true");
		boolean isQuery = xmiOperation.getAttributeValue("isQuery").equals("true");
		Operation op= new Operation(name); op.setSource(cl); op.setVisibility(visibility); 
		op.setStatic(isStatic); op.setQuery(isQuery); op.setAbstract(isAbstract);
		cl.addOperation(op); 
		Element paramFeature= xmiOperation.getChild("BehavioralFeature.parameter",umlNamespace);
		if (paramFeature!=null) 
		{
			List<Element> xmiParams =paramFeature.getChildren("Parameter",umlNamespace);
			Iterator <Element> itP= xmiParams.iterator();
			while (itP.hasNext())
			{
				Element xmiParameter=itP.next();
				importParametersOperation(xmiParameter,op);
			}
		}
	}
	
	/** Imports the Operation information and adds it to the class*/
	private void importParametersOperation(Element xmiParameter, Operation op) throws ImportXMIException
	{
			String name= xmiParameter.getAttributeValue("name");
			String dir= xmiParameter.getAttributeValue("kind");
			DirectionKind direction=getDirection(dir);
			//Getting the type of the attribute
			Element xmiPType = xmiParameter.getChild("TypedElement.type",umlNamespace2).getChild("DataType",umlNamespace);
			if (xmiPType==null) //The type of the attribute may be a class (including enumeration classes) in the schema
			xmiPType = xmiParameter.getChild("TypedElement.type",umlNamespace2).getChild("Class",umlNamespace);
			//Getting the element corresponding to the reference
			Classifier type = (Classifier) hash.get(xmiPType.getAttributeValue("xmi.idref"));
			Parameter p=new Parameter(name); p.setType(type);  p.setDirection(direction); p.setOwner(op); 
			op.addParam(p);
			if (type==null) 
			{ //We need to set the reference afterwards
				hashPending.put(p, xmiPType.getAttributeValue("xmi.idref"));
			}
	}
	

}
