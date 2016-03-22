/**
 * 
 */
package mdeServices.metamodel.stereotypes;

/**
 * Enum for representing the visibility of an element 
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */

// Some DBMS distinguish between restrict and no action. Here we assume are the same.
public enum E_ForeignKeyEventKind {E_RESTRICT, E_CASCADE, E_SETNULL, E_DEFAULT;

   public String toString()
   {
	   String vis="";
	   if (this==E_ForeignKeyEventKind.E_RESTRICT) vis="restrict"; 
	   if (this==E_ForeignKeyEventKind.E_CASCADE) vis="cascade";
	   if (this==E_ForeignKeyEventKind.E_SETNULL) vis="set null";
	   if (this==E_ForeignKeyEventKind.E_DEFAULT) vis="set default";
	   return vis;
   }
   
   public static E_ForeignKeyEventKind getKind (String event)
   {
	   if (event.equals("restrict"))  return E_ForeignKeyEventKind.E_RESTRICT; 
	   if (event.equals("cascade") )  return E_ForeignKeyEventKind.E_CASCADE ;
	   if (event.equals("set null"))  return E_ForeignKeyEventKind.E_SETNULL ;
	   if (event.equals("default")) return E_ForeignKeyEventKind.E_DEFAULT ;
	   else return null;
	 
   }
   
}
   
