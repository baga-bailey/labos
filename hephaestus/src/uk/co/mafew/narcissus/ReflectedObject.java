package uk.co.mafew.narcissus;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;

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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import uk.co.mafew.format.Convert;
import uk.co.mafew.file.kryptos.Encryption;
import uk.co.mafew.hephaestus.GeneralConfig;
import uk.co.mafew.logging.Logger;

public class ReflectedObject
{
	Object object = null;
	Method method = null;
	Object constructorObjects[];
	Class<?> constructorClasses[];
	Object methodObjects[];
	Class<?> methodClasses[];
	Boolean KeepAlive;
	Logger logger;
	Class<?> req;
	String USER_ENCRYPTION_KEY = "";

	public Boolean getKeepAlive()
	{
		return KeepAlive;
	}

	public void setKeepAlive(Boolean keepAlive)
	{
		KeepAlive = keepAlive;
	}

	public Class<?> getReflectedClass()
	{
		return req;
	}

	public ReflectedObject(Element TargetClass, String userEncryptionKey) throws Exception
	{
		try
		{
			USER_ENCRYPTION_KEY = userEncryptionKey;
			logger = new Logger(this.getClass().getName());
			Element elem = (Element) TargetClass.getElementsByTagName("constructor").item(0);
			logger.log.debug("**********************************************");
			logger.log.debug("*************Target class element*************");
			logger.log.debug(Convert.docToString(elem.getOwnerDocument()));

			// Get constructor
			String className = TargetClass.getElementsByTagName("name").item(0).getTextContent();
			req = Class.forName(className);
			String ka = "false";

			try
			{
				ka = TargetClass.getElementsByTagName("KeepAlive").item(0).getTextContent();
			}
			catch (Exception e)
			{
				logger.log.info("Keep alive is set to false");

			}
			if (ka == "true")
			{
				setKeepAlive(true);
			}
			else
			{
				setKeepAlive(false);
			}

			if (elem != null)
			{
				Constructor<?> constructor = null;
				Element constructorParams = (Element) elem.getElementsByTagName("params").item(0);
				if (constructorParams != null)
				{
					constructorObjects = enumerateObjectList(constructorParams);
					constructorClasses = enumerateClassList(constructorParams);
					constructor = req.getDeclaredConstructor(constructorClasses);
					object = constructor.newInstance(constructorObjects);
				}

				else
				{
					constructor = req.getDeclaredConstructor();
					object = constructor.newInstance();
				}

			}

			elem = (Element) TargetClass.getElementsByTagName("method").item(0);
			if (elem != null)
			{
				Element methodParams = (Element) elem.getElementsByTagName("params").item(0);
				if (methodParams != null)
				{
					methodObjects = enumerateObjectList(methodParams);
					methodClasses = enumerateClassList(methodParams);

					elem = (Element) elem.getElementsByTagName("name").item(0);
					String methodName = elem.getTextContent();
					method = req.getMethod(methodName, methodClasses);
				}
			}
		}
		catch (Exception e)
		{
			logger.log.error(e.getMessage());
			throw e;
		}
	}

	public void setMethod(String meth)
	{
		try
		{
			method = req.getMethod(meth, methodClasses);
		}
		catch (NoSuchMethodException e)
		{
			logger.log.error("ERROR: Method '" + meth + "' not found-" + e.getMessage());
		}
		catch (SecurityException e)
		{
			logger.log.error("ERROR: Security Exception-" + e.getMessage());
		}
	}

	public void setMethodObjects(Object[] methObjects)
	{
		methodObjects = methObjects;
	}

