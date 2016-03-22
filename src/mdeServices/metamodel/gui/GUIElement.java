package mdeServices.metamodel.gui;

import mdeServices.metamodel.ModelElement;

public class GUIElement extends ModelElement {
	
	String caption; //Text to display when showing the graphical element
	
	public GUIElement(String name) {
		super(name);}

	/**
	 * @return the caption
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * @param caption the caption to set
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	
}
