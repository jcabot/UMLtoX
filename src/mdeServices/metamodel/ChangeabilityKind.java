/**
 * 
 */
package mdeServices.metamodel;

/**
 * Enum for representing the changeability of an element 
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */

public enum ChangeabilityKind {C_CHANGEABLE, C_READONLY, C_ADDONLY; 

   public String toString()
   {
	   String cha="";
	   if (this==ChangeabilityKind.C_CHANGEABLE) cha = "changeable";
	   if (this==ChangeabilityKind.C_READONLY) cha = "frozen";
	   if (this==ChangeabilityKind.C_ADDONLY) cha = "addOnly";
	   return cha;
		   
   }


}
