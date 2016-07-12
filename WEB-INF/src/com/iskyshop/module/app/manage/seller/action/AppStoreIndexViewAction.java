package com.iskyshop.module.app.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.foundation.domain.Store;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IOrderFormLogService;
import com.iskyshop.foundation.service.IOrderFormService;
import com.iskyshop.foundation.service.IStoreService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.manage.admin.tools.OrderFormTools;

/**
 * 
 * <p>
 * Title: AppStoreIndexViewAction.java店铺信息
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: 沈阳网之商科技有限公司 www.iskyshop.com
 * </p>
 * 
 * @author lixiaoyang
 * 
 * @date 2015-4-13
 * 
 * @version 1.0
 */
@Controller
public class AppStoreIndexViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IOrderFormService orderFormService;
	@Autowired
	private OrderFormTools orderFormTools;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IOrderFormLogService orderFormLogService;

	/**
	 * 店铺总览
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 */
	@RequestMapping("/app/seller/store_index.htm")
	public void store_index(HttpServletRequest request,
			HttpServletResponse response, String user_id) {
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		json_map.put("username", user.getUsername());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		json_map.put("store_name", store.getStore_name());
		String url = CommUtil.getURL(request);
		if (store.getStore_logo() != null) {
			json_map.put("store_logo", url + "/"
					+ store.getStore_logo().getPath() + "/"
					+ store.getStore_logo().getName());
		} else {
			json_map.put("store_logo", url
					+ "/"
					+ this.configService.getSysConfig().getStoreImage()
							.getPath()
					+ "/"
					+ this.configService.getSysConfig().getStoreImage()
							.getName());
		}
		Map params = new HashMap();
		params.put("store_id", user.getStore().getId().toString());
		params.put("order_cat", 0);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		params.put("addTime", cal.getTime());
		List list = this.orderFormService
				.query("select count(*) from OrderForm obj where  obj.store_id=:store_id and obj.order_cat=:order_cat and DATE_FORMAT(obj.addTime,'%Y-%m-%d') >= DATE_FORMAT( :addTime,'%Y-%m-%d') ",
						params, -1, -1);
		json_map.put("order_today", list.get(0));

		params.remove("addTime");
		params.put("order_status", 10);
		list = this.orderFormService
				.query("select count(*) from OrderForm obj where  obj.store_id=:store_id and obj.order_cat=:order_cat and obj.order_status=:order_status  ",
						params, -1, -1);
		json_map.put("order_to_pay", list.get(0));

		params.remove("order_status");
		params.put("order_status1", 20);
		params.put("order_status2", 16);
		list = this.orderFormService
				.query("select count(*) from OrderForm obj where  obj.store_id=:store_id and obj.order_cat=:order_cat and obj.order_status<=:order_status1 and  obj.order_status>=:order_status2 ",
						params, -1, -1);
		json_map.put("order_to_ship", list.get(0));

		params.put("order_status1", 40);
		params.put("order_status2", 30);
		list = this.orderFormService
				.query("select count(*) from OrderForm obj where  obj.store_id=:store_id and obj.order_cat=:order_cat and obj.order_status<=:order_status1 and  obj.order_status>=:order_status2 ",
						params, -1, -1);
		json_map.put("order_to_confirm", list.get(0));

		params.remove("order_status1");
		params.remove("order_status2");
		params.put("order_status", 50);
		list = this.orderFormService
				.query("select count(*) from OrderForm obj where  obj.store_id=:store_id and obj.order_cat=:order_cat and obj.order_status=:order_status  ",
						params, -1, -1);
		json_map.put("order_confirmed", list.get(0));

		params.put("order_status", 50);
		list = this.orderFormService
				.query("select count(*) from OrderForm obj where  obj.store_id=:store_id and obj.order_cat=:order_cat and obj.order_status>:order_status  ",
						params, -1, -1);
		json_map.put("order_finished", list.get(0));

		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	private void send_json(String json, HttpServletResponse response) {
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
