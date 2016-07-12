package com.iskyshop.foundation.test;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class TestJson {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void main(String[] args) throws IOException, ParseException {
		Map map = new HashMap();
		map.put("key1", "1");
		map.put("key2", "2");
		System.out.println(map);
	}
}
