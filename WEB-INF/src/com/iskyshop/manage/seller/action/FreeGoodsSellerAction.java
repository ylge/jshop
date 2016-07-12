package com.iskyshop.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.ExpressCompany;
import com.iskyshop.foundation.domain.ExpressCompanyCommon;
import com.iskyshop.foundation.domain.FreeApplyLog;
import com.iskyshop.foundation.domain.FreeClass;
import com.iskyshop.foundation.domain.FreeGoods;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.ShipAddress;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.FreeApplyLogQueryObject;
import com.iskyshop.foundation.domain.query.FreeGoodsQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IExpressCompanyCommonService;
import com.iskyshop.foundation.service.IExpressCompanyService;
import com.iskyshop.foundation.service.IFreeApplyLogService;
import com.iskyshop.foundation.service.IFreeClassService;
import com.iskyshop.foundation.service.IFreeGoodsService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IShipAddressService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.FreeTools;
import com.iskyshop.manage.admin.tools.StoreTools;

/**
 * 
 * <p>
 * Title: FreeGoodsSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 卖家0元试用商品控制器
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
 * @date 2014年11月12日
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class FreeGoodsSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IFreeGoodsService freegoodsService;
	@Autowired
	private IFreeClassService freeClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private FreeTools freeTools;
	@Autowired
	private IUserService userService;
	@Autowired
	private IFreeApplyLogService freeapplylogService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IExpressCompanyService expressCompayService;
	@Autowired
	private IShipAddressService shipAddressService;
	@Autowired
	private IExpressCompanyCommonService expressCompanyCommonService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private StoreTools StoreTools;
	@Autowired
	private IMessageService messageService;

	@SecurityMapping(title = "0元试用商品列表", value = "/seller/freegoods_list.htm*", rtype = "seller", rname = "0元试用商品", rcode = "freegoods_seller", rgroup = "0元试用管理")
	@RequestMapping("/seller/freegoods_list.htm")
	public ModelAndView freegoods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String free_name, String beginTime,
			String endTime, String cls, String status) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/freegoods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		SysConfig config = this.configService.getSysConfig();
		if (config.getWhether_free() == 1
				&& user.getStore().getGrade().getWhether_free() == 1) {
			FreeGoodsQueryObject qo = new FreeGoodsQueryObject(currentPage, mv,
					"addTime", "desc");
			qo.addQuery("obj.freeType", new SysMap("freeType", 0), "=");
			qo.addQuery("obj.store_id", new SysMap("store_id", user.getStore()
					.getId()), "=");
			if (free_name != null && !free_name.equals("")) {
				qo.addQuery("obj.free_name", new SysMap("free_name", "%"
						+ free_name + "%"), "like");
				mv.addObject("free_name", free_name);
			}
			if (cls != null && !cls.equals("")) {
				qo.addQuery("obj.class_id",
						new SysMap("class_id", CommUtil.null2Long(cls)), "=");
				mv.addObject("cls_id", cls);
			}
			if (status != null && status.equals("going")) {
				qo.addQuery("obj.freeStatus", new SysMap("freeStatus", 5), "=");
				mv.addObject("status", status);
			}
			if (status != null && status.equals("finish")) {
				qo.addQuery("obj.freeStatus", new SysMap("freeStatus", 10), "=");
				mv.addObject("status", status);
			}
			if (status != null && status.equals("failed")) {
				qo.addQuery("obj.freeStatus", new SysMap("freeStatus", -5), "=");
				mv.addObject("status", status);
			}
			if (status != null && status.equals("waiting")) {
				qo.addQuery("obj.freeStatus", new SysMap("freeStatus", 0), "=");
				mv.addObject("status", status);
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
						new SysMap("endTime", CommUtil.formatDate(endTime)),
						"<=");
				mv.addObject("endTime", endTime);
			}
			IPageList pList = this.freegoodsService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
			List<FreeClass> fcls = this.freeClassService.query(
					"select obj from FreeClass obj", null, -1, -1);
			mv.addObject("fcls", fcls);
			mv.addObject("freeTools", freeTools);
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启0元试用功能");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}

		return mv;
	}

	/**
	 * freegoods添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "0元试用商品添加", value = "/seller/freegoods_add.htm*", rtype = "seller", rname = "0元试用商品添加", rcode = "freegoodsadd_seller", rgroup = "0元试用管理")
	@RequestMapping("/seller/freegoods_add.htm")
	public ModelAndView freegoods_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/freegoods_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		SysConfig config = this.configService.getSysConfig();
		if (config.getWhether_free() == 1
				&& user.getStore().getGrade().getWhether_free() == 1) {
			mv.addObject("currentPage", currentPage);
			List<FreeClass> cls = this.freeClassService.query(
					"select obj from FreeClass obj", null, -1, -1);
			mv.addObject("cls", cls);
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启0元试用功能");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		return mv;
	}

	/**
	 * freegoods保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "0元试用商品保存", value = "/seller/freegoods_save.htm*", rtype = "seller", rname = "0元试用商品添加", rcode = "freegoodsadd_seller", rgroup = "0元试用管理")
	@RequestMapping("/seller/freegoods_save.htm")
	public void freegoods_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String class_id, String goods_id, String default_count) {
		Map json = new HashMap();
		json.put("ret", false);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		SysConfig config = this.configService.getSysConfig();
		if (config.getWhether_free() == 1
				&& user.getStore().getGrade().getWhether_free() == 1) {
			json.put("ret", true);
			WebForm wf = new WebForm();
			FreeGoods freegoods = null;
			if (id.equals("")) {
				freegoods = wf.toPo(request, FreeGoods.class);
				freegoods.setAddTime(new Date());
			} else {
				FreeGoods obj = this.freegoodsService.getObjById(Long
						.parseLong(id));
				freegoods = (FreeGoods) wf.toPo(request, obj);
			}
			String uploadFilePath = this.configService.getSysConfig()
					.getUploadFilePath();
			String saveFilePathName = request.getSession().getServletContext()
					.getRealPath("/")
					+ uploadFilePath + File.separator + "free";
			Map map = new HashMap();
			try {
				String fileName = freegoods.getFree_acc() == null ? ""
						: freegoods.getFree_acc().getName();
				map = CommUtil.saveFileToServer(request, "free_acc",
						saveFilePathName, fileName, null);
				if (fileName.equals("")) {
					if (map.get("fileName") != "") {
						Accessory free_acc = new Accessory();
						free_acc.setName(CommUtil.null2String(map
								.get("fileName")));
						free_acc.setExt(CommUtil.null2String(map.get("mime")));
						free_acc.setSize(BigDecimal.valueOf(CommUtil
								.null2Double(map.get("fileSize"))));
						free_acc.setPath(uploadFilePath + "/free");
						free_acc.setWidth(CommUtil.null2Int(map.get("width")));
						free_acc.setHeight(CommUtil.null2Int(map.get("height")));
						free_acc.setAddTime(new Date());
						this.accessoryService.save(free_acc);
						freegoods.setFree_acc(free_acc);
					}
				} else {
					if (map.get("fileName") != "") {
						Accessory free_acc = freegoods.getFree_acc();
						free_acc.setName(CommUtil.null2String(map
								.get("fileName")));
						free_acc.setExt(CommUtil.null2String(map.get("mime")));
						free_acc.setSize(BigDecimal.valueOf(CommUtil
								.null2Double(map.get("fileSize"))));
						free_acc.setPath(uploadFilePath + "/free");
						free_acc.setWidth(CommUtil.null2Int(map.get("width")));
						free_acc.setHeight(CommUtil.null2Int(map.get("height")));
						free_acc.setAddTime(new Date());
						this.accessoryService.update(free_acc);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			freegoods.setClass_id(CommUtil.null2Long(class_id));
			Goods goods = this.goodsService.getObjById(CommUtil
					.null2Long(goods_id));
			if (goods != null) {
				freegoods.setGoods_name(goods.getGoods_name());
			}
			freegoods.setCurrent_count(CommUtil.null2Int(default_count));
			freegoods.setDefault_count(CommUtil.null2Int(default_count));
			freegoods.setFreeType(0);
			freegoods.setFreeStatus(0);
			freegoods.setStore_id(user.getStore().getId());
			freegoods.setGoods_id(goods.getId());
			if (id.equals("")) {
				this.freegoodsService.save(freegoods);
			} else
				this.freegoodsService.update(freegoods);
			json.put("op_title", "申请0元试用成功");
			json.put("url", CommUtil.getURL(request)
					+ "/seller/freegoods_list.htm");
			this.return_json(Json.toJson(json, JsonFormat.compact()), response);
		} else {
			json.put("op_title", "系统未开启0元试用功能");
			json.put("url", CommUtil.getURL(request) + "/seller/index.htm");
			this.return_json(Json.toJson(json, JsonFormat.compact()), response);
		}
	}

	@SecurityMapping(title = "0元试用商品", value = "/seller/free_goods.htm*", rtype = "seller", rname = "0元试用商品添加", rcode = "freegoodsadd_seller", rgroup = "0元试用管理")
	@RequestMapping("/seller/free_goods.htm")
	public ModelAndView free_goods(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/free_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = this.storeService.getObjById(user.getStore().getId());
		List<GoodsClass> gcs = new ArrayList<GoodsClass>();
		if (store.getGc_detail_info() != null
				&& !store.getGc_detail_info().equals("")) {
			Set<GoodsClass> gc_list = this.StoreTools
					.query_store_DetailGc(store.getGc_detail_info());
			for (GoodsClass obj : gc_list) {
				gcs.add(obj);
			}
		} else {
			Map params = new HashMap();
			params.put("main_gc_id", store.getGc_main_id());
			gcs = this.goodsClassService
					.query("select obj from GoodsClass obj where obj.parent.id=:main_gc_id",
							params, -1, -1);
		}
		mv.addObject("gcs", gcs);
		return mv;
	}

	/**
	 * freegoods编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "0元试用商品编辑", value = "/seller/freegoods_edit.htm*", rtype = "seller", rname = "0元试用商品管理", rcode = "freegoodsadd_seller", rgroup = "0元试用管理")
	@RequestMapping("/seller/freegoods_edit.htm")
	public ModelAndView freegoods_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/freegoods_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		SysConfig config = this.configService.getSysConfig();
		if (config.getWhether_free() == 1
				&& user.getStore().getGrade().getWhether_free() == 1) {
			if (id != null && !id.equals("")) {

				FreeGoods freegoods = this.freegoodsService.getObjById(Long
						.parseLong(id));
				if (freegoods.getFreeStatus() == -5
						|| freegoods.getFreeStatus() == 0) {
					List<FreeClass> cls = this.freeClassService.query(
							"select obj from FreeClass obj", null, -1, -1);
					mv.addObject("cls", cls);
					if (freegoods != null) {
						Goods goods = this.goodsService.getObjById(freegoods
								.getClass_id());
						mv.addObject("goods", goods);
					}
					mv.addObject("obj", freegoods);
					mv.addObject("currentPage", currentPage);
					mv.addObject("edit", true);
				} else {
					mv = new JModelAndView(
							"user/default/sellercenter/seller_error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
					mv.addObject("op_title", "您无此0元试用商品");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/seller/freegoods_list.htm");
				}
			} else {
				mv = new JModelAndView(
						"user/default/sellercenter/seller_error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "您无此0元试用商品");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/freegoods_list.htm");
			}
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启0元试用功能");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}

		return mv;
	}

	@RequestMapping("/seller/free_goods_load.htm")
	public void free_goods_load(HttpServletRequest request,
			HttpServletResponse response, String goods_name, String gc_id) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Map params = new HashMap();
		params.put("goods_name", "%" + goods_name.trim() + "%");
		params.put("goods_type", 1);
		params.put("goods_status", 0);
		params.put("store_id", user.getStore().getId());
		params.put("whether_free", 0);
		String query = "select obj from Goods obj where obj.goods_name like:goods_name and obj.goods_type=:goods_type and obj.goods_status=:goods_status and obj.whether_free=:whether_free and obj.goods_store.id=:store_id";
		if (gc_id != null && !gc_id.equals("")) {
			GoodsClass gc = this.goodsClassService.getObjById(CommUtil
					.null2Long(gc_id));
			Set<Long> ids = this.genericGcIds(gc);
			params.put("ids", ids);
			query = query + " and obj.gc.id in (:ids)";
		}
		List<Goods> goods = this.goodsService.query(query, params, -1, -1);
		List<Map> list = new ArrayList<Map>();
		for (Goods obj : goods) {
			Map map = new HashMap();
			map.put("id", obj.getId());
			map.put("store_price", obj.getStore_price());
			map.put("goods_name", obj.getGoods_name());
			map.put("store_inventory", obj.getGoods_inventory());
			list.add(map);
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

	@SecurityMapping(title = "0元试用商品删除", value = "/seller/free_goods_del.htm*", rtype = "seller", rname = "0元试用商品添加", rcode = "freegoodsadd_seller", rgroup = "0元试用管理")
	@RequestMapping("/seller/free_goods_del.htm")
	public String free_goods_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		for (String id : ids) {
			if (!id.equals("")) {
				FreeGoods freegoods = this.freegoodsService.getObjById(Long
						.parseLong(id));
				if (freegoods != null
						&& freegoods.getFreeStatus() != 5
						&& freegoods.getFreeType() == 0
						&& freegoods.getStore_id().equals(
								user.getStore().getId())) {
					Goods goods = this.goodsService.getObjById(freegoods
							.getGoods_id());
					goods.setWhether_free(0);
					this.goodsService.update(goods);
					this.freegoodsService.delete(Long.parseLong(id));
				}
			}
		}
		return "redirect:freegoods_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "申请记录", value = "/seller/apply_logs.htm*", rtype = "seller", rname = "0元试用商品", rcode = "freegoods_seller", rgroup = "0元试用管理")
	@RequestMapping("/seller/apply_logs.htm")
	public ModelAndView apply_logs(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String id, String userName, String status) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/freeapplylog_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		SysConfig config = this.configService.getSysConfig();
		FreeGoods fg = this.freegoodsService.getObjById(CommUtil.null2Long(id));
		if (fg != null && fg.getStore_id().equals(user.getStore().getId())
				&& config.getWhether_free() == 1
				&& user.getStore().getGrade().getWhether_free() == 1) {
			String url = this.configService.getSysConfig().getAddress();
			if (url == null || url.equals("")) {
				url = CommUtil.getURL(request);
			}
			String params = "";
			FreeApplyLogQueryObject qo = new FreeApplyLogQueryObject(
					currentPage, mv, orderBy, orderType);
			qo.addQuery("obj.freegoods_id",
					new SysMap("freegoods_id", CommUtil.null2Long(id)), "=");
			if (userName != null && !userName.equals("")) {
				qo.addQuery("obj.user_name", new SysMap("userName", "%"
						+ userName + "%"), "like");
				mv.addObject("userName", userName);
			}
			if (status != null && status.equals("yes")) {
				qo.addQuery("obj.apply_status", new SysMap("apply_status", 5),
						"=");
				mv.addObject("status", status);
			}
			if (status != null && status.equals("waiting")) {
				qo.addQuery("obj.apply_status", new SysMap("apply_status", 0),
						"=");
				mv.addObject("status", status);
			}
			if (status != null && status.equals("no")) {
				qo.addQuery("obj.apply_status", new SysMap("apply_status", -5),
						"=");
				mv.addObject("status", status);
			}
			WebForm wf = new WebForm();
			wf.toQueryPo(request, qo, FreeApplyLog.class, mv);
			IPageList pList = this.freeapplylogService.list(qo);
			CommUtil.saveIPageList2ModelAndView(url + "/seller/apply_logs.htm",
					"", params, pList, mv);
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启0元试用功能");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/index.htm");
		}
		mv.addObject("id", id);
		return mv;
	}

	@SecurityMapping(title = "申请记录详情", value = "/seller/apply_log_info.htm*", rtype = "seller", rname = "0元试用商品", rcode = "freegoods_seller", rgroup = "0元试用管理")
	@RequestMapping("/seller/apply_log_info.htm")
	public ModelAndView apply_log_info(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/freeapplylog_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			FreeApplyLog freeapplylog = this.freeapplylogService
					.getObjById(Long.parseLong(id));
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			SysConfig config = this.configService.getSysConfig();
			FreeGoods fg = this.freegoodsService.getObjById(freeapplylog
					.getFreegoods_id());
			if (fg != null && fg.getStore_id().equals(user.getStore().getId())
					&& config.getWhether_free() == 1
					&& user.getStore().getGrade().getWhether_free() == 1) {
				Map params = new HashMap();
				params.put("ecc_type", 0);
				params.put("ecc_store_id", user.getStore().getId());
				List<ExpressCompanyCommon> eccs = this.expressCompanyCommonService
						.query("select obj from ExpressCompanyCommon obj where obj.ecc_type=:ecc_type and obj.ecc_store_id=:ecc_store_id",
								params, -1, -1);
				params.clear();
				params.put("sa_type", 0);
				params.put("sa_user_id", user.getId());
				List<ShipAddress> shipAddrs = this.shipAddressService
						.query("select obj from ShipAddress obj where obj.sa_type=:sa_type and obj.sa_user_id=:sa_user_id order by obj.sa_default desc,obj.sa_sequence asc",
								params, -1, -1);// 按照默认地址倒叙、其他地址按照索引升序排序，保证默认地址在第一位
				mv.addObject("eccs", eccs);
				mv.addObject("shipAddrs", shipAddrs);
				mv.addObject("obj", freeapplylog);
			} else {
				mv = new JModelAndView(
						"user/default/sellercenter/seller_error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "系统未开启0元试用功能");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/index.htm");
			}
		}
		return mv;
	}

	@SecurityMapping(title = "申请记录状态修改", value = "/seller/apply_log_save.htm*", rtype = "seller", rname = "0元试用商品", rcode = "freegoods_seller", rgroup = "0元试用管理")
	@RequestMapping("/seller/apply_log_save.htm")
	public void apply_log_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String status, String shipCode, String ecc_id, String sa_id) {
		Map json = new HashMap();
		json.put("ret", false);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		SysConfig config = this.configService.getSysConfig();
		if (config.getWhether_free() == 1
				&& user.getStore().getGrade().getWhether_free() == 1) {
			WebForm wf = new WebForm();
			FreeApplyLog freeapplylog = null;
			if (id.equals("")) {
				freeapplylog = wf.toPo(request, FreeApplyLog.class);
				freeapplylog.setAddTime(new Date());
			} else {
				FreeApplyLog obj = this.freeapplylogService.getObjById(Long
						.parseLong(id));
				freeapplylog = (FreeApplyLog) wf.toPo(request, obj);
			}

			if (status.equals("yes")) {
				ExpressCompany ec = this.expressCompayService
						.getObjById(CommUtil.null2Long(ecc_id));
				ShipAddress sa = this.shipAddressService.getObjById(CommUtil
						.null2Long(sa_id));
				freeapplylog.setShip_addr_id(sa.getId());
				Area area = this.areaService.getObjById(sa.getSa_area_id());
				freeapplylog.setShip_addr(area.getParent().getParent()
						.getAreaName()
						+ area.getParent().getAreaName()
						+ area.getAreaName()
						+ sa.getSa_addr());
				freeapplylog.setShipCode(shipCode);
				freeapplylog.setShip_addr_id(sa.getId());
				Map json_map = new HashMap();
				json_map.put("express_company_id", ec.getId());
				json_map.put("express_company_name", ec.getCompany_name());
				json_map.put("express_company_mark", ec.getCompany_mark());
				json_map.put("express_company_type", ec.getCompany_type());
				freeapplylog.setExpress_info(Json.toJson(json_map));
				freeapplylog.setApply_status(5);
				FreeGoods fg = this.freegoodsService.getObjById(freeapplylog
						.getFreegoods_id());
				int count = fg.getCurrent_count() - 1;
				fg.setCurrent_count(count);
				if (count <= 0) {
					fg.setFreeStatus(10);
					this.freegoodsService.update(fg);
				}
				String msg_content = "您申请的0元试用："
						+ freeapplylog.getFreegoods_name() + "已通过审核。";
				// 发送系统站内信
				Message msg = new Message();
				msg.setAddTime(new Date());
				msg.setStatus(0);
				msg.setType(0);
				msg.setContent(msg_content);
				msg.setFromUser(SecurityUserHolder.getCurrentUser());
				User buyer = this.userService.getObjById(freeapplylog
						.getUser_id());
				msg.setToUser(buyer);
				this.messageService.save(msg);
			} else {
				freeapplylog.setApply_status(-5);
				freeapplylog.setEvaluate_status(2);
				String msg_content = "您申请的0元试用："
						+ freeapplylog.getFreegoods_name() + "未过审核。";
				// 发送系统站内信
				Message msg = new Message();
				msg.setAddTime(new Date());
				msg.setStatus(0);
				msg.setType(0);
				msg.setContent(msg_content);
				msg.setFromUser(SecurityUserHolder.getCurrentUser());
				User buyer = this.userService.getObjById(freeapplylog
						.getUser_id());
				msg.setToUser(buyer);
				this.messageService.save(msg);
			}
			if (id.equals("")) {
				this.freeapplylogService.save(freeapplylog);
			} else if (freeapplylog.getStore_id().equals(
					user.getStore().getId())) {
				this.freeapplylogService.update(freeapplylog);
			}

			json.put("ret", true);
			json.put("op_title", "修改0元试用申请状态成功");
			json.put("url",
					CommUtil.getURL(request) + "/seller/apply_logs.htm?id="
							+ freeapplylog.getFreegoods_id());
			this.return_json(Json.toJson(json, JsonFormat.compact()), response);
		} else {
			json.put("op_title", "系统未开启0元试用功能");
			json.put("url", CommUtil.getURL(request) + "/seller/index.htm");
			this.return_json(Json.toJson(json, JsonFormat.compact()), response);
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
