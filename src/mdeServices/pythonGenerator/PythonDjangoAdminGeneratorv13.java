package mdeServices.pythonGenerator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Vector;

import mdeServices.metamodel.Attribute;
import mdeServices.metamodel.Class;
import mdeServices.metamodel.Classifier;
import mdeServices.metamodel.DT_Boolean;
import mdeServices.metamodel.DT_Char;
import mdeServices.metamodel.DT_Currency;
import mdeServices.metamodel.DT_Date;
import mdeServices.metamodel.DT_DateTime;
import mdeServices.metamodel.DT_Integer;
import mdeServices.metamodel.DT_LongInteger;
import mdeServices.metamodel.DT_LongReal;
import mdeServices.metamodel.DT_Real;
import mdeServices.metamodel.DT_ShortInteger;
import mdeServices.metamodel.DT_String;
import mdeServices.metamodel.DT_UnsignedInteger;
import mdeServices.metamodel.DT_UnsignedLongInteger;
import mdeServices.metamodel.DT_UnsignedLongReal;
import mdeServices.metamodel.DT_UnsignedReal;
import mdeServices.metamodel.DT_UnsignedShortInteger;
import mdeServices.metamodel.Enumeration;
import mdeServices.metamodel.ModelElement;
import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.stereotypes.S_ForeignKey;
import mdeServices.metamodel.stereotypes.S_ReifiedAssociation;
import mdeServices.metamodel.stereotypes.S_Unique;
import mdeServices.options.LangManager;
import mdeServices.options.Options;

import mdeServices.transformations.TransformationNotApplicable;

import mdeServices.xmi.importXMI.ImportXMIException;

import org.jdom.JDOMException;

public class PythonDjangoAdminGeneratorv13 {
	
	protected LangManager l;
	protected Options o;
	protected Project p;
	protected StaticModel m;
	protected PrintWriter out;
	
	protected int nestedLevel=0; //For printing issues	

	public void generate(Project _p, PrintWriter _out, Options _o, LangManager _l)throws IOException,PythonDomainGenerationException
	{
		p=_p;
		m=p.getStaticModel();
		l=_l;
		o=_o;
		out=_out;
		out.println(nestedString()+commentSymbol()+ l.getString("heading.python.admin.script") + p.getName()+commentSymbolEnd());
		out.println();
		Vector<Class> allClasses= m.getAllPersistentClasses();
		
		//importing the admin module
		
		out.println(nestedString()+ "from django.contrib import admin"  +  commentSymbolEnd());
		
		//Generation of the admin information for each class	
		
		//first iteration is used to generate the imports
		
		Iterator<Class> itClImp= allClasses.iterator();
	
		while (itClImp.hasNext())
		{
		  Class cl=itClImp.next();
		  out.println(nestedString()+ "from "+ p.getName().toLowerCase()+".models import " + cl.getName());
		}
		
		//second to generate the admin info for each class
		out.println();
		Iterator<Class> itCl= allClasses.iterator();
		while (itCl.hasNext())
		{
		  Class cl=itCl.next();
		  generateClass(cl,out);
		}
		out.flush();
		out.close();
					
	}
		
	protected void generateClass(Class c, PrintWriter out) throws IOException,PythonDomainGenerationException

	{
	  try
	  {
		String nameAdminClass=c.getName()+"Admin";
		out.println(nestedString() + "class " + nameAdminClass+ "(admin.ModelAdmin):");
		incNested();
		int numFieldsDisplay=new Integer(o.getProperty("django.admin.list.display")).intValue();
		if (numFieldsDisplay==0)
		{
			out.println(nestedString() + "pass"); // To take all default options
		}
		else
		{
			String listFields="list_display = (";
			Iterator<Attribute> itAt=c.getAtt().iterator();
			if (!(new Boolean(o.getProperty("django.admin.list.display.id")).booleanValue())) itAt.next();
			int i=0;
			for (i = 0; i < numFieldsDisplay && itAt.hasNext(); i++) {
				listFields=listFields+"'"+itAt.next().getName()+"',";
			}
			if (i==0) listFields="pass"; //we simply ignore this parameter since there´s nothing to show
			if(i==1) //Django expects a list so if it´s a single value we need to add with ",)";
					 listFields=listFields +")"; 
			if (i>1) listFields=listFields.substring(0,listFields.length()-1)+")";
			out.println(nestedString() + listFields);
		}
			
		out.println();
		decNested();
		out.println(nestedString() + "admin.site.register("+ c.getName()+", "+nameAdminClass+")");
		out.println();
	  }catch(Exception e)
		{ throw new PythonDomainGenerationException("Error when creating the admin schema file");}
	}
	
	protected String commentSymbol(){ return "#";}

	protected String commentSymbolEnd(){return "";}
	
	protected void incNested(){nestedLevel++;}
	
	protected void decNested(){nestedLevel--;}
	
	protected String nestedString(){
		String tab= "";
		for(int i=0;i<nestedLevel;i++){tab=tab.concat("    ");}
		return tab;
	}
	
}
