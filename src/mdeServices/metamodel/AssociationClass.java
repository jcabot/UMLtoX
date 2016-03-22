package mdeServices.metamodel;

import java.util.Iterator;
import java.util.Vector;

public class AssociationClass extends Class {

	/** set of association ends for the association view */
	protected Vector<AssociationEnd> ends;
	
	/**
	 * @param name
	 * @param isAbstract
	 * @param ends
	 */
	public AssociationClass(String name) {
		super(name);
		this.ends = new Vector<AssociationEnd>(0,1);
	}

	

	/**
	 * @return the ends
	 */
	public Vector<AssociationEnd> getEnds() {
		return ends;
	}

	/**
	 * @param ends the ends to set
	 */
	public void setEnds(Vector<AssociationEnd> ends) {
		this.ends = ends;
		Iterator<AssociationEnd> itEnds= this.ends.iterator();
		while (itEnds.hasNext()) {
			AssociationEnd associationEnd = (AssociationEnd) itEnds.next();
			associationEnd.setAss(this);
		}
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


	/*public AssociationClass clone()
	{
		AssociationClass a = new AssociationClass(this.name);
		copyTo(a);
		return a;
	}*/
	
	/*public void clone(AssociationClass a)
	{
		super.copyTo(a);
		a.ends=this.ends;
	}*/
	
	public void removeAllEnds()
	{
	  ends.removeAllElements();	
	}
	
	public AssociationClass clone()
	{
		AssociationClass a = new AssociationClass(this.name);
		a.inPackage=this.inPackage;
		a.inPackage.addElement(a);
		Iterator<AssociationEnd> it = this.ends.iterator();
		while (it.hasNext())
		{
			AssociationEnd cloneAE=it.next().clone();
			cloneAE.setAss(a);
			a.ends.add(cloneAE);
		}
		
		Iterator<Attribute> itAt = this.att.iterator();
		while (itAt.hasNext())
		{
			Attribute cloneAt=itAt.next().clone();
			cloneAt.setSource(a);
			a.att.add(cloneAt);
		}
		
		Iterator<AssociationEnd> itAss = this.ass.iterator();
		while (itAss.hasNext())
		{
			AssociationEnd cloneAss=itAss.next().clone();
			cloneAss.setSource(a);
			a.ass.add(cloneAss);
		}
		
		a.superCl=this.superCl;
		a.subCl=this.subCl;
		
		return a;
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
