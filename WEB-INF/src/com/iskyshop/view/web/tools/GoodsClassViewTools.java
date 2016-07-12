package com.iskyshop.view.web.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.AdvertPosition;
import com.iskyshop.foundation.domain.GoodsClass;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IAdvertPositionService;
import com.iskyshop.foundation.service.IGoodsClassService;

/**
 * 
 * <p>
 * Title: GoodsClassViewTools.java
 * </p>
 * 
 * <p>
 * Description:
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
 * @date 2014-8-25
 * 
 * @version iskyshop_b2b2c 2014
 */
@Component
public class GoodsClassViewTools {
	@Autowired
	private IGoodsClassService gcService;
	@Autowired
	private IAdvertPositionService apService;
	@Autowired
	private IAccessoryService accessoryService;

	/**
	 * 查询指定数量的商品分类信息
	 * 
	 * @param pid
	 * @param count
	 * @return
	 */
	public List<GoodsClass> query_gc(String pid, int count) {
		List<GoodsClass> gcs = new ArrayList<GoodsClass>();
		Map params = new HashMap();
		params.put("display", true);
		if (!CommUtil.null2String(pid).equals("")) {
			params.put("pid", CommUtil.null2Long(pid));
			gcs = this.gcService
					.query("select obj from GoodsClass obj where obj.parent.id=:pid and obj.display=:display order by obj.sequence asc",
							params, 0, count);
		} else {
			gcs = this.gcService
					.query("select obj from GoodsClass obj where obj.parent.id is null and obj.display=:display order by obj.sequence asc",
							params, 0, count);
		}
		return gcs;
	}

	/**
	 * 查询三级分类的推荐分类
	 * 
	 * @param count
	 * @return
	 */
	public List<GoodsClass> query_third_rec(String pid, int count) {
		List<GoodsClass> gcs = new ArrayList<GoodsClass>();
		Map params = new HashMap();
		params.put("pid", CommUtil.null2Long(pid));
		params.put("display", true);
		params.put("recommend", true);
		gcs = this.gcService
				.query("select obj from GoodsClass obj where obj.parent.parent.id=:pid and obj.display=:display and obj.recommend=:recommend order by obj.sequence asc",
						params, 0, count);
		return gcs;
	}

	/**
	 * 查询主分类中显示的品牌信息
	 * 
	 * @param count
	 * @return
	 */
	public List<Map> query_gc_brand(String gc_id) {
		List<Map> map_list = new ArrayList<Map>();
		GoodsClass gc = this.gcService.getObjById(CommUtil.null2Long(gc_id));
		if (gc.getGb_info() != null && !gc.getGb_info().equals("")) {
			map_list = (List<Map>) Json.fromJson(gc.getGb_info());
		}
		return map_list;
	}

	/**
	 * 查询主分类中显示的广告信息
	 * 
	 * @param count
	 * @return
	 */
	public Map query_gc_advert(String gc_id, String web_url) {
		Map adv_map = new HashMap();
		GoodsClass gc = this.gcService.getObjById(CommUtil.null2Long(gc_id));
		if (gc.getGc_advert() != null && !gc.getGc_advert().equals("")) {
			Map map_temp = (Map) Json.fromJson(gc.getGc_advert());
			if (CommUtil.null2Int(map_temp.get("adv_type")) == 0) {// 系统广告
				AdvertPosition ap = this.apService.getObjById(CommUtil
						.null2Long(map_temp.get("adv_id")));
				if (ap != null) {
					if (ap.getAp_acc_url() != null
							&& !ap.getAp_acc_url().equals("")) {
						adv_map.put(
								"advert_url",
								web_url + "/advert_redirect.htm?url="
										+ ap.getAp_acc_url() + "&id="
										+ ap.getId());
					}
					if (ap.getAp_acc() != null) {
						adv_map.put("advert_img", web_url + "/"
								+ ap.getAp_acc().getPath() + "/"
								+ ap.getAp_acc().getName());
					}
				}
			}
			if (CommUtil.null2Int(map_temp.get("adv_type")) == 1) {// 自定义广告
				Accessory acc = this.accessoryService.getObjById(CommUtil
						.null2Long(map_temp.get("acc_id")));
				if (acc != null) {
					adv_map.put("advert_url",
							CommUtil.null2String(map_temp.get("acc_url")));
					adv_map.put("advert_img", web_url + "/" + acc.getPath()
							+ "/" + acc.getName());
				}
			}
		}
		return adv_map;
	}
}
