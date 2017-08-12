package uk.co.mafew.hephaestus;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.Startup;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import org.w3c.dom.Document;
import uk.co.mafew.format.Convert;
import uk.co.mafew.logging.*;

@Startup
/**
 * Message-Driven Bean implementation class for: QueueMonitor
 *
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "/queue/ProcessQueue") })
public class QueueMonitor implements MessageListener
{

	Logger logger;

	/**
	 * Default constructor.
	 */
	public QueueMonitor()
	{
		logger = new Logger(this.getClass().getName());

	}

	@PostConstruct
	private void initialise()
	{

	}

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	@Override
	public void onMessage(final Message message)
	{
		Document doc = null;
		try
		{
			if (message instanceof TextMessage)
			{
				logger.log.info("TextMessage received at " + new Date());
				TextMessage msg = (TextMessage) message;
				// System.out.println("Message is : " + msg.getText());
				String messageAsString = msg.getText();
				logger.log.debug("*****************Message received*******************");
				logger.log.debug(messageAsString);

				doc = Convert.createMessageDoc(msg.getText());
				ProcessMessage procMess = new ProcessMessage();
				procMess.process(doc);
			}
			else if (message instanceof ObjectMessage)
			{
				System.out.println("ObjectMessage at " + new Date());
			}
			else
			{
				System.out.println("Not a valid message for this Queue MDB");
			}

		}
		catch (JMSException e)
		{
			e.printStackTrace();
		}
	}
}
