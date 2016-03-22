package mdeServices.phpGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.jdom.JDOMException;

import mdeServices.PHPGenerator;
import mdeServices.metamodel.Project;
import mdeServices.transformations.T_AddDefaultNameAssoc;
import mdeServices.transformations.T_AddDefaultNameAssocEnds;
import mdeServices.transformations.T_AddDefaultTypeAttributes;
import mdeServices.transformations.T_AssocClassToClass;
import mdeServices.transformations.T_AssociationsMN_ToAssocClass;
import mdeServices.transformations.T_AssociationsNary_ToAssocClass;
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
import mdeServices.transformations.database.T_RemoveUserDefinedDataTypes;
import mdeServices.xmi.importXMI.ImportXMIException;

public class PHPSymfonyGenerator extends PHPGenerator{
	
	private PHPSymfonyDomainGenerator phpDomain;
	PrintWriter domain=null;
	
	
	public PHPSymfonyGenerator()
	{
	  phpDomain=new PHPSymfonyDomainGenerator();
		
	}
	
	protected void generateScripts(Project p, File output, String user) throws ImportXMIException, JDOMException,IOException,PHPGenerationException, PHPDomainGenerationException, TransformationNotApplicable, FileNotFoundException
	{
		//Generating the sql script
		this.p=p;
	    Transformation t;
	    t= new T_AddDefaultTypeAttributes(p,o);
	    t.exec();
	    t= new T_RemoveUserDefinedDataTypes(p,o);
		t.exec();
	    t= new T_AddDefaultNameAssocEnds(p,o);
		t.exec();
	    t= new T_AddDefaultNameAssoc(p,o);
		t.exec();
		
		//users can decide whether to create explicitly association classes for M:N associations
		// depending on the value of property app.assoc.reify 
		t= new T_AssociationsMN_ToAssocClass(p,o);
		t.exec();
		t= new T_AssociationsNary_ToAssocClass(p,o);
		t.exec();
		t= new T_MarkAsPersistent(p,o);
		t.exec();
		
		t=new T_PreciseDataTypeInformation(p,o);
		t.exec();
		
		t= new T_AddPKeys(p,o); //Generation of PKeys 
		t.exec();
		
		t= new T_NoGeneralizations(p,o);
		t.exec();
		
		t= new T_AssocClassToClass(p,o);
		t.exec();
		
		t= new T_MarkAsPersistent(p,o);
		t.exec();
		
		t= new T_AddPKeysReifiedClasses(p,o); //Generation of PKeys for the reified association classes
		t.exec();
		
		t= new T_AssociationsToForeignKeys(p,o);
		t.exec();
		
		t= new T_AddDefaultNamePKConstraints(p,o);
		t.exec();
		

		t= new T_AddDefaultNameFKConstraints(p,o);
		t.exec();
		

		t= new T_AddDefaultNameUniqueConstraints(p,o);
		t.exec();
	    
	    //Right now, only the domain model is generated
		PrintWriter domain= new PrintWriter(output); 
		phpDomain.generate(p, domain,o,l);
		domain.flush();
		domain.close();

	}

}
