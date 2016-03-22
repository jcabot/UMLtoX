package mdeServices.transformations;

import mdeServices.metamodel.Project;
import mdeServices.options.LangManager;
import mdeServices.options.Options;

public abstract class Transformation {
	
	protected Project p;
	protected Options o;
	protected LangManager l;
	
	public Transformation (Project p, Options o) 
	{
		this.p=p;
		this.o=o;
	};
	
	public Transformation (Project p, Options o, LangManager l) 
	{
		this.p=p;
		this.o=o;
		this.l=l;
	};
	
	public abstract void exec() throws TransformationNotApplicable;
	
	
}
