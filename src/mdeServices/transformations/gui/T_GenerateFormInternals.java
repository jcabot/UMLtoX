package mdeServices.transformations.gui;

import java.util.Iterator;
import java.util.Vector;

import mdeServices.metamodel.ChangeabilityKind;
import mdeServices.metamodel.DT_Boolean;
import mdeServices.metamodel.PrimitiveDataType;
import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.Stereotype;
import mdeServices.metamodel.gui.Button;
import mdeServices.metamodel.gui.ButtonKind;
import mdeServices.metamodel.gui.CheckBox;
import mdeServices.metamodel.gui.ComboBox;
import mdeServices.metamodel.gui.DynamicModel;
import mdeServices.metamodel.gui.EditBox;
import mdeServices.metamodel.gui.Form;
import mdeServices.metamodel.gui.FormActionKind;
import mdeServices.metamodel.gui.Grid;
import mdeServices.metamodel.gui.GridColumn;
import mdeServices.metamodel.gui.Menu;
import mdeServices.metamodel.gui.MenuItem;
import mdeServices.metamodel.stereotypes.S_ForeignKey;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.Attribute;
import mdeServices.options.LangManager;
import mdeServices.options.Options;
import mdeServices.transformations.Transformation;
import mdeServices.transformations.TransformationNotApplicable;


/**
 * Transformation class for generating the internal structure of each form of the application
 * 
 * @version 0.1 Dec 2008
 * @author jcabot
 *
 */

public class T_GenerateFormInternals extends Transformation {

	DynamicModel d;
	StaticModel s;
	LangManager l;
	boolean autoIncrement;

	
	/**
	 * @param p
	 * @param o
	 * @throws TransformationNotApplicable
	 */
	public T_GenerateFormInternals(Project p, Options o)
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
	public T_GenerateFormInternals(Project p, Options o, LangManager l, boolean autoIncrement)
			throws TransformationNotApplicable {
		super(p, o);
		// TODO Auto-generated constructor stub
		d=p.getDynamicModel(); this.o=o;
		s=p.getStaticModel();
		this.l=l;
		this.autoIncrement=autoIncrement;
		
	}
	
	@Override
	public void exec() throws TransformationNotApplicable {
		// TODO Auto-generated method stub
		Iterator<Form> itForm=d.getForms().iterator();
		while (itForm.hasNext()) {
			Form form = (Form) itForm.next();
			if (form.getActionKind()==FormActionKind.FA_VISUALIZE) generateVisualize(form);
			else if(form.getActionKind()==FormActionKind.FA_INSERT) generateInsert(form);
			else if(form.getActionKind()==FormActionKind.FA_UPDATE) generateUpdate(form);
			else if(form.getActionKind()==FormActionKind.FA_VISDETAILS) generateVisDetails(form);
		}
	}
		
	protected void generateVisualize(Form form)
	{
		//Creation of the main grid and the associated buttons, depending on the readOnly property of the class
		createGrid(form.getBaseClass(),form,form.getBaseClass().isReadOnly(),null);
		//Final "close" button
	    Button bClose= new Button(o.getProperty("app.button.prefix") + "Close" + o.getProperty("app.button.suffix"));
    	bClose.setCaption(l.getString("app.caption.button.close"));
    	bClose.setType(ButtonKind.B_CLOSE);
    	bClose.setDestination(d.getInitialForm());
        form.setB_Close(bClose);
	    Button bInsert= new Button(o.getProperty("app.button.prefix") + "Close" + o.getProperty("app.button.suffix"));
    	bInsert.setCaption(l.getString("app.caption.button.close"));
    	bInsert.setType(ButtonKind.B_CLOSE);
    	bInsert.setDestination(selectInsertForm(form.getRelatedForms()));
        form.setB_Insert(bInsert);
	}

	protected Form selectInsertForm(Vector<Form> forms)
	{
		Iterator<Form> fs= forms.iterator();
		boolean found=false; Form aux=null;
		while (fs.hasNext() && !found) {
			Form form = (Form) fs.next();
			if (form.getActionKind()==FormActionKind.FA_INSERT)
			{ aux=form; found=true;}
		}
		return aux;
	}
		
		
	protected void generateInsert(Form form)
	{
		if (!autoIncrement) //Addition of the primary key attributes
		{
	        Attribute at= form.getBaseClass().getPrimaryKeyAttribute();
			createEditBox(at,form,false);
		}
		Iterator<Attribute> itPlain= form.getBaseClass().getPlainAttributes().iterator();
		while (itPlain.hasNext()) {
			Attribute at = (Attribute) itPlain.next();
			if (at.getType() instanceof DT_Boolean)
			{
				createCheckBox(at,form,false);
			}
			else
			{
				createEditBox(at,form,false);
			}
		}
		if (o.getProperty("app.foreignkeys.preferred.control").equals("editbox"))
		{  //Addition of new edit boxes for the foreign key attributes
			Iterator<Attribute> itFK= form.getBaseClass().getFKAttributes().iterator();
			while (itFK.hasNext()) {
				Attribute at = (Attribute) itFK.next();
				createEditBox(at,form,false);
			}
		}
		else if (o.getProperty("app.foreignkeys.preferred.control").equals("combobox"))
		{ //Creation of a single combo box for each foreing key
			Vector<S_ForeignKey> vSFK = form.getBaseClass().getForeignKeys();
			for (Iterator<S_ForeignKey> it = vSFK.iterator(); it.hasNext();) {
				S_ForeignKey st = it.next();
				ComboBox c= new ComboBox(o.getProperty("app.combobox.prefix") + st.getReferencedClass().getName() + o.getProperty("app.combobox.suffix"));
			    c.setCaption(st.getReferencedClass().getName());
			    c.setOwn(st.getOwnAtt()); c.setRef(st.getRefAtt());
			}
		}
	    Button bSubmit= new Button(o.getProperty("app.button.prefix") + "Insert" + o.getProperty("app.button.suffix"));
    	bSubmit.setCaption(l.getString("app.caption.button.insert"));
    	bSubmit.setType(ButtonKind.B_SUBMIT);
    	bSubmit.setDestination(form);
        form.setB_Submit(bSubmit);
	}
	
