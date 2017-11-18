package uk.co.mafew.khronos.holidays.uk.engWalesNI;

import java.util.Calendar;

import uk.co.mafew.khronos.Weekend;
import uk.co.mafew.khronos.holidays.uk.BoxingDay;
import uk.co.mafew.khronos.holidays.uk.BoxingDayPublicHoliday;
import uk.co.mafew.khronos.holidays.uk.ChristmasDay;
import uk.co.mafew.khronos.holidays.uk.ChristmasDayPublicHoliday;
import uk.co.mafew.khronos.holidays.uk.EarlyMayBankHol;
import uk.co.mafew.khronos.holidays.uk.EasterSunday;
import uk.co.mafew.khronos.holidays.uk.GoodFriday;
import uk.co.mafew.khronos.holidays.uk.NewYearsDay;
import uk.co.mafew.khronos.holidays.uk.NewYearsDayPublicHoliday;
import uk.co.mafew.khronos.holidays.uk.SpringBankHol;

public class publicHoliday {

	private String type = "is";

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean compareWith(Calendar calendar) {
		BoxingDay bd = new BoxingDay();
		if (bd.compareWith(calendar))
			return true;

		BoxingDayPublicHoliday bdph = new BoxingDayPublicHoliday();
		if (bdph.compareWith(calendar))
			return true;

		ChristmasDay cd = new ChristmasDay();
		if (cd.compareWith(calendar))
			return true;

		ChristmasDayPublicHoliday cdph = new ChristmasDayPublicHoliday();
		if (cdph.compareWith(calendar))
			return true;

		EarlyMayBankHol embh = new EarlyMayBankHol();
		if (embh.compareWith(calendar))
			return true;

		EasterSunday es = new EasterSunday();
		if (es.compareWith(calendar))
			return true;

		GoodFriday gf = new GoodFriday();
		if (gf.compareWith(calendar))
			return true;

		NewYearsDay nyd = new NewYearsDay();
		if (nyd.compareWith(calendar))
			return true;

		NewYearsDayPublicHoliday nydph = new NewYearsDayPublicHoliday();
		if (nydph.compareWith(calendar))
			return true;

		SpringBankHol sbh = new SpringBankHol();
		if (sbh.compareWith(calendar))
			return true;

		EasterMonday em = new EasterMonday();
		if (em.compareWith(calendar))
			return true;

		SummerPublicHoliday sph = new SummerPublicHoliday();
		if (sph.compareWith(calendar))
			return true;

		return false;

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
