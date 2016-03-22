package mdeServices.metamodel.gui;


/**
 * Class for representing the actions to be performed on a form or grid 
 * This actions can be expressed in the final GUI as a button, as a link,...
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */

public class Button extends GUIElement {

	ButtonKind type;
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


	public Button(String name) {
		super(name);
		
	}


	/**
	 * @return the type
	 */
	public ButtonKind getType() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	public void setType(ButtonKind type) {
		this.type = type;
	}
}
