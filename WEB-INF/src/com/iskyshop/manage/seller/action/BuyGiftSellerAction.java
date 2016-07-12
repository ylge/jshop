package com.iskyshop.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.BuyGift;
import com.iskyshop.foundation.domain.GoldLog;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.SalesLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.BuyGiftQueryObject;
import com.iskyshop.foundation.domain.query.SalesLogQueryObject;
import com.iskyshop.foundation.service.IBuyGiftService;
import com.iskyshop.foundation.service.IGoldLogService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISalesLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.OrderFormTools;

/**
 * 
 * <p>
 * Title: BuyGiftSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商家后台满就送管理控制器
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
 * @date 2014-9-25
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class BuyGiftSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IBuyGiftService buygiftService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoldLogService goldLogService;
	@Autowired
	private ISalesLogService salesLogService;
	@Autowired
	private OrderFormTools orderFormTools;

	/**
	 * 满就送列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "商家中心", value = "/seller/buygift_list.htm*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping("/seller/buygift_list.htm")
	public ModelAndView buygift_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String gift_status, String beginTime,
			String endTime) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/buygift_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		Store store = SecurityUserHolder.getCurrentUser().getStore();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		BuyGiftQueryObject qo = new BuyGiftQueryObject(currentPage, mv,
				orderBy, orderType);
		if (gift_status != null && !gift_status.equals("")) {
			qo.addQuery("obj.gift_status",
					new SysMap("gift_status", CommUtil.null2Int(gift_status)),
					"=");
		}
		if (beginTime != null && !beginTime.equals("")) {
			qo.addQuery("obj.beginTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
		}
		if (endTime != null && !endTime.equals("")) {
			qo.addQuery("obj.endTime",
					new SysMap("endTime", CommUtil.formatDate(endTime)), "<=");
		}
		qo.addQuery("obj.gift_type", new SysMap("gift_type", 1), "=");
		qo.addQuery("obj.store_id", new SysMap("store_id", store.getId()), "=");
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, BuyGift.class, mv);
		IPageList pList = this.buygiftService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/seller/buygift_list.htm",
				"", params, pList, mv);
		return mv;
	}

	@SecurityMapping(title = "满就送添加", value = "/seller/buygift_add.htm*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping("/seller/buygift_add.htm")
	public ModelAndView buygift_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/buygift_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (store.getBuygift_meal_endTime() == null) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您尚未购买满就送套餐");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/buygift_meal.htm");
			return mv;
		}
		if (store.getBuygift_meal_endTime().before(new Date())) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您的满就送套餐已到期");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/buygift_meal.htm");
			return mv;
		}
		Map params = new HashMap();
		params.put("sid", user.getStore().getId());
		List<BuyGift> bgs = this.buygiftService
				.query("select obj from BuyGift obj where obj.store_id=:sid and (obj.gift_status=10 or obj.gift_status=0)",
						params, -1, -1);
		if (bgs.size() > 0) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您当前有正在审核或进行的满就送");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/buygift_list.htm");
			return mv;
		}
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "满就送重新申请", value = "/seller/buygift_edit.htm*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping("/seller/buygift_edit.htm")
	public ModelAndView buygift_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/buygift_edit.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			BuyGift buygift = this.buygiftService
					.getObjById(Long.parseLong(id));
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			if (!buygift.getStore_id().equals(store.getId())) {
				mv = new JModelAndView(
						"user/default/sellercenter/seller_error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "您没有此买就送活动");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/buygift_list.htm");
				return mv;
			}
			List<Map> goodses = Json.fromJson(List.class,
					buygift.getGoods_info());
			for (Map map : goodses) {
				Goods goods = this.goodsService.getObjById(CommUtil
						.null2Long(map.get("goods_id")));
				int inventory = 0;
				if (goods != null) {
					inventory = goods.getGoods_inventory();
				}
				map.put("store_inventory", inventory);
			}
			List<Map> gifts = Json.fromJson(List.class, buygift.getGift_info());
			for (Map map : gifts) {
				Goods goods = this.goodsService.getObjById(CommUtil
						.null2Long(map.get("goods_id")));
				int inventory = 0;
				if (goods != null) {
					inventory = goods.getGoods_inventory();
				}
				map.put("store_inventory", inventory);
			}
			mv.addObject("gifts", gifts);
			mv.addObject("goodses", goodses);
			mv.addObject("obj", buygift);
		}
		mv.addObject("orderFormTools", orderFormTools);
		return mv;
	}

	@SecurityMapping(title = "详情", value = "/seller/buygift_info.htm*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping("/seller/buygift_info.htm")
	public ModelAndView buygift_info(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/buygift_info.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			BuyGift buygift = this.buygiftService
					.getObjById(Long.parseLong(id));
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			if (!buygift.getStore_id().equals(store.getId())) {
				mv = new JModelAndView(
						"user/default/sellercenter/seller_error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "您没有此买就送活动");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/buygift_list.htm");
				return mv;
			}
			List<Map> goodses = Json.fromJson(List.class,
					buygift.getGoods_info());
			for (Map map : goodses) {
				Goods goods = this.goodsService.getObjById(CommUtil
						.null2Long(map.get("goods_id")));
				int inventory = 0;
				if (goods != null) {
					inventory = goods.getGoods_inventory();
				}
				map.put("store_inventory", inventory);
			}
			List<Map> gifts = Json.fromJson(List.class, buygift.getGift_info());
			for (Map map : gifts) {
				Goods goods = this.goodsService.getObjById(CommUtil
						.null2Long(map.get("goods_id")));
				int inventory = 0;
				if (goods != null) {
					inventory = goods.getGoods_inventory();
				}
				map.put("store_inventory", inventory);
			}
			mv.addObject("gifts", gifts);
			mv.addObject("goodses", goodses);
			mv.addObject("obj", buygift);
		}
		mv.addObject("orderFormTools", orderFormTools);
		return mv;
	}

	@SecurityMapping(title = "满就送保存", value = "/seller/buygift_save.htm*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping("/seller/buygift_save.htm")
	public void buygift_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String goods_ids, String gift_ids) {
		Map json = new HashMap();
		json.put("ret", false);
		WebForm wf = new WebForm();
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getBuygift_meal_endTime().before(new Date())) {
			json.put("op_title", "您的满就送套餐已到期");
			json.put("url", CommUtil.getURL(request)
					+ "/seller/buygift_meal.htm");
			this.return_json(Json.toJson(json, JsonFormat.compact()), response);
		} else {
			Map params = new HashMap();
			params.put("sid", user.getStore().getId());
			boolean ret = true;
			List<BuyGift> bgs = this.buygiftService
					.query("select obj from BuyGift obj where obj.store_id=:sid and (obj.gift_status=10 or obj.gift_status=0)",
							params, -1, -1);
			if (bgs.size() > 0) {
				json.put("op_title", "您当前有正在进行的满就送");
				json.put("url", CommUtil.getURL(request)
						+ "/seller/buygift_list.htm");
				this.return_json(Json.toJson(json, JsonFormat.compact()),
						response);
			} else {
				String[] ids = goods_ids.split(",");
				String[] gids = gift_ids.split(",");
				Set<String> ids_set = new TreeSet();
				ids_set.addAll(Arrays.asList(ids));
				Set<String> gids_set = new TreeSet();
				gids_set.addAll(Arrays.asList(gids));
				for (String goods_id : ids_set) {
					for (String gift_id : gids_set) {
						if (goods_id.equals(gift_id)) {
							ret = false;
						}
					}
				}
				Set<String> validate_ids = new TreeSet();
				validate_ids.addAll(Arrays.asList(ids));
				validate_ids.addAll(Arrays.asList(gids));
				for (String vid : validate_ids) {
					Goods v_goods = this.goodsService.getObjById(CommUtil
							.null2Long(vid));
					String goods_store_id = v_goods.getGoods_store().getId()
							.toString();
					String user_store_id = user.getStore().getId().toString();
					if (v_goods.getGoods_status() == 0
							&& v_goods.getGroup_buy() == 0
							&& v_goods.getOrder_enough_give_status() == 0
							&& v_goods.getOrder_enough_if_give() == 0
							&& v_goods.getEnough_reduce() == 0
							&& !goods_store_id.equals(user_store_id)) {
						ret = false;
					}
				}
				if (ret) {
					BuyGift buygift = null;
					if (id.equals("")) {
						buygift = wf.toPo(request, BuyGift.class);
						buygift.setAddTime(new Date());
					} else {
						BuyGift obj = this.buygiftService.getObjById(Long
								.parseLong(id));
						buygift = (BuyGift) wf.toPo(request, obj);
					}
					List<Map> goodses = new ArrayList<Map>();
					// 更新商品为参加满就送商品
					for (Object goods_id : ids_set) {
						Goods goods = this.goodsService.getObjById(CommUtil
								.null2Long(goods_id));
						goods.setOrder_enough_give_status(1);
						goods.setBuyGift_amount(BigDecimal.valueOf(CommUtil
								.null2Double(request
										.getParameter("condition_amount"))));
						this.goodsService.update(goods);
						Map map = new HashMap();
						map.put("goods_id", goods.getId());
						map.put("goods_name", goods.getGoods_name());
						map.put("goods_main_photo", goods.getGoods_main_photo()
								.getPath()
								+ "/"
								+ goods.getGoods_main_photo().getName()
								+ "_small."
								+ goods.getGoods_main_photo().getExt());
						map.put("big_goods_main_photo", goods
								.getGoods_main_photo().getPath()
								+ "/"
								+ goods.getGoods_main_photo().getName());
						map.put("goods_price", goods.getGoods_current_price());
						String goods_domainPath = CommUtil.getURL(request)
								+ "/goods_" + goods.getId() + ".htm";
						String store_domainPath = CommUtil.getURL(request)
								+ "/store_" + goods.getGoods_store().getId()
								+ ".htm";
						if (this.configService.getSysConfig()
								.isSecond_domain_open()
								&& goods.getGoods_store()
										.getStore_second_domain() != ""
								&& goods.getGoods_type() == 1) {
							String store_second_domain = "http://"
									+ goods.getGoods_store()
											.getStore_second_domain() + "."
									+ CommUtil.generic_domain(request);
							goods_domainPath = store_second_domain + "/goods_"
									+ goods.getId() + ".htm";
							store_domainPath = store_second_domain;
						}
						map.put("goods_domainPath", goods_domainPath);// 商品二级域名路径
						map.put("store_domainPath", store_domainPath);// 店铺二级域名路径
						goodses.add(map);
					}
					buygift.setGoods_info(Json.toJson(goodses,
							JsonFormat.compact()));
					// 更新商品为赠送商品
					List<Map> gifts = new ArrayList<Map>();
					for (Object gift_id : gids_set) {
						Goods goods = this.goodsService.getObjById(CommUtil
								.null2Long(gift_id));
						int count = CommUtil.null2Int(request
								.getParameter("gift_" + goods.getId()));
						goods.setOrder_enough_if_give(1);
						Map map = new HashMap();
						if (count >= goods.getGoods_inventory()) {
							map.put("storegoods_count", 1);
						} else {
							map.put("storegoods_count", 0);
							map.put("goods_count", count);
						}
						map.put("goods_id", goods.getId());
						map.put("goods_name", goods.getGoods_name());
						map.put("goods_main_photo", goods.getGoods_main_photo()
								.getPath()
								+ "/"
								+ goods.getGoods_main_photo().getName()
								+ "_small."
								+ goods.getGoods_main_photo().getExt());
						map.put("big_goods_main_photo", goods
								.getGoods_main_photo().getPath()
								+ "/"
								+ goods.getGoods_main_photo().getName());
						map.put("goods_price", goods.getGoods_current_price());
						String goods_domainPath = CommUtil.getURL(request)
								+ "/goods_" + goods.getId() + ".htm";
						String store_domainPath = CommUtil.getURL(request)
								+ "/store_" + goods.getGoods_store().getId()
								+ ".htm";
						if (this.configService.getSysConfig()
								.isSecond_domain_open()
								&& goods.getGoods_store()
										.getStore_second_domain() != ""
								&& goods.getGoods_type() == 1) {
							String store_second_domain = "http://"
									+ goods.getGoods_store()
											.getStore_second_domain() + "."
									+ CommUtil.generic_domain(request);
							goods_domainPath = store_second_domain + "/goods_"
									+ goods.getId() + ".htm";
							store_domainPath = store_second_domain;
						}
						map.put("goods_domainPath", goods_domainPath);// 商品二级域名路径
						map.put("store_domainPath", store_domainPath);// 店铺二级域名路径
						goods.setBuyGift_amount(BigDecimal.valueOf(CommUtil
								.null2Double(request
										.getParameter("condition_amount"))));
						this.goodsService.update(goods);
						gifts.add(map);
					}
					buygift.setGift_info(Json.toJson(gifts,
							JsonFormat.compact()));
					buygift.setGift_status(0);
					buygift.setGift_type(1);
					buygift.setStore_id(user.getStore().getId());
					buygift.setStore_name(user.getStore().getStore_name());
					if (id.equals("")) {
						this.buygiftService.save(buygift);
					} else
						this.buygiftService.update(buygift);
					json.put("ret", true);
					json.put("op_title", "申请满就送成功");
					json.put("url", CommUtil.getURL(request)
							+ "/seller/buygift_list.htm");
					this.return_json(Json.toJson(json, JsonFormat.compact()),
							response);
				} else {
					json.put("op_title", "一个商品只能参加一个促销活动，申请满就送失败");
					json.put("url", CommUtil.getURL(request)
							+ "/seller/buygift_list.htm");
					this.return_json(Json.toJson(json, JsonFormat.compact()),
							response);
				}
			}
		}
	}

	@SecurityMapping(title = "满就送添加", value = "/seller/buy_goods_seller.htm*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping("/seller/buy_goods_seller.htm")
	public ModelAndView buy_goods_seller(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/buy_goods_seller.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		return mv;
	}

	@SecurityMapping(title = "满就送添加", value = "/seller/buy_gift_seller.htm*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping("/seller/buy_gift_seller.htm")
	public ModelAndView buy_gift_seller(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/buy_gift_seller.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		return mv;
	}

	@SecurityMapping(title = "满就送商品加载", value = "/seller/buy_goods_seller_load.htm*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping("/seller/buy_goods_seller_load.htm")
	public void buy_goods_seller_load(HttpServletRequest request,
			HttpServletResponse response, String goods_name, String gc_id,
			String goods_ids) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		boolean ret = true;
		Map params = new HashMap();
		params.put("goods_name", "%" + goods_name.trim() + "%");
		params.put("group_buy", 0);
		params.put("goods_type", 1);
		params.put("goods_status", 0);
		params.put("activity_status", 0);
		params.put("order_enough_give_status", 0);
		params.put("order_enough_if_give", 0);
		params.put("enough_reduce", 0);
		params.put("f_sale_type", 0);
		params.put("advance_sale_type", 0);
		params.put("store_id", user.getStore().getId());
		params.put("combin_status", 0);
		String query = "select obj from Goods obj where obj.goods_name like:goods_name and obj.goods_store.id=:store_id and obj.enough_reduce=:enough_reduce and obj.order_enough_if_give=:order_enough_if_give and obj.order_enough_give_status=:order_enough_give_status and obj.group_buy=:group_buy and obj.goods_status=:goods_status and obj.goods_type=:goods_type and obj.activity_status=:activity_status and obj.advance_sale_type=:advance_sale_type and obj.f_sale_type=:f_sale_type and obj.combin_status=:combin_status";
		if (gc_id != null && !gc_id.equals("")) {
			GoodsClass gc = this.goodsClassService.getObjById(CommUtil
					.null2Long(gc_id));
			Set<Long> ids = this.genericGcIds(gc);
			params.put("ids", ids);
			query = query + " and obj.gc.id in (:ids)";
		}
		String[] ids = goods_ids.split(",");
		List ids_list = Arrays.asList(ids);
		List<Goods> goods = this.goodsService.query(query, params, -1, -1);
		List<Map> list = new ArrayList<Map>();
		for (Goods obj : goods) {
			if (!ids_list.contains(obj.getId().toString())) {
				Map map = new HashMap();
				map.put("id", obj.getId());
				map.put("store_price", obj.getStore_price());
				map.put("goods_name", obj.getGoods_name());
				map.put("store_inventory", obj.getGoods_inventory());
				if (obj.getGoods_main_photo() != null) {
					map.put("img", obj.getGoods_main_photo().getPath() + "/"
							+ obj.getGoods_main_photo().getName() + "_small."
							+ obj.getGoods_main_photo().getExt());
				} else {
					map.put("img", this.configService.getSysConfig()
							.getGoodsImage().getPath()
							+ "/"
							+ this.configService.getSysConfig().getGoodsImage()
									.getName());
				}
				list.add(map);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(list, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 满就送套餐
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "商家中心", value = "/seller/buygift_meal.htm*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping("/seller/buygift_meal.htm")
	public ModelAndView buygift_meal(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/buygift_meal.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		mv.addObject("user", user);
		return mv;
	}

	@SecurityMapping(title = "商家中心", value = "/seller/buygift_meal_save.htm*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping("/seller/buygift_meal_save.htm")
	public void buygift_meal_save(HttpServletRequest request,
			HttpServletResponse response, String meal_day)
			throws ParseException {
		Map json = new HashMap();
		json.put("ret", false);
		if (configService.getSysConfig().getBuygift_status() == 1) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			int cost = configService.getSysConfig().getBuygift_meal_gold();
			int days = 30;
			if (meal_day.equals("30")) {
				days = 30;
			}
			if (meal_day.equals("90")) {
				days = 90;
			}
			if (meal_day.equals("180")) {
				days = 180;
			}
			if (meal_day.equals("360")) {
				days = 360;
			}
			int costday = days / 30;
			if (user.getGold() >= costday * cost) {
				user.setGold(user.getGold() - costday * cost);
				this.userService.update(user);
				Date day = user.getStore().getBuygift_meal_endTime();
				Date d = new Date();
				if (day != null) {
					if (day.after(new Date())) {
						// 记录金币日志
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_content("购买满就送套餐");
						log.setGl_count(costday * cost);
						log.setGl_user(user);
						log.setGl_type(-1);
						this.goldLogService.save(log);
						// 保存套餐购买信息
						SalesLog c_log = new SalesLog();
						c_log.setAddTime(new Date());
						c_log.setBegin_time(user.getStore()
								.getBuygift_meal_endTime());
						c_log.setEnd_time(this.addDate(user.getStore()
								.getBuygift_meal_endTime(), CommUtil
								.null2Long(days)));
						c_log.setGold(costday * cost);
						c_log.setSales_info("套餐总时间增加" + days + "天");
						c_log.setStore_id(user.getStore().getId());
						c_log.setSales_type(2);// 促销类型为满就送
						this.salesLogService.save(c_log);
						//计算时间
						user.getStore().setBuygift_meal_endTime(
								this.addDate(user.getStore()
										.getBuygift_meal_endTime(), CommUtil
										.null2Long(days)));
						this.storeService.update(user.getStore());
					} else {
						Calendar ca = Calendar.getInstance();
						ca.add(ca.DATE, days);
						SimpleDateFormat bartDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						String latertime = bartDateFormat.format(ca.getTime());
						user.getStore().setBuygift_meal_endTime(
								CommUtil.formatDate(latertime,
										"yyyy-MM-dd HH:mm:ss"));
						this.storeService.update(user.getStore());
						// 记录金币日志
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_content("购买满就送套餐");
						log.setGl_count(costday * cost);
						log.setGl_user(user);
						log.setGl_type(-1);
						this.goldLogService.save(log);
						// 保存套餐购买信息
						SalesLog c_log = new SalesLog();
						c_log.setAddTime(new Date());
						c_log.setBegin_time(new Date());
						c_log.setEnd_time(CommUtil.formatDate(latertime,
								"yyyy-MM-dd HH:mm:ss"));
						c_log.setGold(costday * cost);
						c_log.setSales_info("套餐总时间增加" + days + "天");
						c_log.setStore_id(user.getStore().getId());
						c_log.setSales_type(2);// 促销类型为满就送
						this.salesLogService.save(c_log);
					}
				} else {
					Calendar ca = Calendar.getInstance();
					ca.add(ca.DATE, days);
					SimpleDateFormat bartDateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String latertime = bartDateFormat.format(ca.getTime());
					user.getStore().setBuygift_meal_endTime(
							CommUtil.formatDate(latertime,
									"yyyy-MM-dd HH:mm:ss"));
					this.storeService.update(user.getStore());
					// 记录金币日志
					GoldLog log = new GoldLog();
					log.setAddTime(new Date());
					log.setGl_content("购买满就送套餐");
					log.setGl_count(costday * cost);
					log.setGl_user(user);
					log.setGl_type(-1);
					this.goldLogService.save(log);
					// 保存套餐购买信息
					SalesLog c_log = new SalesLog();
					c_log.setAddTime(new Date());
					c_log.setBegin_time(new Date());
					c_log.setEnd_time(CommUtil.formatDate(latertime,
							"yyyy-MM-dd HH:mm:ss"));
					c_log.setGold(costday * cost);
					c_log.setSales_info("套餐总时间增加" + days + "天");
					c_log.setStore_id(user.getStore().getId());
					c_log.setSales_type(2);// 促销类型为满就送
					this.salesLogService.save(c_log);
				}
				json.put("ret", true);
				json.put("op_title", "购买成功");
				json.put("url", CommUtil.getURL(request)
						+ "/seller/buygift_list.htm");
				this.return_json(Json.toJson(json, JsonFormat.compact()),
						response);
			} else {
				json.put("op_title", "您的金币不足，无法购买满就送套餐");
				json.put("url", CommUtil.getURL(request)
						+ "/seller/buygift_list.htm");
				this.return_json(Json.toJson(json, JsonFormat.compact()),
						response);
			}
		} else {
			json.put("op_title", "购买失败");
			json.put("url", CommUtil.getURL(request)
					+ "/seller/buygift_list.htm");
			this.return_json(Json.toJson(json, JsonFormat.compact()), response);
		}
	}

	public static Date addDate(Date d, long day) throws ParseException {
		long time = d.getTime();
		day = day * 24 * 60 * 60 * 1000;
		time += day;
		return new Date(time);

	}

	@SecurityMapping(title = "满就送销售套餐日志", value = "/seller/buygift_meal_log.htm*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping("/seller/buygift_meal_log.htm")
	public ModelAndView buygift_meal_log(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/buygift_meal_log.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		SalesLogQueryObject qo = new SalesLogQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.store_id", new SysMap("store_id", store.getId()), "=");
		qo.addQuery("obj.sales_type", new SysMap("sales_type", 2), "=");
		IPageList pList = this.salesLogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	/**
	 * 赠品数量调整
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param store_id
	 */
	@RequestMapping("/seller/gift_count_adjust.htm")
	public void gift_count_adjust(HttpServletRequest request,
			HttpServletResponse response, String gid, String count) {
		String code = "100";// 100表示修改成功，200表示库存不足
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(gid));
		if (goods != null) {
			if (CommUtil.null2Int(count) > goods.getGoods_inventory()) {
				count = goods.getGoods_inventory() + "";
				code = "200";
			}
		}
		Map map = new HashMap();
		map.put("count", count);
		map.put("code", code);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			// System.out.println(Json.toJson(map, JsonFormat.compact()));
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "满就送商品删除", value = "/seller/buygift_del.htm*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping("/seller/buygift_del.htm")
	public String buygift_del(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		BuyGift bg = this.buygiftService.getObjById(CommUtil.null2Long(id));
		if (bg != null && bg.getStore_id().equals(user.getStore().getId())
				&& (bg.getGift_status() == -10 || bg.getGift_status() == 20)) {
			List<Map> maps = Json.fromJson(List.class, bg.getGift_info());
			maps.addAll(Json.fromJson(List.class, bg.getGoods_info()));
			for (Map map : maps) {
				Goods goods = this.goodsService.getObjById(CommUtil
						.null2Long(map.get("goods_id")));
				if (goods != null) {
					goods.setOrder_enough_give_status(0);
					goods.setOrder_enough_if_give(0);
					goods.setBuyGift_id(null);
					this.goodsService.update(goods);
				}
			}
			this.buygiftService.delete(bg.getId());
		}
		return "redirect:buygift_list.htm";
	}

	@SecurityMapping(title = "满就送商品停止", value = "/seller/buygift_stop.htm*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping("/seller/buygift_stop.htm")
	public String buygift_stop(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		BuyGift bg = this.buygiftService.getObjById(CommUtil.null2Long(id));
		if (bg != null && bg.getStore_id().equals(user.getStore().getId())
				&& bg.getGift_status() == 10) {
			bg.setGift_status(20);
			this.buygiftService.update(bg);
			List<Map> maps = Json.fromJson(List.class, bg.getGift_info());
			maps.addAll(Json.fromJson(List.class, bg.getGoods_info()));
			for (Map map : maps) {
				Goods goods = this.goodsService.getObjById(CommUtil
						.null2Long(map.get("goods_id")));
				if (goods != null) {
					goods.setOrder_enough_give_status(0);
					goods.setOrder_enough_if_give(0);
					goods.setBuyGift_id(null);
					goods.setBuyGift_amount(new BigDecimal(0.00));
					this.goodsService.update(goods);
				}
			}
		}
		return "redirect:buygift_list.htm";
	}

	private Set<Long> genericGcIds(GoodsClass gc) {
		Set<Long> ids = new HashSet<Long>();
		if (gc != null) {
			ids.add(gc.getId());
			for (GoodsClass child : gc.getChilds()) {
				Set<Long> cids = genericGcIds(child);
				for (Long cid : cids) {
					ids.add(cid);
				}
				ids.add(child.getId());
			}
		}
		return ids;
	}

	public void return_json(String json, HttpServletResponse response) {
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
}
