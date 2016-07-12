package com.iskyshop.module.weixin.manage.admin.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.FreeGoods;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.Group;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.Role;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.ActivityGoodsQueryObject;
import com.iskyshop.foundation.domain.query.FreeGoodsQueryObject;
import com.iskyshop.foundation.domain.query.GoodsBrandQueryObject;
import com.iskyshop.foundation.domain.query.GoodsClassQueryObject;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.domain.query.GroupGoodsQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IFreeGoodsService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IGroupService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.ImageTools;
import com.iskyshop.module.weixin.domain.VMenu;
import com.iskyshop.module.weixin.service.IVMenuService;
import com.iskyshop.module.weixin.view.tools.Base64Tools;
import com.iskyshop.module.weixin.view.tools.EmojiTools;
import com.iskyshop.module.weixin.view.tools.GetWxToken;
import com.iskyshop.module.weixin.view.tools.WeixinTools;
import com.iskyshop.msg.MsgTools;

/**
 * 
 * <p>
 * Title: WeixinManageAction.java
 * </p>
 * 
 * <p>
 * Description:后台微商城管理
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
 * @date 2014-11-20
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class WeixinManageAction {

	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private WeixinTools weixinTools;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IVMenuService vMenuService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private Base64Tools base64Tools;
	@Autowired
	private EmojiTools emojiTools;
	@Autowired
	private ImageTools imageTools;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private IGroupGoodsService groupgoodsService;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IFreeGoodsService freeGoodsService;
	@Autowired
	private IRoleService roleService;

	@SecurityMapping(title = "微商城配置", value = "/admin/weixin_plat_set.htm*", rtype = "admin", rname = "微信基本设置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping("/admin/weixin_plat_set.htm")
	public ModelAndView weixin_plat_set(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/weixin_plat_set.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "基本设置保存", value = "/admin/weixin_plat_set_save.htm*", rtype = "admin", rname = "微信基本设置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping("/admin/weixin_plat_set_save.htm")
	public ModelAndView weixin_plat_set_save(HttpServletRequest request,
			HttpServletResponse response, String id, String weixin_account,
			String weixin_token, String weixin_appId, String weixin_appSecret,
			String weixin_welecome_content, String weixin_store) {
		SysConfig obj = this.configService.getSysConfig();
		WebForm wf = new WebForm();
		SysConfig sysConfig = null;
		if (id.equals("")) {
			sysConfig = wf.toPo(request, SysConfig.class);
			sysConfig.setAddTime(new Date());
		} else {
			sysConfig = (SysConfig) wf.toPo(request, obj);
		}
		sysConfig.setWeixin_store(CommUtil.null2Int(weixin_store));
		sysConfig.setWeixin_account(weixin_account);
		sysConfig.setWeixin_token(weixin_token);
		sysConfig.setWeixin_appId(weixin_appId);
		sysConfig.setWeixin_appSecret(weixin_appSecret);
		sysConfig.setWeixin_welecome_content(weixin_welecome_content);
		// 图片上传开始logo
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "system";
		Map map = new HashMap();
		try {
			String fileName = this.configService.getSysConfig()
					.getStore_weixin_logo() == null ? "" : this.configService
					.getSysConfig().getStore_weixin_logo().getName();
			map = CommUtil.saveFileToServer(request, "store_weixin_logo",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory store_weixin_logo = new Accessory();
					store_weixin_logo.setName(CommUtil.null2String(map
							.get("fileName")));
					store_weixin_logo.setExt((String) map.get("mime"));
					store_weixin_logo.setSize(BigDecimal.valueOf(Double
							.parseDouble(map.get("fileSize").toString())));
					store_weixin_logo.setPath(uploadFilePath + "/system");
					store_weixin_logo.setWidth((Integer) map.get("width"));
					store_weixin_logo.setHeight((Integer) map.get("height"));
					store_weixin_logo.setAddTime(new Date());
					this.accessoryService.save(store_weixin_logo);
					sysConfig.setStore_weixin_logo(store_weixin_logo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory store_weixin_logo = sysConfig
							.getStore_weixin_logo();
					store_weixin_logo.setName(CommUtil.null2String(map
							.get("fileName")));
					store_weixin_logo.setExt(CommUtil.null2String(map
							.get("mime")));
					store_weixin_logo.setSize(BigDecimal.valueOf((Double
							.parseDouble(map.get("fileSize").toString()))));
					store_weixin_logo.setPath(uploadFilePath + "/system");
					store_weixin_logo.setWidth(CommUtil.null2Int(map
							.get("width")));
					store_weixin_logo.setHeight(CommUtil.null2Int(map
							.get("height")));
					this.accessoryService.update(store_weixin_logo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 微商城二维码
		map.clear();
		try {
			map = CommUtil.saveFileToServer(request, "qr_img",
					saveFilePathName, null, null);
			String fileName = sysConfig.getWeixin_qr_img() != null ? sysConfig
					.getWeixin_qr_img().getName() : "";
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory qr_img = new Accessory();
					qr_img.setName(CommUtil.null2String(map.get("fileName")));
					qr_img.setExt(CommUtil.null2String(map.get("mime")));
					qr_img.setSize(BigDecimal.valueOf(Double.parseDouble(map
							.get("fileSize").toString())));
					qr_img.setPath(uploadFilePath + "/system");
					qr_img.setWidth(CommUtil.null2Int(map.get("width")));
					qr_img.setHeight(CommUtil.null2Int(map.get("heigh")));
					qr_img.setAddTime(new Date());
					this.accessoryService.save(qr_img);
					sysConfig.setWeixin_qr_img(qr_img);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory qr_img = sysConfig.getWeixin_qr_img();
					qr_img.setName(CommUtil.null2String(map.get("fileName")));
					qr_img.setExt(CommUtil.null2String(map.get("mime")));
					qr_img.setSize(BigDecimal.valueOf(Double.parseDouble(map
							.get("fileSize").toString())));
					qr_img.setPath(uploadFilePath + "/system");
					qr_img.setWidth(CommUtil.null2Int(map.get("width")));
					qr_img.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(qr_img);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.clear();
		try {
			String fileName = this.configService.getSysConfig()
					.getWelcome_img() == null ? "" : this.configService
					.getSysConfig().getWelcome_img().getName();
			map = CommUtil.saveFileToServer(request, "welcome_img",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory wel_img = new Accessory();
					wel_img.setName(CommUtil.null2String(map.get("fileName")));
					wel_img.setExt((String) map.get("mime"));
					wel_img.setSize(BigDecimal.valueOf(Double.parseDouble(map
							.get("fileSize").toString())));
					wel_img.setPath(uploadFilePath + "/system");
					wel_img.setWidth((Integer) map.get("width"));
					wel_img.setHeight((Integer) map.get("height"));
					wel_img.setAddTime(new Date());
					this.accessoryService.save(wel_img);
					sysConfig.setWelcome_img(wel_img);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory wel_img = sysConfig.getWelcome_img();
					wel_img.setName(CommUtil.null2String(map.get("fileName")));
					wel_img.setExt(CommUtil.null2String(map.get("mime")));
					wel_img.setSize(BigDecimal.valueOf((Double.parseDouble(map
							.get("fileSize").toString()))));
					wel_img.setPath(uploadFilePath + "/system");
					wel_img.setWidth(CommUtil.null2Int(map.get("width")));
					wel_img.setHeight(CommUtil.null2Int(map.get("height")));
					this.accessoryService.update(wel_img);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (id.equals("")) {
			this.configService.save(sysConfig);
		} else {
			this.configService.update(sysConfig);
		}
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "基本设置成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/weixin_plat_set.htm");
		return mv;
	}

	@SecurityMapping(title = "微信客户端商品", value = "/admin/weixin_goods.htm*", rtype = "admin", rname = "微商城商品", rcode = "admin_weixin_goods", rgroup = "运营")
	@RequestMapping("/admin/weixin_goods.htm")
	public ModelAndView weixin_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name, String query_type) {
		ModelAndView mv = new JModelAndView("admin/blue/weixin_goods.html",
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
				qo.addQuery("obj.weixin_hot", new SysMap("weixin_hot", 1), "=");
			}
			if (query_type.equals("1")) {
				qo.addQuery("obj.weixin_recommend", new SysMap(
						"weixin_recommend", 1), "=");
			}
		}
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		mv.addObject("goods_name", goods_name);
		mv.addObject("query_type", query_type);
		return mv;
	}

	@SecurityMapping(title = "微信商品AJAX更新", value = "/admin/weixin_goods_ajax.htm*", rtype = "admin", rname = "微商城商品", rcode = "admin_weixin_goods", rgroup = "运营")
	@RequestMapping("/admin/weixin_goods_ajax.htm")
	public void weixin_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName) {
		Goods obj = this.goodsService.getObjById(Long.parseLong(id));
		boolean val = false;
		if (fieldName.equals("weixin_recommend")) {
			if (obj.getWeixin_recommend() == 1) {
				obj.setWeixin_recommend(0);
				obj.setWeixin_recommendTime(null);
				val = false;
			} else {
				obj.setWeixin_recommend(1);
				obj.setWeixin_recommendTime(new Date());
				val = true;
			}
		}
		if (fieldName.equals("weixin_hot")) {
			if (obj.getWeixin_hot() == 1) {
				obj.setWeixin_hot(0);
				obj.setWeixin_hotTime(null);
				val = false;
			} else {
				obj.setWeixin_hot(1);
				obj.setWeixin_hotTime(new Date());
				val = true;
			}
		}
		this.goodsService.update(obj);
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

	@SecurityMapping(title = "微信商品分类图片列表", value = "/admin/weixin_class_img.htm*", rtype = "admin", rname = "微商城分类", rcode = "admin_weixin_class", rgroup = "运营")
	@RequestMapping("/admin/weixin_class_img.htm")
	public ModelAndView weixin_class_img(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/weixin_goods_class_list.html",
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
				url + "/admin/weixin_class_img.htm", "", "", pList, mv);
		mv.addObject("imageTools", imageTools);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "微信商品分类图片上传", value = "/admin/weixin_class_img_upload.htm*", rtype = "admin", rname = "微商城分类", rcode = "admin_weixin_class", rgroup = "运营")
	@RequestMapping("/admin/weixin_class_img_upload.htm")
	public void weixin_class_img_upload(HttpServletRequest request,
			HttpServletResponse response, String id) {
		GoodsClass goodsClass = this.goodsClassService.getObjById(CommUtil
				.null2Long(id));
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "class_icon_wx";
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
				photo.setPath(uploadFilePath + "/class_icon_wx");
				photo.setWidth(CommUtil.null2Int(map_app.get("width")));
				photo.setHeight(CommUtil.null2Int(map_app.get("height")));
				photo.setAddTime(new Date());
				this.accessoryService.save(photo);
				goodsClass.setWx_icon("" + photo.getId());
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

	@SecurityMapping(title = "微信团购商品列表", value = "/admin/weixin_groupgoods.htm*", rtype = "admin", rname = "微商城团购", rcode = "admin_weixin_group", rgroup = "运营")
	@RequestMapping("/admin/weixin_groupgoods.htm")
	public ModelAndView weixin_groupgoods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String gg_name, String goods_name,
			String weixin_recommend) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/weixin_groupgoods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("beginTime", new Date());
		params.put("endTime", new Date());
		params.put("status", 0);
		List<Group> groups = this.groupService
				.query("select obj from Group obj where obj.beginTime<=:beginTime and obj.endTime>=:endTime and obj.status=:status",
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
			if (weixin_recommend != null && !weixin_recommend.equals("")) {
				qo.addQuery(
						"obj.weixin_recommend",
						new SysMap("weixin_recommend", CommUtil
								.null2Int(weixin_recommend)), "=");
			}
			IPageList pList = this.groupgoodsService.list(qo);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("goods_name", goods_name);
			mv.addObject("gg_name", gg_name);
			mv.addObject("weixin_recommend", weixin_recommend);
		}
		return mv;
	}

	@SecurityMapping(title = "微信团购商品更新", value = "/admin/weixin_groupgoods_ajax.htm*", rtype = "admin", rname = "微商城团购", rcode = "admin_weixin_group", rgroup = "运营")
	@RequestMapping("/admin/weixin_groupgoods_ajax.htm")
	public void weixin_groupgoods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		GroupGoods obj = this.groupgoodsService.getObjById(Long.parseLong(id));
		boolean val = false;
		if (obj.getWeixin_recommend() == 1) {
			obj.setWeixin_recommend(0);
			obj.setWeixin_recommendTime(null);
			val = false;
		} else {
			obj.setWeixin_recommend(1);
			obj.setWeixin_recommendTime(new Date());
			val = true;
		}
		this.groupgoodsService.update(obj);
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

	@SecurityMapping(title = "微信品牌", value = "/admin/weixin_brand.htm*", rtype = "admin", rname = "微商城品牌", rcode = "admin_weixin_brand", rgroup = "运营")
	@RequestMapping("/admin/weixin_brand.htm")
	public ModelAndView weixin_brand(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String brand_name) {
		ModelAndView mv = new JModelAndView("admin/blue/weixin_brand.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsBrandQueryObject qo = new GoodsBrandQueryObject(currentPage, mv,
				orderBy, orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, GoodsBrand.class, mv);
		if (brand_name != null && !brand_name.equals("")) {
			qo.addQuery("obj.name", new SysMap("name", "%" + brand_name + "%"),
					"like");
		}
		qo.addQuery("obj.audit", new SysMap("audit", 1), "=");
		IPageList pList = this.goodsBrandService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("brand_name", brand_name);
		return mv;
	}

	@SecurityMapping(title = "微信品牌更新", value = "/admin/weixin_brand_ajax.htm*", rtype = "admin", rname = "微商城品牌", rcode = "admin_weixin_brand", rgroup = "运营")
	@RequestMapping("/admin/weixin_brand_ajax.htm")
	public void weixin_brand_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String value)
			throws ClassNotFoundException {
		GoodsBrand obj = this.goodsBrandService.getObjById(Long.parseLong(id));
		boolean val = false;
		if (obj.getWeixin_recommend() == 1) {
			obj.setWeixin_recommend(0);
			obj.setWeixin_recommendTime(null);
			val = false;
		} else {
			obj.setWeixin_recommend(1);
			obj.setWeixin_recommendTime(new Date());
			val = true;
		}
		this.goodsBrandService.update(obj);
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

	@SecurityMapping(title = "微信活动商品列表", value = "/admin/weixin_activitygoods.htm*", rtype = "admin", rname = "微商城活动", rcode = "admin_weixin_activity", rgroup = "运营")
	@RequestMapping("/admin/weixin_activitygoods.htm")
	public ModelAndView weixin_activitygoods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/weixin_activitygoods.html",
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

	@SecurityMapping(title = "微信活动商品更新", value = "/admin/weixin_activitygoods_ajax.htm*", rtype = "admin", rname = "微商城活动", rcode = "admin_weixin_activity", rgroup = "运营")
	@RequestMapping("/admin/weixin_activitygoods_ajax.htm")
	public void weixin_activitygoods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String value)
			throws ClassNotFoundException {
		ActivityGoods obj = this.activityGoodsService.getObjById(Long
				.parseLong(id));
		boolean val = false;
		if (obj.getWeixin_recommend() == 1) {
			obj.setWeixin_recommend(0);
			obj.setWeixin_recommendTime(null);
			val = false;
		} else {
			obj.setWeixin_recommend(1);
			obj.setWeixin_recommendTime(new Date());
			val = true;
		}
		this.activityGoodsService.update(obj);
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

	@SecurityMapping(title = "微信0元试用", value = "/admin/weixin_freegoods.htm*", rtype = "admin", rname = "微商城0元试用", rcode = "admin_weixin_weixin_freegoods", rgroup = "运营")
	@RequestMapping("/admin/weixin_freegoods.htm")
	public ModelAndView weixin_freegoods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String freegoods_name) {
		ModelAndView mv = new JModelAndView("admin/blue/weixin_freegoods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		FreeGoodsQueryObject qo = new FreeGoodsQueryObject(currentPage, mv,
				orderBy, orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, FreeGoods.class, mv);
		if (freegoods_name != null && !freegoods_name.equals("")) {
			qo.addQuery("obj.free_name", new SysMap("name", "%"
					+ freegoods_name + "%"), "like");
		}
		qo.addQuery("obj.freeStatus", new SysMap("freeStatus", 5), "=");
		IPageList pList = this.freeGoodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("freegoods_name", freegoods_name);
		return mv;
	}

	@SecurityMapping(title = "微信品牌更新", value = "/admin/weixin_freegoods_ajax.htm*", rtype = "admin", rname = "微商城活动", rcode = "admin_weixin_brand", rgroup = "运营")
	@RequestMapping("/admin/weixin_freegoods_ajax.htm")
	public void weixin_freegoods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String value)
			throws ClassNotFoundException {
		FreeGoods obj = this.freeGoodsService.getObjById(Long.parseLong(id));
		boolean val = false;
		if (obj.getWeixin_recommend() == 1) {
			obj.setWeixin_recommend(0);
			obj.setWeixin_recommendTime(null);
			val = false;
		} else {
			obj.setWeixin_recommend(1);
			obj.setWeixin_recommendTime(new Date());
			val = true;
		}
		this.freeGoodsService.update(obj);
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

	@RequestMapping("/weixin_develop_action.htm")
	public void weixin_develop_action(HttpServletRequest request,
			HttpServletResponse response, String signature, String timestamp,
			String nonce, String echostr, String redriver)
			throws InterruptedException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/xml");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(ServletInputStream) request.getInputStream(), "UTF-8"));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			System.out.println("信息：" + sb.toString());
			Map<String, String> map = this.weixinTools.parse_xml(sb.toString());
			System.out.println(sb.toString());
			String ToUserName = map.get("ToUserName");
			String FromUserName = map.get("FromUserName");
			String CreateTime = map.get("CreateTime");
			String MsgType = map.get("MsgType");
			String Content = CommUtil.null2String(map.get("Content"));
			System.out.println(map.get("Content"));
			String MsgId = map.get("MsgId");
			String Event = CommUtil.null2String(map.get("Event"));
			System.out.println("Event" + Event);
			String EventKey = CommUtil.null2String(map.get("EventKey"));
			System.out.println("EventKey" + EventKey);
			String scene_id = CommUtil.null2String(map.get("scene_id"));
			// System.out.println("scene_id="+map.get("scene_id"));
			String user_id = CommUtil.null2String(map.get("user_id"));
			// System.out.println("user_id="+user_id);
			String reply_xml = "";
			String reply_title = "";
			String reply_content = "";
			String reply_bottom = "";
			String web_url = CommUtil.getURL(request);
			String reply_all = this.configService.getSysConfig()
					.getWeixin_welecome_content();

			if (Event.equals("") || Event == null) {

			} else {
				if (Event.equals("subscribe")) {// 订阅，回复欢迎词
					String[] key = EventKey.split("_");
					if (key[0].endsWith("qrscene")) {
						System.out.println("key[0]=" + key[0]);
						System.out.println("key[1]=" + key[1]);
						// this.user_add(FromUserName, request);

						this.binding(FromUserName, key[1], request);

					} else {

						reply_all = this.configService.getSysConfig()
								.getWeixin_welecome_content();
						this.user_add(FromUserName, request);
						this.send_welcome(map, request);
					}

				}
				if (Event.equals("unsubscribe")) {// 取消订阅，不需要回复任何内容
					String openid = map.get("FromUserName");
					Map params = new HashMap();
					params.put("openId", openid);
					List<User> users = this.userService
							.query("select obj from User obj where obj.openId=:openId",
									params, -1, -1);
					if (users.size() == 1) {
						User user = users.get(0);
						user.setOpenId("");
						this.userService.update(user);
						//this.userService.delete(user.getId());
					}
				}

				if (Event.equals("click") || Event.equals("CLICK")) {// 返回菜单key对应的内容
					Map menu_map = new HashMap();
					menu_map.put("menu_key", EventKey);
					List<VMenu> vMeuns = this.vMenuService
							.query("select obj from VMenu obj where obj.menu_key=:menu_key",
									menu_map, -1, -1);
					if (vMeuns.size() > 0) {
						reply_all = vMeuns.get(0).getMenu_key_content();
					}
				}
				if (EventKey.equals("findany") || EventKey.equals("FINDANY")) {// 返回菜单key对应的内容
					Map menu_map = new HashMap();
					menu_map.put("menu_key", EventKey);
					List<VMenu> vMeuns = this.vMenuService
							.query("select obj from VMenu obj where obj.menu_key=:menu_key",
									menu_map, -1, -1);
					if (vMeuns.size() > 0) {
						reply_all = vMeuns.get(0).getMenu_key_content();
						reply_xml = this.weixinTools.reply_xml("text", map,
								reply_all, request);
					}
				}
			}
			PrintWriter writer;
			writer = response.getWriter();
			if (echostr != null && !echostr.equals("")) {
				System.out.println(echostr);
				writer.print(echostr);
			} else if (map.get("EventKey") != null
					&& !"".equals(map.get("EventKey"))) {
				System.out.println(reply_xml);
				writer.print(reply_xml);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@SecurityMapping(title = "微商城菜单配置", value = "/admin/weixin_plat_menu.htm*", rtype = "admin", rname = "微信基本设置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping("/admin/weixin_plat_menu.htm")
	public ModelAndView weixin_plat_menu(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/weixin_plat_menu.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		List<VMenu> weixin_menus = this.vMenuService
				.query("select obj from VMenu obj where  obj.parent.id is null order by obj.menu_sequence asc",
						params, -1, -1);
		mv.addObject("weixin_menus", weixin_menus);
		return mv;
	}

	@SecurityMapping(title = "微商城菜单添加", value = "/admin/weixin_menu_add.htm*", rtype = "admin", rname = "微信基本设置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping("/admin/weixin_menu_add.htm")
	public ModelAndView weixin_menu_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String menu_id,
			String pmenu_id) {
		ModelAndView mv = new JModelAndView("admin/blue/weixin_menu_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		VMenu obj = this.vMenuService.getObjById(CommUtil.null2Long(menu_id));
		mv.addObject("obj", obj);
		mv.addObject("pmenu_id", obj == null ? pmenu_id
				: (obj.getParent() != null ? obj.getParent().getId() : ""));
		return mv;
	}

	@SecurityMapping(title = "微商城菜单保存", value = "/admin/weixin_menu_save.htm*", rtype = "admin", rname = "微信基本设置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping("/admin/weixin_menu_save.htm")
	public String weixin_menu_save(HttpServletRequest request,
			HttpServletResponse response, String menu_id, String pmenu_id,
			String menu_url, String menu_key_content, String menu_key)
			throws IOException {
		WebForm wf = new WebForm();
		VMenu parent = this.vMenuService.getObjById(CommUtil
				.null2Long(pmenu_id));
		String url = menu_url == null ? "" : menu_url;
		String content = menu_key_content == null ? "" : menu_key_content;
		String appId = CommUtil.null2String(this.configService.getSysConfig()
				.getWeixin_appId());
		String oauth_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ appId
				+ "&redirect_uri="
				+ CommUtil.getURL(request)
				+ "/catchopenid.htm&response_type=code&scope=snsapi_base&state="
				+ menu_key + "#wechat_redirect";
		if (!CommUtil.null2String(menu_id).equals("")) {
			VMenu obj = this.vMenuService.getObjById(CommUtil
					.null2Long(menu_id));
			obj = (VMenu) wf.toPo(request, obj);
			if (url.trim().equals(content.trim())) {
				obj.setMenu_url(oauth_url);
				obj.setMenu_key_content(menu_key_content);
			}
			obj.setParent(parent);
			this.vMenuService.update(obj);
		} else {
			VMenu obj = wf.toPo(request, VMenu.class);
			if (url.trim().equals(content.trim())) {
				obj.setMenu_url(oauth_url);
				obj.setMenu_key_content(menu_key_content);
			}
			obj.setParent(parent);
			this.vMenuService.save(obj);
		}
		return "redirect:weixin_plat_menu.htm";
	}

	@SecurityMapping(title = "微商城菜单删除", value = "/admin/weixin_menu_delete.htm*", rtype = "admin", rname = "微信基本设置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping("/admin/weixin_menu_delete.htm")
	public String weixin_menu_delete(HttpServletRequest request,
			HttpServletResponse response, String menu_id) throws IOException {
		if (!CommUtil.null2String(menu_id).equals("")) {
			this.vMenuService.delete(CommUtil.null2Long(menu_id));
		}
		return "redirect:weixin_plat_menu.htm";
	}

	@SecurityMapping(title = "微商城菜单创建", value = "/admin/weixin_plat_menu_create.htm*", rtype = "admin", rname = "微信基本设置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping("/admin/weixin_plat_menu_create.htm")
	public void weixin_plat_menu_create(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		int ret = this.createMenu();
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "微商城菜单验证", value = "/admin/weixin_menukey_verify.htm*", rtype = "admin", rname = "微信基本设置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping("/admin/weixin_menukey_verify.htm")
	public void weixin_menukey_verify(HttpServletRequest request,
			HttpServletResponse response, String menu_id, String menu_key,
			String store_id) throws IOException {
		boolean ret = true;
		Map params = new HashMap();
		params.put("menu_key", menu_key);
		params.put("menu_id", CommUtil.null2Long(menu_id));
		List<VMenu> VMenus = this.vMenuService
				.query("select obj from VMenu obj where obj.menu_key=:menu_key and obj.id!=:menu_id",
						params, -1, -1);
		if (VMenus != null && VMenus.size() > 0) {
			ret = false;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping("/catchopenid.htm")
	public void catchopenid(HttpServletRequest request,
			HttpServletResponse response) {
		String code = request.getParameter("code");
		if (code != null && !code.equals("")) {
			String state = request.getParameter("state");

			// 获得appid
			String appId = CommUtil.null2String(this.configService
					.getSysConfig().getWeixin_appId());
			String secret = CommUtil.null2String(this.configService
					.getSysConfig().getWeixin_appSecret());
			String action = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
					+ appId
					+ "&secret="
					+ secret
					+ "&code="
					+ code
					+ "&grant_type=authorization_code";
			try {
				URL urlGet = new URL(action);
				HttpURLConnection http = (HttpURLConnection) urlGet
						.openConnection();
				http.setRequestMethod("GET"); // 必须是get方式请求
				http.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				http.setDoOutput(true);
				http.setDoInput(true);
				System.setProperty("sun.net.client.defaultConnectTimeout",
						"30000");// 连接超时30秒
				System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
				System.setProperty("jsse.enableSNIExtension", "false");
				http.connect();
				InputStream is = http.getInputStream();
				int size = is.available();
				byte[] jsonBytes = new byte[size];
				is.read(jsonBytes);
				String message = new String(jsonBytes, "UTF-8");
				System.out.println("message:" + message);
				Map map = Json.fromJson(HashMap.class, message);
				String openid = map.get("openid").toString();
				String[] s = state.split("_");
				if (s.length == 3) {
					if (s[0].equals("order")) {
						response.sendRedirect(CommUtil.getURL(request)
								+ "/weixin/pay/wx_pay.htm?openid=" + openid
								+ "&id=" + s[1] + "&type=" + s[2]);
					}
				}
				Map params = new HashMap();
				params.put("openid", openid);
				List<User> user = this.userService.query(
						"select obj from User obj where obj.openId=:openid",
						params, -1, -1);
				if (user.size() == 1) {
					String userName = user.get(0).getUserName();
					String password = user.get(0).getPassword();
					params.clear();
					params.put("key", state);
					List<VMenu> vms = this.vMenuService
							.query("select obj from VMenu obj where obj.menu_key=:key",
									params, -1, -1);
					String his_url=vms.size()>0?vms.get(0).getMenu_key_content():CommUtil.getURL(request)
							+ "/wap/index.htm";
					request.getSession(false).setAttribute(
							"his_url",
							his_url);
					request.getSession().removeAttribute("verify_code");
					response.sendRedirect(CommUtil.getURL(request)
							+ "/iskyshop_login.htm?username="
							+ CommUtil.encode(userName) + "&password="
							+ Globals.THIRD_ACCOUNT_LOGIN + password
							+ "&encode=true&login_role=user");
				}
				// System.out.println("message：" + message);
				// System.out.println("accessToken：" + accessToken);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private int createMenu() throws IOException {
		int ret = 0;
		Map weixin_plat_menu = new HashMap();
		List<Weixin_Menu> list = new ArrayList<Weixin_Menu>();
		Map params = new HashMap();
		List<VMenu> vmenus = this.vMenuService
				.query("select obj from VMenu obj where obj.parent.id is null order by obj.menu_sequence asc",
						params, -1, -1);
		for (VMenu vmenu : vmenus) {
			Weixin_Menu menu = new Weixin_Menu();
			menu.setKey(vmenu.getMenu_key());
			menu.setName(vmenu.getMenu_name());
			menu.setType(vmenu.getMenu_type());
			menu.setUrl(vmenu.getMenu_url());
			for (VMenu c_vmenu : vmenu.getChilds()) {
				Weixin_Menu c_menu = new Weixin_Menu();
				c_menu.setKey(c_vmenu.getMenu_key());
				c_menu.setName(c_vmenu.getMenu_name());
				c_menu.setType(c_vmenu.getMenu_type());
				c_menu.setUrl(c_vmenu.getMenu_url());
				menu.getSub_button().add(c_menu);
			}
			list.add(menu);
		}
		weixin_plat_menu.put("button", list);
		System.out.println(Json.toJson(weixin_plat_menu, JsonFormat.compact()));
		String user_define_menu = Json.toJson(weixin_plat_menu,
				JsonFormat.compact());
		String appId = CommUtil.null2String(this.configService.getSysConfig()
				.getWeixin_appId());
		String appSecret = CommUtil.null2String(this.configService
				.getSysConfig().getWeixin_appSecret());
		String access_token = GetWxToken.instance()
				.getWxToken(appId, appSecret);
		String action = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token="
				+ access_token;
		try {
			URL url = new URL(action);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			http.setDoOutput(true);
			http.setDoInput(true);
			System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
			System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
			System.setProperty("jsse.enableSNIExtension", "false");
			http.connect();
			OutputStream os = http.getOutputStream();
			os.write(user_define_menu.getBytes("UTF-8"));// 传入参数
			os.flush();
			os.close();
			InputStream is = http.getInputStream();
			int size = is.available();
			byte[] jsonBytes = new byte[size];
			is.read(jsonBytes);
			String message = new String(jsonBytes, "UTF-8");
			System.out.println("menu:" + message);
			Map ret_map = Json.fromJson(HashMap.class, message);
			ret = CommUtil.null2Int(ret_map.get("errcode"));
			is.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Async
	private int send_welcome(Map map, HttpServletRequest request) {
		int ret = 0;
		String appId = CommUtil.null2String(this.configService.getSysConfig()
				.getWeixin_appId());
		String appSecret = CommUtil.null2String(this.configService
				.getSysConfig().getWeixin_appSecret());
		String access_token = GetWxToken.instance()
				.getWxToken(appId, appSecret);
		String action = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="
				+ access_token;
		Map text_json = new LinkedHashMap();
		text_json.put("touser", map.get("FromUserName").toString());
		text_json.put("msgtype", "news");
		List<Map> maps = new ArrayList<Map>();
		Map map1 = new HashMap();
		map1.put("title", "欢迎关注"
				+ this.configService.getSysConfig().getWebsiteName());
		map1.put("description", this.configService.getSysConfig()
				.getWeixin_welecome_content());
		map1.put("url", CommUtil.getURL(request) + "/wap/index.htm");
		map1.put("picurl", CommUtil.getURL(request) + "/"
				+ this.configService.getSysConfig().getWelcome_img().getPath()
				+ "/"
				+ this.configService.getSysConfig().getWelcome_img().getName());
		maps.add(map1);
		Map map2 = new HashMap();
		map2.put("articles", maps);
		text_json.put("news", map2);
		String news_json = Json.toJson(text_json);
		try {
			URL url = new URL(action);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			http.setDoOutput(true);
			http.setDoInput(true);
			System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
			System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
			System.setProperty("jsse.enableSNIExtension", "false");
			http.connect();
			OutputStream os = http.getOutputStream();
			os.write(news_json.getBytes("UTF-8"));// 传入参数
			System.out.println(news_json);
			os.flush();
			os.close();
			InputStream is = http.getInputStream();
			int size = is.available();
			byte[] jsonBytes = new byte[size];
			is.read(jsonBytes);
			String message = new String(jsonBytes, "UTF-8");
			System.out.println("menu:" + message);
			Map ret_map = Json.fromJson(HashMap.class, message);
			ret = CommUtil.null2Int(ret_map.get("errcode"));
			is.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	// 绑定微商城号
	public void binding(String openId, String user_id,
			HttpServletRequest request) {
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		System.out.println("openId=" + openId);
		if (user != null) {
			user.setOpenId(openId);
			System.out.println(user.getOpenId());
			this.userService.update(user);

		}

	}

	// 生成二维码
	public String code(String user_id) {
		String ret = "";
		User user = null;
		String appId = CommUtil.null2String(this.configService.getSysConfig()
				.getWeixin_appId());
		String appSecret = CommUtil.null2String(this.configService
				.getSysConfig().getWeixin_appSecret());
		String access_token = GetWxToken.instance()
				.getWxToken(appId, appSecret);
		System.out.println("access_token" + access_token);
		String action = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="
				+ access_token;
		Map new_json = new LinkedHashMap();
		new_json.put("expire_seconds", "1800");
		new_json.put("action_name", "QR_SCENE");
		Map new_json_child = new LinkedHashMap();
		Map new_json_child_child = new LinkedHashMap();
		new_json_child_child.put("scene_id", user_id);
		new_json_child.put("scene", new_json_child_child);
		new_json.put("action_info", new_json_child);
		String json = Json.toJson(new_json, JsonFormat.compact());
		try {
			URL url = new URL(action);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			http.setDoOutput(true);
			http.setDoInput(true);
			System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
			System.setProperty("sun.net.client.defaultReadTimeout", "30000");
			System.setProperty("jsse.enableSNIExtension", "false");
			http.connect();
			OutputStream os = http.getOutputStream();
			os.write(json.getBytes("UTF-8"));
			System.out.println(json);
			os.flush();
			os.close();
			InputStream is = http.getInputStream();
			int size = is.available();
			byte[] jsonBytes = new byte[size];
			is.read(jsonBytes);
			String message = new String(jsonBytes, "UTF-8");
			System.out.println("menu:" + message);

			Map ret_map = Json.fromJson(HashMap.class, message);
			ret = CommUtil.null2String((ret_map.get("ticket")));
			System.out.println("ret" + ret);
			is.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public void user_add(String openId, HttpServletRequest request)
			throws IOException, InterruptedException {
		Map params = new HashMap();
		params.put("openId", openId);
		List<User> users = this.userService.query(
				"select obj from User obj where obj.openId=:openId", params,
				-1, -1);
		if (users.size() == 0) {
			// 拉取用户信息
			String userInfo = this.getWeiXin_userInfo(openId);
			Map map = Json.fromJson(HashMap.class, userInfo);
			String sub = map.get("subscribe").toString();
			if (sub.equals("1")) {
				User new_user = new User();
				new_user.setAddTime(new Date());
				new_user.setOpenId(map.get("openid").toString());
				this.userService.save(new_user);
				new_user.setUserRole("BUYER");
				new_user.setPassword(Md5Encrypt.md5(map.get("openid")
						.toString().substring(0, 8)));
				String mark = this.base64Tools.encodeStr(map.get("openid")
						.toString().substring(0, 8))
						+ CommUtil.randomString(4);
				new_user.setUserMark(mark);
				new_user.setUserName(mark);
				int sex = CommUtil.null2Int(map.get("sex"));
				params.clear();
				params.put("type", "BUYER");
				List<Role> roles = this.roleService
						.query("select obj from Role obj where obj.type=:type",
								params, -1, -1);
				new_user.getRoles().addAll(roles);
				switch (sex) {
				case 1:
					new_user.setSex(1);
					break;
				case 2:
					new_user.setSex(0);
					break;
				case 0:
					new_user.setSex(-1);
					break;
				}
				// 上传头像
				String fileName = this.getWeiXin_userPhoto(map
						.get("headimgurl").toString(), request);
				Accessory photo = new Accessory();
				photo.setAddTime(new Date());
				photo.setExt(".jpg");
				photo.setName(fileName);
				String uploadFilePath = this.configService.getSysConfig()
						.getUploadFilePath();
				photo.setPath(uploadFilePath);
				this.accessoryService.save(photo);
				// 上传头像结束
				new_user.setPhoto(photo);
				String nickName = this.emojiTools.filterEmoji(map.get(
						"nickname").toString());
				new_user.setNickName(nickName);
				this.userService.update(new_user);
			}
		}
	}

	private String getWeiXin_userInfo(String openId) {
		String appId = CommUtil.null2String(this.configService.getSysConfig()
				.getWeixin_appId());
		String appSecret = CommUtil.null2String(this.configService
				.getSysConfig().getWeixin_appSecret());
		String access_token = GetWxToken.instance()
				.getWxToken(appId, appSecret);
		String action = "https://api.weixin.qq.com/cgi-bin/user/info?access_token="
				+ access_token + "&openid=" + openId + "&lang=zh_CN";
		String message = null;
		try {
			URL url = new URL(action);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			http.setDoOutput(true);
			http.setDoInput(true);
			System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
			System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
			System.setProperty("jsse.enableSNIExtension", "false");
			http.connect();
			InputStream is = http.getInputStream();
			int size = is.available();
			byte[] jsonBytes = new byte[size];
			is.read(jsonBytes);
			message = new String(jsonBytes, "UTF-8");
			is.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			message = getWeiXin_userInfo(openId);
			return message;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}

	private String getWeiXin_userPhoto(String urlString,
			HttpServletRequest request) throws IOException {
		URL url = new URL(urlString);
		URLConnection con = url.openConnection();
		con.setConnectTimeout(30000);
		InputStream is = con.getInputStream();
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath;
		byte[] bs = new byte[1024];
		int len;
		File sf = new File(saveFilePathName);
		if (!sf.exists()) {
			sf.mkdirs();
		}
		String fileName = CommUtil.formatTime("yyyyMMddHHmmss", new Date())
				+ ".jpg";
		OutputStream os = new FileOutputStream(sf.getPath() + File.separator
				+ fileName);
		while ((len = is.read(bs)) != -1) {
			os.write(bs, 0, len);
		}
		os.close();
		is.close();
		return fileName;
	}

	@Async
	private String asyncLogin(String userName, String password) {
		return "redirect:iskyshop_login.htm?username=" + userName
				+ "&password=" + password + "&encode=true";
	}

	public class Weixin_Menu {
		private String type;// click或者view
		private String name;// 菜单名称
		private String key;// 菜单key
		private String url;// 菜单url
		private List<Weixin_Menu> sub_button = new ArrayList<Weixin_Menu>();// 子菜单

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public List<Weixin_Menu> getSub_button() {
			return sub_button;
		}

		public void setSub_button(List<Weixin_Menu> sub_button) {
			this.sub_button = sub_button;
		}

	}
}
