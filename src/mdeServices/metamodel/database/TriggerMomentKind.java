package mdeServices.metamodel.database;

/**
 * Enum for representing the event that fires the trigger
 * 
 * @author jcabot
 *
 */

//B_SELECT opens the new form passing the pk of the selected item
//B_OPEN just opens a new form
public enum TriggerMomentKind {M_BEFORE, M_AFTER;


  public String toString()
  {
	  String moment="";
	  if (this==TriggerMomentKind.M_BEFORE) moment="before";
	  if (this==TriggerMomentKind.M_AFTER) moment="after";
	   return moment;  
  }


}