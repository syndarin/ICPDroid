package com.syndarin.icpdroid;

import java.util.Calendar;

import android.text.format.DateFormat;

public class CalendarHelper {

	public static String calendarToString(Calendar date, String pattern){
		return DateFormat.format(pattern, date).toString();
	}
	
}
