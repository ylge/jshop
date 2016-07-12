package com.iskyshop.module.weixin.view.action;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Activity;
import com.iskyshop.foundation.domain.ActivityGoods;
import com.iskyshop.foundation.domain.Article;
import com.iskyshop.foundation.domain.ArticleClass;
import com.iskyshop.foundation.domain.Document;
import com.iskyshop.foundation.domain.FreeGoods;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsBrand;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.GroupGoods;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IActivityGoodsService;
import com.iskyshop.foundation.service.IActivityService;
import com.iskyshop.foundation.service.IArticleClassService;
import com.iskyshop.foundation.service.IArticleService;
import com.iskyshop.foundation.service.IDocumentService;
import com.iskyshop.foundation.service.IFreeGoodsService;
import com.iskyshop.foundation.service.IGoodsBrandService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupGoodsService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.lucene.LuceneResult;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.manage.admin.tools.FreeTools;
import com.iskyshop.manage.admin.tools.ImageTools;
import com.iskyshop.manage.admin.tools.UserTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;

/**
 * 
 * <p>
 * Title: WapIndexViewAction.java
 * </p>
 * 
 * <p>
 * Description:wap以及微信商城使用的前台首页控制器
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
 * @author jinxinzhe
 * 
 * @date 2014-11-20
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Controller
public class WeixinIndexViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsBrandService brandService;
	@Autowired
	private IGoodsClassService goodsClassService;
	@Autowired
	private IGroupGoodsService groupgoodsService;
	@Autowired
	private IGoodsBrandService goodsBrandService;
	@Autowired
	private IActivityService activityService;
	@Autowired
	private IGoodsCartService goodsCartService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private IActivityGoodsService activityGoodsService;
	@Autowired
	private IFreeGoodsService freeGoodsService;
	@Autowired
	private FreeTools freeTools;
	@Autowired
	private ImageTools imageTools;
	@Autowired
	private UserTools userTools;
	@Autowired
	private IDocumentService documentService;
	@Autowired
	private IArticleService articleService;
	@Autowired
	private IArticleClassService articleClassService;

	/**
	 * 手机客户端商城首页
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/wap/index.htm")
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("wap/index.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Date date = new Date();
		Map params = new HashMap();
		params.clear();
		params.put("weixin_hot", 1);
		params.put("goods_status", 0);
		List<Goods> goods_hots = this.goodsService
				.query("select obj from Goods obj where obj.weixin_hot=:weixin_hot and obj.goods_status =:goods_status order by weixin_hotTime desc",
						params, 0, 3);
		mv.addObject("goods_hots", goods_hots);
		params.clear();
		params.put("weixin_recommend", 1);
		params.put("goods_status", 0);
		List<Goods> top_recommends = this.goodsService
				.query("select obj from Goods obj where obj.weixin_recommend=:weixin_recommend and obj.goods_status =:goods_status order by obj.weixin_recommendTime desc",
						params, 0, 3);
		mv.addObject("top_recommends", top_recommends);
		params.clear();
		params.put("display", true);
		List<GoodsClass> gcs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.display=:display and obj.level=0 ",
						params, 0, 4);
		mv.addObject("gcs", gcs);
		params.clear();
		params.put("weixin_recommend", 1);
		params.put("audit", 1);
		List<GoodsBrand> gbs = this.brandService
				.query("select obj from GoodsBrand obj where obj.audit=:audit and obj.weixin_recommend=:weixin_recommend order by weixin_recommendTime desc",
						params, 0, 4);
		mv.addObject("gbs", gbs);
		params.clear();
		params.put("ac_begin_time", date);
		params.put("ac_end_time",date);
		params.put("ac_status", 1);
		List<Activity> activitys = this.activityService
				.query("select obj from Activity obj where obj.ac_status=:ac_status and obj.ac_begin_time<=:ac_begin_time and "
						+ "obj.ac_end_time>=:ac_end_time", params, 0, 1);
		if (activitys.size() > 0) {
			params.clear();
			params.put("ag_status", 1);
			params.put("goods_status", 0);
			params.put("weixin_recommend", 1);
			params.put("act_id", activitys.get(0).getId());
			List<ActivityGoods> activitygoods = this.activityGoodsService
					.query("select obj from ActivityGoods obj where obj.ag_status=:ag_status and obj.ag_goods.goods_status=:goods_status "
							+ "and obj.weixin_recommend=:weixin_recommend and obj.act.id=:act_id "
							+ " order by weixin_recommendTime desc", params, 0,
							3);
			mv.addObject("activitygoods", activitygoods);
		}
		params.clear();
		params.put("gg_status", 1);
		params.put("group_status", 0);
		params.put("weixin_recommend", 1);
		params.put("beginTime", date);
		params.put("endTime", date);
		params.put("group_beginTime", date);
		params.put("group_endTime", date);
		List<GroupGoods> groupgoods = this.groupgoodsService
				.query("select obj from GroupGoods obj where obj.gg_status=:gg_status and obj.group.status=:group_status "
						+ "and obj.weixin_recommend=:weixin_recommend and obj.group.beginTime<=:group_beginTime and "
						+ "obj.group.endTime>=:group_endTime and obj.beginTime<=:beginTime and obj.endTime>=:endTime order by weixin_recommendTime desc",
						params, 0, 4);
		mv.addObject("groupgoods", groupgoods);
		params.clear();
		params.put("freeStatus", 5);
		params.put("weixin_recommend", 1);
		List<FreeGoods> freegoods = this.freeGoodsService
				.query("select obj from FreeGoods obj where obj.freeStatus=:freeStatus and obj.weixin_recommend="
						+ ":weixin_recommend order by obj.weixin_recommendTime desc",
						params, 0, 1);
		mv.addObject("freegoods", freegoods);
		mv.addObject("freeTools", freeTools);
		mv.addObject("imageTools", imageTools);
		return mv;
	}

	/**
	 * 手机端商品搜索
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/wap/search.htm")
	public ModelAndView search(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String currentPage,
			String orderBy, String orderType, String goods_type,
			String goods_inventory, String keyword, String goods_transfee,
			String goods_cod) throws UnsupportedEncodingException {
		ModelAndView mv = new JModelAndView("wap/search_goods.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (keyword != null && !keyword.equals("")) {
			// 根据店铺SEO关键字，查出关键字命中的店铺
			if (keyword != null && !keyword.equals("") && keyword.length() > 1) {
				mv.addObject("stores", search_stores_seo(keyword));
			}
			String path = System.getProperty("iskyshopb2b2c.root") + File.separator
					+ "luence" + File.separator + "goods";
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setIndex_path(path);
			List temp_list = this.goodsClassService.query(
					"select obj.id from GoodsClass obj", null, -1, -1);
			lucene.setGc_size(temp_list.size());
			boolean order_type = true;
			String order_by = "";
			Sort sort = null;
			String query_gc = "";
			// 处理排序方式
			if (CommUtil.null2String(orderBy).equals("goods_salenum")) {
				order_by = "goods_salenum";
				sort = new Sort(new SortField(order_by, SortField.Type.INT,
						order_type));
			}
			if (CommUtil.null2String(orderBy).equals("goods_collect")) {
				order_by = "goods_collect";
				sort = new Sort(new SortField(order_by, SortField.Type.INT,
						order_type));
			}
			if (CommUtil.null2String(orderBy).equals("well_evaluate")) {
				order_by = "well_evaluate";
				sort = new Sort(new SortField(order_by, SortField.Type.DOUBLE,
						order_type));
			}
			if (CommUtil.null2String(orderType).equals("asc")) {
				order_type = false;
			}
			if (CommUtil.null2String(orderType).equals("")) {
				orderType = "desc";
			}
			if (CommUtil.null2String(orderBy).equals("store_price")) {
				order_by = "store_price";
				sort = new Sort(new SortField(order_by, SortField.Type.DOUBLE,
						order_type));
			}
			if (gc_id != null && !gc_id.equals("")) {
				GoodsClass gc = this.goodsClassService.getObjById(CommUtil
						.null2Long(gc_id));
				query_gc = gc.getLevel() == 1 ? gc_id + "_*" : CommUtil
						.null2String(gc.getParent().getId()) + "_" + gc_id;
				mv.addObject("gc_id", gc_id);
			}
			LuceneResult pList = null;
			if (sort != null) {
				pList = lucene.search(keyword, CommUtil.null2Int(currentPage),
						goods_inventory, goods_type, query_gc, goods_transfee,
						goods_cod, sort, null, null, null);
			} else {
				pList = lucene.search(keyword, CommUtil.null2Int(currentPage),
						goods_inventory, goods_type, query_gc, goods_transfee,
						goods_cod, null, null, null);
			}

			CommUtil.saveLucene2ModelAndView(pList, mv);

			// 对关键字命中的商品进行分类提取
			Set<String> list_gcs = lucene.LoadData_goods_class(keyword);
			// 对商品分类数据进行分析加载,只查询id和className
			List<GoodsClass> gcs = this.query_GC_second(list_gcs);

			mv.addObject("list_gc", list_gcs);
			mv.addObject("gcs", gcs);
			mv.addObject("allCount", pList.getRows());
		}
		mv.addObject("keyword", keyword);
		mv.addObject("orderBy", orderBy);
		mv.addObject("orderType", orderType);
		mv.addObject("goods_type", goods_type);
		mv.addObject("goods_inventory", goods_inventory);
		mv.addObject("goods_transfee", goods_transfee);
		mv.addObject("goods_cod", goods_cod);
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("userTools", userTools);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * 手机端商品搜索
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/wap/search_ajax.htm")
	public ModelAndView search_ajax(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String currentPage,
			String orderBy, String orderType, String goods_type,
			String goods_inventory, String keyword, String goods_transfee,
			String goods_cod) {
		ModelAndView mv = new JModelAndView(
				"wap/search_goods_data.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (keyword != null && !keyword.equals("")) {
			// 根据店铺SEO关键字，查出关键字命中的店铺
			if (keyword != null && !keyword.equals("") && keyword.length() > 1) {
				mv.addObject("stores", search_stores_seo(keyword));
			}
			String path = System.getProperty("iskyshopb2b2c.root") + File.separator
					+ "luence" + File.separator + "goods";
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setIndex_path(path);
			List temp_list = this.goodsClassService.query(
					"select obj.id from GoodsClass obj", null, -1, -1);
			lucene.setGc_size(temp_list.size());
			boolean order_type = true;
			String order_by = "";
			Sort sort = null;
			String query_gc = "";
			// 处理排序方式
			if (CommUtil.null2String(orderBy).equals("goods_salenum")) {
				order_by = "goods_salenum";
				sort = new Sort(new SortField(order_by, SortField.Type.INT,
						order_type));
			}
			if (CommUtil.null2String(orderBy).equals("goods_collect")) {
				order_by = "goods_collect";
				sort = new Sort(new SortField(order_by, SortField.Type.INT,
						order_type));
			}
			if (CommUtil.null2String(orderBy).equals("well_evaluate")) {
				order_by = "well_evaluate";
				sort = new Sort(new SortField(order_by, SortField.Type.DOUBLE,
						order_type));
			}
			if (CommUtil.null2String(orderType).equals("asc")) {
				order_type = false;
			}
			if (CommUtil.null2String(orderType).equals("")) {
				orderType = "desc";
			}
			if (CommUtil.null2String(orderBy).equals("store_price")) {
				order_by = "store_price";
				sort = new Sort(new SortField(order_by, SortField.Type.DOUBLE,
						order_type));
			}
			if (gc_id != null && !gc_id.equals("")) {
				GoodsClass gc = this.goodsClassService.getObjById(CommUtil
						.null2Long(gc_id));
				query_gc = gc.getLevel() == 1 ? gc_id + "_*" : CommUtil
						.null2String(gc.getParent().getId()) + "_" + gc_id;
				mv.addObject("gc_id", gc_id);
			}
			LuceneResult pList = null;
			if (sort != null) {
				pList = lucene.search(keyword, CommUtil.null2Int(currentPage),
						goods_inventory, goods_type, query_gc, goods_transfee,
						goods_cod, sort, null, null, null);
			} else {
				pList = lucene.search(keyword, CommUtil.null2Int(currentPage),
						goods_inventory, goods_type, query_gc, goods_transfee,
						goods_cod, null, null, null);
			}

			CommUtil.saveLucene2ModelAndView(pList, mv);

			// 对关键字命中的商品进行分类提取
			Set<String> list_gcs = lucene.LoadData_goods_class(keyword);
			// 对商品分类数据进行分析加载,只查询id和className
			List<GoodsClass> gcs = this.query_GC_second(list_gcs);

			mv.addObject("list_gc", list_gcs);
			mv.addObject("gcs", gcs);
			mv.addObject("allCount", pList.getRows());
		}
		mv.addObject("keyword", keyword);
		mv.addObject("orderBy", orderBy);
		mv.addObject("orderType", orderType);
		mv.addObject("goods_type", goods_type);
		mv.addObject("goods_inventory", goods_inventory);
		mv.addObject("goods_transfee", goods_transfee);
		mv.addObject("goods_cod", goods_cod);
		mv.addObject("goodsViewTools", goodsViewTools);
		mv.addObject("userTools", userTools);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@RequestMapping("/wap/footer.htm")
	public ModelAndView footer(HttpServletRequest request,
			HttpServletResponse response, String op) {
		ModelAndView mv = new JModelAndView("wap/footer.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("name", request.getServerName());
		return mv;
	}
	
	@RequestMapping("/wap/layer.htm")
	public ModelAndView layer(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("wap/layer.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}

	/**
	 * 根据前端二维码扫描结果自动下载对应的手机客户端
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/wap/app/download.htm")
	public void app_download(HttpServletRequest request,
			HttpServletResponse response) {
		// System.out.println(request.getHeader("User-Agent").toLowerCase());
		String user_agent = request.getHeader("User-Agent").toLowerCase();
		String url = CommUtil.getURL(request);
		// String ios_reg =
		// ".+?\\(iphone; cpu \\w+ os [1-9]\\d*_\\d+_\\d+ \\w+ mac os x\\).+";
		if (user_agent.indexOf("iphone") > 0) {
			url = this.configService.getSysConfig().getIos_download();
		}
		if (user_agent.indexOf("android") > 0) {
			url = this.configService.getSysConfig().getAndroid_download();
		}
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 计算并合并购车信息
	 * 
	 * @param request
	 * @return
	 */
	private List<GoodsCart> cart_calc(HttpServletRequest request) {
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
						if (gc.getGoods().getGoods_type() == 0) {// 该商品为商家商品
							
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
		// 将cookie购物车与用户user购物车合并，去重
		if (user != null) {
			for (GoodsCart ugc : carts_user) {
				carts_list.add(ugc);
			}
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
				if (add) {// 将cookie_cart转变为user_cart
					cookie.setCart_session_id(null);
					cookie.setUser(user);
					this.goodsCartService.update(cookie);
					carts_list.add(cookie);
				}
			}
		} else {
			for (GoodsCart cookie : carts_cookie) {
				carts_list.add(cookie);
			}
		}
		return carts_list;
	}
	
	/**
	 * 对商品分类数据进行处理去重，返回页面用以显示的二级分类
	 * 
	 * @param lucenc商品分类数据
	 * @return
	 */
	public List<GoodsClass> query_GC_second(Set<String> list_gcs) {
		String sid = new String();
		Map params = new HashMap();
		List<GoodsClass> gcs = new ArrayList<GoodsClass>();
		Set<Long> ids = new HashSet<Long>();
		for (String str : list_gcs) {
			sid = str.split("_")[0];
			ids.add(CommUtil.null2Long(sid));
		}
		if (!ids.isEmpty()) {
			params.put("ids", ids);
			gcs = this.goodsClassService
					.query("select new GoodsClass(id,className) from GoodsClass obj where obj.id in(:ids)",
							params, -1, -1);
		}
		return gcs;
	}
	
	/**
	 * 根据店铺SEO关键字，查出关键字命中的店铺
	 * 
	 * @param keyword
	 * @return
	 */
	public List<Store> search_stores_seo(String keyword) {
		Map params = new HashMap();
		params.put("keyword1", keyword);
		params.put("keyword2", keyword + ",%");
		params.put("keyword3", "%," + keyword + ",%");
		params.put("keyword4", "%," + keyword);
		List<Store> stores = this.storeService
				.query("select obj from Store obj where obj.store_seo_keywords =:keyword1 or obj.store_seo_keywords like:keyword2 or obj.store_seo_keywords like:keyword3 or obj.store_seo_keywords like:keyword4",
						params, 0, 3);
		Collections.sort(stores, new Comparator() {
			public int compare(Object o1, Object o2) {
				Store store1 = (Store) o1;
				Store store2 = (Store) o2;
				int l1 = store1.getStore_seo_keywords().split(",").length;
				int l2 = store2.getStore_seo_keywords().split(",").length;
				if (l1 > l2) {
					return 1;
				}
				;
				if (l1 == l2) {
					if (store1.getPoint().getStore_evaluate()
							.compareTo(store2.getPoint().getStore_evaluate()) == 1) {
						return -1;
					}
					;
					if (store1.getPoint().getStore_evaluate()
							.compareTo(store2.getPoint().getStore_evaluate()) == -1) {
						return 1;
					}
					;
					return 0;
				}
				return -1;
			}
		});
		return stores;
	}
	
	@RequestMapping("/wap/doc.htm")
	public ModelAndView doc(HttpServletRequest request,
			HttpServletResponse response, String mark) {
		ModelAndView mv = new JModelAndView("wap/article.html", configService
				.getSysConfig(), this.userConfigService.getUserConfig(), 1,
				request, response);
		mv.addObject("doc", "doc");
		Document obj = this.documentService.getObjByProperty(null,"mark", mark);
		mv.addObject("obj", obj);
		return mv;
	}
}
