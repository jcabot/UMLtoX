/**
 * 
 */
package mdeServices.xmi.exportXMI;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;


import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;


import mdeServices.metamodel.ModelElement;
import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;



/**
 *  Abstract class for all types of XMI imports
 * 
 * @version 0.1 Aug 2008
 * @author jcabot
 *
 */
public abstract class ExportXMI {

	protected File xmi;
	protected Project project;
    PrintWriter out;
	protected Document document;
	protected Element root;
	protected HashMap<ModelElement, String> hash;
	protected HashMap<String, String> hashStereotypes;
	int autoId=0;
    int nestedLevel=0;
    
	public ExportXMI() {}
	
	/**
	 * @param p Project to be exported
	 * @param xmi  File where to add the project xmi description
	 */
	public void exportFile(Project p, File xmi) throws JDOMException, IOException
	{
		this.xmi = xmi;
		project=p;
		hash = new HashMap<ModelElement,String>(0);
		hashStereotypes = new HashMap<String,String>(0);
		out = new PrintWriter(xmi);
		//Since we are now allowing exports to textual languages
		if (isXMLTypeFile()) out.println("<?xml version = '1.0' encoding = 'UTF-8' ?>");
		exportProject(); //Starting the import of the file
       	out.flush();
       	out.close();
    }
	
	private void exportProject()
	{
	   exportProjectInfo();
	   exportStaticModel(project.getStaticModel());
	   endDocument();
	}
	
	protected int newID(){autoId++;return autoId;}
	
	protected void incNested(){nestedLevel++;}
	
	protected void decNested(){nestedLevel--;}
	
	protected String nestedString(){
		String tab= "";
		for(int i=0;i<=nestedLevel;i++){tab=tab.concat("  ");}
		return tab;
	}

	/* Abstract methods */
	protected abstract void exportStaticModel(StaticModel s);
	
	protected abstract void exportProjectInfo();
	
	protected abstract void endDocument();
	
	public abstract boolean isNAriesAssocSupported();
	public abstract boolean isAssocClassSupported();
	public abstract boolean isMultipleInheritanceSupported();
	public abstract boolean isTextNormalizationNeeded(); 
	
	public boolean isXMLTypeFile()
	{
	  return true;// Default value	
	}
		
}
