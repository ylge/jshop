package com.iskyshop.view.web.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.service.ISysConfigService;

/**
 * 
 * <p>
 * Title: AppDownloadViewAction.java
 * </p>
 * 
 * <p>
 * Description: PC端页面app下载控制器，通过手机扫描二维码，自动识别手机型号，完成下载路径导向，后台填写下载地址要写完整的绝对地址
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
 * @author erikzhang
 * 
 * @date 2015-2-3
 * 
 * @version iskyshop_b2b2c 2015
 */
@Controller
public class AppDownloadViewAction {
	@Autowired
	private ISysConfigService configService;

	/**
	 * 根据前端二维码扫描结果自动下载对应的手机客户端
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/app_download.htm")
	public void app_download(HttpServletRequest request,
			HttpServletResponse response) {
		// System.out.println(request.getHeader("User-Agent").toLowerCase());
		String user_agent = request.getHeader("User-Agent").toLowerCase();
		String url = CommUtil.getURL(request);
		// String ios_reg =
		// ".+?\\(iphone; cpu \\w+ os [1-9]\\d*_\\d+_\\d+ \\w+ mac os x\\).+";
		if (user_agent.indexOf("iphone") > 0) {
			url = this.configService.getSysConfig().getIos_download();
		}
		if (user_agent.indexOf("android") > 0) {
			url = this.configService.getSysConfig().getAndroid_download();
		}
		// System.out.println("自动下载app");
		// request.getRequestDispatcher(url).forward(request, response);
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据前端二维码扫描结果自动下载对应的商家手机客户端
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/app_seller_download.htm")
	public void app_seller_download(HttpServletRequest request,
			HttpServletResponse response) {
		String user_agent = request.getHeader("User-Agent").toLowerCase();
		String url = CommUtil.getURL(request);
		// String ios_reg =
		// ".+?\\(iphone; cpu \\w+ os [1-9]\\d*_\\d+_\\d+ \\w+ mac os x\\).+";
		if (user_agent.indexOf("iphone") > 0) {
			url = this.configService.getSysConfig().getIos_seller_download();
		}
		if (user_agent.indexOf("android") > 0) {
			url = this.configService.getSysConfig().getAndroid_seller_download();
		}
		// System.out.println("自动下载app");
		// request.getRequestDispatcher(url).forward(request, response);
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
