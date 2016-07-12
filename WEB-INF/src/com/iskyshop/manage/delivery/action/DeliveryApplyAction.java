package com.iskyshop.manage.delivery.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
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
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.DeliveryAddress;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.DeliveryAddressQueryObject;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IDeliveryAddressService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.delivery.tools.DeliveryAddressTools;
import com.iskyshop.view.web.tools.GoodsClassViewTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.NavViewTools;

/**
 * 
 * <p>
 * Title: DeliveryApplyAction.java
 * </p>
 * 
 * <p>
 * Description: 会员自提点申请控制器
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
 * @author jy
 * 
 * @date 2014-11-19
 * 
 * @version iskyshop_b2b2c_2015
 */
@Controller
public class DeliveryApplyAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IDeliveryAddressService deliveryAddressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private NavViewTools navTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private DeliveryAddressTools DeliveryAddressTools;
	@Autowired
	private GoodsClassViewTools gcViewTools;

	/**
	 * 自提点申请入口
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/delivery_apply0.htm")
	public ModelAndView delivery_apply0(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("delivery/delivery_apply0.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = new HashMap();
		params.put("display", true);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
						params, 0, 8);
		mv.addObject("gcs", gcs);
		mv.addObject("navTools", navTools);
		mv.addObject("gcViewTools", gcViewTools);
		String op = CommUtil.null2String(request.getAttribute("op"));
		if ("free".equals(op)) {
			mv.addObject("mark", "free/index.htm");
		}
		this.head(request, response, mv);
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("areas", areas);
		return mv;
	}

	/**
	 * 自提点申请1
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "自提点申请第一步", value = "/delivery_apply1.htm*", rtype = "buyer", rname = "自提点申请", rcode = "delivery_apply", rgroup = "自提点申请")
	@RequestMapping("/delivery_apply1.htm")
	public ModelAndView delivery_apply1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("delivery/delivery_apply1.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		if (user.getDelivery_id() == null) {
			return mv;
		}
		DeliveryAddress da = this.deliveryAddressService.getObjById(user
				.getDelivery_id());
		mv = new JModelAndView("delivery/delivery_notice.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		this.notice(request, response, mv);
		if (da.getDa_status() == 0) {
			mv.addObject("notice", "我们正在审核您的申请单...");
			return mv;
		}
		if (da.getDa_status() == 4) {
			mv.addObject("notice", "您的申请单未通过审核...");
			mv.addObject("again", "true");
			return mv;
		}
		if(da.getDa_status()>4){
			mv.addObject("notice","您的申请单已经通过！");
			return mv;
		}
		return mv;
	}

	/**
	 * 自提点申请2
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "自提点申请第二步", value = "/delivery_apply2.htm*", rtype = "buyer", rname = "自提点申请", rcode = "delivery_apply", rgroup = "自提点申请")
	@RequestMapping("/delivery_apply2.htm")
	public ModelAndView delivery_apply2(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("delivery/delivery_apply2.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("areas", areas);
		String delivery_session = CommUtil.randomString(32);
		request.getSession(false).setAttribute("delivery_session",
				delivery_session);
		mv.addObject("delivery_session", delivery_session);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		mv.addObject("obj", this.deliveryAddressService.getObjById(user.getDelivery_id()));
		return mv;
	}

	/**
	 * 自提点申请3
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "自提点申请第三步", value = "/delivery_apply3.htm*", rtype = "buyer", rname = "自提点申请", rcode = "delivery_apply", rgroup = "自提点申请")
	@RequestMapping("/delivery_apply3.htm")
	public ModelAndView delivery_apply3(HttpServletRequest request,
			HttpServletResponse response, String id, String da_service_day,
			String area3, String delivery_session) {
		ModelAndView mv = new JModelAndView("delivery/delivery_notice.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String delivery_session1 = (String) request.getSession(false)
				.getAttribute("delivery_session");
		if (CommUtil.null2String(delivery_session1).equals(delivery_session)) {
			request.getSession(false).removeAttribute("delivery_session");
			WebForm wf = new WebForm();
			DeliveryAddress deliveryaddress = null;
			if (id == null || id.equals("")) {
				deliveryaddress = wf.toPo(request, DeliveryAddress.class);
				deliveryaddress.setAddTime(new Date());
			} else {
				DeliveryAddress obj = this.deliveryAddressService
						.getObjById(Long.parseLong(id));
				deliveryaddress = (DeliveryAddress) wf.toPo(request, obj);
			}
			deliveryaddress.setDa_area(this.areaService.getObjById(CommUtil
					.null2Long(area3)));
			deliveryaddress.setDa_service_day(da_service_day.toString());
			deliveryaddress.setDa_type(1);
			deliveryaddress.setDa_status(0);
			User user = this.userService.getObjById(SecurityUserHolder
					.getCurrentUser().getId());
			deliveryaddress.setDa_user_id(user.getId());
			deliveryaddress.setDa_user_name(user.getUserName());
			if (id == null || id.equals("")) {
				this.deliveryAddressService.save(deliveryaddress);
			} else {
				this.deliveryAddressService.update(deliveryaddress);
			}
			user.setDelivery_id(deliveryaddress.getId());
			this.userService.update(user);
			mv.addObject("notice", "您已成功提交申请，我们会尽快处理...");
		} else {
			mv.addObject("notice", "我们正在审核您的申请单...");
		}
		this.notice(request, response, mv);
		return mv;
	}

	private void head(HttpServletRequest request, HttpServletResponse response,
			ModelAndView mv) {
		String type = CommUtil.null2String(request.getAttribute("type"));
		String cart_session_id = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("cart_session_id")) {
					cart_session_id = CommUtil.null2String(cookie.getValue());
				}
			}
		}
		if (cart_session_id.equals("")) {
			cart_session_id = UUID.randomUUID().toString();
			Cookie cookie = new Cookie("cart_session_id", cart_session_id);
			cookie.setDomain(CommUtil.generic_domain(request));
		}
		List<GoodsCart> carts_list = new ArrayList<GoodsCart>();// 用户整体购物车
		List<GoodsCart> carts_cookie = new ArrayList<GoodsCart>();// 未提交的用户cookie购物车
		List<GoodsCart> carts_user = new ArrayList<GoodsCart>();// 未提交的用户user购物车
		User user = SecurityUserHolder.getCurrentUser();
		Map cart_map = new HashMap();
		if (user != null) {
			user = userService.getObjById(user.getId());
			if (!cart_session_id.equals("")) {
				cart_map.clear();
				cart_map.put("cart_session_id", cart_session_id);
				cart_map.put("cart_status", 0);
				carts_cookie = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
				// 如果用户拥有自己的店铺，删除carts_cookie购物车中自己店铺中的商品信息
				if (user.getStore() != null) {
					for (GoodsCart gc : carts_cookie) {
						if (gc.getGoods().getGoods_type() == 1) {// 该商品为商家商品
							if (gc.getGoods().getGoods_store().getId()
									.equals(user.getStore().getId())) {
								this.goodsCartService.delete(gc.getId());
							}
						}
					}
				}
				cart_map.clear();
				cart_map.put("user_id", user.getId());
				cart_map.put("cart_status", 0);
				carts_user = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
			} else {
				cart_map.clear();
				cart_map.put("user_id", user.getId());
				cart_map.put("cart_status", 0);
				carts_user = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.user.id=:user_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
			}
		} else {
			if (!cart_session_id.equals("")) {
				cart_map.clear();
				cart_map.put("cart_session_id", cart_session_id);
				cart_map.put("cart_status", 0);
				carts_cookie = this.goodsCartService
						.query("select obj from GoodsCart obj where obj.cart_session_id=:cart_session_id and obj.cart_status=:cart_status ",
								cart_map, -1, -1);
			}
		}
		// 将cookie购物车与user购物车合并，并且去重
		if (user != null) {
			for (GoodsCart cookie : carts_cookie) {
				boolean add = true;
				for (GoodsCart gc2 : carts_user) {
					if (cookie.getGoods().getId()
							.equals(gc2.getGoods().getId())) {
						if (cookie.getSpec_info().equals(gc2.getSpec_info())) {
							add = false;
							this.goodsCartService.delete(cookie.getId());
						}
					}
				}
				if (add) {// 将cookie去重并添加到cart_list中
					cookie.setCart_session_id(null);
					cookie.setUser(user);
					this.goodsCartService.update(cookie);
					carts_list.add(cookie);
				}
			}
		} else {
			for (GoodsCart gc : carts_cookie) {// 将carts_cookie添加到cart_list中
				carts_list.add(gc);
			}
		}
		for (GoodsCart gc : carts_user) {// 将carts_user添加到cart_list中
			carts_list.add(gc);
		}
		// 组合套装处理，只显示套装主购物车,套装内其他购物车不显示
		List<GoodsCart> combin_carts_list = new ArrayList<GoodsCart>();
		for (GoodsCart gc : carts_list) {
			if (gc.getCart_type() != null && gc.getCart_type().equals("combin")) {
				if (gc.getCombin_main() != 1) {
					combin_carts_list.add(gc);
				}
			}
		}
		if (combin_carts_list.size() > 0) {
			carts_list.removeAll(combin_carts_list);
		}
		mv.addObject("carts", carts_list);
		mv.addObject("type", type.equals("") ? "goods" : type);
	}

	/**
	 * 自提点申请入口，查询本城市自提点
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/query_area_delivery.htm")
	public ModelAndView query_area_delivery(HttpServletRequest request,
			HttpServletResponse response, String city_id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"delivery/query_area_delivery.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Area area = this.areaService.getObjById(CommUtil.null2Long(city_id));
		DeliveryAddressQueryObject qo = new DeliveryAddressQueryObject(
				currentPage, mv, "addTime", "desc");
		qo.addQuery("obj.da_area.parent.id",
				new SysMap("da_area_id", CommUtil.null2Long(city_id)), "=");
		qo.addQuery("obj.da_status",
				new SysMap("da_status", 10), "=");
		qo.setPageSize(10);
		IPageList pList = this.deliveryAddressService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		String url2 = CommUtil.getURL(request) + "/query_area_delivery.htm";
		mv.addObject("objs", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url2, "",
				pList.getCurrentPage(), pList.getPages()));
		mv.addObject("DeliveryAddressTools", DeliveryAddressTools);
		return mv;
	}

	private void notice(HttpServletRequest request,
			HttpServletResponse response, ModelAndView mv) {
		Map params = new HashMap();
		params.put("display", true);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
						params, 0, 8);
		mv.addObject("gcs", gcs);
		mv.addObject("navTools", navTools);
		mv.addObject("gcViewTools", gcViewTools);
		String op = CommUtil.null2String(request.getAttribute("op"));
		if ("free".equals(op)) {
			mv.addObject("mark", "free/index.htm");
		}
		this.head(request, response, mv);
	}
}
