package com.iskyshop.module.app.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpException;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.foundation.domain.Album;
import com.iskyshop.foundation.domain.Document;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.Role;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.VerifyCode;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IDocumentService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IVerifyCodeService;
import com.iskyshop.module.app.domain.QRLogin;
import com.iskyshop.module.app.service.IQRLoginService;
import com.iskyshop.msg.MsgTools;

/**
 * 
 * <p>
 * Title: MobileLoginViewAction.java
 * </p>
 * 
 * <p>
 * Description: 手机端登录请求管理类
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
 * @author hezeng、erikzhang
 * 
 * @date 2014-7-22
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class AppLoginViewAction {
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
	 * 手机客户端用户登录
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/app/iskyshop_user_login.htm")
	public void app_login(HttpServletRequest request,
			HttpServletResponse response, String userName, String password) {
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
					if (!u.getPassword().equals(password)) {
						code = "-200";
					} else {
						if (u.getUserRole().equalsIgnoreCase("admin")) {
							code = "-100";
						} else {
							user_id = CommUtil.null2String(u.getId());
							user_name = u.getUserName();
							code = "100";
							login_token = CommUtil.randomString(12) + user_id;
							u.setApp_login_token(login_token.toLowerCase());
							this.userService.update(u);
							login_user = u;
							break;
						}
					}
				}
			} else {
				code = "-100";
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
	 * 手机客户端注册完成
	 * 
	 * @param request
	 * @param userName
	 * @param password
	 * @param email
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	@RequestMapping("/app/register_finish.htm")
	public void app_register(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String type, String mobile, String verify_code)
			throws HttpException, IOException {
		boolean verify = true;
		boolean reg = true;// 防止机器注册，如后台开启验证码则强行验证验证码
		int code = 100;// 100注册成功，-100，手机注册时验证码错误，-200用户名已存在
		Map params = new HashMap();
		Map json_map = new HashMap();
		User user = new User();
		String login_token = "";
		if (CommUtil.null2String(type).equals("mobile")) {
			userName = mobile;
			password = "123456";
			code = this.app_verify_mobile_code(verify_code, mobile);
			if (code == -100) {
				reg = false;
			}
		}
		if (verify) {// 头文件验证成功
			// 进一步控制用户名不能重复，防止在未开启注册码的情况下注册机恶意注册
			params.put("userName", userName);
			params.put("mobile", userName);
			List<User> users = this.userService
					.query("select obj from User obj where obj.userName=:userName or obj.mobile=:mobile",
							params, -1, -1);
			if (users != null && users.size() > 0) {
				reg = false;
				code = -200;
			}
			if (reg) {
				user.setUserName(userName);
				user.setUserRole("BUYER");
				user.setAddTime(new Date());
				user.setAvailableBalance(BigDecimal.valueOf(0));
				user.setFreezeBlance(BigDecimal.valueOf(0));
				// 生成token
				login_token = CommUtil.randomString(12)
						+ CommUtil.null2String(user.getId());
				user.setApp_login_token(login_token.toLowerCase());
				user.setPassword(Md5Encrypt.md5(password).toLowerCase());
				if (type != null && type.equals("mobile")) {
					user.setMobile(mobile);
				}
				params.clear();
				params.put("type", "BUYER");
				List<Role> roles = this.roleService.query(
						"select obj from Role obj where obj.type=:type",
						params, -1, -1);
				user.getRoles().addAll(roles);
				if (this.configService.getSysConfig().isIntegral()) {
					user.setIntegral(this.configService.getSysConfig()
							.getMemberRegister());
					this.userService.save(user);
					IntegralLog log = new IntegralLog();
					log.setAddTime(new Date());
					log.setContent("用户注册增加"
							+ this.configService.getSysConfig()
									.getMemberRegister() + "分");
					log.setIntegral(this.configService.getSysConfig()
							.getMemberRegister());
					log.setIntegral_user(user);
					log.setType("reg");
					this.integralLogService.save(log);
				} else {
					this.userService.save(user);
				}
				// 创建用户默认相册
				Album album = new Album();
				album.setAddTime(new Date());
				album.setAlbum_default(true);
				album.setAlbum_name("默认相册");
				album.setAlbum_sequence(-10000);
				album.setUser(user);
				this.albumService.save(album);
				request.getSession(false).removeAttribute("verify_code");
				json_map.put("verify", this.create_appverify(user));
				json_map.put("user_id", user.getId().toString());
				json_map.put("userName", user.getUserName());
				json_map.put("token", login_token);
				// 如果手机号码注册，注册成功后清除验证码信息
				if (type != null && type.equals("mobile")) {
					Map mvc_params = new HashMap();
					mvc_params.put("mobile", mobile);
					List<VerifyCode> mvcs = this.mobileverifycodeService
							.query("select obj from VerifyCode obj where obj.mobile=:mobile ",
									mvc_params, -1, -1);
					for (VerifyCode mvc : mvcs) {
						this.mobileverifycodeService.delete(mvc.getId());
					}
				}
			}
		} else {
			reg = false;
		}
		json_map.put("code", code);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(json_map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 手机客户端查看注册协议
	 * 
	 */
	@RequestMapping("/app/register_doc.htm")
	public ModelAndView app_register_doc(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("app/doc.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Document doc = this.documentService.getObjByProperty(null, "mark",
				"reg_agree");
		mv.addObject("doc", doc);
		return mv;
	}

	private int app_verify_mobile_code(String verify_code, String mobile) {
		int code = -100;// 100验证成功，-100，验证失败
		Map params = new HashMap();
		params.put("mobile", mobile);
		List<VerifyCode> mvcs = this.mobileverifycodeService.query(
				"select obj from VerifyCode obj where obj.mobile=:mobile ",
				params, -1, -1);
		if (mvcs.size() > 0) {
			VerifyCode mv = mvcs.get(0);
			if (mv.getCode().equals(verify_code)) {
				code = 100;
			}
		}
		return code;
	}

	/**
	 * 手机发送注册验证码
	 * 
	 * @param request
	 * @param response
	 * @param type
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/app/send_register_code.htm")
	public void send_register_code(HttpServletRequest request,
			HttpServletResponse response, String mobile)
			throws UnsupportedEncodingException {
		Map json_map = new HashMap();
		String ret = "100";// 100发送成功，200，发送失败，300系统未开启短信功能
		String code = CommUtil.randomInt(6);
		String content = "您正在使用手机号注册，您的验证码为：" + code + "。["
				+ this.configService.getSysConfig().getTitle() + "]";
		if (mobile != null && !mobile.equals("")) {
			if (this.configService.getSysConfig().isSmsEnbale()) {
				boolean ret1 = this.msgTools.sendSMS(mobile, content);
				if (ret1) {
					// 删除所有该号码code
					Map params = new HashMap();
					params.put("mobile", mobile);
					List<VerifyCode> codes = this.mobileverifycodeService
							.query("select obj from VerifyCode obj where obj.mobile=:mobile ",
									params, -1, -1);
					for (VerifyCode cd : codes) {
						this.mobileverifycodeService.delete(cd.getId());
					}
					// 每次新建一个mvc
					VerifyCode mvc = new VerifyCode();
					mvc.setAddTime(new Date());
					mvc.setCode(code);
					json_map.put("code", code);// 用于测试
					mvc.setMobile(mobile);
					this.mobileverifycodeService.update(mvc);
				} else {
					ret = "200";
				}
			} else {
				ret = "300";
			}
		} else {
			code = "200";
		}
		json_map.put("ret", ret);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(json_map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 手机app扫描二维码登录，要求手机客户端必须为登录状态
	 */
	@RequestMapping("/app/buyer/app_qr_login.htm")
	public void app_qr_login(HttpServletRequest request,
			HttpServletResponse response, String user_id, String qr_id) {
		Map json_map = new HashMap();
		String ret = "100";
		QRLogin qrlogin = new QRLogin();
		qrlogin.setAddTime(new Date());
		qrlogin.setUser_id(user_id);
		qrlogin.setQr_session_id(qr_id);
		this.qrLoginService.save(qrlogin);
		json_map.put("ret", ret);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(json_map, JsonFormat.compact()));
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

	/**
	 * 找回密码第一步
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/app/forget1.htm")
	public ModelAndView forget1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("app/forget1.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		SysConfig config = this.configService.getSysConfig();
		if (!config.isEmailEnable() && !config.isSmsEnbale()) {
			mv = new JModelAndView("app/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统关闭邮件及手机短信功能，不能找回密码");
			mv.addObject("url", CommUtil.getURL(request) + "/app/forget1.htm");
		}
		return mv;
	}

	/**
	 * 找回密码第二步
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/app/forget2.htm")
	public ModelAndView forget2(HttpServletRequest request,
			HttpServletResponse response, String userName) {
		ModelAndView mv = new JModelAndView("app/forget2.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		SysConfig config = this.configService.getSysConfig();
		if (!config.isEmailEnable() && !config.isSmsEnbale()) {
			mv = new JModelAndView("app/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统关闭邮件及手机短信功能，不能找回密码");
			mv.addObject("url", CommUtil.getURL(request) + "/app/forget1.htm");
		} else {
			Map params = new HashMap();
			params.put("userName", userName);
			params.put("email", userName);
			params.put("mobile", userName);
			List<User> users = this.userService
					.query("select obj from User obj where obj.userName =:userName or obj.email=:email or obj.mobile =:mobile",
							params, -1, -1);
			if (users.size() > 0) {
				User user = users.get(0);
				mv.addObject("user", user);
				if (!CommUtil.null2String(user.getEmail()).equals("")) {
					String email = user.getEmail();
					int i = email.indexOf("@");
					if (i > 2) {
						email = email.substring(0, 4) + "***"
								+ email.substring(i, email.length());
					} else {
						email = "***" + email.substring(i, email.length());
					}
					mv.addObject("method_email", "使用邮箱" + email + "找回");
				}
				if (!CommUtil.null2String(user.getMobile()).equals("")) {
					String mobile = user.getMobile();
					mobile = mobile.substring(0, 3) + "****"
							+ mobile.substring(7, 11);
					mv.addObject("method_mobile", "使用手机号码" + mobile + "找回");
				}
				if (CommUtil.null2String(user.getMobile()).equals("")
						&& CommUtil.null2String(user.getEmail()).equals("")) {
					mv = new JModelAndView("app/error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "用户没有绑定邮箱和手机，无法找回");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/app/forget1.htm");
				}

			} else {
				mv = new JModelAndView("app/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "不存在该用户");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/app/forget1.htm");
			}
		}
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 * @return
	 */
	@RequestMapping("/app/forget3.htm")
	public ModelAndView forget3(HttpServletRequest request,
			HttpServletResponse response, String accept_type, String email,
			String mobile, String userName, String verify_code) {
		ModelAndView mv = new JModelAndView("app/forget3.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (accept_type.equals("email")) {
			VerifyCode vc = this.mobileverifycodeService.getObjByProperty(null,
					"email", email);
			if (vc != null) {
				if (!vc.getCode().equals(verify_code)) {
					mv = new JModelAndView("app/error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "验证码输入错误");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/app/forget2.htm?userName=" + userName);
				} else {
					String verify_session = CommUtil.randomString(64)
							.toLowerCase();
					mv.addObject("verify_session", verify_session);
					request.getSession(false).setAttribute("verify_session",
							verify_session);
					mv.addObject("userName", userName);
					this.mobileverifycodeService.delete(vc.getId());
				}
			} else {
				mv = new JModelAndView("app/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "验证码输入错误");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/app/forget2.htm?userName=" + userName);
			}
		} else if (accept_type.equals("mobile")) {
			VerifyCode vc = this.mobileverifycodeService.getObjByProperty(null,
					"mobile", mobile);
			if (vc != null) {
				if (!vc.getCode().equals(verify_code)) {
					mv = new JModelAndView("app/error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "验证码输入错误");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/app/forget2.htm?userName=" + userName);
				} else {
					String verify_session = CommUtil.randomString(64)
							.toLowerCase();
					mv.addObject("verify_session", verify_session);
					request.getSession(false).setAttribute("verify_session",
							verify_session);
					mv.addObject("userName", userName);
					this.mobileverifycodeService.delete(vc.getId());
				}
			} else {
				mv = new JModelAndView("app/error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "验证码输入错误");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/app/forget2.htm?userName=" + userName);
			}
		} else {
			mv = new JModelAndView("app/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "验证错误，请重试");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/app/forget2.htm?userName=" + userName);
		}
		return mv;
	}

	@RequestMapping("/app/forget4.htm")
	public ModelAndView forget4(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String verify_session) {
		ModelAndView mv = new JModelAndView("app/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String verify_session1 = CommUtil.null2String(request.getSession(false)
				.getAttribute("verify_session"));
		if (!verify_session1.equals("")
				&& verify_session1.equals(verify_session)) {
			User user = this.userService.getObjByProperty(null, "userName",
					userName);
			user.setPassword(Md5Encrypt.md5(password).toLowerCase());
			this.userService.update(user);
			request.getSession(false).removeAttribute("verify_session");
			mv.addObject("op_title", "密码修改成功，请使用新密码登录");
		} else {
			mv = new JModelAndView("app/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "会话过期，找回密码失败");
			mv.addObject("url", CommUtil.getURL(request) + "/app/forget1.htm");
		}
		return mv;
	}

}
