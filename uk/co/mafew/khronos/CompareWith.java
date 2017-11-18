

 


package uk.co.mafew.khronos;

import java.util.Calendar;

public interface CompareWith
{
	public abstract boolean compareWith(Calendar calendar);

	public String getType();

	public void setType(String type);
}
