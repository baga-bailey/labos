package uk.co.mafew.format;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;


public class Convert {
	
	public static void main(String[] args) {
		try {
			byte[] bytes = compressString("Hello", "UTF-8");
			System.out.print(decompressString(bytes, "UTF-8"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String docToString(Document doc) {
		String returnString = "ERROR";
		StringWriter sw = new StringWriter();
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			DOMSource source = new DOMSource(doc);
			StreamResult r = new StreamResult(sw);

			transformer.transform(source, r);
			returnString = sw.toString();
		} catch (TransformerConfigurationException e) {
			returnString = e.getMessageAndLocation();
			System.out.println(returnString);
		} catch (TransformerFactoryConfigurationError e) {
			returnString = "ERROR: " + e.getMessage();
			System.out.println(returnString);
		} catch (TransformerException e) {
			returnString = "ERROR: " + e.getMessageAndLocation();
			System.out.println(returnString);
		} catch (Exception e) {
			returnString = "ERROR: " + e.getMessage();
			System.out.println(e.getStackTrace());
		} finally {
			try {
				sw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return returnString;
	}

	public static String docToStringNoFromat(Document doc) {
		String returnString = "ERROR";
		StringWriter sw = new StringWriter();
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult r = new StreamResult(sw);

			transformer.transform(source, r);
			returnString = sw.toString();
		} catch (TransformerConfigurationException e) {
			returnString = e.getMessageAndLocation();
			System.out.println(returnString);
		} catch (TransformerFactoryConfigurationError e) {
			returnString = "ERROR: " + e.getMessage();
			System.out.println(returnString);
		} catch (TransformerException e) {
			returnString = "ERROR: " + e.getMessageAndLocation();
			System.out.println(returnString);
		} catch (Exception e) {
			returnString = "ERROR: " + e.getMessage();
			System.out.println(e.getStackTrace());
		} finally {
			try {
				sw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return returnString;
	}

	public static String elementToString(Element elem) {
		StringWriter sw = new StringWriter();
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(elem);
			StreamResult r = new StreamResult(sw);
			transformer.transform(source, r);
			// System.out.println(sw.toString());
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sw.toString();
	}

	public static Document createMessageDoc(String message) {
		Document doc = null;

		try {
			Reader xmlReader = new StringReader(message);
			InputSource is = new InputSource(xmlReader);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(is);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}

	public static byte[] compressString(String str, String encoding) throws Exception {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            OutputStream out = new DeflaterOutputStream(baos);
            out.write(str.getBytes(encoding));
            out.close();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return baos.toByteArray();
	}
	
	public static String decompressString(byte[] bytes, String encoding) throws Exception {
		String returnString = "Error";
		
		 InputStream in = new InflaterInputStream(new ByteArrayInputStream(bytes));
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        try {
	            byte[] buffer = new byte[8192];
	            int len;
	            while((len = in.read(buffer))>0)
	                baos.write(buffer, 0, len);
	            return new String(baos.toByteArray(), encoding);
	        } catch (IOException e) {
	            throw new AssertionError(e);
	        }
     }

}
