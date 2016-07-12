package com.iskyshop.manage.buyer.action;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StorePoint;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.EvaluateQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IStorePointService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.ImageTools;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.buyer.tools.EvaluateTools;

/**
 * 
 * <p>
 * Title: EvaluateBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 用户评价管理
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
 * @author lixiaoyang
 * 
 * @date 2015-3-3
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class EvaluateBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private ImageTools imageTools;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IStorePointService storePointService;
	@Autowired
	private IUserService userService;
	@Autowired
	private EvaluateTools evaluateTools;

	@SecurityMapping(title = "买家评价列表", value = "/buyer/evaluate_list.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/evaluate_list.htm")
	public ModelAndView evaluate_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/evaluate_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		EvaluateQueryObject qo = new EvaluateQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.evaluate_user.id", new SysMap("user_id",
				SecurityUserHolder.getCurrentUser().getId()), "=");
		IPageList pList = this.evaluateService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("evaluateTools", evaluateTools);
		mv.addObject("imageTools", imageTools);
		return mv;
	}

	@SecurityMapping(title = "买家评价修改", value = "/buyer/evaluate_edit.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/evaluate_edit.htm")
	public ModelAndView evaluate_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/evaluate_edit.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Evaluate evaluate = this.evaluateService.getObjById(CommUtil
				.null2Long(id));
		OrderForm obj = evaluate.getOf();
		if (evaluate != null
				&& obj != null
				&& obj.getUser_id().equals(
						SecurityUserHolder.getCurrentUser().getId().toString())) {
			mv.addObject("evaluate", evaluate);
			mv.addObject("id", id);
			mv.addObject("imageTools", imageTools);
			List<Map> list = orderFormTools.queryGoodsInfo(obj.getGoods_info());
			for (Map map : list) {
				if (map.get("goods_id")
						.toString()
						.equals(evaluate.getEvaluate_goods().getId().toString())) {
					mv.addObject("obj", map);
				}
			}
			mv.addObject("orderFormTools", orderFormTools);
			String evaluate_session = CommUtil.randomString(32);
			request.getSession(false).setAttribute("evaluate_session",
					evaluate_session);
			mv.addObject("evaluate_session", evaluate_session);
			if (obj.getOrder_status() > 50) {
				mv = new JModelAndView("success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "订单已关闭评价");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/evaluate_list.htm");
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单信息错误");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/evaluate_list.htm");
		}
		if (orderFormTools.evaluate_able(obj.getFinishTime()) == 0) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "已超出评价期限");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/evaluate_list.htm");
		}
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("jsessionid", request.getSession().getId());

		return mv;
	}

	@SecurityMapping(title = "买家评价保存", value = "/buyer/evaluate_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/evaluate_save.htm")
	public ModelAndView evaluate_save(HttpServletRequest request,
			HttpServletResponse response, String id, String evaluate_session) {
		JModelAndView mv;

		Evaluate eva = this.evaluateService.getObjById(CommUtil.null2Long(id));
		String goods_id = eva.getEvaluate_goods().getId().toString();

		int description = eva_rate(request.getParameter("description_evaluate"
				+ goods_id));
		int service = eva_rate(request.getParameter("service_evaluate"
				+ goods_id));
		int ship = eva_rate(request.getParameter("ship_evaluate" + goods_id));
		if (description == 0 || service == 0 || ship == 0) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，禁止评价");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/evaluate_list.htm");
			return mv;
		}

		OrderForm obj = eva.getOf();
		String evaluate_session1 = (String) request.getSession(false)
				.getAttribute("evaluate_session");
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		if (orderFormTools.evaluate_able(obj.getFinishTime()) == 0) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "已超出评价期限");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/evaluate_list.htm");
			return mv;
		} else {
			if (evaluate_session1 != null
					&& evaluate_session1.equals(evaluate_session)
					&& obj != null
					&& obj.getUser_id().equals(
							SecurityUserHolder.getCurrentUser().getId()
									.toString())) {
				request.getSession(false).removeAttribute("evaluate_session");
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("修改评价订单");
				ofl.setLog_user(SecurityUserHolder.getCurrentUser());
				ofl.setOf(obj);
				this.orderFormLogService.save(ofl);
				List<Accessory> img_list = imageTools.queryImgs(eva
						.getEvaluate_photos());

				eva.setEvaluate_info(request.getParameter("evaluate_info_"
						+ goods_id));
				eva.setEvaluate_photos(request.getParameter("evaluate_photos_"
						+ goods_id));
				eva.setEvaluate_buyer_val(1);
				eva.setDescription_evaluate(BigDecimal.valueOf(CommUtil.null2Double(eva_rate(request
						.getParameter("description_evaluate" + goods_id)))));
				eva.setService_evaluate(BigDecimal.valueOf(CommUtil
						.null2Double(eva_rate(request
								.getParameter("service_evaluate" + goods_id)))));
				eva.setShip_evaluate(BigDecimal.valueOf(CommUtil
						.null2Double(eva_rate(request
								.getParameter("ship_evaluate" + goods_id)))));
				this.evaluateService.update(eva);
				String im_str = request.getParameter("evaluate_photos_"
						+ goods_id);
				if (im_str != null && !im_str.equals("") && im_str.length() > 0) {
					for (String str : im_str.split(",")) {
						if (str != null && !str.equals("")) {
							Accessory image = this.accessoryService
									.getObjById(CommUtil.null2Long(str));
							if (image.getInfo().equals("eva_temp")) {
								image.setInfo("eva_img");
								this.accessoryService.save(image);
							}
							img_list.remove(image);
						}
					}
				}
				for (Accessory acc : img_list) {
					CommUtil.del_acc(request, acc);
				}

				Map params = new HashMap();
				if (goods.getGoods_type() == 1) {
					Store store = this.storeService.getObjById(CommUtil
							.null2Long(goods.getGoods_store().getId()));
					params.put("store_id", store.getId().toString());
					List<Evaluate> evas = this.evaluateService
							.query("select obj from Evaluate obj where obj.of.store_id=:store_id  and obj.evaluate_status<2",
									params, -1, -1);
					double store_evaluate = 0;
					double description_evaluate = 0;
					double description_evaluate_total = 0;
					double service_evaluate = 0;
					double service_evaluate_total = 0;
					double ship_evaluate = 0;
					double ship_evaluate_total = 0;
					int store_credit = 0;
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
						store_credit += eva1.getEvaluate_buyer_val();
					}
					description_evaluate = CommUtil.null2Double(df
							.format(description_evaluate_total / evas.size()));
					service_evaluate = CommUtil.null2Double(df
							.format(service_evaluate_total / evas.size()));
					ship_evaluate = CommUtil.null2Double(df
							.format(ship_evaluate_total / evas.size()));
					store_evaluate = (description_evaluate + service_evaluate + ship_evaluate) / 3;// 综合评分=三项具体评分之和/3
					store.setStore_credit(store_credit);
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
							.valueOf(description_evaluate>5?5:description_evaluate));
					point.setService_evaluate(BigDecimal
							.valueOf(service_evaluate>5?5:service_evaluate));
					point.setShip_evaluate(BigDecimal.valueOf(ship_evaluate>5?5:ship_evaluate));
					point.setStore_evaluate(BigDecimal.valueOf(store_evaluate>5?5:store_evaluate));
					if (sps.size() > 0) {
						this.storePointService.update(point);
					} else {
						this.storePointService.save(point);
					}
				} else {
					User sp_user = this.userService.getObjById(obj
							.getEva_user_id());
					params.put("user_id", SecurityUserHolder.getCurrentUser()
							.getId().toString());
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
				this.goodsService.update(goods);

				mv = new JModelAndView("success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "评价修改成功");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/evaluate_list.htm");
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "禁止重复评价");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/evaluate_list.htm");
			}
		}
		return mv;
	}

	@SecurityMapping(title = "买家评价删除", value = "/buyer/evaluate_del.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/evaluate_del.htm")
	public String evaluate_del(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/evaluate_edit.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Evaluate evaluate = this.evaluateService.getObjById(CommUtil
				.null2Long(id));
		OrderForm obj = evaluate.getOf();
		if (evaluate != null
				&& obj != null
				&& obj.getUser_id().equals(
						SecurityUserHolder.getCurrentUser().getId().toString())) {
			evaluate.setEvaluate_status(2);
			this.evaluateService.update(evaluate);
			Goods goods = evaluate.getEvaluate_goods();
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("删除评价");
			ofl.setLog_user(SecurityUserHolder.getCurrentUser());
			ofl.setOf(obj);
			this.orderFormLogService.save(ofl);
			Map params = new HashMap();
			if (goods.getGoods_type() == 1) {
				Store store = this.storeService.getObjById(CommUtil
						.null2Long(goods.getGoods_store().getId()));
				params.put("store_id", store.getId().toString());
				List<Evaluate> evas = this.evaluateService
						.query("select obj from Evaluate obj where obj.of.store_id=:store_id and obj.evaluate_status<2",
								params, -1, -1);
				double store_evaluate = 0;
				double description_evaluate = 0;
				double description_evaluate_total = 0;
				double service_evaluate = 0;
				double service_evaluate_total = 0;
				double ship_evaluate = 0;
				double ship_evaluate_total = 0;
				int store_credit = 0;
				DecimalFormat df = new DecimalFormat("0.0");
				for (Evaluate eva1 : evas) {
					description_evaluate_total = description_evaluate_total
							+ CommUtil.null2Double(eva1
									.getDescription_evaluate());
					service_evaluate_total = service_evaluate_total
							+ CommUtil.null2Double(eva1.getService_evaluate());
					ship_evaluate_total = ship_evaluate_total
							+ CommUtil.null2Double(eva1.getShip_evaluate());
					store_credit += eva1.getEvaluate_buyer_val();
				}
				description_evaluate = CommUtil.null2Double(df
						.format(description_evaluate_total / evas.size()));
				service_evaluate = CommUtil.null2Double(df
						.format(service_evaluate_total / evas.size()));
				ship_evaluate = CommUtil.null2Double(df
						.format(ship_evaluate_total / evas.size()));
				store_evaluate = (description_evaluate + service_evaluate + ship_evaluate) / 3;// 综合评分=三项具体评分之和/3
				store.setStore_credit(store_credit);
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
						.valueOf(description_evaluate));
				point.setService_evaluate(BigDecimal.valueOf(service_evaluate));
				point.setShip_evaluate(BigDecimal.valueOf(ship_evaluate));
				point.setStore_evaluate(BigDecimal.valueOf(store_evaluate));
				if (sps.size() > 0) {
					this.storePointService.update(point);
				} else {
					this.storePointService.save(point);
				}
			} else {
				User sp_user = this.userService
						.getObjById(obj.getEva_user_id());
				params.put("user_id", SecurityUserHolder.getCurrentUser()
						.getId().toString());
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
							+ CommUtil.null2Double(eva1.getService_evaluate());
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
				point.setService_evaluate(BigDecimal.valueOf(service_evaluate));
				point.setShip_evaluate(BigDecimal.valueOf(ship_evaluate));
				point.setStore_evaluate(BigDecimal.valueOf(store_evaluate));
				if (sps.size() > 0) {
					this.storePointService.update(point);
				} else {
					this.storePointService.save(point);
				}
			}
			this.goodsService.update(goods);
		}
		return "redirect:/buyer/evaluate_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "买家追加评价", value = "/buyer/order_evaluate_add.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/evaluate_add.htm")
	public ModelAndView order_evaluate_add(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/evaluate_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Evaluate evaluate = this.evaluateService.getObjById(CommUtil
				.null2Long(id));
		OrderForm obj = evaluate.getOf();
		Goods goods = evaluate.getEvaluate_goods();
		if (orderFormTools.evaluate_able(obj.getFinishTime()) == 0) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "已超出评价追加期限");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/evaluate_list.htm");
		} else {
			if (evaluate != null
					&& evaluate.getAddeva_status() == 0
					&& obj != null
					&& obj.getUser_id().equals(
							SecurityUserHolder.getCurrentUser().getId()
									.toString())) {
				mv.addObject("evaluate", evaluate);
				mv.addObject("imageTools", imageTools);

				List<Map> list = orderFormTools.queryGoodsInfo(obj
						.getGoods_info());
				for (Map map : list) {
					if (map.get("goods_id").toString()
							.equals(goods.getId().toString())) {
						mv.addObject("obj", map);
					}
				}

				mv.addObject("id", id);
				mv.addObject("orderFormTools", orderFormTools);
				String evaluate_session = CommUtil.randomString(32);
				request.getSession(false).setAttribute("evaluate_session",
						evaluate_session);
				mv.addObject("evaluate_session", evaluate_session);
				if (obj.getOrder_status() > 50) {
					mv = new JModelAndView("success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "订单已关闭评价");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/evaluate_list.htm");
				}
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "评价信息错误");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/evaluate_list.htm");
			}
		}
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("jsessionid", request.getSession().getId());
		return mv;
	}

	@SecurityMapping(title = "买家追加评价保存", value = "/buyer/evaluate_add_save.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/evaluate_add_save.htm")
	public ModelAndView evaluate_add_save(HttpServletRequest request,
			HttpServletResponse response, String id, String evaluate_session)
			throws Exception {
		Evaluate eva = this.evaluateService.getObjById(CommUtil.null2Long(id));
		String goods_id = eva.getEvaluate_goods().getId().toString();
		OrderForm obj = eva.getOf();
		String evaluate_session1 = (String) request.getSession(false)
				.getAttribute("evaluate_session");
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		JModelAndView mv;
		if (orderFormTools.evaluate_able(obj.getFinishTime()) == 0) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "已超出评价追加期限");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/evaluate_list.htm");
		} else {
			if (evaluate_session1 != null
					&& evaluate_session1.equals(evaluate_session)) {
				request.getSession(false).removeAttribute("evaluate_session");
				if (obj != null
						&& obj.getUser_id().equals(
								SecurityUserHolder.getCurrentUser().getId()
										.toString())) {
					if (obj.getOrder_status() == 50) {
						OrderFormLog ofl = new OrderFormLog();
						ofl.setAddTime(new Date());
						ofl.setLog_info("追加评价订单");
						ofl.setLog_user(SecurityUserHolder.getCurrentUser());
						ofl.setOf(obj);
						this.orderFormLogService.save(ofl);

						if (eva.getAddeva_status() == 0) {
							eva.setAddeva_status(1);
							eva.setAddeva_info(request
									.getParameter("evaluate_info_"
											+ goods.getId()));
							eva.setAddeva_photos(request
									.getParameter("evaluate_photos_"
											+ goods.getId()));
							eva.setAddeva_time(new Date());
							this.evaluateService.save(eva);
							String im_str = request
									.getParameter("evaluate_photos_"
											+ goods.getId());
							if (im_str != null && !im_str.equals("")
									&& im_str.length() > 0) {
								for (String str : im_str.split(",")) {
									if (str != null && !str.equals("")) {
										Accessory image = this.accessoryService
												.getObjById(CommUtil
														.null2Long(str));
										if (image.getInfo().equals("eva_temp")) {
											image.setInfo("eva_img");
											this.accessoryService.save(image);
										}
									}
								}
							}
						}
					}
					mv = new JModelAndView("success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "订单追加评价成功");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/evaluate_list.htm");
					return mv;
				} else {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "订单信息错误");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/evaluate_list.htm");
					return mv;
				}

			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "禁止重复评价");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/evaluate_list.htm");
				return mv;
			}

		}
		return mv;
	}

	private int eva_rate(String rate) {
		int score = 0;
		if (rate.equals("a")) {
			score = 1;
		} else if (rate.equals("b")) {
			score = 2;
		} else if (rate.equals("c")) {
			score = 3;
		} else if (rate.equals("d")) {
			score = 4;
		} else if (rate.equals("e")) {
			score = 5;
		}
		return score;
	}
}
