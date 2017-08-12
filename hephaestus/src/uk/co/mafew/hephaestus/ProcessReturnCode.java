package uk.co.mafew.hephaestus;

import java.io.File;

import javax.naming.InitialContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.co.mafew.file.Config;
import uk.co.mafew.logging.Logger;
import uk.co.mafew.narcissus.ReflectedObject;

public class ProcessReturnCode
{
	Logger logger;

	private String RETURN_CODE_DOCUMENT = "";

	public static void main(String[] args)
	{
		ProcessReturnCode prCode = new ProcessReturnCode();
		prCode.RETURN_CODE_DOCUMENT = "C:\\Users\\jbailey1\\Documents\\uranus\\processes\\config\\returnCodes.xml";
		prCode.run("1001", "101");
	}

	public ProcessReturnCode()
	{
		try
		{
			logger = new Logger(this.getClass().getName());
			loadConfig();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String run(String processId, String returnCode)
	{
		String retString = "";
		String status = "";

		// Load the returnCodes.xml file
		if (RETURN_CODE_DOCUMENT != "")
		{
			try
			{
				XPathFactory factory = XPathFactory.newInstance();
				XPath xPath = factory.newXPath();

				logger.log.info("***Loading return code config for " + processId + " from " + RETURN_CODE_DOCUMENT);
				File returnCodeFile = new File(RETURN_CODE_DOCUMENT);
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(returnCodeFile);

				// Get the fragment of xml for this processId and this return
				// code
				String processIdXpath = String.format(
						"/processes/process[@id='%s']/returnCodes/returnCode/value[text()='%s']/parent::*", processId,
						returnCode);
				NodeList returnCodeList = (NodeList) xPath.evaluate(processIdXpath, doc, XPathConstants.NODESET);
				// Step through the returnCodes calling StartProcess each time
				// //returnCode/value matches the returnCode parameter, and
				// updating the 'status' string
				if (returnCodeList != null)
				{
					for (int i = 0; i < returnCodeList.getLength(); i++)
					{
						Node targetClass = (Node) xPath.evaluate("TargetClass", returnCodeList.item(i),
								XPathConstants.NODE);
						if (targetClass != null)
						{
							retString = callReflectedObject((Element) targetClass);
						}
						String tempStatus = (String) xPath.evaluate("status/text()", returnCodeList.item(i),
								XPathConstants.STRING);
						if (tempStatus != null)
						{
							status = tempStatus;
						}
					}
				}

				// Step through the statuses calling StartProcess each time
				// status/value matches the 'status' string
				processIdXpath = String.format(
						"/processes/process[@id='%s']/statuses/status/value[text()='%s']/parent::*", processId, status);
				returnCodeList = (NodeList) xPath.evaluate(processIdXpath, doc, XPathConstants.NODESET);
				if (returnCodeList != null)
				{
					for (int i = 0; i < returnCodeList.getLength(); i++)
					{
						Node targetClass = (Node) xPath.evaluate("TargetClass", returnCodeList.item(i),
								XPathConstants.NODE);
						retString = callReflectedObject((Element) targetClass);
					}
				}
				// Update the database/file with the status
			}
			catch (Exception e)
			{
				retString = e.getMessage();
			}
		}
		return retString;
	}

	private String callReflectedObject(Element targetClass)
	{
		String retValueString = "ERROR";
		try
		{
			ReflectedObject ro = new ReflectedObject(targetClass, "");
			ro.invoke();
			retValueString = "SUCCESS";
		}
		catch (NoSuchMethodException ne)
		{
			retValueString = "THE METHOD WAS NOT FOUND";
			logger.log.error("THE METHOD WAS NOT FOUND");
		}
		catch (Exception e)
		{
			retValueString = e.getMessage();
		}
		return retValueString;
	}

	private void loadConfig()
	{
		try
		{
			logger.log.debug("***Attempting to load returnCodeDirectory config from EJB***");
			InitialContext ctx = new InitialContext();
			GeneralConfig gc = (GeneralConfig) ctx.lookup("java:module/GeneralConfig");

			logger.log.debug("***Attempting to load nextStepDocument config from EJB***");
			RETURN_CODE_DOCUMENT = gc.getConfigValue("returnCodeConfigFile");

			logger.log.info("***Return code config file loaded from EJB***");

		}
		catch (Exception e1)
		{
			logger.log.warn("****Error loading returnCodeDirectory config from EJB***");

			try
			{
				logger.log.debug("***Attempting to load messageQueuePauseTime config from file***");
				Config cfg = new Config();
				cfg.loadFile("config.xml");

				logger.log.debug("***Attempting to load nextStepDocument config from File***");
				RETURN_CODE_DOCUMENT = cfg.getConfigValue("returnCodeConfigFile");

				logger.log.info("***Return code config file loaded from file***");
			}
			catch (Exception e)
			{
				logger.log.error("****Error loading returnCodeDirectory config from file***");
			}
		}
	}

}
