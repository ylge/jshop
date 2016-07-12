package com.iskyshop.module.app.view.action;

import java.io.IOException;
import java.io.PrintWriter;
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
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.FreeGoods;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.IntegralGoods;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IActivityService;
import com.iskyshop.foundation.service.IFreeGoodsService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IIntegralGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.view.web.tools.GoodsViewTools;

/**
 * 
 * <p>
 * Title: MobileIndexViewAction.java
 * </p>
 * 
 * <p>
 * Description:手机客户端商城前台请求控制器
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
 * @date 2014-7-14
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class AppIndexViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsBrandService brandService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGroupGoodsService groupgoodsService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IGroupLifeGoodsService grouplifeGoodsService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private GoodsViewTools goodsviewTools;
	@Autowired
	private IIntegralGoodsService integralGoodsService;
	@Autowired
	private IFreeGoodsService freegoodsService;

	/**
	 * 手机客户端商城首页，返回html页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/app/index.htm")
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response, String type) {
		ModelAndView mv = new JModelAndView("app/index.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig()
				.getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		String defaultImg = url + "/"
				+ this.configService.getSysConfig().getGoodsImage().getPath()
				+ "/"
				+ this.configService.getSysConfig().getGoodsImage().getName();
		Map params = new HashMap();
		params.clear();
		params.put("mobile_hot", 1);
		params.put("goods_status", 0);
		List<Goods> goods_hots = this.goodsService
				.query("select obj from Goods obj where obj.mobile_hot=:mobile_hot and obj.goods_status =:goods_status order by mobile_hotTime desc",
						params, 0, 3);
		List<Map> hots = new ArrayList<Map>();
		for (Goods obj : goods_hots) {
			Map map = new HashMap();
			map.put("id", obj.getId());
			map.put("name", obj.getGoods_name());
			if (obj.getGoods_main_photo() != null) {
				map.put("img", url + "/" + obj.getGoods_main_photo().getPath()
						+ "/" + obj.getGoods_main_photo().getName());
			} else {
				map.put("img", defaultImg);
			}
			hots.add(map);
		}
		mv.addObject("hots", hots);
		params.clear();
		params.put("mobile_recommend", 1);
		params.put("goods_status", 0);
		List<Goods> top_recommends = this.goodsService
				.query("select obj from Goods obj where obj.mobile_recommend=:mobile_recommend and obj.goods_status =:goods_status order by mobile_recommendTime desc",
						params, 0, 3);
		List<Map> recommends = new ArrayList<Map>();
		for (Goods obj : top_recommends) {
			Map map = new HashMap();
			map.put("id", obj.getId());
			map.put("name", obj.getGoods_name());
			if (obj.getGoods_main_photo() != null) {
				map.put("img", url + "/" + obj.getGoods_main_photo().getPath()
						+ "/" + obj.getGoods_main_photo().getName()
						+ "_middle." + obj.getGoods_main_photo().getExt());
			} else {
				map.put("img", defaultImg);
			}
			recommends.add(map);
		}
		mv.addObject("recommends", recommends);
		params.clear();
		params.put("ag_status", 1);
		params.put("goods_status", 0);
		params.put("mobile_recommend", 1);
		params.put("ac_begin_time", new Date());
		params.put("ac_end_time", new Date());
		params.put("ac_status", 1);
		List<ActivityGoods> activitygoods = this.activityGoodsService
				.query("select obj from ActivityGoods obj where obj.ag_status=:ag_status and obj.ag_goods.goods_status=:goods_status "
						+ "and obj.mobile_recommend=:mobile_recommend and obj.act.ac_end_time>=:ac_end_time and obj.act.ac_begin_time<=:ac_begin_time and obj.act.ac_status=:ac_status"
						+ " order by mobile_recommendTime desc", params, 0, 4);
		List<Map> activitys_list = new ArrayList<Map>();
		for (ActivityGoods obj : activitygoods) {
			Map map = new HashMap();
			map.put("id", obj.getAg_goods().getId());
			map.put("name", obj.getAg_goods().getGoods_name());
			if (obj.getAg_goods().getGoods_main_photo() != null) {
				map.put("img", url + "/"
						+ obj.getAg_goods().getGoods_main_photo().getPath()
						+ "/"
						+ obj.getAg_goods().getGoods_main_photo().getName()
						+ "_middle."
						+ obj.getAg_goods().getGoods_main_photo().getExt());
			} else {
				map.put("img", defaultImg);
			}
			activitys_list.add(map);
		}
		if (activitys_list.size()>0) {
			mv.addObject("activitys", activitys_list);
		}
		// 商品团购推荐
		params.clear();
		params.put("gg_status", 1);
		params.put("group_status", 0);
		params.put("mobile_recommend", 1);
		params.put("group_type", 0);// 商品团购
		params.put("beginTime", new Date());
		params.put("endTime", new Date());
		params.put("group_beginTime", new Date());
		params.put("group_endTime", new Date());
		List<GroupGoods> groupgoods = this.groupgoodsService
				.query("select obj from GroupGoods obj where obj.gg_status=:gg_status and obj.group.status=:group_status "
						+ "and obj.mobile_recommend=:mobile_recommend and obj.group.group_type=:group_type and obj.group.beginTime<=:group_beginTime and "
						+ "obj.group.endTime>=:group_endTime and obj.beginTime<=:beginTime and obj.endTime>=:endTime order by mobile_recommendTime desc",
						params, 0, 4);
		List<Map> groupsgoods = new ArrayList<Map>();
		for (GroupGoods obj : groupgoods) {
			Map map = new HashMap();
			map.put("id", obj.getId());
			map.put("name", obj.getGg_name());
			map.put("sale", obj.getGg_selled_count());
			if (obj.getGg_img() != null) {
				map.put("img", url + "/" + obj.getGg_img().getPath() + "/"
						+ obj.getGg_img().getName());
			} else {
				map.put("img", defaultImg);
			}
			groupsgoods.add(map);
		}
		mv.addObject("groupsgoods", groupsgoods);
		// 生活团购推荐
		params.clear();
		params.put("group_status", 0);
		params.put("mobile_recommend", 1);
		params.put("group_type", 1);// 生活团购
		params.put("group_beginTime", new Date());
		params.put("group_endTime", new Date());
		params.put("beginTime", new Date());
		params.put("endTime", new Date());
		List<GroupLifeGoods> grouplife = this.grouplifeGoodsService
				.query("select obj from GroupLifeGoods obj where obj.group.status=:group_status "
						+ "and obj.mobile_recommend=:mobile_recommend and obj.group.group_type=:group_type and obj.group.beginTime<=:group_beginTime and "
						+ "obj.group.endTime>=:group_endTime and obj.beginTime<=:beginTime and obj.endTime>=:endTime order by mobile_recommendTime desc",
						params, 0, 4);
		List<Map> grouplifes = new ArrayList<Map>();
		for (GroupLifeGoods obj : grouplife) {
			Map map = new HashMap();
			map.put("id", obj.getId());
			map.put("name", obj.getGg_name());
			map.put("sale", obj.getSelled_count());
			if (obj.getGroup_acc() != null) {
				map.put("img", url + "/" + obj.getGroup_acc().getPath() + "/"
						+ obj.getGroup_acc().getName());
			} else {
				map.put("img", defaultImg);
			}
			grouplifes.add(map);
		}
		mv.addObject("grouplifes", grouplifes);
		// 积分商品
		List<Map> integrals = new ArrayList<Map>();
		params.clear();
		params.put("mobile_recommend", 1);
		List<IntegralGoods> integrals_goods = this.integralGoodsService
				.query("select obj from IntegralGoods obj where obj.mobile_recommend=:mobile_recommend order by obj.mobile_recommendTime asc",
						params, 0, 6);
		for (IntegralGoods obj : integrals_goods) {
			Map map = new HashMap();
			map.put("id", obj.getId());
			map.put("name", obj.getIg_goods_name());
			if (obj.getIg_goods_img() != null) {
				map.put("img", url + "/" + obj.getIg_goods_img().getPath()
						+ "/" + obj.getIg_goods_img().getName());
			} else {
				map.put("img", defaultImg);
			}
			integrals.add(map);
		}
		mv.addObject("integrals", integrals);
		// 免费试用
		List<Map> fress = new ArrayList<Map>();
		params.clear();
		params.put("freeStatus", 5);
		params.put("mobile_recommend", 1);
		List<FreeGoods> fgs = this.freegoodsService
				.query("select obj from FreeGoods obj where obj.freeStatus=:freeStatus and obj.mobile_recommend=:mobile_recommend order by obj.mobile_recommendTime desc",
						params, 0, 5);
		for (FreeGoods obj : fgs) {
			Map map = new HashMap();
			map.put("id", obj.getId());
			map.put("name", obj.getGoods_name());
			map.put("apply_count", obj.getApply_count());
			if (obj.getFree_acc() != null) {
				map.put("img", url + "/" + obj.getFree_acc().getPath() + "/"
						+ obj.getFree_acc().getName());
			} else {
				map.put("img", defaultImg);
			}
			fress.add(map);
		}
		mv.addObject("fress", fress);
		mv.addObject("type", type);
		return mv;
	}

	/**
	 * gb_id：商品品牌id，gc_id：商品分类id,beginCount：查询起始位置，selectCount:查询个数
	 * 
	 * @param request
	 * @param response
	 * @param goods_current_price
	 *            ,goods_salenum,goods_seller_time,goods_click
	 * @param orderType
	 * @param gb_id
	 * @param gc_id
	 */
	@RequestMapping("/app/goods_list.htm")
	public void goods_list(HttpServletRequest request,
			HttpServletResponse response, String orderBy, String orderType,
			String beginCount, String selectCount, String gc_id, String gb_id,
			String keyword, String queryType, String store_id) {
		boolean verify = true;
		Map json_map = new HashMap();
		if (orderBy == null || orderBy.equals("")) {
			orderBy = "goods_salenum";
		}
		if (orderType == null || orderType.equals("")) {
			orderType = "desc";
		}
		if (verify && orderBy != null && !orderBy.equals("")) {
			List<Goods> goods_list = null;
			Map params = new HashMap();
			params.put("goods_status", 0);
			String query = "select obj from Goods obj where 1=1 and obj.goods_status=:goods_status ";
			if (gc_id != null && !gc_id.equals("")) {
				params.put("gc_id", CommUtil.null2Long(gc_id));
				query += "and obj.gc.id=:gc_id   ";
			}
			if (gb_id != null && !gb_id.equals("")) {
				params.put("gb_id", CommUtil.null2Long(gb_id));
				query += "and obj.goods_brand.id=:gb_id  ";
			}
			if (keyword != null && !keyword.equals("")) {
				params.put("keyword", "%" + keyword + "%");
				query += "and obj.goods_name like:keyword  ";
			}
			if (store_id != null && !store_id.equals("")) {
				params.put("store_id", CommUtil.null2Long(store_id));
				query += "and obj.goods_store.id=:store_id";
			}
			query += " order by ";
			/*
			 * if (gc_id == null && gb_id == null && keyword == null &&
			 * queryType == null && store_id == null) { params = null; }
			 */
			String url = CommUtil.getURL(request);
			if (!"".equals(CommUtil.null2String(this.configService
					.getSysConfig().getImageWebServer()))) {
				url = this.configService.getSysConfig().getImageWebServer();
			}
			goods_list = this.goodsService.query(query + orderBy + " "
					+ orderType, params, CommUtil.null2Int(beginCount),
					CommUtil.null2Int(selectCount));
			List map_list = new ArrayList();
			for (Goods obj : goods_list) {
				Map goods_map = new HashMap();
				goods_map.put("id", obj.getId());
				goods_map.put("goods_name", obj.getGoods_name());
				goods_map.put("goods_current_price",
						CommUtil.null2String(obj.getGoods_current_price()));// 商品现价
				goods_map.put("goods_salenum", obj.getGoods_salenum());// 销量
				String goods_main_photo = url// 系统默认商品图片
						+ "/"
						+ this.configService.getSysConfig().getGoodsImage()
								.getPath()
						+ "/"
						+ this.configService.getSysConfig().getGoodsImage()
								.getName();
				if (obj.getGoods_main_photo() != null) {// 商品主图片
					goods_main_photo = url + "/"
							+ obj.getGoods_main_photo().getPath() + "/"
							+ obj.getGoods_main_photo().getName() + "_small."
							+ obj.getGoods_main_photo().getExt();
				}
				goods_map.put("goods_main_photo", goods_main_photo);

				Map map = goodsviewTools.query_goods_preferential(obj.getId());
				String status = map.get("name").toString();
				goods_map.put("status", status);
				if (obj.getEvaluate_count() > 0) {
					double rate = CommUtil.null2Double(obj.getWell_evaluate()) * 100;
					goods_map.put("evaluate",
							rate + "%好评（" + obj.getEvaluate_count() + "）人");
				} else {
					goods_map.put("evaluate", "暂无评价");
				}

				map_list.add(goods_map);
			}
			json_map.put("goods_list", map_list);
		}
		json_map.put("code", CommUtil.null2String(verify));
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
	 * 手机客户端商城首页，返回html页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/app/close.htm")
	public ModelAndView close(HttpServletRequest request,
			HttpServletResponse response, String type) {
		ModelAndView mv = new JModelAndView("app/close.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}

}
