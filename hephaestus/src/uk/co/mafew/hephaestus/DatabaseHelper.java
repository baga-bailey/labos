package uk.co.mafew.hephaestus;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import uk.co.mafew.logging.*;

public class DatabaseHelper
{
	// #region Testing
	static String DATASOURCE_CONTEXT = "java:jboss/datasources/mySQL/idata";
	static String DB_CONN_STRING = "jdbc:sqlserver://usalvwaldbpp1:1433;databaseName=es";
	static String DRIVER_CLASS_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	static String USER_NAME = "LMO";
	static String PASSWORD = "LM0@3";
	
	public static void main(String[] args)
	{
		DatabaseHelper dbHelper = new DatabaseHelper(DB_CONN_STRING,DRIVER_CLASS_NAME,USER_NAME,PASSWORD);
		dbHelper.executeQuery("SELECT [audit_id] FROM [es].[es].[g_lmo_audit_customer_answer] question_id = 3 and audit_id = '00122587' ;");

	}
	// #endregion

	Connection con;
	Logger logger;

	// #region gettersAndSetters

	public String getDATASOURCE_CONTEXT()
	{
		return DATASOURCE_CONTEXT;
	}

	public void setDATASOURCE_CONTEXT(String dATASOURCE_CONTEXT)
	{
		DATASOURCE_CONTEXT = dATASOURCE_CONTEXT;
	}

	public String getDB_CONN_STRING()
	{
		return DB_CONN_STRING;
	}

	public void setDB_CONN_STRING(String dB_CONN_STRING)
	{
		DB_CONN_STRING = dB_CONN_STRING;
	}

	public String getDRIVER_CLASS_NAME()
	{
		return DRIVER_CLASS_NAME;
	}

	public void setDRIVER_CLASS_NAME(String dRIVER_CLASS_NAME)
	{
		DRIVER_CLASS_NAME = dRIVER_CLASS_NAME;
	}

	public String getUSER_NAME()
	{
		return USER_NAME;
	}

	public void setUSER_NAME(String uSER_NAME)
	{
		USER_NAME = uSER_NAME;
	}

	public String getPASSWORD()
	{
		return PASSWORD;
	}

	public void setPASSWORD(String pASSWORD)
	{
		PASSWORD = pASSWORD;
	}

	// #endregion

	// #region constructors
	public DatabaseHelper()
	{
		con = getSimpleConnection();
	}

	public DatabaseHelper(String jndiName)
	{
		logger = new Logger(this.getClass().getName());
		setDATASOURCE_CONTEXT(jndiName);
		con = getJNDIConnection();
	}

	public DatabaseHelper(String connectionString, String driverClass, String username, String password)
	{
		logger = new Logger(this.getClass().getName());
		setDB_CONN_STRING(connectionString);
		setDRIVER_CLASS_NAME(driverClass);
		setUSER_NAME(username);
		setPASSWORD(password);
		con = getSimpleConnection();
	}

	// #endregion

	// #region Connections
	private Connection getJNDIConnection()
	{

		Connection result = null;
		try
		{
			Context initialContext = new InitialContext();

			DataSource datasource = (DataSource) initialContext.lookup(DATASOURCE_CONTEXT);
			if (datasource != null)
			{
				result = datasource.getConnection();
			}
			else
			{
				logger.log.error("ERROR - unable to look up DataSource");
			}
		}
		catch (NamingException e)
		{
			logger.log.error("ERROR: " + e);
		}
		catch (SQLException e)
		{
			logger.log.error("ERROR: " + e);
		}
		return result;
	}

	private Connection getSimpleConnection()
	{

		Connection result = null;
		try
		{
			Class.forName(DRIVER_CLASS_NAME).newInstance();
			result = DriverManager.getConnection(DB_CONN_STRING, USER_NAME, PASSWORD);
		}
		catch (SQLException e)
		{
			logger.log.error("ERROR: Error connecting with connection string; '" + DB_CONN_STRING + "'");
			logger.log.error(e.getMessage());
		}
		catch (Exception e)
		{
			logger.log.error(e.getMessage());
		}
		return result;
	}

	// #endregion

	// #region public methods

	public String executeUpdate(String sql)
	{
		String retString = "ERROR";
		Statement stmt = null;
		try
		{
			int retValue;
			stmt = con.createStatement();
			retValue = stmt.executeUpdate(sql);
			if (retValue == 0)
			{
				retString = "SUCCESS";
			}
			else
			{
				retString = "SUCCESS: " + retValue + " rows affected";
			}
		}
		catch (SQLException e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		catch (Exception e)
		{
			retString = "ERROR: " + e.getMessage();
		}
		finally
		{
			try
			{
				con.close();
			}
			catch (SQLException e)
			{
			}
		}

		return retString;
	}
	
	public String executePreparedStatementUpdate(String sql, Object[] objects)
	{
		String retString = "ERROR";
		int retValue = 0;
		try
		{
			PreparedStatement preparedStmt = con.prepareStatement(sql);
			for(int i = 0; i<objects.length;i++)
			{
				if (objects[i] == null) {
					preparedStmt.setNull(i+1, Integer.MIN_VALUE); 
			    } else {
			    	preparedStmt.setObject(i+1, objects[i]);
			    }
			}
			logger.log.debug("Executing prepared statement -- " + sql);
			retValue = preparedStmt.executeUpdate();
			logger.log.debug("prepared statement completed and returned the -- " + retValue);
			if (retValue == 0)
			{
				retString = "SUCCESS";
			}
			else
			{
				retString = "SUCCESS: " + retValue + " rows affected";
			}
		}
		
		catch (Exception e)
		{
			logger.log.error("ERROR: " + e.getMessage());
			retString = "ERROR: " + e.getMessage();
		}
		finally
		{
			try
			{
				con.close();
			}
			catch (SQLException e)
			{
			}
		}

		return retString;
	}

	public Node executeQuery(String sql)
	{
		Statement stmt = null;
		ResultSet rs = null;

		Node result = null;
		Document doc = null;
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<resultSet></resultSet>");
			InputSource is = new InputSource(sr);
			doc = db.parse(is);

			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next())
			{
				Node rowNode = doc.createElement("row");
				doc.getElementsByTagName("resultSet").item(0).appendChild(rowNode);

				for (int i = 1; i < rsmd.getColumnCount() + 1; i++)
				{
					String column = rsmd.getColumnName(i);
					Node columnNode = doc.createElement(column);
					columnNode.setTextContent(rs.getString(rsmd.getColumnName(i)));
					rowNode.appendChild(columnNode);
				}
			}
		}

		catch (Exception e)
		{
			String error = e.getMessage();
			error = error.replaceAll("&", "&amp;");
			error = error.replaceAll("<", "&lt;");
			error = error.replaceAll(">", "&gt;");
			Node node = doc.createElement("error");
			node.setTextContent(error);
			doc.getElementsByTagName("resultSet").item(0).appendChild(node);
		}
		finally
		{
			try
			{
				con.close();
			}
			catch (Exception e)
			{
			}
		}

		result = doc.getElementsByTagName("resultSet").item(0);
		return result;
	}

	// #endregion

	

}
