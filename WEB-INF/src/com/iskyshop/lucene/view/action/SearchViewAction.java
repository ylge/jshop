package com.iskyshop.lucene.view.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IGoodsClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IGroupClassService;
import com.iskyshop.foundation.service.IGroupLifeGoodsService;
import com.iskyshop.foundation.service.IGroupPriceRangeService;
import com.iskyshop.foundation.service.IGroupService;
import com.iskyshop.foundation.service.IStoreGradeService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.lucene.LuceneResult;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.manage.admin.tools.UserTools;
import com.iskyshop.view.web.tools.GoodsViewTools;
import com.iskyshop.view.web.tools.NavViewTools;
import com.iskyshop.view.web.tools.StoreViewTools;

/**
 * 
 * <p>
 * Title: SearchViewAction.java
 * </p>
 * 
 * <p>
 * Description: 商品搜索控制器，商城搜索支持关键字全文搜索
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
 * @author erikzhang,jy
 * 
 * @date 2014-6-5
 * 
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Controller
public class SearchViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private StoreViewTools storeViewTools;
	@Autowired
	private IStoreGradeService storeGradeService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IGroupPriceRangeService groupPriceRangeService;
	@Autowired
	private IGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private IGroupClassService groupClassService;
	@Autowired
	private NavViewTools navTools;
	@Autowired
	private UserTools userTools;
	@Autowired
	private IGroupService groupService;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IGoodsClassService goodsClassService;

	@RequestMapping("/search.htm")
	public ModelAndView search(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String currentPage,
			String orderBy, String orderType, String goods_type,
			String goods_inventory, String keyword, String goods_transfee,
			String goods_cod) throws UnsupportedEncodingException {
		ModelAndView mv = new JModelAndView("lucene/search_goods_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		// 将关键字加入用户的搜索历史中
		if (keyword != null && !keyword.equals("")) {
			response.addCookie(search_history_cookie(request, keyword));

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
			if (CommUtil.null2String(orderBy).equals("goods_current_price")) {
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
		// 加载页面上其它的商品信息，最近浏览，猜你喜欢，推广热卖，直通车。
		this.search_other_goods(mv);
		// 处理系统商品对比信息
		List<Goods> goods_compare_list = (List<Goods>) request
				.getSession(false).getAttribute("goods_compare_cart");
		// 计算商品对比中第一间商品的分类，只允许对比同一个分类的商品
		if (goods_compare_list == null) {
			goods_compare_list = new ArrayList<Goods>();
		}
		int compare_goods_flag = 0;// 默认允许对比商品，如果商品分类不一致曾不允许对比
		for (Goods compare_goods : goods_compare_list) {
			if (compare_goods != null) {
				compare_goods = this.goodsService.getObjById(compare_goods
						.getId());
				if (!compare_goods.getGc().getParent().getParent().getId()
						.equals(CommUtil.null2Long(gc_id))) {
					compare_goods_flag = 1;
				}
			}
		}
		mv.addObject("compare_goods_flag", compare_goods_flag);
		mv.addObject("goods_compare_list", goods_compare_list);
		return mv;
	}

	/**
	 * 搜索框下拉提示相关分类
	 * 
	 * @param request
	 * @param response
	 * @param keyword
	 */
	@RequestMapping("/search_goodsclass.htm")
	public void search_goodsclass(HttpServletRequest request,
			HttpServletResponse response, String keyword) {
		if (keyword == null || keyword.equals("")) {
			return;
		}
		Map params = new HashMap();
		params.put("level", 2);
		params.put("keyword", keyword + "%");
		List<GoodsClass> objs = this.goodsClassService
				.query("select obj from GoodsClass obj where obj.level<:level and obj.className like :keyword",
						params, -1, -1);
		Map map = null;
		if (objs.size() > 0) {
			map = new HashMap();
			List<Map> list_parent = new ArrayList<Map>();
			List list_child = new ArrayList<Map>();
			for (GoodsClass obj : objs) {
				Map<String, String> parent_gc = new HashMap<String, String>();
				parent_gc.put("id", obj.getId().toString());
				parent_gc.put("name", obj.getClassName());
				list_parent.add(parent_gc);
				List<Map<String, String>> list_temp = new ArrayList<Map<String, String>>();
				for (GoodsClass Child : obj.getChilds()) {
					Map<String, String> map_temp = new HashMap<String, String>();
					map_temp.put("id", Child.getId().toString());
					map_temp.put("name", Child.getClassName());
					list_temp.add(map_temp);
				}
				list_child.add(list_temp);
			}
			map.put("parent_gc", list_parent);
			map.put("list_child", list_child);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加载页面上其它的商品信息，最近浏览，猜你喜欢，推广热卖，直通车。
	 * 
	 * @param mv
	 */
	public void search_other_goods(ModelAndView mv) {
		// 如果系统开启直通车，商品列表页顶部推荐热卖商品及左侧推广商品均显示直通车商品
		if (this.configService.getSysConfig().isZtc_status()) {
			// 页面左侧8条数据，从第3位开始查询
			List<Goods> left_ztc_goods = null;
			Map ztc_map = new HashMap();
			ztc_map.put("ztc_status", 3);
			ztc_map.put("now_date", new Date());
			ztc_map.put("ztc_gold", 0);
			// 获取所有商品数量，查询出的对象为只有id的对象，减少查询压力,查询所有直通车商品，随机显示出指定数量
			List<Goods> all_left_ztc_goods = this.goodsService
					.query("select obj.id from Goods obj where obj.ztc_status =:ztc_status "
							+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
							+ "order by obj.ztc_dredge_price desc", ztc_map,
							-1, -1);
			left_ztc_goods = this.goodsService
					.query("select new Goods(id, goods_name, goods_current_price,goods_price, goods_main_photo) from Goods obj where obj.ztc_status =:ztc_status "
							+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
							+ "order by obj.ztc_dredge_price desc", ztc_map, 3,
							all_left_ztc_goods.size());
			left_ztc_goods = this.goodsViewTools.randomZtcGoods2(
					left_ztc_goods, 8);
			mv.addObject("left_ztc_goods", left_ztc_goods);
			// 页面顶部,直通车前3个商品
			List<Goods> top_ztc_goods = null;
			Map ztc_map2 = new HashMap();
			ztc_map2.put("ztc_status", 3);
			ztc_map2.put("now_date", new Date());
			ztc_map2.put("ztc_gold", 0);
			top_ztc_goods = this.goodsService
					.query("select new Goods(id, goods_name, goods_current_price,goods_price, goods_main_photo) from Goods obj where obj.ztc_status =:ztc_status "
							+ "and obj.ztc_begin_time <=:now_date and obj.ztc_gold>:ztc_gold "
							+ "order by obj.ztc_dredge_price desc", ztc_map2,
							0, 3);
			mv.addObject("top_ztc_goods", top_ztc_goods);
		} else {
			Map params = new HashMap();
			params.put("store_recommend", true);
			params.put("goods_status", 0);
			List<Goods> top_ztc_goods = this.goodsService
					.query("select new Goods(id, goods_name, goods_current_price,goods_price, goods_main_photo) from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params, 0, 3);
			mv.addObject("top_ztc_goods", top_ztc_goods);
			params.clear();
			params.put("store_recommend", true);
			params.put("goods_status", 0);
			// 获取所有商品数量，查询出的对象为只有id的对象，减少查询压力,查询所有直通车商品，随机显示出指定数量
			List<Goods> all_goods = this.goodsService
					.query("select obj.id from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params, -1, -1);
			List<Goods> left_ztc_goods = this.goodsService
					.query("select new Goods(id, goods_name, goods_current_price,goods_price, goods_main_photo) from Goods obj where obj.store_recommend=:store_recommend and obj.goods_status=:goods_status order by obj.goods_salenum desc",
							params, 3, all_goods.size());
			left_ztc_goods = this.goodsViewTools.randomZtcGoods2(
					left_ztc_goods, 8);
			mv.addObject("left_ztc_goods", left_ztc_goods);
		}
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
			params.put("display", true);
			gcs = this.goodsClassService
					.query("select new GoodsClass(id,className) from GoodsClass obj where obj.id in(:ids) and obj.display=:display",
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

	/**
	 * 得到一个存有搜索数据的Cookie
	 * 
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public Cookie search_history_cookie(HttpServletRequest request,
			String keyword) throws UnsupportedEncodingException {
		String str = "";
		Cookie[] cookies = request.getCookies();
		Cookie search_cookie = null;
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("search_history")) {
				String str_temp = URLDecoder.decode(cookie.getValue(), "UTF-8");
				for (String s : str_temp.split(",")) {
					if (!s.equals(keyword) && !str.equals("")) {
						str = str + "," + s;
					} else if (!s.equals(keyword)) {
						str = s;
					}
				}
				break;
			}
			;
		}
		if (str.equals("")) {
			str = keyword;
			str = URLEncoder.encode(str, "UTF-8");
			search_cookie = new Cookie("search_history", str);
		} else {
			str = keyword + "," + str;
			str = URLEncoder.encode(str, "UTF-8");
			search_cookie = new Cookie("search_history", str);
		}
		return search_cookie;
	}
}
