package mdeServices.transformations.gui;

import java.util.Iterator;

import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.Package;
import mdeServices.metamodel.gui.DynamicModel;
import mdeServices.metamodel.gui.Form;
import mdeServices.metamodel.gui.FormActionKind;
import mdeServices.metamodel.gui.Menu;
import mdeServices.metamodel.gui.MenuItem;
import mdeServices.metamodel.Class;
import mdeServices.options.LangManager;
import mdeServices.options.Options;
import mdeServices.transformations.Transformation;
import mdeServices.transformations.TransformationNotApplicable;

/**
 * Transformation class for generating the empty initial form 
 * 
 * @version 0.1 Dec 2008
 * @author jcabot
 *
 */

public class T_GenerateInitialForm extends Transformation {

	DynamicModel d;
	StaticModel s;
	LangManager l;

	/**
	 * @param p
	 * @param o
	 * @throws TransformationNotApplicable
	 */
	public T_GenerateInitialForm(Project p, Options o)
			throws TransformationNotApplicable {
		super(p, o);
		// TODO Auto-generated constructor stub
		d=p.getDynamicModel(); this.o=o;
		s=p.getStaticModel();
	}
	
	/**
	 * @param p
	 * @param o
	 * @throws TransformationNotApplicable
	 */
	public T_GenerateInitialForm(Project p, Options o, LangManager l)
			throws TransformationNotApplicable {
		super(p, o);
		// TODO Auto-generated constructor stub
		d=p.getDynamicModel(); this.o=o;
		s=p.getStaticModel();
		this.l=l;
		
	}
	
	@Override
	public void exec() throws TransformationNotApplicable {
		// TODO Auto-generated method stub
		Form f= new Form(o.getProperty("app.form.prefix") + "MainForm" + o.getProperty("app.form.suffix"));
        f.setCaption(l.getString("app.caption.form.main.prefix") + " " + p.getName() + " " + l.getString("app.caption.form.main.suffix"));
		d.setInitialForm(f); 
    }
		
}
