package mdeServices.metamodel;

/**
 *  String data type. Can be used to specify properties for the data type (e.g. length)
 * 
 * @version 0.1 Aug 2008
 * @author jcabot
 *
 */

public class DT_DateTime extends PrimitiveDataType
{
	private static final int DATE_LENGTH = 20;

	/**
	 * @param name
	 */
	public DT_DateTime(String name) {
		super(name);
		length=new Integer(DATE_LENGTH);
	}
	
	/*public DT_DateTime clone()
	{
		DT_DateTime d=new DT_DateTime(this.name);
		copyTo(d);
		return d;
	}*/
	
	/*public void replace(DataType d)
	{
		super.replace(d);
		
	}*/
	
}

