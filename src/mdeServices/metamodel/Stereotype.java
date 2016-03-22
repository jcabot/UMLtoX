package mdeServices.metamodel;

import java.util.Vector;

/**
 * Class representing all kinds of elements that can be generalized
 * and have associations with other elements 
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */

public class Stereotype extends Classifier{

	//Base (meta)class for the stereotype (e.g. class, association,...) 
    protected String baseClass;
    
    protected Vector<ModelElement> extended; 
	

	/**
	 * @param name
	 */
	public Stereotype(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		extended= new Vector<ModelElement>(0,1);
	}

	/*public Stereotype clone()
	{
		Stereotype c=new Stereotype(this.name);
		copyTo(c);
		return c;
	}*/
			
	public void replaceWith(Stereotype c)
	{
		super.replaceWith(c);
		
	}
	
	/**
	 * @return the baseClass
	 */
	public String getBaseClass() {
		return baseClass;
	}

	/**
	 * @param baseClass the baseClass to set
	 */
	public void setBaseClass(String baseClass) {
		this.baseClass = baseClass;
	}

}