	protected void generateUpdate(Form form)
	{
		Attribute atPk = form.getBaseClass().getPrimaryKeyAttribute();
		 createEditBox(atPk,form,true);  //Pk attributes cannot be modified
		
		Iterator<Attribute> itPlain= form.getBaseClass().getPlainAttributes().iterator();
		while (itPlain.hasNext()) {
			Attribute at = (Attribute) itPlain.next();
			if (at.getType() instanceof DT_Boolean)
			{
				createCheckBox(at,form,false);
			}
			else
			{
				createEditBox(at,form,at.getChangeability()==ChangeabilityKind.C_READONLY);
			}
		}
		if (o.getProperty("app.foreignkeys.preferred.control").equals("editbox"))
		{  //Addition of new edit boxes for the foreign key attributes
			Iterator<Attribute> itFK= form.getBaseClass().getFKAttributes().iterator();
			while (itFK.hasNext()) {
				Attribute at = (Attribute) itFK.next();
				createEditBox(at,form,false);
			}
		}
		else if (o.getProperty("app.foreignkeys.preferred.control").equals("combobox"))
		{ //Creation of a single combo box for each foreing key
			Vector<S_ForeignKey> vSFK = form.getBaseClass().getForeignKeys();
			for (Iterator<S_ForeignKey> it = vSFK.iterator(); it.hasNext();) {
				S_ForeignKey st = it.next();
				ComboBox c= new ComboBox(o.getProperty("app.combobox.prefix") + st.getReferencedClass().getName() + o.getProperty("app.combobox.suffix"));
			    c.setCaption(st.getReferencedClass().getName());
			    c.setOwn(st.getOwnAtt()); c.setRef(st.getRefAtt());
			}
		}
		
		if(o.getProperty("app.show.related.form.update").equals("true"))
		{  //We create a grid for each class with a foreign key to this class
			Iterator<Class> it= s.getAllPersistentClasses().iterator();
			while (it.hasNext()) {
				Class class1 = (Class) it.next();
				Iterator<S_ForeignKey> st= class1.getForeignKeys().iterator();
				while (st.hasNext()) {
					S_ForeignKey sFK = st.next();
					if (sFK.getReferencedClass()==form.getBaseClass())
					{
						createGrid(class1,form,class1.isReadOnly(), sFK);
					}
				}
			}
		}
	    Button bSubmit= new Button(o.getProperty("app.button.prefix") + "Close" + o.getProperty("app.button.suffix"));
    	bSubmit.setCaption(l.getString("app.caption.button.close"));
    	bSubmit.setType(ButtonKind.B_SUBMIT);
    	bSubmit.setDestination(form);
        form.setB_Submit(bSubmit);
	}
	
