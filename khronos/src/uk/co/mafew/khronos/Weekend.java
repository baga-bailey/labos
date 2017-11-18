

 


package uk.co.mafew.khronos;

import java.util.Calendar;

// Referenced classes of package uk.co.mafew.khronos:
//            CompareWith

public class Weekend implements CompareWith
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

	public Weekend()
	{
	}

	public boolean compareWith(Calendar calendar)
	{
		return calendar.get(7) == 7 || calendar.get(7) == 1;
	}

	public static void main(String args1[])
	{
	}
}
