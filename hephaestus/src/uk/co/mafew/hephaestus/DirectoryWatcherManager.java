package uk.co.mafew.hephaestus;

import static javax.ejb.LockType.READ;

import java.io.File;
import java.io.IOException;
import java.nio.file.WatchKey;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Remote;
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
import uk.co.mafew.logging.Logger;

/**
 * Session Bean implementation class DirectoryWatcherManager
 */
@Startup
@Singleton
@LocalBean
@Lock(READ)

public class DirectoryWatcherManager
{
	private static DirectoryWatcherManager instance = new DirectoryWatcherManager("");

	Logger logger;
	private String FILE_WATCHER_URL = "";
	private long GLOBAL_WRITE_CHECK_PAUSE = 5000;
	private DirectoryWatcher dirWatcher = null;

	public static void main(String[] args)
	{
		DirectoryWatcherManager dwm = new DirectoryWatcherManager();
		dwm.startFileWatcher();
	}

	/**
	 * Default constructor.
	 */
	private DirectoryWatcherManager()
	{
		
	}
	
	private DirectoryWatcherManager(String str)
	{
		logger = new Logger(this.getClass().getName());
		loadConfig();
		startFileWatcher();
		//instance = this;
	}
	
	
	public void run()
	{
		getInstance();
	}
	
	
	public static DirectoryWatcherManager getInstance() {
	      
	      return instance;
	   }

	
	public void startFileWatcher()
	{

		if (FILE_WATCHER_URL != "")
		{
			try
			{
				XPathFactory factory = XPathFactory.newInstance();
				XPath xPath = factory.newXPath();

				logger.log.info("***Loading FileWatchers from " + FILE_WATCHER_URL);
				File fileWatchers = new File(FILE_WATCHER_URL);
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(fileWatchers);

				// Get the individual filewatchers
				NodeList fwList = doc.getElementsByTagName("FileWatcher");
				logger.log.info(Integer.toString(fwList.getLength()) + " Filewatchers found in FileWatcher.xml");
				dirWatcher = new DirectoryWatcher();
				dirWatcher.setWRITE_CHECK_PAUSE(GLOBAL_WRITE_CHECK_PAUSE);
				logger.log.info("Created directory watcher");

				// Iterate the filewatchers, register them with the parent
				// DirectoryWatcher and binding them to their target class
				for (int i = 0; i < fwList.getLength(); i++)
				{
					Element elem = (Element) fwList.item(i);

					String watchedDirectory = (String) xPath.evaluate("Directory", elem, XPathConstants.STRING);
					NodeList targetClassList = (NodeList) xPath.evaluate("TargetClass", elem, XPathConstants.NODESET);
					Element targetClassElem = (Element) targetClassList.item(0);
					WatchKey key = dirWatcher.register(watchedDirectory);
					dirWatcher.bind(key, elem);
					xPath.reset();
				}
				dirWatcher.start();
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
				logger.log.error("Unable to load config for Filewatcher");
				logger.log.error("Filewatches cannot be started");
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
	}

	private void loadConfig()
	{
		try
		{
			logger.log.debug("***Attempting to load FileWatcher config from EJB***");
			InitialContext ctx = new InitialContext();
			GeneralConfig gc = null;
			try
			{
				gc = (GeneralConfig) ctx.lookup("java:module/GeneralConfig");
			}
			catch (Exception e)
			{
				gc = (GeneralConfig) ctx.lookup("java:global/uranus-ear/hephaestus/GeneralConfig!uk.co.mafew.hephaestus.GeneralConfig");
			}
			
			FILE_WATCHER_URL = gc.getConfigValue("fileWatcher");
			GLOBAL_WRITE_CHECK_PAUSE = Long.parseLong(gc.getConfigValue("globalWriteCheckPause"));

			logger.log.info("***FileWatcher Config loaded from EJB***");
		}
		catch (Exception e1)
		{
			logger.log.warn("****Error loading FileWatcher config from EJB***");
			e1.printStackTrace();

			try
			{
				logger.log.debug("***Attempting to load FileWatcher config from file***");
				Config cfg = new Config();
				cfg.loadFile("config.xml");
				FILE_WATCHER_URL = cfg.getConfigValue("fileWatcher");
				GLOBAL_WRITE_CHECK_PAUSE = Long.parseLong(cfg.getConfigValue("globalWriteCheckPause"));
				logger.log.info("***Config loaded from file***");
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				logger.log.error("****Error loading FileWatcher config from file***");
				e.printStackTrace();
			}
		}
	}
}
