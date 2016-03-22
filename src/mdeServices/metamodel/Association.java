/**
 * 
 */
package mdeServices.metamodel;

import java.util.Iterator;
import java.util.Vector;

/**
 *  Class representing associations between model elements
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */
public class Association extends ModelElement{
    /** set of association ends for the association */
	protected Vector<AssociationEnd> ends;

	protected boolean isReification=false;
	
	/**
	 * @return the isReification
	 */
	public boolean isReification() {
		return isReification;
	}


	/**
	 * @param isReification the isReification to set
	 */
	public void setReification(boolean isReification) {
		this.isReification = isReification;
	}


	/**
	 * @param name
	 */
	public Association(String name) {
		super(name);
		ends= new Vector<AssociationEnd>(0,1);
	}
	
	
	/**
	 * @return the assEnds
	 */
	public Vector<AssociationEnd> getEnds() {
		return ends;
	}

	/**
	 * @param assEnds the assEnds to set
	 */
	public void setEnds(Vector<AssociationEnd> ends) {
		this.ends = ends;
	}

	/**
	 * @param assEnd the assEnd to add
	 */
	public void addAssociationEnd(AssociationEnd assEnd) {
		this.ends.add(assEnd);
	}
	
	/**
	 * @param assEnd the assEnd to add
	 */
	public void removeAssociationEnd(AssociationEnd assEnd) {
		this.ends.remove(assEnd);
	}
	
	/*public Association clone()
	{
		Association a = new Association(this.name);
		copyTo(a);
		return a;
	}*/
	
	
	public Association clone()
	{
		Association a = new Association(this.name);
		a.inPackage=this.inPackage;
		a.inPackage.addElement(a);
		Iterator<AssociationEnd> it = this.ends.iterator();
		while (it.hasNext())
		{
			AssociationEnd cloneAE=it.next().clone();
			cloneAE.setAss(a);
			a.ends.add(cloneAE);
		}
		return a;
	}
	
	public void replaceWith(Association a)
	{
		super.replaceWith(a);
		a.ends=this.ends;
	}
	
	/**Returns true if the association is a n-ary association or is a MN association
	 * 
	 */
	
	public boolean isMNorNary()
	{
	  return (isMN() || isNary());	
	}
	
	//Checks if all association ends have a multiplicity >1
	public boolean isMN()
	{
		Iterator<AssociationEnd> itE = ends.iterator();
		int count=0;
		while (itE.hasNext())
		{
			AssociationEnd ae= itE.next();
			if (ae.getMax()==Property.N_Multiplicity || ae.getMax()>1) count++;
		}
		return (count>1);
	}
	
	public boolean isNary()
	{
		return (this.ends.size()>2);
	}
	
	public AssociationEnd oppositeEnd(AssociationEnd ae)
	{
		Iterator<AssociationEnd> it = this.ends.iterator();
		while (it.hasNext())
		{
			AssociationEnd opp= it.next();
			if (opp!=ae) return opp;
		}
		return null;
	}
	
	public boolean isReflexive(Classifier c)
	{
	  Iterator<AssociationEnd> itAE= this.ends.iterator();
	  int i=0;
	  while (itAE.hasNext()) {
		AssociationEnd ae= (AssociationEnd) itAE.next();
		if (ae.getSource()==c) i=i+1;
	  }
	  if (i>=2) return true;
	  else return false;
	}
	  
	


}
