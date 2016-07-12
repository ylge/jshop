package com.iskyshop.module.app.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
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
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.ShipAddress;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IShipAddressService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: AppSellerShipAddressViewAction.java
 * </p>
 * 
 * <p>
 * Description:卖家发货地址管理控制器，用来添加、删除、编辑卖家发货地址信息，发货地址主要用在发货设置、快递跟踪等
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
 * @date 2015-3-30
 * 
 * @version 1.0
 */

@Controller
public class AppSellerShipAddressViewAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IShipAddressService shipAddressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private IUserService userService;

	/**
	 * 发货地址列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping("/app/seller/ship_address.htm")
	public void ship_address(HttpServletRequest request,
			HttpServletResponse response, String user_id, String begin_count,
			String select_count) {
		Map json_map = new HashMap();
		Map params = new HashMap();
		params.put("sa_type", 0);
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		params.put("sa_user_id", user.getId());
		List<ShipAddress> list = this.shipAddressService
				.query("select obj from ShipAddress obj where obj.sa_type=:sa_type and obj.sa_user_id=:sa_user_id order by sa_sequence",
						params, CommUtil.null2Int(begin_count),
						CommUtil.null2Int(select_count));
		List ship_address_list = new ArrayList();
		for (ShipAddress shipAddress : list) {
			Map map = new HashMap();
			map.put("id", shipAddress.getId());
			map.put("sa_name", shipAddress.getSa_name());
			map.put("sa_sequence", shipAddress.getSa_sequence());
			map.put("sa_default", shipAddress.getSa_default());
			map.put("sa_addr", shipAddress.getSa_addr());
			ship_address_list.add(map);
		}
		json_map.put("ship_address_list", ship_address_list);
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	/**
	 * 发货地址编辑
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param id
	 */
	@RequestMapping("/app/seller/ship_address_edit.htm")
	public void ship_address_edit(HttpServletRequest request,
			HttpServletResponse response, String user_id, String id) {
		Map json_map = new HashMap();
		ShipAddress obj = this.shipAddressService.getObjById(CommUtil
				.null2Long(id));
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		if (obj != null && obj.getSa_user_id().equals(user.getId())) {
			json_map.put("ret", 100);
			json_map.put("id", obj.getId());
			json_map.put("sa_name", obj.getSa_name());
			json_map.put("sa_sequence", obj.getSa_sequence());
			json_map.put("sa_user", obj.getSa_user());
			json_map.put("sa_telephone", obj.getSa_telephone());
			json_map.put("sa_company", obj.getSa_company());
			json_map.put("sa_zip", obj.getSa_zip());
			json_map.put("sa_area_id", obj.getSa_area_id());
			Area area = this.areaService.getObjById(CommUtil.null2Long(obj
					.getSa_area_id()));
			json_map.put(
					"sa_area_name",
					area.getParent().getParent().getAreaName() + ">"
							+ area.getParent().getAreaName() + ">"
							+ area.getAreaName());
			json_map.put("sa_addr", obj.getSa_addr());
		} else {
			json_map.put("ret", -100);
			json_map.put("msg", "参数错误");
		}
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}
	/**
	 * 发货地址编辑保存
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param id
	 * @param sa_name
	 * @param sa_sequence
	 * @param sa_user
	 * @param sa_telephone
	 * @param sa_company
	 * @param sa_zip
	 * @param sa_area_id
	 * @param sa_addr
	 */
	@RequestMapping("/app/seller/ship_address_save.htm")
	public void ship_address_save(HttpServletRequest request,
			HttpServletResponse response, String user_id, String id,
			String sa_name, String sa_sequence, String sa_user,
			String sa_telephone, String sa_company, String sa_zip,
			String sa_area_id, String sa_addr) {
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		ShipAddress shipaddress = null;
		boolean ret = true;
		if (id != null && !id.equals("")) {
			shipaddress = this.shipAddressService.getObjById(CommUtil
					.null2Long(id));
		} else {
			shipaddress = new ShipAddress();
			shipaddress.setAddTime(new Date());
		}
		shipaddress.setSa_type(0);
		shipaddress.setSa_user_id(user.getId());
		shipaddress.setSa_user_name(user.getUsername());
		shipaddress.setSa_name(sa_name);
		shipaddress.setSa_sequence(CommUtil.null2Int(sa_sequence));
		shipaddress.setSa_user(sa_user);
		shipaddress.setSa_telephone(sa_telephone);
		shipaddress.setSa_company(sa_company);
		shipaddress.setSa_zip(sa_zip);
		shipaddress.setSa_area_id(CommUtil.null2Long(sa_area_id));
		shipaddress.setSa_addr(sa_addr);

		if (id != null && !id.equals("")) {
			ret = this.shipAddressService.update(shipaddress);
		} else
			ret = this.shipAddressService.save(shipaddress);
		json_map.put("ret", 100);
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}

	/**
	 * 发货地址删除
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param mulitId
	 */
	@RequestMapping("/app/seller/ship_address_del.htm")
	public void ship_address_del(HttpServletRequest request,
			HttpServletResponse response, String user_id, String mulitId) {
		Map json_map = new HashMap();
		String[] ids = mulitId.split(",");
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		for (String id : ids) {
			if (!id.equals("")) {
				ShipAddress obj = this.shipAddressService.getObjById(CommUtil
						.null2Long(id));
				if (obj != null && obj.getSa_type() == 0
						&& obj.getSa_user_id().equals(user.getId())) {// 只能删除自己添加的发货地址
					this.shipAddressService.delete(obj.getId());
					json_map.put("ret", 100);
				}
			}
		}
		this.send_json(Json.toJson(json_map, JsonFormat.compact()), response);
	}
	/**
	 * 发货地址默认
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param id
	 */
	@RequestMapping("/app/seller/ship_address_default.htm")
	public void ship_address_default(HttpServletRequest request,
			HttpServletResponse response, String user_id, String id) {
		Map json_map = new HashMap();
		User user = this.userService.getObjById(CommUtil.null2Long(user_id));
		user = user.getParent() == null ? user : user.getParent();
		Map params = new HashMap();
		params.put("sa_default", 1);
		params.put("sa_user_id", user.getId());
		List<ShipAddress> sa_list = this.shipAddressService
				.query("select obj from ShipAddress obj where obj.sa_default=:sa_default and obj.sa_user_id=:sa_user_id",
						params, -1, -1);
		for (ShipAddress sa : sa_list) {
			sa.setSa_default(0);
			this.shipAddressService.update(sa);
		}
		ShipAddress obj = this.shipAddressService.getObjById(CommUtil
				.null2Long(id));
		if (obj.getSa_user_id().equals(user.getId())) {// 只能设置自己添加地址为默认地址
			obj.setSa_default(1);
			this.shipAddressService.update(obj);
			json_map.put("ret", 100);
		}
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
