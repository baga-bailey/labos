package uk.co.mafew.messaging;

import java.util.Properties;

import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import org.w3c.dom.Document;

import uk.co.mafew.format.Convert;
import uk.co.mafew.logging.Logger;

public class GeneralMessageSender
{
	Logger logger;
	String queueName="";
	
	public GeneralMessageSender(String queueName)
	{
		logger = new Logger(this.getClass().getName());
		this.queueName=queueName;
	}
	
	public void sendMessage(TextMessage msg)
	{
		QueueConnection cnn = null;
		QueueSender sender = null;
		QueueSession session = null;
		InitialContext ctx;
		try
		{
			Properties props = new Properties();
			props.setProperty("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
			props.setProperty("java.naming.factory.url.pkgs","org.jboss.naming");
			props.setProperty("java.naming.provider.url", "jnp://127.0.0.1:1090");

			ctx = new InitialContext(props);
			Queue queue = (Queue) ctx.lookup("queueName");
			QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup("ConnectionFactory");
			cnn = factory.createQueueConnection();
			session = cnn.createQueueSession(false,	QueueSession.AUTO_ACKNOWLEDGE);
			sender = session.createSender(queue);
			sender.send(msg);
			sender.close();
			logger.log.info("TextMessage sent successfully to remote queue.");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String message)
	{
		MessageWriter mw = new MessageWriter(queueName);
		mw.setMessage(message);
		mw.props.setProperty("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
		mw.props.setProperty("java.naming.factory.url.pkgs", "org.jboss.naming");
		mw.props.setProperty("java.naming.provider.url", "127.0.0.1:1090");
		try
		{
			mw.writeMessage();
			logger.log.info("String Message sent successfully to remote queue.");
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void sendMessage(Document doc)
	{
		//String message = docToString(doc);
		String message = Convert.docToString(doc);
		sendMessage(message);
	}
	
	
	public static void main(String[] args)
	{
		GeneralMessageSender gsm = new GeneralMessageSender("/queue/ProcessQueue");
		gsm.sendMessage("hello");
		//gsm.test();
	}
}
