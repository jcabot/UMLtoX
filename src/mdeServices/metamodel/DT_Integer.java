package mdeServices.metamodel;

/**
 *  Integer data type. Can be used to specify properties for the data type (e.g. length)
 * 
 * @version 0.1 Aug 2008
 * @author jcabot
 *
 */

public class DT_Integer extends PrimitiveDataType
{

	/**
	 * @param name
	 */
	public DT_Integer(String name) {
		super(name);
	}


/*	public DT_Integer clone()
	{
		DT_Integer d=new DT_Integer(this.name);
		copyTo(d);
		return d;
	}
	
	public void copyTo(DT_Integer d)
	{
		super.copyTo(d);
	}*/
	
}

//{dt_integer, dt_string, dt_float, dt_boolean, dt_char, dt_date}