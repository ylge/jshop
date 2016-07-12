package com.iskyshop.manage.seller.action;

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
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsBrandCategory;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.GoodsBrandQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IGoodsBrandCategoryService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.StoreTools;

/**
 * 
 * <p>
 * Title: GoodsBrandSellerAction.java
 * </p>
 * 
 * <p>
 * Description:商家品牌管理控制器，所有商家都可以申请自己的平台，平台管理员审核通过后即可在前端展示品牌信息
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
 * @author erikzhang
 * 
 * @date 2014-6-10
 * 
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class GoodsBrandSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsBrandCategoryService goodsBrandCategoryService;
	@Autowired
	private StoreTools storeTools;

	/**
	 * 卖家品牌列表页，分页显示所有卖家申请的品牌信息
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */

	@SecurityMapping(title = "卖家品牌列表", value = "/seller/goods_brand_list.htm*", rtype = "seller", rname = "品牌申请", rcode = "goods_brand_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_brand_list.htm")
	public ModelAndView goods_brand_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/goods_brand_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsBrandQueryObject qo = new GoodsBrandQueryObject(currentPage, mv,
				orderBy, orderType);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		qo.addQuery("obj.store_id", new SysMap("store_id", user.getStore()
				.getId()), "=");
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		System.out.println(qo.getQuery());
		IPageList pList = this.goodsBrandService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	/**
	 * 卖家品牌申请，管理员审核品牌信息，通过后该品牌将会作为商城品牌以供使用
	 * 
	 * @param request
	 * @param response
	 * @return
	 */

	@SecurityMapping(title = "卖家品牌申请", value = "/seller/goods_brand_add.htm*", rtype = "seller", rname = "品牌申请", rcode = "goods_brand_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_brand_add.htm")
	public ModelAndView goods_brand_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/goods_brand_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		List<GoodsClass> gcs = this.storeTools.query_store_detail_MainGc(user
				.getStore().getGc_detail_info());
		if (gcs.size() > 0) {
			mv.addObject("gcs", gcs);
		} else {
			GoodsClass parent = this.goodsClassService.getObjById(user
					.getStore().getGc_main_id());
			mv.addObject("gc_name", parent.getClassName());
		}
		List<GoodsBrandCategory> categorys = this.goodsBrandCategoryService
				.query("select obj from GoodsBrandCategory obj", null, -1, -1);
		mv.addObject("categorys", categorys);
		return mv;
	}

	/**
	 * 卖家品牌编辑，
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "卖家品牌编辑", value = "/seller/goods_brand_edit.htm*", rtype = "seller", rname = "品牌申请", rcode = "goods_brand_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_brand_edit.htm")
	public ModelAndView goods_brand_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/goods_brand_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (id != null && !id.equals("")) {
			GoodsBrand goodsBrand = this.goodsBrandService.getObjById(Long
					.parseLong(id));
			if (goodsBrand != null && user.getStore() != null
					&& goodsBrand.getStore_id() == user.getStore().getId()) {
				mv.addObject("obj", goodsBrand);
				mv.addObject("edit", true);
				List<GoodsClass> gcs = this.storeTools
						.query_store_detail_MainGc(user.getStore()
								.getGc_detail_info());
				if (gcs.size() > 0) {
					mv.addObject("gcs", gcs);
				} else {
					GoodsClass parent = this.goodsClassService.getObjById(user
							.getStore().getGc_main_id());
					mv.addObject("gc_name", parent.getClassName());
				}
				List<GoodsBrandCategory> categorys = this.goodsBrandCategoryService
						.query("select obj from GoodsBrandCategory obj", null,
								-1, -1);
				mv.addObject("categorys", categorys);
			} else {
				mv = new JModelAndView(
						"user/default/sellercenter/seller_error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "参数不正确");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/goods_brand_list.htm");
			}
		}

		return mv;
	}

	@SecurityMapping(title = "卖家品牌删除", value = "/seller/goods_brand_delete.htm*", rtype = "seller", rname = "品牌申请", rcode = "goods_brand_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_brand_delete.htm")
	public String goods_brand_delete(HttpServletRequest request, String id,
			String currentPage) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (!id.equals("")) {
			GoodsBrand brand = this.goodsBrandService.getObjById(Long
					.parseLong(id));
			if (brand != null && user.getStore() != null
					&& brand.getStore_id() == user.getStore().getId()) {
				if (brand.getAudit() != 1) {
					CommUtil.del_acc(request, brand.getBrandLogo());
					this.goodsBrandService.delete(Long.parseLong(id));
				}
			}
		}
		return "redirect:goods_brand_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "卖家品牌保存", value = "/seller/goods_brand_save.htm*", rtype = "seller", rname = "品牌申请", rcode = "goods_brand_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_brand_save.htm")
	public void goods_brand_save(HttpServletRequest request,
			HttpServletResponse response, String id, String cmd,
			String cat_name, String list_url, String add_url, String gc_id) {
		WebForm wf = new WebForm();
		GoodsBrand goodsBrand = null;
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (id.equals("") || id == null) {
			goodsBrand = wf.toPo(request, GoodsBrand.class);
			goodsBrand.setAddTime(new Date());
			goodsBrand.setAudit(0);
			goodsBrand.setUserStatus(1);
			goodsBrand.setStore_id(user.getStore().getId());
		} else {
			GoodsBrand obj = this.goodsBrandService.getObjById(Long
					.parseLong(id));
			goodsBrand = (GoodsBrand) wf.toPo(request, obj);
		}
		// 品牌标识图片
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "brand";
		Map map = new HashMap();
		try {
			String fileName = goodsBrand.getBrandLogo() == null ? ""
					: goodsBrand.getBrandLogo().getName();
			map = CommUtil.saveFileToServer(request, "brandLogo",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/brand");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.save(photo);
					goodsBrand.setBrandLogo(photo);
				}
			} else {
				if (map.get("fileName") != "") {
					Accessory photo = goodsBrand.getBrandLogo();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/brand");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.update(photo);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GoodsClass gc = null;
		if (gc_id != null && !gc_id.equals("")) {
			gc = this.goodsClassService.getObjById(Long.valueOf(gc_id));
		} else {
			gc = this.goodsClassService.getObjById(user.getStore()
					.getGc_main_id());
		}
		goodsBrand.setGc(gc);// 关联品牌分类
		if (cat_name != null && !cat_name.equals("")) {
			GoodsBrandCategory cat = this.goodsBrandCategoryService
					.getObjByProperty(null, "name", cat_name);
			goodsBrand.setCategory(cat);
		} else {
			goodsBrand.setCategory(null);
		}
		if (id.equals("")) {
			this.goodsBrandService.save(goodsBrand);
		} else
			this.goodsBrandService.update(goodsBrand);
		Map json = new HashMap();
		json.put("ret", true);
		json.put("op_title", "品牌申请成功");
		json.put("url", CommUtil.getURL(request)
				+ "/seller/goods_brand_list.htm");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(json, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
