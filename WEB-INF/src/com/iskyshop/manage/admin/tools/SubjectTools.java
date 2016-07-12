package com.iskyshop.manage.admin.tools;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISubjectService;
import com.iskyshop.foundation.service.ISysConfigService;

/**
 * 
 * <p>
 * Title: SubjectTools.java
 * </p>
 * 
 * <p>
 * Description: 专题json解析工具类
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
 * @date 2014-11-14
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Component
public class SubjectTools {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private ISubjectService subjectService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IAccessoryService accessoryService;

	/**
	 * 平台设置专题获取状体热点信息方法
	 * 
	 * @param areaInfo
	 * @return
	 */
	public List<Map> getAreaInfo(String areaInfo) {
		List<Map> maps = new ArrayList<Map>();
		if (areaInfo != null && !areaInfo.equals("")) {
			String infos[] = areaInfo.split("-");
			for (String obj : infos) {
				if (!obj.equals("")) {
					Map map = new HashMap();
					String detail_infos[] = obj.split("==");
					detail_infos[0] = detail_infos[0].replace("_", ",");
					String coords = detail_infos[0];
					map.put("coords", coords);
					map.put("url", detail_infos[1]);
					map.put("width", this.getWidth(detail_infos[0]));
					map.put("height", this.getHeight(detail_infos[0]));
					map.put("top", this.getTop(detail_infos[0]));
					map.put("left", this.getLeft(detail_infos[0]));
					maps.add(map);
				}
			}
		}
		System.out.println("maps:"+maps);
		return maps;
	}

	public int getWidth(String str) {
		int width = 0;
		String strs[] = str.split(",");
		int temp_width = CommUtil.null2Int(strs[0])
				- CommUtil.null2Int(strs[2]);
		if (temp_width > 0) {
			width = temp_width;
		} else {
			width = 0 - temp_width;
		}
		return width;
	}

	public int getHeight(String str) {
		int height = 0;
		String strs[] = str.split(",");
		int temp_height = CommUtil.null2Int(strs[1])
				- CommUtil.null2Int(strs[3]);
		if (temp_height > 0) {
			height = temp_height;
		} else {
			height = 0 - temp_height;
		}
		return height;
	}

	public int getTop(String str) {
		int top = 0;
		String strs[] = str.split(",");
		top = CommUtil.null2Int(strs[1]);
		return top;
	}

	public int getLeft(String str) {
		int left = 0;
		String strs[] = str.split(",");
		left = CommUtil.null2Int(strs[0]);
		return left;
	}

	public List<Map> getGoodsInfos(String goods_ids) {
		List<Map> maps = new ArrayList<Map>();
		if (goods_ids != null && !goods_ids.equals("")) {
			String ids[] = goods_ids.split(",");
			for (String id : ids) {
				Map map = new HashMap();
				Goods obj = this.goodsService
						.getObjById(CommUtil.null2Long(id));
				if (obj != null) {
					map.put("id", obj.getId());
					map.put("name", obj.getGoods_name());
					map.put("price", obj.getGoods_current_price());
					map.put("img", obj.getGoods_main_photo().getPath() + "/"
							+ obj.getGoods_main_photo().getName());
					maps.add(map);
				}
			}
		}
		return maps;
	}

	/**
	 * 前台专题详情获取是否需要二级域名
	 * 
	 * @param id
	 * @return
	 */
	public String getGoodsUrl(String id, String webUrl) {
		String ret = "false";
		Goods obj = this.goodsService.getObjById(CommUtil.null2Long(id));
		if (obj.getGoods_type() == 1) {
			if (this.configService.getSysConfig().isSecond_domain_open()
					&& obj.getGoods_store().getStore_second_domain() != "") {
				ret = "true";
			}
		}
		return ret;
	}

	/**
	 * 获取图片宽度，在编辑专业页详情中调用该方法，用作定位图片外层div的定位位置。
	 * 
	 * @param str
	 * @return
	 */
	public int getImageWidth(String id) {
		int width = 1640;
		Accessory image = this.accessoryService.getObjById(CommUtil
				.null2Long(id));
		if (image.getWidth() < width) {
			width = image.getWidth();
		}
		return width;
	}

}
