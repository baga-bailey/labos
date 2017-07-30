package uk.co.mafew.file.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

import uk.co.mafew.logging.Logger;

public class HistoryFragment
{
	String status;
	String name;
	String processId;
	String processName;
	String startDate;
	String startTime;
	String endDate;
	String endTime;
	String id;
	String friendlyName;
	String uuid;
	Document doc;
	Logger logger;

	public HistoryFragment()
	{
		logger = new Logger(this.getClass().getName());
	}

	public void createProcessFragment()
	{
		try
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("process");
			doc.appendChild(rootElement);

			// uuid attribute
			Attr uuid = doc.createAttribute("uuid");
			rootElement.setAttributeNode(uuid);

			// id attribute
			Attr id = doc.createAttribute("id");
			rootElement.setAttributeNode(id);

			// name attribute
			Attr name = doc.createAttribute("name");
			rootElement.setAttributeNode(name);

			// Start Date element
			Element startDate = doc.createElement("startDate");
			rootElement.appendChild(startDate);

			// Start Time element
			Element startTime = doc.createElement("startTime");
			rootElement.appendChild(startTime);

			// End Date element
			Element endDate = doc.createElement("endDate");
			rootElement.appendChild(endDate);

			// End Time element
			Element endTime = doc.createElement("endTime");
			rootElement.appendChild(endTime);

			Element status = doc.createElement("status");
			rootElement.appendChild(status);
		}
		catch (DOMException e)
		{
			logger.log.error("ERROR: " + e.getMessage());
		}
		catch (ParserConfigurationException e)
		{
			logger.log.error("ERROR: " + e.getMessage());
		}
	}

	public void createStepFragment()
	{
		try
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("step");
			doc.appendChild(rootElement);

			// id attribute
			Attr id = doc.createAttribute("id");
			rootElement.setAttributeNode(id);

			// friendlyName attribute
			Attr name = doc.createAttribute("friendlyName");
			rootElement.setAttributeNode(name);

			// uuid attribute
			Attr uuid = doc.createAttribute("uuid");
			rootElement.setAttributeNode(uuid);

			// Start Date element
			Element startDate = doc.createElement("startDate");
			rootElement.appendChild(startDate);

			// Start Time element
			Element startTime = doc.createElement("startTime");
			rootElement.appendChild(startTime);

			// End Date element
			Element endDate = doc.createElement("endDate");
			rootElement.appendChild(endDate);

			// End Time element
			Element endTime = doc.createElement("endTime");
			rootElement.appendChild(endTime);

			// Status element
			Element status = doc.createElement("status");
			rootElement.appendChild(status);

			// Process id element
			Element processID = doc.createElement("processID");
			rootElement.appendChild(processID);

			// Process name element
			Element processName = doc.createElement("processName");
			rootElement.appendChild(processName);
		}
		catch (DOMException e)
		{
			logger.log.error("ERROR: " + e.getMessage());
		}
		catch (ParserConfigurationException e)
		{
			logger.log.error("ERROR: " + e.getMessage());
		}
	}

	/*************************************************************************************/
	/*SETTERS*/
	
	public void setUuid(String uuid)
	{
		this.uuid = uuid;
		doc.getDocumentElement().getAttributes().getNamedItem("uuid").setTextContent(uuid);
	}

	public void setId(String id)
	{
		this.id = id;
		doc.getDocumentElement().getAttributes().getNamedItem("id").setTextContent(id);
	}
	
	public void setStatus(String status)
	{
		this.status = status;
		doc.getElementsByTagName("status").item(0).setTextContent(status);
	}

	public void setName(String name)
	{
		this.name = name;
		doc.getDocumentElement().getAttributes().getNamedItem("name").setTextContent(name);
	}

	public void setStartDate(String startDate)
	{
		this.startDate = startDate;
		doc.getElementsByTagName("startDate").item(0).setTextContent(startDate);
	}
	
	public void setEndDate(String endDate)
	{
		this.endDate = endDate;
		doc.getElementsByTagName("endDate").item(0).setTextContent(endDate);
	}
	
	public void setProcessId(String processId)
	{
		this.processId = processId;
		doc.getElementsByTagName("processID").item(0).setTextContent(processId);
	}

	public void setProcessName(String processName)
	{
		this.processName = processName;
		doc.getElementsByTagName("processName").item(0).setTextContent(processName);
	}

	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
		doc.getElementsByTagName("startTime").item(0).setTextContent(startTime);
	}

	public void setEndTime(String endTime)
	{
		this.endTime = endTime;
		doc.getElementsByTagName("endTime").item(0).setTextContent(endTime);
	}

	public void setFriendlyName(String friendlyName)
	{
		this.friendlyName = friendlyName;
		doc.getDocumentElement().getAttributes().getNamedItem("friendlyName").setTextContent(friendlyName);
	}

	public void setDoc(Document doc)
	{
		this.doc = doc;
	}

	/*************************************************************************************/
	/*GETTERS*/
	
	public String getEndDate()
	{
		return endDate;
	}

	public String getEndTime()
	{
		return endTime;
	}

	public String getFriendlyName()
	{
		return friendlyName;
	}

	public Document getDoc()
	{
		return doc;
	}

	public String getId()
	{
		return id;

	}

	public String getStatus()
	{
		return status;
	}

	public String getStartDate()
	{
		return startDate;
	}

	public String getStartTime()
	{
		return startTime;
	}

}
