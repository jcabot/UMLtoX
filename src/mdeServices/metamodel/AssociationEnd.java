/**
 * 
 */
package mdeServices.metamodel;

/**
 * Class representing association ends in an association
 *   
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */
 
public class AssociationEnd extends Property{
	/** Association or AssociationClass where the association end belongs */
	protected ModelElement ass;
	protected boolean isNavigable;
	protected AggregationKind aggregation;
	protected boolean isIdentifier=false;
	

	/**
	 * @return the isIdentifier
	 */
	public boolean isIdentifier() {
		return isIdentifier;
	}

	/**
	 * @param isIdentifier the isIdentifier to set
	 */
	public void setIdentifier(boolean isIdentifier) {
		this.isIdentifier = isIdentifier;
	}

	/**
	 * @param name
	 */
	public AssociationEnd(String name)
	{
	  super(name);
	  aggregation=AggregationKind.A_NONE;
	  changeability=ChangeabilityKind.C_CHANGEABLE;
	  visibility=VisibilityKind.V_PUBLIC;
	  
	}
	
	

	/**
	 * @return the ass
	 */
	public ModelElement getAss() {
		return ass;
	}

	/**
	 * @return the ass
	 */
	public Classifier getOppositeEndClass() {
		Classifier opp=null;
		if (ass instanceof Association)
		{
			Association a= (Association) ass;
			opp=a.oppositeEnd(this).getSource();
		}
		return opp;
	}
	
	/**
	 * @param ass the ass to set
	 */
	public void setAss(ModelElement ass) {
		this.ass = ass;
	}

	/**
	 * @return the isNavigable
	 */
	public boolean isNavigable() {
		return isNavigable;
	}

	/**
	 * @param isNavigable the isNavigable to set
	 */
	public void setNavigable(boolean isNavigable) {
		this.isNavigable = isNavigable;
	}

	/**
	 * @return the aggregation
	 */
	public AggregationKind getAggregation() {
		return aggregation;
	}

	/**
	 * @param aggregation the aggregation to set
	 */
	public void setAggregation(AggregationKind aggregation) {
		this.aggregation = aggregation;
	}
	
	
	public boolean isMMultiplicity()
	{
		return (getMax()==Property.N_Multiplicity || getMax()>1); 

	}
	

	public AssociationEnd clone()
	{
		AssociationEnd a = new AssociationEnd(this.name);
		super.copyTo(a);
		a.isNavigable=this.isNavigable;
		a.ass=this.ass;
		a.isIdentifier=this.isIdentifier;
	
		a.visibility=this.visibility;
		return a;
	}
	/*	public AssociationEnd clone()
	{
		AssociationEnd a = new AssociationEnd(this.name);
		copyTo(a);
		return a;
	}*/
	
	/*public void clone(AssociationEnd a)
	{
		super.copyTo(a);
		a.aggregation=this.aggregation;
		a.ass=this.ass;
		a.isNavigable=this.isNavigable;
	}*/

}
