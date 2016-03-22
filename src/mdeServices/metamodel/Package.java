/**
 * 
 */
package mdeServices.metamodel;

import java.util.Iterator;
import java.util.Vector;

/**
 * Class representing a package
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */
public class Package extends ModelElement {

	/** Elements in the package */
	protected Vector<ModelElement> elements;
	protected Model inModel;

		
	/**
	 * @param name
	 */ 
	public Package(String name) {
		super(name);
		this.elements = new Vector<ModelElement>(0,1);
	}


	/**
	 * @return the elements
	 */
	public Vector<ModelElement> getElements() {
		return elements;
	}

	/**
	 * @param elements the elements to set
	 */
	public void setElements(Vector<ModelElement> elements) {
		this.elements = elements;
	}

	/**
	 * @param element the element to add
	 */
	public void addElement(ModelElement element) {
		this.elements.add(element);
		element.setInPackage(this);
	}

	/**
	 * @return the model
	 */
	public Model getModel() {
		return inModel;
	}


	/**
	 * @param model the model to set
	 */
	public void setModel(Model model) {
		this.inModel = model;
	}
	
	
/*	public Package clone()
	{
	   Package p= new Package(this.name);
	   copyTo(p);
	   return p;
	}*/

   /* public void copyTo(Package p)
    {
    	super.copyTo(p);
    	p.elements=this.elements;
    	p.inModel=this.inModel;
    }*/
    
