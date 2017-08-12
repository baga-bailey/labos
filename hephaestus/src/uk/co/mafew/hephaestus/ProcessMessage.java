package uk.co.mafew.hephaestus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.naming.InitialContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import uk.co.mafew.messaging.GeneralMessageSender;
//import uk.co.mafew.messaging.*;
import uk.co.mafew.narcissus.ReflectedObject;
import uk.co.mafew.file.Config;
import uk.co.mafew.file.xml.HistoryFragment;
import uk.co.mafew.file.xml.XmlHelper;
import uk.co.mafew.format.Convert;
import uk.co.mafew.logging.*;

public class ProcessMessage
{
	Logger logger;

	long MESSAGE_QUEUE_PAUSE_TIME = 1000;
	String NEXT_STEP_DOCUMENT = "";
	String COMPLETED_PROCESS_DIRECTORY = "";
	String USER_ENCRYPTION_KEY = "";

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		ProcessMessage pm = new ProcessMessage();
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			// Document doc = db.parse(is);
			File f = new File(
					"C:\\Documents and Settings\\BaileyJ\\My Documents\\eclipse\\workspace\\NewScheduler\\message.xml");
			Document doc = db.parse(f);

			pm.process(doc);
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public ProcessMessage()
	{
		logger = new Logger(this.getClass().getName());
		loadConfig();
	}

	public void process(Document messageDoc)
	{
		try
		{
			logger.log.debug("*****************Incoming document*******************");
			logger.log.debug(Convert.docToString(messageDoc));

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();

			// Validate message
			// Load the Business Process Doc (BPD)
			XPathExpression expr = xpath.compile("//message/header/process/text()");
			// TODO Surely this needs to be an XPathConstants.STRING
			// I suspect I had problems getting the text so did it this way
			// Need to try again
			Node node = (Node) expr.evaluate(messageDoc, XPathConstants.NODE);
			// TODO Remove if not needed
			// NodeList nodes = (NodeList)expr.evaluate(messageDoc,
			// XPathConstants.NODESET);
			// String BusProcess = nodes.item(0).getNodeValue();
			String BusProcess = node.getNodeValue();

			// Use the the BPD to transform the message
			// ***
			Document transformedDoc = transformDoc(messageDoc, BusProcess, "");
			// ***
			logger.log.debug("*****************Transformed document*******************");
			logger.log.debug(Convert.docToString(transformedDoc));

			// Get the id of the process being executed
			expr = xpath.compile("//message/body/businessProcess/@id");
			String processId = (String) expr.evaluate(transformedDoc, XPathConstants.STRING);
			logger.log.debug("*****************Process ID=" + processId + "*******************");

			// Get the name of the process being executed
			expr = xpath.compile("//message/body/businessProcess/@name");
			String processName = (String) expr.evaluate(transformedDoc, XPathConstants.STRING);
			logger.log.debug("*****************Process Name=" + processName + "*******************");

			// Get the previously completed step
			xpath = factory.newXPath();
			expr = xpath.compile("//message/header/previousStep/text()");
			// TODO As above todo
			node = (Node) expr.evaluate(messageDoc, XPathConstants.NODE);
			// TODO Remove if not needed
			// nodes = (NodeList)expr.evaluate(messageDoc,
			// XPathConstants.NODESET);
			// String previousStep = nodes.item(0).getNodeValue();
			String previousStep = node.getNodeValue();

			logger.log.debug("******************Last completed Step is " + previousStep + "******************");

			// If this is the first step to run (ie the previous step = 0)
			// append the headers
			if (previousStep.compareTo("0") == 0)
			{
				// TODO Add the headers from the transformed doc to the message
				// doc
			}
			// Get the next step.
			// This is the step in the transformed doc with the lowest 'order'
			// value
			// that is greater than the previously completed step
			// TODO Revisit the way that the next step is determined
			// is a transformation the best way
			Document nextStepDoc = transformDoc(transformedDoc, NEXT_STEP_DOCUMENT, previousStep);
			
			logger.log.debug("******************Last completed Step is " + previousStep + "******************");
			logger.log.debug(Convert.docToString(nextStepDoc));

			ExecuteAsync executeAsync = new ExecuteAsync(messageDoc, nextStepDoc, processId, processName);

			xpath = factory.newXPath();
			expr = xpath.compile("//step/@mode");
			String mode = (String) expr.evaluate(nextStepDoc, XPathConstants.STRING);
			logger.log.debug("Mode = " + mode);

			// If Async
			if (mode.equals("asynchronous"))
			{
				logger.log.debug("Executing asynchronously");
				Thread t = new Thread(executeAsync);
				t.start();
			}
			// If sync
			else
			{
				logger.log.debug("Executing synchronously");
				executeAsync.process();
			}

		}
		catch (Exception e)
		{
			logger.log.error(e.getMessage());
		}

	}

