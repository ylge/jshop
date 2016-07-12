package com.iskyshop.module.weixin.view.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.XMLUtil;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IPredepositLogService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ISystemTipService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
/**
 * 
 * <p>
 * Title: WapRechargeAction.java
 * </p>
 * 
 * <p>
 * Description:wap系统充值控制器,用来查询并计算充值应缴纳的金额、手机充值等服务
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author jinxinzhe
 * 
 * @date 2014-1-4
 * 
 * @version iskyshop_b2b2c v2.0 2015版 
 */
@Controller
public class WeixinRechargeAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ISystemTipService systemTipService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IPaymentService paymentService;
	
	
	@RequestMapping("/wap/recharge.htm")
	public ModelAndView recharge(HttpServletRequest request,
			HttpServletResponse response, String mobile, String rc_amount) {
		ModelAndView mv = new JModelAndView("recharge.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String userid = this.configService.getSysConfig().getOfcard_userid();
		String userpws = Md5Encrypt.md5(this.configService.getSysConfig()
				.getOfcard_userpws());
		String query_url = "http://api2.ofpay.com/telquery.do?userid=" + userid
				+ "&userpws=" + userpws + "&phoneno=" + mobile + "&pervalue="
				+ rc_amount + "&version=6.0";
		String return_xml = this.getHttpContent(query_url, "gb2312", "POST");
		Map map = XMLUtil.parseXML(return_xml, true);
		double inprice = CommUtil.null2Double(map.get("inprice"));
		if (CommUtil.null2Double(map.get("inprice")) <= CommUtil
				.null2Double(rc_amount)) {
			inprice = CommUtil.add(map.get("inprice"), this.configService
					.getSysConfig().getOfcard_mobile_profit());
			if (inprice > CommUtil.null2Double(rc_amount)) {
				inprice = CommUtil.null2Double(rc_amount);
			}
		}
		map.put("inprice", inprice);
		String recharge_session = CommUtil.randomString(64);
		request.getSession(false).setAttribute("recharge_session",
				recharge_session);
		mv.addObject("recharge_session", recharge_session);
		mv.addObject("map", map);
		mv.addObject("rc_amount", rc_amount);
		mv.addObject("mobile", mobile);
		return mv;
	}

	private static String getHttpContent(String url, String charSet,
			String method) {
		HttpURLConnection connection = null;
		String content = "";
		try {
			URL address_url = new URL(url);
			connection = (HttpURLConnection) address_url.openConnection();
			connection.setRequestMethod("GET");
			// 设置访问超时时间及读取网页流的超时时间,毫秒值
			connection.setConnectTimeout(1000000);
			connection.setReadTimeout(1000000);
			// 得到访问页面的返回值
			int response_code = connection.getResponseCode();
			if (response_code == HttpURLConnection.HTTP_OK) {
				InputStream in = connection.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in, charSet));
				String line = null;
				while ((line = reader.readLine()) != null) {
					content += line;
				}
				return content;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return "";
	}
}
