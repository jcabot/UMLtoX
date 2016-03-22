/**
 * 
 */
package mdeServices.metamodel;

import java.util.Iterator;

import mdeServices.metamodel.stereotypes.S_ForeignKey;

/**
 * Class representing an attribute 
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */
public class Attribute extends Property{

	/** Type of the attribute */
	protected Classifier type;

    
	/**
	 * @param name
	 */
	public Attribute(String name){
		super(name);
		changeability=ChangeabilityKind.C_CHANGEABLE;
	}

	/**
	 * @return the type
	 */
	public Classifier getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Classifier type) {
		this.type = type;
	}
	
	public boolean hasDataType()
	{
		return (type!=null);
	}

	public void replaceWith(Attribute a)
	{
		super.replaceWith(a);
		a.type=this.type;
		a.visibility=this.visibility;
	}
	
	public Attribute clone()
	{
		Attribute a = new Attribute(this.name);
		super.copyTo(a);
		a.type=this.type;
		a.visibility=this.visibility;
		return a;
	}
	
	public boolean isPartOfPK()
	{
		Attribute a= ((Class) getSource()).getPrimaryKeyAttribute();
		if (a==this) return true;
		else return false;
		
	}
	
	public boolean isPartOfFK()
	{
		Iterator<S_ForeignKey> it=  ((Class) getSource()).getForeignKeys().iterator();
		boolean found=false;
		while (it.hasNext() && !(found)) {
			S_ForeignKey stereotype = (S_ForeignKey) it.next();
			//if (stereotype.getOwnAtt().contains(this)) found=true;
			if (stereotype.getOwnAtt()==this) found=true;	
		}
		return found;
	}
	
	public boolean isPartOfPKorFK()
	{
		return (isPartOfPK() || isPartOfFK());
		
	}

}
