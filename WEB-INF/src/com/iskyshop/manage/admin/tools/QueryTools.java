package com.iskyshop.manage.admin.tools;

import java.util.List;

import org.springframework.stereotype.Component;

import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.query.QueryObject;

/**
 * 
 * <p>
 * Title: QueryTools.java
 * </p>
 * 
 * <p>
 * Description: 商品查询语句工具类
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
 * @date 2014-10-29
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Component
public class QueryTools {

	/**
	 * 屏蔽商品多种活动状态，并且查询出的商品为正常上架商品(满就赠中的赠品状态目前没有屏蔽)
	 * str_list:无需屏蔽的商品状态，当没有需要屏蔽的状态时传入null即可,买就送赠品不可参加活动
	 * 
	 * @return
	 */

	public void shieldGoodsStatus(QueryObject qo, List<String> str_list) {
		String temp_str = "";
		String status_list[] = { "goods_status", "activity_status",
				"group_buy", "combin_status", "order_enough_give_status",
				"enough_reduce", "f_sale_type", "advance_sale_type","order_enough_if_give" };
		if (str_list != null) {
			for (String str : str_list) {
				if (!"".equals(str)) {
					temp_str = temp_str + "," + str;
				}
			}
		}
		for (String status : status_list) {
			if (temp_str.indexOf(("," + status)) < 0) {
				qo.addQuery("obj." + status, new SysMap(status, 0), "=");
			}
		}
	}
}
