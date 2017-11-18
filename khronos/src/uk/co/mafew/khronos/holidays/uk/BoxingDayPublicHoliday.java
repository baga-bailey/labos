package uk.co.mafew.khronos.holidays.uk;

import java.util.Calendar;
import uk.co.mafew.khronos.CompareWith;

public class BoxingDayPublicHoliday implements CompareWith {
	private String type = "is";

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BoxingDayPublicHoliday() {
		
	}

	public boolean compareWith(Calendar calendar) {
		Calendar pubHol = get(calendar);
		return pubHol.get(2) == calendar.get(2)
				&& pubHol.get(5) == calendar.get(5);
	}

	public static Calendar get(Calendar calendar) {
		int dayOfWeek = getDayOfWeek(calendar);
		Calendar publicHoliday = (Calendar) calendar.clone();
		publicHoliday.set(2, 11);
		switch (dayOfWeek) {
		case 7:
			publicHoliday.set(5, 28);
			break;

		case 1:
			publicHoliday.set(5, 27);
			break;

		default:
			publicHoliday.set(5, 26);
			break;
		}
		return publicHoliday;
	}

	private static int getDayOfWeek(Calendar calendar) {
		Calendar cd = Calendar.getInstance();
		cd.set(1, calendar.get(1));
		cd.set(2, 11);
		cd.set(5, 26);
		return cd.get(7);
	}

	public static void main(String args[]) {
		BoxingDayPublicHoliday es = new BoxingDayPublicHoliday();
		Calendar calendar = Calendar.getInstance();
		calendar.set(5, 27);
		calendar.set(2, 11);
		calendar.set(1, 2003);
		System.out.println(get(calendar).getTime());
		System.out.println(es.compareWith(calendar));
	}
}
