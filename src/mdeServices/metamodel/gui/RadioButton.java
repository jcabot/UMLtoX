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


public class RadioButton extends TFieldControl {

  Vector<String> values;

public RadioButton(String name) {
	super(name);
}

/**
 * @return the values
 */
public Vector<String> getValues() {
	return values;
}

/**
 * @param values the values to set
 */
public void setValues(Vector<String> values) {
	this.values = values;
}


   
}
