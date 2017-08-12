package uk.co.mafew.hephaestus.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import uk.co.mafew.format.Convert;

public class HttpHelper
{
	HttpURLConnection httpCon;
	HttpsURLConnection httpsCon;
	URL url;
	String soapRequest;

	/*******************************************************************************/
	/* TODO */
	/*
	 * This is very basic and no where near adequate at the moment. We need to
	 * add the capability to accept xml which would contain all of the arguments
	 * required to construct complex HTTP requests.
	 */
	/*******************************************************************************/

	// #region Main method for testing
	public static void main(String[] args)
	{
		HttpHelper helper = new HttpHelper();
		// System.out.println(Convert.elementToString((Element)
		// helper.httpXhtmlRequestAuthorised("http://jira.infor.com/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml",
		// "jqlQuery=project+%3D+LMTS+AND+status+in+%28Open%2C+%22In+Progress%22%2C+Reopened%29+AND+%22Audit+ID%22+~+%22110918+%22&amp",
		// "GET", "Basic aWRhdGE6dWtzb3Z3aWRhdGE=")));

		// System.out.println(helper.httpRequestSecure("https://owa-emea1.infor.com/ews/exchange.asmx",
		// "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:t=\"http://schemas.microsoft.com/exchange/services/2006/types\"><soap:Header> <t:RequestServerVersion Version=\"Exchange2010\"/></soap:Header><soap:Body><GetMailTips xmlns=\"http://schemas.microsoft.com/exchange/services/2006/messages\"><SendingAs><t:EmailAddress>jon.bailey@infor.com</t:EmailAddress><t:RoutingType>SMTP</t:RoutingType></SendingAs><Recipients><t:Mailbox><t:EmailAddress>jon.bailey@infor.com</t:EmailAddress><t:RoutingType>SMTP</t:RoutingType></t:Mailbox></Recipients> <MailTipsRequested>OutOfOfficeMessage</MailTipsRequested></GetMailTips></soap:Body></soap:Envelope>",
		// "POST"));
		// helper.httpRequestSecureAuthorised("https://api.pushbullet.com/v2/pushes",
		// "{\"type\": \"note\", \"title\": \"Hello Matt\", \"body\": \"Note Body\"}",
		// "POST",
		// "Bearer dTDEZ5rBG3fgwSPJDOTemKX4VplHSlrV"));

		// System.out.println(helper.testMethod());
		System.out
				.println(helper
						.httpRequestAuthorised(
								"http://jira.infor.com/rest/api/2/groupuserpicker",
								"query=Nicole.Rehfeuter@infor.com",
								"GET", "Basic aWRhdGE6dWtzb3Z3aWRhdGE="));
	}

	// #endregion

	// #region HTTP

