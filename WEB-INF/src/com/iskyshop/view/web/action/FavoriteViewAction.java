package com.iskyshop.view.web.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Favorite;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.GoodsLog;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IFavoriteService;
import com.iskyshop.foundation.service.IGoodsLogService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.lucene.LuceneUtil;
import com.iskyshop.lucene.tools.LuceneVoTools;
import com.iskyshop.view.web.tools.GoodsViewTools;

/**
 * 
 * <p>
 * Title: FavoriteViewAction.java
 * </p>
 * 
 * <p>
 * Description: 商城前台收藏控制器，用来添加商品、店铺收藏
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
 * @date 2014-4-30
 * 
 * @version iskyshop_b2b2c v2.0 2015版 
 */
@Controller
public class FavoriteViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IFavoriteService favoriteService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private LuceneVoTools luceneVoTools;
	@Autowired
	private GoodsViewTools goodsViewTools;
	@Autowired
	private IGoodsLogService goodsLogService;

	@RequestMapping("/add_goods_favorite.htm")
	public void add_goods_favorite(HttpServletResponse response, String id) {
		Map params = new HashMap();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		params.put("goods_id", CommUtil.null2Long(id));
		List<Favorite> list = this.favoriteService
				.query("select obj from Favorite obj where obj.user_id=:user_id and obj.goods_id=:goods_id",
						params, -1, -1);
		int ret = 0;
		if (list.size() == 0) {
			Goods goods = this.goodsService.getObjById(CommUtil.null2Long(id));
			Favorite obj = new Favorite();
			obj.setAddTime(new Date());
			obj.setType(0);
			User user = SecurityUserHolder.getCurrentUser();
			obj.setUser_name(user.getUserName());
			obj.setUser_id(user.getId());
			obj.setGoods_id(goods.getId());
			obj.setGoods_name(goods.getGoods_name());
			if(goods.getGoods_main_photo()!=null){
			obj.setGoods_photo(goods.getGoods_main_photo().getPath() + "/"
					+ goods.getGoods_main_photo().getName());
			obj.setGoods_photo_ext(goods.getGoods_main_photo().getExt());
			}
			obj.setGoods_store_id(goods.getGoods_store() == null ? null : goods
					.getGoods_store().getId());
			obj.setGoods_type(goods.getGoods_type());
			obj.setGoods_current_price(goods.getGoods_current_price());
			if (this.configService.getSysConfig().isSecond_domain_open()) {
				Store store = this.storeService.getObjById(obj.getStore_id());
				obj.setGoods_store_second_domain(store.getStore_second_domain());
			}
			this.favoriteService.save(obj);
			goods.setGoods_collect(goods.getGoods_collect() + 1);
			this.goodsService.update(goods);
			GoodsLog todayGoodsLog = this.goodsViewTools.getTodayGoodsLog(obj
					.getId());
			todayGoodsLog
					.setGoods_collect(todayGoodsLog.getGoods_collect() + 1);
			this.goodsLogService.update(todayGoodsLog);
			// 更新lucene索引
			String goods_lucene_path = System.getProperty("iskyshopb2b2c.root")
					+ File.separator + "luence" + File.separator + "goods";
			File file = new File(goods_lucene_path);
			if (!file.exists()) {
				CommUtil.createFolder(goods_lucene_path);
			}
			LuceneUtil lucene = LuceneUtil.instance();
			lucene.setIndex_path(goods_lucene_path);
			lucene.update(CommUtil.null2String(goods.getId()),
					luceneVoTools.updateGoodsIndex(goods));
		} else {
			ret = 1;
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

	@RequestMapping("/add_store_favorite.htm")
	public void add_store_favorite(HttpServletResponse response, String id) {
		Map params = new HashMap();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		params.put("store_id", CommUtil.null2Long(id));
		List<Favorite> list = this.favoriteService
				.query("select obj from Favorite obj where obj.user_id=:user_id and obj.store_id=:store_id",
						params, -1, -1);
		int ret = 0;
		if (list.size() == 0) {
			Favorite obj = new Favorite();
			obj.setAddTime(new Date());
			obj.setType(1);
			User user = SecurityUserHolder.getCurrentUser();
			Store store = this.storeService.getObjById(CommUtil.null2Long(id));
			obj.setUser_id(user.getId());
			obj.setStore_id(store.getId());
			obj.setStore_name(store.getStore_name());
			obj.setStore_photo(store.getStore_logo() != null ? store
					.getStore_logo().getPath()
					+ "/"
					+ store.getStore_logo().getName() : null);
			if (this.configService.getSysConfig().isSecond_domain_open()) {
				obj.setStore_second_domain(store.getStore_second_domain());
			}
			String store_addr = "";
			if (store.getArea() != null) {
				store_addr = store.getArea().getAreaName() + store.getStore_address();
				if (store.getArea().getParent() != null) {
					store_addr = store.getArea().getParent().getAreaName()
							+ store_addr;
					if (store.getArea().getParent().getParent() != null) {
						store_addr = store.getArea().getParent().getParent()
								.getAreaName()
								+ store_addr;
					}
				}
			}
			obj.setStore_ower(store.getUser().getUserName());
			obj.setStore_addr(store_addr);
			this.favoriteService.save(obj);
			store.setFavorite_count(store.getFavorite_count() + 1);
			this.storeService.update(store);
		} else {
			ret = 1;
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
}
