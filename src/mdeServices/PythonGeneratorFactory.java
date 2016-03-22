package mdeServices;

import java.io.File;
import java.io.IOException;


import mdeServices.phpGenerator.PHPGenerationException;
import mdeServices.phpGenerator.PHPSymfonyGenerator;
import mdeServices.pythonGenerator.PythonDjangoGeneratorv13;
import mdeServices.pythonGenerator.PythonGenerationException;
 
public class PythonGeneratorFactory {

	public static PythonGenerator createPythonGenerator(String framework) throws PythonGenerationException	{
		if(framework.equals("django1.3")) return new PythonDjangoGeneratorv13();
		else throw new PythonGenerationException("framework not supported");
		
	}
}
