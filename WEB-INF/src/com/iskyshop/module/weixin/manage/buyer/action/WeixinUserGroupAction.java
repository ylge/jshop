package com.iskyshop.module.weixin.manage.buyer.action;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StorePoint;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.virtual.TransInfo;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IReturnGoodsLogService;
import com.iskyshop.foundation.service.IStorePointService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.PaymentTools;
import com.iskyshop.manage.buyer.tools.ShipTools;
import com.iskyshop.msg.MsgTools;

/**
 * 
 * 
 * <p>
 * Title:MobileUserOrderAction.java
 * </p>
 * 
 * <p>
 * Description: 手机端买家中心团购订单控制器
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
 * @date 2014年8月20日
 * 
 * @version iskyshop_b2b2c_2015
 */
@Controller
public class WeixinUserGroupAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IStorePointService storePointService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IExpressCompanyService expressCompayService;
	@Autowired
	private IReturnGoodsLogService returnGoodsLogService;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private IPayoffLogService payoffLogservice;
	@Autowired
	private IGroupGoodsService ggService;
	@Autowired
	private ShipTools ShipTools;

	@SecurityMapping(title = "团购订单列表", value = "/wap/buyer/group_list.htm*", rtype = "buyer", rname = "移动端用户团购订单列表", rcode = "wap_order_list", rgroup = "移动端用户团购订单")
	@RequestMapping("/wap/buyer/group_list.htm")
	public ModelAndView group_list(HttpServletRequest request,
			HttpServletResponse response, String type, String begin_count,
			String order_status) {
		ModelAndView mv = new JModelAndView(
				"user/wap/usercenter/group_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<OrderForm> orders = null;
		Map map = new HashMap();
		map.put("user_id", SecurityUserHolder.getCurrentUser().getId()
				.toString());
		map.put("order_main", 0);
		String sql = "select obj from OrderForm obj where obj.user_id=:user_id and obj.order_main=:order_main and obj.order_cat=2";
		if (CommUtil.null2String(type).equals("order_nopay")) {
			map.put("status", 10);
			sql = sql + " and obj.order_status=:status";
		}
		if (CommUtil.null2String(type).equals("order_noship")) {
			map.put("status", 20);
			sql = sql + " and obj.order_status=:status";
		}
		if (CommUtil.null2String(type).equals("order_notake")) {
			map.put("status1", 30);
			map.put("status2", 35);
			sql = sql
					+ " and (obj.order_status=:status1 or obj.order_status=:status2)";
		}
		if (CommUtil.null2String(type).equals("order_over")) {
			map.put("status", 40);
			sql = sql + " and obj.order_status>=:status";
		}
		sql = sql + " order by obj.addTime desc";
		orders = this.orderFormService.query(sql, map, -1, 12);
		mv.addObject("orders", orders);
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("order_status", order_status);
		mv.addObject("type", type);
		return mv;
	}

	@SecurityMapping(title = "团购订单列表", value = "/wap/buyer/group_data.htm*", rtype = "buyer", rname = "移动端用户团购订单列表", rcode = "wap_order_list", rgroup = "移动端用户团购订单")
	@RequestMapping("/wap/buyer/group_data.htm")
	public ModelAndView group_data(HttpServletRequest request,
			HttpServletResponse response, String type, String begin_count,
			String order_status) {
		ModelAndView mv = new JModelAndView(
				"user/wap/usercenter/group_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<OrderForm> orders = null;
		Map map = new HashMap();
		map.put("user_id", SecurityUserHolder.getCurrentUser().getId()
				.toString());
		map.put("order_main", 0);
		String sql = "select obj from OrderForm obj where obj.user_id=:user_id and obj.order_main=:order_main and obj.order_cat=2";
		if (CommUtil.null2String(type).equals("order_nopay")) {
			map.put("status", 10);
			sql = sql + " and obj.order_status=:status";
		}
		if (CommUtil.null2String(type).equals("order_noship")) {
			map.put("status", 20);
			sql = sql + " and obj.order_status=:status";
		}
		if (CommUtil.null2String(type).equals("order_notake")) {
			map.put("status1", 30);
			map.put("status2", 35);
			sql = sql
					+ " and (obj.order_status=:status1 or obj.order_status=:status2)";
		}
		if (CommUtil.null2String(type).equals("order_over")) {
			map.put("status", 40);
			sql = sql + " and obj.order_status>=:status";
		}
		sql = sql + " order by obj.addTime desc";
		orders = this.orderFormService.query(sql, map,
				CommUtil.null2Int(begin_count), 12);
		mv.addObject("orders", orders);
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("order_status", order_status);
		mv.addObject("type", type);
		return mv;
	}

	@SecurityMapping(title = "团购订单详情", value = "/wap/buyer/group_view.htm*", rtype = "buyer", rname = "移动端用户团购订单详情", rcode = "wap_order_detail", rgroup = "移动端用户团购订单")
	@RequestMapping("/wap/buyer/group_view.htm")
	public ModelAndView group_view(HttpServletRequest request,
			HttpServletResponse response, String id, String type) {
		ModelAndView mv = new JModelAndView(
				"user/wap/usercenter/group_detail.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null
				&& obj.getUser_id()
						.toString()
						.compareTo(
								SecurityUserHolder.getCurrentUser().getId()
										.toString()) == 0) {
			if (obj.getOrder_cat() == 1) {// 手机充值团购订单
				mv = new JModelAndView(
						"user/wap/usercenter/recharge_order_detail.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
			}
			boolean query_ship = false;// 是否查询物流
			if (!CommUtil.null2String(obj.getShipCode()).equals("")) {
				query_ship = true;
			}
			mv.addObject("obj", obj);
			mv.addObject("orderFormTools", orderFormTools);
			mv.addObject("query_ship", query_ship);
		} else {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "团购订单编号错误");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/wap/buyer/center.htm");
		}
		if (!CommUtil.null2String(type).equals("") && !type.equals("0")) {
			mv.addObject("type", type);
		}
		return mv;
	}

	@SecurityMapping(title = "团购订单取消", value = "/wap/buyer/group_cancel.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_order_cancel", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/group_cancel.htm")
	public String group_cancel(HttpServletRequest request,
			HttpServletResponse response, String id) {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null
				&& obj.getUser_id().compareTo(
						SecurityUserHolder.getCurrentUser().getId().toString()) == 0
				&& obj.getOrder_status() == 10) {
			obj.setOrder_status(0);
			this.orderFormService.save(obj);
		}
		return "redirect:group_view.htm?id=" + id;
	}

	/**
	 * 买家确认收货，确认收货后，团购订单状态值改变为40，如果是预存款支付，买家冻结预存款中同等团购订单账户金额自动转入商家预存款，如果开启预存款分润，
	 * 则按照分润比例，买家预存款分别进入商家及平台商的账户
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "确认收货", value = "/wap/buyer/group_cofirm.htm*", rtype = "buyer", rname = "移动端用户团购订单确认收货", rcode = "wap_order_cofirm", rgroup = "移动端用户团购订单")
	@RequestMapping("/wap/buyer/group_cofirm.htm")
	public ModelAndView group_cofirm(HttpServletRequest request,
			HttpServletResponse response, String id) throws Exception {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		ModelAndView mv = new JModelAndView("wap/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (obj != null
				&& obj.getUser_id()
						.toString()
						.compareTo(
								SecurityUserHolder.getCurrentUser().getId()
										.toString()) == 0) {
			obj.setOrder_status(40);
			Calendar ca = Calendar.getInstance();
			ca.add(ca.DATE, this.configService.getSysConfig()
					.getAuto_order_return());
			SimpleDateFormat bartDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String latertime = bartDateFormat.format(ca.getTime());
			obj.setReturn_shipTime(CommUtil.formatDate(latertime));
			obj.setConfirmTime(new Date());// 设置确认收货时间
			boolean ret = this.orderFormService.update(obj);
			if (obj.getPayment().getMark().equals("payafter")) {// 如果买家支付方式为货到付款，买家确认收货时更新商品库存
				this.update_goods_inventory(obj);// 更新商品库存
			}
			if (ret) {// 团购订单状态更新成功，更新相关信息
				if (obj.getOrder_main() == 1
						&& !CommUtil.null2String(obj.getChild_order_detail())
								.equals("")) {// 更新子团购订单状态信息
					List<Map> maps = this.orderFormTools.queryGoodsInfo(obj
							.getChild_order_detail());
					for (Map map : maps) {
						OrderForm child_order = this.orderFormService
								.getObjById(CommUtil.null2Long(map
										.get("order_id")));
						child_order.setOrder_status(40);
						child_order.setReturn_shipTime(CommUtil
								.formatDate(latertime));
						child_order.setConfirmTime(new Date());// 设置确认收货时间
						this.orderFormService.update(child_order);
						if (obj.getPayment().getMark().equals("payafter")) {// 如果买家支付方式为货到付款，买家确认收货，子团购订单商品销量增加
							List<Map> goods_map = this.orderFormTools
									.queryGoodsInfo(child_order.getGoods_info());
							for (Map child_map : goods_map) {
								Goods goods = this.goodsService
										.getObjById(CommUtil
												.null2Long(child_map
														.get("goods_id")));
								goods.setGoods_salenum(goods.getGoods_salenum()
										+ CommUtil.null2Int(child_map
												.get("goods_count")));// 增加商品销量
								goods.setGoods_inventory(goods
										.getGoods_inventory()
										- CommUtil.null2Int(child_map
												.get("goods_count")));// 库存减少
								if (goods.getGroup_buy() == 2) {// 如果为团购商品，增加团购销量,减少团购库存
									for (GroupGoods gg : goods
											.getGroup_goods_list()) {
										if (gg.getGroup()
												.getId()
												.equals(goods.getGroup()
														.getId())) {
											gg.setGg_selled_count(CommUtil.null2Int(gg
													.getGg_selled_count()
													+ CommUtil.null2Int(map
															.get("goods_count"))));// 增加团购销量
											if (gg.getGg_count()// 减少团购库存
													- CommUtil
															.null2Int(map
																	.get("goods_count")) > 0) {
												gg.setGg_count(gg.getGg_count()
														- CommUtil.null2Int(map
																.get("goods_count")));
											} else {
												gg.setGg_count(0);
											}
											this.ggService.update(gg);
										}
									}
								}
								this.goodsService.update(goods);
							}
						}
						// 向子团购订单商家发送提醒信息，同时生成结算日志，如果子团购订单为平台自营，则不发送短信和邮件,
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
										store.getUser().getEmail(), json, null,
										obj.getStore_id());
							}
							// 团购订单生成商家结算日志
							PayoffLog plog = new PayoffLog();
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
									.getCommission_amount());// 该团购订单总佣金费用
							plog.setGoods_info(child_order.getGoods_info());
							plog.setOrder_total_price(child_order
									.getGoods_amount());// 该团购订单总商品金额
							plog.setTotal_amount(BigDecimal.valueOf(CommUtil.add(
									CommUtil.subtract(
											child_order.getGoods_amount(),
											child_order.getCommission_amount()),
									child_order.getShip_price())));// 该订单应结算金额：结算金额=订单总商品金额-总佣金费用+运费
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
				// 主团购订单生成商家结算日志
				if (obj.getOrder_form() == 0) {
					Store store = this.storeService.getObjById(CommUtil
							.null2Long(obj.getStore_id()));
					PayoffLog plog = new PayoffLog();
					plog.setPl_sn("pl"
							+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
							+ store.getUser().getId());
					plog.setPl_info("确认收货");
					plog.setAddTime(new Date());
					plog.setSeller(store.getUser());
					plog.setO_id(CommUtil.null2String(obj.getId()));
					plog.setOrder_id(obj.getOrder_id().toString());
					plog.setCommission_amount(obj.getCommission_amount());// 该团购订单总佣金费用
					plog.setGoods_info(obj.getGoods_info());
					plog.setOrder_total_price(obj.getGoods_amount());// 该团购订单总商品金额
					plog.setTotal_amount(BigDecimal.valueOf(CommUtil.add(
							CommUtil.subtract(obj.getGoods_amount(),
									obj.getCommission_amount()),
							obj.getShip_price())));// 该订单应结算金额：结算金额=订单总商品金额-总佣金费用+运费
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
			mv.addObject("op_title", "确认成功");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/wap/buyer/group_list.htm");
		} else {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "团购订单编号错误");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/wap/buyer/group_list.htm");
		}
		return mv;
	}

	/**
	 * 团购订单评论
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "团购订单评论", value = "/wap/buyer/group_discuss.htm*", rtype = "buyer", rname = "移动端用户团购订单评论", rcode = "wap_order_cofirm", rgroup = "移动端用户团购订单")
	@RequestMapping("/wap/buyer/group_discuss.htm")
	public ModelAndView group_discuss(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/wap/usercenter/group_discuss.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (obj != null
				&& obj.getUser_id()
						.toString()
						.compareTo(
								SecurityUserHolder.getCurrentUser().getId()
										.toString()) == 0) {
			mv.addObject("obj", obj);
			mv.addObject("orderFormTools", orderFormTools);
			String evaluate_session = CommUtil.randomString(32);
			request.getSession(false).setAttribute("evaluate_session",
					evaluate_session);
			mv.addObject("evaluate_session", evaluate_session);
			if (obj.getOrder_status() >= 50) {
				mv = new JModelAndView("wap/success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "团购订单已经评价！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/user/wap/usercenter/group_list.htm");
			}
		} else {
			mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的团购订单！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/wap/buyer/center.htm");
		}
		mv.addObject("orderFormTools", orderFormTools);
		return mv;
	}

	@SecurityMapping(title = "买家评价保存", value = "/wap/buyer/group_discuss_save.htm*", rtype = "buyer", rname = "移动端用户团购订单评论", rcode = "user_center", rgroup = "移动端用户团购订单")
	@RequestMapping("/wap/buyer/group_discuss_save.htm")
	public ModelAndView group_discuss_save(HttpServletRequest request,
			HttpServletResponse response, String evaluate_session)
			throws Exception {
		String[] ids = request.getParameterValues("id");
		String evaluate_session1 = (String) request.getSession(false)
				.getAttribute("evaluate_session");
		if (CommUtil.null2String(evaluate_session1).equals(evaluate_session)) {
			for (String id : ids) {
				OrderForm obj = this.orderFormService.getObjById(CommUtil
						.null2Long(id));
				if (obj != null
						&& obj.getUser_id()
								.toString()
								.compareTo(
										SecurityUserHolder.getCurrentUser()
												.getId().toString()) == 0) {
					if (obj.getOrder_status() == 40) {
						obj.setOrder_status(50);
						this.orderFormService.update(obj);
						OrderFormLog ofl = new OrderFormLog();
						ofl.setAddTime(new Date());
						ofl.setLog_info("评价团购订单");
						ofl.setLog_user(SecurityUserHolder.getCurrentUser());
						ofl.setOf(obj);
						this.orderFormLogService.save(ofl);
						List<Map> json = this.orderFormTools.queryGoodsInfo(obj
								.getGoods_info());
						for (Map map : json) {
							map.put("orderForm", obj.getId());
						}
						for (Map map : json) {
							Evaluate eva = new Evaluate();
							Goods goods = this.goodsService.getObjById(CommUtil
									.null2Long(map.get("goods_id")));
							eva.setAddTime(new Date());
							eva.setEvaluate_goods(goods);
							eva.setGoods_num(CommUtil.null2Int(map
									.get("goods_count")));
							eva.setGoods_price(map.get("goods_price")
									.toString());
							eva.setGoods_spec(map.get("goods_gsp_val")
									.toString());
							eva.setEvaluate_info(request
									.getParameter("evaluate_info_"
											+ goods.getId()));
							eva.setEvaluate_buyer_val(CommUtil.null2Int(request
									.getParameter("evaluate_buyer_val"
											+ goods.getId())));
							eva.setDescription_evaluate(BigDecimal.valueOf(CommUtil.null2Double(request
									.getParameter("description_evaluate"
											+ goods.getId()))));
							eva.setService_evaluate(BigDecimal.valueOf(CommUtil
									.null2Double(request
											.getParameter("service_evaluate"
													+ goods.getId()))));
							eva.setShip_evaluate(BigDecimal.valueOf(CommUtil
									.null2Double(request
											.getParameter("ship_evaluate"
													+ goods.getId()))));
							eva.setEvaluate_type("goods");
							eva.setEvaluate_user(SecurityUserHolder
									.getCurrentUser());
							eva.setOf(this.orderFormService.getObjById(CommUtil
									.null2Long(map.get("orderForm"))));
							this.evaluateService.save(eva);
							Map params = new HashMap();
							User sp_user = this.userService.getObjById(obj
									.getEva_user_id());
							params.put("user_id", SecurityUserHolder
									.getCurrentUser().getId().toString());
							List<Evaluate> evas = this.evaluateService
									.query("select obj from Evaluate obj where obj.of.user_id=:user_id",
											params, -1, -1);
							double user_evaluate1 = 0;
							double user_evaluate1_total = 0;
							double description_evaluate = 0;
							double description_evaluate_total = 0;
							double service_evaluate = 0;
							double service_evaluate_total = 0;
							double ship_evaluate = 0;
							double ship_evaluate_total = 0;
							DecimalFormat df = new DecimalFormat("0.0");
							for (Evaluate eva1 : evas) {
								user_evaluate1_total = user_evaluate1_total
										+ eva1.getEvaluate_buyer_val();
								description_evaluate_total = description_evaluate_total
										+ CommUtil.null2Double(eva1
												.getDescription_evaluate());
								service_evaluate_total = service_evaluate_total
										+ CommUtil.null2Double(eva1
												.getService_evaluate());
								ship_evaluate_total = ship_evaluate_total
										+ CommUtil.null2Double(eva1
												.getShip_evaluate());
							}
							user_evaluate1 = CommUtil
									.null2Double(df.format(user_evaluate1_total
											/ evas.size()));
							description_evaluate = CommUtil.null2Double(df
									.format(description_evaluate_total
											/ evas.size()));
							service_evaluate = CommUtil.null2Double(df
									.format(service_evaluate_total
											/ evas.size()));
							ship_evaluate = CommUtil.null2Double(df
									.format(ship_evaluate_total / evas.size()));
							params.clear();
							params.put("user_id", obj.getEva_user_id());
							List<StorePoint> sps = this.storePointService
									.query("select obj from StorePoint obj where obj.user.id=:user_id",
											params, -1, -1);
							StorePoint point = null;
							if (sps.size() > 0) {
								point = sps.get(0);
							} else {
								point = new StorePoint();
							}
							point.setAddTime(new Date());
							point.setUser(sp_user);
							point.setDescription_evaluate(BigDecimal
									.valueOf(description_evaluate > 5 ? 5
											: description_evaluate));
							point.setService_evaluate(BigDecimal
									.valueOf(service_evaluate > 5 ? 5
											: service_evaluate));
							point.setShip_evaluate(BigDecimal
									.valueOf(ship_evaluate > 5 ? 5
											: ship_evaluate));
							if (sps.size() > 0) {
								this.storePointService.update(point);
							} else {
								this.storePointService.save(point);
							}
							// 增加用户积分和消费金额
							User user = this.userService.getObjById(CommUtil
									.null2Long(obj.getUser_id()));
							user.setIntegral(user.getIntegral()
									+ this.configService.getSysConfig()
											.getIndentComment());
							user.setUser_goods_fee(BigDecimal.valueOf(CommUtil
									.add(user.getUser_goods_fee(),
											obj.getTotalPrice())));
							this.userService.update(user);
						}
					}
					if (obj.getOrder_form() == 0) {
						Store store = this.storeService.getObjById(CommUtil
								.null2Long(obj.getStore_id()));
						Map map = new HashMap();
						map.put("seller_id", store.getUser().getId().toString());
						map.put("order_id", obj.getId().toString());
						String json = Json.toJson(map);
						this.msgTools.sendEmailCharge(CommUtil.getURL(request),
								"email_toseller_evaluate_ok_notify", store
										.getUser().getEmail(), json, null, obj
										.getStore_id());
					}
				}
			}
			request.getSession(false).removeAttribute("evaluate_session");
			ModelAndView mv = new JModelAndView("wap/success.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "团购订单评价成功！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/user/wap/buyer/group_list.htm");
			return mv;
		} else {
			ModelAndView mv = new JModelAndView("wap/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "禁止重复评价!");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/user/wap/buyer/group_list.htm");
			return mv;
		}

	}

	private void update_goods_inventory(OrderForm order) {
		// 付款成功，团购订单状态更新，同时更新商品库存，如果是团购商品，则更新团购库存
		List<Goods> goods_list = this.orderFormTools.queryOfGoods(CommUtil
				.null2String(order.getId()));
		for (Goods goods : goods_list) {
			int goods_count = this.orderFormTools.queryOfGoodsCount(
					CommUtil.null2String(order.getId()),
					CommUtil.null2String(goods.getId()));
			if (goods.getGroup() != null && goods.getGroup_buy() == 2) {
				for (GroupGoods gg : goods.getGroup_goods_list()) {
					if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
						gg.setGg_count(gg.getGg_count() - goods_count);
						this.groupGoodsService.update(gg);
						// 更新lucene索引
						String goods_lucene_path = System
								.getProperty("iskyshopb2b2c.root")
								+ File.separator
								+ "luence"
								+ File.separator
								+ "groupgoods";
						File file = new File(goods_lucene_path);
						if (!file.exists()) {
							CommUtil.createFolder(goods_lucene_path);
						}
						LuceneUtil lucene = LuceneUtil.instance();
						lucene.setIndex_path(goods_lucene_path);
						lucene.update(CommUtil.null2String(goods.getId()),
								luceneVoTools.updateGroupGoodsIndex(gg));
					}
				}
			}
			List<String> gsps = new ArrayList<String>();
			List<GoodsSpecProperty> temp_gsp_list = this.orderFormTools
					.queryOfGoodsGsps(CommUtil.null2String(order.getId()),
							CommUtil.null2String(goods.getId()));
			for (GoodsSpecProperty gsp : temp_gsp_list) {
				gsps.add(gsp.getId().toString());
			}
			String[] gsp_list = new String[gsps.size()];
			gsps.toArray(gsp_list);
			goods.setGoods_salenum(goods.getGoods_salenum() + goods_count);
			String inventory_type = goods.getInventory_type() == null ? "all"
					: goods.getInventory_type();
			if (inventory_type.equals("all")) {
				goods.setGoods_inventory(goods.getGoods_inventory()
						- goods_count);
			} else {
				List<HashMap> list = Json
						.fromJson(ArrayList.class, CommUtil.null2String(goods
								.getGoods_inventory_detail()));
				for (Map temp : list) {
					String[] temp_ids = CommUtil.null2String(temp.get("id"))
							.split("_");
					Arrays.sort(temp_ids);
					Arrays.sort(gsp_list);
					if (Arrays.equals(temp_ids, gsp_list)) {
						temp.put("count", CommUtil.null2Int(temp.get("count"))
								- goods_count);
					}
				}
				goods.setGoods_inventory_detail(Json.toJson(list,
						JsonFormat.compact()));
			}
			for (GroupGoods gg : goods.getGroup_goods_list()) {
				if (gg.getGroup().getId().equals(goods.getGroup().getId())
						&& gg.getGg_count() == 0) {
					goods.setGroup_buy(3);// 标识商品的状态为团购数量已经结束
				}
			}
			this.goodsService.update(goods);
			// 更新lucene索引
			String goods_lucene_path = System.getProperty("iskyshopb2b2c.root")
					+ File.separator + "luence" + File.separator + "goods";
			File file = new File(goods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(goods_lucene_path);
			}
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setIndex_path(goods_lucene_path);
			lucene.update(CommUtil.null2String(goods.getId()),
					luceneVoTools.updateGoodsIndex(goods));
		}
	}

	@SecurityMapping(title = "物流信息", value = "/wap/buyer/group_ship_detail.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "移动端用户团购订单")
	@RequestMapping("/wap/buyer/group_ship_detail.htm")
	public ModelAndView group_ship_detail(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String order_id,
			String type) {
		ModelAndView mv = new JModelAndView(
				"user/wap/usercenter/group_ship_detail.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<TransInfo> transInfo_list = new ArrayList<TransInfo>();
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		TransInfo transInfo = this.ShipTools.query_Ordership_getData(CommUtil
				.null2String(order_id));
		if (transInfo != null) {
			transInfo.setExpress_company_name(this.orderFormTools.queryExInfo(
					order.getExpress_info(), "express_company_name"));
			transInfo.setExpress_ship_code(order.getShipCode());
			transInfo_list.add(transInfo);
		}
		mv.addObject("order", order);
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("transInfo_list", transInfo_list);
		if (!CommUtil.null2String(type).equals("") && !type.equals("0")) {
			mv.addObject("type", type);
		}
		return mv;
	}
}
