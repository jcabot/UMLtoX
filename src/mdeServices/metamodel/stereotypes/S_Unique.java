/**
 * 
 */
package mdeServices.metamodel.stereotypes;

import java.util.Vector;

import mdeServices.metamodel.Attribute;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.Stereotype;

/**
 *  Class representing Foreign Key Stereotypes
 * 
 * The order of the attributes in both vectors identify the relationships between the
 * elements of both classes
 * @version 0.1 Sep 2008
 * @author jcabot
 *
 */
public class S_Unique extends Stereotype {

	protected Vector<Attribute> refAtt;
	boolean partial=false; // Internal attribute used when generating unique identifiers for foreign keys
	//all partial s_unique stereotypes are finally merged in a single one
	protected String constraintName;
	
	/**
	 * @param name
	 */
	public S_Unique(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param name
	 */
	public S_Unique() {
		super("Unique");
		refAtt = new Vector(0,1);
	}

	/**
	 * @return the refAtt
	 */
	public Vector<Attribute> getRefAtt() {
		return refAtt;
	}

	/**
	 * @param refAtt the refAtt to set
	 */
	public void setRefAtt(Vector<Attribute> refAtt) {
		this.refAtt = refAtt;
	}

	/**
	 * @param refAtt the refAtt to set
	 */
	public void addRefAtt(Attribute refAtt) {
		this.refAtt.add(refAtt);
	}

	/**
	 * @return the partial
	 */
	public boolean isPartial() {
		return partial;
	}

	/**
	 * @param partial the partial to set
	 */
	public void setPartial(boolean partial) {
		this.partial = partial;
	}

	/**
	 * @return the constraintName
	 */
	public String getConstraintName() {
		return constraintName;
	}

	/**
	 * @param constraintName the constraintName to set
	 */
	public void setConstraintName(String constraintName) {
		this.constraintName = constraintName;
	}

	

	
	
}
