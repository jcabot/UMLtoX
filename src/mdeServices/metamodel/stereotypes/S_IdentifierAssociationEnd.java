/**
 * 
 */
package mdeServices.metamodel.stereotypes;

import mdeServices.metamodel.Stereotype;

/**
 *  Class representing Identifier associations 
 *  Elements of a class A related to an element of a class B through an identifier 
 *  association requires the related instance in B to completely (externally) identify the A instance
 * 
 * The stereotype is attached to the association end in A
 * 
 * @version 0.1 Sep 2008
 * @author jcabot
 *
 */
public class S_IdentifierAssociationEnd extends Stereotype {

	/**
	 * @param name
	 */
	public S_IdentifierAssociationEnd(String name) {
		super(name);
		baseClass="AssociationEnd";
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param name
	 */
	public S_IdentifierAssociationEnd() {
		super("IdentifierAssociation");
		baseClass="AssociationEnd";
		// TODO Auto-generated constructor stub
	}
}
