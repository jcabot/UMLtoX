package mdeServices.metamodel;

/**
 *  Real data type. Can be used to specify properties for the data type (e.g. length)
 * 
 * @version 0.1 Aug 2008
 * @author jcabot
 *
 */

public class DT_Real extends PrimitiveDataType
{
	Integer length;
	Integer precision;
	/**
	 * @param name
	 */
	public DT_Real(String name) {
		super(name);
	}
	
	/**
	 * @return the precision
	 */
	public Integer getPrecision() {
		return precision;
	}
	/**
	 * @param precision the precision to set
	 */
	public void setPrecision(Integer precision) {
		this.precision = precision;
	}

		/*public DT_Real clone()
	{
		DT_Real d=new DT_Real(this.name);
		copyTo(d);
		return d;
	}
	
	public void copyTo(DT_Real d)
	{
		super.copyTo(d);
		
	}*/
	
}

//{dt_integer, dt_string, dt_float, dt_boolean, dt_char, dt_date}