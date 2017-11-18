

package uk.co.mafew.khronos.holidays.uk;

import java.io.PrintStream;
import java.util.Calendar;
import uk.co.mafew.khronos.CompareWith;

public class EasterSunday implements CompareWith
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

	public EasterSunday()
	{
		
	}

	public boolean compareWith(Calendar calendar)
	{
		Calendar easterSunday = get(calendar);
		int day = calendar.get(5);
		int month = calendar.get(2);
		int easterDay = easterSunday.get(5);
		int easterMonth = easterSunday.get(2);
		return day == easterDay && month == easterMonth;
	}

	private static int getGoldenNumber(int year)
	{
		return year % 19;
	}

	private static int getCenturyNumber(int year)
	{
		return year / 100 + 1;
	}

	private static int getNonLeapDayCorrection(int centuryNumber)
	{
		int temp = (centuryNumber * 3) / 4;
		return temp - 12;
	}

	private static int getFullMoonCorrection(int centuryNumber)
	{
		return (centuryNumber * 8 + 5) / 25;
	}

	private static int getProvisionalEpact(int goldenNo,
			int nonLeapDayCorrection, int fullMoonCorrection)
	{
		return (((goldenNo * 11 + 2) - nonLeapDayCorrection) + fullMoonCorrection) % 30;
	}

	private static int getCorrectedEpact(int provisionalEpact, int goldenNo)
	{
		if (provisionalEpact == 0)
			return 1;
		if (provisionalEpact == 1 && goldenNo > 10)
			return 2;
		else
			return provisionalEpact;
	}

	private static int getSundayShift(int century, int nonLeapDayCorrection,
			int provisionalEpact)
	{
		int temp = (century * 5) / 4;
		return ((temp + 5) - nonLeapDayCorrection - provisionalEpact) % 7;
	}

	private static int getFinalEpact(int sundayShift, int provisionalEpact)
	{
		return provisionalEpact + sundayShift;
	}

	public static Calendar get(Calendar calendar)
	{
		int goldenNo = getGoldenNumber(calendar.get(1));
		int centuryNumber = getCenturyNumber(calendar.get(1));
		int nonLeapDayCorrection = getNonLeapDayCorrection(centuryNumber);
		int fullMoonCorrection = getFullMoonCorrection(centuryNumber);
		int provisionalEpact = getProvisionalEpact(goldenNo,
				nonLeapDayCorrection, fullMoonCorrection);
		provisionalEpact = getCorrectedEpact(provisionalEpact, goldenNo);
		int sundayShift = getSundayShift(calendar.get(1), nonLeapDayCorrection,
				provisionalEpact);
		int finalEpact = getFinalEpact(sundayShift, provisionalEpact);
		return getEasterSunday(calendar, finalEpact);
	}

	private static Calendar getEasterSunday(Calendar calendar, int finalEpact)
	{
		Calendar cal = Calendar.getInstance();
		cal.set(2, 3);
		cal.set(5, 26);
		cal.set(1, calendar.get(1));
		cal.add(5, finalEpact * -1);
		return cal;
	}

	public static void main(String args[])
	{
		EasterSunday es = new EasterSunday();
		Calendar calendar = Calendar.getInstance();
		calendar.set(5, 23);
		calendar.set(2, 2);
		calendar.set(1, 2008);
		System.out.println(es.compareWith(calendar));
	}
}
