package uk.co.mafew.khronos.holidays.uk.engWalesNI;

import java.util.Calendar;

import uk.co.mafew.khronos.Weekend;

public class WorkingDay {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

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

	public boolean compareWith(Calendar calendar)
	{
		Weekend weekend = new Weekend();
		if(weekend.compareWith(calendar))
			return false;
		
		publicHoliday ph = new publicHoliday();
		if(ph.compareWith(calendar))
			return false;
		
		return true;
	}

}
