package com.iskyshop.manage.seller.tools;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.OrderForm;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.ISysConfigService;

/**
 * 
 * <p>
 * Title: OrderTools.java
 * </p>
 * 
 * <p>
 * Description: 卖家中心订单处理工具类，用来计算订单收货倒计时等等功能
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
 * @date 2014-11-12
 * 
 * @version iskyshop_b2b2c 2015
 */
@Component
public class OrderTools {
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private ISysConfigService configService;

	public Date cal_confirm_time(String order_id) {
		OrderForm obj = this.orderFormService.getObjById(CommUtil
				.null2Long(order_id));
		if (obj != null) {
			Date ship_time = obj.getShipTime();
			Calendar cal = Calendar.getInstance();
			cal.setTime(ship_time);
			cal.add(Calendar.DAY_OF_YEAR, this.configService.getSysConfig()
					.getAuto_order_confirm() + obj.getOrder_confirm_delay());
			Date confirm_time = cal.getTime();
			return confirm_time;
		} else
			return null;
	}
}
