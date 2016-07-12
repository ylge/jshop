package com.iskyshop.foundation.test;

import com.iskyshop.core.tools.CommUtil;

public class TestVar {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String name = "平台管理员ADMIN";
		String regex = "(.*管理员.*)";
		String regex1 = "(.*admin.*)";
		boolean a = name.matches(regex);
		System.out.println(a);
		boolean b = name.toLowerCase().matches(regex1);
		System.out.println(b);
		if(a||b){
			System.out.println("1");
		}
	}

}
