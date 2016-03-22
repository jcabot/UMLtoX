package mdeServices.metamodel.gui;


/**
 * Class representing a menu item of an application. It can contain subitems
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */

import java.util.Vector;

public class MenuItem extends Menu {
	
	Form destination;
	
	/**
	 * @return the destination
	 */
	public Form getDestination() {
		return destination;
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(Form destination) {
		this.destination = destination;
	}

	public MenuItem(String name) {
		super(name);
	}

}
