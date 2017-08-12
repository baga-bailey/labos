package uk.co.mafew.pi;

import java.io.File;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.InitialContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import uk.co.mafew.file.Config;
import uk.co.mafew.hephaestus.GeneralConfig;
import uk.co.mafew.logging.Logger;

@Startup
@Singleton
public class IOManager
{
	Logger logger;
	private String IO_MONITOR_URL = ""; 
	private String PORT = "/dev/ttyUSB0";
	private String BAUD_RATE = "9600";

	public IOManager()
	{
		logger = new Logger(this.getClass().getName());
		loadConfig();
	}

	@PostConstruct
	public void startIOMonitor()
	{
		if (IO_MONITOR_URL != null && IO_MONITOR_URL.compareTo("") != 0)
		{
			try
			{
				XPathFactory factory = XPathFactory.newInstance();
				XPath xPath = factory.newXPath();

				logger.log.info("***Loading IO Monitors from " + IO_MONITOR_URL);
				logger.log.debug("IO_MONITOR_URL = " + IO_MONITOR_URL);
				File iomonitors = new File(IO_MONITOR_URL);
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(iomonitors);
				

				// Get the individual IO Monitors
				NodeList ioList = (NodeList) xPath.evaluate("/pi/gpio/pin", doc.getDocumentElement(), XPathConstants.NODESET);

				// Iterate the IOMonitors
				for (int i = 0; i < ioList.getLength(); i++)
				{
					Element elem = (Element) ioList.item(i);

					String watchedPin = (String) xPath.evaluate("number", elem, XPathConstants.STRING);
					logger.log.debug("pin = " + watchedPin);
					NodeList targetClassList = (NodeList) xPath.evaluate("TargetClass", elem, XPathConstants.NODESET);
					Element targetClassElem = (Element) targetClassList.item(0);
					
					IOMonitor ioMonitor = new IOMonitor();
					ioMonitor.startPinMonitor(watchedPin, targetClassElem, PORT, Integer.parseInt(BAUD_RATE));
					//xPath.reset();
				}
			}

			catch (Exception e)
			{
				logger.log.error(e.getMessage());
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				logger.log.error("Unable to load config for IOMonitor");
				logger.log.error("IOMonitor cannot be started");
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	

	private void loadConfig()
	{
		try
		{
			logger.log.debug("***Attempting to load IO config from EJB***");
			InitialContext ctx = new InitialContext();
			GeneralConfig gc = (GeneralConfig) ctx.lookup("java:module/GeneralConfig");
			IO_MONITOR_URL = gc.getConfigValue("pi");
			BAUD_RATE = gc.getConfigValue("baudRate");
			PORT = gc.getConfigValue("arduinoPort");

			logger.log.info("***IO Config loaded from EJB***");
		}
		catch (Exception e1)
		{
			logger.log.warn("****Error loading IO config from EJB***");
			logger.log.error(e1.getMessage());

			try
			{
				logger.log.debug("***Attempting to load IO config from file***");
				Config cfg = new Config();
				cfg.loadFile("config.xml");
				IO_MONITOR_URL = cfg.getConfigValue("pi");
				BAUD_RATE = cfg.getConfigValue("baudRate");
				PORT = cfg.getConfigValue("arduinoPort");
				logger.log.info("***IO Config loaded from file***");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				logger.log.error("****Error loading IO config from file***");
				logger.log.error(e.getMessage());
			}
		}
		
		logger.log.debug("IO_MONITOR_URL = " + IO_MONITOR_URL);
		logger.log.debug("BAUD_RATE = " + BAUD_RATE);
		logger.log.debug("PORT = " + PORT);
	}

}
