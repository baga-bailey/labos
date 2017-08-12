package uk.co.mafew.hephaestus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class JsonClientHelper {
	
	HttpURLConnection httpCon;
	String soapRequest;

	public JsonClientHelper()
	{

	}

	public static void main(String args[])
	{
		JsonClientHelper test = new JsonClientHelper();
		System.out.println(test.downloadJsonToFile(
				"http://jira.infor.com/rest/api/2/search?jql=project+%3D+LMTS+AND+issuetype+in+%28%22Change+Request%22%2C+Project%29+AND+status+in+%28Open%2C+%22In+Progress%22%2C+Reopened%29+ORDER+BY+updated+DESC%2C+cf%5B10190%5D+ASC%2C+due+DESC",
				"C:\\Users\\jbailey1\\Documents\\LMTS workload\\Jira Reports\\Automation\\json\\CRandProj.js"));
		System.out.println(test.downloadJsonToFile(
				"http://jira.infor.com/rest/api/2/search?jql=project+%3D+LMTS+AND+status+%21%3D+Resolved+AND+status+%21%3D+Closed+AND+type+in+%28%22Technical+Question%22%29+ORDER+BY+created+DESC%2c+updated+ASC%2c+key+DESC",
				"C:\\Users\\jbailey1\\Documents\\LMTS workload\\Jira Reports\\Automation\\json\\Parent.js"));
		System.out.println(test.downloadJsonToFile(
				"http://jira.infor.com/rest/api/2/search?jql=project+%3D+LMTS+AND+status+%21%3DResolved+AND+status+%21%3D+Closed+and+parent%3D+%22LMTS-8780%22+ORDER+BY+key+DESC",
				"C:\\Users\\jbailey1\\Documents\\LMTS workload\\Jira Reports\\Automation\\json\\Queued.js"));

	}

	public String downloadJsonToFile(String urlString, String file)
	{
		BufferedWriter writer = null;
		String retString="ERROR";
		try
		{
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", "Basic SkJhaWxleTE6RmViQGluZm9y");
													 
			if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299)
			{
				retString = "ERROR : HTTP code : " + conn.getResponseCode();
			}
			else
			{
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				String output;
				String jsonString = "";
				
				while ((output = br.readLine()) != null)
				{
					jsonString += output;
				}

				conn.disconnect();

				writer = new BufferedWriter(new FileWriter(file));
				writer.write(jsonString);
				writer.close();
				
				retString = "SUCCESS : HTTP code : " + conn.getResponseCode();
			}

		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			retString="ERROR - Malformed URL Exception";
		}
		catch (ProtocolException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			retString="ERROR - Protocol Exception";
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			retString="ERROR - IO Exception";
		}
		finally
		{
			try
			{
				if (writer != null)
					writer.close();
			}
			catch (IOException e)
			{
			}
		}
		
		return retString;
	}

}
