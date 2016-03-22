package mdeServices.pythonGenerator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jdom.JDOMException;

import mdeServices.PHPGenerator;
import mdeServices.PythonGenerator;
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

public class PythonDjangoGeneratorv13 extends PythonGenerator{
	
	private PythonDjangoDomainGeneratorv13 pythonDomain;
	private PythonDjangoAdminGeneratorv13 pythonAdmin;
	PrintWriter domain=null;
	
	
	public PythonDjangoGeneratorv13()
	{
	  pythonDomain=new PythonDjangoDomainGeneratorv13();
	  pythonAdmin=new PythonDjangoAdminGeneratorv13();
	}
	
	protected void generateScripts(Project p, File output, String user) throws PythonGenerationException, PythonDomainGenerationException, TransformationNotApplicable, IOException, FileNotFoundException
	{
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
		
		o.setProperty("db.pk.subtype.name.add","true"); //In django it is mandatory that pk of a subtype and that of a supertype
		//differ when following the generateall strategy
		
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
	    		
		//get the directory of the output file
		File directory=output.getParentFile();
		
		//and create two additional files, one for the domain and one for the admin file
		
		File domain = new File(directory,"models.py");
		domain.delete();
		domain.createNewFile();
		
	    //Generation of the domain model
		PrintWriter domainpy= new PrintWriter(domain); 
		pythonDomain.generate(p, domainpy,o,l);
		domainpy.flush();
		domainpy.close();
		
		File admin = new File(directory,"admin.py");
		admin.delete();
		admin.createNewFile();
		 //Generation of the admin model
		PrintWriter adminpy= new PrintWriter(admin); 
		pythonAdmin.generate(p, adminpy,o,l);
		adminpy.flush();
		adminpy.close();
		
		//Now we create a zip file 
		final int BUFFER = 2048;
		FileOutputStream out = new FileOutputStream(output);
		ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(out));
		byte data[] = new byte[BUFFER];
		
		BufferedInputStream origin = null;

		//adding the domain file to the zip
		
		FileInputStream inputDomain = new FileInputStream(domain);
        origin = new BufferedInputStream(inputDomain, BUFFER);
        ZipEntry entry = new ZipEntry(domain.getName());
        
        zipOut.putNextEntry(entry);
        int count;
        while((count = origin.read(data, 0,BUFFER)) != -1) {
         zipOut.write(data, 0, count);
        }
        origin.close();
           
      //adding the admin file to the zip
		
		FileInputStream inputAdmin = new FileInputStream(admin);
        origin = new BufferedInputStream(inputAdmin, BUFFER);
        entry = new ZipEntry(admin.getName());
        zipOut.putNextEntry(entry);
        count=0;
        while((count = origin.read(data, 0,BUFFER)) != -1) {
         zipOut.write(data, 0, count);
        }
        origin.close();
        
        zipOut.flush();
        zipOut.close();
		
	}

}
