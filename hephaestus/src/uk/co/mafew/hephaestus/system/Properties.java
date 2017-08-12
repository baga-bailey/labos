package uk.co.mafew.hephaestus.system;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.w3c.dom.Node;

import uk.co.mafew.format.Convert;

public class Properties {

	public static void main(String[] args) {
		Properties p = new Properties();

		//System.out.println(Convert.elementToString(
		//		(Element) p.directoryListing("\\\\UKFAVWLMOIDATA\\internal\\exceptions", ".*zip.*.xml$")));
		
		System.out.println(p.setLastModifiedTime("C:\\Users\\jbailey1\\Documents\\uranus\\files\\iDataBatch\\ValidityBU.csv", "2007-10-21 02:32:45", "yyyy-MM-dd HH:mm:ss"));
		
		//System.out.println(Convert.elementToString(
			//			(Element)p.directoryListingVerbose("C:\\Users\\jbailey1\\Documents")));

	}

	public Node file(String path) {
		Node result = null;
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<properties></properties>");
			InputSource is = new InputSource(sr);
			doc = db.parse(is);

			Path file = Paths.get(path);
			BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);

			Node node = doc.createElement("creationTime");
			node.setTextContent(attr.creationTime().toString());
			doc.getElementsByTagName("properties").item(0).appendChild(node);

			node = doc.createElement("lastAccessTime");
			node.setTextContent(attr.lastAccessTime().toString());
			doc.getElementsByTagName("properties").item(0).appendChild(node);

			node = doc.createElement("lastModifiedTime");
			node.setTextContent(attr.lastModifiedTime().toString());
			doc.getElementsByTagName("properties").item(0).appendChild(node);

			node = doc.createElement("isDirectory");
			node.setTextContent(new Boolean(attr.isDirectory()).toString());
			doc.getElementsByTagName("properties").item(0).appendChild(node);

			node = doc.createElement("isOther");
			node.setTextContent(new Boolean(attr.isOther()).toString());
			doc.getElementsByTagName("properties").item(0).appendChild(node);

			node = doc.createElement("isRegularFile");
			node.setTextContent(new Boolean(attr.isRegularFile()).toString());
			doc.getElementsByTagName("properties").item(0).appendChild(node);

			node = doc.createElement("isSymbolicLink");
			node.setTextContent(new Boolean(attr.isSymbolicLink()).toString());
			doc.getElementsByTagName("properties").item(0).appendChild(node);

