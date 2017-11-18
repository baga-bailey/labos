package uk.co.mafew.khronos;

import java.util.Calendar;
import java.util.Date;

import javax.naming.InitialContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import uk.co.mafew.file.Config;
import uk.co.mafew.hephaestus.DatabaseHelper;
import uk.co.mafew.hephaestus.GeneralConfig;
import uk.co.mafew.logging.Logger;

import org.w3c.dom.*;

import uk.co.mafew.narcissus.*;

public class ScheduledEvent {
	Logger logger;
	
	private Element event;
	
	String PERSISTENCE;

	public ScheduledEvent() {
		logger = new Logger(this.getClass().getName());
		loadConfig();
	}

	public void start(Element evt) {
		
		event = evt;
		Iterator it = new Iterator();
		int hourOfDay = 0;
		int minute = 0;
		int second = 0;
		int day = 0;
		int month = 0;
		int year = 0;

		try {
			NodeList list = event.getElementsByTagName("RegularEvent");
			for (int i = 0; i < list.getLength(); i++) {
				Element elem = (Element) list.item(i);

				Calendar calendar = Calendar.getInstance();
				String nodevalue = elem.getElementsByTagName("hour").item(0)
						.getFirstChild().getNodeValue();
				if (nodevalue.compareToIgnoreCase("now") == 0) {
					hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
				} else {
					hourOfDay = Integer.parseInt(nodevalue);
				}

				nodevalue = elem.getElementsByTagName("minute").item(0)
						.getFirstChild().getNodeValue();
				if (nodevalue.compareToIgnoreCase("now") == 0) {
					minute = calendar.get(Calendar.MINUTE);
				} else {
					minute = Integer.parseInt(nodevalue);
				}

				nodevalue = elem.getElementsByTagName("second").item(0)
						.getFirstChild().getNodeValue();
				if (nodevalue.compareToIgnoreCase("now") == 0) {
					second = calendar.get(Calendar.SECOND);
				} else {
					second = Integer.parseInt(nodevalue);
				}

				nodevalue = elem.getElementsByTagName("day").item(0)
						.getFirstChild().getNodeValue();
				if (nodevalue.compareToIgnoreCase("now") == 0) {
					day = calendar.get(Calendar.DAY_OF_MONTH);
				} else {
					day = Integer.parseInt(nodevalue);
				}

				nodevalue = elem.getElementsByTagName("month").item(0)
						.getFirstChild().getNodeValue();
				if (nodevalue.compareToIgnoreCase("now") == 0) {
					month = calendar.get(Calendar.MONTH)+1;
				} else {
					month = Integer.parseInt(nodevalue);
				}

				nodevalue = elem.getElementsByTagName("year").item(0)
						.getFirstChild().getNodeValue();
				if (nodevalue.compareToIgnoreCase("now") == 0) {
					year = calendar.get(Calendar.YEAR);
				} else {
					year = Integer.parseInt(nodevalue);
				}

				int delay = (new Integer(elem.getElementsByTagName("delay")
						.item(0).getFirstChild().getNodeValue())).intValue();
				String period = elem.getElementsByTagName("period").item(0)
						.getFirstChild().getNodeValue();
				it.addEvent(new RegularEvent(hourOfDay, minute, second, day,
						month, year, delay, period));
			}

			list = event.getElementsByTagName("Conditions");
			// There should only be one 'Conditions' element
			if (list.getLength() == 1) {
				Element elem = (Element) list.item(0);

				System.out.println(elem.getNodeName());

				// Get a list of ConditionGroups
				NodeList groupList = elem
						.getElementsByTagName("ConditionGroup");
				for (int j = 0; j < groupList.getLength(); j++) {
					// A ConditionGroup holds a list of 'And' conditions and a
					// list of 'Or' conditions
					ConditionGroup conGroup = new ConditionGroup();

					Element groupElem = (Element) groupList.item(j);

					String type = groupElem.getAttribute("type");
					conGroup.setType(type);

					System.out.print(groupElem.getNodeName() + " ");
					System.out.println(groupElem.getAttribute("name"));

					// Add the ConditionGroup to the appropriate Iterator list
					if (groupElem.getAttribute("operator").compareTo("and") == 0) {
						// Add to the Iterator 'andList'
						it.addAndGroup(conGroup);
					} else if (groupElem.getAttribute("operator").compareTo(
							"or") == 0) {
						// Add to the Iterator 'orList'
						it.addOrGroup(conGroup);
					}

					NodeList conditionList = groupElem
							.getElementsByTagName("Condition");
					for (int x = 0; x < conditionList.getLength(); x++) {

						Element conditionElem = (Element) conditionList.item(x);
						String conditionType = groupElem.getAttribute("type");

						ReflectedObject ro = new ReflectedObject(conditionElem,"");
						Object object = ro.getObject();
						CompareWith cw = (CompareWith) object;
						if (conditionType != null && conditionType != "") {
							cw.setType(conditionType);
						}

						if (conditionElem.getAttribute("operator").compareTo(
								"and") == 0)
							conGroup.addAndCondition(cw);
						if (conditionElem.getAttribute("operator").compareTo(
								"or") == 0)
							conGroup.addOrCondition(cw);

					}
				}
			}

		} catch (Exception exp) {
			System.out.println(exp.getMessage());
			exp.printStackTrace();
		}
		Element targetClass = (Element) event.getElementsByTagName(
				"TargetClass").item(0);
		Alarm alarm = new Alarm();
		Date nextRunDate = alarm.initialise(it, targetClass);		
		
		if (PERSISTENCE.compareToIgnoreCase("database") == 0)
		{
			String nextRunString = nextRunDate.toString();
			DatabaseHelper dbh = new DatabaseHelper("java:jboss/datasources/ExampleDS");
			
			XPathFactory factory = XPathFactory.newInstance();
			XPath xPath = factory.newXPath();
			
			try
			{
				String target = (String) xPath.compile("//ScheduledEvent/TargetClass/method/params/param/value/text()").evaluate(event.getOwnerDocument(), XPathConstants.STRING);
				if(target.compareTo("uk.co.mafew.hephaestus.StartProcess")==0)
				{
					target = (String) xPath.compile("/ScheduledEvent/TargetClass/name/text()").evaluate(event, XPathConstants.STRING);
				}

				System.out.println("Process " + target + "is next set to run at " + nextRunString);
				String sqlString = "";

				String retString = dbh.executeUpdate(sqlString);
				if (retString.contains("ERROR"))
				{
					logger.log.error(retString);
				}
				else
				{
					logger.log.debug(retString);
				}
			}
			catch (XPathExpressionException e)
			{
				logger.log.error(e.getMessage());
			}
		}
	}

