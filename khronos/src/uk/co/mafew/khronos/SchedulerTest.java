package uk.co.mafew.khronos;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class SchedulerTest
{
	String str = "null";

	public SchedulerTest()
	{

	}

	public SchedulerTest(String str)
	{
		this.str = str;
	}

	public static void main(String[] args)
	{
		SchedulerTest tst = new SchedulerTest();
		System.out.println(tst.run(10));
	}

	/**
	 * @param args
	 */
	public Integer run(Integer str)
	{
		return str;
	}
	
	public String run(String str)
	{
		System.out.println("SchedulerTest has executed with value " + str);
		return str;
	}
	
	public int run(int str)
	{
		return str;
	}
	
	public Byte run(Byte str)
	{
		return str;
	}
	public Short run(Short str)
	{
		return str;
	}
	public Long run(Long str)
	{
		return str;
	}
	public Float run(Float str)
	{
		return str;
	}
	public Double run(Double str)
	{
		return str;
	}
	public Character run(Character str)
	{
		return str;
	}
	public Boolean run(Boolean str)
	{
		return str;
	}

	private boolean containsInterface(String iFace,
			Class<CompareWith> classDefinition)
	{
		Class<?>[] interfaces = classDefinition.getInterfaces();
		for (int i = 0; i < interfaces.length; i++)
			if (interfaces[i].getName() == iFace)
				return true;

		return false;
	}
}