	/**
	 * @param args
	 */
	// Suppressing warnings on main method as its only used for testing
	@SuppressWarnings("unused")
	public static void main(String[] args)
	{

		try
		{
			String file = "file:///C:/Users/baileyj/Documents/eclipse/workspace/timerTest/TimeTable.xml";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);

			Element targetClass = (Element) doc.getElementsByTagName("TargetClass").item(0);
			ReflectedObject ro = new ReflectedObject(targetClass, "");

		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private Class<?>[] enumerateClassList(Element elem)
	{
		NodeList paramNodeList = elem.getElementsByTagName("param");
		Class<?>[] classes = null;
		String type;
		ArrayList<Class<?>> paramClassList = new ArrayList<Class<?>>(0);

		try
		{
			for (int y = 0; y < paramNodeList.getLength(); y++)
			{
				Element paramElem = (Element) paramNodeList.item(y);
				int ordinal = (new Integer(paramElem.getElementsByTagName("ordinal").item(0).getTextContent()))
						.intValue();
				type = paramElem.getElementsByTagName("type").item(0).getTextContent();
				// Classes
				if (type.compareTo("Byte") == 0)
				{
					paramClassList.add(ordinal, Class.forName("java.lang.Byte"));
				}
				else if (type.compareTo("Short") == 0)
				{
					paramClassList.add(ordinal, Class.forName("java.lang.Short"));
				}
				else if (type.compareTo("Integer") == 0)
				{
					paramClassList.add(ordinal, Class.forName("java.lang.Integer"));
				}
				else if (type.compareTo("Long") == 0)
				{
					paramClassList.add(ordinal, Class.forName("java.lang.Long"));
				}
				else if (type.compareTo("Float") == 0)
				{
					paramClassList.add(ordinal, Class.forName("java.lang.Float"));
				}
				else if (type.compareTo("Double") == 0)
				{
					paramClassList.add(ordinal, Class.forName("java.lang.Double"));
				}
				else if (type.compareTo("Character") == 0)
				{
					paramClassList.add(ordinal, Class.forName("java.lang.Character"));
				}
				else if (type.compareTo("Boolean") == 0)
				{
					paramClassList.add(ordinal, Class.forName("java.lang.Boolean"));
				}
				else if (type.compareTo("String") == 0)
				{
					paramClassList.add(ordinal, Class.forName("java.lang.String"));
				}
				else if (type.compareTo("NodeList") == 0)
				{
					paramClassList.add(ordinal, Class.forName("org.w3c.dom.NodeList"));
				}
				else if (type.compareTo("xml") == 0)
				{
					paramClassList.add(ordinal, Class.forName("org.w3c.dom.Node"));
				}
				// Primitive types
				else if (type.compareTo("byte") == 0)
				{
					paramClassList.add(ordinal, Byte.TYPE);
				}
				else if (type.compareTo("short") == 0)
				{
					paramClassList.add(ordinal, Short.TYPE);
				}
				else if (type.compareTo("int") == 0)
				{
					paramClassList.add(ordinal, Integer.TYPE);
				}
				else if (type.compareTo("long") == 0)
				{
					paramClassList.add(ordinal, Long.TYPE);
				}
				else if (type.compareTo("float") == 0)
				{
					paramClassList.add(ordinal, Float.TYPE);
				}
				else if (type.compareTo("double") == 0)
				{
					paramClassList.add(ordinal, Double.TYPE);
				}
				else if (type.compareTo("char") == 0)
				{
					paramClassList.add(ordinal, Character.TYPE);
				}
				else if (type.compareTo("boolean") == 0)
				{
					paramClassList.add(ordinal, Boolean.TYPE);
				}
				else
				{
					logger.log.error("ERROR: Type '" + type + "' not found");
				}

			}

			classes = new Class[paramClassList.size()];
			for (int z = 0; z < paramClassList.size(); z++)
			{
				classes[z] = paramClassList.get(z);
			}

		}
		catch (Exception e)
		{
			logger.log.error("ERROR: when enumerating the class list");
			logger.log.error("ERROR: " + e.getMessage());
		}
		return classes;
	}

	private Object[] enumerateObjectList(Element elem)
	{
		
		NodeList paramNodeList = elem.getElementsByTagName("param");
		String type;
		ArrayList<Object> paramObjectList = new ArrayList<Object>(0);
		try
		{
			for (int y = 0; y < paramNodeList.getLength(); y++)
			{
				Element paramElem = (Element) paramNodeList.item(y);
				
				XPathFactory factory = null;
				XPath xPath = null;
//				
				String isEncrypted = "false";

				int ordinal = (new Integer(paramElem.getElementsByTagName("ordinal").item(0).getTextContent()))
						.intValue();
				type = paramElem.getElementsByTagName("type").item(0).getTextContent();
				String value = paramElem.getElementsByTagName("value").item(0).getTextContent();

				// Evaluate any variables in the value
				value = evaluateVariables(value);
				try
				{
					isEncrypted = paramElem.getElementsByTagName("value").item(0).getAttributes()
							.getNamedItem("encrypted").getTextContent();
					logger.log.debug("'encrypted' is set to " + isEncrypted);
					if (isEncrypted.compareTo("true") == 0)
					{
						value = decryptVariable(value);
					}
				}
				catch (Exception e)
				{
					logger.log.warn("'encrypted' attribute may not be set");
				}

				// Classes
				if (type.compareTo("Byte") == 0)
				{
					paramObjectList.add(ordinal, new Byte(value));
				}
				else if (type.compareTo("Short") == 0)
				{
					paramObjectList.add(ordinal, new Short(value));
				}
				else if (type.compareTo("Integer") == 0)
				{
					paramObjectList.add(ordinal, new Integer(value));
				}
				else if (type.compareTo("Long") == 0)
				{
					paramObjectList.add(ordinal, new Long(value));
				}
				else if (type.compareTo("Float") == 0)
				{
					paramObjectList.add(ordinal, new Float(value));
				}
				else if (type.compareTo("Double") == 0)
				{
					paramObjectList.add(ordinal, new Double(value));
				}
				else if (type.compareTo("Character") == 0)
				{
					paramObjectList.add(ordinal, new Character(value.charAt(0)));
				}
				else if (type.compareTo("Boolean") == 0)
				{
					paramObjectList.add(ordinal, new Boolean(value));
				}
				else if (type.compareTo("String") == 0)
				{
					paramObjectList.add(ordinal, new String(value));
				}
				else if (type.compareTo("NodeList") == 0)
				{
					paramObjectList.add(ordinal, elem.getElementsByTagName("nodes").item(0).getChildNodes());
				}
				else if (type.compareTo("xml") == 0)
				{
					factory = XPathFactory.newInstance();
					xPath = factory.newXPath();
					Node xmlParam = (Node) xPath.evaluate("value/*[1]", paramElem, XPathConstants.NODE);
					paramObjectList.add(ordinal, xmlParam);
				}
				// Primitive types
				else if (type.compareTo("byte") == 0)
				{
					paramObjectList.add(ordinal, Byte.parseByte(value));
				}
				else if (type.compareTo("short") == 0)
				{
					paramObjectList.add(ordinal, Short.parseShort(value));
				}
				else if (type.compareTo("int") == 0)
				{
					paramObjectList.add(ordinal, Integer.parseInt(value));
					// paramObjectList.add(ordinal, new Integer(value));
				}
				else if (type.compareTo("long") == 0)
				{
					paramObjectList.add(ordinal, Long.parseLong(value));
				}
				else if (type.compareTo("float") == 0)
				{
					paramObjectList.add(ordinal, Float.parseFloat(value));
				}
				else if (type.compareTo("double") == 0)
				{
					paramObjectList.add(ordinal, Double.parseDouble(value));
				}
				else if (type.compareTo("char") == 0)
				{
					paramObjectList.add(ordinal, value.charAt(0));
				}
				else if (type.compareTo("boolean") == 0)
				{
					paramObjectList.add(ordinal, Boolean.parseBoolean(value));
				}
				else
				{
					logger.log.error("ERROR: Type '" + type + "' not found");
				}
			}

		}
		catch (Exception e)
		{
			logger.log.error("ERROR: when enumerating the object list");
			logger.log.error("ERROR: " + e.getMessage());
			logger.log.error(e.getStackTrace());
		}
		return paramObjectList.toArray();
	}

	public Object getObject()
	{
		return this.object;
	}

	public Document invoke()
	{
		Object result;
		Document doc = null;
		try
		{
			result = method.invoke(object, methodObjects);
			doc = parseObject(result);
		}
		catch (Exception e)
		{
			try
			{
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				StringReader sr = new StringReader("<returnValue><error>" + e.getMessage() + "</error></returnValue>");
				InputSource is = new InputSource(sr);

				doc = db.parse(is);
				logger.log.debug(Convert.docToString(doc));
			}
			catch (ParserConfigurationException e1)
			{
				logger.log.error("ERROR: " + e1.getMessage());
			}
			catch (SAXException e1)
			{
				logger.log.error("ERROR: " + e1.getMessage());
			}
			catch (IOException e1)
			{
				logger.log.error("ERROR: " + e1.getMessage());
			}
		}

		return doc;
	}

	private Document parseObject(Object ob)
	{
		Document doc = null;
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<returnValue></returnValue>");
			InputSource is = new InputSource(sr);

			doc = db.parse(is);

			if (ob instanceof Byte)
			{
				Node returnValueNode = doc.getFirstChild();
				Node objectNode = doc.createElement("object");
				Node typeNode = doc.createElement("type");
				typeNode.setTextContent("Byte");
				Node valueNode = doc.createElement("value");
				Byte i = (Byte) ob;
				valueNode.setTextContent(i.toString());

				returnValueNode.appendChild(objectNode);
				objectNode.appendChild(typeNode);
				objectNode.appendChild(valueNode);

			}
			else if (ob instanceof Short)
			{
				Node returnValueNode = doc.getFirstChild();
				Node objectNode = doc.createElement("object");
				Node typeNode = doc.createElement("type");
				typeNode.setTextContent("Short");
				Node valueNode = doc.createElement("value");
				Short i = (Short) ob;
				valueNode.setTextContent(i.toString());

				returnValueNode.appendChild(objectNode);
				objectNode.appendChild(typeNode);
				objectNode.appendChild(valueNode);

			}
			else if (ob instanceof Integer)
			{
				Node returnValueNode = doc.getFirstChild();
				Node objectNode = doc.createElement("object");
				Node typeNode = doc.createElement("type");
				typeNode.setTextContent("Integer");
				Node valueNode = doc.createElement("value");
				Integer i = (Integer) ob;
				valueNode.setTextContent(i.toString());

				returnValueNode.appendChild(objectNode);
				objectNode.appendChild(typeNode);
				objectNode.appendChild(valueNode);

			}
			else if (ob instanceof Long)
			{
				Node returnValueNode = doc.getFirstChild();
				Node objectNode = doc.createElement("object");
				Node typeNode = doc.createElement("type");
				typeNode.setTextContent("Long");
				Node valueNode = doc.createElement("value");
				Long i = (Long) ob;
				valueNode.setTextContent(i.toString());

				returnValueNode.appendChild(objectNode);
				objectNode.appendChild(typeNode);
				objectNode.appendChild(valueNode);

			}
			else if (ob instanceof Float)
			{
				Node returnValueNode = doc.getFirstChild();
				Node objectNode = doc.createElement("object");
				Node typeNode = doc.createElement("type");
				typeNode.setTextContent("Float");
				Node valueNode = doc.createElement("value");
				Float i = (Float) ob;
				valueNode.setTextContent(i.toString());

				returnValueNode.appendChild(objectNode);
				objectNode.appendChild(typeNode);
				objectNode.appendChild(valueNode);

			}
			else if (ob instanceof Double)
			{
				Node returnValueNode = doc.getFirstChild();
				Node objectNode = doc.createElement("object");
				Node typeNode = doc.createElement("type");
				typeNode.setTextContent("Double");
				Node valueNode = doc.createElement("value");
				Double i = (Double) ob;
				valueNode.setTextContent(i.toString());

				returnValueNode.appendChild(objectNode);
				objectNode.appendChild(typeNode);
				objectNode.appendChild(valueNode);

			}
			else if (ob instanceof Character)
			{
				Node returnValueNode = doc.getFirstChild();
				Node objectNode = doc.createElement("object");
				Node typeNode = doc.createElement("type");
				typeNode.setTextContent("Character");
				Node valueNode = doc.createElement("value");
				Character i = (Character) ob;
				valueNode.setTextContent(i.toString());

				returnValueNode.appendChild(objectNode);
				objectNode.appendChild(typeNode);
				objectNode.appendChild(valueNode);

			}
			else if (ob instanceof Boolean)
			{
				Node returnValueNode = doc.getFirstChild();
				Node objectNode = doc.createElement("object");
				Node typeNode = doc.createElement("type");
				typeNode.setTextContent("Boolean");
				Node valueNode = doc.createElement("value");
				Boolean i = (Boolean) ob;
				valueNode.setTextContent(i.toString());

				returnValueNode.appendChild(objectNode);
				objectNode.appendChild(typeNode);
				objectNode.appendChild(valueNode);

			}
			else if (ob instanceof String)
			{
				Node returnValueNode = doc.getFirstChild();
				Node objectNode = doc.createElement("object");
				Node typeNode = doc.createElement("type");
				typeNode.setTextContent("String");
				Node valueNode = doc.createElement("value");
				String i = (String) ob;
				valueNode.setTextContent(i.toString());

				returnValueNode.appendChild(objectNode);
				objectNode.appendChild(typeNode);
				objectNode.appendChild(valueNode);

			}
			else if (ob instanceof Node)
			{
				Node returnValueNode = doc.getFirstChild();
				Node objectNode = doc.createElement("object");
				Node typeNode = doc.createElement("type");
				typeNode.setTextContent("Node");
				Node valueNode = doc.createElement("value");
				Node importNode = doc.importNode((Node) ob, true);
				valueNode.appendChild(importNode);

				returnValueNode.appendChild(objectNode);
				objectNode.appendChild(typeNode);
				objectNode.appendChild(valueNode);

			}
			else
			{
				logger.log.error("ERROR: Object not found");
			}
		}
		catch (Exception e)
		{
			try
			{
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				StringReader sr = new StringReader("<returnValue><error>" + e.getMessage() + "</error></returnValue>");
				InputSource is = new InputSource(sr);

				doc = db.parse(is);
			}
			catch (ParserConfigurationException e1)
			{
				logger.log.error("ERROR: " + e1.getMessage());
			}
			catch (SAXException e1)
			{
				logger.log.error("ERROR: " + e1.getMessage());
			}
			catch (IOException e1)
			{
				logger.log.error("ERROR: " + e1.getMessage());
			}
		}
		return doc;
	}

	private String decryptVariable(String value)
	{
		try
		{
			// Decrypt first with the built in key
			logger.log.debug("Decrypting using the System key");
			value = Encryption.decrypt(value, Encryption.getSystemEncryptionKey());

			// Now decrypt with the user defined key
			logger.log.debug("Decrypting using the User key");
			value = Encryption.decrypt(value, USER_ENCRYPTION_KEY);
		}
		catch (Exception e)
		{
			logger.log.error("There was an error whilst decrypting the 'value': " + value);
		}

		logger.log.debug("Decrypted variable: " + value);

		return value;
	}

	private String evaluateVariables(String value)
	{
		try
		{
			InitialContext ctx = new InitialContext();
			GeneralConfig gc = (GeneralConfig) ctx.lookup("java:global/uranus-ear/hephaestus/GeneralConfig");
			value = gc.evaluateVariables(value);
		}
		catch (Exception e)
		{
			logger.log.error(e.getMessage());
		}

		return value;
	}

}
