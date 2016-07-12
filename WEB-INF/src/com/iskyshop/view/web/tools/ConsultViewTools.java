package com.iskyshop.view.web.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Consult;
import com.iskyshop.foundation.service.IConsultService;

/**
 * 
 * <p>
 * Title: ConsultViewTools.java
 * </p>
 * 
 * <p>
 * Description: 商品咨询管理类，用于前端velocity中的信息查询并显示
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
public class ConsultViewTools {
	@Autowired
	private IConsultService consultService;

	/**
	 * 根据分类查询所有该分类的商品咨询
	 * 
	 * @param type
	 *            咨询类型
	 * @return 返回商品咨询列表
	 */
	public List<Consult> queryByType(String type, String goods_id) {
		List<Consult> list = new ArrayList<Consult>();
		if (!CommUtil.null2String(type).equals("")) {
			Map params = new HashMap();
			params.put("consult_type", CommUtil.null2String(type));
			params.put("goods_id", CommUtil.null2Long(goods_id));
			list = this.consultService
					.query("select obj from Consult obj where obj.consult_type=:consult_type and obj.goods_id=:goods_id",
							params, -1, -1);
		} else {
			Map params = new HashMap();
			params.put("goods_id", CommUtil.null2Long(goods_id));
			list = this.consultService.query(
					"select obj from Consult obj where obj.goods_id=:goods_id",
					params, -1, -1);
		}
		return list;
	}

}
