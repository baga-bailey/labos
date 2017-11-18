package uk.co.mafew.khronos.holidays.uk.ni;

import java.util.Calendar;
import uk.co.mafew.khronos.CompareWith;

public class SaintPatricksDay implements CompareWith
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

	public SaintPatricksDay()
	{
		System.out.println("SaintPatricksDay instantiated");
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
		Calendar cal = (Calendar) calendar.clone();
		cal.set(2, 2);
		cal.set(5, 14);
		return cal;
	}

	public static void main(String args[])
	{
		SaintPatricksDay es = new SaintPatricksDay();
		Calendar calendar = Calendar.getInstance();
		calendar.set(5, 13);
		calendar.set(2, 2);
		calendar.set(1, 2007);
		System.out.println(get(calendar).getTime());
		System.out.println(es.compareWith(calendar));
	}
}
