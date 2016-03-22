package mdeServices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import mdeServices.dbGenerator.DBGenerationException;
import mdeServices.metamodel.Project;
import mdeServices.metamodel.StaticModel;
import mdeServices.metamodel.gui.DynamicModel;
import mdeServices.options.LangManager;
import mdeServices.options.Options;
import mdeServices.transformations.T_AddDefaultNameAssocEnds;
import mdeServices.transformations.Transformation;
import mdeServices.transformations.TransformationNotApplicable;
import mdeServices.transformations.gui.T_DefaultOrderAndShowAttributes;
import mdeServices.transformations.gui.T_GenerateAppSkeleton;
import mdeServices.transformations.gui.T_GenerateFormInternals;
import mdeServices.transformations.gui.T_GenerateInitialForm;
import mdeServices.transformations.gui.T_GenerateLoginForm;

/**
 *  Class for grouping the MDEServices provided by the Model to Delphi application
 * 
 * @version 0.1 24 Aug 2008
 * @author jcabot
 *
 */

public abstract class AppGenerator extends MDEServices{

	DBGenerator dbGen;
	String baseDir;
	String baseDirApp;
	DynamicModel d;
	
	int nestedLevel=0; //For printing issues	

	
	public AppGenerator() {
		super();
	}
	
	/**
	 * @param m
	 * @throws DBGenerationException 
	 */
	public void  initialize(Project p, Options o, LangManager l, String dbName) throws DBGenerationException {
		this.p= p;
		this.o= o;
		this.l=l;
		dbGen= DBGeneratorFactory.createDBGenerator(dbName);
		dbGen.initialize(p, o,l);
	}
	
	public void generate() throws IOException, DBGenerationException, TransformationNotApplicable
	{
		String name= o.getProperty("app.name.prefix") + p.getName()+ o.getProperty("app.name.suffix");
		d=new DynamicModel(name);
		p.setDynamicModel(d);
		p.getStaticModel().setDynamicModel(d); //Link between both kinds of models
		
		//Base directory for the application
		baseDir=p.getUser()+File.separator+p.getName();
		
		//Base directory for the database script
		String baseDirDB=baseDir + File.separator + o.getProperty("db.script.file.directory");
		File fDBDir = new File(baseDirDB);
        if (!fDBDir.exists()) fDBDir.mkdirs();
	
        //Normalization of the model + creation of the database script
		String dbScriptName= o.getProperty("db.script.file.name");
        File fDBScript = new File(baseDirDB+File.separator+dbScriptName);
        fDBScript.createNewFile();
	//	dbGen.generate(fDBScript);
		
		//Creation of the gui
		baseDirApp=baseDir + File.separator + o.getProperty("app.script.file.directory");
		File fAppDir = new File(baseDirApp);
        if (!fAppDir.exists()) fAppDir.mkdirs();
		Transformation t;
		t= new T_GenerateAppSkeleton(p,o,l); t.exec();
		if (o.getProperty("app.generate.login.form").equals("true"))
		{
			t= new T_GenerateLoginForm(p,o,l); t.exec();
		}
		t= new T_GenerateInitialForm(p,o,l); t.exec();
		t= new T_GenerateLoginForm(p,o,l); t.exec();
		t= new T_GenerateFormInternals(p,o,l, dbGen.isAutoIncrementPossible()); t.exec();
		t= new T_DefaultOrderAndShowAttributes(p,o,l);t.exec();
		//Possible transformations technology-specific
		additionalTransformations();
		generateScripts();
	}


protected abstract void additionalTransformations() throws TransformationNotApplicable ;

protected abstract void generateScripts() throws FileNotFoundException ;

protected void incNested(){nestedLevel++;}

protected void decNested(){nestedLevel--;}

protected String nested(){
	String tab= "";
	for(int i=0;i<=nestedLevel;i++){tab=tab.concat("  ");}
	return tab;
}

}
