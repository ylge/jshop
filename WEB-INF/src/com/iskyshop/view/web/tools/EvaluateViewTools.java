package com.iskyshop.view.web.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Accessory;
import com.iskyshop.foundation.domain.EnoughReduce;
import com.iskyshop.foundation.domain.Evaluate;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.service.IAccessoryService;
import com.iskyshop.foundation.service.IEnoughReduceService;
import com.iskyshop.foundation.service.IEvaluateService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.ISysConfigService;

/**
 * 
 * <p>
 * Title: EvaluateViewTools.java
 * </p>
 * 
 * <p>
 * Description:商品评价查询类，用来查询商品评价信息并在前台显示
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
 * @date 2014-9-29
 * 
 * @version iskyshop_b2b2c 2015
 */
@Component
public class EvaluateViewTools {
	@Autowired
	private IEvaluateService evaluateService;
	@Autowired
	private IAccessoryService accessoryService;

	public List<Evaluate> queryByEva(String goods_id, String eva) {
		Map params = new HashMap();
		params.put("goods_id", CommUtil.null2Long(goods_id));
		params.put("evaluate_type", "goods");
		params.put("evaluate_status", 0);
		List<Evaluate> list = new ArrayList<Evaluate>();
		if (eva.equals("100")) {
			params.put("evaluate_photos", "");
			list = this.evaluateService
					.query("select obj from Evaluate obj where obj.evaluate_goods.id=:goods_id and obj.evaluate_type=:evaluate_type and obj.evaluate_status=:evaluate_status and obj.evaluate_photos!=:evaluate_photos",
							params, -1, -1);
		} else if (eva.equals("all")) {
			list = this.evaluateService
					.query("select obj from Evaluate obj where obj.evaluate_goods.id=:goods_id and obj.evaluate_type=:evaluate_type and obj.evaluate_status=:evaluate_status",
							params, -1, -1);
		} else {
			params.put("evaluate_buyer_val", CommUtil.null2Int(eva));
			list = this.evaluateService
					.query("select obj from Evaluate obj where obj.evaluate_goods.id=:goods_id and obj.evaluate_type=:evaluate_type and obj.evaluate_status=:evaluate_status and obj.evaluate_buyer_val=:evaluate_buyer_val",
							params, -1, -1);// 查询好评数
		}
		return list;
	}

	public List queryEvaImgSrc(String ids) {
		List list = new ArrayList();
		if (ids != null && !ids.equals("")) {
			for (String str : ids.split(",")) {
				if (str != null && !str.equals("")) {
					Accessory img = this.accessoryService.getObjById(CommUtil
							.null2Long(str));
					if(img!=null){
						list.add(img);
					}
				}
			}
		}

		return list;
	}
}
