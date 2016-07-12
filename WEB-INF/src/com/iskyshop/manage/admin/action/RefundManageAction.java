package com.iskyshop.manage.admin.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.PredepositLog;
import com.iskyshop.foundation.domain.RefundLog;
import com.iskyshop.foundation.domain.ReturnGoodsLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.GroupInfoQueryObject;
import com.iskyshop.foundation.domain.query.RefundLogQueryObject;
import com.iskyshop.foundation.domain.query.ReturnGoodsLogQueryObject;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IPredepositLogService;
import com.iskyshop.foundation.service.IPredepositService;
import com.iskyshop.foundation.service.IRefundLogService;
import com.iskyshop.foundation.service.IReturnGoodsLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.OrderFormTools;

/**
 * 
 * 
 * <p>
 * Title: RefundManageAction.java
 * </p>
 * 
 * <p>
 * Description: 平台向买家进行退款，退款统一给买家发放预存款，买家通过预存款兑换现金
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
 * @author jinxinzhe
 * 
 * @date 2014年5月14日
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class RefundManageAction {
	@Autowired
	private IReturnGoodsLogService returngoodslogService;
	@Autowired
	private IExpressCompanyService expressCompayService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IPredepositService predepositService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IPredepositLogService predepositLogService;
	@Autowired
	private IReturnGoodsLogService returnGoodsLogService;
	@Autowired
	private IRefundLogService refundLogService;
	@Autowired
	private IPayoffLogService payoffLogService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGroupInfoService groupinfoService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IMessageService messageService;

	/**
	 * refund_list列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商品退款列表", value = "/admin/refund_list.htm*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping("/admin/refund_list.htm")
	public ModelAndView refund_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String name, String user_name,
			String refund_status) {
		ModelAndView mv = new JModelAndView("admin/blue/refund_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ReturnGoodsLogQueryObject qo = new ReturnGoodsLogQueryObject(
				currentPage, mv, orderBy, orderType);
			qo.addQuery("obj.goods_return_status", new SysMap("goods_return_status",
					CommUtil.null2String(10)), "=");
			qo.addQuery("obj.goods_return_status", new SysMap("goods_return_status1",
					CommUtil.null2String(11)), "=","or");

		
		if (user_name != null && !user_name.equals("")) {
			qo.addQuery("obj.user_name", new SysMap("user_name", user_name),
					"=");
		}
		if (name != null && !name.equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%" + name
					+ "%"), "like");
		}
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, ReturnGoodsLog.class, mv);
		IPageList pList = this.returngoodslogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("name", name);
		mv.addObject("user_name", user_name);
		return mv;
	}

	/**
	 * refund_list列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "消费码退款列表", value = "/admin/groupinfo_refund_list.htm*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping("/admin/groupinfo_refund_list.htm")
	public ModelAndView groupinfo_refund_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String group_sn, String user_name,
			String refund_status) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/groupinfo_refund_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GroupInfoQueryObject qo = new GroupInfoQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.status", new SysMap("status", 5), "=");
		if (group_sn != null && !group_sn.equals("")) {
			qo.addQuery("obj.group_sn", new SysMap("group_sn", group_sn), "=");
			mv.addObject("group_sn", group_sn);
		}
		if (user_name != null && !user_name.equals("")) {
			qo.addQuery("obj.user_name", new SysMap("user_name", user_name),
					"=");
			mv.addObject("user_name", user_name);
		}
		if (refund_status != null && !refund_status.equals("")) {
			qo.addQuery("obj.status",
					new SysMap("status", CommUtil.null2Int(refund_status)), "=");
			mv.addObject("refund_status", refund_status);
		}
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, GroupInfo.class, mv);
		IPageList pList = this.groupinfoService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	/**
	 * refund_view查看
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "查看退款", value = "/admin/refund_view.htm*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping("/admin/refund_view.htm")
	public ModelAndView refund_view(HttpServletRequest request,
			HttpServletResponse response, String id, String type) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/refund_predeposit_modify.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (type != null && !type.equals("")) {
			if (type.equals("groupinfo")) {// 消费码退款
				mv.addObject("type", type);
				GroupInfo gi = this.groupinfoService.getObjById(CommUtil
						.null2Long(id));
				User user = this.userService.getObjById(gi.getUser_id());
				mv.addObject("refund_money", gi.getLifeGoods().getGroup_price());
				mv.addObject("user", user);
				mv.addObject("gi", gi);
				mv.addObject(
						"msg",
						gi.getLifeGoods().getGg_name() + "消费码"
								+ gi.getGroup_sn() + "退款成功，预存款"
								+ gi.getLifeGoods().getGroup_price()
								+ "元已存入您的账户");
			}
		} else {// 商品退款
			ReturnGoodsLog obj = this.returngoodslogService.getObjById(CommUtil
					.null2Long(id));
			OrderForm of = this.orderFormService.getObjById(obj
					.getReturn_order_id());
			double temp_refund_money = 0.0;
			if (of.getCoupon_info() != null && !of.getCoupon_info().equals("")) {
				Map map = this.orderFormTools.queryCouponInfo(of
						.getCoupon_info());
				BigDecimal rate = new BigDecimal(map.get("coupon_goods_rate")
						.toString());
				BigDecimal old_price = new BigDecimal(obj.getGoods_all_price());
				BigDecimal coupon_price=new BigDecimal(map.get("coupon_amount").toString());
				BigDecimal price=new BigDecimal(map.get("price").toString());
				double refund_money = CommUtil.mul(1-(CommUtil.null2Double(coupon_price)/CommUtil.null2Double(price)), old_price);
				temp_refund_money = refund_money;
				mv.addObject("refund_money", refund_money);
			} else {
				temp_refund_money = CommUtil.null2Double(obj
						.getGoods_all_price());
				mv.addObject("refund_money", obj.getGoods_all_price());
			}
			mv.addObject("msg", "退货服务号为" + obj.getReturn_service_id()
					+ "的商品退款成功，预存款" + temp_refund_money + "元已存入您的账户");

			if (CommUtil.null2Double(of.getEnough_reduce_amount()) > 0) {// 满就减金额退款
				Map er_info = (Map) Json.fromJson(of.getEnough_reduce_info());
				Iterator<String> it = er_info.keySet().iterator();
				// {"all_23":1110.0,"enouhg_23":"500.00","23":[13],"reduce23":100.0}
				while (it.hasNext()) {
					String key = it.next();
					if (key.substring(0, 1).equals("a")) {// 查找该商品的满就减信息
						String key2 = key.substring(4, key.length());
						List list = (List) er_info.get(key2);
						for (Object good_id : list) {

							if (CommUtil.null2Double(good_id) == obj
									.getGoods_id()) {// 有该商品
								double goods_price = CommUtil.null2Double(obj
										.getGoods_all_price());
								double all = CommUtil.null2Double(er_info.get(
										key).toString());
								double enouhg = CommUtil.null2Double(er_info
										.get("enouhg_" + key2).toString());
								if (all - goods_price < enouhg) {// 去掉该商品后不满足满就减
									double reduce = CommUtil
											.null2Double(er_info.get(
													"reduce_" + key2)
													.toString());
									double return_account = goods_price / all
											* reduce;
									temp_refund_money = CommUtil
											.null2Double(new BigDecimal(
													temp_refund_money
															- return_account));
									mv.addObject("refund_money",
											temp_refund_money);
									mv.addObject(
											"msg",
											"退货服务号为"
													+ obj.getReturn_service_id()
													+ "的商品退款成功，预存款"
													+ temp_refund_money
													+ "元已存入您的账户,其中扣除了"
													+ return_account + "元满减金额");
								}
							}
						}
					}
				}
			}

			mv.addObject("obj", obj);
			User user = this.userService.getObjById(obj.getUser_id());
			mv.addObject("user", user);
		}
		return mv;
	}

	/**
	 * refundlog_list列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "退款日志列表", value = "/admin/refundlog_list.htm*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping("/admin/refundlog_list.htm")
	public ModelAndView refundlog_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String refund_id, String user_name,
			String return_service_id, String beginTime, String endTime) {
		ModelAndView mv = new JModelAndView("admin/blue/refundlog_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		RefundLogQueryObject qo = new RefundLogQueryObject(currentPage, mv,
				orderBy, orderType);
		if (user_name != null && !user_name.equals("")) {
			qo.addQuery("obj.returnLog_userName", new SysMap(
					"returnLog_userName", user_name), "=");
		}
		if (refund_id != null && !refund_id.equals("")) {
			qo.addQuery("obj.refund_id", new SysMap("refund_id", refund_id),
					"=");
		}
		if (return_service_id != null && !return_service_id.equals("")) {
			qo.addQuery("obj.returnService_id", new SysMap("returnService_id",
					return_service_id), "=");
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			qo.addQuery("obj.addTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			qo.addQuery("obj.addTime",
					new SysMap("endTime", CommUtil.formatDate(endTime)), "<=");
		}
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, RefundLog.class, mv);
		IPageList pList = this.refundLogService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/refundlog_list.htm",
				"", params, pList, mv);
		mv.addObject("refund_id", refund_id);
		mv.addObject("user_name", user_name);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("return_service_id", return_service_id);
		return mv;
	}

	@SecurityMapping(title = "平台退款完成", value = "/admin/refund_finish.htm*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping("/admin/refund_finish.htm")
	public ModelAndView predeposit_modify_save(HttpServletRequest request,
			HttpServletResponse response, String user_id, String amount,
			String type, String info, String list_url, String refund_user_id,
			String obj_id, String gi_id) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().isDeposit()) {
			User user = null;
			if (user_id != null && !user_id.equals("")) {
				user = this.userService.getObjById(CommUtil.null2Long(user_id));
			} else {
				user = this.userService.getObjById(CommUtil
						.null2Long(refund_user_id));
			}
			user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
					user.getAvailableBalance(), amount)));
			this.userService.update(user);
			// 保存充值日志
			PredepositLog log = new PredepositLog();
			log.setPd_log_admin(SecurityUserHolder.getCurrentUser());
			log.setAddTime(new Date());
			log.setPd_log_amount(BigDecimal.valueOf(CommUtil
					.null2Double(amount)));
			log.setPd_log_info(info);
			log.setPd_log_user(user);
			log.setPd_op_type("人工退款");
			log.setPd_type("可用预存款");
			this.predepositLogService.save(log);
			if (obj_id != null && !obj_id.equals("")) {// 商品退款
				ReturnGoodsLog rgl = this.returnGoodsLogService
						.getObjById(CommUtil.null2Long(obj_id));
				rgl.setRefund_status(1);
				rgl.setGoods_return_status("11");// 平台退款完成
				this.returnGoodsLogService.update(rgl);
				RefundLog r_log = new RefundLog();
				r_log.setAddTime(new Date());
				r_log.setRefund_id(CommUtil.formatTime("yyyyMMddHHmmss",
						new Date()) + user.getId());
				r_log.setReturnLog_id(rgl.getId());
				r_log.setReturnService_id(rgl.getReturn_service_id());
				r_log.setRefund(BigDecimal.valueOf(CommUtil.null2Double(amount)));
				r_log.setRefund_log(info);
				r_log.setRefund_type("预存款");
				r_log.setRefund_user(SecurityUserHolder.getCurrentUser());
				r_log.setReturnLog_userName(rgl.getUser_name());
				r_log.setReturnLog_userId(rgl.getUser_id());
				this.refundLogService.save(r_log);
				OrderForm of = this.orderFormService.getObjById(rgl
						.getReturn_order_id());
				Goods goods = this.goodsService.getObjById(rgl.getGoods_id());
				// 如果为自营商品时不添加结算日志，只有第三方经销商的商品才有结算日志
				if (goods.getGoods_type() == 1) {
					Store store = this.goodsService.getObjById(
							rgl.getGoods_id()).getGoods_store();
					PayoffLog pol = new PayoffLog();
					pol.setPl_sn("pl"
							+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
							+ store.getUser().getId());
					pol.setAddTime(new Date());
					pol.setGoods_info(of.getReturn_goods_info());
					pol.setRefund_user_id(rgl.getUser_id());
					pol.setSeller(goods.getGoods_store().getUser());
					pol.setRefund_userName(rgl.getUser_name());
					pol.setReturn_service_id(rgl.getReturn_service_id());
					pol.setPayoff_type(-1);
					pol.setPl_info("退款完成");
					BigDecimal price = BigDecimal.valueOf(CommUtil
							.null2Double(amount)); // 商品的原价
					BigDecimal mission = BigDecimal.valueOf(CommUtil.subtract(
							1, rgl.getGoods_commission_rate()));// 商品的佣金比例
					BigDecimal final_money = BigDecimal.valueOf(CommUtil
							.subtract(0, CommUtil.mul(price, mission)));
					pol.setTotal_amount(final_money);
					List<Map> list = new ArrayList<Map>();
					Map json = new HashMap();
					json.put("goods_id", rgl.getGoods_id());
					json.put("goods_name", rgl.getGoods_name());
					json.put("goods_price", rgl.getGoods_price());
					json.put("goods_mainphoto_path",
							rgl.getGoods_mainphoto_path());
					json.put("goods_commission_rate",
							rgl.getGoods_commission_rate());
					json.put("goods_count", rgl.getGoods_count());
					json.put("goods_all_price", rgl.getGoods_all_price());
					json.put(
							"goods_commission_price",
							CommUtil.mul(rgl.getGoods_all_price(),
									rgl.getGoods_commission_rate()));
					json.put("goods_payoff_price", final_money);
					list.add(json);
					pol.setReturn_goods_info(Json.toJson(list,
							JsonFormat.compact()));
					pol.setO_id(CommUtil.null2String(rgl.getReturn_order_id()));
					pol.setOrder_id(of.getOrder_id());
					pol.setCommission_amount(BigDecimal.valueOf(0));
					pol.setOrder_total_price(final_money);
					this.payoffLogService.save(pol);
					store.setStore_sale_amount(BigDecimal.valueOf(CommUtil
							.subtract(store.getStore_sale_amount(), amount)));// 减少店铺本次结算总销售金额
					store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil
							.subtract(store.getStore_payoff_amount(),
									CommUtil.mul(price, mission))));// 减少店铺本次结算总金额
					this.storeService.update(store);
					// 减少系统总销售金额、总结算金额
					SysConfig sc = this.configService.getSysConfig();
					sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.subtract(
							sc.getPayoff_all_sale(), amount)));
					sc.setPayoff_all_amount(BigDecimal.valueOf(CommUtil
							.subtract(sc.getPayoff_all_amount(),
									CommUtil.mul(price, mission))));
					sc.setPayoff_all_amount_reality(BigDecimal.valueOf(CommUtil
							.add(pol.getReality_amount(),
									sc.getPayoff_all_amount_reality())));// 增加系统实际总结算
					this.configService.update(sc);
				}
				String msg_content = "成功为订单号：" + of.getOrder_id() + "退款"
						+ amount + "元，请到收支明细中查看。";
				// 发送系统站内信
				Message msg = new Message();
				msg.setAddTime(new Date());
				msg.setStatus(0);
				msg.setType(0);
				msg.setContent(msg_content);
				msg.setFromUser(SecurityUserHolder.getCurrentUser());
				msg.setToUser(user);
				this.messageService.save(msg);
				mv.addObject("list_url", CommUtil.getURL(request)
						+ "/admin/refund_list.htm");
			}
			if (gi_id != null && !gi_id.equals("")) {// 消费码退款
				GroupInfo gi = this.groupinfoService.getObjById(CommUtil
						.null2Long(gi_id));
				gi.setStatus(7);// 退款完成
				this.groupinfoService.update(gi);
				OrderForm of = this.orderFormService.getObjById(gi
						.getOrder_id());
				if (of.getOrder_form() == 0) {// 商家订单生成退款结算账单
					Store store = this.storeService.getObjById(CommUtil
							.null2Long(of.getStore_id()));
					PayoffLog pol = new PayoffLog();
					pol.setPl_sn("pl"
							+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
							+ store.getUser().getId());
					pol.setAddTime(new Date());
					pol.setGoods_info(of.getReturn_goods_info());
					pol.setRefund_user_id(gi.getUser_id());
					pol.setSeller(store.getUser());
					pol.setRefund_userName(gi.getUser_name());
					pol.setPayoff_type(-1);
					pol.setPl_info("退款完成");
					BigDecimal price = BigDecimal.valueOf(CommUtil
							.null2Double(amount)); // 商品的原价
					BigDecimal final_money = BigDecimal.valueOf(CommUtil
							.subtract(0, price));
					pol.setTotal_amount(final_money);
					// 将订单中group_info（{}）转换为List<Map>([{}])
					List<Map> Map_list = new ArrayList<Map>();
					Map group_map = this.orderFormTools.queryGroupInfo(of
							.getGroup_info());
					Map_list.add(group_map);
					pol.setReturn_goods_info(Json.toJson(Map_list,
							JsonFormat.compact()));
					pol.setO_id(of.getId().toString());
					pol.setOrder_id(of.getOrder_id());
					pol.setCommission_amount(BigDecimal.valueOf(0));
					pol.setOrder_total_price(final_money);
					this.payoffLogService.save(pol);

					store.setStore_sale_amount(BigDecimal.valueOf(CommUtil
							.subtract(store.getStore_sale_amount(), amount)));// 减少店铺本次结算总销售金额
					store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil
							.subtract(store.getStore_payoff_amount(), price)));// 减少店铺本次结算总金额
					this.storeService.update(store);
					// 减少系统总销售金额、总结算金额
					SysConfig sc = this.configService.getSysConfig();
					sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.subtract(
							sc.getPayoff_all_sale(), amount)));
					sc.setPayoff_all_amount(BigDecimal.valueOf(CommUtil
							.subtract(sc.getPayoff_all_amount(),
									CommUtil.mul(amount, 0))));
					sc.setPayoff_all_amount_reality(BigDecimal.valueOf(CommUtil
							.add(pol.getReality_amount(),
									sc.getPayoff_all_amount_reality())));// 增加系统实际总结算
					this.configService.update(sc);
				}
				// 生成退款日志
				RefundLog r_log = new RefundLog();
				r_log.setAddTime(new Date());
				r_log.setRefund_id(CommUtil.formatTime("yyyyMMddHHmmss",
						new Date()) + user.getId());
				r_log.setReturnLog_id(gi.getId());
				r_log.setReturnService_id(gi.getGroup_sn());
				r_log.setRefund(BigDecimal.valueOf(CommUtil.null2Double(amount)));
				r_log.setRefund_log(info);
				r_log.setRefund_type("预存款");
				r_log.setRefund_user(SecurityUserHolder.getCurrentUser());
				r_log.setReturnLog_userName(gi.getUser_name());
				r_log.setReturnLog_userId(gi.getUser_id());
				this.refundLogService.save(r_log);
				String msg_content = "您的团购商品：" + gi.getLifeGoods().getGg_name()
						+ "消费码已经成功退款，退款金额为："
						+ gi.getLifeGoods().getGroup_price() + "，退款消费码:"
						+ gi.getGroup_sn();
				// 发送系统站内信
				Message msg = new Message();
				msg.setAddTime(new Date());
				msg.setStatus(0);
				msg.setType(0);
				msg.setContent(msg_content);
				msg.setFromUser(SecurityUserHolder.getCurrentUser());
				msg.setToUser(user);
				this.messageService.save(msg);
				mv.addObject("op_title", "退款成功");
				mv.addObject("list_url", CommUtil.getURL(request)
						+ "/admin/groupinfo_refund_list.htm");
			}
		} else {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/operation_base_set.htm");
		}
		return mv;
	}

}
