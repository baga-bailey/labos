package uk.co.mafew.hephaestus;

import javax.naming.InitialContext;

import uk.co.mafew.file.Config;

public class TestClass
{
	public TestClass()
	{
		try 
		{
			InitialContext ctx = new InitialContext();
			GeneralConfig gc = (GeneralConfig) ctx.lookup("java:global/uranus-ear/hephaestus/GeneralConfig");
			
		} catch (Exception e1) {
			System.out.println(e1.getMessage());
		}
	}

}
