package com.marwahtechsolutions.hijriwidget.models;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import android.os.Build;

public class HijriCalendar {
	/*
	 * Properties
	 */
	public final static int ERA = 0;
	public final static int DAY = 1;
	public final static int MONTH = 2;
	public final static int YEAR = 3;
	public final static int DAY_OF_WEEK = 4;
	public final static int ADJUST_DAYS = 5;
	public final static int LOCALE = 6;
	public final static int AR = 0;
	public final static int EN = 1;

	public final static String AH = "AH";
	private int[] fields;
	private static final int BASE_FIELD_COUNT = 7;
	private Calendar calendar;
	private String[] arrHijriDayOfWeek;
	private String[] arrHijriMonths;

	/*
	 * Constructor
	 */
	public HijriCalendar(int diff, Integer locale,
						 String[] arrHijriMonths,
						 String[] arrHijriDayOfWeek,
                         int hour,
                         int minute) {
		calendar = Calendar.getInstance();

		if(calendar.get(Calendar.HOUR_OF_DAY)> hour)
	    {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
	    }
	    else if(calendar.get(Calendar.HOUR_OF_DAY)== hour && calendar.get(Calendar.MINUTE)>=minute)
	    {
	    	calendar.add(Calendar.DAY_OF_MONTH, 1);
	    }
		fields = new int[BASE_FIELD_COUNT];
		fields[ADJUST_DAYS] = diff;
		fields[LOCALE] = locale;
		this.arrHijriDayOfWeek = arrHijriDayOfWeek;
		this.arrHijriMonths = arrHijriMonths;
		compute();
	}

	/*
	 * Private Members
	 */

	private void compute() {
		int adj = fields[ADJUST_DAYS];
		int wd = calendar.get(Calendar.DAY_OF_WEEK);//gmod((int) jd + 1, 7) + 1;
		int adjustmili = 1000 * 60 * 60 * 24 * adj;
		long todaymili = calendar.getTimeInMillis() + adjustmili;
		calendar.setTimeInMillis(todaymili);

		double day = calendar.get(Calendar.DAY_OF_MONTH);
		double month = calendar.get(Calendar.MONTH);
		double year = calendar.get(Calendar.YEAR);

		double m = month + 1;
		double y = year;
		if (m < 3) {
			y -= 1;
			m += 12;
		}

		double a = Math.floor(y / 100.);
		double b = 2 - a + Math.floor(a / 4.);

		if (y < 1583)
			b = 0;
		if (y == 1582) {
			if (m > 10)
				b = -10;
			if (m == 10) {
				b = 0;
				if (day > 4)
					b = -10;
			}
		}

		double jd = Math.floor(365.25 * (y + 4716))
				+ Math.floor(30.6001 * (m + 1)) + day + b - 1524;

		b = 0;
		if (jd > 2299160) {
			a = Math.floor((jd - 1867216.25) / 36524.25);
			b = 1 + a - Math.floor(a / 4.);
		}
		double bb = jd + b + 1524;
		double cc = Math.floor((bb - 122.1) / 365.25);
		double dd = Math.floor(365.25 * cc);
		double ee = Math.floor((bb - dd) / 30.6001);
		day = (int) ((bb - dd) - Math.floor(30.6001 * ee));
		month = (int) ee - 1;
		if (ee > 13) {
			cc += 1;
			month = (int) ee - 13;
		}
		year = (int) cc - 4716;

		

		double iyear = 10631. / 30.;
		double epochastro = 1948084;
		// double epochcivil = 1948085;

		double shift1 = 8.01 / 60.;

		double z = jd - epochastro;
		double cyc = Math.floor(z / 10631.);
		z = z - 10631 * cyc;
		double j = Math.floor((z - shift1) / iyear);
		double iy = 30 * cyc + j;
		z = z - Math.floor(j * iyear + shift1);
		double im = Math.floor((z + 28.5001) / 29.5);
		if (im == 13)
			im = 12;
		double id = z - Math.floor(29.5001 * im - 29);

		fields[DAY_OF_WEEK] = wd-1;
		fields[DAY] = (int) id;
		fields[MONTH] = (int) im - 1;
		fields[YEAR] = (int) iy;
	}

	/*
	 * Public Getters and Setters
	 */

	public String GetDayName() {
		return this.arrHijriDayOfWeek[fields[DAY_OF_WEEK]];
	}

	public String GetMonthName() {
		return this.arrHijriMonths[fields[MONTH]];
	}


    public String getYear()  {
        return String.valueOf(fields[YEAR]);
    }

    public String getDay()  {
        return String.valueOf(fields[DAY]);
    }

    public String getFormatedYear() {
        return String.format("%s %s", this.getYear(), this.getERA());
    }

	public String getERA() {
		if (fields[LOCALE] == AR) {
			return "";
		} else
			return AH;
	}

	public String getSuffix() {
		if (fields[LOCALE] == AR) {
			return "";
		}
		final int n = fields[DAY];
		if (n >= 11 && n <= 13) {
			return "th";
		}
		switch (n % 10) {
		case 1:
			return "st";
		case 2:
			return "nd";
		case 3:
			return "rd";
		default:
			return "th";
		}
	}

	public static long getNextDayMilliSeconds(int hour, int minute)
	{
		Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, hour);
	    calendar.set(Calendar.MINUTE, minute);
	    calendar.set(Calendar.SECOND, 0);
	    return calendar.getTimeInMillis();
	}

	public static int getLocale(String locale) {
		if(locale.equalsIgnoreCase("English"))
			return EN;
		return AR;
	}
}