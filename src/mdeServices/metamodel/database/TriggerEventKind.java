package mdeServices.metamodel.database;

/**
 * Enum for representing the event that 
 * 
 * @author jcabot
 *
 */

//B_SELECT opens the new form passing the pk of the selected item
//B_OPEN just opens a new form
public enum TriggerEventKind {E_INSERT, E_UPDATE, E_DELETE;


  public String toString()
  {
	  String event="";
	  if (this==TriggerEventKind.E_INSERT) event="insert";
	  if (this==TriggerEventKind.E_UPDATE) event="update";
	  if (this==TriggerEventKind.E_DELETE) event="delete";
	  return event;  
  }


}