package com.iskyshop.manage.buyer.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.FootPoint;
import com.iskyshop.foundation.domain.IntegralGoodsOrder;
import com.iskyshop.foundation.domain.Message;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.FavoriteQueryObject;
import com.iskyshop.foundation.domain.virtual.FootPointView;
import com.iskyshop.foundation.domain.virtual.IntegralGoodsOrderView;
import com.iskyshop.foundation.service.ICouponInfoService;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.foundation.service.IFootPointService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IIntegralGoodsOrderService;
import com.iskyshop.foundation.service.IMessageService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.OrderFormTools;
import com.iskyshop.manage.buyer.tools.FootPointTools;
import com.iskyshop.view.web.tools.IntegralViewTools;
import com.iskyshop.view.web.tools.OrderViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;

/**
 * 
 * <p>
 * Title: BaseBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 买家中心基础管理控制器
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
 * @date 2014-5-19
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class BaseBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IMessageService messageService;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private OrderViewTools orderViewTools;
	@Autowired
	private IFavoriteService favService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private ICouponInfoService couponInfoService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private IIntegralGoodsOrderService integralGoodsOrderService;
	@Autowired
	private IFootPointService footPointService;
	@Autowired
	private FootPointTools footPointTools;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IntegralViewTools integralViewTools;

	/**
	 * * 买家首页并分页查询所有动态,可以根据type参数不同进行不同的条件查询，
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param type
	 *            ：条件查询参数，type=1为查询自己，type=2为查询好友，type=3为查询相互关注
	 * @return
	 */
	@SecurityMapping(title = "买家中心", value = "/buyer/index.htm*", rtype = "buyer", rname = "买家中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/index.htm")
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response, String type) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buyer_index.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		mv.addObject("storeViewTools", storeViewTools);
		mv.addObject("orderViewTools", orderViewTools);
		if (user != null) {
			Map params = new HashMap();
			params.put("user_id", user.getId());
			params.put("status", 0);
			mv.addObject(
					"couponInfos",
					this.couponInfoService
							.query("select obj.id from CouponInfo obj where obj.user.id=:user_id and obj.status = :status",
									params, -1, -1).size());
		}
		// 查询订单信息
		int[] status = new int[] { 10, 30, 50 }; // 已提交 已发货 已完成
		String[] string_status = new String[] { "order_submit",
				"order_shipping", "order_finish" };
		Map orders_status = new LinkedHashMap();
		for (int i = 0; i < status.length; i++) {
			int size = this.orderFormService.query(
					"select obj.id from OrderForm obj where obj.order_cat!=2 and obj.user_id="
							+ user.getId().toString()
							+ " and obj.order_status =" + status[i]
							+ " order by obj.addTime desc", null, -1, -1)
					.size();
			mv.addObject("order_size_" + status[i], size);
			orders_status.put(string_status[i], size);
		}
		// 查询待付款的9个订单
		List<OrderForm> orderForms = this.orderFormService
				.query("select obj from OrderForm obj where obj.user_id=" + user.getId().toString()
						+ " and obj.order_main=1 and obj.order_status=10 and obj.order_cat!=2 order by obj.addTime desc", null, 0, 9);
		mv.addObject("orderForms", orderForms);
		// 查询9个用户关注收藏的商品
		Map params = new HashMap();
		params.put("goods_type", 0);
		params.put("user_id", user.getId());
		List<Favorite> favorite_goods = this.favService
				.query("select obj from Favorite obj where obj.user_id=:user_id and obj.type=:goods_type order by obj.addTime desc",
						params, 0, 9);
		mv.addObject("favorite_goods", favorite_goods);
		// 查询9个用户关注收藏的店铺
		params.clear();
		params.put("store_type", 1);
		params.put("user_id", user.getId());
		List<Favorite> favorite_stores = this.favService
				.query("select obj from Favorite obj where obj.user_id=:user_id and obj.type=:store_type order by obj.addTime desc",
						params, 0, 9);
		mv.addObject("favorite_stores", favorite_stores);
		mv.addObject("orders_status", orders_status);
		mv.addObject("user", user);
		mv.addObject("type", type);
		// 查询未读站内信数量
		List<Message> msgs = new ArrayList<Message>();
		params.clear();
		params.put("status", 0);
		params.put("user_id", user.getId());
		msgs = this.messageService
				.query("select count(obj.id) from Message obj where obj.status=:status and obj.toUser.id=:user_id and obj.parent.id is null order by obj.addTime desc",
						params, -1, -1);
		mv.addObject("msg_size", msgs.get(0));
		mv.addObject("orderFormTools", orderFormTools);
		mv.addObject("integralViewTools", integralViewTools);
		// 查询9个已经付款完成的积分兑换信息,
		params.clear();
		params.put("igo_user", user.getId());
		// params.put("igo_status", 20);
		List<IntegralGoodsOrder> igos = this.integralGoodsOrderService
				.query("select obj from IntegralGoodsOrder obj where obj.igo_user.id=:igo_user order by obj.addTime desc",
						params, 0, 9);
		List<IntegralGoodsOrderView> igois = new ArrayList<IntegralGoodsOrderView>();
		for (IntegralGoodsOrder igo : igos) {
			IntegralGoodsOrderView igoi = new IntegralGoodsOrderView();
			igoi.setIgo_order_id(igo.getId());
			igoi.setIgo_total_integral(igo.getIgo_total_integral());
			List<Map> maps = this.orderFormTools.query_integral_goodsinfo(igo
					.getGoods_info());
			for (Map map : maps) {
				igoi.setIgo_goods_name(CommUtil.null2String(map
						.get("ig_goods_name")));
				igoi.setIgo_goods_id(CommUtil.null2Long(map.get("id")));
				igoi.setIgo_goods_img(CommUtil.null2String(map
						.get("ig_goods_img")));
				break;
			}
			igois.add(igoi);
		}
		mv.addObject("igois", igois);
		// 查询9条浏览足迹信息
		params.clear();
		params.put("fp_user_id", user.getId());
		List<FootPoint> fps = this.footPointService
				.query("select obj from FootPoint obj where obj.fp_user_id=:fp_user_id order by obj.addTime desc",
						params, 0, 6);
		List<FootPointView> fpvs = new ArrayList<FootPointView>();
		for (FootPoint fp : fps) {
			List<FootPointView> list = this.footPointTools.generic_fpv(fp
					.getFp_goods_content());
			fpvs.addAll(list);
		}
		mv.addObject("fpvs", fpvs);
		// 计算密码安全度
		int pws_safe = 20;
		int num = CommUtil.checkInput(user.getPassword());
		if (num > 1) {
			pws_safe = pws_safe + 10;
		}
		if (num > 2) {
			pws_safe = pws_safe + 10;
		}
		if (!CommUtil.null2String(user.getEmail()).equals("")) {
			pws_safe = pws_safe + 30;
		}
		if (!CommUtil.null2String(user.getMobile()).equals("")) {
			pws_safe = pws_safe + 30;
		}
		mv.addObject("pws_safe", pws_safe);
		return mv;
	}

	@SecurityMapping(title = "买家中心导航", value = "/buyer/nav.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/nav.htm")
	public ModelAndView nav(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buyer_nav.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String op = CommUtil.null2String(request.getAttribute("op"));
		mv.addObject("op", op);
		List<Message> msgs = new ArrayList<Message>();
		if (SecurityUserHolder.getCurrentUser() != null) {
			Map map = new HashMap();
			map.put("status", 0);
			map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			msgs = this.messageService
					.query("select count(obj.id) from Message obj where obj.status=:status and obj.toUser.id=:user_id and obj.parent.id is null",
							map, -1, -1);
			mv.addObject("msg_size", msgs.get(0));
		}
		return mv;
	}

	@SecurityMapping(title = "买家中心导航", value = "/buyer/head.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/head.htm")
	public ModelAndView head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buyer_head.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	@RequestMapping("/buyer/authority.htm")
	public ModelAndView authority(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("error.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "您登录的用户角色不正确");
		mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		return mv;
	}

}
