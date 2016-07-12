package com.iskyshop.manage.admin.action;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.SecurityManager;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.Md5Encrypt;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.core.tools.database.DatabaseTools;
import com.iskyshop.foundation.domain.Res;
import com.iskyshop.foundation.domain.Role;
import com.iskyshop.foundation.domain.RoleGroup;
import com.iskyshop.foundation.domain.SysLog;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.SysLogQueryObject;
import com.iskyshop.foundation.domain.query.UserQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAdvertService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IResService;
import com.iskyshop.foundation.service.IRoleGroupService;
import com.iskyshop.foundation.service.IRoleService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.ISysLogService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.buyer.action.AccountBuyerAction;
import com.iskyshop.manage.buyer.action.AddressBuyerAction;
import com.iskyshop.manage.buyer.action.BaseBuyerAction;
import com.iskyshop.manage.buyer.action.ComplaintBuyerAction;
import com.iskyshop.manage.buyer.action.ConsultBuyerAction;
import com.iskyshop.manage.buyer.action.CouponBuyerAction;
import com.iskyshop.manage.buyer.action.EvaluateBuyerAction;
import com.iskyshop.manage.buyer.action.FavoriteBuyerAction;
import com.iskyshop.manage.buyer.action.FootPointBuyerAction;
import com.iskyshop.manage.buyer.action.FreeBuyerAction;
import com.iskyshop.manage.buyer.action.GroupBuyerAction;
import com.iskyshop.manage.buyer.action.IntegralOrderBuyerAction;
import com.iskyshop.manage.buyer.action.MessageBuyerAction;
import com.iskyshop.manage.buyer.action.OrderBuyerAction;
import com.iskyshop.manage.buyer.action.PredepositBuyerAction;
import com.iskyshop.manage.buyer.action.PredepositCashBuyerAction;
import com.iskyshop.manage.buyer.action.RechargeCardBuyerAction;
import com.iskyshop.manage.delivery.action.DeliveryApplyAction;
import com.iskyshop.manage.delivery.action.DeliveryIndexAction;
import com.iskyshop.manage.seller.action.ActivitySellerAction;
import com.iskyshop.manage.seller.action.AdvertSellerAction;
import com.iskyshop.manage.seller.action.AlbumSellerAction;
import com.iskyshop.manage.seller.action.BaseSellerAction;
import com.iskyshop.manage.seller.action.BuyGiftSellerAction;
import com.iskyshop.manage.seller.action.CombinSellerAction;
import com.iskyshop.manage.seller.action.ComplaintSellerAction;
import com.iskyshop.manage.seller.action.ConsultSellerAction;
import com.iskyshop.manage.seller.action.CouponSellerAction;
import com.iskyshop.manage.seller.action.EnoughReduceSellerAction;
import com.iskyshop.manage.seller.action.EvaluateSellerAction;
import com.iskyshop.manage.seller.action.ExpressCompanyCommonSellerAction;
import com.iskyshop.manage.seller.action.FreeGoodsSellerAction;
import com.iskyshop.manage.seller.action.GoldSellerAction;
import com.iskyshop.manage.seller.action.GoodsBrandSellerAction;
import com.iskyshop.manage.seller.action.GoodsClassSellerAction;
import com.iskyshop.manage.seller.action.GoodsFormatSellerAction;
import com.iskyshop.manage.seller.action.GoodsSellerAction;
import com.iskyshop.manage.seller.action.GoodsSpecSellerAction;
import com.iskyshop.manage.seller.action.GoodsStatSellerAction;
import com.iskyshop.manage.seller.action.GroupSellerAction;
import com.iskyshop.manage.seller.action.OrderSellerAction;
import com.iskyshop.manage.seller.action.PayoffLogsellerAction;
import com.iskyshop.manage.seller.action.ReturnSellerAction;
import com.iskyshop.manage.seller.action.SellerCrmManageAction;
import com.iskyshop.manage.seller.action.ShipAddressSellerAction;
import com.iskyshop.manage.seller.action.SmsSellerAction;
import com.iskyshop.manage.seller.action.StoreAdjustApplyAction;
import com.iskyshop.manage.seller.action.StoreDecorateSellerAction;
import com.iskyshop.manage.seller.action.StoreNavSellerAction;
import com.iskyshop.manage.seller.action.StoreNoticeAction;
import com.iskyshop.manage.seller.action.StoreSellerAction;
import com.iskyshop.manage.seller.action.StoreStatSellerAction;
import com.iskyshop.manage.seller.action.SubAccountSellerAction;
import com.iskyshop.manage.seller.action.TaobaoSellerAction;
import com.iskyshop.manage.seller.action.TransportSellerAction;
import com.iskyshop.manage.seller.action.WaterMarkSellerAction;
import com.iskyshop.manage.seller.action.ZtcSellerAction;
import com.iskyshop.module.app.manage.admin.action.AppManageAction;
import com.iskyshop.module.chatting.manage.admin.action.PlatChattingManageAction;
import com.iskyshop.module.chatting.manage.seller.action.StoreChattingViewAction;
import com.iskyshop.module.circle.manage.admin.action.CircleClassManageAction;
import com.iskyshop.module.circle.manage.admin.action.CircleManageAction;
import com.iskyshop.module.circle.view.action.CircleCreateViewAction;
import com.iskyshop.module.circle.view.action.CircleViewAction;
import com.iskyshop.module.circle.view.action.InvitationViewAction;
import com.iskyshop.module.cms.manage.admin.action.InformationClassManageAction;
import com.iskyshop.module.cms.manage.admin.action.InformationManageAction;
import com.iskyshop.module.cms.manage.admin.action.InformationReplyManageAction;
import com.iskyshop.module.cms.manage.seller.action.InformationSellerAction;
import com.iskyshop.module.sns.manage.admin.action.SnsManageAction;
import com.iskyshop.module.sns.manage.buyer.action.SnsBuyerAction;
import com.iskyshop.module.sns.manage.buyer.action.SnsBuyerDynamicAction;
import com.iskyshop.module.sns.view.action.SnsOtherUserViewAction;
import com.iskyshop.module.sns.view.action.SnsUserShareAction;
import com.iskyshop.module.weixin.manage.admin.action.WeixinManageAction;
import com.iskyshop.module.weixin.manage.buyer.action.WeixinUserAddressAction;
import com.iskyshop.module.weixin.manage.buyer.action.WeixinUserCenterAction;
import com.iskyshop.module.weixin.manage.buyer.action.WeixinUserComplaintAction;
import com.iskyshop.module.weixin.manage.buyer.action.WeixinUserFreeAction;
import com.iskyshop.module.weixin.manage.buyer.action.WeixinUserGroupAction;
import com.iskyshop.module.weixin.manage.buyer.action.WeixinUserGroupInfoAction;
import com.iskyshop.module.weixin.manage.buyer.action.WeixinUserIntegralAction;
import com.iskyshop.module.weixin.manage.buyer.action.WeixinUserOrderAction;
import com.iskyshop.module.weixin.view.action.WeixinCartViewAction;
import com.iskyshop.module.weixin.view.action.WeixinFreeGoodsViewAction;
import com.iskyshop.module.weixin.view.action.WeixinGroupViewAction;
import com.iskyshop.module.weixin.view.action.WeixinIntegralViewAction;
import com.iskyshop.view.web.action.CartViewAction;
import com.iskyshop.view.web.action.FreeViewAction;
import com.iskyshop.view.web.action.IntegralViewAction;
import com.iskyshop.view.web.action.RechargeAction;
import com.iskyshop.view.web.action.SellerApplyAction;

