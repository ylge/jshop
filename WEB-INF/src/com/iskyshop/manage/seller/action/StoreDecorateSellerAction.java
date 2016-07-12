package com.iskyshop.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
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
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StoreNavigation;
import com.iskyshop.foundation.domain.StoreSlide;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IStoreNavigationService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.IStoreSlideService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.UserTools;

/**
 * 
 * <p>
 * Title: StoreDecorateSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 店铺装修功能管理控制器
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
 * @date 2014-12-18
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class StoreDecorateSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IStoreNavigationService storenavigationService;
	@Autowired
	private IStoreSlideService storeSlideService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private UserTools userTools;

	@SecurityMapping(title = "店铺装修", value = "/seller/decorate.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate.htm")
	public ModelAndView decorate(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/decorate/index.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map params = new HashMap();
		params.put("store_id", store.getId());
		params.put("display", true);
		List<StoreNavigation> navs = this.storenavigationService
				.query("select obj from StoreNavigation obj where obj.store.id=:store_id and obj.display=:display order by obj.sequence asc",
						params, -1, -1);

		if (store.getStore_decorate_info() != null) {
			List<Map> old_maps = Json.fromJson(List.class,
					store.getStore_decorate_info());
			mv.addObject("maps", old_maps);
		}
		if (store.getStore_decorate_base_info() != null) {
			List<Map> fundations = Json.fromJson(List.class,
					store.getStore_decorate_base_info());
			for (Map fun : fundations) {
				mv.addObject("fun_" + fun.get("key"), fun.get("val"));
			}
		}
		params.clear();
		params.put("sid", store.getId());
		List<StoreSlide> slides = this.storeSlideService.query(
				"select obj from StoreSlide obj where obj.store.id=:sid",
				params, -1, -1);
		String store_theme = "default";
		if (store.getStore_decorate_theme() != null) {
			store_theme = store.getStore_decorate_theme();
		}
		if (store.getStore_decorate_background_info() != null) {
			Map bg = Json.fromJson(Map.class,
					store.getStore_decorate_background_info());
			mv.addObject("bg", bg);
		}
		mv.addObject("store_theme", store_theme);
		mv.addObject("navs", navs);
		mv.addObject("store", store);
		mv.addObject("default_slides", slides);
		return mv;
	}

	@SecurityMapping(title = "店铺装修预览", value = "/seller/decorate_preview.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_preview.htm")
	public ModelAndView decorate_preview(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/decorate/index_preview.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		this.generic_evaluate(store, mv);// 店铺信用信息
		Map params = new HashMap();
		params.put("store_id", store.getId());
		params.put("display", true);
		List<StoreNavigation> navs = this.storenavigationService
				.query("select obj from StoreNavigation obj where obj.store.id=:store_id and obj.display=:display order by obj.sequence asc",
						params, -1, -1);
		mv.addObject("navs", navs);
		mv.addObject("store", store);
		mv.addObject("userTools", userTools);
		if (store.getStore_decorate_base_info() != null) {
			List<Map> fundations = Json.fromJson(List.class,
					store.getStore_decorate_base_info());
			for (Map fun : fundations) {
				mv.addObject("fun_" + fun.get("key"), fun.get("val"));
			}
		}
		if (store.getStore_decorate_info() != null) {
			List<Map> old_maps = Json.fromJson(List.class,
					store.getStore_decorate_info());
			mv.addObject("maps", old_maps);
		}
		params.clear();
		params.put("sid", store.getId());
		params.put("slide_type", 0);
		List<StoreSlide> slides = this.storeSlideService
				.query("select obj from StoreSlide obj where obj.store.id=:sid and obj.slide_type=:slide_type",
						params, -1, -1);
		mv.addObject("default_slides", slides);
		String store_theme = "default";
		if (store.getStore_decorate_theme() != null) {
			store_theme = store.getStore_decorate_theme();
		}
		mv.addObject("store_theme", store_theme);
		if (store.getStore_decorate_background_info() != null) {
			Map bg = Json.fromJson(Map.class,
					store.getStore_decorate_background_info());
			mv.addObject("bg", bg);
		}
		return mv;
	}

	/**
	 * 保存并退出
	 * 
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "店铺装修主题设置", value = "/seller/decorate_theme_set.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_theme_set.htm")
	public void decorate_theme_set(HttpServletRequest request,
			HttpServletResponse response, String theme) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		store.setStore_decorate_theme(CommUtil.null2String(theme));
		this.storeService.update(store);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 保存并退出
	 * 
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "店铺装修保存并退出", value = "/seller/decorate_subquite.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_subquite.htm")
	public void decorate_subquite(HttpServletRequest request,
			HttpServletResponse response) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		store.setStore_decorate_old_info(store.getStore_decorate_info());
		store.setStore_decorate_background_old_info(store
				.getStore_decorate_background_info());
		store.setStore_decorate_base_old_info(store
				.getStore_decorate_base_info());
		store.setStore_decorate_old_theme(store.getStore_decorate_theme());
		this.storeService.update(store);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 撤销装修信息，返回老的装修信息
	 * 
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "店铺装修撤销", value = "/seller/decorate_backout.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_backout.htm")
	public void decorate_backout(HttpServletRequest request,
			HttpServletResponse response) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		store.setStore_decorate_info(store.getStore_decorate_old_info());
		store.setStore_decorate_background_info(store
				.getStore_decorate_background_old_info());
		store.setStore_decorate_theme(store.getStore_decorate_old_theme());
		store.setStore_decorate_base_info(store
				.getStore_decorate_base_old_info());
		this.storeService.update(store);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 店铺装修背景设置
	 * 
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "店铺装修背景设置", value = "/seller/decorate_background_set.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_background_set.htm")
	public void decorate_background_set(HttpServletRequest request,
			HttpServletResponse response, String type, String bg_img_id,
			String repeat, String bg_color) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (type.equals("save")) {
			Accessory img = this.accessoryService.getObjById(CommUtil
					.null2Long(bg_img_id));
			Map map = new HashMap();
			if (img != null) {
				map.put("bg_img_id", bg_img_id);
				map.put("bg_img_src", img.getPath() + "/" + img.getName());
			}
			if (bg_color != null && !bg_color.equals("")) {
				map.put("bg_color", bg_color);
			}
			map.put("repeat", repeat);
			store.setStore_decorate_background_info(Json.toJson(map,
					JsonFormat.compact()));
		} else {
			store.setStore_decorate_background_info(null);
		}
		this.storeService.update(store);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "店铺装修背景图片上传", value = "/seller/decorate_background_upload.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_background_upload.htm")
	public void decorate_background_upload(HttpServletRequest request,
			HttpServletResponse response, String img_id) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath
				+ File.separator
				+ "store_slide"
				+ File.separator + store.getId();
		CommUtil.createFolder(saveFilePathName);
		Map map = new HashMap();
		String fileName = "";
		Map json_map = new HashMap();
		Accessory photo = new Accessory();
		if (img_id != null && !img_id.equals("")) {
			photo = this.accessoryService
					.getObjById(CommUtil.null2Long(img_id));
			CommUtil.del_acc(request, photo);
			fileName = photo.getName();
		}
		SysConfig config = this.configService.getSysConfig();
		String Suffix = config.getImageSuffix();
		String suffs[] = Suffix.split("|");
		List<String> list = new ArrayList<String>();
		for (String temp : suffs) {
			list.add(temp);
		}
		String[] suf = (String[]) list.toArray(new String[0]);
		try {
			map = CommUtil.saveFileToServer(request, "bg_img",
					saveFilePathName, "", suf);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store_slide/"
							+ store.getId());
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					json_map.put("src", photo.getPath() + "/" + photo.getName());
					json_map.put("id", photo.getId());
				}
			} else {
				if (map.get("fileName") != "") {
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store_slide/"
							+ store.getId());
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.update(photo);
					json_map.put("src", photo.getPath() + "/" + photo.getName());
					json_map.put("id", photo.getId());
				}
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
	 * @param layout
	 *            :布局类型：layout0通栏布局上，layout1通栏布局下，layout2整体布局，layout3：1:2布局,
	 *            layout4:2:1布局
	 * @return
	 */
	@SecurityMapping(title = "店铺装修布局加载", value = "/seller/decorate_layout.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_layout.htm")
	public ModelAndView decorate_layout(HttpServletRequest request,
			HttpServletResponse response, String layout) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/decorate/" + layout + ".html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		String mark = CommUtil.randomInt(5) + "_" + store.getId();
		List<Map> map_list = new ArrayList<Map>();
		if (store.getStore_decorate_info() != null) {
			map_list = Json
					.fromJson(List.class, store.getStore_decorate_info());
		}
		Map map = new HashMap();
		map.put("layout", layout);
		map.put("mark", mark);
		if (layout.equals("layout4") || layout.equals("layout3")) {
			map.put("div1", "true");
			map.put("div2", "true");
		}
		map_list.add(map);
		store.setStore_decorate_info(Json.toJson(map_list, JsonFormat.compact()));
		mv.addObject("mark", mark);
		mv.addObject("layout", layout);
		this.storeService.update(store);
		return mv;
	}

	@SecurityMapping(title = "店铺装修布局移除", value = "/seller/decorate_layout_remove.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_layout_remove.htm")
	public void decorate_layout_remove(HttpServletRequest request,
			HttpServletResponse response, String layout, String mark, String div) {
		String code = "true";
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		List<Map> map_list = new ArrayList<Map>();
		if (store.getStore_decorate_info() != null) {
			map_list = Json
					.fromJson(List.class, store.getStore_decorate_info());
			Map obj_map = null;
			for (Map map : map_list) {
				if (CommUtil.null2String(map.get("mark")).equals(mark)) {
					obj_map = map;
					break;
				}
			}
			if (layout.equals("layout1") || layout.equals("layout0")
					|| layout.equals("layout2")) {
				map_list.remove(obj_map);
			} else if (layout.equals("layout3") || layout.equals("layout4")) {
				obj_map.put(div, "false");
				if (CommUtil.null2String(obj_map.get("div1")).equals("false")
						&& CommUtil.null2String(obj_map.get("div2")).equals(
								"false")) {
					map_list.remove(obj_map);
				}
			}
		} else {
			code = "false";
		}
		if (map_list.size() > 0) {
			store.setStore_decorate_info(Json.toJson(map_list,
					JsonFormat.compact()));
		} else {
			store.setStore_decorate_info(null);
		}
		this.storeService.update(store);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(code);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "店铺装修布局模块编辑窗口加载", value = "/seller/decorate_module.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_module.htm")
	public ModelAndView decorate_module(HttpServletRequest request,
			HttpServletResponse response, String layout, String mark,
			String div, String position) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/decorate/module.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("layout", layout);
		mv.addObject("mark", mark);
		mv.addObject("position", position);
		if (div != null) {
			mv.addObject("div", div);
		}
		return mv;
	}

	/**
	 * 鼠标拖动可移动模块时需实时记录模块位置
	 * 
	 * @param request
	 * @param response
	 * @param marks
	 */
	@SecurityMapping(title = "店铺装修布局模块位置记录", value = "/seller/decorate_module_location_record.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_module_location_record.htm")
	public void decorate_module_location_record(HttpServletRequest request,
			HttpServletResponse response, String marks) {
		if (marks != null && !marks.equals("")) {
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			List<Map> maps = Json.fromJson(List.class,
					store.getStore_decorate_info());
			List<Map> new_maps = new ArrayList<Map>();
			String temp_marks[] = marks.split(",");
			for (String mark : temp_marks) {
				if (!mark.equals("")) {
					for (Map map : maps) {
						if (map.get("mark").equals(mark)) {
							new_maps.add(map);
							break;
						}
					}
				}
			}
			store.setStore_decorate_info(Json.toJson(new_maps,
					JsonFormat.compact()));
			this.storeService.update(store);
		}
	}

	@SecurityMapping(title = "店铺装修布局模块保存", value = "/seller/decorate_module_save.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_module_save.htm")
	public String decorate_module_save(HttpServletRequest request,
			HttpServletResponse response, String type, String mark, String div,
			String defined_content, String defined_goods_ids, String position,
			String title, String img_ids, String coor_datas, String coor_img) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		boolean flag = false;
		String fundation_url = ",nav,slide,class,goods_sale,store_info";
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = Json.fromJson(List.class,
					store.getStore_decorate_info());
			for (Map map : maps) {
				if (map.get("mark").equals(mark)) {
					if (fundation_url.indexOf("," + type) >= 0) {// 基础模块
						if (div != null && !div.equals("")) {
							if (div.equals("div1") || div.equals("div2")) {
								map.put(div + "_url", type);
							}
						} else {
							map.put("url", type);
						}
					} else {
						if (type.equals("defined")) {// 自定义内容模块
							if (div != null && !div.equals("")) {
								if (div.equals("div1") || div.equals("div2")) {
									map.put(div + "_content", defined_content);
									map.put(div + "_url", type);
								}
							} else {
								map.put("content", defined_content);
								map.put("url", type);
							}
						}
						if (type.equals("defined_goods")) {// 自定义商品模块
							map.put("goods_ids", defined_goods_ids);
							if (position.equals("goods_top")) {
								map.put("url", position);
								map.put("title", title);
							} else {
								map.put("div2_url", position);
							}
							type = position;
						}
						if (type.equals("defined_slide")) {// 自定义幻灯模块
							String ids[] = img_ids.split(",");
							String slide_info = "";
							int i = 1;
							for (String id : ids) {
								if (!id.equals("")) {
									Accessory img = this.accessoryService
											.getObjById(CommUtil.null2Long(id));
									String url = request.getParameter("url_"
											+ i);
									slide_info = slide_info + "|" + img.getId()
											+ "==" + url;
									i++;
								}
							}
							map.put("slide_info", slide_info);
							if (div != null && !div.equals("")) {
								map.put("div2_url", "defined_slide");
							} else {
								map.put("url", "defined_slide");
							}
							map.put("height", 300);
							map.put("effect", "fade");
							map.put("delayTime", 500);
							map.put("autoPlay", true);
						}
						if (type.equals("hotspot")) {// 自定义热点模块
							String datas[] = coor_datas.split("\\|");
							List<Map> coors_list = new ArrayList<Map>();
							for (String data : datas) {
								if (data != null && data != ""
										&& !data.equals("undefined")) {
									String coors[] = data.split("==");
									Map temp_map = new HashMap();
									String temp_url = coors[0];
									if (temp_url.indexOf("http://") < 0) {
										temp_url = "http://" + temp_url;
									}
									temp_map.put("url", temp_url);
									if (coors.length >= 1) {
										temp_map.put("coor", coors[1]);
									}
									coors_list.add(temp_map);
								}
							}
							if (div != null && !div.equals("")) {
								if (div.equals("div1") || div.equals("div2")) {
									map.put(div + "_url", type);
									map.put("coors_list_" + div, coors_list);
									map.put("coors_img_id_" + div, coor_img);
								}
							} else {
								map.put("coors_list", coors_list);
								map.put("coors_img_id", coor_img);
								map.put("url", type);
							}
						}
					}
					flag = true;
					break;
				}
			}
			if (flag) {
				store.setStore_decorate_info(Json.toJson(maps,
						JsonFormat.compact()));
				this.storeService.update(store);
			}
		}
		return "redirect:/module_loading.htm?url=" + type + "&id="
				+ store.getId() + "&mark=" + mark + "&decorate_view=true&div="
				+ div;
	}

	/**
	 * 编辑背景颜色、编辑边框颜色、编辑幻灯高度等等信息
	 * 
	 * @param request
	 * @param response
	 * @param mark
	 * @param div
	 * @return
	 */
	@SecurityMapping(title = "店铺装修自定义模块编辑窗口加载", value = "/seller/decorate_module_set.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_module_set.htm")
	public ModelAndView decorate_module_set(HttpServletRequest request,
			HttpServletResponse response, String mark, String div) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/decorate/module_set.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		// 模块设置信息
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map obj = new HashMap();
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = Json.fromJson(List.class,
					store.getStore_decorate_info());
			for (Map map : maps) {
				if (map.get("mark").equals(mark)) {
					obj = map;
					break;
				}
			}
		}
		mv.addObject("obj", obj);
		if (obj.get("layout").equals("layout0")
				|| obj.get("layout").equals("layout1")
				|| obj.get("layout").equals("layout2")) {
			mv.addObject("url", obj.get("url"));
		} else {
			mv.addObject("url", obj.get(div + "_url"));
		}
		mv.addObject("div", div);
		return mv;
	}

	/**
	 * 编辑背景颜色、编辑边框颜色、编辑幻灯高度等等信息
	 * 
	 * @param request
	 * @param response
	 * @param mark
	 * @param div
	 * @return
	 */
	@SecurityMapping(title = "店铺装修自定义模块编辑图片上传", value = "/seller/decorate_module_set_load.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_module_set_load.htm")
	public void decorate_module_set_load(HttpServletRequest request,
			HttpServletResponse response, String mark, String div, String type,
			String img_id) {

		// 模块设置信息
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map json_map = new HashMap();
		Map obj = new HashMap();
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = Json.fromJson(List.class,
					store.getStore_decorate_info());
			for (Map map : maps) {
				if (map.get("mark").equals(mark)) {
					obj = map;
					break;
				}
			}
		}
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		if (type.equals("goods")) {
			// 图片
			String saveFilePathName = request.getSession().getServletContext()
					.getRealPath("/")
					+ uploadFilePath + File.separator + "store_decorate";
			Map map = new HashMap();
			String fileName = "";
			Accessory img = null;
			if (img_id != null && !img_id.equals("undefined")
					&& !img_id.equals("")) {
				img = this.accessoryService.getObjById(CommUtil
						.null2Long(img_id));
				CommUtil.del_acc(request, img);
				fileName = img.getName();
			}
			SysConfig config = this.configService.getSysConfig();
			String Suffix = config.getImageSuffix();
			String suffs[] = Suffix.split("|");
			List<String> list = new ArrayList<String>();
			for (String temp : suffs) {
				list.add(temp);
			}
			String[] suf = (String[]) list.toArray(new String[0]);
			try {
				map = CommUtil.saveFileToServer(request, "font_img",
						saveFilePathName, "", suf);
				if (fileName.equals("")) {
					if (map.get("fileName") != "") {
						Accessory photo = new Accessory();
						photo.setName(CommUtil.null2String(map.get("fileName")));
						photo.setExt(CommUtil.null2String(map.get("mime")));
						photo.setSize(BigDecimal.valueOf(CommUtil
								.null2Double(map.get("fileSize"))));
						photo.setPath(uploadFilePath + "/store_decorate");
						photo.setWidth(CommUtil.null2Int(map.get("width")));
						photo.setHeight(CommUtil.null2Int(map.get("height")));
						photo.setAddTime(new Date());
						this.accessoryService.save(photo);
						json_map.put("src",
								photo.getPath() + "/" + photo.getName());
						json_map.put("id", photo.getId());
					}
				} else {
					if (map.get("fileName") != "") {
						Accessory photo = img;
						photo.setName(CommUtil.null2String(map.get("fileName")));
						photo.setExt(CommUtil.null2String(map.get("mime")));
						photo.setSize(BigDecimal.valueOf(CommUtil
								.null2Double(map.get("fileSize"))));
						photo.setPath(uploadFilePath + "/store_decorate");
						photo.setWidth(CommUtil.null2Int(map.get("width")));
						photo.setHeight(CommUtil.null2Int(map.get("height")));
						photo.setAddTime(new Date());
						this.accessoryService.update(photo);
						json_map.put("src",
								photo.getPath() + "/" + photo.getName());
						json_map.put("id", photo.getId());
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(json_map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 编辑背景颜色、编辑边框颜色、编辑幻灯高度等等信息
	 * 
	 * @param request
	 * @param response
	 * @param mark
	 * @param div
	 * @return
	 */
	@SecurityMapping(title = "店铺装修自定义模块编辑保存", value = "/seller/decorate_module_set_save.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_module_set_save.htm")
	public void decorate_module_set_save(HttpServletRequest request,
			HttpServletResponse response, String mark, String div, String url,
			String height, String font_size, String font_color,
			String nav_color, String back_color, String hover_color,
			String effect, String delayTime, String autoPlay, String title,
			String font_img_id, String goods_count, String font_back_color,
			String board_back_color) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map obj_map = new HashMap();
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = Json.fromJson(List.class,
					store.getStore_decorate_info());
			for (Map map : maps) {
				if (map.get("mark").equals(mark)) {
					obj_map = map;
					break;
				}
			}
			if (url.equals("nav")) {
				obj_map.put("height", height);
				obj_map.put("font_size", font_size);
				obj_map.put("font_color", font_color);
				obj_map.put("nav_color", nav_color);
				obj_map.put("back_color", back_color);
				obj_map.put("hover_color", hover_color);
			}
			if (url.equals("defined_slide")) {
				obj_map.put("height", height);
				obj_map.put("effect", effect);
				obj_map.put("delayTime", delayTime);
				obj_map.put("autoPlay", CommUtil.null2Boolean(autoPlay));
			}
			if (url.equals("goods_top")) {
				if (font_img_id != null && !font_img_id.equals("")) {
					Accessory img = this.accessoryService.getObjById(CommUtil
							.null2Long(font_img_id));
					obj_map.put("font_img_id", font_img_id);
					obj_map.put("font_img_src",
							img.getPath() + "/" + img.getName());
				}
				obj_map.put("title", title);
				obj_map.put("font_color", font_color);
				obj_map.put("back_color", back_color);
			}
			if (url.equals("goods_right")) {
				obj_map.put("hover_color", hover_color);
				obj_map.put("back_color", back_color);
			}
			if (url.equals("goods_sale")) {
				obj_map.put("goods_count", goods_count);
				obj_map.put("font_back_color", font_back_color);
				obj_map.put("font_color", font_color);
				obj_map.put("board_back_color", board_back_color);
			}
			if (url.equals("class") || url.equals("store_info")) {
				obj_map.put("font_back_color", font_back_color);
				obj_map.put("font_color", font_color);
				obj_map.put("board_back_color", board_back_color);
			}
			store.setStore_decorate_info(Json.toJson(maps, JsonFormat.compact()));
			this.storeService.update(store);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "店铺装修自定义模块编辑窗口加载", value = "/seller/decorate_module_defined.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_module_defined.htm")
	public ModelAndView decorate_module_defined(HttpServletRequest request,
			HttpServletResponse response, String mark, String div) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/decorate/module_defined.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		String content = "";
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = Json.fromJson(List.class,
					store.getStore_decorate_info());
			for (Map temp : maps) {
				if (temp.get("mark").equals(mark)) {
					if (div != null && !div.equals("undefined")
							&& !div.equals("") && !div.equals("null")) {
						content = CommUtil.null2String(temp.get(div
								+ "_content"));
					} else {
						content = CommUtil.null2String(temp.get("content"));
					}
					break;
				}
			}
		}
		mv.addObject("mark", mark);
		mv.addObject("div", div);
		mv.addObject("content", content);
		return mv;
	}

	@SecurityMapping(title = "店铺装修商品模块窗口加载", value = "/seller/decorate_module_goods.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_module_goods.htm")
	public ModelAndView decorate_module_goods(HttpServletRequest request,
			HttpServletResponse response, String mark, String position) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/decorate/module_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		List<Goods> objs = new ArrayList<Goods>();
		String ids = null;
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = Json.fromJson(List.class,
					store.getStore_decorate_info());
			for (Map temp : maps) {
				if (temp.get("mark").equals(mark)) {
					ids = CommUtil.null2String(temp.get("goods_ids"));
					break;
				}
			}
		}
		if (ids != null) {
			String temp_ids[] = ids.split(",");
			for (String id : temp_ids) {
				if (!id.equals("")) {
					Goods obj = this.goodsService.getObjById(CommUtil
							.null2Long(id));
					objs.add(obj);
				}
			}
		}
		mv.addObject("objs", objs);
		mv.addObject("position", position);
		mv.addObject("mark", mark);
		return mv;
	}

	@SecurityMapping(title = "店铺装修商品模块窗口加载", value = "/seller/decorate_module_goods_load.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_module_goods_load.htm")
	public ModelAndView decorate_module_goods_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String goods_name) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/decorate/module_goods_load.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, "addTime",
				"desc");
		qo.setPageSize(6);
		if (!CommUtil.null2String(goods_name).equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("goods_name", "%"
					+ goods_name + "%"), "like");
		}
		qo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		qo.addQuery("obj.goods_store.id",
				new SysMap("goods_store_id", store.getId()), "=");
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/seller/decorate_module_goods_load.htm", "", "&goods_name="
				+ goods_name, pList, mv);
		return mv;
	}

	@SecurityMapping(title = "店铺装修热点区域模块窗口加载", value = "/seller/decorate_module_hotspot.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_module_hotspot.htm")
	public ModelAndView decorate_module_hotspot(HttpServletRequest request,
			HttpServletResponse response, String mark, String div) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/decorate/module_hotspot.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map temp_map = new HashMap();
		Map obj = new HashMap();
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = Json.fromJson(List.class,
					store.getStore_decorate_info());
			for (Map temp : maps) {
				if (temp.get("mark").equals(mark)) {
					temp_map = temp;
					break;
				}
			}
		}
		String coors_img_id_mark = "coors_img_id";
		String coors_list_mark = "coors_list";
		if (div != null && !div.equals("undefined") && !div.equals("null")
				&& !div.equals("")) {
			coors_img_id_mark = "coors_img_id_" + div;
			coors_list_mark = "coors_list_" + div;
		}
		Accessory img = this.accessoryService.getObjById(CommUtil
				.null2Long(temp_map.get(coors_img_id_mark)));
		if (img != null) {
			List<Map> coors_list = (List<Map>) temp_map.get(coors_list_mark);
			obj.put("src", CommUtil.getURL(request) + "/" + img.getPath() + "/"
					+ img.getName());
			obj.put("id", img.getId());
			obj.put("height", img.getHeight());
			obj.put("width", img.getWidth());
			if (img.getWidth() > 680) {
				obj.put("height", CommUtil.mul(
						CommUtil.div(img.getHeight(), img.getWidth()), 680));
			}

			List<Map> coors_list_temp = new ArrayList<Map>();
			for (Map temp_coor : coors_list) {
				String coor = CommUtil.null2String(temp_coor.get("coor"));
				String url = CommUtil.null2String(temp_coor.get("url"));			
				int height = 0;
				int width = 0;
				int top = 0;
				int left = 0;
				String coor_datas[] = coor.split(",");
				if (coor_datas.length>3) {
					height = CommUtil.null2Int(coor_datas[3])
							- CommUtil.null2Int(coor_datas[1]);
					width = CommUtil.null2Int(coor_datas[2])
							- CommUtil.null2Int(coor_datas[0]);
					top = CommUtil.null2Int(coor_datas[1]);
					left = CommUtil.null2Int(coor_datas[0]);
				}		
				Map temp = new HashMap();
				temp.put("height", height);
				temp.put("width", width);
				temp.put("top", top);
				temp.put("left", left);
				temp.put("coor_data", url + "==" + coor);
				temp.put("coor_url", url);
				coors_list_temp.add(temp);
			}
			mv.addObject("coors_list", coors_list_temp);
			mv.addObject("obj", obj);
		}
		mv.addObject("mark", mark);
		mv.addObject("div", div);
		return mv;
	}

	@SecurityMapping(title = "店铺装修热点区域模块图片上传", value = "/seller/decorate_module_hotspot_img_upload.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_module_hotspot_img_upload.htm")
	public void decorate_module_hotspot_img_upload(HttpServletRequest request,
			HttpServletResponse response, String mark, String img_id) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "store_decorate";
		Map map = new HashMap();
		String fileName = "";
		Map json_map = new HashMap();
		Accessory img = null;
		if (img_id != null && !img_id.equals("undefined") && !img_id.equals("")) {
			img = this.accessoryService.getObjById(CommUtil.null2Long(img_id));
			CommUtil.del_acc(request, img);
			fileName = img.getName();
		}
		SysConfig config = this.configService.getSysConfig();
		String Suffix = config.getImageSuffix();
		String suffs[] = Suffix.split("|");
		List<String> list = new ArrayList<String>();
		for (String temp : suffs) {
			list.add(temp);
		}
		String[] suf = (String[]) list.toArray(new String[0]);
		try {
			map = CommUtil.saveFileToServer(request, "img", saveFilePathName,
					"", suf);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store_decorate");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					json_map.put("src", photo.getPath() + "/" + photo.getName());
					json_map.put("id", photo.getId());
					json_map.put("height", photo.getHeight());
					json_map.put("width", photo.getWidth());
					if (photo.getWidth() > 680) {
						json_map.put(
								"height",
								CommUtil.mul(
										CommUtil.div(photo.getHeight(),
												photo.getWidth()), 680));
					}
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory photo = img;
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store_decorate");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.update(photo);
					json_map.put("src", photo.getPath() + "/" + photo.getName());
					json_map.put("id", photo.getId());
					json_map.put("height", photo.getHeight());
					json_map.put("width", photo.getWidth());
					if (photo.getWidth() > 680) {
						json_map.put(
								"height",
								CommUtil.mul(
										CommUtil.div(photo.getHeight(),
												photo.getWidth()), 680));
					}
				}
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
			writer.print(Json.toJson(json_map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "店铺装修热点区设置保存", value = "/seller/decorate_module_hotspot_save.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_module_hotspot_save.htm")
	public void decorate_module_hotspot_save(HttpServletRequest request,
			HttpServletResponse response, String coor_datas, String img_id,
			String mark) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map obj_map = new HashMap();
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = Json.fromJson(List.class,
					store.getStore_decorate_info());
			for (Map temp : maps) {
				if (temp.get("mark").equals(mark)) {
					obj_map = temp;
					break;
				}
			}
		}

	}

	/**
	 * 店铺装修幻灯设置窗口加载
	 * 
	 * @param request
	 * @param response
	 * @param mark
	 * @return
	 */
	@SecurityMapping(title = "店铺装修自定义幻灯设置窗口加载", value = "/seller/decorate_module_slide.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_module_slide.htm")
	public ModelAndView decorate_module_slide(HttpServletRequest request,
			HttpServletResponse response, String mark, String div) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/decorate/module_defined_slide.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map obj_map = null;
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = Json.fromJson(List.class,
					store.getStore_decorate_info());
			for (Map temp : maps) {
				if (temp.get("mark").equals(mark)) {
					obj_map = temp;
					break;
				}
			}
		}
		if (obj_map != null) {
			List<Map> objs = new ArrayList<Map>();
			String temp_str[] = CommUtil.null2String(obj_map.get("slide_info"))
					.split("\\|");
			for (String str : temp_str) {
				if (!str.equals("")) {
					String temp[] = str.split("==");
					Accessory img = this.accessoryService.getObjById(CommUtil
							.null2Long(temp[0]));
					Map obj = new HashMap();
					obj.put("id", img.getId());
					obj.put("src", img.getPath() + "/" + img.getName());
					if (temp.length > 1) {
						obj.put("url", temp[1]);
					}
					objs.add(obj);
				}
			}
			mv.addObject("slides", objs);
		}
		mv.addObject("div", div);
		mv.addObject("mark", mark);
		return mv;
	}

	@SecurityMapping(title = "店铺装修自定义幻灯模块图片上传", value = "/seller/decorate_module_slide_upload.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_module_slide_upload.htm")
	public void decorate_module_slide_upload(HttpServletRequest request,
			HttpServletResponse response, String img_id, String count) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath
				+ File.separator
				+ "store_slide"
				+ File.separator + store.getId();
		CommUtil.createFolder(saveFilePathName);
		Map map = new HashMap();
		String fileName = "";
		Map json_map = new HashMap();
		Accessory photo = new Accessory();
		if (img_id != null && !img_id.equals("")) {
			photo = this.accessoryService
					.getObjById(CommUtil.null2Long(img_id));
			CommUtil.del_acc(request, photo);
			fileName = photo.getName();
		}
		SysConfig config = this.configService.getSysConfig();
		String Suffix = config.getImageSuffix();
		String suffs[] = Suffix.split("|");
		List<String> list = new ArrayList<String>();
		for (String temp : suffs) {
			list.add(temp);
		}
		String[] suf = (String[]) list.toArray(new String[0]);
		try {
			map = CommUtil.saveFileToServer(request, "img_file_" + count,
					saveFilePathName, "", suf);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store_slide/"
							+ store.getId());
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					json_map.put("src", photo.getPath() + "/" + photo.getName());
					json_map.put("id", photo.getId());
				}
			} else {
				if (map.get("fileName") != "") {
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store_slide/"
							+ store.getId());
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.update(photo);
					json_map.put("src", photo.getPath() + "/" + photo.getName());
					json_map.put("id", photo.getId());
				}
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
			writer.print(Json.toJson(json_map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 店铺装修基础模块设置（店铺默认信息，店铺默认banner,店铺默认导航,默认幻灯）
	 * 
	 * @param request
	 * @param response
	 * @param type
	 * @param status
	 */
	@SecurityMapping(title = "店铺装修基础模块设置", value = "/seller/decorate_fundation_set.htm*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping("/seller/decorate_fundation_set.htm")
	public void decorate_fundation_set(HttpServletRequest request,
			HttpServletResponse response, String type, String status) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		List<Map> maps = new ArrayList<Map>();
		if (store.getStore_decorate_base_info() != null) {
			maps = Json.fromJson(List.class,
					store.getStore_decorate_base_info());
			for (Map map : maps) {
				if (map.get("key").equals(type)) {
					map.put("val", status);
					break;
				}
			}
		} else {
			String types[] = { "nav", "info", "banner", "slide" };
			for (String temp : types) {
				Map map = new HashMap();
				if (type.equals(temp)) {
					map.put("key", temp);
					map.put("val", status);
				} else {
					map.put("key", temp);
					map.put("val", "on");
				}
				maps.add(map);
			}
		}
		store.setStore_decorate_base_info(Json.toJson(maps,
				JsonFormat.compact()));
		this.storeService.update(store);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 加载店铺评分信息
	 * 
	 * @param store
	 * @param mv
	 */
	private void generic_evaluate(Store store, ModelAndView mv) {
		double description_result = 0;
		double service_result = 0;
		double ship_result = 0;
		GoodsClass gc = this.goodsClassService
				.getObjById(store.getGc_main_id());
		if (store != null && gc != null && store.getPoint() != null) {
			float description_evaluate = CommUtil.null2Float(gc
					.getDescription_evaluate());
			float service_evaluate = CommUtil.null2Float(gc
					.getService_evaluate());
			float ship_evaluate = CommUtil.null2Float(gc.getShip_evaluate());

			float store_description_evaluate = CommUtil.null2Float(store
					.getPoint().getDescription_evaluate());
			float store_service_evaluate = CommUtil.null2Float(store.getPoint()
					.getService_evaluate());
			float store_ship_evaluate = CommUtil.null2Float(store.getPoint()
					.getShip_evaluate());
			// 计算和同行比较结果
			description_result = CommUtil.div(store_description_evaluate
					- description_evaluate, description_evaluate);
			service_result = CommUtil.div(store_service_evaluate
					- service_evaluate, service_evaluate);
			ship_result = CommUtil.div(store_ship_evaluate - ship_evaluate,
					ship_evaluate);
		}
		if (description_result > 0) {
			mv.addObject("description_css", "value_strong");
			mv.addObject(
					"description_result",
					CommUtil.null2String(CommUtil.mul(description_result, 100) > 100 ? 100
							: CommUtil.mul(description_result, 100))
							+ "%");
		}
		if (description_result == 0) {
			mv.addObject("description_css", "value_normal");
			mv.addObject("description_result", "-----");
		}
		if (description_result < 0) {
			mv.addObject("description_css", "value_light");
			mv.addObject(
					"description_result",
					CommUtil.null2String(CommUtil.mul(-description_result, 100))
							+ "%");
		}
		if (service_result > 0) {
			mv.addObject("service_css", "value_strong");
			mv.addObject("service_result",
					CommUtil.null2String(CommUtil.mul(service_result, 100))
							+ "%");
		}
		if (service_result == 0) {
			mv.addObject("service_css", "value_normal");
			mv.addObject("service_result", "-----");
		}
		if (service_result < 0) {
			mv.addObject("service_css", "value_light");
			mv.addObject("service_result",
					CommUtil.null2String(CommUtil.mul(-service_result, 100))
							+ "%");
		}
		if (ship_result > 0) {
			mv.addObject("ship_css", "value_strong");
			mv.addObject("ship_result",
					CommUtil.null2String(CommUtil.mul(ship_result, 100)) + "%");
		}
		if (ship_result == 0) {
			mv.addObject("ship_css", "value_normal");
			mv.addObject("ship_result", "-----");
		}
		if (ship_result < 0) {
			mv.addObject("ship_css", "value_light");
			mv.addObject("ship_result",
					CommUtil.null2String(CommUtil.mul(-ship_result, 100)) + "%");
		}
	}
}
