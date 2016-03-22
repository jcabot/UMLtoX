/**
 * 
 */
package mdeServices.metamodel.stereotypes;

import java.util.Vector;

import mdeServices.metamodel.AssociationEnd;
import mdeServices.metamodel.Attribute;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.Stereotype;

/**
 *  Class representing Foreign Key Stereotypes
 * 
 * The order of the attributes in both vectors identify the relationships between the
 * elements of both classes
 * @version 0.1 Sep 2008
 * @author jcabot
 *
 */
public class S_ReifiedAssociation extends Stereotype {

	protected Class associationClass;
	
	AssociationEnd oppositeEnd;  //name of the association end at the end of the target class before the reification
	AssociationEnd ownEnd;
	
	/**
	 * @return the ownEndName
	 */
	public String getOwnEndName() {
		return ownEnd.getName();
	}

	public AssociationEnd getOwnEnd()
	{
		return ownEnd;
	}
	
	public AssociationEnd getOppositeEnd()
	{
		return ownEnd;
	}
	
	/**
	 * @param ownEndName the ownEndName to set
	 */
	public void setOwnEnd(AssociationEnd ownEnd) {
		this.ownEnd= ownEnd;
	}

	/**
	 * @return the oppositeName
	 */
	public String getOppositeName() {
		return oppositeEnd.getName();
	}

	/**
	 * @param oppositeName the oppositeName to set
	 */
	public void setOppositeEnd(AssociationEnd oppositeEnd) {
		this.oppositeEnd = oppositeEnd;
	}

	/**
	 * @return the associationClass
	 */
	public Class getAssociationClass() {
		return associationClass;
	}

	/**
	 * @param associationClass the associationClass to set
	 */
	public void setAssociationClass(Class associationClass) {
		this.associationClass = associationClass;
	}

	protected Class referencedClass;
	

	// With just one attribute as PK, the FK can just refer to a single attribute as well
	//	protected Vector<Attribute> refAtt;
//	protected Vector<Attribute> ownAtt;
	
	protected Attribute refAtt;
	protected Attribute ownAtt;
	
	protected E_ForeignKeyEventKind onDelete;
	protected E_ForeignKeyEventKind onUpdate;
	
	boolean oppositeMany=true; //Stores if the opposite association end is a Many end (default) or one (indicating that the relationship is a one
	//to one relationship
	
	
	public boolean isOppositeMany() {
		return oppositeMany;
	}

	public void setOppositeMany(boolean oppositeMany) {
		this.oppositeMany = oppositeMany;
	}

	/**
	 * @return the ownAtt
	 */
	public Attribute getOwnAtt() {
		return ownAtt;
	}

	/**
	 * @param ownAtt the ownAtt to set
	 */
	public void setOwnAtt(Attribute ownAtt) {
		this.ownAtt = ownAtt;
	}

	/**
	 * @param name
	 */
	public S_ReifiedAssociation(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param name
	 */
	public S_ReifiedAssociation() {
		super("ForeignKey");
	//	refAtt = new Vector(0,1);
	//	ownAtt = new Vector(0,1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the refClass
	 */
	public Class getReferencedClass() {
		return referencedClass;
	}


	/**
	 * @return the refAtt
	 */
	public Attribute getRefAtt() {
		return refAtt;
	}

	public void addAtt(Attribute ref, Attribute own)
	{
        refAtt=ref;
		ownAtt=own;		
	}

	/**
	 * @return the onDelete
	 */
	public E_ForeignKeyEventKind getOnDelete() {
		return onDelete;
	}

	/**
	 * @param onDelete the onDelete to set
	 */
	public void setOnDelete(E_ForeignKeyEventKind onDelete) {
		this.onDelete = onDelete;
	}

	/**
	 * @return the onUpdate
	 */
	public E_ForeignKeyEventKind getOnUpdate() {
		return onUpdate;
	}

	/**
	 * @param onUpdate the onUpdate to set
	 */
	public void setOnUpdate(E_ForeignKeyEventKind onUpdate) {
		this.onUpdate = onUpdate;
	}

	/**
	 * @param referencedClass the referencedClass to set
	 */
	public void setReferencedClass(Class referencedClass) {
		this.referencedClass = referencedClass;
	}

	/**
	 * @param refAtt the refAtt to set
	 */
	public void setRefAtt(Attribute refAtt) {
		this.refAtt = refAtt;
	}

	
}
