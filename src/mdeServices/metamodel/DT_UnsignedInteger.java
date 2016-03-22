package mdeServices.metamodel;

/**
 *  Unsigned Integer data type. 
 * 
 * @version 0.1 May 2009
 * @author jcabot
 *
 */

public class DT_UnsignedInteger extends DT_Integer
{

	/**
	 * @param name
	 */
	public DT_UnsignedInteger(String name) {
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