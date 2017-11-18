package uk.co.mafew.messaging;

import java.util.Properties;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import uk.co.mafew.format.Convert;
import uk.co.mafew.logging.Logger;

public class MessageWriter {
	public Properties props;
	QueueConnection cnn = null;
	QueueSender sender = null;
	QueueSession session = null;
	private String queueName = "test";
	private String message = null;
	private Logger logger;
	
	
	//@Resource(lookup = "jms/ConnectionFactory")private static ConnectionFactory connectionFactory;
	//@Resource(lookup = "/queue/ProcessQueue")private static Queue queue;
	@Resource(lookup = "")private static ConnectionFactory connectionFactory;
	@Resource(lookup = "")private static Queue queue;

	public static void main(String[] args) {
		MessageWriter mw = new MessageWriter("/queue/ProcessQueue");
		try {
			mw.writeMessage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public MessageWriter() {
		logger = new Logger(this.getClass().getName());
		props = new Properties();
	}

	public MessageWriter(String queueName) {
		logger = new Logger(this.getClass().getName());
		props = new Properties();
		this.queueName = queueName;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void writeMessage() throws Exception {
		
		InitialContext ctx = null;
		Destination dest = null;
		try {
			ctx = new InitialContext();
			queue = (Queue) ctx.lookup(queueName);
			dest = (Destination) queue; 
		 	} 
		catch (Exception e) {
		    System.err.println("Error setting destination: " + e.toString()); 
		    e.printStackTrace(); 
		    System.exit(1);
		}
		
		Connection connection = null;
		try {
			// lookup the queue connection factory
		    QueueConnectionFactory connectionFactory = (QueueConnectionFactory) ctx.lookup("java:/ConnectionFactory");
		    
			connection = connectionFactory.createConnection(); 
			Session session = connection.createSession(
			            false, 
			            Session.AUTO_ACKNOWLEDGE);

			//Creates a MessageProducer and a TextMessage:

			MessageProducer producer = session.createProducer(dest);
			javax.jms.TextMessage txtMessage = session.createTextMessage();

			//Sends one or more messages to the destination:

			for (int i = 0; i < 1; i++) { 
				txtMessage.setText(message); 
				String messageAsString = txtMessage.getText();
				
				logger.log.debug("*******************Message Type**********************");
				logger.log.debug(txtMessage.getClass().toString());
				logger.log.debug("******************Sending message********************");
				logger.log.debug(messageAsString);
				
			    producer.send(txtMessage);
			}

			//Sends an empty control message to indicate the end of the message stream:

			//producer.send(session.createMessage());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		 finally
		 {
		    if (connection != null) { 
		        try { connection.close(); } 
		        catch (JMSException e) { } 
		    }
		    
		}
	}
}
