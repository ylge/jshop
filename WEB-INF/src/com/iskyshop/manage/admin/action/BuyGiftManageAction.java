package com.iskyshop.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.beans.BeanUtils;
import com.iskyshop.core.beans.BeanWrapper;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.BuyGift;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.query.BuyGiftQueryObject;
import com.iskyshop.foundation.service.IBuyGiftService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * 
 * <p>
 * Title: BuyGiftManageAction.java
 * </p>
 * 
 * <p>
 * Description:满就送促销管理
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
public class BuyGiftManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IBuyGiftService buygiftService;
	@Autowired
	private IGoodsService goodsService;

	/**
	 * BuyGift列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "满就送列表", value = "/admin/buygift_list.htm*", rtype = "admin", rname = "满就送管理", rcode = "buygift_manage", rgroup = "运营")
	@RequestMapping("/admin/buygift_list.htm")
	public ModelAndView buygift_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String gift_status, String beginTime,
			String endTime, String store_name) {
		ModelAndView mv = new JModelAndView("admin/blue/buygift_list.html",
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
		if (store_name != null && !store_name.equals("")) {
			qo.addQuery("obj.store_name", new SysMap("store_name", "%"
					+ store_name + "%"), "like");
			mv.addObject("store_name", store_name);
		}
		qo.addQuery("obj.gift_type", new SysMap("gift_type", 1), "=");
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, BuyGift.class, mv);
		IPageList pList = this.buygiftService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/buygift_list.htm",
				"", params, pList, mv);
		return mv;
	}

	/**
	 * buygift添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "满就送添加", value = "/admin/buygift_add.htm*", rtype = "admin", rname = "满就送管理", rcode = "buygift_manage", rgroup = "运营")
	@RequestMapping("/admin/buygift_add.htm")
	public ModelAndView buygift_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/buygift_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * buygift编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "满就送编辑", value = "/admin/buygift_info.htm*", rtype = "admin", rname = "满就送管理", rcode = "buygift_manage", rgroup = "运营")
	@RequestMapping("/admin/buygift_info.htm")
	public ModelAndView buygift_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/buygift_info.html",
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

	/**
	 * buygift保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "满就送保存", value = "/admin/buygift_save.htm*", rtype = "admin", rname = "满就送管理", rcode = "buygift_manage", rgroup = "运营")
	@RequestMapping("/admin/buygift_save.htm")
	public ModelAndView buygift_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String gift_status) {
		WebForm wf = new WebForm();
		BuyGift buygift = null;
		if (id.equals("")) {
			buygift = wf.toPo(request, BuyGift.class);
			buygift.setAddTime(new Date());
		} else {
			BuyGift obj = this.buygiftService.getObjById(Long.parseLong(id));
			buygift = (BuyGift) wf.toPo(request, obj);
			buygift.setGift_status(CommUtil.null2Int(gift_status));
			if (CommUtil.null2Int(gift_status) == -10) {
				List<Map> list = new ArrayList<Map>();
				if (obj.getGoods_info() != null
						&& !obj.getGoods_info().equals("")) {
					list = Json.fromJson(List.class, obj.getGoods_info());
				}
				if (obj.getGift_info() != null
						&& !obj.getGift_info().equals("")) {
					list.addAll(Json.fromJson(List.class, obj.getGift_info()));
				}
				for (Map map : list) {
					Goods goods = this.goodsService.getObjById(CommUtil
							.null2Long(map.get("goods_id")));
					if (goods != null) {
						goods.setOrder_enough_give_status(0);
						goods.setOrder_enough_if_give(0);
						this.goodsService.update(goods);
					}
				}
			}
			// 库存更新
			if (CommUtil.null2Int(gift_status) == 10) {
				List<Map> list = new ArrayList<Map>();
				if (obj.getGift_info() != null
						&& !obj.getGift_info().equals("")) {
					list = Json.fromJson(List.class, obj.getGift_info());
				}
				for (Map map : list) {
					if(CommUtil.null2Int(map.get("storegoods_count")) == 0){
						Goods goods = this.goodsService.getObjById(CommUtil
								.null2Long(map.get("goods_id")));
						if (goods != null) {
							int count = CommUtil.null2Int(map.get("goods_count"));
							goods.setGoods_inventory(goods.getGoods_inventory()
									- count);
							goods.setOrder_enough_if_give(1);
							goods.setBuyGift_id(obj.getId());
							this.goodsService.update(goods);
						}
					}
				}
				list.clear();
				if (obj.getGift_info() != null
						&& !obj.getGift_info().equals("")) {
					list = Json.fromJson(List.class, obj.getGoods_info());
				}
				for (Map map : list) {
						Goods goods = this.goodsService.getObjById(CommUtil
								.null2Long(map.get("goods_id")));
						if (goods != null) {
							goods.setOrder_enough_give_status(1);
							goods.setBuyGift_id(obj.getId());
							this.goodsService.update(goods);
						}
				}
				
			}
		}
		if (id.equals("")) {
			this.buygiftService.save(buygift);
		} else
			this.buygiftService.update(buygift);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/buygift_list.htm");
		mv.addObject("op_title", "审核满就送成功");
		return mv;
	}

	@SecurityMapping(title = "满就送删除", value = "/admin/buygift_del.htm*", rtype = "admin", rname = "满就送管理", rcode = "buygift_manage", rgroup = "运营")
	@RequestMapping("/admin/buygift_del.htm")
	public String buygift_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				BuyGift buygift = this.buygiftService.getObjById(Long
						.parseLong(id));
				if (buygift != null) {
					List<Map> maps = Json.fromJson(List.class, buygift.getGift_info());
					maps.addAll(Json.fromJson(List.class, buygift.getGoods_info()));
					for (Map map : maps) {
						Goods goods = this.goodsService.getObjById(CommUtil.null2Long(map
								.get("goods_id")));
						if (goods != null) {
							goods.setOrder_enough_give_status(0);
							goods.setOrder_enough_if_give(0);
							goods.setBuyGift_id(null);
							this.goodsService.update(goods);
						}
					}
					this.buygiftService.delete(Long.parseLong(id));
				}
			}
		}
		return "redirect:buygift_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "满就送ajax", value = "/admin/buygift_ajax.htm*", rtype = "admin", rname = "满就送管理", rcode = "buygift_manage", rgroup = "运营")
	@RequestMapping("/admin/buygift_ajax.htm")
	public void buygift_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		BuyGift obj = this.buygiftService.getObjById(Long.parseLong(id));
		Field[] fields = BuyGift.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.String");
				if (field.getType().getName().equals("int")) {
					clz = Class.forName("java.lang.Integer");
				}
				if (field.getType().getName().equals("boolean")) {
					clz = Class.forName("java.lang.Boolean");
				}
				if (!value.equals("")) {
					val = BeanUtils.convertType(value, clz);
				} else {
					val = !CommUtil.null2Boolean(wrapper
							.getPropertyValue(fieldName));
				}
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		this.buygiftService.update(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}