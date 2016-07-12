package com.iskyshop.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
import com.iskyshop.foundation.domain.EnoughReduce;
import com.iskyshop.foundation.domain.GoldLog;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.SalesLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.EnoughReduceQueryObject;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.domain.query.SalesLogQueryObject;
import com.iskyshop.foundation.service.IEnoughReduceService;
import com.iskyshop.foundation.service.IGoldLogService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISalesLogService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.QueryTools;
import com.iskyshop.manage.admin.tools.StoreTools;
import com.iskyshop.view.web.tools.GoodsViewTools;

/**
 * 
 * <p>
 * Title: EnoughReduceSellerManageAction.java
 * </p>
 * 
 * <p>
 * Description: 卖家满就减控制器
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
 * @date 2014-9-22
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class EnoughReduceSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IEnoughReduceService enoughreduceService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoldLogService goldLogService;
	@Autowired
	private ISalesLogService salesLogService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private QueryTools queryTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private StoreTools storeTools;

	/**
	 * EnoughReduce列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商家满就减活动列表", value = "/seller/enoughreduce_list.htm*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping("/seller/enoughreduce_list.htm")
	public ModelAndView enoughreduce_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String ertitle,
			String erstatus, String erbegin_time, String erend_time) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/enoughreduce_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		Store store = SecurityUserHolder.getCurrentUser().getStore();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		EnoughReduceQueryObject qo = new EnoughReduceQueryObject(currentPage,
				mv, "addTime", "desc");
		qo.addQuery("obj.store_id", new SysMap("store_id", store.getId()
				.toString()), "=");

		if (erbegin_time != null && !erbegin_time.equals("")
				&& erend_time != null && !erend_time.equals("")) {
			qo.addQuery("DATE_FORMAT(obj.erbegin_time,'%Y-%m-%d')", new SysMap(
					"erbegin_time", erbegin_time), ">=");
			qo.addQuery("DATE_FORMAT(obj.erend_time,'%Y-%m-%d')", new SysMap(
					"erend_time", erend_time), "<=");
		}

		mv.addObject("erbegin_time", erbegin_time);
		mv.addObject("erend_time", erend_time);

		if (ertitle != null && !"".equals(ertitle)) {
			qo.addQuery("obj.ertitle", new SysMap("ertitle", "%" + ertitle
					+ "%"), "like");
			mv.addObject("ertitle", ertitle);
		}
		if (erstatus != null && !"".equals(erstatus)) {
			qo.addQuery("obj.erstatus",
					new SysMap("erstatus", CommUtil.null2Int(erstatus)), "=");
			mv.addObject("erstatus", erstatus);
		}

		IPageList pList = this.enoughreduceService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/seller/enoughreduce_list.htm", "", params, pList, mv);
		return mv;
	}
	/**
	 * enoughreduce添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "商家满就减活动添加", value = "/seller/enoughreduce_add.htm*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping("/seller/enoughreduce_add.htm")
	public ModelAndView enoughreduce_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/enoughreduce_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (store.getEnoughreduce_meal_endTime() == null) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您尚未购买满就减套餐");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/buygift_meal.htm");
			return mv;
		}
		if (store.getEnoughreduce_meal_endTime().before(new Date())) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您的满就减套餐已到期");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/enoughreduce_meal.htm");
			return mv;
		}
		Map params = new HashMap();
		params.put("sid", store.getId().toString());
		List<EnoughReduce> er = this.enoughreduceService
				.query("select obj from EnoughReduce obj where obj.store_id=:sid and (obj.erstatus=10 or obj.erstatus=5)",
						params, -1, -1);
		for (EnoughReduce enoughReduce : er) {
			if (enoughReduce.getErend_time().before(new Date())) {
				enoughReduce.setErstatus(20);
			}
			this.enoughreduceService.update(enoughReduce);
		}
		er = this.enoughreduceService
				.query("select obj from EnoughReduce obj where obj.store_id=:sid and (obj.erstatus=10 or obj.erstatus=5)",
						params, -1, -1);
		if (er.size() >= this.configService.getSysConfig()
				.getEnoughreduce_max_count()) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您当前正在审核或进行的满就减超过了规定的最大值");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/enoughreduce_list.htm");
			return mv;
		}
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * enoughreduce编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "满就减活动修改", value = "/seller/enoughreduce_edit.htm*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping("/seller/enoughreduce_edit.htm")
	public ModelAndView enoughreduce_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/enoughreduce_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			EnoughReduce enoughreduce = this.enoughreduceService
					.getObjById(Long.parseLong(id));
			if (enoughreduce.getErstatus() > 0) {
				mv = new JModelAndView(
						"user/default/sellercenter/seller_error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "该活动不可编辑");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/enoughreduce_list.htm");
				return mv;
			}
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			if (enoughreduce.getStore_id().equals("" + store.getId())) {
				mv.addObject("obj", enoughreduce);
				mv.addObject("currentPage", currentPage);
				mv.addObject("edit", true);
			} else {
				mv = new JModelAndView(
						"user/default/sellercenter/seller_error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "您的店铺中没有该活动");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/enoughreduce_list.htm");
				return mv;
			}

		}
		return mv;
	}
	/**
	 * enoughreduce保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "商家满就减活动保存", value = "/seller/enoughreduce_save.htm*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping("/seller/enoughreduce_save.htm")
	public void enoughreduce_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String url, String add_url, String count) {
		Map map = new HashMap();
		map.put("ret", false);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (store.getEnoughreduce_meal_endTime().before(new Date())) {
			map.put("op_title", "您的满就减套餐已到期");
		}
		Map params = new HashMap();
		params.put("sid", store.getId().toString());
		List<EnoughReduce> er = this.enoughreduceService
				.query("select obj from EnoughReduce obj where obj.store_id=:sid and (obj.erstatus=10 or obj.erstatus=5)",
						params, -1, -1);
		for (EnoughReduce enoughReduce : er) {
			if (enoughReduce.getErend_time().before(new Date())) {
				enoughReduce.setErstatus(20);
			}
			this.enoughreduceService.update(enoughReduce);
		}
		er = this.enoughreduceService
				.query("select obj from EnoughReduce obj where obj.store_id=:sid and (obj.erstatus=10 or obj.erstatus=5)",
						params, -1, -1);
		if (er.size() >= this.configService.getSysConfig()
				.getEnoughreduce_max_count()) {
			map.put("op_title", "您当前正在审核或进行的满就减超过了规定的最大值");
		}

		WebForm wf = new WebForm();
		EnoughReduce enoughreduce = null;
		if (CommUtil.null2String(id).equals("")) {
			enoughreduce = wf.toPo(request, EnoughReduce.class);
			enoughreduce.setAddTime(new Date());
			enoughreduce.setEr_type(1);
		} else {
			EnoughReduce obj = this.enoughreduceService.getObjById(Long
					.parseLong(id));
			if (obj.getErstatus() > 0) {
				map.put("op_title", "该活动不可编辑");
			}
			enoughreduce = (EnoughReduce) wf.toPo(request, obj);
		}

		TreeMap<Double, Double> jsonmap = new TreeMap<Double, Double>();
		for (int i = 1; i <= CommUtil.null2Int(count); i++) {
			String enoughMoney = CommUtil.null2String(request
					.getParameter("enoughMoney_" + i));
			String reduceMoney = CommUtil.null2String(request
					.getParameter("reduceMoney_" + i));
			if (enoughMoney != null && !"".equals(enoughMoney)
					&& reduceMoney != null && !"".equals(reduceMoney)) {
				jsonmap.put(CommUtil.null2Double(new BigDecimal(enoughMoney)),
						CommUtil.null2Double(new BigDecimal(reduceMoney)));
			}
		}
		enoughreduce.setEr_json(Json.toJson(jsonmap, JsonFormat.compact()));
		String ertag = "";
		Iterator<Double> it = jsonmap.keySet().iterator();
		while (it.hasNext()) {
			double key = it.next();
			double value = jsonmap.get(key);
			ertag += "满" + key + "减" + value + ",";
		}
		ertag = ertag.substring(0, ertag.length() - 1);
		enoughreduce.setErtag(ertag);
		enoughreduce.setErstatus(0);
		enoughreduce.setEr_type(1);
		enoughreduce.setStore_id("" + store.getId());
		enoughreduce.setStore_name(store.getStore_name());
		enoughreduce.setErgoods_ids_json("[]");
		if (id.equals("")) {
			this.enoughreduceService.save(enoughreduce);
			map.put("op_title", "保存满就减活动成功");
			map.put("ret", true);
			map.put("id", enoughreduce.getId());
		} else {
			this.enoughreduceService.update(enoughreduce);
			map.put("op_title", "保存满就减活动成功");
			map.put("ret", true);
			map.put("id", enoughreduce.getId());
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@SecurityMapping(title = "满就减活动删除", value = "/seller/enoughreduce_del.htm*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping("/seller/enoughreduce_del.htm")
	public String enoughreduce_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		for (String id : mulitId.split(",")) {
			if (!id.equals("")) {
				EnoughReduce enoughreduce = this.enoughreduceService
						.getObjById(Long.parseLong(id));
				User user = this.userService.getObjById(SecurityUserHolder
						.getCurrentUser().getId());
				user = user.getParent() == null ? user : user.getParent();
				Store store = user.getStore();
				if (enoughreduce.getStore_id().equals(store.getId().toString())) {
					String goods_json = enoughreduce.getErgoods_ids_json();
					if (goods_json != null && !goods_json.equals("")) {
						List<String> goods_id_list = (List) Json
								.fromJson(goods_json);
						for (String goods_id : goods_id_list) {
							Goods ergood = this.goodsService
									.getObjById(CommUtil.null2Long(goods_id));
							if (ergood.getOrder_enough_reduce_id().equals(id)) {
								ergood.setEnough_reduce(0);
								ergood.setOrder_enough_reduce_id("");
								this.goodsService.update(ergood);
							}
						}
					}
					this.enoughreduceService.delete(Long.parseLong(id));
				}
			}
		}
		return "redirect:enoughreduce_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "满就减活动申请审核", value = "/seller/enoughreduce_apply.htm*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping("/seller/enoughreduce_apply.htm")
	public ModelAndView enoughreduce_apply(HttpServletRequest request,
			HttpServletResponse response, String id) {
		if (id != null && !id.equals("")) {
			EnoughReduce enoughreduce = this.enoughreduceService
					.getObjById(Long.parseLong(id));
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			if (!enoughreduce.getStore_id().equals("" + store.getId())) {
				JModelAndView mv = new JModelAndView(
						"user/default/sellercenter/seller_error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "您的店铺中没有该活动");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/enoughreduce_list.htm");
				return mv;
			}
			Map params = new HashMap();
			params.put("sid", store.getId().toString());
			List er = this.enoughreduceService
					.query("select obj from EnoughReduce obj where obj.store_id=:sid and (obj.erstatus=10 or obj.erstatus=5)",
							params, -1, -1);
			if (er.size() >= this.configService.getSysConfig()
					.getEnoughreduce_max_count()) {
				JModelAndView mv = new JModelAndView(
						"user/default/sellercenter/seller_error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "您当前正在审核或进行的满就减超过了规定的最大值");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/enoughreduce_list.htm");
				return mv;
			}
			if (enoughreduce.getErstatus() == 0
					|| enoughreduce.getErstatus() == -10) {
				enoughreduce.setErstatus(5);
				enoughreduce.setFailed_reason("");
				this.enoughreduceService.update(enoughreduce);
				ModelAndView mv = new JModelAndView(
						"user/default/sellercenter/seller_success.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/enoughreduce_list.htm");
				mv.addObject("op_title", "提交申请成功");
				return mv;
			}
		}
		JModelAndView mv = new JModelAndView(
				"user/default/sellercenter/seller_error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "提交申请失败");
		mv.addObject("url", CommUtil.getURL(request)
				+ "/seller/enoughreduce_list.htm");
		return mv;
	}

	/**
	 * 活动商品添加
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "满就减活动商品列表", value = "/seller/enoughreduce_goods.htm*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping("/seller/enoughreduce_goods.htm")
	public ModelAndView enoughreduce_goods(HttpServletRequest request,
			HttpServletResponse response, String er_id, String currentPage,
			String brand_id, String searchstr) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/enoughreduce_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		EnoughReduce er = this.enoughreduceService.getObjById(CommUtil
				.null2Long(er_id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (!er.getStore_id().equals("" + store.getId())) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您的店铺中没有该活动");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/enoughreduce_list.htm");
			return mv;
		}
		if (er.getErstatus() > 0) {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "该活动不可编辑");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/enoughreduce_list.htm");
			return mv;
		}
		String store_id = store.getId().toString();
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, null, null);
		this.queryTools.shieldGoodsStatus(qo, null);
		qo.addQuery("obj.goods_store.id",
				new SysMap("store_id", CommUtil.null2Long(store_id)), "=");

		Map para = new HashMap();
		para.put("ids", genericIds(er.getErgoods_ids_json()));
		String hql = "1=1 or (obj.id in (:ids)";

		if (searchstr != null && !searchstr.equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("obj_goods_name", "%"
					+ searchstr + "%"), "like");
			mv.addObject("searchstr", searchstr);
			hql += "and obj.goods_name like :searchstr";
			para.put("searchstr", "%" + searchstr + "%");
		}
		if (brand_id != null && !brand_id.equals("")) {
			qo.addQuery("obj.goods_brand.id", new SysMap("obj_goods_brand",
					CommUtil.null2Long(brand_id)), "=");
			mv.addObject("brand_id", brand_id);
			hql += "and obj.goods_brand.id = :obj_goods_brand";
			para.put("brand_id", brand_id);
		}
		hql += ")";
		if (er.getErgoods_ids_json().length() > 2) {
			qo.addQuery(hql, para);// 或者id被记录下来的
		}
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);

		mv.addObject("storeTools", storeTools);
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("er_title", er.getErtitle());
		mv.addObject("ercontent", er.getErcontent());
		mv.addObject("er_id", er_id);
		return mv;
	}

	@SecurityMapping(title = "满就减商品AJAX更新", value = "/seller/enoughreduce_goods_ajax.htm*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping("/seller/enoughreduce_goods_ajax.htm")
	public void enoughreduce_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String er_id)
			throws ClassNotFoundException {
		Goods obj = this.goodsService.getObjById(Long.parseLong(id));
		EnoughReduce er = this.enoughreduceService.getObjById(Long
				.parseLong(er_id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (obj.getGoods_store().getId() == store.getId()) {
			boolean data = false;
			int flag = obj.getEnough_reduce();
			String json = er.getErgoods_ids_json();
			List jsonlist = new ArrayList();
			if (json != null && !"".equals(json)) {
				jsonlist = (List) Json.fromJson(json);
			}
			if (flag == 0) {
				data = true;
				if (obj.getCombin_status() == 0 && obj.getGroup_buy() == 0
						&& obj.getGoods_type() == 1
						&& obj.getActivity_status() == 0
						&& obj.getF_sale_type() == 0
						&& obj.getAdvance_sale_type() == 0
						&& obj.getOrder_enough_give_status() == 0) {
					obj.setEnough_reduce(1);
					obj.setOrder_enough_reduce_id(er_id);
					jsonlist.add(id);
					er.setErgoods_ids_json(Json.toJson(jsonlist,
							JsonFormat.compact()));
				}
			} else {
				data = false;
				obj.setEnough_reduce(0);
				obj.setOrder_enough_reduce_id("");
				if (jsonlist.contains(id)) {
					jsonlist.remove(id);
				}
				er.setErgoods_ids_json(Json.toJson(jsonlist,
						JsonFormat.compact()));
			}
			this.enoughreduceService.update(er);
			this.goodsService.update(obj);
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer;
			try {
				writer = response.getWriter();
				writer.print(data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@SecurityMapping(title = "满就减活动商品批量管理", value = "/seller/enoughreduce_goods_admin.htm*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping("/seller/enoughreduce_goods_admin.htm")
	public String enoughreduce_goods_admin(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage,
			String er_id, String type) {
		EnoughReduce enoughreduce = this.enoughreduceService.getObjById(Long
				.parseLong(er_id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		List<String> goods_id_list = new ArrayList<String>();
		String goods_json = enoughreduce.getErgoods_ids_json();
		if (goods_json != null && !goods_json.equals("")
				&& goods_json.length() > 2) {
			goods_id_list = (List) Json.fromJson(goods_json);
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Goods ergood = this.goodsService.getObjById(CommUtil
						.null2Long(id));
				if (ergood.getGoods_store().getId() == store.getId()) {
					if (ergood.getEnough_reduce() == 0
							|| ergood.getOrder_enough_reduce_id().equals(er_id)) {
						if (type.equals("add")) {
							if (ergood.getCombin_status() == 0
									&& ergood.getGroup_buy() == 0
									&& ergood.getGoods_type() == 1
									&& ergood.getActivity_status() == 0
									&& ergood.getF_sale_type() == 0
									&& ergood.getAdvance_sale_type() == 0
									&& ergood.getOrder_enough_give_status() == 0) {
								goods_id_list.add(id);
								ergood.setEnough_reduce(1);
								ergood.setOrder_enough_reduce_id(er_id);
							}
						} else {
							if (goods_id_list.contains(id)) {
								goods_id_list.remove(id);
							}
							ergood.setEnough_reduce(0);
							ergood.setOrder_enough_reduce_id("");
						}
					}
					this.goodsService.update(ergood);
				}
			}
		}
		enoughreduce.setErgoods_ids_json(Json.toJson(goods_id_list,
				JsonFormat.compact()));
		this.enoughreduceService.update(enoughreduce);

		return "redirect:enoughreduce_goods.htm?currentPage=" + currentPage
				+ "&er_id=" + er_id;
	}

	public static Date addDate(Date d, long day) throws ParseException {
		long time = d.getTime();
		day = day * 24 * 60 * 60 * 1000;
		time += day;
		return new Date(time);

	}

	/**
	 * enoughreduce添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "满就减活动添加", value = "/seller/enoughreduce_meal.htm*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping("/seller/enoughreduce_meal.htm")
	public ModelAndView enoughreduce_meal(HttpServletRequest request,
			HttpServletResponse response) {
		if (this.configService.getSysConfig().getEnoughreduce_status() == 0) {
			ModelAndView mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "商城没有开启满就减活动");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/seller/enoughreduce_list.htm");
			return mv;
		}
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/enoughreduce_meal.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		mv.addObject("user", user);
		return mv;
	}

	/**
	 * enoughreduce保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "满就减活动保存", value = "/seller/enoughreduce_male_save.htm*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping("/seller/enoughreduce_male_save.htm")
	public void enoughreduce_male_save(HttpServletRequest request,
			HttpServletResponse response, String meal_day)
			throws ParseException {
		Map map = new HashMap();
		if (configService.getSysConfig().getEnoughreduce_status() == 1) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			int cost = configService.getSysConfig().getEnoughreduce_meal_gold();
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
				Date day = user.getStore().getEnoughreduce_meal_endTime();
				Date d = new Date();
				if (day != null) {
					if (day.after(new Date())) {
						// 记录金币日志
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_content("购买满就减套餐");
						log.setGl_count(costday * cost);
						log.setGl_user(user);
						log.setGl_type(-1);
						this.goldLogService.save(log);
						// 保存套餐购买信息
						SalesLog c_log = new SalesLog();
						c_log.setAddTime(new Date());
						c_log.setBegin_time(user.getStore()
								.getEnoughreduce_meal_endTime());
						c_log.setEnd_time(this.addDate(user.getStore()
								.getEnoughreduce_meal_endTime(), CommUtil
								.null2Long(days)));
						c_log.setGold(costday * cost);
						c_log.setSales_info("套餐总时间增加" + days + "天");
						c_log.setStore_id(user.getStore().getId());
						c_log.setSales_type(3);// 促销类型为满就减
						//计算时间
						this.salesLogService.save(c_log);
						user.getStore().setEnoughreduce_meal_endTime(
								this.addDate(user.getStore()
										.getEnoughreduce_meal_endTime(), CommUtil
										.null2Long(days)));
						this.storeService.update(user.getStore());
					} else {
						Calendar ca = Calendar.getInstance();
						ca.add(ca.DATE, days);
						SimpleDateFormat bartDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						String latertime = bartDateFormat.format(ca.getTime());
						user.getStore().setEnoughreduce_meal_endTime(
								CommUtil.formatDate(latertime,
										"yyyy-MM-dd HH:mm:ss"));
						this.storeService.update(user.getStore());
						// 记录金币日志
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_content("购买满就减套餐");
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
						c_log.setSales_type(3);// 促销类型为满就减
						this.salesLogService.save(c_log);
					}
				} else {
					Calendar ca = Calendar.getInstance();
					ca.add(ca.DATE, days);
					SimpleDateFormat bartDateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String latertime = bartDateFormat.format(ca.getTime());
					user.getStore().setEnoughreduce_meal_endTime(
							CommUtil.formatDate(latertime,
									"yyyy-MM-dd HH:mm:ss"));
					this.storeService.update(user.getStore());
					// 记录金币日志
					GoldLog log = new GoldLog();
					log.setAddTime(new Date());
					log.setGl_content("购买满就减套餐");
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
					c_log.setSales_type(3);// 促销类型为满就减
					this.salesLogService.save(c_log);
				}
				map.put("ret", true);
				map.put("msg", "购买成功");
			} else {
				map.put("ret", false);
				map.put("msg", "您的金币不足，无法购买满就减套餐");
			}
		} else {
			map.put("ret", false);
			map.put("msg", "购买失败,商城未开启满就减活动");
		}

		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "满就减购买记录", value = "/seller/enoughreduce_meal_log.htm*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping("/seller/enoughreduce_meal_log.htm")
	public ModelAndView enoughreduce_meal_log(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/enoughreduce_meal_log.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		SalesLogQueryObject qo = new SalesLogQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.store_id", new SysMap("store_id", store.getId()), "=");
		qo.addQuery("obj.sales_type", new SysMap("sales_type", 3), "=");
		IPageList pList = this.salesLogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "满就减详情", value = "/seller/enoughreduce_info.htm*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping("/seller/enoughreduce_info.htm")
	public ModelAndView enoughreduce_info(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/enoughreduce_info.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (id != null && !id.equals("")) {
			EnoughReduce er = this.enoughreduceService.getObjById(CommUtil
					.null2Long(id));
			if (er.getStore_id().equals("" + store.getId())) {
				mv.addObject("er", er);
				GoodsQueryObject qo = null;
				Map para = new HashMap();
				if (er.getErgoods_ids_json().length() > 2) {
					qo = new GoodsQueryObject(currentPage, mv, orderBy,
							orderType);
					para.put("ids", genericIds(er.getErgoods_ids_json()));
					qo.addQuery("obj.id in (:ids)", para);// id被记录下来的
				}

				IPageList pList = this.goodsService.list(qo);
				CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			} else {
				mv = new JModelAndView(
						"user/default/sellercenter/seller_error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "您的店铺中没有该活动");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/enoughreduce_list.htm");
				return mv;
			}
		}
		return mv;
	}
	private Set<Long> genericIds(String str) {
		Set<Long> ids = new HashSet<Long>();
		List list = (List) Json.fromJson(str);
		for (Object object : list) {
			ids.add(CommUtil.null2Long(object));
		}
		return ids;
	}
}