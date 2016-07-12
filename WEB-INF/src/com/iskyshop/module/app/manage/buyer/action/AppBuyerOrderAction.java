package com.iskyshop.module.app.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.ExpressCompany;
import com.iskyshop.foundation.domain.FreeApplyLog;
import com.iskyshop.foundation.domain.FreeGoods;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.IntegralGoods;
import com.iskyshop.foundation.domain.IntegralGoodsOrder;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.PredepositLog;
import com.iskyshop.foundation.domain.RefundLog;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StorePoint;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.virtual.TransContent;
import com.iskyshop.foundation.domain.virtual.TransInfo;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IFreeApplyLogService;
import com.iskyshop.foundation.service.IFreeGoodsService;
import com.iskyshop.foundation.service.IGoodsLogService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IIntegralGoodsOrderService;
import com.iskyshop.foundation.service.IIntegralGoodsService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IPredepositLogService;
import com.iskyshop.foundation.service.IRefundLogService;
import com.iskyshop.foundation.service.IReturnGoodsLogService;
import com.iskyshop.foundation.service.IStorePointService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.kuaidi100.pojo.TaskRequest;
import com.iskyshop.kuaidi100.pojo.TaskResponse;
import com.iskyshop.kuaidi100.post.HttpRequest;
import com.iskyshop.kuaidi100.post.JacksonHelper;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.buyer.tools.ShipTools;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.IntegralViewTools;

