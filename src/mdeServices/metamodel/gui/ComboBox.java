package mdeServices.metamodel.gui;

import java.util.Vector;

import mdeServices.metamodel.Attribute;
import mdeServices.metamodel.Class;

/**
 * Class for representing a combo box in the form. We use this class also for representing drop down lists 
 * 
 * @version 0.1 Dec 2008
 * @author jcabot
 *
 */

public class ComboBox extends GUIElement {

	//Attribute of the own class (i.e. the foreign key attribute)
	Attribute own;
	//Attribute of the "parent" class (i.e. the primary key of the other class)
	Attribute ref;
	//Attribute to show in the text of the combo box
	Vector<Attribute> show; 
	//Attribute to order the results
	Vector<Attribute> order; 
	//Referred Class
	Class refClass;
	
	boolean isReadOnly=false;
	/**
	 * @return the isReadOnly
	 */
	public boolean isReadOnly() {
		return isReadOnly;
	}


	/**
	 * @param isReadOnly the isReadOnly to set
	 */
	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}


	/**
	 * @param name
	 */
	public ComboBox(String name) {
		super(name);
		show=new Vector<Attribute>(0,1);
		order=new Vector<Attribute>(0,1);
		// TODO Auto-generated constructor stub
	}
	

	/**
	 * @return the refClass
	 */
	public Class getRefClass() {
		return refClass;
	}


	/**
	 * @param refClass the refClass to set
	 */
	public void setRefClass(Class refClass) {
		this.refClass = refClass;
	}


	/**
	 * @return the own
	 */
	public Attribute getOwn() {
		return own;
	}


	/**
	 * @param own the own to set
	 */
	public void setOwn(Attribute own) {
		this.own = own;
	}


	/**
	 * @return the ref
	 */
	public Attribute getRef() {
		return ref;
	}


	/**
	 * @param ref the ref to set
	 */
	public void setRef(Attribute ref) {
		this.ref = ref;
	}


	/**
	 * @return the show
	 */
	public Vector<Attribute> getShow() {
		return show;
	}


	/**
	 * @param show the show to set
	 */
	public void setShow(Vector<Attribute> show) {
		this.show = show;
	}


	/**
	 * @param show the show to set
	 */
	public void addShow(Attribute show) {
		this.show.add(show);
	}

	/**
	 * @return the order
	 */
	public Vector<Attribute> getOrder() {
		return order;
	}


	/**
	 * @param order the order to set
	 */
	public void setOrder(Vector<Attribute> order) {
		this.order = order;
	}

	/**
	 * @param order the order to set
	 */
	public void addOrder(Attribute order) {
		this.order.add(order);
	}



	
}
