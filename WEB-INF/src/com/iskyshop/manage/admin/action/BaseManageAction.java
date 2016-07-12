package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.concurrent.SessionInformation;
import org.springframework.security.concurrent.SessionRegistry;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.Log;
import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.qrcode.QRCodeUtil;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.core.tools.database.DatabaseTools;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Advert;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.GoldLog;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.IntegralGoodsOrder;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.LogType;
import com.iskyshop.foundation.domain.PredepositCash;
import com.iskyshop.foundation.domain.StorePoint;
import com.iskyshop.foundation.domain.StoreStat;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.SysLog;
import com.iskyshop.foundation.domain.SystemTip;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.SystemTipQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAdvertService;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IComplaintGoodsService;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.IDeliveryAddressService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.foundation.service.IGoldLogService;
import com.iskyshop.foundation.service.IGoldRecordService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGoodsSpecPropertyService;
import com.iskyshop.foundation.service.IGoodsSpecificationService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IIntegralGoodsOrderService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IPredepositCashService;
import com.iskyshop.foundation.service.IPredepositService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.IStoreGradeService;
import com.iskyshop.foundation.service.IStorePointService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.IStoreStatService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ISysLogService;
import com.iskyshop.foundation.service.ISystemTipService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserGoodsClassService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.StatTools;
import com.iskyshop.manage.admin.tools.StoreTools;
import com.iskyshop.module.sns.domain.SnsAttention;
import com.iskyshop.module.sns.service.ISnsAttentionService;
import com.iskyshop.msg.MsgTools;

