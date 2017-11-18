

 


package uk.co.mafew.khronos.holidays.uk;

import java.io.PrintStream;
import java.util.Calendar;
import uk.co.mafew.khronos.CompareWith;

// Referenced classes of package uk.co.mafew.khronos.holidays.uk:
//            EasterSunday

public class GoodFriday implements CompareWith
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

	public GoodFriday()
	{
		
	}

	public boolean compareWith(Calendar calendar)
	{
		Calendar goodFriday = get(calendar);
		return goodFriday.get(2) == calendar.get(2)
				&& goodFriday.get(5) == calendar.get(5);
	}

	public static Calendar get(Calendar calendar)
	{
		EasterSunday es = new EasterSunday();
		Calendar goodFriday = EasterSunday.get((Calendar) calendar.clone());
		goodFriday.add(7, -2);
		return goodFriday;
	}

	public static void main(String args[])
	{
		GoodFriday es = new GoodFriday();
		Calendar calendar = Calendar.getInstance();
		calendar.set(5, 26);
		calendar.set(2, 2);
		calendar.set(1, 2006);
		System.out.println(get(calendar).getTime());
		System.out.println(es.compareWith(calendar));
	}
}
