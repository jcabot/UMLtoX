package mdeServices.metamodel.gui;

import java.util.Vector;

import mdeServices.metamodel.Attribute;

/**
 * Class representing an edit box in a form
 * 
 * @version 0.1 Dec 2008
 * @author jcabot
 *
 */


public abstract class TFieldControl extends GUIElement {

   //Attribute linked to the form	
   Attribute at;
   boolean isReadOnly=false;

/**
 * @return the at
 */
public Attribute getAttribute() {
	return at;
}

public TFieldControl(String name) {
	super(name);
}

/**
 * @param at the at to set
 */
public void setAttribute(Attribute at) {
	this.at = at;
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

}
