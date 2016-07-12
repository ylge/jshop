package com.iskyshop.manage.admin.action;

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

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.CombinPlan;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.query.CombinPlanQueryObject;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.service.ICombinPlanService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.QueryTools;
import com.iskyshop.manage.seller.tools.CombinTools;

/**
 * 
 * <p>
 * Title: CombinSelfManageAction.java
 * </p>
 * 
 * <p>
 * Description: 平台自营组合销售管理控制器
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
 * @date 2014-10-15
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class SelfCombinManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ICombinPlanService combinplanService;
	@Autowired
	private CombinTools combinTools;
	@Autowired
	private QueryTools queryTools;

	@SecurityMapping(title = "组合销售商品列表", value = "/admin/self_combin.htm*", rtype = "admin", rname = "组合销售", rcode = "self_combin", rgroup = "自营")
	@RequestMapping("/admin/self_combin.htm")
	public ModelAndView combin(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String type,
			String combin_status, String goods_name, String beginTime,
			String endTime) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/self_combin_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		CombinPlanQueryObject qo = new CombinPlanQueryObject(currentPage, mv,
				"addTime", "desc");
		qo.addQuery("obj.combin_form", new SysMap("combin_form", 0), "=");// 平台自营
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
			qo.addQuery("obj.main_goods_name", new SysMap("main_goods_name",
					"%" + CommUtil.null2String(goods_name) + "%"), "like");
			mv.addObject("goods_name", goods_name);
		}
		if (beginTime != null && !beginTime.equals("")) {
			qo.addQuery("obj.beginTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
			mv.addObject("beginTime", beginTime);
		}
		if (endTime != null && !endTime.equals("")) {
			qo.addQuery("obj.endTime",
					new SysMap("endTime", CommUtil.formatDate(beginTime)), "<=");
			mv.addObject("endTime", endTime);
		}
		qo.setPageSize(10);
		IPageList pList = this.combinplanService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("combinTools", combinTools);
		return mv;
	}

	@SecurityMapping(title = "组合销售商品添加", value = "/admin/self_combin_add.htm*", rtype = "admin", rname = "组合销售", rcode = "self_combin", rgroup = "自营")
	@RequestMapping("/admin/self_combin_add.htm")
	public ModelAndView combin_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/self_combin_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Date now = new Date();
		mv.addObject("now", CommUtil.formatShortDate(now));
		return mv;
	}

	/**
	 * 验证商品两个组合类型是否存在（组合套装、组合配件）
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = " 验证商品两个组合类型是否存在", value = "/admin/self_verify_combin.htm*", rtype = "admin", rname = "组合销售", rcode = "self_combin", rgroup = "自营")
	@RequestMapping("/admin/self_verify_combin.htm")
	public void verify_combin(HttpServletRequest request,
			HttpServletResponse response, String gid, String combin_mark,
			String id) {
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
	@SecurityMapping(title = "组合销售添加时获得商品全部价格", value = "/admin/self_getPrice.htm*", rtype = "admin", rname = "组合销售", rcode = "self_combin", rgroup = "自营")
	@RequestMapping("/admin/self_getPrice.htm")
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

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "组合套餐设置", value = "/admin/self_combin_set_goods.htm*", rtype = "admin", rname = "组合销售", rcode = "self_combin", rgroup = "自营")
	@RequestMapping("/admin/self_combin_set_goods.htm")
	public ModelAndView combin_set_goods(HttpServletRequest request,
			HttpServletResponse response, String type, String plan_count) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/self_combin_set_goods.html",
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
	@SecurityMapping(title = "组合套餐设置", value = "/admin/self_combin_set_goods_load.htm*", rtype = "admin", rname = "组合销售", rcode = "self_combin", rgroup = "自营")
	@RequestMapping("/admin/self_combin_set_goods_load.htm")
	public ModelAndView combin_set_goods_load(HttpServletRequest request,
			HttpServletResponse response, String goods_name,
			String currentPage, String type, String plan_count) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/self_combin_set_goods_load.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsQueryObject qo = new GoodsQueryObject();
		qo.setCurrentPage(CommUtil.null2Int(currentPage));
		if (!CommUtil.null2String(goods_name).equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%"
					+ CommUtil.null2String(goods_name) + "%"), "like");
		}
		qo.addQuery(
				"(obj.combin_suit_id is null or obj.combin_parts_id is null)",
				null);
		List<String> params = new ArrayList<String>();
		params.add("combin_status");
		this.queryTools.shieldGoodsStatus(qo, params);
		qo.setPageSize(10);
		qo.addQuery("obj.goods_type", new SysMap("goods_type", 0), "=");
		IPageList pList = this.goodsService.list(qo);
		String url = CommUtil.getURL(request)
				+ "/admin/self_combin_set_goods_load.htm";
		mv.addObject("objs", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url, "",
				pList.getCurrentPage(), pList.getPages()));
		mv.addObject("type", type);
		if (plan_count == null || plan_count.equals("")) {
			plan_count = "1";
		}
		mv.addObject("plan_count", plan_count);
		System.out.println("qo:" + qo.getQuery());
		return mv;
	}

	/**
	 * 组合销售方案ajax保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "组合套餐设置", value = "/admin/self_combin_plan_save.htm*", rtype = "admin", rname = "组合销售", rcode = "self_combin", rgroup = "自营")
	@RequestMapping("/admin/self_combin_plan_save.htm")
	public ModelAndView combin_plan_save(HttpServletRequest request,
			HttpServletResponse response, String plan_num,
			String main_goods_id, String old_main_goods_id, String beginTime,
			String endTime, String id, String combin_mark) {
		// 如果是将主商品替换，将就主商品信息还原,并取消其商品状态
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
		main_map.put("url", goods_domainPath);// 商品二级域名
		String img = CommUtil.getURL(request) + "/"
				+ this.configService.getSysConfig().getGoodsImage().getPath()
				+ "/"
				+ this.configService.getSysConfig().getGoodsImage().getName();
		if (main_goods.getGoods_main_photo() != null) {
			img = main_goods.getGoods_main_photo().getPath() + "/"
					+ main_goods.getGoods_main_photo().getName() + "_small."
					+ main_goods.getGoods_main_photo().getExt();
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
						String goods_url = CommUtil.getURL(request) + "/goods_"
								+ obj.getId() + ".htm";
						temp_map.put("url", goods_url);
						String img2 = this.configService.getSysConfig()
								.getGoodsImage().getPath()
								+ "/"
								+ this.configService.getSysConfig()
										.getGoodsImage().getName();
						if (obj.getGoods_main_photo() != null) {
							img2 = obj.getGoods_main_photo().getPath() + "/"
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
		String plan_list_json = Json.toJson(plan_list, JsonFormat.compact());
		combinplan.setCombin_plan_info(plan_list_json);// 组合信息
		combinplan.setCombin_status(1);// 已审核
		combinplan.setBeginTime(CommUtil.formatDate(beginTime));
		combinplan.setEndTime(CommUtil.formatDate(endTime));
		combinplan.setCombin_form(0);// 平台组合
		if (id != null && !id.equals("")) {
			this.combinplanService.update(combinplan);
		} else {
			this.combinplanService.save(combinplan);
		}
		main_goods.setCombin_status(1);// 主商品组合状态
		if (combinplan.getCombin_type() == 0) {
			main_goods.setCombin_suit_id(combinplan.getId());
		} else {
			main_goods.setCombin_parts_id(combinplan.getId());
		}
		this.goodsService.update(main_goods);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/self_combin.htm");
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/admin/self_combin_add.htm");
		mv.addObject("op_title", "组合销售添加成功");
		return mv;
	}

	@SecurityMapping(title = "组合销售商品编辑", value = "/admin/self_combin_plan_edit.htm*", rtype = "admin", rname = "组合销售", rcode = "self_combin", rgroup = "自营")
	@RequestMapping("/admin/self_combin_plan_edit.htm")
	public ModelAndView combin_plan_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/self_combin_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		CombinPlan obj = this.combinplanService.getObjById(CommUtil
				.null2Long(id));
		mv.addObject("edit", true);
		mv.addObject("obj", obj);
		mv.addObject("combinTools", combinTools);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param bargain_time
	 * @return
	 */
	@SecurityMapping(title = "组合销售删除", value = "/admin/self_combin_plan_delete.htm*", rtype = "admin", rname = "组合销售", rcode = "self_combin", rgroup = "自营")
	@RequestMapping("/admin/self_combin_plan_delete.htm")
	public String combin_plan_delete(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String type) {
		if (id != null && !id.equals("")) {
			CombinPlan obj = this.combinplanService.getObjById(CommUtil
					.null2Long(id));
			if (obj != null) {
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
		return "redirect:/admin/self_combin.htm?currentPage=" + currentPage
				+ "&type=" + type;
	}

}
