package com.iskyshop.manage.seller.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.CombinPlan;
import com.iskyshop.foundation.domain.GoodsCart;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.service.ICombinPlanService;
import com.iskyshop.foundation.service.IGoodsCartService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;

/**
 * 
 * <p>
 * Title: CombinTools.java
 * </p>
 * 
 * <p>
 * Description: 组合销售商品信息解析工具类
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
 * @date 2014-10-8
 * 
 * @version iskyshop_b2b2c 2.0
 */
@Component
public class CombinTools {
	@Autowired
	private ICombinPlanService combinplanService;
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsCartService goodsCartService;

	/**
	 * 解析组合商品中主商品信息
	 * 
	 * @param plan_id
	 * @return
	 */
	public Map getMainGoodsMap(String plan_id) {
		Map map_temp = new HashMap();
		CombinPlan obj = this.combinplanService.getObjById(CommUtil
				.null2Long(plan_id));
		map_temp = (Map) Json.fromJson(obj.getMain_goods_info());
		return map_temp;
	}

	/**
	 * 解析组合商品中所有方案信息
	 * 
	 * @param plan_id
	 * @return
	 */
	public List<Map> getCombinGoodsMaps(String plan_id) {
		List<Map> map_temps = new ArrayList<Map>();
		CombinPlan obj = this.combinplanService.getObjById(CommUtil
				.null2Long(plan_id));
		map_temps = (List<Map>) Json.fromJson(obj.getCombin_plan_info());
		return map_temps;
	}

	/**
	 * 解析方案中商品信息,返回List<Map>
	 * 
	 * @param plan_id
	 * @return
	 */
	public List<Map> getCombinGoodsInfo(Map map) {
		List<Map> map_temps = new ArrayList<Map>();
		map_temps = (List<Map>) map.get("goods_list");
		return map_temps;
	}

	/**
	 * 解析方案中商品信息,返回List<Map>,返回list长度为5，如果长度不够5，使用null替代
	 * 
	 * @param plan_id
	 * @return
	 */
	public List<Map> getCombinGoodsInfo_list(Map map) {
		List<Map> map_list = new ArrayList<Map>();
		List<Map> map_temps = (List<Map>) map.get("goods_list");
		int max = this.configService.getSysConfig().getCombin_count();
		for (int i = 0; i < max; i++) {
			map_list.add(null);
		}
		for (int i = 0; i < map_temps.size(); i++) {
			map_list.set(i, map_temps.get(i));
		}
		return map_list;
	}

	/**
	 * 解析方案中商品信息id,返回多个id，以逗号间隔
	 * 
	 * @param plan_id
	 * @return
	 */
	public String getCombinGoodsIds(Map map) {
		String ids = "";
		List<Map> map_temps = (List<Map>) map.get("goods_list");
		for (Map map2 : map_temps) {
			ids = ids + "," + CommUtil.null2String(map2.get("id"));
		}
		return ids;
	}

	/**
	 * 解析组合销售所属店铺
	 * 
	 * @param plan_id
	 * @return
	 */
	public String getStoreName(String plan_id) {
		String store_name = "平台自营";
		CombinPlan obj = this.combinplanService.getObjById(CommUtil
				.null2Long(plan_id));
		if (obj.getCombin_form() == 1) {
			Store store = this.storeService.getObjById(obj.getStore_id());
			if (store != null) {
				store_name = store.getStore_name();
			}
		}
		return store_name;
	}

	public List<GoodsCart> combin_carts_detail(String id) {
		Map json_map = new HashMap();
		List<Map> map_list = new ArrayList<Map>();
		GoodsCart cart = this.goodsCartService.getObjById(CommUtil
				.null2Long(id));
		List<GoodsCart> gcs = new ArrayList<GoodsCart>();
		if (cart != null) {
			if (cart.getCart_type() != null
					&& cart.getCart_type().equals("combin")
					&& cart.getCombin_main() == 1) {
				String cart_ids[] = cart.getCombin_suit_ids().split(",");
				for (String cart_id : cart_ids) {
					if (!cart_id.equals("") && !cart_id.equals(id)) {
						GoodsCart other = this.goodsCartService
								.getObjById(CommUtil.null2Long(cart_id));
						if (other != null) {
							gcs.add(other);
						}
					}
				}
			}
		}
		return gcs;
	}

}
