package mdeServices.transformations.gui;

import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.gui.Button;
import mdeServices.metamodel.gui.ButtonKind;
import mdeServices.metamodel.gui.DynamicModel;
import mdeServices.metamodel.gui.EditBox;
import mdeServices.metamodel.gui.Form;
import mdeServices.options.LangManager;
import mdeServices.options.Options;
import mdeServices.transformations.Transformation;
import mdeServices.transformations.TransformationNotApplicable;


/**
 * Transformation class for adding an initial login form 
 * 
 * @version 0.1 Dec 2008
 * @author jcabot
 *
 */


public class T_GenerateLoginForm extends Transformation {

	DynamicModel d;
	StaticModel s;
	LangManager l;

	/**
	 * @param p
	 * @param o
	 * @throws TransformationNotApplicable
	 */
	public T_GenerateLoginForm(Project p, Options o)
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
	public T_GenerateLoginForm(Project p, Options o, LangManager l)
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
		Form f= new Form(o.getProperty("app.form.prefix") + "Login" + o.getProperty("app.form.suffix"));
        f.setCaption(l.getString("app.caption.form.login"));
        EditBox eLogin= new EditBox(o.getProperty("app.editbox.prefix") + "Login" + o.getProperty("app.editbox.suffix"));
        eLogin.setCaption(l.getString("app.caption.editbox.login"));
        eLogin.setContentSize(new Integer(o.getProperty("app.editbox.login.maxSize")).intValue());
        eLogin.setSize(new Integer(o.getProperty("app.editbox.login.maxSize")).intValue());
        
        EditBox ePwd= new EditBox(o.getProperty("app.editbox.prefix") + "Password" + o.getProperty("app.editbox.suffix"));
        ePwd.setCaption(l.getString("app.caption.editbox.password"));ePwd.setPassword(true);
        ePwd.setContentSize(new Integer(o.getProperty("app.editbox.password.maxSize")).intValue());
        ePwd.setSize(new Integer(o.getProperty("app.editbox.login.maxSize")).intValue());
        
        //Submit button
        Button b= new Button("submit");
        b.setType(ButtonKind.B_SUBMIT);
        b.setDestination(f); b.setCaption(l.getString("app.caption.button.submit"));
        f.setB_Submit(b);
        d.setLoginForm(f); f.addItem(eLogin);f.addItem(ePwd);
    }
		

}
