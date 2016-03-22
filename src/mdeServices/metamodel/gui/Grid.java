package mdeServices.metamodel.gui;

import 	java.util.Vector;
import mdeServices.metamodel.Attribute;
import mdeServices.metamodel.Class;


public class Grid extends GUIElement {

	Class baseClass;
	Vector<GridColumn> cols; //Details of the columns in the grid
	//Actions to be performed on the elements in the grid
	Button b_Update;
	Button b_Delete;
	Button b_Details;
	
	//Order of the data to show in the grid
	Vector<Attribute> order;
	
	//Param for retrieving the data -> identifies the foreign key that must be used when showing the
	//data in a form used to manage the information of an associated class.
	
	Attribute param;
	
	boolean isReadOnly=false;
	
	public Grid(String name) {
		super(name);
		cols=new Vector<GridColumn>(0,1);
		order=new Vector<Attribute>(0,1);
		
	}

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
	 * @return the baseClass
	 */
	public Class getBaseClass() {
		return baseClass;
	}

	/**
	 * @param baseClass the baseClass to set
	 */
	public void setBaseClass(Class baseClass) {
		this.baseClass = baseClass;
	}

	/**
	 * @return the cols
	 */
	public Vector<GridColumn> getColumns() {
		return cols;
	}

	/**
	 * @param cols the cols to set
	 */
	public void setColumns(Vector<GridColumn> cols) {
		this.cols = cols;
	}

	/**
	 * @param cols the cols to set
	 */
	public void addColumn(GridColumn col) {
		this.cols.add(col);
	}

	/**
	 * @return the b_Update
	 */
	public Button getB_Update() {
		return b_Update;
	}

	/**
	 * @param update the b_Update to set
	 */
	public void setB_Update(Button update) {
		b_Update = update;
	}

	/**
	 * @return the b_Delete
	 */
	public Button getB_Delete() {
		return b_Delete;
	}

	/**
	 * @param delete the b_Delete to set
	 */
	public void setB_Delete(Button delete) {
		b_Delete = delete;
	}

	/**
	 * @return the b_Details
	 */
	public Button getB_Details() {
		return b_Details;
	}

	/**
	 * @param details the b_Details to set
	 */
	public void setB_Details(Button details) {
		b_Details = details;
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

	/**
	 * @return the param
	 */
	public Attribute getParam() {
		return param;
	}

	/**
	 * @param param the param to set
	 */
	public void setParam(Attribute param) {
		this.param = param;
	}

}
