/**
 * 
 */
package mdeServices.metamodel;



/**
 * Abstract class representing properties (attributes or association ends)
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */

abstract public class Property extends Feature{
	
	/** Multiplicity: '*' represented as -1 */
	
	public static final int N_Multiplicity= -1;
	protected int min;
	protected int max;
	protected ChangeabilityKind changeability;
	//CONSTRUCTOR	
	/**
	 * @param name
	
	 */
	public Property(String name) {
		super(name);
	}

	/**
	 * @return the min
	 */
	public int getMin() {
		return min;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(int min) {
		this.min = min;
	}

	/**
	 * @return the max
	 */
	public int getMax() {
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(int max) {
		this.max = max;
	}

	/**
	 * @return the changeability
	 */
	public ChangeabilityKind getChangeability() {
		return changeability;
	}

	/**
	 * @param changeability the changeability to set
	 */
	public void setChangeability(ChangeabilityKind changeability) {
		this.changeability = changeability;
	}
	
	public void replaceWith(Property p)
	{
	   super.replaceWith(p);
	   p.changeability=this.changeability;
	   p.max=this.max;
	   p.min=this.min;
	}
	
	public void copyTo(Property p)
	{
	   super.copyTo(p);
	   p.changeability=this.changeability;
	   p.max=this.max;
	   p.min=this.min;
	}
  

}
