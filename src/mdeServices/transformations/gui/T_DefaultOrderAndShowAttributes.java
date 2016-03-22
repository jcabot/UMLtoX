package mdeServices.transformations.gui;

import java.util.Iterator;

import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.gui.ComboBox;
import mdeServices.metamodel.gui.DynamicModel;
import mdeServices.metamodel.gui.EditBox;
import mdeServices.metamodel.gui.Form;
import mdeServices.metamodel.gui.GUIElement;
import mdeServices.metamodel.gui.Grid;
import mdeServices.metamodel.gui.Menu;
import mdeServices.metamodel.gui.MenuItem;
import mdeServices.options.LangManager;
import mdeServices.options.Options;
import mdeServices.transformations.Transformation;
import mdeServices.transformations.TransformationNotApplicable;

/**
 * Transformation class for adding the default "order" and "show" attributes to the grid and combo boxes
 * 
 * @version 0.1 Dec 2008
 * @author jcabot
 *
 */

public class T_DefaultOrderAndShowAttributes extends Transformation {

	/**
	 * @param p
	 * @param o
	 * @throws TransformationNotApplicable
	 */
	public T_DefaultOrderAndShowAttributes(Project p, Options o) {
		super(p, o);
	}
	
	/**
	 * @param p
	 * @param o
	 * @throws TransformationNotApplicable
	 */
	public T_DefaultOrderAndShowAttributes(Project p, Options o, LangManager l)
	{
		super(p, o,l);
	}
	
	@Override
	public void exec() throws TransformationNotApplicable {
		Iterator<Form> itForms=p.getDynamicModel().getForms().iterator();
		while (itForms.hasNext()) {
			Form form = (Form) itForms.next();
			Iterator<GUIElement> itEls=form.getItems().iterator();
			while (itEls.hasNext()) {
				GUIElement element = (GUIElement) itEls.next();
				if (element instanceof Grid)
				{
					Grid g=(Grid) element;
					g.addOrder(g.getBaseClass().getPrimaryKeyAttribute());
				}
				if (element instanceof ComboBox)
				{
					ComboBox c=(ComboBox) element;
					c.addOrder(c.getRefClass().getPrimaryKeyAttribute());
					c.addShow((c.getRefClass().getPrimaryKeyAttribute()));
				}
				
			}
			
		}
		
		
    }
		

}
