/**
 * 
 */
package mdeServices.metamodel;

import java.util.Iterator;
import java.util.Vector;

import mdeServices.metamodel.gui.DynamicModel;
import mdeServices.metamodel.stereotypes.S_Persistent;
import mdeServices.metamodel.stereotypes.S_PrimaryKey;
import mdeServices.metamodel.stereotypes.S_IdentifierAssociationEnd;


/**
 * Class representing a model
 * 
 * @version 0.1 Aug 2008
 * @author jcabot
 *
 */

public class StaticModel extends Model{
    
	/** Package Elements in the model */
	protected Vector<Package> packages;
	
	/** Data Types used in the project */
	protected Vector<DataType> dataTypes;
	
	/** Available Stereotypes - (so far only useful for exporting the models)*/
	protected Vector<Stereotype> stereotypes;
	
	/** Dynamic model for the static model */
	DynamicModel dyModel;
	
	/**
	 * @param name
	 * @param description
	 */
	public StaticModel( String name) {
		super(name);
		packages=new Vector<Package>(0,1);
		dataTypes=new Vector<DataType>(0,1);
		stereotypes= new Vector<Stereotype>(0,1);
		stereotypes.add(new S_Persistent());
		stereotypes.add(new S_PrimaryKey());
		stereotypes.add(new S_IdentifierAssociationEnd());
	}



	/**
	 * @return the packages
	 */
	public Vector<Package> getPackages() {
		return packages;
	}


	
	/**
	 * @param packages the packages to set
	 */
	public void setPackages(Vector<Package> packages) {
		this.packages = packages;
	}

	/**
	 * @return the dataTypes
	 */
	public Vector<DataType> getDataTypes() {
		return dataTypes;
	}

	/**
	 * @param dataTypes the dataTypes to set
	 */
	public void setDataTypes(Vector<DataType> dataTypes) {
		this.dataTypes = dataTypes;
	}
	
	/**
	 * @param dataType the new data type to add
	 */
	public void addDataType(DataType dt)
	{
		this.dataTypes.add(dt);
	}
	
	/**
	 * @param package the new package to add
	 */
	public void addPackage(Package p)
	{
		this.packages.add(p);
		p.setModel(this);
	}
	
	/**
	 * @return the classes in the model
	 */
	public Vector<Class> getAllClasses() {
		Iterator<Package> it= packages.iterator();
		Vector<Class> cl= new Vector<Class>();
		while (it.hasNext())
		{
			cl.addAll(it.next().getAllClasses());
		}
		return cl;
	}

	/**
	 * @return the number of classes in the model
	 */
	public int getNumberAllClasses() {
		Iterator<Package> it= packages.iterator();
		int NCl= 0;
		while (it.hasNext())
		{
			NCl=NCl+it.next().getNumberAllClasses();
		}
		return NCl;
	}

	
	/**
	 * @return the classes in the model
	 */
	public Vector<Class> getAllClassesNoAssoc() {
		Iterator<Package> it= packages.iterator();
		Vector<Class> cl= new Vector<Class>();
		while (it.hasNext())
		{
			cl.addAll(it.next().getAllClassesNoAssoc());
		}
		return cl;
	}

	/**
	 * @return all persitent classes in the model
	 */
	public Vector<Class> getAllPersistentClasses() {
		Iterator<Package> it= packages.iterator();
		Vector<Class> cl= new Vector<Class>();
		while (it.hasNext())
		{
			cl.addAll(it.next().getAllPersistentClasses());
		}
		return cl;
	}
	
	/**
	 * @return all root classes in the model
	 */
	public Vector<Class> getAllRootClasses() {
		Iterator<Package> it= packages.iterator();
		Vector<Class> cl= new Vector<Class>();
		while (it.hasNext())
		{
			cl.addAll(it.next().getAllRootClasses());
		}
		return cl;
	}
	
