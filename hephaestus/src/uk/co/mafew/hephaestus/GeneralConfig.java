package uk.co.mafew.hephaestus;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.w3c.dom.Document;

import uk.co.mafew.file.*;
import uk.co.mafew.logging.Logger;

import static javax.ejb.LockType.READ;

/**
 * Session Bean implementation class GeneralConfig
 */
@Startup
@Singleton
@LocalBean
@Lock(READ)
public class GeneralConfig
{
	Config cfg = null;
	ProcessConfig pc = null;
	ProcessVariables pv = null;
	Document configDoc = null;
	String timeTableURL = "";
	Logger logger;

	public static void main(String[] args)
	{
		GeneralConfig gc = new GeneralConfig();
		gc.loadGeneralConfig();
	}

	/**
	 * Default constructor.
	 */
	public GeneralConfig()
	{
		logger = new Logger(this.getClass().getName());
	}

	// private final Map<Class, Object> components = new HashMap<Class,
	// Object>();
	//
	// public <T> T getComponent(final Class<T> type)
	// {
	// return (T) components.get(type);
	// }
	//
	// public Collection<?> getComponents()
	// {
	// return new ArrayList(components.values());
	// }
	//
	// @Lock(WRITE)
	// public <T> T setComponent(final Class<T> type, final T value)
	// {
	// return (T) components.put(type, value);
	// }
	//
	// @Lock(WRITE)
	// public <T> T removeComponent(final Class<T> type)
	// {
	// return (T) components.remove(type);
	// }

	@PostConstruct
	public void loadGeneralConfig()
	{
		loadConfig();
	}

	public String loadConfig()
	{
		String retString = "ERROR";
		System.out.println("***Loading config from file***");
		try
		{
			cfg = new Config();
			// configDoc = cfg.loadFile("config.xml");

			pc = new ProcessConfig();
			pv = new ProcessVariables();
			retString = "SUCCESS: Config loaded successfully";
		}
		catch (Exception e)
		{
			retString = "ERROR: Error loading congig - " + e.getMessage();
			System.out.println(retString);
		}
		return retString;
	}

	public Document getConfigDoc()
	{
		return configDoc;
	}

	public HashMap<String, String> getVariableHashMap()
	{
		return pv.getVariableHashMap();
	}

	public String getConfigValue(String configKey)
	{
		return cfg.getConfigValue(configKey);
	}

	public String getProcessValue(String processKey)
	{
		return pc.getConfigValue(processKey);
	}

	public String getVariableValue(String variableKey)
	{
		return pv.getConfigValue(variableKey);
	}

	public String evaluateVariables(String value) throws Exception
	{
		// create a flag to indicate that we haven't had a clean run through
		// the variables to remove compound variables
		Boolean hasUpdated = true;
		// Create a counter just in case we go into a forever loop
		int safeguard = 0;

		while (hasUpdated && (safeguard < 100))
		{
			// hasUpdated is set to false, and will only be set back to true
			// if a variable is updated
			hasUpdated = false;
			safeguard++;

			// First check to see if there's an occurrence of '|'
			// If there is one this could indicate a compound variable
			int firstOccurance = value.indexOf("|");
			int secondOccurance = -1;

			// If we found a '|', we then then check for another
			// occurrence
			// if we find a second '|', we treat it as a compound
			// variable
			if (firstOccurance > -1)
			{
				secondOccurance = value.indexOf("|", firstOccurance + 1);
				if (secondOccurance > -1)
				{
					// We've found two '|' signs, so we assume the
					// string in between the signs is a variable name
					// Set the hasUpdated flag to true to show that a
					// compound variable has been found
					hasUpdated = true;

					// Store the variable name from between the |s in
					// tempKey
					String tempKey = value.substring(firstOccurance + 1, secondOccurance);

					// Get the value from General config using the key we've
					// just found
					String tempValue = getVariableValue(tempKey);

					if (tempValue != null)
					{
						logger.log.debug("Variable " + tempKey + " has evalusted to " + tempValue);
						// Replace all instances of the variable name we've
						// found with the value of that variable
						value = value.replace("|" + tempKey + "|", tempValue);
					}
					else {
						logger.log.debug(tempKey + " could not be found");
					}

				}
			}
		}

		return value;
	}

}
