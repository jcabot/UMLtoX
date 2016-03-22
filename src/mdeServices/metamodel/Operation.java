package mdeServices.metamodel;

import java.util.Vector;

/**
 * Class representing the operations of a class 
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */

public class Operation extends Feature{

  //Identifies the operation as a side-effect operation	 
  boolean isQuery;
  
  //Identifies if the operation is abstract
  boolean isAbstract;
  
  /** Attributes of the classifier */
	protected Vector<Parameter> params;
	

	public Operation(String name) {
		super(name);
		params= new Vector<Parameter>(0,1);
	}
  
@Override
public ModelElement clone() {
	// TODO Auto-generated method stub
	return null;
}

/**
 * @return the isQuery
 */
public boolean isQuery() {
	return isQuery;
}

/**
 * @param isQuery the isQuery to set
 */
public void setQuery(boolean isQuery) {
	this.isQuery = isQuery;
}

/**
 * @return the params
 */
public Vector<Parameter> getParams() {
	return params;
}

/**
 * @param params the params to set
 */
public void setParams(Vector<Parameter> params) {
	this.params = params;
}

public void addParam(Parameter p)
{
  params.add(p);	
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

}
