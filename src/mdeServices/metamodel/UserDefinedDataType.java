package mdeServices.metamodel;

/**
 *  SuperClass for all (user-defined) data types 
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */

public class UserDefinedDataType extends DataType
{
	/**
	 * @param name
    */
	public UserDefinedDataType(String name) {
		super(name);
	}

	//public abstract PrimitiveDataType clone();
	
/*	public void replace(PrimitiveDataType d)
	{
		super.replace(d);
		
	}*/
}

//{dt_integer, dt_string, dt_float, dt_boolean, dt_char, dt_date}