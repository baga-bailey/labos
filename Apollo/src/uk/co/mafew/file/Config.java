package uk.co.mafew.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Config
{
	Document configDoc;
	HashMap<String, String> configMap;

	public static void main(String[] args)
	{
	}

	public Config()
	{
		configDoc = loadFile("config.xml");
		configMap = parseConfig(configDoc);
	}

	public String getConfigValue(String configKey)
	{
		return (String) configMap.get(configKey);
	}

	public Document getConfigDoc()
	{
		return configDoc;
	}

	public Document loadFile(String fileName)
	{
		Document doc = null;
		InputStream in = null;
		try
		{
			ClassLoader loader = this.getClass().getClassLoader();
			in = loader.getResourceAsStream(fileName);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(in);
		}
		catch (ParserConfigurationException e)
		{
			System.out.println("ERROR: " + e.getMessage());
		}
		catch (SAXException e)
		{
			System.out.println("ERROR: " + e.getMessage());
		}
		catch (IOException e)
		{
			System.out.println("ERROR: " + e.getMessage());
		}
		finally
		{
			try
			{
				in.close();
			}
			catch (IOException e)
			{
				System.out.println("ERROR: " + e.getMessage());
			}
		}
		return doc;
	}

	private HashMap<String, String> parseConfig(Document doc)
	{
		HashMap<String, String> configMap = new HashMap<String, String>();
		NodeList list = configDoc.getElementsByTagName("config").item(0).getChildNodes();
		for (int i = 0; i < list.getLength(); i++)
		{
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				String key = list.item(i).getNodeName();
				String value = list.item(i).getTextContent();
				configMap.put(key, value);
			}
		}
		return configMap;
	}
}
