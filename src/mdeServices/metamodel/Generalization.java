/**
 * 
 */
package mdeServices.metamodel;


/**
 *  Class representing associations between model elements
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */
public class Generalization extends ModelElement{
   
	protected Classifier superType;
	protected Classifier subType;
	protected String discriminator;
	
	public Generalization(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * @return the superType
	 */
	public Classifier getSuperType() {
		return superType;
	}
	/**
	 * @param superType the superType to set
	 */
	public void setSuperType(Classifier superType) {
		this.superType = superType;
	}
	/**
	 * @return the subType
	 */
	public Classifier getSubType() {
		return subType;
	}
	/**
	 * @param subType the subType to set
	 */
	public void setSubType(Classifier subType) {
		this.subType = subType;
	}


	/**
	 * @return the discriminator
	 */
	public String getDiscriminator() {
		return discriminator;
	}


	/**
	 * @param discriminator the discriminator to set
	 */
	public void setDiscriminator(String discriminator) {
		this.discriminator = discriminator;
	}

}
