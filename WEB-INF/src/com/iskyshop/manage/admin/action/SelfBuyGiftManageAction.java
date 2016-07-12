package com.iskyshop.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.BuyGiftQueryObject;
import com.iskyshop.foundation.service.IBuyGiftService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * 
 * <p>
 * Title: BuyGiftSelfManageAction.java
 * </p>
 * 
 * <p>
 * Description:自营满就送促销管理
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
 * @date 2014-9-23
 * 
 * @version iskyshop_b2b2c 2015
 */
@Controller
public class SelfBuyGiftManageAction {
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

	@SecurityMapping(title = "自营满就送列表", value = "/admin/buygift_self_list.htm*", rtype = "admin", rname = "满就送管理", rcode = "buygift_self_manage", rgroup = "自营")
	@RequestMapping("/admin/buygift_self_list.htm")
	public ModelAndView buygift_self_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String gift_status, String beginTime,
			String endTime, String store_name) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/buygift_self_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
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
			mv.addObject("gift_status", gift_status);
		}
		if (beginTime != null && !beginTime.equals("")) {
			qo.addQuery("obj.beginTime",
					new SysMap("beginTime", CommUtil.formatDate(beginTime)),
					">=");
			mv.addObject("beginTime", beginTime);
		}
		if (endTime != null && !endTime.equals("")) {
			qo.addQuery("obj.endTime",
					new SysMap("endTime", CommUtil.formatDate(endTime)), "<=");
			mv.addObject("endTime", endTime);
		}
		qo.addQuery("obj.gift_type", new SysMap("gift_type", 0), "=");
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, BuyGift.class, mv);
		IPageList pList = this.buygiftService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/buygift_list.htm",
				"", params, pList, mv);
		return mv;
	}

	@SecurityMapping(title = "自营满就送添加", value = "/admin/buygift_self_add.htm*", rtype = "admin", rname = "满就送管理", rcode = "buygift_self_manage", rgroup = "自营")
	@RequestMapping("/admin/buygift_self_add.htm")
	public ModelAndView buygift_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/buygift_self_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "自营满就送商品", value = "/admin/buy_goods_self.htm*", rtype = "admin", rname = "满就送管理", rcode = "buygift_self_manage", rgroup = "自营")
	@RequestMapping("/admin/buy_goods_self.htm")
	public ModelAndView buy_goods_self(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/buy_goods_self.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		return mv;
	}

	@SecurityMapping(title = "自营满就送load", value = "/admin/buy_goods_self_load.htm*", rtype = "admin", rname = "满就送管理", rcode = "buygift_self_manage", rgroup = "自营")
	@RequestMapping("/admin/buy_goods_self_load.htm")
	public void buy_goods_self_load(HttpServletRequest request,
			HttpServletResponse response, String goods_name, String gc_id,
			String goods_ids) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("goods_name", "%" + goods_name.trim() + "%");
		params.put("group_buy", 0);
		params.put("goods_type", 0);
		params.put("goods_status", 0);
		params.put("activity_status", 0);
		params.put("order_enough_give_status", 0);
		params.put("order_enough_if_give", 0);
		params.put("enough_reduce", 0);
		params.put("f_sale_type", 0);
		params.put("advance_sale_type", 0);
		params.put("combin_status", 0);
		String query = "select obj from Goods obj where obj.goods_name like:goods_name and obj.enough_reduce=:enough_reduce and obj.order_enough_if_give=:order_enough_if_give and obj.order_enough_give_status=:order_enough_give_status and obj.group_buy=:group_buy and obj.goods_status=:goods_status and obj.goods_type=:goods_type and obj.activity_status=:activity_status and obj.advance_sale_type=:advance_sale_type and obj.f_sale_type=:f_sale_type and obj.combin_status=:combin_status";
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
				if(obj.getGoods_main_photo()!=null){
					map.put("img", obj.getGoods_main_photo().getPath()+"/"+obj.getGoods_main_photo().getName()+ "_small."
							+ obj.getGoods_main_photo().getExt());
				}else{
					map.put("img", this.configService
							.getSysConfig()
							.getGoodsImage()
							.getPath()
							+ "/"
							+ this.configService
									.getSysConfig()
									.getGoodsImage()
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

	@SecurityMapping(title = "自营满就送保存", value = "/admin/buygift_self_save.htm*", rtype = "admin", rname = "满就送管理", rcode = "buygift_self_manage", rgroup = "自营")
	@RequestMapping("/admin/buygift_self_save.htm")
	public ModelAndView buygift_self_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String goods_ids, String gift_ids) {
		WebForm wf = new WebForm();
		String[] ids = goods_ids.split(",");
		String[] gids = gift_ids.split(",");
		Set ids_set = new TreeSet();
		ids_set.addAll(Arrays.asList(ids));
		Set gids_set = new TreeSet();
		gids_set.addAll(Arrays.asList(gids));
		BuyGift buygift = null;
		List<Goods> gift_goods = new ArrayList<Goods>();
		if (id.equals("")) {
			buygift = wf.toPo(request, BuyGift.class);
			buygift.setAddTime(new Date());
		} else {
			BuyGift obj = this.buygiftService.getObjById(Long.parseLong(id));
			buygift = (BuyGift) wf.toPo(request, obj);
		}
		List<Map> goodses = new ArrayList<Map>();
		// 更新商品为参加满就送商品
		for (Object goods_id : ids_set) {
			Goods goods = this.goodsService.getObjById(CommUtil
					.null2Long(goods_id));
			gift_goods.add(goods);
			goods.setOrder_enough_give_status(1);
			goods.setBuyGift_amount(BigDecimal.valueOf(CommUtil
					.null2Double(request.getParameter("condition_amount"))));
			this.goodsService.update(goods);
			Map map = new HashMap();
			map.put("goods_id", goods.getId());
			map.put("goods_name", goods.getGoods_name());
			map.put("goods_main_photo", goods.getGoods_main_photo().getPath()
					+ "/" + goods.getGoods_main_photo().getName() + "_small."
					+ goods.getGoods_main_photo().getExt());
			map.put("big_goods_main_photo", goods.getGoods_main_photo().getPath()
					+ "/" + goods.getGoods_main_photo().getName());
			map.put("goods_price", goods.getGoods_current_price());
			String goods_domainPath = CommUtil.getURL(request) + "/goods_"
					+ goods.getId() + ".htm";
			if (this.configService.getSysConfig().isSecond_domain_open()
					&& goods.getGoods_store().getStore_second_domain() != ""
					&& goods.getGoods_type() == 1) {
				String store_second_domain = "http://"
						+ goods.getGoods_store().getStore_second_domain() + "."
						+ CommUtil.generic_domain(request);
				goods_domainPath = store_second_domain + "/goods_"
						+ goods.getId() + ".htm";
			}
			map.put("goods_domainPath", goods_domainPath);// 商品二级域名路径
			goodses.add(map);
		}
		buygift.setGoods_info(Json.toJson(goodses, JsonFormat.compact()));
		// 更新商品为赠送商品
		List<Map> gifts = new ArrayList<Map>();
		for (Object gift_id : gids_set) {
			Goods goods = this.goodsService.getObjById(CommUtil
					.null2Long(gift_id));
			gift_goods.add(goods);
			int count = CommUtil.null2Int(request.getParameter("gift_"
					+ goods.getId()));
			goods.setOrder_enough_if_give(1);
			Map map = new HashMap();
			if (count >= goods.getGoods_inventory()) {
				map.put("storegoods_count", 1);
			} else {
				map.put("storegoods_count", 0);
				map.put("goods_count", count);
				goods.setGoods_inventory(goods.getGoods_inventory() - count);
			}
			map.put("goods_id", goods.getId());
			map.put("goods_name", goods.getGoods_name());
			map.put("goods_main_photo", goods.getGoods_main_photo().getPath()
					+ "/" + goods.getGoods_main_photo().getName() + "_small."
					+ goods.getGoods_main_photo().getExt());
			map.put("big_goods_main_photo", goods.getGoods_main_photo().getPath()
					+ "/" + goods.getGoods_main_photo().getName());
			map.put("goods_price", goods.getGoods_current_price());
			String goods_domainPath = CommUtil.getURL(request) + "/goods_"
					+ goods.getId() + ".htm";
			if (this.configService.getSysConfig().isSecond_domain_open()
					&& goods.getGoods_store().getStore_second_domain() != ""
					&& goods.getGoods_type() == 1) {
				String store_second_domain = "http://"
						+ goods.getGoods_store().getStore_second_domain() + "."
						+ CommUtil.generic_domain(request);
				goods_domainPath = store_second_domain + "/goods_"
						+ goods.getId() + ".htm";
			}
			map.put("goods_domainPath", goods_domainPath);// 商品二级域名路径
			goods.setBuyGift_amount(BigDecimal.valueOf(CommUtil
					.null2Double(request.getParameter("condition_amount"))));
			this.goodsService.update(goods);
			gifts.add(map);
		}
		buygift.setGift_info(Json.toJson(gifts, JsonFormat.compact()));
		buygift.setGift_status(10);
		if (id.equals("")) {
			this.buygiftService.save(buygift);
		} else
			this.buygiftService.update(buygift);
		for(Goods g :gift_goods){
			g.setBuyGift_id(buygift.getId());
			this.goodsService.update(g);
		}
		System.out.println(buygift.getId());
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/buygift_self_list.htm");
		mv.addObject("op_title", "保存满就送成功");
		return mv;
	}

	@SecurityMapping(title = "自营满就送商品", value = "/admin/buy_gift_self.htm*", rtype = "admin", rname = "满就送管理", rcode = "buygift_self_manage", rgroup = "自营")
	@RequestMapping("/admin/buy_gift_self.htm")
	public ModelAndView buy_gift_self(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/buy_gift_self.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		return mv;
	}


	@SecurityMapping(title = "自营满就送详情", value = "/admin/buygift_self_info.htm*", rtype = "admin", rname = "满就送管理", rcode = "buygift_self_manage", rgroup = "自营")
	@RequestMapping("/admin/buygift_self_info.htm")
	public ModelAndView buygift_self_info(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/buygift_self_info.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			BuyGift buygift = this.buygiftService
					.getObjById(Long.parseLong(id));
			mv.addObject("obj", buygift);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}
	
	@SecurityMapping(title = "自营满就送停止", value = "/admin/buygift_stop.htm*", rtype = "admin", rname = "满就送管理", rcode = "buygift_self_manage", rgroup = "自营")
	@RequestMapping("/admin/buygift_stop.htm")
	public ModelAndView buygift_stop(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		BuyGift bg = this.buygiftService.getObjById(CommUtil.null2Long(id));
		if (bg != null && bg.getGift_type()==0
				&& bg.getGift_status() == 10) {
			bg.setGift_status(20);
			List<Map> maps = Json.fromJson(List.class, bg.getGift_info());
			maps.addAll(Json.fromJson(List.class, bg.getGoods_info()));
			for(Map map : maps){
				Goods goods = this.goodsService.getObjById(CommUtil.null2Long(map.get("goods_id")));
				if(goods!=null){
					goods.setOrder_enough_give_status(0);
					goods.setOrder_enough_if_give(0);
					goods.setBuyGift_id(null);
					goods.setBuyGift_amount(new BigDecimal(0.00));
					this.goodsService.update(goods);
				}
			}
			this.buygiftService.update(bg);
		}
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/buygift_self_list.htm");
		mv.addObject("op_title", "停止满就送成功");
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
	@RequestMapping("/gift_count_adjust.htm")
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
}
