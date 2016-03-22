package mdeServices.metamodel;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * Class representing all kinds of elements that can be generalized
 * and have associations with other elements 
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */

public class Classifier extends ModelElement{

	/** Supertypes of the classifier */
	protected Set<Generalization> superCl;
	
	/** Subtypes of the classifier */
	protected Set<Generalization> subCl;
	/** Associations where the classifier participates*/
	protected Vector<AssociationEnd> ass;
	/** Attributes of the classifier */
	protected Vector<Attribute> att;
	
	/** Operations of the classifier */
	protected Vector<Operation> ops;
	
	/**isReified  = true when the class has been created when normalizing an association class  */
	protected boolean isReified=false;

	/** isReadOnly = true when the class is read only  */
	protected boolean isReadOnly=false;
	
	/**
	 * @return the isReadOnly
	 */
	public boolean isReadOnly() {
		return isReadOnly;
	}

	/**
	 * @param isReadOnly the isReadOnly to set
	 */
	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	/**
	 * @param name
	 * @param inPackage
	 */
	public Classifier(String name) {
		super(name);
		ass= new Vector<AssociationEnd>(0,1);
		att= new Vector<Attribute>(0,1);
		ops= new Vector<Operation>(0,1);
		superCl= new HashSet<Generalization>(0,1);
		subCl= new HashSet<Generalization>(0,1);
	}
	
	/**
	 * @return the op
	 */
	public Vector<Operation> getOps() {
		return ops;
	}
	/**
	 * @param op the op to set
	 */
	public void setOps(Vector<Operation> ops) {
		this.ops = ops;
	}
	
	/**
	 * @param op the op to add
	 */
	public void addOperation(Operation op) {
		this.ops.add(op);
	}
	
	/**
	 * @return the superEl
	 */
	public Set<Generalization> getSuperCl() {
		return superCl;
	}
	/**
	 * @param superEl the superEl to set
	 */
	public void setSuperEl(Set<Generalization> superCl) {
		this.superCl = superCl;
	}
	/**
	 * @return the subEl
	 */
	public Set<Generalization> getSubCl() {
		return subCl;
	}
	/**
	 * @param subEl the subEl to set
	 */
	public void setSubEl(Set<Generalization> subCl) {
		this.subCl = subCl;
	}
	/**
	 * @return the ass
	 */
	public Vector<AssociationEnd> getAss() {
		return ass;
	}
	/**
	 * @param ass the ass to set
	 */
	public void setAss(Vector<AssociationEnd> ass) {
		this.ass = ass;
	}
	/**
	 * @return the att
	 */
	public Vector<Attribute> getAtt() {
		return att;
	}
	/**
	 * @param att the att to set
	 */
	public void setAtt(Vector<Attribute> att) {
		this.att = att;
	}
	
	/**
	 * @param refAtt the att to add
	 */
	public void addAttribute(Attribute at) {
		this.att.add(at);
		at.setSource(this);
	}
	
	
	public boolean hasAttributes()
	{
		return att.size()>0;
	}


	public boolean hasAssociations()
	{
		return ass.size()>0;
	}

	
	/**
	 * @param refAtt the att to add
	 */
	public void addAttributeBeginning(Attribute at) {
		this.att.add(0,at);
		at.setSource(this);
	}
	
	/**
	 * @param ass the association end to add
	 */
	public void addAssociationEnd(AssociationEnd as) {
		this.ass.add(as);
		as.setSource(this);
	}

	
	public void addSubType(Generalization g)
	{ 
		this.subCl.add(g);
	}
	
	
	public void addSuperType(Generalization g)
	{ 
		this.superCl.add(g);
	}
	
	/**
	 * @param cl the class to add as a superclass
	 */
	/*public void addSuperType(Classifier cl) {
		Generalization g= new Generalization("");
		g.setSuperType(cl);
		g.setSubType(this);
		this.superCl.add(g);
	}
	
	/**
	 * @param cl the class to add as a superclass
	 */
	/*public void addSubType(Classifier cl) {
		Generalization g= new Generalization("");
		g.setSuperType(this);
		g.setSubType(cl);
		this.subCl.add(g);
	}
	
	/*public Classifier clone()
	{
		Classifier c=new Classifier(this.name);
		replace(c);
		return c;
	}*/
			
	public void replaceWith(Classifier c)
	{
		super.replaceWith(c);
		c.ass=this.ass;
		Iterator<AssociationEnd> itAs=this.ass.iterator();
		while (itAs.hasNext())
		{
			AssociationEnd ae=itAs.next();
			ae.setSource(c);
		}
		
		c.att=this.att;
		Iterator<Attribute> itAt=this.att.iterator();
		while (itAt.hasNext())
		{
			Attribute at=itAt.next();
			at.setSource(c);
		}
			
		c.subCl=this.subCl;
		Iterator<Generalization> itCl=this.subCl.iterator();
		while (itCl.hasNext())
		{
			Generalization g=itCl.next();
			g.setSuperType(c);
			/*Classifier cl=itCl.next().getSubType();
			cl.addSuperType(c);
			cl.removeSuperType(this);*/
		}
		
		c.superCl=this.superCl;
		Iterator<Generalization> itCl2=this.superCl.iterator();
		while (itCl2.hasNext())
		{
			Generalization g=itCl.next();
			g.setSubType(c);
			/*Classifier cl=itCl2.next().getSuperType();
			cl.addSubType(c);
			cl.removeSubType(this);*/
		}
		
		c.superCl=this.superCl;
	}
	
	public void removeSuperCl()
	{
	   superCl.clear();	
	}
	
	public void removeSuperType(Generalization g)
	{
	   superCl.remove(g);	
	}
	
	public void removeSubType(Generalization g)
	{
	   subCl.remove(g);	
	}
	
	public void removeAssociationEnd(AssociationEnd ae)
	{
	   ass.remove(ae);	
	}
	
	public void removeSubCl()
	{
	   subCl.clear();	
	}
	
	public Attribute getAttributeByName(String at)
	{
		Iterator<Attribute> itAt= this.getAtt().iterator();
		while (itAt.hasNext()) {
			Attribute attribute = (Attribute) itAt.next();
			if (attribute.getName().equals(at)) return attribute;
		}
		return null;
	}
	
	/**
	 * @return the isReified
	 */
	public boolean isReified() {
		return isReified;
	}

	/**
	 * @param isReified the isReified to set
	 */
	public void setReified(boolean isReified) {
		this.isReified = isReified;
	}
	
	public Set<Association> getReflexiveAssNoAssCl()
	{
		Set<Association> asSet= new HashSet<Association>();
	  	Iterator<AssociationEnd> itAE=ass.iterator();
	  	while (itAE.hasNext()) {
			AssociationEnd ae = (AssociationEnd) itAE.next();
			if (ae.getAss() instanceof Association) 
			{
				Association a= (Association) ae.getAss();
				if (a.isReflexive(this)) asSet.add(a);
			}
			
		}
	  	return asSet;
	}
	
	public Set<AssociationClass> getReflexiveAssCl()
	{
		Set<AssociationClass> asSet= new HashSet<AssociationClass>();
	  	Iterator<AssociationEnd> itAE=ass.iterator();
	  	while (itAE.hasNext()) {
			AssociationEnd ae = (AssociationEnd) itAE.next();
			if (ae.getAss() instanceof AssociationClass) 
			{
				AssociationClass a= (AssociationClass) ae.getAss();
				if (a.isReflexive(this)) asSet.add(a); 
			}
			
		}
	  	return asSet;
		
	}
	
	
}
