package mdeServices.transformations;

import java.util.Iterator;

import mdeServices.metamodel.Association;
import mdeServices.metamodel.AssociationClass;
import mdeServices.metamodel.AssociationEnd;
import mdeServices.metamodel.Classifier;
import mdeServices.metamodel.Project;
import mdeServices.metamodel.Property;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.AggregationKind;
import mdeServices.metamodel.Package;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.stereotypes.S_ForeignKey;
import mdeServices.metamodel.stereotypes.S_ReifiedAssociation;
import mdeServices.options.Options;


/**
 * Association classes are transformed to regular classes. The association ends
 * are transformed into a set of 1:N associations marked with the stereotype Identifier
 * 
 * @version 0.1 Sep 2008
 * @author jcabot
 *
 */

public class T_AssocClassToClass extends Transformation{
	
	
	//Model where to apply the transformation
	protected StaticModel m;


	public T_AssocClassToClass(Project p, Options o) throws TransformationNotApplicable {
		super(p, o);
		this.m=(StaticModel) p.getStaticModel();
		
	}
	
	//Transforms the ends of the association class in 1:M or 1:1 associations
	//and the class into a normal class
	public void exec() throws TransformationNotApplicable
	{
	  try{
		  
	  	Iterator<Package> itP = m.getPackages().iterator();
		Iterator<AssociationClass> itCl;
		while (itP.hasNext())
		{
			Package p=itP.next();
			itCl=p.getAllAssociationClasses().iterator();
			while (itCl.hasNext())
			{
				AssociationClass cl=itCl.next();
				Class newCl= new Class(cl.getName());
				Iterator<AssociationEnd> itEnds=cl.getEnds().iterator();
				while (itEnds.hasNext())
				{
					AssociationEnd aEnd= itEnds.next();
					Association a = new Association(aEnd.getName().concat(cl.getName()));
					//AssociationEnd endAsClass = new AssociationEnd(cl.getName());
					//maybe makes more sense to put as an association end name the name of the association end in the other end
					//then the code would be: 
					AssociationEnd endAsClass = new AssociationEnd(cl.oppositeEnd(aEnd).getName());
					endAsClass.setAss(a);
					AssociationEnd endTargetClass = new AssociationEnd(aEnd.getName());
					endTargetClass.setAss(a);
					a.addAssociationEnd(endAsClass);
					a.addAssociationEnd(endTargetClass);
					a.setInPackage(p); p.addElement(a);
					a.setComments(cl.getComments());
					a.setReification(true); //This association has been created due to a reification
					
					
					endTargetClass.setIdentifier(true);
					//all Instances of the association class must be related to an instance of the target class
					endTargetClass.setMin(1);
					endTargetClass.setMax(1);
					endTargetClass.setAggregation(AggregationKind.A_NONE);
					endTargetClass.setNavigable(aEnd.isNavigable());
					Classifier targetClass=aEnd.getSource();
					endTargetClass.setSource(targetClass); targetClass.addAssociationEnd(endTargetClass);
					endTargetClass.setAss(a);
					targetClass.removeAssociationEnd(aEnd);
					
					//Instances of the target class are related to N elements of the 
					//association class if its a N-ary association or one of the other ends has a N multiplicity
					//Similarly the min multiplicity also depends on the other ends
					//However currently we just assign the most probable situation min=0, max=N
					endAsClass.setMin(0);
					endAsClass.setMax(Property.N_Multiplicity);
					endAsClass.setAggregation(AggregationKind.A_NONE);
					endAsClass.setNavigable(true);
					endAsClass.setSource(cl); cl.addAssociationEnd(endAsClass);
					endAsClass.setAss(a);
					
					//Keeping some information on the reification required for the doctrine and django generation
					//only for binary associations
					if(!a.isNary())
					{
						S_ReifiedAssociation reifiedInfo=new S_ReifiedAssociation();
						reifiedInfo.setReferencedClass((Class) cl.oppositeEnd(aEnd).getSource());
						reifiedInfo.setAssociationClass(newCl);
						reifiedInfo.setOppositeEnd(cl.oppositeEnd(aEnd));
						reifiedInfo.setOwnEnd(aEnd);
						//adding the name of the association end is important to avoid duplicates in reflexive associations
						if (cl.isReflexive(targetClass))
						{
						  String name=	cl.oppositeEnd(aEnd).getName();
						  reifiedInfo.setName(cl.getName()+ name.substring(0, 1).toUpperCase()+ name.substring(1,name.length()));
						}
						  else  reifiedInfo.setName(cl.getName());
						((Class) targetClass).addReifiedAssociation(reifiedInfo);
					}
				}
				
				((Class) cl).replaceWith(newCl);
				newCl.setReified(true);
				
			}
		}
	  }catch(Exception e){throw new TransformationNotApplicable("Error when converting association classes to classes");}
	}
}
