package uk.co.mafew.messaging.email;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailHelper
{

	public static void main(String[] args)
	{
		EmailHelper eh = new EmailHelper();
		System.out.println(eh.sendSimpleHtmlMail("jon.bailey@infor.com", "jon.bailey@infor.com", "test subject",
				"<i>test body</i>", "mail.infor.com"));
	}

	public boolean sendSimpleHtmlMail(String to, String from, String subject, String body, String host)
	{
		try
		{
			sendSimpleMail(to, from, subject, body, "", host, true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean sendSimpleHtmlMail(String to, String from, String subject, String body, String cc, String host)
	{
		try
		{
			sendSimpleMail(to, from, subject, body, cc, host, true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean sendSimpleMail(String to, String from, String subject, String body, String host)
	{
		try
		{
			sendSimpleMail(to, from, subject, body, "", host, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean sendSimpleMail(String to, String from, String subject, String body, String cc, String host)
	{
		try
		{
			sendSimpleMail(to, from, subject, body, cc, host, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean sendSimpleMail(String to, String from, String subject, String body, String cc, String host,
			boolean isHtml)
	{
		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", host);

		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties);

		try
		{
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			String[] toAddresseStrings = to.split(";");

			for (int i = 0; i < toAddresseStrings.length; i++)
			{
				// Set To: header field of the header.
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddresseStrings[i]));
			}

			// Set Subject: header field
			message.setSubject(subject);

			// Determine if the message needs to be formatted as HTML
			if (isHtml)
			{
				message.setContent(body, "text/html; charset=utf-8");
			}
			else
			{
				message.setText(body);
			}

			// Add CC'd addresses
			toAddresseStrings = cc.split(";");

			for (int i = 0; i < toAddresseStrings.length; i++)
			{
				String toAdd = toAddresseStrings[i];
				// Set To: header field of the header.
				if (toAdd.compareTo("") != 0)
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(toAdd));
			}

			// Send message
			Transport.send(message);
		}
		catch (MessagingException mex)
		{
			mex.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean sendMailWithAttachment(String to, String from, String subject, String body, String attachment,
			String host)
	{
		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", host);

		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties);

		try
		{
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			String[] toAddresseStrings = to.split(";");

			for (int i = 0; i < toAddresseStrings.length; i++)
			{
				// Set To: header field of the header.
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddresseStrings[i]));
			}

			// Set Subject: header field
			message.setSubject(subject);

			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();

			// Now set the actual message
			messageBodyPart.setText(body);

			Multipart multipart = new MimeMultipart();

			// Set text message part
			multipart.addBodyPart(messageBodyPart);

			Path p = Paths.get(attachment);
			String file = p.getFileName().toString();

			DataSource source = new FileDataSource(attachment);
			messageBodyPart = new MimeBodyPart();
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(file);
			multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart);

			// Send message
			Transport.send(message);
		}
		catch (MessagingException mex)
		{
			mex.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean sendMailWithAttachment(String to, String from, String subject, String body, String attachment,
			String cc, String host)
	{
		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", host);

		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties);

		try
		{
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			String[] toAddresseStrings = to.split(";");

			for (int i = 0; i < toAddresseStrings.length; i++)
			{
				// Set To: header field of the header.
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddresseStrings[i]));
			}

			// Set Subject: header field
			message.setSubject(subject);

			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();

			// Now set the actual message
			messageBodyPart.setText(body);

			Multipart multipart = new MimeMultipart();

			// Set text message part
			multipart.addBodyPart(messageBodyPart);

			Path p = Paths.get(attachment);
			String file = p.getFileName().toString();

			DataSource source = new FileDataSource(attachment);
			messageBodyPart = new MimeBodyPart();
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(file);
			multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart);

			// Add CC'd addresses
			toAddresseStrings = cc.split(";");

			for (int i = 0; i < toAddresseStrings.length; i++)
			{
				String toAdd = toAddresseStrings[i];
				// Set To: header field of the header.
				if (toAdd.compareTo("") != 0)
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(toAdd));
			}

			// Send message
			Transport.send(message);
		}
		catch (MessagingException mex)
		{
			mex.printStackTrace();
			return false;
		}
		return true;
	}

}
