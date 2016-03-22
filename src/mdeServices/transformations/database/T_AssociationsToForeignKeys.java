package mdeServices.transformations.database;

import java.util.Iterator;
import java.util.Vector;

import mdeServices.metamodel.Association;
import mdeServices.metamodel.AssociationEnd;
import mdeServices.metamodel.Attribute;
import mdeServices.metamodel.Package;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.stereotypes.E_ForeignKeyEventKind;
import mdeServices.metamodel.Project;
import mdeServices.metamodel.Property;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.database.MultiplicityTrigger;
import mdeServices.metamodel.database.TriggerEventKind;
import mdeServices.metamodel.database.TriggerMomentKind;
import mdeServices.metamodel.stereotypes.S_ForeignKey;
import mdeServices.metamodel.stereotypes.S_Unique;
import mdeServices.options.Options;
import mdeServices.transformations.Transformation;
import mdeServices.transformations.TransformationNotApplicable;
 
/**
 *  Class for transforming 1:N associations into foreign key attributes
 *  
 *  It is assumed that M:N associations and n-ary associations have been already transformed into association classes
 *  
 *  It is also assumed that association classes have been already normalized
 *  
 *  If the association is identifier for one of the participants a Unique stereotype is added 
 *  The stereotype is attached to the association end in A
 * 
 * @version 0.1 Sep 2008
 * @author jcabot
 *
 */

public class T_AssociationsToForeignKeys extends Transformation {
	
	//Model where to apply the transformation
	protected StaticModel m;

	public T_AssociationsToForeignKeys(Project p, Options o) throws TransformationNotApplicable 
	{
		super(p, o);
		this.m=(StaticModel) p.getStaticModel();
		this.o=o;
	}
	
	public void exec() throws TransformationNotApplicable
	{
	  try
	  {
		Iterator<Package> itP = m.getPackages().iterator();
		Iterator<Association> itAs;
		
		Vector<S_Unique> vUnique=new Vector(0,1); 
			
		//Adding foreign key information
		while (itP.hasNext())
		{
			Package p=itP.next();
			itAs=p.getAllAssociationsNoAssCl().iterator();
			Vector<Class> classesToMergeUniquesStereotypes= new Vector<Class>(0,1);
			while (itAs.hasNext())
			{
				Association as=itAs.next();
				if (!as.isMNorNary()) 
				{
				  AssociationEnd pAE1= as.getEnds().get(0);
				  Class pCl1 = (Class) pAE1.getSource();
				  AssociationEnd pAE2= as.getEnds().get(1);
				  Class pCl2 = (Class) pAE2.getSource();
				  Class clYesFK=null; AssociationEnd aeYesFK=null;
				  Class clNoFK=null; AssociationEnd aeNoFK=null;
				  //Deciding where to add the foreign key
				  if ((pAE2.getMax()==1 && pAE1.isMMultiplicity() || pAE2.getMax()==1 && pAE1.getMin()==0)) 
				  	{clYesFK=pCl1; clNoFK=pCl2; aeYesFK=pAE1; aeNoFK=pAE2;}
				  else {clYesFK=pCl2; clNoFK=pCl1; aeYesFK=pAE2; aeNoFK=pAE1;} //When the association 1:1 we could put the fk on any of the ends (or in both)
				
				  //Creation of the new attribute and the corresponding stereotype
				  Attribute pkAt =clNoFK.getPrimaryKeyAttribute(); 
				  S_ForeignKey s = new S_ForeignKey();
				  s.setReferencedClass(clNoFK);
				  s.setName(aeNoFK.getName());
				  clYesFK.addForeignKey(s);
				  Attribute atRef=pkAt;
				  Attribute atNew= atRef.clone();
				  clYesFK.addAttribute(atNew);
				  atNew.setMax(1);
				  atNew.setSource(clYesFK);
			      atNew.setMin(aeNoFK.getMin());
				  String name="";
				  name=aeNoFK.getName(); 
				  atNew.setName(o.getProperty("db.fk.att.name.prefix") + name + o.getProperty("db.fk.att.name.suffix") );
			      atNew.addRefines(as); // Adding trace information
				  s.addAtt(atRef, atNew);
				  s.setOnDelete(E_ForeignKeyEventKind.getKind(o.getProperty("db.fk.ondelete")));
				  s.setOnUpdate(E_ForeignKeyEventKind.getKind(o.getProperty("db.fk.onupdate")));
				  s.setOppositeMany(aeYesFK.isMMultiplicity());
				  s.setOppositeName(aeYesFK.getName());
				  s.setGeneralization(false); //This foreign key does not represent a link with a supertype
				  s.setReification(as.isReification());// This foreign key comes from an association resulting of a reification			
			      if (aeNoFK.isIdentifier())
				  {
					S_Unique su= new S_Unique();
					su.addRefAtt(atNew);
					su.setPartial(true);						
					clYesFK.addUniqueKey(su);
					classesToMergeUniquesStereotypes.add(clYesFK);
				  }
				}
	    		Iterator<Class> itC = classesToMergeUniquesStereotypes.iterator();
		    	while (itC.hasNext())
			  {
				  Class c=itC.next();
				  Vector<S_Unique>  vU= c.getUniqueKeys();
			      Vector<S_Unique> toRemove= new Vector(0,1);
			  	  Iterator<S_Unique> itU= vU.iterator();
				  S_Unique s = (S_Unique) itU.next();
				  while (itU.hasNext())
				  {
					  S_Unique s2= (S_Unique) itU.next();
					  s.addRefAtt( s2.getRefAtt().get(0));
					  //c.removeUniqueKey(s2);
					  toRemove.add(s2);
				  }
				  for (Iterator iterator = toRemove.iterator(); iterator.hasNext();) {
					S_Unique unique = (S_Unique) iterator.next();
					c.removeUniqueKey(unique);
					
				  }
			  }
			}
		}
	}catch(Exception e){throw new TransformationNotApplicable("Error when transforming associations into FKs");}
  }
}
