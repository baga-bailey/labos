package uk.co.mafew.khronos;

import java.util.Calendar;

// Referenced classes of package uk.co.mafew.khronos:
//            CompareWith

public class PointInTime extends Calendar implements CompareWith
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

	public PointInTime()
	{
		era = false;
		month = false;
		weekOfYear = false;
		weekOfMonth = false;
		dayOfMonth = false;
		dayOfYear = false;
		dayOfWeek = false;
		dayOfWeekInMonth = false;
		amPm = false;
		hour = false;
		hourOfDay = false;
		minute = false;
		second = false;
	}

	public PointInTime(int field, int value)
	{
		era = false;
		month = false;
		weekOfYear = false;
		weekOfMonth = false;
		dayOfMonth = false;
		dayOfYear = false;
		dayOfWeek = false;
		dayOfWeekInMonth = false;
		amPm = false;
		hour = false;
		hourOfDay = false;
		minute = false;
		second = false;
		setFieldToTest(field, value);
	}

	public void setFieldToTest(int field, int value)
	{
		super.set(field, value);
		fieldToTest = field;
	}

	public int getFieldToTest()
	{
		return fieldToTest;
	}

	public void set(int field, int value)
	{
		super.set(field, value);
		setFlag(field, true);
	}

	public void setFlag(int field, boolean value)
	{
		switch (field)
		{
		case 0: // '\0'
			era = value;
			break;

		case 1: // '\001'
			month = value;
			break;

		case 2: // '\002'
			weekOfYear = value;
			break;

		case 3: // '\003'
			weekOfMonth = value;
			break;

		case 4: // '\004'
			dayOfMonth = value;
			break;

		case 5: // '\005'
			dayOfYear = value;
			break;

		case 6: // '\006'
			dayOfWeek = value;
			break;

		case 7: // '\007'
			dayOfWeekInMonth = value;
			break;

		case 8: // '\b'
			amPm = value;
			break;

		case 10: // '\n'
			hour = value;
			break;

		case 11: // '\013'
			hourOfDay = value;
			break;

		case 12: // '\f'
			minute = value;
			break;

		case 13: // '\r'
			second = value;
			break;
		}
	}

	public boolean compareWith(Calendar calendar)
	{
		return calendar.get(fieldToTest) == get(fieldToTest);
	}

	protected void computeTime()
	{
	}

	protected void computeFields()
	{
	}

	public void add(int i, int j)
	{
	}

	public void roll(int i, boolean flag)
	{
	}

	public int getMinimum(int arg0)
	{
		return 0;
	}

	public int getMaximum(int arg0)
	{
		return 0;
	}

	public int getGreatestMinimum(int arg0)
	{
		return 0;
	}

	public int getLeastMaximum(int arg0)
	{
		return 0;
	}

	public int compareTo(Calendar anotherCalendar)
	{
		return 0;
	}

	public static void main(String args1[])
	{
	}

	private boolean era;
	private boolean month;
	private boolean weekOfYear;
	private boolean weekOfMonth;
	private boolean dayOfMonth;
	private boolean dayOfYear;
	private boolean dayOfWeek;
	private boolean dayOfWeekInMonth;
	private boolean amPm;
	private boolean hour;
	private boolean hourOfDay;
	private boolean minute;
	private boolean second;
	private int fieldToTest;
}
