

 


package uk.co.mafew.khronos.holidays.uk.engWalesNI;

import java.util.Calendar;
import uk.co.mafew.khronos.CompareWith;
import uk.co.mafew.khronos.holidays.uk.EasterSunday;

public class EasterMonday implements CompareWith
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

	public EasterMonday()
	{
		
	}

	public boolean compareWith(Calendar calendar)
	{
		Calendar easterMonday = get(calendar);
		int day = calendar.get(5);
		int month = calendar.get(2);
		return day == easterMonday.get(5) && month == easterMonday.get(2);
	}

	public static Calendar get(Calendar calendar)
	{
		Calendar es = EasterSunday.get(calendar);
		es.add(5, 1);
		return es;
	}

	public static void main(String args[])
	{
		EasterMonday em = new EasterMonday();
		Calendar calendar = Calendar.getInstance();
		calendar.set(5, 9);
		calendar.set(2, 3);
		calendar.set(1, 2012);
		System.out.println(get(calendar).getTime());
		System.out.println(em.compareWith(calendar));
	}
}
