package mdeServices.system.commandLine;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.jdom.JDOMException;

 

import  mdeServices.system.commandLine.ArgParser.*;
import mdeServices.system.commandLine.Arguments.*;
import mdeServices.xmi.exportXMI.ExportXMI;
import mdeServices.xmi.exportXMI.ExportXMIArgoUML;
import mdeServices.xmi.exportXMI.ExportXMIFactory;
import mdeServices.xmi.importXMI.ImportXMI;
import mdeServices.xmi.importXMI.ImportXMIException;
import mdeServices.xmi.importXMI.ImportXMIFactory;
import mdeServices.AppGeneratorFactory;
import mdeServices.AppGeneratorPHP;
import mdeServices.DBGeneratorFactory;
import mdeServices.MDEServices;
import mdeServices.DBGenerator;
import mdeServices.AppGenerator;
import mdeServices.PHPGenerator;
import mdeServices.PictureToUMLGenerator;
import mdeServices.PythonGenerator;
import mdeServices.PythonGeneratorFactory;
import mdeServices.phpGenerator.PHPSymfonyGenerator;
import mdeServices.pythonGenerator.PythonDjangoGeneratorv13;
import mdeServices.XMIGenerator;

import mdeServices.metamodel.Project;
import mdeServices.options.LangManager;
import mdeServices.options.Options;

/**
 *  Basic class for command-line execution of the system 
 * 
 * @version 0.1 25 Aug 2008
 * @author jcabot
 *
 */

