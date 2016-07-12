package com.iskyshop.manage.admin.action;

import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.beans.BeanUtils;
import com.iskyshop.core.beans.BeanWrapper;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.core.tools.database.DatabaseTools;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Album;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsFormat;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.GoodsType;
import com.iskyshop.foundation.domain.GoodsTypeProperty;
import com.iskyshop.foundation.domain.Payment;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.Transport;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.WaterMark;
import com.iskyshop.foundation.domain.query.AccessoryQueryObject;
import com.iskyshop.foundation.domain.query.AlbumQueryObject;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.domain.query.TransportQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsFormatService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGoodsSpecPropertyService;
import com.iskyshop.foundation.service.IGoodsSpecificationService;
import com.iskyshop.foundation.service.IGoodsTypePropertyService;
import com.iskyshop.foundation.service.IGoodsTypeService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPaymentService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.ITransportService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserGoodsClassService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IWaterMarkService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.LuceneVo;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.manage.admin.tools.GoodsTools;
import com.iskyshop.manage.admin.tools.ImageTools;
import com.iskyshop.manage.admin.tools.StoreTools;
import com.iskyshop.manage.seller.tools.TransportTools;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;

/**
 * 
 * <p>
 * Title: GoodsSelfManageAction.java
 * </p>
 * 
 * <p>
 * Description:自营商品管理控制器，平台可发布商品并进行管理
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
 * @date 2014年4月25日
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class SelfGoodsManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private MsgTools msgTools;
	@Autowired
	private DatabaseTools databaseTools;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IOrderFormLogService orderFormLogService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private IGoodsSpecPropertyService specPropertyService;
	@Autowired
	private IGoodsTypePropertyService goodsTypePropertyService;
	@Autowired
	private IWaterMarkService waterMarkService;
	@Autowired
	private IAlbumService albumService;
	@Autowired
	private ITransportService transportService;
	@Autowired
	private IPaymentService paymentService;
	@Autowired
	private TransportTools transportTools;
	@Autowired
	private StoreTools storeTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IGoodsSpecificationService goodsSpecificationService;
	@Autowired
	private IGoodsFormatService goodsFormatService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private GoodsTools goodsTools;
	@Autowired
	private ImageTools ImageTools;
	@Autowired
	private IGoodsTypePropertyService gtpService;
	@Autowired
	private IGoodsTypeService goodsTypeService;

	/**
	 * 商品发布第一步
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商品发布第一步", value = "/admin/add_goods_first.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/add_goods_first.htm")
	public ModelAndView add_goods_first(HttpServletRequest request,
			HttpServletResponse response, String id, String add_type) {
		ModelAndView mv = new JModelAndView("admin/blue/error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		request.getSession(false).removeAttribute("goods_class_info");
		Map params = new HashMap();
		List<Payment> payments = new ArrayList<Payment>();
		params.put("install", true);
		payments = this.paymentService.query(
				"select obj from Payment obj where obj.install=:install",
				params, -1, -1);
		if (payments.size() == 0) {
			mv.addObject("op_title", "请至少开通一种支付方式");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/admin/payment_list.htm");
			return mv;
		} else {
			mv = new JModelAndView("admin/blue/add_goods_first.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			String json_staples = "";
			if (user.getStaple_gc() != null && !user.getStaple_gc().equals("")) {
				json_staples = user.getStaple_gc();
			}
			List<Map> staples = Json.fromJson(List.class, json_staples);
			List<GoodsClass> goodsClass = this.goodsClassService
					.query("select obj from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
							null, -1, -1);
			mv.addObject("goodsClassStaple", staples);
			mv.addObject("goodsClass", goodsClass);
			mv.addObject("id", CommUtil.null2String(id));
			mv.addObject("add_type", add_type);
			return mv;
		}
	}

	/**
	 * 根据常用商品分类加载分类信息
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "根据常用商品分类加载分类信息", value = "/admin/load_goods_class_staple.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/load_goods_class_staple.htm")
	public void load_goods_class_staple(HttpServletRequest request,
			HttpServletResponse response, String id, String name) {
		GoodsClass obj = null;
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (id != null && !id.equals("")) {
			List<Map> list_map = Json.fromJson(List.class, user.getStaple_gc());
			for (Map map : list_map) {
				if (CommUtil.null2String(map.get("id")).equals(id)) {
					obj = this.goodsClassService.getObjById(CommUtil
							.null2Long(map.get("id")));
				}
			}
		}
		if (name != null && !name.equals(""))
			obj = this.goodsClassService.getObjByProperty(null, "className",
					name);
		List<List<Map>> list = new ArrayList<List<Map>>();
		if (obj != null) {
			// 该版本要求三级分类才能添加到常用分类
			request.getSession(false).setAttribute("goods_class_info", obj);
			Map params = new HashMap();
			List<Map> second_list = new ArrayList<Map>();
			List<Map> third_list = new ArrayList<Map>();
			List<Map> other_list = new ArrayList<Map>();

			if (obj.getLevel() == 2) {
				params.put("pid", obj.getParent().getParent().getId());
				List<GoodsClass> second_gcs = this.goodsClassService
						.query("select obj from GoodsClass obj where obj.parent.id=:pid order by obj.sequence asc",
								params, -1, -1);
				for (GoodsClass gc : second_gcs) {
					Map map = new HashMap();
					map.put("id", gc.getId());
					map.put("className", gc.getClassName());
					second_list.add(map);
				}
				params.clear();
				params.put("pid", obj.getParent().getId());
				List<GoodsClass> third_gcs = this.goodsClassService
						.query("select obj from GoodsClass obj where obj.parent.id=:pid order by obj.sequence asc",
								params, -1, -1);
				for (GoodsClass gc : third_gcs) {
					Map map = new HashMap();
					map.put("id", gc.getId());
					map.put("className", gc.getClassName());
					third_list.add(map);
				}
			}

			if (obj.getLevel() == 1) {
				params.clear();
				params.put("pid", obj.getParent().getId());
				List<GoodsClass> third_gcs = this.goodsClassService
						.query("select obj from GoodsClass obj where obj.parent.id=:pid order by obj.sequence asc",
								params, -1, -1);
				for (GoodsClass gc : third_gcs) {
					Map map = new HashMap();
					map.put("id", gc.getId());
					map.put("className", gc.getClassName());
					second_list.add(map);
				}
			}

			Map map = new HashMap();
			String staple_info = this.generic_goods_class_info(obj);
			map.put("staple_info",
					staple_info.substring(0, staple_info.length() - 1));
			other_list.add(map);

			list.add(second_list);
			list.add(third_list);
			list.add(other_list);
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

	/**
	 * AJAX加载商品分类数据
	 * 
	 * @param request
	 * @param response
	 * @param pid
	 *            上级分类Id
	 * @param session
	 *            是否加载到session中
	 */
	@SecurityMapping(title = "加载商品分类", value = "/admin/load_goods_class.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/load_goods_class.htm")
	public void load_goods_class(HttpServletRequest request,
			HttpServletResponse response, String pid, String session) {
		GoodsClass obj = this.goodsClassService.getObjById(CommUtil
				.null2Long(pid));
		List<Map> list = new ArrayList<Map>();
		if (obj != null) {
			for (GoodsClass gc : obj.getChilds()) {
				Map map = new HashMap();
				map.put("id", gc.getId());
				map.put("className", gc.getClassName());
				list.add(map);
			}
			if (CommUtil.null2Boolean(session)) {
				request.getSession(false).setAttribute("goods_class_info", obj);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(list));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 添加管理员常用商品分类
	 * 
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "添加常用商品分类", value = "/admin/load_goods_class.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/add_goods_class_staple.htm")
	public void add_goods_class_staple(HttpServletRequest request,
			HttpServletResponse response) {
		String ret = "error";
		String json = "";
		Map map = new HashMap();
		if (request.getSession(false).getAttribute("goods_class_info") != null) {
			GoodsClass gc = this.goodsClassService
					.getObjById(((GoodsClass) request.getSession(false)
							.getAttribute("goods_class_info")).getId());
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			List<Map> list_map = new ArrayList<Map>();
			if (user.getStaple_gc() != null && !user.getStaple_gc().equals("")) {
				list_map = Json.fromJson(List.class, user.getStaple_gc());
			}
			if (list_map.size() > 0) {
				boolean flag = true;
				for (Map staple : list_map) {
					if (gc.getId().toString()
							.equals(CommUtil.null2String(staple.get("id")))) {
						flag = false;
						break;
					}
				}
				if (flag) {
					System.out.println(gc.getParent().getId());
					map.put("name",
							gc.getParent().getParent().getClassName() + ">"
									+ gc.getParent().getClassName() + ">"
									+ gc.getClassName());
					map.put("id", gc.getId());
					list_map.add(map);
					json = Json.toJson(list_map, JsonFormat.compact());
				}
			} else {
				map.put("name",
						gc.getParent().getParent().getClassName() + ">"
								+ gc.getParent().getClassName() + ">"
								+ gc.getClassName());
				map.put("id", gc.getId());
				list_map.add(map);
				json = Json.toJson(list_map, JsonFormat.compact());
			}
			user.setStaple_gc(json);
			boolean flag = this.userService.update(user);
			if (flag) {
				ret = "success";
			}
		}
		map.put("ret", ret);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json = Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 删除管理员常用商品分类
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "删除常用商品分类", value = "/admin/del_goods_class_staple.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/del_goods_class_staple.htm")
	public void del_goods_class_staple(HttpServletRequest request,
			HttpServletResponse response, String id) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		List<Map> list_map = Json.fromJson(List.class, user.getStaple_gc());
		boolean ret = false;
		for (Map map : list_map) {
			if (CommUtil.null2String(map.get("id")).equals(id)) {
				ret = list_map.remove(map);
			}
		}
		user.setStaple_gc(Json.toJson(list_map, JsonFormat.compact()));
		this.userService.update(user);
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

	/**
	 * Goods列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商品发布第二步", value = "/admin/add_goods_second.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/add_goods_second.htm")
	public ModelAndView add_goods_second(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView("admin/blue/add_goods_second.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (request.getSession(false).getAttribute("goods_class_info") != null) {
			GoodsClass gc = (GoodsClass) request.getSession(false)
					.getAttribute("goods_class_info");
			gc = this.goodsClassService.getObjById(gc.getId());
			String goods_class_info = this.generic_goods_class_info(gc);
			mv.addObject("goods_class",
					this.goodsClassService.getObjById(gc.getId()));
			mv.addObject("goods_class_info", goods_class_info.substring(0,
					goods_class_info.length() - 1));
			request.getSession(false).removeAttribute("goods_class_info");
			if (gc.getLevel() == 2) {// 发布商品选择分类时选择三级分类,查询出所有与该三级分类关联的规格，即规格对应的详细商品分类
				Map spec_map = new HashMap();
				spec_map.put("spec_type", 0);
				List<GoodsSpecification> goods_spec_list = this.goodsSpecificationService
						.query("select obj from GoodsSpecification obj where obj.spec_type=:spec_type order by sequence asc",
								spec_map, -1, -1);
				List<GoodsSpecification> spec_list = new ArrayList<GoodsSpecification>();
				for (GoodsSpecification gspec : goods_spec_list) {
					for (GoodsClass spec_goodsclass_detail : gspec
							.getSpec_goodsClass_detail()) {
						if (gc.getId().equals(spec_goodsclass_detail.getId())) {
							spec_list.add(gspec);
						}

					}
				}
				mv.addObject("goods_spec_list", spec_list);
			} else if (gc.getLevel() == 1) {// 发布商品选择分类时选择二级分类,规格对应的主营商品分类
				Map spec_map = new HashMap();
				spec_map.put("spec_type", 0);
				spec_map.put("gc_id", gc.getId());
				List<GoodsSpecification> goods_spec_list = this.goodsSpecificationService
						.query("select obj from GoodsSpecification obj where obj.spec_type=:spec_type and obj.goodsclass.id=:gc_id order by sequence asc",
								spec_map, -1, -1);
				mv.addObject("goods_spec_list", goods_spec_list);
			}
			Map params = new HashMap();
			GoodsClass goods_class = null;
			if (gc.getLevel() == 2) {
				goods_class = gc.getParent().getParent();
			}
			if (gc.getLevel() == 1) {
				goods_class = gc.getParent();
			}
			params.clear();
			params.put("audit", 1);
			List<GoodsBrand> gbs = this.goodsBrandService
					.query("select obj from GoodsBrand obj where obj.audit=:audit order by obj.first_word asc",
							params, -1, -1);
			mv.addObject("gbs", gbs);
			mv.addObject("imageSuffix", this.storeViewTools
					.genericImageSuffix(this.configService.getSysConfig()
							.getImageSuffix()));
			// 查询商品版式信息
			params.clear();
			params.put("gf_cat", 1);
			List<GoodsFormat> gfs = this.goodsFormatService.query(
					"select obj from GoodsFormat obj where obj.gf_cat=:gf_cat",
					params, -1, -1);
			mv.addObject("gfs", gfs);
			// 查询地址信息，前端需要商家选择发货地址
			List<Area> areas = this.areaService.query(
					"select obj from Area obj where obj.parent.id is null",
					null, -1, -1);
			mv.addObject("areas", areas);
			String goods_session = CommUtil.randomString(32);
			mv.addObject("goods_session", goods_session);
			request.getSession(false).setAttribute("goods_session",
					goods_session);
			// 处理上传格式
			String[] strs = this.configService.getSysConfig().getImageSuffix()
					.split("\\|");
			StringBuffer sb = new StringBuffer();
			for (String str : strs) {
				sb.append("." + str + ",");
			}
			mv.addObject("imageSuffix1", sb);
		}
		this.isAdminAlbumExist();
		return mv;
	}

	@SecurityMapping(title = "产品规格显示", value = "/admin/goods_inventory.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/goods_inventory.htm")
	public ModelAndView goods_inventory(HttpServletRequest request,
			HttpServletResponse response, String goods_spec_ids,
			String supplement) {
		ModelAndView mv = mv = new JModelAndView(
				"admin/blue/goods_inventory.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String[] spec_ids = goods_spec_ids.split(",");
		List<GoodsSpecProperty> gsps = new ArrayList<GoodsSpecProperty>();
		for (String spec_id : spec_ids) {
			if (!spec_id.equals("")) {
				GoodsSpecProperty gsp = this.specPropertyService
						.getObjById(Long.parseLong(spec_id));
				gsps.add(gsp);
			}
		}
		Set<GoodsSpecification> specs = new HashSet<GoodsSpecification>();
		for (GoodsSpecProperty gsp : gsps) {
			specs.add(gsp.getSpec());
		}
		for (GoodsSpecification spec : specs) {
			spec.getProperties().clear();
			for (GoodsSpecProperty gsp : gsps) {
				if (gsp.getSpec().getId().equals(spec.getId())) {
					spec.getProperties().add(gsp);
				}
			}
		}
		GoodsSpecification[] spec_list = specs
				.toArray(new GoodsSpecification[specs.size()]);
		Arrays.sort(spec_list, new Comparator() {
			@Override
			public int compare(Object obj1, Object obj2) {
				// TODO Auto-generated method stub
				GoodsSpecification a = (GoodsSpecification) obj1;
				GoodsSpecification b = (GoodsSpecification) obj2;
				if (a.getSequence() == b.getSequence()) {
					return 0;
				} else {
					return a.getSequence() > b.getSequence() ? 1 : -1;
				}
			}
		});
		List<List<GoodsSpecProperty>> gsp_list = this
				.generic_spec_property(specs);
		mv.addObject("specs", Arrays.asList(spec_list));
		mv.addObject("gsps", gsp_list);
		if (supplement != null && !supplement.equals("")) {
			mv.addObject("supplement", supplement);
		}
		return mv;
	}

	/**
	 * arraylist转化为二维数组
	 * 
	 * @param list
	 * @return
	 */
	public static GoodsSpecProperty[][] list2group(
			List<List<GoodsSpecProperty>> list) {
		GoodsSpecProperty[][] gps = new GoodsSpecProperty[list.size()][];
		for (int i = 0; i < list.size(); i++) {
			gps[i] = list.get(i).toArray(
					new GoodsSpecProperty[list.get(i).size()]);
		}
		return gps;
	}

	/**
	 * 生成库存组合
	 * 
	 * @param specs
	 * @return
	 */
	private List<List<GoodsSpecProperty>> generic_spec_property(
			Set<GoodsSpecification> specs) {
		List<List<GoodsSpecProperty>> result_list = new ArrayList<List<GoodsSpecProperty>>();
		List<List<GoodsSpecProperty>> list = new ArrayList<List<GoodsSpecProperty>>();
		int max = 1;
		for (GoodsSpecification spec : specs) {
			list.add(spec.getProperties());
		}
		// 将List<List<GoodsSpecProperty>> 转换为二维数组
		GoodsSpecProperty[][] gsps = this.list2group(list);
		for (int i = 0; i < gsps.length; i++) {
			max *= gsps[i].length;
		}
		for (int i = 0; i < max; i++) {
			List<GoodsSpecProperty> temp_list = new ArrayList<GoodsSpecProperty>();
			int temp = 1; // 注意这个temp的用法。
			for (int j = 0; j < gsps.length; j++) {
				temp *= gsps[j].length;
				temp_list.add(j, gsps[j][i / (max / temp) % gsps[j].length]);
			}
			GoodsSpecProperty[] temp_gsps = temp_list
					.toArray(new GoodsSpecProperty[temp_list.size()]);
			Arrays.sort(temp_gsps, new Comparator() {
				public int compare(Object obj1, Object obj2) {
					// TODO Auto-generated method stub
					GoodsSpecProperty a = (GoodsSpecProperty) obj1;
					GoodsSpecProperty b = (GoodsSpecProperty) obj2;
					if (a.getSpec().getSequence() == b.getSpec().getSequence()) {
						return 0;
					} else {
						return a.getSpec().getSequence() > b.getSpec()
								.getSequence() ? 1 : -1;
					}
				}
			});
			result_list.add(Arrays.asList(temp_gsps));
		}
		return result_list;
	}

	@SecurityMapping(title = "运费模板显示", value = "/admin/goods_transport.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/goods_transport.htm")
	public ModelAndView goods_transport(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String ajax) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_transport.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (CommUtil.null2Boolean(ajax)) {
			mv = new JModelAndView("admin/blue/goods_transport_list.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		TransportQueryObject qo = new TransportQueryObject(currentPage, mv,
				orderBy, orderType);
		Store store = store = this.userService.getObjById(
				SecurityUserHolder.getCurrentUser().getId()).getStore();
		qo.addQuery("obj.trans_user", new SysMap("obj_trans_user", 0), "=");
		qo.setPageSize(1);
		IPageList pList = this.transportService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/goods_transport.htm",
				"", params, pList, mv);
		mv.addObject("transportTools", transportTools);
		return mv;
	}

	/**
	 * 商品发布第三步
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商品发布第三步", value = "/admin/add_goods_finish.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/add_goods_finish.htm")
	public ModelAndView add_goods_finish(HttpServletRequest request,
			HttpServletResponse response, String id, String goods_class_id,
			String image_ids, String goods_main_img_id, String goods_brand_id,
			String goods_spec_ids, String goods_properties,
			String intentory_details, String goods_session,
			String transport_type, String transport_id, String goods_status,
			String f_code_count, String f_code_profix, String advance_date,
			String goods_top_format_id, String goods_bottom_format_id,
			String delivery_area_id, String goods_spec_id_value,
			String currentPage) {
		ModelAndView mv = null;
		String goods_session1 = CommUtil.null2String(request.getSession(false)
				.getAttribute("goods_session"));
		if (goods_session1.equals("")) {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "禁止重复提交表单");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/admin/add_goods_first.htm");
		} else {
			if (true) {
				if (CommUtil.null2String(id).equals("")) {
					mv = new JModelAndView("admin/blue/success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
					mv.addObject("op_title", "商品发布成功");
					mv.addObject("list_url", CommUtil.getURL(request)
							+ "/admin/add_goods_first.htm");
				} else {
					mv = new JModelAndView("admin/blue/success.html",
							configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
					mv.addObject("op_title", "商品编辑成功");
					mv.addObject("list_url", CommUtil.getURL(request)
							+ "/admin/goods_self_list.htm?currentPage="
							+ currentPage);
				}
				WebForm wf = new WebForm();
				Goods goods = null;
				String obj_status = null;
				Map temp_params = new HashMap();
				Set<Long> temp_ids = new HashSet<Long>();
				if (id.equals("")) {
					goods = wf.toPo(request, Goods.class);
					goods.setAddTime(new Date());
					goods.setUser_admin(SecurityUserHolder.getCurrentUser());
					goods.setGoods_type(0);
				} else {
					Goods obj = this.goodsService
							.getObjById(Long.parseLong(id));
					BigDecimal old_price = obj.getGoods_current_price();
					obj_status = CommUtil.null2String(obj.getGoods_status());
					goods = (Goods) wf.toPo(request, obj);
					goods.setPrice_history(old_price);
				}
				if (goods.getGroup_buy() != 2) {// 只有非团购商品店铺价格与当前价格一致，团购商品价格为团购价格
					goods.setGoods_current_price(goods.getStore_price());
				}
				// 商品名称不可以带有任何html字样，进行过滤
				goods.setGoods_name(Jsoup.clean(goods.getGoods_name(),
						Whitelist.none()));
				// 商品详情不可以带有违规标签，如script等等
				// goods.setGoods_details(CommUtil.filterHTML(goods
				// .getGoods_details()));
				goods.setGoods_details(goods.getGoods_details());
				GoodsClass gc = this.goodsClassService.getObjById(Long
						.parseLong(goods_class_id));
				goods.setGc(gc);
				Accessory main_img = null;
				if (goods_main_img_id != null && !goods_main_img_id.equals("")) {
					main_img = this.accessoryService.getObjById(Long
							.parseLong(goods_main_img_id));
				}
				goods.setGoods_main_photo(main_img);
				if (image_ids != null && !image_ids.equals("")) {
					String[] img_ids = image_ids.split(",");

					goods.getGoods_photos().clear();
					temp_ids.clear();
					for (String img_id : img_ids) {
						if (!img_id.equals("")) {
							temp_ids.add(CommUtil.null2Long(img_id));
						}
					}
					if (!temp_ids.isEmpty()) {
						temp_params.clear();
						temp_params.put("ids", temp_ids);
						List<Accessory> temp_list = this.accessoryService
								.query("select obj from Accessory obj where obj.id in(:ids)",
										temp_params, -1, -1);
						goods.getGoods_photos().addAll(temp_list);
					}
				}
				if (goods_brand_id != null && !goods_brand_id.equals("")) {
					GoodsBrand goods_brand = this.goodsBrandService
							.getObjById(Long.parseLong(goods_brand_id));
					goods.setGoods_brand(goods_brand);
				}
				goods.getGoods_specs().clear();
				String[] spec_ids = goods_spec_ids.split(",");
				temp_ids.clear();
				for (String spec_id : spec_ids) {
					if (!spec_id.equals("")) {
						temp_ids.add(CommUtil.null2Long(spec_id));
					}
				}
				if (!temp_ids.isEmpty()) {
					temp_params.clear();
					temp_params.put("ids", temp_ids);
					List<GoodsSpecProperty> temp_list = this.specPropertyService
							.query("select new GoodsSpecProperty(id) from GoodsSpecProperty obj where obj.id in(:ids)",
									temp_params, -1, -1);
					goods.getGoods_specs().addAll(temp_list);
				}
				if (!goods_spec_id_value.isEmpty()) {
					String id_values[] = goods_spec_id_value.split(",");
					List list = new ArrayList();
					for (String id_value : id_values) {
						String single_id_value[] = id_value.split(":");
						Map map = new HashMap();
						map.put("id", single_id_value[0]);
						map.put("name", single_id_value[1]);
						list.add(map);
					}
					goods.setGoods_specs_info(Json.toJson(list,
							JsonFormat.compact()));
				}

				List<Map> maps = new ArrayList<Map>();
				String[] properties = goods_properties.split(";");
				for (String property : properties) {
					if (!property.equals("")) {
						String[] list = property.split(",");
						Map map = new HashMap();
						map.put("id", list[0]);
						map.put("val", list[1]);
						map.put("name", this.goodsTypePropertyService
								.getObjById(Long.parseLong(list[0])).getName());
						maps.add(map);
					}
				}
				goods.setGoods_property(Json.toJson(maps, JsonFormat.compact()));
				maps.clear();
				boolean warn_suppment = false;
				String[] inventory_list = intentory_details.split(";");
				for (String inventory : inventory_list) {
					if (!inventory.equals("")) {
						String[] list = inventory.split(",");
						Map map = new HashMap();
						map.put("id", list[0]);
						map.put("count", list[1]);
						map.put("supp", list[2]);
						map.put("price", list[3]);
						map.put("code", list[4]);
						maps.add(map);
						if (CommUtil.null2Int(list[2]) > CommUtil
								.null2Int(list[1])) {
							warn_suppment = true;
						}
					}
				}
				if (warn_suppment) {
					goods.setWarn_inventory_status(-1);
				} else {
					goods.setWarn_inventory_status(0);
				}
				goods.setGoods_inventory_detail(Json.toJson(maps,
						JsonFormat.compact()));
				if (goods.getInventory_type().equals("all")) {
					int inventory = goods.getGoods_inventory();
					if (CommUtil.null2Int(inventory)
							- goods.getGoods_warn_inventory() > 0) {
						goods.setWarn_inventory_status(0);// 预警状态恢复
					} else {
						goods.setWarn_inventory_status(-1);
					}
				}
				if (CommUtil.null2Int(transport_type) == 0) {// 使用运费模板
					Transport trans = this.transportService.getObjById(CommUtil
							.null2Long(transport_id));
					goods.setTransport(trans);
				}
				if (CommUtil.null2Int(transport_type) == 1) {// 使用固定运费
					goods.setTransport(null);
				}
				// 是否为F码商品，是则生成F码
				if (goods.getF_sale_type() == 1
						&& CommUtil.null2Int(f_code_count) > 0) {
					Set<String> set = new HashSet<String>();
					while (true) {
						if (set.size() == CommUtil.null2Int(f_code_count)) {
							break;
						}
						set.add((f_code_profix + CommUtil.randomString(12))
								.toUpperCase());
					}
					List<Map> f_code_maps = new ArrayList<Map>();
					if (!CommUtil.null2String(goods.getGoods_f_code()).equals(
							"")) {
						f_code_maps = Json.fromJson(List.class,
								goods.getGoods_f_code());
					}

					for (String code : set) {
						Map f_code_map = new HashMap();
						f_code_map.put("code", code);
						f_code_map.put("status", 0);// 0表示该F码未使用，1为已经使用
						f_code_maps.add(f_code_map);
					}
					if (f_code_maps.size() > 0) {
						goods.setGoods_f_code(Json.toJson(f_code_maps,
								JsonFormat.compact()));
					}
				}
				// 是否为预售商品，是则加入发货时间
				if (goods.getAdvance_sale_type() == 1) {
					goods.setAdvance_date(CommUtil.formatDate(advance_date));
				}

				// 添加商品版式
				goods.setGoods_top_format_id(CommUtil
						.null2Long(goods_top_format_id));
				GoodsFormat gf = this.goodsFormatService.getObjById(CommUtil
						.null2Long(goods_top_format_id));
				if (gf != null) {
					goods.setGoods_top_format_content(gf.getGf_content());
				} else {
					goods.setGoods_top_format_content(null);
				}
				goods.setGoods_bottom_format_id(CommUtil
						.null2Long(goods_bottom_format_id));
				gf = this.goodsFormatService.getObjById(CommUtil
						.null2Long(goods_bottom_format_id));
				if (gf != null) {
					goods.setGoods_bottom_format_content(gf.getGf_content());
				} else {
					goods.setGoods_bottom_format_content(null);
				}
				goods.setDelivery_area_id(CommUtil.null2Long(delivery_area_id));
				Area de_area = this.areaService.getObjById(CommUtil
						.null2Long(delivery_area_id));
				if (de_area != null) {
					String delivery_area = de_area.getParent().getParent()
							.getAreaName()
							+ de_area.getParent().getAreaName()
							+ de_area.getAreaName();
					goods.setDelivery_area(delivery_area);
				}
				if (id.equals("")) {
					this.goodsService.save(goods);
					// 自营商品第一次发布商品，生成二维码
					String uploadFilePath = this.configService.getSysConfig()
							.getUploadFilePath();
					goods.setQr_img_path(CommUtil.getURL(request) + "/"
							+ uploadFilePath + "/" + "goods_qr" + "/"
							+ goods.getId() + "_qr.jpg");
					this.goodsTools
							.createSelfGoodsQR(CommUtil.getURL(request),
									CommUtil.null2String(goods.getId()),
									uploadFilePath, goods.getGoods_main_photo()
											.getId());
					if ("0".equals(goods_status)) {
						// 新增lucene索引
						String goods_lucene_path = System
								.getProperty("iskyshopb2b2c.root")
								+ File.separator
								+ "luence"
								+ File.separator
								+ "goods";
						File file = new File(goods_lucene_path);
						if (!file.exists()) {
							CommUtil.createFolder(goods_lucene_path);
						}
						LuceneVo vo = this.luceneVoTools
								.updateGoodsIndex(goods);
						SysConfig config = this.configService.getSysConfig();
						LuceneUtil lucene = LuceneUtil.instance();
						lucene.setConfig(config);
						lucene.setIndex_path(goods_lucene_path);
						lucene.writeIndex(vo);
					}
				} else {
					String goods_lucene_path = System
							.getProperty("iskyshopb2b2c.root")
							+ File.separator
							+ "luence" + File.separator + "goods";
					if ("0".equals(obj_status) && "0".equals(goods_status)) {// 发布后直接上架
						// 更新lucene索引
						File file = new File(goods_lucene_path);
						if (!file.exists()) {
							CommUtil.createFolder(goods_lucene_path);
						}
						LuceneVo vo = this.luceneVoTools
								.updateGoodsIndex(goods);
						LuceneUtil lucene = LuceneUtil.instance();
						lucene.setIndex_path(goods_lucene_path);
						lucene.update(CommUtil.null2String(goods.getId()), vo);
					}
					if ("0".equals(obj_status)
							&& ("1".equals(goods_status) || "2"
									.equals(goods_status))) {// 发布后在仓库中
						// 删除lucene索引
						LuceneUtil lucene = LuceneUtil.instance();
						lucene.setIndex_path(goods_lucene_path);
						lucene.delete_index(id);
					}
					if (("1".equals(obj_status) || "2".equals(obj_status))
							&& "0".equals(goods_status)) {// 在仓库中发布后上架
						// 添加lucene索引
						LuceneVo vo = this.luceneVoTools
								.updateGoodsIndex(goods);
						LuceneUtil lucene = LuceneUtil.instance();
						lucene.setIndex_path(goods_lucene_path);
						lucene.writeIndex(vo);
					}
					this.goodsService.update(goods);
				}
				mv.addObject("obj", goods);
				request.getSession(false).removeAttribute("goods_session");
			} else {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "参数错误");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/admin/add_goods_first.htm");
			}
		}
		return mv;
	}

	@SecurityMapping(title = "商品补货", value = "/admin/goods_supplement.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/goods_supplement.htm")
	public ModelAndView goods_supplement(HttpServletRequest request,
			HttpServletResponse response, String id) throws IOException {
		ModelAndView mv = new JModelAndView("admin/blue/goods_supplement.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		String gsp_ids = "";
		for (GoodsSpecProperty gsp : obj.getGoods_specs()) {
			gsp_ids = gsp_ids + "," + gsp.getId();
		}
		if (obj != null && obj.getGoods_type() == 0) {
			Map spec_map = new HashMap();
			spec_map.put("spec_type", 0);
			List<GoodsSpecification> goods_spec_list = this.goodsSpecificationService
					.query("select obj from GoodsSpecification obj where obj.spec_type=:spec_type order by sequence asc",
							spec_map, -1, -1);
			List<GoodsSpecification> spec_list = new ArrayList<GoodsSpecification>();
			for (GoodsSpecification gspec : goods_spec_list) {
				for (GoodsClass spec_goodsclass_detail : gspec
						.getSpec_goodsClass_detail()) {
					if (obj.getGc().getId()
							.equals(spec_goodsclass_detail.getId())
							|| obj.getGc().getParent().getId()
									.equals(spec_goodsclass_detail.getId())) {
						spec_list.add(gspec);
					}
				}
				mv.addObject("goods_spec_list", spec_list);
			}
			mv.addObject("gsp_ids", gsp_ids);
			mv.addObject("obj", obj);
		}
		return mv;
	}

	@SecurityMapping(title = "商品补货保存", value = "/admin/goods_supplement_save.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/goods_supplement_save.htm")
	public void goods_supplement_save(HttpServletRequest request,
			HttpServletResponse response, String id, String inventory,
			String intentory_details) throws IOException {
		int code = -100;
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Map json_map = new HashMap();
		boolean warn_suppment = true;
		if (obj != null && obj.getGoods_type() == 0) {
			if (obj.getInventory_type().equals("all")) {
				obj.setGoods_inventory(CommUtil.null2Int(inventory));
				if (CommUtil.null2Int(inventory)
						- obj.getGoods_warn_inventory() > 0) {
					obj.setWarn_inventory_status(0);// 预警状态恢复
				}
				code = 100;
			}
			if (obj.getInventory_type().equals("spec")) {
				List<Map> maps = (List<Map>) Json.fromJson(obj
						.getGoods_inventory_detail());
				for (Map map : maps) {
					String[] inventory_list = intentory_details.split(";");
					for (String temp_inventory : inventory_list) {
						if (!temp_inventory.equals("")) {
							String[] list = temp_inventory.split(",");
							if (list[0].equals(CommUtil.null2String(map
									.get("id")))) {
								map.put("count", list[1]);
								if (CommUtil.null2Int(map.get("count")) <= CommUtil
										.null2Int(map.get("supp"))) {
									warn_suppment = false;
								}
							}
						}
					}
				}
				if (warn_suppment) {
					obj.setWarn_inventory_status(0);// 预警状态恢复
				}
				obj.setGoods_inventory_detail(Json.toJson(maps,
						JsonFormat.compact()));
				code = 100;
			}
			this.goodsService.update(obj);
		}
		json_map.put("code", code);
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

	@SecurityMapping(title = "商品图片上传", value = "/admin/swf_upload.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/swf_upload.htm")
	public void swf_upload(HttpServletRequest request,
			HttpServletResponse response, String album_id, String session_u_id) {
		User user = null;
		if (SecurityUserHolder.getCurrentUser() != null) {
			user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
		} else {
			int len = session_u_id.length();
			session_u_id = session_u_id.substring(0, len - 5);
			session_u_id = session_u_id.substring(5, session_u_id.length());
			user = this.userService
					.getObjById(CommUtil.null2Long(session_u_id));
		}
		String path = this.storeTools.createAdminFolder(request);
		String url = this.storeTools.createAdminFolderURL();
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest
				.getFile("imgFile");
		Map json_map = new HashMap();
		try {
			Map map = CommUtil.saveFileToServer(request, "imgFile", path, null,
					null);
			Map params = new HashMap();
			params.put("user_id", user.getId());
			List<WaterMark> wms = this.waterMarkService.query(
					"select obj from WaterMark obj where obj.user.id=:user_id",
					params, -1, -1);
			if (wms.size() > 0) {
				WaterMark mark = wms.get(0);
				if (mark.isWm_image_open() && mark.getWm_image() != null) {
					String pressImg = request.getSession().getServletContext()
							.getRealPath("")
							+ File.separator
							+ mark.getWm_image().getPath()
							+ File.separator + mark.getWm_image().getName();
					String targetImg = path + File.separator
							+ map.get("fileName");
					int pos = mark.getWm_image_pos();
					float alpha = mark.getWm_image_alpha();
					CommUtil.waterMarkWithImage(pressImg, targetImg, pos, alpha);
				}
				if (mark.isWm_text_open()) {
					String targetImg = path + File.separator
							+ map.get("fileName");
					int pos = mark.getWm_text_pos();
					String text = mark.getWm_text();
					String markContentColor = mark.getWm_text_color();
					CommUtil.waterMarkWithText(targetImg, targetImg, text,
							markContentColor, new Font(mark.getWm_text_font(),
									Font.BOLD, mark.getWm_text_font_size()),
							pos, 100f);
				}
			}
			Accessory image = new Accessory();
			image.setAddTime(new Date());
			image.setExt((String) map.get("mime"));
			image.setPath(url);
			image.setWidth(CommUtil.null2Int(map.get("width")));
			image.setHeight(CommUtil.null2Int(map.get("height")));
			image.setName(CommUtil.null2String(map.get("fileName")));
			image.setUser(user);
			Album album = null;
			if (album_id != null && !album_id.equals("")) {
				album = this.albumService.getObjById(CommUtil
						.null2Long(album_id));
			} else {
				album = this.albumService.getDefaultAlbum(user.getId());
				if (album == null) {
					album = new Album();
					album.setAddTime(new Date());
					album.setAlbum_name("默认相册【" + user.getUserName() + "】");
					album.setAlbum_sequence(-10000);
					album.setAlbum_default(true);
					album.setUser(user);
					this.albumService.save(album);
				}
			}
			image.setAlbum(album);
			this.accessoryService.save(image);
			json_map.put("url", CommUtil.getURL(request) + "/" + url + "/"
					+ image.getName());
			json_map.put("id", image.getId());
			// 同步生成小图片
			String ext = image.getExt().indexOf(".") < 0
					? "." + image.getExt()
					: image.getExt();
			String source = request.getSession().getServletContext()
					.getRealPath("/")
					+ image.getPath() + File.separator + image.getName();
			String target = source + "_small" + ext;
			CommUtil.createSmall(source, target, this.configService
					.getSysConfig().getSmallWidth(), this.configService
					.getSysConfig().getSmallHeight());
			// 同步生成中等图片
			String midext = image.getExt().indexOf(".") < 0 ? "."
					+ image.getExt() : image.getExt();
			String midtarget = source + "_middle" + ext;
			CommUtil.createSmall(source, midtarget, this.configService
					.getSysConfig().getMiddleWidth(), this.configService
					.getSysConfig().getMiddleHeight());
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
			writer.print(Json.toJson(json_map));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String generic_goods_class_info(GoodsClass gc) {
		String goods_class_info = gc.getClassName() + ">";
		if (gc.getParent() != null) {
			String class_info = generic_goods_class_info(gc.getParent());
			goods_class_info = class_info + goods_class_info;
		}
		return goods_class_info;
	}

	@SecurityMapping(title = "商品图片删除", value = "/admin/goods_image_del.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/goods_image_del.htm")
	public void goods_image_del(HttpServletRequest request,
			HttpServletResponse response, String image_id) {
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			Map map = new HashMap();
			Accessory img = this.accessoryService.getObjById(CommUtil
					.null2Long(image_id));
			boolean ret = false;
			if (img != null) {
				for (Goods goods : img.getGoods_main_list()) {
					goods.setGoods_main_photo(null);
					this.goodsService.update(goods);
				}
				for (Goods goods1 : img.getGoods_list()) {
					goods1.getGoods_photos().remove(img);
					this.goodsService.update(goods1);
				}
				ret = this.accessoryService.delete(img.getId());
				if (ret) {
					CommUtil.deleteFile(request.getSession()
							.getServletContext().getRealPath("/")
							+ img.getPath()
							+ File.separator
							+ img.getName()
							+ "_middle." + img.getExt());
					CommUtil.del_acc(request, img);
				}
			}
			map.put("result", ret);
			writer = response.getWriter();
			writer.print(Json.toJson(map));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 自营商品列表
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "自营商品列表", value = "/admin/goods_self_list.htm*", rtype = "admin", rname = "商品管理", rcode = "goods_self", rgroup = "自营")
	@RequestMapping("/admin/goods_self_list.htm")
	public ModelAndView goods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_status, String brand_id,
			String goods_name, String u_admin_id) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_self_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, Goods.class, mv);
		if (goods_status == null) {
			goods_status = "0";
		}
		if (goods_status.equals("0")) {
			qo.addQuery("obj.goods_status", new SysMap("goods_status", 0), "=");
		}
		if (goods_status.equals("1")) {
			qo.addQuery("obj.goods_status", new SysMap("goods_status", 1), "=");
			mv = new JModelAndView("admin/blue/goods_self_storage.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		if (goods_status.equals("-2")) {
			qo.addQuery("obj.goods_status", new SysMap("goods_status", -2), "=");
			mv = new JModelAndView("admin/blue/goods_self_outline.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		if (goods_name != null && !goods_name.equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("obj_goods_name", "%"
					+ goods_name + "%"), "like");
			mv.addObject("goods_name", goods_name);
		}
		if (brand_id != null && !brand_id.equals("")) {
			qo.addQuery("obj.goods_brand.id", new SysMap("obj_goods_brand",
					CommUtil.null2Long(brand_id)), "=");
			mv.addObject("brand_id", brand_id);
		}
		if (u_admin_id != null && !u_admin_id.equals("")) {
			qo.addQuery("obj.user_admin.id", new SysMap("obj_admin_id",
					CommUtil.null2Long(u_admin_id)), "=");
			mv.addObject("u_admin_id", u_admin_id);
		}
		mv.addObject("goods_status", goods_status);
		qo.addQuery("obj.goods_type", new SysMap("obj_goods_type", 0), "=");
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		List<GoodsBrand> gbs = this.goodsBrandService
				.query("select new GoodsBrand(id,addTime,name) from GoodsBrand obj order by obj.sequence asc",
						null, -1, -1);
		Map admin_map = new HashMap();
		admin_map.put("userRole", "ADMIN");
		List<User> user_admins = this.userService.query(
				"select obj from User obj where obj.userRole=:userRole",
				admin_map, -1, -1);
		mv.addObject("user_admins", user_admins);
		mv.addObject("gbs", gbs);
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "商品编辑", value = "/admin/goods_self_edit.htm*", rtype = "admin", rname = "商品管理", rcode = "goods_self", rgroup = "自营")
	@RequestMapping("/admin/goods_self_edit.htm")
	public ModelAndView goods_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String add_type) {
		ModelAndView mv = new JModelAndView("admin/blue/add_goods_second.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (add_type == null) {// 非编辑分类
			request.getSession(false).removeAttribute("goods_class_info");
		}
		Goods obj = this.goodsService.getObjById(Long.parseLong(id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		AccessoryQueryObject aqo = new AccessoryQueryObject();
		aqo.setPageSize(8);
		aqo.addQuery("obj.user.userRole", new SysMap("user_role", "admin"), "=");
		aqo.setOrderBy("addTime");
		aqo.setOrderType("desc");
		IPageList pList = this.accessoryService.list(aqo);
		String photo_url = CommUtil.getURL(request)
				+ "/admin/goods_img_album.htm";
		mv.addObject("photos", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(photo_url,
				"", pList.getCurrentPage(), pList.getPages()));
		mv.addObject("edit", true);
		mv.addObject("img_remain_size", 100000);
		mv.addObject("obj", obj);
		mv.addObject("currentPage", currentPage);
		if (request.getSession(false).getAttribute("goods_class_info") != null) {
			GoodsClass session_gc = (GoodsClass) request.getSession(false)
					.getAttribute("goods_class_info");
			GoodsClass gc = this.goodsClassService.getObjById(session_gc
					.getId());
			mv.addObject("goods_class_info",
					this.storeTools.generic_goods_class_info(gc));
			mv.addObject("goods_class", gc);
			HashMap params = new HashMap();
			GoodsClass goods_class = null;
			if (gc.getLevel() == 2) {
				goods_class = gc.getParent().getParent();
			}
			if (gc.getLevel() == 1) {
				goods_class = gc.getParent();
			}
			params.clear();
			params.put("audit", 1);
			List<GoodsBrand> gbs = this.goodsBrandService
					.query("select obj from GoodsBrand obj where obj.audit=:audit order by obj.first_word asc",
							params, -1, -1);
			mv.addObject("gbs", gbs);
			if (gc.getLevel() == 2) {// 发布商品选择分类时选择三级分类,查询出所有与该三级分类关联的规格，即规格对应的详细商品分类
				Map spec_map = new HashMap();
				spec_map.put("spec_type", 0);
				List<GoodsSpecification> goods_spec_list = this.goodsSpecificationService
						.query("select obj from GoodsSpecification obj where obj.spec_type=:spec_type order by sequence asc",
								spec_map, -1, -1);
				List<GoodsSpecification> spec_list = new ArrayList<GoodsSpecification>();
				for (GoodsSpecification gspec : goods_spec_list) {
					for (GoodsClass spec_goodsclass_detail : gspec
							.getSpec_goodsClass_detail()) {
						if (gc.getId().equals(spec_goodsclass_detail.getId())) {
							spec_list.add(gspec);
						}

					}
				}
				mv.addObject("goods_spec_list", spec_list);
			} else if (gc.getLevel() == 1) {// 发布商品选择分类时选择二级分类,规格对应的主营商品分类
				Map spec_map = new HashMap();
				spec_map.put("spec_type", 0);
				spec_map.put("gc_id", gc.getId());
				List<GoodsSpecification> goods_spec_list = this.goodsSpecificationService
						.query("select obj from GoodsSpecification obj where obj.spec_type=:spec_type and obj.goodsclass.id=:gc_id order by sequence asc",
								spec_map, -1, -1);
				mv.addObject("goods_spec_list", goods_spec_list);
			}
			request.getSession(false).removeAttribute("goods_class_info");
		} else {
			if (obj.getGc() != null) {
				mv.addObject("goods_class_info",
						this.storeTools.generic_goods_class_info(obj.getGc()));
				mv.addObject("goods_class", obj.getGc());
				GoodsClass gc = obj.getGc();
				if (gc.getLevel() == 2) {// 发布商品选择分类时选择三级分类,查询出所有与该三级分类关联的规格，即规格对应的详细商品分类
					Map spec_map = new HashMap();
					spec_map.put("spec_type", 0);
					List<GoodsSpecification> goods_spec_list = this.goodsSpecificationService
							.query("select obj from GoodsSpecification obj where obj.spec_type=:spec_type order by sequence asc",
									spec_map, -1, -1);
					List<GoodsSpecification> spec_list = new ArrayList<GoodsSpecification>();
					for (GoodsSpecification gspec : goods_spec_list) {
						for (GoodsClass spec_goodsclass_detail : gspec
								.getSpec_goodsClass_detail()) {
							if (gc.getId().equals(
									spec_goodsclass_detail.getId())) {
								spec_list.add(gspec);
							}

						}
					}
					mv.addObject("goods_spec_list", spec_list);
				} else if (gc.getLevel() == 1) {// 发布商品选择分类时选择二级分类,规格对应的主营商品分类
					Map spec_map = new HashMap();
					spec_map.put("spec_type", 0);
					spec_map.put("gc_id", gc.getId());
					List<GoodsSpecification> goods_spec_list = this.goodsSpecificationService
							.query("select obj from GoodsSpecification obj where obj.spec_type=:spec_type and obj.goodsclass.id=:gc_id order by sequence asc",
									spec_map, -1, -1);
					mv.addObject("goods_spec_list", goods_spec_list);
				}
				GoodsClass goods_class = null;
				if (obj.getGc().getLevel() == 2) {
					goods_class = obj.getGc().getParent().getParent();
				}
				if (obj.getGc().getLevel() == 1) {
					goods_class = obj.getGc().getParent();
				}
				Map params = new HashMap();
				params.put("gc_id", goods_class.getId());
				List<GoodsBrand> gbs = this.goodsBrandService
						.query("select obj from GoodsBrand obj where obj.gc.id=:gc_id order by obj.sequence asc",
								params, -1, -1);
				mv.addObject("gbs", gbs);
				// 查询商品版式信息
				params.clear();
				params.put("gf_cat", 1);
				List<GoodsFormat> gfs = this.goodsFormatService
						.query("select obj from GoodsFormat obj where obj.gf_cat=:gf_cat",
								params, -1, -1);
				mv.addObject("gfs", gfs);
				// 查询地址信息，前端需要商家选择发货地址
				List<Area> areas = this.areaService.query(
						"select obj from Area obj where obj.parent.id is null",
						null, -1, -1);
				mv.addObject("areas", areas);
			}
		}
		String goods_session = CommUtil.randomString(32);
		mv.addObject("goods_session", goods_session);
		request.getSession(false).setAttribute("goods_session", goods_session);
		mv.addObject("imageSuffix", this.storeViewTools
				.genericImageSuffix(this.configService.getSysConfig()
						.getImageSuffix()));
		// 处理上传格式
		String[] strs = this.configService.getSysConfig().getImageSuffix()
				.split("\\|");
		StringBuffer sb = new StringBuffer();
		for (String str : strs) {
			sb.append("." + str + ",");
		}
		mv.addObject("imageSuffix1", sb);
		mv.addObject("jsessionid", request.getSession().getId());
		return mv;
	}

	@SecurityMapping(title = "商品上下架", value = "/admin/goods_self_sale.htm*", rtype = "admin", rname = "商品管理", rcode = "goods_self", rgroup = "自营")
	@RequestMapping("/admin/goods_self_sale.htm")
	public String goods_sale(HttpServletRequest request,
			HttpServletResponse response, String mulitId) {
		String status = "0";
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Goods goods = this.goodsService.getObjById(Long.parseLong(id));
				int goods_status = goods.getGoods_status() == 0 ? 1 : 0;
				goods.setGoods_status(goods_status);
				this.goodsService.update(goods);
				if (goods_status == 0) {
					status = "1";
					// 添加lucene索引
					String goods_lucene_path = System
							.getProperty("iskyshopb2b2c.root")
							+ File.separator
							+ "luence" + File.separator + "goods";
					File file = new File(goods_lucene_path);
					if (!file.exists()) {
						CommUtil.createFolder(goods_lucene_path);
					}
					LuceneVo vo = this.luceneVoTools.updateGoodsIndex(goods);
					LuceneUtil lucene = LuceneUtil.instance();
					lucene.setIndex_path(goods_lucene_path);
					lucene.update(CommUtil.null2String(goods.getId()), vo);
				} else {
					// 删除lucene索引
					String goods_lucene_path = System
							.getProperty("iskyshopb2b2c.root")
							+ File.separator
							+ "luence" + File.separator + "goods";
					File file = new File(goods_lucene_path);
					if (!file.exists()) {
						CommUtil.createFolder(goods_lucene_path);
					}
					LuceneUtil lucene = LuceneUtil.instance();
					lucene.setIndex_path(goods_lucene_path);
					lucene.delete_index(CommUtil.null2String(goods.getId()));
				}
			}
		}
		return "redirect:" + "/admin/goods_self_list.htm?goods_status="
				+ status;
	}

	@SecurityMapping(title = "商品删除", value = "/admin/goods_self_del.htm*", rtype = "admin", rname = "商品管理", rcode = "goods_self", rgroup = "自营")
	@RequestMapping("/admin/goods_self_del.htm")
	public String goods_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String op,
			String currentPage) {
		int status = 0;
		if (CommUtil.null2String(op).equals("storage")) {
			status = 1;
		}
		if (CommUtil.null2String(op).equals("out")) {
			status = -2;
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Goods goods = this.goodsService.getObjById(CommUtil
						.null2Long(id));
				Map map = new HashMap();
				map.put("gid", goods.getId());
				List<GoodsCart> goodCarts = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.goods.id = :gid",
								map, -1, -1);
				Long ofid = null;
				List<Evaluate> evaluates = goods.getEvaluates();
				for (Evaluate e : evaluates) {
					this.evaluateService.delete(e.getId());
				}
				goods.getGoods_ugcs().clear();
				goods.getGoods_ugcs().clear();
				goods.getGoods_photos().clear();
				goods.getGoods_ugcs().clear();
				goods.getGoods_specs().clear();
				for (GoodsCart gc : goods.getCarts()) {
					gc.getGsps().clear();
					this.goodsCartService.delete(gc.getId());
				}
				this.goodsService.delete(goods.getId());
				// 删除索引
				String goods_lucene_path = System
						.getProperty("iskyshopb2b2c.root")
						+ File.separator
						+ "luence" + File.separator + "goods";
				File file = new File(goods_lucene_path);
				if (!file.exists()) {
					CommUtil.createFolder(goods_lucene_path);
				}
				LuceneUtil lucene = LuceneUtil.instance();
				lucene.setIndex_path(goods_lucene_path);
				lucene.delete_index(CommUtil.null2String(id));
			}
		}
		return "redirect:/admin/goods_self_list.htm?goods_status=" + status
				+ "&curentPage=" + currentPage;
	}

	@SecurityMapping(title = "商品AJAX更新", value = "/admin/goods_self_ajax.htm*", rtype = "admin", rname = "商品管理", rcode = "goods_self", rgroup = "自营")
	@RequestMapping("/admin/goods_self_ajax.htm")
	public void goods_self_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Goods obj = this.goodsService.getObjById(Long.parseLong(id));
		Field[] fields = Goods.class.getDeclaredFields();
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
		if (fieldName.equals("store_recommend")) {
			if (obj.isStore_recommend()) {
				obj.setStore_recommend_time(new Date());
			} else
				obj.setStore_recommend_time(null);
		}
		this.goodsService.update(obj);
		// if (obj.getGoods_status() == 0) {
		// // 更新lucene索引
		// String goods_lucene_path = System.getProperty("iskyshopb2b2c.root")
		// + File.separator + "luence" + File.separator + "goods";
		// File file = new File(goods_lucene_path);
		// if (!file.exists()) {
		// CommUtil.createFolder(goods_lucene_path);
		// }
		// LuceneVo vo = this.luceneVoTools.updateGoodsIndex(obj);
		// LuceneUtil lucene = LuceneUtil.instance();
		// lucene.setIndex_path(goods_lucene_path);
		// lucene.update(CommUtil.null2String(obj.getId()), vo);
		// } else {
		// String goods_lucene_path = System.getProperty("iskyshopb2b2c.root")
		// + File.separator + "luence" + File.separator + "goods";
		// File file = new File(goods_lucene_path);
		// if (!file.exists()) {
		// CommUtil.createFolder(goods_lucene_path);
		// }
		// LuceneUtil lucene = LuceneUtil.instance();
		// lucene.setIndex_path(goods_lucene_path);
		// lucene.delete_index(CommUtil.null2String(id));
		// }
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

	@SecurityMapping(title = "商品相册列表", value = "/admin/goods_album.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/goods_album.htm")
	public ModelAndView goods_album(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String ajax_type,
			String album_name) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_album.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		AlbumQueryObject aqo = new AlbumQueryObject();
		aqo.addQuery("obj.user.userRole", new SysMap("user_role", "admin"), "=");
		if (CommUtil.null2String(album_name) != "") {
			aqo.addQuery("obj.album_name", new SysMap("album_name", "%"
					+ album_name + "%"), "like");
			mv.addObject("album_name", album_name);
		}
		aqo.setCurrentPage(CommUtil.null2Int(currentPage));
		aqo.setOrderBy("album_sequence");
		aqo.setOrderType("asc");
		aqo.setPageSize(5);
		IPageList pList = this.albumService.list(aqo);
		String album_url = CommUtil.getURL(request) + "/admin/goods_album.htm";
		if (pList.getResult().size() > 0) {
			mv.addObject("albums", pList.getResult());
		} else {// 创建默认相册
			Album album = new Album();
			album.setAddTime(new Date());
			album.setAlbum_default(true);
			album.setAlbum_name("默认相册");
			album.setAlbum_sequence(-10000);
			album.setUser(user);
			this.albumService.save(album);
		}
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(album_url,
				"", pList.getCurrentPage(), pList.getPages()));
		mv.addObject("ajax_type", ajax_type);
		mv.addObject("ImageTools", ImageTools);
		return mv;
	}

	@SecurityMapping(title = "商品图片列表", value = "/admin/goods_img.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/goods_img.htm")
	public ModelAndView goods_img(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String type,
			String album_id) {
		ModelAndView mv = new JModelAndView("admin/blue/" + type + ".html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		AccessoryQueryObject aqo = new AccessoryQueryObject(currentPage, mv,
				"addTime", "desc");
		aqo.setPageSize(20);
		aqo.addQuery("obj.album.id",
				new SysMap("album_id", CommUtil.null2Long(album_id)), "=");
		aqo.addQuery("obj.user.userRole", new SysMap("user_role", "admin"), "=");
		aqo.setOrderBy("addTime");
		aqo.setOrderType("desc");
		IPageList pList = this.accessoryService.list(aqo);
		String photo_url = CommUtil.getURL(request) + "/admin/goods_img.htm";
		mv.addObject("photos", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(photo_url,
				"", pList.getCurrentPage(), pList.getPages()));
		mv.addObject("album_id", album_id);
		return mv;
	}

	/**
	 * 自营商品-二维码生成
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "商品二维码生成", value = "/admin/goods_self_qr.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/goods_self_qr.htm")
	public String goods_self_qr(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage)
			throws ClassNotFoundException {
		String ids[] = mulitId.split(",");
		for (String id : ids) {
			if (id != null && !id.equals("")) {
				Goods goods = this.goodsService.getObjById(CommUtil
						.null2Long(id));
				if (goods != null) {
					if (goods.getGoods_type() == 0) {// 只能操作自营商品
						String uploadFilePath = this.configService
								.getSysConfig().getUploadFilePath();
						goods.setQr_img_path(CommUtil.getURL(request) + "/"
								+ uploadFilePath + "/" + "goods_qr" + "/"
								+ goods.getId() + "_qr.jpg");
						this.goodsTools.createSelfGoodsQR(CommUtil
								.getURL(request), CommUtil.null2String(goods
								.getId()), uploadFilePath, goods
								.getGoods_main_photo().getId());
						goodsService.update(goods);
					}
				}
			}
		}
		return "redirect:goods_self_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "商品二维码Excel生成并下载", value = "/admin/goods_self_f_code_download.htm*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping("/admin/goods_self_f_code_download.htm")
	public void goods_self_f_code_download(HttpServletRequest request,
			HttpServletResponse response, String id) throws IOException {
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		String excel_url = "";
		if (obj.getF_sale_type() == 1 && obj.getGoods_type() == 0
				&& !CommUtil.null2String(obj.getGoods_f_code()).equals("")) {
			List<Map> list = Json.fromJson(List.class, obj.getGoods_f_code());
			String name = CommUtil.null2String(UUID.randomUUID());
			String path = request.getSession().getServletContext()
					.getRealPath("/")
					+ File.separator + "excel";
			CommUtil.createFolder(path);
			path = path + File.separator + name + ".xls";
			FileOutputStream out = new FileOutputStream(new File(path));
			this.exportList2Excel("F码列表", new String[]{"F码信息", "F码状态"}, list,
					out);
			excel_url = CommUtil.getURL(request) + "/excel/" + name + ".xls";
		}

		response.sendRedirect(excel_url);
	}

	/**
	 * 商品发布页新增属性ajax保存,count为数据的条数，params为填写的数据包括名称序号以及属性值
	 * 
	 * @param request
	 * @param response
	 * @param count
	 * @param params
	 * @return
	 */
	@SecurityMapping(title = "属性Ajax保存", value = "/admin/ajax_save_gtp.htm*", rtype = "seller", rname = "属性管理", rcode = "gtp_seller", rgroup = "商品")
	@RequestMapping("/admin/ajax_save_gtp.htm")
	public ModelAndView ajax_save_gtp(HttpServletRequest request,
			HttpServletResponse response, String count, String params,
			String goodsType,String goodsclass) {

		ModelAndView mv = new JModelAndView("admin/blue/goods_type_load.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		String gtps[] = params.substring(1).split("&");
		List gtpList = new ArrayList();
		for (int i = 0; i < CommUtil.null2Int(count); i++) {
			if (gtps.length > 0) {
				String gtp_info[] = gtps[i].split("/");
				GoodsTypeProperty goodsTypeProperty = new GoodsTypeProperty();
				goodsTypeProperty.setName(gtp_info[0]);
				goodsTypeProperty.setValue(gtp_info[1]);
				goodsTypeProperty.setSequence(CommUtil.null2Int(gtp_info[2]));
				goodsTypeProperty.setAddTime(new Date());
				goodsTypeProperty.setDisplay(true);
				GoodsType goodstype = this.goodsTypeService.getObjById(CommUtil
						.null2Long(goodsType));
				if (goodstype == null) {
					GoodsClass goodsClass = this.goodsClassService
							.getObjById(CommUtil.null2Long(goodsclass));
					goodstype = new GoodsType();
					goodstype.setAddTime(new Date());
					this.goodsTypeService.save(goodstype);
					goodsClass.setGoodsType(goodstype);
					this.goodsClassService.update(goodsClass);
				}
				goodsTypeProperty.setGoodsType(goodstype);
				this.gtpService.save(goodsTypeProperty);
				gtpList.add(goodsTypeProperty);
			}
		}
		Comparator comparator = new Comparator<GoodsTypeProperty>() {
			public int compare(GoodsTypeProperty g1, GoodsTypeProperty g2) {
				if (g1.getSequence() != g2.getSequence()) {
					return g1.getSequence() - g2.getSequence();
				} else {
					return g1.getSequence();
				}
			}
		};
		Collections.sort(gtpList, comparator);
		mv.addObject("gtpList", gtpList);
		return mv;
	}

	// 判断管理员相册是否存在，不存在测创建默认相册
	private void isAdminAlbumExist() {
		Map params = new HashMap();
		params.put("role1", "ADMIN");
		params.put("role2", "ADMIN_SELLER");
		List<Album> albums = this.albumService
				.query("select obj.id from Album obj where obj.user.userRole=:role1 or obj.user.userRole=:role2",
						params, -1, -1);
		if (albums.size() == 0) {
			Album album = new Album();
			album.setAddTime(new Date());
			album.setAlbum_default(true);
			album.setAlbum_name("默认相册");
			album.setAlbum_sequence(-10000);
			album.setUser(SecurityUserHolder.getCurrentUser());
			this.albumService.save(album);
		}
	}

	private static boolean exportList2Excel(String title, String[] headers,
			List<Map> list, OutputStream out) {
		boolean ret = true;
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 生成一个表格
		HSSFSheet sheet = workbook.createSheet(title);
		// 设置表格默认宽度为15个字节
		sheet.setDefaultColumnWidth(20);
		// 生成一个样式
		HSSFCellStyle style = workbook.createCellStyle();
		// 设置这些样式
		style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);

		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		// 生成一个字体
		HSSFFont font = workbook.createFont();
		font.setColor(HSSFColor.VIOLET.index);
		font.setFontHeightInPoints((short) 14);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		// 把字体应用到当前的样式
		style.setFont(font);

		// 生成并设置另一个样式
		HSSFCellStyle style_ = workbook.createCellStyle();
		style_.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		style_.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style_.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style_.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style_.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style_.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style_.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style_.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// 生成另一个字体
		HSSFFont font_ = workbook.createFont();
		// font.setColor(color);
		font_.setFontHeightInPoints((short) 14);
		// font_.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		style_.setFont(font_);
		// 产生表格标题行
		HSSFRow row = sheet.createRow(0);
		for (int i = 0; i < headers.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(new HSSFRichTextString(headers[i]));
		}
		// 遍历集合数据，产生数据行
		int index = 0;
		for (Map map : list) {
			index++;
			row = sheet.createRow(index);
			String value = CommUtil.null2String(map.get("code"));
			HSSFCell cell = row.createCell(0);
			cell.setCellStyle(style_);
			cell.setCellValue(value);
			value = CommUtil.null2Int(map.get("status")) == 0 ? "未使用" : "已使用";
			cell = row.createCell(1);
			cell.setCellStyle(style_);
			cell.setCellValue(value);
		}
		try {
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			ret = false;
			e.printStackTrace();
		}
		return ret;
	}

}