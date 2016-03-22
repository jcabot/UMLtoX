package mdeServices.transformations.database;

import java.util.Iterator;

import mdeServices.metamodel.Association;
import mdeServices.metamodel.AssociationClass;
import mdeServices.metamodel.AssociationEnd;
import mdeServices.metamodel.Attribute;
import mdeServices.metamodel.ChangeabilityKind;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.Classifier;
import mdeServices.metamodel.Generalization;
import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.VisibilityKind;
import mdeServices.metamodel.stereotypes.E_ForeignKeyEventKind;
import mdeServices.metamodel.stereotypes.S_ForeignKey;
import mdeServices.metamodel.stereotypes.S_PrimaryKey;
import mdeServices.options.Options;
import mdeServices.transformations.Transformation;
import mdeServices.transformations.TransformationNotApplicable;

/**
 * Transformation class for removing generalizations from the model 
 *  
 * @author jcabot
 *
 */


public class T_NoGeneralizations extends Transformation {
	
	//Model where to apply the transformation
	protected StaticModel m;
	
	public T_NoGeneralizations(Project p, Options o) throws TransformationNotApplicable {
		super(p, o);
		this.m=(StaticModel) p.getStaticModel();
		this.o=o;
	}
	
	
	public void exec() throws TransformationNotApplicable
	{
		try
		{
			
			String howTo= o.getProperty("db.gen.handling");
			if (howTo.equals("all"))
			{
				Iterator<Class> itCl=m.getAllRootClassesNoAssCl().iterator();
				while (itCl.hasNext())
				{
					Class cl=itCl.next();
					generateAll(cl);
					
				}
			}
			else if (howTo.equals("subtype") )
			{
				Iterator<Class> itCl=m.getAllRootClassesNoAssCl().iterator();
				while (itCl.hasNext())
				{
					Class cl=itCl.next();
					generateSub(cl);
				}
			}
			else if (howTo.equals("supertype")) 
			{
				Iterator<Class> itCl=m.getAllLeafClasses().iterator();
				while (itCl.hasNext())
				{
					Class cl=itCl.next();
					generateSuper(cl);
				}
			}
			else throw new TransformationNotApplicable("Property for collapsing generalizations not defined");
		}catch(TransformationNotApplicable t) {throw t;
		}catch(Exception e)
		{ throw new TransformationNotApplicable("Error when removing the generalization relationships");}
	}
	
	
	protected void generateAll(Class superCl)
	{
		Iterator<Generalization> itSub = superCl.getSubCl().iterator();
		while (itSub.hasNext())
		{
		  Generalization g=itSub.next();
		  Classifier sub=g.getSubType();
		  S_PrimaryKey s= new S_PrimaryKey();
		  S_ForeignKey f= new S_ForeignKey();
		  f.setGeneralization(true);
		  f.setReferencedClass(superCl);
		  f.setOnDelete(E_ForeignKeyEventKind.getKind(o.getProperty("db.fk.ondelete")));
		  f.setOnUpdate(E_ForeignKeyEventKind.getKind(o.getProperty("db.fk.onupdate")));
		  Attribute atPk= superCl.getPrimaryKeyAttribute();
		  Attribute a= new Attribute(atPk.getName());
		  //check whether we need to add the name of the class as a prefix. Mandatory in django
		  if (new Boolean(o.getProperty("db.pk.subtype.name.add")).booleanValue())
			  a.setName(sub.getName()+"_"+a.getName());
		  sub.addAttributeBeginning(a);
		  a.setChangeability(ChangeabilityKind.C_CHANGEABLE);
		  a.setMax(1);
		  a.setMin(1);
		  a.setSource(sub);
		  a.setStatic(false);
		  a.setVisibility(VisibilityKind.V_PUBLIC);
		  a.setType(m.findDataType("Integer"));
		  s.setRefAtt(a); //Adding the new attribute as a PK
		  f.addAtt(atPk, a); //Adding the FK to the supertype
		  f.setOppositeMany(false); //in a inheritace relationship this must be one-to-one
		  f.setOppositeName(sub.getName()); //The opposite name is simply the name of the subclass
		  f.setName(superCl.getName());
		  ((Class) sub).setPrimaryKey(s);
		  ((Class) sub).addForeignKey(f);
		  sub.removeSuperType(g);
		  //If the taxonomy has multiple levels we propagate the changes 
		  if (sub.getSubCl().size()>0) generateAll((Class) sub); 
		}
		
		superCl.removeSubCl();
	}
		
