package com.iskyshop.manage.admin.action;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.iskyshop.foundation.domain.Area;
import com.iskyshop.foundation.domain.ShipAddress;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.domain.query.ShipAddressQueryObject;
import com.iskyshop.foundation.service.IAreaService;
import com.iskyshop.foundation.service.IShipAddressService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;

/**
 * 
 * <p>
 * Title: ShipAddressManageAction.java
 * </p>
 * 
 * <p>
 * Description:自营商家发货地址管理控制器，用来管理自营发货地址信息
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
 * @date 2014-11-11
 * 
 * @version iskyshop_b2b2c 2015
 */
@Controller
public class ShipAddressManageAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IShipAddressService shipAddressService;
	@Autowired
	private IAreaService areaService;

	/**
	 * ShipAddress列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "自营发货地址列表", value = "/admin/ship_address_list.htm*", rtype = "admin", rname = "发货地址", rcode = "ship_adress", rgroup = "自营")
	@RequestMapping("/admin/ship_address_list.htm")
	public ModelAndView ship_address_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"admin/blue/ship_address_list.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ShipAddressQueryObject qo = new ShipAddressQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.sa_type", new SysMap("sa_type", 1), "=");
		IPageList pList = this.shipAddressService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	/**
	 * shipaddress添加管理
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "自营发货地址添加", value = "/admin/ship_address_add.htm*", rtype = "admin", rname = "发货地址", rcode = "ship_adress", rgroup = "自营")
	@RequestMapping("/admin/ship_address_add.htm")
	public ModelAndView ship_address_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/ship_address_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("areas", areas);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * shipaddress编辑管理
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@SecurityMapping(title = "自营发货地址编辑", value = "/admin/ship_address_edit.htm*", rtype = "admin", rname = "发货地址", rcode = "ship_adress", rgroup = "自营")
	@RequestMapping("/admin/ship_address_edit.htm")
	public ModelAndView ship_address_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView("admin/blue/ship_address_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (id != null && !id.equals("")) {
			ShipAddress shipaddress = this.shipAddressService
					.getObjById(CommUtil.null2Long(id));
			List<Area> areas = this.areaService.query(
					"select obj from Area obj where obj.parent.id is null",
					null, -1, -1);
			mv.addObject("sa_area",
					this.areaService.getObjById(shipaddress.getSa_area_id()));
			mv.addObject("areas", areas);
			mv.addObject("obj", shipaddress);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}

	/**
	 * shipaddress保存管理
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "自营发货地址保存", value = "/admin/ship_address_save.htm*", rtype = "admin", rname = "发货地址", rcode = "ship_adress", rgroup = "自营")
	@RequestMapping("/admin/ship_address_save.htm")
	public ModelAndView ship_address_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		WebForm wf = new WebForm();
		ShipAddress shipaddress = null;
		if (id.equals("")) {
			shipaddress = wf.toPo(request, ShipAddress.class);
			shipaddress.setAddTime(new Date());
		} else {
			ShipAddress obj = this.shipAddressService.getObjById(Long
					.parseLong(id));
			shipaddress = (ShipAddress) wf.toPo(request, obj);
		}
		shipaddress.setSa_type(1);
		shipaddress.setSa_user_id(SecurityUserHolder.getCurrentUser().getId());
		shipaddress.setSa_user_name(SecurityUserHolder.getCurrentUser()
				.getUsername());
		if (id.equals("")) {
			this.shipAddressService.save(shipaddress);
		} else
			this.shipAddressService.update(shipaddress);
		ModelAndView mv = new JModelAndView("admin/blue/success.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "发货地址保存成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/admin/ship_address_list.htm?currentPage=" + currentPage);
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/admin/ship_address_add.htm?currentPage=" + currentPage);
		return mv;
	}

	@SecurityMapping(title = "自营发货地址删除", value = "/admin/ship_address_del.htm*", rtype = "admin", rname = "发货地址", rcode = "ship_adress", rgroup = "自营")
	@RequestMapping("/admin/ship_address_del.htm")
	public String ship_address_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				ShipAddress obj = this.shipAddressService.getObjById(CommUtil
						.null2Long(id));
				if (obj.getSa_type() == 1) {// 只能删除自营发货地址
					this.shipAddressService.delete(obj.getId());
				}
			}
		}
		return "redirect:ship_address_list.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "设置默认发货地址", value = "/admin/ship_address_default.htm*", rtype = "admin", rname = "发货地址", rcode = "ship_adress", rgroup = "自营")
	@RequestMapping("/admin/ship_address_default.htm")
	public String ship_address_default(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		Map params = new HashMap();
		params.put("sa_default", 1);
		params.put("sa_type", 1);
		List<ShipAddress> sa_list = this.shipAddressService
				.query("select obj from ShipAddress obj where obj.sa_default=:sa_default and obj.sa_type=:sa_type",
						params, -1, -1);
		for (ShipAddress sa : sa_list) {
			sa.setSa_default(0);
			this.shipAddressService.update(sa);
		}
		ShipAddress obj = this.shipAddressService.getObjById(CommUtil
				.null2Long(id));
		if (obj.getSa_type() == 1) {// 只能设置自营发货地址
			obj.setSa_default(1);
			this.shipAddressService.update(obj);
		}
		return "redirect:ship_address_list.htm?currentPage=" + currentPage;
	}
}