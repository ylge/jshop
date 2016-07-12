package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
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
import com.iskyshop.core.tools.database.DatabaseTools;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.Template;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.ZTCGoldLog;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGoodsSpecPropertyService;
import com.iskyshop.foundation.service.IGoodsTypePropertyService;
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
import com.iskyshop.foundation.service.IZTCGoldLogService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.LuceneVo;
import com.iskyshop.manage.admin.tools.GoodsTools;
import com.iskyshop.manage.admin.tools.StoreTools;
import com.iskyshop.manage.seller.tools.TransportTools;
import com.iskyshop.msg.MsgTools;
import com.iskyshop.msg.SpelTemplate;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;

/**
 * 
 * <p>
 * Title: GoodsManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商品管理类
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
 * @date 2014年5月27日
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class GoodsManageAction {
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
	private IZTCGoldLogService ztcglService;
	@Autowired
	private IFavoriteService favoriteService;
	@Autowired
	private GoodsTools goodsTools;

	/**
	 * Goods列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "商品列表", value = "/admin/goods_list.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_list.htm")
	public ModelAndView goods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String store_name, String brand_id, String gc_id,
			String goods_type, String goods_name, String store_recommend,
			String status) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);
		qo.setOrderBy("addTime");
		qo.setOrderType("desc");
		WebForm wf = new WebForm();
		wf.toQueryPo(request, qo, Goods.class, mv);
		if (store_name != null && !store_name.equals("")) {
			qo.addQuery("obj.goods_store.store_name", new SysMap("store_name",
					"%" + CommUtil.null2String(store_name) + "%"), "like");
			mv.addObject("store_name", store_name);
		}
		if (brand_id != null && !brand_id.equals("")) {
			qo.addQuery("obj.goods_brand.id", new SysMap("goods_brand_id",
					CommUtil.null2Long(brand_id)), "=");
			mv.addObject("brand_id", brand_id);
		}
		if (gc_id != null && !gc_id.equals("")) {
			GoodsClass gc=this.goodsClassService.getObjById(CommUtil.null2Long(gc_id));
			Set<Long> ids = null;
			if (gc != null) {
				ids = this.genericIds(gc.getId());
			}
			if (ids != null && ids.size() > 0) {
				Map paras = new HashMap();
				paras.put("ids", ids);
				qo.addQuery("obj.gc.id in (:ids)", paras);
			}
			mv.addObject("gc_id", gc_id);
		}
		if (goods_type != null && !goods_type.equals("")) {
			qo.addQuery("obj.goods_type", new SysMap("goods_goods_type",
					CommUtil.null2Int(goods_type)), "=");
			mv.addObject("goods_type", goods_type);
		}
		if (goods_name != null && !goods_name.equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("goods_goods_name", "%"
					+ goods_name + "%"), "like");
			mv.addObject("goods_name", goods_name);
		}
		if (store_recommend != null && !store_recommend.equals("")) {
			qo.addQuery(
					"obj.store_recommend",
					new SysMap("goods_store_recommend", CommUtil
							.null2Boolean(store_recommend)), "=");
			mv.addObject("store_recommend", store_recommend);
		}
		if (status != null && !status.equals("")) {
			qo.addQuery("obj.goods_status",
					new SysMap("goods_status", CommUtil.null2Int(status)), "=");
			mv.addObject("status", status);
		} else {
			qo.addQuery("obj.goods_status", new SysMap("goods_status", -2), ">");
		}
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		List<GoodsBrand> gbs = this.goodsBrandService
				.query("select new GoodsBrand(id,addTime,name) from GoodsBrand obj order by obj.sequence asc",
						null, -1, -1);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select new GoodsClass(id,className) from GoodsClass obj where obj.parent.id is null order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		mv.addObject("gbs", gbs);
		return mv;
	}

	@SecurityMapping(title = "违规商品列表", value = "/admin/goods_outline.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_outline.htm")
	public ModelAndView goods_outline(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name, String gb_id, String gc_id) {
		ModelAndView mv = new JModelAndView("admin/blue/goods_outline.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		GoodsQueryObject qo = new GoodsQueryObject(currentPage, mv, orderBy,
				orderType);
		if (goods_name != null && !goods_name.equals("")) {
			qo.addQuery("obj.goods_name", new SysMap("goods_goods_name", "%"
					+ goods_name + "%"), "like");
			mv.addObject("goods_name", goods_name);
		}
		if (gb_id != null && !gb_id.equals("")) {
			qo.addQuery("obj.goods_brand.id", new SysMap("goods_brand_id",
					CommUtil.null2Long(gb_id)), "=");
			mv.addObject("gb_id", gb_id);
		}
		if (gb_id != null && !gc_id.equals("")) {
			qo.addQuery("obj.gc.id",
					new SysMap("goods_class_id", CommUtil.null2Long(gc_id)),
					"=");
			mv.addObject("gc_id", gc_id);
		}
		qo.addQuery("obj.goods_status", new SysMap("goods_status", -2), "=");
		IPageList pList = this.goodsService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/admin/goods_list.htm", "",
				params, pList, mv);
		List<GoodsBrand> gbs = this.goodsBrandService
				.query("select new GoodsBrand(id,addTime,name) from GoodsBrand obj order by obj.sequence asc",
						null, -1, -1);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.level=1 order by obj.sequence asc",
						null, -1, -1);
		mv.addObject("gcs", gcs);
		mv.addObject("gbs", gbs);
		return mv;
	}

	@SecurityMapping(title = "商品删除", value = "/admin/goods_del.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_del.htm")
	public String goods_del(HttpServletRequest request, String mulitId)
			throws Exception {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Goods goods = this.goodsService.getObjById(CommUtil
						.null2Long(id));				
				String goods_name = goods.getGoods_name();
				User seller = null;
				if (goods.getGoods_store()!=null) {
					Long seller_id = goods.getGoods_store().getUser().getId();
					 seller = this.userService.getObjById(seller_id);
				}
				List<Evaluate> evaluates = goods.getEvaluates();
				for (Evaluate e : evaluates) {
					this.evaluateService.delete(e.getId());
				}
				goods.getGoods_photos().clear();
				goods.getGoods_ugcs().clear();
				goods.getGoods_specs().clear();
				Map params = new HashMap();
				params.clear();// 直通车商品记录
				params.put("gid", goods.getId());
				List<GoodsCart> goodCarts = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.goods.id = :gid",
								params, -1, -1);
				for (GoodsCart gc : goods.getCarts()) {
					gc.getGsps().clear();
					this.goodsCartService.delete(gc.getId());
				}
				List<ZTCGoldLog> ztcgls = this.ztcglService
						.query("select obj from ZTCGoldLog obj where obj.zgl_goods_id=:gid",
								params, -1, -1);
				for (ZTCGoldLog ztc : ztcgls) {
					this.ztcglService.delete(ztc.getId());
				}
				// 删除相应商品收藏
				params.clear();
				params.put("gid", goods.getId());
				List<Favorite> favs = this.favoriteService.query(
						"select obj from Favorite obj where obj.goods_id=:gid",
						params, -1, -1);
				for (Favorite obj : favs) {
					this.favoriteService.delete(obj.getId());
				}
				goods.setGoods_main_photo(null);
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
				// 发送站内短信提醒卖家
				if (goods.getGoods_type() == 1) {
					this.send_site_msg(request,
							"sms_toseller_goods_delete_by_admin_notify",
							seller, goods_name, "商品存在违规，系统已删除");
				}
			}
		}
		return "redirect:goods_list.htm";
	}

	private void send_site_msg(HttpServletRequest request, String mark,
			User seller, String goods_name, String reason) throws Exception {
		Template template = this.templateService.getObjByProperty(null, "mark",
				mark);
		if (template.isOpen() && seller != null) {
			if (seller.getMobile()!=null) {
				ExpressionParser exp = new SpelExpressionParser();
				EvaluationContext context = new StandardEvaluationContext();
				context.setVariable("reason", reason);
				context.setVariable("user", seller);
				context.setVariable("goods_name", goods_name);
				context.setVariable("config", this.configService.getSysConfig());
				context.setVariable("webPath", CommUtil.getURL(request));
				Expression ex = exp.parseExpression(template.getContent(),
						new SpelTemplate());
				String content = ex.getValue(context, String.class);
				boolean ret = this.msgTools.sendSMS(seller.getMobile(), content);
			}
		}
	}

	@SecurityMapping(title = "商品AJAX更新", value = "/admin/goods_ajax.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_ajax.htm")
	public void goods_ajax(HttpServletRequest request,
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
		if (obj.getGoods_status() == 0) {
			// 更新lucene索引
			String goods_lucene_path = System.getProperty("iskyshopb2b2c.root")
					+ File.separator + "luence" + File.separator + "goods";
			File file = new File(goods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(goods_lucene_path);
			}
			LuceneVo vo = new LuceneVo();
			vo.setVo_id(obj.getId());
			vo.setVo_title(obj.getGoods_name());
			vo.setVo_content(obj.getGoods_details());
			vo.setVo_type("goods");
			vo.setVo_store_price(CommUtil.null2Double(obj
					.getGoods_current_price()));
			vo.setVo_add_time(obj.getAddTime().getTime());
			vo.setVo_goods_salenum(obj.getGoods_salenum());
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setConfig(this.configService.getSysConfig());
			lucene.setIndex_path(goods_lucene_path);
			lucene.update(CommUtil.null2String(obj.getId()), vo);
		} else {
			String goods_lucene_path = System.getProperty("iskyshopb2b2c.root")
					+ File.separator + "luence" + File.separator + "goods";
			File file = new File(goods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(goods_lucene_path);
			}
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setConfig(this.configService.getSysConfig());
			lucene.setIndex_path(goods_lucene_path);
			lucene.delete_index(CommUtil.null2String(id));
		}
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

	@SecurityMapping(title = "商品审核", value = "/admin/goods_audit.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_audit.htm")
	public String goods_audit(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String status)
			throws ClassNotFoundException {
		String ids[] = mulitId.split(",");
		for (String id : ids) {
			if (id != null) {
				Goods obj = this.goodsService
						.getObjById(CommUtil.null2Long(id));
				obj.setGoods_status(obj.getPublish_goods_status());// 设置商品发布审核后状态
				goodsService.update(obj);
			}
		}
		return "redirect:goods_list.htm?status=" + status;
	}

	@SecurityMapping(title = "商品拒绝", value = "/admin/goods_refuse.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_refuse.htm")
	public void goods_refuse(HttpServletRequest request,
			HttpServletResponse response,String good_id,String refuse_info,String status,String currentPage) {
		response.setCharacterEncoding("utf-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/plain");
		Goods good=this.goodsService.getObjById(CommUtil.null2Long(good_id));
		String ret="";
		if(good!=null){
			good.setGoods_status(-6);
			good.setRefuse_info(refuse_info);
			this.goodsService.update(good);
			ret=CommUtil.getURL(request)+"/admin/goods_list.htm?status="+status+"&currentPage="+currentPage;
		}else{
			ret="no";
		}
		try {
			response.getWriter().print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 商品管理-二维码生成
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "商品二维码生成", value = "/admin/goods_qr.htm*", rtype = "admin", rname = "商品管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping("/admin/goods_qr.htm")
	public String goods_qr(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage)
			throws ClassNotFoundException {
		String ids[] = mulitId.split(",");
		for (String id : ids) {
			if (id != null) {
				Goods goods = this.goodsService.getObjById(CommUtil
						.null2Long(id));
				String uploadFilePath = this.configService.getSysConfig()
						.getUploadFilePath();
				goods.setQr_img_path(CommUtil.getURL(request) + "/"
						+ uploadFilePath + "/" + "goods_qr" + "/"
						+ goods.getId() + "_qr.jpg");
				String second_domain = "";
				if (goods.getGoods_type() == 1) {// 商家商品
					if (this.configService.getSysConfig()
							.isSecond_domain_open()
							&& !CommUtil.null2String(
									goods.getGoods_store()
											.getStore_second_domain()).equals(
									"")) {
						second_domain = CommUtil.generic_domain(request);
					}
					this.goodsTools
							.createUserGoodsQR(CommUtil.getURL(request),
									second_domain, CommUtil.null2String(goods
											.getId()), uploadFilePath, goods
											.getGoods_main_photo().getId());
				} else {
					this.goodsTools
							.createSelfGoodsQR(CommUtil.getURL(request),
									CommUtil.null2String(goods.getId()),
									uploadFilePath, goods.getGoods_main_photo()
											.getId());
				}
				goodsService.update(goods);
			}
		}
		return "redirect:goods_list.htm?currentPage=" + currentPage;
	}
	private Set<Long> genericIds(Long id) {
		Set<Long> ids = new HashSet<Long>();
		if (id != null) {
			ids.add(id);
			Map params = new HashMap();
			params.put("pid", id);
			List id_list = this.goodsClassService
					.query("select obj.id from GoodsClass obj where obj.parent.id=:pid",
							params, -1, -1);
			ids.addAll(id_list);
			for (int i = 0; i < id_list.size(); i++) {
				Long cid = CommUtil.null2Long(id_list.get(i));
				Set<Long> cids = genericIds(cid);
				ids.add(cid);
				ids.addAll(cids);
			}
		}
		return ids;
	}
}