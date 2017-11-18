package uk.co.mafew.khronos.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import uk.co.mafew.khronos.holidays.uk.engWalesNI.WorkingDay;

public class DateTimeHelper {
	
	private int START_OF_WORKING_DAY = 8;
	private int END_OF_WORKING_DAY = 17;

	public static void main(String[] args) {
		DateTimeHelper dth = new DateTimeHelper();
		
		//System.out.println(dth.isWorkingHour("2014-12-23 20:00:00", "yyyy-MM-dd hh:mm:ss"));
		//System.out.println(dth.reformatDate("2016-02-11 12:08:14", "yyyy-MM-dd hh:mm:ss", "yyyyMMddhhmmss"));
		System.out.println(dth.getDateDiff("2016-02-11 12:08:14", "2016-02-11 12:08:19","SECONDS", "yyyy-MM-dd hh:mm:ss"));

	}
	
	public String reformatDate(String date1,String date1Format, String date2Format)
	{
		try 
		{
			SimpleDateFormat formatter = new SimpleDateFormat(date1Format);
			Date date = formatter.parse(date1);
			formatter = new SimpleDateFormat(date2Format);
			return formatter.format(date);
		}
		catch (Exception e) {
			return "Error - Date parameters are not in the correct format";
		}
	}
	
	public String getWorkingDayDiff(String date1, String date2, String dateFormat)
	{
		Calendar dt1, dt2, earlyDate, laterDate;
		dt1 = Calendar.getInstance();
		dt2 = Calendar.getInstance();
		earlyDate = Calendar.getInstance();
		laterDate = Calendar.getInstance();
		try 
		{
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			dt1.setTime(formatter.parse(date1));
			dt2.setTime(formatter.parse(date2));
		} catch (Exception e) {
			return "Error - Date parameters are not in the correct format";
		}
		
		//Its only the days, months and years we're interested in
		if(dt1.before(dt2))
		{
			earlyDate.set(Calendar.YEAR, dt1.get(Calendar.YEAR));
			earlyDate.set(Calendar.MONTH, dt1.get(Calendar.MONTH));
			earlyDate.set(Calendar.DAY_OF_MONTH, dt1.get(Calendar.DAY_OF_MONTH));
			laterDate.set(Calendar.YEAR, dt2.get(Calendar.YEAR));
			laterDate.set(Calendar.MONTH, dt2.get(Calendar.MONTH));
			laterDate.set(Calendar.DAY_OF_MONTH, dt2.get(Calendar.DAY_OF_MONTH));
		}
		else
		{
			earlyDate.set(Calendar.YEAR, dt2.get(Calendar.YEAR));
			earlyDate.set(Calendar.MONTH, dt2.get(Calendar.MONTH));
			earlyDate.set(Calendar.DAY_OF_MONTH, dt2.get(Calendar.DAY_OF_MONTH));
			laterDate.set(Calendar.YEAR, dt1.get(Calendar.YEAR));
			laterDate.set(Calendar.MONTH, dt1.get(Calendar.MONTH));
			laterDate.set(Calendar.DAY_OF_MONTH, dt1.get(Calendar.DAY_OF_MONTH));
		}
		
		WorkingDay wd = new WorkingDay();
		int noOfDays = 0;
		while (earlyDate.before(laterDate))
		{
			// Advance the early date by one day
			// If the new date is a working day
			// increment noOfDays by 1
			
			earlyDate.add(Calendar.DAY_OF_MONTH, 1);
			if(wd.compareWith(earlyDate))
				noOfDays+=1;
			
		}
			
		return Integer.valueOf(noOfDays).toString();
	}
	
