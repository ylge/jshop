package com.iskyshop.manage.admin.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.FreeApplyLog;
import com.iskyshop.foundation.domain.FreeClass;
import com.iskyshop.foundation.domain.Goods;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IFreeApplyLogService;
import com.iskyshop.foundation.service.IFreeClassService;
import com.iskyshop.foundation.service.IGoodsService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: FreeTools.java
 * </p>
 * 
 * <p>
 * Description:0元试用相关工具类
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
 * @date 2014-11-11
 * 
 * 
 * @version iskyshop_b2b2c v2.0 2015版
 */
@Component
public class FreeTools {
	@Autowired
	private IFreeClassService freeClassService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IFreeApplyLogService freeApplyLogService;

	public FreeClass queyFreeClass(String class_id) {
		FreeClass fc = this.freeClassService.getObjById(CommUtil
				.null2Long(class_id));
		return fc;
	}

	public Goods queryGoods(String goods_id) {
		Goods goods = this.goodsService
				.getObjById(CommUtil.null2Long(goods_id));
		return goods;
	}

	public List<User> queryUser(String free_id) {
		List<User> users = new ArrayList<User>();
		Map map = new HashMap();
		map.put("free_id", CommUtil.null2Long(free_id));
		List<FreeApplyLog> fals = this.freeApplyLogService
				.query("select obj from FreeApplyLog obj where  obj.freegoods_id=:free_id  order by obj.addTime desc",
						map, 0, 10);
		for (FreeApplyLog fal : fals) {
			User user = this.userService.getObjById(CommUtil.null2Long(fal
					.getUser_id()));
			if (user != null) {
				users.add(user);
			}
		}
		return users;
	}

	public User queryEvaluteUser(String user_id) {
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		return user;
	}
}
