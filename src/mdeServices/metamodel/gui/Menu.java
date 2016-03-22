package mdeServices.metamodel.gui;

import java.util.Vector;

/**
 * Class representing the main menu of the application
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */

public class Menu extends GUIElement {
	
	Vector<MenuItem> items;
	
	public Menu(String name) {
		super(name);
	    items= new Vector<MenuItem>(0,1);	
	}
	
	public void addItem(MenuItem mi)
	{
	 items.add(mi)	;
	}

	/**
	 * @return the items
	 */
	public Vector<MenuItem> getItems() {
		return items;
	}

	public void addItemBeginning(MenuItem mi)
	{
	 items.add(0,mi)	;
	}
}