	// #region Simple Request
	public String httpRequest(String urlString, String queryString, String method)
	{
		String retString = "ERROR";

		try
		{
			if (method.compareTo("GET") == 0)
			{
				urlString = urlString + "?" + queryString;
			}
			URL url = new URL(urlString);
			httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setRequestMethod(method);
			retString = sendRequest(queryString);
		}
		catch (MalformedURLException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (ProtocolException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (IOException e)
		{
			retString = "ERROR: " + e.getMessage();
		}

		return retString;
	}

	public String httpRequestAuthorised(String urlString, String queryString, String method, String authValue)
	{
		String retString = "ERROR";

		try
		{
			if (method.compareTo("GET") == 0)
			{
				urlString = urlString + "?" + queryString;
			}
			URL url = new URL(urlString);
			httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setRequestMethod(method);
			httpCon.setRequestProperty("Authorization", authValue);
			retString = sendRequest(queryString);
		}
		catch (MalformedURLException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (ProtocolException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (IOException e)
		{
			retString = "ERROR: " + e.getMessage();
		}

		return retString;
	}

	public Node httpXhtmlRequestAuthorised(String urlString, String queryString, String method, String authValue)
	{
		String retString = "ERROR";
		Document returnDoc = null;
		Document xhtmlDoc = null;

		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<xhtml></xhtml>");
			InputSource is = new InputSource(sr);
			returnDoc = db.parse(is);

			if (method.compareTo("GET") == 0)
			{
				urlString = urlString + "?" + queryString;
			}
			URL url = new URL(urlString);
			httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setRequestMethod(method);
			httpCon.setRequestProperty("Authorization", authValue);
			retString = sendRequest(queryString);

			sr = new StringReader(retString);
			is = new InputSource(sr);
			xhtmlDoc = db.parse(is);
			Node tempNode = returnDoc.importNode(xhtmlDoc.getDocumentElement(), true);
			returnDoc.getElementsByTagName("xhtml").item(0).appendChild(tempNode);
		}
		catch (MalformedURLException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (ProtocolException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (IOException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (Exception e)
		{
			retString = escapeXmlReservedCharacters(e.getMessage());
			
			Node node = returnDoc.createElement("error");
			node.setTextContent(retString);
			returnDoc.getElementsByTagName("xhtml").item(0).appendChild(node);
		}

		return returnDoc.getDocumentElement();
	}

	// #endregion

	// #region save response directly to a file
	public String httpToFileAuthorised(String urlString, String queryString, String method, String file,
			String authValue)
	{
		String retString = "ERROR";
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			retString = httpRequestAuthorised(urlString, queryString, method, authValue);
			if (!(retString.startsWith("ERROR")))
			{
				writer.write(retString);
				writer.close();
				retString = "SUCCESS";
			}

		}
		catch (IOException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		return retString;
	}

	// #endregion

	private String sendRequest(String queryString)
	{
		String retString = "";
		try
		{
			if (httpCon.getRequestMethod().compareTo("GET") != 0)
			{
				httpCon.setRequestProperty("Content-Type", "application/json");
				httpCon.setDoOutput(true);
				OutputStream os = httpCon.getOutputStream();
				os.write(queryString.getBytes());
				os.flush();
			}

			if (httpCon.getResponseCode() < 200 || httpCon.getResponseCode() > 299)
			{
				retString = "Error : HTTP code : " + httpCon.getResponseCode();
			}
			else
			{
				BufferedReader br = new BufferedReader(new InputStreamReader((httpCon.getInputStream())));

				String output;

				while ((output = br.readLine()) != null)
				{
					retString += output;
				}

			}

		}
		catch (Exception e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		finally
		{
			httpCon.disconnect();
		}
		return retString;
	}

	// #endregion

	// #region HTTPS

	// #region Simple Request

	@Deprecated
	public String httpRequestSecure(String urlString, String queryString, String method)
	{
		String retString = "ERROR";

		try
		{
			if (method.compareTo("GET") == 0)
			{
				urlString = urlString + "?" + queryString;
			}
			URL url = new URL(urlString);
			httpsCon = (HttpsURLConnection) url.openConnection();
			httpsCon.setRequestMethod(method);
			retString = sendSecureRequest(queryString, "application/json");
		}
		catch (MalformedURLException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (ProtocolException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (IOException e)
		{
			retString = "ERROR: " + e.getMessage();
		}

		return retString;
	}

	public String httpRequestSecure(String urlString, String queryString, String method, String contentType)
	{
		String retString = "ERROR";

		try
		{
			if (method.compareTo("GET") == 0)
			{
				urlString = urlString + "?" + queryString;
			}
			URL url = new URL(urlString);
			httpsCon = (HttpsURLConnection) url.openConnection();
			httpsCon.setRequestMethod(method);
			retString = sendSecureRequest(queryString, contentType);
		}
		catch (MalformedURLException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (ProtocolException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (IOException e)
		{
			retString = "ERROR: " + e.getMessage();
		}

		return retString;
	}

	@Deprecated
	public String httpRequestSecureAuthorised(String urlString, String queryString, String method, String authValue)
	{
		String retString = "ERROR";

		try
		{
			if (method.compareTo("GET") == 0)
			{
				urlString = urlString + "?" + queryString;
			}
			URL url = new URL(urlString);
			httpsCon = (HttpsURLConnection) url.openConnection();
			httpsCon.setRequestMethod(method);
			httpsCon.setRequestProperty("Authorization", authValue);
			retString = sendSecureRequest(queryString, "application/json");
		}
		catch (MalformedURLException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (ProtocolException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (IOException e)
		{
			retString = "ERROR: " + e.getMessage();
		}

		return retString;
	}

	public Node webServiceSecureAuthorised(String urlString, String queryString, String method, String authValue,
			String contentType)
	{
		Node returnNode = null;
		Document doc = null;

		try
		{
			Reader xmlReader = new StringReader(removeXmlStringNamespaceAndPreamble(httpRequestSecureAuthorised(urlString, queryString, method, authValue, contentType)));
			InputSource is = new InputSource(xmlReader);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(is);
			returnNode = doc.getDocumentElement();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return returnNode;
	}

	public String httpRequestSecureAuthorised(String urlString, String queryString, String method, String authValue,
			String contentType)
	{
		String retString = "ERROR";

		try
		{
			if (method.compareTo("GET") == 0)
			{
				urlString = urlString + "?" + queryString;
			}
			URL url = new URL(urlString);
			httpsCon = (HttpsURLConnection) url.openConnection();
			httpsCon.setRequestMethod(method);
			httpsCon.setRequestProperty("Authorization", authValue);
			retString = sendSecureRequest(queryString, contentType);
		}
		catch (MalformedURLException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (ProtocolException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (IOException e)
		{
			retString = "ERROR: " + e.getMessage();
		}

		return retString;
	}

	// #endregion

	// #region save response directly to a file

	public String httpToFileSecure(String urlString, String queryString, String method, String file)
	{
		String retString = "ERROR";
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			retString = httpRequestSecure(urlString, queryString, method);
			if (!(retString.startsWith("ERROR")))
			{
				writer.write(retString);
				writer.close();
				retString = "SUCCESS";
			}

		}
		catch (IOException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		return retString;
	}

	public String httpToFileSecureAuthorised(String urlString, String queryString, String method, String file,
			String authValue)
	{
		String retString = "ERROR";
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			retString = httpRequestSecureAuthorised(urlString, queryString, method, authValue);
			if (!(retString.startsWith("ERROR")))
			{
				writer.write(retString);
				writer.close();
				retString = "SUCCESS";
			}

		}
		catch (IOException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		return retString;
	}

	// #endregion

	private String sendSecureRequest(String queryString, String contentType)
	{
		String retString = "";
		try
		{
			if (httpsCon.getRequestMethod().compareTo("GET") != 0)
			{
				httpsCon.setRequestProperty("Content-Type", contentType);
				httpsCon.setDoOutput(true);
				OutputStream os = httpsCon.getOutputStream();
				os.write(queryString.getBytes());
				os.flush();
			}
			if (httpsCon.getResponseCode() < 200 || httpsCon.getResponseCode() > 299)
			{
				retString = "Error : HTTP code : " + httpsCon.getResponseCode();
			}
			else
			{
				BufferedReader br = new BufferedReader(new InputStreamReader((httpsCon.getInputStream())));

				String output;

				while ((output = br.readLine()) != null)
				{
					retString += output;
				}

			}

			httpsCon.disconnect();

		}
		catch (Exception e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		finally
		{
			httpsCon.disconnect();
		}
		return retString;
	}

	// #endregion
	
	/*******************************************************************************/
	/* TODO */
	//Pinched this, so will have to write my own
	/*******************************************************************************/
	private String removeXmlStringNamespaceAndPreamble(String xmlString) {
		  return xmlString.replaceAll("(<\\?[^<]*\\?>)?", ""). /* remove preamble */
		  replaceAll("xmlns.*?(\"|\').*?(\"|\')", "") /* remove xmlns declaration */
		  .replaceAll("(<)(\\w+:)(.*?>)", "$1$3") /* remove opening tag prefix */
		  .replaceAll("(</)(\\w+:)(.*?>)", "$1$3"); /* remove closing tags prefix */
		}
	
	private String escapeXmlReservedCharacters(String str)
	{
		str = str.replaceAll("&", "&amp;");
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll(">", "&gt;");
		return str;
	}
}
