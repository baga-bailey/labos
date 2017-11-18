

 


package uk.co.mafew.khronos.holidays.uk;

import java.io.PrintStream;
import java.util.Calendar;
import uk.co.mafew.khronos.CompareWith;

// Referenced classes of package uk.co.mafew.khronos.holidays.uk:
//            NewYearsDay

public class NewYearsDayPublicHoliday implements CompareWith
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

	public NewYearsDayPublicHoliday()
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
		Calendar cal = NewYearsDay.get(calendar);
		if (cal.get(7) == 7)
			cal.add(5, 2);
		else if (cal.get(7) == 1)
			cal.add(5, 1);
		return cal;
	}

	public static void main(String args[])
	{
		NewYearsDayPublicHoliday es = new NewYearsDayPublicHoliday();
		Calendar calendar = Calendar.getInstance();
		calendar.set(5, 2);
		calendar.set(2, 0);
		calendar.set(1, 2005);
		System.out.println(get(calendar).getTime());
		System.out.println(es.compareWith(calendar));
	}
}
