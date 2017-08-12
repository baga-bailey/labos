package uk.co.mafew.hephaestus.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import uk.co.mafew.format.Convert;

public class CmdHelper
{
	
	public static void main(String args[])
	{
		CmdHelper ch = new CmdHelper();
		Node returnNode = ch.runCommand("C:\\Users\\jbailey1\\Documents\\Analyse54\\analyse54.exe " + 
				"C:\\Users\\jbailey1\\Documents\\Analyse54\\sourceFiles\\Analyse54.properties", true);
		System.out.println(Convert.elementToString((Element)returnNode));
		
		//ch.runCommand("C:\\Users\\jbailey1\\Documents\\temp\\test.bat", false);
		
		//C:\Users\jbailey1\Documents\temp\test.bat
	}
	
	public Node runCommand(String command)
	{
		return runCommand(command,false);
	}
	
	public Node runCommand(String command, boolean waitFor)
	{
		BufferedReader in = null;
		BufferedReader errStream = null;
		Node cmdResult = null;
		Document doc = null;
		String outputLine = "";
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<cmdResult></cmdResult>");
			InputSource is = new InputSource(sr);

			doc = db.parse(is);

			Runtime r = Runtime.getRuntime();
			Process p = r.exec(command);
			int code = 0;
			if(waitFor)
			{
				code = p.waitFor();
				Node node = doc.createElement("returnCode");
				node.setTextContent(Integer.toString(code));
				doc.getElementsByTagName("cmdResult").item(0).appendChild(node);
			}

			in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			errStream = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			while ((outputLine = in.readLine()) != null)
			{
				outputLine = outputLine.replaceAll("&", "&amp;");
				outputLine = outputLine.replaceAll("<", "&lt;");
				outputLine = outputLine.replaceAll(">", "&gt;");
				Node node = doc.createElement("line");
				node.setTextContent(outputLine);
				doc.getElementsByTagName("cmdResult").item(0).appendChild(node);
			}
			
			while ((outputLine = errStream.readLine()) != null)
			{
				outputLine = outputLine.replaceAll("&", "&amp;");
				outputLine = outputLine.replaceAll("<", "&lt;");
				outputLine = outputLine.replaceAll(">", "&gt;");
				Node node = doc.createElement("error");
				node.setTextContent(outputLine);
				doc.getElementsByTagName("cmdResult").item(0).appendChild(node);
			}
			
		}
		catch (Exception e)
		{
			outputLine = e.getMessage();
			outputLine = outputLine.replaceAll("&", "&amp;");
			outputLine = outputLine.replaceAll("<", "&lt;");
			outputLine = outputLine.replaceAll(">", "&gt;");
			Node node = doc.createElement("error");
			node.setTextContent(outputLine);
			doc.getElementsByTagName("cmdResult").item(0).appendChild(node);
		}
		finally
		{
			try
			{
				in.close();
				errStream.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		cmdResult = doc.getElementsByTagName("cmdResult").item(0);
		return cmdResult;
	}
}
