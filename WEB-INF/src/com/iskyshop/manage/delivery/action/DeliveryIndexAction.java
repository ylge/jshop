package com.iskyshop.manage.delivery.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.DeliveryAddress;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.OrderFormQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IDeliveryAddressService;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IGoodsLogService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IReturnGoodsLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.kuaidi100.service.IExpressInfoService;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.buyer.tools.ShipTools;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.view.web.tools.GoodsViewTools;

/**
 * 
 * <p>
 * Title: DeliveryLoginAction.java
 * </p>
 * 
 * <p>
 * Description: 自提点管理控制器
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
 * @author jy
 * 
 * @date 2014-11-25
 * 
 * @version iskyshop_b2b2c_2015
 */
@Controller
public class DeliveryIndexAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IDeliveryAddressService deliveryAddressService;
	@Autowired
	private IPayoffLogService payoffLogservice;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IReturnGoodsLogService returnGoodsLogService;
	@Autowired
	private ShipTools shipTools;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IExpressInfoService expressInfoService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private IGroupGoodsService ggService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IExpressCompanyService expressCompayService;

	@RequestMapping("/delivery/login.htm")
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("delivery/delivery_login.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).removeAttribute("verify_code");// 如果系统未开启前台登录验证码，则需要移除session中保留的验证码信息
		return mv;
	}

	@SecurityMapping(title = "自提点管理首页", value = "/delivery/index.htm*", rtype = "delivery", rname = "自提点管理", rcode = "delivery_center", rgroup = "自提点管理")
	@RequestMapping("/delivery/index.htm")
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String keyword) {
		ModelAndView mv = new JModelAndView("delivery/delivery_index.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv,
				"addTime", "desc");
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		DeliveryAddress deliveryaddress = this.deliveryAddressService
				.getObjById(user.getDelivery_id());
		ofqo.addQuery("obj.delivery_address_id", new SysMap(
				"delivery_address_id", user.getDelivery_id()), "=");
		ofqo.addQuery("obj.order_main", new SysMap("order_main", 1), "=");// 只显示主订单,通过主订单完成子订单的加载
		ofqo.addQuery("obj.order_cat", new SysMap("order_cat", 2), "!=");
		ofqo.addQuery("obj.order_status", new SysMap("order_status", 20), ">");
		if (!CommUtil.null2String(keyword).equals("")) {
			Map map = new HashMap();
			map.put("buyname", "%" + CommUtil.null2String(keyword) + "%");
			map.put("order_id", "%" + CommUtil.null2String(keyword) + "%");
			map.put("receiver_telephone", "%" + CommUtil.null2String(keyword)
					+ "%");
			map.put("receiver_mobile", "%" + CommUtil.null2String(keyword)
					+ "%");
			ofqo.addQuery(
					"and (obj.receiver_Name like :buyname or obj.order_id like:order_id or obj.receiver_telephone like:receiver_telephone or obj.receiver_mobile like:receiver_mobile )",
					map);
			mv.addObject("keyword", keyword);
		}
		mv.addObject("orderFormTools", orderFormTools);
		IPageList pList = this.orderFormService.list(ofqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("deliaddr", deliveryaddress);
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("areas", areas);
		return mv;
	}

	@SecurityMapping(title = "自提点管理首页", value = "/delivery/ajax_base_save.htm*", rtype = "delivery", rname = "自提点管理", rcode = "delivery_center", rgroup = "自提点管理")
	@RequestMapping("/delivery/ajax_base_save.htm")
	public void ajax_base_save(HttpServletRequest request,
			HttpServletResponse response, String id, String da_service_day,
			String area3) {
		WebForm wf = new WebForm();
		DeliveryAddress deliveryaddress = null;
		DeliveryAddress obj = this.deliveryAddressService.getObjById(Long
				.parseLong(id));
		deliveryaddress = (DeliveryAddress) wf.toPo(request, obj);
		deliveryaddress.setDa_area(this.areaService.getObjById(CommUtil
				.null2Long(area3)));
		deliveryaddress.setDa_service_day(da_service_day.toString());
		this.deliveryAddressService.update(deliveryaddress);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "自提点管理首页", value = "/delivery/set_divery_status.htm*", rtype = "delivery", rname = "自提点管理", rcode = "delivery_center", rgroup = "自提点管理")
	@RequestMapping("/delivery/set_divery_status.htm")
	public void set_divery_status(HttpServletRequest request,
			HttpServletResponse response, String id, String mark) {
		DeliveryAddress obj = this.deliveryAddressService.getObjById(Long
				.parseLong(id));
		if (mark.equals("start")) {
			obj.setDa_status(10);
		}
		if (mark.equals("stop")) {
			obj.setDa_status(5);
		}
		this.deliveryAddressService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "自提点管理接收快件", value = "/delivery/confirm_order.htm*", rtype = "delivery", rname = "自提点管理", rcode = "delivery_center", rgroup = "自提点管理")
	@RequestMapping("/delivery/confirm_order.htm")
	public void confirm_order(HttpServletRequest request,
			HttpServletResponse response, String id) throws ParseException,
			UnsupportedEncodingException {
		OrderForm obj = this.orderFormService.getObjById(Long.parseLong(id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (obj != null
				&& obj.getDelivery_address_id().equals(user.getDelivery_id())&& obj.getOrder_status() < 35) {
			obj.setOrder_status(35);
			String da_code = this.update_deliveryInfo_code(obj);
			boolean ret = this.orderFormService.update(obj);
			if (ret) {// 订单状态更新成功，更新相关信息
				if (obj.getOrder_main() == 1
						&& !CommUtil.null2String(obj.getChild_order_detail())
								.equals("")) {// 更新子订单状态信息
					List<Map> maps = this.orderFormTools.queryGoodsInfo(obj
							.getChild_order_detail());
					for (Map map : maps) {
						OrderForm child_order = this.orderFormService
								.getObjById(CommUtil.null2Long(map
										.get("order_id")));
						child_order.setOrder_status(35);
						this.orderFormService.update(child_order);
					}
				}
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("自提点确认代收货");
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(obj);
				this.orderFormLogService.save(ofl);
				User buyer = this.userService.getObjById(CommUtil.null2Long(obj
						.getUser_id()));
				DeliveryAddress deladd = this.deliveryAddressService
						.getObjById(user.getDelivery_id());
				String msg_content = "尊敬的买家您好，你购买的订单号为：" + obj.getOrder_id()
						+ "的商品快件已经到达" + deladd.getDa_name() + "，取件六位码为"
						+ da_code + ",请持该号码到指定自提点领取你的快件。";
				// 向买家发送短信
				if (this.configService.getSysConfig().isSmsEnbale()) {
					String buyer_mobile = obj.getReceiver_mobile();
					if (buyer_mobile != null && !buyer_mobile.equals("")) {
						this.msgTools.sendSMS(buyer_mobile, msg_content);
					}
				}
				// 向买家发送邮件
				if (this.configService.getSysConfig().isEmailEnable()) {
					String buyerEmail = buyer.getEmail();
					if (buyerEmail != null && !buyerEmail.equals("")) {
						this.msgTools.sendEmail(buyerEmail, this.configService
								.getSysConfig().getTitle() + "系统消息",
								msg_content);
					}
				}
				// 向买家发送站内信
				Message msg = new Message();
				msg.setAddTime(new Date());
				msg.setStatus(0);
				msg.setType(1);
				msg.setContent(msg_content);
				msg.setFromUser(user);
				msg.setToUser(buyer);
				this.messageService.save(msg);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "自提点管理重发六位码", value = "/delivery/delivery_code_again.htm*", rtype = "delivery", rname = "自提点管理", rcode = "delivery_center", rgroup = "自提点管理")
	@RequestMapping("/delivery/delivery_code_again.htm")
	private void delivery_code_again(HttpServletRequest request,
			HttpServletResponse response, String orderForm_id)
			throws UnsupportedEncodingException {
		OrderForm orderForm = this.orderFormService.getObjById(Long
				.parseLong(orderForm_id));
		DeliveryAddress deliveryAddress = this.deliveryAddressService
				.getObjById(orderForm.getDelivery_address_id());
		String notice = "重发六位码送失败！";
		if (deliveryAddress.getDa_user_id().equals(
				SecurityUserHolder.getCurrentUser().getId())) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			String da_code = this.update_deliveryInfo_code(orderForm);
			User buyer = this.userService.getObjById(CommUtil
					.null2Long(orderForm.getUser_id()));
			String msg_content = "尊敬的买家您好，你购买的订单号为：" + orderForm.getOrder_id()
					+ "的商品快件已经到达" + deliveryAddress.getDa_name() + "，取件六位码为"
					+ da_code + ",请持该号码到指定自提点领取您的快件。";
			notice = "新六位码已成功发至";
			Boolean ret = false;
			// 向买家发送短信
			if (this.configService.getSysConfig().isSmsEnbale()) {
				String buyer_mobile = orderForm.getReceiver_mobile();
				if (buyer_mobile != null && !buyer_mobile.equals("")) {
					ret = this.msgTools.sendSMS(buyer_mobile, msg_content);
					if (ret) {
						notice = notice + "手机";
					}
				}
			}
			// 向买家发送邮件
			if (this.configService.getSysConfig().isEmailEnable()) {
				String buyerEmail = buyer.getEmail();
				if (buyerEmail != null && !buyerEmail.equals("")) {
					ret = this.msgTools.sendEmail(buyerEmail,
							this.configService.getSysConfig().getTitle()
									+ "系统消息", msg_content);
					if (ret) {
						notice = notice + ",邮箱";
					}
				}
			}
			// 向买家发送站内信
			Message msg = new Message();
			msg.setAddTime(new Date());
			msg.setStatus(0);
			msg.setType(1);
			msg.setContent(msg_content);
			msg.setFromUser(user);
			msg.setToUser(buyer);
			ret = this.messageService.save(msg);
			if (ret) {
				notice = notice + ",站内信。";
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(notice);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "自提点管理六位码", value = "/delivery/delivery_code.htm*", rtype = "delivery", rname = "自提点管理", rcode = "delivery_center", rgroup = "自提点管理")
	@RequestMapping("/delivery/delivery_code.htm")
	private ModelAndView delivery_code(HttpServletRequest request,
			HttpServletResponse response, String orderForm_id,
			String currentPage) {
		ModelAndView mv = new JModelAndView("delivery/delivery_code.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("orderForm_id", orderForm_id);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "自提点管理六位码验证", value = "/delivery/delivery_code_verify.htm*", rtype = "delivery", rname = "自提点管理", rcode = "delivery_center", rgroup = "自提点管理")
	@RequestMapping("/delivery/delivery_code_verify.htm")
	private void delivery_code_verify(HttpServletRequest request,
			HttpServletResponse response, String delivery_code,
			String orderForm_id, String currentPage) throws Exception {
		String verify = "defeat";
		OrderForm orderForm = this.orderFormService.getObjById(Long
				.parseLong(orderForm_id));
		DeliveryAddress deliveryAddress = this.deliveryAddressService
				.getObjById(orderForm.getDelivery_address_id());
		if (deliveryAddress.getDa_user_id().equals(
				SecurityUserHolder.getCurrentUser().getId())) {
			Map<String, String> infoMap = Json.fromJson(Map.class,
					orderForm.getDelivery_info());
			String infoCode = infoMap.get("da_code");
			if (infoCode != null && infoCode.equals(delivery_code)) {
				this.update_takes_goods(request, orderForm);
				verify = "success";
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(verify);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 在OrderForm的deliveryInfo中添加取件六位码
	 * 
	 * @param of
	 * @return code(六位码)
	 */
	private String update_deliveryInfo_code(OrderForm of) {
		Map infoMap = Json.fromJson(Map.class, of.getDelivery_info());
		String da_code = CommUtil.randomInt(6);
		infoMap.put("da_code", da_code);
		of.setDelivery_info(Json.toJson(infoMap, JsonFormat.compact()));
		return da_code;
	}

	private void update_takes_goods(HttpServletRequest request, OrderForm obj)
			throws Exception {
		if (obj != null
				&& obj.getUser_id().equals(
						CommUtil.null2String(SecurityUserHolder
								.getCurrentUser().getId()))) {
			obj.setOrder_status(40);
			// 增加购物积分
			SysConfig sc_ = this.configService.getSysConfig();
			int user_integral = (int) CommUtil.div(obj.getTotalPrice(),
					sc_.getConsumptionRatio());
			if (user_integral > sc_.getEveryIndentLimit()) {
				user_integral = sc_.getEveryIndentLimit();
			}
			User orderUser = this.userService.getObjById(CommUtil.null2Long(obj
					.getUser_id()));
			if (orderUser != null) {
				orderUser
						.setIntegral(CommUtil.null2Int(orderUser.getIntegral())
								+ user_integral);
				this.userService.update(orderUser);
				// 记录积分明细
				if (sc_.isIntegral()) {
					IntegralLog log = new IntegralLog();
					log.setAddTime(new Date());
					log.setContent("购物增加" + user_integral + "分");
					log.setIntegral(user_integral);
					log.setIntegral_user(orderUser);
					log.setType("order");
					this.integralLogService.save(log);
				}
			}
			Calendar ca = Calendar.getInstance();
			ca.add(ca.DATE, this.configService.getSysConfig()
					.getAuto_order_return());
			SimpleDateFormat bartDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String latertime = bartDateFormat.format(ca.getTime());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = sdf.parse(latertime);
			obj.setReturn_shipTime(date);
			obj.setConfirmTime(new Date());// 设置确认收货时间
			if (obj.getOrder_form() == 0) {
				Store store = this.storeService.getObjById(CommUtil
						.null2Long(obj.getStore_id()));
				Map json_map = new HashMap();
				json_map.put("seller_id", store.getUser().getId().toString());
				json_map.put("order_id", obj.getId().toString());
				String json = Json.toJson(json_map);
				this.msgTools.sendEmailCharge(CommUtil.getURL(request),
						"email_toseller_order_receive_ok_notify", store
								.getUser().getEmail(), json, null, obj
								.getStore_id());
				this.msgTools.sendSmsCharge(CommUtil.getURL(request),
						"sms_toseller_order_receive_ok_notify", store.getUser()
								.getMobile(), json, null, obj.getStore_id());
			}
			boolean ret = this.orderFormService.update(obj);
			if (ret) {// 订单状态更新成功，更新相关信息
				if (obj.getOrder_main() == 1
						&& !CommUtil.null2String(obj.getChild_order_detail())
								.equals("")) {// 更新子订单状态信息
					List<Map> maps = this.orderFormTools.queryGoodsInfo(obj
							.getChild_order_detail());
					for (Map map : maps) {
						OrderForm child_order = this.orderFormService
								.getObjById(CommUtil.null2Long(map
										.get("order_id")));
						child_order.setOrder_status(40);
						child_order.setReturn_shipTime(date);
						child_order.setConfirmTime(new Date());// 设置确认收货时间
						this.orderFormService.update(child_order);
						// 向子订单商家发送提醒信息，同时生成结算日志，如果子订单为平台自营，则不发送短信和邮件,
						if (child_order.getOrder_form() == 0) {
							Store store = this.storeService.getObjById(CommUtil
									.null2Long(child_order.getStore_id()));
							Map json_map = new HashMap();
							json_map.put("seller_id", store.getUser().getId()
									.toString());
							json_map.put("childorder_id", child_order.getId()
									.toString());
							String json = Json.toJson(json_map);
							if (obj.getOrder_form() == 0) {
								this.msgTools
										.sendEmailCharge(
												CommUtil.getURL(request),
												"email_toseller_order_receive_ok_notify",
												store.getUser().getEmail(),
												json, null, obj.getStore_id());
								this.msgTools.sendSmsCharge(
										CommUtil.getURL(request),
										"sms_toseller_order_receive_ok_notify",
										store.getUser().getMobile(), json,
										null, obj.getStore_id());
							}
							// 订单生成商家结算日志
							PayoffLog plog = new PayoffLog();
							if (obj.getPayType() != null
									&& obj.getPayType().equals("payafter")) {
								plog.setPl_sn("pl"
										+ CommUtil.formatTime("yyyyMMddHHmmss",
												new Date())
										+ store.getUser().getId());
								plog.setPl_info("货到付款");
								plog.setAddTime(new Date());
								plog.setSeller(store.getUser());
								plog.setO_id(CommUtil.null2String(child_order
										.getId()));
								plog.setOrder_id(child_order.getOrder_id()
										.toString());
								plog.setCommission_amount(child_order
										.getCommission_amount());// 该订单总佣金费用
								plog.setGoods_info(child_order.getGoods_info());
								plog.setOrder_total_price(child_order
										.getGoods_amount());// 该订单总商品金额
								plog.setTotal_amount(BigDecimal.valueOf(CommUtil
										.subtract(0, child_order
												.getCommission_amount())));// 该订单应结算金额：该订单总佣金费用
							} else {
								plog.setPl_sn("pl"
										+ CommUtil.formatTime("yyyyMMddHHmmss",
												new Date())
										+ store.getUser().getId());
								plog.setPl_info("确认收货");
								plog.setAddTime(new Date());
								plog.setSeller(store.getUser());
								plog.setO_id(CommUtil.null2String(child_order
										.getId()));
								plog.setOrder_id(child_order.getOrder_id()
										.toString());
								plog.setCommission_amount(child_order
										.getCommission_amount());// 该订单总佣金费用
								plog.setGoods_info(child_order.getGoods_info());
								plog.setOrder_total_price(child_order
										.getGoods_amount());// 该订单总商品金额
								plog.setTotal_amount(BigDecimal.valueOf(CommUtil
										.add(CommUtil.subtract(child_order
												.getGoods_amount(), child_order
												.getCommission_amount()),
												child_order.getShip_price())));// 该订单应结算金额：结算金额=订单总商品金额-总佣金费用+运费
							}
							this.payoffLogservice.save(plog);
							store.setStore_sale_amount(BigDecimal
									.valueOf(CommUtil.add(
											child_order.getGoods_amount(),
											store.getStore_sale_amount())));// 店铺本次结算总销售金额
							store.setStore_commission_amount(BigDecimal.valueOf(CommUtil
									.add(child_order.getCommission_amount(),
											store.getStore_commission_amount())));// 店铺本次结算总佣金
							store.setStore_payoff_amount(BigDecimal
									.valueOf(CommUtil.add(
											plog.getTotal_amount(),
											store.getStore_payoff_amount())));// 店铺本次结算总佣金
							this.storeService.update(store);
							// 增加系统总销售金额、总佣金
							SysConfig sc = this.configService.getSysConfig();
							sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil
									.add(child_order.getGoods_amount(),
											sc.getPayoff_all_sale())));
							sc.setPayoff_all_commission(BigDecimal
									.valueOf(CommUtil.add(
											child_order.getCommission_amount(),
											sc.getPayoff_all_commission())));
							this.configService.update(sc);
						}
					}
				}
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("确认收货");
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(obj);
				this.orderFormLogService.save(ofl);
				// 主订单生成商家结算日志
				if (obj.getOrder_form() == 0) {
					Store store = this.storeService.getObjById(CommUtil
							.null2Long(obj.getStore_id()));
					PayoffLog plog = new PayoffLog();
					if (obj.getPayType() != null
							&& obj.getPayType().equals("payafter")) {
						plog.setPl_sn("pl"
								+ CommUtil.formatTime("yyyyMMddHHmmss",
										new Date()) + store.getUser().getId());
						plog.setPl_info("货到付款");
						plog.setAddTime(new Date());
						plog.setSeller(store.getUser());
						plog.setO_id(CommUtil.null2String(obj.getId()));
						plog.setOrder_id(obj.getOrder_id().toString());
						plog.setCommission_amount(obj.getCommission_amount());// 该订单总佣金费用
						plog.setGoods_info(obj.getGoods_info());
						plog.setOrder_total_price(obj.getGoods_amount());// 该订单总商品金额
						plog.setTotal_amount(BigDecimal.valueOf(CommUtil
								.subtract(0, obj.getCommission_amount())));// 该订单应结算金额：该订单总佣金费用
					} else {
						plog.setPl_sn("pl"
								+ CommUtil.formatTime("yyyyMMddHHmmss",
										new Date()) + store.getUser().getId());
						plog.setPl_info("确认收货");
						plog.setAddTime(new Date());
						plog.setSeller(store.getUser());
						plog.setO_id(CommUtil.null2String(obj.getId()));
						plog.setOrder_id(obj.getOrder_id().toString());
						plog.setCommission_amount(obj.getCommission_amount());// 该订单总佣金费用
						plog.setGoods_info(obj.getGoods_info());
						plog.setOrder_total_price(obj.getGoods_amount());// 该订单总商品金额
						plog.setTotal_amount(BigDecimal.valueOf(CommUtil.add(
								CommUtil.subtract(obj.getGoods_amount(),
										obj.getCommission_amount()),
								obj.getShip_price())));// 该订单应结算金额：结算金额=订单总商品金额-总佣金费用+运费
					}
					this.payoffLogservice.save(plog);
					store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.add(
							obj.getGoods_amount(), store.getStore_sale_amount())));// 店铺本次结算总销售金额
					store.setStore_commission_amount(BigDecimal
							.valueOf(CommUtil.add(obj.getCommission_amount(),
									store.getStore_commission_amount())));// 店铺本次结算总佣金
					store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil
							.add(plog.getTotal_amount(),
									store.getStore_payoff_amount())));// 店铺本次结算总佣金
					this.storeService.update(store);
					// 增加系统总销售金额、总佣金
					SysConfig sc = this.configService.getSysConfig();
					sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(
							obj.getGoods_amount(), sc.getPayoff_all_sale())));
					sc.setPayoff_all_commission(BigDecimal.valueOf(CommUtil
							.add(obj.getCommission_amount(),
									sc.getPayoff_all_commission())));
					this.configService.update(sc);
				}
			}
		}
	}

}
