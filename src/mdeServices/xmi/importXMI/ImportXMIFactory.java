package mdeServices.xmi.importXMI;

import java.io.File;
import java.io.IOException;

import org.jdom.JDOMException;


/**
 *  Factory for instantiating the right ImportXMI class
 * 
 * @version 0.1 25 Aug 2008
 * @author jcabot
 *
 */

public class ImportXMIFactory {
	
	public static ImportXMI createImportXMI(String tool) throws JDOMException, IOException, ImportXMIException
	{
		ImportXMI imp=null;
        /* Automatic selection discarded since not all tools have enough information for that
        *  if(tool==null) //Automatic selection
        {
        	 Element xmiExporter = document.getRootElement().getChild("XMI.header").getChild("XMI.documentation").getChild("XMI.exporter"); 
          	 if (xmiExporter.getText().contains("ArgoUML"))
        	{	imp=new ImportXMIArgoUML();
        	}
        	else throw new JDOMException(xmi.getPath());
        }
        else */
        //Selection by name
        	if (tool.equals("ArgoUML_v024")) imp=new ImportXMIArgoUML_v024();
        	else if (tool.equals("ArgoUML_v028")) imp=new ImportXMIArgoUML_v028();    
        	else if (tool.equals("Poseidon_v5")) imp=new ImportXMIPoseidon_v5();     		
        	else if (tool.equals("Visio_v2007")) imp=new ImportXMIVisio_v2007();   
        	else if (tool.equals("EclipseUML2")) imp=new ImportXMIEclipseUML2();   
        	else if (tool.equals("MOSKitt")) imp=new ImportXMIMoskitt();   
        	else if (tool.equals("MagicDraw_v15")) imp=new ImportXMIMagicDraw_v15();   
        	else if (tool.equals("Modelio_v1")) imp=new ImportXMIModelio_v1(); 
       	return imp;
	}
	

	

	
}
