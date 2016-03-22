package mdeServices;


public class AppGeneratorFactory {

	public static AppGenerator createAppGenerator(String technology) throws AppGenerationException	{
		if(technology.equals("PHP")) return new AppGeneratorPHP();
		else throw new AppGenerationException("Technology not supported");
		
	}
}
