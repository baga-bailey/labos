

 


package uk.co.mafew.khronos.holidays.uk.engWalesNI;

import java.util.Calendar;
import uk.co.mafew.khronos.CompareWith;

public class SummerPublicHoliday implements CompareWith
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

	public SummerPublicHoliday()
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
		cal.set(2, 7);
		cal.set(5, 31);
		cal.set(1, calendar.get(1));
		for (int i = 0; i < 8; i++)
		{
			if (cal.get(7) == 2)
				return cal;
			cal.add(5, -1);
		}

		return cal;
	}

	public static void main(String args[])
	{
		SummerPublicHoliday es = new SummerPublicHoliday();
		Calendar calendar = Calendar.getInstance();
		calendar.set(5, 28);
		calendar.set(2, 7);
		calendar.set(1, 2008);
		System.out.println(get(calendar).getTime());
		System.out.println(es.compareWith(calendar));
	}
}
