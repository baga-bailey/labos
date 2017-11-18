package uk.co.mafew.khronos;

// TODO delete this class once we know we don't need it
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import uk.co.mafew.logging.Logger;

public class ReflectedObjectOld
{
	Object object = null;
	Method method = null;
	Object constructorObjects[];
	Class constructorClasses[];
	Object methodObjects[];
	Class methodClasses[];
	Boolean KeepAlive;
	Logger logger;

	public Boolean getKeepAlive()
	{
		return KeepAlive;
	}

	public void setKeepAlive(Boolean keepAlive)
	{
		KeepAlive = keepAlive;
	}

	public ReflectedObjectOld(Element TargetClass) throws Exception
	{
		try
		{
			logger =  new Logger(this.getClass().getName());
			
			// Get constructor
			String className = TargetClass.getElementsByTagName("name").item(0)
					.getTextContent();
			Class req = Class.forName(className);
			String ka = "false";
			
			try
			{
				ka = TargetClass.getElementsByTagName("KeepAlive").item(0)
						.getTextContent();
			}
			catch(Exception e)
			{
				logger.log.info("Keep alive is set to false");
			
			}
			if(ka=="true")
			{
				setKeepAlive(true);
			}
			else
			{
				setKeepAlive(false);
			}

			Element elem = (Element) TargetClass.getElementsByTagName("constructor").item(0);
			if (elem != null)
			{
				Constructor constructor = null;
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
				}
				elem = (Element) elem.getElementsByTagName("name").item(0);
				String methodName = elem.getTextContent();
				method = req.getMethod(methodName, methodClasses);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		try
		{
			String className = "uk.co.mafew.kronos.SchedulerTest";

			String file = "file:///C:/Users/baileyj/Documents/eclipse/workspace/timerTest/TimeTable.xml";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);

			Element targetClass = (Element) doc.getElementsByTagName(
					"TargetClass").item(0);
			ReflectedObjectOld ro = new ReflectedObjectOld(targetClass);
			NodeList paramNodeList = targetClass.getElementsByTagName("param");
			Object object = ro.getObject();
			doc = ro.invoke();
		} catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Class[] enumerateClassList(Element elem)
	{
		NodeList paramNodeList = elem.getElementsByTagName("param");
		Class[] classes = null;
		String type;
		ArrayList paramClassList = new ArrayList(0);

		try
		{
			for (int y = 0; y < paramNodeList.getLength(); y++)
			{
				Element paramElem = (Element) paramNodeList.item(y);
				int ordinal = (new Integer(paramElem.getElementsByTagName(
						"ordinal").item(0).getTextContent())).intValue();
				type = paramElem.getElementsByTagName("type").item(0)
						.getTextContent();
				String value = paramElem.getElementsByTagName("value").item(0)
						.getTextContent();
				// Classes
				if (type.compareTo("Byte") == 0)
				{
					paramClassList
							.add(ordinal, Class.forName("java.lang.Byte"));
				} else if (type.compareTo("Short") == 0)
				{
					paramClassList.add(ordinal, Class
							.forName("java.lang.Short"));
				} else if (type.compareTo("Integer") == 0)
				{
					paramClassList.add(ordinal, Class
							.forName("java.lang.Integer"));
				} else if (type.compareTo("Long") == 0)
				{
					paramClassList
							.add(ordinal, Class.forName("java.lang.Long"));
				} else if (type.compareTo("Float") == 0)
				{
					paramClassList.add(ordinal, Class
							.forName("java.lang.Float"));
				} else if (type.compareTo("Double") == 0)
				{
					paramClassList.add(ordinal, Class
							.forName("java.lang.Double"));
				} else if (type.compareTo("Character") == 0)
				{
					paramClassList.add(ordinal, Class
							.forName("java.lang.Character"));
				} else if (type.compareTo("Boolean") == 0)
				{
					paramClassList.add(ordinal, Class
							.forName("java.lang.Boolean"));
				} else if (type.compareTo("String") == 0)
				{
					paramClassList.add(ordinal, Class
							.forName("java.lang.String"));
				}
				// Primitive types
				else if (type.compareTo("byte") == 0)
				{
					paramClassList.add(ordinal, Byte.TYPE);
				} else if (type.compareTo("short") == 0)
				{
					paramClassList.add(ordinal, Short.TYPE);
				} else if (type.compareTo("int") == 0)
				{
					paramClassList.add(ordinal, Integer.TYPE);
				} else if (type.compareTo("long") == 0)
				{
					paramClassList.add(ordinal, Long.TYPE);
				} else if (type.compareTo("float") == 0)
				{
					paramClassList.add(ordinal, Float.TYPE);
				} else if (type.compareTo("double") == 0)
				{
					paramClassList.add(ordinal, Double.TYPE);
				} else if (type.compareTo("char") == 0)
				{
					paramClassList.add(ordinal, Character.TYPE);
				} else if (type.compareTo("boolean") == 0)
				{
					paramClassList.add(ordinal, Boolean.TYPE);
				}

			}

			classes = new Class[paramClassList.size()];
			for (int z = 0; z < paramClassList.size(); z++)
			{
				classes[z] = (Class) paramClassList.get(z);
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return classes;
	}

	private Object[] enumerateObjectList(Element elem)
	{
		NodeList paramNodeList = elem.getElementsByTagName("param");

		Object object = null;
		String type;
		ArrayList paramObjectList = new ArrayList(0);

		try
		{
			for (int y = 0; y < paramNodeList.getLength(); y++)
			{
				Element paramElem = (Element) paramNodeList.item(y);
				int ordinal = (new Integer(paramElem.getElementsByTagName(
						"ordinal").item(0).getTextContent())).intValue();
				type = paramElem.getElementsByTagName("type").item(0)
						.getTextContent();
				String value = paramElem.getElementsByTagName("value").item(0)
						.getTextContent();

				// Classes
				if (type.compareTo("Byte") == 0)
				{
					paramObjectList.add(ordinal, new Byte(value));
				} else if (type.compareTo("Short") == 0)
				{
					paramObjectList.add(ordinal, new Short(value));
				} else if (type.compareTo("Integer") == 0)
				{
					paramObjectList.add(ordinal, new Integer(value));
				} else if (type.compareTo("Long") == 0)
				{
					paramObjectList.add(ordinal, new Long(value));
				} else if (type.compareTo("Float") == 0)
				{
					paramObjectList.add(ordinal, new Float(value));
				} else if (type.compareTo("Double") == 0)
				{
					paramObjectList.add(ordinal, new Double(value));
				} else if (type.compareTo("Character") == 0)
				{
					paramObjectList
							.add(ordinal, new Character(value.charAt(0)));
				} else if (type.compareTo("Boolean") == 0)
				{
					paramObjectList.add(ordinal, new Boolean(value));
				} else if (type.compareTo("String") == 0)
				{
					paramObjectList.add(ordinal, new String(value));
				}
				// Primitive types
				else if (type.compareTo("byte") == 0)
				{
					paramObjectList.add(ordinal, Byte.parseByte(value));
				} else if (type.compareTo("short") == 0)
				{
					paramObjectList.add(ordinal, Short.parseShort(value));
				} else if (type.compareTo("int") == 0)
				{
					paramObjectList.add(ordinal, Integer.parseInt(value));
					// paramObjectList.add(ordinal, new Integer(value));
				} else if (type.compareTo("long") == 0)
				{
					paramObjectList.add(ordinal, Long.parseLong(value));
				} else if (type.compareTo("float") == 0)
				{
					paramObjectList.add(ordinal, Float.parseFloat(value));
				} else if (type.compareTo("double") == 0)
				{
					paramObjectList.add(ordinal, Double.parseDouble(value));
				} else if (type.compareTo("char") == 0)
				{
					paramObjectList.add(ordinal, value.charAt(0));
				} else if (type.compareTo("boolean") == 0)
				{
					paramObjectList.add(ordinal, Boolean.parseBoolean(value));
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
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
		Document doc=null;
		try
		{
			result = method.invoke(object, methodObjects);
			doc = parseObject(result);
		} catch (Exception e)
		{
			try
			{
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				StringReader sr = new StringReader("<returnValue><error>" + e.getMessage()
													+ "</error></returnValue>");
				InputSource is = new InputSource(sr);

				doc = db.parse(is);
			} catch (ParserConfigurationException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SAXException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
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
				
			} else if (ob instanceof Short)
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
				
			} else if (ob instanceof Integer)
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
				
			} else if (ob instanceof Long)
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
				
			} else if (ob instanceof Float)
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
				
			} else if (ob instanceof Double)
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
				
			} else if (ob instanceof Character)
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
				
			} else if (ob instanceof Boolean)
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
				
			} else if (ob instanceof String)
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
		} catch (Exception e)
		{
			try
			{
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				StringReader sr = new StringReader("<returnValue><error>" + e.getMessage()
													+ "</error></returnValue>");
				InputSource is = new InputSource(sr);

				doc = db.parse(is);
			} catch (ParserConfigurationException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SAXException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return doc;
	}

	// TODO Remove if not needed
	/*public static StringBuilder convertDocToString(Document doc)
	{
		StringBuilder stringBuilder = null;
		try
		{
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			OutputFormat outputformat = new OutputFormat();
			outputformat.setIndent(4);
			outputformat.setIndenting(true);
			outputformat.setPreserveSpace(false);
			XMLSerializer serializer = new XMLSerializer();
			serializer.setOutputFormat(outputformat);
			serializer.setOutputByteStream(stream);
			serializer.asDOMSerializer();
			serializer.serialize(doc.getDocumentElement());

			stringBuilder = new StringBuilder(stream.toString());
		} catch (Exception e)
		{
			e.getMessage();
		}
		return stringBuilder;
	}*/

}
