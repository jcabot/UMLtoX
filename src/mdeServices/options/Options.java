package mdeServices.options;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *  List of options to configure the application 
 * 
 * @version 0.1 Nov 2008
 * @author jcabot
 *
 */

public class Options {

	private String appPath;
	private String configFileName; 
	private String defaultFileName; 
	private String userPath;
	private String appFile; //$NON-NLS-1$
	private String userName;
	//private static final String userFile = appPath + configFileName; //$NON-NLS-1$
	
	private Properties props;
	
	public Options(){}
	
	public Options(String _userName) {
		userName=_userName;
		appPath= System.getProperty("user.dir", ".");
		userPath = System.getProperty("user.home", "."); //$NON-NLS-1$
		defaultFileName= "MDEServicesOptions.xml";
		configFileName=userName+defaultFileName;
		System.out.println("username:" + userName);
		System.out.println("appPath:" + appPath);
		props = load();
	}
	
	
	public Properties load() {
		Properties config = new Properties();
		appFile= appPath + File.separatorChar + configFileName;
		try {
			config.loadFromXML(
					new BufferedInputStream(
							new FileInputStream(appFile)));
		} catch (FileNotFoundException e) {
			try{
				appFile=appPath + File.separatorChar + defaultFileName;
				System.out.println("appFile:" + appFile);
				config.loadFromXML(
						new BufferedInputStream(
								new FileInputStream(appFile)));
			}catch(FileNotFoundException e2) {
				System.err.println("Configuration file doesn't exist");  //$NON-NLS-1$
			}catch(IOException e3) {
				e3.printStackTrace();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		Properties properties = new Properties(
				System.getProperties());
		properties.putAll(config);
		return properties;
	}
	
	
		
	public void load(File propFile) {
		props = new Properties();
		try{
			props.loadFromXML(
					new BufferedInputStream(
							new FileInputStream(propFile)));
		}catch(FileNotFoundException e2) {
			System.err.println("Configuration file doesn't exist");  //$NON-NLS-1$
		}catch(IOException e3) {
			e3.printStackTrace();
		}
	}
	
	public void setUserName(String _userName)
	{
		userName=_userName;
	}
	
	//Returns all properties
	public Properties getProperties()
	{
	  return props;	
	}
	
	//Retrieving the language 
	public String getLanguage() {
		return props.getProperty("language"); //$NON-NLS-1$
	}
	
	//Retrieving a single property
	public String getProperty(String key) {
		return props.getProperty(key); //$NON-NLS-1$
	}
	
	//Retrieving a single property
	public void setProperty(String key, String value) {
		props.setProperty(key, value); //$NON-NLS-1$
	}
	
	public void setLanguage(String lang) {
		props.setProperty("umltocsp.language", lang); //$NON-NLS-1$
	}

	//The options for the user are saved 
	public void save() {
	try {
		props.storeToXML(
				new FileOutputStream(appPath + File.separatorChar + configFileName), "MDEServices options"); //$NON-NLS-1$
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	}
	
	/*private Properties getDefaults() {
	Properties defaults = new Properties();
	defaults.setProperty("eclipse.directory", ""); //$NON-NLS-1$
	defaults.setProperty("dot.directory", ""); //$NON-NLS-1$
	defaults.setProperty("umltocsp.language", LangManager.KEY_ENGLISH); //$NON-NLS-1$
	return defaults;
}*/


	
}
