package uk.co.mafew.khronos;

import java.io.IOException;
import java.util.Calendar;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

// Referenced classes of package uk.co.mafew.khronos:
//            Iterator, ScheduledEvent

public class TimeTable
{

	public TimeTable()
	{
	}

	public static void main(String args[])
	{
		try
		{
			String file = "file:///C:/Users/jbailey1/workspace/khronos/src/xml/TimeTable.xml";
			Calendar cal[] =
			{ Calendar.getInstance() };
			Iterator it = new Iterator();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			NodeList list = doc.getElementsByTagName("ScheduledEvent");
			for (int i = 0; i < list.getLength(); i++)
			{
				ScheduledEvent myTimeTable = new ScheduledEvent();
				myTimeTable.start((Element) list.item(i));
			}

		} catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		} catch (SAXException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
