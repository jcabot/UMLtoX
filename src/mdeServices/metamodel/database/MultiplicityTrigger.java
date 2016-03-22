package mdeServices.metamodel.database;

import mdeServices.metamodel.Attribute;

public class MultiplicityTrigger extends Trigger{
	
	protected boolean min;
	protected int multiplicity;
	protected Attribute at;
	
	/**
	 * @param name
	 */
	public MultiplicityTrigger(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the min
	 */
	public boolean isMin() {
		return min;
	}
	
	/**
	 * @return the max
	 */
	public boolean isMax() {
		return !min;
	}
	
	/**
	 * @param min the min to set
	 */
	public void setMin(boolean min) {
		this.min = min;
	}
	
	/**
	 * @return the multiplicity
	 */
	public int getMultiplicity() {
		return multiplicity;
	}
	/**
	 * @param multiplicity the multiplicity to set
	 */
	public void setMultiplicity(int multiplicity) {
		this.multiplicity = multiplicity;
	}
	/**
	 * @return the at
	 */
	public Attribute getAttribute() {
		return at;
	}
	/**
	 * @param at the at to set
	 */
	public void setAttribute(Attribute at) {
		this.at = at;
	}
	
	

}