public class Launch {

public static void main(String[] args) {
	Options o=null;
	LangManager l=null;
	AppGenerator mdePHP=null;
	AppGenerator mdeDelphi=null;
	
	ImportXMI impXMI=null;
    String userName=null;
    File xmiFile=null;
    Project p=null;
    
	ArgParser parser = new ArgParser(Arguments.description) {
		@Override
		protected void processArgument(Object code, String[] params) {
			Arguments.processArgument(code, params);
		}
	};
		
	try {
		parser.parse(args);
	} catch (InvalidArgument e) {
		error("Invalid Argument");
	} catch (InvalidParametersNum e) {
		error("Invalid number of parameters");
	}
		

	//Retrieving options for the user
	if (Arguments.userName.length() > 0) {
		o = new Options(Arguments.userName);
		l = new LangManager(o.getProperties());
	}
	else error("UserName required");
		
	// XMI file
	if (Arguments.modelFile.length() > 0) {
		xmiFile = new File(Arguments.modelFile);
		System.out.println(xmiFile.getPath());
		if (xmiFile.exists() && xmiFile.isFile()){
			System.out.println("Tot bé");
			//impXMI = new ImportXMI(file);
		}
		else error(l.getString("file.not.found")+ ": " + Arguments.modelFile);
	}
	else error(l.getString("model.parameter.not.found"));
	
	try {
		
		//Exporting the file
	/*	String exportName= Arguments.modelFile.substring(0, Arguments.modelFile.length()-4);
		exportName=exportName.concat("_export.xmi");
		File exportXMIFile = new File(exportName);
		exportXMIFile.delete();
		exportXMIFile.createNewFile();*/
			
		//XMIGenerator xmiGen= new XMIGenerator();
		//System.out.println(xmiGen.getTermination("EclipseUML2"));
		//xmiGen.generate(xmiFile, "Poseidon_v5", exportXMIFile, "ArgoUML_v024",  "Jordi");
	//	xmiGen.generate(xmiFile, "Visio_v2007", exportXMIFile, "ArgoUML_v024",  "Jordi");
		//xmiGen.generate(xmiFile, "Visio_v2007", exportXMIFile, "EclipseUML2",  "Jordi");
	//	xmiGen.generate(xmiFile, "EclipseUML2", exportXMIFile, "EclipseUML2",  "Jordi");
	//	p=xmiGen.getProject();
		
		
		//impXMI = ImportXMIFactory.createImportXMI("MagicDraw_v15");
	//	impXMI = ImportXMIFactory.createImportXMI("Visio_v2007");
	//	impXMI = ImportXMIFactory.createImportXMI("ArgoUML_v024");
	//	impXMI = ImportXMIFactory.createImportXMI("EclipseUML2");
		//impXMI = ImportXMIFactory.createImportXMI("Modelio_v1");
	/* 	p= impXMI.importFile(xmiFile);
		p.setUser("Jordi");
		System.out.println("Núm classes: " + p.getStaticModel().getNumberAllClasses());
		System.out.println("Núm associations: " + p.getStaticModel().getNumberAllAssociationsNoAssCl());
		System.out.println("Núm Atributs: " + p.getStaticModel().getNumberAllAttributes());
		System.out.println("Núm Generalizations: " + p.getStaticModel().getNumberAllGeneralizations());
		System.out.println("Núm association Classes: " + p.getStaticModel().getNumberAllAssociationClasses());
	*/	
	//	ExportXMI exp =ExportXMIFactory.createExportXMI("ArgoUML_v024");
	//	ExportXMI exp =ExportXMIFactory.createExportXMI("Modelio_v1");
	//	exp.exportFile(p, exportXMIFile);
		
	
		//Generation of the database script for the model
		
	//	DBGenerator dbGen= DBGeneratorFactory.createDBGenerator("mysql");
	//	DBGenerator dbGen= DBGeneratorFactory.createDBGenerator("oracle");
	//	DBGenerator dbGen= DBGeneratorFactory.createDBGenerator("firebird");
	//	DBGenerator dbGen= DBGeneratorFactory.createDBGenerator("interbase");
	//	DBGenerator dbGen= DBGeneratorFactory.createDBGenerator("postgresql");	
	//	DBGenerator dbGen= DBGeneratorFactory.createDBGenerator("db2");	
	//	DBGenerator dbGen= DBGeneratorFactory.createDBGenerator("sqlserver");	
	//	DBGenerator dbGen= DBGeneratorFactory.createDBGenerator("voltdb");
	/*	dbGen.initialize(p, o,l);
		String sqlFileName= Arguments.modelFile.substring(0, Arguments.modelFile.length()-4);
		sqlFileName=sqlFileName.concat(".sql");
		File dbFile = new File(sqlFileName);
		dbFile.delete();
		dbFile.createNewFile();
		dbGen.generate(p, dbFile, "Jordi");
*/
		//Tests PHP
	/*    PHPGenerator php= new PHPSymfonyGenerator();
		String ymlFileName= Arguments.modelFile.substring(0, Arguments.modelFile.length()-4);
		ymlFileName=ymlFileName.concat(".yml");
		File ymlFile= new File(ymlFileName);
		ymlFile.delete();
		ymlFile.createNewFile();
		php.generate(xmiFile, "ArgoUML_v024", ymlFile, "Jordi"); */
		
		
		//Tests Django
		   
		/*    PythonGenerator py= PythonGeneratorFactory.createPythonGenerator("django1.3");
			String pyFileName= Arguments.modelFile.substring(0, Arguments.modelFile.length()-4);
			pyFileName=pyFileName.concat(".zip");
			File pyFile= new File(pyFileName);
			pyFile.delete();
			pyFile.createNewFile();
			py.generate(xmiFile, "ArgoUML_v024", pyFile, "Jordi");
		*/
		
		// Tests UML From Pîctures
		
		PictureToUMLGenerator pic= new PictureToUMLGenerator();
		pic.generate(xmiFile, "Jordi"); //In fact in this case is not an XMIFile but an image file so the name of the var could be changed
		
			
	} catch (JDOMException e) {error (l.getString("unexpected.xmi.format") +  " " + e.getMessage());
	}
	catch (ImportXMIException e) {error (l.getString("error.xmi.import") +  " " + e.getMessage() );
	}
	catch (IOException e) {error (l.getString("file.not.found") +  " " + e.getMessage());
	}catch (Exception e) {error (l.getString("error.generation.application") + " " + e.getClass()+ "  " + e.getMessage());}
 	}
	
	private static final void error(String msg) {
		System.err.println(msg);
		System.exit(-1);
	}
	
	
	
}
