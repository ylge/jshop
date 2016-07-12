package com.iskyshop.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
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
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Album;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.ComplaintGoods;
import com.iskyshop.foundation.domain.Coupon;
import com.iskyshop.foundation.domain.CouponInfo;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.GoldLog;
import com.iskyshop.foundation.domain.GoldRecord;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GoodsSpecProperty;
import com.iskyshop.foundation.domain.GoodsSpecification;
import com.iskyshop.foundation.domain.GroupInfo;
import com.iskyshop.foundation.domain.GroupLifeGoods;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.PayoffLog;
import com.iskyshop.foundation.domain.Role;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StoreGrade;
import com.iskyshop.foundation.domain.StorePoint;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.Template;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.WaterMark;
import com.iskyshop.foundation.domain.ZTCGoldLog;
import com.iskyshop.foundation.domain.query.StoreQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAlbumService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IComplaintGoodsService;
import com.iskyshop.foundation.service.IConsultService;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.ICouponService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.foundation.service.IGoldLogService;
import com.iskyshop.foundation.service.IGoldRecordService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGoodsSpecPropertyService;
import com.iskyshop.foundation.service.IGoodsSpecificationService;
import com.iskyshop.foundation.service.IGroupInfoService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IPayoffLogService;
import com.iskyshop.foundation.service.IPredepositService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.IStoreGradeService;
import com.iskyshop.foundation.service.IStorePointService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ITemplateService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.foundation.service.IWaterMarkService;
import com.iskyshop.foundation.service.IZTCGoldLogService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.manage.admin.tools.AreaManageTools;
import com.iskyshop.manage.admin.tools.StoreTools;
import com.iskyshop.msg.SpelTemplate;

