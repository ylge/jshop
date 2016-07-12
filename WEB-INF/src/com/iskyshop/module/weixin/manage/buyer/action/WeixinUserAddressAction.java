package com.iskyshop.module.weixin.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
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
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.domain.virtual.SysMap;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.query.support.IPageList;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.WebForm;
import com.iskyshop.foundation.domain.Address;
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.AddressQueryObject;
import com.iskyshop.foundation.service.IAddressService;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.iskyshop.view.web.tools.AreaViewTools;

/**
 * 
 * 
 * <p>
 * Title:MobileUserAddressAction.java
 * </p>
 * 
 * <p>
 * Description: 用户中心地址管理
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
 * @author jy
 * 
 * @date 2014年8月20日
 * 
 * @version iskyshop_b2b2c_2015
 */
@Controller
public class WeixinUserAddressAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IAreaService areaService;
	@Autowired
	private AreaViewTools areaViewTools;

	/**
	 * 买家中心地址管理
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "收货地址管理", value = "/wap/buyer/address.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_address", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/address.htm")
	public ModelAndView address(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView("user/wap/usercenter/address.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map params = new HashMap();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		List<Address> addresses = this.addressService.query(
				"select obj from Address obj where obj.user.id =:user_id",
				params, -1, -1);
		mv.addObject("addrs", addresses);
		mv.addObject("areaViewTools", areaViewTools);
		return mv;
	}

	/**
	 * 买家中心新增地址
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "新增收货地址", value = "/wap/buyer/address_add.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_address_add", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/address_add.htm")
	public ModelAndView address_add(HttpServletRequest request,
			HttpServletResponse response, String type) {
		ModelAndView mv = new JModelAndView(
				"user/wap/usercenter/address_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null order by obj.sequence,obj.id asc", null,
				-1, -1);
		mv.addObject("areas", areas);
		//common为true时，用户添加地址时可以默认选择；20150703
		Map params = new HashMap();
		params.put("common", true);
		List<Area> bjs = this.areaService
				.query("select new Area(id,addTime) from Area obj where obj.common = :common order by obj.sequence asc",
						params, 0, 1);
		if (bjs.size() > 0) {
			mv.addObject("bj", this.areaService.getObjById(bjs.get(0).getId()));
		} else {
			params.clear();
			params.put("areaName", "东城区");
			List<Area> bjs2 = this.areaService.query(
					"select obj from Area obj where obj.areaName = :areaName",
					params, 0, 1);
			mv.addObject("bj", bjs2.size() > 0 ? bjs2.get(0) : null);
		}
		mv.addObject("areaViewTools", areaViewTools);
		mv.addObject("type", type);
		return mv;
	}

	/**
	 * 买家中心编辑地址
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "编辑收货地址", value = "/wap/buyer/address_edit.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_address_edit", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/address_edit.htm")
	public ModelAndView address_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String type) {
		ModelAndView mv = new JModelAndView(
				"user/wap/usercenter/address_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("areas", areas);
		Address obj = this.addressService.getObjById(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("areaViewTools", areaViewTools);
		mv.addObject("type", type);
		return mv;
	}

	/**
	 * 买家中心地址保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "收货地址保存", value = "/wap/buyer/address_save.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_address_save", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/address_save.htm")
	public ModelAndView address_save(HttpServletRequest request,
			HttpServletResponse response, String id, String area_id,
			String currentPage, String type) {
		ModelAndView mv = new JModelAndView("wap/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		WebForm wf = new WebForm();
		Address address = null;
		if (id == null || id.equals("")) {
			address = wf.toPo(request, Address.class);
			address.setAddTime(new Date());
		} else {
			Address obj = this.addressService.getObjById(Long.parseLong(id));
			address = (Address) wf.toPo(request, obj);
		}
		address.setUser(SecurityUserHolder.getCurrentUser());
		Area area = this.areaService.getObjById(CommUtil.null2Long(area_id));
		address.setArea(area);
		if (id == null || id.equals("")) {
			this.addressService.save(address);
		} else
			this.addressService.update(address);
		mv.addObject("op_title", "保存成功");
		if (type != null && !type.equals("")) {
			mv.addObject("url", type + "&addr_id=" + address.getId());
			System.out.println(type + "&addr_id=" + address.getId());
		} else {
			mv.addObject("url", CommUtil.getURL(request)
					+ "/wap/buyer/address.htm");
		}
		return mv;
	}

	/**
	 * 买家中心地址删除
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "收货地址删除", value = "/wap/buyer/address_del.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_address_del", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/address_del.htm")
	public String address_del(HttpServletRequest request,
			HttpServletResponse response, String id) {
		Address address = this.addressService
				.getObjById(CommUtil.null2Long(id));
		if (address != null
				&& address.getUser().getId()
						.equals(SecurityUserHolder.getCurrentUser().getId())) {
			this.addressService.delete(Long.parseLong(id));
		}
		return "redirect:/wap/buyer/address.htm";
	}

	@SecurityMapping(title = "收货地址删除", value = "/wap/buyer/ajax_address_del.htm*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_address_del", rgroup = "移动端用户中心")
	@RequestMapping("/wap/buyer/ajax_address_del.htm")
	public String ajax_address_del(HttpServletRequest request,
			HttpServletResponse response, String id) {
		Address address = this.addressService
				.getObjById(CommUtil.null2Long(id));
		if (address != null
				&& address.getUser().getId()
						.equals(SecurityUserHolder.getCurrentUser().getId())) {
			this.addressService.delete(Long.parseLong(id));
		}
		return "success";
	}

	@RequestMapping("/wap/buyer/address_add_ajax.htm")
	public void address_add_ajax(HttpServletRequest request,
			HttpServletResponse response, String aid) {
		Area area = this.areaService.getObjById(CommUtil.null2Long(aid));
		Map json_map = new HashMap();
		List map_list = new ArrayList();
		if (area.getLevel() != 2) {
			json_map.put("level", true);
			//List<Area> childs = area.getChilds();
			Map params=new HashMap();
			params.put("aid", CommUtil.null2Long(aid));
			List<Area> childs =this.areaService.query("select obj from Area obj where obj.parent.id=:aid order by  sequence asc", params, -1, -1);
			for (Area child : childs) {
				Map map = new HashMap();
				map.put("addr_id", child.getId());
				map.put("addr_name", child.getAreaName());
				map_list.add(map);
			}
			json_map.put("data", map_list);
		} else {
			json_map.put("level", false);
			List<Area> areas = this.areaService.query(
					"select obj from Area obj where obj.parent.id is null order by  sequence asc",
					null, -1, -1);
			for (Area child : areas) {
				Map map = new HashMap();
				map.put("addr_id", child.getId());
				map.put("addr_name", child.getAreaName());
				map_list.add(map);
			}
			json_map.put("data", map_list);
			json_map.put("info", areaViewTools.generic_area_info(aid));
			json_map.put("aid", aid);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(json_map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@RequestMapping("/wap/load_area_data.htm")
	public void load_area_data(HttpServletRequest request,
			HttpServletResponse response, String aid) {
		Map json_map = new HashMap();
		List map_list = new ArrayList();
			List<Area> childs =this.areaService.query("select obj from Area obj where obj.level=0 order by  sequence asc", null, -1, -1);
			for (Area child : childs) {
				Map map = new HashMap();
				map.put("addr_id", child.getId());
				map.put("addr_name", child.getAreaName());
				map_list.add(map);
			}
			json_map.put("data", map_list);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(json_map, JsonFormat.compact()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
