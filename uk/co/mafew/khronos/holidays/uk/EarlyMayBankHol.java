

 


package uk.co.mafew.khronos.holidays.uk;

import java.io.PrintStream;
import java.util.Calendar;
import uk.co.mafew.khronos.CompareWith;

public class EarlyMayBankHol implements CompareWith
{
	private String type = "is";

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public EarlyMayBankHol()
	{
		
	}

	public boolean compareWith(Calendar calendar)
	{
		Calendar cal = get(calendar);
		int day = calendar.get(5);
		int month = calendar.get(2);
		System.out.println(day + " and " + cal.get(5));
		return day == cal.get(5) && month == cal.get(2);
	}

	public static Calendar get(Calendar calendar)
	{
		Calendar cal = Calendar.getInstance();
		cal.set(5, 1);
		cal.set(2, 4);
		cal.set(1, calendar.get(1));
		for (int i = 0; i < 8; i++)
		{
			if (cal.get(7) == 2)
				return cal;
			cal.add(5, 1);
		}

		return cal;
	}

	public static void main(String args[])
	{
		EarlyMayBankHol es = new EarlyMayBankHol();
		Calendar calendar = Calendar.getInstance();
		calendar.set(5, 7);
		calendar.set(2, 4);
		calendar.set(1, 2007);
		System.out.println(es.compareWith(calendar));
	}
}
