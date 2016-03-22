package mdeServices.options;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 *  List of language strings for the application 
 * 
 * @version 0.1 Nov 2008
 * @author jcabot
 *
 */

public class LangManager {

	private Properties general;
	private Properties language;
	private String appPath;
	private String langFile;
	
	public LangManager(){}
	
	public LangManager(Properties _general) {
		general = _general; 
		appPath= System.getProperty("user.dir", ".");
		String selectedLanguage = general.getProperty("language");
		langFile= appPath + general.getProperty("language.path")+ File.separatorChar + selectedLanguage+ ".xml";
		System.out.println("Lang file" + langFile);
		language = load(langFile);
	}
	
	public Properties load(String langFile) {
		Properties language = new Properties();
		try {
			language.loadFromXML(
					new BufferedInputStream(
							new FileInputStream(langFile)));
		} catch (FileNotFoundException e) {
		   System.err.println("Language file doesn't exist");  //$NON-NLS-1$
		}catch(IOException e3) {
			e3.printStackTrace();
		}
		return language;
	}
		
	public void load(File langFile) {
		language = new Properties();
		try {
			language.loadFromXML(
					new BufferedInputStream(
							new FileInputStream(langFile)));
		} catch (FileNotFoundException e) {
		   System.err.println("Language file doesn't exist");  //$NON-NLS-1$
		}catch(IOException e3) {
			e3.printStackTrace();
		}
	}
	
	//Retrieving a language string
	public String getString(String key) {
		return language.getProperty(key); //$NON-NLS-1$
	}
	
/*	public void setLanguage(String lang) {
		language.setProperty("umltocsp.language", lang); //$NON-NLS-1$
	}*/

		
}
