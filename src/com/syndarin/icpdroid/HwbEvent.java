package com.syndarin.icpdroid;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HwbEvent {

	private String office;
	private Calendar date;
	private String state;
	private String comment;

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(String dateString) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
		Date date = format.parse(dateString);
		this.date = Calendar.getInstance();
		this.date.setTime(date);
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return office+"\n"+date+"\n"+state+"\n"+comment;
	}
	

}
