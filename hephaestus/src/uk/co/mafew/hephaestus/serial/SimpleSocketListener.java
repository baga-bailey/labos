package uk.co.mafew.hephaestus.serial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.ejb.Stateless;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import uk.co.mafew.format.Convert;
import uk.co.mafew.hephaestus.StartProcess;
import uk.co.mafew.logging.Logger;
import uk.co.mafew.narcissus.ReflectedObject;

@Stateless
public class SimpleSocketListener extends Thread
{

	private BufferedReader br = null;
	private ArrayList<Pair> pairList = new ArrayList<SimpleSocketListener.Pair>();

	Logger logger;

	public static void main(String[] args)
	{
		// SimpleSocketListener listener = new SimpleSocketListener();
		// listener.connect("192.168.1.20", "10001");
		// listener.run();
	}

	public SimpleSocketListener()
	{

	}

	public SimpleSocketListener(String ip, String port)
	{
		logger = new Logger(this.getClass().getName());
		connect(ip, Integer.parseInt(port));
	}

	public void run()
	{
		String s;
		try
		{
			while ((s = br.readLine()) != null)
			{
				// Loop through the arrayList Pair.keys looking for regex's that
				// match 's'
				// When we find one, execute the method in Pair.value
				Iterator<Pair> pairIterator = pairList.iterator();
				while (pairIterator.hasNext())
				{
					Pair pair = pairIterator.next();
					String key = pair.getKey();
					logger.log.debug("Message received - " + s);
					if (s.matches(key))
					{
						logger.log.debug("Match found for message - " + pair.getKey() + " - " + pair.getValue());
						try
						{
							//StartProcess sp = new StartProcess();
							//sp.run(pair.getValue());
							XPathFactory factory = XPathFactory.newInstance();
							XPath xPath = factory.newXPath();
							
							TransformerFactory tfactory = TransformerFactory.newInstance();
							Transformer tx = tfactory.newTransformer();
							DOMSource source = new DOMSource(pair.getFilter());
							DOMResult result = new DOMResult();
							tx.transform(source, result);
							Element tempFilter = ((Document) result.getNode()).getDocumentElement();
														
							Element targetClass = (Element) tempFilter.getElementsByTagName("TargetClass").item(0);
							Element headerElement = (Element) xPath.evaluate("constructor/params/param/value/header[1]", targetClass, XPathConstants.NODE);
							Element messageElement = headerElement.getOwnerDocument().createElement("message");
							Node messageText = headerElement.getOwnerDocument().createTextNode(s);
							messageElement.appendChild(messageText);
							headerElement.appendChild(messageElement);
							
							String testString  = Convert.elementToString(targetClass);

							ReflectedObject ro = new ReflectedObject(targetClass, "");
							ro.invoke();
							//callReflectedObject cro = new callReflectedObject(tempTargetClass);
							//cro.run();
							
						}
						catch (Exception e)
						{
							logger.log.error("Error : " + e.getMessage());
						}
					}
				}

			}
		}
		catch (IOException e)
		{
			logger.log.error("Error : " + e.getMessage());
		}
	}

	public void connect(String ip, int port)
	{
		Socket mSocket = null;
		InetAddress ipa = null;

		// reset all of our variables
		mSocket = null;
		try
		{
			ipa = InetAddress.getByName(ip);
		}
		catch (UnknownHostException e)
		{
			logger.log.error("Error : " + e.getMessage());
			return;
		}
		try
		{ // Open the socket
			mSocket = new Socket(ipa.getHostAddress(), port);
		}
		catch (IOException e)
		{
			logger.log.error("Error : " + e.getMessage());
			logger.log.error("Error opening socket");
			return;
		}

		try
		{
			// Create an input stream
			br = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
		}
		catch (Exception e)
		{
			logger.log.error("Error : " + e.getMessage());
			logger.log.error("Error creating input stream");
		}
	}

	public void addMessagePair(String key, String value)
	{
		Pair pair = new Pair();
		pair.setKey(key);
		pair.setValue(value);
		pairList.add(pair);
	}
	
	public void addMessagePair(String key, Element value)
	{
		Pair pair = new Pair();
		pair.setKey(key);
		pair.setFilter(value);
		pairList.add(pair);
	}

	private class Pair
	{
		String key;
		String value;
		private Element filter;

		public String getKey()
		{
			return key;
		}

		public void setKey(String key)
		{
			this.key = key;
		}

		public String getValue()
		{
			return value;
		}

		public void setValue(String value)
		{
			this.value = value;
		}

		public Element getFilter()
		{
			return filter;
		}

		public void setFilter(Element filter)
		{
			this.filter = filter;
		}
		
		
	}

}
