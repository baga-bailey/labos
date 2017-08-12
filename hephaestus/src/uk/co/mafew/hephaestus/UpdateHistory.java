package uk.co.mafew.hephaestus;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import javax.naming.InitialContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import uk.co.mafew.file.Config;
import uk.co.mafew.file.xml.HistoryFragment;
import uk.co.mafew.file.xml.XmlHelper;
import uk.co.mafew.format.Convert;
import uk.co.mafew.logging.*;

public class UpdateHistory {

	Logger logger;
	// FileLock lock;
	String COMPLETED_PROCESS_DIRECTORY;
	String PROCESS_HISTORY_DOCUMENT;
	String PERSISTENCE;

	/**
	 * Default constructor.
	 */
	public UpdateHistory() {
		logger = new Logger(this.getClass().getName());
		loadConfig();
	}

	public void update(Document doc) {
		FileLock lock = null;
		FileChannel fChannel = null;
		RandomAccessFile histFile = null;

		try {
			// String historyDocument = COMPLETED_PROCESS_DIRECTORY +
			// File.separator + PROCESS_HISTORY_DOCUMENT;
			XPathFactory factory = XPathFactory.newInstance();
			XPath xPath = factory.newXPath();

			// Update variables
			String uuidValue = (String) xPath.compile("/*[1]/@uuid").evaluate(doc, XPathConstants.STRING);
			String id = (String) xPath.compile("/*[1]/@id").evaluate(doc, XPathConstants.STRING);
			String friendlyName = (String) xPath.compile("/*[1]/@friendlyName").evaluate(doc, XPathConstants.STRING);
			String startDate = (String) xPath.compile("//startDate").evaluate(doc, XPathConstants.STRING);
			String startTime = (String) xPath.compile("//startTime").evaluate(doc, XPathConstants.STRING);
			String status = (String) xPath.compile("//status").evaluate(doc, XPathConstants.STRING);
			String endDate = (String) xPath.compile("//endDate").evaluate(doc, XPathConstants.STRING);
			String endTime = (String) xPath.compile("//endTime").evaluate(doc, XPathConstants.STRING);
			String processId = (String) xPath.compile("//processID").evaluate(doc, XPathConstants.STRING);
			String processName = (String) xPath.compile("//processName").evaluate(doc, XPathConstants.STRING);
			String fragment = Convert.docToString(doc);

			logger.log.debug("****Update History****");
			logger.log.debug(fragment);
			logger.log.debug("uuidValue = " + uuidValue);
			logger.log.debug("id = " + id);
			logger.log.debug("friendlyName = " + friendlyName);
			logger.log.debug("startDate = " + startDate);
			logger.log.debug("startTime = " + startTime);
			logger.log.debug("status = " + status);
			logger.log.debug("endDate = " + endDate);
			logger.log.debug("endTime = " + endTime);
			logger.log.debug("processId = " + processId);
			logger.log.debug("processName = " + processName);

			if (PERSISTENCE.compareToIgnoreCase("database") == 0) {

				if (status.compareTo("started") == 0) {
					DatabaseHelper dbh = new DatabaseHelper("java:jboss/datasources/ExampleDS");

					String sqlString = "INSERT INTO PROCESS (UUID,STARTTIME,STATUS) " + "VALUES ('" + uuidValue + "','"
							+ startDate + " " + startTime + "','" + status + "')";

					logger.log.debug("Updating database with '" + sqlString + "'");
					String retString = dbh.executeUpdate(sqlString);
					if (retString.contains("ERROR")) {
						logger.log.error(retString);
					} else {
						logger.log.debug(retString);
					}
				} else if (status.compareTo("running") == 0) {
					byte[] bytes = Convert.compressString(fragment, "UTF-8");
					DatabaseHelper dbh = new DatabaseHelper("java:jboss/datasources/ExampleDS");

					String sqlString = "INSERT INTO STEP (ID, UUID,FRIENDLYNAME, STARTTIME, ENDTIME, FRAGMENT) "
							+ "VALUES (?,?,?,?,?,?);";

					Object[] objects = new Object[6];
					objects[0] = id;
					objects[1] = uuidValue;
					objects[2] = friendlyName;
					objects[3] = java.sql.Timestamp.valueOf(startDate + " " + startTime);
					objects[4] = java.sql.Timestamp.valueOf(endDate + " " + endTime);
					objects[5] = new ByteArrayInputStream(bytes);

					// '" + id + "','" + uuidValue + "','" + friendlyName +
					// "','" + startDate + " " + startTime + "','" + endDate + "
					// " + endTime + "','" + Convert.compressString(fragment,
					// "UTF-8") + "'
					logger.log.debug("Updating database with '" + sqlString + "'");
					String retString = dbh.executePreparedStatementUpdate(sqlString, objects);

					if (retString.contains("ERROR")) {
						logger.log.error(sqlString + " exited with");
						logger.log.error(retString);
					} else {
						logger.log.debug(sqlString + " exited with");
						logger.log.debug(retString);

						dbh = new DatabaseHelper("java:jboss/datasources/ExampleDS");
						sqlString = "UPDATE PROCESS SET STATUS = '" + status + "', ID='" + processId + "', NAME='"
								+ processName + "' " + "WHERE UUID = '" + uuidValue + "'";

						logger.log.debug("Updating database with '" + sqlString + "'");
						retString = dbh.executeUpdate(sqlString);
						if (retString.contains("ERROR")) {
							logger.log.error(retString);
						} else {
							logger.log.debug(retString);
						}
					}

				} else if (status.compareTo("completed") == 0) {
					DatabaseHelper dbh = new DatabaseHelper("java:jboss/datasources/ExampleDS");

					String sqlString = "UPDATE PROCESS SET STATUS = '" + status + "', ENDTIME='" + endDate + " "
							+ endTime + "' " + "WHERE UUID = '" + uuidValue + "'";

					logger.log.debug("Updating database with '" + sqlString + "'");
					String retString = dbh.executeUpdate(sqlString);
					if (retString.contains("ERROR")) {
						logger.log.error(retString);
					} else {
						logger.log.debug(retString);
					}
				}
			} else if (PERSISTENCE.compareToIgnoreCase("file") == 0) {
				logger.log.debug("************INCOMING HISTORY FRAGMENT*****************");
				logger.log.debug(Convert.docToString(doc));

				// Get the current history document
				File file = new File(COMPLETED_PROCESS_DIRECTORY, PROCESS_HISTORY_DOCUMENT);
				histFile = new RandomAccessFile(file, "rw");
				fChannel = histFile.getChannel();

				boolean isLocked = false;
				while (!isLocked) {
					try {
						lock = fChannel.tryLock();
						isLocked = true;
					} catch (Exception e) {
						Thread.sleep(500);
					}

				}

				long fileSize = fChannel.size();
				ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
				fChannel.read(buffer);
				buffer.flip();
				String histDocAsString = new String(buffer.array(), Charset.forName("UTF-8"));
				Document histDocument = Convert.createMessageDoc(histDocAsString);

				// Document histDocument = loadFile(historyDocument);
				logger.log.debug("************INCOMING HISTORY DOCUMENT*****************");
				logger.log.debug(Convert.docToString(histDocument));

				NodeList nodeList;
				String statusUpdate;

				// Get the uuid in the incoming 'update' message
				uuidValue = (String) xPath.compile("/*[1]/@uuid").evaluate(doc, XPathConstants.STRING);

				// Test whether that uuid exists in the history document
				// Get all elements that have the same uuid as the incoming
				// 'update'
				// document
				String uuidXpath = "//processes/process[@uuid='" + uuidValue + "']";
				logger.log.debug("************THIS IS XPATH BEING USED*****************");
				logger.log.debug(uuidXpath);

				nodeList = (NodeList) xPath.evaluate(uuidXpath, histDocument.getDocumentElement(),
						XPathConstants.NODESET);
				if (nodeList != null)
					logger.log.debug("************NODE COUNT IS " + nodeList.getLength() + "******************");

				// If it doesn't exist, create a new base element into which
				// we'll
				// insert subsequent fragments
				// Else, update the current entry
				if (nodeList == null || nodeList.getLength() < 1) {
					logger.log.debug("************CREATING NEW PROCESS ENTRY************");

					// Get the root node to insert into the history document
					Element element = doc.getDocumentElement();

					// import the node so that it can be inserted
					Node node = histDocument.importNode(element, true);

					// Get the root element of the history doc
					Element histRoot = histDocument.getDocumentElement();

					histRoot.appendChild(node);

					XmlHelper xhHelper = new XmlHelper();
					// xhHelper.saveDocToFile(histDocument,
					// COMPLETED_PROCESS_DIRECTORY, PROCESS_HISTORY_DOCUMENT);
					Charset charset = Charset.forName("UTF-8");
					CharsetEncoder encoder = charset.newEncoder();
					String historyDocument = Convert.docToStringNoFromat(histDocument);
					buffer = encoder.encode(CharBuffer.wrap(historyDocument));
					fChannel.position(0);
					fChannel.write(buffer);
					fChannel.force(false);

				}
				// Check to make sure there's only one process with the uuid
				else if (nodeList.getLength() == 1) {
					logger.log.debug("************APPENDING TO PROCESS ENTRY************");

					// From the history document, get the element to be updated
					Element processElement = (Element) nodeList.item(0);

					// Get the status in the incoming 'update' message
					statusUpdate = (String) xPath.evaluate("//status/text()", doc.getDocumentElement(),
							XPathConstants.STRING);

					Element statusElement = (Element) doc.getElementsByTagName("status").item(0);
					statusElement.getParentNode().removeChild(statusElement);

					// Update the status
					processElement.getElementsByTagName("status").item(0).setTextContent(statusUpdate);

					if (statusUpdate.compareTo("completed") == 0) {
						// Get process end date and time
						String endDateUpdate = (String) xPath.evaluate("//endDate/text()", doc.getDocumentElement(),
								XPathConstants.STRING);

						String endTimeUpdate = (String) xPath.evaluate("//endTime/text()", doc.getDocumentElement(),
								XPathConstants.STRING);

						// Update the end date and time
						processElement.getElementsByTagName("endDate").item(0).setTextContent(endDateUpdate);

						processElement.getElementsByTagName("endTime").item(0).setTextContent(endTimeUpdate);
					} else {
						// Get the processId in the incoming 'update' message
						processId = (String) xPath.evaluate("//processID/text()", doc.getDocumentElement(),
								XPathConstants.STRING);
						// Get the processName in the incoming 'update' message
						processName = (String) xPath.evaluate("//processName/text()", doc.getDocumentElement(),
								XPathConstants.STRING);

						processElement.getAttributes().getNamedItem("id").setTextContent(processId);

						processElement.getAttributes().getNamedItem("name").setTextContent(processName);

						Element processIdElement = (Element) doc.getElementsByTagName("processID").item(0);
						processIdElement.getParentNode().removeChild(processIdElement);

						Element processNameElement = (Element) doc.getElementsByTagName("processName").item(0);
						processNameElement.getParentNode().removeChild(processNameElement);

						// Get the root node to insert into the history document
						Element element = doc.getDocumentElement();

						// import the node so that it can be inserted
						Node node = histDocument.importNode(element, true);

						// Append the update to the process node
						processElement.appendChild(node);

					}

					XmlHelper xhHelper = new XmlHelper();
					Charset charset = Charset.forName("UTF-8");
					CharsetEncoder encoder = charset.newEncoder();
					String historyDocument = Convert.docToStringNoFromat(histDocument);
					buffer = encoder.encode(CharBuffer.wrap(historyDocument));
					fChannel.position(0);
					fChannel.write(buffer);
					fChannel.force(false);
					// xhHelper.saveDocToFile(histDocument,
					// COMPLETED_PROCESS_DIRECTORY, PROCESS_HISTORY_DOCUMENT);
					logger.log.debug("History document successfully updated");

				} else {
					logger.log.error("ERROR: There are multiple processes where id= " + uuidValue);
				}
			}
		} catch (XPathExpressionException e) {
			logger.log.error(e.getMessage());
		} catch (Exception e) {
			logger.log.error(e.getMessage());
		} finally {
			try {
				// fChannel.close();
				if (lock != null && lock.isValid())
					lock.release();
				if (histFile != null)
					histFile.close();
			} catch (IOException e) {
				logger.log.debug(e.getMessage());
			}
		}
	}

