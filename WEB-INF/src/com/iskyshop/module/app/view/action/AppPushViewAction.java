package com.iskyshop.module.app.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.module.app.domain.AppPushUser;
import com.iskyshop.module.app.service.IAppPushUserService;

/**
 * 
 * <p>
 * Title: AppPushViewAction.java
 * </p>
 * 
 * <p>
 * Description: 处理推送的绑定，
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
 * @author lixiaoyang
 * 
 * @date 2015-2-7
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class AppPushViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IAppPushUserService appPushUserService;

	/**
	 * app开启式上传标识，如果登录了，记录用户
	 * 
	 * @param request
	 * @param response
	 * @param appid
	 * @param userId
	 * @param channelId
	 * @param type
	 * @param user_id
	 */
	@RequestMapping("/app/push_bind.htm")
	public void push_bind(HttpServletRequest request,
			HttpServletResponse response, String appid, String userId,
			String channelId, String type, String user_id, String deviceToken) {
		boolean verify = true;
		Map json_map = new HashMap();
		if (verify) {
			if (userId != null && !userId.equals("") && channelId != null
					&& !channelId.equals("")) {
				Map params = new HashMap();
				params.put("app_id", userId);
				List<AppPushUser> list = this.appPushUserService
						.query("select obj from AppPushUser obj where obj.app_id=:app_id",
								params, -1, -1);
				AppPushUser push;
				if (list.size() > 0) {
					push = list.get(0);
				} else {
					push = new AppPushUser();
					push.setAddTime(new Date());
					push.setApp_id(userId);
					push.setApp_channelId(channelId);
					push.setApp_userRole(appid);
					push.setApp_type(type);
				}
				push.setUser_id(user_id);
				this.appPushUserService.save(push);
				json_map.put("code", 100);
			}
			if (type.equals("iOS")) {
				Map params = new HashMap();
				params.put("app_id", deviceToken);
				List<AppPushUser> list = this.appPushUserService
						.query("select obj from AppPushUser obj where obj.app_id=:app_id",
								params, -1, -1);
				AppPushUser push;
				if (list.size() > 0) {
					push = list.get(0);
				} else {
					push = new AppPushUser();
					push.setApp_id(deviceToken);
					push.setApp_type(type);
				}

				push.setUser_id(user_id);
				this.appPushUserService.save(push);
				json_map.put("code", 100);
			}
		}
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	/**
	 * 退出登录时，删除用户id
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 */
	@RequestMapping("/app/push_unbind.htm")
	public void push_unbind(HttpServletRequest request,
			HttpServletResponse response, String user_id) {

		Map json_map = new HashMap();
		if (user_id != null && !user_id.equals("")) {
			Map params = new HashMap();
			params.put("user_id", user_id);
			List<AppPushUser> pushs = this.appPushUserService
					.query("select obj from AppPushUser obj where obj.user_id=:user_id",
							params, -1, -1);
			if (pushs.size() > 0) {
				AppPushUser push = pushs.get(0);
				push.setUser_id("");
				this.appPushUserService.save(push);
				json_map.put("code", 100);
			}
		}
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	private void send_json(String json, HttpServletResponse response) {
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
