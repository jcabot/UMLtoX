package mdeServices.metamodel;

/**
 *  Short data type. 
 * 
 * @version 0.1 May 2009
 * @author jcabot
 *
 */

public class DT_ShortInteger extends DT_Integer
{

	/**
	 * @param name
	 */
	public DT_ShortInteger(String name) {
		super(name);
		length=1; //Predefined lenght for short integers
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