package mdeServices.metamodel.gui;

import mdeServices.metamodel.Attribute;

/**
 * Class for representing a GridColumn in a Grid control in the form 
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */

public class GridColumn extends GUIElement{
	

	Attribute att;
	int size;
	

	public GridColumn(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the att
	 */
	public Attribute getAtt() {
		return att;
	}

	/**
	 * @param att the att to set
	 */
	public void setAtt(Attribute att) {
		this.att = att;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}


}
