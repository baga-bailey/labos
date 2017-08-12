package uk.co.mafew.hephaestus;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Level;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import uk.co.mafew.file.Config;
import uk.co.mafew.logging.Logger;

public class ProcessVariables
{
	String VARIABLES_URL = "";

	Logger logger;
	Document configDoc;
	HashMap<String, String> configMap;

	public static void main(String[] args)
	{
	}

	public ProcessVariables()
	{
		logger = new Logger(this.getClass().getName());
		loadConfig();
		initialise();
	}

	public String initialise()
	{
		String returnValue = "ERROR";
		try
		{
			configDoc = loadFile();
			configMap = parseConfig(configDoc);
			returnValue = "SUCCESS: Variables updated";
		}
		catch (Exception e)
		{
			returnValue = "ERROR: " + e.getMessage();
		}
		return returnValue;
	}

	public HashMap<String, String> getVariableHashMap()
	{
		return this.configMap;
	}

	public String getConfigValue(String configKey)
	{
		return (String) configMap.get(configKey);
	}

	public Document getConfigDoc()
	{
		return configDoc;
	}

	public Document loadFile()
	{
		Document doc = null;
		try
		{
			logger.log.info("***Loading processes from " + VARIABLES_URL);
			File fileProcesses = new File(VARIABLES_URL);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(fileProcesses);
		}
		catch (ParserConfigurationException e)
		{
			logger.log.error(e.getMessage());
		}
		catch (SAXException e)
		{
			logger.log.error(e.getMessage());
		}
		catch (IOException e)
		{
			logger.log.error(e.getMessage());
		}
		return doc;
	}

	/**
	 * @param doc
	 * @return
	 */
	private HashMap<String, String> parseConfig(Document doc)
	{

		HashMap<String, String> configMap = null;
		try
		{
			XPathFactory factory = XPathFactory.newInstance();
			XPath xPath = factory.newXPath();

			configMap = new HashMap<String, String>();
			NodeList processList = (NodeList) xPath.evaluate("/variables/variable", doc.getDocumentElement(),
					XPathConstants.NODESET);
			String key = "";
			String value = "";
			for (int i = 0; i < processList.getLength(); i++)
			{
				key = (String) xPath.evaluate("key/text()", processList.item(i), XPathConstants.STRING);
				value = (String) xPath.evaluate("value/text()", processList.item(i), XPathConstants.STRING);
				configMap.put(key, value);
			}

			// create a flag to indicate that we haven't had a clean run through
			// the variables to remove compound variables
			Boolean hasUpdated = true;
			// Create a counter just in case we go into a forever loop
			int safeguard = 0;

			while (hasUpdated && (safeguard < 1000))
			{
				// hasUpdated is set to false, and will only be set back to true
				// if a variable is updated
				hasUpdated = false;
				safeguard++;
				Iterator<Entry<String, String>> it = configMap.entrySet().iterator();
				/*
				 * Step through the entries in the variables HashMap. If an
				 * entry is found which contains another variable (a compound
				 * variable), we need to evaluate the variable which we've found
				 * and replace the variable name with the variable value. We
				 * keep looping through until no compound variables are left
				 */
				while (it.hasNext())
				{
					Map.Entry<String, String> pair = (Entry<String, String>) it.next();
					key = (String) pair.getKey();
					value = (String) pair.getValue();

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

							// Store the variable name from between the |s in
							// tempKey
							String tempKey = value.substring(firstOccurance + 1, secondOccurance);

							// Get the value from the variables HashMap using
							// the key we've just found
							String tempValue = (String) configMap.get(tempKey);

							if (tempValue != null)
							{
								try
								{
									// Replace all instances of the variable
									// name
									// we've
									// found with the value of that variable
									value = value.replace("|" + tempKey + "|", tempValue);

									// Update the variables HashMap with the
									// evaluated
									// value
									configMap.put(key, value);

									// Set the hasUpdated flag to true to show
									// that
									// a
									// compound variable has been successfully
									// updated
									hasUpdated = true;
								}
								catch (Exception e)
								{
									logger.log.error(e.getMessage());
								}
							}
						}
					}
				}
			}

			logger.log.debug("**************************Variable list*****************************");
			Iterator<Entry<String, String>> it = configMap.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry<String, String> pair = (Entry<String, String>) it.next();
				key = (String) pair.getKey();
				value = (String) pair.getValue();
				logger.log.debug(pair.getKey() + " = " + pair.getValue());
			}

		}
		catch (XPathExpressionException e)
		{
			logger.log.error(e.getMessage());
		}
		catch (DOMException e)
		{
			logger.log.error(e.getMessage());
		}

		return configMap;
	}

	private void loadConfig()
	{
		try
		{
			logger.log.debug("***Attempting to load variables URL config from file***");
			Config cfg = new Config();
			cfg.loadFile("config.xml");
			VARIABLES_URL = cfg.getConfigValue("variables");

			logger.log.info("***Message variables URL config loaded from file***");
		}
		catch (Exception e)
		{
			logger.log.error("****Error loading variables URL config from file***");
		}
	}

}
