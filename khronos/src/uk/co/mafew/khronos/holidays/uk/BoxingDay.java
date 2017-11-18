package uk.co.mafew.khronos.holidays.uk;

import java.util.Calendar;
import uk.co.mafew.khronos.CompareWith;

public class BoxingDay implements CompareWith
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

	public BoxingDay()
	{
		
	}

	public boolean compareWith(Calendar calendar)
	{
		return calendar.get(2) == 11 && calendar.get(5) == 26;
	}

	public static Calendar get(Calendar calendar)
	{
		Calendar cal = (Calendar) calendar.clone();
		cal.set(2, 11);
		cal.set(5, 26);
		return cal;
	}

	public static void main(String args[])
	{
		BoxingDay es = new BoxingDay();
		Calendar calendar = Calendar.getInstance();
		calendar.set(5, 27);
		calendar.set(2, 11);
		calendar.set(1, 2006);
		System.out.println(get(calendar).getTime());
		System.out.println(es.compareWith(calendar));
	}
}
