package com.iskyshop.module.app.view.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.iskyshop.core.ip.IPSeeker;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.CombinPlan;
import com.iskyshop.foundation.domain.Consult;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.Group;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IActivityService;
import com.iskyshop.foundation.service.ICombinPlanService;
import com.iskyshop.foundation.service.IConsultService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.seller.tools.TransportTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.IntegralViewTools;

/**
 * 
 * <p>
 * Title: MobileGoodsViewAction.java
 * </p>
 * 
 * <p>
 * Description:手机客户端商城前台商品请求
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
public class AppGoodsViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsBrandService brandService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IGroupGoodsService groupgoodsService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IConsultService consultService;
	@Autowired
	private GoodsViewTools goodsviewTools;
	@Autowired
	private IFavoriteService favoriteService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IUserService userService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private IntegralViewTools integralViewTools;
	@Autowired
	private ICombinPlanService combinplanService;

	/**
	 * 手机客户端商城首页商品详情请求
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/app/goods.htm")
	public void goods(HttpServletRequest request, HttpServletResponse response,
			String id, String user_id, String token) {
		Map goods_map = new HashMap();
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		if (obj.getGroup() != null && obj.getGroup_buy() == 2) {// 如果是团购商品，检查团购是否过期
			Group group = obj.getGroup();
			if (group.getEndTime().before(new Date())) {
				obj.setGroup(null);
				obj.setGroup_buy(0);
				obj.setGoods_current_price(obj.getStore_price());
			}
		}
		if (obj.getCombin_status() == 1) {// 如果是组合商品，检查组合是否过期
			Map params = new HashMap();
			params.put("endTime", new Date());
			params.put("main_goods_id", obj.getId());
			List<CombinPlan> combins = this.combinplanService
					.query("select obj from CombinPlan obj where obj.endTime<=:endTime and obj.main_goods_id=:main_goods_id",
							params, -1, -1);
			if (combins.size() > 0) {
				for (CombinPlan com : combins) {
					if (com.getCombin_type() == 0) {
						if (obj.getCombin_suit_id().equals(com.getId())) {
							obj.setCombin_suit_id(null);
						}
					} else {
						if (obj.getCombin_parts_id().equals(com.getId())) {
							obj.setCombin_parts_id(null);
						}
					}
					obj.setCombin_status(0);
				}
			}
		}
		this.goodsService.update(obj);
		goods_map.put("id", obj.getId());
		goods_map.put("favorite", "false");
		if (user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {

				Map params = new HashMap();
				params.put("user_id", CommUtil.null2Long(user_id));
				params.put("goods_id", obj.getId());
				List<Favorite> list = this.favoriteService
						.query("select obj from Favorite obj where obj.user_id=:user_id and obj.goods_id=:goods_id",
								params, -1, -1);
				if (list.size() > 0) {
					goods_map.put("favorite", "true");

				}
			}
		}
		goods_map.put("goods_name", obj.getGoods_name());
		goods_map.put("goods_cod", obj.getGoods_cod());// 是否支持货到付款，默认为0：支持货到付款，-1为不支持货到付款
		goods_map.put("goods_type", obj.getGoods_type());// 商品类型，0为自营商品，1为第三方经销商
		goods_map.put("goods_choice_type", obj.getGoods_choice_type());// 0实体商品，1为虚拟商品
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig()
				.getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		if (obj.getGoods_type() == 1) {// 店铺信息
			Store store = obj.getGoods_store();
			Map store_info = new HashMap();
			store_info.put("store_name", store.getStore_name());// 名称
			if (store.getStore_logo() != null) {
				store_info.put("store_logo", url + "/"
						+ store.getStore_logo().getPath() + "/"
						+ store.getStore_logo().getName());
			} else {
				store_info.put("store_logo", url
						+ "/"
						+ this.configService.getSysConfig().getStoreImage()
								.getPath()
						+ "/"
						+ this.configService.getSysConfig().getStoreImage()
								.getName());
			}
			store_info.put("store_id", store.getId());// id
			store_info.put("store_rate",
					CommUtil.null2Double(store.getPoint().getStore_evaluate()));// 综合评分
			store_info.put("description_evaluate", CommUtil.null2Double(store
					.getPoint().getDescription_evaluate()));// 描述相符评价
			store_info.put("service_evaluate", CommUtil.null2Double(store
					.getPoint().getService_evaluate()));// 服务态度评价
			store_info.put("ship_evaluate",
					CommUtil.null2Double(store.getPoint().getShip_evaluate()));// 发货速度评价
			goods_map.put("store_info", store_info);
		}

		goods_map
				.put("goods_price", CommUtil.null2String(obj.getGoods_price()));// 商品原价
		goods_map.put("goods_current_price",
				CommUtil.null2String(obj.getGoods_current_price()));// 商品现价
		goods_map.put("goods_inventory", obj.getGoods_inventory());// 库存
		goods_map.put("inventory_type", obj.getInventory_type());// 库存方式分为all全局库存，spec按规格库存
		goods_map.put("goods_salenum", obj.getGoods_salenum());// 销量
		goods_map.put("goods_fee", obj.getGoods_fee());// 运费
		goods_map.put("goods_well_evaluate",
				CommUtil.mul(obj.getWell_evaluate(), 100) + "%");// 好评率
		goods_map.put("goods_middle_evaluate",
				CommUtil.mul(obj.getMiddle_evaluate(), 100) + "%");// 中评率
		goods_map.put("goods_bad_evaluate",
				CommUtil.mul(obj.getBad_evaluate(), 100) + "%");// 差评率
		goods_map.put("evaluate_count", obj.getEvaluates().size());// 总评论数
		Map params = new HashMap();
		params.put("goods_id", obj.getId());
		List<Consult> goods_consults = this.consultService.query(
				"select obj from Consult obj where obj.goods_id=:goods_id",
				params, -1, -1);
		goods_map.put("consult_count", goods_consults.size());// 总咨询数

		Map map = goodsviewTools.query_goods_preferential(obj.getId());
		goods_map.put("status", map.get("name"));// 是否参加活动
		goods_map.put("status_info", map.get("info"));
		List photo_list = new ArrayList();
		photo_list.add(url + "/" + obj.getGoods_main_photo().getPath() + "/"
				+ obj.getGoods_main_photo().getName() + "_middle."
				+ obj.getGoods_main_photo().getExt());// 添加主图片
		for (Accessory acc : obj.getGoods_photos()) {// 添加附图
			photo_list.add(url + "/" + acc.getPath() + "/" + acc.getName());
		}
		goods_map.put("goods_photos", photo_list);
		String current_ip = CommUtil.getIpAddr(request);// 获得本机IP
		String current_city = "未知地区";
		if (CommUtil.isIp(current_ip)) {
			IPSeeker ip = new IPSeeker(null, null);
			current_city = ip.getIPLocation(current_ip).getCountry();
		}
		goods_map.put("current_city", current_city);
		// 设置运费信息
		String trans_information = "商家承担";
		if (obj.getGoods_transfee() == 0) {
			if (obj.getTransport() != null && !obj.getTransport().equals("")) {
				String main_info = "平邮(¥"
						+ this.transportTools.cal_goods_trans_fee(obj
								.getTransport().getId().toString(), "mail",
								CommUtil.null2String(obj.getGoods_weight()),
								CommUtil.null2String(obj.getGoods_volume()),
								current_city);
				String express_info = "快递(¥"
						+ this.transportTools.cal_goods_trans_fee(obj
								.getTransport().getId().toString(), "express",
								CommUtil.null2String(obj.getGoods_weight()),
								CommUtil.null2String(obj.getGoods_volume()),
								current_city);
				String ems_info = "EMS(¥"
						+ this.transportTools.cal_goods_trans_fee(obj
								.getTransport().getId().toString(), "ems",
								CommUtil.null2String(obj.getGoods_weight()),
								CommUtil.null2String(obj.getGoods_volume()),
								current_city);
				trans_information = main_info + ") | " + express_info + ") | "
						+ ems_info + ")";
			} else {
				trans_information = "平邮(¥"
						+ CommUtil.null2Float(obj.getMail_trans_fee()) + ") | "
						+ "快递(¥"
						+ CommUtil.null2Float(obj.getExpress_trans_fee())
						+ ") | " + "EMS(¥"
						+ CommUtil.null2Float(obj.getEms_trans_fee()) + ")";
			}
		}
		goods_map.put("trans_information", trans_information);
		goods_map.put("ret", "true");
		String json = Json.toJson(goods_map, JsonFormat.compact());
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
	 * 根据商品id和地区名称查询相应商品运费
	 * 
	 * @param request
	 * @param response
	 * @param current_city
	 * @param goods_id
	 */
	@RequestMapping("/app/goods_trans_fee.htm")
	public void goods_trans_fee(HttpServletRequest request,
			HttpServletResponse response, String current_city, String goods_id) {
		Map json_map = new HashMap();
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(goods_id));
		// 设置运费信息
		String trans_information = "商家承担";
		if (obj.getGoods_transfee() == 0) {
			if (obj.getTransport() != null && !obj.getTransport().equals("")) {
				String main_info = "平邮(¥"
						+ this.transportTools.cal_goods_trans_fee(obj
								.getTransport().getId().toString(), "mail",
								CommUtil.null2String(obj.getGoods_weight()),
								CommUtil.null2String(obj.getGoods_volume()),
								current_city);
				String express_info = "快递(¥"
						+ this.transportTools.cal_goods_trans_fee(obj
								.getTransport().getId().toString(), "express",
								CommUtil.null2String(obj.getGoods_weight()),
								CommUtil.null2String(obj.getGoods_volume()),
								current_city);
				String ems_info = "EMS(¥"
						+ this.transportTools.cal_goods_trans_fee(obj
								.getTransport().getId().toString(), "ems",
								CommUtil.null2String(obj.getGoods_weight()),
								CommUtil.null2String(obj.getGoods_volume()),
								current_city);
				trans_information = main_info + ") | " + express_info + ") | "
						+ ems_info + ")";
			} else {
				trans_information = "平邮(¥"
						+ CommUtil.null2Float(obj.getMail_trans_fee()) + ") | "
						+ "快递(¥"
						+ CommUtil.null2Float(obj.getExpress_trans_fee())
						+ ") | " + "EMS(¥"
						+ CommUtil.null2Float(obj.getEms_trans_fee()) + ")";
			}
		}
		json_map.put("trans_information", trans_information);
		json_map.put("ret", "true");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			// System.out.println(Json.toJson(map, JsonFormat.compact()));
			writer.print(Json.toJson(json_map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 点击商品规格加载商品规格信息
	 * 
	 * @param request
	 * @param response
	 * @param gsp
	 * @param id
	 */
	@RequestMapping("/app/load_goods_gsp.htm")
	public void load_goods_gsp(HttpServletRequest request,
			HttpServletResponse response, String gsp, String id,
			String user_id, String token) {
		Map map = new HashMap();
		Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
		int count = 0;
		double price = 0;
		double act_price = 0;
		if (goods.getGroup() != null && goods.getGroup_buy() == 2) {// 团购商品统一按照团购价格处理
			for (GroupGoods gg : goods.getGroup_goods_list()) {
				if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
					count = gg.getGg_count();
					price = CommUtil.null2Double(gg.getGg_price());
				}
			}
		} else {
			count = goods.getGoods_inventory();
			price = CommUtil.null2Double(goods.getGoods_current_price());
			if (goods.getInventory_type().equals("spec")) {
				List<HashMap> list = Json.fromJson(ArrayList.class,
						goods.getGoods_inventory_detail());
				if (gsp != null) {
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
			int temp_count = 0;
			BigDecimal ac_rebate = null;
			if (user_id != null && token != null) {
				if (goods.getActivity_status() == 2) {// 如果是促销商品，根据规格配置价格计算相应配置的促销价格
					ActivityGoods actGoods = this.activityGoodsService
							.getObjById(goods.getActivity_goods_id());
					// 0—铜牌会员1—银牌会员2—金牌会员3—超级会员
					BigDecimal rebate = BigDecimal.valueOf(0.00);
					int level = this.integralViewTools
							.query_user_level(user_id);
					if (level == 0) {
						rebate = actGoods.getAct().getAc_rebate();
					} else if (level == 1) {
						rebate = actGoods.getAct().getAc_rebate1();
					} else if (level == 2) {
						rebate = actGoods.getAct().getAc_rebate2();
					} else if (level == 3) {
						rebate = actGoods.getAct().getAc_rebate3();
					}
					act_price = CommUtil.mul(rebate, price);
				}
			}

		}
		map.put("count", count);
		map.put("price", price);
		map.put("act_price", act_price);
		map.put("ret", "true");
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

	/**
	 * 手机客户端商城商品规格
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/app/goods_specs.htm")
	public void goods_specs(HttpServletRequest request,
			HttpServletResponse response, String id) {
		Map map = new HashMap();
		List list = new ArrayList();
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig()
				.getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		List<GoodsSpecification> specs = goodsviewTools.generic_spec(id);
		List<Map> goods_specs_info = obj.getGoods_specs_info() == null
				? new ArrayList<Map>()
				: (List<Map>) Json.fromJson(obj.getGoods_specs_info());
		for (GoodsSpecification spec : specs) {
			Map spec_map = new HashMap();
			spec_map.put("spec_type", spec.getType());
			spec_map.put("spec_key", spec.getName());
			List spec_list = new ArrayList();
			for (GoodsSpecProperty spro : obj.getGoods_specs()) {
				if (spro.getSpec().getId().equals(spec.getId())) {
					Map map_par = new HashMap();
					map_par.put("id", spro.getId());
					map_par.put("val", spro.getValue());
					if (goods_specs_info.size() > 0) {
						for (Map map1 : goods_specs_info) {
							if (CommUtil.null2Long(map1.get("id")).equals(
									spro.getId())) {
								map_par.put("val", map1.get("name").toString());
							}
						}
					}
					spec_list.add(map_par);
				}
			}
			spec_map.put("spec_values", spec_list);
			list.add(spec_map);
		}
		map.put("spec_list", list);
		map.put("ret", "true");
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
	 * 手机客户端商品详细介绍
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/app/goods_introduce.htm")
	public ModelAndView goods_introduce(HttpServletRequest request,
			HttpServletResponse response, String id, String user_id) {
		ModelAndView mv = new JModelAndView("app/goods_introduce.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		if (!obj.getGoods_property().equals("[]")) {
			List propertities = Json.fromJson(ArrayList.class,
					obj.getGoods_property());
			mv.addObject("propertities", propertities);
		}
		if (obj.getActivity_status() == 2) {// 如果是促销商品，根据规格配置价格计算相应配置的促销价格
			if (user_id != null && !user_id.equals("")) {
				User user = this.userService.getObjById(CommUtil
						.null2Long(user_id));
				ActivityGoods actGoods = this.activityGoodsService
						.getObjById(obj.getActivity_goods_id());
				// 0—铜牌会员1—银牌会员2—金牌会员3—超级会员
				BigDecimal rebate = BigDecimal.valueOf(0.00);
				int level = this.integralViewTools.query_user_level(CommUtil
						.null2String(user.getId()));
				BigDecimal ac_rebate = null;
				if (level == 0) {
					ac_rebate = actGoods.getAct().getAc_rebate();
				} else if (level == 1) {
					ac_rebate = actGoods.getAct().getAc_rebate1();
				} else if (level == 2) {
					ac_rebate = actGoods.getAct().getAc_rebate2();
				} else if (level == 3) {
					ac_rebate = actGoods.getAct().getAc_rebate3();
				}
				double activity_price = CommUtil.mul(
						obj.getGoods_current_price(), ac_rebate);
				mv.addObject("activity_price", activity_price);
			}
		}
		mv.addObject("obj", obj);
		return mv;
	}

	/**
	 * 手机客户端商城商品评价,type:类型，1:好评，0中评，-1差评
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping("/app/goods_evaluate.htm")
	public void goods_evaluate(HttpServletRequest request,
			HttpServletResponse response, String id, String type,
			String beginCount, String selectCount) {
		Map json_map = new HashMap();
		List eva_list = new ArrayList();
		if (true) {
			Map params = new HashMap();
			String query;
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
			if (type != null && !type.equals("")) {// 查询好评、中评、差评
				params.clear();
				params.put("gid", CommUtil.null2Long(id));
				params.put("evaluate_status", 0);
				params.put("evaluate_buyer_val", CommUtil.null2Int(type));
				query = "select obj from Evaluate obj where obj.evaluate_goods.id=:gid and obj.evaluate_status=:evaluate_status and obj.evaluate_buyer_val=:evaluate_buyer_val order by addTime desc";
			} else {// 查询所有评价
				params.clear();
				params.put("gid", CommUtil.null2Long(id));
				params.put("evaluate_status", 0);
				query = "select obj from Evaluate obj where obj.evaluate_goods.id=:gid and obj.evaluate_status=:evaluate_status order by addTime desc";
			}
			List<Evaluate> evas = this.evaluateService.query(query, params,
					CommUtil.null2Int(beginCount),
					CommUtil.null2Int(selectCount));
			for (Evaluate eva : evas) {
				Map map = new HashMap();
				map.put("user", eva.getEvaluate_user().getUserName());
				map.put("content", eva.getEvaluate_info());
				map.put("addTime", CommUtil.formatShortDate(eva.getAddTime()));
				eva_list.add(map);
			}
			json_map.put("eva_list", eva_list);
			int well_count = 0;
			int middle_count = 0;
			int bad_count = 0;
			Map params2 = new HashMap();
			int evs[] = {-1, 0, 1};
			for (int ev : evs) {
				params2.clear();
				params2.put("gid", CommUtil.null2Long(id));
				params2.put("evaluate_status", 0);
				params2.put("evaluate_buyer_val", ev);
				List<Evaluate> all_evas = this.evaluateService
						.query("select obj.id from Evaluate obj where obj.evaluate_goods.id=:gid and obj.evaluate_status=:evaluate_status and obj.evaluate_buyer_val=:evaluate_buyer_val order by addTime desc",
								params2, -1, -1);
				if (ev == -1) {
					bad_count = all_evas.size();
					json_map.put("bad", CommUtil.null2String(bad_count) + "-"
							+ CommUtil.mul(goods.getBad_evaluate(), 100) + "%");
				}
				if (ev == 0) {
					middle_count = all_evas.size();
					json_map.put(
							"middle",
							CommUtil.null2String(middle_count)
									+ "-"
									+ CommUtil.mul(goods.getMiddle_evaluate(),
											100) + "%");
				}
				if (ev == 1) {
					well_count = all_evas.size();
					json_map.put("well", CommUtil.null2String(well_count) + "-"
							+ CommUtil.mul(goods.getWell_evaluate(), 100) + "%");
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
	 * 手机客户端商城商品资讯信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/app/goods_consult.htm")
	public void goods_consult(HttpServletRequest request,
			HttpServletResponse response, String id, String beginCount,
			String selectCount) {
		Map json_map = new HashMap();
		List consult_list = new ArrayList();
		Map params = new HashMap();
		params.put("gid", CommUtil.null2Long(id));
		List<Consult> consults = this.consultService
				.query("select obj from Consult obj where obj.goods_id=:gid order by addTime desc",
						params, CommUtil.null2Int(beginCount),
						CommUtil.null2Int(selectCount));
		for (Consult obj : consults) {
			Map map = new HashMap();
			map.put("addTime", CommUtil.formatShortDate(obj.getAddTime()));// 咨询时间
			map.put("consult_user", obj.getConsult_user_name());// 咨询用户名称
			map.put("content", obj.getConsult_content());// 咨询内容
			map.put("reply", obj.isReply());// 是否回复
			if (obj.isReply()) {
				map.put("reply_content", obj.getConsult_reply());// 回复内容
				map.put("reply_user", obj.getReply_user_name());// 回复人姓名
				map.put("reply_time",
						CommUtil.formatShortDate(obj.getReply_time()));// 回复时间
			}
			consult_list.add(map);
		}
		json_map.put("consult_list", consult_list);
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
	 * 手机客户端发布商品咨询信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/app/goods_consult_save.htm")
	public void goods_consult_save(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String user_id,
			String token, String content, String consult_type) {
		boolean verify = true;
		Map json_map = new HashMap();
		if (verify) {
			String url = CommUtil.getURL(request);
			if (!"".equals(CommUtil.null2String(this.configService
					.getSysConfig().getImageWebServer()))) {
				url = this.configService.getSysConfig().getImageWebServer();
			}
			User user = null;
			if (user_id != null && !user_id.equals("") && token != null
					&& !token.equals("")) {
				user = this.userService.getObjById(CommUtil.null2Long(user_id));
			}
			Goods goods = this.goodsService.getObjById(CommUtil
					.null2Long(goods_id));
			Consult obj = new Consult();
			List<Map> maps = new ArrayList<Map>();
			Map map = new HashMap();
			map.put("goods_id", goods.getId());
			map.put("goods_name", goods.getGoods_name());
			map.put("goods_main_photo", goods.getGoods_main_photo().getPath()
					+ "/" + goods.getGoods_main_photo().getName() + "_small."
					+ goods.getGoods_main_photo().getExt());
			map.put("goods_price", goods.getGoods_current_price());
			String goods_domainPath = url + "/goods_" + goods.getId() + ".htm";
			if (this.configService.getSysConfig().isSecond_domain_open()
					&& goods.getGoods_store() != null
					&& goods.getGoods_store().getStore_second_domain() != ""
					&& goods.getGoods_type() == 1) {
				String store_second_domain = "http://"
						+ goods.getGoods_store().getStore_second_domain() + "."
						+ CommUtil.generic_domain(request);
				goods_domainPath = store_second_domain + "/goods_"
						+ goods.getId() + ".htm";
			}
			map.put("goods_domainPath", goods_domainPath);// 商品二级域名路径
			maps.add(map);
			obj.setConsult_type(consult_type);
			obj.setGoods_info(Json.toJson(maps, JsonFormat.compact()));
			obj.setGoods_id(goods.getId());
			if (goods.getGoods_store() != null) {
				obj.setStore_id(goods.getGoods_store().getId());
				obj.setStore_name(goods.getGoods_store().getStore_name());
			} else {
				obj.setWhether_self(1);
			}
			obj.setAddTime(new Date());
			obj.setConsult_content(content);
			if (user != null) {
				obj.setConsult_user_id(user.getId());
				obj.setConsult_user_name(user.getUserName());
			} else {
				obj.setConsult_user_name("游客");
			}
			this.consultService.save(obj);

		} else {
			verify = false;
		}
		json_map.put("ret", CommUtil.null2String(verify));
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
	 * 手机客户端收藏商品请求
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/app/goods_favorite_save.htm")
	public void goods_favorite_save(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String user_id,
			String token, String type) {
		boolean verify = true;
		int ret = 100;// 操作成功
		Map json_map = new HashMap();
		if (verify && user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			if (user != null) {
				if (user.getApp_login_token().equals(token.toLowerCase())) {
					Goods goods = this.goodsService.getObjById(CommUtil
							.null2Long(goods_id));
					if (goods != null) {
						Map params = new HashMap();
						params.put("user_id", CommUtil.null2Long(user_id));
						params.put("goods_id", goods.getId());
						List<Favorite> list = this.favoriteService
								.query("select obj from Favorite obj where obj.user_id=:user_id and obj.goods_id=:goods_id",
										params, -1, -1);
						if (type.equals("add")) {

							if (list.size() == 0) {
								Favorite obj = new Favorite();
								obj.setAddTime(new Date());
								obj.setType(0);
								obj.setUser_name(user.getUserName());
								obj.setUser_id(user.getId());
								obj.setGoods_id(goods.getId());
								obj.setGoods_name(goods.getGoods_name());
								obj.setGoods_photo(goods.getGoods_main_photo()
										.getPath()
										+ "/"
										+ goods.getGoods_main_photo().getName());
								obj.setGoods_photo_ext(goods
										.getGoods_main_photo().getExt());
								if (this.configService.getSysConfig()
										.isSecond_domain_open()) {
									Store store = this.storeService
											.getObjById(obj.getStore_id());
									obj.setGoods_store_second_domain(store
											.getStore_second_domain());
								}
								obj.setGoods_store_id(goods.getGoods_store() == null
										? null
										: goods.getGoods_store().getId());
								obj.setGoods_type(goods.getGoods_type());
								obj.setGoods_current_price(goods
										.getGoods_current_price());
								this.favoriteService.save(obj);
								goods.setGoods_collect(goods.getGoods_collect() + 1);
								this.goodsService.update(goods);
								// 更新lucene索引
								String goods_lucene_path = System
										.getProperty("iskyshopb2b2c.root")
										+ File.separator
										+ "luence"
										+ File.separator + "goods";
								File file = new File(goods_lucene_path);
								if (!file.exists()) {
									CommUtil.createFolder(goods_lucene_path);
								}
								LuceneUtil lucene = LuceneUtil.instance();
								lucene.setIndex_path(goods_lucene_path);
								lucene.update(
										CommUtil.null2String(goods.getId()),
										luceneVoTools.updateGoodsIndex(goods));
								ret = 200;
							} else {
								ret = -300;// 已经关注过该商品
							}
						}
						if (type.equals("del")) {
							this.favoriteService.delete(list.get(0).getId());
							goods.setGoods_collect(goods.getGoods_collect() - 1);
							this.goodsService.update(goods);
							// 更新lucene索引
							String goods_lucene_path = System
									.getProperty("iskyshopb2b2c.root")
									+ File.separator
									+ "luence"
									+ File.separator + "goods";
							File file = new File(goods_lucene_path);
							if (!file.exists()) {
								CommUtil.createFolder(goods_lucene_path);
							}
							LuceneUtil lucene = LuceneUtil.instance();
							lucene.setIndex_path(goods_lucene_path);
							lucene.update(CommUtil.null2String(goods.getId()),
									luceneVoTools.updateGoodsIndex(goods));
							ret = 300;
						}
					} else {
						ret = -350;// 商品信息错误
					}

				} else {
					ret = -400;// 用户信息错误
				}
			} else {
				ret = -400;// 用户信息错误
			}
		} else {
			ret = -500;// 请求错误
		}
		json_map.put("code", ret);
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
	 * 手机客户端商城首页商品详情底部“你可能喜欢的商品”列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/app/like_goods_list.htm")
	public void like_goods_list(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean verify = true;
		Map goods_map = new HashMap();
		List goods_list = new ArrayList();
		if (verify) {
			Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
			Map params = new HashMap();
			params.put("gid", CommUtil.null2Long(id));
			params.put("gc_id", obj.getGc().getId());
			params.put("goods_status", 0);
			List<Goods> lists_goods = this.goodsService
					.query("select obj from Goods obj where obj.id!=:gid and obj.gc.id=:gc_id and obj.goods_status=:goods_status",
							params, 0, 30);
			String url = CommUtil.getURL(request);
			if (!"".equals(CommUtil.null2String(this.configService
					.getSysConfig().getImageWebServer()))) {
				url = this.configService.getSysConfig().getImageWebServer();
			}
			for (Goods goods : lists_goods) {
				Map map = new HashMap();
				map.put("id", goods.getId());
				map.put("name", goods.getGoods_name());
				String goods_main_photo = url// 系统默认商品图片
						+ "/"
						+ this.configService.getSysConfig().getGoodsImage()
								.getPath()
						+ "/"
						+ this.configService.getSysConfig().getGoodsImage()
								.getName();
				if (goods.getGoods_main_photo() != null) {// 商品主图片
					goods_main_photo = url + "/"
							+ goods.getGoods_main_photo().getPath() + "/"
							+ goods.getGoods_main_photo().getName() + "_small."
							+ goods.getGoods_main_photo().getExt();
				}
				map.put("goods_main_photo", goods_main_photo);
				goods_list.add(map);
			}
			goods_map.put("goods_list", goods_list);
		}
		goods_map.put("ret", CommUtil.null2String(verify));
		String json = Json.toJson(goods_map, JsonFormat.compact());
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
	 * 通过会员id查询会员的等级，然后返回该会员等级对应的活动商品价格
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/app/query_goodsActivity_price.htm")
	public void query_user_level(HttpServletRequest request,
			HttpServletResponse response, String user_id, String token,
			String goods_id) {
		boolean verify = true;
		Map json_map = new HashMap();
		if (verify && user_id != null && !user_id.equals("") && token != null
				&& !token.equals("")) {
			User user = this.userService
					.getObjById(CommUtil.null2Long(user_id));
			String level_name = "";
			if (user != null) {
				if (user.getApp_login_token().equals(token.toLowerCase())) {
					Goods goods = this.goodsService.getObjById(CommUtil
							.null2Long(goods_id));
					int temp_count = 0;
					BigDecimal ac_rebate = null;
					if (user_id != null && token != null) {
						if (goods.getActivity_status() == 2) {// 如果是促销商品，根据规格配置价格计算相应配置的促销价格
							ActivityGoods actGoods = this.activityGoodsService
									.getObjById(goods.getActivity_goods_id());
							// 0—铜牌会员1—银牌会员2—金牌会员3—超级会员
							BigDecimal rebate = BigDecimal.valueOf(0.00);
							int level = this.integralViewTools
									.query_user_level(CommUtil.null2String(user
											.getId()));
							if (level == 0) {
								ac_rebate = actGoods.getAct().getAc_rebate();
							} else if (level == 1) {
								ac_rebate = actGoods.getAct().getAc_rebate1();
							} else if (level == 2) {
								ac_rebate = actGoods.getAct().getAc_rebate2();
							} else if (level == 3) {
								ac_rebate = actGoods.getAct().getAc_rebate3();
							}
						}
						level_name = this.integralViewTools
								.query_user_level_name(user_id);
					}
					json_map.put("act_rate", ac_rebate);// 享有折扣率
					json_map.put("level_name", level_name);// 会员等级名称
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
		System.out.println(json);
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

	// 商品组合套装信息
	@RequestMapping("/app/get_goods_suits.htm")
	private void get_goods_suits(HttpServletRequest request,
			HttpServletResponse response, String id) {
		Map json_map = new HashMap();
		List<Map> plan_list = new ArrayList<Map>();
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		if (obj.getCombin_status() == 1) {
			String url = CommUtil.getURL(request);
			if (!"".equals(CommUtil.null2String(this.configService
					.getSysConfig().getImageWebServer()))) {
				url = this.configService.getSysConfig().getImageWebServer();
			}
			List<Map> plans = this.goodsviewTools.getCombinPlans(
					CommUtil.null2String(obj.getId()), "suit");
			for (Map suit : plans) {
				Map plan = new HashMap();
				List<Map> goods_list = new ArrayList<Map>();
				List<Map> objs = this.goodsviewTools.getCombinPlanGoods(suit);
				for (Map temp : objs) {
					Map temp_goods = new HashMap();
					temp_goods.put("goods_id", temp.get("id"));
					temp_goods.put("goods_name", temp.get("name"));
					temp_goods.put("goods_img", url + "/" + temp.get("img"));
					goods_list.add(temp_goods);
				}
				plan.put("plan_price", suit.get("plan_goods_price"));
				plan.put("all_price", suit.get("all_goods_price"));
				plan.put("goods_list", goods_list);
				plan_list.add(plan);
			}
			if (plan_list.size() > 0) {
				json_map.put("plan_list", plan_list);
				json_map.put("verify", true);
			} else {
				json_map.put("verify", false);
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

	// 商品组合配件信息
	@RequestMapping("/app/get_goods_parts.htm")
	private void get_goods_parts(HttpServletRequest request,
			HttpServletResponse response, String id) {
		Map json_map = new HashMap();
		List<Map> plan_list = new ArrayList<Map>();
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		if (obj.getCombin_status() == 1) {
			String url = CommUtil.getURL(request);
			if (!"".equals(CommUtil.null2String(this.configService
					.getSysConfig().getImageWebServer()))) {
				url = this.configService.getSysConfig().getImageWebServer();
			}
			List<Map> plans = this.goodsviewTools.getCombinPlans(
					CommUtil.null2String(obj.getId()), "parts");
			for (Map suit : plans) {
				Map plan = new HashMap();
				List<Map> goods_list = new ArrayList<Map>();
				List<Map> objs = this.goodsviewTools.getCombinPlanGoods(suit);
				for (Map temp : objs) {
					Map temp_goods = new HashMap();
					temp_goods.put("goods_id", temp.get("id"));
					temp_goods.put("goods_name", temp.get("name"));
					temp_goods.put("goods_img", url + "/" + temp.get("img"));
					goods_list.add(temp_goods);
				}
				plan.put("plan_price", suit.get("plan_goods_price"));
				plan.put("all_price", suit.get("all_goods_price"));
				plan.put("goods_list", goods_list);
				plan_list.add(plan);
			}
			if (plan_list.size() > 0) {
				json_map.put("plan_list", plan_list);
				json_map.put("verify", true);
			} else {
				json_map.put("verify", false);
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

}
