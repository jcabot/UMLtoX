package mdeServices.metamodel;

/**
 *  Integer data type. Can be used to specify properties for the data type (e.g. length)
 * 
 * @version 0.1 Aug 2008
 * @author jcabot
 *
 */

public class DT_Boolean extends PrimitiveDataType
{
	private static final int LENGTH_BOOLEAN = 1;

	/**
	 * @param name
	 */
	public DT_Boolean(String name) {
		super(name);
		length=new Integer (LENGTH_BOOLEAN);
	}
	
/*	public DT_Boolean clone()
	{
		DT_Boolean d=new DT_Boolean(this.name);
		copyTo(d);
		return d;
	}*/
	
/*	public void copyTo(DT_Boolean d)
	{
		super.copyTo(d);
		
	}*/

	
}

//{dt_integer, dt_string, dt_float, dt_boolean, dt_char, dt_date}