	/**
	 * @return all root classes in the model
	 */
	public Vector<Class> getAllRootClassesNoAssCl() {
		Iterator<Package> it= packages.iterator();
		Vector<Class> cl= new Vector<Class>();
		while (it.hasNext())
		{
			cl.addAll(it.next().getAllRootClassesNoAssCl());
		}
		return cl;
	}
	
	
	/**
	 * @return all leaf classes in the model
	 */
	public Vector<Class> getAllLeafClasses() {
		Iterator<Package> it= packages.iterator();
		Vector<Class> cl= new Vector<Class>();
		while (it.hasNext())
		{
			cl.addAll(it.next().getAllLeafClasses());
		}
		return cl;
	}
	
	/**
	 * @return all generalizations in the model
	 */
	public Vector<Generalization> getAllGeneralizations() {
		Iterator<Package> it= packages.iterator();
		Vector<Generalization> g= new Vector<Generalization>();
		while (it.hasNext())
		{
			g.addAll(it.next().getAllGeneralizations());
		}
		return g;
	}
	
	/**
	 * @return the association classes in the model
	 */
	public Vector<AssociationClass> getAllAssociationClasses() {
		Iterator<Package> it= packages.iterator();
		Vector<AssociationClass> cl= new Vector<AssociationClass>();
		while (it.hasNext())
		{
			cl.addAll(it.next().getAllAssociationClasses());
		}
		return cl;
	}
	
	/**
	 * @return the number of association classes in the model
	 */
	public int getNumberAllAssociationClasses() {
		Iterator<Package> it= packages.iterator();
		int NCl= 0;
		while (it.hasNext())
		{
			NCl= NCl + it.next().getNumberAllAssociationClasses();
		}
		return NCl;
	}
	
	/**
	 * @return the associations in the model
	 */
	public Vector<Association> getAllAssociationsNoAssCl() {
		Iterator<Package> it= packages.iterator();
		Vector<Association> as= new Vector<Association>();
		while (it.hasNext())
		{
			as.addAll(it.next().getAllAssociationsNoAssCl());
		}
		return as;
	}
	
	/**
	 * @return the associations in the model
	 */
	public int getNumberAllAssociationsNoAssCl() {
		Iterator<Package> it= packages.iterator();
		int as= 0; 
		while (it.hasNext())
		{
			as = as + it.next().getNumberAllAssociationsNoAssCl();
		}
		return as;
	}
	
	/**
	 * @return the associations in the model
	 */
	public int getNumberAllAttributes() {
		Iterator<Package> it= packages.iterator();
		int att= 0; 
		while (it.hasNext())
		{
			att = att + it.next().getNumberAllAttributes();
		}
		return att;
	}
	
	/**
	 * @return the associations in the model
	 */
	public int getNumberAllGeneralizations() {
		Iterator<Package> it= packages.iterator();
		int gen= 0; 
		while (it.hasNext())
		{
			gen = gen + it.next().getNumberAllGeneralizations();
		}
		return gen;
	}
	
	/**
	 * @return the associations in the model
	 */
	public int getNumberAllPackages() {
		return packages.size();}
	
	/**
	 * @return the stereotypes
	 */
	public Vector<Stereotype> getStereotypes() {
		return stereotypes;
	}
	
