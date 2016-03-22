/**
 * 
 */
package mdeServices.metamodel;

/**
 * Enum for representing the visibility of an element 
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */

public enum VisibilityKind {V_PUBLIC, V_PROTECTED, V_PRIVATE;

   public String toString()
   {
	   String vis="";
	   if (this==VisibilityKind.V_PUBLIC) vis="public"; 
	   if (this==VisibilityKind.V_PRIVATE) vis="private";
	   if (this==VisibilityKind.V_PROTECTED) vis="protected";
	   return vis;
   }

}
   