/**
 * 
 * <p>
 * Title: AdminManageAction.java
 * </p>
 * 
 * <p>
 * Description: 超级管理员管理控制器,用来添加、编辑管理员信息，包括给管理员分配权限，初始化系统权限等等
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
 * @date 2014-5-16
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class AdminManageAction implements ServletContextAware {
	private ServletContext servletContext;
	@Autowired
	private IUserService userService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IRoleGroupService roleGroupService;
	@Autowired
	private DatabaseTools databaseTools;
	@Autowired
	SecurityManager securityManager;
	@Autowired
	private IResService resService;
	@Autowired
	private ISysLogService syslogService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IAdvertService advertService;

	@SecurityMapping(title = "管理员列表", value = "/admin/admin_list.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
	@RequestMapping("/admin/admin_list.htm")
	public ModelAndView admin_list(String currentPage, String orderBy,
			String orderType, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/admin_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		UserQueryObject uqo = new UserQueryObject(currentPage, mv, orderBy,
				orderType);
		WebForm wf = new WebForm();
		wf.toQueryPo(request, uqo, User.class, mv);
		uqo.addQuery("obj.deleteStatus", new SysMap("deleteStatus", 0), "=");
		uqo.addQuery("obj.userRole", new SysMap("userRole", "ADMIN"), "=");
		uqo.addQuery("obj.userRole", new SysMap("userRole1",
				"ADMIN_BUYER_SELLER"), "=", "or");
		uqo.addQuery("obj.deleteStatus", new SysMap("deleteStatus1", 0), "=");
		IPageList pList = this.userService.list(uqo);
		System.out.println("uqo.getQuery():" + uqo.getQuery());
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		CommUtil.saveIPageList2ModelAndView(url + "/admin/admin_list.htm", "",
				"", pList, mv);
		mv.addObject("userRole", "ADMIN");
		return mv;
	}

	@SecurityMapping(title = "管理员添加", value = "/admin/admin_add.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
	@RequestMapping("/admin/admin_add.htm")
	public ModelAndView admin_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/admin_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("type", "ADMIN");
		List<RoleGroup> rgs = this.roleGroupService
				.query("select obj from RoleGroup obj where obj.type=:type order by obj.sequence asc",
						params, -1, -1);
		mv.addObject("rgs", rgs);
		mv.addObject("op", "admin_add");
		return mv;
	}

	@SecurityMapping(title = "管理员编辑", value = "/admin/admin_edit.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
	@RequestMapping("/admin/admin_edit.htm")
	public ModelAndView admin_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String op) {
		ModelAndView mv = new JModelAndView("admin/blue/admin_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("type", "ADMIN");
		List<RoleGroup> rgs = this.roleGroupService
				.query("select obj from RoleGroup obj where obj.type=:type order by obj.sequence asc",
						params, -1, -1);
		if (id != null) {
			if (!id.equals("")) {
				User user = this.userService.getObjById(Long.parseLong(id));
				mv.addObject("obj", user);
			}
		}
		mv.addObject("rgs", rgs);
		mv.addObject("op", op);
		return mv;
	}

	@SecurityMapping(title = "管理员保存", value = "/admin/admin_save.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
	@RequestMapping("/admin/admin_save.htm")
	public ModelAndView admin_save(HttpServletRequest request,
			HttpServletResponse response, String id, String role_ids,
			String list_url, String add_url, String userName, String password,
			String new_password) {
		WebForm wf = new WebForm();
		User user = null;
		if (id.equals("")) {
			user = wf.toPo(request, User.class);
			user.setAddTime(new Date());
			if (userName != null && !userName.equals("")) {
				user.setUserName(userName);
			}
			if (CommUtil.null2String(password).equals("")) {
				user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
			} else {
				user.setPassword(Md5Encrypt.md5(password).toLowerCase());
			}
		} else {
			User u = this.userService.getObjById(CommUtil.null2Long(id));
			user = (User) wf.toPo(request, u);
			if (!CommUtil.null2String(new_password).equals("")) {
				user.setPassword(Md5Encrypt.md5(new_password).toLowerCase());
			}
		}
		
		Boolean ret = (!id.equals(""))&&CommUtil.null2String(new_password).equals("");
		if (id.equals("")||ret) {
			user.getRoles().clear();
			if (user.getUserRole().equalsIgnoreCase("ADMIN")) {
				Map params = new HashMap();
				params.put("display", false);
				params.put("type", "ADMIN");
				params.put("type1", "BUYER");
				List<Role> roles = this.roleService
						.query("select obj from Role obj where (obj.display=:display and obj.type=:type) or obj.type=:type1",
								params, -1, -1);
				user.getRoles().addAll(roles);
			}
			String[] rids = role_ids.split(",");
			for (String rid : rids) {
				if (!rid.equals("")) {
					Role role = this.roleService.getObjById(Long.parseLong(rid));
					user.getRoles().add(role);
				}
			}
		}
		
		if (id.equals("")) {
			this.userService.save(user);
		} else {
			this.userService.update(user);
		}

		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存管理员成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url);
		}
		return mv;
	}

	@SecurityMapping(title = "管理员删除", value = "/admin/admin_del.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
	@RequestMapping("/admin/admin_del.htm")
	public String admin_del(HttpServletRequest request, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				User user = this.userService.getObjById(Long.parseLong(id));
				if (!user.getUsername().equals("admin")) {
					// 删除管理员操作日志
					Map params = new HashMap();
					params.put("user_id", CommUtil.null2Long(id));
					List<SysLog> logs = this.syslogService
							.query("select obj from SysLog obj where obj.user_id=:user_id",
									params, -1, -1);
					List list = new ArrayList();
					for (SysLog log : logs) {
						list.add(log.getId());
					}
					this.syslogService.batchDelete(list);
					// 管理员伪删除,取消所有管理员权限
					user.setDeleteStatus(-1);
					user.getRoles().clear();
					user.setUserName("_" + user.getUserName());
					user.setMobile("_" + user.getMobile());
					this.userService.update(user);
				}
			}
		}
		return "redirect:admin_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "管理员修改密码", value = "/admin/admin_pws.htm*", rtype = "admin", rname = "商城后台管理", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/admin_pws.htm")
	public ModelAndView admin_pws(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/admin_pws.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("user", this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId()));
		return mv;
	}

	@SecurityMapping(title = "管理员密码保存", value = "/admin/admin_pws_save.htm*", rtype = "admin", rname = "商城后台管理", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping("/admin/admin_pws_save.htm")
	public ModelAndView admin_pws_save(HttpServletRequest request,
			HttpServletResponse response, String old_password, String password) {
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (Md5Encrypt.md5(old_password).toLowerCase()
				.equals(user.getPassword())) {
			user.setPassword(Md5Encrypt.md5(password).toLowerCase());
			this.userService.update(user);
			mv.addObject("op_title", "修改密码成功");
		} else {
			mv = new JModelAndView("admin/blue/error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "原密码错误");
		}
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/admin_pws.htm");
		return mv;
	}

	@SecurityMapping(title = "管理员操作日志", value = "/admin/admin_log_list.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
	@RequestMapping("/admin/admin_log_list.htm")
	public ModelAndView admin_log_list(String currentPage, String orderBy,
			String orderType, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("admin/blue/admin_log_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysLogQueryObject qo = new SysLogQueryObject(currentPage, mv, orderBy,
				orderType);
		qo.setPageSize(20);
		IPageList pList = this.syslogService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "管理员操作日志", value = "/admin/admin_log_delete.htm*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
	@RequestMapping("/admin/admin_log_delete.htm")
	public String admin_log_delete(String currentPage,
			HttpServletRequest request, HttpServletResponse response,
			String mulitId, String type) {
		if (type != null && type.equals("all")) {
			List list = this.syslogService.query(
					"select obj.id from SysLog obj order by obj.addTime asc",
					null, -1, -1);
			this.syslogService.batchDelete(list);
		} else {
			String[] ids = mulitId.split(",");
			for (String id : ids) {
				if (!id.equals("")) {
					this.syslogService.delete(CommUtil.null2Long(id));
				}
			}
		}
		return "redirect:/admin/admin_log_list.htm?currentPage=" + currentPage;
	}

	@RequestMapping("/admin/init_role.htm")
	public String init_role() {
		// TODO Auto-generated method stub
		User current_user = SecurityUserHolder.getCurrentUser();
		if (current_user != null
				&& current_user.getUserRole().indexOf("ADMIN") >= 0
				&& current_user.getUsername().equals("admin")) {
//			 this.databaseTools.execute("delete from iskyshop_role_res");
//			 this.databaseTools.execute("delete from iskyshop_res");
//			 this.databaseTools.execute("delete from iskyshop_user_role");
//			 this.databaseTools.execute("delete from iskyshop_role");
//			 this.databaseTools.execute("delete from iskyshop_rolegroup");
			List<Class> clzs = new ArrayList<Class>();
			// 超级管理权限加载
			clzs.add(BaseManageAction.class);
			clzs.add(GoodsManageAction.class);
			clzs.add(GoodsCaseManageAction.class);
			clzs.add(SelfGoodsManageAction.class);
			clzs.add(StoreManageAction.class);
			clzs.add(UserManageAction.class);
			clzs.add(OrderManageAction.class);
			clzs.add(ArticleManageAction.class);
			clzs.add(OperationManageAction.class);
			clzs.add(PayoffLogManageAction.class);
			clzs.add(PaymentManageAction.class);
			clzs.add(TemplateManageAction.class);
			clzs.add(AreaManageAction.class);
			clzs.add(TransAreaManageAction.class);
			clzs.add(GoodsClassManageAction.class);
			clzs.add(GoodsBrandManageAction.class);
			clzs.add(GoodsTypeManageAction.class);
			clzs.add(SelfOrderManageAction.class);
			clzs.add(SelfTaobaoManageAction.class);
			clzs.add(SelfImageManageAction.class);
			clzs.add(SelfAlbumManageAction.class);
			clzs.add(SelfTransportManageAction.class);
			clzs.add(SelfGoodsSpecManageAction.class);
			clzs.add(SelfGroupManageAction.class);
			clzs.add(SelfActivityManageAction.class);
			clzs.add(AdminEvaManageAction.class);
			clzs.add(SelfReturnManageAction.class);
			clzs.add(StoreGradeManageAction.class);
			clzs.add(PredepositManageAction.class);
			clzs.add(PredepositLogManageAction.class);
			clzs.add(AdminManageAction.class);
			clzs.add(ConsultManageAction.class);
			clzs.add(EvaluateManageAction.class);
			clzs.add(ComplaintManageAction.class);
			clzs.add(ComplaintSubjectManageAction.class);
			clzs.add(ArticleClassManageAction.class);
			clzs.add(PartnerManageAction.class);
			clzs.add(DocumentManageAction.class);
			clzs.add(NavigationManageAction.class);
			clzs.add(GoldRecordManageAction.class);
			clzs.add(IntegralLogManageAction.class);
			clzs.add(ZtcManageAction.class);
			clzs.add(CouponManageAction.class);
			clzs.add(AdvertManageAction.class);
			clzs.add(IntegralGoodsManageAction.class);
			clzs.add(GroupAreaManageAction.class);
			clzs.add(GroupClassManageAction.class);
			clzs.add(GroupManageAction.class);
			clzs.add(GroupPriceRangeManageAction.class);
			clzs.add(GoodsFloorManageAction.class);
			clzs.add(DatabaseManageAction.class);
			clzs.add(CacheManageAction.class);
			clzs.add(LuceneManageAction.class);
			clzs.add(ActivityManageAction.class);
			clzs.add(ExpressCompanyManageAction.class);
			clzs.add(TransAreaManageAction.class);
			clzs.add(SnsManageAction.class);
			clzs.add(ImageManageAction.class);
			clzs.add(AppManageAction.class);
			clzs.add(RefundManageAction.class);
			clzs.add(StatManageAction.class);
			clzs.add(PlatChattingManageAction.class);
			clzs.add(SelfCombinManageAction.class);
			clzs.add(CombinManageAction.class);
			clzs.add(SelfEnoughReduceManageAction.class);
			clzs.add(EnoughReduceManageAction.class);
			clzs.add(RechargeCardManageAction.class);
			clzs.add(SelfBuyGiftManageAction.class);
			clzs.add(BuyGiftManageAction.class);
			clzs.add(SelfGoodsFormatManageAction.class);
			clzs.add(ShipAddressManageAction.class);
			clzs.add(SmsManageAction.class);
			clzs.add(SubjectManageAction.class);
			clzs.add(SelfExpressCommonManageAction.class);
			clzs.add(SelfGoodsStatAction.class);
			clzs.add(FreeClassManageAction.class);
			clzs.add(FreeGoodsManageAction.class);
			clzs.add(SelfFreeGoodsManageAction.class);
			clzs.add(SelfCrmManageAction.class);
			clzs.add(SelfConsultManageAction.class);
			clzs.add(DeliveryAddressManageAction.class);
			clzs.add(InformationManageAction.class);
			clzs.add(InformationClassManageAction.class);
			clzs.add(CircleManageAction.class);
			clzs.add(CircleClassManageAction.class);
			clzs.add(StoreAdjustInfoManageAction.class);
			clzs.add(WeixinManageAction.class);
			clzs.add(SelfEvaluateManageAction.class);
			clzs.add(InformationClassManageAction.class);
			clzs.add(InformationReplyManageAction.class);
			// 卖家权限加载
			clzs.add(GoodsSellerAction.class);
			clzs.add(GoodsClassSellerAction.class);
			clzs.add(GoodsFormatSellerAction.class);
			clzs.add(GoodsBrandSellerAction.class);
			clzs.add(GoodsSpecSellerAction.class);
			clzs.add(TaobaoSellerAction.class);
			clzs.add(OrderSellerAction.class);
			clzs.add(ShipAddressSellerAction.class);
			clzs.add(GroupSellerAction.class);
			clzs.add(TransportSellerAction.class);
			clzs.add(StoreSellerAction.class);
			clzs.add(StoreDecorateSellerAction.class);
			clzs.add(StoreNoticeAction.class);
			clzs.add(SubAccountSellerAction.class);
			clzs.add(StoreNavSellerAction.class);
			clzs.add(StoreAdjustApplyAction.class);
			clzs.add(ActivitySellerAction.class);
			clzs.add(CombinSellerAction.class);
			clzs.add(BuyGiftSellerAction.class);
			clzs.add(EnoughReduceSellerAction.class);
			clzs.add(ZtcSellerAction.class);
			clzs.add(CouponSellerAction.class);
			clzs.add(FreeGoodsSellerAction.class);
			clzs.add(PayoffLogsellerAction.class);
			clzs.add(ReturnSellerAction.class);
			clzs.add(ConsultSellerAction.class);
			clzs.add(ComplaintSellerAction.class);
			clzs.add(SellerCrmManageAction.class);
			clzs.add(EvaluateSellerAction.class);
			clzs.add(StoreChattingViewAction.class);
			clzs.add(StoreStatSellerAction.class);
			clzs.add(GoodsStatSellerAction.class);
			clzs.add(GoldSellerAction.class);
			clzs.add(AdvertSellerAction.class);
			clzs.add(AlbumSellerAction.class);
			clzs.add(InformationSellerAction.class);
			clzs.add(BaseSellerAction.class);
			clzs.add(ExpressCompanyCommonSellerAction.class);
			clzs.add(SmsSellerAction.class);
			clzs.add(WaterMarkSellerAction.class);

			// 买家权限加载
			clzs.add(AccountBuyerAction.class);
			clzs.add(AddressBuyerAction.class);
			clzs.add(BaseBuyerAction.class);
			clzs.add(ComplaintBuyerAction.class);
			clzs.add(ConsultBuyerAction.class);
			clzs.add(CouponBuyerAction.class);
			clzs.add(FavoriteBuyerAction.class);
			clzs.add(FootPointBuyerAction.class);
			clzs.add(FreeBuyerAction.class);
			clzs.add(GroupBuyerAction.class);
			clzs.add(IntegralOrderBuyerAction.class);
			clzs.add(MessageBuyerAction.class);
			clzs.add(OrderBuyerAction.class);
			clzs.add(PredepositBuyerAction.class);
			clzs.add(PredepositCashBuyerAction.class);
			clzs.add(IntegralViewAction.class);
			clzs.add(SellerApplyAction.class);
			clzs.add(RechargeAction.class);
			clzs.add(RechargeCardBuyerAction.class);
			clzs.add(SnsBuyerAction.class);
			clzs.add(SnsBuyerDynamicAction.class);
			clzs.add(SnsOtherUserViewAction.class);
			clzs.add(FreeViewAction.class);
			clzs.add(SnsUserShareAction.class);
			clzs.add(CircleCreateViewAction.class);
			clzs.add(CircleViewAction.class);
			clzs.add(InvitationViewAction.class);
			clzs.add(EvaluateBuyerAction.class);
			// wap买家权限
			clzs.add(WeixinUserAddressAction.class);
			clzs.add(WeixinUserCenterAction.class);
			clzs.add(WeixinUserComplaintAction.class);
			clzs.add(WeixinUserFreeAction.class);
			clzs.add(WeixinUserGroupAction.class);
			clzs.add(WeixinUserGroupInfoAction.class);
			clzs.add(WeixinUserIntegralAction.class);
			clzs.add(WeixinUserOrderAction.class);
			clzs.add(WeixinFreeGoodsViewAction.class);
			clzs.add(WeixinCartViewAction.class);
			clzs.add(WeixinGroupViewAction.class);
			clzs.add(WeixinIntegralViewAction.class);
			// 自提点申请权限加载
			clzs.add(DeliveryApplyAction.class);
			// 购物权限加载
			clzs.add(CartViewAction.class);
			// 自提后台管理权限
			clzs.add(DeliveryIndexAction.class);
			int sequence = 0;
			for (Class clz : clzs) {
				try {
					Method[] ms = clz.getMethods();
					for (Method m : ms) {
						Annotation[] annotation = m.getAnnotations();
						for (Annotation tag : annotation) {
							if (SecurityMapping.class.isAssignableFrom(tag
									.annotationType())) {
								String value = ((SecurityMapping) tag).value();
								Map params = new HashMap();
								params.put("value", value);
								List<Res> ress = this.resService
										.query("select obj from Res obj where obj.value=:value",
												params, -1, -1);
								if (ress.size() == 0) {
									Res res = new Res();
									res.setResName(((SecurityMapping) tag)
											.title());
									res.setValue(value);
									res.setType("URL");
									res.setAddTime(new Date());
									this.resService.save(res);
									String roleCode = ((SecurityMapping) tag)
											.rcode();
									if (roleCode.indexOf("ROLE_") != 0) {
										roleCode = ("ROLE_" + roleCode)
												.toUpperCase();
									}
									params.clear();
									params.put("roleCode", roleCode);
									List<Role> roles = this.roleService
											.query("select obj from Role obj where obj.roleCode=:roleCode",
													params, -1, -1);
									Role role = null;
									if (roles.size() > 0) {
										role = roles.get(0);
									}
									if (role == null) {
										role = new Role();
										role.setRoleName(((SecurityMapping) tag)
												.rname());
										role.setRoleCode(roleCode.toUpperCase());
									}
									role.getReses().add(res);
									res.getRoles().add(role);
									role.setAddTime(new Date());
									role.setDisplay(((SecurityMapping) tag)
											.display());
									role.setType(((SecurityMapping) tag)
											.rtype().toUpperCase());
									// 获取权限分组
									String groupName = ((SecurityMapping) tag)
											.rgroup();
									params.clear();
									params.put("groupName", groupName);
									params.put("type", role.getType());
									List<RoleGroup> rgs = this.roleGroupService
											.query("select obj from RoleGroup obj where obj.name=:groupName and obj.type=:type",
													params, -1, -1);
									RoleGroup rg = null;
									if (rgs.size() > 0) {
										rg = rgs.get(0);
									}
									if (rg == null) {
										rg = new RoleGroup();
										rg.setAddTime(new Date());
										rg.setName(groupName);
										rg.setSequence(sequence);
										rg.setType(role.getType());
										this.roleGroupService.save(rg);
									}
									role.setRg(rg);
									this.roleService.save(role);
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				sequence++;
			}
			// 添加默认超级管理员并赋予所有权限
			User user = this.userService.getObjByProperty(null, "userName",
					"admin");
			Map params = new HashMap();
			List<Role> roles = this.roleService.query(
					"select obj from Role obj order by obj.addTime desc", null,
					-1, -1);
			if (user == null) {
				user = new User();
				user.setUserName("admin");
				user.setUserRole("ADMIN");
				user.setPassword(Md5Encrypt.md5("123456").toLowerCase());
				for (Role role : roles) {
					if (!role.getType().equalsIgnoreCase("SELLER")) {
						user.getRoles().add(role);
					}
				}
				this.userService.save(user);
			} else {
				for (Role role : roles) {
					if (!role.getType().equals("SELLER")) {
						System.out.println(role.getRoleName() + " "
								+ role.getType() + " " + role.getRoleCode());
						user.getRoles().add(role);
					}
				}
				this.userService.update(user);
			}
			// 给其他管理员添加系统默认的权限及买家权限
			params.clear();
			params.put("display", false);
			params.put("type", "ADMIN");
			List<Role> admin_roles = this.roleService
					.query("select obj from Role obj where obj.display=:display and obj.type=:type",
							params, -1, -1);
			params.clear();
			params.put("type", "BUYER");
			List<Role> buyer_roles = this.roleService.query(
					"select obj from Role obj where obj.type=:type", params,
					-1, -1);
			params.clear();
			params.put("userRole", "ADMIN");
			params.put("userName", "admin");
			List<User> admins = this.userService
					.query("select obj from User obj where obj.userRole=:userRole and obj.userName!=:userName",
							params, -1, -1);
			for (User admin : admins) {
				admin.getRoles().addAll(admin_roles);
				admin.getRoles().addAll(buyer_roles);
				this.userService.update(admin);
			}
			// 给所有用户添加买家权限
			params.clear();
			params.put("userRole", "BUYER");
			List<User> buyers = this.userService.query(
					"select obj from User obj where obj.userRole=:userRole",
					params, -1, -1);
			for (User buyer : buyers) {
				buyer.getRoles().addAll(buyer_roles);
				this.userService.update(buyer);
			}
			// 给所有卖家添加卖家权限
			params.clear();
			params.put("type1", "BUYER");
			params.put("type2", "SELLER");
			List<Role> seller_roles = this.roleService
					.query("select obj from Role obj where (obj.type=:type1 or obj.type=:type2)",
							params, -1, -1);
			params.clear();
			params.put("userRole0", "SELLER");
			params.put("userName", "admin");
			List<User> sellers = this.userService
					.query("select obj from User obj where obj.userRole=:userRole0 and obj.userName!=:userName ",
							params, -1, -1);
			for (User seller : sellers) {
				if (seller.getStore() != null
						&& seller.getStore().getStore_status() == 15) {// 商家店铺正常营业状态下，初始化权限时候才给该商家赋予权限
					seller.getRoles().addAll(buyer_roles);
					seller.getRoles().addAll(seller_roles);
					this.userService.update(seller);
				}
			}
			// 重新加载系统权限
			Map<String, String> urlAuthorities = this.securityManager
					.loadUrlAuthorities();
			this.servletContext.setAttribute("urlAuthorities", urlAuthorities);
			return "redirect:admin_list.htm";
		} else {
			return "redirect:login.htm";
		}
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		// TODO Auto-generated method stub
		this.servletContext = servletContext;
	}
}
