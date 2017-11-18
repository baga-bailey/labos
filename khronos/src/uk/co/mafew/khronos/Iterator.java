package uk.co.mafew.khronos;

import java.util.*;

import uk.co.mafew.logging.Logger;

// Referenced classes of package uk.co.mafew.khronos:
//            CompareWith, RegularEvent

public class Iterator
{

	private Vector eventList;
	private Vector exceptionList;
	private Vector inclusionList;
	private Vector nextValidDate;
	private RegularEvent regEvent;

	private Vector andList;
	private Vector orList;
	
	Logger logger;

	public Iterator()
	{
		logger = new Logger(Iterator.class.getName());
		eventList = new Vector();
		exceptionList = new Vector();
		nextValidDate = new Vector();
		inclusionList = new Vector();

		andList = new Vector();
		orList = new Vector();
	}

	public boolean addAndGroup(ConditionGroup al)
	{
		return andList.add(al);
	}

	public boolean addOrGroup(ConditionGroup ol)
	{
		return orList.add(ol);
	}

	public boolean addEvent(RegularEvent event)
	{
		return eventList.add(event);
	}

	public boolean addException(CompareWith comp)
	{
		return exceptionList.add(comp);
	}

	public boolean addInclusion(CompareWith comp)
	{
		return inclusionList.add(comp);
	}

	public Calendar next()
	{
		Calendar calendar;
		Boolean hasConditions = !(andList.isEmpty() && orList.isEmpty());
		if (hasConditions)
		{
			while (!isValid(calendar = getNext()));
		}
		else
		{
			calendar = getNext();
		}
		return calendar;
	}

	private boolean isValid(Calendar calendar)
	{
		boolean isValid = false;

		// Iterate through the orList
		// isValid will only remain false if all conditions
		// evaluate to false
		for (int i = 0; i < orList.size(); i++)
		{
			ConditionGroup cg = (ConditionGroup) orList.get(i);
			if (cg.isValid(calendar) == true)
			{
				isValid = true;
			}
		}

		if (!isValid && andList.size() > 0)
		{
			isValid = true;

			// Iterate through the andList
			// isValid will only remain true if all conditions
			// evaluate to true
			for (int i = 0; i < andList.size(); i++)
			{
				ConditionGroup cg = (ConditionGroup) andList.get(i);
				if (cg.isValid(calendar) == false)
				{
					isValid = false;
				}
			}
		}

		logger.log.debug("Iterator = " + Boolean.toString(isValid));
		return isValid;
	}

	private Calendar getNext()
	{
		Date date = null;
		nextValidDate.clear();
		for (int i = 0; i < eventList.size(); i++)
		{
			regEvent = (RegularEvent) eventList.get(i);
			regEvent.next();
		}

		RegularEvent earliestEvent = (RegularEvent) eventList.get(0);
		for (int i = 1; i < eventList.size(); i++)
		{
			RegularEvent tempEvent = (RegularEvent) eventList.get(i);
			RegularEvent eventToRollBack;
			if (tempEvent.getDate().before(earliestEvent.getDate()))
			{
				eventToRollBack = earliestEvent;
				earliestEvent = tempEvent;
			} else
			{
				eventToRollBack = tempEvent;
			}
			if (eventToRollBack.getDate().compareTo(earliestEvent.getDate()) != 0)
			{
				date = eventToRollBack.previous();
				if (date.before(eventToRollBack.getInitialDate()))
					eventToRollBack.next();
			}
		}

		return earliestEvent.getDate();
	}

}
