

 


package uk.co.mafew.khronos.holidays.uk.scot;

import java.io.PrintStream;
import java.util.Calendar;
import uk.co.mafew.khronos.CompareWith;

// Referenced classes of package uk.co.mafew.khronos.holidays.uk.scot:
//            SecondJanuary

public class SecondJanuaryPublicHoliday implements CompareWith
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

	public SecondJanuaryPublicHoliday()
	{
		System.out.println("SecondJanuaryPublicHoliday instantiated");
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
		Calendar cal = SecondJanuary.get(calendar);
		if (cal.get(7) == 7 || cal.get(7) == 1)
			cal.add(5, 2);
		return cal;
	}

	public static void main(String args[])
	{
		SecondJanuaryPublicHoliday es = new SecondJanuaryPublicHoliday();
		Calendar calendar = Calendar.getInstance();
		calendar.set(5, 2);
		calendar.set(2, 0);
		calendar.set(1, 2003);
		System.out.println(get(calendar).getTime());
		System.out.println(es.compareWith(calendar));
	}
}
