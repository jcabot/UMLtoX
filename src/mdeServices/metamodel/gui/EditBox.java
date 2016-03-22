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


public class EditBox extends TFieldControl {

   boolean isPassword=false;
   int size;
   int contentSize;

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

/**
 * @return the at
 */
public Attribute getAttribute() {
	return at;
}

public EditBox(String name) {
	super(name);
}

/**
 * @return the isPassword
 */
public boolean isPassword() {
	return isPassword;
}

/**
 * @param isPassword the isPassword to set
 */
public void setPassword(boolean isPassword) {
	this.isPassword = isPassword;
}

/**
 * @return the contentSize
 */
public int getContentSize() {
	return contentSize;
}

/**
 * @param contentSize the contentSize to set
 */
public void setContentSize(int contentSize) {
	this.contentSize = contentSize;
}
   
}
