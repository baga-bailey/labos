package uk.co.mafew.hephaestus;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import javax.naming.InitialContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import uk.co.mafew.file.Config;
import uk.co.mafew.file.xml.HistoryFragment;
import uk.co.mafew.format.Convert;
import uk.co.mafew.logging.Logger;
import uk.co.mafew.messaging.GeneralMessageSender;

public class StartProcess
{
	// private String INITIAL_MESSAGE_DOCUMENT =
	// "C:/Uranus/processes/config/message.xml";
	private String INITIAL_MESSAGE_DOCUMENT = "C:\\Users\\jbailey1\\workspace\\utilities\\src\\xml\\message.xml";
	private NodeList headerParamList = null;
	private String PROCESS_URL = "";
	private Logger logger;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		StartProcess sp = new StartProcess();
		sp.run("C:\\Users\\jbailey1\\Documents\\uranus\\processes\\monitoring.xsl");
	}

	public StartProcess()
	{
		try
		{
			logger = new Logger(this.getClass().getName());
			// loadConfig();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public StartProcess(NodeList headerParamList)
	{
		logger = new Logger(this.getClass().getName());
		logger.log.debug("header node count = " + headerParamList.getLength());
		logger.log.debug("header node 1 = " + headerParamList.item(0).getLocalName());
		// loadConfig();

		this.headerParamList = headerParamList;
	}

	public StartProcess(Node headerElement)
	{
		logger = new Logger(this.getClass().getName());
		logger.log.debug("*****************************************************");
		logger.log.debug("*************Header Element**************************");
		logger.log.debug(Convert.elementToString((Element) headerElement));
		// loadConfig();
		XPathFactory factory = XPathFactory.newInstance();
		XPath xPath = factory.newXPath();
		try
		{
			this.headerParamList = (NodeList) xPath.evaluate("//header/*[normalize-space() !='']", headerElement,
					XPathConstants.NODESET);
		}
		catch (XPathExpressionException e)
		{
			logger.log.error(e.getMessage());
		}
	}

	public boolean run(String urlId)
	{
		try
		{
			loadConfig(urlId);
			logger.log.info("************************************************************");
			logger.log.info("Start process " + urlId);
			Document initialMessagedoc = loadInitialMessage();

			// Append the header elements to the message which will start the
			// process
			if (headerParamList != null)
			{
				for (int i = 0; i < headerParamList.getLength(); i++)
				{
					Node headerNode = headerParamList.item(i);
					Node importNode = initialMessagedoc.importNode(headerNode, true);
					initialMessagedoc.getElementsByTagName("header").item(0).appendChild(importNode);
				}
			}

			// Append the processID to the message header
			Node procId = initialMessagedoc.createElement("processID");
			initialMessagedoc.getElementsByTagName("header").item(0).appendChild(procId);
			Node procIdValueNode = initialMessagedoc.createTextNode(urlId);
			procId.appendChild(procIdValueNode);

			/********************************************************************************************/
			/****************************** UUID **********************************************************/
			// Check to see if the message has a UUID
			
			XPathFactory factory = XPathFactory.newInstance();
			XPath xPath = factory.newXPath();
			
			Node uuidNode = (Node) xPath.evaluate("//header/UUID", initialMessagedoc, XPathConstants.NODE);

			// If UUID doesn't exist create it
			if (uuidNode == null)
			{
				uuidNode = initialMessagedoc.createElement("UUID");
				initialMessagedoc.getElementsByTagName("header").item(0).appendChild(uuidNode);
			}

			// If UUID is empty insert a value
			String uuidValue = uuidNode.getNodeValue();
			if (uuidValue == "" || uuidValue == null)
			{
				uuidValue = UUID.randomUUID().toString();
				Node uuidValueNode = initialMessagedoc.createTextNode(uuidValue);
				uuidNode.appendChild(uuidValueNode);
			}
			/*********************************************************************************************/
			/*********************************************************************************************/

			HistoryFragment hf = new HistoryFragment();
			hf.createProcessFragment();

			// Get start date
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			String startDateValue = dateFormat.format(cal.getTime());

			// Start Time element
			SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
			String startTimeValue = timeFormat.format(cal.getTime());
			hf.setStartDate(startDateValue);
			hf.setStartTime(startTimeValue);
			hf.setUuid(uuidValue);
			hf.setStatus("started");

			GeneralMessageSender historySender = new GeneralMessageSender("/queue/HistoryQueue");
			historySender.sendMessage(hf.getDoc());

			addProcessToMessage(initialMessagedoc, PROCESS_URL);
			writeMessageToQueue(initialMessagedoc);
		}
		catch (Exception e)
		{
			logger.log.error(e.getMessage());
			return false;
		}
		return true;
	}

	private Document loadInitialMessage()
	{
		Document doc = null;

		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			File f = new File(INITIAL_MESSAGE_DOCUMENT);
			doc = db.parse(f);
			System.out.println("Initial message loaded");

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

	private void addProcessToMessage(Document initialMessagedoc, String process)
	{
		initialMessagedoc.getElementsByTagName("process").item(0).setTextContent(process);
		logger.log.debug("Initial message doc updated: process = " + process);
	}

	private void writeMessageToQueue(Document doc)
	{
		GeneralMessageSender gms = new GeneralMessageSender("/queue/ProcessQueue");
		gms.sendMessage(doc);
		logger.log.info("Initial message doc written to queue");
	}

	private void loadConfig(String urlId)
	{
		try
		{
			// TestClass tc = new TestClass();
			logger.log.debug("***Attempting to load initialMessageDocument config from EJB***");
			InitialContext ctx = new InitialContext();
			GeneralConfig gc = (GeneralConfig) ctx.lookup("java:global/uranus-ear/hephaestus/GeneralConfig");
			INITIAL_MESSAGE_DOCUMENT = gc.getConfigValue("initialMessageDocument");
			PROCESS_URL = gc.getProcessValue(urlId);

			logger.log.info("Process document loaded from EJB url=" + INITIAL_MESSAGE_DOCUMENT);
			logger.log.info("Process document loaded from EJB url=" + PROCESS_URL);
			logger.log.info("***initialMessageDocument loaded from EJB***");

		}
		catch (Exception e1)
		{
			logger.log.warn("****Error loading initialMessageDocument config from EJB***");

			try
			{
				logger.log.debug("***Attempting to load messageQueuePauseTime config from file***");
				Config cfg = new Config();
				cfg.loadFile("config.xml");
				INITIAL_MESSAGE_DOCUMENT = cfg.getConfigValue("initialMessageDocument");

				ProcessConfig pc = new ProcessConfig();
				PROCESS_URL = pc.getConfigValue(urlId);
				logger.log.info("***Message processing config loaded from file***");
			}
			catch (Exception e)
			{
				logger.log.error("****Error loading initialMessageDocument config from file***");
			}
		}
	}

}
