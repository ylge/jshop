package com.iskyshop.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.GoldLog;
import com.iskyshop.foundation.domain.SmsGoldLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.Template;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.SmsGoldLogQueryObject;
import com.iskyshop.foundation.service.IGoldLogService;
import com.iskyshop.foundation.service.ISmsGoldLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: SmsSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商家购买平台短信邮件控制器
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
 * @author hezeng
 * 
 * @date 2014-10-31
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class SmsSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private ISmsGoldLogService SmsGoldLogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoldLogService goldLogService;
	@Autowired
	private ITemplateService templateService;

	@SecurityMapping(title = "短信邮件", value = "/seller/sms_email.htm*", rtype = "seller", rname = "短信邮件", rcode = "sms_email_seller", rgroup = "其他管理")
	@RequestMapping("/seller/sms_email.htm")
	public ModelAndView sms_email(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/sms_email.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig sc = this.configService.getSysConfig();
		if (sc.getSms_buy() != 1 && sc.getEmail_buy() != 1) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("url",CommUtil.getURL(request)+"/seller/index.htm");
			mv.addObject("op_title", "系统没有开启邮件和短信购买功能");
			return mv;
		}
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		String sms_email_session = "sms_email_" + UUID.randomUUID();
		request.getSession(false).setAttribute("sms_email_session",
				sms_email_session);
		mv.addObject("account_gold", user.getGold());
		mv.addObject("account_sms", user.getStore().getStore_sms_count());
		mv.addObject("account_email", user.getStore().getStore_email_count());
		mv.addObject("sms_email_session", sms_email_session);
		return mv;
	}

	@SecurityMapping(title = "短信邮件购买", value = "/seller/sms_email_buy.htm*", rtype = "seller", rname = "短信邮件", rcode = "sms_email_seller", rgroup = "其他管理")
	@RequestMapping("/seller/sms_email_buy.htm")
	public ModelAndView sms_email_buy(HttpServletRequest request,
			HttpServletResponse response, String type, String count,
			String sms_email_session) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("url", "/seller/sms_email.htm");
		mv.addObject("op_title", "参数错误或者重复提交");
		SysConfig sc = this.configService.getSysConfig();
		if (sc.getSms_buy() != 1 && sc.getEmail_buy() != 1) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("url", "/seller/index.htm");
			mv.addObject("op_title", "系统没有开启邮件和短信购买功能");
			return mv;
		}
		boolean ret = true;
		if (type == null) {
			ret = false;
		} else {
			if (!type.equals("sms") && !type.equals("email")) {
				ret = false;
			}
		}
		if (count == null) {
			ret = false;
		} else {
			int temp_count = CommUtil.null2Int(count);
			if (temp_count < 0) {
				ret = false;
			}
		}
		if (ret) {
			String temp_session = CommUtil.null2String(request
					.getSession(false).getAttribute("sms_email_session"));
			if (temp_session != null) {
				if (sms_email_session.equals(temp_session)) {
					User user = this.userService.getObjById(SecurityUserHolder
							.getCurrentUser().getId());
					user = user.getParent() == null ? user : user.getParent();
					int price = this.configService.getSysConfig()
							.getSms_buy_cost();
					if (type.equals("email")) {
						price = this.configService.getSysConfig()
								.getEmail_buy_cost();
					}
					SmsGoldLog obj = new SmsGoldLog();
					if (type.equals("sms")) {
						obj.setTitle("短信");
					} else {
						obj.setTitle("邮件");
					}
					obj.setLog_type(type);
					obj.setAddTime(new Date());
					obj.setSeller_id(user.getId());
					obj.setStore_name(user.getStore().getStore_name());
					obj.setCount(CommUtil.null2Int(count));
					obj.setGold(price);
					obj.setAll_gold((int) CommUtil.mul(
							CommUtil.null2Int(count), CommUtil.null2Int(price)));
					obj.setLog_status(0);
					mv = new JModelAndView(
							"user/default/sellercenter/sms_email_buy.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
					mv.addObject("obj", obj);
					mv.addObject("sms_email_session", sms_email_session);
				}
			}
		}
		return mv;
	}

	@SecurityMapping(title = "短信邮件购买", value = "/seller/sms_email_buy_save.htm*", rtype = "seller", rname = "短信邮件", rcode = "sms_email_seller", rgroup = "其他管理")
	@RequestMapping("/seller/sms_email_buy_save.htm")
	public void sms_email_buy_save(HttpServletRequest request,
			HttpServletResponse response, String count, String log_type,
			String id) {
		int code = -100;// 提示代码，100支付成功，-100参数错误，-200非用户订单，-300金币不足,-400重复提交
		boolean ret = true;
		int all_gold = 0;
		int gold = 0;
		int temp_count = 0;
		String title = "";
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (log_type == null) {
			code = -100;
			ret = false;
		} else {
			if (!log_type.equals("sms") && !log_type.equals("email")) {
				code = -100;
				ret = false;
			}
		}
		if (ret) {
			if (CommUtil.null2Int(count) <= 0) {
				code = -100;
				ret = false;
			} else {
				temp_count = CommUtil.null2Int(count);
			}
		}
		if (request.getSession(false).getAttribute("sms_email_session") == null) {
			code = -400;
			ret = false;
		}
		if (ret) {
			if (log_type.equals("sms")) {
				gold = this.configService.getSysConfig().getSms_buy_cost();
				all_gold = gold * temp_count;
				title = "短信";
			}
			if (log_type.equals("email")) {
				gold = this.configService.getSysConfig().getEmail_buy_cost();
				all_gold = gold * temp_count;
				title = "邮件";
			}
		}
		SmsGoldLog obj = new SmsGoldLog();
		if (ret || code == -300) {
			obj.setTitle(title);
			obj.setLog_type(log_type);
			obj.setAddTime(new Date());
			obj.setSeller_id(user.getId());
			obj.setStore_name(user.getStore().getStore_name());
			obj.setCount(temp_count);
			obj.setGold(gold);
			obj.setAll_gold(all_gold);
			obj.setLog_status(0);
			this.SmsGoldLogService.save(obj);
		}
		if (id != null && !id.equals("")) {
			SmsGoldLog sgl = this.SmsGoldLogService.getObjById(CommUtil
					.null2Long(id));
			if (sgl != null) {
				if (sgl.getSeller_id() == user.getId()) {
					ret = true;
					obj = sgl;
					log_type = obj.getLog_type();
					temp_count = obj.getCount();
					all_gold = obj.getAll_gold();
				}
			}
		}

		if (ret) {
			if (user.getGold() >= all_gold) {
				obj.setLog_status(5);
				this.SmsGoldLogService.update(obj);
				// 扣除用户金币
				user.setGold(user.getGold() - obj.getAll_gold());
				this.userService.update(user);
				// 增加店铺短信邮件数量
				if (obj.getLog_type().equals("sms")) {
					store.setStore_sms_count(store.getStore_sms_count()
							+ obj.getCount() * 100);
				}
				if (obj.getLog_type().equals("email")) {
					store.setStore_email_count(store.getStore_email_count()
							+ obj.getCount() * 100);
				}
				this.storeService.update(store);
				GoldLog log = new GoldLog();
				log.setAddTime(new Date());
				if (log_type.equals("sms")) {
					log.setGl_content("购买短信扣除金币");
				}
				if (log_type.equals("email")) {
					log.setGl_content("购买邮件扣除金币");
				}
				log.setGl_count(obj.getAll_gold());
				log.setGl_user(user);
				log.setGl_type(-1);
				log.setGl_money(obj.getAll_gold());
				boolean flag = this.goldLogService.save(log);
				if (flag) {
					code = 100;
				}
				request.getSession(false).removeAttribute("sms_email_session");
			} else {
				code = -300;
				ret = false;
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(code);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "短信邮件记录", value = "/seller/sms_email_log.htm*", rtype = "seller", rname = "短信邮件", rcode = "sms_email_seller", rgroup = "其他管理")
	@RequestMapping("/seller/sms_email_log.htm")
	public ModelAndView sms_email_log(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/sms_email_log.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig sc = this.configService.getSysConfig();
		if (sc.getSms_buy() != 1 && sc.getEmail_buy() != 1) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("url", "/seller/index.htm");
			mv.addObject("op_title", "系统没有开启邮件和短信购买功能");
			return mv;
		}
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		SmsGoldLogQueryObject qo = new SmsGoldLogQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.setPageSize(15);
		qo.addQuery("obj.seller_id", new SysMap("seller_id", user.getId()), "=");
		IPageList pList = this.SmsGoldLogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "短信邮件记录删除", value = "/seller/sms_email_log_dele.htm*", rtype = "seller", rname = "短信邮件", rcode = "sms_email_seller", rgroup = "其他管理")
	@RequestMapping("/seller/sms_email_log_dele.htm")
	public String sms_email_log_dele(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		SmsGoldLog obj = this.SmsGoldLogService.getObjById(CommUtil
				.null2Long(id));
		if (obj != null) {
			if (obj.getSeller_id() == user.getId()) {
				this.SmsGoldLogService.delete(obj.getId());
			}
		}
		return "redirect:/seller/sms_email_log.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "短信邮件功能设置", value = "/seller/sms_email_set.htm*", rtype = "seller", rname = "短信邮件", rcode = "sms_email_seller", rgroup = "其他管理")
	@RequestMapping("/seller/sms_email_set.htm")
	public ModelAndView sms_email_set(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/sms_email_set.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig sc = this.configService.getSysConfig();
		if (sc.getSms_buy() != 1 && sc.getEmail_buy() != 1) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("url", "/seller/index.htm");
			mv.addObject("op_title", "系统没有开启邮件和短信购买功能");
			return mv;
		}
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (store.getStore_sms_count() == 0
				&& store.getStore_email_count() == 0) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("url", "/seller/sms_email.htm");
			mv.addObject("op_title", "购买短信或者邮件后才能开启本功能");
			return mv;
		}
		if (store.getSms_email_info() != null) {
			List<Map> functions = (List<Map>) Json.fromJson(store
					.getSms_email_info());
			mv.addObject("objs", functions);
		}
		return mv;
	}

	/**
	 * [{"id":1,"mark":"email_toseller_order_return_apply_notify ","title":
	 * "买家申请退货通知 ","sms_count":0,"email_count":0,"sms_open":1,
	 * "email_open":0},{},{}],id，对应系统模板id，sms_count为该功能已发短信数量，sms_open为该功能短信是否开启
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 */
	@SecurityMapping(title = "短信邮件功能数据初始化", value = "/seller/sms_email_init.htm*", rtype = "seller", rname = "短信邮件", rcode = "sms_email_seller", rgroup = "其他管理")
	@RequestMapping("/seller/sms_email_init.htm")
	public String sms_email_init(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map map = new HashMap();
		Set params = new TreeSet();
		params.add("email_tobuyer_selforder_ship_notify");
		params.add("email_tobuyer_selforder_cancel_notify");
		params.add("email_tobuyer_selforder_update_fee_notify");
		params.add("email_tobuyer_order_outline_pay_ok_notify");
		params.add("email_toseller_outline_pay_ok_notify");
		params.add("sms_tobuyer_selforder_ship_notify");
		params.add("sms_tobuyer_selforder_cancel_notify");
		params.add("sms_tobuyer_selforder_fee_notify");
		params.add("sms_tobuyer_pws_modify_notify");
		params.add("sms_tobuyer_mobilebind_notify");
		params.add("sms_toseller_outline_pay_ok_notify");
		params.add("sms_tobuyer_order_outline_pay_ok_notify");
		params.add("msg_toseller_store_update_refuse_notify");
		map.put("marks", params);
		map.put("type", "msg");
		List<Template> objs = this.templateService
				.query("select obj from Template obj where obj.type!=:type and obj.mark not in(:marks) order by addTime desc",
						map, -1, -1);
		List<Map> map_list = new ArrayList<Map>();
		for (Template obj : objs) {
			Map temp = new HashMap();
			temp.put("id", obj.getId());
			temp.put("mark", obj.getMark());
			temp.put("type", obj.getType());
			temp.put("title", obj.getTitle());
			temp.put("sms_count", 0);// 已发送短信数量
			temp.put("sms_open", 0);// 短信功能是否开启
			temp.put("email_count", 0);
			temp.put("email_open", 0);
			map_list.add(temp);
		}
		String json = Json.toJson(map_list, JsonFormat.compact());
		store.setSms_email_info(json);
		this.storeService.update(store);
		return "redirect:/seller/sms_email_set.htm";
	}

	@SecurityMapping(title = "短信邮件功能开启", value = "/seller/sms_email_open.htm*", rtype = "seller", rname = "短信邮件", rcode = "sms_email_seller", rgroup = "其他管理")
	@RequestMapping("/seller/sms_email_open.htm")
	public String sms_email_open(HttpServletRequest request,
			HttpServletResponse response, String id, String type, String all) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (all != null && all.equals("all")) {// 开启所有功能
			if (store.getSms_email_info() != null) {
				List<Map> objs = (List<Map>) Json.fromJson(store
						.getSms_email_info());
				for (Map map : objs) {
					if (type.equals("sms") && map.get("type").equals("sms")) {
						map.put("sms_open", 1);
					}
					if (type.equals("email") && map.get("type").equals("email")) {
						map.put("email_open", 1);
					}
				}
				String json = Json.toJson(objs, JsonFormat.compact());
				store.setSms_email_info(json);
				this.storeService.update(store);
			}
		} else {
			if (id != null && !id.equals("") && type != null
					&& !type.equals("")) {
				if (store.getSms_email_info() != null) {
					List<Map> objs = (List<Map>) Json.fromJson(store
							.getSms_email_info());
					for (Map map : objs) {
						if (id.equals(CommUtil.null2String(map.get("id")))) {
							if (type.equals("sms")) {
								map.put("sms_open", 1);
							} else {
								map.put("email_open", 1);
							}
							break;
						}
					}
					String json = Json.toJson(objs, JsonFormat.compact());
					store.setSms_email_info(json);
					this.storeService.update(store);
				}
			}
		}
		return "redirect:/seller/sms_email_set.htm";
	}

	@SecurityMapping(title = "短信邮件功能关闭", value = "/seller/sms_email_close.htm*", rtype = "seller", rname = "短信邮件", rcode = "sms_email_seller", rgroup = "其他管理")
	@RequestMapping("/seller/sms_email_close.htm")
	public String sms_email_close(HttpServletRequest request,
			HttpServletResponse response, String id, String type, String all) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/sms_email_set.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (all != null && all.equals("all")) {// 开启所有功能
			if (store.getSms_email_info() != null) {
				List<Map> objs = (List<Map>) Json.fromJson(store
						.getSms_email_info());
				for (Map map : objs) {
					if (type.equals("sms") && map.get("type").equals("sms")) {
						map.put("sms_open", 0);
					}
					if (type.equals("email") && map.get("type").equals("email")) {
						map.put("email_open", 0);
					}
				}
				String json = Json.toJson(objs, JsonFormat.compact());
				store.setSms_email_info(json);
				this.storeService.update(store);
			}
		} else {
			if (id != null && !id.equals("") && type != null
					&& !type.equals("")) {
				if (store.getSms_email_info() != null) {
					List<Map> objs = (List<Map>) Json.fromJson(store
							.getSms_email_info());
					for (Map map : objs) {
						if (id.equals(CommUtil.null2String(map.get("id")))) {
							if (type.equals("sms")) {
								map.put("sms_open", 0);
							} else {
								map.put("email_open", 0);
							}
							break;
						}
					}
					String json = Json.toJson(objs, JsonFormat.compact());
					store.setSms_email_info(json);
					this.storeService.update(store);
				}
			}
		}
		return "redirect:/seller/sms_email_set.htm";
	}

}
