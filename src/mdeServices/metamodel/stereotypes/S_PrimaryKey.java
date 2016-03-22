/**
 * 
 */
package mdeServices.metamodel.stereotypes;

import java.util.Vector;

import mdeServices.metamodel.Attribute;
import mdeServices.metamodel.Stereotype;

/**
 *  Class representing Primary Key Stereotyypes
 * 
 * @version 0.1 Sep 2008
 * @author jcabot
 *
 */
public class S_PrimaryKey extends Stereotype {
	
	//Maximum one attribute per PK (for practical reasons)
	
	//protected Vector<Attribute> refAtt;
	protected Attribute refAtt; //Attribute acting as primary key
	
	//Constraint name for the pk
	protected String constraintName;
	
	boolean autoIncrement=false;

	/**
	 * @return the autoIncrement
	 */
	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	/**
	 * @param autoIncrement the autoIncrement to set
	 */
	public void setAutoIncrement(boolean autoIncrement) {
		//It does not make sense to autoincrement pk's with more than one attribute
		//if(refAtt.size()<2) this.autoIncrement = autoIncrement;
		this.autoIncrement = autoIncrement;
	}

	/**
	 * @return the refAtt
	 */
	public Attribute getRefAtt() {
		return refAtt;
	}

	/**
	 * @param refAtt the refAtt to set
	 */
	public void setRefAtt(Attribute refAtt) {
		this.refAtt = refAtt;
	}
	
	/**
	 * @param refAtt the refAtt to set
	
	public void addRefAtt(Attribute a) {
		this.refAtt.add(a);
	}*/

	/**
	 * @param name
	 */
	public S_PrimaryKey(String name) {
		super(name);
		baseClass="Class";
		//refAtt=new Vector<Attribute>(0,1);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param name
	 */
	public S_PrimaryKey() {
		super("PrimaryKey");
		baseClass="Attribute";
		//refAtt=new Vector<Attribute>(0,1);
		// TODO Auto-generated constructor stub
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
	
	public S_PrimaryKey clone()
	{
	  S_PrimaryKey copy= new S_PrimaryKey(this.getName());
	  copy.setAutoIncrement(this.isAutoIncrement());
	  copy.setBaseClass(this.getBaseClass());
	  copy.setConstraintName(this.constraintName);
	  copy.setRefAtt(this.getRefAtt());
	  return copy;
	}
}