	protected void generateVisDetails(Form form)
	{
		Attribute atPk= form.getBaseClass().getPrimaryKeyAttribute();
		createEditBox(atPk,form,true);  //Pk attributes cannot be modified
		Iterator<Attribute> itPlain= form.getBaseClass().getPlainAttributes().iterator();
		while (itPlain.hasNext()) {
			Attribute at = (Attribute) itPlain.next();
			if (at.getType() instanceof DT_Boolean)
			{
				createCheckBox(at,form,true);
			}
			else
			{
				createEditBox(at,form,true);
			}
		}
		if (o.getProperty("app.foreignkeys.preferred.control").equals("editbox"))
		{  //Addition of new edit boxes for the foreign key attributes
			Iterator<Attribute> itFK= form.getBaseClass().getFKAttributes().iterator();
			while (itFK.hasNext()) {
				Attribute at = (Attribute) itFK.next();
				createEditBox(at,form,true);
			}
		}
		else if (o.getProperty("app.foreignkeys.preferred.control").equals("combobox"))
		{ //Creation of a single combo box for each foreing key
			Vector<S_ForeignKey> vSFK = form.getBaseClass().getForeignKeys();
			for (Iterator<S_ForeignKey> it = vSFK.iterator(); it.hasNext();) {
				S_ForeignKey st = it.next();
				ComboBox c= new ComboBox(o.getProperty("app.combobox.prefix") + st.getReferencedClass().getName() + o.getProperty("app.combobox.suffix"));
			    c.setCaption(st.getReferencedClass().getName());
			    c.setOwn(st.getOwnAtt()); c.setRef(st.getRefAtt());
			    c.setReadOnly(true);
			}
		}
				
		if(o.getProperty("app.show.related.form.detail").equals("true"))
		{  //We create a grid for each class with a foreign key to this class
			Iterator<Class> it= s.getAllPersistentClasses().iterator();
			while (it.hasNext()) {
				Class class1 = (Class) it.next();
				Iterator<S_ForeignKey> st= class1.getForeignKeys().iterator();
				while (st.hasNext()) {
					S_ForeignKey sFK= st.next();
					if (sFK.getReferencedClass()==form.getBaseClass())
					{
						createGrid(class1,form,class1.isReadOnly(), sFK);
					}
				}
			}
		}
	}
	
	protected void createCheckBox(Attribute at, Form form, boolean isReadOnly)
	{
		CheckBox ck= new CheckBox(o.getProperty("app.checkbox.prefix") + at.getName() + o.getProperty("app.checkbox.suffix"));
		ck.setCaption(at.getName());
		ck.setAttribute(at);ck.setReadOnly(isReadOnly);
		form.addItem(ck);
	}
	
	protected void createEditBox(Attribute at, Form form, boolean isReadOnly)
	{
		EditBox ed= new EditBox(o.getProperty("app.editbox.prefix") + at.getName() + o.getProperty("app.button.suffix"));
		ed.setCaption(at.getName());
		ed.setAttribute(at);
		ed.setReadOnly(isReadOnly);
		ed.setSize((new Integer(o.getProperty("app.editbox.size"))).intValue());
		ed.setContentSize(((PrimitiveDataType) at.getType()).getLength());
		form.addItem(ed);
	}
	
	
	protected void createGrid(Class cl, Form form, boolean isReadOnly, S_ForeignKey s)
	{
		Grid grid=new Grid(o.getProperty("app.form.prefix") + cl.getName()+ o.getProperty("app.form.suffix"));
		int nColumns= new Integer(o.getProperty("app.grid.columns.maxnumber")).intValue();
	    Vector<Attribute> vAt=cl.getAtt();
	    for (int i = 0; (i < vAt.size()) && (i<nColumns); i++) {
			Attribute at= vAt.get(i);
			GridColumn gc= new GridColumn(at.getName());
	    	gc.setAtt(at);
	    	gc.setSize((new Integer(o.getProperty("app.grid.columns.size"))).intValue());
	    	grid.addColumn(gc);
	  	}
	    grid.setCaption(cl.getName());

	    if (s!=null)
	    {	
	    	grid.setParam(s.getOwnAtt());
	    	grid.setBaseClass(s.getReferencedClass());
	    }
	    else {
	    	grid.setBaseClass(form.getBaseClass());
	    }
	    
	    if(!isReadOnly)
	    {  //Buttons for the grid
	    	Button bUpd= new Button(o.getProperty("app.button.prefix") + "Upd" + cl.getName()+ o.getProperty("app.button.suffix"));
	    	grid.setB_Update(bUpd);
	    	bUpd.setType(ButtonKind.B_SELECT);
	    	bUpd.setDestination(form.selectRelatedForm(FormActionKind.FA_UPDATE));
	    	bUpd.setCaption(l.getString("app.caption.button.update"));
	    
    	   	//Deletion of the selected object
	    	Button bDelete= new Button(o.getProperty("app.button.prefix") + "Del" + cl.getName()+ o.getProperty("app.button.suffix"));
	    	grid.setB_Delete(bDelete); bDelete.setCaption(l.getString("app.caption.button.delete"));
	    	bDelete.setType(ButtonKind.B_DELETE); 
	    	bDelete.setDestination(form);
	     	
	    	Button bInsert= new Button(o.getProperty("app.button.prefix") + "Ins" + cl.getName()+ o.getProperty("app.button.suffix"));
	    	form.addItem(bInsert); bInsert.setCaption(l.getString("app.caption.button.insert"));
	    	bInsert.setType(ButtonKind.B_OPEN);	
	    }
	    else
	    {
	    	Button bDet= new Button(o.getProperty("app.button.prefix") + "Det" + cl.getName()+ o.getProperty("app.button.suffix"));
		    grid.setB_Details(bDet);
		    bDet.setType(ButtonKind.B_SELECT);bDet.setCaption(l.getString("app.caption.button.details"));
		    bDet.setDestination(form.selectRelatedForm(FormActionKind.FA_VISDETAILS));
		    grid.setReadOnly(true);
		     	
	    }
	    form.addItem(grid);
	}
	
}
