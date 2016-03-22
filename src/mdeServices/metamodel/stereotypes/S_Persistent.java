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
public class S_Persistent extends Stereotype {

	String tableName;
	
	/**
	 * @param name
	 */
	public S_Persistent(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param name
	 */
	public S_Persistent() {
		super("Persistent");
		baseClass="Class";
	}

	/**
	 * @return the att
	 */
	public Vector<Attribute> getAtt() {
		return att;
	}

	/**
	 * @param att the att to set
	 */
	public void setAtt(Vector<Attribute> att) {
		this.att = att;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	
	
}
