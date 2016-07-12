package com.iskyshop.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.StoreNavigation;
import com.iskyshop.foundation.domain.StoreSlide;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.UserGoodsClass;
import com.iskyshop.foundation.domain.query.GoodsQueryObject;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IStoreNavigationService;
import com.iskyshop.foundation.service.IStorePartnerService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.IStoreSlideService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserGoodsClassService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.UserTools;
import com.iskyshop.view.web.tools.AreaViewTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;

/**
 * 
 * 
 * <p>
 * Title:StoreViewAction.java
 * </p>
 * 
 * <p>
 * Description: 前端店铺控制器
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
 * @author erikzhang、jy
 * 
 * @date 2014年4月24日
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class StoreViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IStoreSlideService storeSlideService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IUserGoodsClassService userGoodsClassService;
	@Autowired
	private IStoreNavigationService storenavigationService;
	@Autowired
	private IStorePartnerService storepartnerService;
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private AreaViewTools areaViewTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private UserTools userTools;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private IUserService userService;
	@Autowired
	private IAccessoryService accessoryService;
	@Autowired
	private IGoodsCartService goodsCartService;

	/**
	 * 店铺首页
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping("/store.htm")
	public ModelAndView store(HttpServletRequest request,
			HttpServletResponse response, String id) {
		String serverName = request.getServerName().toLowerCase();
		String secondDomain = "";
		if (this.configService.getSysConfig().isSecond_domain_open()) {
			secondDomain = serverName.substring(0, serverName.indexOf("."));
		}
		Store store = null;
		if (this.configService.getSysConfig().isSecond_domain_open()
				&& serverName.indexOf(".") != serverName.lastIndexOf(".")
				&& !secondDomain.equals("www")) {
			store = this.storeService.getObjByProperty(null,
					"store_second_domain", secondDomain);
		} else {
			store = this.storeService.getObjById(CommUtil.null2Long(id));
		}
		if (store == null) {
			ModelAndView mv = new JModelAndView("error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "不存在该店铺信息");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			return mv;
		} else {
			ModelAndView mv = null;
			if (store.getStore_decorate_old_info() != null
					&& !store.getStore_decorate_old_info().equals("")) {
				mv = new JModelAndView("default/store_index.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
			} else {
				mv = new JModelAndView("default/store_default.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
			}
			if (store.getStore_status() == 15) {
				if (store.getStore_decorate_old_info() != null
						&& !store.getStore_decorate_old_info().equals("")) {// 解析装修信息
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
					if (store.getStore_decorate_base_old_info() != null) {
						List<Map> fundations = Json.fromJson(List.class,
								store.getStore_decorate_base_old_info());
						for (Map fun : fundations) {
							mv.addObject("fun_" + fun.get("key"),
									fun.get("val"));
						}
					}
					List<Map> old_maps = Json.fromJson(List.class,
							store.getStore_decorate_old_info());
					mv.addObject("maps", old_maps);
					params.clear();
					params.put("sid", CommUtil.null2Long(id));
					params.put("slide_type", 0);
					List<StoreSlide> slides = this.storeSlideService
							.query("select obj from StoreSlide obj where obj.store.id=:sid and obj.slide_type=:slide_type",
									params, -1, -1);
					mv.addObject("default_slides", slides);
				} else {// 显示默认店铺装修信息
					this.add_store_common_info(mv, store);// 店铺商品信息
					this.generic_evaluate(store, mv);// 店铺信用信息
					mv.addObject("userTools", userTools);
				}
				String store_theme = "default";
				if (store.getStore_decorate_old_theme() != null) {
					store_theme = store.getStore_decorate_old_theme();
				}
				mv.addObject("store_theme", store_theme);
				if (store.getStore_decorate_background_old_info() != null) {
					Map bg = Json.fromJson(Map.class,
							store.getStore_decorate_background_old_info());
					mv.addObject("bg", bg);
				}
			} else if (store.getStore_status() == 25
					|| store.getStore_status() == 26) {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "店铺因为合同到期现已关闭，如有疑问请联系商城客服");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			} else if (store.getStore_status() < 15) {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "店铺未正常营业");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			} else if (store.getStore_status() == 20) {
				mv = new JModelAndView("error.html",
						configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "店铺因为违反商城相关规定现已关闭，如有疑问请联系商城客服");
				mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
			}
			return mv;
		}
	}

	/**
	 * 店铺头部，在店铺内所有页面使用httpInclude.include("/store_head.htm")
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/store_head.htm")
	public ModelAndView store_head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("default/store_head.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String type = CommUtil.null2String(request.getAttribute("type"));
		String store_id = CommUtil
				.null2String(request.getAttribute("store_id"));
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
		mv.addObject("store_id", store_id);
		mv.addObject("carts", carts_list);
		mv.addObject("type", type.equals("") ? "goods" : type);
		return mv;
	}

	/**
	 * 除店铺首页外，在店铺其他页面依然使用httpInclude.include("/store_nav.htm")
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/store_nav.htm")
	public ModelAndView store_nav(HttpServletRequest request,
			HttpServletResponse response) {
		Long id = CommUtil.null2Long(request.getAttribute("id"));
		Store store = this.storeService.getObjById(id);
		ModelAndView mv = new JModelAndView("default/store_nav.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (store.getStore_status() == 15) {
			Map params = new HashMap();
			params.put("store_id", store.getId());
			params.put("display", true);
			List<StoreNavigation> navs = this.storenavigationService
					.query("select obj from StoreNavigation obj where obj.store.id=:store_id and obj.display=:display order by obj.sequence asc",
							params, -1, -1);
			mv.addObject("navs", navs);
			mv.addObject("store", store);
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "店铺信息错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	/**
	 * 店铺导航详情页
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/store_url.htm")
	public ModelAndView store_url(HttpServletRequest request,
			HttpServletResponse response, String id) {
		StoreNavigation nav = this.storenavigationService.getObjById(CommUtil
				.null2Long(id));
		ModelAndView mv = new JModelAndView("default/store_url.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("store", nav.getStore());
		mv.addObject("nav", nav);
		mv.addObject("nav_id", nav.getId());
		this.add_store_common_info(mv, nav.getStore());// 店铺商品信息
		this.generic_evaluate(nav.getStore(), mv);// 店铺信用信息
		mv.addObject("userTools", userTools);
		String store_theme = "default";
		if (nav.getStore().getStore_decorate_old_theme() != null) {
			store_theme = nav.getStore().getStore_decorate_old_theme();
		}
		mv.addObject("store_theme", store_theme);
		if (nav.getStore().getStore_decorate_background_old_info() != null) {
			Map bg = Json.fromJson(Map.class, nav.getStore()
					.getStore_decorate_background_old_info());
			mv.addObject("bg", bg);
		}
		return mv;
	}

	/**
	 * 根据单个店铺分类查看对应的商品
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@RequestMapping("/goods_list.htm")
	public ModelAndView goods_list(HttpServletRequest request,
			HttpServletResponse response, String ugc_id, String store_id,
			String keyword, String orderBy, String orderType,
			String currentPage, String submit_type) {
		ModelAndView mv = new JModelAndView("default/goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService
				.getObjById(CommUtil.null2Long(store_id));
		if (store != null) {
			if (submit_type != null && !submit_type.equals("")) {
				if (keyword != null && !keyword.equals("")) {// 关键字搜索商品
					GoodsQueryObject gqo = new GoodsQueryObject(currentPage,
							mv, "", "");
					gqo.addQuery("obj.goods_store.id", new SysMap(
							"goods_store_id", store.getId()), "=");
					gqo.addQuery("obj.goods_status", new SysMap("goods_status",
							0), "=");
					gqo.addQuery("obj.goods_name", new SysMap("goods_name", "%"
							+ keyword + "%"), "like");
					gqo.setPageSize(18);
					if (orderBy != null && !orderBy.equals("")) {
						gqo.setOrderBy(orderBy);
						mv.addObject("orderBy", orderBy);
						gqo.setOrderType(orderType);
						mv.addObject("orderType", orderType);
					}
					IPageList pList = this.goodsService.list(gqo);
					String url = this.configService.getSysConfig().getAddress();
					CommUtil.saveIPageList2ModelAndView(
							url + "/goods_list.htm", "", "", pList, mv);
				}
				mv.addObject("submit_type", submit_type);
			} else {
				if (ugc_id != null && !ugc_id.equals("")) {// 店铺分类搜索商品
					UserGoodsClass ugc = this.userGoodsClassService
							.getObjById(CommUtil.null2Long(ugc_id));
					GoodsQueryObject gqo = new GoodsQueryObject(currentPage,
							mv, "", "");
					gqo.addQuery("obj.goods_store.id", new SysMap(
							"goods_store_id", store.getId()), "=");
					gqo.addQuery("obj.goods_status", new SysMap("goods_status",
							0), "=");
					if (ugc != null) {
						Set<Long> ids = this.genericUserGcIds(ugc);
						List<UserGoodsClass> ugc_list = new ArrayList<UserGoodsClass>();
						for (Long g_id : ids) {
							UserGoodsClass temp_ugc = this.userGoodsClassService
									.getObjById(g_id);
							ugc_list.add(temp_ugc);
						}
						Map paras = new HashMap();
						paras.put("ugc", ugc);
						gqo.addQuery("(:ugc member of obj.goods_ugcs", paras);
						// gqo.addQuery("ugc", ugc, "obj.goods_ugcs",
						// "member of");
						for (int i = 0; i < ugc_list.size(); i++) {
							// gqo.addQuery("ugc" + i, ugc_list.get(i),
							// "obj.goods_ugcs","member of", "or");
							paras.clear();
							if (i == ugc_list.size() - 1) {
								paras.put("ugc" + i, ugc_list.get(i));
								gqo.addQuery(" or :ugc" + i
										+ " member of obj.goods_ugcs)", paras);
							} else {
								paras.put("ugc" + i, ugc_list.get(i));
								gqo.addQuery(" or :ugc" + i
										+ " member of obj.goods_ugcs", paras);
							}
						}
					} else {
						ugc = new UserGoodsClass();
						ugc.setClassName("全部商品");
						mv.addObject("ugc", ugc);
					}
					if (orderBy != null && !orderBy.equals("")) {
						gqo.setOrderBy(orderBy);
						mv.addObject("orderBy", orderBy);
						gqo.setOrderType(orderType);
						mv.addObject("orderType", orderType);
					}
					gqo.setPageSize(18);
					IPageList pList = this.goodsService.list(gqo);
					String url = this.configService.getSysConfig().getAddress();
					CommUtil.saveIPageList2ModelAndView(
							url + "/goods_list.htm", "", "", pList, mv);
					mv.addObject("ugc", ugc);
				}
			}
			mv.addObject("ugc_id", ugc_id);
			mv.addObject("keyword", keyword);
			mv.addObject("store", store);
			mv.addObject("goodsViewTools", goodsViewTools);
			mv.addObject("storeViewTools", storeViewTools);
			mv.addObject("areaViewTools", areaViewTools);
			Map params = new HashMap();
			params.put("user_id", store.getUser().getId());
			params.put("display", true);
			List<UserGoodsClass> ugcs = this.userGoodsClassService
					.query("select obj from UserGoodsClass obj where obj.user_id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
							params, -1, -1);
			mv.addObject("ugcs", ugcs);// 店内分类
			params.clear();
			params.put("store_id", store.getId());
			List<Goods> hotgoods = this.goodsService
					.query("select obj from Goods obj where obj.goods_store.id=:store_id order by obj.goods_salenum desc",
							params, 0, 5);
			mv.addObject("hotgoods", hotgoods);// 热销排行
			params.clear();
			params.put("store_id", store.getId());
			params.put("display", true);
			List<StoreNavigation> navs = this.storenavigationService
					.query("select obj from StoreNavigation obj where obj.store.id=:store_id and obj.display =:display order by obj.sequence asc ",
							params, -1, -1);
			mv.addObject("navs", navs);// 导航栏
			this.generic_evaluate(store, mv);// 店铺评分信息
			mv.addObject("userTools", userTools);
			String store_theme = "default";
			if (store.getStore_decorate_old_theme() != null) {
				store_theme = store.getStore_decorate_old_theme();
			}
			mv.addObject("store_theme", store_theme);
			if (store.getStore_decorate_background_old_info() != null) {
				Map bg = Json.fromJson(Map.class,
						store.getStore_decorate_background_old_info());
				mv.addObject("bg", bg);
			}
		} else {
			mv = new JModelAndView("error.html", configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "请求参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index.htm");
		}
		return mv;
	}

	/**
	 * 店铺模块加载，通过url,加载不同的店铺模块
	 * 
	 * @param request
	 * @param response
	 * @param url
	 * @return
	 */
	@RequestMapping("/module_loading.htm")
	public String module_loading(HttpServletRequest request,
			HttpServletResponse response, String url, String id, String mark,
			String decorate_view, String div) {
		return "redirect:module_" + url + ".htm?id=" + id + "&mark=" + mark
				+ "&decorate_view=" + decorate_view + "&div=" + div;
	}

	/**
	 * 店铺导航模块加载
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/module_nav.htm")
	public ModelAndView module_nav(HttpServletRequest request,
			HttpServletResponse response, String id, String mark) {
		Store store = this.storeService.getObjById(CommUtil.null2Long(id));
		ModelAndView mv = new JModelAndView("default/module_nav.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		// 模块设置信息
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
		// 导航内容
		Map params = new HashMap();
		params.put("store_id", store.getId());
		params.put("display", true);
		List<StoreNavigation> navs = this.storenavigationService
				.query("select obj from StoreNavigation obj where obj.store.id=:store_id and obj.display=:display order by obj.sequence asc",
						params, -1, -1);
		mv.addObject("navs", navs);
		mv.addObject("store", store);
		return mv;
	}

	/**
	 * 店铺自定义幻灯模块加载
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/module_defined_slide.htm")
	public ModelAndView module_defined_slide(HttpServletRequest request,
			HttpServletResponse response, String id, String mark,
			String decorate_view) {
		ModelAndView mv = new JModelAndView(
				"default/module_defined_slide.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.getObjById(CommUtil.null2Long(id));
		Map params = new HashMap();
		List<Map> objs = new ArrayList<Map>();
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
			String temp_str[] = CommUtil.null2String(obj_map.get("slide_info"))
					.split("\\|");
			for (String str : temp_str) {
				if (!str.equals("")) {
					String temp[] = str.split("==");
					Accessory img = this.accessoryService.getObjById(CommUtil
							.null2Long(temp[0]));
					Map obj = new HashMap();
					obj.put("src", img.getPath() + "/" + img.getName());
					if (temp.length > 1) {
						obj.put("url", temp[1]);
					}
					objs.add(obj);
				}
			}
			mv.addObject("obj", obj_map);
			mv.addObject("slides", objs);
		}
		mv.addObject("decorate_view", decorate_view);// 是否为店铺装修视图，
		return mv;
	}

	/**
	 * 店铺分类模块加载
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/module_class.htm")
	public ModelAndView module_class(HttpServletRequest request,
			HttpServletResponse response, String id, String mark,
			String decorate_view) {
		ModelAndView mv = new JModelAndView("default/module_class.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.getObjById(CommUtil.null2Long(id));
		Map params = new HashMap();
		params.put("user_id", store.getUser().getId());
		params.put("display", true);
		List<UserGoodsClass> ugcs = this.userGoodsClassService
				.query("select obj from UserGoodsClass obj where obj.user_id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
						params, -1, -1);
		mv.addObject("ugcs", ugcs);
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
		mv.addObject("store", store);
		mv.addObject("decorate_view", decorate_view);
		mv.addObject("obj", obj_map);
		return mv;
	}

	/**
	 * 店铺热销商品列表模块加载
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/module_goods_sale.htm")
	public ModelAndView module_goods_sale(HttpServletRequest request,
			HttpServletResponse response, String id, String mark) {
		ModelAndView mv = new JModelAndView("default/module_goods_sale.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.getObjById(CommUtil.null2Long(id));
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
		int count = 5;
		if (obj_map != null && obj_map.get("goods_count") != null
				&& !obj_map.get("goods_count").equals("")) {
			count = CommUtil.null2Int(obj_map.get("goods_count"));
		}
		Map params = new HashMap();
		params.put("store_id", store.getId());
		params.put("goods_status", 0);
		List<Goods> hotgoods = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_salenum desc",
						params, 0, count);
		mv.addObject("hotgoods", hotgoods);// 加载热销商品
		mv.addObject("obj", obj_map);
		return mv;
	}

	/**
	 * 店铺信息模块加载
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/module_store_info.htm")
	public ModelAndView module_store_info(HttpServletRequest request,
			HttpServletResponse response, String id, String mark) {
		ModelAndView mv = new JModelAndView("default/module_store_info.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.getObjById(CommUtil.null2Long(id));
		this.generic_evaluate(store, mv);
		mv.addObject("store", store);
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
		mv.addObject("obj", obj_map);
		return mv;
	}

	/**
	 * 店铺自定义商品列表块加载（2排3列）
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/module_goods_top.htm")
	public ModelAndView module_goods_top(HttpServletRequest request,
			HttpServletResponse response, String id, String mark) {
		ModelAndView mv = new JModelAndView("default/module_goods_top.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.getObjById(CommUtil.null2Long(id));
		String goods_ids = null;
		List<Goods> objs = new ArrayList<Goods>();
		Map obj_map = new HashMap();
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = Json.fromJson(List.class,
					store.getStore_decorate_info());
			for (Map temp : maps) {
				if (temp.get("mark").equals(mark)) {
					goods_ids = CommUtil.null2String(temp.get("goods_ids"));
					obj_map = temp;
					break;
				}
			}
		}
		if (goods_ids != null) {
			String ids[] = goods_ids.split(",");
			for (String gid : ids) {
				if (!gid.equals("")) {
					Goods obj = this.goodsService.getObjById(CommUtil
							.null2Long(gid));
					objs.add(obj);
				}
			}
			mv.addObject("obj", obj_map);
			mv.addObject("objs", objs);
		}
		return mv;
	}

	/**
	 * 店铺自定义商品列表块加载（4排3列）
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param mark
	 * @return
	 */
	@RequestMapping("/module_goods_right.htm")
	public ModelAndView module_goods_right(HttpServletRequest request,
			HttpServletResponse response, String id, String mark) {
		ModelAndView mv = new JModelAndView("default/module_goods_right.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.getObjById(CommUtil.null2Long(id));
		String goods_ids = null;
		List<Goods> objs = new ArrayList<Goods>();
		Map obj = new HashMap();
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = Json.fromJson(List.class,
					store.getStore_decorate_info());
			for (Map temp : maps) {
				if (temp.get("mark").equals(mark)) {
					goods_ids = CommUtil.null2String(temp.get("goods_ids"));
					obj = temp;
					break;
				}
			}
		}
		if (goods_ids != null) {
			String ids[] = goods_ids.split(",");
			for (String gid : ids) {
				if (!gid.equals("")) {
					Goods goods = this.goodsService.getObjById(CommUtil
							.null2Long(gid));
					objs.add(goods);
				}
			}
			mv.addObject("obj", obj);
			mv.addObject("objs", objs);
		}
		return mv;
	}

	/**
	 * 店铺热点模块加载
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/module_hotspot.htm")
	public ModelAndView module_hotspot(HttpServletRequest request,
			HttpServletResponse response, String id, String mark, String div) {
		ModelAndView mv = new JModelAndView("default/module_hotspot.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.getObjById(CommUtil.null2Long(id));
		Map temp_map = null;
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
		if (div != null && !div.equals("undefined") && !div.equals("")
				&& !div.equals("null")) {
			coors_img_id_mark = "coors_img_id_" + div;
			coors_list_mark = "coors_list_" + div;
		}
		Accessory img = this.accessoryService.getObjById(CommUtil
				.null2Long(temp_map.get(coors_img_id_mark)));
		List<Map> temp_coors_list = (List<Map>) temp_map.get(coors_list_mark);
		List<Map> coors_list = new ArrayList<Map>();
		mv.addObject("coors_list", temp_coors_list);
		if (img != null) {
			Map obj = new HashMap();
			obj.put("src", CommUtil.getURL(request) + "/" + img.getPath() + "/"
					+ img.getName());
			obj.put("id", img.getId());
			mv.addObject("obj", obj);
		}
		return mv;
	}

	/**
	 * 解析前台热点坐标,会自动根据屏幕宽度缩放坐标比例
	 * 
	 * @param coords
	 * @param img_id
	 * @param width
	 *            ：屏幕分辨率宽度
	 * @return
	 */
	@RequestMapping("/generic_coords.htm")
	public void generic_coords(HttpServletRequest request,
			HttpServletResponse response, String coords, String img_id,
			String screen_width) {
		Accessory img = this.accessoryService.getObjById(CommUtil
				.null2Long(img_id));
		int setWidth = 680;// 设置图片时所显示的最大宽度
		int imageWidth = img.getWidth();// 图片宽度
		int screenWidth = CommUtil.null2Int(screen_width);// 屏幕实际分辨率宽度
		Map json_map = new HashMap();
		String final_coords = coords;
		double rate = 1;
		if (img != null && img.getWidth() > setWidth) {// 判断图片宽度是否大于680,680为平台设置图片时显示的最大宽度，前台显示时根据实际图片宽度等比例缩放热点区域大小
			rate = CommUtil.div(img.getWidth(), setWidth);
		}
		String nums[] = coords.split(",");
		String temp_coords = "";
		for (String num : nums) {
			String coor = CommUtil.null2String(Math.round(CommUtil.mul(rate,
					num)));
			if (temp_coords.equals("")) {
				temp_coords = coor;
			} else {
				temp_coords = temp_coords + "," + coor;
			}
		}
		if (!temp_coords.equals("")) {
			final_coords = temp_coords;
		}
		// 根据屏幕实际宽度缩放坐标
		int real_width = CommUtil.null2Int(screen_width);
		if (img.getWidth() > real_width) {
			double rate2 = CommUtil.div(real_width, img.getWidth());
			String temp_real_coors[] = temp_coords.split(",");
			String real_coors = "";
			for (String real : temp_real_coors) {
				String coor = CommUtil.null2String(Math.round(CommUtil.mul(
						rate2, real)));
				if (real_coors.equals("")) {
					real_coors = coor;
				} else {
					real_coors = real_coors + "," + coor;
				}
			}
			if (!real_coors.equals("")) {
				final_coords = real_coors;
			}
		}
		json_map.put("coords", final_coords);
		String json = Json.toJson(json_map, JsonFormat.compact());
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
	 * 店铺自定义内容模块加载
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/module_defined.htm")
	public ModelAndView module_defined(HttpServletRequest request,
			HttpServletResponse response, String id, String mark, String div) {
		ModelAndView mv = new JModelAndView("default/module_defined.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.getObjById(CommUtil.null2Long(id));
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
		mv.addObject("content", content);
		return mv;
	}

	/**
	 * 加载店铺相关信息
	 * 
	 * @param mv
	 * @param store
	 */
	private void add_store_common_info(ModelAndView mv, Store store) {
		Map params = new HashMap();
		params.put("user_id", store.getUser().getId());
		params.put("display", true);
		List<UserGoodsClass> ugcs = this.userGoodsClassService
				.query("select obj from UserGoodsClass obj where obj.user_id=:user_id and obj.display=:display and obj.parent.id is null order by obj.sequence asc",
						params, -1, -1);
		mv.addObject("ugcs", ugcs);// 加载店内分类

		params.clear();
		params.put("store_id", store.getId());
		params.put("goods_status", 0);
		List<Goods> hotgoods = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_salenum desc",
						params, 0, 5);
		mv.addObject("hotgoods", hotgoods);// 加载热销商品

		params.clear();
		params.put("recommend", true);
		params.put("goods_store_id", store.getId());
		params.put("goods_status", 0);
		List<Goods> goods_recommend = this.goodsService
				.query("select obj from Goods obj where obj.goods_recommend=:recommend and obj.goods_store.id=:goods_store_id and obj.goods_status=:goods_status order by obj.addTime desc",
						params, 0, 6);
		mv.addObject("goods_recommend", goods_recommend);// 加载推荐商品

		params.clear();
		params.put("store_id", store.getId());
		params.put("goods_status", 0);
		List<Goods> goods_collect = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.id=:store_id and obj.goods_status=:goods_status order by obj.goods_collect desc",
						params, 0, 6);
		mv.addObject("goods_collect", goods_collect);// 加载人气商品

		params.clear();
		params.put("goods_store_id", store.getId());
		params.put("goods_status", 0);
		List<Goods> goods_new = this.goodsService
				.query("select obj from Goods obj where obj.goods_store.id=:goods_store_id and obj.goods_status=:goods_status order by obj.addTime desc ",
						params, 0, 12);
		mv.addObject("goods", goods_new);// 加载最新商品

		params.clear();
		params.put("store_id", store.getId());
		params.put("display", true);
		List<StoreNavigation> navs = this.storenavigationService
				.query("select obj from StoreNavigation obj where obj.store.id=:store_id and obj.display =:display order by obj.sequence asc ",
						params, -1, -1);
		mv.addObject("navs", navs);// 导航栏

		mv.addObject("store", store);
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("storeViewTools", storeViewTools);
		mv.addObject("areaViewTools", areaViewTools);
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
			mv.addObject(
					"service_result",
					CommUtil.null2String(CommUtil.mul(service_result, 100) > 100 ? 100
							: CommUtil.mul(service_result, 100))
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
			mv.addObject(
					"ship_result",
					CommUtil.null2String(CommUtil.mul(ship_result, 100) > 100 ? 100
							: CommUtil.mul(ship_result, 100))
							+ "%");
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

	private Set<Long> genericUserGcIds(UserGoodsClass ugc) {
		Set<Long> ids = new HashSet<Long>();
		ids.add(ugc.getId());
		for (UserGoodsClass child : ugc.getChilds()) {
			Set<Long> cids = genericUserGcIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}
}
