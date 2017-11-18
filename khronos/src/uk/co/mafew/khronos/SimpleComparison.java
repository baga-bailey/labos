package uk.co.mafew.khronos;

import java.util.Calendar;

public class SimpleComparison implements CompareWith
{
	private int field;
	private int value;

	public SimpleComparison(String field, String value, String type)
	{
		this.field = getIntEquivalent(field);
		this.value = getIntEquivalent(value);
		this.type = type;
	}

	private String type = "is";

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public SimpleComparison(String field, String value)
	{
		this.field = getIntEquivalent(field);
		this.value = getIntEquivalent(value);
	}

	public boolean compareWith(Calendar calendar)
	{
		boolean match;

		if (calendar.get(field) == value)
		{
			match = true;
		} else
		{
			match = false;
		}
		if (type.compareTo("not") == 0)
		{
			return !match;
		}
		//System.out.println("SimpleComparison = " + Boolean.toString(match));
		return match;
	}

	private int getIntEquivalent(String val)
	{
		if (val.compareTo("DAY_OF_WEEK") == 0)
		{
			return Calendar.DAY_OF_WEEK;
		} else if (val.compareTo("TUESDAY") == 0)
		{
			return Calendar.TUESDAY;
		} else if (val.compareTo("AM") == 0)
		{
			return Calendar.AM;
		} else if (val.compareTo("AM_PM") == 0)
		{
			return Calendar.AM_PM;
		} else if (val.compareTo("APRIL") == 0)
		{
			return Calendar.APRIL;
		} else if (val.compareTo("AUGUST") == 0)
		{
			return Calendar.AUGUST;
		} else if (val.compareTo("DATE") == 0)
		{
			return Calendar.DATE;
		} else if (val.compareTo("DAY_OF_MONTH") == 0)
		{
			return Calendar.DAY_OF_MONTH;
		} else if (val.compareTo("DAY_OF_WEEK") == 0)
		{
			return Calendar.DAY_OF_WEEK;
		} else if (val.compareTo("DAY_OF_WEEK_IN_MONTH") == 0)
		{
			return Calendar.DAY_OF_WEEK_IN_MONTH;
		} else if (val.compareTo("DAY_OF_YEAR") == 0)
		{
			return Calendar.DAY_OF_YEAR;
		} else if (val.compareTo("DECEMBER") == 0)
		{
			return Calendar.DECEMBER;
		} else if (val.compareTo("DST_OFFSET") == 0)
		{
			return Calendar.DST_OFFSET;
		} else if (val.compareTo("ERA") == 0)
		{
			return Calendar.ERA;
		} else if (val.compareTo("FEBRUARY") == 0)
		{
			return Calendar.FEBRUARY;
		} else if (val.compareTo("FIELD_COUNT") == 0)
		{
			return Calendar.FIELD_COUNT;
		} else if (val.compareTo("FRIDAY") == 0)
		{
			return Calendar.FRIDAY;
		} else if (val.compareTo("HOUR") == 0)
		{
			return Calendar.HOUR;
		}

		else if (val.compareTo("HOUR_OF_DAY") == 0)
		{
			return Calendar.HOUR_OF_DAY;
		}

		else if (val.compareTo("JANUARY") == 0)
		{
			return Calendar.JANUARY;
		}

		else if (val.compareTo("JULY") == 0)
		{
			return Calendar.JULY;
		}

		else if (val.compareTo("JUNE") == 0)
		{
			return Calendar.JUNE;
		}

		else if (val.compareTo("MARCH") == 0)
		{
			return Calendar.MARCH;
		}

		else if (val.compareTo("MAY") == 0)
		{
			return Calendar.MAY;
		}

		else if (val.compareTo("MILLISECOND") == 0)
		{
			return Calendar.MILLISECOND;
		}

		else if (val.compareTo("MINUTE") == 0)
		{
			return Calendar.MINUTE;
		}

		else if (val.compareTo("MONDAY") == 0)
		{
			return Calendar.MONDAY;
		}

		else if (val.compareTo("MONTH") == 0)
		{
			return Calendar.MONTH;
		}

		else if (val.compareTo("NOVEMBER") == 0)
		{
			return Calendar.NOVEMBER;
		}

		else if (val.compareTo("OCTOBER") == 0)
		{
			return Calendar.OCTOBER;
		}

		else if (val.compareTo("PM") == 0)
		{
			return Calendar.PM;
		}

		else if (val.compareTo("SATURDAY") == 0)
		{
			return Calendar.SATURDAY;
		}

		else if (val.compareTo("SECOND") == 0)
		{
			return Calendar.SECOND;
		}

		else if (val.compareTo("SEPTEMBER") == 0)
		{
			return Calendar.SEPTEMBER;
		}

		else if (val.compareTo("SUNDAY") == 0)
		{
			return Calendar.SUNDAY;
		}

		else if (val.compareTo("THURSDAY") == 0)
		{
			return Calendar.THURSDAY;
		}

		else if (val.compareTo("TUESDAY") == 0)
		{
			return Calendar.TUESDAY;
		}

		else if (val.compareTo("UNDECIMBER") == 0)
		{
			return Calendar.UNDECIMBER;
		}

		else if (val.compareTo("WEDNESDAY") == 0)
		{
			return Calendar.WEDNESDAY;
		}

		else if (val.compareTo("WEEK_OF_MONTH") == 0)
		{
			return Calendar.WEEK_OF_MONTH;
		}

		else if (val.compareTo("WEEK_OF_YEAR") == 0)
		{
			return Calendar.WEEK_OF_YEAR;
		}

		else if (val.compareTo("YEAR") == 0)
		{
			return Calendar.YEAR;
		}

		else if (val.compareTo("ZONE_OFFSET") == 0)
		{
			return Calendar.ZONE_OFFSET;
		} else
		{
			try
			{
				return Integer.parseInt(val);
			} catch (NumberFormatException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return -1;
			}

		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
