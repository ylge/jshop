package com.iskyshop.module.app.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Group;
import com.iskyshop.foundation.domain.GroupArea;
import com.iskyshop.foundation.domain.GroupClass;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.GroupPriceRange;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.OrderFormLog;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupAreaService;
import com.iskyshop.foundation.service.IGroupClassService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IGroupPriceRangeService;
import com.iskyshop.foundation.service.IGroupService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.PaymentTools;
import com.iskyshop.view.web.tools.GoodsClassViewTools;
import com.iskyshop.view.web.tools.GroupViewTools;
import com.iskyshop.view.web.tools.NavViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;

/**
 * 
 * <p>
 * Title: IntegralViewAction.java
 * </p>
 * 
 * <p>
 * Description: app积分兑换
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
 * @date 2015-1-12
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class AppGroupViewAction {

	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IGroupAreaService groupAreaService;
	@Autowired
	private IGroupPriceRangeService groupPriceRangeService;
	@Autowired
	private IGroupClassService groupClassService;
	@Autowired
	private IGroupGoodsService groupGoodsService;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IUserService userService;
	@Autowired
	private GroupViewTools groupViewTools;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private NavViewTools navTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private PaymentTools paymentTools;
	@Autowired
	private GoodsClassViewTools gcViewTools;
	@Autowired
	private IPaymentService paymentService;

	/**
	 * 团购首页，列表 商品惠 生活惠 2种
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/app/group_index.htm")
	public void group_index(HttpServletRequest request,
			HttpServletResponse response, String orderBy, String orderType,
			String gc_id, String gpr_id, String ga_id, String type,
			String begincount, String selectcount) {
		Map json = new HashMap();
		List list = new ArrayList();
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig().getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		if ("".equals(type) || "goods".equals(type) || type == null) {
			Map params = new HashMap();
			params.put("beginTime", new Date());
			params.put("endTime", new Date());
			params.put("group_type", 0);
			List<Group> groups = this.groupService
					.query("select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime and obj.group_type=:group_type",
							params, -1, -1);
			if (groups.size() > 0) {
				params.clear();
				params.put("beginTime", new Date());
				params.put("endTime", new Date());
				params.put("group_id", groups.get(0).getId());
				params.put("gg_status", 1);
				params.put("status", 0);
				StringBuilder sb = new StringBuilder(
						"select obj from GroupGoods obj where obj.group.id=:group_id and  obj.beginTime<=:beginTime and obj.endTime >=:endTime and  obj.gg_status=:gg_status and obj.gg_goods.goods_status=:status ");
				if (gc_id != null && !gc_id.equals("")) {
					params.put("gc_id", CommUtil.null2Long(gc_id));
					sb.append(" and obj.gg_gc.id=:gc_id ");
				}
				if (ga_id != null && !ga_id.equals("")) {
					params.put("ga_id", CommUtil.null2Long(ga_id));
					sb.append(" and obj.gg_ga.id=:ga_id ");
				}
				GroupPriceRange gpr = this.groupPriceRangeService
						.getObjById(CommUtil.null2Long(gpr_id));
				if (gpr != null) {
					params.put("begin_price",
							BigDecimal.valueOf(gpr.getGpr_begin()));
					params.put("end_price",
							BigDecimal.valueOf(gpr.getGpr_end()));
					sb.append(" and obj.gg_price>=:begin_price ");
					sb.append(" and obj.gg_price<=:end_price ");
				}
				if (orderBy == null || orderBy.equals("")) {
					orderBy = "addTime";
				}
				if (orderType == null || orderType.equals("")) {
					orderType = "desc";
				}
				sb.append(" order by obj." + orderBy + " " + orderType);
				List<GroupGoods> groupGoods = this.groupGoodsService.query(
						sb.toString(), params, CommUtil.null2Int(begincount),
						CommUtil.null2Int(selectcount));
				for (GroupGoods gg : groupGoods) {
					Map map = new HashMap();
					map.put("gg_img", url + "/" + gg.getGg_img().getPath()
							+ "/" + gg.getGg_img().getName());
					map.put("gg_name", gg.getGg_name());
					map.put("gg_price", CommUtil.null2Double(gg.getGg_price()));
					map.put("gg_selled_count", gg.getGg_selled_count());
					map.put("id", gg.getId());
					list.add(map);
				}

				List<GroupClass> gcs = this.groupClassService
						.query("select obj from GroupClass obj where obj.gc_type=0 and obj.parent.id is null order by obj.gc_sequence asc",
								null, -1, -1);
				List<GroupPriceRange> gprs = this.groupPriceRangeService
						.query("select obj from GroupPriceRange obj order by obj.gpr_begin asc",
								null, -1, -1);
			}
		}

		if ("life".equals(type)) {
			Map params = new HashMap();
			params.put("beginTime", new Date());
			params.put("endTime", new Date());
			params.put("group_type", 1);
			List<Group> groups = this.groupService
					.query("select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime and obj.group_type=:group_type",
							params, -1, -1);
			if (groups.size() > 0) {
				params.clear();
				params.put("beginTime", new Date());
				params.put("endTime", new Date());
				params.put("group_id", groups.get(0).getId());
				params.put("gg_status", 1);
				StringBuilder sb = new StringBuilder(
						"select obj from GroupLifeGoods obj where obj.group.id=:group_id and  obj.beginTime<=:beginTime and obj.endTime >=:endTime and obj.group_status=:gg_status ");
				if (gc_id != null && !gc_id.equals("")) {
					params.put("gc_id", CommUtil.null2Long(gc_id));
					sb.append(" and obj.gg_gc.id=:gc_id ");
				}
				if (ga_id != null && !ga_id.equals("")) {
					params.put("ga_id", CommUtil.null2Long(ga_id));
					sb.append(" and obj.gg_ga.id=:ga_id ");
				}
				GroupPriceRange gpr = this.groupPriceRangeService
						.getObjById(CommUtil.null2Long(gpr_id));
				if (gpr != null) {
					params.put("begin_price",
							BigDecimal.valueOf(gpr.getGpr_begin()));
					params.put("end_price",
							BigDecimal.valueOf(gpr.getGpr_end()));
					sb.append(" and obj.group_price>=:begin_price ");
					sb.append(" and obj.group_price<=:end_price ");
				}
				if (orderBy == null || orderBy.equals("")) {
					orderBy = "addTime";
				}
				if (orderType == null || orderType.equals("")) {
					orderType = "desc";
				}
				sb.append(" order by " + orderBy + " " + orderType);
				List<GroupLifeGoods> groupGoods = this.groupLifeGoodsService
						.query(sb.toString(), params,
								CommUtil.null2Int(begincount),
								CommUtil.null2Int(selectcount));
				for (GroupLifeGoods gg : groupGoods) {
					Map map = new HashMap();
					map.put("gg_img", url + "/" + gg.getGroup_acc().getPath()
							+ "/" + gg.getGroup_acc().getName());
					map.put("gg_name", gg.getGg_name());
					map.put("gg_price",
							CommUtil.null2Double(gg.getGroup_price()));
					map.put("gg_selled_count", gg.getSelled_count());
					map.put("id", gg.getId());
					list.add(map);
				}
				List<GroupClass> gcs = this.groupClassService
						.query("select obj from GroupClass obj where obj.gc_type=1 and obj.parent.id is null order by obj.gc_sequence asc",
								null, -1, -1);
				List<GroupPriceRange> gprs = this.groupPriceRangeService
						.query("select obj from GroupPriceRange obj order by obj.gpr_begin asc",
								null, -1, -1);
			}
		}

		json.put("grouplist", list);
		this.send_json(Json.toJson(json, JsonFormat.compact()), response);
	}
	/**
	 * 团购地区
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/app/group_area.htm")
	public void group_are(HttpServletRequest request,
			HttpServletResponse response) {
		List list = new ArrayList();
		List<GroupArea> gas = this.groupAreaService
				.query("select obj from GroupArea obj where obj.parent.id is null order by obj.ga_sequence asc",
						null, -1, -1);
		for (GroupArea groupArea : gas) {
			Map map = new HashMap();
			map.put("name", groupArea.getGa_name());
			map.put("id", groupArea.getId());
			list.add(map);
		}

		Map json = new HashMap();
		json.put("area_list", list);
		this.send_json(Json.toJson(json, JsonFormat.compact()), response);
	}

	/**
	 * 团购筛选条件
	 * 
	 * @param request
	 * @param response
	 * @param type
	 */
	@RequestMapping("/app/group_class.htm")
	public void group_class(HttpServletRequest request,
			HttpServletResponse response, String type) {
		Map json = new HashMap();
		List<GroupClass> gcs = new ArrayList<GroupClass>();
		List<GroupPriceRange> gprs = new ArrayList<GroupPriceRange>();
		if ("".equals(type) || "goods".equals(type) || type == null) {
			Map params = new HashMap();
			params.put("beginTime", new Date());
			params.put("endTime", new Date());
			params.put("group_type", 0);
			List<Group> groups = this.groupService
					.query("select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime and obj.group_type=:group_type",
							params, -1, -1);
			if (groups.size() > 0) {
				gcs = this.groupClassService
						.query("select obj from GroupClass obj where obj.gc_type=0 and obj.parent.id is null order by obj.gc_sequence asc",
								null, -1, -1);
				gprs = this.groupPriceRangeService
						.query("select obj from GroupPriceRange obj order by obj.gpr_begin asc",
								null, -1, -1);
			}
		}

		if ("life".equals(type)) {
			Map params = new HashMap();
			params.put("beginTime", new Date());
			params.put("endTime", new Date());
			params.put("group_type", 1);
			List<Group> groups = this.groupService
					.query("select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime and obj.group_type=:group_type",
							params, -1, -1);
			if (groups.size() > 0) {
				gcs = this.groupClassService
						.query("select obj from GroupClass obj where obj.gc_type=1 and obj.parent.id is null order by obj.gc_sequence asc",
								null, -1, -1);
				gprs = this.groupPriceRangeService
						.query("select obj from GroupPriceRange obj order by obj.gpr_begin asc",
								null, -1, -1);
			}
		}

		List gc_list = new ArrayList();
		for (GroupClass groupClass : gcs) {
			Map map = new HashMap();
			map.put("name", groupClass.getGc_name());
			map.put("id", groupClass.getId());
			gc_list.add(map);
		}
		json.put("gc_list", gc_list);

		List gp_list = new ArrayList();
		for (GroupPriceRange groupPriceRange : gprs) {
			Map map = new HashMap();
			map.put("name", groupPriceRange.getGpr_name());
			map.put("id", groupPriceRange.getId());
			gp_list.add(map);
		}
		json.put("gp_list", gp_list);

		this.send_json(Json.toJson(json, JsonFormat.compact()), response);
	}

	/**
	 * 商品惠
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping("/app/group_goods.htm")
	public void group_goods(HttpServletRequest request,
			HttpServletResponse response, String id) {
		GroupGoods groupGoods = this.groupGoodsService.getObjById(CommUtil
				.null2Long(id));
		Map json = new HashMap();
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig().getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		json.put("gg_img", url + "/" + groupGoods.getGg_img().getPath() + "/"
				+ groupGoods.getGg_img().getName());
		json.put("gg_name", groupGoods.getGg_name());
		json.put("gg_price", CommUtil.null2Double(groupGoods.getGg_price()));

		json.put("gg_rebate", CommUtil.null2Double(groupGoods.getGg_rebate()));
		json.put("gg_endTime",
				CommUtil.formatShortDate(groupGoods.getEndTime()));
		json.put("gg_selled_count", groupGoods.getGg_selled_count());
		json.put("gg_store_price", groupGoods.getGg_goods().getGoods_price());
		json.put("gg_goods_id", groupGoods.getGg_goods().getId());

		json.put("goods_well_evaluate",
				CommUtil.mul(groupGoods.getGg_goods().getWell_evaluate(), 100)
						+ "%");// 好评率
		json.put(
				"goods_middle_evaluate",
				CommUtil.mul(groupGoods.getGg_goods().getMiddle_evaluate(), 100)
						+ "%");// 中评率
		json.put("goods_bad_evaluate",
				CommUtil.mul(groupGoods.getGg_goods().getBad_evaluate(), 100)
						+ "%");// 差评率
		json.put("evaluate_count", groupGoods.getGg_goods().getEvaluates()
				.size());// 总评论数

		this.send_json(Json.toJson(json, JsonFormat.compact()), response);
	}

	/**
	 * 商品惠web详情
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/app/group_goods_introduce.htm")
	public ModelAndView group_goods_introduce(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("app/simple_goods_introduce.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GroupGoods groupGoods = this.groupGoodsService.getObjById(CommUtil
				.null2Long(id));
		mv.addObject("obj", groupGoods.getGg_content());
		return mv;
	}

	/**
	 * 生活惠
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping("/app/group_life.htm")
	public void group_life(HttpServletRequest request,
			HttpServletResponse response, String id) {
		GroupLifeGoods groupLifeGoods = this.groupLifeGoodsService
				.getObjById(CommUtil.null2Long(id));
		Map json = new HashMap();
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig().getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		json.put("gg_img", url + "/" + groupLifeGoods.getGroup_acc().getPath()
				+ "/" + groupLifeGoods.getGroup_acc().getName());
		json.put("gg_name", groupLifeGoods.getGg_name());
		json.put("gg_price",
				CommUtil.null2Double(groupLifeGoods.getGroup_price()));
		json.put("gg_selled_count", groupLifeGoods.getSelled_count());

		json.put("gg_rebate",
				CommUtil.null2Double(groupLifeGoods.getGg_rebate()));
		json.put("gg_endTime",
				CommUtil.formatShortDate(groupLifeGoods.getEndTime()));
		json.put("gg_selled_count", groupLifeGoods.getSelled_count());
		json.put("gg_store_price", groupLifeGoods.getCost_price());

		this.send_json(Json.toJson(json, JsonFormat.compact()), response);
	}

	/**
	 * 生活惠web详情
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/app/group_life_introduce.htm")
	public ModelAndView group_life_introduce(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("app/simple_goods_introduce.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GroupLifeGoods groupLifeGoods = this.groupLifeGoodsService
				.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", groupLifeGoods.getGroup_details());
		return mv;
	}

	@RequestMapping("/app/group_life_order_save.htm")
	public void group_life_order_save(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token,
			String group_id, String order_count, String pay_method) {
		boolean verify = true;
		int code = 0;
		Map map = new HashMap();
		if (verify && user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
				if (user.getApp_login_token().equals(token.toLowerCase())) {
					if (CommUtil.null2Int(order_count) <= 0) {
						order_count = "1";
					}
					GroupLifeGoods group = null;
					if (group_id != null && !group_id.equals("")) {
						group = this.groupLifeGoodsService.getObjById(CommUtil
								.null2Long(group_id));

						double group_total_price = 0;
						OrderForm orderForm = new OrderForm();
						group_total_price = CommUtil.null2Double(group
								.getGroup_price())
								* CommUtil.null2Int(order_count);
						orderForm.setAddTime(new Date());
						orderForm.setUser_id(user.getId().toString());
						orderForm.setUser_name(user.getUserName());
						Map json = new HashMap();
						json.put("goods_id", group.getId().toString());
						json.put("goods_name", group.getGg_name());
						json.put("goods_type", group.getGoods_type());
						json.put("goods_price", group.getGroup_price());
						json.put("goods_count", CommUtil.null2Int(order_count));
						json.put("goods_total_price", group_total_price);
						json.put("goods_mainphoto_path", group.getGroup_acc()
								.getPath()
								+ "/"
								+ group.getGroup_acc().getName());
						orderForm.setGroup_info(Json.toJson(json,
								JsonFormat.compact()));
						if (group.getGoods_type() == 0) {
							if (group.getUser().getStore() != null) {
								orderForm.setStore_id(group.getUser()
										.getStore().getId().toString());
							}
							orderForm.setOrder_form(0);
						} else {
							orderForm.setOrder_form(1);
						}
						orderForm.setTotalPrice(BigDecimal
								.valueOf(group_total_price));
						orderForm.setOrder_id("life"
								+ user.getId()
								+ CommUtil.formatTime("yyyyMMddHHmmss",
										new Date()));
						orderForm.setOrder_status(10);
						orderForm.setOrder_cat(2);
						orderForm.setPayType(pay_method);
						orderForm.setPayment(this.get_payment(pay_method));
						this.orderFormService.save(orderForm);
						map.put("total_price", group_total_price);
						map.put("order_sn", orderForm.getOrder_id());
						map.put("order_id", orderForm.getId());
						OrderFormLog ofl = new OrderFormLog();
						ofl.setAddTime(new Date());
						ofl.setOf(orderForm);
						ofl.setLog_info("提交订单");
						ofl.setLog_user(user);
						this.orderFormLogService.save(ofl);
						code = 100;
					} else {
						code = -100;
					}
				}
			}
		}
		map.put("code", code);
		this.send_json(Json.toJson(map, JsonFormat.compact()), response);
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
