package com.iskyshop.module.app.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
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

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IDocumentService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IVerifyCodeService;
import com.iskyshop.module.app.service.IQRLoginService;
import com.iskyshop.msg.MsgTools;

/**
 * 
 * <p>
 * Title: AppSellerLoginViewAction.java
 * </p>
 * 
 * <p>
 * Description: 卖家app登录
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author lixiaoyang
 * 
 * @date 2015-3-17
 * 
 * @version 1.0
 */
@Controller
public class AppSellerLoginViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IDocumentService documentService;
	@Autowired
	private IQRLoginService qrLoginService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IVerifyCodeService mobileverifycodeService;

	/**
	 * 手机客户端卖家登录
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 */

	@RequestMapping("/app/iskyshop_seller_login.htm")
	public void app_login(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String device) {
		if (device != null && device.equals("iOS")) {
			userName = CommUtil.convert(userName, "utf-8");
		}
		String code = "-300";// 100,登陆成功,-100账号不存在，-200,密码不正确，-300登录失败
		Map json_map = new HashMap();
		String user_id = "";
		String user_name = "";
		String login_token = "";
		User login_user = null;
		if (userName != null && !userName.equals("") && password != null
				&& !password.equals("")) {
			password = Md5Encrypt.md5(password).toLowerCase();
			Map map = new HashMap();
			map.put("userName", userName);
			List<User> users = this.userService
					.query("select obj from User obj where obj.userName=:userName order by addTime asc",
							map, -1, -1);
			if (users.size() > 0) {
				for (User u : users) {
					if (!u.getUserRole().equalsIgnoreCase("SELLER")) {
						code = "-200";// 该用户没有开店
					} else {
						if (!u.getPassword().equals(password)) {
							code = "-300";// 密码错误
						} else {
							user_id = CommUtil.null2String(u.getId());
							user_name = u.getUserName();
							code = "100";
							login_token = CommUtil.randomString(12) + user_id;
							u.setApp_seller_login_token(login_token
									.toLowerCase());
							this.userService.update(u);
							login_user = u;
							break;
						}
					}
				}
			} else {
				code = "-100";// 用户不存在
			}
		}
		if (code.equals("100")) {
			json_map.put("verify", this.create_appverify(login_user));
			json_map.put("user_id", user_id.toString());
			json_map.put("userName", user_name);
			json_map.put("token", login_token);
		}
		json_map.put("code", code);
		String json = Json.toJson(json_map, JsonFormat.compact());
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

	/**
	 * 当用户登录后生成verify返回给客户端保存，每次发送用户中心中请求时将verify放入到请求头中，
	 * 用来验证用户密码是否已经被更改，如已经更改，手机客户端提示用户重新登录
	 * 
	 * @param user
	 * @return
	 */
	private String create_appverify(User user) {
		String app_verify = user.getPassword() + user.getApp_login_token();
		app_verify = Md5Encrypt.md5(app_verify).toLowerCase();
		return app_verify;
	}
}
