package com.iskyshop.module.app.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.view.web.tools.IntegralViewTools;

@Controller
public class AppBuyerAccountAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderformService;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private OrderFormTools orderformTools;

	/**
	 * 手机端用户中心-账户密码修改
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param token
	 * @param order_cat
	 * @param beginCount
	 * @param selectCount
	 */
	@RequestMapping("/app/buyer/password_modify.htm")
	public void password_modify(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token,
			String new_password, String old_password) {
		Map map = new HashMap();
		int code = 100;// 100，修改成功，-100原密码不正确，-200用户信息不正确
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		if (user != null) {
			String old_psw = Md5Encrypt.md5(old_password).toLowerCase();
			if (user.getPassword().equals(old_psw)) {
				String password = Md5Encrypt.md5(new_password).toLowerCase();
				user.setPassword(password);
				this.userService.update(user);
			} else {
				code = -100;
			}
		} else {
			code = -200;
		}
		map.put("code", code);
		map.put("ret", "true");
		String json = Json.toJson(map, JsonFormat.compact());
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
	 * 手机端查询支付密码
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/app/buyer/pay_password.htm")
	public void pay_password(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token)
			throws UnsupportedEncodingException {
		Map json_map = new HashMap();
		int code = 100;// 100修改成功，-100参数错误，-200用户信息错误,-300未设置支付密码
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		if (user != null) {
			String pay_password = user.getMobile_pay_password();
			if (pay_password != null && !pay_password.equals("")) {
				pay_password = this.decodeStr(pay_password);
				for (int i = 0; i < pay_password.length(); i++) {
					if (i < pay_password.length() - 1) {
						pay_password = pay_password.replaceFirst(
								CommUtil.null2String(pay_password.charAt(i)),
								"*");
					}
				}
				json_map.put("pay_password", pay_password);
			} else {
				code = -300;
			}
		} else {
			code = -200;
		}
		json_map.put("ret", "true");
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
	 * 手机端支付密码设置
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping("/app/buyer/pay_password_setting.htm")
	public void pay_password_setting(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token,
			String pay_psw, String login_psw) {
		Map json_map = new HashMap();
		int code = 100;// 100修改成功，-100参数错误，-200用户信息错误，-300登录密码错误
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		if (user != null) {
			login_psw = Md5Encrypt.md5(login_psw).toLowerCase();
			if (user.getPassword().equals(login_psw)) {
				String temp_pay_psw = this.encodeStr(pay_psw);
				user.setMobile_pay_password(temp_pay_psw);
				this.userService.update(user);
			} else {
				code = -300;
			}
		} else {
			code = -200;
		}
		json_map.put("ret", "true");
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
	 * 加密
	 * 
	 * @param pwd
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public String encodeStr(String str) {
		byte[] enbytes = Base64.encodeBase64Chunked(str.getBytes());
		return new String(enbytes);
	}

	/**
	 * 解密
	 * 
	 * @param pwd
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public String decodeStr(String str) {
		byte[] debytes = Base64.decodeBase64(new String(str).getBytes());
		return new String(debytes);
	}

}