	/** Returns the element in the package with that name */
	public ModelElement findElement(String name)
	{
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux.getName().equals(name)) return aux;
		}
		return null;
		
	}
	
	/** Returns all classes in the package (including association classes) */
	public Vector<Class> getAllClasses()
	{
		Vector<Class> classes= new Vector(0,1);
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux instanceof Class) classes.add((Class) aux);
			if (aux instanceof Package) classes.addAll(((Package) aux).getAllClasses());
		}
		return classes;
	}
	
	/** Returns all attributes in the package  */
	public Vector<Attribute> getAllAttributes()
	{
		Vector<Attribute> attributes= new Vector(0,1);
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux instanceof Class) 
			{
				Class cl= (Class) aux;
				attributes.addAll(cl.getAtt());
			}
			if (aux instanceof Package) attributes.addAll(((Package) aux).getAllAttributes());
		}
		return attributes;
	}
	
	/** Returns all classes in the package (including association classes) */
	public int getNumberAllClasses()
	{
		int NCl= 0;
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux instanceof Class) NCl++; ;
			if (aux instanceof Package) NCl=NCl+ (((Package) aux).getNumberAllClasses());
		}
		return NCl;
	}
	
	/** Returns the number of attributes in the package  */
	public int getNumberAllAttributes()
	{
		int NCl= 0;
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux instanceof Class) NCl=NCl + ((Class) aux).getAtt().size();
			if (aux instanceof Package) NCl=NCl+ (((Package) aux).getNumberAllClasses());
		}
		return NCl;
	}
	
	/** Returns all classes directly embedded in the package (including association classes) */
	public Vector<Class> getAllDirectClasses()
	{
		Vector<Class> classes= new Vector(0,1);
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux instanceof Class) classes.add((Class) aux);
		
		}
		return classes;
	}
	
	
	/** Returns all first level subpackages in the package */
	public Vector<Package> getFirstLevelPackages()
	{
		Vector<Package> packs= new Vector(0,1);
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux instanceof Package) packs.add((Package) aux);
		}
		return packs;
	}
	
	/** Returns all classes in the package (including association classes) */
	public Vector<Class> getAllPersistentClasses()
	{
		Vector<Class> classes= new Vector(0,1);
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux instanceof Class && ((Class) aux).isPersistent()) classes.add((Class) aux);
			if (aux instanceof Package) classes.addAll(((Package) aux).getAllPersistentClasses());
		}
		return classes;
	}
	
	/** Returns all classes in the package (excluding association classes) */
	public Vector<Class> getAllClassesNoAssoc()
	{
		Vector<Class> classes= new Vector(0,1);
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if ((aux instanceof Class) && (! (aux instanceof AssociationClass))) classes.add((Class) aux);
			if (aux instanceof Package) classes.addAll(((Package) aux).getAllClassesNoAssoc());
		}
		return classes;
	}
	
	/** Returns all classes DIRECTLY embedded in the package (excluding association classes) */
	public Vector<Class> getAllDirectClassesNoAssoc()
	{
		Vector<Class> classes= new Vector(0,1);
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if ((aux instanceof Class) && (! (aux instanceof AssociationClass))) classes.add((Class) aux);
		}
		return classes;
	}

	/** Returns all classes (including association classes) that are superclass of an another class*/
	public Vector<Class> getAllSuperClasses()
	{
		Vector<Class> classes= new Vector(0,1);
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux instanceof Class) 
				if ( ((Class) aux).subCl.size()>0) classes.add((Class) aux);
			if (aux instanceof Package) classes.addAll(((Package) aux).getAllSuperClasses());
		}
		return classes;
	}
	
	/** Returns all direct classes (including association classes) that are superclass of an another class*/
	public Vector<Class> getAllDirectSuperClasses()
	{
		Vector<Class> classes= new Vector(0,1);
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux instanceof Class) 
				if ( ((Class) aux).subCl.size()>0) classes.add((Class) aux);
		}
		return classes;
	}
	
	
	/** Returns all classes (including association classes) that are superclass of an another class*/
	public Vector<Class> getAllRootClasses()
	{
		Vector<Class> classes= new Vector(0,1);
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux instanceof Class) 
				if ( ((Class) aux).subCl.size()>0 && ((Class) aux).superCl.size()==0 ) classes.add((Class) aux);
			if (aux instanceof Package) classes.addAll(((Package) aux).getAllRootClasses());
		}
		return classes;
	}
	
	/** Returns all classes (including association classes) that are superclass of an another class*/
	public Vector<Class> getAllRootClassesNoAssCl()
	{
		Vector<Class> classes= new Vector(0,1);
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux instanceof Class && (! (aux instanceof AssociationClass))) 
				if ( ((Class) aux).subCl.size()>0 && ((Class) aux).superCl.size()==0 ) classes.add((Class) aux);
			if (aux instanceof Package) classes.addAll(((Package) aux).getAllRootClasses());
		}
		return classes;
	}
	
	
	/** Returns all classes (including association classes) that are leaf classes*/
	public Vector<Class> getAllLeafClasses()
	{
		Vector<Class> classes= new Vector(0,1);
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux instanceof Class) 
				if ( ((Class) aux).subCl.size()==0) classes.add((Class) aux);
		}
		if (aux instanceof Package) classes.addAll(((Package) aux).getAllLeafClasses());
		return classes;
	}
	
	/** Returns all generalizations*/
	public Vector<Generalization> getAllGeneralizations()
	{
		Vector<Generalization> gens= new Vector(0,1);
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux instanceof Generalization)
			{
				gens.add((Generalization)aux);
			}
		}
		if (aux instanceof Package) gens.addAll(((Package) aux).getAllGeneralizations());
		return gens;
	}
	
	/** Returns number of all generalizations*/
	public int getNumberAllGeneralizations()
	{
		int gens= 0;
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux instanceof Class) gens=gens+ ((Class) aux).subCl.size();
		}
		if (aux instanceof Package) gens=gens+ ((Package) aux).getNumberAllGeneralizations();
		return gens;
	}
	
	
	/** Returns all classes that do not have a supertype*/
	public Vector<Class> getAllClassesNoSuperType()
	{
		Vector<Class> classes= new Vector(0,1);
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux instanceof Class) 
				if ( ((Class) aux).superCl.size()==0) classes.add((Class) aux);
		}
		if (aux instanceof Package) classes.addAll(((Package) aux).getAllClassesNoSuperType());
		return classes;
	}
	
	/** Returns all association classes in the package  */
	public Vector<AssociationClass> getAllAssociationClasses()
	{
		Vector<AssociationClass> classes= new Vector(0,1);
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux instanceof AssociationClass) classes.add((AssociationClass) aux);
		}
		if (aux instanceof Package) classes.addAll(((Package) aux).getAllAssociationClasses());
		return classes;
	}
	
	/** Returns the number of association classes in the package  */
	public int getNumberAllAssociationClasses()
	{
		int NCl= 0;
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux instanceof AssociationClass) NCl++;
		}
		if (aux instanceof Package) NCl=NCl + ((Package) aux).getNumberAllAssociationClasses();
		return NCl;
	}
	
	/** Returns all direct association classes in the package  */
	public Vector<AssociationClass> getAllDirectAssociationClasses()
	{
		Vector<AssociationClass> classes= new Vector(0,1);
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux instanceof AssociationClass) classes.add((AssociationClass) aux);
		}
		return classes;
	}

	/** Returns all associations in the package (excluding association classes) */
	public Vector<Association> getAllAssociationsNoAssCl()
	{
		Vector<Association> ass= new Vector(0,1);
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux instanceof Association) ass.add((Association) aux);
			if (aux instanceof Package) ass.addAll(((Package) aux).getAllAssociationsNoAssCl());
		}
		return ass;
	}
	
	/** Returns all associations in the package (excluding association classes) */
	public int getNumberAllAssociationsNoAssCl()
	{
		int ass= 0;
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux instanceof Association) ass++; 
			if (aux instanceof Package) ass = ass + ((Package) aux).getNumberAllAssociationsNoAssCl();
		}
		return ass;
	}
	/** Returns all direct associations in the package (excluding association classes) */
	public Vector<Association> getAllDirectAssociationsNoAssCl()
	{
		Vector<Association> ass= new Vector(0,1);
		ModelElement aux=null;
		Iterator<ModelElement> itME= elements.iterator();
		while (itME.hasNext())
		{
			aux=itME.next(); 
			if (aux instanceof Association) ass.add((Association) aux);
		}
		return ass;
	}
	
	/**Removes an element from the package */
	
	public void removeElement(ModelElement me)
	{
	  this.elements.remove(me);
	}
	
}


