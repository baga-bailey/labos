package uk.co.mafew.hephaestus.serial;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import uk.co.mafew.file.Config;
import uk.co.mafew.format.Convert;
import uk.co.mafew.hephaestus.GeneralConfig;
import uk.co.mafew.logging.Logger;

/**
 * Session Bean implementation class DirectoryWatcherManager
 */
@Startup
@Singleton
public class SimpleSocketManager
{
	Logger logger;
	private String SOCKET_WATCHER_URL = "C:\\Users\\jbailey1\\Documents\\uranus\\socketwatcher\\SocketWatcher.xml";

	public static void main(String[] args)
	{
		SimpleSocketManager ssm = new SimpleSocketManager();
		ssm.startMonitors();

	}

	public SimpleSocketManager()
	{
		logger = new Logger(this.getClass().getName());
		loadConfig();
	}

	@PostConstruct
	private void startMonitors()
	{

		if (SOCKET_WATCHER_URL != "")
		{
			try
			{
				XPathFactory factory = XPathFactory.newInstance();
				XPath xPath = factory.newXPath();

				logger.log.info("***Loading SocketWatchers from " + SOCKET_WATCHER_URL);
				File socketWatchers = new File(SOCKET_WATCHER_URL);
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(socketWatchers);

				// Get the individual SocketWatchers
				NodeList servertList = doc.getElementsByTagName("server");
				logger.log.info(Integer.toString(servertList.getLength())
						+ " SocketWatchers found in SocketWatcher.xml");

				// Iterate the SocketWatchers
				for (int i = 0; i < servertList.getLength(); i++)
				{
					Element serverElem = (Element) servertList.item(i);
					String serverName = (String) xPath.evaluate("address", serverElem, XPathConstants.STRING);

					NodeList sockList = (NodeList) xPath.evaluate("socket", serverElem, XPathConstants.NODESET);

					for (int j = 0; j < sockList.getLength(); j++)
					{
						Element socketElem = (Element) sockList.item(j);
						String port = (String) xPath.evaluate("port", socketElem, XPathConstants.STRING);
												
						SimpleSocketListener ssListener = new SimpleSocketListener(serverName, port);						
												
						NodeList filterList = (NodeList) xPath.evaluate("filters/filter", socketElem,
								XPathConstants.NODESET);
						for (int k = 0; k < filterList.getLength(); k++)
						{	
							Element filter = (Element) filterList.item(k);
							
							Element headerElement = (Element) xPath.evaluate("TargetClass/constructor/params/param/value/header[1]", filter, XPathConstants.NODE);
							
							Element ipElement = headerElement.getOwnerDocument().createElement("address");
							Node addressText = headerElement.getOwnerDocument().createTextNode(serverName);
							ipElement.appendChild(addressText);
							headerElement.appendChild(ipElement);
							
							Element portElement = headerElement.getOwnerDocument().createElement("port");
							Node portText = headerElement.getOwnerDocument().createTextNode(port);
							portElement.appendChild(portText);
							headerElement.appendChild(portElement);
							
							String regex = (String) xPath.evaluate("regex", filter, XPathConstants.STRING);
							String process = (String) xPath.evaluate("process", filter, XPathConstants.STRING);
							
							//ssListener.addMessagePair(regex, process);
							ssListener.addMessagePair(regex, filter);
						}
						ssListener.start();
					}

					xPath.reset();
				}

			}

			catch (SAXException e)
			{
				logger.log.error(e.getMessage());

			}
			catch (IOException e)
			{
				logger.log.error(e.getMessage());

			}
			catch (Exception e)
			{
				logger.log.error(e.getMessage());

			}
		}
		else
		{
			try
			{
				logger.log.error("Unable to load config for SocketWatcher");
				logger.log.error("SocketWatchers cannot be started");
			}
			catch (Exception e)
			{
				logger.log.error(e.getMessage());
			}
		}

		
	}

	private void loadConfig()
	{
		try
		{
			logger.log.debug("***Attempting to load SocketWatcher config from EJB***");
			InitialContext ctx = new InitialContext();
			GeneralConfig gc = null;
			try
			{
				gc = (GeneralConfig) ctx.lookup("java:module/GeneralConfig");
			}
			catch (Exception e)
			{
				gc = (GeneralConfig) ctx
						.lookup("java:global/uranus-ear/hephaestus/GeneralConfig!uk.co.mafew.hephaestus.GeneralConfig");
			}

			SOCKET_WATCHER_URL = gc.getConfigValue("socketWatcher");

			logger.log.info("***SocketWatcher Config loaded from EJB***");
		}
		catch (Exception e1)
		{
			logger.log.warn("****Error loading SocketWatcher config from EJB***");
			e1.printStackTrace();

			try
			{
				logger.log.debug("***Attempting to load SocketWatcher config from file***");
				Config cfg = new Config();
				cfg.loadFile("config.xml");
				SOCKET_WATCHER_URL = cfg.getConfigValue("socketWatcher");
				logger.log.info("***Config loaded from file***");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				logger.log.error("****Error loading SocketWatcher config from file***");
				e.printStackTrace();
			}
		}
	}

}