	/** Creates a clone of the current model (all internal elements are recreated as well)*/
	/*
	public StaticModel clone()
	{
		StaticModel m= new StaticModel(this.name);
		m.description=this.description;
		m.project=this.project; //The project information is not cloned
		Iterator<Model> itM = this.refinements.iterator();
		while (itM.hasNext())
		{
			m.refinements.add( ((StaticModel) itM.next()).clone());
		}
		
		Iterator<DataType> itDT = this.dataTypes.iterator();
		while (itDT.hasNext())
		{
			m.dataTypes.add(itDT.next().clone());
		}
		
		Iterator<Package> itP = this.packages.iterator();
		while (itP.hasNext())
		{
			Package p= itP.next();
			Package newP= p.clone();
			m.packages.add(newP);
			newP.elements.removeAllElements();
			Iterator<ModelElement> itEl = p.elements.iterator();
			while (itEl.hasNext())
			{
				ModelElement el= itEl.next();
				if (el instanceof Class)
				{
					Class cl= (Class) el;
					Class clNew= cl.clone();
					Iterator<Attribute> itAt = cl.getAtt().iterator();
					while (itAt.hasNext())
					{
						Attribute at = itAt.next();
						Attribute atNew=at.clone();
						clNew.addAttribute(atNew);
						atNew.setType( m.findDataType(at.type.name));
					}
					if (cl instanceof AssociationClass) //Complement the object with the association class features
					{
						AssociationClass as= (AssociationClass) cl;
						Iterator<AssociationEnd> itAs = as.getEnds().iterator();
						while (itAs.hasNext())
						{
							AssociationEnd ae = itAs.next();
							AssociationEnd aeNew=ae.clone();
							cl.addAssociationEnd(aeNew);
							aeNew.setSource((Classifier) newP.findElement(ae.getSource().getName()));
						}
						
					}
					newP.addElement(clNew);
					clNew.setInPackage(newP);
					
				}
				if (el instanceof Association)
				{
					Association as= (Association) el;
					Association asNew= as.clone();
					Iterator<AssociationEnd> itAs = as.getEnds().iterator();
					while (itAs.hasNext())
					{
						AssociationEnd ae = itAs.next();
						AssociationEnd aeNew=ae.clone();
						asNew.addAssociationEnd(aeNew);
						aeNew.setSource((Classifier) newP.findElement(ae.getSource().getName()));
					}
					newP.addElement(asNew); asNew.setInPackage(newP);
				}
			
			}
			//Now that all classes have been cloned, change references to super and subclasses
			Iterator<ModelElement> itEl2 = newP.elements.iterator();
			while (itEl2.hasNext())
			{
				ModelElement el= itEl2.next();
				if (el instanceof Class)
				{
					Class cl= (Class) el;
				    //Changing super class references
					Iterator<Classifier> itSup = cl.getSuperCl().iterator();
				    Vector<Classifier> newSupCl = new Vector(0,1);
				    while (itSup.hasNext())
				    {
				    	newSupCl.add( (Classifier) p.findElement(itSup.next().getName()));
				    }
				    cl.setSuperEl(newSupCl);
				    //Changing subclass references
				    Iterator<Classifier> itSub = cl.getSubCl().iterator();
				    Vector<Classifier> newSubCl = new Vector(0,1);
				    while (itSub.hasNext())
				    {
				    	newSubCl.add( (Classifier) p.findElement(itSub.next().getName()));
				    }
				    cl.setSubEl(newSupCl);
				}
			}
		}	    
		return m;
	}*/
	
	/** Returns the datatype in the model with that name */
	public DataType findDataType(String name)
	{
		DataType aux=null;
		Iterator<DataType> itDT= dataTypes.iterator();
		while (itDT.hasNext())
		{
			aux=itDT.next(); 
			if (aux.getName().equals(name)) return aux;
		}
		return null;
	}

    //Making sure that at least the Integer datatype exists (needed for creating PKs)
	public void addDefaultDataTypes()
	{
	   if (findDataType("Integer")==null)
	   {
		   dataTypes.add(new DT_Integer("Integer"));	
	   }
	   if (findDataType("String")==null)
	   {
		   dataTypes.add(new DT_String("String"));	
	   }
	}
	

	/**
	 * @return the dyModel
	 */
	public DynamicModel getDynamicModel() {
		return dyModel;
	}



	/**
	 * @param dyModel the dyModel to set
	 */
	public void setDynamicModel(DynamicModel dyModel) {
		this.dyModel = dyModel;
	}
	
}
