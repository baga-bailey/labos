package uk.co.mafew.logging;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import uk.co.mafew.file.Config;

public class Logger {
	
	public org.apache.log4j.Logger log;
	private String LOG_PROPERTIES_FILE = "";
	
	public Logger(String className) {
		try {
			log = org.apache.log4j.Logger.getLogger(className);
			loadConfig();
			initializeLogger();
		} catch (Exception e) {
			
			System.out.println("Errors when initialising logging");
			System.out.println(e.getLocalizedMessage());
		}

	}

	private void initializeLogger() {

		Properties logProperties = new Properties();
		try {
			//ClassLoader loader = this.getClass().getClassLoader();
			// FileInputStream fis = (FileInputStream)
			// loader.getResourceAsStream("./configuration/log4j.properties");
			logProperties.load(new FileInputStream(LOG_PROPERTIES_FILE));
		} catch (Exception e) {
			System.out.println("Unable to load logging properties file");
			System.out.println(e.getLocalizedMessage());
		}
		PropertyConfigurator.configure(logProperties);
		log.info("Logging initialised using " + LOG_PROPERTIES_FILE);
	}

	private void loadConfig() {
		try {
			Config cfg = new Config();
			cfg.loadFile("config.xml");
			LOG_PROPERTIES_FILE = cfg.getConfigValue("logPropertiesFile");
			//LOG_DATE_FORMAT = cfg.getConfigValue("logDateFormat");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
