/**
 * 
 */
package mdeServices.xmi.importXMI;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import mdeServices.metamodel.*;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.Package;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;


/**
 *  Import for Visio files
 * 
 * @version 0.1 April 2009
 * @author jcabot
 *
 */
public class ImportXMIVisio extends ImportXMI {

	protected StaticModel m;
	protected HashMap<String, String> hashGeneralizationSupertypes;

    //No namespaces used in the Visio export
    Element enumStereotype;
	/**
	 * @param xmi
	 */
	public ImportXMIVisio() {	super();
		hashGeneralizationSupertypes = new HashMap<String,String>(0);
	}
	
	/** Operation for importing the project info */
	public Project importProjectInfo() throws ImportXMIException
	{   String language=null;
		Element xmiHeading = root.getChild("XMI.header").getChild("XMI.metamodel");
		if (xmiHeading!=null) language= xmiHeading.getAttributeValue("xmi.name");
	  	Project pr = new Project("","","",language);
		return pr;		
	}
	
	public void unsetValidation()
	{
		builder.setEntityResolver(new DummyEntityResolver());
		builder.setValidation(false);
	}
	
	/** Operation for importing the static model info */
	public StaticModel importStaticModel() throws ImportXMIException {
		
		//Creating the model instance
		Element xmiModel=root.getChild("XMI.content").getChild("Model_Management.Model");
		String modelName= xmiModel.getChild("Foundation.Core.ModelElement.name").getText();
		m= new StaticModel(modelName);
		//Recursive preorder traversal of the tree - In Visio there is always at least a top package so we do not need the default package
	
		//Identifying the Enumeration Stereotype to detect datat types that are in fact Enumerations and no simple datat types
		//(in Visio Enumerations are defined as data types with the stereotype enumeration)
		Element xmiDefaultSubsystem=root.getChild("XMI.content").getChild("Model_Management.Subsystem").getChild("Foundation.Core.Namespace.ownedElement");
		enumStereotype=getStereotypeEnumerationElement(xmiDefaultSubsystem.getChildren("Foundation.Extension_Mechanisms.Stereotype"));
		
		//Loading of all default datatypes provided by Visio
		importElements(xmiDefaultSubsystem.getChildren(),null,m);
		
		//Sometimes a visio diagram stores several models. We merge all of them 
		
		Iterator<Element> itEl=root.getChild("XMI.content").getChildren("Model_Management.Model").iterator();
		while (itEl.hasNext()) {
			Element xmiModelAux = (Element) itEl.next();
			
			/*First pass on the generalizations. Generalizations in Visio 2007 do not point to the subtype, 
			to identify the subtype we need to find the class with the same generalization id.
			Therefore, for the import, we first record the generalization id's and the supertype
			Then, when accessing the subtypes we retrieve this information to create the generalization
			<Foundation.Core.GeneralizableElement.specialization>
			<Foundation.Core.Generalization xmi.idref="UID83B1AD75-68BA-4F7B-B2F3-37D300203216"/>
			</Foundation.Core.GeneralizableElement.specialization>*/
			// (all of this because the Generalization Element as an individual information is not always included
			importGeneralizationSupertypes(xmiModelAux.getDescendants(new ElementFilter("Foundation.Core.GeneralizableElement.specialization")));
		        		
		}
		
		Iterator<Element> itEl2=root.getChild("XMI.content").getChildren("Model_Management.Model").iterator();
		while (itEl2.hasNext()) {
			Element xmiModelAux = (Element) itEl2.next();
			//Loading of the model elements
		    importElements(xmiModelAux.getChild("Foundation.Core.Namespace.ownedElement").getChildren(),null,m);
		}
				
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
	
	
	/** Loads information about pairs <GeneralizationID, SupertypeID> in hashGeneralizationSupertype 
	 * 
	 * 
	 */
	protected void importGeneralizationSupertypes(Iterator<Element> gens)
	{
		while (gens.hasNext()) {
			Element element = (Element) gens.next();
			String idSuper= ((Element) element.getParent()).getAttributeValue("xmi.id");
			Iterator<Element> genElements = element.getChildren("Foundation.Core.Generalization").iterator();
			while (genElements.hasNext()) {
				Element element2 = (Element) genElements.next();
				String idGen=element2.getAttributeValue("xmi.idref");
				hashGeneralizationSupertypes.put(idGen,idSuper);
			}
		} 
	}
	
	
	
	/** Returns the Element representing the Enumeration Stereotype */
	protected Element getStereotypeEnumerationElement(List<Element> stereotypes)
	{
		Element enumStereotype=null;
		Iterator<Element> itStereotypes= stereotypes.iterator();
		boolean found=false;		
		while (itStereotypes.hasNext() && !found) {
			Element element = (Element) itStereotypes.next();
			if (getNameXML(element).equals("enumeration"))
			{
				enumStereotype=element;found=true;
			}
		}
		return enumStereotype;
	}
		
	
	protected void importElements(List<Element> elements, ModelElement parent, StaticModel m) throws ImportXMIException
	{
		Iterator<Element> itEl = elements.iterator();
		while (itEl.hasNext()) {
			Element element = (Element) itEl.next();
			if (element.getName().equals("Model_Management.Package"))
				importPackage(element,(Package) parent,m);
			else if (element.getName().equals("Foundation.Core.Class"))
				   importClass(element,(Package) parent,m);
			else if (element.getName().equals("Foundation.Core.Association"))
				importAssociation(element,(Package) parent,m);
			else if (element.getName().equals("Foundation.Core.DataType"))
				 {if (!isEnumeration(element)) importDataType(element,m);
				 else  importEnumeration(element,m);
				 }
		//	else if (element.getName().equals("Generalization"))
				//in case the initial generalization import hasn't found all the generalizations we 
				//have this redundant checking
			//	importGeneralizationAlternative(element,(Package) parent,m);
			else if (element.getName().equals("Foundation.Core.AssociationClass"))
				importAssociationClass(element,(Package) parent,m);
		}
	}
	
	

	//Returns true if the class is in fact an enumeration
	protected boolean isEnumeration(Element el)
	{
		boolean found=false;
		if(enumStereotype!=null)
		{
			Element hasStereotypes=el.getChild("Foundation.Core.ModelElement.stereotype");
			if(hasStereotypes!=null)
			{
				List<Element> stereotypes= hasStereotypes.getChildren("Foundation.Extension_Mechanisms.Stereotype");
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
		Package p= new Package(getNameXML(pack));
		Element xmiPackageContents=pack.getChild("Foundation.Core.Namespace.ownedElement");
		if(xmiPackageContents!=null)
		{
			importElements(xmiPackageContents.getChildren(),p,m); 
			if(p.getElements().size()>0)
			{
				if (parent==null) { m.addPackage(p); p.setModel(m);}
				else parent.addElement(p);
			}
		}
	}
	
	
	
	private void importGeneralizationAlternative(Element xmiGen, ModelElement parent, StaticModel m) throws ImportXMIException
	{
		Class supertype,subtype;
		String id=xmiGen.getAttributeValue("xmi.idref");
		if(!hashGeneralizationSupertypes.containsKey(id)) //has not been found in the first generalization iteration
		{
			String name=xmiGen.getAttributeValue("name");
			String discriminator=xmiGen.getAttributeValue("discriminator");
			Generalization g=new Generalization(name);
			g.setDiscriminator(discriminator);
			//Retrieving the subclass
			Element xmiGenSub= xmiGen.getChild("Foundation.Core.Generalization.child");
			String idSub= xmiGenSub.getChild("Foundation.Core.Class").getAttributeValue("xmi.idref");
			subtype = (Class) hash.get(idSub);
			if (subtype==null) 
			{ //We need to set the reference afterwards
				hashPending.put(g, xmiGenSub.getChild("Foundation.Core.Class").getAttributeValue("xmi.idref"));
			}
			else subtype.addSuperType(g);
			//Retrieving the superclass
			Element xmiGenSup= xmiGen.getChild("Foundation.Core.Generalization.parent");
			String idSup= xmiGenSup.getChild("Foundation.Core.Class").getAttributeValue("xmi.idref");
			supertype = (Class) hash.get(idSup);
			if (supertype==null) 
			{ //We need to set the reference afterwards
				hashPending.put(g, xmiGenSup.getChild("Foundation.Core.Class").getAttributeValue("xmi.idref"));
			}
			else supertype.addSubType(g);
			g.setSubType(subtype);
			g.setSuperType(supertype);
		}
		
	}
	

	private void importGeneralization(String idGen, Package p,Class sub) 
	{
		Class supertype,subtype;
		subtype=sub;
		Generalization g=new Generalization("gen");
		p.addElement(g);
		//Retrieving the subclass
		subtype.addSuperType(g);
		g.setSubType(subtype);
		
		//Retrieving the superclass (through the idGen parameter)
		String idSup= hashGeneralizationSupertypes.get(idGen);
		supertype = (Class) hash.get(idSup);
		if (supertype==null) 
		{ //We need to set the reference afterwards
			hashPending.put(g, idSup);
		}
		else
		{	
			g.setSuperType(supertype);
			supertype.addSubType(g);
		}
		
	}


	/** Imports the class information and adds it to the package*/
	private void importClass(Element xmiClass, Package p, StaticModel m) throws ImportXMIException
	{
		String name= getNameXML(xmiClass);
		if (!isDataType(name))
		{
			boolean isAbstract = getIsAbstractXML(xmiClass).equals("true");
			String vis = getVisibilityXML(xmiClass);
			VisibilityKind visibility=getVisibility(vis);
			Class cl= new Class(name); cl.setAbstract(isAbstract); cl.setVisibility(visibility);
			hash.put(xmiClass.getAttributeValue("xmi.id"),cl);
			p.addElement(cl);
			
			//Importing the generalizations (in which this class appears as subtype
			Element generalizations= xmiClass.getChild("Foundation.Core.GeneralizableElement.generalization");
			if (generalizations!=null)
			{
			  Iterator<Element> itGen=generalizations.getChildren().iterator();
			  while (itGen.hasNext()) {
				Element element = (Element) itGen.next();
				String idGen = element.getAttributeValue("xmi.idref");
				importGeneralization(idGen,p,cl);
			  }
			}
			
			
			
			//Importing the attributes
			Element classFeature= xmiClass.getChild("Foundation.Core.Classifier.feature");
			if (classFeature!=null) 
			{
				List<Element> xmiAttributes =classFeature.getChildren("Foundation.Core.Attribute");
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
				List<Element> xmiOperations =classFeature.getChildren("Foundation.Core.Operation");
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
		String name= getNameXML(xmiAsClass);
		
		boolean isAbstract = getIsAbstractXML(xmiAsClass).equals("true");
		String vis = getVisibilityXML(xmiAsClass);
		VisibilityKind visibility=getVisibility(vis);
		AssociationClass asCl= new AssociationClass(name); asCl.setAbstract(isAbstract);asCl.setVisibility(visibility);
		hash.put(xmiAsClass.getAttributeValue("xmi.id"),asCl);
		p.addElement(asCl);
		
		
				
		//Importing the generalizations (in which this class appears as subtype
		Element generalizations= xmiAsClass.getChild("Foundation.Core.GeneralizableElement.generalization");
		if (generalizations!=null)
		{
		  Iterator<Element> itGen=generalizations.getChildren().iterator();
		  while (itGen.hasNext()) {
			Element element = (Element) itGen.next();
			String idGen = element.getAttributeValue("xmi.idref");
			importGeneralization(idGen,p,asCl);
		  }
		}
		//Importing the attributes
		Element classFeature= xmiAsClass.getChild("Foundation.Core.Classifier.feature");
		if (classFeature!=null) 
		{
			List<Element> xmiAttributes =classFeature.getChildren("Foundation.Core.Attribute");
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
			List<Element> xmiOperations =classFeature.getChildren("Foundation.Core.Operation");
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
		List<Element> xmiAssociationEnds= xmiAsClass.getChild("Foundation.Core.Association.connection").getChildren("Foundation.Core.AssociationEnd");
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
		String name = getNameXML(xmiDataType);
		DataType aux=ModelFactory.createDataType(name);
		hash.put(xmiDataType.getAttributeValue("xmi.id"), aux);
		m.addDataType(aux);
	}


	/* Importing the enumeration. Since it is represented as a class we just access
	 * the class attributes and get their name. Each attribute represents a literal in the enumeration
	 */
	private void importEnumeration(Element xmiEnumeration, StaticModel m)
	{
		String name = getNameXML(xmiEnumeration);
		Enumeration aux=new Enumeration(name);
		//With the current Visio Exporter, enumeration literals are ignored
		//Therefore we cannot import them 
		/*Element classFeature= xmiEnumeration.getChild("Classifier.feature",umlNamespace);
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
		}*/
		hash.put(xmiEnumeration.getAttributeValue("xmi.id"), aux);
		m.addDataType(aux);
	}
	
	/** Imports the association information and adds it to the package*/
	private void importAssociation(Element xmiAssociation, Package p, StaticModel m) throws ImportXMIException
	{
		String name= getNameXML(xmiAssociation);
		Association as= new Association(name);
		hash.put(xmiAssociation.getAttributeValue("xmi.id"),as);
		p.addElement(as);
		List<Element> xmiAssociationEnds= xmiAssociation.getChild("Foundation.Core.Association.connection").getChildren("Foundation.Core.AssociationEnd");
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
		String name= getNameXML(xmiAEnd);
		
		String targetScope= getTargetScopeXML(xmiAEnd);
		boolean isStatic= isStaticVisio(targetScope);
		String vis = getVisibilityXML(xmiAEnd);
		VisibilityKind visibility=getVisibility(vis);
		String cha= getChangeabilityAEndXML(xmiAEnd);
		ChangeabilityKind changeability=getChangeabilityVisio(cha);
		boolean isNavigable= getNavigabilityXML(xmiAEnd).equals("true");
		
		String agg= getAggregabilityXML(xmiAEnd);
		AggregationKind aggregation=getAggregationKindVisio(agg);
		
		//Getting the participant type 
		Element xmiAEType = xmiAEnd.getChild("Foundation.Core.AssociationEnd.type").getChild("Foundation.Core.Class");
		if (xmiAEType==null) //It may be an association involving an association class
		{
			xmiAEType = xmiAEnd.getChild("Foundation.Core.AssociationEnd.type").getChild("Foundation.Core.AssociationClass");
		}
		
		Classifier type = (Classifier) hash.get(xmiAEType.getAttributeValue("xmi.idref"));
	 	//Getting the multiplicity
		int min; int max; 
		String xmiMult = getMultiplicityAEndXML(xmiAEnd);
	    min=getMinMultiplicity(xmiMult);
	    max=getMaxMultiplicity(xmiMult);
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
		String name= getNameXML(xmiAttribute);
		String ownerScope= getOwnerScopeXML(xmiAttribute);
		boolean isStatic= isStaticVisio(ownerScope);
		String vis = getVisibilityXML(xmiAttribute);
		VisibilityKind visibility=getVisibility(vis);
		String cha= getChangeabilityXML(xmiAttribute);
		ChangeabilityKind changeability=getChangeabilityVisio(cha);
		
		//Getting the type of the attribute
		Element xmiHasType = xmiAttribute.getChild("Foundation.Core.StructuralFeature.type");
		Classifier type=null;Element xmiAtType=null;
		if (xmiHasType!=null)
		{
			xmiAtType = xmiHasType.getChild("Foundation.Core.DataType");
			if (xmiAtType==null) //The type of the attribute may be a class in the schema
				xmiAtType = xmiHasType.getChild("Foundation.Core.Class");

			//Getting the element corresponding to the reference
			type = (Classifier) hash.get(xmiAtType.getAttributeValue("xmi.idref"));
			
		}
			
		//Getting the multiplicity. By default, multiplicity is 1 in Visio
		int min; int max; 
		String xmiMult = getMultiplicityXML(xmiAttribute);
	    min=getMinMultiplicity(xmiMult);
	    max=getMaxMultiplicity(xmiMult);
		Attribute at= ModelFactory.createAttribute(name, type, cl, min, max, visibility, changeability, isStatic);
		if (type==null) 
		{ //We need to set the reference afterwards
			hashPending.put(at, xmiAtType.getAttributeValue("xmi.idref"));
		}
	}
	
	/** Imports the Operation information and adds it to the class*/
	private void importOperation(Element xmiOperation, Class cl) throws ImportXMIException
	{
		String name= getNameXML(xmiOperation);
		String ownerScope=getOwnerScopeXML(xmiOperation);
		boolean isStatic= isStaticVisio(ownerScope);
		String vis = getVisibilityXML(xmiOperation);
		VisibilityKind visibility=getVisibility(vis);
		//The attribute isAbstract cannot be defined for operations in Visio
		//boolean isAbstract = xmiOperation.getAttributeValue("isAbstract").equals("true");
		
		boolean isQuery = getIsQueryXML(xmiOperation).equals("true");
		Operation op= new Operation(name); op.setSource(cl); op.setVisibility(visibility); 
		op.setStatic(isStatic); op.setQuery(isQuery);// op.setAbstract(isAbstract);
		cl.addOperation(op); 
		Element paramFeature= xmiOperation.getChild("Foundation.Core.BehavioralFeature.parameter");
		if (paramFeature!=null) 
		{
			List<Element> xmiParams =paramFeature.getChildren("Foundation.Core.Parameter");
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
		String name= getNameXML(xmiParameter);
		String dir= getDirectionXML(xmiParameter);
		DirectionKind direction=getDirectionVisio(dir);
		//Getting the type of the attribute
		
		Element xmiHasType = xmiParameter.getChild("Foundation.Core.Parameter.type");
		Classifier type=null;Element xmiType=null;
		if (xmiHasType!=null)
		{
			xmiType = xmiHasType.getChild("Foundation.Core.DataType");
			if (xmiType==null) //The type of the attribute may be a class in the schema
				xmiType = xmiHasType.getChild("Foundation.Core.Class");

			//Getting the element corresponding to the reference
			type = (Classifier) hash.get(xmiType.getAttributeValue("xmi.idref"));
			
		}
		
		Parameter p=new Parameter(name); p.setType(type);  p.setDirection(direction); p.setOwner(op); 
		op.addParam(p);
		if (type==null) 
		{ //We need to set the reference afterwards
			hashPending.put(p, xmiType.getAttributeValue("xmi.idref"));
		}
	}
	
	
	protected String getNameXML(Element el)
	{
		return el.getChild("Foundation.Core.ModelElement.name").getTextNormalize();
	}
	
	protected String getIsAbstractXML(Element el)
	{
	  return el.getChild("Foundation.Core.GeneralizableElement.isAbstract").getAttributeValue("xmi.value");
	}
	
	protected String getIsQueryXML(Element el)
	{
	  return el.getChild("Foundation.Core.BehavioralFeature.isQuery").getAttributeValue("xmi.value");
	}
	
	protected String getVisibilityXML(Element el)
	{
	  return el.getChild("Foundation.Core.ModelElement.visibility").getAttributeValue("xmi.value");
	}
	
	protected String getOwnerScopeXML(Element el)
	{
	  return el.getChild("Foundation.Core.Feature.ownerScope").getAttributeValue("xmi.value");
	}
	protected String getTargetScopeXML(Element el)
	{
	  return el.getChild("Foundation.Core.AssociationEnd.targetScope").getAttributeValue("xmi.value");
	}
	protected String getMultiplicityXML(Element el)
	{
	  return el.getChild("Foundation.Core.StructuralFeature.multiplicity").getText();
	}
	protected String getMultiplicityAEndXML(Element el)
	{
	  return el.getChild("Foundation.Core.AssociationEnd.multiplicity").getText();
	}
	protected String getChangeabilityXML(Element el)
	{
	  return el.getChild("Foundation.Core.StructuralFeature.changeable").getAttributeValue("xmi.value");
	}
	
	protected String getChangeabilityAEndXML(Element el)
	{
	  return el.getChild("Foundation.Core.AssociationEnd.changeable").getAttributeValue("xmi.value");
	}
	
	protected String getAggregabilityXML(Element el)
	{
	  return el.getChild("Foundation.Core.AssociationEnd.aggregation").getAttributeValue("xmi.value");
	}
	
	
	protected String getNavigabilityXML(Element el)
	{
	  return el.getChild("Foundation.Core.AssociationEnd.isNavigable").getAttributeValue("xmi.value");
	}
	
	protected String getDirectionXML(Element el)
	{
	  return el.getChild("Foundation.Core.Parameter.kind").getAttributeValue("xmi.value");
	}
	
	protected int getMinMultiplicity(String xmlMult)
	{   int min=1;
		if (xmlMult.equals("1")) min =1;
		else if (xmlMult.equals("*")) min =0;
		else if (xmlMult.equals("0..1")) min =0;
		else if (xmlMult.equals("0..*")) min =0;
		else if (xmlMult.equals("1..1")) min =1;
		else if (xmlMult.equals("1..*")) min =1;
		return min;	
	}
	
	protected int getMaxMultiplicity(String xmlMult)
	{   int max=1;
		if (xmlMult.equals("1")) max =1;
		else if (xmlMult.equals("*")) max = Property.N_Multiplicity;
		else if (xmlMult.equals("0..1")) max =1;
		else if (xmlMult.equals("0..*")) max  =Property.N_Multiplicity;
		else if (xmlMult.equals("1..1")) max =1;
		else if (xmlMult.equals("1..*")) max =Property.N_Multiplicity;
		return max;	
	}
	 
	  //We have to translate the String 
	  protected DirectionKind getDirectionVisio(String dir)
	  { 
		 String dirEnglish=dir;
		 if (dir.equals("entrada")) dirEnglish="in";
 		 else if (dir.equals("salida")) dirEnglish="out";
 		 else if (dir.equals("entrada y salida") ) dirEnglish="inout";
 		 else if (dir.equals("retorno")) dirEnglish="return";
 		 return getDirection(dirEnglish);
		  
	  }
	  
	  protected boolean isStaticVisio(String ownerScope)
	  {
		String staEnglish=ownerScope;
		if (ownerScope.toUpperCase().equals("INSTANCIA")) staEnglish="INSTANCE";
		return isStatic(staEnglish);	 
		}
	  
	  protected ChangeabilityKind getChangeabilityVisio(String cha)
		{
		  String chaEnglish=cha;
		  if (cha!=null)
		  {
			if (cha.equals("ninguno")) chaEnglish="changeable";
			else if (cha.equals("inmovilizado")) chaEnglish="frozen";
			else if (cha.equals("solo agregar")) chaEnglish="addOnly";
		  }
		  return getChangeability(chaEnglish);
		}
		
		protected AggregationKind getAggregationKindVisio(String agg)
		{
			String aggEnglish=agg;
			if (agg!=null)
			{
				if (agg.equals("ninguno")) aggEnglish="none";
				else if (agg.equals("compartido")) aggEnglish="aggregate";
				else if (agg.equals("compuesto")) aggEnglish="composite";
			}
			return getAggregationKind(aggEnglish);
		}
}
