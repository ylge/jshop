package com.iskyshop.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.core.tools.database.DatabaseTools;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.GoodsSpecificationQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsSpecPropertyService;
import com.iskyshop.foundation.service.IGoodsSpecificationService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.StoreTools;

/**
 * 
 * <p>
 * Title: GoodsSpecSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商家商品规格管理控制器，商家可以自行管理规格属性，发不商品时商家选择自己添加的规格属性，规格属性只在商品详细页显示并可以选择，
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
 * @date 2014-5-7
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class GoodsSpecSellerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IGoodsSpecificationService goodsSpecService;
	@Autowired
	private IGoodsSpecPropertyService goodsSpecPropertyService;
	@Autowired
	private DatabaseTools databaseTools;
	@Autowired
	private StoreTools shopTools;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsClassService goodsclassService;

	/**
	 * GoodsSpecification列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商品规格列表", value = "/seller/goods_spec_list.htm*", rtype = "seller", rname = "规格管理", rcode = "spec_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_spec_list.htm")
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType,String name) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/goods_spec_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		GoodsSpecificationQueryObject qo = new GoodsSpecificationQueryObject(
				currentPage, mv, orderBy, orderType);
		if(name!=null&&!"".equals(name)){
			qo.addQuery("obj.name", new SysMap("name","%"+name+"%"), "like");
			mv.addObject("name", name);
		}
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, GoodsSpecification.class, mv);
		qo.addQuery("obj.store.id", new SysMap("store_id", user.getStore()
				.getId()), "=");
		qo.setOrderBy("sequence");
		qo.setOrderType("asc");
		IPageList pList = this.goodsSpecService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("shopTools", shopTools);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * goodsSpecification添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "商品规格添加", value = "/seller/goods_spec_add.htm*", rtype = "seller", rname = "规格管理", rcode = "spec_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_spec_add.htm")
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/goods_spec_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		mv.addObject("store_id", user.getStore().getId());
		Set<GoodsClass> gcs = this.shopTools.query_store_DetailGc(user
				.getStore().getGc_detail_info());// 解析所有详细类目
		GoodsClass main_gc = this.goodsClassService.getObjById(user.getStore()
				.getGc_main_id());
		if (gcs.size() > 0) {// 如果商家注册时选择了详细类目
			mv.addObject("gcs", gcs);
		} else {// 如果商家注册时没有选择详细类目，将查询主营类目的所有下级类目
			mv.addObject("gcs", main_gc.getChilds());
		}
		return mv;
	}

	/**
	 * goodsSpecification编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "商品规格编辑", value = "/seller/goods_spec_edit.htm*", rtype = "seller", rname = "规格管理", rcode = "spec_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_spec_edit.htm")
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,String name) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/goods_spec_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (id != null && !id.equals("")) {
			GoodsSpecification goodsSpecification = this.goodsSpecService
					.getObjById(Long.parseLong(id));
			if (goodsSpecification != null) {
				if (goodsSpecification.getStore().getUser().getId()
						.equals(user.getId())) {
					mv.addObject("obj", goodsSpecification);
					mv.addObject("currentPage", currentPage);
					mv.addObject("store_id", user.getStore().getId());
					mv.addObject("edit", true);
					mv.addObject("name", name);
					Store store = user.getStore();
					Set<GoodsClass> gcs = this.shopTools
							.query_store_DetailGc(user.getStore()
									.getGc_detail_info());// 解析所有详细类目
					GoodsClass main_gc = this.goodsClassService
							.getObjById(store.getGc_main_id());
					if (gcs.size() > 0) {// 如果商家注册时选择了详细类目
						mv.addObject("gcs", gcs);
					} else {// 如果商家注册时没有选择详细类目，将查询主营类目的所有下级类目
						mv.addObject("gcs", main_gc.getChilds());
					}
					mv.addObject("gc_details", goodsSpecification
							.getGoodsclass().getChilds());
				} else {
					mv = new JModelAndView("error.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("url", CommUtil.getURL(request)
							+ "/seller/goods_spec_list.htm?currentPage="
							+ currentPage);
					mv.addObject("op_title", "您所访问的地址不存在");
				}
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/goods_spec_list.htm?currentPage="
						+ currentPage);
				mv.addObject("op_title", "您所访问的地址不存在");
			}
		}
		return mv;
	}

	/**
	 * goodsSpecification保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "商品规保存", value = "/seller/goods_spec_save.htm*", rtype = "seller", rname = "规格管理", rcode = "spec_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_spec_save.htm")
	public String save(HttpServletRequest request,
			HttpServletResponse response, String id, String count,
			String currentPage, String gc_id, String goodsClass_detail_ids,String name) {
		WebForm wf = new WebForm();
		GoodsSpecification goodsSpecification = null;
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (id.equals("")) {
			goodsSpecification = wf.toPo(request, GoodsSpecification.class);
			goodsSpecification.setAddTime(new Date());
		} else {
			GoodsSpecification obj = this.goodsSpecService.getObjById(Long
					.parseLong(id));
			goodsSpecification = (GoodsSpecification) wf.toPo(request, obj);
		}
		goodsSpecification.setSpec_type(1);
		goodsSpecification.setStore(user.getStore());
		if (gc_id != null && !gc_id.equals("")) {
			GoodsClass gc_main = this.goodsClassService.getObjById(CommUtil
					.null2Long(gc_id));
			goodsSpecification.setGoodsclass(gc_main);
		}
		if (goodsClass_detail_ids != null && !goodsClass_detail_ids.equals("")) {
			String ids[] = goodsClass_detail_ids.split(",");
			List<GoodsClass> gc_list = new ArrayList<GoodsClass>();
			for (String c_id : ids) {
				GoodsClass gc_detail = this.goodsClassService
						.getObjById(CommUtil.null2Long(c_id));
				if (gc_detail != null) {
					gc_list.add(gc_detail);
				}
			}
			if (gc_list.size() > 0) {
				goodsSpecification.setSpec_goodsClass_detail(gc_list);
			}
		} else {
			goodsSpecification.getSpec_goodsClass_detail().removeAll(
					goodsSpecification.getSpec_goodsClass_detail());
		}
		if (id.equals("")) {
			this.goodsSpecService.save(goodsSpecification);
		} else
			this.goodsSpecService.update(goodsSpecification);
		this.genericProperty(request, goodsSpecification, count);
		request.getSession(false).setAttribute("url",
				CommUtil.getURL(request) + "/seller/goods_spec_list.htm?currentPage="+currentPage+"&name="+name);
		request.getSession(false).setAttribute("op_title", "规格添加成功");
		return "redirect:/seller/success.htm";
	}

	private void clearProperty(HttpServletRequest request,
			GoodsSpecification spec) {
		for (GoodsSpecProperty property : spec.getProperties()) {
			Accessory img = property.getSpecImage();
			CommUtil.del_acc(request, img);
			for (Goods goods : property.getGoods_list()) {
				goods.getGoods_specs().remove(property);
			}
			for (GoodsCart gc : property.getCart_list()) {
				gc.getGsps().remove(property);
			}
			this.goodsSpecPropertyService.delete(property.getId());
		}
	}

	private void genericProperty(HttpServletRequest request,
			GoodsSpecification spec, String count) {
		for (int i = 1; i <= CommUtil.null2Int(count); i++) {
			Integer sequence = CommUtil.null2Int(request
					.getParameter("sequence_" + i));
			String value = CommUtil.null2String(request.getParameter("value_"
					+ i));
			if (sequence != null && !sequence.equals("") && value != null
					&& !value.equals("")) {
				String id = CommUtil.null2String(request
						.getParameter("id_" + i));
				GoodsSpecProperty property = null;
				if (id != null && !id.equals("")) {
					property = this.goodsSpecPropertyService.getObjById(Long
							.parseLong(id));
				} else
					property = new GoodsSpecProperty();
				property.setAddTime(new Date());
				property.setSequence(sequence);
				property.setSpec(spec);
				property.setValue(value);
				String uploadFilePath = this.configService.getSysConfig()
						.getUploadFilePath();
				String saveFilePathName = request.getSession()
						.getServletContext().getRealPath("/")
						+ uploadFilePath + File.separator + "spec";
				Map map = new HashMap();
				try {
					String fileName = property.getSpecImage() == null
							? ""
							: property.getSpecImage().getName();
					map = CommUtil.saveFileToServer(request, "specImage_" + i,
							saveFilePathName, fileName, null);
					if (fileName.equals("")) {
						if (map.get("fileName") != "") {
							Accessory specImage = new Accessory();
							specImage.setName(CommUtil.null2String(map
									.get("fileName")));
							specImage.setExt(CommUtil.null2String(map
									.get("mime")));
							specImage.setSize(BigDecimal.valueOf(CommUtil
									.null2Double(map.get("fileSize"))));
							specImage.setPath(uploadFilePath + "/spec");
							specImage.setWidth(CommUtil.null2Int(map
									.get("width")));
							specImage.setHeight(CommUtil.null2Int(map
									.get("height")));
							specImage.setAddTime(new Date());
							this.accessoryService.save(specImage);
							property.setSpecImage(specImage);
						}
					} else {
						if (map.get("fileName") != "") {
							Accessory specImage = property.getSpecImage();
							specImage.setName(CommUtil.null2String(map
									.get("fileName")));
							specImage.setExt(CommUtil.null2String(map
									.get("mime")));
							specImage.setSize(BigDecimal.valueOf(CommUtil
									.null2Double(map.get("fileSize"))));
							specImage.setPath(uploadFilePath + "/spec");
							specImage.setWidth(CommUtil.null2Int(map
									.get("width")));
							specImage.setHeight(CommUtil.null2Int(map
									.get("height")));
							specImage.setAddTime(new Date());
							this.accessoryService.update(specImage);
						}
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (id.equals("")) {
					this.goodsSpecPropertyService.save(property);
				} else {
					this.goodsSpecPropertyService.update(property);
				}
			}
		}
	}

	@SecurityMapping(title = "商品规格删除", value = "/seller/goods_spec_del.htm*", rtype = "seller", rname = "规格管理", rcode = "spec_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_spec_del.htm")
	public String delete(HttpServletRequest request, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		for (String id : ids) {
			if (!id.equals("")) {
				GoodsSpecification obj = this.goodsSpecService.getObjById(Long
						.parseLong(id));
				if (obj != null) {
					if (obj.getStore().getUser().getId().equals(user.getId())) {
						this.clearProperty(request, obj);
						this.goodsSpecService.delete(Long.parseLong(id));
					}
				}
			}
		}
		return "redirect:goods_spec_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "商品规格Ajax删除", value = "/seller/goods_property_delete.htm*", rtype = "seller", rname = "规格管理", rcode = "spec_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_property_delete.htm")
	public void goods_property_delete(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean ret = true;
		if (!id.equals("")) {
			this.databaseTools.execute("delete from "
					+ Globals.DEFAULT_TABLE_SUFFIX
					+ "goods_spec where spec_id=" + id);
			this.databaseTools.execute("delete from "
					+ Globals.DEFAULT_TABLE_SUFFIX + "cart_gsp where gsp_id="
					+ id);
			GoodsSpecProperty property = this.goodsSpecPropertyService
					.getObjById(Long.parseLong(id));
			property.setSpec(null);
			Accessory img = property.getSpecImage();
			CommUtil.del_acc(request, img);
			property.setSpecImage(null);
			ret = this.goodsSpecPropertyService.delete(property.getId());
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

	@SecurityMapping(title = "规格名称验证", value = "/seller/goods_spec_verify.htm*", rtype = "seller", rname = "规格管理", rcode = "spec_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_spec_verify.htm")
	public void goods_spec_verify(HttpServletRequest request,
			HttpServletResponse response, String name, String id,
			String store_id) {
		boolean ret = true;
		Map params = new HashMap();
		params.put("name", name);
		params.put("store_id", CommUtil.null2Long(store_id));
		params.put("id", CommUtil.null2Long(id));
		List<GoodsSpecification> gss = this.goodsSpecService
				.query("select obj from GoodsSpecification obj where obj.name=:name and obj.id!=:id and obj.store.id=:store_id",
						params, -1, -1);
		if (gss != null && gss.size() > 0) {
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

	@SecurityMapping(title = "规格新增分类加载", value = "/seller/spec_gc_load.htm*", rtype = "seller", rname = "规格管理", rcode = "spec_seller", rgroup = "商品管理")
	@RequestMapping("/seller/spec_gc_load.htm")
	public ModelAndView spec_gc_load(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String id) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/spec_gc_load.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (gc_id != null && !gc_id.equals("")) {
			GoodsClass gc = this.goodsClassService.getObjById(CommUtil
					.null2Long(gc_id));
			mv.addObject("gcs", gc.getChilds());
		}
		if (id != null && !id.equals("")) {
			GoodsSpecification obj = this.goodsSpecService.getObjById(CommUtil
					.null2Long(id));
			mv.addObject("obj", obj);
		}
		return mv;
	}

	@SecurityMapping(title = "商品规格AJAX添加", value = "/seller/goods_spec_ajax_add.htm*", rtype = "seller", rname = "规格管理", rcode = "spec_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_spec_ajax_add.htm")
	public void goods_spec_ajax_add(HttpServletRequest request,
			HttpServletResponse response, String id, String name)
			throws ClassNotFoundException {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		Map jsonmap = new HashMap();

		SysConfig config = this.configService.getSysConfig();
		String path = "";
		String uploadFilePath = config.getUploadFilePath();

		GoodsSpecification obj = this.goodsSpecService.getObjById(Long
				.parseLong(id));
		GoodsSpecProperty property = new GoodsSpecProperty();
		property.setAddTime(new Date());
		property.setSequence(obj.getProperties().size() + 1);
		property.setSpec(obj);
		property.setValue(name);
		if (obj.getType().equals("img")) {
			try {
				// <1>. 判断路径是否存在,若不存在则创建路径
				String filePath = request.getSession().getServletContext()
						.getRealPath("/")
						+ uploadFilePath + File.separator + "spec";
				CommUtil.createFolder(filePath);
				path = uploadFilePath + "/spec";
				Map map = CommUtil.saveFileToServer(request, "gsp_add_img_"
						+ id, filePath, null, null);
				Accessory image = new Accessory();
				image.setUser(user);
				image.setAddTime(new Date());
				image.setExt((String) map.get("mime"));
				image.setPath(path);
				image.setWidth(CommUtil.null2Int(map.get("width")));
				image.setHeight(CommUtil.null2Int(map.get("height")));
				image.setName(CommUtil.null2String(map.get("fileName")));
				image.setInfo("");
				this.accessoryService.save(image);
				property.setSpecImage(image);
				jsonmap.put("url", CommUtil.getURL(request) + "/" + path + "/"
						+ image.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (this.goodsSpecPropertyService.save(property)) {
			jsonmap.put("id", "" + property.getId());
			jsonmap.put("name", property.getValue());
		}

		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(jsonmap, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SecurityMapping(title = "商品规格大类AJAX添加", value = "/seller/goods_specification_ajax_add.htm*", rtype = "seller", rname = "规格管理", rcode = "spec_seller", rgroup = "商品管理")
	@RequestMapping("/seller/goods_specification_ajax_add.htm")
	public void goods_specification_ajax_add(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String name,
			String sequence, String type) throws ClassNotFoundException {
		Map jsonmap = new HashMap();

		GoodsSpecification goodsSpecification = new GoodsSpecification();
		goodsSpecification.setAddTime(new Date());
		goodsSpecification.setSpec_type(1);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		goodsSpecification.setStore(user.getStore());
		goodsSpecification.setSequence(CommUtil.null2Int(sequence));
		goodsSpecification.setType(type);
		goodsSpecification.setName(name);
		GoodsClass gc = this.goodsclassService.getObjById(CommUtil
				.null2Long(gc_id));
		goodsSpecification.getSpec_goodsClass_detail().removeAll(
				goodsSpecification.getSpec_goodsClass_detail());
		if (gc.getLevel() == 2) {
			goodsSpecification.setGoodsclass(gc.getParent());
			List list = new ArrayList();
			list.add(gc);
			goodsSpecification.setSpec_goodsClass_detail(list);
		} else {
			goodsSpecification.setGoodsclass(gc);
		}
		if (this.goodsSpecService.save(goodsSpecification)) {
			jsonmap.put("id", goodsSpecification.getId());
			jsonmap.put("name", name);
			jsonmap.put("sequence", goodsSpecification.getSequence());
			jsonmap.put("type", goodsSpecification.getType());
		}

		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(jsonmap, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}