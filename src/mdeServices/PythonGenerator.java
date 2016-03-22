/**
 * 
 */
package mdeServices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.jdom.JDOMException;

import mdeServices.dbGenerator.DBGenerationException;
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
import mdeServices.metamodel.DT_ShortInteger;
import mdeServices.metamodel.DT_UnsignedInteger;
import mdeServices.metamodel.DT_UnsignedLongInteger;
import mdeServices.metamodel.DT_LongReal;
import mdeServices.metamodel.DT_Real;
import mdeServices.metamodel.DT_String;
import mdeServices.metamodel.DT_UnsignedLongReal;
import mdeServices.metamodel.DT_UnsignedReal;
import mdeServices.metamodel.DT_UnsignedShortInteger;
import mdeServices.metamodel.Enumeration;
import mdeServices.metamodel.ModelElement;
import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.database.MultiplicityTrigger;
import mdeServices.metamodel.database.Trigger;
import mdeServices.metamodel.database.TriggerEventKind;
import mdeServices.metamodel.stereotypes.S_ForeignKey;
import mdeServices.metamodel.stereotypes.S_Unique;
import mdeServices.options.Options;
import mdeServices.phpGenerator.PHPGenerationException;
import mdeServices.pythonGenerator.PythonGenerationException;
import mdeServices.transformations.T_AddDefaultNameAssoc;
import mdeServices.transformations.T_AddDefaultNameAssocEnds;
import mdeServices.transformations.T_AddDefaultTypeAttributes;
import mdeServices.transformations.T_AssociationsMN_ToAssocClass;
import mdeServices.transformations.T_AssociationsNary_ToAssocClass;
import mdeServices.transformations.T_AssocClassToClass;
import mdeServices.transformations.T_PreciseDataTypeInformation;
import mdeServices.transformations.Transformation;
import mdeServices.transformations.TransformationNotApplicable;
import mdeServices.transformations.database.T_AddDefaultNameFKConstraints;
import mdeServices.transformations.database.T_AddDefaultNamePKConstraints;
import mdeServices.transformations.database.T_AddDefaultNameUniqueConstraints;
import mdeServices.transformations.database.T_AddPKeys;
import mdeServices.transformations.database.T_AddPKeysReifiedClasses;
import mdeServices.transformations.database.T_AssociationsToForeignKeys;
import mdeServices.transformations.database.T_MarkAsPersistent;
import mdeServices.transformations.database.T_NoGeneralizations;
import mdeServices.utils.Digraph;
import mdeServices.utils.DigraphNode;
import mdeServices.xmi.exportXMI.ExportXMIException;
import mdeServices.xmi.importXMI.ImportXMI;
import mdeServices.xmi.importXMI.ImportXMIException;
import mdeServices.xmi.importXMI.ImportXMIFactory;

/**
 *  Main Class for generating the Python application 
 * 
 * @version 0.1 Feb 2011
 * @author jcabot
 *
 */

public abstract class PythonGenerator extends MDEServices{

   //different implementations may return a different number of output files (e.g. one for the domain and one for the interface)
	public void generate(File input, String importFromTool, File output, String user) throws ImportXMIException, JDOMException,IOException,PythonGenerationException, TransformationNotApplicable, FileNotFoundException
	{
		//If not yet done, initializing the language resource and properties
		if (o==null || l==null)
		{
			System.out.println("Configuration files not initialized");
			initialize(user);
		}
		//Importing the model
		ImportXMI impXMI = ImportXMIFactory.createImportXMI(importFromTool);
		p= impXMI.importFile(input);
		p.setUser(user);
		generateScripts(p,output,user);
	}
	
	public void generate(Project p, File output, String user) throws ImportXMIException, JDOMException,IOException,PythonGenerationException, TransformationNotApplicable, FileNotFoundException
	{
		//If not yet done, initializing the language resource and properties
		if (o==null || l==null)
		{
			System.out.println("Configuration files not initialized");
			initialize(user);
		}
		//Importing the model
		p.setUser(user);
		generateScripts(p,output,user);
		
	}
		
	protected abstract void generateScripts(Project p, File output, String user) throws IOException,PythonGenerationException, TransformationNotApplicable, FileNotFoundException;
	
			
}

