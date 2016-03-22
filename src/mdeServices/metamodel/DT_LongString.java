package mdeServices.metamodel;

/**
 *  String data type. Can be used to specify properties for the data type (e.g. length)
 * 
 * @version 0.1 Aug 2008
 * @author jcabot
 *
 */

public class DT_LongString extends DT_String
{

	Boolean fixLength;
	/**
	 * @param name
	 */
	public DT_LongString(String name) {
		super(name);
	}
	
	/**
	 * @return the fixLength
	 */
	public Boolean getFixLength() {
		return fixLength;
	}
	/**
	 * @param fixLength the fixLength to set
	 */
	public void setFixLength(Boolean fixLength) {
		this.fixLength = fixLength;
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