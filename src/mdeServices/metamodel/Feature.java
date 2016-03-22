package mdeServices.metamodel;

public abstract class Feature extends ModelElement{
	/**Classifier where the feature is attached*/
	protected Classifier source;
	/** static?*/
	protected boolean isStatic;
	
	/**Visibility of the feature */
	protected VisibilityKind visibility;
	
	//CONSTRUCTOR
	/**
	 * @param id
	 * @param name
	 * @param inPackage
	 * @param comments
	 * @param isReadOnly
	 * @param source
	 * @param isStatic
	 */
	public Feature(String name) {
		super(name);
	}

	/**
	 * @return the source
	 */
	public Classifier getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(Classifier source) {
		this.source = source;
	}

	/**
	 * @return the isStatic
	 */
	public boolean isStatic() {
		return isStatic;
	}

	/**
	 * @param isStatic the isStatic to set
	 */
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	
	
	public void replaceWith(Feature f)
	{
		super.replaceWith(f);
		f.isStatic=this.isStatic;
		f.source=this.source;
	 }

	/**
	 * @return the visibility
	 */
	public VisibilityKind getVisibility() {
		return visibility;
	}

	/**
	 * @param visibility the visibility to set
	 */
	public void setVisibility(VisibilityKind visibility) {
		this.visibility = visibility;
	}
	
	public void copyTo(Feature f)
	{
		super.copyTo(f);
		f.isStatic=this.isStatic;
		f.source=this.source;
	    // No puc posar aquesta línia pq llavors em fallen els iteradors (estic afegint un nou atribut a l'iterador enmig de la seva execució)
		//	if (f instanceof Attribute) this.source.addAttribute((Attribute) f);
	 }


}
