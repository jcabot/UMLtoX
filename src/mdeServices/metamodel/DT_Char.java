package mdeServices.metamodel;

/**
 *  Char data type
 * 
 * @version 0.1 May 2009
 * @author jcabot
 *
 */

public class DT_Char extends DT_String
{


	/**
	 * @param name
	 */
	public DT_Char(String name) {
		super(name);
		fixLength=true;
		length=1;
	}
	
	/**
	 * @return the fixLength
	 */
	public Boolean getFixLength() {
		return fixLength;
	}


	
	
	/*public DT_String clone()
	{
		DT_String d=new DT_String(this.name);
		copyTo(d);
		return d;
	}
	
	public void replace(DataType d)
	{
		super.replace(d);
		
	}*/
	
}

//{dt_integer, dt_string, dt_float, dt_boolean, dt_char, dt_date}