/**
 * 
 * <p>
 * Title: StoreManageAction.java
 * </p>
 * 
 * <p>
 * Description: 运营商店铺管理控制器，用来管理店铺，可以添加、修改、删除店铺，运营商所有对店铺的操作均通过该管理控制器完成
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
 * @date 2014-5-12
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class StoreManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IStoreGradeService storeGradeService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IConsultService consultService;
	@Autowired
	private AreaManageTools areaManageTools;
	@Autowired
	private StoreTools storeTools;
	@Autowired
	private DatabaseTools databaseTools;
	@Autowired
	private ITemplateService templateService;
	@Autowired
	private IMessageService messageService;
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
	private IAlbumService albumService;
	@Autowired
	private IGoodsClassService goodsclassService;
	@Autowired
	private IStorePointService storePointService;
	@Autowired
	private IFavoriteService favoriteService;
	@Autowired
	private IComplaintGoodsService complaintGoodsService;
	@Autowired
	private IPredepositService predepositService;

	@Autowired
	private IGroupLifeGoodsService grouplifegoodsService;
	@Autowired
	private IGroupInfoService groupinfoService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private IPayoffLogService paylogService;
	@Autowired
	private IGoodsSpecPropertyService specpropertyService;
	@Autowired
	private IGoodsSpecificationService specService;
	@Autowired
	private IGoldRecordService grService;
	@Autowired
	private IZTCGoldLogService ztcglService;
	@Autowired
	private IGoldLogService glService;
	@Autowired
	private ICouponService couponService;
	@Autowired
	private IWaterMarkService waterMarkService;

	/**
	 * Store列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "店铺列表", value = "/admin/store_list.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_list.htm")
	public ModelAndView store_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String store_status, String store_name,
			String grade_id) {
		ModelAndView mv = new JModelAndView("admin/blue/store_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		StoreQueryObject qo = new StoreQueryObject(currentPage, mv, orderBy,
				orderType);
		if (store_status != null && !store_status.equals("")) {
			qo.addQuery(
					"obj.store_status",
					new SysMap("store_status", CommUtil.null2Int(store_status)),
					"=");
			mv.addObject("store_status", store_status);
		}
		if (store_name != null && !store_name.equals("")) {
			qo.addQuery("obj.store_name", new SysMap("store_name", "%"
					+ CommUtil.null2String(store_name) + "%"), "like");
			mv.addObject("store_name", store_name);
		}
		if (grade_id != null && !grade_id.equals("")) {
			qo.addQuery("obj.grade.id",
					new SysMap("grade_id", CommUtil.null2Long(grade_id)), "=");
			mv.addObject("grade_id", grade_id);
		}
		IPageList pList = this.storeService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		List<StoreGrade> grades = this.storeGradeService.query(
				"select obj from StoreGrade obj order by obj.sequence asc",
				null, -1, -1);
		mv.addObject("grades", grades);
		return mv;
	}

	/**
	 * store添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "店铺添加1", value = "/admin/store_add.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_add.htm")
	public ModelAndView store_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/store_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "店铺添加2", value = "/admin/store_new.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_new.htm")
	public ModelAndView store_new(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String userName,
			String list_url, String add_url) {
		ModelAndView mv = new JModelAndView("admin/blue/store_new.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjByProperty(null, "userName",
				userName);
		Store store = null;
		if (user != null)
			store = this.storeService.getObjByProperty(null, "user.id",
					user.getId());
		if (user == null) {
			mv = new JModelAndView("admin/blue/tip.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_tip", "不存在该用户");
			mv.addObject("list_url", list_url);
		} else {
			if (store == null) {
				List<Area> areas = this.areaService.query(
						"select obj from Area obj where obj.parent.id is null",
						null, -1, -1);
				List<StoreGrade> grades = this.storeGradeService
						.query("select obj from StoreGrade obj order by obj.sequence asc",
								null, -1, -1);
				mv.addObject("grades", grades);
				mv.addObject("areas", areas);
				mv.addObject("currentPage", currentPage);
				mv.addObject("user", user);
				List<GoodsClass> gcs = this.goodsclassService
						.query("select obj from GoodsClass obj where obj.parent.id is null ",
								null, -1, -1);
				mv.addObject("goodsClass", gcs);
			} else {
				mv = new JModelAndView("admin/blue/tip.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_tip", "该用户已经开通店铺");
				mv.addObject("list_url", add_url);
			}
		}
		return mv;
	}

	/**
	 * 店铺经营类目Ajax加载
	 * 
	 * @param request
	 * @param response
	 * @param cid
	 * @return
	 */
	@SecurityMapping(title = "店铺添加2", value = "/admin/store_gc_ajax.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_gc_ajax.htm")
	public ModelAndView store_goodsclass_dialog(HttpServletRequest request,
			HttpServletResponse response, String cid) {
		ModelAndView mv = new JModelAndView("admin/blue/store_gc_ajax.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (cid != null && !cid.equals("")) {
			GoodsClass goodsClass = this.goodsclassService.getObjById(CommUtil
					.null2Long(cid));
			Set<GoodsClass> gcs = goodsClass.getChilds();
			mv.addObject("gcs", gcs);
		}
		return mv;
	}

	/**
	 * store公司信息查看
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "店铺公司信息查看", value = "/admin/store_company.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_company.htm")
	public ModelAndView store_company(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/store_company.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Store store = this.storeService.getObjById(CommUtil.null2Long(id));
		mv.addObject("store", store);
		Area area1 = store.getLicense_area();
		mv.addObject("license_area_info",
				this.areaManageTools.generic_area_info(area1));
		Area area2 = store.getLicense_c_area();
		mv.addObject("license_c_area_info",
				this.areaManageTools.generic_area_info(area2));
		Area area3 = store.getBank_area();
		mv.addObject("bank_area_info",
				this.areaManageTools.generic_area_info(area3));
		return mv;
	}

	/**
	 * store编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "店铺编辑", value = "/admin/store_edit.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_edit.htm")
	public ModelAndView store_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/store_edit.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			Store store = this.storeService.getObjById(Long.parseLong(id));
			List<Area> areas = this.areaService.query(
					"select obj from Area obj where obj.parent.id is null",
					null, -1, -1);
			mv.addObject("areas", areas);
			mv.addObject("obj", store);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
			mv.addObject("goodsClass_main",
					this.goodsclassService.getObjById(store.getGc_main_id()));
			mv.addObject("goodsClass_detail", this.storeTools
					.query_store_DetailGc(store.getGc_detail_info()));
			if (store.getArea() != null) {
				String info = this.areaManageTools.generic_area_info(store
						.getArea());
				mv.addObject("area_info", info);
			}
		}
		return mv;
	}

	/**
	 * store保存管理
	 * 
	 * @param id
	 * @param gc_main_id
	 *            :主营类目id
	 * @param gc_detail_ids
	 *            ：详细类目id
	 * @param id
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "店铺保存", value = "/admin/store_save.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_save.htm")
	public ModelAndView store_save(HttpServletRequest request,
			HttpServletResponse response, String id, String store_status,
			String currentPage, String cmd, String list_url, String add_url,
			String user_id, String grade_id, String area_id, String validity,
			String gc_main_id_clone, String gc_detail_ids, String gc_detail_info)
			throws Exception {
		WebForm wf = new WebForm();
		Store store = null;
		if (id.equals("")) {
			store = wf.toPo(request, Store.class);
			store.setAddTime(new Date());
		} else {
			Store obj = this.storeService.getObjById(Long.parseLong(id));
			store = (Store) wf.toPo(request, obj);
		}
		if (store_status != null && !store_status.equals("")) {
			if (store_status.equals("5") || store_status.equals("10")) {// 入驻审核中
				store.setStore_status(CommUtil.null2Int(store_status));
			} else if (store_status.equals("6") || store_status.equals("11")) {// 入驻审核失败
				store.setStore_status(CommUtil.null2Int(store_status));
				this.send_site_msg(request,
						"msg_toseller_store_update_refuse_notify", store);
			} else if (store_status.equals("15")) {// 入驻成功，给用户赋予卖家权限
				if (user_id != null && !user_id.equals("")) {// 平台为用户新增店铺
					User user = this.userService.getObjById(CommUtil
							.null2Long(user_id));
					store.setUser(user);
					Area area = this.areaService.getObjById(CommUtil
							.null2Long(area_id));
					store.setArea(area);
					StoreGrade grade = this.storeGradeService
							.getObjById(CommUtil.null2Long(grade_id));
					store.setGrade(grade);
					store.setGc_main_id(CommUtil.null2Long(gc_main_id_clone));
					store.setValidity(CommUtil.formatDate(validity));
					// if (gc_detail_ids != null && !gc_detail_ids.equals("")) {
					// String[] gc_detail = gc_detail_ids.split(",");
					// Map map = new HashMap();
					// for (int i = 0; i < gc_detail.length; i++) {
					// map.put("id" + i, gc_detail[i]);
					// }
					// System.out.println(Json.toJson(map,
					// JsonFormat.compact()));
					// store.setGc_detail_info(Json.toJson(map,
					// JsonFormat.compact()));
					// }
					store.setGc_detail_info(gc_detail_info);
					this.storeService.save(store);
					if (store.getPoint() == null) {
						StorePoint sp = new StorePoint();
						sp.setAddTime(new Date());
						sp.setStore(store);
						sp.setStore_evaluate(BigDecimal.valueOf(0));
						sp.setDescription_evaluate(BigDecimal.valueOf(0));
						sp.setService_evaluate(BigDecimal.valueOf(0));
						sp.setShip_evaluate(BigDecimal.valueOf(0));
						this.storePointService.save(sp);
					}
				}
				String store_user_id = CommUtil.null2String(store.getUser()
						.getId());
				if (store_user_id != null && !store_user_id.equals("")) {
					User store_user = this.userService.getObjById(Long
							.parseLong(store_user_id));
					store_user.setStore(store);
					if (!store_user.getUserRole().equalsIgnoreCase("admin")) {// 非admin该表用户为seller角色
						store_user.setUserRole("SELLER");
					} else {
						store_user.setUserRole("ADMIN_SELLER");
					}
					// 给用户赋予卖家权限
					Map params = new HashMap();
					params.put("type", "SELLER");
					List<Role> roles = this.roleService.query(
							"select obj from Role obj where obj.type=:type",
							params, -1, -1);
					for (Role role : roles) {
						store_user.getRoles().add(role);
					}
					store_user.getRoles().addAll(roles);
					this.userService.update(store_user);
					if (store.getStore_start_time() == null) {// 开店时间为空，意味着入驻审核通过，成功开店
						store.setStore_start_time(new Date());
						this.send_site_msg(request,
								"msg_toseller_store_update_allow_notify", store);
					}
				}
				store.setStore_status(CommUtil.null2Int(store_status));
			} else if (store_status.equals("20")) {// 关闭违规店铺发送站内信提醒
				store.setStore_status(CommUtil.null2Int(store_status));
				if (!id.equals("") && store.getStore_status() == 20) {
					this.send_site_msg(request,
							"msg_toseller_store_closed_notify", store);
				}
			}
		}
		if (store.isStore_recommend()) {
			store.setStore_recommend_time(new Date());
		} else
			store.setStore_recommend_time(null);
		this.storeService.update(store);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存店铺成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	private void send_site_msg(HttpServletRequest request, String mark,
			Store store) throws Exception {
		Template template = this.templateService.getObjByProperty(null, "mark",
				mark);
		if (template != null && template.isOpen()) {
			if (store.getUser() != null) {
				ExpressionParser exp = new SpelExpressionParser();
				EvaluationContext context = new StandardEvaluationContext();
				context.setVariable("reason", store.getViolation_reseaon());
				context.setVariable("user", store.getUser());
				context.setVariable("store", store);
				context.setVariable("config", this.configService.getSysConfig());
				context.setVariable("send_time",
						CommUtil.formatLongDate(new Date()));
				Expression ex = exp.parseExpression(template.getContent(),
						new SpelTemplate());
				String content = ex.getValue(context, String.class);
				Map params = new HashMap();
				params.put("userName", "admin");
				params.put("userRole", "ADMIN");
				List<User> fromUsers = this.userService
						.query("select obj from User obj where obj.userName=:userName and obj.userRole=:userRole",
								params, -0, 1);
				if (fromUsers.size() > 0) {
					Message msg = new Message();
					msg.setAddTime(new Date());
					msg.setContent(content);
					msg.setFromUser(fromUsers.get(0));
					msg.setTitle(template.getTitle());
					msg.setToUser(store.getUser());
					msg.setType(0);
					this.messageService.save(msg);
				}
			}
		}
	}

	@SecurityMapping(title = "店铺删除", value = "/admin/store_del.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_del.htm")
	public String store_del(HttpServletRequest request, String mulitId)
			throws Exception {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Store store = this.storeService.getObjById(CommUtil
						.null2Long(id));
				Map params = new HashMap();
				if (store.getUser() != null) {
					store.getUser().setStore(null);
					User user = store.getUser();
					if (user != null) {
						Set<Role> roles = user.getRoles();
						Set<Role> new_roles = new HashSet<Role>();
						for (Role role : roles) {
							if (!role.getType().equals("SELLER")) {
								new_roles.add(role);
							}
						}
						user.getRoles().clear();// 清除所有权限，重新添加不含商家的权限信息
						user.getRoles().addAll(new_roles);//
						user.setStore_apply_step(0);
						this.userService.update(user);
						for (User u : user.getChilds()) {// 清除子账户所有权限信息
							roles = u.getRoles();
							Set<Role> new_roles2 = new HashSet<Role>();
							for (Role role : roles) {
								if (!role.getType().equals("SELLER")) {
									new_roles2.add(role);
								}
							}
							u.getRoles().clear();// 清除所有权限，重新添加不含商家的权限信息
							u.getRoles().addAll(new_roles2);//
							u.setStore_apply_step(0);
							this.userService.update(u);
						}
					}
					params.clear();// 删除商家优惠券
					params.put("store_id", store.getId());
					List<Coupon> coupons = this.couponService
							.query("select obj from Coupon obj where obj.store.id=:store_id",
									params, -1, -1);
					for (Coupon coupon : coupons) {
						for (CouponInfo couponInfo : coupon.getCouponinfos()) {
							this.couponInfoService.delete(couponInfo.getId());
						}
						this.couponService.delete(coupon.getId());
					}

					for (GoldRecord gr : user.getGold_record()) {// 商家充值记录
						this.grService.delete(gr.getId());
					}
					params.clear();
					params.put("uid", user.getId());
					List<GoldLog> gls = this.glService
							.query("select obj from GoldLog obj where obj.gl_user.id=:uid",
									params, -1, -1);
					for (GoldLog gl : gls) {
						this.glService.delete(gl.getId());
					}
					for (GoldRecord gr : user.getGold_record()) {
						this.grService.delete(gr.getId());
					}
					for (GroupLifeGoods glg : user.getGrouplifegoods()) {// 商家发布的生活购
						for (GroupInfo gi : glg.getGroupInfos()) {
							this.groupinfoService.delete(gi.getId());
						}
						glg.getGroupInfos().removeAll(glg.getGroupInfos());
						this.grouplifegoodsService.delete(CommUtil
								.null2Long(glg.getId()));
					}
					for (PayoffLog log : user.getPaylogs()) {// 商家结算日志
						this.paylogService.delete(log.getId());
					}
					for (Album album : user.getAlbums()) {// 商家相册删除
						album.setAlbum_cover(null);
						this.albumService.update(album);
						params.clear();
						params.put("album_id", album.getId());
						List<Accessory> accs = this.accessoryService
								.query("select obj from Accessory obj where obj.album.id=:album_id",
										params, -1, -1);
						for (Accessory acc : accs) {
							CommUtil.del_acc(request, acc);
							this.accessoryService.delete(acc.getId());
						}
						this.albumService.delete(album.getId());
					}
				}
				for (Goods goods : store.getGoods_list()) {// 店铺内的商品
					goods.setGoods_main_photo(null);
					goods.setGoods_brand(null);
					this.goodsService.update(goods);
					goods.getGoods_photos().clear();
					goods.getGoods_specs().clear();
					goods.getGoods_ugcs().clear();
				}
				for (Goods goods : store.getGoods_list()) {// 删除店铺内的商品
					for (GoodsCart gc : goods.getCarts()) {
						this.goodsCartService.delete(gc.getId());
					}
					List<Evaluate> evaluates = goods.getEvaluates();
					for (Evaluate e : evaluates) {
						this.evaluateService.delete(e.getId());
					}
					for (ComplaintGoods cg : goods.getCgs()) {
						this.complaintGoodsService.delete(cg.getId());
					}
					//删除相应商品收藏
					params.clear();
					params.put("gid", goods.getId());
					List<Favorite> favs = this.favoriteService
							.query("select obj from Favorite obj where obj.goods_id=:gid",
									params, -1, -1);
					for(Favorite obj:favs){
						this.favoriteService.delete(obj.getId());
					}
					goods.getCarts().removeAll(goods.getCarts());// 移除对象中的购物车
					goods.getEvaluates().removeAll(goods.getEvaluates());
					goods.getCgs().removeAll(goods.getCgs());
					params.clear();// 直通车商品记录
					params.put("gid", goods.getId());
					List<ZTCGoldLog> ztcgls = this.ztcglService
							.query("select obj from ZTCGoldLog obj where obj.zgl_goods_id=:gid",
									params, -1, -1);
					for (ZTCGoldLog ztc : ztcgls) {
						this.ztcglService.delete(ztc.getId());
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
				store.getGoods_list().removeAll(store.getGoods_list());

				for (GoodsSpecification spec : store.getSpecs()) {// 店铺规格
					for (GoodsSpecProperty pro : spec.getProperties()) {
						this.specpropertyService.delete(pro.getId());
					}
					spec.getProperties().removeAll(spec.getProperties());
				}
				String path = request.getSession().getServletContext()
						.getRealPath("/")
						+ this.configService.getSysConfig().getUploadFilePath()
						+ File.separator
						+ "store"
						+ File.separator
						+ store.getId();
				CommUtil.deleteFolder(path);
				// 删除店铺之前删除店铺的水印
				params.clear();
				params.put("sid", CommUtil.null2Long(id));
				List<WaterMark> wm = this.waterMarkService
						.query("select obj from WaterMark obj where obj.store.id=:sid",
								params, 0, 1);
				if (wm.size() > 0) {
					this.waterMarkService.delete(wm.get(0).getId());
				}
				// 删除店铺收藏
				params.clear();
				params.put("store_id", CommUtil.null2Long(id));
				List<Favorite> favs = this.favoriteService
						.query("select obj from Favorite obj where obj.store_id=:store_id",
								params, -1, -1);
				for (Favorite obj : favs) {
					this.favoriteService.delete(obj.getId());
				}
				this.storeService.delete(CommUtil.null2Long(id));
				this.send_site_msg(request, "msg_toseller_store_delete_notify",
						store);
			}
		}
		return "redirect:store_list.htm";
	}

	@SecurityMapping(title = "店铺AJAX更新", value = "/admin/store_ajax.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/store_ajax.htm")
	public void store_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Store obj = this.storeService.getObjById(Long.parseLong(id));
		Field[] fields = Store.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(obj);
		Object val = null;
		for (Field field : fields) {
			// System.out.println(field.getName());
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
			} else {
				obj.setStore_recommend_time(null);
			}
		}
		this.storeService.update(obj);
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

	@SecurityMapping(title = "入驻管理", value = "/admin/store_base.htm*", rtype = "admin", rname = "入驻管理", rcode = "admin_store_base", rgroup = "店铺")
	@RequestMapping("/admin/store_base.htm")
	public ModelAndView store_base(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/store_base_set.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@SecurityMapping(title = "卖家信用保存", value = "/admin/store_set_save.htm*", rtype = "admin", rname = "入驻管理", rcode = "admin_store_base", rgroup = "店铺")
	@RequestMapping("/admin/store_set_save.htm")
	public ModelAndView store_set_save(HttpServletRequest request,
			HttpServletResponse response, String id, String list_url,
			String store_allow) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig sc = this.configService.getSysConfig();
		sc.setStore_allow(CommUtil.null2Boolean(store_allow));
		if (id.equals("")) {
			this.configService.save(sc);
		} else
			this.configService.update(sc);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存店铺设置成功");
		return mv;
	}

	@SecurityMapping(title = "开店申请Ajax更新", value = "/admin/store_base_ajax.htm*", rtype = "admin", rname = "入驻管理", rcode = "admin_store_base", rgroup = "店铺")
	@RequestMapping("/admin/store_base_ajax.htm")
	public void integral_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String fieldName)
			throws ClassNotFoundException {
		SysConfig sc = this.configService.getSysConfig();
		Field[] fields = SysConfig.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(sc);
		Object val = null;
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				Class clz = Class.forName("java.lang.Boolean");
				val = !CommUtil.null2Boolean(wrapper
						.getPropertyValue(fieldName));
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		this.configService.update(sc);
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

	// 店铺模板管理
	// @SecurityMapping(title = "店铺模板", value = "/admin/store_template.htm*",
	// rtype = "admin", rname = "店铺模板", rcode = "admin_store_template", rgroup =
	// "店铺")
	// @RequestMapping("/admin/store_template.htm")
	public ModelAndView store_template(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/store_template.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("path", request.getSession().getServletContext()
				.getRealPath("/"));
		mv.addObject("separator", File.separator);
		return mv;
	}

	@SecurityMapping(title = "等级限制时可选的类目", value = "/admin/sg_limit_gc.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/sg_limit_gc.htm")
	public void storeGrade_limit_goodsClass(HttpServletRequest request,
			HttpServletResponse response, String storeGrade_id,
			String goodsClass_id) {
		String jsonList = "";
		StoreGrade storeGrade = this.storeGradeService.getObjById(CommUtil
				.null2Long(storeGrade_id));
		if (storeGrade != null && storeGrade.getMain_limit() != 0) {
			GoodsClass goodsClass = this.goodsclassService.getObjById(CommUtil
					.null2Long(goodsClass_id));
			if (goodsClass != null) {
				List<Map<String, String>> gcList = new ArrayList<Map<String, String>>();
				for (GoodsClass gc : goodsClass.getChilds()) {
					Map map = new HashMap();
					map.put("gc_id", gc.getId());
					map.put("gc_name", gc.getClassName());
					gcList.add(map);
				}
				jsonList = Json.toJson(gcList, JsonFormat.compact());
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(jsonList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "新增详细经营类目", value = "/admin/add_gc_detail.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/add_gc_detail.htm")
	public ModelAndView addStore_GoodsClass_detail(HttpServletRequest request,
			HttpServletResponse response, String did, String gc_detail_info) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/store_detailgc_ajax.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsClass gc = this.goodsclassService.getObjById(CommUtil
				.null2Long(did));
		List<Map> list = null;// 用于转换成店铺中的详细经营类目json
		if (gc != null) {
			GoodsClass parent = gc.getParent();
			if (gc_detail_info != null && !gc_detail_info.equals("")) {
				if (storeTools.query_MainGc_Map(parent.getId().toString(),
						gc_detail_info) == null) {// 不在一个大分类下
					list = Json.fromJson(ArrayList.class, gc_detail_info);
					List<Integer> gc_list = new ArrayList();
					Map map = new HashMap();
					gc_list.add(CommUtil.null2Int(did));
					map.put("gc_list", gc_list);
					map.put("m_id", parent.getId());
					list.add(map);
					String listJson = Json.toJson(list, JsonFormat.compact());
					mv.addObject("gc_detail_info", listJson);
					mv.addObject("gcs",
							storeTools.query_store_DetailGc(listJson));
				} else {// 在一个大分类下
					List<Map> oldList = Json.fromJson(ArrayList.class,
							gc_detail_info);
					list = new ArrayList<Map>();
					for (Map map : oldList) {
						if (CommUtil.null2Long(map.get("m_id")).equals(
								parent.getId())) {
							List<Integer> gc_list = (List<Integer>) map
									.get("gc_list");
							gc_list.add(CommUtil.null2Int(did));
							Map map2 = new HashMap();
							HashSet set = new HashSet(gc_list);
							gc_list = new ArrayList<Integer>(set);
							System.out.println(gc_list);
							map2.put("gc_list", gc_list);
							map2.put("m_id", parent.getId());
							list.add(map2);
						} else {
							list.add(map);
						}
					}
					String listJson = Json.toJson(list, JsonFormat.compact());
					mv.addObject("gc_detail_info", listJson);
					mv.addObject("gcs",
							storeTools.query_store_DetailGc(listJson));
				}
				;
			} else {
				list = new ArrayList<Map>();
				Map map = new HashMap();
				List<Integer> gc_list = new ArrayList();
				gc_list.add(CommUtil.null2Int(did));
				map.put("gc_list", gc_list);
				map.put("m_id", parent.getId());
				list.add(map);
				String listJson = Json.toJson(list, JsonFormat.compact());
				mv.addObject("gc_detail_info", listJson);
				mv.addObject("gcs", storeTools.query_store_DetailGc(listJson));
			}
		}
		return mv;
	}

	@SecurityMapping(title = "删除详细经营类目", value = "/admin/del_gc_detail.htm*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping("/admin/del_gc_detail.htm")
	public ModelAndView delStore_GoodsClass_detail(HttpServletRequest request,
			HttpServletResponse response, String did, String gc_detail_info) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/store_detailgc_ajax.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsClass gc = this.goodsclassService.getObjById(CommUtil
				.null2Long(did));
		if (gc_detail_info != null && !gc_detail_info.equals("") && gc != null) {
			GoodsClass parent = gc.getParent();
			List<Map> oldList = Json.fromJson(ArrayList.class, gc_detail_info);
			List<Map> list = new ArrayList<Map>();
			for (Map oldMap : oldList) {
				if (!CommUtil.null2Long(oldMap.get("m_id")).equals(
						parent.getId())) {
					list.add(oldMap);
				} else {
					List<Integer> gc_list = (List<Integer>) oldMap
							.get("gc_list");
					for (Integer integer : gc_list) {
						if (integer.equals(CommUtil.null2Int(did))) {
							gc_list.remove(integer);
							break;
						}
					}
					if (gc_list.size() > 0) {
						Map map = new HashMap();
						map.put("gc_list", gc_list);
						map.put("m_id", parent.getId());
						list.add(oldMap);
					}
				}
			}
			if (list.size() > 0) {
				String listJson = Json.toJson(list, JsonFormat.compact());
				mv.addObject("gc_detail_info", listJson);
				mv.addObject("gcs", storeTools.query_store_DetailGc(listJson));
			}
		}
		return mv;
	}

}