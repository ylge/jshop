package com.iskyshop.module.app.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
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

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.ExpressCompanyCommon;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.ShipAddress;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IExpressCompanyCommonService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IShipAddressService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.kuaidi100.domain.ExpressInfo;
import com.iskyshop.kuaidi100.pojo.TaskRequest;
import com.iskyshop.kuaidi100.pojo.TaskResponse;
import com.iskyshop.kuaidi100.post.HttpRequest;
import com.iskyshop.kuaidi100.post.JacksonHelper;
import com.iskyshop.kuaidi100.service.IExpressInfoService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.pay.alipay.config.AlipayConfig;
import com.iskyshop.pay.alipay.util.AlipaySubmit;

/**
 * 
 * <p>
 * Title: AppSellerOrderViewAction.java
 * </p>
 * 
 * <p>
 * Description: 卖家订单管理
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
 * @date 2015-3-18
 * 
 * @version 1.0
 */
@Controller
public class AppSellerOrderViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IExpressCompanyCommonService expressCompanyCommonService;
	@Autowired
	private IShipAddressService shipAddressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IExpressInfoService expressInfoService;
	@Autowired
	private IPaymentService paymentService;

	/**
	 * 卖家订单列表
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param order_status
	 * @param begin_count
	 */
	@RequestMapping("/app/seller/order_list.htm")
	public void order_list(HttpServletRequest request,
			HttpServletResponse response, String user_id, String order_status,
			String begin_count, String select_count, String beginTime,
			String endTime) {
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		Map params = new HashMap();
		params.put("store_id", user.getStore().getId().toString());
		params.put("order_cat", 0);
		String hql = "select obj from OrderForm obj where obj.store_id=:store_id and obj.order_cat=:order_cat";
		if (!CommUtil.null2String(beginTime).equals("")) {
			params.put("beginTime", CommUtil.formatDate(beginTime));
			hql += " and obj.addTime>=:beginTime";
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			String ends = endTime + " 23:59:59";
			params.put("endTime",
					CommUtil.formatDate(ends, "yyyy-MM-dd hh:mm:ss"));
			hql += " and obj.addTime<=:endTime";
		}

		if (order_status.equals("10")) {// 待付款
			params.put("order_status", 10);
			hql += " and obj.order_status=:order_status";
		}
		if (order_status.equals("20")) {// 待发货（已支付和货到付款）
			params.put("order_status1", 16);
			params.put("order_status2", 20);
			hql += " and ( obj.order_status=:order_status1 or obj.order_status=:order_status2 )";
		}
		if (order_status.equals("30")) {// 已发货
			params.put("order_status1", 30);
			params.put("order_status2", 35);
			hql += " and ( obj.order_status=:order_status1 or obj.order_status=:order_status2 )";
		}
		if (order_status.equals("50")) {// 已完成
			params.put("order_status", 40);
			hql += " and obj.order_status>=:order_status";
		}
		hql += " order by obj.addTime desc";
		List<OrderForm> order_list = this.orderFormService
				.query(hql, params, CommUtil.null2Int(begin_count),
						CommUtil.null2Int(select_count));
		List list = new ArrayList();
		for (OrderForm of : order_list) {
			Map map = new HashMap();
			map.put("order_id", of.getId());
			map.put("order_num", of.getOrder_id());
			map.put("addTime", of.getAddTime());
			map.put("order_status", of.getOrder_status());

			String payment = "未支付";
			if (order_status.equals("20")) {
				if (of.getPayment().getMark() != null
						&& !of.getPayment().getMark().equals("")) {
					if (of.getPayment().getMark().equals("alipay")) {
						payment = "支付宝";
					}
					if (of.getPayment().getMark().equals("alipay_wap")) {
						payment = "手机网页支付宝";
					}
					if (of.getPayment().getMark().equals("alipay_app")) {
						payment = "手机支付宝APP";
					}
					if (of.getPayment().getMark().equals("tenpay")) {
						payment = "财付通";
					}
					if (of.getPayment().getMark().equals("bill")) {
						payment = "快钱";
					}
					if (of.getPayment().getMark().equals("chinabank")) {
						payment = "网银在线";
					}
					if (of.getPayment().getMark().equals("balance")) {
						payment = "预存款支付";
					}
					if (of.getPayment().getMark().equals("paypal")) {
						payment = "paypal";
					}
				}
				if (of.getPayType() != null) {
					if (of.getPayType().equals("payafter")) {
						payment = "货到付款";
					}
				}
			}
			map.put("payment", payment);

			String order_type = "PC订单";
			if (of.getOrder_type().equals("weixin")) {
				order_type = "微信订单";
			}
			if (of.getOrder_type().equals("android")) {
				order_type = "Android订单";
			}
			if (of.getOrder_type().equals("ios")) {
				order_type = "iOS订单";
			}

			map.put("order_type", order_type);
			map.put("ship_price", of.getShip_price());
			map.put("totalPrice",
					orderFormTools.query_order_price(of.getId().toString()));

			List goods_mainphoto_list = new ArrayList();
			List goods_name_list = new ArrayList();
			List<Map> temp_maps = this.orderFormTools.queryGoodsInfo(of
					.getGoods_info());
			String url = CommUtil.getURL(request);
			for (Map goods : temp_maps) {
				goods_mainphoto_list.add(url + "/"
						+ goods.get("goods_mainphoto_path"));
				goods_name_list.add(goods.get("goods_name"));
			}
			if (of.getChild_order_detail() != null
					&& !of.getChild_order_detail().equals("")) {
				List<Map> temp_maps2 = this.orderFormTools.queryGoodsInfo(of
						.getChild_order_detail());
				for (Map goods : temp_maps2) {
					String child_order_id = CommUtil.null2String(goods
							.get("order_id"));
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(child_order_id));
					List<Map> temp_maps3 = this.orderFormTools
							.queryGoodsInfo(child_order.getGoods_info());
					for (Map goods3 : temp_maps3) {
						goods_mainphoto_list.add(url + "/"
								+ goods3.get("goods_mainphoto_path"));
						goods_name_list.add(goods.get("goods_name"));
					}
				}
			}
			map.put("photo_list", goods_mainphoto_list);
			map.put("name_list", goods_name_list);

			list.add(map);
		}
		json_map.put("order_status", order_status);
		json_map.put("order_list", list);

		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);

	}

	/**
	 * 订单查询 订单号或者用户名
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param search_word
	 */
	@RequestMapping("/app/seller/order_search.htm")
	public void order_search(HttpServletRequest request,
			HttpServletResponse response, String user_id, String search_word,
			String begin_count, String select_count, String beginTime,
			String endTime) {
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		Map params = new HashMap();
		params.put("store_id", user.getStore().getId().toString());
		params.put("order_cat", 0);
		params.put("order_id", "%" + search_word + "%");
		params.put("user_name", search_word);
		List<OrderForm> order_list = this.orderFormService
				.query("select obj from OrderForm obj where obj.store_id=:store_id and obj.order_cat=:order_cat and (obj.order_id like :order_id or obj.user_name=:user_name)",
						params, CommUtil.null2Int(begin_count),
						CommUtil.null2Int(select_count));
		List list = new ArrayList();
		for (OrderForm of : order_list) {
			Map map = new HashMap();
			map.put("order_id", of.getId());
			map.put("order_num", of.getOrder_id());
			map.put("addTime", of.getAddTime());
			map.put("order_status", of.getOrder_status());
			String order_status = "";

			String order_type = "PC订单";
			if (of.getOrder_type().equals("weixin")) {
				order_type = "微信订单";
			}
			if (of.getOrder_type().equals("android")) {
				order_type = "Android订单";
			}
			if (of.getOrder_type().equals("ios")) {
				order_type = "iOS订单";
			}

			map.put("order_type", order_type);
			map.put("ship_price", of.getShip_price());
			map.put("totalPrice",
					orderFormTools.query_order_price(of.getId().toString()));

			List goods_mainphoto_list = new ArrayList();
			List goods_name_list = new ArrayList();
			List<Map> temp_maps = this.orderFormTools.queryGoodsInfo(of
					.getGoods_info());
			String url = CommUtil.getURL(request);
			for (Map goods : temp_maps) {
				goods_mainphoto_list.add(url + "/"
						+ goods.get("goods_mainphoto_path"));
				goods_name_list.add(goods.get("goods_name"));
			}
			if (of.getChild_order_detail() != null
					&& !of.getChild_order_detail().equals("")) {
				List<Map> temp_maps2 = this.orderFormTools.queryGoodsInfo(of
						.getChild_order_detail());
				for (Map goods : temp_maps2) {
					String child_order_id = CommUtil.null2String(goods
							.get("order_id"));
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(child_order_id));
					List<Map> temp_maps3 = this.orderFormTools
							.queryGoodsInfo(child_order.getGoods_info());
					for (Map goods3 : temp_maps3) {
						goods_mainphoto_list.add(url + "/"
								+ goods3.get("goods_mainphoto_path"));
						goods_name_list.add(goods.get("goods_name"));
					}
				}
			}
			map.put("photo_list", goods_mainphoto_list);
			map.put("name_list", goods_name_list);

			list.add(map);
		}
		json_map.put("order_list", list);
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	/**
	 * 卖家订单修改价格
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param order_id
	 */
	@RequestMapping("/app/seller/order_fee.htm")
	public void order_fee(HttpServletRequest request,
			HttpServletResponse response, String user_id, String order_id) {
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		if (user.getStore().getId().equals(store.getId())) {
			json_map.put("ret", 100);
			json_map.put("id", obj.getId());
			json_map.put("user_name", obj.getUser_name());
			json_map.put("order_id", obj.getOrder_id());
			json_map.put("commission_amount", obj.getCommission_amount());
			json_map.put("goods_amount", obj.getGoods_amount());
			json_map.put("ship_price", obj.getShip_price());
			json_map.put("totalPrice", obj.getTotalPrice());
		} else {
			json_map.put("ret", -100);
			json_map.put("msg", "您没有编号为" + order_id + "的订单！");
		}

		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	/**
	 * 卖家修改价格保存
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param order_id
	 * @param goods_amount
	 * @param ship_price
	 * @param totalPrice
	 * @throws Exception
	 */
	@RequestMapping("/app/seller/order_fee_save.htm")
	public void order_fee_save(HttpServletRequest request,
			HttpServletResponse response, String user_id, String order_id,
			String goods_amount, String ship_price, String totalPrice)
			throws Exception {
		Map json_map = new HashMap();
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (CommUtil.null2Double(obj.getCommission_amount()) <= CommUtil
				.null2Double(goods_amount)) {
			Store store = this.storeService.getObjById(CommUtil.null2Long(obj
					.getStore_id()));
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			user = user.getParent() == null ? user : user.getParent();
			if (user.getStore().getId().equals(store.getId())) {
				obj.setGoods_amount(BigDecimal.valueOf(CommUtil
						.null2Double(goods_amount)));
				obj.setShip_price(BigDecimal.valueOf(CommUtil
						.null2Double(ship_price)));
				obj.setTotalPrice(BigDecimal.valueOf(CommUtil
						.null2Double(totalPrice)));
				obj.setOperation_price_count(obj.getOperation_price_count() + 1);
				this.orderFormService.update(obj);
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("调整订单费用");
				ofl.setState_info("调整订单总金额为:" + totalPrice + ",调整运费金额为:"
						+ ship_price);
				user = this.userService.getObjById(CommUtil.null2Long(user_id));
				ofl.setLog_user(user);		
				ofl.setOf(obj);
				this.orderFormLogService.save(ofl);
				User buyer = this.userService.getObjById(CommUtil.null2Long(obj
						.getUser_id()));
				Map map = new HashMap();
				map.put("buyer_id", buyer.getId().toString());
				map.put("seller_id", store.getUser().getId().toString());
				map.put("order_id", obj.getId());
				String json = Json.toJson(map);
				if (obj.getOrder_form() == 0) {
					this.msgTools.sendEmailCharge(CommUtil.getURL(request),
							"email_tobuyer_order_update_fee_notify",
							buyer.getEmail(), json, null, obj.getStore_id());
					this.msgTools.sendSmsCharge(CommUtil.getURL(request),
							"sms_tobuyer_order_fee_notify", buyer.getMobile(),
							json, null, obj.getStore_id());
				} else {
					this.msgTools.sendEmailFree(CommUtil.getURL(request),
							"email_tobuyer_order_update_fee_notify",
							buyer.getEmail(), json, null);
					this.msgTools.sendSmsFree(CommUtil.getURL(request),
							"sms_tobuyer_order_fee_notify", buyer.getMobile(),
							json, null);
				}
				json_map.put("ret", 100);
			} else {
				json_map.put("ret", -100);
			}
		} else {
			json_map.put("ret", -100);
		}
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);

	}

	/**
	 * 买家取消订单保存
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param order_id
	 * @param state_info
	 * @param other_state_info
	 * @throws Exception
	 */
	@RequestMapping("/app/seller/order_cancel_save.htm")
	public void order_cancel_save(HttpServletRequest request,
			HttpServletResponse response, String user_id, String order_id,
			String state_info, String other_state_info) throws Exception {
		Map json_map = new HashMap();
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		User puser = (user.getParent() == null ? user : user.getParent());
		if (puser.getStore().getId().equals(store.getId())) {
			obj.setOrder_status(0);
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("取消订单");
			ofl.setLog_user(user);		
			ofl.setOf(obj);
			if (state_info.equals("其他原因")) {
				ofl.setState_info(other_state_info);
			} else {
				ofl.setState_info(state_info);
			}
			this.orderFormLogService.save(ofl);
			User buyer = this.userService.getObjById(CommUtil.null2Long(obj
					.getUser_id()));
			Map map = new HashMap();
			map.put("buyer_id", buyer.getId().toString());
			map.put("seller_id", store.getUser().getId().toString());
			map.put("order_id", obj.getId());
			String json = Json.toJson(map);
			if (obj.getOrder_form() == 0) {
				this.msgTools.sendEmailCharge(CommUtil.getURL(request),
						"email_tobuyer_order_cancel_notify", buyer.getEmail(),
						json, null, obj.getStore_id());
				this.msgTools.sendSmsCharge(CommUtil.getURL(request),
						"sms_tobuyer_order_cancel_notify", buyer.getMobile(),
						json, null, obj.getStore_id());
			} else {
				this.msgTools.sendEmailFree(CommUtil.getURL(request),
						"email_tobuyer_order_cancel_notify", buyer.getEmail(),
						json, null);
				this.msgTools.sendSmsFree(CommUtil.getURL(request),
						"sms_tobuyer_order_cancel_notify", buyer.getMobile(),
						json, null);
			}
			json_map.put("ret", 100);
		} else {
			json_map.put("ret", -100);
		}
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	/**
	 * 卖家发货
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param order_id
	 */
	@RequestMapping("/app/seller/order_shipping.htm")
	public void order_shipping(HttpServletRequest request,
			HttpServletResponse response, String user_id, String order_id) {
		Map json_map = new HashMap();
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			json_map.put("ret", 100);
			json_map.put("id", obj.getId());
			json_map.put("order_id", obj.getOrder_id());

			// 当前订单中的虚拟商品
			List<Goods> list_goods = this.orderFormTools.queryOfGoods(order_id);
			List<Goods> deliveryGoods = new ArrayList<Goods>();
			boolean physicalGoods = false;
			for (Goods g : list_goods) {
				if (g.getGoods_choice_type() == 1) {
					deliveryGoods.add(g);
				} else {
					physicalGoods = true;
				}
			}
			json_map.put("physicalGoods", physicalGoods);

			if (physicalGoods) {
				Map params = new HashMap();
				params.put("ecc_type", 0);
				params.put("ecc_store_id", store.getId());
				List<ExpressCompanyCommon> eccs = this.expressCompanyCommonService
						.query("select obj from ExpressCompanyCommon obj where obj.ecc_type=:ecc_type and obj.ecc_store_id=:ecc_store_id",
								params, -1, -1);
				params.clear();
				params.put("sa_type", 0);
				params.put("sa_user_id", user.getId());
				List<ShipAddress> shipAddrs = this.shipAddressService
						.query("select obj from ShipAddress obj where obj.sa_type=:sa_type and obj.sa_user_id=:sa_user_id order by obj.sa_default desc,obj.sa_sequence asc",
								params, -1, -1);// 按照默认地址倒叙、其他地址按照索引升序排序，保证默认地址在第一位

				List eccs_list = new ArrayList();
				for (ExpressCompanyCommon expressCompanyCommon : eccs) {
					Map map = new HashMap();
					map.put("id", expressCompanyCommon.getId());
					map.put("ecc_name", expressCompanyCommon.getEcc_name());
					eccs_list.add(map);
				}
				json_map.put("expressCompanyCommon_list", eccs_list);
				List shipAddrs_list = new ArrayList();
				for (ShipAddress shipAddress : shipAddrs) {
					Map map = new HashMap();
					map.put("id", shipAddress.getId());
					map.put("sa_name", shipAddress.getSa_name());
					shipAddrs_list.add(map);
				}
				json_map.put("shipAddrs_list", shipAddrs_list);
			}

			if (deliveryGoods.size() > 0) {

				List deliveryGoods_list = new ArrayList();
				for (Goods goods : deliveryGoods) {
					Map map = new HashMap();
					map.put("goods_id", goods.getId());
					map.put("goods_name", goods.getGoods_name());
					map.put("goods_count", orderFormTools.queryOfGoodsCount(
							order_id, goods.getId().toString()));
					deliveryGoods_list.add(map);
				}
				json_map.put("deliveryGoods", deliveryGoods_list);
			}

		} else {
			json_map.put("ret", -100);
			json_map.put("msg", "订单参数错误！");
		}
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	/**
	 * 卖家发货保存
	 * 
	 * @param request
	 * @param response
	 * @param order_id
	 * @param user_id
	 * @param shipCode
	 * @param state_info
	 * @param order_seller_intro
	 * @param goods_id
	 * @param goods_name
	 * @param goods_count
	 * @param ecc_id
	 * @param sa_id
	 * @throws Exception
	 */
	@RequestMapping("/app/seller/order_shipping_save.htm")
	public void order_shipping_save(HttpServletRequest request,
			HttpServletResponse response, String order_id, String user_id,
			String shipCode, String state_info, String order_seller_intro,
			String goods_id, String goods_name, String goods_count,
			String ecc_id, String sa_id) throws Exception {

		Map jsonmap = new HashMap();
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		ExpressCompanyCommon ecc = this.expressCompanyCommonService
				.getObjById(CommUtil.null2Long(ecc_id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		User puser = (user.getParent() == null ? user : user.getParent());
		if (puser.getStore().getId().equals(store.getId())) {
			obj.setOrder_status(30);
			obj.setShipCode(shipCode);
			obj.setShipTime(new Date());
			if (ecc != null) {
				Map json_map = new HashMap();
				json_map.put("express_company_id", ecc.getId());
				json_map.put("express_company_name", ecc.getEcc_name());
				json_map.put("express_company_mark", ecc.getEcc_code());
				json_map.put("express_company_type", ecc.getEcc_ec_type());
				obj.setExpress_info(Json.toJson(json_map));
			}
			if (order_seller_intro != null && order_seller_intro.length() > 0) {
				String[] order_seller_intros = order_seller_intro.split(",");
				String[] goods_ids = goods_id.split(",");
				String[] goods_names = goods_name.split(",");
				String[] goods_counts = goods_count.split(",");
				if (order_seller_intros != null
						&& order_seller_intros.length > 0) {
					List<Map> list_map = new ArrayList<Map>();
					for (int i = 0; i < goods_ids.length; i++) {
						Map json_map = new HashMap();
						json_map.put("goods_id", goods_ids[i]);
						json_map.put("goods_name", goods_names[i]);
						json_map.put("goods_count", goods_counts[i]);
						json_map.put("order_seller_intro",
								order_seller_intros[i]);
						json_map.put("order_id", order_id);
						list_map.add(json_map);
					}
					obj.setOrder_seller_intro(Json.toJson(list_map));
				}
			}
			ShipAddress sa = this.shipAddressService.getObjById(CommUtil
					.null2Long(sa_id));
			if (sa != null) {
				obj.setShip_addr_id(sa.getId());
				Area area = this.areaService.getObjById(sa.getSa_area_id());
				obj.setShip_addr(area.getParent().getParent().getAreaName()
						+ area.getParent().getAreaName() + area.getAreaName()
						+ sa.getSa_addr());
			}
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("确认发货");
			ofl.setState_info(state_info);
			ofl.setLog_user(user);		
			ofl.setOf(obj);
			this.orderFormLogService.save(ofl);
			User buyer = this.userService.getObjById(CommUtil.null2Long(obj
					.getUser_id()));
			// 如果是收费接口，则通知快递100，建立订单物流查询推送
			if (this.configService.getSysConfig().getKuaidi_type() == 1) {
				TaskRequest req = new TaskRequest();
				Map express_map = Json.fromJson(Map.class,
						obj.getExpress_info());
				req.setCompany(CommUtil.null2String(express_map
						.get("express_company_mark")));
				String from_addr = obj.getShip_addr();
				req.setFrom(from_addr);
				req.setTo(obj.getReceiver_area());
				req.setNumber(obj.getShipCode());
				req.getParameters().put(
						"callbackurl",
						CommUtil.getURL(request)
								+ "/kuaidi100_callback.htm?order_id="
								+ obj.getId() + "&orderType=0");
				req.getParameters().put(
						"salt",
						Md5Encrypt.md5(CommUtil.null2String(obj.getId()))
								.toLowerCase());
				req.setKey(this.configService.getSysConfig().getKuaidi_id2());
				HashMap<String, String> p = new HashMap<String, String>();
				p.put("schema", "json");
				p.put("param", JacksonHelper.toJSON(req));
				try {
					String ret = HttpRequest.postData(
							"http://www.kuaidi100.com/poll", p, "UTF-8");
					TaskResponse resp = JacksonHelper.fromJSON(ret,
							TaskResponse.class);
					if (resp.getResult() == true) {
						ExpressInfo ei = new ExpressInfo();
						ei.setAddTime(new Date());
						ei.setOrder_id(obj.getId());
						ei.setOrder_express_id(obj.getShipCode());
						ei.setOrder_type(0);
						Map map = Json.fromJson(Map.class,
								CommUtil.null2String(obj.getExpress_info()));
						if (map != null) {
							ei.setOrder_express_name(CommUtil.null2String(map
									.get("express_company_name")));
						}
						// System.out.println(Json.toJson(result.getData(),JsonFormat.compact()));
						this.expressInfoService.save(ei);
						System.out.println("订阅成功");
					} else {
						System.out.println("订阅失败");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 异步通知支付宝,只有在支付宝担保支付情况下才支持此接口
			Payment payment = this.paymentService.getObjById(obj
					.getPayment().getId());
			if (payment != null && payment.getMark().equals("alipay")
					&& payment.getInterfaceType() == 1) {
				// 把请求参数打包成数组
				boolean synch = false;
				String safe_key = "";
				String partner = "";
				if (!CommUtil.null2String(payment.getSafeKey()).equals("")
						&& !CommUtil.null2String(payment.getPartner()).equals(
								"")) {
					safe_key = payment.getSafeKey();
					partner = payment.getPartner();
					synch = true;
				}
				if (synch) {
					AlipayConfig config = new AlipayConfig();
					config.setKey(safe_key);
					config.setPartner(partner);
					Map<String, String> sParaTemp = new HashMap<String, String>();
					sParaTemp.put("service", "send_goods_confirm_by_platform");
					sParaTemp.put("partner", config.getPartner());
					sParaTemp.put("_input_charset", config.getInput_charset());
					sParaTemp.put("trade_no", obj.getOut_order_id());
					sParaTemp.put("logistics_name", ecc.getEcc_name());
					sParaTemp.put("invoice_no", shipCode);
					sParaTemp.put("transport_type", ecc.getEcc_ec_type());
					// 建立请求
					String sHtmlText = AlipaySubmit.buildRequest(config, "web",
							sParaTemp, "", "");
					// System.out.println(sHtmlText);
				}
			}
			Map map = new HashMap();
			map.put("buyer_id", buyer.getId().toString());
			map.put("seller_id", store.getUser().getId().toString());
			map.put("order_id", obj.getId());
			String json = Json.toJson(map);
			if (obj.getOrder_form() == 0) {
				this.msgTools.sendEmailCharge(CommUtil.getURL(request),
						"email_tobuyer_order_ship_notify", buyer.getEmail(),
						json, null, obj.getStore_id());
				this.msgTools.sendSmsCharge(CommUtil.getURL(request),
						"sms_tobuyer_order_ship_notify", buyer.getMobile(),
						json, null, obj.getStore_id());
			} else {
				this.msgTools.sendEmailFree(CommUtil.getURL(request),
						"email_tobuyer_order_ship_notify", buyer.getEmail(),
						json, null);
				this.msgTools.sendSmsFree(CommUtil.getURL(request),
						"sms_tobuyer_order_ship_notify", buyer.getMobile(),
						json, null);
			}

			jsonmap.put("ret", 100);
		} else {
			jsonmap.put("ret", -100);
		}
		this.send_json(Json.toJson(jsonmap, JsonFormat.compact()), response);
	}

	/**
	 * 物流修改
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param order_id
	 */
	@RequestMapping("/app/seller/order_shipping_code.htm")
	public void order_shipping_code(HttpServletRequest request,
			HttpServletResponse response, String user_id, String order_id) {
		Map json_map = new HashMap();
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();

		if (user.getStore().getId().equals(store.getId())) {
			json_map.put("ret", 100);
			json_map.put("id", obj.getId());
			json_map.put("order_id", obj.getOrder_id());
			json_map.put("shipCode", obj.getShipCode());
		} else {
			json_map.put("ret", -100);
			json_map.put("msg", "您没有编号为" + order_id + "的订单！");
		}
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	/**
	 * 物流修改保存
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param order_id
	 * @param shipCode
	 * @param state_info
	 */
	@RequestMapping("/app/seller/order_shipping_code_save.htm")
	public void order_shipping_code_save(HttpServletRequest request,
			HttpServletResponse response, String user_id, String order_id,
			String shipCode, String state_info) {
		Map json_map = new HashMap();
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		User puser = user.getParent() == null ? user : user.getParent();
		if (puser.getStore().getId().equals(store.getId())) {
			obj.setShipCode(shipCode);
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("修改物流信息");
			ofl.setState_info(state_info);
			ofl.setLog_user(puser);
			ofl.setOf(obj);
			this.orderFormLogService.save(ofl);
			// 如果是收费接口，则通知快递100，建立订单物流查询推送
			if (this.configService.getSysConfig().getKuaidi_type() == 1) {
				TaskRequest req = new TaskRequest();
				Map express_map = Json.fromJson(Map.class,
						obj.getExpress_info());
				req.setCompany(CommUtil.null2String(express_map
						.get("express_company_mark")));
				String from_addr = obj.getShip_addr();
				req.setFrom(from_addr);
				req.setTo(obj.getReceiver_area());
				req.setNumber(obj.getShipCode());
				req.getParameters().put(
						"callbackurl",
						CommUtil.getURL(request)
								+ "/kuaidi100_callback.htm?order_id="
								+ obj.getId() + "&orderType=0");
				req.getParameters().put(
						"salt",
						Md5Encrypt.md5(CommUtil.null2String(obj.getId()))
								.toLowerCase());
				req.setKey(this.configService.getSysConfig().getKuaidi_id2());
				HashMap<String, String> p = new HashMap<String, String>();
				p.put("schema", "json");
				p.put("param", JacksonHelper.toJSON(req));
				try {
					String ret = HttpRequest.postData(
							"http://www.kuaidi100.com/poll", p, "UTF-8");
					TaskResponse resp = JacksonHelper.fromJSON(ret,
							TaskResponse.class);
					if (resp.getResult() == true) {
						ExpressInfo ei = new ExpressInfo();
						ei.setAddTime(new Date());
						ei.setOrder_id(obj.getId());
						ei.setOrder_express_id(obj.getShipCode());
						ei.setOrder_type(0);
						Map map = Json.fromJson(Map.class,
								CommUtil.null2String(obj.getExpress_info()));
						if (map != null) {
							ei.setOrder_express_name(CommUtil.null2String(map
									.get("express_company_name")));
						}
						// System.out.println(Json.toJson(result.getData(),JsonFormat.compact()));
						this.expressInfoService.save(ei);
						System.out.println("订阅成功");
					} else {
						System.out.println("订阅失败");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			json_map.put("ret", 100);
		}
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	/**
	 * 延长收货时间保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param delay_time
	 * @param currentPage
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/app/seller/order_confirm_delay_save.htm")
	public void order_confirm_delay_save(HttpServletRequest request,
			HttpServletResponse response, String user_id, String order_id,
			String delay_time) throws Exception {
		Map json_map = new HashMap();
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		User puser = user.getParent() == null ? user : user.getParent();
		if (puser.getStore().getId().equals(store.getId())) {
			obj.setOrder_confirm_delay(obj.getOrder_confirm_delay()
					+ CommUtil.null2Int(delay_time));
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("延长收货时间");
			ofl.setState_info("延长收货时间：" + delay_time + "天");
			ofl.setLog_user(puser);
			ofl.setOf(obj);
			this.orderFormLogService.save(ofl);
			json_map.put("ret", 100);
		} else {
			json_map.put("ret", -100);
		}
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	@RequestMapping("/app/seller/order_detail.htm")
	public void order_detail(HttpServletRequest request,
			HttpServletResponse response, String user_id, String order_id) {
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		if (user.getStore().getId().equals(store.getId())) {
			json_map.put("ret", 100);

			json_map.put("order_id", obj.getId());// 订单id
			json_map.put("order_num", obj.getOrder_id());// 订单号
			json_map.put("addTime", obj.getAddTime());// 下单时间
			json_map.put("order_total_price",
					this.orderFormTools.query_order_price(order_id));// 订单总金额
			json_map.put("goods_price",
					this.orderFormTools.query_order_goods(order_id));// 商品金额
			json_map.put("reduce_amount",
					CommUtil.null2Double(obj.getEnough_reduce_amount()));// 满减金额
			if (obj.getOrder_status() == 16 || obj.getOrder_status() == 20) {// 当已经付款或者货到付款，查询该订单是否可以确认收货
				json_map.put("order_status",
						this.query_order_status(obj.getId()));
			} else {
				json_map.put("order_status", obj.getOrder_status());
			}
			if (CommUtil.null2Int(json_map.get("order_status")) < 16) {
				json_map.put("payType", "未支付");// 支付方式
			} else {
				if (obj.getPayment().getMark() != null
						&& !obj.getPayment().getMark().equals("")) {
					String pay_mark = obj.getPayment().getMark();
					if (pay_mark.equals("alipay_app")) {
						json_map.put("payType", "手机App支付宝");
					}
					if (pay_mark.equals("alipay_wap")) {
						json_map.put("payType", "手机网页支付宝");
					}
					if (pay_mark.equals("alipay")) {
						json_map.put("payType", "支付宝");
					}
					if (pay_mark.equals("wx_app")) {
						json_map.put("payType", "手机App微信支付");
					}
					if (pay_mark.equals("wx_pay")) {
						json_map.put("payType", "微信支付");
					}
					if (pay_mark.equals("balance")) {
						json_map.put("payType", "预存款");
					}
					if (pay_mark.equals("tenpay")) {
						json_map.put("payType", "财付通");
					}
					if (pay_mark.equals("bill")) {
						json_map.put("payType", "快钱支付");
					}
					if (pay_mark.equals("chinabank")) {
						json_map.put("payType", "网银支付");
					}
					if (pay_mark.equals("paypal")) {
						json_map.put("payType", "Paypal支付");
					}
				} else {
					if (obj.getPayType().equals("payafter")) {
						json_map.put("payType", "货到付款");
					}
				}
			}

			if (obj.getExpress_info() != null
					&& !obj.getExpress_info().equals("")) {
				Map express_map = this.orderFormTools.queryCouponInfo(obj
						.getExpress_info());
				json_map.put("express_company",
						express_map.get("express_company_name"));// 物流公司信息
			}
			json_map.put("shipTime", obj.getShipTime());// 发货时间

			json_map.put("train_order_id", obj.getId());// 物流对应订单id
			json_map.put("shipCode", obj.getShipCode());// 物流单号

			json_map.put("ship_msg", obj.getMsg());
			json_map.put("ship_price", obj.getShip_price());// 运费
			json_map.put("payTime", obj.getPayTime());// 付款时间
			json_map.put("confirmTime", obj.getConfirmTime());// 确认收货时间
			json_map.put("finishTime", obj.getFinishTime());// 完成时间
			json_map.put("receiver_Name", obj.getReceiver_Name());// 收货人姓名
			json_map.put("receiver_area", obj.getReceiver_area());// 收货地址
			json_map.put("receiver_area_info", obj.getReceiver_area_info());// 地址详情
			json_map.put("receiver_zip", obj.getReceiver_zip());// 地址邮编
			json_map.put("receiver_telephone", obj.getReceiver_telephone());// 收货人电话
			json_map.put("receiver_mobile", obj.getReceiver_mobile());// 收货人手机
			if (obj.getCoupon_info() != null
					&& !obj.getCoupon_info().equals("")) {
				Map coupon_map = this.orderFormTools.queryCouponInfo(obj
						.getCoupon_info());
				json_map.put("coupon_price", coupon_map.get("coupon_amount"));// 优惠券价格
			} else {
				json_map.put("coupon_price", "0.0");// 优惠券价格
			}
			String invoiceType = "普通发票";
			if (obj.getInvoiceType() == 1) {
				invoiceType = "增值税发票";
			}
			json_map.put("invoiceType", invoiceType);
			json_map.put("invoice", obj.getInvoice());// 发票信息
			List goods_list = new ArrayList();
			List<Map> temp_maps = this.orderFormTools.queryGoodsInfo(obj
					.getGoods_info());
			String url = CommUtil.getURL(request);
			for (Map goods : temp_maps) {
				Map goods_map = new HashMap();
				goods_map.put("goods_id", goods.get("goods_id"));
				goods_map.put("goods_name", goods.get("goods_name"));
				goods_map.put("goods_type", goods.get("goods_type"));
				goods_map.put("goods_count", goods.get("goods_count"));
				goods_map.put("goods_price", goods.get("goods_price"));
				goods_map.put("goods_gsp_val", goods.get("goods_gsp_val"));
				goods_map.put("goods_mainphoto_path",
						url + "/" + goods.get("goods_mainphoto_path"));
				goods_list.add(goods_map);
			}
			if (obj.getChild_order_detail() != null
					&& !obj.getChild_order_detail().equals("")) {
				List<Map> temp_maps2 = this.orderFormTools.queryGoodsInfo(obj
						.getChild_order_detail());
				for (Map goods : temp_maps2) {
					String child_order_id = CommUtil.null2String(goods
							.get("order_id"));
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(child_order_id));
					List<Map> temp_maps3 = this.orderFormTools
							.queryGoodsInfo(child_order.getGoods_info());
					for (Map goods3 : temp_maps3) {
						Map goods_map = new HashMap();
						goods_map.put("goods_id", goods3.get("goods_id"));
						goods_map.put("goods_name", goods3.get("goods_name"));
						goods_map.put("goods_type", goods3.get("goods_type"));
						goods_map.put("goods_count", goods3.get("goods_count"));
						goods_map.put("goods_price", goods3.get("goods_price"));
						goods_map.put("goods_gsp_val",
								goods3.get("goods_gsp_val"));
						goods_map.put("goods_mainphoto_path", url + "/"
								+ goods3.get("goods_mainphoto_path"));
						goods_list.add(goods_map);
					}
				}
			}
			json_map.put("goods_list", goods_list);
		} else {
			json_map.put("ret", -100);
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

	/**
	 * 查询订单的状态，用在买家中心的订单列表中，多商家复合订单中只有全部商家都已经发货，卖家中心才会出现确认收货按钮
	 * 
	 * @param order_id
	 * @return
	 */
	public int query_order_status(Long order_id) {
		int order_status = 0;
		OrderForm order = this.orderFormService.getObjById(order_id);
		if (order != null) {
			order_status = order.getOrder_status();
			if (order.getOrder_main() == 1
					&& !CommUtil.null2String(order.getChild_order_detail())
							.equals("")) {
				List<Map> maps = this.orderFormTools.queryGoodsInfo(order
						.getChild_order_detail());
				for (Map child_map : maps) {
					OrderForm child_order = this.orderFormService
							.getObjById(CommUtil.null2Long(child_map
									.get("order_id")));
					if (child_order.getOrder_status() < 30) {
						order_status = child_order.getOrder_status();
					}
				}
			}
		}
		return order_status;
	}

}
