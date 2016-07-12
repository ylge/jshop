package com.iskyshop.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
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
import com.iskyshop.foundation.domain.CombinPlan;
import com.iskyshop.foundation.domain.GoldLog;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.SalesLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.CombinPlanQueryObject;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.domain.query.SalesLogQueryObject;
import com.iskyshop.foundation.service.ICombinPlanService;
import com.iskyshop.foundation.service.IGoldLogService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISalesLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.QueryTools;
import com.iskyshop.manage.seller.tools.CombinTools;

/**
 * 
 * <p>
 * Title: CombinSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商家组合销售管理控制器
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
 * @date 2014-9-19
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class CombinSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private ISalesLogService combinlogService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoldLogService goldLogService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private ISalesLogService saleslogService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ICombinPlanService combinplanService;
	@Autowired
	private CombinTools combinTools;
	@Autowired
	private QueryTools queryTools;

	@SecurityMapping(title = "组合销售商品列表", value = "/seller/combin.htm*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping("/seller/combin.htm")
	public ModelAndView combin(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String type,
			String combin_status, String goods_name, String beginTime,
			String endTime) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/combin_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig config = this.configService.getSysConfig();
		if (config.getCombin_status() == 1) {
			CombinPlanQueryObject qo = new CombinPlanQueryObject(currentPage,
					mv, "addTime", "desc");
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			qo.addQuery("obj.store_id", new SysMap("store_id", store.getId()),
					"=");
			if (type != null && !type.equals("")) {
				qo.addQuery("obj.combin_type",
						new SysMap("type", CommUtil.null2Int(type)), "=");
				mv.addObject("type", type);
			} else {
				qo.addQuery("obj.combin_type", new SysMap("type", 0), "=");
			}
			if (combin_status != null && !combin_status.equals("")) {
				qo.addQuery("obj.combin_status", new SysMap("combin_status",
						CommUtil.null2Int(combin_status)), "=");
				mv.addObject("combin_status", combin_status);
			}
			if (goods_name != null && !goods_name.equals("")) {
				qo.addQuery(
						"obj.main_goods_name",
						new SysMap("main_goods_name", "%"
								+ CommUtil.null2String(goods_name) + "%"),
						"like");
				mv.addObject("goods_name", goods_name);
			}
			if (beginTime != null && !beginTime.equals("")) {
				qo.addQuery(
						"obj.beginTime",
						new SysMap("beginTime", CommUtil.formatDate(beginTime)),
						">=");
				mv.addObject("beginTime", beginTime);
			}
			if (endTime != null && !endTime.equals("")) {
				qo.addQuery("obj.endTime",
						new SysMap("endTime", CommUtil.formatDate(beginTime)),
						"<=");
				mv.addObject("endTime", endTime);
			}
			System.out.println(qo.getQuery());
			qo.setPageSize(10);
			IPageList pList = this.combinplanService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("combinTools", combinTools);
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启组合销售促销方式");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "组合销售商品添加", value = "/seller/combin_add.htm*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping("/seller/combin_add.htm")
	public ModelAndView combin_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/combin_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		boolean ret = true;
		if (store.getCombin_end_time() != null) {
			Calendar cal = Calendar.getInstance();
			Date now = new Date();
			if (cal.getTime().before(now)) {
				ret = false;
			}
			mv.addObject("now", CommUtil.formatShortDate(now));
		} else {
			ret = false;
		}
		if (!ret) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您没有购买组合套餐或者套餐时间已到期");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/combin.htm");
		}
		return mv;
	}

	/**
	 * 验证商品两个组合类型是否存在（组合套装、组合配件）
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = " 验证商品两个组合类型是否存在", value = "/seller/verify_combin.htm*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping("/seller/verify_combin.htm")
	public void verify_combin(HttpServletRequest request,
			HttpServletResponse response, String gid, String combin_mark,
			String endTime, String id) {
		boolean ret = true;
		String code = "参数错误";
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(gid));
		if (goods == null) {
			ret = false;
			code = "主体商品信息错误";
		}
		if (combin_mark == null || combin_mark.equals("")) {
			ret = false;
			code = "参数错误";
		} else {
			if (!combin_mark.equals("0") && !combin_mark.equals("1")) {
				ret = false;
				code = "参数错误";
			}
		}
		Date endTime2 = CommUtil.formatDate(endTime);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (endTime2.after(store.getCombin_end_time())) {
			ret = false;
			code = "结束时间不能超出当前商家的套餐结束时间";
		}
		if (ret) {
			if (combin_mark.equals("0")) {
				if (goods.getCombin_suit_id() != null && id.equals("")) {// 非编辑状态
					ret = false;
					code = "该主商品已经存在组合套餐，请先将存在的组合套餐删除掉再添加新的组合套餐";
				}
			} else {
				if (goods.getCombin_parts_id() != null && id.equals("")) {// 非编辑状态
					ret = false;
					code = "该主商品已经存在组合配件，请先将存在的组合配件删除掉再添加新的组合配件";
				}
			}
		}
		Map json_map = new HashMap();
		json_map.put("ret", ret);
		json_map.put("code", code);
		try {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			writer = response.getWriter();
			writer.print(Json.toJson(json_map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "组合销售添加时获得商品全部价格", value = "/seller/getPrice.htm*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping("/seller/getPrice.htm")
	public void combin_getPrice(HttpServletRequest request,
			HttpServletResponse response, String other_ids, String main_goods_id) {
		double all_price = 0.00;
		if (!main_goods_id.equals("")) {
			Goods main = this.goodsService.getObjById(CommUtil
					.null2Long(main_goods_id));
			all_price = CommUtil.null2Double(main.getGoods_current_price());
		}
		if (other_ids != null && !other_ids.equals("")) {
			String ids[] = other_ids.split(",");
			for (String id : ids) {
				if (!id.equals("")) {
					Goods other = this.goodsService.getObjById(CommUtil
							.null2Long(id));
					all_price = all_price
							+ CommUtil.null2Double(other
									.getGoods_current_price());
				}
			}
		}
		try {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			writer = response.getWriter();
			writer.print(all_price);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "组合销售套餐", value = "/seller/combin_meal.htm*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping("/seller/combin_meal.htm")
	public ModelAndView combin_meal(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/combin_meal.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig config = this.configService.getSysConfig();
		if (config.getCombin_status() == 1) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = this.storeService.getObjById(user.getStore().getId());
			mv.addObject("store", store);
			mv.addObject("gold", user.getGold());
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启组合销售促销方式");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		return mv;
	}

	@SecurityMapping(title = "组合销售套餐保存", value = "/seller/combin_meal_buy.htm*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping("/seller/combin_meal_buy.htm")
	public void combin_meal_buy(HttpServletRequest request,
			HttpServletResponse response, String meal_day) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		int gold = user.getGold();
		int count = 0;
		boolean verify = true;
		int day = 30;
		if (meal_day.equals("a")) {
			count = 1;
		} else if (meal_day.equals("b")) {
			count = 2;
		} else if (meal_day.equals("c")) {
			count = 3;
		} else if (meal_day.equals("d")) {
			count = 6;
		} else if (meal_day.equals("e")) {
			count = 12;
		} else {
			verify = false;
		}
		day = day * count;
		int combin_gold = CommUtil.null2Int(count)
				* this.configService.getSysConfig().getCombin_amount();
		if (gold >= combin_gold && verify) {
			// 扣除用户金币
			user.setGold(gold - combin_gold);
			this.userService.update(user);
			
			// 记录金币日志
			GoldLog log = new GoldLog();
			log.setAddTime(new Date());
			log.setGl_content("购买组合销售套餐");
			log.setGl_count(combin_gold);
			log.setGl_user(user);
			log.setGl_type(-1);
			this.goldLogService.save(log);
			// 设置店铺的组合销售套餐信息
			Store store = user.getStore();
			Date meal_begin_time = null;
			Calendar cal = Calendar.getInstance();
		    meal_begin_time=store.getCombin_end_time();
			try {
			// 保存套餐购买信息
			SalesLog c_log = new SalesLog();
			c_log.setAddTime(new Date());
			if (meal_begin_time != null) {
				meal_begin_time=store.getCombin_end_time();
				if(meal_begin_time.after(new Date())){
					c_log.setBegin_time(meal_begin_time);
					c_log.setEnd_time(this.addDate(meal_begin_time, CommUtil
							.null2Long(day)));
				}else{
					c_log.setBegin_time(new Date());
					c_log.setEnd_time(this.addDate(new Date(), CommUtil
							.null2Long(day)));
				}	
			} else {
				c_log.setBegin_time(new Date());
				c_log.setEnd_time(this.addDate(new Date(), CommUtil
						.null2Long(day)));
			}
			c_log.setGold(combin_gold);
			c_log.setSales_info("套餐总时间增加" + day + "天");
			c_log.setStore_id(store.getId());
			c_log.setSales_type(1);// 促销类型为组合销售
			this.combinlogService.save(c_log);
			if(meal_begin_time!=null){
			if(meal_begin_time.after(new Date())){
				store.setCombin_end_time(
						this.addDate(meal_begin_time, CommUtil
								.null2Long(day)));
				}else{
					store.setCombin_end_time(
							this.addDate(new Date(), CommUtil
									.null2Long(day)));
				}
			}else{
				store.setCombin_end_time(
						this.addDate(new Date(), CommUtil
								.null2Long(day)));
			}
			this.storeService.update(store);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			try {
				writer = response.getWriter();
				writer.print("true");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			try {
				writer = response.getWriter();
				writer.print("false");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SecurityMapping(title = "组合销售套餐", value = "/seller/combin_meal_log.htm*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping("/seller/combin_meal_log.htm")
	public ModelAndView combin_meal_log(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/combin_meal_log.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		SalesLogQueryObject qo = new SalesLogQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.store_id", new SysMap("store_id", store.getId()), "=");
		qo.addQuery("obj.sales_type", new SysMap("sales_type", 1), "=");
		IPageList pList = this.saleslogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "组合套餐设置", value = "/seller/combin_set_goods.htm*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping("/seller/combin_set_goods.htm")
	public ModelAndView combin_set_goods(HttpServletRequest request,
			HttpServletResponse response, String type, String plan_count) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/combin_set_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("type", type);
		mv.addObject("plan_count", plan_count);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "组合套餐设置", value = "/seller/combin_set_goods_load.htm*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping("/seller/combin_set_goods_load.htm")
	public ModelAndView combin_set_goods_load(HttpServletRequest request,
			HttpServletResponse response, String goods_name,
			String currentPage, String type, String plan_count) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/combin_set_goods_load.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		GoodsQueryObject qo = new GoodsQueryObject();
		qo.setCurrentPage(CommUtil.null2Int(currentPage));
		if (!CommUtil.null2String(goods_name).equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%"
					+ CommUtil.null2String(goods_name) + "%"), "like");
		}
		qo.addQuery(
				"(obj.combin_suit_id is null or obj.combin_parts_id is null)",
				null);
		qo.addQuery("obj.goods_store.id",
				new SysMap("store_id", store.getId()), "=");
		List<String> params = new ArrayList<String>();
		params.add("combin_status");
		this.queryTools.shieldGoodsStatus(qo, params);
		qo.setPageSize(10);
		IPageList pList = this.goodsService.list(qo);
		String url = CommUtil.getURL(request)
				+ "/seller/combin_set_goods_load.htm";
		mv.addObject("objs", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url, "",
				pList.getCurrentPage(), pList.getPages()));
		mv.addObject("type", type);
		if (plan_count == null || plan_count.equals("")) {
			plan_count = "1";
		}
		mv.addObject("plan_count", plan_count);
		return mv;
	}

	/**
	 * 组合销售方案ajax保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "组合套餐设置", value = "/seller/combin_plan_save.htm*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping("/seller/combin_plan_save.htm")
	public void combin_plan_save(HttpServletRequest request,
			HttpServletResponse response, String plan_num,
			String main_goods_id, String beginTime, String endTime, String id,
			String combin_mark, String old_main_goods_id) {
		boolean ret = true;
		if (main_goods_id == null || main_goods_id.equals("")) {
			ret = false;
		}
		if (plan_num == null || plan_num.equals("")) {
			ret = false;
		}
		if (beginTime == null || beginTime.equals("") || endTime == null
				|| endTime.equals("")) {
			ret = false;
		}
		if (combin_mark == null || combin_mark.equals("")) {
			ret = false;
		} else {
			if (!combin_mark.equals("0") && !combin_mark.equals("1")) {
				ret = false;
			}
		}
		if (ret) {
			if (old_main_goods_id != null && !old_main_goods_id.equals("")) {
				Goods old_main_goods = this.goodsService.getObjById(CommUtil
						.null2Long(old_main_goods_id));
				old_main_goods.setCombin_parts_id(null);
				old_main_goods.setCombin_suit_id(null);
				old_main_goods.setCombin_status(0);
				this.goodsService.update(old_main_goods);
			}
			double all_price = 0.00;
			Goods main_goods = this.goodsService.getObjById(CommUtil
					.null2Long(main_goods_id));
			CombinPlan combinplan = null;
			if (id != null && !id.equals("")) {
				combinplan = this.combinplanService.getObjById(CommUtil
						.null2Long(id));
			} else {
				combinplan = new CombinPlan();
				combinplan.setAddTime(new Date());
				combinplan.setCombin_type(CommUtil.null2Int(combin_mark));
			}
			Map main_map = new HashMap();
			main_map.put("id", main_goods.getId());
			main_map.put("name", main_goods.getGoods_name());
			main_map.put("price", main_goods.getGoods_current_price());
			main_map.put("store_price", main_goods.getStore_price());
			main_map.put("inventory", main_goods.getGoods_inventory());
			String goods_domainPath = CommUtil.getURL(request) + "/goods_"
					+ main_goods.getId() + ".htm";
			if (this.configService.getSysConfig().isSecond_domain_open()
					&& main_goods.getGoods_store().getStore_second_domain() != ""
					&& main_goods.getGoods_type() == 1) {
				String store_second_domain = "http://"
						+ main_goods.getGoods_store().getStore_second_domain()
						+ "." + CommUtil.generic_domain(request);
				goods_domainPath = store_second_domain + "/goods_"
						+ main_goods.getId() + ".htm";
			}
			main_map.put("url", goods_domainPath);// 商品二级域名
			String img = this.configService.getSysConfig().getGoodsImage()
					.getPath()
					+ "/"
					+ this.configService.getSysConfig().getGoodsImage()
							.getName();
			if (main_goods.getGoods_main_photo() != null) {
				img = main_goods.getGoods_main_photo().getPath() + "/"
						+ main_goods.getGoods_main_photo().getName()
						+ "_small." + main_goods.getGoods_main_photo().getExt();
			}
			main_map.put("img", img);// 商品图片
			combinplan.setMain_goods_info(Json.toJson(main_map,
					JsonFormat.compact()));// 设置主体商品信息
			combinplan.setMain_goods_id(main_goods.getId());
			combinplan.setMain_goods_name(main_goods.getGoods_name());
			List plan_list = new ArrayList();
			String nums[] = plan_num.split(",");
			for (String count : nums) {
				all_price = CommUtil.null2Double(main_goods
						.getGoods_current_price());
				if (!count.equals("")) {
					String other_goods_ids = request
							.getParameter("other_goods_ids_" + count);
					String other_ids[] = other_goods_ids.split(",");
					List goods_list = new ArrayList();
					for (String other_id : other_ids) {
						if (!other_id.equals("")) {
							Goods obj = this.goodsService.getObjById(CommUtil
									.null2Long(other_id));
							all_price = all_price
									+ CommUtil.null2Double(obj
											.getGoods_current_price());
							Map temp_map = new HashMap();
							temp_map.put("id", obj.getId());
							temp_map.put("name", obj.getGoods_name());
							temp_map.put("price", obj.getGoods_current_price());
							temp_map.put("store_price", obj.getStore_price());
							temp_map.put("inventory", obj.getGoods_inventory());
							String goods_url = CommUtil.getURL(request)
									+ "/goods_" + obj.getId() + ".htm";
							if (this.configService.getSysConfig()
									.isSecond_domain_open()
									&& obj.getGoods_store()
											.getStore_second_domain() != ""
									&& obj.getGoods_type() == 1) {
								String store_second_domain = "http://"
										+ obj.getGoods_store()
												.getStore_second_domain() + "."
										+ CommUtil.generic_domain(request);
								goods_url = store_second_domain + "/goods_"
										+ obj.getId() + ".htm";
							}
							temp_map.put("url", goods_url);
							String img2 = this.configService.getSysConfig()
									.getGoodsImage().getPath()
									+ "/"
									+ this.configService.getSysConfig()
											.getGoodsImage().getName();
							if (obj.getGoods_main_photo() != null) {
								img2 = obj.getGoods_main_photo().getPath()
										+ "/"
										+ obj.getGoods_main_photo().getName()
										+ "_small."
										+ obj.getGoods_main_photo().getExt();
							}
							temp_map.put("img", img2);// 商品图片
							goods_list.add(temp_map);
						}
					}
					Map combin_goods_map = new HashMap();
					combin_goods_map.put("goods_list", goods_list);
					combin_goods_map.put("plan_goods_price",
							request.getParameter("combin_price_" + count));
					combin_goods_map.put("all_goods_price",
							CommUtil.formatMoney(all_price));
					plan_list.add(combin_goods_map);
				}
			}
			String plan_list_json = Json
					.toJson(plan_list, JsonFormat.compact());
			combinplan.setCombin_plan_info(plan_list_json);// 组合信息
			combinplan.setCombin_status(0);// 待审核
			combinplan.setBeginTime(CommUtil.formatDate(beginTime));
			combinplan.setEndTime(CommUtil.formatDate(endTime));
			combinplan.setCombin_form(1);// 商家组合
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			combinplan.setStore_id(store.getId());
			if (id != null && !id.equals("")) {
				this.combinplanService.update(combinplan);
			} else {
				this.combinplanService.save(combinplan);
			}
			main_goods.setCombin_status(1);// 主商品组合状态待审核
			if (combinplan.getCombin_type() == 0) {
				main_goods.setCombin_suit_id(combinplan.getId());
			} else {
				main_goods.setCombin_parts_id(combinplan.getId());
			}
			this.goodsService.update(main_goods);
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			try {
				writer = response.getWriter();
				writer.print("true");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			try {
				writer = response.getWriter();
				writer.print("false");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SecurityMapping(title = "组合销售商品编辑", value = "/seller/combin_plan_edit.htm*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping("/seller/combin_plan_edit.htm")
	public ModelAndView combin_plan_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "您所访问的地址不存在");
		mv.addObject("url", CommUtil.getURL(request) + "/seller/combin.htm");
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (id != null && !id.equals("")) {
			CombinPlan obj = this.combinplanService.getObjById(CommUtil
					.null2Long(id));
			if (obj != null) {
				if (CommUtil.null2String(store.getId()).equals(
						CommUtil.null2String(obj.getStore_id()))) {
					mv = new JModelAndView(
							"user/default/sellercenter/combin_add.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
					mv.addObject("edit", true);
					mv.addObject("obj", obj);
					mv.addObject("combinTools", combinTools);
				}
			}
		}
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param bargain_time
	 * @return
	 */
	@SecurityMapping(title = "组合销售删除", value = "/seller/combin_plan_delete.htm*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping("/seller/combin_plan_delete.htm")
	public String combin_plan_delete(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String type) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (id != null && !id.equals("")) {
			CombinPlan obj = this.combinplanService.getObjById(CommUtil
					.null2Long(id));
			if (obj != null) {
				if (CommUtil.null2String(store.getId()).equals(
						CommUtil.null2String(obj.getStore_id()))) {
					Goods goods = this.goodsService.getObjById(obj
							.getMain_goods_id());
					goods.setCombin_status(0);// 无组合销售促销活动
					if (obj.getCombin_type() == 0
							&& goods.getCombin_suit_id() != null) {
						if (goods.getCombin_suit_id().equals(obj.getId())) {
							goods.setCombin_suit_id(null);
						}
					} else if (obj.getCombin_type() == 1
							&& goods.getCombin_parts_id() != null) {
						if (goods.getCombin_parts_id().equals(obj.getId())) {
							goods.setCombin_parts_id(null);
						}
					}
					boolean ret = this.goodsService.update(goods);
					if (ret) {
						this.combinplanService.delete(CommUtil.null2Long(id));
					}
				}
			}
		}
		return "redirect:/seller/combin.htm?currentPage=" + currentPage
				+ "&type=" + type;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param bargain_time
	 * @return
	 */
	@SecurityMapping(title = "组合销售上架下架", value = "/seller/combin_plan_switch.htm*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping("/seller/combin_plan_switch.htm")
	public String combin_plan_switch(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (id != null && !id.equals("")) {
			CombinPlan obj = this.combinplanService.getObjById(CommUtil
					.null2Long(id));
			if (obj != null) {
				if (CommUtil.null2String(store.getId()).equals(
						CommUtil.null2String(obj.getStore_id()))) {
					if (obj.getCombin_status() == -5) {
						obj.setCombin_status(1);
					} else if (obj.getCombin_status() == 1) {
						obj.setCombin_status(-5);
					}
					this.combinplanService.update(obj);
				}
			}
		}
		return "redirect:/seller/combin.htm?currentPage=" + currentPage;
	}
	public static Date addDate(Date d, long day) throws ParseException {
		long time = d.getTime();
		day = day * 24 * 60 * 60 * 1000;
		time += day;
		return new Date(time);

	}
}
