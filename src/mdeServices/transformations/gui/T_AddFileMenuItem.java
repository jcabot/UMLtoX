package mdeServices.transformations.gui;

import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.gui.DynamicModel;
import mdeServices.metamodel.gui.EditBox;
import mdeServices.metamodel.gui.Form;
import mdeServices.metamodel.gui.Menu;
import mdeServices.metamodel.gui.MenuItem;
import mdeServices.options.LangManager;
import mdeServices.options.Options;
import mdeServices.transformations.Transformation;
import mdeServices.transformations.TransformationNotApplicable;

/**
 * Transformation class for adding the default home menu item in web applications
 * 
 * @version 0.1 Dec 2008
 * @author jcabot
 *
 */

public class T_AddFileMenuItem extends Transformation {

	/**
	 * @param p
	 * @param o
	 * @throws TransformationNotApplicable
	 */
	public T_AddFileMenuItem(Project p, Options o) {
		super(p, o);
	}
	
	/**
	 * @param p
	 * @param o
	 * @throws TransformationNotApplicable
	 */
	public T_AddFileMenuItem(Project p, Options o, LangManager l)
			{
		super(p, o,l);
	}
	
	@Override
	public void exec() throws TransformationNotApplicable {
		Menu menu=p.getDynamicModel().getMainMenu();
		MenuItem mi=new MenuItem(l.getString("app.caption.menu.item.file"));
		MenuItem sub=new MenuItem(l.getString("app.caption.menu.item.exit"));
		menu.addItemBeginning(mi);
		mi.addItem(sub);
    }
		

}
