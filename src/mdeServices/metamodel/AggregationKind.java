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

public enum AggregationKind {A_NONE, A_AGGREGATION, A_COMPOSITION;


  public String toString()
  {
	  String agg="";
	  if (this==AggregationKind.A_NONE) agg="none";
	  if (this==AggregationKind.A_AGGREGATION) agg="aggregate";
	  if (this==AggregationKind.A_COMPOSITION) agg="composite";
	  return agg;  
  }


}