/**
 * 
 * <p>
 * Title: AppBuyerOrderAction.java
 * </p>
 * 
 * <p>
 * Description: 手机app用户订单管理控制器
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
 * @date 2015-1-23
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class AppBuyerOrderAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormlogService;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IStorePointService storePointService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGroupGoodsService ggService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IPayoffLogService payoffLogservice;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private IExpressCompanyService expressCompayService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IGroupInfoService groupInfoService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private OrderFormTools orderformtools;
	@Autowired
	private IIntegralGoodsService integralGoodsService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IIntegralGoodsOrderService integralGoodsOrderService;
	@Autowired
	private IFreeGoodsService freegoodsService;
	@Autowired
	private IFreeApplyLogService freeapplylogService;
	@Autowired
	private IRefundLogService refundLogService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IPayoffLogService payoffLogService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private ShipTools ShipTools;
	@Autowired
	private IReturnGoodsLogService returnGoodsLogService;

	/**
	 * 用户订单查询，order_cat：查询订单类型，0为购物订单，1为手机充值订单，
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param token
	 * @param order_cat
	 * @param beginCount
	 * @param selectCount
	 */
	@RequestMapping("/app/buyer/goods_order.htm")
	public void buyer_order(HttpServletRequest request,
			HttpServletResponse response, String user_id, String beginCount,
			String selectCount, String goods_choice_type) {
		Map map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		if (user != null) {
			String url = CommUtil.getURL(request);
			if (!"".equals(CommUtil.null2String(this.configService
					.getSysConfig().getImageWebServer()))) {
				url = this.configService.getSysConfig().getImageWebServer();
			}
			int order_cat = 0;// 购物订单
			Map params = new HashMap();
			params.put("user_id", user_id);
			params.put("order_cat", order_cat);// 购物订单
			params.put("order_main", 1);// 主订单
			List<OrderForm> orders = this.orderFormService
					.query("select obj from OrderForm obj where obj.user_id=:user_id and obj.order_cat=:order_cat and obj.order_main=:order_main order by addTime desc",
							params, CommUtil.null2Int(beginCount),
							CommUtil.null2Int(selectCount));
			List list = new ArrayList();
			for (OrderForm of : orders) {
				Map order_map = new HashMap();
				order_map.put("order_id", of.getId());// 订单id
				order_map.put("order_num", of.getOrder_id());// 订单号
				order_map.put("addTime", of.getAddTime());// 下单时间
				order_map.put("order_total_price", this.orderFormTools
						.query_order_price(of.getId().toString()));// 订单总金额
				order_map.put("ship_price", of.getShip_price());// 物流费用
				if (of.getOrder_status() == 16 || of.getOrder_status() == 20) {// 当已经付款或者货到付款，查询该订单是否可以确认收货
					order_map.put("order_status",
							this.query_order_status(of.getId()));
				} else {
					order_map.put("order_status", of.getOrder_status());
				}
				// 订单状态,0为订单取消，10为已提交待付款，16为货到付款，20为已付款待发货，30为已发货待收货，40为已收货
				// 50买家评价完毕 ,65订单不可评价，到达设定时间，系统自动关闭订单相互评价功能
				List<Map> goods_infos = orderFormTools.queryGoodsInfo(of
						.getGoods_info());
				List list_detail = new ArrayList();
				List name_list = new ArrayList();
				for (Map map_temp : goods_infos) {
					list_detail.add(url + "/"
							+ map_temp.get("goods_mainphoto_path"));
					name_list.add(map_temp.get("goods_name"));
				}
				if (of.getChild_order_detail() != null
						&& !of.getChild_order_detail().equals("")) {
					List<Map> goods_child_infos = orderFormTools
							.queryGoodsInfo(of.getChild_order_detail());
					for (Map map_temp : goods_child_infos) {
						List<Map> child_goods_map = orderFormTools
								.queryGoodsInfo(CommUtil.null2String(map_temp
										.get("order_goods_info")));
						for (Map map_temp2 : child_goods_map) {
							list_detail.add(url + "/"
									+ map_temp2.get("goods_mainphoto_path"));
							name_list.add(map_temp2.get("goods_name"));
						}
					}
				}
				order_map.put("photo_list", list_detail);
				order_map.put("name_list", name_list);
				list.add(order_map);
			}
			map.put("order_list", list);
		}
		map.put("ret", "true");
		String json = Json.toJson(map, JsonFormat.compact());
		System.out.println("json:" + json);
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

	@RequestMapping("/app/buyer/goods_order_view.htm")
	public void buyer_order_view(HttpServletRequest request,
			HttpServletResponse response, String user_id, String order_id) {
		int code = 100;// 100请求成功，-100，用户信息错误,-200订单信息错误,
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (obj != null && user != null) {
			if (obj.getUser_id().equals(user_id)) {
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
					if (obj.getPayment() != null
							&& !obj.getPayment().equals("")) {
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
				Map map_train = new HashMap();
				List trans_list = new ArrayList();
				if (obj.getExpress_info() != null
						&& !obj.getExpress_info().equals("")) {
					Map express_map = this.orderFormTools.queryCouponInfo(obj
							.getExpress_info());
					map_train.put("express_company",
							express_map.get("express_company_name"));// 物流公司信息
				}
				map_train.put("shipTime", obj.getShipTime());// 发货时间
				map_train.put("train_order_id", obj.getId());// 物流对应订单id
				map_train.put("shipCode", obj.getShipCode());// 物流单号
				trans_list.add(map_train);
				if (obj.getChild_order_detail() != null
						&& !obj.getChild_order_detail().equals("")) {
					List<Map> temp_maps = this.orderFormTools
							.queryGoodsInfo(obj.getChild_order_detail());
					for (Map map : temp_maps) {
						OrderForm of = this.orderFormService
								.getObjById(CommUtil.null2Long(map
										.get("order_id")));
						Map map_train2 = new HashMap();
						if (of.getExpress_info() != null
								&& !of.getExpress_info().equals("")) {
							Map express_map = this.orderFormTools
									.queryCouponInfo(of.getExpress_info());
							map_train2.put("express_company",
									express_map.get("express_company_name"));// 物流公司信息
						}
						map_train2.put("shipTime", of.getShipTime());// 发货时间
						map_train2.put("train_order_id", of.getId());// 物流对应订单id
						map_train2.put("shipCode", of.getShipCode());// 物流单号
						trans_list.add(map_train2);
					}
				}
				json_map.put("trans_list", trans_list);
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
					json_map.put("coupon_price",
							coupon_map.get("coupon_amount"));// 优惠券价格
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
				if (!"".equals(CommUtil.null2String(this.configService
						.getSysConfig().getImageWebServer()))) {
					url = this.configService.getSysConfig().getImageWebServer();
				}
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
					List<Map> temp_maps2 = this.orderFormTools
							.queryGoodsInfo(obj.getChild_order_detail());
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
							goods_map.put("goods_name",
									goods3.get("goods_name"));
							goods_map.put("goods_type",
									goods3.get("goods_type"));
							goods_map.put("goods_count",
									goods3.get("goods_count"));
							goods_map.put("goods_price",
									goods3.get("goods_price"));
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
				code = -200;
			}
		} else {
			code = -200;
		}
		json_map.put("code", code);
		json_map.put("ret", "true");
		String json = Json.toJson(json_map, JsonFormat.compact());
		System.out.println("json:" + json);
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

	@RequestMapping("/app/buyer/goods_order_cancel.htm")
	public void buyer_order_cancel(HttpServletRequest request,
			HttpServletResponse response, String order_id, String user_id)
			throws Exception {
		Map json_map = new HashMap();
		int code = 100;// 100取消订单成功，-100用户信息不正确，-200订单信息不正确，
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (obj != null && obj.getUser_id().equals(user.getId().toString())) {
			if (obj.getOrder_main() == 1) {
				List<Map> maps = (List<Map>) Json.fromJson(CommUtil
						.null2String(obj.getChild_order_detail()));
				if (maps != null) {
					for (Map map : maps) {
						OrderForm child_order = this.orderFormService
								.getObjById(CommUtil.null2Long(map
										.get("order_id")));
						child_order.setOrder_status(0);
						this.orderFormService.update(child_order);
					}
				}
			}
			obj.setOrder_status(0);
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("取消订单");
			ofl.setLog_user(user);
			ofl.setOf(obj);
			ofl.setState_info("手机端取消订单");
			this.orderFormlogService.save(ofl);
			Store store = this.storeService.getObjById(CommUtil.null2Long(obj
					.getStore_id()));
			Map map = new HashMap();
			if (store != null) {
				map.put("seller_id", store.getUser().getId().toString());
			}
			map.put("order_id", obj.getId().toString());
			String json = Json.toJson(map);
			if (obj.getOrder_form() == 0) {
				this.msgTools.sendEmailCharge(CommUtil.getURL(request),
						"email_toseller_order_cancel_notify", store.getUser()
								.getEmail(), json, null, CommUtil
								.null2String(store.getId()));
				this.msgTools.sendSmsCharge(CommUtil.getURL(request),
						"sms_toseller_order_cancel_notify", store.getUser()
								.getMobile(), json, null, CommUtil
								.null2String(store.getId()));
			}
		} else {
			code = -200;
		}
		json_map.put("code", code);
		json_map.put("ret", "true");
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
	 * 买家确认收货，确认收货后，订单状态值改变为40，如果是预存款支付，买家冻结预存款中同等订单账户金额自动转入商家预存款，如果开启预存款分润，
	 * 则按照分润比例，买家预存款分别进入商家及平台商的账户
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/app/buyer/goods_order_cofirm.htm")
	public void buyer_order_cofirm(HttpServletRequest request,
			HttpServletResponse response, String order_id, String user_id)
			throws Exception {
		Map json_map_large = new HashMap();
		int code = 100;// 100确认收货成功，-100用户信息错误
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (obj != null
				&& obj.getUser_id().equals(CommUtil.null2String(user.getId()))&& obj.getOrder_status() < 40) {
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
										store.getUser().getEmail(), json, null,
										obj.getStore_id());
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
				ofl.setLog_user(user);
				ofl.setOf(obj);
				this.orderFormlogService.save(ofl);
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
		} else {
			code = -100;
		}
		json_map_large.put("code", code);
		String json = Json.toJson(json_map_large, JsonFormat.compact());
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

	@RequestMapping("/app/buyer/goods_order_evaluate_query.htm")
	public void buyer_order_evaluate_query(HttpServletRequest request,
			HttpServletResponse response, String order_id, String user_id) {
		Map json_map = new HashMap();
		int code = 100;// -200用户信息错误，
		List goods_list = new ArrayList();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		List<Map> temp_maps = this.orderFormTools.queryGoodsInfo(obj
				.getGoods_info());
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig()
				.getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
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
				Map goods_map = new HashMap();
				String child_order_id = CommUtil.null2String(goods
						.get("order_id"));
				OrderForm child_order = this.orderFormService
						.getObjById(CommUtil.null2Long(child_order_id));
				List<Map> temp_maps3 = this.orderFormTools
						.queryGoodsInfo(child_order.getGoods_info());
				for (Map goods3 : temp_maps3) {
					goods_map.put("goods_id", goods3.get("goods_id"));
					goods_map.put("goods_name", goods3.get("goods_name"));
					goods_map.put("goods_type", goods3.get("goods_type"));
					goods_map.put("goods_count", goods3.get("goods_count"));
					goods_map.put("goods_price", goods3.get("goods_price"));
					goods_map.put("goods_gsp_val", goods3.get("goods_gsp_val"));
					goods_map.put("goods_mainphoto_path",
							url + "/" + goods.get("goods_mainphoto_path"));
					goods_list.add(goods_map);
				}
			}
		}
		json_map.put("goods_list", goods_list);
		json_map.put("code", code);
		json_map.put("ret", "true");
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

	@RequestMapping("/app/buyer/goods_order_evaluate.htm")
	public void buyer_order_evaluate(HttpServletRequest request,
			HttpServletResponse response, String order_id, String user_id)
			throws Exception {
		Map json_map_large = new HashMap();
		int code = 100;// -200用户信息错误，-300订单已经评价
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (obj.getOrder_status() == 40) {
			obj.setOrder_status(50);
			this.orderFormService.update(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("评价订单");
			ofl.setLog_user(user);
			ofl.setOf(obj);
			this.orderFormLogService.save(ofl);
			List<Map> json = this.orderFormTools.queryGoodsInfo(obj
					.getGoods_info());
			for (Map map : json) {
				map.put("orderForm", obj.getId());
			}
			List<Map> child_order = this.orderFormTools.queryGoodsInfo(obj
					.getChild_order_detail());
			List<Map> child_goods = new ArrayList<Map>();
			for (Map c : child_order) {
				List<Map> maps = this.orderFormTools.queryGoodsInfo(c.get(
						"order_goods_info").toString());
				for (Map cmap : maps) {
					cmap.put("orderForm", c.get("order_id"));
				}
				child_goods.addAll(maps);
			}
			if (child_goods.size() > 0) {
				json.addAll(child_goods);
			}
			for (Map map : json) {
				Evaluate eva = new Evaluate();
				Goods goods = this.goodsService.getObjById(CommUtil
						.null2Long(map.get("goods_id")));
				eva.setAddTime(new Date());
				eva.setEvaluate_goods(goods);
				eva.setGoods_num(CommUtil.null2Int(map.get("goods_count")));
				eva.setGoods_price(map.get("goods_price").toString());
				eva.setGoods_spec(map.get("goods_gsp_val").toString());
				eva.setEvaluate_info(request.getParameter("evaluate_info_"
						+ goods.getId()));
				eva.setEvaluate_buyer_val(CommUtil.null2Int(request
						.getParameter("evaluate_buyer_val" + goods.getId())));
				eva.setDescription_evaluate(BigDecimal.valueOf(CommUtil
						.null2Double(request
								.getParameter("description_evaluate"
										+ goods.getId()))));
				eva.setService_evaluate(BigDecimal.valueOf(CommUtil
						.null2Double(request.getParameter("service_evaluate"
								+ goods.getId()))));
				eva.setShip_evaluate(BigDecimal.valueOf(CommUtil
						.null2Double(request.getParameter("ship_evaluate"
								+ goods.getId()))));
				eva.setEvaluate_type("goods");
				eva.setEvaluate_user(user);
				eva.setOf(this.orderFormService.getObjById(CommUtil
						.null2Long(map.get("orderForm"))));
				eva.setReply_status(0);
				this.evaluateService.save(eva);
				Map params = new HashMap();
				if (goods.getGoods_type() == 1) {
					Store store = this.storeService.getObjById(CommUtil
							.null2Long(goods.getGoods_store().getId()));
					params.put("store_id", store.getId().toString());
					List<Evaluate> evas = this.evaluateService
							.query("select obj from Evaluate obj where obj.of.store_id=:store_id",
									params, -1, -1);
					double store_evaluate = 0;
					double description_evaluate = 0;
					double description_evaluate_total = 0;
					double service_evaluate = 0;
					double service_evaluate_total = 0;
					double ship_evaluate = 0;
					double ship_evaluate_total = 0;
					DecimalFormat df = new DecimalFormat("0.0");
					for (Evaluate eva1 : evas) {
						description_evaluate_total = description_evaluate_total
								+ CommUtil.null2Double(eva1
										.getDescription_evaluate());
						service_evaluate_total = service_evaluate_total
								+ CommUtil.null2Double(eva1
										.getService_evaluate());
						ship_evaluate_total = ship_evaluate_total
								+ CommUtil.null2Double(eva1.getShip_evaluate());
					}
					description_evaluate = CommUtil.null2Double(df
							.format(description_evaluate_total / evas.size()));
					service_evaluate = CommUtil.null2Double(df
							.format(service_evaluate_total / evas.size()));
					ship_evaluate = CommUtil.null2Double(df
							.format(ship_evaluate_total / evas.size()));
					store_evaluate = (description_evaluate + service_evaluate + ship_evaluate) / 3;
					store.setStore_credit(store.getStore_credit()
							+ eva.getEvaluate_buyer_val());
					this.storeService.update(store);
					params.clear();
					params.put("store_id", store.getId());
					List<StorePoint> sps = this.storePointService
							.query("select obj from StorePoint obj where obj.store.id=:store_id",
									params, -1, -1);
					StorePoint point = null;
					if (sps.size() > 0) {
						point = sps.get(0);
					} else {
						point = new StorePoint();
					}
					point.setAddTime(new Date());
					point.setStore(store);
					point.setDescription_evaluate(BigDecimal
							.valueOf(description_evaluate > 5 ? 5
									: description_evaluate));
					point.setService_evaluate(BigDecimal
							.valueOf(service_evaluate > 5 ? 5
									: service_evaluate));
					point.setShip_evaluate(BigDecimal
							.valueOf(ship_evaluate > 5 ? 5 : ship_evaluate));
					point.setStore_evaluate(BigDecimal
							.valueOf(store_evaluate > 5 ? 5 : store_evaluate));
					if (sps.size() > 0) {
						this.storePointService.update(point);
					} else {
						this.storePointService.save(point);
					}
				} else {
					User sp_user = this.userService.getObjById(obj
							.getEva_user_id());
					params.put("user_id", user_id);
					List<Evaluate> evas = this.evaluateService
							.query("select obj from Evaluate obj where obj.of.user_id=:user_id",
									params, -1, -1);
					double store_evaluate = 0;
					double description_evaluate = 0;
					double description_evaluate_total = 0;
					double service_evaluate = 0;
					double service_evaluate_total = 0;
					double ship_evaluate = 0;
					double ship_evaluate_total = 0;
					DecimalFormat df = new DecimalFormat("0.0");
					for (Evaluate eva1 : evas) {
						description_evaluate_total = description_evaluate_total
								+ CommUtil.null2Double(eva1
										.getDescription_evaluate());
						service_evaluate_total = service_evaluate_total
								+ CommUtil.null2Double(eva1
										.getService_evaluate());
						ship_evaluate_total = ship_evaluate_total
								+ CommUtil.null2Double(eva1.getShip_evaluate());
					}
					description_evaluate = CommUtil.null2Double(df
							.format(description_evaluate_total / evas.size()));
					service_evaluate = CommUtil.null2Double(df
							.format(service_evaluate_total / evas.size()));
					ship_evaluate = CommUtil.null2Double(df
							.format(ship_evaluate_total / evas.size()));
					store_evaluate = (description_evaluate + service_evaluate + ship_evaluate) / 3;
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
							.valueOf(description_evaluate));
					point.setService_evaluate(BigDecimal
							.valueOf(service_evaluate));
					point.setShip_evaluate(BigDecimal.valueOf(ship_evaluate));
					point.setStore_evaluate(BigDecimal.valueOf(store_evaluate));
					if (sps.size() > 0) {
						this.storePointService.update(point);
					} else {
						this.storePointService.save(point);
					}
				}
				// 增加用户积分和消费金额
				user.setIntegral(user.getIntegral()
						+ this.configService.getSysConfig().getIndentComment());
				user.setUser_goods_fee(BigDecimal.valueOf(CommUtil.add(
						user.getUser_goods_fee(), obj.getTotalPrice())));
				this.userService.update(user);
			}
			if (obj.getOrder_form() == 0) {
				Store store = this.storeService.getObjById(CommUtil
						.null2Long(obj.getStore_id()));
				Map map = new HashMap();
				map.put("seller_id", store.getUser().getId().toString());
				map.put("order_id", obj.getId().toString());
				String json2 = Json.toJson(map);
				this.msgTools.sendEmailCharge(CommUtil.getURL(request),
						"email_toseller_evaluate_ok_notify", store.getUser()
								.getEmail(), json2, null, CommUtil
								.null2String(store.getId()));
			}
		} else {
			code = -300;
		}
		json_map_large.put("code", code);
		json_map_large.put("ret", "true");
		String json = Json.toJson(json_map_large, JsonFormat.compact());
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

	private void update_goods_inventory(OrderForm order) {
		if (order.getOrder_cat() != 2) {
			orderFormTools.updateGoodsInventory(order);
		}
	}

	@RequestMapping("/app/buyer/goods_order_ship.htm")
	public void buyer_order_ship(HttpServletRequest request,
			HttpServletResponse response, String order_id) {
		Map json_map = new HashMap();
		List json_list = new ArrayList();
		int code = 100;// 100成功，-100，用户信息错误，-200，订单信息错误，-500参数错误
		OrderForm order = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		List<TransInfo> transInfo_list = new ArrayList<TransInfo>();
		TransInfo transInfo = this.ShipTools.query_Ordership_getData(order_id);
		transInfo.setExpress_company_name(this.orderFormTools.queryExInfo(
				order.getExpress_info(), "express_company_name"));
		transInfo.setExpress_ship_code(order.getShipCode());
		transInfo_list.add(transInfo);
		for (TransInfo transinfo : transInfo_list) {
			Map map = new HashMap();
			map.put("message", transinfo.getMessage());
			map.put("status", transinfo.getStatus());
			List list = new ArrayList();
			for (TransContent con : transinfo.getData()) {
				Map map_con = new HashMap();
				map_con.put("content", con.getContext());
				map_con.put("time", con.getTime());
				list.add(map_con);
			}
			map.put("content", list);
			json_list.add(map);
		}
		json_map.put("code", code);
		json_map.put("json_list", json_list);
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

	/**
	 * 生活团购商品订单列表接口
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param order_id
	 * @return
	 */
	@RequestMapping("/app/buyer/grouplife_order.htm")
	public void grouplife_order(HttpServletRequest request,
			HttpServletResponse response, String user_id, String beginCount) {
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		int begin = 0;
		if (beginCount != null) {
			begin = CommUtil.null2Int(beginCount);
		}
		Map json_map = new HashMap();
		Map params = new HashMap();
		params.put("user_id", user_id);
		params.put("order_main", 0);
		params.put("order_cat", 2);
		List<OrderForm> orders = this.orderFormService
				.query("select obj from OrderForm obj where obj.user_id=:user_id and obj.order_main=:order_main and obj.order_cat=:order_cat order by obj.addTime desc",
						params, begin, 10);
		List<Map> datas = new ArrayList<Map>();
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig()
				.getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		for (OrderForm obj : orders) {
			Map map = new HashMap();
			map.put("order_id", obj.getId());// 订单id
			map.put("order_num", obj.getOrder_id());// 订单号
			map.put("addTime", obj.getAddTime());// 下单时间
			map.put("order_total_price", this.orderFormTools
					.query_order_price(obj.getId().toString()));// 订单总金额
			map.put("ship_price", obj.getShip_price());// 物流费用
			map.put("order_status", obj.getOrder_status());// 订单状态,0为订单取消，10为已提交待付款，16为货到付款，20为已付款待发货，30为已发货待收货，40为已收货
			// 50买家评价完毕 ,65订单不可评价，到达设定时间，系统自动关闭订单相互评价功能
			map.put("payType", "未支付");// 支付方式
			if (obj.getPayment() != null && !obj.getPayment().equals("")) {
				String pay_mark = obj.getPayment().getMark();
				if (pay_mark.equals("alipay_app")) {
					map.put("payType", "手机App支付宝");
				}
				if (pay_mark.equals("alipay_wap")) {
					map.put("payType", "手机网页支付宝");
				}
				if (pay_mark.equals("alipay")) {
					map.put("payType", "支付宝");
				}
				if (pay_mark.equals("wx_app")) {
					map.put("payType", "手机App微信支付");
				}
				if (pay_mark.equals("wx_pay")) {
					map.put("payType", "微信支付");
				}
				if (pay_mark.equals("balance")) {
					map.put("payType", "预存款");
				}
				if (pay_mark.equals("payafter")) {
					map.put("payType", "货到付款");
				}
				if (pay_mark.equals("tenpay")) {
					map.put("payType", "财付通");
				}
				if (pay_mark.equals("bill")) {
					map.put("payType", "快钱支付");
				}
				if (pay_mark.equals("chinabank")) {
					map.put("payType", "网银支付");
				}
				if (pay_mark.equals("paypal")) {
					map.put("payType", "Paypal支付");
				}
			}
			Map goods_map = this.orderformtools.queryGroupInfo(obj
					.getGroup_info());
			map.put("goods_name", goods_map.get("goods_name"));
			map.put("goods_id", goods_map.get("goods_id"));
			map.put("goods_price", goods_map.get("goods_price"));
			map.put("goods_count", goods_map.get("goods_count"));
			map.put("goods_total_price", goods_map.get("goods_total_price"));
			map.put("goods_img",
					url + "/" + goods_map.get("goods_mainphoto_path"));
			datas.add(map);
		}
		json_map.put("order_list", datas);
		json_map.put("ret", "true");
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
	 * 生活团购商品订单详情接口
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param order_id
	 * @return
	 */
	@RequestMapping("/app/buyer/grouplife_order_detail.htm")
	public void grouplife_order_detail(HttpServletRequest request,
			HttpServletResponse response, String user_id, String oid) {
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		Map json_map = new HashMap();
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(oid));
		json_map.put("oid", obj.getId());
		if (obj.getPayType() != null) {
			json_map.put("paytype", obj.getPayType());
		} else {
			json_map.put("paytype", "");
		}
		json_map.put("order_id", obj.getOrder_id());
		json_map.put("order_addTime", obj.getAddTime());
		json_map.put("order_status", obj.getOrder_status());

		if (obj.getOrder_status() == 10) {
			json_map.put("order_status_msg", "未支付");
			json_map.put("order_pay_msg", "未支付");
		} else if (obj.getOrder_status() == 20) {
			String order_pay_msg = "";
			if (obj.getPayment() != null && !obj.getPayment().equals("")) {
				String pay_mark = obj.getPayment().getMark();
				if (pay_mark.equals("alipay_app")) {
					order_pay_msg = "手机App支付宝";
				}
				if (pay_mark.equals("alipay_wap")) {
					order_pay_msg = "手机网页支付宝";
				}
				if (pay_mark.equals("alipay")) {
					order_pay_msg = "支付宝";
				}
				if (pay_mark.equals("wx_app")) {
					order_pay_msg = "手机App微信支付";
				}
				if (pay_mark.equals("wx_pay")) {
					order_pay_msg = "微信支付";
				}
				if (pay_mark.equals("balance")) {
					order_pay_msg = "预存款";
				}
				if (pay_mark.equals("tenpay")) {
					order_pay_msg = "财付通";
				}
				if (pay_mark.equals("bill")) {
					order_pay_msg = "快钱支付";
				}
				if (pay_mark.equals("chinabank")) {
					order_pay_msg = "网银支付";
				}
				if (pay_mark.equals("paypal")) {
					order_pay_msg = "Paypal支付";
				}
			} else {
				if (obj.getPayType().equals("payafter")) {
					order_pay_msg = "货到付款";
				}
			}
			json_map.put("order_status_msg", "已付款");
			json_map.put("order_pay_msg", order_pay_msg);
			json_map.put("order_pay_time",
					CommUtil.formatLongDate(obj.getPayTime()));
		}
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig()
				.getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		Map goods_map = this.orderformtools.queryGroupInfo(obj.getGroup_info());
		json_map.put("goods_name", goods_map.get("goods_name"));
		json_map.put("goods_id", goods_map.get("goods_id"));
		json_map.put("goods_price", goods_map.get("goods_price"));
		json_map.put("goods_count", goods_map.get("goods_count"));
		json_map.put("goods_total_price", goods_map.get("goods_total_price"));
		json_map.put("goods_img",
				url + "/" + goods_map.get("goods_mainphoto_path"));
		List<Map> groupinfos = new ArrayList<Map>();
		Map params = new HashMap();
		params.put("user_id", CommUtil.null2Long(user_id));
		params.put("order_id", obj.getId());
		List<GroupInfo> infos = this.groupInfoService
				.query("select obj from GroupInfo obj where obj.user_id=:user_id and obj.order_id=:order_id",
						params, -1, -1);
		for (GroupInfo info : infos) {
			Map map = new HashMap();
			map.put("info_sn", info.getGroup_sn());
			map.put("info_status", info.getStatus());
			if (info.getStatus() == 0) {
				map.put("info_status_msg", "未使用");
			} else if (info.getStatus() == 1) {
				map.put("info_status_msg", "已使用");
			} else if (info.getStatus() == -1) {
				map.put("info_status_msg", "已过期");
			} else if (info.getStatus() == 3) {
				map.put("info_status_msg", "已申请退款");
			} else if (info.getStatus() == 5) {
				map.put("info_status_msg", "退款中");
			} else if (info.getStatus() == 7) {
				map.put("info_status_msg", "已退款");
			}
			map.put("info_end_time",
					CommUtil.formatLongDate(info.getLifeGoods().getEndTime()));
			groupinfos.add(map);
		}
		json_map.put("groupinfos", groupinfos);
		json_map.put("ret", "true");
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
	 * 手机端生活购商品订单取消接口
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param oid
	 */
	@RequestMapping("/app/buyer/grouplife_order_cancel.htm")
	public void grouplife_order_cancel(HttpServletRequest request,
			HttpServletResponse response, String user_id, String oid) {
		Map json_map = new HashMap();
		OrderForm of = this.orderFormService
				.getObjById(CommUtil.null2Long(oid));
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		if (of.getUser_id().equals(user_id)) {
			of.setOrder_status(0);
			this.orderFormService.update(of);
		}
		json_map.put("ret", "true");
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
	 * 用户积分商品订单列表接口
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 */
	@RequestMapping("/app/buyer/integral_order.htm")
	public void integral_order(HttpServletRequest request,
			HttpServletResponse response, String user_id, String beginCount) {
		Map json_map = new HashMap();
		Map params = new HashMap();
		params.put("igo_user_id", CommUtil.null2Long(user_id));
		List<IntegralGoodsOrder> orders = this.integralGoodsOrderService
				.query("select obj from IntegralGoodsOrder obj where obj.igo_user.id=:igo_user_id order by addTime desc",
						params, CommUtil.null2Int(beginCount), 10);
		List list = new ArrayList();
		for (IntegralGoodsOrder of : orders) {
			Map order_map = new HashMap();
			order_map.put("oid", of.getId());// 订单id
			order_map.put("order_id", of.getIgo_order_sn());// 订单编号
			order_map.put("addTime", of.getAddTime());// 下单时间
			order_map.put("order_total_price", of.getIgo_total_integral());// 订单总金额
			order_map.put("ship_price", of.getIgo_trans_fee());// 物流费用
			order_map.put("igo_status", of.getIgo_status());
			order_map.put("pay_time", of.getIgo_pay_time());
			if (of.getIgo_payment() != null && !of.getIgo_payment().equals("")) {
				String payment = of.getIgo_payment();
				String temp = "无运费订单";
				if (payment.equals("alipay")) {
					temp = "支付宝";
				}
				if (payment.equals("alipay_app")) {
					temp = "支付宝App";
				}
				if (payment.equals("wx_app")) {
					temp = "微信App";
				}
				if (payment.equals("tenpay")) {
					temp = "财付通";
				}
				if (payment.equals("bill")) {
					temp = "快钱";
				}
				if (payment.equals("chinabank")) {
					temp = "网银在线";
				}
				if (payment.equals("balance")) {
					temp = "预存款支付";
				}
				order_map.put("payType", temp);// 支付方式
			} else {
				order_map.put("payType", "未支付");// 支付方式
			}
			List<Map> goods_list = new ArrayList<Map>();
			List<Map> ig_maps = this.orderformtools.query_integral_goodsinfo(of
					.getGoods_info());
			for (Map map : ig_maps) {
				Map goods_map = new HashMap();
				goods_map.put("id", map.get("id"));
				goods_map.put("goods_name", map.get("ig_goods_name"));
				goods_map.put("goods_count", map.get("ig_goods_count"));
				goods_map.put("goods_img", map.get("ig_goods_img"));
				System.out.println("========" + map.get("ig_goods_img"));
				goods_list.add(goods_map);
			}
			order_map.put("goods_list", goods_list);
			list.add(order_map);
		}
		json_map.put("order_list", list);
		json_map.put("ret", "true");
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
	 * 用户积分商品订单详情接口
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 */
	@RequestMapping("/app/buyer/integral_order_detail.htm")
	public void integral_order_detail(HttpServletRequest request,
			HttpServletResponse response, String user_id, String oid) {
		Map json_map = new HashMap();
		IntegralGoodsOrder of = this.integralGoodsOrderService
				.getObjById(CommUtil.null2Long(oid));
		json_map.put("oid", of.getId());// 订单id
		json_map.put("order_id", of.getIgo_order_sn());// 订单编号
		json_map.put("addTime", of.getAddTime());// 下单时间
		json_map.put("order_total_price", of.getIgo_total_integral());// 订单总金额
		json_map.put("ship_price", of.getIgo_trans_fee());// 物流费用
		json_map.put("pay_time", of.getIgo_pay_time());
		if (of.getIgo_payment() != null && !of.getIgo_payment().equals("")) {
			String payment = of.getIgo_payment();
			String temp = "无运费订单";
			if (payment.equals("alipay")) {
				temp = "支付宝";
			}
			if (payment.equals("alipay_app")) {
				temp = "手机App支付宝";
			}
			if (payment.equals("wx_app")) {
				temp = "手机微信支付";
			}
			if (payment.equals("wx_pay")) {
				temp = "微信支付";
			}
			if (payment.equals("tenpay")) {
				temp = "财付通";
			}
			if (payment.equals("bill")) {
				temp = "快钱";
			}
			if (payment.equals("chinabank")) {
				temp = "网银在线";
			}
			if (payment.equals("balance")) {
				temp = "预存款支付";
			}
			json_map.put("payType", temp);// 支付方式
		} else {
			json_map.put("payType", "未支付");// 支付方式
		}
		if (of.getIgo_status() == 0) {
			json_map.put("status_msg", "已提交未付款");
		}
		if (of.getIgo_status() == 20) {
			json_map.put("status_msg", "已付款待发货");
		}
		if (of.getIgo_status() == 30) {
			json_map.put("status_msg", "已发货");
		}
		if (of.getIgo_status() == 40) {
			json_map.put("status_msg", "已收货");
		}
		if (of.getIgo_status() == -1) {
			json_map.put("status_msg", "已取消");
		}
		json_map.put("igo_status", of.getIgo_status());
		json_map.put("receiver_address",
				of.getReceiver_area() + of.getReceiver_area_info());
		json_map.put("receiver_name", of.getReceiver_Name());
		json_map.put("receiver_mobile", of.getReceiver_mobile());
		json_map.put("receiver_tel", of.getReceiver_telephone());

		if (of.getIgo_status() == 30) {
			Map map = (Map) Json.fromJson(of.getIgo_express_info());
			json_map.put("express_company_name",
					map.get("express_company_name"));
			json_map.put("shipCode", of.getIgo_ship_code());
			json_map.put("igo_ship_content", of.getIgo_ship_content());
			json_map.put("igo_ship_time",
					CommUtil.formatLongDate(of.getIgo_ship_time()));
		}
		List<Map> goods_list = new ArrayList<Map>();
		List<Map> ig_maps = this.orderformtools.query_integral_goodsinfo(of
				.getGoods_info());
		for (Map map : ig_maps) {
			Map goods_map = new HashMap();
			goods_map.put("id", map.get("id"));
			goods_map.put("goods_name", map.get("ig_goods_name"));
			goods_map.put("goods_count", map.get("ig_goods_count"));
			goods_map.put("goods_img", map.get("ig_goods_img"));
			goods_list.add(goods_map);
		}
		json_map.put("goods_list", goods_list);
		json_map.put("ret", "true");
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
	 * 用户积分商品订单确认收货
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 */
	@RequestMapping("/app/buyer/integral_order_complete.htm")
	public void integral_order_complete(HttpServletRequest request,
			HttpServletResponse response, String user_id, String oid) {
		Map json_map = new HashMap();
		IntegralGoodsOrder of = this.integralGoodsOrderService
				.getObjById(CommUtil.null2Long(oid));
		if (user_id.equals(of.getIgo_user().getId().toString())) {
			of.setIgo_status(40);
			this.integralGoodsOrderService.update(of);
			json_map.put("ret", "true");
		}
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
	 * 用户积分商品订单取消接口
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/app/buyer/integral_order_cancel.htm")
	public void integral_order_cancel(HttpServletRequest request,
			HttpServletResponse response, String user_id, String oid) {
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		IntegralGoodsOrder obj = this.integralGoodsOrderService
				.getObjById(CommUtil.null2Long(oid));
		if (obj != null && obj.getIgo_user().getId().equals(user.getId())) {
			obj.setIgo_status(-1);
			this.integralGoodsOrderService.update(obj);
			List<IntegralGoods> igs = this.orderformtools
					.query_integral_all_goods(obj.getId().toString());
			for (IntegralGoods ig : igs) {
				IntegralGoods goods = ig;
				int sale_count = this.orderformtools
						.query_integral_one_goods_count(obj, ig.getId()
								.toString());
				goods.setIg_goods_count(goods.getIg_goods_count() + sale_count);
				this.integralGoodsService.update(goods);
			}
			user.setIntegral(user.getIntegral() + obj.getIgo_total_integral());
			this.userService.update(user);
			IntegralLog log = new IntegralLog();
			log.setAddTime(new Date());
			log.setContent("取消" + obj.getIgo_order_sn() + "积分兑换，返还积分");
			log.setIntegral(obj.getIgo_total_integral());
			log.setIntegral_user(obj.getIgo_user());
			log.setType("integral_order");
			this.integralLogService.save(log);
			json_map.put("ret", "true");
		}
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
	 * 零元购商品订单列表接口
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 */
	@RequestMapping("/app/buyer/free_order.htm")
	public void free_order(HttpServletRequest request,
			HttpServletResponse response, String user_id, String beginCount) {
		Map json_map = new HashMap();
		Map params = new HashMap();
		params.put("user_id", CommUtil.null2Long(user_id));
		List<FreeApplyLog> orders = this.freeapplylogService
				.query("select obj from FreeApplyLog obj where obj.user_id=:user_id order by addTime desc",
						params, CommUtil.null2Int(beginCount), 10);
		List list = new ArrayList();
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig()
				.getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		for (FreeApplyLog of : orders) {
			Map order_map = new HashMap();
			order_map.put("oid", of.getId());// 订单id
			order_map.put("addTime", of.getAddTime());// 下单时间
			order_map.put("evaluate_status", of.getEvaluate_status());
			order_map.put("apply_status", of.getApply_status());
			FreeGoods fg = this.freegoodsService.getObjById(of
					.getFreegoods_id());
			SysConfig sc = this.configService.getSysConfig();
			String img = url + "/" + sc.getGoodsImage().getPath() + "/"
					+ sc.getGoodsImage().getName();
			if (fg.getFree_acc() != null) {
				order_map.put("goods_img", url + "/"
						+ fg.getFree_acc().getPath() + "/"
						+ fg.getFree_acc().getName());
			} else {
				order_map.put("goods_img", img);
			}
			order_map.put("goods_name", fg.getFree_name());
			list.add(order_map);
		}
		json_map.put("order_list", list);
		json_map.put("ret", "true");
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
	 * 零元购商品订单详情接口
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 */
	@RequestMapping("/app/buyer/free_order_detail.htm")
	public void free_order_detail(HttpServletRequest request,
			HttpServletResponse response, String oid) {
		Map json_map = new HashMap();
		FreeApplyLog of = this.freeapplylogService.getObjById(CommUtil
				.null2Long(oid));
		Map order_map = new HashMap();
		if (of.getExpress_info() != null && !of.getExpress_info().equals("")) {
			Map map = (Map) Json.fromJson(of.getExpress_info());
			order_map.put("express_company_name",
					map.get("express_company_name"));
			order_map.put("shipCode", of.getShipCode());
		}
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig()
				.getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		order_map.put("oid", of.getId());// 订单id
		order_map.put("addTime", of.getAddTime());// 下单时间
		order_map.put("evaluate_status", of.getEvaluate_status());
		order_map.put("use_experience", of.getUse_experience());
		order_map.put("apply_status", of.getApply_status());
		FreeGoods fg = this.freegoodsService.getObjById(of.getFreegoods_id());
		SysConfig sc = this.configService.getSysConfig();
		String img = url + "/" + sc.getGoodsImage().getPath() + "/"
				+ sc.getGoodsImage().getName();
		if (fg.getFree_acc() != null) {
			order_map.put("goods_img", url + "/" + fg.getFree_acc().getPath()
					+ "/" + fg.getFree_acc().getName());
		} else {
			order_map.put("goods_img", img);
		}
		order_map.put("goods_name", fg.getFree_name());
		order_map.put("receiver_address",
				of.getReceiver_area() + of.getReceiver_area_info());
		order_map.put("receiver_name", of.getReceiver_Name());
		order_map.put("receiver_mobile", of.getReceiver_mobile());
		order_map.put("receiver_tel", of.getReceiver_telephone());
		json_map.put("ret", "true");
		json_map.put("data", order_map);
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
	 * 0元试用订单评价保存接口
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param use_experience
	 */
	@RequestMapping("/app/buyer/free_order_evaluate_save.htm")
	public void free_order_evaluate_save(HttpServletRequest request,
			HttpServletResponse response, String oid, String use_experience,
			String user_id) {
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		Map json_map = new HashMap();
		FreeApplyLog fal = this.freeapplylogService.getObjById(CommUtil
				.null2Long(oid));
		if (fal.getUser_id().equals(CommUtil.null2Long(user.getId()))) {
			fal.setUse_experience(use_experience);
			fal.setEvaluate_time(new Date());
			fal.setEvaluate_status(1);
			this.freeapplylogService.save(fal);
			json_map.put("ret", "true");
		}
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
	 * 普通商品退货订单接口
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param token
	 * @param begin_count
	 */
	@RequestMapping("/app/buyer/goods_return.htm")
	public void goods_return(HttpServletRequest request,
			HttpServletResponse response, String user_id, String begin_count) {
		Map json_map = new HashMap();
		List<Map> datas = new ArrayList<Map>();
		int code = 100;// 100，信息正确，-100，信息不正确
		Map params = new HashMap();
		params.put("user_id", user_id);
		params.put("order_main", 1);
		params.put("order_cat", 2);
		params.put("order_status", 40);
		params.put("return_shipTime", new Date());
		int begin = 0;
		if (begin_count != null) {
			begin = CommUtil.null2Int(begin_count);
		}
		List<OrderForm> orders = this.orderFormService
				.query("select obj from OrderForm obj where obj.user_id=:user_id and obj.order_main=:order_main and obj.order_cat!=:order_cat and obj.order_status>=:order_status and obj.return_shipTime>=:return_shipTime order by obj.addTime desc",
						params, begin, 10);
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig()
				.getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		for (OrderForm obj : orders) {
			Map of_map = new HashMap();
			List<Map> goods_maps = new ArrayList<Map>();
			of_map.put("oid", obj.getId());
			of_map.put("addTime", obj.getAddTime());
			of_map.put("order_id", obj.getOrder_id());
			List<Map> goods_maps_temp = this.orderFormTools
					.queryGoodsInfo(CommUtil.null2String(obj.getGoods_info()));
			for (Map goods : goods_maps_temp) {
				Map goods_map = new HashMap();
				goods_map.put("goods_id", goods.get("goods_id"));
				goods_map.put("goods_name", goods.get("goods_name"));
				goods_map.put("goods_img",
						url + "/" + goods.get("goods_mainphoto_path"));
				goods_map.put("goods_gsp_ids", goods.get("goods_gsp_ids"));
				String return_can = "true";
				String return_mark = "申请退货";
				String goods_return_status = CommUtil.null2String(goods
						.get("goods_return_status"));
				if (CommUtil.null2Int(goods.get("goods_choice_type")) == 1) {
					return_can = "false";
					return_mark = "无法退货";
				} else {
					if (goods_return_status != null) {
						if (goods_return_status.equals("5")) {
							return_can = "false";
							return_mark = "已申请";
						} else if (goods_return_status.equals("6")) {
							return_can = "false";
							return_mark = "填写退货物流";
						} else if (goods_return_status.equals("7")) {
							return_can = "false";
							return_mark = "退货中";
						} else if (goods_return_status.equals("8")) {
							return_can = "false";
							return_mark = "退款完成";
						}
					}
				}
				goods_map.put("return_can", return_can);
				goods_map.put("return_mark", return_mark);
				goods_maps.add(goods_map);
			}
			of_map.put("goods_maps", goods_maps);
			// 如果子订单信息存在
			if (obj.getChild_order_detail() != null) {
				List<Map> child_goods_maps_temp = this.orderFormTools
						.queryGoodsInfo(CommUtil.null2String(obj
								.getChild_order_detail()));
				for (Map goods : child_goods_maps_temp) {
					Map goods_map = new HashMap();
					goods_map.put("goods_id", goods.get("goods_id"));
					goods_map.put("goods_name", goods.get("goods_name"));
					goods_map.put("goods_img",
							goods.get("goods_mainphoto_path"));
					goods_map.put("goods_gsp_ids", goods.get("goods_gsp_ids"));
					String return_can = "true";
					String return_mark = "申请退货";
					String goods_return_status = CommUtil.null2String(goods
							.get("goods_return_status"));
					if (CommUtil.null2Int(goods.get("goods_choice_type")) == 1) {
						return_can = "false";
						return_mark = "无法退货";
					} else {
						if (goods_return_status != null) {
							if (goods_return_status.equals("5")) {
								return_can = "false";
								return_mark = "已申请";
							} else if (goods_return_status.equals("6")
									|| goods_return_status.equals("7")) {
								return_can = "false";
								return_mark = "退货中";
							} else if (goods_return_status.equals("8")) {
								return_can = "false";
								return_mark = "等待平台退款";
							}
						}
					}
					goods_map.put("return_can", return_can);
					goods_map.put("return_mark", return_mark);
					goods_maps.add(goods_map);
				}
				of_map.put("goods_maps", goods_maps);
			}
			datas.add(of_map);
		}
		json_map.put("code", code);
		json_map.put("datas", datas);
		json_map.put("ret", "true");
		String json = Json.toJson(json_map, JsonFormat.compact());
		System.out.println("json_map:" + json_map);
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
	 * 普通商品退货申请接口
	 * 
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param oid
	 * @param goods_gsp_ids
	 */
	@RequestMapping("/app/buyer/goods_return_apply.htm")
	public void goods_return_apply(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String oid,
			String goods_gsp_ids, String user_id) {
		Map json_map = new HashMap();
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(oid));
		if (obj.getUser_id().equals(user_id)) {
			List<Map> maps = this.orderFormTools.queryGoodsInfo(obj
					.getGoods_info());
			Goods goods = this.goodsService.getObjById(CommUtil
					.null2Long(goods_id));
			Map obj_map = new HashMap();
			for (Map m : maps) {
				if (CommUtil.null2String(m.get("goods_id")).equals(goods_id)) {
					obj_map = m;
					break;
				}
			}
			json_map.put(
					"return_count",
					obj_map.get("return_goods_count") == null ? 1 : obj_map
							.get("return_goods_count"));
			json_map.put("oid", oid);
			json_map.put("goods_id", goods.getId());
			if (CommUtil.null2String(obj_map.get("goods_return_status"))
					.equals("5")) {
				json_map.put("view", true);
				List<Map> return_maps = this.orderFormTools.queryGoodsInfo(obj
						.getReturn_goods_info());
				Map return_map = new HashMap();
				for (Map map : return_maps) {
					if (CommUtil.null2String(map.get("return_goods_id"))
							.equals(goods_id)) {
						return_map = map;
						break;
					}
				}
				json_map.put("return_goods_content",
						return_map.get("return_goods_content"));
			}
		}
		json_map.put("goods_gsp_ids", goods_gsp_ids);
		json_map.put("ret", "ret");
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
	 * 普通商品退货申请保存接口
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param return_goods_content
	 * @param goods_id
	 * @param return_goods_count
	 * @param goods_gsp_ids
	 * @param user_id
	 * @param token
	 * @throws Exception
	 */
	@RequestMapping("/app/buyer/goods_return_apply_save.htm")
	public void goods_return_apply_save(HttpServletRequest request,
			HttpServletResponse response, String oid,
			String return_goods_content, String goods_id,
			String return_goods_count, String goods_gsp_ids, String user_id)
			throws Exception {
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(oid));
		List<Goods> goods_list = this.orderFormTools.queryOfGoods(obj.getId()
				.toString());
		Goods goods = null;
		for (Goods g : goods_list) {
			if (g.getId().toString().equals(goods_id)) {
				goods = g;
				break;
			}
		}
		if (obj != null && obj.getUser_id().equals(user_id) && goods != null) {
			List<Map> list = new ArrayList<Map>();
			Map json = new HashMap();
			json.put("return_goods_id", goods.getId());
			json.put("return_goods_content",
					CommUtil.filterHTML(return_goods_content));
			json.put("return_goods_count", return_goods_count);
			json.put("return_goods_price", goods.getStore_price());
			json.put("return_goods_commission_rate", goods.getGc()
					.getCommission_rate());
			json.put("return_order_id", oid);
			list.add(json);
			obj.setReturn_goods_info(Json.toJson(list, JsonFormat.compact()));
			List<Map> maps = this.orderFormTools.queryGoodsInfo(obj
					.getGoods_info());
			List<Map> new_maps = new ArrayList<Map>();
			Map gls = new HashMap();
			for (Map m : maps) {
				if (m.get("goods_id").toString().equals(goods_id)
						&&m.get("goods_gsp_ids")!=null&& goods_gsp_ids.equals(m.get("goods_gsp_ids")
								.toString())) {
					m.put("goods_return_status", 5);
					gls.putAll(m);
				}
				new_maps.add(m);
			}
			obj.setGoods_info(Json.toJson(new_maps));
			this.orderFormService.update(obj);
			ReturnGoodsLog rlog = new ReturnGoodsLog();
			rlog.setReturn_service_id("re" + user.getId()
					+ CommUtil.formatTime("yyyyMMddHHmmss", new Date()));
			rlog.setUser_name(user.getUserName());
			rlog.setUser_id(user.getId());
			rlog.setReturn_content(CommUtil.filterHTML(return_goods_content));
			rlog.setGoods_all_price(gls.get("goods_all_price")==null?null:gls.get("goods_all_price").toString());
			rlog.setGoods_count(gls.get("goods_count").toString());
			rlog.setGoods_id(CommUtil.null2Long(gls.get("goods_id").toString()));
			rlog.setGoods_mainphoto_path(gls.get("goods_mainphoto_path")
					.toString());
			rlog.setGoods_commission_rate(BigDecimal.valueOf(CommUtil
					.null2Double(gls.get("goods_commission_rate"))));
			rlog.setGoods_name(gls.get("goods_name").toString());
			rlog.setGoods_price(gls.get("goods_price").toString());
			rlog.setGoods_return_status("5");
			rlog.setAddTime(new Date());
			rlog.setReturn_order_id(CommUtil.null2Long(oid));
			rlog.setGoods_type(goods.getGoods_type());
			if (goods.getGoods_store() != null) {
				rlog.setStore_id(goods.getGoods_store().getId());
			}
			this.returnGoodsLogService.save(rlog);
			// 如果是收费接口，则通知快递100，建立订单物流查询推送
			if (this.configService.getSysConfig().getKuaidi_type() == 1) {
				TaskRequest req = new TaskRequest();
				Map express_map = Json.fromJson(Map.class,
						obj.getExpress_info());
				req.setCompany(CommUtil.null2String(express_map
						.get("express_company_mark")));
				String from_addr = "";
				req.setFrom(from_addr);
				req.setTo(obj.getReceiver_area());
				req.setNumber(obj.getShipCode());
				req.getParameters().put(
						"callbackurl",
						CommUtil.getURL(request)
								+ "/kuaidi100_callback.htm?order_id="
								+ obj.getId() + "&orderType=1");
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
						System.out.println("订阅成功");
					} else {
						System.out.println("订阅失败");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (obj.getOrder_form() == 0) {
				User seller = this.userService.getObjById(this.storeService
						.getObjById(CommUtil.null2Long(obj.getStore_id()))
						.getUser().getId());
				Map map = new HashMap();
				map.put("buyer_id", user.getId().toString());
				map.put("seller_id", seller.getId().toString());
				String map_json = Json.toJson(map);
				this.msgTools.sendEmailCharge(CommUtil.getURL(request),
						"email_toseller_order_return_apply_notify",
						seller.getEmail(), map_json, null, obj.getStore_id());
				this.msgTools.sendSmsCharge(CommUtil.getURL(request),
						"sms_toseller_order_return_apply_notify",
						seller.getMobile(), map_json, null, obj.getStore_id());
			}
		}
		json_map.put("ret", "true");
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
	 * 普通商品取消退货申请接口
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param goods_id
	 * @param goods_gsp_ids
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/app/buyer/goods_return_cancel_save.htm")
	public void goods_return_cancel_save(HttpServletRequest request,
			HttpServletResponse response, String oid, String goods_id,
			String goods_gsp_ids, String user_id) throws Exception {
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(oid));
		List<Goods> goods_list = this.orderFormTools.queryOfGoods(obj.getId()
				.toString());
		Goods goods = null;
		for (Goods g : goods_list) {
			if (g.getId().toString().equals(goods_id)) {
				goods = g;
				break;
			}
		}
		if (obj != null && obj.getUser_id().equals(user.getId().toString())
				&& goods != null) {
			obj.setReturn_goods_info("");
			List<Map> maps = this.orderFormTools.queryGoodsInfo(obj
					.getGoods_info());
			List<Map> new_maps = new ArrayList<Map>();
			Map gls = new HashMap();
			for (Map m : maps) {
				if (m.get("goods_gsp_ids") != null) {
					if (m.get("goods_id").toString().equals(goods_id)
							&& goods_gsp_ids.equals(m.get("goods_gsp_ids")
									.toString())) {
						m.put("goods_return_status", "");
						gls.putAll(m);
						new_maps.add(m);
						break;
					}
				} else {
					if (m.get("goods_id").toString().equals(goods_id)) {
						m.put("goods_return_status", "");
						gls.putAll(m);
						new_maps.add(m);
						break;
					}
				}
			}
			if (new_maps.size() > 0) {
				obj.setGoods_info(Json.toJson(new_maps));
				this.orderFormService.update(obj);
			}
			Map map = new HashMap();
			map.put("goods_id",
					CommUtil.null2Long(gls.get("goods_id").toString()));
			map.put("return_order_id", CommUtil.null2Long(oid));
			map.put("uid", user.getId());
			List<ReturnGoodsLog> objs = this.returnGoodsLogService
					.query("select obj from ReturnGoodsLog obj where obj.goods_id=:goods_id and obj.return_order_id=:return_order_id and obj.user_id=:uid",
							map, -1, -1);
			for (ReturnGoodsLog rl : objs) {
				this.returnGoodsLogService.delete(rl.getId());
			}
		}
		json_map.put("ret", "true");
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
	 * 普通商品退货物流信息接口
	 * 
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param oid
	 * @param goods_gsp_ids
	 */
	@RequestMapping("/app/buyer/goods_return_ship.htm")
	public void goods_return_ship(HttpServletRequest request,
			HttpServletResponse response, String oid, String goods_id,
			String user_id) {
		Map json_map = new HashMap();
		List<Map> datas = new ArrayList<Map>();
		OrderForm of = this.orderFormService
				.getObjById(CommUtil.null2Long(oid));
		Map params = new HashMap();
		ReturnGoodsLog obj = null;
		params.put("oid", CommUtil.null2Long(oid));
		params.put("goods_id", CommUtil.null2Long(goods_id));
		params.put("user_id", CommUtil.null2Long(user_id));
		List<ReturnGoodsLog> rgls = this.returnGoodsLogService
				.query("select obj from ReturnGoodsLog obj where obj.goods_id=:goods_id and obj.return_order_id=:oid and obj.user_id=:user_id",
						params, 0, 1);
		if (rgls.size() > 0) {
			obj = rgls.get(0);
			json_map.put("rid", obj.getId());
			json_map.put("return_content", obj.getReturn_content());
			json_map.put("self_address", obj.getSelf_address());
		}
		if (obj.getUser_id().toString().equals(user_id)) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (obj.getUser_id().equals(user.getId())) {
				if (obj.getGoods_return_status().equals("6")
						|| obj.getGoods_return_status().equals("7")) {
					params.clear();
					params.put("status", 0);
					List<ExpressCompany> expressCompanys = this.expressCompayService
							.query("select obj from ExpressCompany obj where obj.company_status=:status order by company_sequence asc",
									params, -1, -1);
					List<Map> express_list = new ArrayList<Map>();
					for (ExpressCompany ex : expressCompanys) {
						Map ex_map = new HashMap();
						ex_map.put("express_id", ex.getId());
						ex_map.put("express_name", ex.getCompany_name());
						express_list.add(ex_map);
					}
					json_map.put("express_list", express_list);
				}
			}
		}
		json_map.put("ret", "true");
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
	 * 普通商品退货物流信息填写保存接口
	 * 
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param oid
	 * @param goods_gsp_ids
	 */
	@RequestMapping("/app/buyer/goods_return_ship_save.htm")
	public void goods_return_ship_save(HttpServletRequest request,
			HttpServletResponse response, String rid, String user_id,
			String express_id, String express_code) {
		Map json_map = new HashMap();
		ReturnGoodsLog obj = this.returnGoodsLogService.getObjById(CommUtil
				.null2Long(rid));
		if (obj.getUser_id().toString().equals(user_id)) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (obj != null && obj.getUser_id().equals(user.getId())) {
				ExpressCompany ec = this.expressCompayService
						.getObjById(CommUtil.null2Long(express_id));
				Map json_map2 = new HashMap();
				json_map2.put("express_company_id", ec.getId());
				json_map2.put("express_company_name", ec.getCompany_name());
				json_map2.put("express_company_mark", ec.getCompany_mark());
				json_map2.put("express_company_type", ec.getCompany_type());
				String express_json = Json.toJson(json_map2);
				obj.setReturn_express_info(express_json);
				obj.setExpress_code(express_code);
				obj.setGoods_return_status("7");
				this.returnGoodsLogService.update(obj);
				OrderForm return_of = this.orderFormService.getObjById(obj
						.getReturn_order_id());
				List<Map> maps = this.orderFormTools.queryGoodsInfo(return_of
						.getGoods_info());
				List<Map> new_maps = new ArrayList<Map>();
				Map gls = new HashMap();
				for (Map m : maps) {
					if (m.get("goods_id").toString()
							.equals(CommUtil.null2String(obj.getGoods_id()))) {
						m.put("goods_return_status", 7);
						gls.putAll(m);
					}
					new_maps.add(m);
				}
				return_of.setGoods_info(Json.toJson(new_maps));
				this.orderFormService.update(return_of);
			}
		}
		json_map.put("ret", "true");
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
	 * 手机端生活购商品退货订单接口
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param token
	 * @param begin_count
	 */
	@RequestMapping("/app/buyer/grouplife_refund.htm")
	public void grouplife_refund(HttpServletRequest request,
			HttpServletResponse response, String user_id, String begin_count) {
		Map json_map = new HashMap();
		List<Map> datas = new ArrayList<Map>();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		Map params = new HashMap();
		params.put("user_id", CommUtil.null2Long(user_id));
		params.put("status1", 1);
		params.put("status2", -1);
		params.put("refund_Time", new Date());
		int begin = 0;
		if (begin_count != null) {
			begin = CommUtil.null2Int(begin_count);
		}
		List<GroupInfo> infos = this.groupInfoService
				.query("select obj from GroupInfo obj where obj.user_id=:user_id and obj.status!=:status1 and obj.status!=:status2 and obj.refund_Time>=:refund_Time order by obj.addTime desc",
						params, begin, 10);
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig()
				.getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		for (GroupInfo obj : infos) {
			Map data = new HashMap();
			data.put("group_id", obj.getId());
			data.put("group_addTime", obj.getAddTime());
			data.put("group_sn", obj.getGroup_sn());
			data.put("group_goods_name", obj.getLifeGoods().getGg_name());
			data.put("group_goods_price", obj.getLifeGoods().getGroup_price());
			data.put("group_goods_img", url + "/"
					+ obj.getLifeGoods().getGroup_acc().getPath() + "/"
					+ obj.getLifeGoods().getGroup_acc().getName());
			data.put("group_status", obj.getStatus());
			String refund_msg = "申请退款";
			if (obj.getStatus() == 1) {
				refund_msg = "已使用";
			}
			if (obj.getStatus() == -1) {
				refund_msg = "已过期";
			}
			if (obj.getStatus() == 3) {
				refund_msg = "已申请退款";
			}
			if (obj.getStatus() == 5) {
				refund_msg = "退款中";
			}
			if (obj.getStatus() == 7) {
				refund_msg = "退款完成";
			}
			data.put("refund_msg", refund_msg);
			datas.add(data);
		}
		json_map.put("datas", datas);
		json_map.put("ret", "true");
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
	 * 手机端生活购商品退货订单申请接口
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/app/buyer/grouplife_refund_apply.htm")
	public void grouplife_refund_apply(HttpServletRequest request,
			HttpServletResponse response, String group_id) {
		Map json_map = new HashMap();
		json_map.put("group_id", group_id);
		json_map.put("ret", "true");
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
	 * 手机端生活购商品退货订单申请保存接口 生活购商品退款，系统自动完成，无需商家审核，将退款金额存入用户预存款中
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/app/buyer/grouplife_refund_apply_save.htm")
	public void grouplife_refund_apply_save(HttpServletRequest request,
			HttpServletResponse response, String group_id, String user_id,
			String return_content, String return_reasion) throws Exception {
		Map json_map = new HashMap();
		GroupInfo obj = this.groupInfoService.getObjById(CommUtil
				.null2Long(group_id));
		if (obj != null && obj.getUser_id() == CommUtil.null2Long(user_id)) {
			obj.setStatus(7);// 退款完成
			obj.setRefund_reasion(return_reasion + "[" + return_content + "]");// 退款说明
			this.groupInfoService.update(obj);
			OrderForm order = this.orderFormService.getObjById(obj
					.getOrder_id());
			if (order.getOrder_form() == 0) {
				User seller = this.userService.getObjById(this.storeService
						.getObjById(CommUtil.null2Long(order.getStore_id()))
						.getUser().getId());
				Map map = new HashMap();
				map.put("buyer_id", obj.getUser_id().toString());
				map.put("seller_id", seller.getId().toString());
				String map_json = Json.toJson(map);
				this.msgTools.sendEmailCharge(CommUtil.getURL(request),
						"email_toseller_order_refund_apply_notify",
						seller.getEmail(), map_json, null, order.getStore_id());
				this.msgTools
						.sendSmsCharge(CommUtil.getURL(request),
								"sms_toseller_order_refund_apply_notify",
								seller.getMobile(), map_json, null,
								order.getStore_id());
			}
			this.groupinfo_refund_complete(group_id);
			json_map.put("ret", "true");
		} else {
			json_map.put("ret", "false");
		}
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

	// 生活购商品退款，系统自动完成，无需商家审核，将退款金额存入用户预存款中
	private void groupinfo_refund_complete(String group_id) {
		GroupInfo obj = this.groupInfoService.getObjById(CommUtil
				.null2Long(group_id));
		GroupLifeGoods gfg = obj.getLifeGoods();
		BigDecimal amount = gfg.getGroup_price();
		User user = this.userService.getObjById(CommUtil.null2Long(obj
				.getUser_id()));
		user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
				user.getAvailableBalance(), amount)));
		this.userService.update(user);
		// 保存充值日志
		String info = "<" + obj.getLifeGoods().getGg_name() + ">消费码"
				+ obj.getGroup_sn() + "退款成功";
		PredepositLog log = new PredepositLog();
		Map params = new HashMap();
		params.put("userRole", "ADMIN");
		List<User> admins = this.userService.query(
				"select obj from User obj where obj.userRole=:userRole",
				params, 0, 1);
		log.setPd_log_admin(admins.get(0));
		log.setAddTime(new Date());
		log.setPd_log_amount(BigDecimal.valueOf(CommUtil.null2Double(amount)));
		log.setPd_log_info(info);
		log.setPd_log_user(user);
		log.setPd_op_type("系统自动退款");
		log.setPd_type("可用预存款");
		this.predepositLogService.save(log);
		OrderForm of = this.orderFormService.getObjById(obj.getOrder_id());
		if (of.getOrder_form() == 0) {// 商家订单生成退款结算账单
			Store store = this.storeService.getObjById(CommUtil.null2Long(of
					.getStore_id()));
			PayoffLog pol = new PayoffLog();
			pol.setPl_sn("pl"
					+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
					+ store.getUser().getId());
			pol.setAddTime(new Date());
			pol.setGoods_info(of.getReturn_goods_info());
			pol.setRefund_user_id(obj.getUser_id());
			pol.setSeller(store.getUser());
			pol.setRefund_userName(obj.getUser_name());
			pol.setPayoff_type(-1);
			pol.setPl_info("退款完成");
			BigDecimal price = BigDecimal.valueOf(CommUtil.null2Double(amount)); // 商品的原价
			BigDecimal final_money = BigDecimal.valueOf(CommUtil.subtract(0,
					price));
			pol.setTotal_amount(final_money);
			// 将订单中group_info（{}）转换为List<Map>([{}])
			List<Map> Map_list = new ArrayList<Map>();
			Map group_map = this.orderFormTools.queryGroupInfo(of
					.getGroup_info());
			Map_list.add(group_map);
			pol.setReturn_goods_info(Json.toJson(Map_list, JsonFormat.compact()));
			pol.setO_id(of.getId().toString());
			pol.setOrder_id(of.getOrder_id());
			pol.setCommission_amount(BigDecimal.valueOf(0));
			pol.setOrder_total_price(final_money);
			this.payoffLogService.save(pol);
			// 生成退款日志
			RefundLog r_log = new RefundLog();
			r_log.setAddTime(new Date());
			r_log.setRefund_id(CommUtil
					.formatTime("yyyyMMddHHmmss", new Date()) + user.getId());
			r_log.setReturnLog_id(obj.getId());
			r_log.setReturnService_id(obj.getGroup_sn());
			r_log.setRefund(BigDecimal.valueOf(CommUtil.null2Double(amount)));
			r_log.setRefund_log(info);
			r_log.setRefund_type("预存款");
			r_log.setRefund_user(user);
			r_log.setReturnLog_userName(obj.getUser_name());
			r_log.setReturnLog_userId(obj.getUser_id());
			this.refundLogService.save(r_log);
			store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.subtract(
					store.getStore_sale_amount(), amount)));// 减少店铺本次结算总销售金额
			store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil.subtract(
					store.getStore_payoff_amount(), price)));// 减少店铺本次结算总金额
			this.storeService.update(store);
			// 减少系统总销售金额、总结算金额
			SysConfig sc = this.configService.getSysConfig();
			sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.subtract(
					sc.getPayoff_all_sale(), amount)));
			sc.setPayoff_all_amount(BigDecimal.valueOf(CommUtil.subtract(
					sc.getPayoff_all_amount(), CommUtil.mul(amount, 0))));
			sc.setPayoff_all_amount_reality(BigDecimal.valueOf(CommUtil.add(
					pol.getReality_amount(), sc.getPayoff_all_amount_reality())));// 增加系统实际总结算
			this.configService.update(sc);
			String msg_content = "您的团购商品：" + gfg.getGg_name()
					+ "消费码已经成功退款，退款金额为：" + amount + "，退款消费码:"
					+ obj.getGroup_sn() + ",已存入您的账户预存款";
			// 发送系统站内信
			Message msg = new Message();
			msg.setAddTime(new Date());
			msg.setStatus(0);
			msg.setType(0);
			msg.setContent(msg_content);
			msg.setFromUser(admins.get(0));
			msg.setToUser(user);
			this.messageService.save(msg);
		}

	}

	private Payment get_payment(String mark) {
		Map params = new HashMap();
		Set marks = new TreeSet();
		Payment payment = null;
		// 在线支付
		if (mark.equals("online")) {
			mark = "alipay_app";
		}
		marks.add(mark);
		params.put("marks", marks);
		params.put("install", true);
		List<Payment> payments = this.paymentService
				.query("select obj from Payment obj where obj.mark in(:marks) and obj.install=:install",
						params, -1, -1);
		if (payments.size() > 0) {
			payment = payments.get(0);
		}
		return payment;
	}
}
