package mdeServices.metamodel;

/**
 *  SuperClass for all (predefined) data types 
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */

public abstract class PrimitiveDataType extends DataType
{
	Integer length;
	
	/**
	 * @return the length
	 */
	public Integer getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(Integer length) {
		this.length = length;
	}

	/**
	 * @param name
    */
	public PrimitiveDataType(String name) {
		super(name);
	}

	//public abstract PrimitiveDataType clone();
	
/*	public void replace(PrimitiveDataType d)
	{
		super.replace(d);
		
	}*/
}

//{dt_integer, dt_string, dt_float, dt_boolean, dt_char, dt_date}