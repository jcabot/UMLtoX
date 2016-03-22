package mdeServices;

import java.io.File;
import java.io.IOException;


import mdeServices.phpGenerator.PHPGenerationException;
import mdeServices.phpGenerator.PHPSymfonyGenerator;
 
public class PHPGeneratorFactory {

	public static PHPGenerator createPHPGenerator(String framework) throws PHPGenerationException	{
		if(framework.equals("doctrine1.2")) return new PHPSymfonyGenerator();
		else throw new PHPGenerationException("framework not supported");
		
	}
}
