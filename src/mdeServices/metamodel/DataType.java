package mdeServices.metamodel;

/**
 *  SuperClass for all (predefined) data types in the system
 *  
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */

public abstract class DataType extends Classifier
{
	/**
	 * @param name
    */
	public DataType(String name) {
		super(name);
	}

	//public abstract DataType clone();
	
	public void replaceWith(DataType d)
	{
		super.replaceWith(d);
		
	}
	
}

//{dt_integer, dt_string, dt_float, dt_boolean, dt_char, dt_date}