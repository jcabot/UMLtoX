/**
 * 
 */
package mdeServices.metamodel;

import java.util.Iterator;
import java.util.Vector;

import mdeServices.metamodel.database.Trigger;
import mdeServices.metamodel.stereotypes.S_ForeignKey;
import mdeServices.metamodel.stereotypes.S_Persistent;
import mdeServices.metamodel.stereotypes.S_PrimaryKey;
import mdeServices.metamodel.stereotypes.S_ReifiedAssociation;
import mdeServices.metamodel.stereotypes.S_Unique;

/**
 * Class representing the classes in the model
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */
public class Class extends Classifier {
	
	/** abstract class? */
	protected boolean isAbstract=false;
	/** Visibility of the class */
	protected VisibilityKind visibility;
		
	/* if persistent!=null, the class is persistent*/
	protected S_Persistent persistent; 
	
	/* if it is persistent primarykey contains the information about the class primary key*/
	protected S_PrimaryKey primaryKey; 
	
	/* Foreign and unique keys for the class */
	protected Vector<S_ForeignKey> foreignKeys;
	protected Vector<S_Unique> uniqueKeys;
	protected Vector<Trigger> triggers;
	
	//Required for the doctrine generation. Info about the reified association in which the class participates
	protected Vector<S_ReifiedAssociation> reifiedAssociations;

	

	/**
	 * @return the reifiedKeys
	 */
	public Vector<S_ReifiedAssociation> getReifiedAssociations() {
		return reifiedAssociations;
	}

	/**
	 * @param reifiedKeys the reifiedKeys to set
	 */
	public void addReifiedAssociation(S_ReifiedAssociation reifiedAssociation) {
		this.reifiedAssociations.add(reifiedAssociation);
	}

	/**
	 * @param name
	 */
	public Class(String name) {
		super( name);
		foreignKeys=new Vector<S_ForeignKey>(0,1);
		uniqueKeys=new Vector<S_Unique>(0,1);
		triggers=new Vector<Trigger>(0,1);
		reifiedAssociations=new Vector<S_ReifiedAssociation>(0,1);
	}
	
	/**
	 * @return the persistent
	 */
	public S_Persistent getPersistent() {
		return persistent;
	}

	/**
	 * @param persistent the persistent to set
	 */
	public void setPersistent(S_Persistent persistent) {
		this.persistent = persistent;
	}
	
	public void replaceWith(Class c)
	{
		super.replaceWith(c);
		c.isAbstract=this.isAbstract;
		c.visibility=this.visibility;
		c.persistent=this.persistent;
		c.foreignKeys=this.foreignKeys;
		c.uniqueKeys=this.uniqueKeys;
		c.primaryKey=this.primaryKey;
	}

	/**
	 * @return the isAbstract
	 */
	public boolean isAbstract() {
		return isAbstract;
	}

	/**
	 * @param isAbstract the isAbstract to set
	 */
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	/**
	 * @return the visibility
	 */
	public VisibilityKind getVisibility() {
		return visibility;
	}

	/**
	 * @param visibility the visibility to set
	 */
	public void setVisibility(VisibilityKind visibility) {
		this.visibility = visibility;
		//To be on the safe side, if no visibility is provided we assign a public visibility
		if (this.visibility==null)
		{
			this.visibility=VisibilityKind.V_PUBLIC;
		}
	}

	public Attribute getPrimaryKeyAttribute()
	{
		if (primaryKey!=null) return primaryKey.getRefAtt();
		else return null;
	}
	
	public void moveUpPKAttribute()
	{
		Attribute aux=null;
		if (primaryKey!=null)
		{	
			aux=primaryKey.getRefAtt();
			att.removeElement(aux);
		    addAttributeBeginning(aux);
		}
	}
	
	public S_PrimaryKey getPrimaryKey()
	{
		if (primaryKey!=null) return primaryKey;
		else return null;
	}
	
	
	public void setPrimaryKey(Attribute a)
	{
		primaryKey = new S_PrimaryKey();
		primaryKey.setRefAtt(a);
	}

	//Returns the attributes that are not part of a PK nor a FK
	public Vector<Attribute> getPlainAttributes()
	{
		Vector<Attribute> plain=new Vector<Attribute>(0,1);
		Vector<Attribute> attFK=getFKAttributes();
				
		Iterator<Attribute> it=att.iterator();
		while (it.hasNext()) {
			Attribute attribute = (Attribute) it.next();
			if (!attFK.contains(attribute))
			{
				if(primaryKey!=null)
					if (primaryKey.getRefAtt()!=attribute)
						plain.add(attribute);
			}
		}
		return plain;
	}
	
