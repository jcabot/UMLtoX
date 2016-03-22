/**
 * 
 */
package mdeServices.metamodel;



/**
 * Abstract class representing properties (attributes or association ends)
 * 
 * @version 0.1 Nov 2008
 * @author jcabot
 *
 */

public class Parameter extends ModelElement{
	
	/** Multiplicity: '*' represented as -1 */
	
	public static final int N_Multiplicity= -1;
	protected int min;
	protected int max;
	protected DirectionKind direction;
	protected Classifier type;
	protected Operation owner;
	
	//CONSTRUCTOR	
	/**
	 * @param name
	
	 */
	public Parameter(String name) {
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


	
	public void copyTo(Parameter p)
	{
	  
	}

	/**
	 * @return the direction
	 */
	public DirectionKind getDirection() {
		return direction;
	}

	/**
	 * @param direction the direction to set
	 */
	public void setDirection(DirectionKind direction) {
		this.direction = direction;
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

	/**
	 * @return the owner
	 */
	public Operation getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(Operation owner) {
		this.owner = owner;
	}

	@Override
	public ModelElement clone() {
		// TODO Auto-generated method stub
		return null;
	}
	
  

}