/**
 * 
 * <p>
 * Title: BaseManageAction.java
 * </p>
 * 
 * <p>
 * Description: 平台管理基础控制，这里包含平台管理的基础方法、系统全局配置信息的保存、修改及一些系统常用请求
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
 * @date 2014-5-9
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class BaseManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IStoreGradeService storeGradeService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IAdvertService advertService;
	@Autowired
	private IPredepositService predepositService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IUserGoodsClassService ugcService;
	@Autowired
	private ISysLogService syslogService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IGroupLifeGoodsService grouplifegoodsService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGroupInfoService groupinfoService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private IPayoffLogService paylogService;
	@Autowired
	private IGoodsSpecPropertyService specpropertyService;
	@Autowired
	private IGoodsSpecificationService specService;
	@Autowired
	private IGoldLogService goldlogService;
	@Autowired
	private IFavoriteService favoriteService;
	@Autowired
	private IComplaintGoodsService complaintGoodsService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoldRecordService grService;
	@Autowired
	private IStorePointService storepointService;
	@Autowired
	private IGoldLogService glService;
	@Autowired
	private IPredepositCashService redepositcashService;
	@Autowired
	private StoreTools storeTools;
	@Autowired
	private IIntegralGoodsOrderService integralGoodsOrderService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private ISnsAttentionService snsAttentionService;
	@Autowired
	private IDeliveryAddressService deliveryAddressService;
	@Autowired
	private SessionRegistry sessionRegistry;
	@Autowired
	private IStoreStatService storeStatService;
	@Autowired
	private ISystemTipService systemTipService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private StatTools statTools;
	@Autowired
	private DatabaseTools databaseTools;

	/**
	 * 用户登录后去向控制，根据用户角色UserRole进行控制,该请求不纳入权限管理
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@Log(title = "用户登陆", type = LogType.LOGIN)
	@RequestMapping("/login_success.htm")
	public void login_success(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			if (this.configService.getSysConfig().isIntegral()) {
				if (user.getLoginDate() == null
						|| user.getLoginDate().before(
								CommUtil.formatDate(CommUtil
										.formatShortDate(new Date())))) {
					user.setIntegral(user.getIntegral()
							+ this.configService.getSysConfig()
									.getMemberDayLogin());
					IntegralLog log = new IntegralLog();
					log.setAddTime(new Date());
					log.setContent("用户"
							+ CommUtil.formatLongDate(new Date())
							+ "登录增加"
							+ this.configService.getSysConfig()
									.getMemberDayLogin() + "分");
					log.setIntegral(this.configService.getSysConfig()
							.getMemberRegister());
					log.setIntegral_user(user);
					log.setType("login");
					this.integralLogService.save(log);
				}
			}
			user.setLoginDate(new Date());
			user.setLoginIp(CommUtil.getIpAddr(request));
			user.setLoginCount(user.getLoginCount() + 1);
			this.userService.update(user);
			HttpSession session = request.getSession(false);
			session.setAttribute("user", user);
			session.setAttribute("userName", user.getUsername());
			session.setAttribute("lastLoginDate", new Date());// 设置登录时间
			session.setAttribute("loginIp", CommUtil.getIpAddr(request));// 设置登录IP
			session.setAttribute("login", true);// 设置登录标识
			String role = user.getUserRole();
			String url = CommUtil.getURL(request) + "/user_login_success.htm";
			if (!CommUtil.null2String(
					request.getSession(false).getAttribute("refererUrl"))
					.equals("")) {
				url = CommUtil.null2String(request.getSession(false)
						.getAttribute("refererUrl"));
			}
			String login_role = (String) session.getAttribute("login_role");
			boolean ajax_login = CommUtil.null2Boolean(session
					.getAttribute("ajax_login"));
			if (ajax_login) {
				response.setContentType("text/plain");
				response.setHeader("Cache-Control", "no-cache");
				response.setCharacterEncoding("UTF-8");
				PrintWriter writer;
				try {
					writer = response.getWriter();
					writer.print("success");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				if (login_role.equalsIgnoreCase("admin")) {
					if (role.indexOf("ADMIN") >= 0) {
						url = CommUtil.getURL(request) + "/admin/index.htm";
						request.getSession(false).setAttribute("admin_login",
								true);
					}
				}
				if (login_role.equalsIgnoreCase("seller")
						&& role.indexOf("SELLER") >= 0) {
					url = CommUtil.getURL(request) + "/seller/index.htm";
					request.getSession(false)
							.setAttribute("seller_login", true);
				}
				if (request.getSession(false).getAttribute("his_url") != null) {
					url = request.getSession(false).getAttribute("his_url")
							.toString();
				}
				if (!CommUtil.null2String(
						request.getSession(false).getAttribute("refererUrl"))
						.equals("")) {
					url = CommUtil.null2String(request.getSession(false)
							.getAttribute("refererUrl"));
					request.getSession(false).removeAttribute("refererUrl");
				}
				String userAgent = request.getHeader("user-agent");
				if (userAgent != null && userAgent.indexOf("Mobile") > 0) {
					url = CommUtil.getURL(request) + "/wap/index.htm";
				}
				if (!CommUtil.null2String(
						request.getSession(false).getAttribute("his_url"))
						.equals("")) {
					url = CommUtil.null2String(request.getSession(false)
							.getAttribute("his_url"));
					request.getSession(false).removeAttribute("his_url");
				}
				response.sendRedirect(url);
			}
		} else {
			String url = CommUtil.getURL(request) + "/index.htm";
			response.sendRedirect(url);
		}

	}

	/**
	 * 用户成功退出后的URL导向
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/logout_success.htm")
	public void logout_success(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		boolean admin_login = CommUtil.null2Boolean(session
				.getAttribute("admin_login"));
		boolean seller_login = CommUtil.null2Boolean(session
				.getAttribute("seller_login"));
		String targetUrl = CommUtil.getURL(request) + "/user/login.htm";
		if (admin_login) {
			targetUrl = CommUtil.getURL(request) + "/index.htm";
		}
		if (seller_login) {
			targetUrl = CommUtil.getURL(request) + "/index.htm";
		}
		
		//
		String userName = CommUtil
				.null2String(session.getAttribute("userName"));
		// System.out.println(userName);
		Object[] objs = this.sessionRegistry.getAllPrincipals();
		for (int i = 0; i < objs.length; i++) {
			if (CommUtil.null2String(objs[i]).equals(userName)) {
				SessionInformation[] ilist = this.sessionRegistry
						.getAllSessions(objs[i], true);
				for (int j = 0; j < ilist.length; j++) {
					SessionInformation sif = ilist[j];
					// 以下踢出用户
					sif.expireNow();
					this.sessionRegistry.removeSessionInformation(sif
							.getSessionId());
				}
			}
		}
		//
		session.removeAttribute("admin_login");
		session.removeAttribute("seller_login");
		session.removeAttribute("user");
		session.removeAttribute("userName");
		session.removeAttribute("login");
		session.removeAttribute("role");
		session.removeAttribute("cart");
		((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest().getSession(false).removeAttribute("user");
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && userAgent.indexOf("Mobile") > 0) {
			targetUrl = CommUtil.getURL(request) + "/wap/index.htm";
		}
		Map map = Json.fromJson(Map.class,
				session.getAttribute("weixin_bind") != null ? session
						.getAttribute("weixin_bind").toString() : "");
		if (map != null) {
			if (CommUtil.null2Boolean(map.get("login"))) {
				boolean login = CommUtil.null2Boolean(map.get("login")
						.toString());
				if (login) {
					if (map.get("userName") != null
							&& map.get("passwd") != null) {
						userName = map.get("userName").toString();
						String passwd = map.get("passwd").toString();
						request.getSession().removeAttribute("verify_code");//20150925增加，否则不能够自动登录
						targetUrl = CommUtil.getURL(request)
								+ "/iskyshop_login.htm?username="
								+ CommUtil.encode(userName) + "&password="
								+ Globals.THIRD_ACCOUNT_LOGIN + passwd
								+ "&encode=true&login_role=user";
						if (CommUtil.null2Boolean(map.get("login"))) {
							Long id = CommUtil.null2Long(map.get("userId"));
							User user = this.userService.getObjById(id);
							user.getRoles().clear();
							if (user.getStore() != null) {
							}
							for (CouponInfo ci : user.getCouponinfos()) {// 用户拥有的优惠券
								this.couponInfoService.delete(ci.getId());
							}
							user.getCouponinfos().remove(user.getCouponinfos());
							for (Accessory acc : user.getFiles()) {// 用户附件
								if (acc.getAlbum() != null) {
									if (acc.getAlbum().getAlbum_cover() != null) {
										if (acc.getAlbum().getAlbum_cover()
												.getId().equals(acc.getId())) {
											acc.getAlbum().setAlbum_cover(null);
											this.albumService.update(acc
													.getAlbum());
										}
									}
								}
								CommUtil.del_acc(request, acc);
								this.accessoryService.delete(acc.getId());
							}
							user.getFiles().removeAll(user.getFiles());
							user.getCouponinfos().remove(user.getCouponinfos());// 用户的所有购物车
							for (GoodsCart cart : user.getGoodscarts()) {
								this.goodsCartService.delete(cart.getId());
							}
							// 充值记录
							Map params = new HashMap();
							params.put("uid", user.getId());
							List<PredepositCash> PredepositCash_list = this.redepositcashService
									.query("select obj from PredepositCash obj where obj.cash_user.id=:uid",
											params, -1, -1);
							for (PredepositCash pc : PredepositCash_list) {
								this.redepositcashService.delete(pc.getId());
							}
							// 删除积分订单
							params.clear();
							params.put("user_id", user.getId());
							List<IntegralGoodsOrder> integralGoodsOrders = this.integralGoodsOrderService
									.query("select obj from IntegralGoodsOrder obj where obj.igo_user.id=:user_id",
											params, -1, -1);
							for (IntegralGoodsOrder integralGoodsOrder : integralGoodsOrders) {
								this.integralGoodsOrderService
										.delete(integralGoodsOrder.getId());
							}
							// 删除积分日志
							params.clear();
							params.put("user_id", user.getId());
							List<IntegralLog> integralLogs = this.integralLogService
									.query("select obj from IntegralLog obj where obj.integral_user.id=:user_id",
											params, -1, -1);
							for (IntegralLog integralLog : integralLogs) {
								this.integralLogService.delete(integralLog
										.getId());
							}

							params.clear();
							params.put("uid", user.getId());
							List<GoldLog> GoldLog_list = this.goldlogService
									.query("select obj from GoldLog obj where obj.gl_user.id=:uid",
											params, -1, -1);
							for (GoldLog gl : GoldLog_list) {
								this.goldlogService.delete(gl.getId());
							}
							params.clear();
							params.put("uid", user.getId());
							List<StorePoint> storepoint_list = this.storepointService
									.query("select obj from StorePoint obj where obj.user.id=:uid",
											params, -1, -1);
							for (StorePoint sp : storepoint_list) {
								this.storepointService.delete(sp.getId());
							}
							params.clear();
							params.put("uid", user.getId());// 商家广告
							List<Advert> adv_list = this.advertService
									.query("select obj from Advert obj where obj.ad_user.id=:uid",
											params, -1, -1);
							for (Advert ad : adv_list) {
								this.advertService.delete(ad.getId());
							}
							this.userService.delete(user.getId());
							// 自提点
							if (user.getDelivery_id() != null
									&& !user.getDelivery_id().equals("")) {
								this.deliveryAddressService.delete(user
										.getDelivery_id());
							}
							// 删除sns关注信息
							params.clear();
							params.put("fromUser", user.getId());
							params.put("toUser", user.getId());
							List<SnsAttention> list = this.snsAttentionService
									.query("select obj from SnsAttention obj where obj.fromUser.id=:fromUser or obj.toUser.id=:toUser",
											params, -1, -1);
							for (SnsAttention sa : list) {
								this.snsAttentionService.delete(sa.getId());
							}
							// 删除用户日志
							params.clear();
							params.put("user_id", user.getId());
							List<SysLog> logs = this.syslogService
									.query("select obj from SysLog obj where obj.user_id=:user_id",
											params, -1, -1);
							for (SysLog log : logs) {
								this.syslogService.delete(log.getId());
							}

						}
					}
				}
			}
			session.removeAttribute("weixin_bind");
		}
		response.sendRedirect(targetUrl);
	}

	@RequestMapping("/login_error.htm")
	public ModelAndView login_error(HttpServletRequest request,
			HttpServletResponse response) {
		String login_role = (String) request.getSession(false).getAttribute(
				"login_role");
		ModelAndView mv = null;
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && userAgent.indexOf("Mobile") > 0) {//手机访问
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", CommUtil.getURL(request)
					+ "/wap/index.htm");
		}else{//非手机访问
			if (login_role == null)
				login_role = "user";
			if (login_role.equalsIgnoreCase("admin")) {
				mv = new JModelAndView("admin/blue/login_error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
			}
			if (login_role.equalsIgnoreCase("seller")) {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/login.htm");
			}
			if (login_role.equalsIgnoreCase("user")) {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", CommUtil.getURL(request)
						+ "/user/login.htm");
			}
		}
		mv.addObject("op_title", "登录失败");
		return mv;
	}

	/**
	 * 管理页面
	 * 
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商城后台管理", value = "/admin/index.htm*", rtype = "admin", rname = "商城后台管理", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/index.htm")
	public ModelAndView manage(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/manage.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "欢迎页面", value = "/admin/welcome.htm*", rtype = "admin", rname = "欢迎页面", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/welcome.htm")
	public ModelAndView welcome(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/welcome.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Properties props = System.getProperties();
		mv.addObject("os", props.getProperty("os.name"));
		mv.addObject("java_version", props.getProperty("java.version"));
		mv.addObject("shop_version", Globals.DEFAULT_SHOP_VERSION);
		mv.addObject("database_version",
				this.databaseTools.queryDatabaseVersion());
		mv.addObject("web_server_version", request.getSession(false)
				.getServletContext().getServerInfo());
		List<StoreStat> stats = this.storeStatService.query(
				"select obj from StoreStat obj order by obj.addTime desc",
				null, -1, -1);
		Map params = new HashMap();
		params.put("st_status", 0);
		List<SystemTip> sts = this.systemTipService
				.query("select obj from SystemTip obj where obj.st_status=:st_status order by obj.st_level desc",
						params, -1, -1);
		StoreStat stat = null;
		if (stats.size() > 0) {
			stat = stats.get(0);
		} else {
			stat = new StoreStat();
		}
		mv.addObject("stat", stat);
		mv.addObject("sts", sts);
		return mv;
	}

	@SecurityMapping(title = "系统提醒页", value = "/admin/sys_tip_list.htm*", rtype = "admin", rname = "系统提示页", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/sys_tip_list.htm")
	public ModelAndView sys_tip_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/sys_tip_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SystemTipQueryObject qo = new SystemTipQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.setOrderBy("st_status asc,obj.st_level desc,obj.addTime");
		qo.setOrderType("desc");
		IPageList pList = this.systemTipService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "系统提醒删除", value = "/admin/sys_tip_del.htm*", rtype = "admin", rname = "系统提示页", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/sys_tip_del.htm")
	public String sys_tip_del(HttpServletRequest request, String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				SystemTip st = this.systemTipService.getObjById(CommUtil
						.null2Long(id));
				this.systemTipService.delete(Long.parseLong(id));
			}
		}
		return "redirect:sys_tip_list.htm";
	}

	@SecurityMapping(title = "系统提醒处理", value = "/admin/sys_tip_do.htm*", rtype = "admin", rname = "系统提示页", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/sys_tip_do.htm")
	public String sys_tip_do(HttpServletRequest request, String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				SystemTip st = this.systemTipService.getObjById(CommUtil
						.null2Long(id));
				st.setSt_status(1);
				this.systemTipService.save(st);
			}
		}
		return "redirect:sys_tip_list.htm";
	}

	@SecurityMapping(title = "关于我们", value = "/admin/aboutus.htm*", rtype = "admin", rname = "关于我们", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/aboutus.htm")
	public ModelAndView aboutus(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/aboutus.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "站点设置", value = "/admin/set_site.htm*", rtype = "admin", rname = "站点设置", rcode = "admin_set_site", rgroup = "设置")
	@RequestMapping("/admin/set_site.htm")
	public ModelAndView site_set(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_site_setting.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "上传设置", value = "/admin/set_image.htm*", rtype = "admin", rname = "上传设置", rcode = "admin_set_image", rgroup = "设置")
	@RequestMapping("/admin/set_image.htm")
	public ModelAndView set_image(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/set_image_setting.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "保存商城配置", value = "/admin/sys_config_save.htm*", rtype = "admin", display = false, rname = "保存商城配置", rcode = "admin_config_save", rgroup = "设置")
	@RequestMapping("/admin/sys_config_save.htm")
	public ModelAndView sys_config_save(HttpServletRequest request,
			HttpServletResponse response, String id, String list_url,
			String op_title, String app_download, String android_download,
			String ios_download, String android_seller_download,
			String ios_seller_download, String app_seller_download) {
		SysConfig obj = this.configService.getSysConfig();
		WebForm wf = new WebForm();
		SysConfig sysConfig = null;
		if (id.equals("")) {
			sysConfig = wf.toPo(request, SysConfig.class);
			sysConfig.setAddTime(new Date());
		} else {
			sysConfig = (SysConfig) wf.toPo(request, obj);
		}
		if (sysConfig.getAddress() != null
				&& !sysConfig.getAddress().equals("")) {
			String address = sysConfig.getAddress();
			if (address.indexOf("http://") < 0) {
				address = "http://" + address;
				sysConfig.setAddress(address);
			}
		}
		// 图片上传开始logo
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "system";
		CommUtil.createFolder(saveFilePathName);
		Map map = new HashMap();
		try {
			String fileName = this.configService.getSysConfig()
					.getWebsiteLogo() == null ? "" : this.configService
					.getSysConfig().getWebsiteLogo().getName();
			map = CommUtil.saveFileToServer(request, "websiteLogo",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory logo = new Accessory();
					logo.setName(CommUtil.null2String(map.get("fileName")));
					logo.setExt((String) map.get("mime"));
					logo.setSize(BigDecimal.valueOf((CommUtil.null2Double(map
							.get("fileSize")))));
					logo.setPath(uploadFilePath + "/system");
					logo.setWidth(CommUtil.null2Int(map.get("width")));
					logo.setHeight(CommUtil.null2Int(map.get("heigh")));
					logo.setAddTime(new Date());
					this.accessoryService.save(logo);
					sysConfig.setWebsiteLogo(logo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory logo = sysConfig.getWebsiteLogo();
					logo.setName(CommUtil.null2String(map.get("fileName")));
					logo.setExt(CommUtil.null2String(map.get("mime")));
					logo.setSize(BigDecimal.valueOf((CommUtil.null2Double(map
							.get("fileSize")))));
					logo.setPath(uploadFilePath + "/system");
					logo.setWidth(CommUtil.null2Int(map.get("width")));
					logo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(logo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 默认商品图片
		map.clear();
		try {
			map = CommUtil.saveFileToServer(request, "goodsImage",
					saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getGoodsImage() == null ? ""
					: this.configService.getSysConfig().getGoodsImage()
							.getName();
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("heigh")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					sysConfig.setGoodsImage(photo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory photo = sysConfig.getGoodsImage();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 默认店铺标识
		map.clear();
		try {
			map = CommUtil.saveFileToServer(request, "storeImage",
					saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getStoreImage() == null ? ""
					: this.configService.getSysConfig().getStoreImage()
							.getName();
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt((String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("heigh")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					sysConfig.setStoreImage(photo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory photo = sysConfig.getStoreImage();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 默认会员图片
		map.clear();
		try {
			map = CommUtil.saveFileToServer(request, "memberIcon",
					saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getMemberIcon() == null ? ""
					: this.configService.getSysConfig().getMemberIcon()
							.getName();
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt((String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("heigh")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					sysConfig.setMemberIcon(photo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory photo = sysConfig.getMemberIcon();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 平台登录Logo
		map.clear();
		try {
			map = CommUtil.saveFileToServer(request, "admin_login_img",
					saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig()
					.getAdmin_login_logo() == null ? "" : this.configService
					.getSysConfig().getAdmin_login_logo().getName();
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt((String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("heigh")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					sysConfig.setAdmin_login_logo(photo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory photo = sysConfig.getAdmin_login_logo();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 平台管理Logo
		map.clear();
		try {
			map = CommUtil.saveFileToServer(request, "admin_manage_img",
					saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig()
					.getAdmin_manage_logo() == null ? "" : this.configService
					.getSysConfig().getAdmin_manage_logo().getName();
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt((String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("heigh")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					sysConfig.setAdmin_manage_logo(photo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory photo = sysConfig.getAdmin_manage_logo();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 平台上传ico
		map.clear();
		try {
			map = CommUtil.saveFileToServer(request, "website_ico",
					saveFilePathName, "favicon.ico", null);
			String fileName = this.configService.getSysConfig()
					.getWebsite_ico() == null ? "" : this.configService
					.getSysConfig().getWebsite_ico().getName();

			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt((String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("heigh")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					sysConfig.setWebsite_ico(photo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory photo = sysConfig.getWebsite_ico();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/system");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 上传系统二维码中心Logo图片
		map.clear();
		try {
			map = CommUtil.saveFileToServer(request, "qrLogo",
					saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getQr_logo() == null ? ""
					: this.configService.getSysConfig().getQr_logo().getName();
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory logo = new Accessory();
					logo.setName((String) map.get("fileName"));
					logo.setExt((String) map.get("mime"));
					logo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					logo.setPath(uploadFilePath + "/system");
					logo.setWidth(CommUtil.null2Int(map.get("width")));
					logo.setHeight(CommUtil.null2Int(map.get("heigh")));
					logo.setAddTime(new Date());
					this.accessoryService.save(logo);
					sysConfig.setQr_logo(logo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory logo = sysConfig.getQr_logo();
					logo.setName(CommUtil.null2String(map.get("fileName")));
					logo.setExt(CommUtil.null2String(map.get("mime")));
					logo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					logo.setPath(uploadFilePath + "/system");
					logo.setWidth(CommUtil.null2Int(map.get("width")));
					logo.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(logo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 手机app
		if(!"".equals(app_download)&&app_download!=null){
		sysConfig.setApp_download(CommUtil.null2Int(app_download));
		sysConfig.setAndroid_download(android_download);
		sysConfig.setIos_download(ios_download);
		}
		if (CommUtil.null2Int(app_download) == 1) {// 开启app下载生成下载链接二维码
			String destPath = System.getProperty("iskyshopb2b2c.root")
					+ uploadFilePath + File.separator + "app";
			if (!CommUtil.fileExist(destPath)) {
				CommUtil.createFolder(destPath);
			}
			String logoPath = "";
			if (this.configService.getSysConfig().getQr_logo() != null) {
				logoPath = request.getSession().getServletContext()
						.getRealPath("/")
						+ this.configService.getSysConfig().getQr_logo()
								.getPath()
						+ File.separator
						+ this.configService.getSysConfig().getQr_logo()
								.getName();
			}
			String download_url = CommUtil.getURL(request)
					+ "/app_download.htm";
			QRCodeUtil.encode(download_url, logoPath, destPath + File.separator
					+ "app_dowload.png", true);
		}
		// 商家app
		if(!"".equals(app_seller_download)&&app_seller_download!=null){
		sysConfig.setApp_seller_download(CommUtil.null2Int(app_seller_download));
		sysConfig.setAndroid_seller_download(android_seller_download);
		sysConfig.setIos_seller_download(ios_seller_download);
		}
		if (CommUtil.null2Int(app_seller_download) == 1) {// 开启商家app下载生成下载链接二维码
			String destPath = System.getProperty("iskyshopb2b2c.root")
					+ uploadFilePath + File.separator + "app";
			if (!CommUtil.fileExist(destPath)) {
				CommUtil.createFolder(destPath);
			}
			String logoPath = "";
			if (this.configService.getSysConfig().getQr_logo() != null) {
				logoPath = request.getSession().getServletContext()
						.getRealPath("/")
						+ this.configService.getSysConfig().getQr_logo()
								.getPath()
						+ File.separator
						+ this.configService.getSysConfig().getQr_logo()
								.getName();
			}
			String download_url = CommUtil.getURL(request)
					+ "/app_seller_download.htm";
			QRCodeUtil.encode(download_url, logoPath, destPath + File.separator
					+ "app_seller_download.png", true);
		}
		if (sysConfig.getHotSearch() != null
				&& !sysConfig.getHotSearch().equals("")) {
			sysConfig.setHotSearch(sysConfig.getHotSearch()
					.replaceAll("，", ","));// 替换全角分隔号
		}
		if (sysConfig.getKeywords() != null
				&& !sysConfig.getKeywords().equals("")) {
			sysConfig.setKeywords(sysConfig.getKeywords().replaceAll("，", ","));
		}
		// 处理运行上传文件名的后缀,不允许一下后缀名被修改为可以上传的文件
		String imageSuffix = sysConfig.getImageSuffix();
		String[] suffix_list = new String[] { "php", "asp", "jsp", "html",
				"htm", "cgi", "action", "js", "css" };
		for (String suffix : suffix_list) {
			imageSuffix = imageSuffix.replaceAll(suffix, "");
		}
		sysConfig.setImageSuffix(imageSuffix);
		if (id.equals("")) {
			this.configService.save(sysConfig);
		} else {
			this.configService.update(sysConfig);
		}
		for (int i = 0; i < 4; i++) {
			try {
				map.clear();
				String fileName = "";
				if (sysConfig.getLogin_imgs().size() > i) {
					fileName = sysConfig.getLogin_imgs().get(i).getName();
				}
				map = CommUtil.saveFileToServer(request, "img" + i,
						saveFilePathName, fileName, null);
				if (fileName.equals("")) {
					if (map.get("fileName") != "") {
						Accessory img = new Accessory();
						img.setName(CommUtil.null2String(map.get("fileName")));
						img.setExt((String) map.get("mime"));
						img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
								.get("fileSize"))));
						img.setPath(uploadFilePath + "/system");
						img.setWidth((Integer) map.get("width"));
						img.setHeight((Integer) map.get("height"));
						img.setAddTime(new Date());
						img.setConfig(sysConfig);
						this.accessoryService.save(img);
					}
				} else {
					if (map.get("fileName") != "") {
						Accessory img = sysConfig.getLogin_imgs().get(i);
						img.setName(CommUtil.null2String(map.get("fileName")));
						img.setExt(CommUtil.null2String(map.get("mime")));
						img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
								.get("fileSize"))));
						img.setPath(uploadFilePath + "/system");
						img.setWidth(CommUtil.null2Int(map.get("width")));
						img.setHeight(CommUtil.null2Int(map.get("height")));
						img.setConfig(sysConfig);
						this.accessoryService.update(img);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", op_title);
		mv.addObject("list_url", list_url);
		return mv;
	}

	@SecurityMapping(title = "Email设置", value = "/admin/set_email.htm*", rtype = "admin", rname = "Email设置", rcode = "admin_set_email", rgroup = "设置")
	@RequestMapping("/admin/set_email.htm")
	public ModelAndView set_email(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/set_email_setting.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "短信设置", value = "/admin/set_sms.htm*", rtype = "admin", rname = "短信设置", rcode = "admin_set_sms", rgroup = "设置")
	@RequestMapping("/admin/set_sms.htm")
	public ModelAndView set_sms(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_sms_setting.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "SEO设置", value = "/admin/set_seo.htm*", rtype = "admin", rname = "SEO设置", rcode = "admin_set_seo", rgroup = "设置")
	@RequestMapping("/admin/set_seo.htm")
	public ModelAndView set_seo(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/set_seo_setting.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "二级域名设置", value = "/admin/set_second_domain.htm*", rtype = "admin", rname = "二级域名", rcode = "admin_set_second_domain", rgroup = "设置")
	@RequestMapping("/admin/set_second_domain.htm")
	public ModelAndView set_second_domain(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/set_second_domain.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "二级域名设置保存", value = "/admin/set_second_domain_save.htm*", rtype = "admin", rname = "二级域名", rcode = "admin_set_second_domain", rgroup = "设置")
	@RequestMapping("/admin/set_second_domain_save.htm")
	public ModelAndView set_second_domain_save(HttpServletRequest request,
			HttpServletResponse response, String id, String domain_allow_count,
			String sys_domain, String second_domain_open) {
		String serverName = request.getServerName().toLowerCase();
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		// System.out.println("二级域名："+Globals.SSO_SIGN);
		if (Globals.SSO_SIGN) {
			SysConfig config = this.configService.getSysConfig();
			config.setDomain_allow_count(CommUtil.null2Int(domain_allow_count));
			config.setSys_domain(sys_domain);
			config.setSecond_domain_open(CommUtil
					.null2Boolean(second_domain_open));
			if (id.equals("")) {
				this.configService.save(config);
			} else
				this.configService.update(config);
			mv.addObject("op_title", "二级域名保存成功");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/set_second_domain.htm");
		} else {
			SysConfig config = this.configService.getSysConfig();
			config.setDomain_allow_count(CommUtil.null2Int(domain_allow_count));
			config.setSys_domain(sys_domain);
			config.setSecond_domain_open(false);
			if (id.equals("")) {
				this.configService.save(config);
			} else
				this.configService.update(config);
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "当前网站无法开启二级域名");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/set_second_domain.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "QQ互联登录", value = "/admin/set_site_qq.htm*", rtype = "admin", rname = "二级域名", rcode = "admin_set_second_domain", rgroup = "设置")
	@RequestMapping("/admin/set_site_qq.htm")
	public ModelAndView set_site_qq(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/set_second_domain.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	/**
	 * 管理员退出，清除管理员权限数据,退出后，管理员可以作为普通登录用户进行任意操作，该请求在前台将不再使用，保留以供二次开发使用
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/admin/logout.htm")
	public String logout(HttpServletRequest request,
			HttpServletResponse response) {
		User user = SecurityUserHolder.getCurrentUser();
		if (user != null) {
			Authentication authentication = new UsernamePasswordAuthenticationToken(
					SecurityContextHolder.getContext().getAuthentication()
							.getPrincipal(), SecurityContextHolder.getContext()
							.getAuthentication().getCredentials(),
					user.get_common_Authorities());
			SecurityContextHolder.getContext()
					.setAuthentication(authentication);
		}
		return "redirect:../index.htm";
	}

	/**
	 * 登录页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/admin/login.htm")
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/login.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		request.getSession(false).removeAttribute("verify_code");
		if (user != null) {
			mv.addObject("user", user);
		}
		return mv;
	}

	@RequestMapping("/success.htm")
	public ModelAndView success(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title",
				request.getSession(false).getAttribute("op_title"));
		mv.addObject("url", request.getSession(false).getAttribute("url"));
		request.getSession(false).removeAttribute("op_title");
		request.getSession(false).removeAttribute("url");
		return mv;
	}

	/**
	 * 默认错误页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/error.htm")
	public ModelAndView error(HttpServletRequest request,
			HttpServletResponse response) {
		User user = SecurityUserHolder.getCurrentUser();
		ModelAndView mv = new JModelAndView("error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (user != null && user.getUserRole().equalsIgnoreCase("ADMIN")) {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);

		}
		mv.addObject("op_title",
				request.getSession(false).getAttribute("op_title"));
		mv.addObject("list_url", request.getSession(false).getAttribute("url"));
		mv.addObject("url", request.getSession(false).getAttribute("url"));
		request.getSession(false).removeAttribute("op_title");
		request.getSession(false).removeAttribute("url");
		return mv;
	}

	/**
	 * 默认异常出现
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/exception.htm")
	public ModelAndView exception(HttpServletRequest request,
			HttpServletResponse response) {
		User user = (User) request.getSession().getAttribute("user");
		ModelAndView mv = new JModelAndView("error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (user != null && user.getUserRole().equalsIgnoreCase("ADMIN")) {
			mv = new JModelAndView("admin/blue/exception.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		} else {
			mv.addObject("op_title", "系统出现异常");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	/**
	 * 超级后台默认无权限页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/authority.htm")
	public ModelAndView authority(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/authority.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		boolean domain_error = CommUtil.null2Boolean(request.getSession(false)
				.getAttribute("domain_error"));
		if (domain_error) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "域名绑定错误，请与http://www.iskyshop.com联系");
		}
		return mv;
	}

	/**
	 * 语言验证码处理
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/voice.htm")
	public ModelAndView voice(HttpServletRequest request,
			HttpServletResponse response) {
		return new JModelAndView("include/flash/soundPlayer.swf",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), request, response);
	}

	/**
	 * flash获取验证码
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/getCode.htm")
	public void getCode(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		HttpSession session = request.getSession(false);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter writer = response.getWriter();
		writer.print("result=true&code="
				+ (String) session.getAttribute("verify_code"));
	}

	/**
	 * 初始化系统相关图片，如商品默认图等，管理员修改后可以选择恢复默认
	 * 
	 * @param request
	 * @param response
	 * @param type
	 * @throws IOException
	 */
	@SecurityMapping(title = "初始化系统默认图片", value = "/admin/restore_img.htm*", rtype = "admin", rname = "商城后台管理", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/restore_img.htm")
	public void restore_img(HttpServletRequest request,
			HttpServletResponse response, String type) throws IOException {
		SysConfig config = this.configService.getSysConfig();
		Map map = new HashMap();
		if (type.equals("member")) {// 恢复系统默认会员头像
			Accessory acc = config.getMemberIcon();
			if (acc == null) {
				acc = new Accessory();
			} else {
				acc = config.getMemberIcon();
			}
			acc.setPath("resources/style/common/images");
			acc.setName("member.jpg");
			config.setMemberIcon(acc);
			this.configService.update(config);
			map.put("path", CommUtil.getURL(request)
					+ "/resources/style/common/images/member.jpg");
		}
		if (type.equals("goods")) {// 恢复系统默认商品头像
			Accessory acc = config.getGoodsImage();
			if (acc == null) {
				acc = new Accessory();
			} else {
				acc = config.getGoodsImage();
			}
			acc.setPath("resources/style/common/images");
			acc.setName("good.jpg");
			config.setGoodsImage(acc);
			this.configService.update(config);
			map.put("path", CommUtil.getURL(request)
					+ "/resources/style/common/images/good.jpg");
		}
		if (type.equals("store")) {// 恢复系统默认店铺头像
			Accessory acc = config.getStoreImage();
			if (acc == null) {
				acc = new Accessory();
			} else {
				acc = config.getStoreImage();
			}
			acc.setPath("resources/style/common/images");
			acc.setName("store.jpg");
			config.setStoreImage(acc);
			this.configService.update(config);
			map.put("path", CommUtil.getURL(request)
					+ "/resources/style/common/images/store.jpg");
		}
		if (type.equals("admin_login_img")) {// 恢复平台管理登录页左上角Logo
			Accessory acc = config.getAdmin_login_logo();
			config.setAdmin_login_logo(null);
			this.configService.update(config);
			if (acc != null) {
				this.accessoryService.delete(acc.getId());
			}
			map.put("path",
					CommUtil.getURL(request)
							+ "/resources/style/system/manage/blue/images/login/login_logo.png");
		}
		if (type.equals("admin_manage_img")) {// 恢复平台管理中心左上角的Logo
			Accessory acc = config.getAdmin_manage_logo();
			config.setAdmin_manage_logo(null);
			this.configService.update(config);
			if (acc != null) {
				this.accessoryService.delete(acc.getId());
			}
			map.put("path", CommUtil.getURL(request)
					+ "/resources/style/system/manage/blue/images/logo.png");
		}
		map.put("type", type);
		HttpSession session = request.getSession(false);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter writer = response.getWriter();
		writer.print(Json.toJson(map, JsonFormat.compact()));
	}

	/**
	 * 系统编辑器图片上传
	 * 
	 * @param request
	 * @param response
	 * @throws ClassNotFoundException
	 */
	@RequestMapping("/upload.htm")
	public void upload(HttpServletRequest request, HttpServletResponse response)
			throws ClassNotFoundException {
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ this.configService.getSysConfig().getUploadFilePath()
				+ File.separator + "common";
		String webPath = request.getContextPath().equals("/") ? "" : request
				.getContextPath();
		if (this.configService.getSysConfig().getAddress() != null
				&& !this.configService.getSysConfig().getAddress().equals("")) {
			webPath = this.configService.getSysConfig().getAddress() + webPath;
		}
		JSONObject obj = new JSONObject();
		try {
			Map map = CommUtil.saveFileToServer(request, "imgFile",
					saveFilePathName, null, null);
			String url = webPath + "/"
					+ this.configService.getSysConfig().getUploadFilePath()
					+ "/common/" + map.get("fileName");
			obj.put("error", 0);
			obj.put("url", url);
		} catch (IOException e) {
			obj.put("error", 1);
			obj.put("message", e.getMessage());
			e.printStackTrace();
		}
		response.setContentType("text/html");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(obj.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@RequestMapping("/js.htm")
	public ModelAndView js(HttpServletRequest request,
			HttpServletResponse response, String js) {
		ModelAndView mv = new JModelAndView("resources/js/" + js + ".js",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 2, request, response);
		return mv;
	}

	@RequestMapping("/admin/test_mail.htm")
	public void test_email(HttpServletResponse response, String email) {
		String subject = this.configService.getSysConfig().getTitle() + "测试邮件";
		boolean ret = this.msgTools.sendEmail(email, subject, subject);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping("/admin/test_sms.htm")
	public void test_sms(HttpServletResponse response, String mobile)
			throws UnsupportedEncodingException {
		String content = this.configService.getSysConfig().getTitle()
				+ "亲,如果您收到短信，说明发送成功！";
		boolean ret = this.msgTools.sendSMS(mobile, content);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 商城平台样式设置，默认样式为blue
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "websiteCss设置", value = "/admin/set_websiteCss.htm*", rtype = "admin", rname = "站点设置", rcode = "admin_set_site", rgroup = "设置")
	@RequestMapping("/admin/set_websiteCss.htm")
	public void set_websiteCss(HttpServletRequest request,
			HttpServletResponse response, String webcss) {
		SysConfig obj = this.configService.getSysConfig();
		if (!webcss.equals("blue") && !webcss.equals("black")) {
			webcss = "blue";
		}
		obj.setWebsiteCss(webcss);
		this.configService.update(obj);
	}

}
