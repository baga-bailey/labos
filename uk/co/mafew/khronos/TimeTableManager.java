package uk.co.mafew.khronos;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.InitialContext;
import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import uk.co.mafew.logging.Logger;
import uk.co.mafew.file.Config;
import uk.co.mafew.hephaestus.*;

@Startup
@Singleton

public class TimeTableManager {

	Logger logger;
	
	String TIMETABLE_URL = "";
	
	
	public TimeTableManager() {
		logger = new Logger(this.getClass().getName());
		loadConfig();
	}

	public static void main(String args[]) {

		TimeTableManager ttm = new TimeTableManager();
		ttm.TIMETABLE_URL="C:\\Users\\jbailey1\\Documents\\uranus\\timetable\\TimeTable.xml";
		ttm.startTimeTable();
	}

	@PostConstruct
	public void startTimeTable() 
	{
		if (TIMETABLE_URL != "") {
			try {
				logger.log.debug("***Loading Timetable from " + TIMETABLE_URL);
				File xmlFile = new File(TIMETABLE_URL);
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(xmlFile);

				if (validateTimeTable(doc))
				{
					NodeList list = doc.getElementsByTagName("ScheduledEvent");
					for (int i = 0; i < list.getLength(); i++) {
						ScheduledEvent myTimeTable = new ScheduledEvent();
						myTimeTable.start((Element) list.item(i));
					}
				}

			} catch (SAXException e) {
				logger.log.error(e.getMessage());
				
			} catch (IOException e) {
				logger.log.error(e.getMessage());
				
			} catch (Exception e) {
				logger.log.error(e.getMessage());
				
			}
		}
		
	}

	private Boolean validateTimeTable(Document doc) {
		try {
			SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			URL url = this.getClass().getResource("/xml/TimeTable.xsd");
			new StreamSource(new File(url.getFile()));

			// create a Validator instance, which can be used to validate an
			// instance document
			// Validator validator = schema.newValidator();

			// validate the DOM tree
			// validator.validate(new DOMSource(doc));
		} catch (Exception e) {
			logger.log.debug(e.getMessage());
			return false;
		}
		return true;
	}

	public String loadTimeTable() {
		return "";
	}
	
	private void loadConfig()
	{
		try 
		{
			logger.log.debug("***Attempting to load timetable URL config from EJB***");
			InitialContext ctx = new InitialContext();
			GeneralConfig gc = (GeneralConfig) ctx.lookup("java:module/GeneralConfig");
			TIMETABLE_URL = gc.getConfigValue("timeTable");
			
			logger.log.info("***Message timetable URL config loaded from EJB***");
			
		} catch (Exception e1) {
			logger.log.warn("****Error loading timetable URL config from EJB***");

			try {
				logger.log.debug("***Attempting to load timetable URL config from file***");
				Config cfg = new Config();
				cfg.loadFile("config.xml");
				TIMETABLE_URL = cfg.getConfigValue("timeTable");
				
				logger.log.info("***Message timetable URL config loaded from file***");
			} catch (Exception e) {
				logger.log.error("****Error loading timetable URL config from file***");
			}
		}
	}


}