			node = doc.createElement("size");
			node.setTextContent(new Long(attr.size()).toString());
			doc.getElementsByTagName("properties").item(0).appendChild(node);

		} catch (Exception e) {
			String error = e.getMessage();
			error = error.replaceAll("&", "&amp;");
			error = error.replaceAll("<", "&lt;");
			error = error.replaceAll(">", "&gt;");
			Node node = doc.createElement("error");
			node.setTextContent(error);
			doc.getElementsByTagName("properties").item(0).appendChild(node);
		}

		result = doc.getElementsByTagName("properties").item(0);
		return result;

	}

	public String setLastAccessTime(String file, String date1,String date1Format) {
		String returnValue = "ERROR";
		try {
			Path path = Paths.get(file);
			SimpleDateFormat formatter = new SimpleDateFormat(date1Format);
			Date date = formatter.parse(date1);
			FileTime fileTime = FileTime.fromMillis(date.getTime());
			Files.setAttribute(path, "lastAccessTime", fileTime);
			returnValue = "Success";
		} catch (Exception e) {
			returnValue = e.getMessage();
		}
		return returnValue;
	}
	
	public String setLastModifiedTime(String file, String date1,String date1Format) {
		String returnValue = "ERROR";
		try {
			Path path = Paths.get(file);
			SimpleDateFormat formatter = new SimpleDateFormat(date1Format);
			Date date = formatter.parse(date1);
			FileTime fileTime = FileTime.fromMillis(date.getTime());
			Files.setAttribute(path, "lastModifiedTime", fileTime);
			returnValue = "Success";
		} catch (Exception e) {
			returnValue = e.getMessage();
		}
		return returnValue;
	}
	
	public Node directoryListingVerbose(String path) {
		Node result = null;
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<properties></properties>");
			InputSource is = new InputSource(sr);
			doc = db.parse(is);

			File folder = new File(path);

			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					Element fileElement = doc.createElement("file");
					Node node = doc.createElement("filename");
					node.setTextContent(listOfFiles[i].getName());
					fileElement.appendChild(node);
					Node tempNode = doc.importNode(file(listOfFiles[i].getPath()), true);
					fileElement.appendChild(tempNode);
					doc.getElementsByTagName("properties").item(0).appendChild(fileElement);
				} else if (listOfFiles[i].isDirectory()) {
					Node node = doc.createElement("directoryName");
					node.setTextContent(listOfFiles[i].getName());
					doc.getElementsByTagName("properties").item(0).appendChild(node);
				}
			}

		} catch (Exception e) {
			String error = e.getMessage();
			error = error.replaceAll("&", "&amp;");
			error = error.replaceAll("<", "&lt;");
			error = error.replaceAll(">", "&gt;");
			Node node = doc.createElement("error");
			node.setTextContent(error);
			doc.getElementsByTagName("properties").item(0).appendChild(node);
		}

		result = doc.getElementsByTagName("properties").item(0);
		return result;
	}

	public Node directoryListing(String path) {
		Node result = null;
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<properties></properties>");
			InputSource is = new InputSource(sr);
			doc = db.parse(is);

			File folder = new File(path);

			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					Node node = doc.createElement("filename");
					node.setTextContent(listOfFiles[i].getName());
					doc.getElementsByTagName("properties").item(0).appendChild(node);
				} else if (listOfFiles[i].isDirectory()) {
					Node node = doc.createElement("directoryName");
					node.setTextContent(listOfFiles[i].getName());
					doc.getElementsByTagName("properties").item(0).appendChild(node);
				}
			}

		} catch (Exception e) {
			String error = e.getMessage();
			error = error.replaceAll("&", "&amp;");
			error = error.replaceAll("<", "&lt;");
			error = error.replaceAll(">", "&gt;");
			Node node = doc.createElement("error");
			node.setTextContent(error);
			doc.getElementsByTagName("properties").item(0).appendChild(node);
		}

		result = doc.getElementsByTagName("properties").item(0);
		return result;
	}

	public Node directoryListing(String path, String regex) {
		Node result = null;
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<properties></properties>");
			InputSource is = new InputSource(sr);
			doc = db.parse(is);

			File folder = new File(path);

			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				String filename = listOfFiles[i].getName();
				if (isMatch(regex, filename)) {
					if (listOfFiles[i].isFile()) {
						Node node = doc.createElement("filename");
						node.setTextContent(filename);
						doc.getElementsByTagName("properties").item(0).appendChild(node);
					} else if (listOfFiles[i].isDirectory()) {
						Node node = doc.createElement("directoryName");
						node.setTextContent(listOfFiles[i].getName());
						doc.getElementsByTagName("properties").item(0).appendChild(node);
					}
				}
			}

		} catch (Exception e) {
			String error = e.getMessage();
			error = error.replaceAll("&", "&amp;");
			error = error.replaceAll("<", "&lt;");
			error = error.replaceAll(">", "&gt;");
			Node node = doc.createElement("error");
			node.setTextContent(error);
			doc.getElementsByTagName("properties").item(0).appendChild(node);
		}

		result = doc.getElementsByTagName("properties").item(0);
		return result;
	}

	private boolean isMatch(String regex, String targetStr) {

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(targetStr);

		boolean found = false;
		while (matcher.find()) {
			found = true;

		}

		return found;
	}

}
