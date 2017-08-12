package uk.co.mafew.hephaestus;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import uk.co.mafew.file.Config;
import uk.co.mafew.logging.Logger;

public class ProcessConfig
{
	String PROCESS_URL = "";

	Logger logger;
	Document configDoc;
	HashMap<String, String> configMap;

	public static void main(String[] args)
	{
	}

	public ProcessConfig()
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
			returnValue = "SUCCESS: Config updated";
		}
		catch (Exception e)
		{
			returnValue = "ERROR: " + e.getMessage();
		}
		return returnValue;
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
		    logger.log.info("***Loading processes from " + PROCESS_URL);
			File fileProcesses = new File(PROCESS_URL);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(fileProcesses);
		}
		catch (Exception e)
		{
			logger.log.error(e.getMessage());
		}
		
		return doc;
	}

	private HashMap<String, String> parseConfig(Document doc)
	{

		HashMap<String, String> configMap = null;
		try
		{
			XPathFactory factory = XPathFactory.newInstance();
			XPath xPath = factory.newXPath();
			
			configMap = new HashMap<String, String>();
			NodeList processList = (NodeList)xPath.evaluate("/processes/process", doc.getDocumentElement(), XPathConstants.NODESET);
			for (int i = 0; i < processList.getLength(); i++)
			{
				String key = (String)xPath.evaluate("key/text()", processList.item(i),XPathConstants.STRING);
				String value = (String)xPath.evaluate("value/text()", processList.item(i),XPathConstants.STRING);
				configMap.put(key, value);
			}
		}
		catch (Exception e)
		{
			logger.log.error(e.getMessage());
		}
		

		return configMap;
	}

	private void loadConfig()
	{
		try
		{
			logger.log.debug("***Attempting to load processes URL config from file***");
			Config cfg = new Config();
			cfg.loadFile("config.xml");
			PROCESS_URL = cfg.getConfigValue("processes");

			logger.log.info("***Message processes URL config loaded from file***");
		}
		catch (Exception e)
		{
			logger.log.error("****Error loading processes URL config from file***");
		}
	}

}
