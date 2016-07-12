package com.iskyshop.manage.seller.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.nutz.json.Json;
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
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.ExpressCompany;
import com.iskyshop.foundation.domain.ExpressCompanyCommon;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.ShipAddress;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.OrderFormQueryObject;
import com.iskyshop.foundation.domain.virtual.TransInfo;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IExpressCompanyCommonService;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsReturnService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IRefundLogService;
import com.iskyshop.foundation.service.IShipAddressService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.kuaidi100.domain.ExpressInfo;
import com.iskyshop.kuaidi100.pojo.TaskRequest;
import com.iskyshop.kuaidi100.pojo.TaskResponse;
import com.iskyshop.kuaidi100.post.HttpRequest;
import com.iskyshop.kuaidi100.post.JacksonHelper;
import com.iskyshop.kuaidi100.service.IExpressInfoService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.PaymentTools;
import com.iskyshop.manage.buyer.tools.ShipTools;
import com.iskyshop.manage.seller.tools.OrderTools;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.pay.alipay.config.AlipayConfig;
import com.iskyshop.pay.alipay.util.AlipaySubmit;
import com.iskyshop.view.web.tools.StoreViewTools;

/**
 * 
 * <p>
 * Title: OrderSellerAction.java
 * </p>
 * 
 * <p>
 * Description:卖家订单控制器，卖家中心订单管理所有控制器都在这里
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
 * @date 2014-5-20
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class OrderSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IRefundLogService refundLogService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsReturnService goodsReturnService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IExpressCompanyService expressCompanyService;
	@Autowired
	private IExpressCompanyCommonService expressCompanyCommonService;
	@Autowired
	private IShipAddressService shipAddressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private IPayoffLogService payoffservice;
	@Autowired
	private IExpressInfoService expressInfoService;
	@Autowired
	private OrderTools orderTools;
	@Autowired
	private ShipTools ShipTools;

	private static final BigDecimal WHETHER_ENOUGH = new BigDecimal(0.00);
	private static final Map<Integer, String> STATUS_MAP = new HashMap<Integer, String>() {
		{
			put(0, "已取消");
			put(10, "待付款");
			put(15, "线下支付待审核");
			put(16, "货到付款待发货");
			put(20, "已付款");
			put(30, "已发货");
			put(40, "已收货");
			put(50, "已完成");
			put(60, "已结束");
		}
	};

	private static final Map<String, String> PAYMENT_MAP = new HashMap<String, String>() {
		{
			put(null, "未支付");
			put("", "未支付");
			put("alipay", "支付宝");
			put("alipay_wap", "手机网页支付宝");
			put("alipay_app", "手机支付宝APP");
			put("tenpay", "财付通");
			put("bill", "快钱");
			put("chinabank", "网银在线");
			put("outline", "线下支付");
			put("balance", "预存款支付");
			put("payafter", "货到付款");
			put("paypal", "paypal");
		}
	};

	private static final Map<String, String> TYPE_MAP = new HashMap<String, String>() {
		{
			put(null, "PC订单");
			put("", "PC订单");
			put("weixin", "微信订单");
			put("android", "Android订单");
			put("ios", "IOS订单");
		}
	};

	@SecurityMapping(title = "卖家订单列表", value = "/seller/order.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order.htm")
	public ModelAndView order(HttpServletRequest request,
			HttpServletResponse response, String currentPage,
			String order_status, String order_id, String beginTime,
			String endTime, String buyer_userName) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_order.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv,
				"addTime", "desc");
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		ofqo.addQuery("obj.store_id", new SysMap("store_id", user.getStore()
				.getId().toString()), "=");
		ofqo.addQuery("obj.order_cat", new SysMap("order_cat", 2), "!=");
		if (!CommUtil.null2String(order_status).equals("")) {
			if (order_status.equals("order_submit")) {// 已经提交
				Map map = new HashMap();
				map.put("order_status1", 10);
				map.put("order_status2", 16);
				ofqo.addQuery(
						"(obj.order_status=:order_status1 or obj.order_status=:order_status2)",
						map);
			}
			if (order_status.equals("order_pay")) {// 已经付款
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status", 20), "=");
			}
			if (order_status.equals("order_shipping")) {// 已经发货
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status", 30), "=");
			}
			if (order_status.equals("order_evaluate")) {// 等待评价
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status", 40), "=");
			}
			if (order_status.equals("order_finish")) {// 已经完成
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status", 50), "=");
			}
			if (order_status.equals("order_cancel")) {// 已经取消
				ofqo.addQuery("obj.order_status",
						new SysMap("order_status", 0), "=");
			}
		}
		if (!CommUtil.null2String(order_id).equals("")) {
			ofqo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id
					+ "%"), "like");
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			ofqo.addQuery("obj.addTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
			mv.addObject("beginTime", beginTime);
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			String ends = endTime + " 23:59:59";
			ofqo.addQuery(
					"obj.addTime",
					new SysMap("endTime", CommUtil.formatDate(ends,
							"yyyy-MM-dd hh:mm:ss")), "<=");
			mv.addObject("endTime", endTime);
		}
		if (!CommUtil.null2String(buyer_userName).equals("")) {
			ofqo.addQuery("obj.user_name", new SysMap("user_name",
					buyer_userName), "=");
		}
		IPageList pList = this.orderFormService.list(ofqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("order_id", order_id);
		mv.addObject("order_status", order_status == null ? "all"
				: order_status);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("buyer_userName", buyer_userName);
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("store", user.getStore());
		return mv;
	}

	@SecurityMapping(title = "卖家待发货订单管理", value = "/seller/order_ship.htm*", rtype = "seller", rname = "发货管理", rcode = "order_ship_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_ship.htm")
	public ModelAndView order_ship(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String order_id,
			String beginTime, String endTime, String buyer_userName) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_order_ship.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv,
				"payTime", "asc");
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		ofqo.addQuery("obj.store_id", new SysMap("store_id", user.getStore()
				.getId().toString()), "=");
		ofqo.addQuery("obj.order_cat", new SysMap("order_cat", 2), "!=");
		ofqo.addQuery("obj.order_status", new SysMap("order_status1", 16), ">=");
		ofqo.addQuery("obj.order_status", new SysMap("order_status2", 20), "<=");
		if (!CommUtil.null2String(order_id).equals("")) {
			ofqo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id
					+ "%"), "like");
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			ofqo.addQuery("obj.addTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
			mv.addObject("beginTime", beginTime);
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			String ends = endTime + " 23:59:59";
			ofqo.addQuery(
					"obj.addTime",
					new SysMap("endTime", CommUtil.formatDate(ends,
							"yyyy-MM-dd hh:mm:ss")), "<=");
			mv.addObject("endTime", endTime);
		}
		if (!CommUtil.null2String(buyer_userName).equals("")) {
			ofqo.addQuery("obj.user_name", new SysMap("user_name",
					buyer_userName), "=");
		}
		// System.out.println(ofqo.getQuery());
		IPageList pList = this.orderFormService.list(ofqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("order_id", order_id);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("buyer_userName", buyer_userName);
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("store", user.getStore());
		return mv;
	}

	@SecurityMapping(title = "卖家发货待收货订单管理", value = "/seller/order_confirm.htm*", rtype = "seller", rname = "收货管理", rcode = "order_confirm_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_confirm.htm")
	public ModelAndView order_confirm(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String order_id,
			String beginTime, String endTime, String buyer_userName) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_order_confirm.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderFormQueryObject ofqo = new OrderFormQueryObject(currentPage, mv,
				"shipTime", "desc");
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		ofqo.addQuery("obj.store_id", new SysMap("store_id", user.getStore()
				.getId().toString()), "=");
		ofqo.addQuery("obj.order_cat", new SysMap("order_cat", 2), "!=");
		Map temp = new HashMap();
		temp.put("order_status1", 30);
		temp.put("order_status2", 35);
		ofqo.addQuery(
				"and (obj.order_status =:order_status1 or obj.order_status =:order_status2)",
				temp);
		if (!CommUtil.null2String(order_id).equals("")) {
			ofqo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id
					+ "%"), "like");
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			ofqo.addQuery("obj.addTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
			mv.addObject("beginTime", beginTime);
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			String ends = endTime + " 23:59:59";
			ofqo.addQuery(
					"obj.addTime",
					new SysMap("endTime", CommUtil.formatDate(ends,
							"yyyy-MM-dd hh:mm:ss")), "<=");
			mv.addObject("endTime", endTime);
		}
		if (!CommUtil.null2String(buyer_userName).equals("")) {
			ofqo.addQuery("obj.user_name", new SysMap("user_name",
					buyer_userName), "=");
		}
		// System.out.println(ofqo.getQuery());
		IPageList pList = this.orderFormService.list(ofqo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("order_id", order_id);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("buyer_userName", buyer_userName);
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("store", user.getStore());
		mv.addObject("orderTools", this.orderTools);
		return mv;
	}

	@SecurityMapping(title = "卖家订单详情", value = "/seller/order_view.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_view.htm")
	public ModelAndView order_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/order_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			mv.addObject("obj", obj);
			mv.addObject("store", store);
			mv.addObject("orderFormTools", orderFormTools);
			mv.addObject("express_company_name", this.orderFormTools
					.queryExInfo(obj.getExpress_info(), "express_company_name"));
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家取消订单", value = "/seller/order_cancel.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_cancel.htm")
	public ModelAndView order_cancel(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_order_cancel.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家取消订单保存", value = "/seller/order_cancel_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_cancel_save.htm")
	public String order_cancel_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String state_info, String other_state_info) throws Exception {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			obj.setOrder_status(0);
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("取消订单");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
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

		}
		return "redirect:order.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "卖家调整订单费用", value = "/seller/order_fee.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_fee.htm")
	public ModelAndView order_fee(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_order_fee.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家调整订单费用保存", value = "/seller/order_fee_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_fee_save.htm")
	public String order_fee_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String goods_amount, String ship_price, String totalPrice)
			throws Exception {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		if (CommUtil.null2Double(obj.getCommission_amount()) <= CommUtil
				.null2Double(goods_amount)) {
			Store store = this.storeService.getObjById(CommUtil.null2Long(obj
					.getStore_id()));
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
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
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
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
			}
		}
		return "redirect:order.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "卖家确认发货", value = "/seller/order_shipping.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_shipping.htm")
	public ModelAndView order_shipping(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String page_status) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_order_shipping.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
			// 当前订单中的虚拟商品
			List<Goods> list_goods = this.orderFormTools.queryOfGoods(id);
			List<Goods> deliveryGoods = new ArrayList<Goods>();
			boolean physicalGoods = false;
			for (Goods g : list_goods) {
				if (g.getGoods_choice_type() == 1) {
					deliveryGoods.add(g);
				} else {
					physicalGoods = true;
				}
			}
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
			mv.addObject("eccs", eccs);
			mv.addObject("shipAddrs", shipAddrs);
			mv.addObject("physicalGoods", physicalGoods);
			mv.addObject("deliveryGoods", deliveryGoods);
			mv.addObject("page_status", page_status);
			mv.addObject("orderFormTools", orderFormTools);
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "订单参数错误！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家确认发货保存", value = "/seller/order_shipping_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_shipping_save.htm")
	public String order_shipping_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String shipCode, String state_info, String order_seller_intro,
			String ecc_id, String sa_id, String page_status) throws Exception {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		ExpressCompanyCommon ecc = this.expressCompanyCommonService
				.getObjById(CommUtil.null2Long(ecc_id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
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
			String[] order_seller_intros = request
					.getParameterValues("order_seller_intro");
			String[] goods_ids = request.getParameterValues("goods_id");
			String[] goods_names = request.getParameterValues("goods_name");
			String[] goods_counts = request.getParameterValues("goods_count");
			if (order_seller_intros != null && order_seller_intros.length > 0) {
				List<Map> list_map = new ArrayList<Map>();
				for (int i = 0; i < goods_ids.length; i++) {
					Map json_map = new HashMap();
					json_map.put("goods_id", goods_ids[i]);
					json_map.put("goods_name", goods_names[i]);
					json_map.put("goods_count", goods_counts[i]);
					json_map.put("order_seller_intro", order_seller_intros[i]);
					json_map.put("order_id", id);
					list_map.add(json_map);
				}
				obj.setOrder_seller_intro(Json.toJson(list_map));
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
			if (obj.getPayment() != null
					&& obj.getPayment().getMark().equals("alipay")
					&& obj.getPayment().getInterfaceType() == 1) {
				// 把请求参数打包成数组
				boolean synch = false;
				String safe_key = "";
				String partner = "";
				if (!CommUtil.null2String(obj.getPayment().getSafeKey())
						.equals("")
						&& !CommUtil.null2String(obj.getPayment().getPartner())
								.equals("")) {
					safe_key = obj.getPayment().getSafeKey();
					partner = obj.getPayment().getPartner();
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
		}
		if (page_status.equals("order_ship")) {
			return "redirect:order_ship.htm?currentPage=" + currentPage;
		} else
			return "redirect:order.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "卖家修改物流", value = "/seller/order_shipping_code.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_shipping_code.htm")
	public ModelAndView order_shipping_code(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_order_shipping_code.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家修改物流保存", value = "/seller/order_shipping_code_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_shipping_code_save.htm")
	public String order_shipping_code_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String shipCode, String state_info) {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			obj.setShipCode(shipCode);
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("修改物流信息");
			ofl.setState_info(state_info);
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
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
		}
		return "redirect:order.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "打印订单", value = "/seller/order_print.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_print.htm")
	public ModelAndView order_print(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/order_print.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			OrderForm orderform = this.orderFormService.getObjById(CommUtil
					.null2Long(id));
			Store store = this.storeService.getObjById(CommUtil
					.null2Long(orderform.getStore_id()));
			mv.addObject("store", store);
			mv.addObject("obj", orderform);
			mv.addObject("orderFormTools", orderFormTools);
		}
		return mv;
	}

	@SecurityMapping(title = "卖家物流详情", value = "/seller/ship_view.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/ship_view.htm")
	public ModelAndView order_ship_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/order_ship_view.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			mv.addObject("obj", obj);
			TransInfo transInfo = this.ShipTools
					.query_Ordership_getData(CommUtil.null2String(obj.getId()));
			mv.addObject("transInfo", transInfo);
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "卖家订单导出Excel", value = "/seller/order_excel.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_excel.htm")
	public void order_excel(HttpServletRequest request,
			HttpServletResponse response, String order_status, String order_id,
			String beginTime, String endTime, String buyer_userName) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		OrderFormQueryObject qo = new OrderFormQueryObject();
		qo.setPageSize(1000000000);
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		qo.addQuery("obj.store_id", new SysMap("store_id", user.getStore()
				.getId().toString()), "=");
		qo.addQuery("obj.order_cat", new SysMap("order_cat", 2), "!=");
		if (!CommUtil.null2String(order_status).equals("")) {
			if (order_status.equals("order_submit")) {// 已经提交
				Map map = new HashMap();
				map.put("order_status1", 10);
				map.put("order_status2", 16);
				qo.addQuery(
						"(obj.order_status=:order_status1 or obj.order_status=:order_status2)",
						map);
			}
			if (order_status.equals("order_pay")) {// 已经付款
				qo.addQuery("obj.order_status", new SysMap("order_status", 20),
						"=");
			}
			if (order_status.equals("order_shipping")) {// 已经发货
				qo.addQuery("obj.order_status", new SysMap("order_status", 30),
						"=");
			}
			if (order_status.equals("order_evaluate")) {// 等待评价
				qo.addQuery("obj.order_status", new SysMap("order_status", 40),
						"=");
			}
			if (order_status.equals("order_finish")) {// 已经完成
				qo.addQuery("obj.order_status", new SysMap("order_status", 50),
						"=");
			}
			if (order_status.equals("order_cancel")) {// 已经取消
				qo.addQuery("obj.order_status", new SysMap("order_status", 0),
						"=");
			}
		}
		if (!CommUtil.null2String(order_id).equals("")) {
			qo.addQuery("obj.order_id", new SysMap("order_id", "%" + order_id
					+ "%"), "like");
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			qo.addQuery("obj.addTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			String ends = endTime + " 23:59:59";
			qo.addQuery(
					"obj.addTime",
					new SysMap("endTime", CommUtil.formatDate(ends,
							"yyyy-MM-dd hh:mm:ss")), "<=");
		}
		if (!CommUtil.null2String(buyer_userName).equals("")) {
			qo.addQuery("obj.user_name",
					new SysMap("user_name", buyer_userName), "=");
		}
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DAY_OF_MONTH,
				ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		qo.setOrderType("desc");
		IPageList pList = this.orderFormService.list(qo);
		if (pList.getResult() != null) {
			List<OrderForm> datas = pList.getResult();
			// 创建Excel的工作书册 Workbook,对应到一个excel文档
			HSSFWorkbook wb = new HSSFWorkbook();
			// 创建Excel的工作sheet,对应到一个excel文档的tab
			HSSFSheet sheet = wb.createSheet("订单列表");
			HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
			List<HSSFClientAnchor> anchor = new ArrayList<HSSFClientAnchor>();
			for (int i = 0; i < datas.size(); i++) {
				anchor.add(new HSSFClientAnchor(0, 0, 1000, 255, (short) 1,
						2 + i, (short) 1, 2 + i));
			}
			// 设置excel每列宽度
			sheet.setColumnWidth(0, 6000);
			sheet.setColumnWidth(1, 4000);
			sheet.setColumnWidth(2, 4000);
			sheet.setColumnWidth(3, 6000);
			sheet.setColumnWidth(4, 6000);
			sheet.setColumnWidth(5, 6000);
			sheet.setColumnWidth(6, 6000);
			sheet.setColumnWidth(7, 6000);
			sheet.setColumnWidth(8, 6000);
			sheet.setColumnWidth(9, 6000);
			sheet.setColumnWidth(10, 6000);
			sheet.setColumnWidth(11, 8000);
			// 创建字体样式
			HSSFFont font = wb.createFont();
			font.setFontName("Verdana");
			font.setBoldweight((short) 100);
			font.setFontHeight((short) 300);
			font.setColor(HSSFColor.BLUE.index);
			// 创建单元格样式
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			style.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			// 设置边框
			style.setBottomBorderColor(HSSFColor.RED.index);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style.setFont(font);// 设置字体
			// 创建Excel的sheet的一行
			HSSFRow row = sheet.createRow(0);
			row.setHeight((short) 500);// 设定行的高度
			// 创建一个Excel的单元格
			HSSFCell cell = row.createCell(0);
			// 合并单元格(startRow，endRow，startColumn，endColumn)
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));
			// 给Excel的单元格设置样式和赋值
			cell.setCellStyle(style);
			String title = "订单列表";
			Date time1 = CommUtil.formatDate(beginTime);
			Date time2 = CommUtil.formatDate(endTime);
			String time = CommUtil.null2String(CommUtil.formatShortDate(time1)
					+ " - " + CommUtil.formatShortDate(time2));
			cell.setCellValue(this.configService.getSysConfig().getTitle()
					+ title + "（" + time + "）");
			// 设置单元格内容格式时间
			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd"));
			style1.setWrapText(true);// 自动换行
			style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			HSSFCellStyle style2 = wb.createCellStyle();
			style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			row = sheet.createRow(1);
			cell = row.createCell(0);
			cell.setCellStyle(style2);
			cell.setCellValue("订单号");
			cell = row.createCell(1);
			cell.setCellStyle(style2);
			cell.setCellValue("下单时间");
			cell = row.createCell(2);
			cell.setCellStyle(style2);
			cell.setCellValue("支付方式");
			cell = row.createCell(3);
			cell.setCellStyle(style2);
			cell.setCellValue("订单类型");
			cell = row.createCell(4);
			cell.setCellStyle(style2);
			cell.setCellValue("商品");
			cell = row.createCell(5);
			cell.setCellStyle(style2);
			cell.setCellValue("物流单号");
			cell = row.createCell(6);
			cell.setCellStyle(style2);
			cell.setCellValue("运费");
			cell = row.createCell(7);
			cell.setCellStyle(style2);
			cell.setCellValue("商品总价");
			cell = row.createCell(8);
			cell.setCellStyle(style2);
			cell.setCellValue("订单总额");
			cell = row.createCell(9);
			cell.setCellStyle(style2);
			cell.setCellValue("订单状态");
			cell = row.createCell(10);
			cell.setCellStyle(style2);
			cell.setCellValue("发货时间");
			cell = row.createCell(11);
			cell.setCellStyle(style2);
			cell.setCellValue("活动信息");
			double all_order_price = 0.00;// 订单总金额
			double all_total_amount = 0.00;// 商品总金额
			for (int j = 2; j <= datas.size() + 1; j++) {
				row = sheet.createRow(j);
				// 设置单元格的样式格式
				int i = 0;
				cell = row.createCell(i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getOrder_id());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.formatLongDate(datas.get(j - 2)
						.getAddTime()));

				cell = row.createCell(++i);
				cell.setCellStyle(style2);

				if (datas.get(j - 2).getPayment() != null) {
					cell.setCellValue(PAYMENT_MAP.get(datas.get(j - 2)
							.getPayment().getMark()));
				} else {
					cell.setCellValue("未支付");
				}

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(TYPE_MAP
						.get(datas.get(j - 2).getOrder_type()));

				List<Map> goods_json = Json.fromJson(List.class,
						datas.get(j - 2).getGoods_info());
				StringBuilder sb = new StringBuilder();
				boolean whether_combin = false;
				for (Map map : goods_json) {
					sb.append(map.get("goods_name") + "*"
							+ map.get("goods_count") + ",");
					if (map.get("goods_type").toString().equals("combin")) {
						whether_combin = true;
					}
				}
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(sb.toString());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getShipCode());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getShip_price().toString());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(datas.get(j - 2).getGoods_amount().toString());
				all_total_amount = CommUtil.add(all_total_amount,
						datas.get(j - 2).getGoods_amount());// 计算商品总价

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.null2String(datas.get(j - 2)
						.getTotalPrice()));
				all_order_price = CommUtil.add(all_order_price, datas
						.get(j - 2).getTotalPrice());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(STATUS_MAP.get(datas.get(j - 2)
						.getOrder_status()));

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.formatLongDate(datas.get(j - 2)
						.getShipTime()));

				if (datas.get(j - 2).getWhether_gift() == 1) {
					List<Map> gifts_json = Json.fromJson(List.class,
							datas.get(j - 2).getGift_infos());
					StringBuilder gsb = new StringBuilder();
					for (Map map : gifts_json) {
						gsb.append(map.get("goods_name") + ",");
					}
					cell = row.createCell(++i);
					cell.setCellStyle(style2);
					cell.setCellValue(gsb.toString());
				}
				if (datas.get(j - 2).getEnough_reduce_amount() != null
						&& datas.get(j - 2).getEnough_reduce_amount()
								.compareTo(WHETHER_ENOUGH) == 1) {
					cell = row.createCell(++i);
					cell.setCellStyle(style2);
					cell.setCellValue("满减");
				}
				if (whether_combin) {
					cell = row.createCell(++i);
					cell.setCellStyle(style2);
					cell.setCellValue("组合销售");
				}
			}
			// 设置底部统计信息
			int m = datas.size() + 2;
			row = sheet.createRow(m);
			// 设置单元格的样式格式
			int i = 0;
			cell = row.createCell(i);
			cell.setCellStyle(style2);
			cell.setCellValue("总计");

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue("本次订单金额：");

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue(all_order_price);

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue("本次商品总金额：");

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue(all_total_amount);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String excel_name = sdf.format(new Date());
			try {
				String path = request.getSession().getServletContext()
						.getRealPath("")
						+ File.separator + "excel";
				response.setContentType("application/x-download");
				response.addHeader("Content-Disposition",
						"attachment;filename=" + excel_name + ".xls");
				OutputStream os = response.getOutputStream();
				wb.write(os);
				os.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SecurityMapping(title = "延长收货时间", value = "/seller/order_comfirm_delay.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_comfirm_delay.htm")
	public ModelAndView order_comfirm_delay(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/order_comfirm_delay.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/order.htm");
		}
		return mv;

	}

	@SecurityMapping(title = "延长收货时间保存", value = "/seller/order_confirm_delay_save.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_confirm_delay_save.htm")
	public String order_confirm_delay_save(HttpServletRequest request,
			HttpServletResponse response, String id, String delay_time,
			String currentPage) throws Exception {
		OrderForm obj = this.orderFormService
				.getObjById(CommUtil.null2Long(id));
		Store store = this.storeService.getObjById(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			obj.setOrder_confirm_delay(obj.getOrder_confirm_delay()
					+ CommUtil.null2Int(delay_time));
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("延长收货时间");
			ofl.setState_info("延长收货时间：" + delay_time + "天");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(obj);
			this.orderFormLogService.save(ofl);
		}
		return "redirect:order_confirm.htm?currentPage=" + currentPage;
	}

	private ExpressCompany queryExpressCompany(String json) {
		ExpressCompany ec = null;
		if (json != null && !json.equals("")) {
			HashMap map = Json.fromJson(HashMap.class, json);
			ec = this.expressCompanyService.getObjById(CommUtil.null2Long(map
					.get("express_company_id")));
		}
		return ec;
	}

	@SecurityMapping(title = "打印快递运单", value = "/seller/order_ship_print.htm*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping("/seller/order_ship_print.htm")
	public ModelAndView order_ship_print(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/order_ship_print.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (order.getStore_id().equals(CommUtil.null2String(store.getId()))) {// 只能打印自己店铺的订单
			Map ec_map = Json.fromJson(Map.class, order.getExpress_info());
			ExpressCompanyCommon ecc = this.expressCompanyCommonService
					.getObjById(CommUtil.null2Long(ec_map
							.get("express_company_id")));
			if (ecc != null) {
				Map offset_map = Json.fromJson(Map.class,
						ecc.getEcc_template_offset());
				ShipAddress ship_addr = this.shipAddressService
						.getObjById(order.getShip_addr_id());
				mv.addObject("ecc", this.expressCompanyCommonService
						.getObjById(CommUtil.null2Long(ec_map
								.get("express_company_id"))));
				mv.addObject("offset_map", offset_map);
				mv.addObject("obj", order);
				mv.addObject("ship_addr", ship_addr);
				mv.addObject("area",
						this.areaService.getObjById(ship_addr.getSa_area_id()));
			} else {
				mv = new JModelAndView(
						"user/default/sellercenter/seller_error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "老版物流订单，无法打印！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/ecc_list.htm");
			}
		}
		return mv;
	}
}
