package com.iskyshop.foundation.test;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class TestDate {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Calendar cal = Calendar. getInstance ();
		System.out.println(cal.getTime());
	    cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE)+20);
	    System.out.println(cal.getTime());
	}
}
