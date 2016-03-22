/**
 * 
 */
package mdeServices.metamodel;

/**
 * Enum for representing the directions of a parameter
 * 
 * @version 0.1 Nov 2008
 * @author jcabot
 *
 */

public enum DirectionKind {D_IN, D_OUT, D_INOUT, D_RETURN; 

   public String toString()
   {
	   String dir="";
	   if (this==DirectionKind.D_IN) dir = "in";
	   if (this==DirectionKind.D_OUT) dir= "out";
	   if (this==DirectionKind.D_INOUT) dir= "inout";
	   if (this==DirectionKind.D_RETURN) dir= "return";
	   return dir;
		   
   }


}
