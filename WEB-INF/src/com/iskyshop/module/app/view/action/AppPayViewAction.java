package com.iskyshop.module.app.view.action;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsLog;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.IntegralGoods;
import com.iskyshop.foundation.domain.IntegralGoodsOrder;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.PredepositLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.Template;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IGoodsLogService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IIntegralGoodsOrderService;
import com.iskyshop.foundation.service.IIntegralGoodsService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IPredepositLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.admin.tools.PaymentTools;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.msg.SpelTemplate;
import com.iskyshop.pay.tenpay.RequestHandler;
import com.iskyshop.pay.tenpay.util.Sha1Util;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.IntegralViewTools;

/**
 * 
 * <p>
 * Title: MobileCartViewAction.java
 * </p>
 * 
 * <p>
 * Description: 手机端订单支付接口控制器
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
 * @date 2014-7-28
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class AppPayViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private OrderFormTools orderTools;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IIntegralGoodsOrderService iorderService;
	@Autowired
	private IGroupInfoService groupInfoService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private IPayoffLogService payoffLogService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsLogService goodsLogService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IIntegralGoodsOrderService integralGoodsOrderService;
	@Autowired
	private IIntegralGoodsService integralGoodsService;
	@Autowired
	private IActivityGoodsService actgoodsService;
	@Autowired
	private IntegralViewTools integralViewTools;

	/**
	 * 订单货到付款支付
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/app/buyer/pay_payafter.htm")
	public void pay_payafter(HttpServletRequest request,
			HttpServletResponse response, String order_id, String pay_msg,
			String user_id) throws Exception {
		Map json_map = new HashMap();
		int code = 100;// 100成功，-100用户信息错误,-200订单信息错误，-300订单支付方式信息错误,-400系统未开启该支付功能，订单不可支付，
		boolean order_verify = false;// 订单信息验证
		OrderForm order = null;
		User user = null;
		order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
		if (order != null && order.getUser_id().equals(user_id)) {
			order_verify = true;
		} else {
			code = -200;
		}
		if (code == 100) {// 订单支付方式信息验证通过
			if (order.getOrder_status() != 16) {
				// 验证订单中商品库存
				boolean inventory_very = true;
				List<Goods> goods_list = this.orderFormTools
						.queryOfGoods(order_id);
				for (Goods obj : goods_list) {
					int order_goods_count = this.orderFormTools
							.queryOfGoodsCount(order_id,
									CommUtil.null2String(obj.getId()));
					String order_goods_gsp_ids = "";
					List<Map> goods_maps = this.orderFormTools
							.queryGoodsInfo(order.getGoods_info());
					for (Map obj_map : goods_maps) {
						if (CommUtil.null2String(obj_map.get("goods_id"))
								.equals(obj.getId().toString())) {
							order_goods_gsp_ids = CommUtil.null2String(obj_map
									.get("goods_gsp_ids"));
							break;
						}
					}
					// 真实商品库存
					int real_goods_count = CommUtil.null2Int(this
							.generic_default_info(obj, order_goods_gsp_ids,
									user_id).get("count"));// 计算商品库存信息
					if (order_goods_count > real_goods_count) {
						inventory_very = false;
						break;
					}
				}
				if (inventory_very) {
					order.setPay_msg(pay_msg);
					order.setPayTime(new Date());
					order.setPayType("payafter");
					order.setOrder_status(16);// 订单货到付款
					this.orderFormService.update(order);
					// 记录支付日志
					OrderFormLog ofl = new OrderFormLog();
					ofl.setAddTime(new Date());
					ofl.setLog_info("提交货到付款申请");
					ofl.setLog_user(user);
					ofl.setOf(order);
					this.orderFormLogService.save(ofl);
					if (order.getOrder_main() == 1
							&& !CommUtil.null2String(
									order.getChild_order_detail()).equals("")) {
						List<Map> maps = this.orderFormTools
								.queryGoodsInfo(order.getChild_order_detail());
						for (Map child_map : maps) {
							OrderForm child_order = this.orderFormService
									.getObjById(CommUtil.null2Long(child_map
											.get("order_id")));
							child_order.setOrder_status(16);
							child_order.setPay_msg(pay_msg);
							order.setPayType("payafter");
							child_order.setPayTime(new Date());
							this.orderFormService.update(child_order);
							// 记录支付日志
							OrderFormLog child_ofl = new OrderFormLog();
							child_ofl.setAddTime(new Date());
							child_ofl.setLog_info("提交货到付款申请");
							child_ofl.setLog_user(user);
							child_ofl.setOf(order);
							this.orderFormLogService.save(child_ofl);
							// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
							Store store = this.storeService.getObjById(CommUtil
									.null2Long(child_order.getStore_id()));
							if (child_order.getOrder_form() == 0) {
								this.msgTools.sendSmsCharge(CommUtil
										.getURL(request),
										"sms_toseller_payafter_pay_ok_notify",
										store.getUser().getMobile(), null,
										CommUtil.null2String(child_order
												.getId()), child_order
												.getStore_id());
								this.msgTools
										.sendEmailCharge(
												CommUtil.getURL(request),
												"email_toseller_payafter_pay_ok_notify",
												store.getUser().getEmail(),
												null,
												CommUtil.null2String(child_order
														.getId()), child_order
														.getStore_id());
							}
						}
					}
					if (order.getOrder_cat() != 2) {
						orderFormTools.updateGoodsInventory(order);// 如果该订单为货到付款订单，下订单同时更新主订单（子订单）库存信息，货到付款可认定为支付完成，等待发货。
					}
				} else {
					code = -200;
				}
			}
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

	@RequestMapping("/app/buyer/integral_order_pay_balance.htm")
	public void integral_order_pay_balance(HttpServletRequest request,
			HttpServletResponse response, String order_id, String user_id) {
		int code = 0;
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		IntegralGoodsOrder order = this.integralGoodsOrderService
				.getObjById(CommUtil.null2Long(order_id));
		if (order.getIgo_status() == 0) {
			if (order.getIgo_user().getId() == user.getId()) {
				if (CommUtil.null2Double(user.getAvailableBalance()) >= CommUtil
						.null2Double(order.getIgo_trans_fee())) {
					order.setIgo_status(20);
					order.setIgo_payment("balance");
					order.setIgo_pay_time(new Date());
					boolean ret = this.integralGoodsOrderService.update(order);
					if (ret) {
						user.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.subtract(user.getAvailableBalance(),
										order.getIgo_trans_fee())));
						this.userService.update(user);
						// 执行库存减少
						List<Map> ig_maps = this.orderFormTools
								.query_integral_goodsinfo(order.getGoods_info());
						for (Map map : ig_maps) {
							IntegralGoods goods = this.integralGoodsService
									.getObjById(CommUtil.null2Long(map
											.get("id")));
							goods.setIg_goods_count(goods.getIg_goods_count()
									- CommUtil.null2Int(map
											.get("ig_goods_count")));
							goods.setIg_exchange_count(goods
									.getIg_exchange_count()
									+ CommUtil.null2Int(map
											.get("ig_goods_count")));
							this.integralGoodsService.update(goods);
						}
					}
					// 记录预存款日志
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_user(user);
					log.setPd_log_amount(order.getIgo_trans_fee());
					log.setPd_op_type("消费");
					log.setPd_type("可用预存款");
					log.setPd_log_info("订单" + order.getIgo_order_sn()
							+ "兑换礼品减少可用预存款");
					this.predepositLogService.save(log);
					code = 100;// 成功
				} else {
					code = -100;// 可用余额不足，支付失败
				}
			}
		} else {
			code = -300;// 已支付
		}
		Map map = new HashMap();
		map.put("code", code);
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
	 * 订单预存款支付
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/app/buyer/pay_balance.htm")
	public void pay_balance(HttpServletRequest request,
			HttpServletResponse response, String order_id, String pay_msg,
			String user_id, String type) throws Exception {
		Map json_map = new HashMap();
		int code = 100;// 100成功，-100用户信息错误,-200订单信息错误，-300订单支付方式信息错误,-400预存款余额不足，-500订单重复支付
		OrderForm order = null;
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		order = this.orderFormService.getObjById(CommUtil.null2Long(order_id));
		if (!order.getUser_id().equals(user_id)) {
			code = -200;
		}
		if (code == 100) {// 订单支付方式信息验证通过
			if (order != null && order.getOrder_status() < 20) {// 订单不为空且订单状态为未付款才可以正常使用预存款付款
				Payment payment = this.getPaymentbyMark("balance");
				double order_total_price = CommUtil.null2Double(order
						.getTotalPrice());
				if (!CommUtil.null2String(order.getChild_order_detail())
						.equals("") && order.getOrder_cat() != 2) {
					order_total_price = this.orderFormTools
							.query_order_price(CommUtil.null2String(order
									.getId()));
				}
				if (CommUtil.null2Double(user.getAvailableBalance()) >= order_total_price) {
					// 验证订单中商品库存
					boolean inventory_very = true;
					List<Goods> temp_goods_list = this.orderFormTools
							.queryOfGoods(order_id);
					for (Goods obj : temp_goods_list) {
						int order_goods_count = this.orderFormTools
								.queryOfGoodsCount(order_id,
										CommUtil.null2String(obj.getId()));
						String order_goods_gsp_ids = "";
						List<Map> goods_maps = this.orderFormTools
								.queryGoodsInfo(order.getGoods_info());
						for (Map obj_map : goods_maps) {
							if (CommUtil.null2String(obj_map.get("goods_id"))
									.equals(obj.getId().toString())) {
								order_goods_gsp_ids = CommUtil.null2String(obj_map
										.get("goods_gsp_ids"));
								break;
							}
						}
						// 真实商品库存
						int real_goods_count = CommUtil.null2Int(this
								.generic_default_info(obj, order_goods_gsp_ids,
										user_id).get("count"));// 计算商品库存信息
						if (order_goods_count > real_goods_count) {
							inventory_very = false;
							break;
						}
					}
					if (inventory_very) {
						order.setPay_msg(pay_msg);
						order.setOrder_status(20);
						order.setPayment(payment);
						order.setPayTime(new Date());
						boolean ret = this.orderFormService.update(order);
						// 主订单记录支付日志
						OrderFormLog ofl = new OrderFormLog();
						ofl.setAddTime(new Date());
						ofl.setLog_info("预付款支付");
						ofl.setLog_user(user);
						ofl.setOf(order);
						this.orderFormLogService.save(ofl);
						if (ret) {
							// 预存款付款成功后，执行子订单状态改变及发送提醒信息
							if (order.getOrder_main() == 1
									&& !CommUtil.null2String(
											order.getChild_order_detail())
											.equals("")
									&& order.getOrder_cat() != 2) {
								List<Map> maps = this.orderFormTools
										.queryGoodsInfo(order
												.getChild_order_detail());
								for (Map child_map : maps) {
									OrderForm child_order = this.orderFormService
											.getObjById(CommUtil
													.null2Long(child_map
															.get("order_id")));
									child_order.setOrder_status(20);
									child_order.setPayment(payment);
									child_order.setPayTime(new Date());
									this.orderFormService.update(child_order);
									// 子订单记录支付日志
									OrderFormLog child_ofl = new OrderFormLog();
									child_ofl.setAddTime(new Date());
									child_ofl.setLog_info("预付款支付");
									child_ofl.setLog_user(user);
									child_ofl.setOf(child_order);
									this.orderFormLogService.save(child_ofl);
									// 向加盟商家发送付款成功短信提示，自营商品无需发送短信提示
									Store store = this.storeService
											.getObjById(CommUtil
													.null2Long(child_order
															.getStore_id()));
									if (child_order.getOrder_form() == 0) {// 使用收费邮件短信接口
										this.msgTools
												.sendEmailCharge(
														CommUtil.getURL(request),
														"email_toseller_balance_pay_ok_notify",
														store.getUser()
																.getEmail(),
														null,
														CommUtil.null2String(child_order
																.getId()),
														child_order
																.getStore_id());
										this.msgTools
												.sendEmailCharge(
														CommUtil.getURL(request),
														"email_tobuyer_balance_pay_ok_notify",
														user.getEmail(),
														null,
														CommUtil.null2String(child_order
																.getId()),
														child_order
																.getStore_id());
										this.msgTools
												.sendSmsCharge(
														CommUtil.getURL(request),
														"sms_toseller_balance_pay_ok_notify",
														store.getUser()
																.getMobile(),
														null,
														CommUtil.null2String(child_order
																.getId()),
														child_order
																.getStore_id());
										this.msgTools
												.sendSmsCharge(
														CommUtil.getURL(request),
														"sms_tobuyer_balance_pay_ok_notify",
														user.getMobile(),
														null,
														CommUtil.null2String(child_order
																.getId()),
														child_order
																.getStore_id());
									} else {// 使用免费短信接口
										this.msgTools
												.sendEmailFree(
														CommUtil.getURL(request),
														"email_tobuyer_balance_pay_ok_notify",
														user.getEmail(),
														null,
														CommUtil.null2String(child_order
																.getId()));
										this.msgTools
												.sendSmsFree(
														CommUtil.getURL(request),
														"sms_tobuyer_balance_pay_ok_notify",
														user.getMobile(),
														null,
														CommUtil.null2String(child_order
																.getId()));
									}
								}
							}
							// 如果是团购订单，则需要执行团购订单相关流程及发送团购码
							if (order.getOrder_cat() == 2) {
								Calendar ca = Calendar.getInstance();
								ca.add(ca.DATE, this.configService
										.getSysConfig().getAuto_order_return());
								SimpleDateFormat bartDateFormat = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm:ss");
								String latertime = bartDateFormat.format(ca
										.getTime());
								SimpleDateFormat sdf = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm:ss");
								Date date = sdf.parse(latertime);
								order.setReturn_shipTime(date);
								Map map = this.orderFormTools
										.queryGroupInfo(order.getGroup_info());
								int count = CommUtil.null2Int(map.get(
										"goods_count").toString());
								String goods_id = map.get("goods_id")
										.toString();
								GroupLifeGoods goods = this.groupLifeGoodsService
										.getObjById(CommUtil
												.null2Long(goods_id));
								goods.setGroup_count(goods.getGroup_count()
										- CommUtil.null2Int(count));
								this.groupLifeGoodsService.update(goods);
								int i = 0;
								List<String> code_list = new ArrayList();// 存放团购消费码
								String codes = "";
								while (i < count) {
									GroupInfo info = new GroupInfo();
									info.setAddTime(new Date());
									info.setLifeGoods(goods);
									info.setPayment(payment);
									info.setUser_id(user.getId());
									info.setUser_name(user.getUserName());
									info.setOrder_id(order.getId());
									info.setGroup_sn(user.getId()
											+ CommUtil.formatTime(
													"yyyyMMddHHmmss" + i,
													new Date()));
									Calendar ca2 = Calendar.getInstance();
									ca2.add(ca2.DATE, this.configService
											.getSysConfig()
											.getGrouplife_order_return());
									SimpleDateFormat bartDateFormat2 = new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss");
									String latertime2 = bartDateFormat2
											.format(ca2.getTime());
									info.setRefund_Time(CommUtil
											.formatDate(latertime2));
									this.groupInfoService.save(info);
									codes = codes + info.getGroup_sn() + " ";
									code_list.add(info.getGroup_sn());
									i++;
								}
								if (order.getOrder_form() == 0) {
									Store store = this.storeService
											.getObjById(CommUtil
													.null2Long(order
															.getStore_id()));
									PayoffLog plog = new PayoffLog();
									plog.setPl_sn("pl"
											+ CommUtil.formatTime(
													"yyyyMMddHHmmss",
													new Date())
											+ store.getUser().getId());
									plog.setPl_info("团购码生成成功");
									plog.setAddTime(new Date());
									plog.setSeller(store.getUser());
									plog.setO_id(CommUtil.null2String(order
											.getId()));
									plog.setOrder_id(order.getOrder_id()
											.toString());
									plog.setCommission_amount(BigDecimal
											.valueOf(CommUtil
													.null2Double("0.00")));// 该订单总佣金费用
									// 将订单中group_info（{}）转换为List<Map>([{}])
									List<Map> Map_list = new ArrayList<Map>();
									Map group_map = this.orderFormTools
											.queryGroupInfo(order
													.getGroup_info());
									Map_list.add(group_map);
									plog.setGoods_info(Json.toJson(Map_list,
											JsonFormat.compact()));
									plog.setOrder_total_price(order
											.getTotalPrice());// 该订单总商品金额
									plog.setTotal_amount(BigDecimal.valueOf(CommUtil
											.add(CommUtil.subtract(order
													.getGoods_amount(), order
													.getCommission_amount()),
													order.getShip_price())));// 该订单应结算金额：结算金额=订单总商品金额-总佣金费用+运费
									this.payoffLogService.save(plog);
									store.setStore_sale_amount(BigDecimal
											.valueOf(CommUtil.add(order
													.getTotalPrice(), store
													.getStore_sale_amount())));// 店铺本次结算总销售金额
									// 团购消费码，没有佣金，店铺总佣金不变
									store.setStore_payoff_amount(BigDecimal
											.valueOf(CommUtil.add(order
													.getTotalPrice(), store
													.getStore_payoff_amount())));// 店铺本次结算总佣金
									this.storeService.update(store);
								}
								// 增加系统总销售金额、消费码没有佣金，系统总佣金不变
								SysConfig sc = this.configService
										.getSysConfig();
								sc.setPayoff_all_sale(BigDecimal
										.valueOf(CommUtil.add(
												order.getTotalPrice(),
												sc.getPayoff_all_sale())));
								this.configService.update(sc);
								// 更新lucene索引
								String goods_lucene_path = System
										.getProperty("iskyshopb2b2c.root")
										+ File.separator
										+ "luence"
										+ File.separator + "grouplifegoods";
								File file = new File(goods_lucene_path);
								if (!file.exists()) {
									CommUtil.createFolder(goods_lucene_path);
								}
								LuceneUtil lucene = LuceneUtil.instance();
								lucene.setIndex_path(goods_lucene_path);
								lucene.update(CommUtil.null2String(goods
										.getId()), luceneVoTools
										.updateLifeGoodsIndex(goods));
								String msg_content = "恭喜您成功购买团购"
										+ map.get("goods_name") + ",团购消费码分别为："
										+ codes + "您可以到用户中心-我的生活购中查看消费码的使用情况";
								// 发送系统站内信给买家
								Message tobuyer_msg = new Message();
								tobuyer_msg.setAddTime(new Date());
								tobuyer_msg.setStatus(0);
								tobuyer_msg.setType(0);
								tobuyer_msg.setContent(msg_content);
								tobuyer_msg.setFromUser(this.userService
										.getObjByProperty(null, "userName",
												"admin"));
								tobuyer_msg.setToUser(user);
								this.messageService.save(tobuyer_msg);
								// 付款成功，发送短信团购消费码
								if (this.configService.getSysConfig()
										.isSmsEnbale()) {
									this.send_groupInfo_sms(
											request,
											order,
											user.getMobile(),
											"sms_tobuyer_online_ok_send_groupinfo",
											code_list, user.getId().toString(),
											goods.getUser().getId().toString());
								}
							}
							user.setAvailableBalance(BigDecimal
									.valueOf(CommUtil.subtract(
											user.getAvailableBalance(),
											order_total_price)));
							this.userService.update(user);
							PredepositLog log = new PredepositLog();
							log.setAddTime(new Date());
							log.setPd_log_user(user);
							log.setPd_op_type("消费");
							log.setPd_log_amount(BigDecimal.valueOf(-CommUtil
									.null2Double(order_total_price)));
							log.setPd_log_info(order.getOrder_id()
									+ "订单购物减少可用预存款");
							log.setPd_type("可用预存款");
							this.predepositLogService.save(log);
							// 执行库存减少,如果是团购商品，团购库存同步减少
							// 执行库存减少,如果是团购商品，团购库存同步减少
							if (order.getOrder_cat() != 2) {
								orderFormTools.updateGoodsInventory(order);
							}
						}
					} else {
						code = -200;// 订单中商品信息错误，库存不足
					}
				} else {
					code = -400;// 预存款余额不足
				}
			} else {
				code = -500;// 订单已经支付
			}
		} else {
			code = -300;// 订单支付方式信息错误
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
	 * 订单订单预存款支付时登录密码验证
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/app/buyer/pay_balance_verify.htm")
	public void pay_balance_verify(HttpServletRequest request,
			HttpServletResponse response, String password, String user_id,
			String token) {
		boolean verify = true;
		Map json_map = new HashMap();
		List map_list1 = new ArrayList();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		String temp_password = this.encodeStr(password);
		if (user.getMobile_pay_password().equals(temp_password)) {
			verify = true;
		} else {
			verify = false;
		}
		json_map.put("verify", verify);
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
	 * 订单详情及列表支付时查询所有在线支付方式,包括支付宝app,微信支付、预存款支付
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param token
	 * @param order_id
	 */
	@RequestMapping("/app/query_all_payment_online.htm")
	public void query_all_payment_online(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token) {
		boolean verify = true;
		Map json_map = new HashMap();
		if (verify && user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
				if (user.getApp_login_token().equals(token.toLowerCase())) {
					String marks = "alipay_app,wx_app,balance";
					String temp_mark[] = marks.split(",");
					String str_payment = "";
					List<Map> datas = new ArrayList<Map>();
					for (String mark : temp_mark) {
						if (!mark.equals("")) {
							Payment online = this.getPaymentbyMark(mark);
							if (online != null) {
								Map pay_map = new HashMap();
								pay_map.put("pay_mark", online.getMark());
								if (online.getName() == null) {
									if (online.getMark().equals("alipay_app")) {
										pay_map.put("pay_name", "手机App支付宝");
									}
									if (online.getMark().equals("wx_app")) {
										pay_map.put("pay_name", "微信App支付");
									}
									if (online.getMark().equals("balance")) {
										pay_map.put("pay_name", "预存款支付");
									}
								} else {
									pay_map.put("pay_name", online.getName());
								}
								datas.add(pay_map);
							}
						}
					}
					if (datas.size() > 0) {
						json_map.put("can_pay", true);
					}
					json_map.put("datas", datas);
				} else {
					verify = false;
				}
			} else {
				verify = false;
			}
		} else {
			verify = false;
		}
		json_map.put("ret", CommUtil.null2String(verify));
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

	/**
	 * 手机端订单支付单个查询在线支付方式接口信息（APP支付宝、wx_app）
	 * mark:支付标志，alipay_app为app支付宝，wx_app为微信支付,并同时查询出该支付方式是否可使用
	 * 
	 */
	@RequestMapping("/app/query_payment_online.htm")
	public void query_payment_online(HttpServletRequest request,
			HttpServletResponse response, String mark) {
		Map json_map = new HashMap();
		Payment online = getPaymentbyMark(mark);
		if (online == null) {
			json_map.put("install", false);
		} else {
			json_map.put("install", true);
			if (mark.equals("alipay_app")) {
				json_map.put("seller", online.getSeller_email());
				json_map.put("partner", online.getPartner());
				json_map.put("private", online.getApp_private_key());
				json_map.put("public", online.getApp_public_key());
				json_map.put("safekey", online.getSafeKey());
			}
			if (mark.equals("wx_app")) {

			}
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
	 * 手机端商品下订单查询货到付款
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param token
	 * @param order_id
	 */
	@RequestMapping("/app/query_payment_payafter.htm")
	public void query_payment_payafter(HttpServletRequest request,
			HttpServletResponse response, String goods_ids) {
		boolean goods_cod = true;
		String ids[] = goods_ids.split(",");
		for (String id : ids) {
			Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
			if (obj != null) {
				if (obj.getGoods_cod() == -1) {// 只要存在一件不允许使用货到付款购买的商品整个订单就不允许使用货到付款
					goods_cod = false;
					break;
				}
			}
		}
		Map json_map = new HashMap();
		json_map.put("can_pay", goods_cod);
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
	 * app微信支付 获取微信支付签名等信息
	 * 
	 * @param id 订单的id
	 * @param type 订单的类型 如果是积分商品订单传 integral goods group
	 * @return json 包含 是否成功 success fail  签名相关参数
	 */
	@RequestMapping("/app/pay/wx_pay.htm")
	public void wx_pay(HttpServletRequest request,
			HttpServletResponse response, String id, String type)
			throws Exception {
		Map json = new HashMap();
		RequestHandler reqHandler = new RequestHandler(request, response);
		// 给订单添加支付方式 ,
		List<Payment> payments = new ArrayList<Payment>();
		Map params = new HashMap();
		params.put("mark", "wx_app");
		payments = this.paymentService.query(
				"select obj from Payment obj where obj.mark=:mark", params, -1,
				-1);
		Payment payment = null;
		if (payments.size() > 0) {
			payment = payments.get(0);
		}
		if ("integral".equals(type)) {
			IntegralGoodsOrder of = this.integralGoodsOrderService
					.getObjById(CommUtil.null2Long(id));
			if (payment != null && of != null) {
				String app_id = payment.getWx_appid();
				String app_key = payment.getWx_paySignKey();
				String partner = payment.getTenpay_partner();
				String noncestr = Sha1Util.getNonceStr();
				String timestamp = Sha1Util.getTimeStamp();
				double total_fee = Double.valueOf(of.getIgo_trans_fee()
						.toString()) * 100;
				int order_price = (int) total_fee;
				String path = request.getContextPath();
				String basePath = request.getScheme() + "://"
						+ request.getServerName() + path + "/";
				reqHandler.setParameter("appid", app_id);
				reqHandler.setParameter("mch_id", partner);
				reqHandler.setParameter("nonce_str", noncestr);
				reqHandler.setParameter("body", of.getIgo_order_sn());
				reqHandler.setParameter("attach",
						of.getId() + "_" + of.getIgo_order_sn() + "_"
								+ of.getIgo_user().getId() + "_" + type);
				reqHandler.setParameter("out_trade_no", of.getIgo_order_sn());
				reqHandler.setParameter("total_fee", order_price + "");
				reqHandler.setParameter("spbill_create_ip",
						CommUtil.getIpAddr(request));
				reqHandler.setParameter("notify_url", basePath
						+ "app/wx_pay_return.htm");
				reqHandler.setParameter("trade_type", "APP");
				String requestUrl = reqHandler.reqToXml(app_key);
				HttpURLConnection conn = this.creatConnection(requestUrl);
				String result = this.getInput(conn);
				Map<String, String> map = this.doXMLParse(result);
				String return_code = map.get("return_code").toString();
				String prepay_id = "";
				if ("SUCCESS".equals(return_code)) {
					String result_code = map.get("result_code").toString();
					if ("SUCCESS".equals(result_code)) {
						prepay_id = map.get("prepay_id");
					}
				}
				if(prepay_id!=null&&!"".equals(prepay_id)){
					reqHandler.getAllParameters().clear();
					reqHandler.setParameter("appid", app_id);
					reqHandler.setParameter("noncestr", noncestr);
					reqHandler.setParameter("package", "Sign=WXPay");
					reqHandler.setParameter("partnerid", partner);
					reqHandler.setParameter("prepayid",prepay_id);
					reqHandler.setParameter("timestamp", timestamp);
					reqHandler.genSign(app_key);
					json.put("msg", "success");
					json.put("appId", app_id);
					json.put("partnerId", partner);
					json.put("timeStamp", timestamp);
					json.put("packageValue", "Sign=WXPay");
					json.put("nonceStr", noncestr);
					json.put("prepayId", prepay_id);
					json.put("sign", reqHandler.getParameter("sign"));
				}else{
					json.put("msg", "fail");
				}
			} else {
				json.put("msg", "fail");
			}
		} else {
			OrderForm of = this.orderFormService.getObjById(CommUtil
					.null2Long(id));
			if (payment != null && of != null) {
				String app_id = payment.getWx_appid();
				String app_key = payment.getWx_paySignKey();
				String partner = payment.getTenpay_partner();
				String noncestr = Sha1Util.getNonceStr();
				String timestamp = Sha1Util.getTimeStamp();
				double order_total_price = CommUtil.null2Double(of
						.getTotalPrice());
				if (!CommUtil.null2String(of.getChild_order_detail()).equals("")
						&& of.getOrder_cat() != 2) {
					order_total_price = this.orderFormTools
							.query_order_price(CommUtil.null2String(of.getId()));
				}
				double total_fee = Double
						.valueOf(order_total_price) * 100;
				int order_price = (int) total_fee;
				String path = request.getContextPath();
				String basePath = request.getScheme() + "://"
						+ request.getServerName() + path + "/";
				reqHandler.setParameter("appid", app_id);
				reqHandler.setParameter("mch_id", partner);
				reqHandler.setParameter("nonce_str", noncestr);
				reqHandler.setParameter("body", of.getOrder_id());
				reqHandler.setParameter(
						"attach",
						of.getId() + "_" + of.getOrder_id() + "_"
								+ of.getUser_id() + "_" + type);
				reqHandler.setParameter("out_trade_no", of.getOrder_id());
				reqHandler.setParameter("total_fee", order_price + "");
				reqHandler.setParameter("spbill_create_ip",
						CommUtil.getIpAddr(request));
				reqHandler.setParameter("notify_url", basePath
						+ "app/wx_pay_return.htm");
				reqHandler.setParameter("trade_type", "APP");
				String requestUrl = reqHandler.reqToXml(app_key);
				HttpURLConnection conn = this.creatConnection(requestUrl);
				String result = this.getInput(conn);
				System.out.println("result:"+result);
				Map<String, String> map = this.doXMLParse(result);
				String return_code = map.get("return_code").toString();
				String prepay_id = "";
				if ("SUCCESS".equals(return_code)) {
					String result_code = map.get("result_code").toString();
					if ("SUCCESS".equals(result_code)) {
						prepay_id = map.get("prepay_id");
					}
				}
				if(prepay_id!=null&&!"".equals(prepay_id)){
					reqHandler.getAllParameters().clear();
					reqHandler.setParameter("appid", app_id);
					reqHandler.setParameter("noncestr", noncestr);
					reqHandler.setParameter("package", "Sign=WXPay");
					reqHandler.setParameter("partnerid", partner);
					reqHandler.setParameter("prepayid",prepay_id);
					reqHandler.setParameter("timestamp", timestamp);
					reqHandler.genSign(app_key);
					json.put("msg", "success");
					json.put("appId", app_id);
					json.put("partnerId", partner);
					json.put("timeStamp", timestamp);
					json.put("packageValue", "Sign=WXPay");
					json.put("nonceStr", noncestr);
					json.put("prepayId", prepay_id);
					json.put("sign", reqHandler.getParameter("sign"));
				}else{
					json.put("msg", "fail");
				}
			} else {
				json.put("msg", "fail");
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(json, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Payment getPaymentbyMark(String mark) {
		Map params = new HashMap();
		Set marks = new TreeSet();
		Payment payment = null;
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

	/**
	 * 订单支付完成
	 * 
	 * @param request
	 * @param response
	 * @param payType
	 * @param order_id
	 * @return
	 */
	@RequestMapping("/app/pay_finish.htm")
	public ModelAndView pay_finish(HttpServletRequest request,
			HttpServletResponse response, String order_id, String order_type,
			String type, String user_id, String token) {
		ModelAndView mv = new JModelAndView("app/pay_finish.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		boolean user_verify = false;
		if (user_id != null && token != null && order_id != null) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
				if (user.getApp_login_token().equals(token.toLowerCase())) {
					user_verify = true;
				}
			}
		}
		if (user_verify) {
			if ("integral".equals(order_type)) {
				IntegralGoodsOrder order = this.integralGoodsOrderService
						.getObjById(CommUtil.null2Long(order_id));
				if (order != null) {
					mv.addObject("order_type", order_type);
					mv.addObject("obj", order);
				}
			} else {
				OrderForm order = this.orderFormService.getObjById(CommUtil
						.null2Long(order_id));
				if (order != null) {
					double order_total_price = this.orderFormTools
							.query_order_price(order_id);

					mv.addObject("obj", order);
					mv.addObject("order_total_price", order_total_price);
				}
			}
		} else {
			mv = new JModelAndView("app/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "访问地址不存在");
		}
		mv.addObject("order_type", order_type);
		mv.addObject("type", type);
		return mv;
	}

	private void send_groupInfo_sms(HttpServletRequest request,
			OrderForm order, String mobile, String mark, List<String> codes,
			String buyer_id, String seller_id) throws Exception {
		Template template = this.templateService.getObjByProperty(null, "mark",
				mark);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < codes.size(); i++) {
			sb.append(codes.get(i) + ",");
		}
		String code = sb.toString();
		if (template != null && template.isOpen()) {
			ExpressionParser exp = new SpelExpressionParser();
			EvaluationContext context = new StandardEvaluationContext();
			context.setVariable("buyer",
					this.userService.getObjById(CommUtil.null2Long(buyer_id)));
			context.setVariable("seller",
					this.userService.getObjById(CommUtil.null2Long(seller_id)));
			context.setVariable("config", this.configService.getSysConfig());
			context.setVariable("send_time",
					CommUtil.formatLongDate(new Date()));
			context.setVariable("webPath", CommUtil.getURL(request));
			context.setVariable("order", order);
			Map map = Json.fromJson(Map.class, order.getGroup_info());
			context.setVariable("group_info", map.get("goods_name"));
			context.setVariable("code", code);
			Expression ex = exp.parseExpression(template.getContent(),
					new SpelTemplate());
			String content = ex.getValue(context, String.class);
			this.msgTools.sendSMS(mobile, content);
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
	 * 根据商品及传递的规格信息，计算该规格商品的价格、库存量
	 * 
	 * @param goods
	 * @param gsp
	 * @return 价格、库存组成的Map
	 */
	private Map generic_default_info(Goods goods, String gsp, String user_id) {
		User user = null;
		if (user_id != null && !user_id.equals("")) {
			user = this.userService.getObjById(CommUtil.null2Long(user_id));
		}
		double price = 0;
		Map map = new HashMap();
		int count = 0;
		if (goods.getGroup() != null && goods.getGroup_buy() == 2) {// 团购商品统一按照团购价格处理
			for (GroupGoods gg : goods.getGroup_goods_list()) {
				if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
					count = gg.getGg_count();
					price = CommUtil.null2Double(gg.getGg_price());
				}
			}
		} else {
			count = goods.getGoods_inventory();
			price = CommUtil.null2Double(goods.getStore_price());
			if ("spec".equals(goods.getInventory_type())) {
				if (gsp != null) {
					List<HashMap> list = Json.fromJson(ArrayList.class,
							goods.getGoods_inventory_detail());
					String[] gsp_ids = gsp.split(",");
					for (Map temp : list) {
						String[] temp_ids = CommUtil
								.null2String(temp.get("id")).split("_");
						Arrays.sort(gsp_ids);
						Arrays.sort(temp_ids);
						if (Arrays.equals(gsp_ids, temp_ids)) {
							count = CommUtil.null2Int(temp.get("count"));
							price = CommUtil.null2Double(temp.get("price"));
						}
					}
				}
			}
		}
		BigDecimal ac_rebate = null;
		if (goods.getActivity_status() == 2 && user != null) {// 如果是促销商品，并且用户已登录，根据规格配置价格计算相应配置的促销价格
			ActivityGoods actGoods = this.actgoodsService.getObjById(goods
					.getActivity_goods_id());
			// 0—铜牌会员1—银牌会员2—金牌会员3—超级会员
			BigDecimal rebate = BigDecimal.valueOf(0.00);
			int level = this.integralViewTools.query_user_level(CommUtil
					.null2String(user.getId()));
			if (level == 0) {
				rebate = actGoods.getAct().getAc_rebate();
			} else if (level == 1) {
				rebate = actGoods.getAct().getAc_rebate1();
			} else if (level == 2) {
				rebate = actGoods.getAct().getAc_rebate2();
			} else if (level == 3) {
				rebate = actGoods.getAct().getAc_rebate3();
			}
			price = CommUtil.mul(rebate, price);
		}
		map.put("price", price);
		map.put("count", count);
		return map;
	}
	
	/**
	 * 创建链接
	 * 
	 * @param requestUrl
	 *            提交的String类型 xml
	 * @throws IOException
	 */
	private HttpURLConnection creatConnection(String requestUrl)
			throws IOException {
		URL url = new URL("https://api.mch.weixin.qq.com/pay/unifiedorder");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(30000); // 设置连接主机超时（单位：毫秒)
		conn.setReadTimeout(30000); // 设置从主机读取数据超时（单位：毫秒)
		conn.setDoOutput(true); // post请求参数要放在http正文内，顾设置成true，默认是false
		conn.setDoInput(true); // 设置是否从httpUrlConnection读入，默认情况下是true
		conn.setUseCaches(false); // Post 请求不能使用缓存
		// 设定传送的内容类型是可序列化的java对象(如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		conn.setRequestMethod("POST");// 设定请求的方法为"POST"，默认是GET
		conn.setRequestProperty("Content-Length", requestUrl.length() + "");
		String encode = "utf-8";
		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(),
				encode);
		out.write(requestUrl.toString());
		out.flush();
		out.close();
		return conn;
	}

	/**
	 * 获取返回内容
	 * 
	 * @param conn
	 *            链接对象
	 * @throws IOException
	 */
	private String getInput(HttpURLConnection conn) throws IOException {
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			return null;
		}
		// 获取响应内容体
		BufferedReader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream(), "UTF-8"));
		String line = "";
		StringBuffer strBuf = new StringBuffer();
		while ((line = in.readLine()) != null) {
			strBuf.append(line).append("\n");
		}
		in.close();
		return strBuf.toString().trim();
	}

	/**
	 * 
	 * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
	 * 
	 * @param strxml
	 * 
	 * @return
	 * 
	 * @throws JDOMException
	 * 
	 * @throws IOException
	 */

	public Map doXMLParse(String strxml) throws JDOMException, IOException {

		strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");

		if (null == strxml || "".equals(strxml)) {

			return null;

		}

		Map m = new HashMap();

		InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));

		SAXBuilder builder = new SAXBuilder();

		org.jdom.Document doc = builder.build(in);

		Element root = doc.getRootElement();

		List list = root.getChildren();

		Iterator it = list.iterator();

		while (it.hasNext()) {

			Element e = (Element) it.next();

			String k = e.getName();

			String v = "";

			List children = e.getChildren();

			if (children.isEmpty()) {

				v = e.getTextNormalize();

			} else {

				v = this.getChildrenText(children);

			}

			m.put(k, v);

		}

		// 关闭流

		in.close();

		return m;

	}

	/**
	 * 
	 * 获取子结点的xml
	 * 
	 * @param children
	 * 
	 * @return String
	 */

	public String getChildrenText(List children) {
		StringBuffer sb = new StringBuffer();
		if (!children.isEmpty()) {
			Iterator it = children.iterator();
			while (it.hasNext()) {
				Element e = (Element) it.next();
				String name = e.getName();
				String value = e.getTextNormalize();
				List list = e.getChildren();
				sb.append("<" + name + ">");
				if (!list.isEmpty()) {
					sb.append(this.getChildrenText(list));
				}
				sb.append(value);
				sb.append("</" + name + ">");
			}
		}
		return sb.toString();

	}

}
