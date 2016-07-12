package com.iskyshop.foundation.test;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.iskyshop.core.tools.CommUtil;

public class Test {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) {
		// String serverName="www.iskyshop.com";
		// String system_domain="";
		// if (serverName.indexOf(".") == serverName.lastIndexOf(".")) {
		// system_domain = serverName;
		// } else {
		// system_domain = serverName
		// .substring(serverName.indexOf(".") + 1);
		// }
		// System.out.println(system_domain);
		// String source = "E:\\1b1e5e9a02b88f9f2d1ddbd529b0ce40.tbi";
		// String target = "E:\\1.tbi";
		String pressImg = "E:\\100_100.png";
		String targetImg = "D:\\a9c473b1-b14c-406d-a051-914ec1d41265.png";
		//
		// CommUtil.waterMarkWithImage(pressImg, targetImg, 9, 50);
		CommUtil.createSmall(targetImg, targetImg + "_small.jpg", 300, 300);

		// String html =
		// "<ahref=\"http://www.baidu.com/gaoji/preferences.html\"name=\"tj_setting\">搜索设置</a>        &nbsp;&nbsp;";
		// String doc = Jsoup.clean(html,
		// Whitelist.none()).replace("&nbsp;","").trim();
		// System.out.println(doc);
		// System.out.println("*******");
		// doc = Jsoup.clean(html, Whitelist.simpleText());
		// System.out.println(doc);
		// System.out.println("*******");
		// doc = Jsoup.clean(html, Whitelist.basic());
		// System.out.println(doc);
		// System.out.println("*******");
		// doc = Jsoup.clean(html, Whitelist.basicWithImages());
		// System.out.println(doc);
		// System.out.println("*******");
		// doc = Jsoup.clean(html, Whitelist.relaxed());
		// System.out.println(doc);
		// List days = new ArrayList();
		// // for (int i = 0; i <= 7; i++) {
		// // Calendar cal = Calendar.getInstance();
		// // cal.add(Calendar.DAY_OF_YEAR, i);
		// // days.add(CommUtil.formatTime("MM-dd", cal.getTime()));
		// // System.out.println(CommUtil.formatTime("MM-dd",
		// // cal.getTime())+"   星期"+(cal.get(Calendar.DAY_OF_WEEK)-1));
		// // }
		// String s = CommUtil.null2String(UUID.randomUUID()).replaceAll("-",
		// "");
		// System.out.println(s);
	}
}