	private Document transformDoc(Document inputDocument, String xslFilename, String var)
	{
		try
		{
			// Create Source doc
			DOMSource source = new DOMSource(inputDocument);
			// Create transformer factory
			javax.xml.transform.TransformerFactory factory = TransformerFactory.newInstance();

			// Use the factory to create a template containing the xsl file
			javax.xml.transform.Templates template = factory.newTemplates(new StreamSource(new FileInputStream(
					xslFilename)));
			// Use the template to create a transformer
			javax.xml.transform.Transformer xformer = template.newTransformer();
			// Add variable
			if (var != "")
			{
				xformer.setParameter("var", var);
			}
			// Create a new document to hold the results
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.newDocument();
			Result result = new DOMResult(doc);
			// Apply the xsl file to the source file and create the DOM tree
			xformer.transform(source, result);

			return doc;
		}
		// An error occurred while creating an empty DOM document
		catch (ParserConfigurationException e)
		{
			logger.log.error(e.getMessage());
		}
		catch (FileNotFoundException e)
		{
			logger.log.error(e.getMessage());
		}
		// An error occurred in the XSL file
		catch (TransformerConfigurationException e)
		{
			logger.log.error(e.getMessage());
			logger.log.error("The file " + xslFilename + " was not well formed");
		}
		// An error occurred while applying the XSL file
		catch (TransformerException e)
		{
			logger.log.error(e.getMessage());
			logger.log.error("There was an error during the transformation of the message.");
			logger.log.error("The input message must be well formed, as an error would have been");
			logger.log.error("earlier if it were not");
			logger.log.error("Similarly, a 'TransformerConfigurationException' would have been");
			logger.log.error("if the XSL document was not well formed");
			logger.log.error("It is likely that there is a syntax error in one or more xPath queries");
			logger.log.error("in the file " + xslFilename);
		}
		return null;

	}

	private void loadConfig()
	{
		try
		{
			logger.log.debug("***Attempting to load messageQueuePauseTime config from EJB***");
			InitialContext ctx = new InitialContext();
			GeneralConfig gc = (GeneralConfig) ctx.lookup("java:module/GeneralConfig");
			MESSAGE_QUEUE_PAUSE_TIME = Long.parseLong(gc.getConfigValue("messageQueuePauseTime"));

			logger.log.debug("***Attempting to load nextStepDocument config from EJB***");
			NEXT_STEP_DOCUMENT = gc.getConfigValue("nextStepDocument");

			logger.log.debug("***Attempting to load process output directory config from EJB***");
			COMPLETED_PROCESS_DIRECTORY = gc.getConfigValue("completedProcessDir");

			logger.log.debug("***Attempting to load process userEncryptionKey config from EJB***");
			USER_ENCRYPTION_KEY = gc.getConfigValue("userEncryptionKey");

			logger.log.info("***Message processing config loaded from EJB***");

		}
		catch (Exception e1)
		{
			logger.log.warn("****Error loading messageQueuePauseTime config from EJB***");

			try
			{
				logger.log.debug("***Attempting to load messageQueuePauseTime config from file***");
				Config cfg = new Config();
				cfg.loadFile("config.xml");
				MESSAGE_QUEUE_PAUSE_TIME = Long.parseLong(cfg.getConfigValue("messageQueuePauseTime"));

				logger.log.debug("***Attempting to load nextStepDocument config from File***");
				NEXT_STEP_DOCUMENT = cfg.getConfigValue("nextStepDocument");

				logger.log.debug("***Attempting to load process output directory config from EJB***");
				COMPLETED_PROCESS_DIRECTORY = cfg.getConfigValue("completedProcessDir");

				logger.log.info("***Message processing config loaded from file***");
			}
			catch (Exception e)
			{
				logger.log.error("****Error loading messageQueuePauseTime config from file***");
			}
		}
	}

	public class ExecuteAsync implements Runnable
	{
		private Document messageDoc;
		private Document nextStepDoc;
		private String processId;
		private String processName;

		public ExecuteAsync(Document messageDoc, Document nextStepDoc, String processId, String processName)
		{
			this.messageDoc = messageDoc;
			this.nextStepDoc = nextStepDoc;
			this.processId = processId;
			this.processName = processName;
		}

