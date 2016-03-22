package mdeServices.metamodel.gui;

import java.util.Vector;

import mdeServices.metamodel.Model;

/**
 * Class representing the dynamic model of the application
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */

public class DynamicModel extends Model{


	Menu mainMenu;
	Vector<Form> forms;
	Form loginForm;
	Form initialForm;
	
	public DynamicModel(String name) {
		super(name);
		forms=new Vector<Form>(0,1);
	}

	
	/**
	 * @return the mainMenu
	 */
	public Menu getMainMenu() {
		return mainMenu;
	}
	/**
	 * @param mainMenu the mainMenu to set
	 */
	public void setMainMenu(Menu mainMenu) {
		this.mainMenu = mainMenu;
	}
	/**
	 * @return the forms
	 */
	public Vector<Form> getForms() {
		return forms;
	}
	
	/**
	 * @param forms the forms to set
	 */
	public void setForms(Vector<Form> forms) {
		this.forms = forms;
	}
	
	/**
	 * @param forms the forms to set
	 */
	public void addForm(Form forms) {
		this.forms.add(forms);
	}


	/**
	 * @return the loginForm
	 */
	public Form getLoginForm() {
		return loginForm;
	}


	/**
	 * @param loginForm the loginForm to set
	 */
	public void setLoginForm(Form loginForm) {
		this.loginForm = loginForm;
	}


	/**
	 * @return the initialForm
	 */
	public Form getInitialForm() {
		return initialForm;
	}


	/**
	 * @param initialForm the initialForm to set
	 */
	public void setInitialForm(Form initialForm) {
		this.initialForm = initialForm;
	}
}