	//Returns the attributes that are part of a FK
	public Vector<Attribute> getFKAttributes()
	{
		Vector<Attribute> fk=new Vector<Attribute>(0,1);
		Iterator<S_ForeignKey> it=foreignKeys.iterator();
		while (it.hasNext()) {
			Attribute attribute = (Attribute) it.next().getOwnAtt();
			fk.add(attribute);
		}
		return fk;
	}
	
	//Returns the attributes that are of type Enumeration
	public Vector<Attribute> getEnumAttributes()
	{
		Vector<Attribute> e=new Vector<Attribute>(0,1);
		Iterator<Attribute> it=att.iterator();
		while (it.hasNext()) {
			Attribute attribute = (Attribute) it.next();
			if (attribute.getType() instanceof Enumeration) e.add(attribute);
		}
		return e;
	}

	public boolean isPKAutoIncrement()
	{
		if (primaryKey!=null) return primaryKey.isAutoIncrement();
		else return false;
	}
		
	//Returns the table where the class instances are stored
	public String getTableName()
	{
		if (persistent!=null) return persistent.getTableName();
		else return null;
	}
	

	public boolean  isPersistent()
	{
       return (persistent!=null);		
	}
	
	public void setPersistent()
	{
	  if (persistent!=null) persistent= new S_Persistent();
	}
	
	public void removePersistent()
	{
	  persistent=null;
	}

	
	/** Get referenced class returns the class pointed by foreign keys included in the present class
	 * 
	 */
	public Vector<Class> getReferencedClasses()
	{
      Vector<Class> cl=new Vector<Class>(0,1);
      Iterator<S_ForeignKey> it= foreignKeys.iterator();
      while (it.hasNext()) {
		S_ForeignKey st = it.next();
		cl.add(st.getReferencedClass());
		}
      return cl;
	}

	/**
	 * @return the foreignKeys
	 */
	public Vector<S_ForeignKey> getForeignKeys() {
		return foreignKeys;
	}
	
	public boolean hasForeignKeys()
	{
		return foreignKeys.size()>0;
	}

	public boolean hasReifiedAssociations()
	{
		return reifiedAssociations.size()>0;
	}

	
	/**
	 * @param foreignKeys the foreignKeys to set
	 */
	public void setForeignKeys(Vector<S_ForeignKey> foreignKeys) {
		this.foreignKeys = foreignKeys;
	}
	
	/**
	 * @param foreignKeys the foreignKeys to set
	 */
	public void addForeignKey(S_ForeignKey foreignKey) {
		this.foreignKeys.add(foreignKey);
	}
	
	/**
	 * @param foreignKeys the foreignKeys to set
	 */
	public void addForeignKeys(Vector<S_ForeignKey> keys) {
		this.foreignKeys.addAll(keys);
	}

	/**
	 * @param UniqueKeys the uniqueKeys to set
	 */
	public void addUniqueKeys(Vector<S_Unique> keys) {
		this.uniqueKeys.addAll(keys);
	}
	/**
	 * @return the uniqueKeys
	 */
	public Vector<S_Unique> getUniqueKeys() {
		return uniqueKeys;
	}

	/**
	 * @param uniqueKeys the uniqueKeys to set
	 */
	public void setUniqueKeys(Vector<S_Unique> uniqueKeys) {
		this.uniqueKeys = uniqueKeys;
	}

	

	/**
	 * @param uniqueKey the uniqueKey to set
	 */
	public void addUniqueKey(S_Unique uniqueKey) {
		this.uniqueKeys.add(uniqueKey);
	}

	/**
	 * @param uniqueKey the uniqueKey to set
	 */
	public void removeUniqueKey(S_Unique uniqueKey) {
		this.uniqueKeys.remove(uniqueKey);
	}
	
	/**
	 * @param primaryKey the primaryKey to set
	 */
	public void setPrimaryKey(S_PrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	/**
	 * @return the triggers
	 */
	public Vector<Trigger> getTriggers() {
		return triggers;
	}

	/**
	 * @param triggers the triggers to set
	 */
	public void setTriggers(Vector<Trigger> triggers) {
		this.triggers = triggers;
	}
	
	/**
	 * @param triggers the triggers to set
	 */
	public void addTrigger(Trigger trigger) {
		this.triggers.add(trigger);
	}
	
	public S_ForeignKey getGeneralizationForeingnKey()
	{
		S_ForeignKey gen=null;
		Iterator<S_ForeignKey> itFK=foreignKeys.iterator();
		while (itFK.hasNext()) {
			S_ForeignKey sForeignKey = (S_ForeignKey) itFK.next();
			if (sForeignKey.isGeneralization()) 
				gen=sForeignKey;
		} 
		return gen;
		
		
	}
	
	
	/*	public Class clone()
	{
		Class c= new Class(this.name);
		copyTo(c);
		return c;
	}*/
	
}
