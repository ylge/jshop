package com.iskyshop.module.app.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
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
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.FreeGoods;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.Group;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.IntegralGoods;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.query.ActivityGoodsQueryObject;
import com.iskyshop.foundation.domain.query.FreeGoodsQueryObject;
import com.iskyshop.foundation.domain.query.GoodsClassQueryObject;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.domain.query.GroupGoodsQueryObject;
import com.iskyshop.foundation.domain.query.GroupLifeGoodsQueryObject;
import com.iskyshop.foundation.domain.query.IntegralGoodsQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IActivityService;
import com.iskyshop.foundation.service.IFreeGoodsService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IGroupService;
import com.iskyshop.foundation.service.IIntegralGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.ImageTools;
import com.iskyshop.module.app.domain.AppPushLog;
import com.iskyshop.module.app.domain.query.AppPushLogQueryObject;
import com.iskyshop.module.app.service.IAppPushLogService;
import com.iskyshop.module.app.service.IAppPushUserService;
import com.iskyshop.module.app.view.tools.AppPushTools;

/**
 * 
 * <p>
 * Title: AndroidManagAction.java
 * </p>
 * 
 * <p>
 * Description: 手机客户端管理类，用于设置安卓、苹果客户端商品显示情况
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
 * @date 2014-7-21
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class AppManageAction {

	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IGroupGoodsService groupgoodsService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IIntegralGoodsService IntegralGoodsService;
	@Autowired
	private ImageTools imageTools;
	@Autowired
	private IFreeGoodsService freegoodsService;
	@Autowired
	private IAppPushUserService appPushuserService;
	@Autowired
	private IAppPushLogService appPushLogService;
	@Autowired
	private AppPushTools appPushTools;
	@Autowired
	private IGroupLifeGoodsService grouplifegoodsService;

	@SecurityMapping(title = "手机客户端商品", value = "/admin/mobile_goods.htm*", rtype = "admin", rname = "手机端商品", rcode = "admin_mobile_goods", rgroup = "运营")
	@RequestMapping("/admin/mobile_goods.htm")
	public ModelAndView mobile_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name, String query_type) {
		ModelAndView mv = new JModelAndView("admin/blue/mobile_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, Goods.class, mv);
		qo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		if (goods_name != null && !goods_name.equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%"
					+ goods_name + "%"), "like");
		}
		if (query_type != null && !query_type.equals("")) {
			if (query_type.equals("0")) {
				qo.addQuery("obj.mobile_hot", new SysMap("mobile_hot", 1), "=");
			}
			if (query_type.equals("1")) {
				qo.addQuery("obj.mobile_recommend", new SysMap(
						"mobile_recommend", 1), "=");
			}
		}
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		mv.addObject("goods_name", goods_name);
		mv.addObject("query_type", query_type);
		return mv;
	}

	@SecurityMapping(title = "手机商品AJAX更新", value = "/admin/mobile_goods_ajax.htm*", rtype = "admin", rname = "手机端商品", rcode = "admin_mobile_goods", rgroup = "运营")
	@RequestMapping("/admin/mobile_goods_ajax.htm")
	public void mobile_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String type, String mulitId, String currentPage) throws IOException {
		boolean val = false;
		if ("all".equals(type)) {
			String ids[] = mulitId.split(",");
			for (String temp_id : ids) {
				if (!temp_id.equals("")) {
					Goods obj = this.goodsService.getObjById(Long
							.parseLong(temp_id));
					if (fieldName.equals("mobile_recommend")) {
						obj.setMobile_recommend(1);
						obj.setMobile_recommendTime(new Date());
						val = true;
					}
					if (fieldName.equals("mobile_hot")) {
						obj.setMobile_hot(1);
						obj.setMobile_hotTime(new Date());
						val = true;
					}
					this.goodsService.update(obj);
				}
			}
			response.sendRedirect("/admin/mobile_goods.htm?currentPage="
					+ currentPage);
		} else {
			Goods obj = this.goodsService.getObjById(Long.parseLong(id));
			if (fieldName.equals("mobile_recommend")) {
				if (obj.getMobile_recommend() == 1) {
					obj.setMobile_recommend(0);
					obj.setMobile_recommendTime(null);
					val = false;
				} else {
					obj.setMobile_recommend(1);
					obj.setMobile_recommendTime(new Date());
					val = true;
				}
			}
			if (fieldName.equals("mobile_hot")) {
				if (obj.getMobile_hot() == 1) {
					obj.setMobile_hot(0);
					obj.setMobile_hotTime(null);
					val = false;
				} else {
					obj.setMobile_hot(1);
					obj.setMobile_hotTime(new Date());
					val = true;
				}
			}
			this.goodsService.update(obj);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SecurityMapping(title = "手机商品分类图片列表", value = "/admin/mobile_class_list.htm*", rtype = "admin", rname = "手机端分类", rcode = "admin_mobile_class", rgroup = "运营")
	@RequestMapping("/admin/mobile_class_list.htm")
	public ModelAndView mobile_class_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/mobile_class_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsClassQueryObject qo = new GoodsClassQueryObject(currentPage, mv,
				"sequence", "asc");
		qo.addQuery("obj.parent.id is null", null);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, GoodsClass.class, mv);
		IPageList pList = this.goodsClassService.list(qo);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		CommUtil.saveIPageList2ModelAndView(
				url + "/admin/mobile_class_img.htm", "", "", pList, mv);
		mv.addObject("imageTools", imageTools);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "手机商品分类图片上传", value = "/admin/mobile_class_img_upload.htm*", rtype = "admin", rname = "手机端分类", rcode = "admin_mobile_class", rgroup = "运营")
	@RequestMapping("/admin/mobile_class_img_upload.htm")
	public void mobile_class_img_upload(HttpServletRequest request,
			HttpServletResponse response, String id) {
		GoodsClass goodsClass = this.goodsClassService.getObjById(CommUtil
				.null2Long(id));
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "class_icon_app";
		Map map = new HashMap();
		Map map_app = new HashMap();
		try {
			map_app = CommUtil.saveFileToServer(request, "upload_img" + id,
					saveFilePathName, "", null);
			if (goodsClass.getApp_icon() != null
					&& !goodsClass.getApp_icon().equals("")) {
				Accessory icon = this.accessoryService.getObjById(CommUtil
						.null2Long(goodsClass.getApp_icon()));
				if (icon != null) {
					boolean ret = this.accessoryService.delete(icon.getId());
					if (ret) {
						CommUtil.del_acc(request, icon);
					}
				}
			}
			if (map_app.get("fileName") != "") {
				Accessory photo = new Accessory();
				photo.setName(CommUtil.null2String(map_app.get("fileName")));
				photo.setExt(CommUtil.null2String(map_app.get("mime")));
				photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map_app
						.get("fileSize"))));
				photo.setPath(uploadFilePath + "/class_icon_app");
				photo.setWidth(CommUtil.null2Int(map_app.get("width")));
				photo.setHeight(CommUtil.null2Int(map_app.get("height")));
				photo.setAddTime(new Date());
				this.accessoryService.save(photo);
				goodsClass.setApp_icon("" + photo.getId());
				this.goodsClassService.update(goodsClass);
				map.put("src", CommUtil.getURL(request) + "/" + photo.getPath()
						+ "/" + photo.getName());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	@SecurityMapping(title = "手机团购商品列表", value = "/admin/mobile_groupgoods.htm*", rtype = "admin", rname = "手机端团购", rcode = "admin_mobile_group", rgroup = "运营")
	@RequestMapping("/admin/mobile_groupgoods.htm")
	public ModelAndView mobile_groupgoods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String gg_name, String goods_name,
			String mobile_recommend) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/mobile_groupgoods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("beginTime", new Date());
		params.put("endTime", new Date());
		params.put("status", 0);
		List<Group> groups = this.groupService
				.query("select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime and obj.status=:status and obj.group_type=0",
						params, -1, -1);
		if (groups.size() > 0) {
			GroupGoodsQueryObject qo = new GroupGoodsQueryObject(currentPage,
					mv, orderBy, orderType);
			WebForm wf = new WebForm();
			wf.toQueryPo(request, qo, GroupGoods.class, mv);
			qo.addQuery("obj.group.id", new SysMap("group_id", groups.get(0)
					.getId()), "=");
			qo.addQuery("obj.gg_status", new SysMap("obj_gg_status", 1), "=");
			if (goods_name != null && !goods_name.equals("")) {
				qo.addQuery(
						"obj.gg_goods.goods_name",
						new SysMap("obj_goods_name", "%"
								+ CommUtil.null2String(goods_name) + "%"),
						"like");
			}
			if (gg_name != null && !gg_name.equals("")) {
				qo.addQuery(
						"obj.gg_name",
						new SysMap("gg_name", "%"
								+ CommUtil.null2String(gg_name) + "%"), "like");
			}
			if (mobile_recommend != null && !mobile_recommend.equals("")) {
				qo.addQuery(
						"obj.mobile_recommend",
						new SysMap("mobile_recommend", CommUtil
								.null2Int(mobile_recommend)), "=");
			}
			IPageList pList = this.groupgoodsService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("goods_name", goods_name);
			mv.addObject("gg_name", gg_name);
			mv.addObject("mobile_recommend", mobile_recommend);
		}
		return mv;
	}

	@SecurityMapping(title = "手机团购生活商品列表", value = "/admin/mobile_grouplife.htm*", rtype = "admin", rname = "手机端团购", rcode = "admin_mobile_group", rgroup = "运营")
	@RequestMapping("/admin/mobile_grouplife.htm")
	public ModelAndView mobile_grouplife(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String gg_name, String goods_name,
			String mobile_recommend) {
		ModelAndView mv = new JModelAndView("admin/blue/mobile_grouplife.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("beginTime", new Date());
		params.put("endTime", new Date());
		params.put("status", 0);
		List<Group> groups = this.groupService
				.query("select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime and obj.status=:status and obj.group_type=1",
						params, -1, -1);
		if (groups.size() > 0) {
			GroupLifeGoodsQueryObject qo = new GroupLifeGoodsQueryObject(
					currentPage, mv, orderBy, orderType);
			WebForm wf = new WebForm();
			wf.toQueryPo(request, qo, GroupGoods.class, mv);
			qo.addQuery("obj.group.id", new SysMap("group_id", groups.get(0)
					.getId()), "=");
			qo.addQuery("obj.group_status", new SysMap("obj_gg_status", 1), "=");
			if (goods_name != null && !goods_name.equals("")) {
				qo.addQuery(
						"obj.gg_goods.goods_name",
						new SysMap("obj_goods_name", "%"
								+ CommUtil.null2String(goods_name) + "%"),
						"like");
			}
			if (gg_name != null && !gg_name.equals("")) {
				qo.addQuery(
						"obj.gg_name",
						new SysMap("gg_name", "%"
								+ CommUtil.null2String(gg_name) + "%"), "like");
			}
			if (mobile_recommend != null && !mobile_recommend.equals("")) {
				qo.addQuery(
						"obj.mobile_recommend",
						new SysMap("mobile_recommend", CommUtil
								.null2Int(mobile_recommend)), "=");
			}
			IPageList pList = this.grouplifegoodsService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("goods_name", goods_name);
			mv.addObject("gg_name", gg_name);
			mv.addObject("mobile_recommend", mobile_recommend);
		}
		return mv;
	}

	@SecurityMapping(title = "手机团购商品更新", value = "/admin/mobile_groupgoods_ajax.htm*", rtype = "admin", rname = "手机端团购", rcode = "admin_mobile_group", rgroup = "运营")
	@RequestMapping("/admin/mobile_groupgoods_ajax.htm")
	public void mobile_groupgoods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String type,
			String mulitId, String currentPage) throws ClassNotFoundException,
			IOException {
		boolean val = false;
		if ("all".equals(type)) {
			String ids[] = mulitId.split(",");
			for (String temp_id : ids) {
				if (!temp_id.equals("")) {
					GroupGoods obj = this.groupgoodsService.getObjById(Long
							.parseLong(temp_id));
					obj.setMobile_recommend(1);
					obj.setMobile_recommendTime(new Date());
					this.groupgoodsService.update(obj);
				}
			}
			response.sendRedirect("/admin/mobile_groupgoods.htm?currentPage="
					+ currentPage);
		} else {
			GroupGoods obj = this.groupgoodsService.getObjById(Long
					.parseLong(id));
			if (obj.getMobile_recommend() == 1) {
				obj.setMobile_recommend(0);
				obj.setMobile_recommendTime(null);
				val = false;
			} else {
				obj.setMobile_recommend(1);
				obj.setMobile_recommendTime(new Date());
				val = true;
			}
			this.groupgoodsService.update(obj);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SecurityMapping(title = "手机生活购商品更新", value = "/admin/mobile_grouplife_ajax.htm*", rtype = "admin", rname = "手机端团购", rcode = "admin_mobile_group", rgroup = "运营")
	@RequestMapping("/admin/mobile_grouplife_ajax.htm")
	public void mobile_grouplife_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String type,
			String mulitId, String currentPage) throws ClassNotFoundException,
			IOException {
		boolean val = false;
		if ("all".equals(type)) {
			String ids[] = mulitId.split(",");
			for (String temp_id : ids) {
				if (!temp_id.equals("")) {
					GroupLifeGoods obj = this.grouplifegoodsService
							.getObjById(Long.parseLong(temp_id));
					obj.setMobile_recommend(1);
					obj.setMobile_recommendTime(new Date());
					this.grouplifegoodsService.update(obj);
				}
			}
			response.sendRedirect("/admin/mobile_grouplife.htm?currentPage="
					+ currentPage);
		} else {
			GroupLifeGoods obj = this.grouplifegoodsService.getObjById(Long
					.parseLong(id));
			if (obj.getMobile_recommend() == 1) {
				obj.setMobile_recommend(0);
				obj.setMobile_recommendTime(null);
				val = false;
			} else {
				obj.setMobile_recommend(1);
				obj.setMobile_recommendTime(new Date());
				val = true;
			}
			this.grouplifegoodsService.update(obj);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SecurityMapping(title = "手机活动商品列表", value = "/admin/mobile_activitygoods.htm*", rtype = "admin", rname = "手机端促销", rcode = "admin_mobile_activity", rgroup = "运营")
	@RequestMapping("/admin/mobile_activitygoods.htm")
	public ModelAndView mobile_activitygoods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/mobile_activitygoods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ActivityGoodsQueryObject qo = new ActivityGoodsQueryObject(currentPage,
				mv, orderBy, orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, GroupGoods.class, mv);
		if (goods_name != null && !goods_name.equals("")) {
			qo.addQuery("obj.ag_goods.goods_name", new SysMap("goods_name", "%"
					+ goods_name + "%"), "like");
		}
		qo.addQuery("obj.act.ac_begin_time", new SysMap("ac_begin_time",
				new Date()), "<=");
		qo.addQuery("obj.act.ac_end_time",
				new SysMap("ac_end_time", new Date()), ">=");
		qo.addQuery("obj.act.ac_status", new SysMap("ac_status", 1), "=");
		qo.addQuery("obj.ag_status", new SysMap("ag_status", 1), "=");
		IPageList pList = this.activityGoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("goods_name", goods_name);
		return mv;
	}

	@SecurityMapping(title = "手机活动商品更新", value = "/admin/mobile_activitygoods_ajax.htm*", rtype = "admin", rname = "手机端促销", rcode = "admin_mobile_activity", rgroup = "运营")
	@RequestMapping("/admin/mobile_activitygoods_ajax.htm")
	public void mobile_activitygoods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String type,
			String mulitId, String currentPage) throws ClassNotFoundException,
			IOException {
		boolean val = false;
		if ("all".equals(type)) {
			String ids[] = mulitId.split(",");
			for (String temp_id : ids) {
				if (!temp_id.equals("")) {
					ActivityGoods obj = this.activityGoodsService
							.getObjById(Long.parseLong(temp_id));
					obj.setMobile_recommend(1);
					obj.setMobile_recommendTime(new Date());
					this.activityGoodsService.update(obj);
				}
			}
			response.sendRedirect("/admin/mobile_activitygoods.htm?currentPage="
					+ currentPage);
		} else {
			ActivityGoods obj = this.activityGoodsService.getObjById(Long
					.parseLong(id));
			if (obj.getMobile_recommend() == 1) {
				obj.setMobile_recommend(0);
				obj.setMobile_recommendTime(null);
				val = false;
			} else {
				obj.setMobile_recommend(1);
				obj.setMobile_recommendTime(new Date());
				val = true;
			}
			this.activityGoodsService.update(obj);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SecurityMapping(title = "手机试用商品列表", value = "/admin/mobile_integralgoods.htm*", rtype = "admin", rname = "手机端积分", rcode = "admin_mobile_", rgroup = "运营")
	@RequestMapping("/admin/mobile_integralgoods.htm")
	public ModelAndView mobile_integralgoods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/mobile_integralgoods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		IntegralGoodsQueryObject qo = new IntegralGoodsQueryObject(currentPage,
				mv, orderBy, orderType);
		if (goods_name != null && !goods_name.equals("")) {
			qo.addQuery("obj.ig_goods_name", new SysMap("ig_goods_name", "%"
					+ goods_name + "%"), "like");
		}
		qo.addQuery("obj.ig_show", new SysMap("ig_show", true), "=");
		IPageList pList = this.IntegralGoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("goods_name", goods_name);
		return mv;
	}

	@SecurityMapping(title = "手机端积分商品更新", value = "/admin/mobile_integralgoods_ajax.htm*", rtype = "admin", rname = "手机端团购", rcode = "admin_mobile_group", rgroup = "运营")
	@RequestMapping("/admin/mobile_integralgoods_ajax.htm")
	public void mobile_integralgoods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String type,
			String mulitId, String currentPage) throws ClassNotFoundException,
			IOException {
		boolean val = false;
		if ("all".equals(type)) {
			String ids[] = mulitId.split(",");
			for (String temp_id : ids) {
				if (!temp_id.equals("")) {
					IntegralGoods obj = this.IntegralGoodsService
							.getObjById(Long.parseLong(temp_id));
					obj.setMobile_recommend(1);
					obj.setMobile_recommendTime(new Date());
					this.IntegralGoodsService.update(obj);
				}
			}
			response.sendRedirect("/admin/mobile_integralgoods.htm?currentPage="
					+ currentPage);
		} else {
			IntegralGoods obj = this.IntegralGoodsService.getObjById(Long
					.parseLong(id));
			if (obj.getMobile_recommend() == 1) {
				obj.setMobile_recommend(0);
				obj.setMobile_recommendTime(null);
				val = false;
			} else {
				obj.setMobile_recommend(1);
				obj.setMobile_recommendTime(new Date());
				val = true;
			}
			this.IntegralGoodsService.update(obj);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SecurityMapping(title = "手机端试用商品列表", value = "/admin/mobile_freegoods.htm*", rtype = "admin", rname = "手机端试用", rcode = "admin_mobile_free", rgroup = "运营")
	@RequestMapping("/admin/mobile_freegoods.htm")
	public ModelAndView mobile_freegoods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String free_name) {
		ModelAndView mv = new JModelAndView("admin/blue/mobile_freegoods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		FreeGoodsQueryObject qo = new FreeGoodsQueryObject(currentPage, mv,
				"addTime", "desc");
		if (free_name != null && !free_name.equals("")) {
			qo.addQuery("obj.free_name", new SysMap("free_name", "%"
					+ free_name + "%"), "like");
		}
		qo.addQuery("obj.freeStatus", new SysMap("freeStatus", 5), "=");
		IPageList pList = this.freegoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("free_name", free_name);
		return mv;
	}

	@SecurityMapping(title = "手机端试用商品更新", value = "/admin/mobile_freegoods_ajax.htm*", rtype = "admin", rname = "手机端试用", rcode = "admin_mobile_free", rgroup = "运营")
	@RequestMapping("/admin/mobile_freegoods_ajax.htm")
	public void mobile_freegoods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String type,
			String mulitId, String currentPage) throws ClassNotFoundException,
			IOException {
		boolean val = false;
		if ("all".equals(type)) {
			String ids[] = mulitId.split(",");
			for (String temp_id : ids) {
				if (!temp_id.equals("")) {
					FreeGoods obj = this.freegoodsService.getObjById(Long
							.parseLong(temp_id));
					obj.setMobile_recommend(1);
					obj.setMobile_recommendTime(new Date());
					this.freegoodsService.update(obj);
				}
			}
			response.sendRedirect("/admin/mobile_freegoods.htm?currentPage="
					+ currentPage);
		} else {
			FreeGoods obj = this.freegoodsService
					.getObjById(Long.parseLong(id));
			if (obj.getMobile_recommend() == 1) {
				obj.setMobile_recommend(0);
				obj.setMobile_recommendTime(null);
				val = false;
			} else {
				obj.setMobile_recommend(1);
				obj.setMobile_recommendTime(new Date());
				val = true;
			}
			this.freegoodsService.update(obj);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(val);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 推送消息设置
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "手机端推送消息设置", value = "/admin/mobile_push_set.htm*", rtype = "admin", rname = "手机端试用", rcode = "admin_mobile_free", rgroup = "运营")
	@RequestMapping("/admin/mobile_push_set.htm")
	public ModelAndView mobile_push_set(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/mobile_push_set.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	/**
	 * 推送消息设置保存
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "手机端推送消息设置", value = "/admin/mobile_push_set_save.htm*", rtype = "admin", rname = "手机端试用", rcode = "admin_mobile_free", rgroup = "运营")
	@RequestMapping("/admin/mobile_push_set_save.htm")
	public ModelAndView mobile_push_set_save(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig obj = this.configService.getSysConfig();
		WebForm wf = new WebForm();
		SysConfig sysConfig = null;
		if (id.equals("")) {
			sysConfig = wf.toPo(request, SysConfig.class);
			sysConfig.setAddTime(new Date());
		} else {
			sysConfig = (SysConfig) wf.toPo(request, obj);
		}
		if (id.equals("")) {
			this.configService.save(sysConfig);
		} else {
			this.configService.update(sysConfig);
		}
		mv.addObject("op_title", "推送设置保存成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/mobile_push.htm");
		return mv;
	}

	/**
	 * 推送消息列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "手机端推送消息列表", value = "/admin/mobile_push.htm*", rtype = "admin", rname = "手机端试用", rcode = "admin_mobile_free", rgroup = "运营")
	@RequestMapping("/admin/mobile_push.htm")
	public ModelAndView mobile_push(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/mobile_push.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		AppPushLogQueryObject qo = new AppPushLogQueryObject(currentPage, mv,
				"addTime", orderType);
		IPageList pList = this.appPushLogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		return mv;
	}

	/**
	 * 推送消息添加
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "手机端推送消息添加", value = "/admin/mobile_push_add.htm*", rtype = "admin", rname = "手机端推送", rcode = "admin_mobile_push", rgroup = "运营")
	@RequestMapping("/admin/mobile_push_add.htm")
	public ModelAndView mobile_push_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/mobile_push_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String date = CommUtil.formatShortDate(new Date());
		mv.addObject("date", date);

		return mv;
	}

	/**
	 * 推送消息发送
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "手机端推送消息保存", value = "/admin/mobile_push_save.htm*", rtype = "admin", rname = "手机端推送", rcode = "admin_mobile_push", rgroup = "运营")
	@RequestMapping("/admin/mobile_push_save.htm")
	public String mobile_push_save(HttpServletRequest request,
			HttpServletResponse response, String sendtime_date,
			String sendtime_hour, String sendtime_min, String device,
			String sendtime_type) {
		WebForm wf = new WebForm();
		AppPushLog appPushLog = new AppPushLog();
		appPushLog = wf.toPo(request, AppPushLog.class);
		appPushLog.setAddTime(new Date());
		appPushLog.setSend_type(CommUtil.null2Int(sendtime_type));
		if (appPushLog.getSend_type() == 0) {
			appPushLog.setStatus(1);
			appPushLog.setSendtime(new Date());
		} else {
			appPushLog.setStatus(0);
			Date send_time = CommUtil.formatDate(sendtime_date);
			send_time.setHours(CommUtil.null2Int(sendtime_hour));
			send_time.setMinutes(CommUtil.null2Int(sendtime_min));
			appPushLog.setSendtime(send_time);
		}
		if (device.indexOf(1) > 0 && device.indexOf(2) > 0) {
			appPushLog.setDevice(0);
		} else {
			appPushLog.setDevice(CommUtil.null2Int(device));
		}
		this.appPushLogService.save(appPushLog);
		if (appPushLog.getSend_type() != 1) {// 非定时发送
			if (appPushLog.getDevice() == 0) {
				this.appPushTools.android_push(appPushLog);// 向所有安卓用户推送
				this.appPushTools.ios_push(appPushLog);// 向所有ios用户推送
			} else if (appPushLog.getDevice() == 1) {
				this.appPushTools.android_push(appPushLog);// 向所有安卓用户推送
			} else if (appPushLog.getDevice() == 2) {
				this.appPushTools.ios_push(appPushLog);// 向所有ios用户推送
			}
		}
		return "redirect:/admin/mobile_push.htm";
	}

	/**
	 * 推送消息删除
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "手机端推送消息删除", value = "/admin/mobile_push_delete.htm*", rtype = "admin", rname = "手机端推送", rcode = "admin_mobile_push", rgroup = "运营")
	@RequestMapping("/admin/mobile_push_delete.htm")
	public String mobile_push_delete(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String mulitId) {
		String ids[] = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				this.appPushLogService.delete(CommUtil.null2Long(id));
			}
		}
		return "redirect:/admin/mobile_push.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "手机端推送消息重新推送", value = "/admin/mobile_repush.htm*", rtype = "admin", rname = "手机端试用", rcode = "admin_mobile_free", rgroup = "运营")
	@RequestMapping("/admin/mobile_repush.htm")
	public String mobile_repush(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		AppPushLog appPushLog = this.appPushLogService.getObjById(CommUtil
				.null2Long(id));
		if (appPushLog.getDevice() == 0) {
			this.appPushTools.android_push(appPushLog);// 向所有安卓用户推送
			this.appPushTools.ios_push(appPushLog);// 向所有ios用户推送
		} else if (appPushLog.getDevice() == 1) {
			this.appPushTools.android_push(appPushLog);// 向所有安卓用户推送
		} else if (appPushLog.getDevice() == 2) {
			this.appPushTools.ios_push(appPushLog);// 向所有ios用户推送
		}
		appPushLog.setStatus(1);
		appPushLog.setSendtime(new Date());
		this.appPushLogService.update(appPushLog);
		return "redirect:/admin/mobile_push.htm?currentPage=" + currentPage;
	}

}
