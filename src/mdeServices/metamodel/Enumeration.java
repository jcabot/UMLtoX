package mdeServices.metamodel;

import java.util.Iterator;
import java.util.Vector;

/**
 *  SuperClass for all (predefined) data types in the systemelements
 * 
 * @version 0.1 Dec 2008
 * @author jcabot
 *
 */

public class Enumeration extends DataType
{
	//Right now we don´t have two different sets one for the code and one for the texts
	Vector<String> values;

	/**
	 * @param name
    */
	public Enumeration(String name) {
		super(name);
		values=new Vector<String>(0,1);
	}
	
	public void addValue(String value)
	{
	  values.add(value);
	}

	/**
	 * @return the values
	 */
	public Vector<String> getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(Vector<String> values) {
		this.values = values;
	}
	
	//Returns the lenght of the longest literal value in the enumeration
	public int getMaxLength()
	{
	   Iterator<String> it=values.iterator();
	   int max=0;
	   while (it.hasNext()) {
		String literal= (String) it.next();
	    if (literal.length()>max) max=literal.length();
	   }
	   return max;
	}
}