	protected void generateSuper(Class sub)
	{	
		Iterator<Generalization> itSuper = sub.getSuperCl().iterator();
		while (itSuper.hasNext())
		{
		  Generalization g=itSuper.next();
          Classifier superCl=g.getSuperType();	
          Iterator<Attribute> itAt = sub.getAtt().iterator();
		  while (itAt.hasNext())
		  {
			  Attribute at=itAt.next();
			  at.setSource(superCl);
			  superCl.addAttribute(at);
			  //The minimum multiplicity is now zero (we have superclass instances that are not instances of that specific subtype)
			  at.setMin(0);
		  }
		  Iterator<AssociationEnd> itAE = sub.getAss().iterator();
		  while (itAE.hasNext())
		  {
			  AssociationEnd ae=itAE.next();
			  ae.setSource(superCl);
			  superCl.addAssociationEnd(ae);
			  
			  //The minimum multiplicity of the opposite association end is now zero (we have superclass instances that are not instances of that specific subtype)
			  AssociationEnd opp=null;
			  if (ae.getAss() instanceof Association)
			  {  opp= ((Association) ae.getAss()).oppositeEnd(ae);}
			  else 
			  {
				 opp= ((AssociationClass) ae.getAss()).oppositeEnd(ae);
			  }
			  opp.setMin(0);
		  }
		  ((Class) superCl).addForeignKeys(sub.getForeignKeys());
		  ((Class) superCl).addUniqueKeys(sub.getUniqueKeys());
		  superCl.removeSubType(g);
		  superCl.addRefines(sub); //Adding the trace information 
		  superCl.addRefinesAll(sub.getRefines()); //Adding the trace information 
		  
		  //Creation of a type attribute to identify the subclass of each row in the supertype
		  if (superCl.getSubCl().size()==0 && o.getProperty("db.table.discriminator.attribute").equals("true"))
		  {
			String name=g.getDiscriminator();
			if (name==null) name =  o.getProperty("db.table.discriminator.attribute.name");
			Attribute a= new Attribute(name);
			a.setSource(superCl);
			superCl.addAttribute(a);
			a.setMin(0);
			a.setMax(1);
			a.setChangeability(ChangeabilityKind.C_CHANGEABLE);
			a.setStatic(false);
			a.setVisibility(VisibilityKind.V_PUBLIC);
			a.setType(m.findDataType("String"));
		  }
		  if (superCl.getSuperCl().size()>0 && superCl.getSubCl().size()==0) generateSuper((Class) superCl); 
		  sub.removeSuperCl();
		  sub.getInPackage().removeElement(sub);
		}
	}
		  
	
	protected void generateSub(Class superCl)
	{	
	    Iterator<Attribute> itAt = superCl.getAtt().iterator();
		while (itAt.hasNext())
		{
			Attribute at=itAt.next();
			Iterator<Generalization> itSub = superCl.getSubCl().iterator();
			while (itSub.hasNext())
			{
				Generalization g=itSub.next();
				Class sub=(Class) g.getSubType();
				Attribute a= at.clone();
				sub.addAttribute(a);
				a.setSource(sub);
			}
		}
		
		//We have to clone each association
		Iterator<AssociationEnd> itAE = superCl.getAss().iterator();
		while (itAE.hasNext())
		{
			AssociationEnd ae=itAE.next();
			if (ae.getAss() instanceof Association && !( ((Association) ae.getAss()).isReflexive(ae.getSource())))
			{
				Association a= (Association) ae.getAss(); 
				Iterator<Generalization> itSub = superCl.getSubCl().iterator();
				while (itSub.hasNext())
				{
					Classifier sub=itSub.next().getSubType();
					Association asNew = a.clone();
					//For MN associations we have to change the name to avoid that the table that will 
					//be generated afterwards for each new association has a repeated name
					if (asNew.isMN()) asNew.setName(asNew.getName()+sub.getName());
					Iterator<AssociationEnd> aeNew=asNew.getEnds().iterator();
					while (aeNew.hasNext()) {
						AssociationEnd associationEnd = (AssociationEnd) aeNew.next();
						if (associationEnd.getSource()==superCl) //Replace with the subtype
						{
							sub.addAssociationEnd(associationEnd);
							associationEnd.setSource(sub);
						}
					}
				}
				//Removal of the old association
				a.getInPackage().removeElement(a);
				Iterator<AssociationEnd> itAE2= a.getEnds().iterator();
				while (itAE2.hasNext()) {
					AssociationEnd associationEnd = (AssociationEnd) itAE2.next();
					//The ends linked to the superCl will be removed when removing the superCl 
					//removing them here would genereate a concurrent modification exception
					if(associationEnd!=ae) associationEnd.getSource().removeAssociationEnd(associationEnd);
				}
			}
			else if (ae.getAss() instanceof AssociationClass && !( ((AssociationClass) ae.getAss()).isReflexive(ae.getSource())))//Association is an association class
			{
				AssociationClass a= (AssociationClass) ae.getAss(); 
				Iterator<Generalization> itSub = superCl.getSubCl().iterator();
				while (itSub.hasNext())
				{
					Classifier sub=itSub.next().getSubType();
					AssociationClass asNew = a.clone();
					asNew.setName(asNew.getName()+sub.getName());
					Iterator<AssociationEnd> aeNew=asNew.getEnds().iterator();
					while (aeNew.hasNext()) {
						AssociationEnd associationEnd = (AssociationEnd) aeNew.next();
						if (associationEnd.getSource()==superCl) //Replace with the subtype
						{
							sub.addAssociationEnd(associationEnd);
							associationEnd.setSource(sub);
						}
					}
				}
				//Removal of the old association
				a.getInPackage().removeElement(a);
				Iterator<AssociationEnd> itAE2= a.getEnds().iterator();
				while (itAE2.hasNext()) {
					AssociationEnd associationEnd = (AssociationEnd) itAE2.next();
					if(associationEnd!=ae) associationEnd.getSource().removeAssociationEnd(associationEnd);
				}
			}
		}
		    
		//Reflexive associations need a special treatment because there is more than one association end
		//with the class as a source
		    
		Iterator<Association> itAs = superCl.getReflexiveAssNoAssCl().iterator();
		while (itAs.hasNext())
		{
			Association a =itAs.next();
			Iterator<Generalization> itSub = superCl.getSubCl().iterator();
			while (itSub.hasNext())
			{
				Classifier sub=itSub.next().getSubType();
				Association asNew = a.clone();
				//For MN associations we have to change the name to avoid that the table that will 
				//be generated afterwards for each new association has a repeated name
				if (asNew.isMN()) asNew.setName(asNew.getName()+sub.getName());
				Iterator<AssociationEnd> aeNew=asNew.getEnds().iterator();
				while (aeNew.hasNext()) {
					AssociationEnd associationEnd = (AssociationEnd) aeNew.next();
					if (associationEnd.getSource()==superCl) //Replace with the subtype
					{
						sub.addAssociationEnd(associationEnd);
						associationEnd.setSource(sub);
					}
				}
			}
				
			//Removal of the old association
			a.getInPackage().removeElement(a);
			Iterator<AssociationEnd> itAE2= a.getEnds().iterator();
			while (itAE2.hasNext()) {
				AssociationEnd associationEnd = (AssociationEnd) itAE2.next();
				if(associationEnd.getSource()!=superCl) associationEnd.getSource().removeAssociationEnd(associationEnd);
			}
		}
		
		//Reflexive associations classes
		    
		Iterator<AssociationClass> itAsCl = superCl.getReflexiveAssCl().iterator();
		while (itAsCl.hasNext())
		{
			AssociationClass a =itAsCl.next();
			Iterator<Generalization> itSub = superCl.getSubCl().iterator();
			while (itSub.hasNext())
			{
				Classifier sub=itSub.next().getSubType();
				AssociationClass asNew = a.clone();
				//For MN associations we have to change the name to avoid that the table that will 
				//be generated afterwards for each new association has a repeated name
				asNew.setName(asNew.getName()+sub.getName());
				Iterator<AssociationEnd> aeNew=asNew.getEnds().iterator();
				while (aeNew.hasNext()) {
					AssociationEnd associationEnd = (AssociationEnd) aeNew.next();
					if (associationEnd.getSource()==superCl) //Replace with the subtype
					{
						sub.addAssociationEnd(associationEnd);
						associationEnd.setSource(sub);
					}
				}
			}
				
			//Removal of the old association
			a.getInPackage().removeElement(a);
			Iterator<AssociationEnd> itAE2= a.getEnds().iterator();
			while (itAE2.hasNext()) {
				AssociationEnd associationEnd = (AssociationEnd) itAE2.next();
				if(associationEnd.getSource()!=superCl) associationEnd.getSource().removeAssociationEnd(associationEnd);
			}
		}
		
		//Copy of the primary key and recursive call for the rest of the tree
		Iterator<Generalization> itSub = superCl.getSubCl().iterator();
		while (itSub.hasNext())
		{ 
			Class sub= (Class) itSub.next().getSubType();
			S_PrimaryKey s=superCl.getPrimaryKey().clone();
			sub.setPrimaryKey(s);
			//After the cloning "s" still references the attributes of the superclass
			//we need to change the reference to make s attributes to point to the new attribute in sub
			s.setRefAtt(sub.getAttributeByName(s.getRefAtt().getName()));
			
			sub.moveUpPKAttribute(); //Putting the PK attribute as the first one
			sub.addRefines(superCl); //Adding the trace information 
			sub.addRefinesAll(superCl.getRefines());
			if (sub.getSubCl().size()>0) 
		    	generateSub((Class) sub); 
		}
	    superCl.removeSubCl();
		superCl.getInPackage().removeElement(superCl);
		
	}

}