		@Override
		public void run()
		{
			try
			{
				process();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private void process() throws Exception
		{
			// Get the UUID of the file
			String uuid = messageDoc.getElementsByTagName("UUID").item(0).getTextContent();
			logger.log.debug("*****************UUID=" + uuid + "*******************");

			logger.log.debug("*****************Next Step document is***********************");
			logger.log.debug(Convert.docToString(nextStepDoc));

			// Set the date/time format to be used
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();

			SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
			String startTimeValue = timeFormat.format(cal.getTime());

			if (nextStepDoc.getElementsByTagName("step").getLength() > 0)
			{
				Element tempElem = (Element) nextStepDoc.getElementsByTagName("step").item(0);
				String stepToComplete = tempElem.getAttribute("order");
				logger.log.debug("*****************Next Step is " + stepToComplete + "***********************");
				logger.log.debug(Convert.elementToString(tempElem));

				Element targetClassElement = (Element) nextStepDoc.getElementsByTagName("TargetClass").item(0);

				// Get id
				String stepId = tempElem.getAttribute("id");

				// TODO Should this be changed to 'friendlyName
				// Get Friendly name
				String friendlyName = tempElem.getAttribute("name");

				// Get start date
				String startDateValue = dateFormat.format(cal.getTime());

				// Execute the next step
				ReflectedObject ro = new ReflectedObject(targetClassElement, USER_ENCRYPTION_KEY);
				Document resultDoc = (Document) (ro.invoke());
				// ***
				logger.log.debug("****************Result of execution******************");
				logger.log.debug(Convert.docToString(resultDoc));

				// Append the completed step and result to the message
				Node resultNode = resultDoc.getElementsByTagName("returnValue").item(0);
				resultNode = nextStepDoc.importNode(resultNode, true);
				Node stepNode = nextStepDoc.getElementsByTagName("step").item(0);

				stepNode.appendChild(resultNode);
				stepNode = messageDoc.importNode(stepNode, true);
				messageDoc.getElementsByTagName("body").item(0).appendChild(stepNode);
				messageDoc.getElementsByTagName("previousStep").item(0).setTextContent(stepToComplete);
				// ***
				logger.log.debug("*****************************************************");
				logger.log.debug("**********************New message********************");
				logger.log.debug(Convert.docToString(messageDoc));
				logger.log.debug("Pausing before writing new message to queue");
				Thread.sleep(MESSAGE_QUEUE_PAUSE_TIME);
				// Write the message to the queue
				GeneralMessageSender gms = new GeneralMessageSender("/queue/ProcessQueue");
				gms.sendMessage(messageDoc);

				// Get end date and time
				String endDateValue = dateFormat.format(cal.getTime());
				String endTimeValue = timeFormat.format(cal.getTime());

				// Create the xml to update the history document
				HistoryFragment hf = new HistoryFragment();
				hf.createStepFragment();
				hf.setUuid(uuid);
				hf.setStartDate(startDateValue);
				hf.setStartTime(startTimeValue);
				hf.setId(stepId);
				hf.setFriendlyName(friendlyName);
				hf.setEndDate(endDateValue);
				hf.setEndTime(endTimeValue);

				// Meta elements
				hf.setStatus("running");
				hf.setProcessId(processId);
				hf.setProcessName(processName);
				
				////////////////////////////////////////
				logger.log.debug("***********UPDATING History Fragment***********");
				XPathFactory factory = XPathFactory.newInstance();
				XPath xPath = factory.newXPath();
				NodeList nodelist = (NodeList) xPath.compile("//step/*").evaluate(nextStepDoc, XPathConstants.NODESET);
				for(int i = 0;i<nodelist.getLength();i++)
				{
					try {
						Node newNode = hf.getDoc().importNode((Node) nodelist.item(i), true);
						hf.getDoc().getDocumentElement().appendChild(newNode);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
				////////////////////////////////////////

				String hfDoc = Convert.docToString(hf.getDoc());
				logger.log.debug("*******************History Fragment**********************");
				logger.log.debug(hfDoc);

				GeneralMessageSender historySender = new GeneralMessageSender("/queue/HistoryQueue");
				historySender.sendMessage(hf.getDoc());
			}
			else
			{
				// write the final message to file

				XmlHelper helper = new XmlHelper();
				helper.saveDocToFile(messageDoc, COMPLETED_PROCESS_DIRECTORY, uuid + ".xml");

				// Get end date and time
				String endDateValue = dateFormat.format(cal.getTime());
				String endTimeValue = timeFormat.format(cal.getTime());

				// Create the xml to update the history document
				HistoryFragment hf = new HistoryFragment();
				hf.createProcessFragment();
				hf.setUuid(uuid);
				hf.setEndDate(endDateValue);
				hf.setEndTime(endTimeValue);
				hf.setStatus("completed");

				GeneralMessageSender historySender = new GeneralMessageSender("/queue/HistoryQueue");
				historySender.sendMessage(hf.getDoc());

				logger.log.info("******************************************************");
				logger.log.info("*******************Process finished*******************");
				logger.log.debug("*******************Final Document*********************");
				logger.log.debug(Convert.docToString(messageDoc));
			}
		}

	}

}
