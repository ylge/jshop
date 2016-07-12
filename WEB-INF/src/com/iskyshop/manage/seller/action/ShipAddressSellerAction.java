package com.iskyshop.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.iskyshop.foundation.service.IUserService;

/**
 * 
 * <p>
 * Title: ShipAddressSellerAction.java
 * </p>
 * 
 * <p>
 * Description:卖家发货地址管理控制器，用来添加、删除、编辑卖家发货地址信息，发货地址主要用在发货设置、快递跟踪等
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
@Controller
public class ShipAddressSellerAction {
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

	@SecurityMapping(title = "发货地址列表", value = "/seller/ship_address.htm*", rtype = "seller", rname = "发货信息", rcode = "ship_address_seller", rgroup = "交易管理")
	@RequestMapping("/seller/ship_address.htm")
	public ModelAndView ship_address(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/ship_address.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ShipAddressQueryObject qo = new ShipAddressQueryObject(currentPage, mv,
				orderBy, orderType);
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		qo.addQuery("obj.sa_type", new SysMap("sa_type", 0), "=");
		qo.addQuery("obj.sa_user_id", new SysMap("sa_user_id", user.getId()),
				"=");
		IPageList pList = this.shipAddressService.list(qo);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	@SecurityMapping(title = "新增发货地址", value = "/seller/ship_address_add.htm*", rtype = "seller", rname = "发货信息", rcode = "ship_address_seller", rgroup = "交易管理")
	@RequestMapping("/seller/ship_address_add.htm")
	public ModelAndView ship_address_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/ship_address_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<Area> areas = this.areaService.query(
				"select obj from Area obj where obj.parent.id is null", null,
				-1, -1);
		mv.addObject("areas", areas);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "编辑发货地址", value = "/seller/ship_address_add.htm*", rtype = "seller", rname = "发货信息", rcode = "ship_address_seller", rgroup = "交易管理")
	@RequestMapping("/seller/ship_address_edit.htm")
	public ModelAndView ship_address_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new JModelAndView(
				"user/default/sellercenter/ship_address_add.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ShipAddress obj = this.shipAddressService.getObjById(CommUtil
				.null2Long(id));
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (obj != null && obj.getSa_user_id().equals(user.getId())) {
			List<Area> areas = this.areaService.query(
					"select obj from Area obj where obj.parent.id is null",
					null, -1, -1);
			mv.addObject("sa_area",
					this.areaService.getObjById(obj.getSa_area_id()));
			mv.addObject("areas", areas);
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new JModelAndView(
					"user/default/sellercenter/seller_error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("url", "/seller/ship_address.htm");
			mv.addObject("op_title", "参数错误");
		}
		return mv;
	}

	@SecurityMapping(title = "编辑发货地址", value = "/seller/ship_address_add.htm*", rtype = "seller", rname = "发货信息", rcode = "ship_address_seller", rgroup = "交易管理")
	@RequestMapping("/seller/ship_address_save.htm")
	public void ship_address_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		WebForm wf = new WebForm();
		ShipAddress shipaddress = null;
		boolean ret = true;
		if (id.equals("")) {
			shipaddress = wf.toPo(request, ShipAddress.class);
			shipaddress.setAddTime(new Date());
		} else {
			ShipAddress obj = this.shipAddressService.getObjById(CommUtil
					.null2Long(id));
			shipaddress = (ShipAddress) wf.toPo(request, obj);
		}
		shipaddress.setSa_type(0);
		shipaddress.setSa_user_id(SecurityUserHolder.getCurrentUser().getId());
		shipaddress.setSa_user_name(SecurityUserHolder.getCurrentUser()
				.getUsername());
		if (id.equals("")) {
			ret = this.shipAddressService.save(shipaddress);
		} else
			ret = this.shipAddressService.update(shipaddress);

		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SecurityMapping(title = "发货地址删除", value = "/seller/ship_address_del.htm*", rtype = "seller", rname = "发货信息", rcode = "ship_address_seller", rgroup = "交易管理")
	@RequestMapping("/seller/ship_address_del.htm")
	public String ship_address_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		for (String id : ids) {
			if (!id.equals("")) {
				ShipAddress obj = this.shipAddressService.getObjById(CommUtil
						.null2Long(id));
				if (obj != null && obj.getSa_type() == 0
						&& obj.getSa_user_id().equals(user.getId())) {// 只能删除自己添加的发货地址
					this.shipAddressService.delete(obj.getId());
				}
			}
		}
		return "redirect:ship_address.htm?currentPage=" + currentPage;
	}

	@SecurityMapping(title = "设置默认发货地址", value = "/seller/ship_address_default.htm*", rtype = "seller", rname = "发货信息", rcode = "ship_address_seller", rgroup = "交易管理")
	@RequestMapping("/seller/ship_address_default.htm")
	public String ship_address_default(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		User user = this.userService.getObjById(SecurityUserHolder
				.getCurrentUser().getId());
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
		}
		return "redirect:ship_address.htm?currentPage=" + currentPage;
	}
}