	public boolean cancel() {
		return true;
	}

	public void setURL(String postURL) {
	}
	
	private void loadConfig()
	{
		try 
		{
			logger.log.debug("***Attempting to load persistence config from EJB***");
			InitialContext ctx = new InitialContext();
			GeneralConfig gc = (GeneralConfig) ctx.lookup("java:module/GeneralConfig");
			PERSISTENCE = gc.getConfigValue("persistence");
			
			logger.log.info("***Message persistence config loaded from EJB***");
			
		} catch (Exception e1) {
			logger.log.warn("****Error loading persistence config from EJB***");

			try {
				logger.log.debug("***Attempting to load persistence config from file***");
				Config cfg = new Config();
				cfg.loadFile("config.xml");
				PERSISTENCE = cfg.getConfigValue("persistence");
				
				logger.log.info("***Message persistence config loaded from file***");
			} catch (Exception e) {
				logger.log.error("****Error loading persistence config from file***");
			}
		}
	}

//	private void callServlet() {
//		try {
//			String data = URLEncoder.encode("key1", "UTF-8") + "="
//					+ URLEncoder.encode("value1", "UTF-8");
//			data = data + "&" + URLEncoder.encode("key2", "UTF-8") + "="
//					+ URLEncoder.encode("value2", "UTF-8");
//			URL url = new URL(postURL);
//			URLConnection conn = url.openConnection();
//			conn.setDoOutput(true);
//			OutputStreamWriter wr = new OutputStreamWriter(
//					conn.getOutputStream());
//			wr.write(data);
//			wr.flush();
//			BufferedReader rd = new BufferedReader(new InputStreamReader(
//					conn.getInputStream()));
//			while ((rd.readLine()) != null)
//				;
//			wr.close();
//			rd.close();
//		} catch (Exception exp) {
//			System.out.println("Error calling URI " + postURL);
//			exp.printStackTrace();
//		}
//	}

//	private boolean containsInterface(String iFace, Class classDefinition) {
//		Class interfaces[] = classDefinition.getInterfaces();
//		for (int i = 0; i < interfaces.length; i++)
//			if (interfaces[i].getName() == iFace)
//				return true;
//
//		return false;
//	}
	
}