	public Document loadFile(String fileName) {
		Document doc = null;
		FileInputStream fis = null;
		RandomAccessFile file = null; // The file we'll lock
		FileChannel channel = null; // The channel to the file
		try {
			file = new RandomAccessFile(fileName, "rw");
			channel = file.getChannel();

			boolean isLocked = false;
			while (!isLocked) {
				try {
					// lock = channel.tryLock();
					isLocked = true;
				} catch (Exception e) {
					logger.log.debug("Cannot obtain lock on 'ProcessHistory.xml'. Going to sleep before trying again");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}

			fis = new FileInputStream(fileName);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(fis);

			System.out.println("***History file found***");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				file.close();
			} catch (IOException e) {
			}
		}

		return doc;
	}

	private void loadConfig() {
		try {
			logger.log.debug("***Attempting to load messageQueuePauseTime config from EJB***");
			InitialContext ctx = new InitialContext();
			GeneralConfig gc = (GeneralConfig) ctx.lookup("java:module/GeneralConfig");

			logger.log.debug("***Attempting to load nextStepDocument config from EJB***");
			PROCESS_HISTORY_DOCUMENT = gc.getConfigValue("processHistory");

			logger.log.debug("***Attempting to load process output directory config from EJB***");
			COMPLETED_PROCESS_DIRECTORY = gc.getConfigValue("completedProcessDir");

			logger.log.debug("***Attempting to load method of persistence from EJB***");
			PERSISTENCE = gc.getConfigValue("persistence");

			logger.log.info("***Message processing config loaded from EJB***");

		} catch (Exception e1) {
			logger.log.warn("****Error loading messageQueuePauseTime config from EJB***");

			try {
				logger.log.debug("***Attempting to load messageQueuePauseTime config from file***");
				Config cfg = new Config();
				cfg.loadFile("config.xml");

				logger.log.debug("***Attempting to load nextStepDocument config from File***");
				PROCESS_HISTORY_DOCUMENT = cfg.getConfigValue("processHistory");

				logger.log.debug("***Attempting to load process output directory config from EJB***");
				COMPLETED_PROCESS_DIRECTORY = cfg.getConfigValue("completedProcessDir");

				logger.log.debug("***Attempting to load method of persistence from EJB***");
				PERSISTENCE = cfg.getConfigValue("persistence");

				logger.log.info("***Message processing config loaded from file***");
			} catch (Exception e) {
				logger.log.error("****Error loading messageQueuePauseTime config from file***");
			}
		}
	}

	public static void main(String[] args) {
		UpdateHistory uh = new UpdateHistory();

		HistoryFragment hf = new HistoryFragment();
		hf.createStepFragment();
		hf.setUuid("302e4f5d-79be-47f1-9199-8ca9485f3183");
		hf.setStartDate("startDateValue");
		hf.setStartTime("startTimeValue");
		hf.setId("stepId");
		hf.setFriendlyName("friendlyName");
		hf.setEndDate("endDateValue");
		hf.setEndTime("endTimeValue");

		// Meta elements
		hf.setStatus("running");
		hf.setProcessId("processId");
		hf.setProcessName("processName");

		System.out.println("********History Document********");
		Convert.docToString(hf.getDoc());

		uh.update(hf.getDoc());
	}

}
