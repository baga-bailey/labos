package uk.co.mafew.khronos;

import java.util.Calendar;
import java.util.Vector;

import uk.co.mafew.logging.Logger;

public class ConditionGroup
{

	/**
	 * 
	 */
	private String type;
	private String operator;

	private Vector<CompareWith> andList;
	private Vector<CompareWith> orList;
	
	Logger logger;

	public ConditionGroup()
	{
		logger = new Logger(this.getClass().getName());
		andList = new Vector<CompareWith>();
		orList = new Vector<CompareWith>();
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getOperator()
	{
		return operator;
	}

	public void setOperator(String operator)
	{
		this.operator = operator;
	}

	public boolean isValid(Calendar calendar)
	{
		boolean isValid = false;

		// Iterate through the orList
		// isValid will only remain false if all conditions
		// evaluate to false
		for (int i = 0; i < orList.size(); i++)
		{
			CompareWith obj = orList.get(i);
			if (obj.compareWith(calendar) == true)
			{
				isValid = true;
			}
		}

		// We only need to check the andList if the orList has returned false

		if (!isValid && andList.size() > 0)
		{
			isValid = true;
			// Iterate through the andList
			// isValid will only remain true if all conditions
			// evaluate to true
			for (int i = 0; i < andList.size(); i++)
			{
				CompareWith obj = andList.get(i);
				if (obj.compareWith(calendar) == false)
				{
					isValid = false;
				}
			}
		}

		if (type.compareTo("not") == 0)
		{
			isValid = !isValid;
		}
		logger.log.debug("ConditionGroup = " + Boolean.toString(isValid));
		return isValid;
	}

	public boolean addAndCondition(CompareWith cw)
	{
		return andList.add(cw);
	}

	public boolean addOrCondition(CompareWith cw)
	{
		return orList.add(cw);
	}

}