	public String getWorkingHourDiff(String date1, String date2, String dateFormat)
	{
		Calendar dt1, dt2, earlyDate, laterDate;
		dt1 = Calendar.getInstance();
		dt2 = Calendar.getInstance();
		earlyDate = Calendar.getInstance();
		laterDate = Calendar.getInstance();
		try 
		{
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			dt1.setTime(formatter.parse(date1));
			dt2.setTime(formatter.parse(date2));
		} catch (Exception e) {
			return "Error - Date parameters are not in the correct format";
		}
		
		if(dt1.before(dt2))
		{
			earlyDate.set(Calendar.YEAR, dt1.get(Calendar.YEAR));
			earlyDate.set(Calendar.MONTH, dt1.get(Calendar.MONTH));
			earlyDate.set(Calendar.DAY_OF_MONTH, dt1.get(Calendar.DAY_OF_MONTH));
			earlyDate.set(Calendar.HOUR_OF_DAY, dt1.get(Calendar.HOUR_OF_DAY));
			earlyDate.set(Calendar.MINUTE, dt1.get(Calendar.MINUTE));
			laterDate.set(Calendar.YEAR, dt2.get(Calendar.YEAR));
			laterDate.set(Calendar.MONTH, dt2.get(Calendar.MONTH));
			laterDate.set(Calendar.DAY_OF_MONTH, dt2.get(Calendar.DAY_OF_MONTH));
			laterDate.set(Calendar.HOUR_OF_DAY, dt2.get(Calendar.HOUR_OF_DAY));
			laterDate.set(Calendar.MINUTE, dt2.get(Calendar.MINUTE));
		}
		else
		{
			earlyDate.set(Calendar.YEAR, dt2.get(Calendar.YEAR));
			earlyDate.set(Calendar.MONTH, dt2.get(Calendar.MONTH));
			earlyDate.set(Calendar.DAY_OF_MONTH, dt2.get(Calendar.DAY_OF_MONTH));
			earlyDate.set(Calendar.HOUR_OF_DAY, dt2.get(Calendar.HOUR_OF_DAY));
			earlyDate.set(Calendar.MINUTE, dt2.get(Calendar.MINUTE));
			laterDate.set(Calendar.YEAR, dt1.get(Calendar.YEAR));
			laterDate.set(Calendar.MONTH, dt1.get(Calendar.MONTH));
			laterDate.set(Calendar.DAY_OF_MONTH, dt1.get(Calendar.DAY_OF_MONTH));
			laterDate.set(Calendar.HOUR_OF_DAY, dt1.get(Calendar.HOUR_OF_DAY));
			laterDate.set(Calendar.MINUTE, dt1.get(Calendar.MINUTE));
		}
		
		int noOfHours = 0;
		while (earlyDate.before(laterDate))
		{
			// Advance the early date by one hour
			// If the new date is a working hour
			// increment noOfHours by 1
			
			
			if(isWorkingHour(earlyDate) && isWorkingHour(laterDate))
			{
				
				noOfHours+=1;
			}
			earlyDate.add(Calendar.HOUR_OF_DAY, 1);
			
		}
			
		return Integer.valueOf(noOfHours).toString();
	}
	
	public String getDateDiff(String date1, String date2, String timeUnit, String dateFormat)
	{
		TimeUnit tu;
		Date dt1, dt2;
		if(timeUnit.compareToIgnoreCase("DAYS")==0)
		{
			tu = TimeUnit.DAYS;
		}
		else if(timeUnit.compareToIgnoreCase("HOURS")==0)
		{
			tu = TimeUnit.HOURS;
		}
		else if(timeUnit.compareToIgnoreCase("MICROSECONDS")==0)
		{
			tu = TimeUnit.MICROSECONDS;
		} 
		else if(timeUnit.compareToIgnoreCase("MILLISECONDS")==0)
		{
			tu = TimeUnit.MILLISECONDS;
		}
		else if(timeUnit.compareToIgnoreCase("MINUTES")==0)
		{
			tu = TimeUnit.MINUTES;
		}
		else if(timeUnit.compareToIgnoreCase("NANOSECONDS")==0)
		{
			tu = TimeUnit.NANOSECONDS;
		}
		else if(timeUnit.compareToIgnoreCase("SECONDS")==0)
		{
			tu = TimeUnit.SECONDS;
		}
		else 
		{
			return "Error - timeUnit must be one of DAYS, HOURS, MICROSECONDS, MILLISECONDS, MINUTES, NANOSECONDS, SECONDS";
		}
		try 
		{
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			dt1 = formatter.parse(date1);
			dt2 = formatter.parse(date2);
		} catch (Exception e) {
			return "Error - Date parameters are not in the correct format";
		}
			
	    long diffInMillies = dt2.getTime() - dt1.getTime();
	    return Long.toString(tu.convert(diffInMillies,TimeUnit.MILLISECONDS));
	}

	public String getCurrentDateTime(String dateFormat)
	{
		SimpleDateFormat formatter;
		Date dt;
		try {
			formatter = new SimpleDateFormat(dateFormat);
			dt = new Date();
		} catch (Exception e) {
			e.printStackTrace();
			return "Error -Parameter dateFormat is not in the correct form";
		}
		return formatter.format(dt);
	}
	
	public boolean isWorkingHour(String date, String dateFormat)
	{
		boolean retValue = false;
		try
		{
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			cal.setTime(formatter.parse(date));
			retValue=isWorkingHour(cal);
		}
		catch (ParseException e)
		{
		}
		
		return retValue;
	}
	
	private boolean isWorkingHour(Calendar cal)
	{
		boolean retValue = false;
		
		WorkingDay wd = new WorkingDay();
		if(wd.compareWith(cal))
		{
			if((cal.get(Calendar.HOUR_OF_DAY) < END_OF_WORKING_DAY) && (cal.get(Calendar.HOUR_OF_DAY) >= START_OF_WORKING_DAY))
			{
				retValue = true;
			}
		}
		return retValue;
	}
}
