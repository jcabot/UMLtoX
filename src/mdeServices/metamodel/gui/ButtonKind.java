package mdeServices.metamodel.gui;

/**
 * Enum for representing the type of a button 
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */

//B_SELECT opens the new form passing the pk of the selected item
//B_OPEN just opens a new form
public enum ButtonKind {B_SUBMIT, B_RESET, B_CLOSE, B_SELECT, B_OPEN, B_DELETE, B_INSERT, B_UPDATE;


  /*public String toString()
  {
	  String but="";
	  if (this==ButtonKind.B_SUBMIT) but="submit";
	  if (this==ButtonKind.B_RESET) but="reset";
	  return but;  
  }*/


}