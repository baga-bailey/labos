

 


package uk.co.mafew.khronos;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import uk.co.mafew.logging.Logger;

public class HttpWebServiceRequest
{

	Logger logger;
	
	public HttpWebServiceRequest(String sURL)
	{
		logger = new Logger(this.getClass().getName());
		try
		{
			URL url = new URL(sURL);
			httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setConnectTimeout(15000);
			httpCon.setReadTimeout(15000);
			httpCon.setAllowUserInteraction(false);
			httpCon.setDoOutput(true);
			httpCon.setDoInput(true);
			httpCon.setRequestMethod("POST");
			setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			setRequestProperty("Content-Language", "en");
		} catch (IOException e)
		{
			logger.log.error(e.getMessage());
		}
	}

	public OutputStream getOutputStream()
	{
		try
		{
			postStream = httpCon.getOutputStream();
		} catch (IOException e)
		{
			logger.log.error(e.getMessage());
			System.out.println(e.getMessage());
		}
		return postStream;
	}

	public void setRequestProperty(String propertyName, String propertyValue)
	{
		httpCon.setRequestProperty(propertyName, propertyValue);
	}

	public void setSoapRequest(String soapRequest)
	{
		this.soapRequest = soapRequest;
	}

	public String postMessage()
	{
		StringBuffer responseMessage = null;
		try
		{
			prnWriter = new PrintWriter(postStream);
			prnWriter.println(soapRequest);
			prnWriter.flush();
			prnWriter.close();
			int responseCode = httpCon.getResponseCode();
			String responseText = httpCon.getResponseMessage();
			System.out.println("Post response: " + responseText);
			InputStreamReader inputStreamReader = new InputStreamReader(httpCon.getInputStream());
			BufferedReader reader = new BufferedReader(inputStreamReader);
			responseMessage = new StringBuffer("");
			String inputLine;
			while ((inputLine = reader.readLine()) != null)
				responseMessage.append(inputLine);
			inputStreamReader.close();
			reader.close();
		} catch (IOException e)
		{
			logger.log.error(e.getMessage());
			return e.getMessage();
		}
		return responseMessage.toString();
	}

	public static void main(String args[])
	{
		HttpWebServiceRequest hwsr = new HttpWebServiceRequest("http://www.webservicex.net/whois.asmx");
		hwsr.setRequestProperty("SOAPAction", "http://www.webservicex.net/GetWhoIS");
		hwsr.setRequestProperty("Host","www.webservicex.ne");
		hwsr.setRequestProperty("Content-Type","text/xml; charset=utf-8");
		hwsr.setRequestProperty("Content-Length","length");
		hwsr.getOutputStream();
		//hwsr.setSoapRequest("<?xml version='1.0' encoding='utf-8'?> <soap:Envelope xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:xsd='http://www.w3.org/2001/XMLSchema' \txmlns:soap='http://schemas.xmlsoap.org/soap/envelope/'> 	<soap:Body> 		<ExecuteDtsSqlAck xmlns='http://www.vertex.co.uk/DtsService/ExecuteDTS'> 			<sPackageName>Y:\\lmcdev\\DTS\\LMCDuplicateActiveLoanCheck.dts</sPackageName> 			<dtsPassword></dtsPassword> 			<guidID>testGUID</guidID> 			<chainVar>No</chainVar> 			<writeToLog>true</writeToLog> 			<logLocation>c:\\temp\\dtslogs</logLocation> 			<connString>conString</connString> 		</ExecuteDtsSqlAck> 	</soap:Body> </soap:Envelope>");
		hwsr.setSoapRequest("<?xml version='1.0' encoding='utf-8'?>" +
							"<soap:Envelope xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns:xsd='http://www.w3.org/2001/XMLSchema' xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/'>" +
								"<soap:Body>" +
									"<GetWhoIS xmlns='http://www.webservicex.net'>" +
									 	"<HostName>mafew.co.uk</HostName>" +
									"</GetWhoIS>" +
								"</soap:Body>" +
							"</soap:Envelope>"); 
		String str = hwsr.postMessage();
		System.out.println(hwsr.postMessage());
	}

	private int responseCode;
	private OutputStream postStream;
	private PrintWriter prnWriter;
	private InputStreamReader inputStreamReader;
	HttpURLConnection httpCon;
	String soapRequest;
}
