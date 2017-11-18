package uk.co.mafew.khronos;

import java.util.Calendar;
import java.util.Date;

import uk.co.mafew.logging.Logger;

public class RegularEvent
{
	private int delay;
	private Calendar calendar;
	private final String period;
	private final Date startingDate;
	Logger logger;

	public RegularEvent(int hourOfDay, int minute, int second, int dayOfMonth,
			int monthOfYear, int year, int delay, String period)
	{
		logger = new Logger(RegularEvent.class.getName());
		this.period = period;
		this.delay = delay;
		calendar = Calendar.getInstance();
		calendar.set(1, year);
		calendar.set(5, dayOfMonth);
		calendar.set(2, monthOfYear - 1);
		calendar.set(11, hourOfDay);
		calendar.set(12, minute);
		calendar.set(13, second);
		calendar.set(14, 0);
		
		while(calendar.before(Calendar.getInstance()))
		{
			next();
		}
		previous();
		//for (; calendar.before(Calendar.getInstance()); next());
		startingDate = calendar.getTime();
		logger.log.debug("Starting time = " + startingDate.toString());
	}

	public Date previous()
	{
		delay *= -1;
		Date date = next();
		delay *= -1;
		return date;
	}

	public Date next()
	{
		if (period.compareTo("second") == 0)
			calendar.add(13, delay);
		else if (period.compareTo("minute") == 0)
			calendar.add(12, delay);
		else if (period.compareTo("daily") == 0)
			calendar.add(5, delay);
		else if (period.compareTo("weekly") == 0)
			calendar.add(4, delay);
		else if (period.compareTo("monthly") == 0)
			calendar.add(2, delay);
		
		return calendar.getTime();
	}

	public Calendar getDate()
	{
		return calendar;
	}

	public Date getInitialDate()
	{
		return startingDate;
	}

	
}
