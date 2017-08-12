package uk.co.mafew.pi;

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
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import uk.co.mafew.file.Config;
import uk.co.mafew.file.xml.HistoryFragment;
import uk.co.mafew.hephaestus.GeneralConfig;
import uk.co.mafew.hephaestus.ProcessConfig;
import uk.co.mafew.logging.Logger;
import uk.co.mafew.messaging.GeneralMessageSender;
import uk.co.mafew.narcissus.ReflectedObject;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;

public class IOMonitor
{
	Logger logger;
	private String INITIAL_MESSAGE_DOCUMENT = "C:\\Users\\jbailey1\\workspace\\utilities\\src\\xml\\message.xml";
	private NodeList headerParamList = null;
	private String PROCESS_URL = "";

	public static void main(String[] args)
	{
	}
	
	public IOMonitor()
	{
		logger = new Logger(this.getClass().getName());
	}
	

	public void startPinMonitor(String pin, final Element targetClassElem, String port, int baudRate)
	{
		// create an instance of the serial communications class
		final Serial serial = SerialFactory.createInstance();
		// serial.open("/dev/ttyUSB0", 9600);
		serial.open(port, baudRate);

		// create and register the serial data listener
		serial.addListener(new SerialDataListener()
		{
			@Override
			public void dataReceived(SerialDataEvent event)
			{
				// print out the data received to the console
				String dataString = event.getData().replaceAll("\r", "").replaceAll("\n", "");
				//logger.log.debug("Serial data received: " + dataString);

				// if (dataString.compareTo("0") == 0)
				//if (dataString.contains("0"))
				//{
					logger.log.info("******************Button press detected******************");
					try
					{
						ReflectedObject ro = new ReflectedObject(targetClassElem, "");
						ro.invoke();
					}
					catch (Exception e)
					{
						logger.log.error(e.getMessage());
					}
				//}
			}
		});

	}

	private Pin getPin(String pin)
	{
		if (pin.compareTo("0") == 0)
		{
			return RaspiPin.GPIO_00;
		}
		else if (pin.compareTo("1") == 0)
		{
			return RaspiPin.GPIO_01;
		}
		else if (pin.compareTo("2") == 0)
		{
			return RaspiPin.GPIO_02;
		}
		else if (pin.compareTo("3") == 0)
		{
			return RaspiPin.GPIO_03;
		}
		else if (pin.compareTo("4") == 0)
		{
			return RaspiPin.GPIO_04;
		}
		else if (pin.compareTo("5") == 0)
		{
			return RaspiPin.GPIO_05;
		}
		else if (pin.compareTo("6") == 0)
		{
			return RaspiPin.GPIO_06;
		}
		else if (pin.compareTo("7") == 0)
		{
			return RaspiPin.GPIO_07;
		}
		else if (pin.compareTo("8") == 0)
		{
			return RaspiPin.GPIO_08;
		}
		else if (pin.compareTo("9") == 0)
		{
			return RaspiPin.GPIO_09;
		}
		else if (pin.compareTo("10") == 0)
		{
			return RaspiPin.GPIO_10;
		}
		else if (pin.compareTo("11") == 0)
		{
			return RaspiPin.GPIO_11;
		}
		else if (pin.compareTo("12") == 0)
		{
			return RaspiPin.GPIO_12;
		}
		else if (pin.compareTo("13") == 0)
		{
			return RaspiPin.GPIO_13;
		}
		else if (pin.compareTo("14") == 0)
		{
			return RaspiPin.GPIO_14;
		}
		else if (pin.compareTo("15") == 0)
		{
			return RaspiPin.GPIO_15;
		}
		else if (pin.compareTo("16") == 0)
		{
			return RaspiPin.GPIO_16;
		}
		else if (pin.compareTo("17") == 0)
		{
			return RaspiPin.GPIO_17;
		}
		else if (pin.compareTo("18") == 0)
		{
			return RaspiPin.GPIO_18;
		}
		else if (pin.compareTo("19") == 0)
		{
			return RaspiPin.GPIO_19;
		}
		else if (pin.compareTo("20") == 0)
		{
			return RaspiPin.GPIO_20;
		}
		else if (pin.compareTo("21") == 0)
		{
			return RaspiPin.GPIO_21;
		}
		else if (pin.compareTo("22") == 0)
		{
			return RaspiPin.GPIO_22;
		}
		else if (pin.compareTo("23") == 0)
		{
			return RaspiPin.GPIO_23;
		}
		else if (pin.compareTo("24") == 0)
		{
			return RaspiPin.GPIO_24;
		}
		else if (pin.compareTo("25") == 0)
		{
			return RaspiPin.GPIO_25;
		}
		else if (pin.compareTo("26") == 0)
		{
			return RaspiPin.GPIO_26;
		}
		else if (pin.compareTo("27") == 0)
		{
			return RaspiPin.GPIO_27;
		}
		else if (pin.compareTo("28") == 0)
		{
			return RaspiPin.GPIO_28;
		}
		else if (pin.compareTo("29") == 0)
		{
			return RaspiPin.GPIO_29;
		}
		return null;
	}

	class GpioPinListenerDigital1 implements GpioPinListenerDigital
	{
		private Element targetClass;
		private ReflectedObject ro;

		public GpioPinListenerDigital1(Element targetClass)
		{
			this.targetClass = targetClass;
			try
			{
				ro = new ReflectedObject(targetClass, "");
			}
			catch (NoSuchMethodException ne)
			{
				logger.log.error("THE METHOD WAS NOT FOUND");
			}
			catch (Exception e)
			{
				logger.log.error(e.getMessage());
				e.printStackTrace();
			}
		}

		public GpioPinListenerDigital1()
		{
			this.ro = ro;
		}

		public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
		{
			try
			{
				ro.invoke();
				// run("1001");
			}
			catch (Exception e)
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

				// Append the header elements to the message which will start
				// the
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
				logger.log.error("*****************************1******************************");
				XPathFactory factory = XPathFactory.newInstance();
				XPath xPath = factory.newXPath();
				logger.log.error("*****************************2******************************");
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

}
