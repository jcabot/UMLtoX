package mdeServices.metamodel.gui;

import java.util.Iterator;
import java.util.Vector;
import mdeServices.metamodel.Class;

/**
 * Class representing the forms of the application
 * 
 * @version 0.1 Dec 2008
 * @author jcabot
 *
 */

public class Form extends GUIElement {
	
	Vector<GUIElement> items;
	
	//Purpose of the form: insert a new element, update it, show ...
	FormActionKind type;
	//Typical buttons that may be added to the form
	Button b_Insert; //Link for opening an insert form
	Button b_Close;
	Button b_Submit;
	
	Class baseClass; //Class managed in the form
	
	Vector<Form> relatedForms; //Other forms for managing the same class, used in the definition of the form internals
	
	/**
	 * @return the baseClass
	 */
	public Class getBaseClass() {
		return baseClass;
	}

	/**
	 * @param baseClass the baseClass to set
	 */
	public void setBaseClass(Class baseClass) {
		this.baseClass = baseClass;
	}

	public Form(String name) {
		super(name);
	    items= new Vector<GUIElement>(0,1);	
	    relatedForms= new Vector<Form>(0,1);
	}

	/**
	 * @return the items
	 */
	public Vector<GUIElement> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(Vector<GUIElement> items) {
		this.items = items;
	}
	
	/**
	 * @param items the items to set
	 */
	public void addItem(GUIElement item) {
		this.items.add(item);
	}

	/**
	 * @return the type
	 */
	public FormActionKind getActionKind() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setActionKind(FormActionKind type) {
		this.type = type;
	}

	/**
	 * @return the relatedForms
	 */
	public Vector<Form> getRelatedForms() {
		return relatedForms;
	}

	/**
	 * @param relatedForms the relatedForms to set
	 */
	public void setRelatedForms(Vector<Form> relatedForms) {
		this.relatedForms = relatedForms;
	}
	
	/**
	 * @param relatedForms the relatedForms to set
	 */
	public void addRelatedForm(Form relatedForms) {
		this.relatedForms.add(relatedForms);
	}
	
	/**
	 * @param relatedForms the relatedForms to set
	 */
	public Form selectRelatedForm(FormActionKind action) {
		Form aux=null; boolean found=false;
		Iterator<Form> itRel= relatedForms.iterator();
		while (itRel.hasNext() && !found) {
			Form form = (Form) itRel.next();
			if (form.getActionKind()==action) {
				aux=form; found=true;
			}
		}
		return aux;
	}

	/**
	 * @return the b_Insert
	 */
	public Button getB_Insert() {
		return b_Insert;
	}

	/**
	 * @param insert the b_Insert to set
	 */
	public void setB_Insert(Button insert) {
		b_Insert = insert;
	}

	/**
	 * @return the b_Close
	 */
	public Button getB_Close() {
		return b_Close;
	}

	/**
	 * @param close the b_Close to set
	 */
	public void setB_Close(Button close) {
		b_Close = close;
	}

	/**
	 * @return the b_Submit
	 */
	public Button getB_Submit() {
		return b_Submit;
	}

	/**
	 * @param submit the b_Submit to set
	 */
	public void setB_Submit(Button submit) {
		b_Submit = submit;
	}

}
