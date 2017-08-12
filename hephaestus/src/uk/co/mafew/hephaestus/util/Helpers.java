package uk.co.mafew.hephaestus.util;

public class Helpers
{

	public static void main(String[] args)
	{
		Helpers helpers = new Helpers();
		System.out.println("hello");
		long l = 10000;
		helpers.Sleep(l);
		System.out.println("hello");
	}
	
	public String Sleep(long period)
	{
		try
		{
			Thread.sleep(period);
		}
		catch (InterruptedException e)
		{
			return "ERROR: " + e.getMessage();
		}
		
		return "SUCCESS: Sleep ended";
	}

}
