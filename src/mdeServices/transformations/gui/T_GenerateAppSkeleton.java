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
 * Transformation class for generating the skeleton of the forms of the application
 * 
 * @version 0.1 Dec 2008
 * @author jcabot
 *
 */


public class T_GenerateAppSkeleton extends Transformation {

	DynamicModel d;
	StaticModel s;

	/**
	 * @param p
	 * @param o
	 * @throws TransformationNotApplicable
	 */
	public T_GenerateAppSkeleton(Project p, Options o)	{
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
	public T_GenerateAppSkeleton(Project p, Options o, LangManager l)
		{
		super(p, o,l);
		// TODO Auto-generated constructor stub
		d=p.getDynamicModel();
		s=p.getStaticModel();
		}
	
	@Override
	public void exec() throws TransformationNotApplicable {
		// TODO Auto-generated method stub
		String nameMenu = o.getProperty("app.menu.prefix") + "MainMenu" + o.getProperty("app.menu.suffix"); 
		Menu menu= new Menu(nameMenu);
		d.setMainMenu(menu);
		
		//Falta login i main form si cal
		Iterator<Package> p=s.getPackages().iterator();
		while (p.hasNext()) {
			Package package1 = (Package) p.next();
			processPackage(package1,menu);
		}
	}
		
	protected void processPackage(Package pack, Menu menu)
	{
		if (pack.getAllClasses().size()>0)
		{
			MenuItem menuPack= new MenuItem(o.getProperty("app.menu.item.prefix") + "Pack"+ pack.getName() +o.getProperty("app.menu.item.suffix"));
			menuPack.setCaption(pack.getName());
			Iterator<Class> c= pack.getAllPersistentClasses().iterator();
			while (c.hasNext()) {
				Class class1 = (Class) c.next();
			 	//General item for all the forms associated to the class
				MenuItem mi= new MenuItem(o.getProperty("app.menu.item.prefix") + class1.getName() + o.getProperty("app.menu.item.suffix"));
				mi.setCaption(class1.getName());
				
				//Specific forms (and menu items) for each individual form
				MenuItem miVis=  new MenuItem(o.getProperty("app.menu.item.prefix") + "Vis"+ class1.getName() + o.getProperty("app.menu.item.suffix") );
				Form fVis= new Form(o.getProperty("app.form.prefix") + "Vis" + class1.getName() + o.getProperty("app.form.suffix"));
				fVis.setCaption(l.getString("app.caption.form.visualization") +  class1.getName());
				fVis.setActionKind(FormActionKind.FA_VISUALIZE);
				fVis.setBaseClass(class1);
				d.addForm(fVis); mi.addItem(miVis); miVis.setDestination(fVis);miVis.setCaption(l.getString("app.caption.menu.item.visualize"));
				if ( ! class1.isReadOnly()) //For non-read only classes we create a form for inserting data and another for updating it
				{
					Form fIns= new Form(o.getProperty("app.form.prefix") + "Ins"+ class1.getName() + o.getProperty("app.form.suffix"));
					fIns.setCaption(l.getString("app.caption.form.insert") +  class1.getName());
					fIns.setActionKind(FormActionKind.FA_INSERT);
					fIns.setBaseClass(class1);
					d.addForm(fIns);fVis.addRelatedForm(fIns);
					if(o.getProperty("app.menu.item.insert").equals("true")) //We create a separate menu entry for the insert form
					{
						MenuItem miIns=  new MenuItem(o.getProperty("app.menu.item.prefix") + "Ins"+ class1.getName() + o.getProperty("app.menu.item.suffix") );
						mi.addItem(miIns);miIns.setDestination(fIns);
						miIns.setCaption(l.getString("app.caption.menu.item.insert"));
					}
					
					Form fUpd= new Form(o.getProperty("app.form.prefix") + "Upd"+ class1.getName() + o.getProperty("app.form.suffix"));
					fUpd.setCaption(l.getString("app.caption.form.update") +  class1.getName());
					fUpd.setActionKind(FormActionKind.FA_UPDATE);
					fUpd.setBaseClass(class1);
					d.addForm(fUpd); fVis.addRelatedForm(fUpd);
				}
				else //if it is read only we just create a form to visualize the details of the row
				{ 
					Form fDet= new Form(o.getProperty("app.form.prefix") + "Det" + class1.getName() + o.getProperty("app.form.suffix"));
					fDet.setCaption(l.getString("app.caption.form.visdetails") +  class1.getName());
					fDet.setActionKind(FormActionKind.FA_VISDETAILS);
					fDet.setBaseClass(class1);
					d.addForm(fDet);fVis.addRelatedForm(fDet);
				}
				if (o.getProperty("app.menu.groupby.package").equals("true") && !s.getPackages().get(0).getName().equals(o.getProperty("db.package.default.name")))
				{ menuPack.addItem(mi); } //We create submenu for each package
				else {menu.addItem(mi);}
			}
			if (o.getProperty("app.menu.groupby.package").equals("true") && !s.getPackages().get(0).getName().equals(o.getProperty("db.package.default.name")))
			{ menu.addItem(menuPack);}
				
		}
		if (pack.getFirstLevelPackages().size()>0)
		{
			Iterator<Package> p=pack.getFirstLevelPackages().iterator();
			while (p.hasNext()) {
				Package package1 = (Package) p.next();
				processPackage(package1,menu);
			} 
				
		}
	}
		
	

}
