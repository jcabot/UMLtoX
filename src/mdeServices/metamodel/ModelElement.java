package mdeServices.metamodel;

import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;

/**
 * Class representing any model element 
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */

public abstract class ModelElement {

	/**Internal identifier of the model */
	protected UUID id;
	/**Name of the element*/
	protected String name; 
	/**Name of the package where the element is in*/
	protected Package inPackage; 
	/**Comments attached to the element*/
	protected java.util.Vector<Comment> comments;
	/** Stereotypes attached to the element */
	protected Vector<Stereotype> stereotypes;
	
	/** Information about the other model elements for which this element is a refinement*/
	protected Vector<ModelElement> refines;
	
	//CONSTRUCTORS

	/**
	 * @param id
	 * @param name
	 * @param inPackage
	 */
	public ModelElement(String name) {
		super();
		this.id = java.util.UUID.randomUUID();
		this.name = name;
		this.comments = new Vector<Comment>(0,1);
		this.stereotypes = new Vector<Stereotype>(0,1);
		this.refines = new Vector<ModelElement>(0,1);
		
	}
	
	/**
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}

	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the inPackage
	 */
	public Package getInPackage() {
		return inPackage;
	}
	/**
	 * @param inPackage the inPackage to set
	 */
	public void setInPackage(Package inPackage) {
		this.inPackage = inPackage;
	}
	/**
	 * @return the comments
	 */
	public java.util.Vector<Comment> getComments() {
		return comments;
	}
	/**
	 * @param comments the comments to set
	 */
	public void setComments(java.util.Vector<Comment> comments) {
		this.comments = comments;
	}
	

	//public abstract ModelElement clone();
	
    public void replaceWith(ModelElement m)
    {
    	m.name= this.name;
    	Iterator<Comment> it=this.comments.iterator();
    	m.comments=this.comments;
    	while (it.hasNext())
    	{
    	  Comment c= it.next();
    	  this.comments.remove(c);
     	}
    	
    	m.inPackage=this.inPackage;
    	this.inPackage.addElement(m);
    	this.inPackage.removeElement(this);
    	m.refines=this.refines;    	
     }

	
	
	
	public boolean hasName()
	{
		boolean hasName=false;
		if (this.name!=null && this.name!="") hasName=true;
		return hasName;
	}

	/**
	 * @return the refines
	 */
	public Vector<ModelElement> getRefines() {
		return refines;
	}

	/**
	 * @param refines the refines to set
	 */
	public void setRefines(Vector<ModelElement> refines) {
		this.refines = refines;
	}
	/**
	 * @param refines the refines to set
	 */
	public void addRefinesAll(Vector<ModelElement> refines) {
		this.refines.addAll(refines);
	}
	
	/**
	 * @param refines the refines to set
	 */
	public void addRefines(ModelElement refined) {
		this.refines.add(refined);
	}


	public void copyTo(ModelElement e)
	{
		
	}
	

	/** Returns true if the element includes the stereotype */
/*	public boolean hasStereotype(java.lang.Class c)
	{
	   Iterator<Stereotype> it = stereotypes.iterator();
	   boolean found=false;
	   while (it.hasNext() && !found)
	   {
		   if (it.next().getClass().getName().equals(c.getName())) found=true;
	   }
	   return found;
	}
	
	/** Returns the stereotype of that type associated to the model element */
	/*public Stereotype getStereotype(java.lang.Class c)
	{
	   Iterator<Stereotype> it = stereotypes.iterator();
	   boolean found=false;
	   Stereotype s=null;
	   while (it.hasNext() && !found)
	   {
		   s=it.next();
		   if (s.getClass().getName().equals(c.getName())) found=true;
	   }
	   if (found) return s;
	   else return null;
	}
		
	/** Returns the stereotypes of that type associated to the model element */
/*	public Vector<Stereotype> getStereotypes(java.lang.Class c)
	{
	   Vector<Stereotype> st=new Vector<Stereotype>(0,1);
		Iterator<Stereotype> it = stereotypes.iterator();
	   Stereotype s=null;
	   while (it.hasNext() )
	   {
		   s=it.next();
		   if (s.getClass().getName().equals(c.getName())) st.add(s);
	   }
	   return st;
	}
	
	//Removes an stereotype from the model element. We assume that there is at most one stereotype of the same type in the object
	public void removeStereotype(java.lang.Class c)
	{
		Iterator<Stereotype> it = stereotypes.iterator();
		boolean found=false;
		while (it.hasNext() && !found)
		{
		  Stereotype s=it.next();	
		  if (s.getClass().getName().equals(c.getName())) stereotypes.remove(s);
		}
	}
	
	//Removes a given stereotype from the model element. 
	public void removeStereotype(Stereotype s)
	{
		stereotypes.remove(s);
	}
	
	/**
	 * @return the stereotypes
	 */
	/*public Vector<Stereotype> getStereotypes() {
		return stereotypes;
	}

	/**
	 * @param stereotypes the stereotypes to set
	 */
	/*public void setStereotypes(Vector<Stereotype> stereotypes) {
		this.stereotypes = stereotypes;
	}
		
	/**
	 * @param stereotypes the stereotypes to set
	 */
	/*public void addStereotype(Stereotype stereotype) {
		this.stereotypes.add(stereotype);
	}
	
	/**
	 * @param stereotypes the stereotypes to set
	 */
	/*public void addStereotypes(Vector<Stereotype> stereotype) {
		this.stereotypes.addAll(stereotype);
	}
	*/
}
