package mdeServices;

import java.io.File;
import java.io.IOException;

import org.jdom.JDOMException;

import mdeServices.metamodel.StaticModel;
import mdeServices.transformations.T_MultipleToSingleInheritance;
import mdeServices.transformations.T_NormalizeNames;
import mdeServices.transformations.Transformation;
import mdeServices.transformations.TransformationNotApplicable;
import mdeServices.xmi.exportXMI.ExportXMI;
import mdeServices.xmi.exportXMI.ExportXMIArgoUML_v024;
import mdeServices.xmi.exportXMI.ExportXMIEclipseUML2;
import mdeServices.xmi.exportXMI.ExportXMIException;
import mdeServices.xmi.exportXMI.ExportXMIFactory;
import mdeServices.xmi.exportXMI.ExportXMIMoskitt;
import mdeServices.xmi.exportXMI.ExportXMIPoseidon_v5;
import mdeServices.xmi.importXMI.ImportXMI;
import mdeServices.xmi.importXMI.ImportXMIException;
import mdeServices.xmi.importXMI.ImportXMIFactory;

public class XMIGenerator extends MDEServices {

	protected ImportXMI impXMI;
	protected ExportXMI expXMI;

	/**
	 * 
	 */
	public XMIGenerator() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void generate(File input, String importFromTool, File output, String exportForTool, String user) throws TransformationNotApplicable,JDOMException, IOException, ImportXMIException, ExportXMIException
	{
		impXMI = ImportXMIFactory.createImportXMI(importFromTool);
		p= impXMI.importFile(input);
		p.setUser(user);
		
		expXMI = ExportXMIFactory.createExportXMI(exportForTool);
		StaticModel s=p.getStaticModel();
		if (!expXMI.isMultipleInheritanceSupported())
		{
		    Transformation t;
		    t= new T_MultipleToSingleInheritance(p,o);
		    t.exec();
		}
		if (expXMI.isTextNormalizationNeeded())
		{
		    Transformation t;
		    t= new T_NormalizeNames(p,o);
		    t.exec();
		}
		expXMI.exportFile(p, output);
		
	}
	
	//Method that returns the termination of the files corresponding to that targetTool
	//It would be more elegant to make each tool reimplement this method but we need the
	//info before instantiating the right exportXMI class
	public String getTermination(String targetTool)
	{
       String term=".xmi"; //default
	   if (targetTool.equals("ArgoUML_v024")) term=".xmi";
       else if (targetTool.equals("ArgoUML_v028")) term=".xmi";
       else if (targetTool.equals("Poseidon_v5")) term=".xmi";
       else if (targetTool.equals("EclipseUML2")) term=".uml";
       else if (targetTool.equals("MOSKitt")) term=".uml";
       else if (targetTool.equals("yUML")) term=".txt";
       return term;
	}
}
